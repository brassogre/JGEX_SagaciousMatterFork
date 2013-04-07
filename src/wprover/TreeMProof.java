package wprover;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TreeMProof extends JTree implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2267579660069843560L;
	private DrawPanelFrame gxInstance;
	private PanelDraw dpane;
	private DrawPanelExtended dp;

	private DefaultMutableTreeNode top;
	private DefaultTreeModel model;
	private TreeCellOPaqueEditor editor;
	private mpopup popup;
	private mnode topm;
	private int rstep = -1;
	private int statusID = -1;

	private int x1, y1, x2, y2;
	private boolean isButtonDown = false;

	public void loadmtree(final mnode n) {
		topm = n;
		top.removeAllChildren();
		loadmnode(top, n);
		cancelEditing();
		model.reload();
		rstep = getRowCount();

	}

	public boolean isTreeEmpty() {
		if (top == null)
			return true;
		final mnode m = (mnode) top.getUserObject();
		if ((m == null) || (m.size() <= 1))
			return true;
		return false;
	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);

		if (isButtonDown) {
			final Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.gray);
			g2.setStroke(UtilityMiscellaneous.DashedStroke);
			g2.drawLine(x1, y1, x1, y2);
			g2.drawLine(x1, y1, x2, y1);
			g2.drawLine(x2, y2, x1, y2);
			g2.drawLine(x2, y2, x2, y1);
		}
	}

	private void loadmnode(final DefaultMutableTreeNode nd, final mnode n) {
		nd.setUserObject(n);
		for (int i = 0; i < n.size(); i++) {
			final mnode n1 = n.get(i);
			final DefaultMutableTreeNode nd1 = new DefaultMutableTreeNode(n1);
			loadmnode(nd1, n1);
			nd.add(nd1);
			if (nd == top) {
				final int index = getLastProveNodeIndex();
				if (index > 0)
					n1.setIndex(index);
			} else
				n1.setIndex(i + 1);
		}
	}

	private DefaultMutableTreeNode getSelectedNodeOrLast() {
		final TreePath path = getSelectionPath();
		DefaultMutableTreeNode node = null;

		if (path == null)
			node = (DefaultMutableTreeNode) getLastNodeOnTop();
		else
			node = (DefaultMutableTreeNode) path.getLastPathComponent();
		return node;
	}

	private TreeNode getLastNodeOnTop() {
		final int n = top.getChildCount();
		if (n > 0)
			return top.getChildAt(n - 1);
		return null;
	}

	private int getLastProveNodeIndex() {
		final int k = top.getChildCount();
		getToProveIndex();
		final int s = getStatusID();
		if ((s >= 0) && (k >= s))
			return k - s - 1;
		return -1;
	}

	public DefaultMutableTreeNode addNewNode(final DefaultMutableTreeNode d,
			final mnode n) {
		// TreePath path = this.getSelectionPath();
		final DefaultMutableTreeNode node = getSelectedNodeOrLast();

		if (node == null)
			return d;

		final mnode n1 = (mnode) node.getUserObject();
		if (n1 == null)
			return d;

		if (node.getParent() == top) {
			final int index = getLastProveNodeIndex();
			if (index >= 0)
				n.setIndex(index + 1);
			top.add(d);
			((mnode) top.getUserObject()).add(n);
		} else {
			final DefaultMutableTreeNode dn = (DefaultMutableTreeNode) node
					.getParent();
			if (dn != null) {
				dn.add(d);
				((mnode) dn.getUserObject()).add(n);
			}
		}
		reload();
		setEditorLastRow();
		return d;
	}

	public DefaultMutableTreeNode addNewNode(final mnode n) {
		final DefaultMutableTreeNode d = new DefaultMutableTreeNode(n);
		return addNewNode(d, n);
	}

	public int getToProveIndex() {
		for (int i = 0; i < top.getChildCount(); i++) {
			final DefaultMutableTreeNode nd = (DefaultMutableTreeNode) top
					.getChildAt(i);
			final mnode t = (mnode) nd.getUserObject();
			if ((t != null) && (t.objSize() != 0) && (t.getObject(0) != null)
					&& (t.getObject(0) instanceof mprefix)) {
				final mprefix f = (mprefix) t.getObject(0);
				if (f.getPrefixType() == 1) {
					statusID = i;
					return i;
				}
			}
		}
		statusID = -1;
		return -1;
	}

	public int getStatusID() {
		return statusID;
	}

	public void undoStep(final UndoStruct un) {
		final mnode n = (mnode) top.getUserObject();
		if (n == null)
			return;
		final int k = n.size();
		if (k > 0) {
			final mnode n2 = n.getChild(k - 1);
			if (n2.containsUndo(un)) {
				cancelEditing();
				n.remove(k - 1);
				top.remove(k - 1);
				reload();
				setEditorLastRow();
			}
		}

	}

	public void reload() {
		final TreePath path = getSelectionPath();
		cancelEditing();
		model.reload();
		setSelectionPath(path);
		rstep = getRowCount();
	}

	public void appendDefault() {
		// TreePath path = this.getSelectionPath();
		final DefaultMutableTreeNode node = getSelectedNodeOrLast();
		if (node == null)
			return;
		final mnode n = (mnode) node.getUserObject();
		if (n == null)
			return;
		mobject d = null;
		if (n.size() == 1) {
			final mobject obj = n.getObject(0);
			if (obj instanceof mprefix) {
				final mprefix pf = (mprefix) obj;
				final int t = pf.getPrefixType();
				if (t == mprefix.GIVEN)
					d = new mdraw();
				else if (t == mprefix.TOPROVE)
					d = new massertion(0);
			}
		}
		if (d == null)
			d = new mobject(0);
		append(d);

	}

	public DefaultMutableTreeNode addChild(final mobject obj) {
		final TreePath path = getSelectionPath();
		final DefaultMutableTreeNode node = getSelectedNodeOrLast();
		if (node == null)
			return null;
		final mnode n = (mnode) node.getUserObject();
		if (n == null)
			return null;
		final mnode nd = new mnode();
		nd.add(obj);
		n.addChild(nd);
		final DefaultMutableTreeNode dn = new DefaultMutableTreeNode(nd);
		node.add(dn);
		cancelEditing();
		reload();
		expandPath(path);
		setEditorLastRow();
		return dn;
	}

	public DefaultMutableTreeNode append(final mobject obj) {
		final TreePath path = getSelectionPath();
		final DefaultMutableTreeNode node = getSelectedNodeOrLast();
		if (node == null)
			return null;
		final mnode n = (mnode) node.getUserObject();
		if (n == null)
			return null;
		n.add(obj);
		startEditingAtPath(path);
		this.setEditorLast();
		return node;
	}

	public DefaultMutableTreeNode addNewNode() {
		final mnode node = new mnode();
		final int k = top.getChildCount();
		final int n = getToProveIndex();
		if (n < 0) {
			// node.add(new mdraw("draw..."));
		} else {
			node.add(new mobject(0));
			node.setIndex(k - n - 1);
		}
		final mnode nt = (mnode) top.getUserObject();
		nt.addChild(node);
		final DefaultMutableTreeNode nd = new DefaultMutableTreeNode(node);
		top.add(nd);

		return nd;
	}

	public DefaultMutableTreeNode getRoot() {
		return top;
	}

	public void setEditorLastRow() {
		final int n = getRowCount();
		if (n == 0)
			return;
		final TreePath path = getPathForRow(n - 1);
		startEditingAtPath(path);
		setEditorLast();
	}

	public void setEditorLast() {
		editor.setSelectionLast();
	}

	public void setEditorFirst(final DefaultMutableTreeNode nd) {
		final TreePath path = new TreePath(nd.getPath());
		expandPath(path);
		startEditingAtPath(path);
		editor.setSelectionFirst();
	}

	public void setEditorLast(final DefaultMutableTreeNode nd) {
		final TreePath path = new TreePath(nd.getPath());
		startEditingAtPath(path);
		editor.setSelectionLast();
	}

	public void init_top() {
		final mnode node = new mnode();
		node.add(new mtext(getLanguage("Theorem")));
		top.setUserObject(node);
		final mnode node1 = new mnode();
		node1.add(new mprefix(0));
		top.add(new DefaultMutableTreeNode(node1));
		node.add(node1);
		topm = node;
	}

	String getLanguage(final String s) {
		if (gxInstance != null)
			return DrawPanelFrame.getLanguage(s);
		return s;
	}

	public String getLanguage(final int n, final String s) {
		String s1 = "";
		if (gxInstance != null)
			s1 = DrawPanelFrame.getLanguage(n);
		if ((s1 != null) && (s1.length() > 0))
			return s1;
		return s;
	}

	public void selectByRect(int y1, int y2) {
		if (y1 > y2) {
			final int y = y1;
			y1 = y2;
			y2 = y;
		}

		int n = getRowCount();
		int r1, r2;
		r1 = r2 = -1;
		for (int i = 0; i < n; i++) {
			final Rectangle rc = getRowBounds(i);
			final double y = rc.getY();
			final double h = rc.getHeight();
			if ((y <= y2) && ((y + h) >= y1))
				if (r1 < 0)
					r1 = r2 = i;
				else
					r2 = i;
		}

		n = (r2 - r1) + 1;
		if (n >= 0) {
			final int[] t = new int[n];
			for (int i = 0; i < n; i++)
				t[i] = r1 + i;
			setSelectionRows(t);
		} else
			clearSelection();
	}

	@Override
	public void setEditable(final boolean flag) {
		super.setEditable(flag);

	}

	public TreeMProof(final DrawPanelFrame gx, final PanelDraw dd,
			final DrawPanelExtended dpp) {
		gxInstance = gx;
		dpane = dd;
		dp = dpp;

		top = new DefaultMutableTreeNode();
		init_top();

		model = new DefaultTreeModel(top);
		setModel(model);

		getSelectionModel().setSelectionMode(
				TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);

		final PanelTreeCellOpaqueRender treeRender = new PanelTreeCellOpaqueRender();
		treeRender.setOpaque(false);
		setCellRenderer(treeRender);
		editor = new TreeCellOPaqueEditor(gxInstance);
		setCellEditor(editor);
		setEditable(true);

		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					showPopupMenu(TreeMProof.this, e.getX(), e.getY());
					return;
				}

				final int n = TreeMProof.this.getRowCount();
				final Rectangle r = TreeMProof.this.getRowBounds(n - 1);
				if (e.getY() > (r.getY() + r.getHeight()))
					TreeMProof.this.setSelectionRow(-1);
				else {
					final TreePath path = TreeMProof.this.getPathForLocation(
							e.getX(), e.getY());
					if (path != null) {
						final DefaultMutableTreeNode n1 = (DefaultMutableTreeNode) path
								.getLastPathComponent();
						final mnode n2 = (mnode) n1.getUserObject();
						dp.setUndoStructForDisPlay(n2.getLastUndo(), true);
						if ((e.getClickCount() > 1) && (n1 == top)) {
						}
					}
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON3) {
					isButtonDown = true;
					x1 = e.getX();
					y1 = e.getY();
					x2 = x1;
					y2 = y1;
					selectByRect(y1, y2);
				}
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				isButtonDown = false;
				x2 = e.getX();
				y2 = e.getY();
				x1 = y1 = x2 = y2 = 0;
				TreeMProof.this.repaint();
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
			}

			@Override
			public void mouseExited(final MouseEvent e) {
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(final MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3)
					return;

				if (isButtonDown) {
					x2 = e.getX();
					y2 = e.getY();
					selectByRect(y1, y2);
					TreeMProof.this.repaint();
				}
			}

			@Override
			public void mouseMoved(final MouseEvent e) {
			}
		});

		addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(final TreeSelectionEvent e) {
				final TreePath path = e.getNewLeadSelectionPath();
				if (path != null) {
					final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
							.getLastPathComponent();
					if (node != null) {
						final Object obj = node.getUserObject();
						if ((obj != null) && (obj instanceof mnode)
								&& !TreeMProof.this.isEditing()) {
							final mnode n = (mnode) obj;
							dp.flashmnode(n);
							dpane.repaint();
						}
					}
				}
			}
		});
		createpopupMenu();
		setForeground(Color.white);
		setBackground(Color.white);
	}

	public void createpopupMenu() {
		popup = new mpopup();

	}

	public void showPopupMenu(final JComponent comp, final int x, final int y) {
		final int n = getSelectionCount();
		popup.setMul(n);
		popup.show(comp, x, y);
	}

	/*
	 * private mnode getSelectedMnode() { TreePath path =
	 * this.getSelectionPath(); if (path == null) { return null; }
	 * DefaultMutableTreeNode nd = (DefaultMutableTreeNode) path.
	 * getLastPathComponent(); if (nd == null) { return null; } return (mnode)
	 * nd.getUserObject(); }
	 */

	public void expandSelectedNode() {
		final TreePath path = getSelectionPath();
		if (path == null)
			return;
		final DefaultMutableTreeNode nd = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		if (nd == null)
			return;
		final mnode n = (mnode) nd.getUserObject();
		if (n == null)
			return;
		if (n.objSize() != 1)
			return;

		if (n.size() != 0) {
			nd.removeAllChildren();
			n.clear();
			model.reload();

		}

		if (n.size() == 0) {
			final ArrayList<UndoStruct> v = n.getUndoList();
			if (v.size() > 1) {
				for (int i = 0; i < v.size(); i++) {
					final UndoStruct u = v.get(i);
					final mdraw d = new mdraw(u.toString());
					d.adddrawStruct(u);
					final mnode n1 = new mnode();
					n1.add(d);
					final DefaultMutableTreeNode t = new DefaultMutableTreeNode(
							n1);
					nd.add(t);
					n.addChild(n1);
				}
				model.reload();
				expandPath(path);
			}
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

	}

	public void deleteRow() {
		final TreePath path = getSelectionPath();
		if (path == null)
			return;
		final DefaultMutableTreeNode nd = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		if (nd == null)
			return;

		final mnode n = (mnode) nd.getUserObject();
		if (n == null)
			return;
		final DefaultMutableTreeNode p = (DefaultMutableTreeNode) nd
				.getParent();
		if (p != null) {
			cancelEditing();
			final mnode n2 = (mnode) p.getUserObject();
			n2.remove(n);
			loadmtree(topm);
			setEditorLastRow();
		}
	}

	public void combineSelection() {
		final TreePath[] paths = getSelectionPaths();
		if (paths.length <= 1)
			return;
		cancelEditing();
		DefaultMutableTreeNode parent = null;
		for (final TreePath path : paths) {
			final DefaultMutableTreeNode nd = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			if ((nd == null) || (nd == top))
				return;
			parent = (DefaultMutableTreeNode) nd.getParent();
			// break;
		}
		final mnode n = (mnode) parent.getUserObject();
		if (n == null)
			return;
		final mnode nx = new mnode();

		int id = -1;
		int dk = 1;
		for (final TreePath path : paths) {
			final DefaultMutableTreeNode nd = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			final mnode n1 = (mnode) nd.getUserObject();
			nd.removeFromParent();
			n1.setIndex(dk++);
			final int k = n.remove(n1);
			if (id == -1)
				id = k;

			nx.add(n1);
		}
		n.add(id, nx);
		loadmtree(topm);
	}

	public void expandSelection() {
		final TreePath path = getSelectionPath();
		if (path == null)
			return;
		final DefaultMutableTreeNode nd = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		if (nd == null)
			return;
		nd.getUserObject();
	}

	// public void addToProve(ArrayList<?> v) {
	// }

	public void stepEnd() {
		while (rstep > 0)
			step();
		rstep = -1;
	}

	public boolean isMStepEnd() {
		return rstep == (getRowCount() - 1);

	}

	public boolean isMStepMid() {
		return (rstep >= 0) && (rstep < getRowCount());
	}

	public void step() {
		dp.clearFlash();

		if (rstep < 0)
			run_to_begin();
		else if (rstep >= (getRowCount() - 1)) {
			rstep = -1;
			setSelectionRow(-1);
			dp.runto();
			dp.clearFlash();
		} else {
			final DefaultMutableTreeNode d = (DefaultMutableTreeNode) getPathForRow(
					rstep).getLastPathComponent();
			final mnode n = (mnode) d.getUserObject();
			final DefaultMutableTreeNode d1 = (DefaultMutableTreeNode) getPathForRow(
					rstep + 1).getLastPathComponent();
			// mnode n1 = (mnode) d1.getUserObject();
			dp.run_to_prove(n.getFirstUndo(), getLastUndo(d1));
			rstep++;
			setSelectionRow(rstep);
		}
		dpane.repaint();
	}

	public void run_to_begin() {
		dp.UndoPure();
		rstep = 0;
	}

	public void run_to_end() {
		dp.redo();
		rstep = getRowCount() - 1;
		setSelectionRow(rstep);
	}

	public UndoStruct getLastUndo(final DefaultMutableTreeNode d) {
		final TreePath path = new TreePath(d.getPath());
		if (!d.isLeaf() && isCollapsed(path)) {
			final int n = d.getChildCount();
			for (int i = n - 1; i >= 0; i--) {
				final DefaultMutableTreeNode d1 = (DefaultMutableTreeNode) d
						.getChildAt(i);
				final UndoStruct u = getLastUndo(d1);
				if (u != null)
					return u;
			}
		} else {
			final mnode n1 = (mnode) d.getUserObject();
			return n1.getLastUndo();
		}
		return null;
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension dm = super.getPreferredSize();
		return dm;
	}

	public void clearAll() {
		cancelEditing();
		top.removeAllChildren();
		init_top();
		model.reload();
		setEditorLastRow();
	}

	public void addUndoObject(final UndoStruct u) {
		if (u.isNodeValued()) {
			if (top.getChildCount() == 1) {
				final DefaultMutableTreeNode nd = (DefaultMutableTreeNode) top
						.getChildAt(0);
				final mnode m = (mnode) nd.getUserObject();
				if (m.objSize() == 1) {
					final mdraw d = new mdraw(u.toString());
					d.adddrawStruct(u);
					m.add(d);
					m.addUndo(u);
					reload();
					setEditorLastRow();
					return;
				}
			}

			final mnode n = new mnode();
			final mdraw d = new mdraw(u.toString());
			d.adddrawStruct(u);
			n.add(d);
			n.addUndo(u);
			this.addNewNode(n);

		} else {
			final TreePath path = getPathForRow(getRowCount() - 1);
			if (path == null)
				return;
			final DefaultMutableTreeNode nd = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			final mnode md = (mnode) nd.getUserObject();
			md.addUndo(u);
		}
	}

	class mpopup extends JPopupMenu implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8530815397800209783L;
		private final JMenuItem bd, bd1, ba, ba1, bc;

		public mpopup() {
			JMenuItem item = new JMenuItem(getLanguage(3101, "Delete"));

			bd = item;
			add(item);
			item.setActionCommand("Delete");
			item.addActionListener(this);
			bd1 = item = new JMenuItem(getLanguage(3102, "Delete This Row"));
			item.addActionListener(this);
			add(item);
			item.setActionCommand("DTR");
			addSeparator();
			ba = item = new JMenuItem(getLanguage(3103, "Add A New Row"));
			item.addActionListener(this);
			add(item);
			item.setActionCommand("AANR");
			ba1 = item = new JMenuItem(getLanguage(3104, "Append A Term"));
			item.addActionListener(this);
			add(item);
			item.setActionCommand("AAT");
			bc = item = new JMenuItem(
					getLanguage(3105, "Combine Selected Rows"));
			item.addActionListener(this);
			add(item);
			item.setActionCommand("CSR");
		}

		public void setMul(final int n) {
			if (n == 0) {
				bd.setEnabled(false);
				bd1.setEnabled(false);
				ba.setEnabled(true);
				ba1.setEnabled(true);
				bc.setEnabled(false);
			} else if (n > 1) {
				bd.setEnabled(false);
				bd1.setEnabled(false);
				ba.setEnabled(true);
				ba1.setEnabled(true);
				bc.setEnabled(true);
			} else {
				bd.setEnabled(true);
				bd1.setEnabled(true);
				ba.setEnabled(true);
				ba1.setEnabled(true);
				bc.setEnabled(false);
			}
		}

		@Override
		public void show(final Component invoker, final int x, final int y) {
			super.show(invoker, x, y);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final String command = e.getActionCommand();
			if (command.equals("CSR"))
				combineSelection();
			else if (command.equals("Delete")) {
				final mnode n = editor.getEditorValue();
				if (n != null)
					if (n.objSize() >= 1) {
						if (n.removeLast())
							cancelEditing();
					} else
						deleteRow();
			} else if (command.equals("DTR"))
				deleteRow();
			else if (command.equals("AAT")) {

			} else if (command.equals("AANR"))
				TreeMProof.this.addNewNode(new mnode());
		}

	}
}



