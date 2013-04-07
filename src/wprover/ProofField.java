package wprover;

import gprover.cond;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by IntelliJ IDEA. User: Ye Date: 2005-8-25 Time: 19:19:11 To change
 * this template use File | Settings | File Templates.
 */
public class ProofField {
	private boolean HEAD = false;
	ProofText cpname = null;
	ArrayList<ProofText> clist;
	ArrayList<ProofText> vlist;

	ProofText pselect = null;
	ProofText pundo = null;
	ProofText pex = null;

	Point pt;
	int rstep = -1;
	int rmid = 0;
	private boolean bIsSemanticallyValid = true;

	public ProofField() {
		pt = new Point(20, 20);
		clist = new ArrayList<ProofText>();
		vlist = new ArrayList<ProofText>();
	}

	/*
	 * public CProveField(drawProcess dp) //undolist; { pt = new Point(20, 20);
	 * 
	 * cpname = new CProveText("", "theorem"); cpname.setFont(new Font("Dialog",
	 * Font.PLAIN, 18)); cpname.setMessageColor(Color.black);
	 * 
	 * clist = new ArrayList<CProveText>(); vlist = new ArrayList<CProveText>();
	 * pselect = null;
	 * 
	 * 
	 * if (dp.undolist != null && !dp.undolist.isEmpty()) { for
	 * (Iterator<UndoStruct> iter = dp.undolist.iterator(); iter.hasNext(); ) {
	 * UndoStruct un = iter.next(); if (un.m_type == UndoStruct.T_TO_PROVE_NODE)
	 * { CProveText cp = new CProveText(un, "To Prove:  "); vlist.add(cp); int
	 * index = 0; while (iter.hasNext()) { UndoStruct un2 = iter.next();
	 * CProveText cp2 = new CProveText(un2, index++); vlist.add(cp2); } }
	 * clist.add(new CProveText(un)); } if (!clist.isEmpty())
	 * (clist.get(0)).setHead("Given:  " + clist.get(0).getHead()); HEAD = true;
	 * expandAll(); } }
	 */

	public ProofField(Element thisElement) {
		this();
		bIsSemanticallyValid = true;
		
		int x = DrawPanelFrame.safeParseInt(thisElement.getAttribute("x"), 20);
		int y = DrawPanelFrame.safeParseInt(thisElement.getAttribute("y"), 20);
		pt = new Point(x, y);
		
		NodeList nl = thisElement.getElementsByTagName("head");
		int nn = (nl != null) ? nl.getLength() : 0;
		if (nn == 0)
			HEAD = false;
		if (nl != null) {
			Node headNode = nl.item(0);
			cpname = new ProofText("", "theorem");
			cpname.setFont(new Font("Dialog", Font.PLAIN, 18));
			cpname.setMessageColor(Color.black);
			cpname.loadFromXMLDocument((Element)headNode);
			HEAD = true;
		}
		pselect = null;

		nl = thisElement.getElementsByTagName("v_text");
		if (nl != null && nl.getLength() > 0) {
			Node vlistNode = nl.item(0);
			NodeList vlistchildren = vlistNode.getChildNodes();
			for (int ii = 0; ii < vlistchildren.getLength(); ++ii) {
				Node child = vlistchildren.item(ii);
				if (child != null && child instanceof Element) {
					ProofText provTxt = new ProofText();
					provTxt.loadFromXMLDocument((Element)child);
					vlist.add(provTxt);
				}
			}
		}
		
		nl = thisElement.getElementsByTagName("c_text");
		if (nl != null && nl.getLength() > 0) {
			Node vlistNode = nl.item(0);
			NodeList vlistchildren = vlistNode.getChildNodes();
			for (int ii = 0; ii < vlistchildren.getLength(); ++ii) {
				Node child = vlistchildren.item(ii);
				if (child != null && child instanceof Element) {
					ProofText provTxt = new ProofText();
					provTxt.loadFromXMLDocument((Element)child);
					clist.add(provTxt);
				}
			}
		}

		expandAll();
	}

