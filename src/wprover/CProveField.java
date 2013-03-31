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
public class CProveField {
	private boolean HEAD = false;
	CProveText cpname = null;
	ArrayList<CProveText> clist;
	ArrayList<CProveText> vlist;

	CProveText pselect = null;
	CProveText pundo = null;
	CProveText pex = null;

	Point pt;
	int rstep = -1;
	int rmid = 0;
	private boolean bIsSemanticallyValid = true;

	public CProveField() {
		pt = new Point(20, 20);
		clist = new ArrayList<CProveText>();
		vlist = new ArrayList<CProveText>();
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

	public CProveField(Element thisElement) {
		this();
		bIsSemanticallyValid = true;
		
		int x = GExpert.safeParseInt(thisElement.getAttribute("x"), 20);
		int y = GExpert.safeParseInt(thisElement.getAttribute("y"), 20);
		pt = new Point(x, y);
		
		NodeList nl = thisElement.getElementsByTagName("head");
		int nn = (nl != null) ? nl.getLength() : 0;
		if (nn == 0)
			HEAD = false;
		if (nl != null) {
			Node headNode = nl.item(0);
			cpname = new CProveText("", "theorem");
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
					CProveText provTxt = new CProveText();
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
					CProveText provTxt = new CProveText();
					provTxt.loadFromXMLDocument((Element)child);
					clist.add(provTxt);
				}
			}
		}