class msymbol extends mobject {
	final static ImageIcon EQQ = DrawPanelFrame
			.createImageIcon("images/symbol/eqq.gif");
	final static ImageIcon EQ = DrawPanelFrame.createImageIcon("images/symbol/eq.gif");
	final static ImageIcon EXISTS = DrawPanelFrame
			.createImageIcon("images/symbol/exist.gif");
	final static ImageIcon FOREVERY = DrawPanelFrame
			.createImageIcon("images/symbol/for_every.gif");
	final static ImageIcon INFINITY = DrawPanelFrame
			.createImageIcon("images/symbol/infinity.gif");
	final static ImageIcon NEQEVER = DrawPanelFrame
			.createImageIcon("images/symbol/neqever.gif");
	final static ImageIcon NOTEQ = DrawPanelFrame
			.createImageIcon("images/symbol/noteq.gif");
	final static ImageIcon SIM = DrawPanelFrame
			.createImageIcon("images/symbol/sim.gif");
	final static ImageIcon EQSIM = DrawPanelFrame
			.createImageIcon("images/symbol/eq_sim.gif");
	final static ImageIcon LESS = DrawPanelFrame
			.createImageIcon("images/symbol/less.gif");
	final static ImageIcon TRI = DrawPanelFrame
			.createImageIcon("images/symbol/triangle.gif");
	final static ImageIcon ANGLE = DrawPanelFrame
			.createImageIcon("images/symbol/angle.gif");
	final static ImageIcon PARA = DrawPanelFrame
			.createImageIcon("images/symbol/para.gif");
	final static ImageIcon PERP = DrawPanelFrame
			.createImageIcon("images/symbol/perp.gif");