	public ProofField(final ArrayList<cond> v, final boolean head) {
		this();
		HEAD = head;
		pselect = null;
		if (head) {
			cpname = new ProofText("", "theorem");
			cpname.setFont(new Font("Dialog", Font.PLAIN, 18));
			cpname.setMessageColor(Color.black);
		}

		ProofText ct = null;
		final int size = v.size();
		if (size == 0)
			return;
		if (head) {
			cond conclus = v.get(size - 1);
			ct = new ProofText(conclus.getText(), "Conclusion:  ");
			vlist.add(ct);
			for (int i = 0; i < size; i++) {
				final cond co = v.get(i);
				ct = new ProofText(v, co, i, false);
				vlist.add(ct);
			}
		} else {
			for (final cond co : v) {
				ct = new ProofText(v, co, -1, false);
				vlist.add(ct);
			}
			if (size == 1)
				ct.setMessage("Since " + ct.getMessage());
		}

		expandAll();
	}

	public ProofField(final ArrayList<UndoStruct> ulist) {
		this();
		if (ulist != null) {
			int index = 0;
			for (final UndoStruct u : ulist) {
				final ProofText ct = new ProofText(u, index++);
				vlist.add(ct);
			}
		}
	}

	/*
	 * public CProveField(cond co, boolean head) { pt = new Point(20, 20); clist
	 * = new ArrayList<CProveText>(); vlist = new ArrayList<CProveText>(); HEAD
	 * = head; pselect = null; if (head) { cpname = new CProveText("",
	 * "theorem"); cpname.setFont(new Font("Dialog", Font.PLAIN, 18));
	 * cpname.setMessageColor(Color.black); }
	 * 
	 * int i = -1; CProveText ct = null; while (co != null) { if (head && i ==
	 * -1) ct = new CProveText(co, "To Prove:  "); else ct = new CProveText(new
	 * ArrayList<cond>(), co, i, true); i++; vlist.add(ct); co = co.nx; }
	 * expandAll();
	 * 
	 * }
	 */

	public final boolean isValid() {
		return bIsSemanticallyValid;
	}
	public void setXY(final int x, final int y) {
		pt.setLocation(x, y);
	}

	public void drag(final double dx, final double dy) {
		if (pselect != null)
			pt.setLocation((int) (pt.getX() + dx), (int) (pt.getY() + dy));
	}

	public void setCaptain(String sname) {
		if (sname != null && sname.endsWith(".gex")) {
			sname = sname.substring(0, sname.length() - 4);
			cpname.setMessage(sname);
		}
	}

	public void reGenerateIndex() {
		if (HEAD) {
			pselect = null;
			if (!vlist.isEmpty())
				vlist.get(0).setVisible(true);
		}

		int index = 0;
		for (final ProofText cp1 : vlist)
			if (cp1.isVisible())
				cp1.setIndex(index++);
	}

	public void genProve(cond co) {
		int i = 0;
		while (co != null) {
			ProofText ct = new ProofText(null, co, i++, false);
			vlist.add(ct);
			co = co.nx;
		}
	}

	public void genCondition(final ArrayList<String> v) {
		for (final String s : v)
			clist.add(new ProofText("", s));

		if (!clist.isEmpty())
			 clist.get(0).setHead("Given:  " + clist.get(0).getHead());
	}


	public void expandAll() {
		for (final ProofText cp : clist)
			cp.toggleExpanded();
		for (final ProofText cp : vlist)
			cp.toggleExpanded();
	}

//	public CProveText getFirstProveNode() {
//		return clist.get(0);
//	}

//	public static CProveText createANewCommentNode() {
//		return new CProveText("", "Click to edit here");
//	}

	public void draw(final Graphics2D g2) {
		final Point p = new Point((int) pt.getX(), (int) pt.getY());
		draw(g2, p);
	}

	public int getFontSize() {
		if (clist.size() == 0)
			return 14;
		final ProofText cp = clist.get(0);
		return cp.getFont().getSize();
	}

	public void setFontSize(final int size) {
		for (final ProofText cp : clist)
			cp.setFontSize(size);
		for (final ProofText cp : vlist)
			cp.setFontSize(size);
		pselect = null;
	}

	public ProofText getSelect() {
		return pselect;
	}

	public boolean undo_to_head(final DrawPanel dp) {
		if (HEAD) {
			pundo = pselect = clist.get(0);

			for (final ListIterator<ProofText> iter = vlist.listIterator(vlist
					.size()); iter.hasPrevious();) {
				final ProofText cpt = iter.previous();
				cpt.undo_to_head(dp);
			}
			for (final ListIterator<ProofText> iter = clist.listIterator(clist
					.size()); iter.hasPrevious();) {
				final ProofText cpt = iter.previous();
				cpt.undo_to_head(dp);
			}

			dp.setUndoStructForDisPlay(pundo.getUndoStruct(), true);

		} else {
			for (final ListIterator<ProofText> iter = vlist.listIterator(vlist
					.size()); iter.hasPrevious();) {
				final ProofText cpt = iter.previous();
				cpt.undo_to_head(dp);
			}
			for (final ListIterator<ProofText> iter = clist.listIterator(clist
					.size()); iter.hasPrevious();) {
				final ProofText cpt = iter.previous();
				cpt.undo_to_head(dp);
			}
		}
		return !vlist.isEmpty();
	}

