package wprover;


import gprover.gib;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.*;


public class DialogRule extends DialogBase implements ChangeListener, ActionListener, MouseListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2025805803172152292L;
	private DrawPanelFrame gxInstance;
    private JTree treeGDD, treeFA;
    private JTabbedPane pane;

    public DialogRule(DrawPanelFrame owner) {
        super(owner.getFrame());

        gxInstance = owner;
        this.setTitle("GDD method");

        Object rootNodes[] = new Object[6];
        int i = 0;
        ArrayList<Rule> vrule = new ArrayList<Rule>();
        RuleList.getAllGDDRules(vrule);

        rootNodes[0] = createNameVector("Rules related to Parallel lines", vrule, i, i += 3);
        rootNodes[1] = createNameVector("Rules related to Perpendicular lines", vrule, ++i, i += 3);
        rootNodes[2] = createNameVector("Rules related to Circles", vrule, ++i, i += 6);
        rootNodes[3] = createNameVector("Rules related to Angles", vrule, ++i, i += 6);
        rootNodes[4] = createNameVector("Rules related to Triangles", vrule, ++i, i += 14);
        rootNodes[5] = createNameVector("Other rules", vrule, ++i, i += 5);

        NamedVector rootVector = new NamedVector("Root", rootNodes);
        treeGDD = new JTree(rootVector.toArray());

        CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
        treeGDD.setCellRenderer(renderer);

//        tree.setCellEditor(new CheckBoxNodeEditor(tree));
        treeGDD.setEditable(false);
        treeGDD.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(treeGDD);
        pane = new JTabbedPane(JTabbedPane.BOTTOM);
        pane.addTab("GDD Method", scrollPane);
        pane.addChangeListener(this);

        ArrayList<Rule> vfull = new ArrayList<Rule>();
        RuleList.getAllFullRules(vfull);
        Object rNodes[] = new Object[1];
        rNodes[0] = createNameVector("Full Angle Method", vfull, 0, 28);
        treeFA = new JTree((new NamedVector("Root", rNodes)).toArray());
        treeFA.setCellRenderer(renderer);
        treeFA.addMouseListener(this);

        JScrollPane scrollPane1 = new JScrollPane(treeFA);
        pane.addTab("Full Angle Method", scrollPane1);

        getContentPane().add(pane, BorderLayout.CENTER);
        expandAll();
        setSize(600, owner.getHeight());
    }

    public void setSelected(int n) {
        pane.setSelectedIndex(n);
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == pane) {
            this.setTitle(pane.getTitleAt(pane.getSelectedIndex()));
        }
    }

    private NamedVector createNameVector(String n, ArrayList<Rule> vlist, int t1, int t2) {
        CheckBoxNode[] list1 = new CheckBoxNode[t2 - t1 + 1];
        createCheckBox(list1, vlist, t1, t2);
        NamedVector v1 = new NamedVector(n, list1);
        return v1;
    }

    private void createCheckBox(CheckBoxNode[] list, ArrayList<Rule> vlist, int t1, int t2) {
        int index = 0;
        for (int i = t1; i < vlist.size() && i <= t2; i++) {
            Rule r = vlist.get(i);
            int t = r.type;
            String sText = new String(r.description);
            if (sText.isEmpty()) {
            	sText = new String(r.head);
	            if (sText.isEmpty())
	            	sText = new String(r.exstring);
            }
            list[index++] = new CheckBoxNode(t, t + ". " + sText, true, r);
        }
    }

    private void expandAll() {
        int n = treeGDD.getRowCount();
        for (int i = n - 1; i >= 0; i--)
            treeGDD.expandRow(i);
        n = treeFA.getRowCount();
        for (int i = n - 1; i >= 0; i--)
            treeFA.expandRow(i);
    }

    public Rule getSelectedRule() {
        JTree tt = (pane.getSelectedIndex() == 0) ? treeGDD : treeFA;
        DefaultMutableTreeNode nd = (DefaultMutableTreeNode) tt.getLastSelectedPathComponent();

        if (nd != null) {
            Object obj = nd.getUserObject();
            if (obj instanceof CheckBoxNode) {
                CheckBoxNode ch = (CheckBoxNode) obj;
                Rule r = ch.getRule();
                return r;
            }
        }
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            JTree tt = (pane.getSelectedIndex() == 0) ? treeGDD : treeFA;

            Rule r = getSelectedRule();
            if (r != null) {
                ppMenu m = new ppMenu(r);
                m.show(tt, e.getX(), e.getY());
            }
        } else {
            if (e.getClickCount() > 1) {
                Rule r = getSelectedRule();
                showRuleDialog(r);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    class CheckBoxNodeRenderer implements TreeCellRenderer {
        private JCheckBox leafRenderer = new JCheckBox();
        private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();
        Color selectionBorderColor, selectionForeground, selectionBackground, textForeground, textBackground;
        CheckBoxNode leafNode = null;

        protected JCheckBox getLeafRenderer() {
            return leafRenderer;
        }

        protected CheckBoxNode getLeafNode() {
            return leafNode;
        }

        public CheckBoxNodeRenderer() {
            Font fontValue;
            fontValue = UIManager.getFont("Tree.font");
            if (fontValue != null) {
                leafRenderer.setFont(fontValue);
            }
            Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
            leafRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));


            selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
            selectionForeground = UIManager.getColor("Tree.selectionForeground");
            selectionBackground = UIManager.getColor("Tree.selectionBackground");
            textForeground = UIManager.getColor("Tree.textForeground");
            textBackground = UIManager.getColor("Tree.textBackground");
            leafRenderer.setBorder(null);
            leafRenderer.addItemListener(new ItemListener() {
		@Override
                public void itemStateChanged(ItemEvent e) {
                }
            });
        }

	@Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded, boolean leaf, int row,
                                                      boolean hasFocus) {

            Component returnValue;
            if (leaf) {
                String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
                leafRenderer.setText(stringValue);
                leafRenderer.setSelected(false);
                leafRenderer.setEnabled(tree.isEnabled());

                if (selected) {
                    leafRenderer.setForeground(selectionForeground);
                    leafRenderer.setBackground(selectionBackground);
                } else {
                    leafRenderer.setForeground(textForeground);
                    leafRenderer.setBackground(textBackground);
                }

                if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                    if (userObject instanceof CheckBoxNode) {
                        CheckBoxNode node = (CheckBoxNode) userObject;
                        leafRenderer.setText(node.getText());
                        leafRenderer.setSelected(node.isSelected());
                        leafNode = node;
                    }
                }
                returnValue = leafRenderer;
            } else {
                returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree,
                        value, selected, expanded, leaf, row, hasFocus);
            }
            return returnValue;
        }
    }

    class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

        /**
		 * 
		 */
		private static final long serialVersionUID = -5251574895624352564L;

		CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();

        ChangeEvent changeEvent1 = null;

        JTree tree;

        public CheckBoxNodeEditor(JTree tree) {
            this.tree = tree;
//            renderer.leafRenderer.addChangeListener();
            renderer.leafRenderer.addChangeListener(new ChangeListener() {
		@Override
                public void stateChanged(ChangeEvent e) {
                    //int n = 0;
                    if (renderer.leafNode != null)
                        renderer.leafNode.updateValue(renderer.leafRenderer.isSelected());
                }

            });
        }

	@Override
        public Object getCellEditorValue() {
            JCheckBox checkbox = renderer.getLeafRenderer();
            CheckBoxNode nd = renderer.getLeafNode();
            if (nd != null && checkbox != null)
                nd.setSelected(checkbox.isSelected());
            return nd;
        }

	@Override
        public boolean isCellEditable(EventObject event) {
            boolean returnValue = false;
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                TreePath path = tree.getPathForLocation(mouseEvent.getX(),
                        mouseEvent.getY());
                if (path != null) {
                    Object node = path.getLastPathComponent();
                    if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
                        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                        Object userObject = treeNode.getUserObject();
                        returnValue = ((treeNode.isLeaf()) && (userObject instanceof CheckBoxNode));
                    }
                }
            }
            return returnValue;
        }

	@Override
        public Component getTreeCellEditorComponent(JTree tree, Object value,
                                                    boolean selected, boolean expanded, boolean leaf, int row) {

            Component editor = renderer.getTreeCellRendererComponent(tree, value,
                    true, expanded, leaf, row, true);

            // editor always selected / focused
            ItemListener itemListener = new ItemListener() {
		@Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    if (stopCellEditing()) {
                        fireEditingStopped();
                    }
                }
            };
            if (editor instanceof JCheckBox) {
                ((JCheckBox) editor).addItemListener(itemListener);
            }

            return editor;
        }
    }

    class CheckBoxNode {

        private String text;
        private boolean selected;
        private int v;
        private Rule rule;

        public CheckBoxNode(int n, String text, boolean selected, Rule rl) {
            this.text = text;
            this.selected = selected;
            this.v = n;
            rule = rl;
        }

        public Rule getRule() {
            return rule;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean newValue) {
            selected = newValue;
        }

        public String getText() {
            return text;
        }

        public void setText(String newValue) {
            text = newValue;
        }

        public void updateValue(boolean r) {
            selected = r;
            gib.setValue(v, r);

        }

	@Override
        public String toString() {
            return getClass().getName() + "[" + text + "/" + selected + "]";
        }
    }

    class NamedVector extends Vector<Object> {
    	private static final long serialVersionUID = -4901889422519392481L;
		String name;

        public NamedVector(String name) {
            this.name = name;
        }

        public NamedVector(String name, CheckBoxNode elements[]) {
            this.name = name;
            for (int i = 0, n = elements.length; i < n; i++) {
                add(elements[i]);
            }
        }
	
        public NamedVector(String name, Object oo[]) {
            this.name = name;
            for (int i = 0, n = oo.length; i < n; i++) {
                add(oo[i]);
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }


    public void showRuleDialog(Rule r) {
        if (r != null) {
            DialogRuleList dlg = new DialogRuleList(gxInstance);
            if (dlg.loadRule(r.isFullRule(), r.type))
                dlg.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cm = e.getActionCommand();
        if (cm.equals("Show Detail")) {
            JMenuItem m1 = (JMenuItem) e.getSource();
            ppMenu m = (ppMenu) m1.getParent();
            Rule r = m.getRule();
            showRuleDialog(r);
        }

    }

    class ppMenu extends JPopupMenu {

		private static final long serialVersionUID = -3810121691461178959L;
		private Rule rule;

        public Rule getRule() {
            return rule;
        }

        public ppMenu(Rule r) {
            super();
            rule = r;

            JMenuItem it = new JMenuItem("Show Detail");
            it.addActionListener(DialogRule.this);
            add(it);
            addSeparator();
            it = new JMenuItem("Enable");
            it.addActionListener(DialogRule.this);
            it.setEnabled(false);
            add(it);
            it = new JMenuItem("Disable");
            it.addActionListener(DialogRule.this);
            it.setEnabled(false);
            add(it);
            addSeparator();
            it = new JMenuItem("Help...");
            it.addActionListener(DialogRule.this);
            it.setEnabled(false);
            add(it);
        }
    }
}