	public static String[] cSprefix = { "because", "hence" };
	static private ArrayList<ImageIcon> vlist = null;
	int type1;

	public static void createAllIcons() {
		vlist = new ArrayList<ImageIcon>();
		for (final String element : cSprefix) {
			final ImageIcon icon = DrawPanelFrame.createImageIcon("images/dtree/"
					+ element + ".gif");
			if (icon == null)
				UtilityMiscellaneous.print("Can not find image : " + element);
			else
				vlist.add(icon);
		}
	}

	public static void fillJComboBox(JComboBox<ImageIcon> jcb) {
		if (jcb != null && vlist != null) {
			for (ImageIcon icon : vlist) {
				jcb.addItem(icon);
			}
		}
	}

	public static ImageIcon getSymbolIcon(final int k) {
		return ((vlist != null) && (vlist.size() > k)) ? (ImageIcon) vlist
				.get(k) : null;
	}

	public msymbol(final int t) {
		super(SYMBOL);
		type1 = t;
	}

	public int getSymbolType() {
		return type1;
	}

	public void setSymbolType(final int t) {
		type1 = t;
	}

	@Override
	public String typeString() {
		return super.typeString() + String.valueOf(type1);
	}

	public ImageIcon getImage() {
		return vlist.get(type1);
	}