	public boolean run_to_begin(final DrawPanel dp) {
		if (HEAD) {
			pselect = clist.get(0);
			pundo = pselect;

			int index = vlist.size() - 1;
			for (int i = index; i >= 0; i--) {
				final ProofText cpt = vlist.get(i);
				cpt.run_to_begin(dp);
			}

			index = clist.size() - 1;
			if (index < 0)
				return false;
			for (int i = index; i >= 1; i--) {
				final ProofText cpt = clist.get(i);
				cpt.run_to_begin(dp);
			}
			dp.setUndoStructForDisPlay(pselect.getUndoStruct(), true);

		} else {
			int index = vlist.size() - 1;

			for (int i = index; i >= 0; i--) {
				final ProofText cpt = vlist.get(i);
				cpt.run_to_begin(dp);
			}
			index = clist.size() - 1;

			for (int i = index; i >= 0; i--) {
				final ProofText cpt = clist.get(i);
				cpt.run_to_begin(dp);
			}
		}
		return true;
	}

	public boolean undo_default(final DrawPanel dp) {
		if (HEAD) {
			if (vlist.size() == 0)
				return false;

			pselect = vlist.get(0);
			pundo = pselect;
			final int index = vlist.size() - 1;
			if (index < 0)
				return false;
			for (int i = index; i >= 1; i--) {
				final ProofText cpt = vlist.get(i);
				cpt.undo_default(dp);
			}
			final ProofText cpt = vlist.get(0);

			dp.setUndoStructForDisPlay(cpt.getUndoStruct(), true);

		} else {
			final int index = vlist.size() - 1;
			if (index < 0)
				return false;

			for (int i = index; i >= 0; i--) {
				final ProofText cpt = vlist.get(i);
				cpt.undo_default(dp);
			}
		}
		return true;
	}

	public boolean run_to_end(final DrawPanel dp) {
		while (true)
			if (!this.next_prove_step(dp))
				return true;
	}

	public void reGenerateAll() {
		if (HEAD)
			reGenerateIndex();

		for (final ProofText cp : clist) {
			cp.regenerateAll();
		}

		for (final ProofText cp : vlist) {
			cp.regenerateAll();
		}
	}

	public ProofText redo_invisible_head(final DrawPanel dp) {
		if (!vlist.isEmpty()) {
			final ProofText ct = vlist.get(0);
			if (!ct.isVisible()) {
				dp.redo_step(ct.getUndoStruct());
				return ct;
			}
		}
		return null;
	}

	public void resetStep() {
		rstep = rmid;
	}

	public void next(final DrawPanelExtended dp) {
		final ProofText ct = fd_text(++rstep);
		pselect = ct;
		if (ct != null) {
			dp.addaux(ct);
		} else {
			dp.resetAux();
			rstep = rmid;
		}
	}

	public ProofText fd_text(final int index) {
		for (final ProofText cp : clist) {
			final ProofText ct = cp.fd_text(index);
			if (ct != null)
				return ct;
		}

		for (final ProofText cp : vlist) {
			final ProofText ct = cp.fd_text(index);
			if (ct != null)
				return ct;
		}
		return null;
	}

	public void setStepRowDefault() {
		for (final ProofText cp : clist) {
			cp.setStepRowDefault();
		}

		for (final ProofText cp : vlist) {
			cp.setStepRowDefault();
		}
	}

	public boolean next_prove_step(final DrawPanel dp) {
		if (HEAD) {
			final ArrayList<GraphicEntity> vv = new ArrayList<GraphicEntity>();

			final ProofText ct = next_prove_step(dp, pundo, false);
			if (ct != null) {
				pselect = ct;
				ct.getFlashObjectList(vv, dp);
				pundo = ct.redo_invisible_head(dp);
				if (pselect != pundo)
					pundo.getUndoStruct().getAllObjects(dp, vv);
				dp.setObjectListForFlash(vv);
			} else
				return false;
		}
		return true;
	}