		expandAll();
	}

	public CProveField(final ArrayList<cond> v, final boolean head) {
		this();
		HEAD = head;
		pselect = null;
		if (head) {
			cpname = new CProveText("", "theorem");
			cpname.setFont(new Font("Dialog", Font.PLAIN, 18));
			cpname.setMessageColor(Color.black);
		}

		CProveText ct = null;
		final int size = v.size();
		if (size == 0)
			return;
		if (head) {
			cond conclus = v.get(size - 1);
			ct = new CProveText(conclus.getText(), "Conclusion:  ");
			vlist.add(ct);
			for (int i = 0; i < size; i++) {
				final cond co = v.get(i);
				ct = new CProveText(v, co, i, false);
				vlist.add(ct);
			}
		} else {
			for (final cond co : v) {
				ct = new CProveText(v, co, -1, false);
				vlist.add(ct);
			}
			if (size == 1)
				ct.setMessage("Since " + ct.getMessage());
		}

		expandAll();
	}

	public CProveField(final ArrayList<UndoStruct> ulist) {
		this();
		if (ulist != null) {
			int index = 0;
			for (final UndoStruct u : ulist) {
				final CProveText ct = new CProveText(u, index++);
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
		for (final CProveText cp1 : vlist)
			if (cp1.isVisible())
				cp1.setIndex(index++);
	}

	public void genProve(cond co) {
		int i = 0;
		while (co != null) {
			CProveText ct = new CProveText(null, co, i++, false);
			vlist.add(ct);
			co = co.nx;
		}
	}

	public void genCondition(final ArrayList<String> v) {
		for (final String s : v)
			clist.add(new CProveText("", s));

		if (!clist.isEmpty())
			 clist.get(0).setHead("Given:  " + clist.get(0).getHead());
	}


	public void expandAll() {
		for (final CProveText cp : clist)
			cp.toggleExpanded();
		for (final CProveText cp : vlist)
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
		final CProveText cp = clist.get(0);
		return cp.getFont().getSize();
	}

	public void setFontSize(final int size) {
		for (final CProveText cp : clist)
			cp.setFontSize(size);
		for (final CProveText cp : vlist)
			cp.setFontSize(size);
		pselect = null;
	}

	public CProveText getSelect() {
		return pselect;
	}

	public boolean undo_to_head(final drawProcess dp) {
		if (HEAD) {
			pundo = pselect = clist.get(0);

			for (final ListIterator<CProveText> iter = vlist.listIterator(vlist
					.size()); iter.hasPrevious();) {
				final CProveText cpt = iter.previous();
				cpt.undo_to_head(dp);
			}
			for (final ListIterator<CProveText> iter = clist.listIterator(clist
					.size()); iter.hasPrevious();) {
				final CProveText cpt = iter.previous();
				cpt.undo_to_head(dp);
			}

			dp.setUndoStructForDisPlay(pundo.getUndoStruct(), true);

		} else {
			for (final ListIterator<CProveText> iter = vlist.listIterator(vlist
					.size()); iter.hasPrevious();) {
				final CProveText cpt = iter.previous();
				cpt.undo_to_head(dp);
			}
			for (final ListIterator<CProveText> iter = clist.listIterator(clist
					.size()); iter.hasPrevious();) {
				final CProveText cpt = iter.previous();
				cpt.undo_to_head(dp);
			}
		}
		return !vlist.isEmpty();
	}

	public boolean run_to_begin(final drawProcess dp) {
		if (HEAD) {
			pselect = clist.get(0);
			pundo = pselect;

			int index = vlist.size() - 1;
			for (int i = index; i >= 0; i--) {
				final CProveText cpt = vlist.get(i);
				cpt.run_to_begin(dp);
			}

			index = clist.size() - 1;
			if (index < 0)
				return false;
			for (int i = index; i >= 1; i--) {
				final CProveText cpt = clist.get(i);
				cpt.run_to_begin(dp);
			}
			dp.setUndoStructForDisPlay(pselect.getUndoStruct(), true);

		} else {
			int index = vlist.size() - 1;

			for (int i = index; i >= 0; i--) {
				final CProveText cpt = vlist.get(i);
				cpt.run_to_begin(dp);
			}
			index = clist.size() - 1;

			for (int i = index; i >= 0; i--) {
				final CProveText cpt = clist.get(i);
				cpt.run_to_begin(dp);
			}
		}
		return true;
	}

	public boolean undo_default(final drawProcess dp) {
		if (HEAD) {
			if (vlist.size() == 0)
				return false;

			pselect = vlist.get(0);
			pundo = pselect;
			final int index = vlist.size() - 1;
			if (index < 0)
				return false;
			for (int i = index; i >= 1; i--) {
				final CProveText cpt = vlist.get(i);
				cpt.undo_default(dp);
			}
			final CProveText cpt = vlist.get(0);

			dp.setUndoStructForDisPlay(cpt.getUndoStruct(), true);

		} else {
			final int index = vlist.size() - 1;
			if (index < 0)
				return false;

			for (int i = index; i >= 0; i--) {
				final CProveText cpt = vlist.get(i);
				cpt.undo_default(dp);
			}
		}
		return true;
	}

	public boolean run_to_end(final drawProcess dp) {
		while (true)
			if (!this.next_prove_step(dp))
				return true;
	}

	public void reGenerateAll() {
		if (HEAD)
			reGenerateIndex();

		for (final CProveText cp : clist) {
			cp.regenerateAll();
		}

		for (final CProveText cp : vlist) {
			cp.regenerateAll();
		}
	}

	public CProveText redo_invisible_head(final drawProcess dp) {
		if (!vlist.isEmpty()) {
			final CProveText ct = vlist.get(0);
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

	public void next(final drawTextProcess dp) {
		final CProveText ct = fd_text(++rstep);
		pselect = ct;
		if (ct != null) {
			dp.addaux(ct);
		} else {
			dp.resetAux();
			rstep = rmid;
		}
	}

	public CProveText fd_text(final int index) {
		for (final CProveText cp : clist) {
			final CProveText ct = cp.fd_text(index);
			if (ct != null)
				return ct;
		}

		for (final CProveText cp : vlist) {
			final CProveText ct = cp.fd_text(index);
			if (ct != null)
				return ct;
		}
		return null;
	}

	public void setStepRowDefault() {
		for (final CProveText cp : clist) {
			cp.setStepRowDefault();
		}

		for (final CProveText cp : vlist) {
			cp.setStepRowDefault();
		}
	}

	public boolean next_prove_step(final drawProcess dp) {
		if (HEAD) {
			final ArrayList<GraphicEntity> vv = new ArrayList<GraphicEntity>();

			final CProveText ct = next_prove_step(dp, pundo, false);
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

	public void setSelectedUndo(final UndoStruct u, final drawProcess dp) {
		final CProveText ct = pselect = findPText(u);
		final ArrayList<GraphicEntity> vv = new ArrayList<GraphicEntity>();
		if (ct != null) {
			ct.getFlashObjectList(vv, dp);
			pundo = ct.redo_invisible_head(dp);
			if (pselect != pundo)
				pundo.getUndoStruct().getAllObjects(dp, vv);
			dp.setObjectListForFlash(vv);
		}
	}

	public CProveText findPText(final UndoStruct un) {
		if (un == null)
			return null;

		for (int i = 0; i < clist.size(); i++) {
			final CProveText cp = clist.get(i);
			{
				final CProveText k = cp.findPText(un);
				if (k != null)
					return k;
			}

		}

		for (int i = 0; i < vlist.size(); i++) {
			final CProveText cp = vlist.get(i);
			{
				final CProveText k = cp.findPText(un);
				if (k != null)
					return k;
			}

		}
		return null;
	}

	public CProveText next_prove_step(final drawProcess dp,
			final CProveText cpt, boolean find) {

		for (final CProveText cp : clist) {
			final CProveText t = cp.next_prove_step(dp, cpt, find);
			if (t != null)
				return t;
		}

		for (final CProveText cp : vlist) {
			final CProveText t = cp.next_prove_step(dp, cpt, find);
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
			CProveText.resetRow();
			setStepRowDefault();

			drawAStep(cpname, p, g2);
			{
				final double tw = cpname.getWidth();
				if (tw > wd)
					wd = tw;
			}

			p.setLocation(p.getX(), p.getY() + 5);
			// p.setY(p.getY() + 5);

			for (final CProveText cp : clist) {
				drawAStep(cp, p, g2);
				final double tw = cp.getWidth();
				if (tw > wd)
					wd = tw;
			}

			rmid = CProveText.getRow();
			if (rstep < 0)
				rstep = rmid;

			p.setLocation(dx, p.getY());
			for (int i = 0; i < vlist.size(); i++) {
				final CProveText cp = vlist.get(i);
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
			for (final CProveText cp : clist) {
				drawAStep(cp, p, g2);
				final double tw = cp.getWidth();
				if (tw > wd)
					wd = tw;
			}

			for (final CProveText cp : vlist) {
				drawAStep(cp, p, g2);
				final double tw = cp.getWidth();
				if (tw > wd)
					wd = tw;
			}
		}

		wd += 5;
		if (HEAD) {
			cpname.setWidth(wd);
			for (final CProveText cp : clist) {
				cp.setWidth(wd);
			}
		}
		for (final CProveText cp : vlist) {
			cp.setWidth(wd);
		}
	}

	public void move(final double x, final double y) {
		pt.setLocation(pt.getX() + (int) x, pt.getY() + (int) y);
	}

	public CProveText mouseMove(final double x, final double y) {
		CProveText fd = null;
		for (int i = 0; i < clist.size(); i++) {
			final CProveText ct = clist.get(i);
			final CProveText cpt = ct.mouseMove(x, y);
			if (cpt != null)
				fd = cpt;
		}
		for (int i = 0; i < vlist.size(); i++) {
			final CProveText ct = vlist.get(i);
			final CProveText cpt = ct.mouseMove(x, y);
			if (cpt != null)
				fd = cpt;
		}

		pex = fd;
		return fd;
	}

	public CProveText select(final double x, final double y,
			final boolean on_select) {
		CProveText sel = null;

		if (HEAD)
			if (cpname.select(x, y))
				sel = cpname;

		CProveText ts;
		for (final CProveText ct : clist) {
			if ((ts = ct.selectAll(x, y)) != null)
				sel = ts;
		}

		for (final CProveText ct : vlist) {
			if ((ts = ct.selectAll(x, y)) != null)
				sel = ts;
		}

		if (HEAD)
			pselect = sel;
		return sel;
	}

	public void expandProveNode(final double x, final double y) {
		final CProveText cpt = select(x, y, true);
		if (cpt != null)
			cpt.toggleExpanded();
	}

	public void clearSelection() {
		for (final CProveText ct : clist) {
			ct.clearSelection();
		}

		for (final CProveText ct : vlist) {
			ct.clearSelection();
		}

	}

	public void drawAStep(final CProveText cp, final Point p,
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
				final CProveText ct = clist.get(i);
				if (i == 0)
					ct.SavePS(fp, stype, 1);
				else
					ct.SavePS(fp, stype, 0);
			}

			for (int i = 0; i < vlist.size(); i++) {
				final CProveText ct = vlist.get(i);
				if (i == 0)
					ct.SavePS(fp, stype, 2);
				else
					ct.SavePS(fp, stype, 0);
			}
		} else {
			for (int i = 0; i < clist.size(); i++) {
				final CProveText ct = clist.get(i);
				ct.SavePS(fp, stype, 0);
			}

			for (int i = 0; i < vlist.size(); i++) {
				final CProveText ct = vlist.get(i);
				ct.SavePS(fp, stype, 0);
			}
		}

	}

	// /////////////////////////////////////////////////////////
	public void saveText(final DataOutputStream out, final int space)
			throws IOException {
		for (final CProveText ct : vlist)
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

			for (final CProveText pt : clist)
				if (pt != null)
					pt.SaveIntoXMLDocument(e);

			e = doc.createElement("v_text");
			elementThis.appendChild(e);

			for (final CProveText pt : vlist)
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
		for (final CProveText ct : clist)
			ct.Save(out);

		size = vlist.size();
		out.writeInt(size);
		for (final CProveText ct : vlist)
			ct.Save(out);

		out.writeInt((int) pt.getX());
		out.writeInt((int) pt.getY());
	}

	public void Load(final DataInputStream in, final drawProcess dp)
			throws IOException {

		HEAD = in.readBoolean();
		if (HEAD) {
			cpname = new CProveText();
			cpname.Load(in, dp);
		}

		int size = in.readInt();
		clist = new ArrayList<CProveText>();
		for (int i = 0; i < size; i++) {
			final CProveText ct = new CProveText();
			ct.Load(in, dp);
			clist.add(ct);
		}

		size = in.readInt();
		vlist = new ArrayList<CProveText>();

		for (int i = 0; i < size; i++) {
			final CProveText ct = new CProveText();
			ct.Load(in, dp);
			vlist.add(ct);
		}

		final int px = in.readInt();
		final int py = in.readInt();
		pt = new Point(px, py);
	}

}