	@Override
	public void Load(final DataInputStream in, final DrawPanel dp)
			throws IOException {
		super.Load(in, dp);
		type1 = in.readInt();
	}

	@Override
	public void Save(final DataOutputStream out) throws IOException {
		super.Save(out);
		// out.writeInt(type);
		out.writeInt(type1);
	}
}

class mprefix extends mobject {

	public static int GIVEN = 0;
	public static int TOPROVE = 1;

	public static String[] cSprefix = { "Given: ", "To Prove: ", "In", "and",
		"Similarly,", " Q.E.D." };
	private int type1;

	public mprefix(final int type) {
		super(PREFIX);
		type1 = type;
	}

	public void setPrefixType(final int t) {
		type1 = t;
	}

	public int getPrefixType() {
		return type1;
	}

	public void setText(final String s) {
	}
	
	@Override
	public String typeString() {
		return super.typeString() + String.valueOf(type1);
	}

	@Override
	public String toString() {
		if (type1 < cSprefix.length)
			return cSprefix[type1];
		else
			return "????";
	}

	@Override
	public void Load(final DataInputStream in, final DrawPanel dp)
			throws IOException {
		super.Load(in, dp);
		type1 = in.readInt();
		if (UtilityMiscellaneous.version_load_now < 0.037)
			if (type1 == 5)
				type1 = 2;
			else if (type1 == 7)
				type1 = 3;
			else if (type1 == 8)
				type1 = 4;
			else if (type1 == 10)
				type1 = 5;
	}

	@Override
	public void Save(final DataOutputStream out) throws IOException {
		super.Save(out);
		out.writeInt(type1);
	}
}

class mdraw extends mobject {

	private final ArrayList<UndoStruct> vunlist = new ArrayList<UndoStruct>();
	private String str = "";

	public mdraw() {
		super(DRAW);
	}

	public mdraw(final String s) {
		super(DRAW);
		str = s;
	}

	public void getAllUndoStruct(final ArrayList<UndoStruct> v) {
		if (v != null)
			v.addAll(vunlist);
	}

	public void setText(final String s) {
		str = s;
	}

	public String getText() {
		return str;
	}

	public void adddrawStruct(final UndoStruct undo) {
		if (!vunlist.contains(undo))
			vunlist.add(undo);
	}

	public UndoStruct getUndoStruct() {
		final int n = vunlist.size();
		if (n == 0)
			return null;
		return vunlist.get(n - 1);
	}

	public int getdrawCount() {
		return vunlist.size();
	}

	@Override
	public String toString() {
		if (str != null)
			return str;
		else if (!vunlist.isEmpty()) {
			String s = "";
			for (int i = 0; i < vunlist.size(); i++)
				s += vunlist.get(i).toString();
			return s;
		} else
			return "NULL";
	}

	@Override
	public void Load(final DataInputStream in, final DrawPanel dp)
			throws IOException {
		super.Load(in, dp);
		final int n = in.readInt();
		for (int i = 0; i < n; i++) {
			final int id = in.readInt();
			final UndoStruct u = dp.getUndoById(id);
			if (u != null)
				vunlist.add(u);
		}

		str = ReadString(in);
	}

	@Override
	public void Save(final DataOutputStream out) throws IOException {
		super.Save(out);
		final int n = vunlist.size();
		out.writeInt(n);
		for (int i = 0; i < n; i++) {
			final UndoStruct u = vunlist.get(i);
			out.writeInt(u.m_id);
		}
		WriteString(out, str);
	}
}

class mtext extends mobject {
	private String str = "";

	public mtext() {
		super(TEXT);
	}

	public mtext(final String s) {
		super(TEXT);
		str = s;
	}

	@Override
	public String toString() {
		return str;
	}

	public void setString(final String s) {
		str = s;
	}

	@Override
	public void Load(final DataInputStream in, final DrawPanel dp)
			throws IOException {
		super.Load(in, dp);
		str = ReadString(in);
	}

	@Override
	public void Save(final DataOutputStream out) throws IOException {
		super.Save(out);
		WriteString(out, str);
	}
}

class mdrobj extends mobject {
	final public static int LINE = 0;
	final public static int TRIANGLE = 1;
	final public static int CIRCLE = 2;
	final public static int SQUARE = 3;
	final public static int AREA = 4;
	final public static int ANGLE = 5;
	final public static int PARALLELOGRAM = 6;
	// public static int VALUE = 7;

	final public static int RECTANGLE = 7;

	final public static int QUADRANGLE = 8;
	final public static int TRAPEZOID = 9;

	final static String[] pStrings = { "line", "triangle", "circle", "square",
		"area", "angle", "parallelogram", "rectangle", "quadrangle",
	"trapezoid" };

	final static String[] tipStrings = { "Please select two points",
		"Please select three non-collinear points",
		"Please select three points", "Please select four points",
		"Please select N(N >=3) points", "Please select three points",
		"Please select four points", "Please select four points",
	"Please select four points" };

	private static ArrayList<ImageIcon> vlist = new ArrayList<ImageIcon>();
	private ArrayList<GEPoint> objlist = new ArrayList<GEPoint>();
	int type1;

	@Override
	public String typeString() {
		return super.typeString() + String.valueOf(type1);
	}
	
	static void createAllIcons() {
		for (final String pString : pStrings) {
			final ImageIcon icon = DrawPanelFrame.createImageIcon("images/dtree/"
					+ pString + ".gif");
			if (icon != null)
				vlist.add(icon);
			else
				UtilityMiscellaneous.print("Can not find object icon " + pString);
		}
	}

	public void fillJComboBox(JComboBox<GEPoint> jcb) {
		if (jcb != null && vlist != null) {
			for (final GEPoint p : objlist) {
				jcb.addItem(p);
			}
		}
	}