	public void setSelectedUndo(final UndoStruct u, final DrawPanel dp) {
		final ProofText ct = pselect = findPText(u);
		final ArrayList<GraphicEntity> vv = new ArrayList<GraphicEntity>();
		if (ct != null) {
			ct.getFlashObjectList(vv, dp);
			pundo = ct.redo_invisible_head(dp);
			if (pselect != pundo)
				pundo.getUndoStruct().getAllObjects(dp, vv);
			dp.setObjectListForFlash(vv);
		}
	}

	public ProofText findPText(final UndoStruct un) {
		if (un == null)
			return null;

		for (int i = 0; i < clist.size(); i++) {
			final ProofText cp = clist.get(i);
			{
				final ProofText k = cp.findPText(un);
				if (k != null)
					return k;
			}

		}

		for (int i = 0; i < vlist.size(); i++) {
			final ProofText cp = vlist.get(i);
			{
				final ProofText k = cp.findPText(un);
				if (k != null)
					return k;
			}

		}
		return null;
	}

	public ProofText next_prove_step(final DrawPanel dp,
			final ProofText cpt, boolean find) {

		for (final ProofText cp : clist) {
			final ProofText t = cp.next_prove_step(dp, cpt, find);
			if (t != null)
				return t;
		}

		for (final ProofText cp : vlist) {
			final ProofText t = cp.next_prove_step(dp, cpt, find);
			if (t != null)
				return t;
		}
		return null;
	}

	public void draw(final Graphics2D g2, final Point p) {
		final int dx = (int) p.getX();
		// int dy = (int) p.getY();

		double wd = 0;

		if (HEAD && (pselect != null))
			pselect.draw(g2, true);

		if (HEAD) {
			ProofText.resetRow();
			setStepRowDefault();

			drawAStep(cpname, p, g2);
			{
				final double tw = cpname.getWidth();
				if (tw > wd)
					wd = tw;
			}

			p.setLocation(p.getX(), p.getY() + 5);
			// p.setY(p.getY() + 5);

			for (final ProofText cp : clist) {
				drawAStep(cp, p, g2);
				final double tw = cp.getWidth();
				if (tw > wd)
					wd = tw;
			}

			rmid = ProofText.getRow();
			if (rstep < 0)
				rstep = rmid;

			p.setLocation(dx, p.getY());
			for (int i = 0; i < vlist.size(); i++) {
				final ProofText cp = vlist.get(i);
				drawAStep(cp, p, g2);
				final double tw = cp.getWidth();
				if (tw > wd)
					wd = tw;
				if (i == 0)
					p.setLocation(p.getX(), p.getY() + 8);
			}
		}
		// p.setY(p.getY() + 10);

		else {
			for (final ProofText cp : clist) {
				drawAStep(cp, p, g2);
				final double tw = cp.getWidth();
				if (tw > wd)
					wd = tw;
			}

			for (final ProofText cp : vlist) {
				drawAStep(cp, p, g2);
				final double tw = cp.getWidth();
				if (tw > wd)
					wd = tw;
			}
		}

		wd += 5;
		if (HEAD) {
			cpname.setWidth(wd);
			for (final ProofText cp : clist) {
				cp.setWidth(wd);
			}
		}
		for (final ProofText cp : vlist) {
			cp.setWidth(wd);
		}
	}

	public void move(final double x, final double y) {
		pt.setLocation(pt.getX() + (int) x, pt.getY() + (int) y);
	}

	public ProofText mouseMove(final double x, final double y) {
		ProofText fd = null;
		for (int i = 0; i < clist.size(); i++) {
			final ProofText ct = clist.get(i);
			final ProofText cpt = ct.mouseMove(x, y);
			if (cpt != null)
				fd = cpt;
		}
		for (int i = 0; i < vlist.size(); i++) {
			final ProofText ct = vlist.get(i);
			final ProofText cpt = ct.mouseMove(x, y);
			if (cpt != null)
				fd = cpt;
		}

		pex = fd;
		return fd;
	}

	public ProofText select(final double x, final double y,
			final boolean on_select) {
		ProofText sel = null;

		if (HEAD)
			if (cpname.select(x, y))
				sel = cpname;

		ProofText ts;
		for (final ProofText ct : clist) {
			if ((ts = ct.selectAll(x, y)) != null)
				sel = ts;
		}

		for (final ProofText ct : vlist) {
			if ((ts = ct.selectAll(x, y)) != null)
				sel = ts;
		}

		if (HEAD)
			pselect = sel;
		return sel;
	}

	public void expandProveNode(final double x, final double y) {
		final ProofText cpt = select(x, y, true);
		if (cpt != null)
			cpt.toggleExpanded();
	}