	public String getTip() {
		if ((type1 >= 0) && (type1 < tipStrings.length))
			return tipStrings[type1];
		else
			return "";
	}

	public boolean check_valid() {
		switch (type1) {
		case LINE:
			return (objlist.size() == 2) && (objlist.get(0) != objlist.get(1));
		case TRIANGLE:
		case ANGLE:
			return (objlist.size() == 3) && (objlist.get(0) != objlist.get(1))
					&& (objlist.get(0) != objlist.get(2))
					&& (objlist.get(1) != objlist.get(2));
		case CIRCLE:
			return (objlist.size() == 3) && (objlist.get(1) != objlist.get(2));
		case SQUARE:
			return ck_pt4() && ck_square();

		case PARALLELOGRAM:
			return ck_pt4() && ck_parallelogram();
		case RECTANGLE:
			return ck_pt4() && ck_rectangle();
		case TRAPEZOID:
			return ck_pt4() && ck_trapezoid();
		case QUADRANGLE:
			return ck_pt4();
		}
		return true;
	}

	public boolean ck_pt4() {
		if (objlist.size() != 4)
			return false;

		final GEPoint p1 = objlist.get(0);
		final GEPoint p2 = objlist.get(1);
		final GEPoint p3 = objlist.get(2);
		final GEPoint p4 = objlist.get(3);
		return (p1 != p2) && (p1 != p3) && (p2 != p3) && (p1 != p4)
				&& (p2 != p4) && (p3 != p4);
	}

	public boolean ck_parallelogram() {
		final GEPoint p1 = objlist.get(0);
		final GEPoint p2 = objlist.get(1);
		final GEPoint p3 = objlist.get(2);
		final GEPoint p4 = objlist.get(3);
		return DrawPanelBase.check_para(p1, p2, p3, p4)
				&& DrawPanelBase.check_para(p1, p4, p2, p3);
	}

	public boolean ck_square() {
		final GEPoint p1 = objlist.get(0);
		final GEPoint p2 = objlist.get(1);
		final GEPoint p3 = objlist.get(2);
		// CPoint p4 = objlist.get(3);

		return DrawPanelBase.check_eqdistance(p1, p2, p2, p3) && ck_rectangle();
	}

	public boolean ck_trapezoid() {
		final GEPoint p1 = objlist.get(0);
		final GEPoint p2 = objlist.get(1);
		final GEPoint p3 = objlist.get(2);
		final GEPoint p4 = objlist.get(3);
		return DrawPanelBase.check_para(p1, p2, p3, p4)
				|| DrawPanelBase.check_para(p1, p4, p2, p3);
	}

	public boolean ck_rectangle() {
		final GEPoint p1 = objlist.get(0);
		final GEPoint p2 = objlist.get(1);
		final GEPoint p3 = objlist.get(2);
		// CPoint p4 = objlist.get(3);

		return DrawPanelBase.check_perp(p1, p2, p2, p3) && ck_parallelogram();
	}

	public static ImageIcon getImageIcon(final int k) {
		if ((k < 0) || (k >= vlist.size()))
			return null;

		return vlist.get(k);
	}

	public static ImageIcon getImageIconFromName(final String s) {
		if (s == null)
			return null;

		if (s.equalsIgnoreCase("line") || s.equalsIgnoreCase("number"))
			return null;

		if (s.equalsIgnoreCase("triangle") || s.equalsIgnoreCase("tri"))
			return vlist.get(1);
		if (s.equalsIgnoreCase("circle") || s.equalsIgnoreCase("cir"))
			return vlist.get(2);
		if (s.equalsIgnoreCase("square"))
			return vlist.get(3);
		if (s.equals("area"))
			return vlist.get(4);
		if (s.equalsIgnoreCase("parallelogram"))
			return vlist.get(6);

		if (s.equalsIgnoreCase("para"))
			return msymbol.PARA;
		if (s.equalsIgnoreCase("perp"))
			return msymbol.PERP;
		if (s.equalsIgnoreCase("angle"))
			return msymbol.ANGLE;

		if (s.equalsIgnoreCase("rectangle"))
			return vlist.get(7);
		if (s.equalsIgnoreCase("quadrangle"))
			return vlist.get(8);

		if (s.equalsIgnoreCase("trapezoid"))
			return vlist.get(9);
		return null;
	}

	public int getObjectNum() {
		return objlist.size();
	}

	public GEPoint getObject(final int id) {
		if (id < objlist.size())
			return objlist.get(id);
		return null;
	}

	public static int getPtAcount(final int t) {
		if (t == 0)
			return 2;
		if (t == 1)
			return 3;
		if (t == 2)
			return 3;
		if (t == 3)
			return 4;
		if (t == 4)
			return 4;
		if (t == 5)
			return 3;
		if (t == 6)
			return 4;

		if ((t == 7) || (t == 8) || (t == 9))
			return 4;
		return 0;
	}

	public boolean isPolygon() {
		return type1 != 0;
	}

	public ImageIcon getImageWithoutLine() {
		if ((type1 == 0) || (type1 >= vlist.size()))
			return null;
		return vlist.get(type1);
	}

	public ImageIcon getImage() {
		if (type1 >= vlist.size())
			return null;
		return vlist.get(type1);
	}

	public void setType1(final int t) {
		type1 = t;
	}

	public int getType1() {
		return type1;
	}

	public mdrobj(final int t) {
		super(DOBJECT);
		type1 = t;
	}

	public void clear() {
		objlist.clear();
	}

	public void add(final GEPoint obj) {
		if (obj != null)
			objlist.add(obj);
	}

	@Override
	public String toString() {
		String s1 = "";
		for (int i = 0; i < objlist.size(); i++)
			s1 += objlist.get(i);
		return s1;
	}

	@Override
	public void Load(final DataInputStream in, final DrawPanel dp)
			throws IOException {
		super.Load(in, dp);
		type1 = in.readInt();
		final int n = in.readInt();
		for (int i = 0; i < n; i++) {
			final int id = in.readInt();
			objlist.add(dp.getPointById(id));
		}

	}

	@Override
	public void Save(final DataOutputStream out) throws IOException {
		super.Save(out);
		out.writeInt(type1);
		out.writeInt(objlist.size());
		for (int i = 0; i < objlist.size(); i++) {
			final GEPoint p = objlist.get(i);
			out.writeInt(p.m_id);
		}
	}
}

class mnode extends ArrayList<mnode> {
	private static final long serialVersionUID = 3638736415600588685L;

	private int index = -1;

	ArrayList<UndoStruct> vundolist = new ArrayList<UndoStruct>();
	ArrayList<mobject> vlist = new ArrayList<mobject>();

	public mnode() {
		super();
	}

	public mnode(int i, ArrayList<mobject> molist) {
		super();
		index = i;
		vlist = molist;
	}

	public boolean containsUndo(final UndoStruct u) {
		return vundolist.contains(u);
	}

	public void setIndex(final int k) {
		index = k;
	}

	public int remove(final mnode m) {
		for (int i = 0; i < size(); i++)
			if (m == get(i)) {
				remove(i);
				return i;
			}
		return -1;
	}

	public boolean removeLast() {
		if (vlist.isEmpty())
			return false;
		final int n = vlist.size();
		vlist.remove(n - 1);
		return true;
	}

	public final int getIndex() {
		return index;
	}

	public void addChild(final mnode node) {
		super.add(node);
	}

	public void add(final mobject node) {
		vlist.add(node);
	}

	public void addUndo(final UndoStruct un) {
		vundolist.add(un);
	}

	public void replace(final mobject a, final mobject obj1) {
		if (!vlist.contains(a))
			return;
		final int size = vlist.size();
		for (int i = 0; i < size; i++)
			if (vlist.get(i) == a) {
				vlist.remove(i);
				vlist.add(i, obj1);
				break;
			}
	}

	public int objSize() {
		return vlist.size();
	}

	public ArrayList<UndoStruct> getUndoList() {
		final ArrayList<UndoStruct> v = new ArrayList<UndoStruct>();
		v.addAll(vundolist);
		return v;
	}

	public mnode getChild(final int id) {
		return get(id);
	}

	public mobject getObject(final int id) {
		return vlist.get(id);
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < vlist.size(); i++)
			s += vlist.get(i);
		return s;
	}

	public ArrayList<UndoStruct> getAllUndoStruct() {
		final ArrayList<UndoStruct> v1 = new ArrayList<UndoStruct>();
		v1.addAll(vundolist);
		return v1;
	}

	public UndoStruct getFirstUndo() {
		if (vundolist.isEmpty())
			return null;
		return vundolist.get(0);
	}

	public UndoStruct getLastUndo() {
		if (vundolist.isEmpty())
			return getUndoFromDraw();
		final int n = vundolist.size();
		return vundolist.get(n - 1);
	}

	public UndoStruct getUndoFromDraw() {
		for (final mobject o : vlist)
			if (o instanceof mdraw) {
				final mdraw d = (mdraw) o;
				return d.getUndoStruct();
			}
		return null;
	}

	public void Load(final DataInputStream in, final DrawPanel dp)
			throws IOException {
		final int n1 = in.readInt();
		final int n2 = in.readInt();
		final int n3 = in.readInt();
		for (int i = 0; i < n1; i++) {
			final UndoStruct u = dp.getUndoById(in.readInt());
			if (u != null)
				vundolist.add(u);
		}
		for (int i = 0; i < n2; i++) {
			final mobject o = mobject.load(in, dp);
			if (o != null)
				vlist.add(o);
		}
		for (int i = 0; i < n3; i++) {
			final mnode nd = new mnode();
			nd.Load(in, dp);
			this.add(nd);
		}
	}

	public void Save(final DataOutputStream out) throws IOException {
		out.writeInt(vundolist.size());
		out.writeInt(vlist.size());
		out.writeInt(size());
		for (final UndoStruct u : vundolist)
			out.writeInt(u.m_id);
		for (final mobject obj : vlist)
			obj.Save(out);
		for (final mnode node : this)
			node.Save(out);
	}

	public static mnode createFromXMLDocument(Element rootElement) {
		assert (rootElement != null);
		mnode newMnode = null;

		NodeList nl = rootElement.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node nn = nl.item(i);
			if (nn != null && nn instanceof Element && ((Element)nn).getTagName().equalsIgnoreCase("Node")) {
				Element thisElement = (Element)nn;
				// int index = GExpert.safeParseInt(thisElement.getAttribute("index"), 0);
				String sIndices = thisElement.getAttribute("undo_info").trim();
				try {
					String[] indices = sIndices.split(" ");
					for (int ii = 0; ii < indices.length; ++ii) {
						Integer.parseInt(indices[ii]);
						// TODO: Figure out what to do with the undo indices.
					}
				} catch (NumberFormatException e) {
					return null; // Returning null signifies that this part of the XML doc was malformed.
				}

				String sTypes = thisElement.getAttribute("types").trim();
				ArrayList<mobject> molist = new ArrayList<mobject>();

				while (!sTypes.isEmpty()) {
					if (!sTypes.startsWith("("))
						return null;
					//sTypes = sTypes.substring(1);
					int k = sTypes.indexOf(')');
					if (k <= 0)
						return null;
					String sThisType = sTypes.substring(1, k);
					sTypes = sTypes.substring(k+1).trim();
					String[] codes = sThisType.split(" ");
					int[] intcodes = new int[codes.length];
					for (int ii = 0; ii < codes.length; ++ii) {
						try {
							int n = Integer.parseInt(codes[ii]);
							intcodes[ii] = n;
						} catch (NumberFormatException e) {
							return null; // Returning null signifies that this part of the XML doc was malformed.
						}
					}
					mobject mo = mobject.createObject(intcodes);
					if (mo == null)
						return null;
					molist.add(mo);
				}

				String s = thisElement.getTagName();
				assert(s.startsWith("Node:"));
				s = s.substring(5);
				Integer I = Integer.parseInt(s);
				if (I == null)
					return null;
				newMnode = new mnode(I, molist);
				NodeList nnl = thisElement.getChildNodes();
				for (int j = 0; j < nnl.getLength(); ++j) {
					Node nd = nnl.item(j);
					if (nd != null && nd instanceof Element) {
						mnode child = createFromXMLDocument((Element)nd);
						if (child == null)
							return null;
						newMnode.addChild(child);
					}
				}

			}
		}
		return newMnode;
	}	

	public void saveIntoXMLDocument(Element rootElement) {
		assert (rootElement != null);
		if (rootElement != null) {
			final Document doc = rootElement.getOwnerDocument();
			Element thisElement = doc.createElement("Node");
			rootElement.appendChild(thisElement);

			thisElement.setAttribute("index", String.valueOf(this.getIndex()));
			
			String s = "";
			for (final Object u : vundolist)
				s += String.valueOf(((UndoStruct)u).m_id) + " ";
			thisElement.setAttribute("undo_info", s);

			s = "";
			for (final Object mo : vlist)
				s += "(" + ((mobject)mo).typeString() + ") ";
			if (!s.isEmpty())
				thisElement.setAttribute("types", s.trim());

			for (final Object nodeO : this) {
				mnode node = (mnode)nodeO;
				if (node != null && node.getIndex() >= 0) {
					node.saveIntoXMLDocument(thisElement);
				}
			}
		}
	}
}

class mobject {
	static String[] pStrings = { "Text", "Keywords", "Symbol", "Assertion",
		"Object", "Draw", "Construction", "Equation", "Rule" };

	final static int NONE = 0;
	final static int TEXT = 1;
	final static int PREFIX = 2;
	final static int SYMBOL = 3;
	final static int ASSERT = 4;
	final static int DOBJECT = 5;
	final static int DRAW = 6;
	final static int CONCLUSION = 7;
	final static int EQUATION = 8;
	final static int RULE = 9;

	int type;

	public mobject(final int t) {
		type = t;
	}

	public int getType() {
		return type;
	}

	public void setType(final int t) {
		type = t;
	}

	@Override
	public String toString() {
		return "     ";
	}
	
	public String typeString() {
		return String.valueOf(type);
	}

	public static mobject createObject(int...t) {
		if (t.length > 0) {
			switch (t[0]) {
			case 0:
				return new mtext("  ");
			case 1:
				return (t.length > 0) ? new mprefix(t[1]) : null;
			case 2:
				return (t.length > 0) ? new msymbol(t[1]) : null;
			case 3:
				return (t.length > 0) ? new massertion(t[1]) : null;
			case 4:
				return (t.length > 0) ? new mdrobj(t[1]) : null;
			case 5:
				return new mdraw("");
			case 7: {
				final mequation eq = new mequation();
				eq.addTerm(new meqterm(-1, new mdrobj(0)));
				return eq;
			}
			case 8:
				return (t.length > 0) ? new mrule(t[1]) : null;
			}
		}
		return null;
	}