	public void clearSelection() {
		for (final ProofText ct : clist) {
			ct.clearSelection();
		}

		for (final ProofText ct : vlist) {
			ct.clearSelection();
		}

	}

	public void drawAStep(final ProofText cp, final Point p,
			final Graphics2D g2) {

		if (cp != null && cp.isVisible()) {
			cp.setCurrentPosition(p);
			if (cp == pselect)
				cp.draw(g2, true);
	
			cp.draw(g2);
			cp.getNextPosition(p);
	
			if (cp.isExpanded()) {
				final int x = (int) p.getX();
				p.setLocation(x + 45, p.getY());
				cp.drawChild(g2, p);
				p.setLocation(x, p.getY());
			}
		}
	}

	// public void removeNode(ArrayList v) {
	// }

	/*
	 * public boolean removeLast() { this.removeLast(); return true; }
	 */

	public void SavePS(final FileOutputStream fp, final int stype)
			throws IOException {
		if (HEAD) {
			fp.write("%draw proof text\n".getBytes());
			fp.write("-60 -100 translate\n/ystep -8 def   /yoff 0 def  /fzoff 10 def\n "
					.getBytes());
			cpname.SavePS(fp, stype, 0);
			for (int i = 0; i < clist.size(); i++) {
				final ProofText ct = clist.get(i);
				if (i == 0)
					ct.SavePS(fp, stype, 1);
				else
					ct.SavePS(fp, stype, 0);
			}

			for (int i = 0; i < vlist.size(); i++) {
				final ProofText ct = vlist.get(i);
				if (i == 0)
					ct.SavePS(fp, stype, 2);
				else
					ct.SavePS(fp, stype, 0);
			}
		} else {
			for (int i = 0; i < clist.size(); i++) {
				final ProofText ct = clist.get(i);
				ct.SavePS(fp, stype, 0);
			}

			for (int i = 0; i < vlist.size(); i++) {
				final ProofText ct = vlist.get(i);
				ct.SavePS(fp, stype, 0);
			}
		}

	}

	// /////////////////////////////////////////////////////////
	public void saveText(final DataOutputStream out, final int space)
			throws IOException {
		for (final ProofText ct : vlist)
			ct.saveText(out, space);
		out.close();
		// return true;
	}

	public void SaveIntoXMLDocument(Element rootElement) {
		assert(rootElement != null);
		if (rootElement != null) {
			Document doc = rootElement.getOwnerDocument();

			Element elementThis = doc.createElement("proof");

    		elementThis.setAttribute("x", String.valueOf((int) pt.getX()));
    		elementThis.setAttribute("y", String.valueOf((int) pt.getY()));

			if (HEAD) {
				Element e = doc.createElement("head");
				elementThis.appendChild(e);
				cpname.SaveIntoXMLDocument(e);
			}
    		
			Element e = doc.createElement("c_text");
			elementThis.appendChild(e);

			for (final ProofText pt : clist)
				if (pt != null)
					pt.SaveIntoXMLDocument(e);

			e = doc.createElement("v_text");
			elementThis.appendChild(e);

			for (final ProofText pt : vlist)
				if (pt != null)
					pt.SaveIntoXMLDocument(e);
		}
	}
	
	public void Save(final DataOutputStream out) throws IOException {

		out.writeBoolean(HEAD);
		if (HEAD)
			cpname.Save(out);

		int size = clist.size();
		out.writeInt(size);
		for (final ProofText ct : clist)
			ct.Save(out);

		size = vlist.size();
		out.writeInt(size);
		for (final ProofText ct : vlist)
			ct.Save(out);

		out.writeInt((int) pt.getX());
		out.writeInt((int) pt.getY());
	}

	public void Load(final DataInputStream in, final DrawPanel dp)
			throws IOException {

		HEAD = in.readBoolean();
		if (HEAD) {
			cpname = new ProofText();
			cpname.Load(in, dp);
		}

		int size = in.readInt();
		clist = new ArrayList<ProofText>();
		for (int i = 0; i < size; i++) {
			final ProofText ct = new ProofText();
			ct.Load(in, dp);
			clist.add(ct);
		}

		size = in.readInt();
		vlist = new ArrayList<ProofText>();

		for (int i = 0; i < size; i++) {
			final ProofText ct = new ProofText();
			ct.Load(in, dp);
			vlist.add(ct);
		}

		final int px = in.readInt();
		final int py = in.readInt();
		pt = new Point(px, py);
	}

}