	protected static void WriteString(final DataOutputStream out, final String s)
			throws IOException {
		out.writeInt(s.length());
		out.writeChars(s);
	}

	protected static void WriteFont(final DataOutputStream out, final Font f)
			throws IOException {
		final String s = f.getName();
		WriteString(out, s);
		out.writeInt(f.getStyle());
		out.writeInt(f.getSize());
	}

	protected static String ReadString(final DataInputStream in)
			throws IOException {
		final int size = in.readInt();
		if (size == 0)
			return new String("");
		String s = new String();
		for (int i = 0; i < size; i++)
			s += in.readChar();
		return s;
	}

	public static mobject load(final DataInputStream in, final DrawPanel dp)
			throws IOException {
		final int t = in.readInt();
		switch (t) {
		case mobject.TEXT: {
			final mobject m = new mtext();
			m.Load(in, dp);
			return m;
		}
		case mobject.PREFIX: {
			final mprefix m = new mprefix(0);
			m.Load(in, dp);
			return m;
		}
		case mobject.SYMBOL: {
			final msymbol m = new msymbol(0);
			m.Load(in, dp);
			return m;
		}
		case mobject.ASSERT: {
			final massertion m = new massertion(0);
			m.Load(in, dp);
			return m;
		}
		case mobject.DOBJECT: {
			final mdrobj m = new mdrobj(0);
			m.Load(in, dp);
			return m;
		}
		case mobject.DRAW: {
			final mdraw m = new mdraw();
			m.Load(in, dp);
			return m;
		}
		case mobject.EQUATION: {
			final mequation eq = new mequation();
			eq.Load(in, dp);
			return eq;
		}
		case mobject.RULE: {
			final mrule r = new mrule(0);
			r.Load(in, dp);
			return r;
		}
		}
		return null;
	}

		public void Load(final DataInputStream in, final DrawPanel dp) throws IOException {
			type = in.readInt();
		}

	public void Save(final DataOutputStream out) throws IOException {
		out.writeInt(type);
		out.writeInt(type);
	}
}

class mrule extends mobject {
	int rindex;
	public static String[] cStrings = { "Rule1", "Rule2", "Rule3", "SAS",
		"AAS", "SSS", "ASA" };

	public mrule(final int n) {
		super(RULE);
		rindex = n;
	}

	@Override
	public String toString() {
		if ((rindex < 0) || (rindex >= cStrings.length))
			return " by Rule?";
		return " by " + cStrings[rindex];
	}

	@Override
	public String typeString() {
		return super.typeString() + String.valueOf(rindex);
	}

	public String getRuleName() {
		final int n = rindex + 1;
		return "Rule" + n;
	}

	public int getRuleIndex() {
		return rindex;
	}

	public void setRuleIndex(final int n) {
		rindex = n;
	}

	@Override
	public void Load(final DataInputStream in, final DrawPanel dp)
			throws IOException {
		super.Load(in, dp);
		rindex = in.readInt();
	}

	@Override
	public void Save(final DataOutputStream out) throws IOException {
		super.Save(out);
		out.writeInt(rindex);
	}

}

class mequation extends mobject {
	private final ArrayList<meqterm> vlist = new ArrayList<meqterm>();

	public mequation() {
		super(EQUATION);
	}

	public void clearAll() {
		vlist.clear();
	}

	public int getTermCount() {
		return vlist.size();
	}

	public void addTerm(final meqterm t) {
		vlist.add(t);
	}

	public meqterm getTerm(final int index) {
		return vlist.get(index);
	}

	@Override
	public void Load(final DataInputStream in, final DrawPanel dp)
			throws IOException {
		super.Load(in, dp);
		final int n = in.readInt();
		for (int i = 0; i < n; i++)
			vlist.add(meqterm.Load(in, dp));

	}

	@Override
	public void Save(final DataOutputStream out) throws IOException {
		super.Save(out);
		out.writeInt(vlist.size());
		for (int i = 0; i < vlist.size(); i++) {
			final meqterm t = vlist.get(i);
			t.Save(out);
		}

	}
}

class meqterm {
	public static String[] cStrings = { " + ", " - ", " * ", " / ", " = ",
		" > ", " >= ", " < ", " <= ", " //= " };
	int etype;
	mdrobj obj;

	public meqterm() {
		etype = -1;
		obj = null;
	}

	public boolean isEqFamily() {
		return etype >= 4;
	}

	public boolean isPolygon() {
		return obj.isPolygon();
	}

	public meqterm(final int t, final mdrobj o) {
		etype = t;
		obj = o;
	}

	public void setEType(final int t) {
		etype = t;
	}

	public void setObject(final mdrobj d) {
		obj = d;
	}

	public int getEType() {
		return etype;
	}

	public mdrobj getObject() {
		return obj;
	}

	public static meqterm Load(final DataInputStream in, final DrawPanel dp)
			throws IOException {
		final int t = in.readInt();
		final mdrobj o = new mdrobj(0);
		in.readInt();
		o.Load(in, dp);
		return new meqterm(t, o);

	}

	public void Save(final DataOutputStream out) throws IOException {
		out.writeInt(etype);
		obj.Save(out);
	}

}

class massertion extends mobject {
	public static String[] cStrings = { "Collinear", "Parallel",
		"Perpendicular", "Midpoint", "Eqdistant", "Cyclic", "Eqangle",
		"Congruent", "Similar", "Distance Less", "Angle Less",
		"Concurrent", "Perp-Bisector", "Parallelogram",

		"Right Triangle", "Isosceles Triangle", "Iso-Right Triangle",
		"Equilateral Triangle", "Trapezoid", "Rectangle", "Square",
		"Between", "Angle Inside", "Angle Outside", "Triangle Inside",
		"Para Inside", "Opposite Inside", "Same Side", "Convex" };

	final public static int COLL = 0;
	final public static int PARA = 1;
	final public static int PERP = 2;
	final public static int MID = 3;
	final public static int EQDIS = 4;
	final public static int CYCLIC = 5;
	final public static int EQANGLE = 6;
	final public static int CONG = 7;
	final public static int SIM = 8;
	final public static int DISLESS = 9;
	final public static int ANGLESS = 10;
	final public static int CONCURRENT = 11;
	final public static int PERPBISECT = 12;
	final public static int PARALLELOGRAM = 13;

	final public static int R_TRIANGLE = 14;
	final public static int ISO_TRIANGLE = 15;
	final public static int R_ISO_TRIANGLE = 16;
	final public static int EQ_TRIANGLE = 17;
	final public static int TRAPEZOID = 18;
	final public static int RECTANGLE = 19;
	final public static int SQUARE = 20;
	final public static int BETWEEN = 21;
	final public static int ANGLE_INSIDE = 22;
	final public static int ANGLE_OUTSIDE = 23;
	final public static int TRIANGLE_INSIDE = 24;
	final public static int PARA_INSIDE = 25;
	final public static int OPPOSITE_SIDE = 26;
	final public static int CONVEX = 28;
	final public static int SAME_SIDE = 27;

	private final ArrayList<Object> objlist = new ArrayList<Object>();
	private int type1;

	public massertion(final int t) {
		super(ASSERT);
		type1 = t;
	}

	public int getAssertionType() {
		return type1;
	}

	public void setAssertionType(final int t) {
		type1 = t;
	}

	@Override
	public String typeString() {
		return super.typeString() + String.valueOf(type1);
	}

	public boolean ShowType() {
		if ((type1 == 0) || (type1 == 3))
			return true;
		return false;
	}

	public String getShowString1() {
		if ((type1 == 0) || (type1 == 3))
			return toString();
		switch (type1) {
		case R_TRIANGLE:
			return plmn(0, 3) + " is a " + cStrings[type1].toLowerCase();
		case R_ISO_TRIANGLE:
		case ISO_TRIANGLE:
		case EQ_TRIANGLE:
			return plmn(0, 3) + " is an " + cStrings[type1].toLowerCase();
		case TRAPEZOID:
			return plmn(0, 4) + " is a trapezoid";
		case RECTANGLE:
			return plmn(0, 4) + " is a rectangle";
		case BETWEEN:
			return plmn(0, 1) + " is between " + plmn(1, 3);
		case ANGLE_INSIDE:
			return plmn(0, 1) + " is inside";
		case ANGLE_OUTSIDE:
			return plmn(0, 1) + " is outside";
		case TRIANGLE_INSIDE:
			return plmn(0, 1) + " is inside";
		case PARA_INSIDE:
			return plmn(0, 1) + " is inside";
		case OPPOSITE_SIDE:
			return plmn(0, 2) + " is on the opposite side of " + plmn(2, 4);
		case SAME_SIDE:
			return plmn(0, 2) + " is on the same side of " + plmn(2, 4);
		case CONVEX:
			return plmn() + " is convex";

		default:
			final int t = objlist.size();
			final int n = t / 2;
			String s = "";
			for (int i = 0; i < n; i++)
				s += objlist.get(i);
			return s;
		}
	}

	public String plmn() {
		return plmn(0, objlist.size());
	}

	public String plmn(final int n, final int m) {
		String s = "";
		if ((n < 0) || (m > objlist.size()))
			return s;

		for (int i = n; i < m; i++)
			s += objlist.get(i);
		return s;
	}

	public String getShowString2() {
		if ((type1 == 0) || (type1 == 3))
			return null;
		switch (type1) {
		case ANGLE_INSIDE:
			return plmn(1, 4);
		case ANGLE_OUTSIDE:
			return plmn(1, 4);
		case TRIANGLE_INSIDE:
			return plmn(1, 4);
		case PARA_INSIDE:
			return plmn(1, 5);
		default:
			final int t = objlist.size();
			final int n = t / 2;
			String s = "";
			for (int i = n; i < t; i++)
				s += objlist.get(i);
			return s;
		}
	}

	public ImageIcon getImageIcon() {
		if ((type1 == COLL) || (type1 == MID))
			return null;
		if (type1 == PARA)
			return msymbol.PARA;
		if (type1 == PERP)
			return msymbol.PERP;
		if ((type1 == EQDIS) || (type1 == EQANGLE))
			return msymbol.EQ;
		if (type1 == SIM)
			return msymbol.SIM;
		if (type1 == CONG)
			return msymbol.EQSIM;
		if ((type1 == DISLESS) || (type1 == ANGLESS))
			return msymbol.LESS;
		return msymbol.EQQ;
	}

	public int getobjNum() {
		return objlist.size();
	}

	public void clearObjects() {
		objlist.clear();
	}

	public void addAll(final massertion a) {
		objlist.clear();
		final int n = a.getobjNum();
		for (int i = 0; i < n; i++) {
			final Object obj = a.getObject(i);
			if (obj != null)
				objlist.add(obj);
		}
	}

	public void addObject(final GEPoint p) {
		if (p != null)
			objlist.add(p);
	}

	public void addObject(final Object p) {
		if (p != null)
			objlist.add(p);

	}

	public Object getObject(final int id) {
		return objlist.get(id);
	}

	@Override
	public String toString() {
		String s1 = cStrings[type1];
		switch (type1) {
		case COLL:
		case MID:
		case CYCLIC:
			return s1 + " " + getTStrings();
		case CONCURRENT: {
			int n = objlist.size();
			n = n / 2;
			for (int i = 0; i < n; i++) {
				if (i == 0)
					s1 += " ";
				else
					s1 += ",";
				s1 = s1 + objlist.get(i * 2) + objlist.get((i * 2) + 1);
			}
			return s1;
		}
		case PARALLELOGRAM:
			return getSSTrings() + " is a " + s1;
		case PERPBISECT:
			final int n = objlist.size();
			if (n < 4)
				return s1;
			return objlist.get(0).toString() + objlist.get(1).toString()
					+ " is the " + s1 + " of " + objlist.get(2).toString()
					+ objlist.get(3).toString();
		default:
			UtilityMiscellaneous.print("massertion type:" + s1 + " not defined.");
			return s1;
		}

	}

	public Object getLabelObject(final int d) {
		switch (type1) {
		case 0:
			if (d == 0)
				return getTStrings() + " are collinear";
			else
				return null;
		case 1:
		case 2:
		case 4:
		case 9:
			break;
		case 5:
			break;
		case 6:
		}
		return null;

	}

	public String sr(final int i) {
		return objlist.get(i).toString();
	}

	public void addItem(final Object p) {
		if (p != null)
			objlist.add(p);
	}

	public boolean checkValid() {
		if (((type == MID) || (type == COLL)) && (objlist.size() == 3))
			return true;
		if (((type == SIM) || (type == EQANGLE) || (type == CONG))
				&& (objlist.size() == 6))
			return true;
		if (type == CONCURRENT)
			return objlist.size() == 6;

		if (objlist.size() == 4)
			return true;
		return false;
	}

	public String getSSTrings() {
		String s = "";
		for (int i = 0; i < objlist.size(); i++)
			s += objlist.get(i);
		return s;

	}

	public String getTStrings() {
		String s = "";
		for (int i = 0; i < objlist.size(); i++) {
			if (i != 0)
				s += ",";
			s += objlist.get(i);
		}
		return s;
	}

	@Override
	public void Load(final DataInputStream in, final DrawPanel dp)
			throws IOException {
		super.Load(in, dp);
		type1 = in.readInt();
		final int n = in.readInt();
		for (int i = 0; i < n; i++) {
			final int id = in.readInt();
			objlist.add(dp.getPointById(id));
		}

	}

	@Override
	public void Save(final DataOutputStream out) throws IOException {
		super.Save(out);
		out.writeInt(type1);
		out.writeInt(objlist.size());
		for (int i = 0; i < objlist.size(); i++) {
			final GEPoint p = (GEPoint) objlist.get(i);
			if (p == null)
				out.writeInt(-1);
			else
				out.writeInt(p.m_id);
		}
	}

}

class mTreeModel implements TreeModel {
	private final mnode node;

	public mTreeModel(final mnode node) {
		this.node = node;
	}

	@Override
	public Object getRoot() {
		return node;
	}

	@Override
	public Object getChild(final Object parent, final int index) {
		final mnode d = (mnode) parent;
		return d.get(index);
	}

	@Override
	public int getChildCount(final Object parent) {
		return ((mnode) parent).size();
	}

	@Override
	public boolean isLeaf(final Object node) {
		return ((mnode) node).size() == 0;
	}

	@Override
	public void valueForPathChanged(final TreePath path, final Object newValue) {
	}

	@Override
	public int getIndexOfChild(final Object parent, final Object child) {
		return ((mnode) parent).indexOf(child);
	}

	@Override
	public void addTreeModelListener(final TreeModelListener l) {
	}

	@Override
	public void removeTreeModelListener(final TreeModelListener l) {
	}

}
