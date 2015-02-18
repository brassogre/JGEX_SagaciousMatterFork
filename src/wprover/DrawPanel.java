package wprover;

import gprover.Pro_point;
import gprover.cons;
import gprover.gib;
import gprover.gterm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;

import maths.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DrawPanel extends DrawPanelBase implements Printable, ActionListener {

	final public static int SELECT = 0;
	final public static int MOVE = 34;
	final public static int VIEWELEMENT = 35;

	final public static int TRANSLATE = 40;
	final public static int ZOOM_IN = 41;
	final public static int ZOOM_OUT = 42;
	final public static int SETTRACK = 43;
	final public static int ANIMATION = 60;
	final public static int DEFINEPOLY = 61;
	final public static int MULSELECTSOLUTION = 62;
	final public static int MOVENAME = 63;
	final public static int AUTOSHOWSTEP = 64;
	final public static int EQMARK = 65;
	final public static int PROVE = 66;
	final public static int TRIANGLE = 67;
	final public static int HIDEOBJECT = 68;

	final public static int DRAWTRIALL = 69; //
	final public static int DRAWTRISQISO = 71;

	final public static int PARALLELOGRAM = 72;
	final public static int RECTANGLE = 73;

	final public static int TRAPEZOID = 74;
	final public static int RA_TRAPEZOID = 75;
	final public static int SETEQSIDE = 76;
	final public static int SHOWOBJECT = 77;
	final public static int SETEQANGLE3P = 78;
	final public static int SETCCTANGENT = 79;
	final public static int NTANGLE = 80;
	final public static int SANGLE = 81;
	final public static int RATIO = 82;
	final public static int RAMARK = 83;
	final public static int TRANSFORM = 84;
	final public static int EQUIVALENCE = 85;
	final public static int FREE_TRANSFORM = 86;
	final public static int LOCUS = 87;

	final public static int ARROW = 88;

	final public static int CONSTRUCT_FROM_TEXT = 100;

	ArrayList<UndoStruct> undolist = new ArrayList<UndoStruct>();
	ArrayList<UndoStruct> redolist = new ArrayList<UndoStruct>();
	UndoStruct currentUndo = new UndoStruct(1);

	// CPoint trackPoint = null;
	protected GEPoint CTrackPt = null;

	Animation animate = null;
	ProofField cpfield = null;
	int pfn = 0; // max 4

	protected JPanel panel;
	private GEPoint pSolution = null;
	private ArrayList<GEPoint> solutionlist = new ArrayList<GEPoint>();
	private GEPoint FirstPnt = null;
	private GEPoint SecondPnt = null;
	private GEPoint ThirdPnt = null;

	private int proportion = 0;
	private UndoStruct undo = null;
	private Timer timer = null;
	private int timer_type; // 1: auto-undoredo , 2: prove;

	private boolean bLeftButtonDown = false;
	private boolean isRecal = true;
	private int v1, v2;
	private double vx1, vy1, vangle = 0;
	private double vtrx, vtry = 0;

	private int PreviousAction;
	private final Collection<DiagramUpdater> updaterListeners = new LinkedList<DiagramUpdater>();
	private boolean needSave = false;
	private int save_id = UtilityMiscellaneous.id_count;
	private int CAL_MODE = 0; // 0: MOVEMODE. 1. CAL
	private int docVersion = 0;
	protected int paraCounter = 1;

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	protected gterm gt;
	protected int nd = 1;

	// //////////////
	private UndoStruct U_Obj = null;
	protected boolean status = true;

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void toggleStatus() {
		status = !status;
	}

	public void setCalMode1() {
		CAL_MODE = 1;
	}

	public void setCalMode0() {
		CAL_MODE = 0;
	}

	public gterm gterm() {
		if (gt == null)
			gt = gxInstance.getProofPanel().getConstructionTerm();
		return gt;
	}

	public void clearConstruction() {
		gt = null;
		nd = 1;
	}

	public void resetUndo() {
		currentUndo.id = UtilityMiscellaneous.id_count;
	}

	public String getName() {
		return name;
	}

	public void setRecal(final boolean r) {
		isRecal = r;
	}

	public void setName(final String s) {
		name = s;
	}

	public void stopTrack() {
		CTrackPt = null;
	}

	public void startTrackPt(final GEPoint pt) {
		CTrackPt = pt;

		for (final GETrace tr : tracelist) {
			if (tr.isTracePt(CTrackPt)) {
				final GETrace t = new GETrace(CTrackPt);
				addGraphicEntity(tracelist, t);
				UndoAdded(t.toString());
				return;
			}
		}
	}

	public param getParameterByindex(final int index) {
		for (int i = 0; i < paraCounter; i++) { // TODO: Convert parameter[paraCounter] to a HashMap.
			//			if (parameter[i] == null)
			//				System.out.println("Parameter["+i+"] is null.");
			//			else
			//				System.out.println("Parameter["+i+"] has index " + parameter[i].xindex);
			assert(parameter[i] != null);
			if (parameter[i].xindex == index)
				return parameter[i];
		}
		return null;
	}

	public GEPoint getLastConstructedPoint() {
		return (pointlist.size() == 0) ? null : pointlist.get(pointlist.size() - 1);
	}

	public GEPoint getPointById(final int id) {
		final GraphicEntity c = getObjectById(id);
		return (c instanceof GEPoint) ? (GEPoint) c : null;
	}

	public void getAllConstraints(Collection<Constraint> v) {
		v.addAll(constraintlist);
	}

	public Constraint getConstraintByid(final int id) {
		for (final Constraint cs : constraintlist) {
			if (cs.id == id)
				return cs;
		}
		return null;
	}

	public GELine getLineByid(final int id) {
		return (GELine) getObjectById(id);
	}

	public GECircle getCircleByid(final int id) {
		return (GECircle) getObjectById(id);
	}

	public GETrace getTraceById(final int id) {
		return (GETrace) getObjectById(id);
	}

	public GEAngle getAngleByid(final int id) {
		return (GEAngle) getObjectById(id);
	}

	public void getAllSolidObj(final Collection<Object> v) {
		// This method fills v with all non-NORMAL_TEXT objects.
		if (v != null) {
			final int n = UtilityMiscellaneous.id_count + 1;
			for (int i = 1; i <= n; i++) {
				final Object o = getObjectById(i);
				if (o instanceof GEText) {
					final GEText tt = (GEText) o;
					if (tt.getType() != GEText.NORMAL_TEXT)
						continue;
				}
				if (o != null)
					v.add(o);
			}
		}
	}

	public GraphicEntity getObjectById(final int id) {
		return gemap.get(id);
		//		GraphicEntity cc = getObjectInListById(id, pointlist);
		//		if (cc != null)
		//			return cc;
		//		cc = getObjectInListById(id, linelist);
		//		if (cc != null)
		//			return cc;
		//		cc = getObjectInListById(id, circlelist);
		//		if (cc != null)
		//			return cc;
		//		cc = getObjectInListById(id, anglelist);
		//		if (cc != null)
		//			return cc;
		//		cc = getObjectInListById(id, distancelist);
		//		if (cc != null)
		//			return cc;
		//		cc = getObjectInListById(id, polygonlist);
		//		if (cc != null)
		//			return cc;
		//		cc = getObjectInListById(id, textlist);
		//		if (cc != null)
		//			return cc;
		//		cc = getObjectInListById(id, tracelist);
		//		if (cc != null)
		//			return cc;
		//		cc = getObjectInListById(id, otherlist);
		//		return cc;
	}

	//	public GraphicEntity getObjectInListById(final int id) {
	//		return gemap.get(id);
	//	}

	public static GraphicEntity getObjectInListById(final int id, final Collection<? extends GraphicEntity> v) {
		for (final GraphicEntity cc : v)
			if (cc != null && cc.m_id == id)
				return cc;
		return null;
	}

	public UndoStruct getUndoById(final int id) {
		for (final UndoStruct cc : undolist) {
			final UndoStruct c1 = cc.getUndoStructByid(id);

			if (c1 != null)
				return c1;
		}
		UtilityMiscellaneous.print("Can not find " + id + " in undo list");
		return null;
	}

	public void SetProportionLineAction(final int prop) {
		SetCurrentAction(DrawPanelBase.LRATIO);
		proportion = prop;
	}

	public void addDiagramUpdaterListener(final DiagramUpdater d) {
		if (!updaterListeners.contains(d))
			updaterListeners.add(d);
	}

	public void RemoveDiagramUpdaterListener(final DiagramUpdater d) {
		updaterListeners.remove(d);
	}

	public void SetActionWithPropotion(final int action, final int prop) {
		SetCurrentAction(action);
		proportion = prop;
	}

	public void initialize() {
		CurrentAction = SELECT;
		SelectList.clear();
		CatchList.clear();
		pointlist.clear();
		linelist.clear();
		circlelist.clear();
		clearAllConstraints();
		textlist.clear();
		distancelist.clear();
		tracelist.clear();
		polygonlist.clear();
		otherlist.clear();

		paraCounter = 1;
		FirstPnt = SecondPnt = null;
		bLeftButtonDown = false;
		polylist = null;
		pblist = null;
		anglelist.clear();
		pnameCounter = 0;
		plineCounter = 1;
		pcircleCounter = 1;
		STATUS = 0;
		pSolution = null;
		solutionlist.clear();

		// trackPoint = null;
		undolist.clear();
		redolist.clear();
		UtilityMiscellaneous.id_count = 1;

		currentUndo = new UndoStruct(paraCounter);
		ComboBoxInteger.resetAll();
		DrawData.setDefaultStatus();
		undo = null;
		animate = null;
		cpfield = null;

		clearFlash();
		if ((gxInstance != null) && gxInstance.hasAFrame()) {
			final ToolBarAnimate ac = gxInstance.getAnimateDialog();
			if (ac != null)
				ac.stopA();
		}
		clearConstruction();
		for (int i = 0; i < parameter.length; i++)
			parameter[i] = null;
		for (int i = 0; i < paraBackup.length; i++)
			paraBackup[i] = 0.0;
		CTrackPt = null;

		file = null;
		vx1 = vy1 = 0.0;
		vtrx = vtry = 0;
		vangle = 0.0;
		UtilityMiscellaneous.Reset();
		needSave = false;
		save_id = UtilityMiscellaneous.id_count;
		GeoPoly.clearZ();
		name = "";
		CAL_MODE = 0;
		status = true;
	}

	public void setSavedTag() {
		needSave = false;
		save_id = UtilityMiscellaneous.id_count;
	}

	public boolean isitSaved() {
		return needSave || (save_id >= UtilityMiscellaneous.id_count);
	}

	public Animation getAnimateC() {
		return animate;
	}

	public File getFile() {
		return file;
	}

	public void setFile(final File f) {
		file = f;
	}

	public void getSelectList(final ArrayList<GraphicEntity> list) {
		list.addAll(SelectList);
	}

	public void SetSnap(final boolean snap) {
		SNAP = snap;
	}

	public int getStatus() {
		return STATUS;
	}

	public void setStatus(final int t) {
		STATUS = t;
	}

	public boolean isSnap() {
		return SNAP;
	}

	public void SetGrid(final boolean grid) {
		DRAWGRID = grid;
	}

	public boolean isDrawGrid() {
		return DRAWGRID;
	}

	public void setGridXY(final int n) {
		if (n > 0)
			GridX = GridY = n;
	}

	public void setMeshStep(final boolean add) {
		if (add) {
			GridX += 10;
			GridY += 10;
		} else if (GridX >= 20) {
			GridX -= 10;
			GridY -= 10;
		}
	}

	public TPoly getCopyPolylist() {
		TPoly pl = polylist;
		TPoly plist = null;

		while (pl != null) {
			final TPoly p = new TPoly();
			p.setPoly(PolyBasic.pcopy(pl.getPoly()));
			p.setNext(plist);
			plist = p;

			pl = pl.getNext();
		}
		return plist;
	}

	public TPoly getCopyPolyBacklist() {
		TPoly pl = pblist;
		TPoly plist = null;

		while (pl != null) {
			final TPoly p = new TPoly();
			p.setPoly(PolyBasic.pcopy(pl.getPoly()));
			p.setNext(plist);
			plist = p;

			pl = pl.getNext();
		}
		return plist;
	}

	public TPoly getPolyList() {
		return polylist;
	}

	public ArrayList<TMono> getPolyVector() {
		final ArrayList<TMono> v = new ArrayList<TMono>();
		TPoly p = polylist;
		while (p != null) {
			v.add(p.getPoly());
			p = p.next;
		}

		return v;
	}

	public TPoly getPBList() {
		return pblist;
	}

	public ArrayList<TMono> getPBMono() {
		TPoly poly1 = pblist;

		final ArrayList<TMono> v = new ArrayList<TMono>();
		while (poly1 != null) {
			final TMono m1 = poly1.getPoly();
			if (m1 != null)
				v.add(0, PolyBasic.p_copy(m1));
			poly1 = poly1.next;
		}

		return v;
	}

	// Get the non-degenerate conditions from the polynomials.
	// These are the simplest non-degenerate conditions.
	public void printNDGS() {
		//final GeoPoly basic = GeoPoly.getPoly();
		// CharSet set = CharSet.getinstance();
		PolyBasic.setRMCOEF(false);
		try {
			final ArrayList<TMono> v = getNDGS();
			final ArrayList<TMono> v1 = new ArrayList<TMono>();
			for (int i = 0; i < v.size(); i++) {
				TMono m = v.get(i);
				m = PolyBasic.simplify(m, parameter);
				if (m != null)
					v1.add(m);
			}

			final int n = v1.size();
			if (n == 0) {
				PolyBasic.setRMCOEF(true);
				return;
			}

			System.out.println("The polynomial of nondegenerate conditions:");
			for (int i = 0; i < n; i++) {
				final TMono m = v1.get(i);
				System.out.println("d" + i + " := " + PolyBasic.getExpandedPrint(m) + ";");
			}
			// System.out.println("The final condition after reduce is: ");
			System.out.print("\nND := ");
			for (int i = 0; i < n; i++) {
				if (i != 0)
					System.out.print("*");
				System.out.print("d" + i);
			}
			System.out.println(";\nND := factor(ND);\n");
		} catch (final Exception ee) {
			ee.printStackTrace();
		}
		PolyBasic.setRMCOEF(true);
	}

	public ArrayList<TMono> getNDGS() {
		TPoly poly1 = pblist;
		final ArrayList<TMono> vx = new ArrayList<TMono>();

		TMono m1, m2;

		if (poly1 == null)
			return vx;
		final int nn = poly1.getPoly().x;

		final ArrayList<TMono> v = new ArrayList<TMono>();
		while (poly1 != null) {
			m1 = poly1.getPoly();
			if (m1 != null)
				v.add(0, m1);
			poly1 = poly1.next;
		}

		for (int n = 1; n < ((nn / 2) + 1); n++) {
			m1 = m2 = null;

			for (int i = 0; i < v.size(); i++) {
				final TMono m = v.get(i);
				if ((m.x == (2 * n)) || (m.x == ((2 * n) - 1)))
					if (m1 == null)
						m1 = m;
					else
						m2 = m;
			}

			if ((m1 != null) && (m2 != null)) {
				TMono t = PolyBasic.ll_delta(2 * n, m1, m2);
				t = reduce(t);
				if ((PolyBasic.plength(t) == 1) && (t.x == 0)
						&& (t.val.intValue() != 0)) {
				} else
					PolyBasic.ppush(t, vx);
			}
			if (m1 != null)
				v.remove(m1);
			if (m2 != null)
				v.remove(m2);
		}
		return vx;
	}

	public String getPolyString(final int id) {
		int index = 0;
		String s = new String();
		TPoly pl = null;

		if (id == 1)
			pl = polylist;
		else if (id == 0)
			pl = pblist;

		while (pl != null) {
			final TMono m = pl.getPoly();
			final String s1 = PolyBasic.printSPoly(m);
			if (id == 1)
				s += "f_" + index + ": ";
			else if (id == 0)
				s += "h_" + index + ": ";

			if (s1.length() > 70)
				s += (s1.substring(0, 60) + " +  ......\n");
			else
				s += s1 + "\n";
			pl = pl.getNext();
			index++;
		}

		return s;
	}

	public boolean reCalculate() {

		if (paraCounter <= 2)
			return true;

		double x1, y1, sin, cos;
		x1 = y1 = 0;
		sin = 0;
		cos = 1.0;

		if (UtilityMiscellaneous.POINT_TRANS) {
			final int n = pointlist.size();
			if (n >= 1) {
				final GEPoint p1 = pointlist.get(0);
				x1 = p1.x1.value;
				y1 = p1.y1.value;
				for (final GEPoint p : pointlist) {
					assert(p.x1.value == p.x1.value);
					assert(p.y1.value == p.y1.value);
					p.x1.value = p.x1.value - x1;
					p.y1.value = p.y1.value - y1;
					assert(p.x1.value == p.x1.value);
					assert(p.y1.value == p.y1.value);
				}
			}
			if (n >= 2) {
				final GEPoint p2 = pointlist.get(1);
				double t1 = p2.getx();
				double t2 = p2.gety();
				final double r = Math.sqrt((t1 * t1) + (t2 * t2));
				if (r == 0.0) {
					sin = 0.0;
					cos = 1.0;
				} else {
					sin = t1 / r;
					cos = t2 / r;
					for (int i = 1; i < pointlist.size(); i++) {
						final GEPoint p = pointlist.get(i);
						t1 = p.getx();
						t2 = p.gety();
						p.setXY((t1 * cos) - (t2 * sin), (t1 * sin)
								+ (t2 * cos));
					}
				}
			}
		}
		// for (int i = 0; i <= paraCounter; i++) {
		// if (parameter[i] != null) {
		// System.out.println("x" + parameter[i].xindex + " = " +
		// parameter[i].value);
		// }
		// }
		calv_parameter();

		boolean success = true;
		for (final GEPoint p : pointlist) {
			assert(p.x1.value == p.x1.value);
			assert(p.y1.value == p.y1.value);
			if (!(success = calculate_a_point(p, true)))
				break;
			assert(p.x1.value == p.x1.value);
			assert(p.y1.value == p.y1.value);
		}

		// backup_parameter(success);
		{
			if (!success) {
				for (int i = 0; i < paraCounter; i++) {
					assert(paraBackup[i] == paraBackup[i]);
					if (parameter[i] != null)
						parameter[i].value = paraBackup[i];
				}
				assert(pptrans[0] == pptrans[0]);
				assert(pptrans[1] == pptrans[1]);
				assert(pptrans[2] == pptrans[2]);
				assert(pptrans[3] == pptrans[3]);
				x1 = pptrans[0];
				y1 = pptrans[1];
				sin = pptrans[2];
				cos = pptrans[3];
			} else {
				for (int i = 0; i < paraCounter; i++) {
					if (parameter[i] != null) {
						assert(paraBackup[i] == paraBackup[i]);
						assert(parameter[i].value == parameter[i].value);
						paraBackup[i] = parameter[i].value;
					}
				}
				assert(pptrans[0] == pptrans[0]);
				assert(pptrans[1] == pptrans[1]);
				assert(pptrans[2] == pptrans[2]);
				assert(pptrans[3] == pptrans[3]);
				pptrans[0] = x1;
				pptrans[1] = y1;
				pptrans[2] = sin;
				pptrans[3] = cos;
				assert(pptrans[0] == pptrans[0]);
				assert(pptrans[1] == pptrans[1]);
				assert(pptrans[2] == pptrans[2]);
				assert(pptrans[3] == pptrans[3]);
			}
		}

		translate_back(x1, y1, sin, cos);

		for (final DiagramUpdater d : updaterListeners)
			d.UpdateDiagram();
		calculate_trace();
		recal_allFlash();
		calculate_text();

		return success;
	}

	public void calculate_text() {
		for (final GEText t : textlist) {
			if (t.getType() == GEText.VALUE_TEXT) {
				double r = calculate(t.tvalue);
				t.tvalue.dvalue = roundn(r, t.m_dash);
				if (t.father != null) {
					final GraphicEntity c = t.father;
					if (c instanceof GEPolygon) {
						final GEPolygon p1 = (GEPolygon) c;
						r = p1.getArea();
					}
				}
				t.tvalue.dvalue = roundn(r, t.m_dash);
			}
		}
	}

	/**
	 * When <code>success</code> is true, this method makes a backup of all parameter[] values in paraBackup[],
	 * and stores the values of <code>x</code>, <code>y</code>, <code>sin</code>, and <code>cos</code> in pptrans[].
	 * 
	 * When <code>success</code> is false, this method restores the parameter[] values from paraBackup[].
	 * It also includes do-nothing code that restores the values in pptrans[] to the local variables
	 * <code>x</code>, <code>y</code>, <code>sin</code>, and <code>cos</code> which then go out of scope.
	 * Not sure what that part of the function was meant to do....
	 * 
	 * @param success
	 * @param x
	 * @param y
	 * @param sin
	 * @param cos
	 */
	public void backup_parameter(final boolean success, double x, double y, double sin, double cos) {
		if (success == false) {
			for (int i = 0; i < paraCounter; i++)
				if (parameter[i] != null)
					parameter[i].value = paraBackup[i];
			x = pptrans[0];
			y = pptrans[1];
			sin = pptrans[2];
			cos = pptrans[3];
		} else {
			for (int i = 0; i < paraCounter; i++)
				if (parameter[i] != null)
					paraBackup[i] = parameter[i].value;
			pptrans[0] = x;
			pptrans[1] = y;
			pptrans[2] = sin;
			pptrans[3] = cos;
		}
	}

	public void translate_back(final double x1, final double y1, double sin, final double cos) {
		if (UtilityMiscellaneous.POINT_TRANS) {
			double t1, t2;
			sin = -sin;
			for (final GEPoint p : pointlist) {
				t1 = p.getx();
				t2 = p.gety();
				p.setXY((t1 * cos) - (t2 * sin), (t1 * sin) + (t2 * cos));
			}

			for (final GEPoint p : pointlist) {
				p.x1.value += x1;
				p.y1.value += y1;
			}
		}

	}

	public void recal_allFlash() {
		for (final Flash f : flashlist) {
			f.recalculate();
		}
	}

	public void calculate_trace() {
		if (tracelist.size() == 0)
			return;

		CAL_MODE = 1;

		for (final GETrace t : tracelist) {
			final GEPoint p = t.getPoint();
			final GEPoint po = t.getonPoint();
			if ((p == null) || (po == null))
				continue;

			final GraphicEntity c = t.getOnObject();
			final int n = t.getPointSize();
			final double xs = po.getx();
			final double ys = po.gety();

			if (c instanceof GELine) {
				final GELine ln = (GELine) c;
				// double w = Width;
				// double h = Height;
				// double k = ln.getK();
				final GEPoint[] lpt = ln.getMaxMinPoint(false);

				final double x0 = lpt[0].getx();
				final double y0 = lpt[0].gety();
				double x, y, dx, dy;
				// double k1 = Math.abs(w / h);
				x = x0;
				y = y0;
				dx = (lpt[1].getx() - lpt[0].getx()) / n;
				dy = (lpt[1].gety() - lpt[0].gety()) / n;

				for (int j = 0; j < n; j++) {
					final double xt = x + (dx * j);
					final double yt = y + (dy * j);
					po.setXY(xt, yt);
					calculate_allpt(false);
					t.setTracePoint(j, p.getx(), p.gety());
				}
			} else if (c instanceof GECircle) {
				final GECircle cr = (GECircle) c;
				final double r = cr.getRadius();
				final double a = (Math.PI * 2) / n;
				final double ox = cr.o.getx();
				final double oy = cr.o.gety();
				for (int j = 0; j < n; j++) {
					final double sinx = Math.sin(a * j);
					final double cosx = Math.cos(a * j);
					final double xt = (r * cosx) + ox;
					final double yt = (r * sinx) + oy;
					po.setXY(xt, yt);
					calculate_allpt(false);
					t.setTracePoint(j, p.getx(), p.gety());
				}
				t.softEdge();
			}
			po.setXY(xs, ys);
		}
		calculate_allpt(true);

		CAL_MODE = 0;
	}

	public double[] getParameter() {
		final double[] r = new double[parameter.length];
		for (int i = 0; i < paraCounter; i++)
			if (parameter[i] != null)
				r[i] = parameter[i].value;
		return r;
	}

	public void setParameter(final double[] r) {
		for (int i = 0; i < paraCounter; i++)
			if (parameter[i] != null)
				parameter[i].value = r[i];
	}

	public void BackupParameter(final double[] rr, final boolean b) {
		if (b)
			for (int i = 0; i < paraCounter; i++) {
				if (parameter[i] != null)
					rr[i] = parameter[i].value;
			}
		else
			for (int i = 0; i < paraCounter; i++)
				if (parameter[i] != null)
					parameter[i].value = rr[i];

	}

	public void setParameterValue(final double[] dd) {
		for (int i = 0; i < dd.length; i++)
			if (parameter[i] != null)
				parameter[i].value = dd[i];

		if (UtilityMiscellaneous.POINT_TRANS) {
			final double x1 = pptrans[0];
			final double y1 = pptrans[1];
			final double sin = pptrans[2];
			final double cos = pptrans[3];
			translate_back(x1, y1, sin, cos);
		}
	}

	public ArrayList<double[]> calculate_allResults() { // calculate all results
		// from the polygons.
		double x1, y1, sin, cos;
		x1 = y1 = 0;
		sin = 0;
		cos = 1.0;

		if (UtilityMiscellaneous.POINT_TRANS) {
			final int n = pointlist.size();
			if (n >= 1) {
				final GEPoint p1 = pointlist.get(0);
				x1 = p1.x1.value;
				y1 = p1.y1.value;
				for (final GEPoint p : pointlist) {
					p.x1.value = p.x1.value - x1;
					p.y1.value = p.y1.value - y1;
				}
			}
			if (n >= 2) {
				final GEPoint p2 = pointlist.get(1);
				double t1 = p2.getx();
				double t2 = p2.gety();
				final double r = Math.sqrt((t1 * t1) + (t2 * t2));
				sin = t1 / r;
				cos = t2 / r;
				for (final GEPoint p : pointlist.subList(1, pointlist.size())) {
					t1 = p.getx();
					t2 = p.gety();
					p.setXY((t1 * cos) - (t2 * sin), (t1 * sin) + (t2 * cos));
				}
			}
		}

		for (int i = 0; i < paraCounter; i++)
			if (parameter[i] != null)
				paraBackup[i] = parameter[i].value;

		final ArrayList<double[]> vlist = new ArrayList<double[]>();
		final int n = paraCounter;
		final double[] rr = new double[n];
		vlist.add(rr);

		for (int t = 0; t < paraCounter; t++) {
			final param pm = parameter[t];
			if (pm != null) {
				final TMono m1 = pm.m;

				for (int k = 0; k < vlist.size(); k++) {
					final double[] rt = vlist.get(k);
					if (m1 == null) {
						rt[t] = parameter[t].value;
						continue;
					}
					for (int m = 0; m < t; m++)
						parameter[m].value = rt[m];
					final double[] result = calcu_m1(m1);
					if ((result == null) || (result.length == 0))
						rt[t] = parameter[t].value;
					else if (result.length == 1)
						rt[t] = result[0];
					else {
						rt[t] = result[0];
						for (int i = 1; i < result.length; i++) {
							final double[] r2 = new double[n];
							for (int c = 0; c < t; c++)
								r2[c] = rt[c];
							r2[t] = result[i];
							vlist.add(k, r2);
							k++;
						}
					}
				}
			}
		}

		for (int i = 0; i < vlist.size(); i++) { // remove the common point.
			final double[] kk = vlist.get(i);
			for (int j = 0; j < n; j++)
				if (parameter[j] != null)
					parameter[j].value = kk[j];

			boolean bk = false;
			for (int m = 0; m < pointlist.size(); m++) {
				final GEPoint p1 = pointlist.get(m);
				for (int n1 = m + 1; n1 < pointlist.size(); n1++) {
					final GEPoint p2 = pointlist.get(n1);
					if ((Math.abs(p1.x1.value - p2.x1.value) < UtilityMiscellaneous.ZERO)
							&& (Math.abs(p1.y1.value - p2.y1.value) < UtilityMiscellaneous.ZERO)) {
						bk = true;
						break;
					}
				}
				if (bk)
					break;
			}
			if (bk) {
				vlist.remove(i);
				i--;
			}
		}

		for (int i = 0; i < paraCounter; i++)
			if (parameter[i] != null)
				parameter[i].value = paraBackup[i];
		translate_back(x1, y1, sin, cos);
		return vlist;
	}

	public boolean calculate_allpt(final boolean d) {
		double x1, y1, sin, cos;
		x1 = y1 = 0;
		sin = 0;
		cos = 1.0;

		if (UtilityMiscellaneous.POINT_TRANS) {
			final int n = pointlist.size();
			if (n >= 1) {
				final GEPoint p1 = pointlist.get(0);
				x1 = p1.x1.value;
				y1 = p1.y1.value;
				for (final GEPoint p : pointlist) {
					p.x1.value = p.x1.value - x1;
					p.y1.value = p.y1.value - y1;
				}
			}
			if (n >= 2) {
				final GEPoint p2 = pointlist.get(1);
				double t1 = p2.getx();
				double t2 = p2.gety();
				final double r = Math.sqrt((t1 * t1) + (t2 * t2));
				sin = t1 / r;
				cos = t2 / r;
				for (final GEPoint p : pointlist) {
					t1 = p.getx();
					t2 = p.gety();
					p.setXY((t1 * cos) - (t2 * sin), (t1 * sin) + (t2 * cos));
				}
			}
		}
		boolean s = true;
		for (final GEPoint p : pointlist)
			if (!(s = calculate_a_point(p, d)))
				break;

		translate_back(x1, y1, sin, cos);
		return s;
	}

	public void popLeadingVariableDialog() {
		final DialogLeadVariable dlg = new DialogLeadVariable(gxInstance);
		dlg.loadVariable(pointlist, false);
		dlg.setVisible(true);
	}

	public void calv_parameter() {
		int n = 0;
		for (final Constraint cs : constraintlist) {
			if (cs.GetConstraintType() == Constraint.SPECIFIC_ANGLE)
				n++;
		}
		for (int i = 0; i < n; i++) {
			final TMono m = polylist.getPoly();
			final int x = PolyBasic.lv(m);
			final double[] r = PolyBasic.calculv(m, parameter);
			if (r != null)
				for (final double element : r)
					if (element > 0) {
						parameter[x - 1].value = element;
						continue;
					}
		}

	}

	public final GECircle fd_pt_on_which_circle(final GEPoint pt) {
		if (pt != null) {
			for (final GECircle circle : circlelist) {
				final GEPoint p1 = circle.getSidePoint();
				if (p1 != null) {
					final GEPoint o = circle.center();
					if (p1.x1.xindex < pt.x1.xindex	&& o.x1.xindex < pt.x1.xindex)
						return circle;
				}
			}
		}
		return null;
	}

	public GELine fd_pt_on_which_line(final GEPoint pt) {
		for (final GELine ln : linelist) {
			if (ln.isCoincidentWith(pt) && (ln.getPtsSize() >= 2)) {
				final GEPoint p1 = ln.getFirstPoint();
				final GEPoint p2 = ln.getPointOtherThan(p1);
				if ((p1.x1.xindex < pt.x1.xindex) && (p2.x1.xindex < pt.x1.xindex))
					return ln;
			}
		}
		return null;
	}

	public double[] calculate_ocir(final GEPoint pt) {
		if ((CurrentAction == MOVE && SelectList.contains(pt)) || CAL_MODE == 1)
			return null;

		final GECircle cr = fd_pt_on_which_circle(pt);
		if (cr != null) {
			final GEPoint p1 = cr.center();
			final GEPoint p2 = cr.getSidePoint();
			final double xt = paraBackup[pt.x1.xindex - 1];
			final double yt = paraBackup[pt.y1.xindex - 1];
			final double x1 = paraBackup[p1.x1.xindex - 1];
			final double y1 = paraBackup[p1.y1.xindex - 1];
			final double x2 = paraBackup[p2.x1.xindex - 1];
			final double y2 = paraBackup[p2.y1.xindex - 1];

			if (check_eqdistance(x1, y1, xt, yt, x1, y1, x2, y2)) {
				double rr = GEAngle.get3pAngle(x2, y2, x1, y1, xt, yt);
				rr -= Math.PI;

				final double cos = Math.cos(rr);
				final double sin = Math.sin(rr);
				final double dx = p2.getx() - p1.getx();
				final double dy = p2.gety() - p1.gety();

				final double[] r = new double[2];
				r[0] = p1.getx() + (dx * cos) - (dy * sin);
				r[1] = p1.gety() + (dx * sin) + (dy * cos);
				return r;
			}
		}
		return null;
	}

	public double[] calculate_oline(final GEPoint pt) {
		if ((CurrentAction == MOVE && SelectList.contains(pt)) || CAL_MODE == 1)
			return null;

		final GELine ln = fd_pt_on_which_line(pt);
		if (ln != null) {
			final GEPoint p1 = ln.getFirstPoint();
			final GEPoint p2 = ln.getPointOtherThan(p1);
			final double xt = paraBackup[pt.x1.xindex - 1];
			final double yt = paraBackup[pt.y1.xindex - 1];
			final double x1 = paraBackup[p1.x1.xindex - 1];
			final double y1 = paraBackup[p1.y1.xindex - 1];
			final double x2 = paraBackup[p2.x1.xindex - 1];
			final double y2 = paraBackup[p2.y1.xindex - 1];

			if (check_Collinear(xt, yt, x1, y1, x2, y2)) {
				double d1 = xt - x1;
				double d2 = x2 - xt;
				if (isZero(d1) || isZero(d2) || isZero(d1 + d2)) {
					d1 = yt - y1;
					d2 = y2 - yt;
				}

				final double d = d1 + d2;
				final double x = ((p1.getx() * d2) + (p2.getx() * d1)) / d;
				final double y = ((p1.gety() * d2) + (p2.gety() * d1)) / d;
				final double[] r = new double[2];
				r[0] = x;
				r[1] = y;
				return r;
			}
		}
		return null;
	}

	public boolean calculate_lccc(final GEPoint cp, final double[] r) {
		final param pm1 = cp.x1;
		final param pm2 = cp.y1;

		final TMono m1 = pm1.m;
		final TMono m2 = pm2.m;
		if (!((m1 != null) && (m2 != null) && (m1.deg == 2) && (m2.deg == 1)))
			return false;

		int type = 0;
		Constraint cs = null;

		for (final Constraint c : constraintlist) {
			type = c.GetConstraintType();
			if (c.getelement(0) == cp && (type == Constraint.INTER_LC || type == Constraint.INTER_CC)) {
				cs = c;
				break;
			}
		}
		if (cs == null)
			return false;
		if ((CurrentAction == MOVE) && SelectList.contains(cp)) {
			cs.proportion = 1;
			return false;
		}

		if (type == Constraint.INTER_LC) {
			final GELine ln = (GELine) cs.getelement(1);
			final GECircle cr = (GECircle) cs.getelement(2);
			final GEPoint p1 = ln.getFirstPoint();
			final GEPoint p2 = ln.getPointOtherThan(p1);
			if ((p1 == null) || (p2 == null) || (p2 == cp))
				return false;
			final GEPoint o = cr.o;
			final double xt = paraBackup[cp.x1.xindex - 1];
			final double yt = paraBackup[cp.y1.xindex - 1];
			final double x1 = paraBackup[p1.x1.xindex - 1];
			final double y1 = paraBackup[p1.y1.xindex - 1];
			final double x2 = paraBackup[p2.x1.xindex - 1];
			final double y2 = paraBackup[p2.y1.xindex - 1];
			final double xo = paraBackup[o.x1.xindex - 1];
			final double yo = paraBackup[o.y1.xindex - 1];

			final double k = (y2 - y1) / (x2 - x1);
			final double k1 = -(x2 - x1) / (y2 - y1);

			final double mx = (((yo - y1) + (k * x1)) - (k1 * xo)) / (k - k1);
			final double my = y1 + (k * (mx - x1));

			final double area = signArea(xt, yt, mx, my, xo, yo);
			final double area1 = signArea(r[0], r[1], mx, my, o.getx(),
					o.gety());
			final double area2 = signArea(r[2], r[3], mx, my, o.getx(),
					o.gety());

			final int n = cs.proportion;
			if (n == 1) {
				if (isZero(area))
					return false;
				if (area > 0)
					cs.proportion = 2;
				else if (area < 0)
					cs.proportion = 3;

			}
			if (cs.proportion == 2) {
				if (area1 > 0) {
					cp.setXY(r[0], r[1]);
					return true;
				} else if (area2 > 0) {
					cp.setXY(r[2], r[3]);
					return true;
				}
			} else if (cs.proportion == 3)
				if (area1 < 0) {
					cp.setXY(r[0], r[1]);
					return true;
				} else if (area2 < 0) {
					cp.setXY(r[2], r[3]);
					return true;
				}

		} else if (type == Constraint.INTER_CC) {
			final GECircle cr1 = (GECircle) cs.getelement(1);
			final GECircle cr2 = (GECircle) cs.getelement(2);
			final GEPoint o1 = cr1.o;
			final GEPoint o2 = cr2.o;

			final double xt = paraBackup[cp.x1.xindex - 1];
			final double yt = paraBackup[cp.y1.xindex - 1];
			final double x1 = paraBackup[o1.x1.xindex - 1];
			final double y1 = paraBackup[o1.y1.xindex - 1];
			final double x2 = paraBackup[o2.x1.xindex - 1];
			final double y2 = paraBackup[o2.y1.xindex - 1];

			final int n = cs.proportion;
			final double area = signArea(xt, yt, x1, y1, x2, y2);
			final double area1 = signArea(r[0], r[1], o1.getx(), o1.gety(),
					o2.getx(), o2.gety());
			final double area2 = signArea(r[2], r[3], o1.getx(), o1.gety(),
					o2.getx(), o2.gety());

			if (n == 1)
				if (area > 0)
					cs.proportion = 2;
				else if (area < 0)
					cs.proportion = 3;
				else
					return false;

			if (cs.proportion == 2) {
				if (area1 > 0) {
					cp.setXY(r[0], r[1]);
					return true;
				} else if (area2 > 0) {
					cp.setXY(r[2], r[3]);
					return true;
				}
			} else if (cs.proportion == 3) {
				if (area1 < 0) {
					cp.setXY(r[0], r[1]);
					return true;
				} else if (area2 < 0) {
					cp.setXY(r[2], r[3]);
					return true;
				} 
			}
		}
		return false;
	}

	public boolean calculate_a_point(final GEPoint p, final boolean d) {
		if ((p == null) || p.isAFreePoint())
			return true;

		assert(p.x1.value == p.x1.value);
		assert(p.y1.value == p.y1.value);

		final GEPoint cp = p;
		final param pm1 = cp.x1;
		final param pm2 = cp.y1;

		TMono m1 = pm1.m;
		final TMono m2 = pm2.m;

		if (((m1 != null) && (m2 == null) && (PolyBasic.deg(m1) == 1)) || ((m1 == null) && (m2 != null) && (PolyBasic.deg(m2) == 1))) {
			final double[] r = calculate_oline(cp);
			if (r != null) {
				assert(cp.x1.value == cp.x1.value);
				assert(cp.y1.value == cp.y1.value);
				cp.x1.value = r[0];
				cp.y1.value = r[1];
				assert(cp.x1.value == cp.x1.value);
				assert(cp.y1.value == cp.y1.value);
				return true;
			}
		}
		if ((m1 == null) && (m2 != null) && (PolyBasic.deg(m2) == 2)) {
			final double[] r = calculate_ocir(cp);
			if (r != null) {
				assert(cp.x1.value == cp.x1.value);
				assert(cp.y1.value == cp.y1.value);
				cp.x1.value = r[0];
				cp.y1.value = r[1];
				assert(cp.x1.value == cp.x1.value);
				assert(cp.y1.value == cp.y1.value);
				return true;
			}
		}

		if ((m1 == null) && (m2 != null) && d) {
			double[] r = null;
			final int v = PolyBasic.deg(m2);
			assert(cp.x1.value == cp.x1.value);
			assert(cp.y1.value == cp.y1.value);
			if (v == 1)
				r = PolyBasic.calculate_online(m2, parameter, cp.x1.xindex, cp.y1.xindex);
			else if (v == 2)
				r = PolyBasic.calculate_oncr(m2, parameter, cp.x1.xindex, cp.y1.xindex);
			if (r != null) {
				cp.x1.value = r[0];
				cp.y1.value = r[1];
				assert(cp.x1.value == cp.x1.value);
				assert(cp.y1.value == cp.y1.value);
				return true;
			}
		}

		int va;
		int vb;
		double[] result = null;
		boolean success = true;
		if (m1 != null)
			while (true) {
				result = calcu_m1(m1);
				va = PolyBasic.deg(m1);
				if ((result == null) || (result.length != 0))
					break;
				if (m1.next == null)
					break;
				m1 = m1.next;
			}
		else {
			va = 1;
			result = new double[1];
			result[0] = cp.x1.value;
			assert(cp.x1.value == cp.x1.value);
		}

		if (m2 == null)
			vb = 1;
		else
			vb = PolyBasic.deg(m2);

		if (result == null) {
			success = false;
			return success;
		} else if ((result.length == 1) && (vb == 1)) {
			// double oldx = cp.x1.value;
			final double oldy = cp.y1.value;

			cp.x1.value = result[0];

			if (m2 != null) {
				double[] result2 = calcu_m1(m2);
				if (result2.length == 0)
					result2 = calform(PolyBasic.lv(m2), parameter);
				if (result2 == null) {
					success = false;
					return success;
				} else if (result2.length == 1)
					cp.y1.value = result2[0];
				else {
					double nx = oldy;
					double ds = Double.MAX_VALUE;
					for (final double element : result2)
						if (p.doConstraintsAllowCoordinates(result[0], element)) {
							final double dlen = Math.pow(oldy - element, 2);
							if (dlen < ds) {
								ds = dlen;
								nx = element;
							}
						}
					cp.y1.value = nx;
					assert(cp.y1.value == cp.y1.value);
				}

			}
		} else { // if (result.length > 1)
			int index = 0;
			final double oldx = cp.x1.value;
			final double oldy = cp.y1.value;

			final double[] r = new double[va * vb * 2];
			double[] result2 = null;
			double ox, oy;
			boolean boy = false;
			ox = oy = 0.0;

			for (final double element : result)
				if (m2 != null) {
					cp.x1.value = element;
					ox = element;
					result2 = calcu_m1(m2);
					if ((result2 == null) || (result2.length == 0))
						result2 = calform(p.y1.xindex, parameter);
					if ((result2 != null) && (result2.length >= 1))
						for (final double element2 : result2) {
							cp.y1.value = element2;

							if (!boy) {
								oy = element2;
								boy = true;
							}
							if (isColocatedWithAnotherExistingPoint(cp) == null)
								if ((2 * index) < r.length) {
									r[2 * index] = element;
									r[(2 * index) + 1] = element2;
									index++;
								}
						}

				} else {
					r[2 * index] = element;
					r[(2 * index) + 1] = cp.y1.value;
					index++;
				}

			if (index == 0)
				if (boy) {
					r[0] = ox;
					r[1] = oy;
				} else
					return false;
			if (index == 1) {
				cp.x1.value = r[0];
				cp.y1.value = r[1];
			} else if ((index == 2) && calculate_lccc(cp, r)) {
			} else {
				int t = -1;
				double dis = Double.POSITIVE_INFINITY;

				for (int i = 0; i < index; i++)
					if (p.doConstraintsAllowCoordinates(r[2 * i], r[(2 * i) + 1])) {
						final double ts = Math.pow(oldx - r[2 * i], 2)
								+ Math.pow(oldy - r[(2 * i) + 1], 2);
						if (ts < dis) {
							dis = ts;
							t = i;
						}
					}
				if (t >= 0) {
					cp.x1.value = r[2 * t];
					cp.y1.value = r[(2 * t) + 1];
				}

			}
		}
		return success;
	}

	public double[] calcu_m1(TMono m) {
		double[] result = PolyBasic.calculv(m, parameter);

		if ((result != null) && (result.length == 0)) {
			final TMono mx = m.next;
			if (mx != null)
				if (PolyBasic.deg(mx) != 0)
					result = PolyBasic.calculv(mx, parameter);
		}

		final int lva = PolyBasic.lv(m);
		if (result == null) {
			if (lva < 1)
				return null;

			TPoly plist = pblist;
			TMono m1 = null;
			TMono m2 = null;
			final int d = PolyBasic.deg(m, lva);

			while (plist != null) {
				if (PolyBasic.lv(plist.getPoly()) == lva)
					if (m1 == null)
						m1 = plist.getPoly();
					else
						m2 = plist.getPoly();
				plist = plist.getNext();
			}

			if ((m1 == null) && (m2 == null)) {

			}
			if ((m1 != null) && (m2 != null))
				result = PolyBasic.calculv2poly(m1, m2, parameter);
			else if (d == 1) {
				m = m1;
				if (m1 == null)
					m = m2;

				final double[] r = PolyBasic.calculv_2v(m, parameter);
				if ((r != null) && (r.length != 0))
					parameter[lva - 2].value = r[0];
				return null;
			}
		}
		return result;
	}

	public void pushbackup() {
		for (int i = 0; i < paraCounter; i++)
			if (parameter[i] != null)
				paraBackup[i] = parameter[i].value;
	}

	public double[] calform(final int lv, final param p[]) {
		TPoly plist = pblist;
		TMono m1, m2;
		m1 = m2 = null;

		while (plist != null) {
			if (PolyBasic.lv(plist.getPoly()) == lv)
				if (m1 == null)
					m1 = plist.getPoly();
				else
					m2 = plist.getPoly();
			plist = plist.getNext();

		}
		if ((m1 == null) || (m2 == null))
			return null;

		double[] result; // = new double[1];
		result = PolyBasic.calculv2poly(m1, m2, p);
		return result;
	}

	public void characteristicSetMethodAndAddPoly(final boolean bRecalculate) {
		final TPoly plist = Constraint.getPolyListAndSetNull();
		if (plist != null) {
			TPoly plist2 = plist;
			while (plist2 != null) {
				pblist = PolyBasic.ppush(PolyBasic.pcopy(plist2.getPoly()), pblist);
				plist2 = plist2.getNext();
			}
			plist2 = plist;

			if (polylist != null) {
				TPoly tp = plist2;
				while (tp != null) {
					final TPoly t = tp;
					tp = tp.getNext();
					t.setNext(null);
					final int lva = PolyBasic.lv(t.getPoly());
					TPoly pl = polylist;

					if (PolyBasic.lv(pl.getPoly()) > lva) {
						t.setNext(polylist);
						polylist = t;
					} else {
						while (pl.getNext() != null) {
							if (PolyBasic.lv(pl.getNext().getPoly()) > lva) {
								t.setNext(pl.getNext());
								pl.setNext(t);
								break;
							}
							pl = pl.getNext();
						}
						if (pl.getNext() == null)
							pl.setNext(t);
					}
				}
			} else
				polylist = plist2;

			try {
				// CMisc.print("----------------------");
				// printPoly(polylist);
				// polylist = optmizePolygonOnLine(polylist);
				polylist = CharacteristicSetMethod.charset(polylist);
				// CMisc.print("======================");
				// printPoly(polylist);
			} catch (final OutOfMemoryError ee) {
				JOptionPane.showMessageDialog(gxInstance, ee.getMessage(),
						ee.toString(), JOptionPane.ERROR_MESSAGE);
			}

			optimizePolynomial();
			setVariable();

			if (!bRecalculate) {
				pushbackup();
				reCalculate();
			}
		}
	}

	public void optimizePolynomial() {
		if (!UtilityMiscellaneous.POINT_TRANS)
			return;
		if (pointlist.size() < 2)
			return;
		final GEPoint p1 = pointlist.get(0);
		final GEPoint p2 = pointlist.get(1);
		GeoPoly.addZ(p1.x1.xindex);
		GeoPoly.addZ(p1.y1.xindex);
		GeoPoly.addZ(p2.x1.xindex); // Kutach: Why is p2.y1.xindex not added? Is that because it represents a variable that can be changed during optimization?

		for (final Constraint cs : constraintlist) {
			final int t = cs.GetConstraintType();
			if ((t == Constraint.PONLINE) || (t == Constraint.INTER_LC)) {
				final GEPoint t1 = (GEPoint) cs.getelement(0);
				final GELine l1 = (GELine) cs.getelement(1); // XXX This threw an exception one time (when loading a faulty gex file) CastException [Was GEPoint]
				if (l1.containsPoints(p1, p2))
					GeoPoly.addZ(t1.x1.xindex);
			} else if (t == Constraint.INTER_LL) {
				final GEPoint t1 = (GEPoint) cs.getelement(0);
				final GELine l1 = (GELine) cs.getelement(1);
				final GELine l2 = (GELine) cs.getelement(2);
				if (l1.containsPoints(p1, p2) || l2.containsPoints(p1, p2))
					GeoPoly.addZ(t1.x1.xindex);
			}
		}
		TPoly tp = polylist;
		while (tp != null) {
			final TMono m = tp.getPoly();
			if ((m != null) && (PolyBasic.plength(m) == 1))
				GeoPoly.addZ(m.x);
			tp = tp.getNext();
		}
	}

	public boolean mulSolutionSelect(final GEPoint p) {
		pSolution = p;
		final TMono m1 = p.x1.m;
		final TMono m2 = p.y1.m;

		if ((m1 == null) || (m2 == null))
			return true;
		if ((m1.deg == 1) && (m2.deg == 1))
			return true;

		double x1, y1, sin, cos;
		x1 = y1 = 0;
		sin = 0;
		cos = 1.0;

		if (UtilityMiscellaneous.POINT_TRANS) {
			final int n = pointlist.size();
			if (n >= 1) {
				final GEPoint p1 = pointlist.get(0);
				x1 = p1.x1.value;
				y1 = p1.y1.value;
				for (final GEPoint px : pointlist) {
					px.x1.value = px.x1.value - x1;
					px.y1.value = px.y1.value - y1;
				}
			}
			if (n >= 2) {
				final GEPoint p2 = pointlist.get(1);
				double t1 = p2.getx();
				double t2 = p2.gety();
				final double r = Math.sqrt((t1 * t1) + (t2 * t2));
				sin = t1 / r;
				cos = t2 / r;
				for (final GEPoint px : pointlist) {
					t1 = px.getx();
					t2 = px.gety();
					px.setXY((t1 * cos) - (t2 * sin), (t1 * sin) + (t2 * cos));
				}
			}
		}

		int lva = PolyBasic.lv(m1);
		double[] result = PolyBasic.calculv(m1, parameter);

		if (result == null)
			result = calform(lva, parameter);
		if (result == null)
			return false;

		lva = PolyBasic.lv(m2);
		for (final double element : result) {
			parameter[p.x1.xindex - 1].value = element;
			double[] r = PolyBasic.calculv(m2, parameter);
			if (r == null)
				r = calform(lva, parameter);

			for (final double element2 : r) {
				final GEPoint pt = createTempPoint(element, element2);
				solutionlist.add(pt);
			}
		}

		if (UtilityMiscellaneous.POINT_TRANS) {
			double t1, t2;
			sin = -sin;
			for (final GEPoint px : pointlist) {
				t1 = px.getx();
				t2 = px.gety();
				px.setXY((t1 * cos) - (t2 * sin), (t1 * sin) + (t2 * cos));
			}

			for (final GEPoint px : pointlist) {
				px.x1.value += x1;
				px.y1.value += y1;
			}
			for (final GEPoint px : solutionlist) {
				t1 = px.getx();
				t2 = px.gety();
				px.setXY((t1 * cos) - (t2 * sin), (t1 * sin) + (t2 * cos));
			}

			for (final GEPoint px : solutionlist) {
				px.x1.value += x1;
				px.y1.value += y1;
			}
		}

		if (solutionlist.size() == 1) {
			solutionlist.clear();
			return true;
		}
		PreviousAction = CurrentAction;
		SetCurrentAction(MULSELECTSOLUTION);
		return true;
	}

	public void ErasedADecidedPoint(final GEPoint p) { // there are some problems
		// in this function.
		final int x1 = p.x1.xindex;
		// int y1 = p.y1.xindex;

		if (!p.x1.Solved || !p.y1.Solved)
			return;
		TPoly plist = polylist;
		TPoly pleft = null;

		TMono m1, m2;
		if (PolyBasic.lv(plist.getPoly()) < x1) {
			while (plist.getNext() != null) {
				if (PolyBasic.lv(plist.getNext().getPoly()) == x1)
					break;
				plist = plist.getNext();
			}
			pleft = plist.getNext();
			m1 = pleft.getPoly();
			pleft = pleft.getNext();
			m2 = pleft.getPoly();

			pleft = pleft.getNext();
			plist.setNext(pleft);

		} else {
			m1 = plist.getPoly();
			plist = plist.getNext();
			m2 = plist.getPoly();
			polylist = plist.getNext();
			pleft = polylist;
		}

		plist = pleft;
		while (plist != null) {
			TMono m = PolyBasic.prem(plist.getPoly(), PolyBasic.pcopy(m2));
			m = PolyBasic.prem(m, PolyBasic.pcopy(m1));
			PolyBasic.printpoly(m);
			plist.setPoly(m);
			plist = plist.getNext();
		}
		paraCounter -= 2;
		return;
	}

	public void SetDimension(final Dimension dim) {
		Width = dim.getWidth();
		Height = dim.getHeight();
	}

	public void SetDimension(final double x, final double y) {
		Width = x;
		Height = y;
	}

	public int GetCurrentAction() {
		return CurrentAction;
	}

	public void setParameter(final int vv1, final int vv2) {
		v1 = vv1;
		v2 = vv2;
	}

	public void setCurrentDrawStartOver() {
		SetCurrentAction(CurrentAction);
	}

	public void SetCurrentAction(final int type) {
		if ((type != MOVE) && (CurrentAction == CONSTRUCT_FROM_TEXT))
			clearFlash();

		if (gxInstance != null)
			gxInstance.setActionPool(type);

		CurrentAction = type;
		SelectList.clear();

		if (type == SETTRACK)
			CTrackPt = null;
		FirstPnt = SecondPnt = null;
		STATUS = 0;
		CatchList.clear();
		vx1 = vy1 = vangle = 0;
		vtrx = vtry = 0;

		if (panel != null)
			panel.repaint();
		else if (gxInstance != null)
			gxInstance.repaint();

		if (gxInstance != null) {
			final ToolBarStyle dlg = gxInstance.getStyleDialog();
			if ((dlg != null) && dlg.isVisible())
				dlg.setAction(getActionType(type));
		}

	}

	public GEEqualDistanceMark fd_edmark(final GEPoint p1, final GEPoint p2) {
		for (final Object obj : otherlist) {
			if (obj instanceof GEEqualDistanceMark) {
				final GEEqualDistanceMark ln = (GEEqualDistanceMark) obj;
				if (((ln.p1 == p1) && (ln.p2 == p2)) || ((ln.p2 == p1) && (ln.p1 == p2)))
					return ln;
			}
		}
		return null;
	}

	public void setcurrentStatus(final int status) {
		STATUS = status;
	}

	public void saveProveText(final String path) throws IOException {
		if (cpfield != null) {
			final File f = new File(path);
			final boolean bExists = f.exists();
			if (bExists)
				f.delete();
			else
				f.createNewFile();
			final FileOutputStream fp = new FileOutputStream(f, bExists);
			@SuppressWarnings("resource")
			final DataOutputStream out = new DataOutputStream(fp);
			cpfield.saveText(out, 0);
		}
	}

	//	public void createProveHead() {
	//	}

	//	public boolean undoProveToHead() {
	//		if (cpfield == null)
	//			return false;
	//		return cpfield.undo_to_head(this);
	//	}

	//	public boolean prove_run_to_prove() {
	//		if (cpfield != null) {
	//			cpfield.run_to_end(this);
	//			return cpfield.undo_default(this);
	//		} else
	//			gxInstance.getpprove().m_runtobegin();
	//		return true;
	//	}

	public boolean nextProveStep() {
		if (cpfield != null) {
			clearSelection();
			final UndoStruct u = redo_step();

			if (u == null)
				Undo();
			else
				cpfield.setSelectedUndo(u, this);
			return true;

			// boolean r = cpfield.next_prove_step(this);

			// if (!) {
			// cpfield.undo_to_head(this); //.undo_default(this);
			// }
		} else {
			// if (gxInstance != null && gxInstance.getpprove() != null)
			// gxInstance.getpprove().mstep();
		}
		return false;
	}

	public GELine findLineWithAllGivenPoints(final Collection<GEPoint> v) {
		for (final GELine ln : linelist)
			if (ln.points.containsAll(v))
				return ln;
		return null;
	}

	public void provePlay(final int num) {
		if (timer_type == 0) {
			timer = new Timer(num, this);
			timer.start();
			timer_type = 2;
		} else if (timer_type == 2) {
			timer.stop();
			timer_type = 0;
			redo();
		}
	}

	public void proveStop() {
		if (timer_type == 2) {
			timer.stop();
			timer_type = 0;
			cpfield.run_to_end(this);
		}
	}

	public boolean run_to_prove(final UndoStruct u, final UndoStruct u1) {
		doFlash();

		if ((u1 == null) && (U_Obj == null))
			return false;

		if (u1 != null)
			runto();
		else {
			runto1(U_Obj);
			repaint();
			return true;
		}
		runto1(u1);
		repaint();
		return true;
	}

	public void runto() {
		final UndoStruct u = U_Obj;
		if (u == null)
			return;

		UndoStruct ux;
		if (already_redo(u))
			return;

		while (true) {
			ux = redo_step(false);
			doFlash();
			if ((ux == null) || (ux == u))
				break;
		}
		U_Obj = null;
	}

	public void runto1(final UndoStruct u) {
		if (u != null && !already_redo(u)) {
			UndoStruct ux;
			while (true) {
				ux = redo_step(false);
				if (ux == null || ux == u) {
					U_Obj = null;
					return;
				}
				if (!all_flash_finished()) {
					U_Obj = u;
					return;
				}
			}
		}
	}

	public boolean all_flash_finished() {
		for (final Flash f : flashlist)
			if (!f.isfinished())
				return false;
		return true;
	}

	public boolean checkCPfieldExists() {
		return cpfield != null;
	}

	public void prove_run_to_end() {
		if (cpfield != null)
			cpfield.run_to_end(this);
		else if ((gxInstance != null) && (gxInstance.getProofPanel() != null))
			gxInstance.getProofPanel().m_runtoend();

	}

	public void prove_run_to_begin() {
		if (cpfield != null) {
			cpfield.run_to_end(this);
			cpfield.run_to_begin(this);
		} else
			gxInstance.getProofPanel().m_runtobegin();
	}

	public void Regenerate_Prove_Text() {
		if (cpfield != null)
			cpfield.reGenerateAll();
	}

	/*
	 * public boolean removeLastProveNode() { if (cpfield != null) { boolean r =
	 * cpfield.removeLast(); if (r == false) { cpfield = null; } return r;
	 * 
	 * } else { return false; } }
	 */

	public double[] getSnap(final double x, final double y) {
		final double[] r = new double[2];
		if (!SNAP) {
			r[0] = x;
			r[1] = y;
			return r;
		}
		final int nx = (int) (0.5 + (x / GridX));
		final int ny = (int) (0.5 + (y / GridY));
		r[0] = nx * GridX;
		r[1] = ny * GridY;
		return r;
	}

	public void DWMouseWheel(final double x, final double y, final int n, final int rt) { // TODO: Make the mouse wheel move the viewable area around.
		//		switch (CurrentAction) {
		//		case MOVE:
		//		case ZOOM_IN:
		//		case ZOOM_OUT:
		final int k = Math.abs(n);
		for (int i = 0; i < k; i++) {
			if (rt > 0)
				zoom_in(x, y, 3);
			else
				zoom_out(x, y, 3);
		}
		if (k > 0)
			reCalculate();
		//		}
	}

	//	public void DWMouseDbClick(final double x, final double y) {
	//		CatchPoint.setXY(x, y);
	//	}

	public void defineSpecificAngle() {
		if (paraCounter != 1) {
			final ArrayList<Integer> v = getSpecifiedAngleList();
			if (v.size() == 0) {
				JOptionPane
				.showMessageDialog(
						gxInstance,
						DrawPanelFrame.getLanguage(1027,
								"Angle specification must be done before drawing anything"),
								DrawPanelFrame.getLanguage(302, "Warning"),
								JOptionPane.WARNING_MESSAGE);
				return;
			}
			final DialogSpecificAngle dlg = new DialogSpecificAngle(null, 1, v);
			dlg.setVisible(true);
			return;
		}
		final DialogSpecificAngle dlg = new DialogSpecificAngle(gxInstance, 0, null);
		dlg.setVisible(true);
		if (!dlg.isOkPressed())
			return;
		final ArrayList<Integer> v = dlg.getSpecifcAngle();

		for (final Integer in : v) {
			final param p1 = parameter[paraCounter - 1] = new param(paraCounter, 0);
			p1.value = in;
			paraCounter++;
			final Constraint cs = new Constraint(Constraint.SPECIFIC_ANGLE, p1, in);
			addConstraintToList(cs);
			characteristicSetMethodAndAddPoly(false);
		}
		if ((paraCounter % 2) != 0) {
			// paraCounter += 2;
			// parameter[paraCounter-1] = new param(0,0);
			// parameter[paraCounter-2] = new param(0,0);
			parameter[paraCounter - 1] = new param(0, 0);
			paraCounter += 1;
			parameter[paraCounter - 1] = new param(0, 0);
			paraCounter += 1;
		} else {
			parameter[paraCounter - 1] = new param(0, 0);
			paraCounter += 1;
		}

	}

	public param getParaForSpecificAngle(final int ang) {
		for (final Constraint cs : constraintlist) {
			if (cs.GetConstraintType() == Constraint.SPECIFIC_ANGLE && cs.proportion == ang) {
				final param pm = (param) cs.getelement(0);
				return pm;
			}
		}
		return null;
	}

	public ArrayList<Integer> getSpecifiedAngleList() {
		final ArrayList<Integer> v = new ArrayList<Integer>();

		for (final Constraint cs : constraintlist) {
			if (cs.GetConstraintType() == Constraint.SPECIFIC_ANGLE)
				v.add(new Integer(cs.proportion));
		}
		return v;
	}

	public boolean viewElementFromXY(final double x, final double y) {
		final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
		SelectAllFromXY(v, x, y, 0);

		GraphicEntity c = null;
		if (v.size() == 0)
			return false;

		if (v.size() > 1)
			c = (GraphicEntity) popSelect(v, (int) x, (int) y);
		else
			c = v.get(0);
		if (c == null)
			return false;
		v.clear();
		v.add(c);
		setObjectListForFlash(v);
		onDBClick(c);
		// viewElement(c);
		return true;
	}

	public Object popSelect(final ArrayList<GraphicEntity> v, final int x, final int y) {
		if (v.size() == 1)
			viewElement(v.get(0));
		if (v.size() > 1) {
			final DialogSelectGraphicEntity sd = gxInstance.getSelectDialog();

			final JPanel d = panel;
			final Point p = d.getLocationOnScreen();
			sd.addItem(v);
			sd.setLocation((int) (p.getX() + x), (int) (p.getY() + y));
			sd.setVisible(true);
			final Object obj = sd.getSelected();
			gxInstance.setFocusable(true);
			return obj;
		}
		return null;
	}

	public void DWMouseRightDown(final double x, final double y) {
		if ((CurrentAction != DEFINEPOLY) && (CurrentAction != TRANSFORM)
				&& (CurrentAction != FREE_TRANSFORM)) {
			CatchPoint.setXY(x, y);
			clearSelection();
			STATUS = 0;
			RightMenuPopup(x, y);
		}
	}

	public void DWMouseRightClick(final double x, final double y) {
		CatchPoint.setXY(x, y);
		switch (CurrentAction) {
		case DEFINEPOLY: {
			if (SelectList.size() == 1 && STATUS != 0) {
				final GEPolygon cp = (GEPolygon) SelectList.get(0);
				if (cp.points.size() >= 3) {
					cp.addAPoint(cp.points.get(0));
					STATUS = 0;
					addPolygonToList(cp);
					UndoAdded(cp.getDescription());
					clearSelection();
				}
				panel.repaint();
			} else {
				CatchPoint.setXY(x, y);
				clearSelection();
				STATUS = 0;
				RightMenuPopup(x, y);
			}
		}
		break;
		case TRANSFORM: {
			if (STATUS != 0)
				new PopupMenuRightTransform(this).show(panel, (int) x, (int) y);
			else
				RightMenuPopup(x, y);
		}
		break;
		case FREE_TRANSFORM: {
			if (SelectList.size() == 1) {
				final JPopupMenu m = new JPopupMenu();
				final ActionListener ls = new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						if (e.getActionCommand().equals("OK"))
							add_free_transform();
						else {
							final GEPolygon poly = (GEPolygon) SelectList.get(0);
							STATUS = 0;
							clearSelection();
							poly.setDraggedPointsNull();
						}
					}
				};
				final JMenuItem item1 = new JMenuItem("OK");
				item1.addActionListener(ls);
				final JMenuItem item2 = new JMenuItem("Cancel");
				item2.addActionListener(ls);
				m.add(item1);
				m.add(item2);
				m.show(panel, (int) x, (int) y);
			}
		}
		break;
		}
	}

	public void RightMenuPopup(final double x, final double y) {
		if (gxInstance != null) {
			final ArrayList<GraphicEntity> selected = new ArrayList<GraphicEntity>();
			SelectAllFromXY(selected, x, y, 0);

			if (!selected.isEmpty()) {
				GraphicEntity ge = null;
				final JPanel d = panel;
				if (selected.size() > 1) {
					final DialogSelectGraphicEntity dlg = new DialogSelectGraphicEntity(gxInstance, selected);
					dlg.addItem(selected);
					final Point p = d.getLocationOnScreen();
					dlg.setLocation((int) (p.getX() + x), (int) (p.getY() + y));
					dlg.setVisible(true);
					ge = (GraphicEntity) dlg.getSelected();
				} else
					ge = selected.get(0);

				setObjectListForFlash(ge);
				new PopupMenuRightClick(ge, gxInstance).show(panel, (int) x, (int) y);
			}
		}
	}

	public static <T extends GraphicEntity> void SelectFromAList(final ArrayList<GraphicEntity> listTo, final ArrayList<T> listFrom, final double x, final double y) { 
		for (final T ge : listFrom)
			if (ge.isLocatedNear(x, y))
				listTo.add(ge);
	}

	public void SelectAllFromXY(final ArrayList<GraphicEntity> v, final double x,
			final double y, final int type) {
		// 2: all; 1: geometry object only 0: point preferential
		// 3: only point, 4:only line, 5: only circle
		// 6: only angle 7: only distance 8:only polygon, 9, only text,10 only
		// trace.

		if (type == 0) {
			SelectFromAList(v, pointlist, x, y);
			if (!v.isEmpty())
				return;
			SelectNameText(v, x, y);
			if (!v.isEmpty())
				return;
			SelectFromAList(v, linelist, x, y);
			SelectFromAList(v, circlelist, x, y);
			SelectFromAList(v, anglelist, x, y);
			SelectFromAList(v, distancelist, x, y);
			SelectFromAList(v, textlist, x, y);
			SelectFromAList(v, tracelist, x, y);
			SelectFromAList(v, otherlist, x, y);
			if (v.isEmpty())
				SelectFromAList(v, polygonlist, x, y);
		} else if (type == 1) {
			SelectFromAList(v, pointlist, x, y);
			if (!v.isEmpty())
				return;
			SelectFromAList(v, linelist, x, y);
			SelectFromAList(v, circlelist, x, y);
		} /* else if (type == 2) {
			SelectFromAList(v, pointlist, x, y);
			SelectFromAList(v, linelist, x, y);
			SelectFromAList(v, circlelist, x, y);
			SelectFromAList(v, anglelist, x, y);
			SelectFromAList(v, distancelist, x, y);
			SelectFromAList(v, textlist, x, y);
			SelectFromAList(v, tracelist, x, y);
			SelectFromAList(v, otherlist, x, y);
			if (v.size() == 0)
				SelectFromAList(v, polygonlist, x, y);
		} else if (type == 3)
			SelectFromAList(v, pointlist, x, y);
		else if (type == 4)
			SelectFromAList(v, linelist, x, y);
		else if (type == 5)
			SelectFromAList(v, circlelist, x, y);
		else if (type == 6)
			SelectFromAList(v, anglelist, x, y);
		else if (type == 7)
			SelectFromAList(v, distancelist, x, y);
		else if (type == 8)
			SelectFromAList(v, polygonlist, x, y);
		else if (type == 9)
			SelectFromAList(v, textlist, x, y);
		else if (type == 10)
			SelectFromAList(v, tracelist, x, y);
		else if (type == 11)
			SelectFromAList(v, otherlist, x, y);*/

	}

	public void SelectNameText(final ArrayList<GraphicEntity> v, final double x,
			final double y) {
		for (final GEText text : textlist) {
			if ((text.getType() == GEText.NAME_TEXT) && text.isLocatedNear(x, y))
				v.add(text);
			if ((text.getType() == GEText.CNAME_TEXT) && text.isLocatedNear(x, y))
				v.add(text);

		}
	}

	public GraphicEntity SelectOneFromXY(final double x, final double y, final int type) {
		final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
		SelectAllFromXY(v, x, y, type);
		if (v.size() == 0)
			return null;
		if (v.size() == 1)
			return v.get(0);
		return (GraphicEntity) popSelect(v, (int) x, (int) y);

	}

	public ArrayList<GraphicEntity> OnSelect(final double x, final double y) {

		final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
		SelectAllFromXY(v, x, y, 0);

		if (v.size() == 0)
			clearSelection();
		else if (v.size() == 1) {
			if (SelectList.containsAll(v)) {
				removeAllSelections(v);
				return SelectList;
			} else {
				final GraphicEntity cc = v.get(0);
				SelectList.addAll(v);
				v.clear();
				if (cc.m_type == GraphicEntity.ANGLE) {
					final GEAngle ag = (GEAngle) cc;
					v.add(ag.lstart);
					v.add(ag.lend);
					flashStep(v);
				}
			}
		} else {
			final Object obj = popSelect(v, (int) x, (int) y);
			if ((obj != null) && (obj instanceof GraphicEntity))
				selectGraphicEntity((GraphicEntity) obj);
		}
		return v;
	}

	public static void getSmartPV(final GEPoint p1, final GEPoint p2) {
		if ((p1 == null) || (p2 == null))
			return;

		final int x1 = (int) p1.getx();
		final int y1 = (int) p1.gety();
		final int x2 = (int) p2.getx();
		final int y2 = (int) p2.gety();

		if ((Math.abs(x2 - x1) < UtilityMiscellaneous.PIXEPS)
				&& ((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) > (4 * UtilityMiscellaneous.PIXEPS * UtilityMiscellaneous.PIXEPS)))
			p2.setXY(x1, y2);
		else if ((Math.abs(y2 - y1) < UtilityMiscellaneous.PIXEPS)
				&& ((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) > (4 * UtilityMiscellaneous.PIXEPS * UtilityMiscellaneous.PIXEPS)))
			p2.setXY(x2, y1);
	}

	public ProofText SelectProveText(final double x, final double y) {
		if (cpfield == null)
			return null;
		return cpfield.select(x, y, false);
	}

	public void clearSelection() {
		SelectList.clear();
		if (gxInstance != null)
			gxInstance.updateActionPool(CurrentAction);
	}

	public void addToSelectList(final GraphicEntity ge) {
		if (ge != null) {
			SelectList.add(ge);
			if (gxInstance != null)
				gxInstance.updateActionPool(CurrentAction);
		}

	}

	public void removeAllSelections(final Collection<GraphicEntity> v) {
		SelectList.removeAll(v);
		if (gxInstance != null)
			gxInstance.updateActionPool(CurrentAction);
	}

	public ArrayList<GraphicEntity> OnCatch(final double x, final double y) {
		CatchList.clear();
		SelectAllFromXY(CatchList, x, y, 0);
		return CatchList;
	}

	public static boolean check_animation(final GEPoint p, final GELine ln) {
		if ((p == null) || (ln == null))
			return false;
		if (p.isAFixedPoint())
			return false;

		if (p.isAFreePoint() && ln.containsPoints(p))
			return false;
		return true;
	}

	@Override
	public void DWButtonDown(double x, double y) {
		GEPoint p = null;
		CatchList.clear();
		bLeftButtonDown = true;
		if (SNAP && (CurrentAction != SELECT)) {
			final double[] r = getSnap(x, y);
			x = r[0];
			y = r[1];
		}
		CatchPoint.setXY(x, y);

		switch (CurrentAction) {
		case SELECT: {
			final GEPoint t = SelectAPoint(x, y);
			boolean bClear = false;

			if (gxInstance.isDialogProveVisible()) {
				clearSelection();
				if (t != null)
					addToSelectList(t);
				bClear = true;
				gxInstance.getDialogProve().setSelect(SelectList);
			}

			if (t == null) {
				if (cpfield != null) {
					final ProofText ct1 = cpfield.mouseMove(x, y);
					if (ct1 == null) {
						bClear = true;
						final ProofText ct = cpfield.select(x, y, false);
						if (ct != null) {
							final UndoStruct un = ct.getUndoStruct();
							if (un != null) {
								final ArrayList<GraphicEntity> vv = new ArrayList<GraphicEntity>();
								un.getAllObjects(this, vv);
								setObjectListForFlash(vv);
							}
						}
					} else {
						//final Point pt = ct1.getPopExLocation();
						//gxInstance.showRulePanel("R1", (int) pt.getX(), (int) pt.getY());
					}
				}

			} else {
				if (gxInstance.hasManualInputBar()) {
					final ProofPanel pp = gxInstance.getProofPanel();
					bClear = pp.selectAPoint(t);
					if (bClear)
						setObjectListForFlash(t);
				}
				gxInstance.selectAPoint(t);
			}

			if (bClear) 
				clearSelection();
			else {
				CatchList.clear();
				SelectAllFromXY(CatchList, x, y, 0);
				if (CatchList.size() == 0)
					clearSelection();
				else
					addToSelectList(CatchList.get(0));
			}

			vx1 = x;
			vy1 = y;
		}
		break;
		case MOVE: {
			FirstPnt = createTempPoint(x, y);
			final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();

			SelectAllFromXY(v, x, y, 0);
			if (v.size() == 0) {
				clearSelection();
				if (cpfield != null) {
					final ProofText ct1 = cpfield.mouseMove(x, y);
					if (ct1 == null) {
						final ProofText ct = cpfield.select(x, y, false);
						if (ct != null) {
							final UndoStruct un = ct.getUndoStruct();
							if (un != null) {
								final ArrayList<GraphicEntity> vv = new ArrayList<GraphicEntity>();
								un.getAllObjects(this, vv);
								setObjectListForFlash(vv);
							}

						}
					} else {
						//final Point pt = ct1.getPopExLocation();
						//gxInstance.showRulePanel(ct1.getRulePath(), (int) pt.getX(), (int) pt.getY());
					}
				}
			} else if (v.size() == 1) {
				clearSelection();
				SelectList.addAll(v);
				final GraphicEntity cc = v.get(0);
				v.clear();
				if (cc instanceof GEPoint)
					if (gxInstance != null) {
						if (gxInstance.isConclusionDialogVisible())
							gxInstance.getConclusionDialog()
							.selectAPoint((GEPoint) cc);
						if (gxInstance.hasManualInputBar())
							gxInstance.getMannalInputToolBar().selectAPoint(
									(GEPoint) cc);
					}
			} else {
				clearSelection();
				addToSelectList(v.get(0));
			}

			if (SelectList.size() == 1)
				if (gxInstance != null)
					gxInstance.viewElementsAuto(SelectList.get(0));
		}
		break;
		case D_POINT: {
			clearSelection();
			p = SmartAddPoint(x, y);
			if (p != null) {
				addToSelectList(p);
				UndoAdded(p.TypeString());
			}
		}
		break;
		case TRIANGLE: {

			if (STATUS == 0) {
				GEPoint pp = (GEPoint) CatchList(pointlist, x, y);
				if (pp == null)
					pp = SmartgetApointFromXY(x, y);

				addToSelectList(pp);
				FirstPnt = pp;
				STATUS = 1;

			} else if (STATUS == 1) {
				GEPoint pp = (GEPoint) CatchList(pointlist, x, y);
				if (pp == null)
					pp = SmartgetApointFromXY(x, y);

				if (!SelectList.contains(pp)) {
					addToSelectList(pp);
					SecondPnt = pp;
					STATUS = 2;
				}

			} else {
				GEPoint pp = (GEPoint) CatchList(pointlist, x, y);
				if (pp == null)
					pp = SmartgetApointFromXY(x, y);

				if (!SelectList.contains(pp))
					addToSelectList(pp);
				else
					break;

				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final GEPoint p3 = (GEPoint) SelectList.get(2);
				final GELine line1 = new GELine(p1, p2);
				final GELine line2 = new GELine(p1, p3);
				final GELine line3 = new GELine(p2, p3);
				addPointToList(p1);
				addPointToList(p2);
				addPointToList(p3);
				addLineToList(line1);
				addLineToList(line2);
				addLineToList(line3);
				final Constraint cs = new Constraint(Constraint.TRIANGLE, p1,
						p2, p3);
				addConstraintToList(cs);
				UndoAdded("Triangle " + p1.m_name + p2.m_name + p3.m_name);
				FirstPnt = SmartgetApointFromXY(x, y);
				SecondPnt = createTempPoint(x, y);
				clearSelection();
				STATUS = 0;
			}
		}
		break;
		case H_LINE:
		case V_LINE:
			if (STATUS == 0) {
				FirstPnt = SmartgetApointFromXY(x, y);
				SecondPnt = createTempPoint(x, y);
				STATUS = 1;
			}
			break;
		case D_LINE: {
			if (STATUS == 0) {
				if ((FirstPnt = SmartgetApointFromXY(x, y)) != null) {
					STATUS = 1;
					addPointToList(FirstPnt);
					addToSelectList(FirstPnt);
				}
			} else if (STATUS == 1) {
				final GEPoint tp = FirstPnt;
				if (isPointOnObject) {
					x = mouseCatchX;
					y = mouseCatchY;
				}
				final GEPoint pp = SmartgetApointFromXY(x, y);
				// pp.setXY(x, y);
				getSmartPV(FirstPnt, pp);

				if (tp != pp && tp != null && pp != null) {
					setSmartPVLine(tp, pp);
					addPointToList(pp);
					final GELine ln = new GELine(pp, tp);
					addLineToList(ln);
					final Constraint cs = new Constraint(Constraint.LINE, tp, pp);
					addConstraintToList(cs);
					reCalculate();
					UndoAdded(ln.getDescription());
				}
				clearSelection();
				STATUS = 0;
				FirstPnt = null;
			}
		}
		break;
		case D_POLYGON: {
			final GEPoint pt = SmartgetApointFromXY(x, y);
			setSmartPVLine(FirstPnt, pt);
			boolean finish = false;

			if (SelectList.size() == 0) {
				addPointToList(pt);
				addToSelectList(pt);
				FirstPnt = pt;
				SecondPnt = createTempPoint(x, y);
			} else if (pt == SelectList.get(0))
				finish = true;
			else if (SelectList.contains(pt))
				break;
			else {
				addPointToList(pt);
				addToSelectList(pt);
				if (SelectList.size() == STATUS)
					finish = true;
				FirstPnt = pt;
			}
			if (finish) {
				if (SelectList.size() <= 1) {
					clearSelection();
					return;
				}
				final GEPoint t1 = (GEPoint) SelectList.get(0);
				GEPoint tp = t1;
				for (int i = 1; i < SelectList.size(); i++) {
					final GEPoint tt = (GEPoint) SelectList.get(i);
					if (findLineGivenTwoPoints(tt, tp) == null) {
						final GELine ln = new GELine(tt, tp);
						addLineToList(ln);
					}
					tp = tt;
				}
				if (findLineGivenTwoPoints(t1, tp) == null) {
					final GELine ln = new GELine(t1, tp);
					addLineToList(ln);
				}

				String s = "";
				final int size = SelectList.size();
				for (int i = 0; i < size; i++) {
					final GraphicEntity cc = SelectList.get(i);
					s += cc.m_name;

				}
				if (size == 3) {
					final Constraint cs = new Constraint(Constraint.TRIANGLE,
							SelectList);
					addConstraintToList(cs);

					UndoAdded("triangle  " + s);
				} else if (size == 4) {
					final Constraint cs = new Constraint(Constraint.QUADRANGLE,
							SelectList);
					addConstraintToList(cs);
					UndoAdded("quadrangle  " + s);
				} else if (size == 5) {
					final Constraint cs = new Constraint(Constraint.PENTAGON,
							SelectList);
					addConstraintToList(cs);
					UndoAdded("pentagon  " + s);
				} else {
					final Constraint cs = new Constraint(Constraint.POLYGON,
							SelectList);
					addConstraintToList(cs);
					UndoAdded("polygon  " + s);
				}
				clearSelection();
			}
		}
		break;
		case D_PARELINE: {
			if (STATUS == 0) {
				clearSelection();
				final GELine line = SmartPLine(CatchPoint);

				if (line == null)
					break;
				addToSelectList(line);
				STATUS = 1;
			} else if (STATUS == 1) {
				if (SelectList.size() == 0)
					break;
				final GEPoint pt = SmartgetApointFromXY(x, y);
				final GELine line = (GELine) SelectList.get(0);

				final GELine line1 = new GELine(GELine.PLine, pt);
				final Constraint cs = new Constraint(Constraint.PARALLEL,
						line1, line);
				addConstraintToList(cs);
				line1.addConstraint(cs);
				clearSelection();
				addLineToList(line1);
				final UndoStruct u = UndoAdded(line1.TypeString()
						+ " parallel " + line.getDescription2() + " passing "
						+ pt.getname());
				u.adddOjbect(line1);
				u.adddOjbect(line);
				u.adddOjbect(pt);
				clearSelection();
				STATUS = 0;
			}
		}
		break;

		case D_PERPLINE: {
			if (STATUS == 0) {
				clearSelection();
				final GELine line = SmartPLine(CatchPoint);
				if (line == null)
					break;

				addToSelectList(line);
				STATUS = 1;
			} else if (STATUS == 1) {
				if (SelectList.size() == 0)
					break;
				final GELine line = (GELine) SelectList.get(0);
				final GEPoint pt = SmartgetApointFromXY(x, y);

				final GELine line1 = new GELine(GELine.TLine, pt);
				final Constraint c = new Constraint(Constraint.PERPENDICULAR,
						line1, line);
				addConstraintToList(c);
				line1.addConstraint(c);
				addLineToList(line1);
				addCTMark(line, line1);
				// otherlist.add(m);
				final UndoStruct u = UndoAdded(line1.TypeString() + " perp "
						+ line.getDescription2() + " passing " + pt.getname());
				u.adddOjbect(line1);
				u.adddOjbect(line);
				u.adddOjbect(pt);
				STATUS = 0;
				clearSelection();
			}
		}
		break;
		case D_ALINE: {
			final int n = SelectList.size();
			if (n < 3) {
				final GELine line = SmartPLine(CatchPoint);
				if (line == null)
					break;
				if (n == 1) {
					final GELine ln1 = (GELine) SelectList.get(0);
					if (GELine.commonPoint(ln1, line) == null) {
						JOptionPane.showMessageDialog(gxInstance,
								getLanguage(1025, "The two selected line don't"
										+ "have any intersected point"),
										getLanguage(302, "Warning"),
										JOptionPane.WARNING_MESSAGE);
						break;
					}
				}
				addToSelectList(line);
			} else {
				final GELine ln1 = (GELine) SelectList.get(0);
				final GELine ln2 = (GELine) SelectList.get(1);
				final GELine ln3 = (GELine) SelectList.get(2);
				GEPoint tt = null;
				if ((SmartPLine(CatchPoint) == ln3)
						|| (((tt = SmartPoint(CatchPoint)) != null) && ln3
								.containsPoints(tt))) {
					final GEPoint p1 = SmartgetApointFromXY(x, y);
					final GELine ln = new GELine(GELine.ALine);
					ln.add(p1);
					final Constraint cs = new Constraint(Constraint.ALINE, ln1,
							ln2, ln3, ln);
					cs.setPolyGenerate(false);

					ln.addConstraint(cs);
					addLineToList(ln);
					addConstraintToList(cs);
					clearSelection();
					UndoAdded("ALine " + ln.getname());
				}
			}
		}
		break;
		case D_ABLINE: {
			int n = SelectList.size();
			if (STATUS == 0) {
				p = SelectAPoint(x, y);
				if (p != null) {
					addToSelectList(p);
					STATUS = 1;
				} else {
					final GELine ln = SelectALine(x, y);
					if (ln != null) {
						addToSelectList(ln);
						CatchPoint.setXY(x, y);
						ln.pointonline(CatchPoint);
						catchX = CatchPoint.getx();
						catchY = CatchPoint.gety();
					}
					STATUS = 2;
				}
			} else if (STATUS == 5) {
				// CLine ln = (CLine) SelectList.get(0);
			} else {

				if ((n < 3) && (STATUS == 1))
					addSelectPoint(x, y);
				else if ((n < 2) && (STATUS == 2)) {
					final GELine ln = SelectALine(x, y);
					if (ln != null) {
						if (SelectList.size() < 1)
							break;
						final GELine ln0 = (GELine) SelectList.get(0);
						if (GELine.commonPoint(ln0, ln) != null)
							addToSelectList(ln);
						else
							JOptionPane
							.showMessageDialog(
									gxInstance,
									DrawPanelFrame.getLanguage(1025,
											"The selected two line don't have intersected point"),
											DrawPanelFrame.getLanguage(1026,
													"No Intersected point"),
													JOptionPane.WARNING_MESSAGE);
					}
				}
				n = SelectList.size();
				{
					GEPoint p1, p2, p3;
					boolean dd = true;
					if ((STATUS == 1) && (n == 3)) {
						p1 = (GEPoint) SelectList.get(0);
						p2 = (GEPoint) SelectList.get(1);
						p3 = (GEPoint) SelectList.get(2);
					} else if ((STATUS == 2) && (n == 2)) {
						final GELine ln1 = (GELine) SelectList.get(0);
						final GELine ln2 = (GELine) SelectList.get(1);
						p2 = GELine.commonPoint(ln1, ln2);
						p1 = ln1.get_Lptv(p2, catchX, catchY);
						p3 = ln2.get_Lptv(p2, x, y);
						dd = false;
					} else
						break;

					if ((p3 != null) && (p3 != p1) && (p3 != p2)) {
						final GELine ln = new GELine(GELine.ABLine);
						ln.add(p2);
						if (dd) {
							final GEPoint pt = CreateANewPoint(0, 0);
							ln.add(pt);
							addALine(p1, p3);
							final Constraint cs = new Constraint(
									Constraint.ANGLE_BISECTOR, p1, p2, p3, ln);
							// constraint cs1 = new
							// constraint(constraint.PONLINE, pt, ln1);
							ln.addConstraint(cs);
							addPointToList(pt);
							addLineToList(ln);
							addConstraintToList(cs);
							characteristicSetMethodAndAddPoly(false);
							clearSelection();
							STATUS = 0;
							UndoAdded(ln.getSimpleName()
									+ " is the bisector of angle " + p1 + p2
									+ p3, true, ln.getPtsSize() > 1);
						} else {
							final Constraint cs = new Constraint(
									Constraint.ANGLE_BISECTOR, p1, p2, p3, ln);
							ln.addConstraint(cs);
							addLineToList(ln);
							addConstraintToList(cs);
							clearSelection();
							STATUS = 0;
							UndoAdded("Angle Bisector " + ln.getname(), true,
									ln.getPtsSize() > 1);
						}
					}
				}
			}
		}
		break;
		case D_PFOOT: {
			if (STATUS == 0) {
				final GEPoint pt = SmartgetApointFromXY(x, y);
				if (SelectList.size() == 1) {
					final GEPoint pa = (GEPoint) SelectList.get(0);
					setSmartPVLine(pa, pt);
					if (findLineGivenTwoPoints(pa, pt) == null) {
						final GELine ln = new GELine(pa, pt);
						addLineToList(ln);
					}
				}
				if (!SelectList.contains(pt))
					addToSelectList(pt);
				if (SelectList.size() == 2)
					STATUS = 2;
			} else if (STATUS == 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);

				final double[] r = get_pt_dmcr(p1.getx(), p1.gety(), p2.getx(),
						p2.gety(), x, y);
				final double xr = r[0];
				final double yr = r[1];
				p = SmartgetApointFromXY(xr, yr);
				if ((p == p1) || (p == p2))
					break;
				GELine ln1, ln2;
				ln1 = ln2 = null;
				if ((ln1 = findLineGivenTwoPoints(p, p1)) == null) {
					ln1 = new GELine(p1, p);
					addLineToList(ln1);
				}
				if ((ln2 = findLineGivenTwoPoints(p, p2)) == null) {
					ln2 = new GELine(p2, p);
					addLineToList(ln2);
				}
				final Constraint cs = new Constraint(
						Constraint.RIGHT_ANGLED_TRIANGLE, p, p1, p2);
				addConstraintToList(cs);
				characteristicSetMethodAndAddPoly(false);
				if (!doesLineBetweenTwoPointsExist(p1, p2)) {
					final GELine lp = new GELine(p1, p2);
					addLineToList(lp);
				}
				clearSelection();
				STATUS = 0;
				addCTMark(ln1, ln2);
				// otherlist.add(m);
				UndoAdded("right triangle " + p1.getname() + p2.getname()
						+ p.getname());
			}
		}
		break;

		case PERPWITHFOOT: {
			if (STATUS == 0) {
				final GEPoint tp = SmartgetApointFromXY(x, y);
				FirstPnt = tp;
				STATUS = 1;
			} else if (STATUS == 1) {
				final GEPoint pt = SmartPoint(CatchPoint);
				if (pt == FirstPnt)
					break;
				final GELine line = SmartPLine(CatchPoint);
				if (line == null)
					break;
				final GEPoint pp = CreateANewPoint(0, 0);
				add_PFOOT(line, FirstPnt, pp);
				STATUS = 0;
			}
		}
		break;
		case D_CIRCLE: {
			if (STATUS == 0) {
				p = SmartgetApointFromXY(x, y);
				if (p != null) {
					FirstPnt = p;
					addToSelectList(p);
					addPointToList(p);
					STATUS = 1;
				}
			} else if (STATUS == 1) {
				p = SmartgetApointFromXY(x, y);
				if (p == FirstPnt)
					break;

				final GECircle c = new GECircle(FirstPnt, p);
				addCircleToList(c);
				final Constraint cs = new Constraint(Constraint.CIRCLE,
						FirstPnt, p);
				addConstraintToList(cs);
				characteristicSetMethodAndAddPoly(false);
				UndoAdded(c.getDescription());
				STATUS = 0;
				clearSelection();
			}
		}
		break;
		case D_CIR_BY_DIM: {

		}
		break;
		case D_CIRCLEBYRADIUS: {
			if (SelectList.size() < 2) {
				p = (GEPoint) CatchList(pointlist, x, y);
				if (p != null)
					selectGraphicEntity(p);
			} else {
				p = SmartgetApointFromXY(x, y);
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);

				final GECircle cr = new GECircle(GECircle.RCircle, p);
				final Constraint cs = new Constraint(Constraint.RCIRCLE, p1,
						p2, cr);
				cr.addConstraint(cs);
				addConstraintToList(cs);
				addCircleToList(cr);

				STATUS = 0;
				clearSelection();
				FirstPnt = SecondPnt = null;
				UndoAdded(cr.getDescription());
			}
		}
		break;
		case D_PRATIO: {
			if (SelectList.size() < 2) {
				p = SelectAPoint(x, y);
				if (p != null)
					selectGraphicEntity(p);
				else
					clearSelection();
			} else {
				final GEPoint px = SmartgetApointFromXY(x, y);
				if (px != null) {
					final GEPoint p1 = (GEPoint) SelectList.get(0);
					final GEPoint p2 = (GEPoint) SelectList.get(1);

					p = CreateANewPoint(x, y);
					final Constraint cs = new Constraint(Constraint.PRATIO, p,
							px, p1, p2, new Integer(v1), new Integer(v2));
					final GEPoint pu = addADecidedPointWithUnite(p);
					if (pu == null) {
						addConstraintToList(cs);
						addPointToList(p);
						if (true) {
							final GELine ln = findLineGivenTwoPoints(p1, p2);
							if (status && ((ln == null) || !ln.containsPoints(px))) {
								final GELine ln1 = new GELine(px, p);
								addLineToList(ln1);
							} else {
								final Constraint cs1 = new Constraint(
										Constraint.PONLINE);
								cs1.setPolyGenerate(false);
								cs1.addElement(p);
								cs1.addElement(ln);
								addConstraintToList(cs1);
								if (status && (ln != null))
									ln.add(p);
							}
						}
						UndoAdded(cs.getMessage());
					} else
						p = pu;
					clearSelection();
				} else
					clearSelection();
			}
		}
		break;
		case D_TRATIO: {
			if (SelectList.size() < 2) {
				p = SelectAPoint(x, y);
				if (p != null)
					selectGraphicEntity(p);
			} else {
				p = SmartgetApointFromXY(x, y);
				if (SelectList.size() != 2)
					break;

				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final GEPoint px = p;
				p = CreateANewPoint(x, y);
				// double dx = p2.getx() - p1.getx();
				// double dy = p2.gety() - p1.gety();

				final Constraint cs = new Constraint(Constraint.TRATIO, p, px,
						p1, p2, new Integer(v1), new Integer(v2));
				final GEPoint pu = addADecidedPointWithUnite(p);
				if (pu == null) {
					addConstraintToList(cs);
					addPointToList(p);
					if (true) {
						final GELine ln = findLineGivenTwoPoints(p, px);
						if (status && (ln == null)) {
							final GELine ln1 = new GELine(px, p);
							addLineToList(ln1);
						} else {
							final Constraint cs1 = new Constraint(
									Constraint.PONLINE);
							cs1.setPolyGenerate(false);
							cs1.addElement(p);
							cs1.addElement(ln);
							addConstraintToList(cs1);
							if (status && (ln != null))
								ln.add(p);
						}
					}
				} else
					p = pu;
				clearSelection();
				STATUS = 0;
				UndoAdded(cs.getMessage());
			}
		}
		break;
		case D_PTDISTANCE: {
			if (SelectList.size() < 3) {
				final GEPoint pt = createTempPoint(x, y);
				p = SmartPoint(pt);
				// String s = null;
				if (p != null) {
					addToSelectList(p);
					// s = (p.m_name + "  selected");
					setObjectListForFlash(p);
				}
				// switch (SelectList.size()) {
				// case 0:
				// gxInstance.setTipText(s + ',' + " Please Select a Point");
				// break;
				// case 1:
				// gxInstance.setTipText("first point  " + s + ',' +
				// "  please select the second point");
				// break;
				// case 2:
				// gxInstance.setTipText("second point  " + s + ',' +
				// "  please select the third point");
				// break;
				// case 3:
				// gxInstance.setTipText("third point  " + s + ',' +
				// "  select a line or a circle");
				// }
			} else {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final GEPoint p3 = (GEPoint) SelectList.get(2);
				GECircle c = null;
				final GELine ln = SelectALine(x, y);
				if (ln != null) {
					final double r = ln.distance(p3.getx(), p3.gety());
					final double r1 = sdistance(p1, p2);
					if (r < r1) {
						final GEPoint pt = CreateANewPoint(x, y);
						addPointToLine(pt, ln, false);
						final Constraint cs = new Constraint(
								Constraint.EQDISTANCE, p1, p2, p3, pt);
						characteristicSetMethodAndAddPoly(true);
						if (mulSolutionSelect(pt)) {
							addConstraintToList(cs);
							addPointToList(pt);
							UndoAdded("Take a point " + pt.m_name + " on "
									+ ln.getDescription() + " st " + p1.m_name
									+ p2.m_name + " = " + p3.m_name + pt.m_name);

						} else {
							ErasedADecidedPoint(pt);
							ln.points.remove(pt);
						}
					} else
						JOptionPane.showMessageDialog(gxInstance,
								"Can not add a point", "No Solution",
								JOptionPane.ERROR_MESSAGE);

				} else if ((c = SelectACircle(x, y)) != null) {
					final GEPoint po = c.o;
					final double d = sdistance(po, p3);
					final double r = c.getRadius();
					final double s = sdistance(p1, p2);
					final double d1 = d + r;
					final double d2 = Math.abs(d - r);
					if ((s > d1) || (s < d2))
						JOptionPane.showMessageDialog(gxInstance,
								"Can not add a point", "No Solution",
								JOptionPane.ERROR_MESSAGE);
					else {
						final GEPoint pt = CreateANewPoint(0, 0);
						final Constraint cs = new Constraint(
								Constraint.EQDISTANCE, p1, p2, p3, pt);
						final Constraint cs1 = new Constraint(
								Constraint.PONCIRCLE, pt, c);
						characteristicSetMethodAndAddPoly(true);
						if (mulSolutionSelect(pt)) {
							addConstraintToList(cs);
							addConstraintToList(cs1);
							addPointToList(pt);
							c.add(pt);
							UndoAdded("Take a point " + pt.m_name + "on "
									+ c.getDescription() + " st " + p1.m_name
									+ p2.m_name + " = " + p3.m_name + pt.m_name);
						} else {
							ErasedADecidedPoint(pt);
							gxInstance
							.setTipText("Failed: can not find a point(P) on Circle "
									+ " that satisfy |"
									+ p1.m_name
									+ p2.m_name
									+ "| = |"
									+ p3.m_name
									+ "P|");
						}
					}

				} else {
				}
				clearSelection();
			}
		}
		break;
		case LRATIO: {
			final GEPoint pt = createTempPoint(x, y);
			p = SmartPoint(pt);
			if (p == null)
				break;
			if (SelectList.size() == 0)
				selectGraphicEntity(p);
			else {
				if (p == SelectList.get(0))
					break;
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				GEPoint pp = CreateANewPoint(x, y);
				final Integer t1 = new Integer(v1);
				final Integer t2 = new Integer(v2);
				final Constraint cs = new Constraint(Constraint.LRATIO, pp, p1,
						p, t1, t2);
				final GEPoint pu = addADecidedPointWithUnite(pp);
				if (pu == null) {
					addConstraintToList(cs);
					addPointToList(pp);
				} else {
					pp = pu;
					clearSelection();
					resetUndo();
					break;
				}

				GELine ln = null;
				for (int i = 0; i < linelist.size(); i++) {
					final GELine t = linelist.get(i);
					if (t.sameLine(p1, p)) {
						ln = t;
						break;
					}
				}
				if (ln != null)
					ln.add(pp);
				characteristicSetMethodAndAddPoly(false);
				clearSelection();
				UndoAdded(pp.TypeString() + ":  " + p1.m_name + pp.m_name
						+ " / " + pp.m_name + p.m_name + " = " + 1 + "/"
						+ proportion);
			}
		}
		break;
		case MEET: {
			GraphicEntity cc = SelectALine(x, y);
			if (cc == null)
				cc = SelectACircle(x, y);

			if (cc == null) {
				clearSelection();
				break;
			}
			selectGraphicEntity(cc);

			if (SelectList.size() == 1)
				break;
			else if (SelectList.size() == 2) {
				final GraphicEntity obj1 = SelectList.get(0);
				final GraphicEntity obj2 = SelectList.get(1);
				meetTwoObject(obj1, obj2, false, x, y);
				clearSelection();
			}
		}
		break;
		case MIRROR: {
			CatchPoint.setXY(x, y);
			GELine ln = null;
			p = SmartPoint(CatchPoint);
			if (p == null) {
				ln = SmartPLine(CatchPoint);
				if (ln != null)
					selectGraphicEntity(ln);
				else {
					final GECircle c = SmartPCircle(CatchPoint);
					if (c != null)
						selectGraphicEntity(c);
				}
			} else
				selectGraphicEntity(p);

			if (SelectList.size() == 2) {
				Object obj1, obj2;
				obj1 = SelectList.get(0);
				obj2 = SelectList.get(1);
				if ((obj1 instanceof GEPoint) && (obj2 instanceof GEPoint)) {
					final GEPoint p1 = (GEPoint) obj1;
					final GEPoint p2 = (GEPoint) obj2;
					GEPoint pp = CreateANewPoint(0, 0);
					final Constraint cs = new Constraint(Constraint.PSYM, pp,
							p1, p2);
					final GEPoint pu = addADecidedPointWithUnite(pp);
					if (pu == null) {
						addPointToList(pp);
						addConstraintToList(cs);
						UndoAdded(pp.TypeString() + " is reflection of "
								+ p1.TypeString() + " wrpt " + p2.TypeString());

					} else
						pp = pu;

				} else if ((obj1 instanceof GEPoint) && (obj2 instanceof GELine)) {
					final GEPoint p1 = (GEPoint) obj1;
					final GELine line = (GELine) obj2;

					GEPoint pp = CreateANewPoint(0, 0);
					final Constraint cs = new Constraint(Constraint.MIRROR, pp,
							p1, line);
					final GEPoint pu = addADecidedPointWithUnite(pp);
					if (pu == null) {
						addPointToList(pp);
						addConstraintToList(cs);
						UndoAdded(pp.TypeString() + " is reflection of "
								+ p1.TypeString() + " wrpt "
								+ line.getDescription2());

					} else
						pp = pu;

				} else if ((obj1 instanceof GELine) && (obj2 instanceof GEPoint)) {
					final GELine line = (GELine) obj1;
					final GEPoint p1 = (GEPoint) obj2;

					int exist_point_number = 0;
					final ArrayList<GEPoint> vp = new ArrayList<GEPoint>();

					for (int i = 0; i < line.getPtsSize(); i++) {
						GEPoint pu = null;
						GEPoint pp = null;
						Constraint cs = null;

						final GEPoint pt = line.points.get(i);
						if (pt == p1)
							pu = pt;
						else {
							pp = CreateANewPoint(0, 0);
							cs = new Constraint(Constraint.PSYM, pp, pt, p1);
							pu = addADecidedPointWithUnite(pp);
						}
						if (pu == null) {
							addPointToList(pp);
							addConstraintToList(cs);
						} else {
							pp = pu;
							exist_point_number++;
						}
						vp.add(pp);
					}

					if (exist_point_number < line.getPtsSize()) {
						if (line.points.contains(p1)) {
							for (final GEPoint tt : vp)
								line.add(tt);
							UndoAdded("reflection");

						} else {
							final GELine line2 = new GELine(line.linetype);
							line2.m_color = line.m_color;
							line2.m_dash = line.m_dash;
							line2.m_width = line.m_width;

							for (final GEPoint tt : vp)
								line2.add(tt);
							final Constraint cs = new Constraint(
									Constraint.LINE, vp);
							addConstraintToList(cs);
							addLineToList(line2);
							UndoAdded(line2.TypeString() + " is reflection of "
									+ line.getDescription2() + " wrpt "
									+ p1.TypeString());
						}

					} else {
						boolean exists = false;
						for (int i = 0; i < linelist.size(); i++) {
							final GELine ll = linelist.get(i);
							if (ll.points.containsAll(vp)) {
								exists = true;
								break;
							}
						}
						if (exists == false) {
							final GELine line2 = new GELine(line.linetype);
							for (int i = 0; i < vp.size(); i++) {
								final GEPoint tt = vp.get(i);
								line2.add(tt);
							}
							line2.m_color = ln.m_color;
							line2.m_dash = ln.m_dash;
							line2.m_width = ln.m_width;
							final Constraint cs = new Constraint(
									Constraint.LINE, vp);
							addConstraintToList(cs);
							addLineToList(line2);
							UndoAdded(line2.getDescription2()
									+ " is reflection of "
									+ line.getDescription2() + " wrpt "
									+ p1.TypeString());

						} else
							UndoAdded("reflection");
					}
				} else if ((obj1 instanceof GELine) && (obj2 instanceof GELine)) {
					final GELine line = (GELine) obj1;
					final GELine line2 = (GELine) obj2;
					final GEPoint cp = GELine.commonPoint(line, line2);

					final GELine line3 = new GELine(line.linetype);
					line3.m_color = line.m_color;
					line3.m_dash = line.m_dash;
					line3.m_width = line.m_width;

					int exist_point_number = 0;
					for (final GEPoint pt : line.points) {

						GEPoint pp;
						if (pt == cp) {
							pp = cp;
							exist_point_number++;
						} else {
							pp = CreateANewPoint(0, 0);
							final Constraint cs = new Constraint(
									Constraint.MIRROR, pp, pt, line2);
							final GEPoint pu = addADecidedPointWithUnite(pp);
							if (pu == null) {
								addPointToList(pp);
								addConstraintToList(cs);
							} else {
								pp = pu;
								exist_point_number++;
							}

						}
						line3.add(pp);
					}
					final Constraint cs = new Constraint(Constraint.LINE,
							line3.points);
					addConstraintToList(cs);

					if (exist_point_number < line.getPtsSize()) {
						addLineToList(line3);

						UndoAdded(line3.getDescription2() + " is reflection of "
								+ line.getDescription2() + " wrpt "
								+ line2.getDescription2());

					} else {
						boolean exists = false;
						for (final GELine ll : linelist)
							if (ll.sameLine(line3)) {
								exists = true;
								break;
							}
						if (exists == false) {
							addLineToList(line3);
							UndoAdded(line3.getDescription2()
									+ " is reflection of "
									+ line.getDescription2() + " wrpt "
									+ line2.getDescription2());
						}
					}

				} else if ((obj1 instanceof GECircle) && (obj2 instanceof GEPoint)) {
					int exist_point_number = 0;

					final GECircle c1 = (GECircle) obj1;
					final GEPoint p1 = (GEPoint) obj2;
					GEPoint pp = CreateANewPoint(0, 0);
					Constraint cs = new Constraint(Constraint.PSYM, pp, c1.o,
							p1);
					final GEPoint pu = addADecidedPointWithUnite(pp);
					if (pu == null) {
						addPointToList(pp);
						addConstraintToList(cs);
					} else {
						exist_point_number++;
						pp = pu;
					}

					GECircle c = null;
					for (int i = 0; i < c1.points.size(); i++) {
						final GEPoint pt = c1.points.get(i);
						p = CreateANewPoint(0, 0);
						cs = new Constraint(Constraint.PSYM, p, pt, p1);
						final GEPoint pu1 = addADecidedPointWithUnite(p);
						if (pu1 == null) {
							addPointToList(p);
							addConstraintToList(cs);
						} else {
							p = pu1;
							exist_point_number++;
						}

						if (i == 0) {
							c = new GECircle(pp, p);
							c.m_color = c1.m_color;
							c.m_dash = c1.m_dash;
							c.m_width = c1.m_width;
						} else
							c.add(p);
					}
					cs = new Constraint(Constraint.CIRCLE, c.o);
					cs.addElement(c.points);
					cs.PolyGenerate();

					addConstraintToList(cs);

					if (exist_point_number < (c1.points.size() + 1)) {
						addCircleToList(c);
						UndoAdded(c.getDescription() + " is reflection of "
								+ c1.getDescription() + " wrpt "
								+ p1.TypeString());
					}

				} else if ((obj1 instanceof GECircle) && (obj2 instanceof GELine)) {
					int exist_point_number = 0;

					final GECircle c1 = (GECircle) obj1;
					final GELine line = (GELine) obj2;
					GEPoint pp = CreateANewPoint(0, 0);
					Constraint cs = new Constraint(Constraint.MIRROR, pp, c1.o,
							line);
					final GEPoint pu1 = addADecidedPointWithUnite(pp);
					if (pu1 == null) {
						addPointToList(pp);
						addConstraintToList(cs);
					} else {
						pp = pu1;
						exist_point_number++;
					}

					GECircle c = null;
					for (int i = 0; i < c1.points.size(); i++) {
						final GEPoint pt = c1.points.get(i);
						p = CreateANewPoint(0, 0);
						cs = new Constraint(Constraint.MIRROR, p, pt, line);
						final GEPoint pu2 = addADecidedPointWithUnite(p);
						if (pu2 == null) {
							addPointToList(p);
							addConstraintToList(cs);
						} else {
							p = pu1;
							exist_point_number++;
						}
						if (i == 0) {
							c = new GECircle(pp, p);
							c.m_color = c1.m_color;
							c.m_dash = c1.m_dash;
							c.m_width = c1.m_width;
						} else
							c.add(p);

					}
					cs = new Constraint(Constraint.CIRCLE, c.o);
					cs.addElement(c.points);
					cs.PolyGenerate();
					addConstraintToList(cs);
					if (exist_point_number < (c1.points.size() + 1)) {
						addCircleToList(c);
						UndoAdded(c.getDescription() + " is reflection of "
								+ c1.getDescription() + " wrpt "
								+ line.getDescription());
					}
				} else
					UtilityMiscellaneous.print("can not mirror by a circle");
				clearSelection();
			}
		}
		break;
		case D_MIDPOINT: {

			final GEPoint tp = SelectAPoint(x, y);
			if (tp != null)
				if ((SelectList.size() == 1) && (tp != SelectList.get(0))) {
					final GEPoint tp1 = (GEPoint) SelectList.get(0);

					GEPoint po = CreateANewPoint(0, 0);
					final Constraint cs = new Constraint(Constraint.MIDPOINT,
							po, tp, tp1);
					final GEPoint pu = addADecidedPointWithUnite(po);
					if (pu == null) {
						addConstraintToList(cs);
						addPointToList(po);
						final GELine ln = findLineGivenTwoPoints(tp, tp1);
						if (ln != null) {
							ln.add(po);
							final Constraint cs2 = new Constraint(
									Constraint.PONLINE, po, ln, false);
							addConstraintToList(cs2);

						}
						UndoAdded(po.getname() + ": the midpoint of "
								+ tp1.m_name + tp.m_name);

					} else
						po = pu;
					clearSelection();
				} else
					selectGraphicEntity(tp);
		}
		break;
		case D_3PCIRCLE: {
			if (STATUS == 0) { // first click
				clearSelection();
				p = SmartgetApointFromXY(x, y);
				selectGraphicEntity(p);
				STATUS = 1;

			} else if (STATUS == 1) {
				p = SmartgetApointFromXY(x, y);
				selectGraphicEntity(p);
				if (SelectList.size() == 2)
					STATUS = 2;

			} else { // third click
				GEPoint p1, p2, p3;
				p1 = (GEPoint) SelectList.get(0);
				p2 = (GEPoint) SelectList.get(1);

				p3 = SelectAPoint(x, y);
				if (p3 != null)
					if (DrawPanelBase.check_Collinear(p1, p2, p3))
						break;

				if (p3 == null)
					p3 = SmartgetApointFromXY(x, y);
				if (p3 == null)
					break;

				if (SelectList.contains(p3))
					break;

				p = CreateANewPoint(0, 0);
				final Constraint cs = new Constraint(Constraint.CIRCLE3P, p,
						p1, p2, p3);
				final GEPoint pu = addADecidedPointWithUnite(p);
				if (pu == null) {
					final GECircle c = new GECircle(p, p1, p2, p3);
					p.m_name = get_cir_center_name();
					addPointToList(p);
					addConstraintToList(cs);
					addCircleToList(c);
					UndoAdded(c.getDescription());

				} else {
					p = pu;
					if (!doesCircleWithThreePointsExist(p1, p2, p3)) {
						final GECircle c = new GECircle(p, p1, p2, p3);
						addCircleToList(c);
						UndoAdded(c.getDescription());
					}
				}

				clearSelection();
				STATUS = 0;
			}
		}
		break;

		case TRANSLATE: {
			FirstPnt = createTempPoint(x, y);
		}
		break;
		case ZOOM_IN:
			zoom_in(x, y, 1);
			reCalculate();
			break;
		case ZOOM_OUT:
			zoom_out(x, y, 1);
			reCalculate();
			break;
		case ANIMATION: {
			CatchPoint.setXY(x, y);
			p = SmartPoint(CatchPoint);

			if (SelectList.size() == 0) {
				if (p != null)
					addToSelectList(p);
				break;
			}
			if (p != null)
				break;

			p = (GEPoint) SelectList.get(0);
			final GELine line = SmartPLine(CatchPoint);
			if ((line != null) && !check_animation(p, line))
				break;

			final ToolBarAnimate af = gxInstance.getAnimateDialog();
			if (line != null) {
				clearSelection();
				animate = new Animation(p, line, Width, Height);
				af.setAttribute(animate);
				gxInstance.showAnimatePane();
				SetCurrentAction(MOVE);
			} else {
				final GECircle c = SmartPCircle(CatchPoint);
				if (c != null) {
					clearSelection();
					animate = new Animation(p, c, Width, Height);
					af.setAttribute(animate);
					gxInstance.showAnimatePane();
					SetCurrentAction(MOVE);
				} else {
					final GETrace ct = (GETrace) selectFromList(tracelist, x, y);
					if (ct != null) {
						clearSelection();
						animate = new Animation(p, ct, Width, Height);
						af.setAttribute(animate);
						gxInstance.showAnimatePane();
						SetCurrentAction(MOVE);
					}
				}
			}

		}
		break;
		case D_ANGLE: {

			if ((STATUS == 0) && (SelectList.size() == 0)) {
				FirstPnt = createTempPoint(x, y);

				final GELine line = SmartPLine(FirstPnt);
				if (line != null)
					addToSelectList(line);
			} else if ((STATUS == 0) && (SelectList.size() == 1)) {
				SecondPnt = createTempPoint(x, y);
				final GELine line = SmartPLine(SecondPnt);
				if (line != null) {
					final GELine l2 = (GELine) SelectList.get(0);
					if (line == l2)
						break;

					final GEAngle ag = new GEAngle(l2, line, FirstPnt, SecondPnt);
					addAngleToList(ag);
					ag.move(x, y);
					clearSelection();
					addToSelectList(ag);
					STATUS = 1;
					UndoAdded(ag.getDescription(), false, false);
				}
			} else if (STATUS == 1) {
				STATUS = 0;
				clearSelection();
			}
		}
		break;
		case SETEQSIDE: {
			final GEPoint pt = (GEPoint) CatchList(pointlist, x, y);
			if (pt == null) {
				clearSelection();
				break;
			}
			if (SelectList.size() == 3) {
				final GEPoint pt1 = (GEPoint) SelectList.get(0);
				final GEPoint pt2 = (GEPoint) SelectList.get(1);
				final GEPoint pt3 = (GEPoint) SelectList.get(2);
				if (STATUS == 1) {
					final Constraint cs = new Constraint(Constraint.EQDISTANCE,
							pt1, pt2, pt3, pt);
					addConstraintToList(cs);
					characteristicSetMethodAndAddPoly(false);
					clearSelection();
					UndoAdded(pt1.m_name + pt2.m_name + " = " + pt3.m_name
							+ pt.m_name);
				} else {
					final Constraint cs = new Constraint(Constraint.NRATIO,
							pt1, pt2, pt3, pt, new Integer(v1), new Integer(v2));
					addConstraintToList(cs);
					characteristicSetMethodAndAddPoly(false);
					clearSelection();
					UndoAdded(pt1.m_name + pt2.m_name + " = " + STATUS + " "
							+ pt3.m_name + pt.m_name);

				}
			} else
				addToSelectList(pt);
		}
		break;

		case SETEQANGLE: {
			if (SelectList.size() == 0) {
				final GEAngle ag = CatchAngle(x, y);
				if (ag != null)
					addToSelectList(ag);
			} else if (SelectList.size() == 1) {
				final GEAngle ag = CatchAngle(x, y);
				final GEAngle ag1 = (GEAngle) SelectList.get(0);

				if (ag == ag1) {
					clearSelection();
					break;
				}

				if ((ag != null) && (ag != ag1)) {
					final GEPoint pd = GEAngle.canEqual(ag, ag1);
					if (pd == null) {
						UtilityMiscellaneous.print("the angle is decided,can not be set equal");
						clearSelection();
					} else {
						clearSelection();
						final Constraint cs = new Constraint(
								Constraint.EQANGLE, ag1, ag);
						addConstraintToList(cs);
						characteristicSetMethodAndAddPoly(false);
						// mulSolutionSelect(pd);
						// reCalculate();
						UndoAdded(ag.getDescription() + " = "
								+ ag1.getDescription());
					}
				}
			}

		}
		break;

		case SETEQANGLE3P: {
			final GEAngle ag = (GEAngle) selectFromList(anglelist, x, y);
			if (ag == null)
				break;
			if (SelectList.size() == 2) {
				final GEAngle ag1 = (GEAngle) SelectList.get(0);
				final GEAngle ag2 = (GEAngle) SelectList.get(1);

				final ArrayList<Integer> alist = getSpecifiedAngleList();
				final DialogSpecificAngle dlg = new DialogSpecificAngle(
						gxInstance, 2, alist);
				dlg.setLocation(400, 400);
				dlg.setTitle("Please select an specific angle");
				dlg.setVisible(true);

				final ArrayList<Integer> v = dlg.getSpecifcAngle();
				if (v.size() == 1) {
					final Integer in = v.get(0);
					final int va = in.intValue();
					final param pm = getParaForSpecificAngle(va);

					final Constraint cs = new Constraint(Constraint.EQANGLE3P,
							ag1, ag2, ag, pm, va);
					addConstraintToList(cs);
					characteristicSetMethodAndAddPoly(false);
					clearSelection();
					UndoAdded(ag1.getDescription() + " + "
							+ ag2.getDescription() + " + "
							+ ag.getDescription() + " = " + ag.getDescription());
				} else {
					clearSelection();
					break;
				}
			} else
				addToSelectList(ag);
		}
		break;
		case SETCCTANGENT: {
			final GECircle c = (GECircle) selectFromList(circlelist, x, y);
			if (c == null)
				break;
			if (SelectList.size() == 1) {
				final GECircle c0 = (GECircle) SelectList.get(0);
				final Constraint cs = new Constraint(Constraint.CCTANGENT, c0,
						c);
				characteristicSetMethodAndAddPoly(false);
				addConstraintToList(cs);
				UndoAdded(c0.getDescription() + " tangent to "
						+ c.getDescription());
			} else
				addToSelectList(c);
		}
		break;
		case D_SQUARE: {
			if (STATUS == 0) {
				GEPoint pt = SmartPoint(CatchPoint);
				if (pt == null) {
					if (SelectList.size() == 0) {
						final GELine line = SmartPLine(CatchPoint);
						if (line != null) {
							addToSelectList(line);
							STATUS = 1;
							break;
						}
					}
					pt = SmartgetApointFromXY(x, y);
				}

				if (SelectList.size() == 1) {
					final GEPoint pa = (GEPoint) SelectList.get(0);
					setSmartPVLine(pa, pt);
				}
				if (!SelectList.contains(pt))
					addToSelectList(pt);
				if (SelectList.size() == 2)
					STATUS = 2;
			} else if (STATUS == 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				addsquare(p1, p2, CatchPoint);
				clearSelection();
				STATUS = 0;

			}
		}
		break;
		case D_CCLINE: {
			if (SelectList.size() == 0) {
				final GECircle c = SmartPCircle(CatchPoint);
				if (c != null)
					selectGraphicEntity(c);
			} else if (SelectList.size() == 1) {
				final GECircle c = SmartPCircle(CatchPoint);
				if (c != null) {
					final GECircle c0 = (GECircle) SelectList.get(0);
					if (c0.o == c.o) {
						clearSelection();
						break;
					}

					final GELine line = new GELine(GELine.CCLine);
					addLineToList(line);

					final Constraint cs = new Constraint(Constraint.CCLine,
							line, c0, c);
					addConstraintToList(cs);
					line.addConstraint(cs);
					clearSelection();
					UndoAdded(line.TypeString() + ":  radical of "
							+ c0.getDescription() + " and "
							+ c.getDescription());
				}
			}

		}
		break;
		case D_IOSTRI: {
			if (SelectList.size() < 2) {
				final GEPoint pt = SmartgetApointFromXY(x, y);
				if (SelectList.size() == 1) {
					final GEPoint pa = (GEPoint) SelectList.get(0);
					setSmartPVLine(pa, pt);
				}
				if (SelectList.size() == 0)
					addToSelectList(pt);
				else if (pt == SelectList.get(0))
					clearSelection();
				else
					addToSelectList(pt);
			} else {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				addisoAngle(p1, p2, CatchPoint, 0);
				clearSelection();
				STATUS = 0;
			}
		}
		break;
		case DRAWTRIALL: {
			if (STATUS == 0) {
				final GEPoint pt = SmartgetApointFromXY(x, y);
				addToSelectList(pt);
				STATUS = 1;
			} else if (STATUS == 1) {
				final GEPoint pt = SmartgetApointFromXY(x, y);
				if (pt == SelectList.get(0)) {
					STATUS = 0;
					clearSelection();
					break;
				}
				if (SelectList.size() == 1) {
					final GEPoint pa = (GEPoint) SelectList.get(0);
					setSmartPVLine(pa, pt);
				}
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				addToSelectList(pt);
				if (findLineGivenTwoPoints(p1, pt) == null) {
					final GELine line = new GELine(p1, pt);
					addLineToList(line);
				}
				STATUS = 2;
			} else {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				GEPoint pt = CreateANewPoint(x, y);

				final Constraint cs = new Constraint(Constraint.PETRIANGLE, pt,
						p1, p2);
				final GEPoint pu = addADecidedPointWithUnite(pt);
				if (pu == null) {
					addConstraintToList(cs);
					addPointToList(pt);
				} else
					pt = pu;
				addALine(pt, p1);
				addALine(pt, p2);
				clearSelection();
				STATUS = 0;
				UndoAdded("equilateral triangle " + pt.m_name + p1.m_name
						+ p2.m_name);
			}
		}
		break;
		case RA_TRAPEZOID: {
			if (STATUS == 0) {
				final GEPoint pt = SmartgetApointFromXY(x, y);

				if (SelectList.size() > 0) {
					final GEPoint pa = (GEPoint) SelectList
							.get(SelectList.size() - 1);
					setSmartPVLine(pa, pt);
				}

				if (!SelectList.contains(pt))
					addToSelectList(pt);
				if (SelectList.size() == 2)
					STATUS = 1;
			} else {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final GEPoint p3 = SmartgetApointFromXY(x, y);
				GEPoint p4 = CreateANewPoint(x, y);
				final Constraint cs = new Constraint(
						Constraint.RIGHT_ANGLE_TRAPEZOID, p1, p2, p3, p4);
				final GEPoint pu = addADecidedPointWithUnite(p4);
				if (pu == null) {
					addALine(p1, p2);
					addALine(p2, p3);
					addALine(p3, p4);
					addALine(p1, p4);
					addPointToList(p4);
					addConstraintToList(cs);
					characteristicSetMethodAndAddPoly(false);
				} else
					p4 = pu;

				UndoAdded("right trapezoid " + p1.m_name + p2.m_name
						+ p3.m_name + p4.m_name);
				STATUS = 0;
				clearSelection();
			}
		}
		break;
		case TRAPEZOID: {
			if (STATUS == 0) {
				final GEPoint pt = SmartgetApointFromXY(x, y);
				if (SelectList.size() > 0) {
					final GEPoint pa = (GEPoint) SelectList
							.get(SelectList.size() - 1);
					setSmartPVLine(pa, pt);
				}
				if (!SelectList.contains(pt))
					addToSelectList(pt);
				else
					break;
				if (SelectList.size() == 3)
					STATUS = 1;
			} else {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final GEPoint p3 = (GEPoint) SelectList.get(2);
				x = CatchPoint.getx();
				y = (((p1.gety() - p2.gety()) * (x - p3.getx())) / (p1.getx() - p3.getx())) + p3.gety();
				// y = (p1.gety() - p2.gety()) * (x - p3.getx()) / (p1.getx() -
				// p2.getx()) + p3.gety();
				GEPoint p4 = SmartgetApointFromXY(x, y);
				final Constraint cs1 = new Constraint(Constraint.TRAPEZOID, p1, p2, p3, p4);
				final GEPoint pu = addADecidedPointWithUnite(p4);
				p4.setXY(x, y);
				if (pu == null) {
					addALine(p1, p2);
					addALine(p2, p3);
					addALine(p3, p4);
					addALine(p1, p4);
					addPointToList(p4);
					addConstraintToList(cs1);
					characteristicSetMethodAndAddPoly(false);
				} else
					p4 = pu;
				reCalculate();
				UndoAdded("trapezoid " + p1.m_name + p2.m_name + p3.m_name
						+ p4.m_name);
				STATUS = 0;
				clearSelection();

			}

		}
		break;
		case PARALLELOGRAM: {
			if (STATUS == 0) {
				final GEPoint pt = SmartgetApointFromXY(x, y);
				addToSelectList(pt);
				STATUS = 1;
			} else if (STATUS == 1) {
				final GEPoint pt = SmartgetApointFromXY(x, y);
				if (pt == SelectList.get(0)) {
					STATUS = 0;
					clearSelection();
					break;
				}
				if (SelectList.size() == 1) {
					final GEPoint pa = (GEPoint) SelectList.get(0);
					setSmartPVLine(pa, pt);
				}
				addToSelectList(pt);
				STATUS = 2;
			} else {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final GEPoint p3 = SmartgetApointFromXY(x, y);
				GEPoint p4 = CreateANewPoint(x, y);
				final Constraint cs = new Constraint(Constraint.PARALLELOGRAM,
						p1, p2, p3, p4);
				final GEPoint pu = addADecidedPointWithUnite(p4);
				if (pu == null) {
					addPointToList(p4);
					addALine(p1, p2);
					addALine(p1, p4);
					addALine(p2, p3);
					addALine(p3, p4);
					addConstraintToList(cs);
					characteristicSetMethodAndAddPoly(false);
				} else
					p4 = pu;
				UndoAdded("parallelogram " + p1.m_name + p2.m_name + p3.m_name
						+ p4.m_name);
				STATUS = 0;
				clearSelection();
			}

		}
		break;
		case RECTANGLE: {
			if (STATUS == 0) {
				final GEPoint pt = SmartgetApointFromXY(x, y);

				addToSelectList(pt);
				STATUS = 1;
			} else if (STATUS == 1) {
				final GEPoint pt = SmartgetApointFromXY(x, y);
				if (pt == SelectList.get(0)) {
					STATUS = 0;
					clearSelection();
					break;
				}
				if (SelectList.size() == 1) {
					final GEPoint pa = (GEPoint) SelectList.get(0);
					setSmartPVLine(pa, pt);
				}
				addToSelectList(pt);
				STATUS = 2;
			} else {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);

				final double x1 = p1.getx();
				final double y1 = p1.gety();
				final double x2 = p2.getx();
				final double y2 = p2.gety();

				final double xc = CatchPoint.getx();
				final double yc = CatchPoint.gety();

				final double dlx = x2 - x1;
				final double dly = y2 - y1;
				final double dl = (dlx * dlx) + (dly * dly);

				final double xx = (((y2 - yc) * dlx * dly) + (dly * dly * xc) + (dlx
						* dlx * x2))
						/ dl;
				final double yy = (((x2 - xc) * dlx * dly) + (dlx * dlx * yc) + (dly
						* dly * y2))
						/ dl;

				final GEPoint p3 = SmartgetApointFromXY(xx, yy);
				final double xt = (x + p1.getx()) - p2.getx();
				final double yt = (y + p1.gety()) - p2.gety();
				GEPoint p4 = CreateANewPoint(xt, yt);
				final Constraint cs1 = new Constraint(Constraint.RECTANGLE, p1,
						p2, p3, p4);
				final GEPoint pu = addADecidedPointWithUnite(p4);
				if (pu == null) {
					addPointToList(p4);
					final GELine tl1 = addALine(p1, p2);
					final GELine tl2 = addALine(p1, p4);
					addALine(p2, p3);
					addALine(p3, p4);
					addCTMark(tl1, tl2);
					addConstraintToList(cs1);
					characteristicSetMethodAndAddPoly(false);
				} else
					p4 = pu;
				UndoAdded("rectangle " + p1.m_name + p2.m_name + p3.m_name
						+ p4.m_name);
				STATUS = 0;
				clearSelection();

			}
		}
		break;
		case DRAWTRISQISO: {
			if (STATUS == 0) {
				final GEPoint pt = SmartgetApointFromXY(x, y);
				addToSelectList(pt);
				STATUS = 1;
			} else if (STATUS == 1) {
				final GEPoint pt = SmartgetApointFromXY(x, y);
				if (pt == SelectList.get(0)) {
					STATUS = 0;
					clearSelection();
					break;
				}
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				addALine(p1, pt);
				addToSelectList(pt);
				STATUS = 2;
			} else {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final GEPoint pt = CreateANewPoint(x, y);
				final GELine ln1 = new GELine(pt, p1);
				final GELine ln2 = new GELine(pt, p2);
				final Constraint cs = new Constraint(Constraint.PERPBISECT, pt, p1, p2);
				final Constraint cs1 = new Constraint(Constraint.PERPENDICULAR, ln1, ln2);
				final GEPoint pu = addADecidedPointWithUnite(pt);
				if (pu == null) {
					addPointToList(pt);
					characteristicSetMethodAndAddPoly(false);
					addConstraintToList(cs1);
					addConstraintToList(cs);
					addLineToList(ln1);
					addLineToList(ln2);
				}

				clearSelection();
				STATUS = 0;
				UndoAdded("isoceles-right triangle " + pt.m_name + p1.m_name + p2.m_name);
			}
		}
		break;

		case DEFINEPOLY: {

			if ((SelectAPoint(x, y) == null) && (SelectList.size() == 0)) {
				final GECircle c = SelectACircle(x, y);
				if (c != null) {
					for (final GEPolygon px : polygonlist)
						if (px.isEqual(c))
							break;
					if (fd_polygon(c) == null) {
						final GEPolygon px = new GEPolygon(c);
						addPolygonToList(px);
						clearSelection();
						UndoAdded(px.getDescription());
					}
					break;
				}
			} else {
				FirstPnt = createTempPoint(x, y);
				p = SmartPoint(FirstPnt);
				if (p != null)
					if (STATUS == 0) {
						final GEPolygon cp = new GEPolygon();
						cp.addAPoint(p);
						addToSelectList(cp);
						STATUS = 1;
					} else {
						final GEPolygon cp = (GEPolygon) SelectList.get(0);
						if (cp.addAPoint(p)) {
							STATUS = 0;
							addPolygonToList(cp);
							clearSelection();
							UndoAdded(cp.getDescription());
						}
					}
			}
		}
		break;
		case D_TEXT: {
			final GEText tc = (GEText) selectFromList(textlist, x, y);
			dialog_addText(tc, (int) x, (int) y);
		}
		break;
		case MULSELECTSOLUTION: {
			for (final GEPoint p1 : solutionlist)
				if ((Math.pow(p1.getx() - x, 2) + Math.pow(p1.gety() - y, 2)) < (18 * 18)) {
					pSolution.setXY(p1.getx(), p1.gety());
					solutionlist.clear();
					pSolution = null;
					SetCurrentAction(PreviousAction);
				}
		}
		break;

		case SETTRACK: {
			CTrackPt = SelectAPoint(x, y);
			boolean r = false;

			for (final GETrace tr : tracelist)
				if (tr.isTracePt(CTrackPt)) {
					r = true;
					break;

				}
			if (!r) {
				final GETrace t = new GETrace(CTrackPt);
				addGraphicEntity(tracelist, t);
				UndoAdded(t.toString());
				if (gxInstance != null)
					gxInstance.setActionMove();
			}
			break;
		}

		case LOCUS: {
			final int n = SelectList.size();
			if (n <= 1) {
				final GEPoint pt = SelectAPoint(x, y);
				if (pt != null) {
					if ((n == 0) && !pt.isAFixedPoint())
						JOptionPane.showMessageDialog(gxInstance,
								"The point should be a fix point", "Warning",
								JOptionPane.WARNING_MESSAGE);
					else
						selectGraphicEntity(pt);
					final int k = SelectList.size();
					if (k == 1)
						gxInstance.setTipText("Please select the second point");
					else if (k == 2)
						gxInstance
						.setTipText("Please select a line or a circle");
				}
			} else {
				final GEPoint pt = (GEPoint) SelectList.get(0);
				final GEPoint pt1 = (GEPoint) SelectList.get(1);
				final GELine ln = SelectALine(x, y);

				if (ln != null) {
					final GETrace t = new GETrace(pt, pt1, ln);
					addGraphicEntity(tracelist, t);
					UndoAdded(t.toString());
				} else {
					final GECircle c = SelectACircle(x, y);
					if (c != null) {
						final GETrace t = new GETrace(pt, pt1, c);
						addGraphicEntity(tracelist, t);
						UndoAdded(t.toString());
					} else {
					}
				}
				clearSelection();
				reCalculate();
			}
		}
		break;
		case INCENTER:
		case BARYCENTER:
		case ORTHOCENTER:
		case CIRCUMCENTER: {
			final GEPoint pt = createTempPoint(x, y);
			final GEPoint tp = SmartPoint(pt);
			if (tp != null)
				selectGraphicEntity(tp);
			if (SelectList.size() == 3) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final GEPoint p3 = (GEPoint) SelectList.get(2);
				final GEPoint pp = CreateANewPoint(x, y);
				Constraint cs = null;
				String s = null;
				if (CurrentAction == BARYCENTER) {
					cs = new Constraint(Constraint.BARYCENTER, pp, p1, p2, p3);
					s = "barycenter";
				} else if (CurrentAction == CIRCUMCENTER) {
					cs = new Constraint(Constraint.CIRCUMCENTER, pp, p1, p2, p3);
					s = " circumcenter";
				} else if (CurrentAction == ORTHOCENTER) {
					cs = new Constraint(Constraint.ORTHOCENTER, pp, p1, p2, p3);
					s = "orthocenter";
				} else if (CurrentAction == INCENTER) {
					cs = new Constraint(Constraint.INCENTER, pp, p1, p2, p3);
					s = "incenter";
					pp.addConstraint(cs);
				} else
					return;

				final GEPoint pu = addADecidedPointWithUnite(pp);
				if (pu == null) {
					addPointToList(pp);
					addConstraintToList(cs);
					UndoAdded(pp.TypeString() + ":  the " + s + " of "
							+ p1.m_name + " " + p2.m_name + " " + p3.m_name);

				} else
					p = pu;
				clearSelection();
			}
		}
		break;

		case NTANGLE: {
			if (STATUS == 0) {
				final GELine ln = (GELine) selectFromList(linelist, x, y);
				if (ln != null)
					addToSelectList(ln);
				if (SelectList.size() == 3) {
					STATUS = 1;
					final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
					v.add(ln);
					flashStep(v);
				}
			} else if (STATUS == 1) {
				final GEPoint pt = SelectAPoint(x, y);
				if (pt != null) {
					final GELine ln = new GELine(GELine.NTALine);
					ln.add(pt);
					addToSelectList(ln);
					final Constraint cs = new Constraint(Constraint.NTANGLE,
							SelectList);
					clearSelection();
					final Constraint cs1 = new Constraint(Constraint.PONLINE,
							pt, ln, false);
					ln.addConstraint(cs);
					addLineToList(ln);
					addConstraintToList(cs1);
					addConstraintToList(cs);
					UndoAdded("eqanle added");
				}
			}

		}
		break;

		case VIEWELEMENT: {
			viewElementFromXY(x, y);
		}
		break;
		case ARROW: {
			final GEPoint pt = SmartgetApointFromXY(x, y);
			if (pt == null)
				break;
			if (SelectList.size() == 0)
				selectGraphicEntity(pt);
			else {
				final GEPoint tp = (GEPoint) SelectList.get(0);
				if (tp == pt)
					break;
				final GEArrow ar = new GEArrow(pt, tp);
				addGraphicEntity(otherlist, ar);
				clearSelection();
				UndoAdded("Arrow " + ar.getDescription());
			}
		}
		case DISTANCE: {

			final GEPoint pt = (GEPoint) selectFromList(pointlist, x, y);
			if (pt == null)
				break;
			if (SelectList.size() == 0)
				selectGraphicEntity(pt);
			else {
				final GEPoint tp = (GEPoint) SelectList.get(0);
				if (tp == pt)
					break;
				final GEDistance dis = new GEDistance(pt, tp);
				addGraphicEntity(distancelist, dis);
				clearSelection();
				UndoAdded("measure " + dis.getDescription());
			}

		}
		break;
		case EQMARK: {
			final GEPoint pt = (GEPoint) selectFromList(pointlist, x, y);
			if (pt == null)
				break;
			if (SelectList.size() == 0)
				selectGraphicEntity(pt);
			else {
				final GEPoint tp = (GEPoint) SelectList.get(0);
				if (tp == pt)
					break;

				final GEEqualDistanceMark ce = new GEEqualDistanceMark(pt, tp, STATUS);
				addGraphicEntity(otherlist, ce);
				clearSelection();
				UndoAdded("mark of " + pt.m_name + tp.m_name);
			}
		}
		break;
		case RAMARK: {
			final GELine ln = (GELine) selectFromList(linelist, x, y);
			if (ln == null)
				break;
			if (SelectList.size() == 0)
				selectGraphicEntity(ln);
			else {
				final GELine ln1 = (GELine) SelectList.get(0);
				if (ln == ln1)
					break;
				addCTMark(ln, ln1);
				// addObjectToList(m, otherlist);
				clearSelection();
				UndoAdded("Right Angle Mark of " + ln.getDescription()
						+ " and " + ln1.getDescription());
			}
		}
		break;
		case HIDEOBJECT: {
			final GraphicEntity cc = SelectOneFromXY(x, y, 0);
			if (cc != null) {
				final Constraint cs = new Constraint(Constraint.INVISIBLE, cc);
				addConstraintToList(cs);
				cc.setVisible(false);
				final UndoStruct un = UndoAdded("Hide " + cc.getDescription());
				if (un != null)
					un.addRelatedObject(cc);
			}
		}
		break;
		case SHOWOBJECT: {
			GraphicEntity cc = null;
			for (final Constraint cs : constraintlist) {
				if (cs.GetConstraintType() != Constraint.INVISIBLE)
					continue;
				final GraphicEntity c1 = (GraphicEntity) cs.getelement(0);
				if (c1.bVisible == false) {
					c1.setVisible(true);
					if (c1.isLocatedNear(x, y)) {
						cc = c1;
						final Constraint cs1 = new Constraint(
								Constraint.VISIBLE, cc);
						addConstraintToList(cs1);
						// UndoStruct un = UndoAdded("Show " +
						// cc.getDescription());
						final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
						v.add(cc);
						setObjectListForFlash(v);
						break;
					} else
						c1.setVisible(false);
				}
			}

		}
		break;

		case SANGLE: {
			final int n = SelectList.size();
			if (n == 0) {
				final GELine line = SmartPLine(CatchPoint);
				if ((line != null) && (line.getPtsSize() >= 2))
					addToSelectList(line);
			} else if (n == 1) {
				p = SelectAPoint(x, y);
				final GELine ln1 = (GELine) SelectList.get(0);
				if ((p != null) && ln1.pointOnLine(p))
					addToSelectList(p);

			} else if (n == 2) {
				final GELine ln1 = (GELine) SelectList.get(0);
				p = (GEPoint) SelectList.get(1);

				final double k = ln1.getSlope();
				final double k1 = Constraint.getSpecifiedAnglesMagnitude(STATUS);
				final double kx1 = (k + k1) / (1 - (k * k1));
				final double kx2 = (k - k1) / (1 + (k * k1));

				final double r1 = GELine.distanceToPoint(p.getx(), p.gety(),
						kx1, x, y);
				final double r2 = GELine.distanceToPoint(p.getx(), p.gety(),
						kx2, x, y);

				Integer I = null;
				int id = 0;

				if (r1 <= r2) {
					I = new Integer(-STATUS);
					id = add_sp_angle_value(-STATUS);
				} else {
					I = new Integer(STATUS);
					id = add_sp_angle_value(STATUS);
				}
				final GELine ln = new GELine(GELine.SLine);
				ln.add(p);
				final Constraint cs = new Constraint(Constraint.SANGLE, ln1,
						ln, I);
				cs.proportion = id;

				ln.addConstraint(cs);
				addConstraintToList(cs);
				addLineToList(ln);
				UndoAdded(ln.getDescription());
				clearSelection();
			}
		}
		break;
		case D_BLINE: {
			addSelectPoint(x, y);
			if (SelectList.size() == 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				if (p1 != p2) {
					final GELine ln = new GELine(GELine.BLine);
					final Constraint cs = new Constraint(Constraint.BLINE, ln,
							p1, p2);
					ln.addConstraint(cs);
					addLineToList(ln);
					addConstraintToList(cs);
					clearSelection();
					UndoAdded("BLine " + ln.getDescription());
				}
			}
		}
		break;
		case D_TCLINE: {
			CatchPoint.setXY(x, y);

			if (SelectList.size() == 0) {
				final GECircle c = SmartPCircle(CatchPoint);
				if (c != null)
					addToSelectList(c);
			} else {
				final GECircle c = (GECircle) SelectList.get(0);
				if (c.on_circle(x, y)) {
					final GEPoint p1 = SmartgetApointFromXY(x, y);
					final GELine ln = new GELine(GELine.TCLine, p1);
					final Constraint cs = new Constraint(Constraint.TCLINE, c,
							ln, p1);
					addConstraintToList(cs);
					ln.addConstraint(cs);
					addLineToList(ln);
				}
			}
		}
		break;
		case CCTANGENT: {
			final GECircle c = SelectACircle(x, y);
			if (c != null) {
				final int n = SelectList.size();
				if (n == 1) {
					final GECircle c1 = (GECircle) SelectList.get(0);
					if ((c != c1) && (c.o != c1.o)) {
						final GEPoint p1 = CreateANewPoint(0, 0);
						final GEPoint p2 = CreateANewPoint(x, y);
						c1.add(p1);
						c.add(p2);
						final Constraint cs = new Constraint(
								Constraint.CCTANGENT_LINE, p1, p2, c1, c);
						addPointToList(p1);
						addPointToList(p2);
						addConstraintToList(cs);
						characteristicSetMethodAndAddPoly(false);
						UndoAdded("TANGENT LINE");
					}
				} else
					selectGraphicEntity(c);
			}
		}
		break;
		case RATIO: {
			final int n = SelectList.size();
			p = SelectAPoint(x, y);
			if (p != null) {
				if (((n % 2) != 0) && (p == SelectList.get(n - 1)))
					break;
				addToSelectList(p);
				setObjectListForFlash(p);
			}
			if (SelectList.size() == 8) {
				final Constraint cs = new Constraint(Constraint.RATIO,
						SelectList);
				addConstraintToList(cs);
				characteristicSetMethodAndAddPoly(false);
				UndoAdded("RATIO");
				clearSelection();
			}
		}
		break;
		case EQUIVALENCE: {
			if (STATUS == 0) {
				final GEPolygon g = (GEPolygon) selectFromList(polygonlist, x, y);
				if (g != null) {
					selectGraphicEntity(g);
					STATUS = 1;
				}
			} else if (STATUS == 1) {
				final GEPoint pt = SelectAPoint(x, y);
				if (pt != null) {
					addToSelectList(pt);
					STATUS = 2;
				} else {
					final GEPolygon g = (GEPolygon) SelectList.get(0);
					final int n = g.getPtn();

					for (int i = 0; i < (n - 1); i++) {
						final GEPoint p1 = g.getPoint(i);
						final GEPoint p2 = g.getPoint(i + 1);
						if (GELine.mouse_on_line(x, y, p1.getx(), p1.gety(),
								p2.getx(), p2.gety())) {
							addToSelectList(p1);
							addToSelectList(p2);
							vx1 = x;
							vy1 = y;
							STATUS = 3;
							break;
						}
					}
				}
				if (STATUS == 1) {
					STATUS = 0;
					clearSelection();
				}

			} else if (STATUS == 2) {
				final GEPolygon g = (GEPolygon) SelectList.get(0);
				final GEPoint p1 = (GEPoint) SelectList.get(1);
				final GEPoint t1 = g.getPreviousPoint(p1);
				final GEPoint t2 = g.getNextPoint(p1);
				final double[] r = getPTInterSection(x, y, p1.getx(),
						p1.gety(), t1.getx(), t1.gety(), t2.getx(), t2.gety());
				final GEPoint pt = SelectAPoint(r[0], r[1]);

				if ((pt != null) && (pt != t1)) {
					final GEPolygon poly = new GEPolygon();
					poly.copy(g);
					final int t = g.getPtn();

					for (int i = 0; i < t; i++) {
						GEPoint m = g.getPoint(i);
						if (m == p1)
							m = pt;
						poly.addAPoint(m);
					}
					if (findPolygon(poly.points) != g) {
						g.setVisible(false);
						final Constraint cs = new Constraint(Constraint.EQUIVALENCE1, g, poly);
						addConstraintToList(cs);
						addGraphicEntity(polygonlist, poly);
						UndoAdded("Area-Preserving");// + g.getDescription() +
						// " transformed to " +
						// poly.getDescription());
					}
				}
				STATUS = 0;
				clearSelection();
				g.setDraggedPoints(null, null, 0, 0);

			} else if (STATUS == 3) {
				final GEPolygon g = (GEPolygon) SelectList.get(0);
				final GEPoint t1 = (GEPoint) SelectList.get(1);
				final GEPoint t2 = (GEPoint) SelectList.get(2);
				final double dx = x - vx1;
				final double dy = y - vy1;

				final GEPoint pt1 = SelectAPoint(t1.getx() + dx, t1.gety() + dy);
				final GEPoint pt2 = SelectAPoint(t2.getx() + dx, t2.gety() + dy);
				if ((pt1 != null) && (pt2 != null)
						&& ((pt1 != t1) || (pt2 != t2))) {
					final GEPolygon poly = new GEPolygon();
					poly.copy(g);
					final int t = g.getPtn();

					for (int i = 0; i < t; i++) {
						GEPoint m = g.getPoint(i);
						if (m == t1)
							m = pt1;
						else if (m == t2)
							m = pt2;

						poly.addAPoint(m);
					}
					if (findPolygon(poly.points) != g) {
						g.setVisible(false);
						final Constraint cs = new Constraint(
								Constraint.EQUIVALENCE2, g, poly);
						addConstraintToList(cs);
						addGraphicEntity(polygonlist, poly);
						UndoAdded("Area-Preserving");// g.getDescription() +
						// " transformed to " +
						// poly.getDescription());
					}
				}

				STATUS = 0;
				clearSelection();
				g.setDraggedPoints(null, null, 0, 0);
			}
		}
		break;
		case FREE_TRANSFORM: {
			if (STATUS == 0) {
				final GEPolygon g = SelectAPolygon(x, y);// SelectFromAList(polygonlist,
				// x, y);
				if (g != null) {
					selectGraphicEntity(g);
					STATUS = 1;
				}
			} else {
				final GEPoint pt = SelectAPoint(x, y);
				final GEPolygon poly = (GEPolygon) SelectList.get(0);

				if (pt == null) {
					STATUS = 0;
					clearSelection();
					poly.setDraggedPointsNull();
				} else if (SelectList.size() == 1) {
					final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
					poly.getDraggedPoints(v);
					boolean already = false;
					for (int i = 0; i < (v.size() / 2); i++)
						if (v.get(i * 2) == pt) {
							already = true;
							break;
						}
					if (!already)
						addToSelectList(pt);
				} else {
					final GEPoint t1 = (GEPoint) SelectList.get(1);
					poly.addDraggedPoints(t1, pt);
					SelectList.remove(t1);
					if (poly.allDragged())
						add_free_transform();
				}

			}

		}
		break;
		case TRANSFORM: {
			if (STATUS == 0) {
				final GEPolygon g = SelectAPolygon(x, y); // SelectFromAList(polygonlist,
				// x, y);
				if (g != null) {
					selectGraphicEntity(g);
					catchX = x;
					catchY = y;
					STATUS = 1;
					FirstPnt = SecondPnt = ThirdPnt = null;
				}
			} else if ((STATUS == 1) || (STATUS == 2))
				if ((STATUS == 2) && ((FirstPnt == null) || (ThirdPnt == null))) {
					final GEPoint pt = SelectAPoint(x - vx1, y - vy1);
					if (pt != null) {
						x = pt.getx() + vx1;
						y = pt.gety() + vy1;
					}
					if (FirstPnt == null) {
						FirstPnt = createTempPoint(x, y);
						SecondPnt = pt;
						catchX = x - vx1;
						catchY = y - vy1;
					} else
						ThirdPnt = createTempPoint(x - vx1, y - vy1);
				} else {
					final GEPolygon poly = (GEPolygon) SelectList.get(0);
					clearSelection();
					STATUS = 0;

					final int n = poly.getPtn();
					final double cx = catchX + vx1;
					final double cy = catchY + vy1;
					final double sin = Math.sin(vangle);
					final double cos = Math.cos(vangle);

					if (Math.abs(vangle) < UtilityMiscellaneous.ZERO)
						PolygonTransPointsCreated(poly);

					for (int i = 0; i < n; i++) {
						final GEPoint t = poly.getPoint(i);
						double tx = (t.getx() + vx1);
						double ty = (t.gety() + vy1);

						tx -= cx;
						ty -= cy;
						final double mx = ((tx) * cos) - ((ty) * sin);
						final double my = ((tx) * sin) + ((ty) * cos);
						tx = mx + cx;
						ty = my + cy;
						final GEPoint t1 = SelectAPoint(tx, ty);
						if (t1 == null) {
							clearSelection();
							break;
						}
						addToSelectList(t1);
					}
					if (!SelectList.isEmpty()) {
						final GEPolygon poly1 = new GEPolygon();
						poly1.setPoints(SelectList);
						if (findPolygon(SelectList) != poly) {
							final Constraint cs = new Constraint(
									Constraint.TRANSFORM, poly, poly1,
									SecondPnt);

							int r = -1;

							if (UtilityMiscellaneous.TransComfirmed) {
								final String s1 = poly.getDescription()
										+ " is transformed to "
										+ poly1.getDescription();
								final String s2 = "Do you want to keep the original polygon visible?";
								final DialogTransformConfirm dlg = new DialogTransformConfirm(
										gxInstance.getFrame(), s1, s2);
								gxInstance.centerDialog(dlg);
								dlg.setVisible(true);
								r = dlg.getResult();
							} else
								r = 1;

							if (r == 0)
								cs.proportion = 0;
							else if (r == 1) {
								poly.setVisible(false);
								cs.proportion = 1;
							} else {
							}
							if (r != 2) {
								addGraphicEntity(polygonlist, poly1);
								poly1.copy(poly);
								addConstraintToList(cs);
								// String s = "Isometry Transforming";
								// if (Math.abs(vangle) < CMisc.ZERO)
								// s = "Transforming";
								// else if (SecondPnt != null)
								// s = "Rotating";

								final String s = poly.getDescription() + " = "
										+ poly1.getDescription();
								UndoAdded(s);// );
							}
						}

					}
					STATUS = 0;
					clearSelection();
					vtrx = vtry = vx1 = vy1 = vangle = 0.0;
					FirstPnt = SecondPnt = ThirdPnt = null;
				}
		}
		break;

		}

	}

	public void add_free_transform() {
		final GEPolygon p = (GEPolygon) SelectList.get(0);
		final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
		p.getTransformedPoints(v);
		final GEPolygon p1 = new GEPolygon();
		p1.copy(p);
		p1.setPoints(v);
		final Constraint cs = new Constraint(Constraint.TRANSFORM1, p, p1, null);
		p.setVisible(false);
		addConstraintToList(cs);
		addGraphicEntity(polygonlist, p1);
		clearSelection();
		STATUS = 0;
		p.setDraggedPointsNull();
		UndoAdded(p.getDescription() + " transformed to " + p1.getDescription());
	}

	// TODO: Rename meetTwoObject to clarify what it does
	public GEPoint meetTwoObject(final GraphicEntity obj1, final GraphicEntity obj2,
			final boolean d, final double x, final double y) {
		if ((obj1 instanceof GELine) && (obj2 instanceof GELine))
			return createIntersectionPoint((GELine) obj1, (GELine) obj2);
		else if ((obj1 instanceof GECircle) && (obj2 instanceof GECircle))
			return MeetCCToDefineAPoint((GECircle) obj1, (GECircle) obj2, d, x, y);
		else if ((obj1 instanceof GELine) && (obj2 instanceof GECircle))
			return MeetLCToDefineAPoint((GELine) obj1, (GECircle) obj2, d, x, y);
		else if ((obj1 instanceof GECircle) && (obj2 instanceof GELine))
			return MeetLCToDefineAPoint((GELine) obj2, (GECircle) obj1, d, x, y);
		return null;
	}

	// public void addToSelectList(Object p) {
	// if (p != null && !SelectList.contains(p))
	// addToSelectList(p);
	// }

	public void addSelectPoint(final double x, final double y) {
		final GEPoint p = SelectAPoint(x, y);
		if (p != null && !SelectList.contains(p))
			addToSelectList(p);
	}

	public GELine addALine(final GEPoint p1, final GEPoint p2) {
		final GELine ln1 = findLineGivenTwoPoints(p1, p2);
		if (ln1 != null)
			return ln1;
		final GELine ln = new GELine(p1, p2);
		addLineToList(ln);
		return ln;
	}

	/*
	 * public Vector printStep(String cc) {
	 * CMisc.print("***************************");
	 * 
	 * Vector v = getConstructionFromDraw(); v.add(cc);
	 * 
	 * for (int i = 0; i < v.size(); i++) { String st = (String) v.get(i);
	 * CMisc.print(st); } return v; }
	 */

	public ArrayList<cons> getConstructionFromDraw() {

		final ArrayList<cons> alist = new ArrayList<cons>();
		for (final Constraint cs : constraintlist) {
			if (cs.csd != null)
				alist.add(cs.csd);
			if (cs.csd1 != null)
				alist.add(cs.csd1);
		}
		final cons st = new cons(gib.C_POINT, pointlist.size());
		for (int i = 0; i < pointlist.size(); i++)
			st.add_pt(pointlist.get(i));
		alist.add(0, st);
		return alist;
	}

	public GEPolygon findPolygon(final Collection<? extends GraphicEntity> v) {
		for (final GEPolygon p : polygonlist)
			if (p.check_eq(v))
				return p;
		return null;
	}

	public GEPolygon findPolygon1(final ArrayList<? extends GraphicEntity> v) {
		for (final GEPolygon p : polygonlist)
			if (p.check_rdeq(v))
				return p;
		return null;
	}

	public boolean canAutoAnimate() {
		if (animate != null)
			return true;
		return false;
	}

	public boolean autoAnimate() {
		if (canAutoAnimate()) {
			final ToolBarAnimate af = gxInstance.getAnimateDialog();

			if (af.isRunning()) {
				af.stopA();
				gxInstance.toggleButton(false);
				return false;
			} else {
				af.setAttribute(animate);
				af.startA();
				gxInstance.toggleButton(true);
				return true;
			}
		} else
			gxInstance.toggleButton(false);
		return false;
	}

	public void autoShowstep() {
		autoUndoRedo();
	}

	public void autoUndoRedo() {
		if (timer_type == 1) {
			timer.stop();
			redo();
			timer_type = 0;

		} else if (timer_type == 0) {
			if ((undolist.size() == 0) && (redolist.size() == 0))
				return;

			if (timer != null)
				timer.stop();
			timer = new Timer(700, this);
			timer.setInitialDelay(700 * 2);
			timer.start();
			timer_type = 1;
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final Object obj = e.getSource();

		if (obj == timer)
			if (timer_type == 1) {
				if (redolist.size() == 0) {
					if (timer.getDelay() == 1400) {
						timer.setDelay(1200);
						return;
					}
					timer.setDelay(1200);
					Undo();
					setUndoStructForDisPlay(null, false);
				} else if (isFlashFinished()) {
					final UndoStruct undo = redolist.get(redolist.size() - 1);
					redo_step();
					setUndoStructForDisPlay(undo, false);
				}
			} else if (timer_type == 2) {
				if (cpfield == null)
					return;
				if (!nextProveStep())
					proveStop();
			} else if (timer_type == 3) {
			}
		panel.repaint();
	}

	public void updateFlashDelay() {
		for (final Flash f : flashlist)
			f.updateTimer();
	}

	public void setTimerDelay(final int delay) {
		if (timer == null)
			return;
		timer.setDelay(delay);
	}

	public void viewElement(final GraphicEntity ge) {
		if (ge == null)
			return;

		if (gxInstance != null) {
			gxInstance.getDialogProperty().setVisible(true);
			gxInstance.cp.SetPanelType(ge);
		}
	}

	public void animationStart() {
		animate.startAnimate();
	}

	public void animationStop() {
		if (animate != null)
			animate.stopAnimate();
		reCalculate();
	}

	public void animationOntime() {
		animate.onTimer();
		reCalculate();
	}

	public static GraphicEntity CatchList(final ArrayList<? extends GraphicEntity> v, final double x, final double y) {
		for (final GraphicEntity ge : v)
			if (ge.isLocatedNear(x, y))
				return ge;
		return null;
	}

	public GEAngle CatchAngle(final double x, final double y) {
		for (final GEAngle ag : anglelist) {
			if (ag.isLocatedNear(x, y))
				return ag;
		}
		return null;
	}

	public void dialog_addText(final GEText tc, final int x, final int y) {
		final DialogTextFrame tf = new DialogTextFrame(gxInstance, x, y);
		tf.setText(tc);
		gxInstance.centerDialog(tf);
		tf.setVisible(true);
	}

	public GEPoint SmartgetApointFromXY(final double x, final double y) {
		final GEPoint pt = SmartAddPoint(x, y);
		return pt;
	}

	public void addPointToList(final GEPoint p) {
		assert(p != null);
		if (p != null) {
			while (!p.hasNameSet()) {
				final String s = getPointNameByCount(pnameCounter);
				if (findPoint(s) == null) {
					p.m_name = s;
				}
				pnameCounter++;
			}
			p.setColorDefault();

			if (addGraphicEntity(pointlist, p) && 
					addGraphicEntity(textlist, p.getNametag())) {
				if (pointlist.size() == 2)
					optimizePolynomial();
				reCalculate();
			}
		}
	}

	public void addPointsToList(final Collection<GEPoint> listPoints) {
		assert(listPoints != null);
		if (listPoints != null) {
			for (GEPoint p : listPoints) {
				if (p != null && !pointlist.contains(p)) {

					while (!p.hasNameSet()) {
						final String s = getPointNameByCount(pnameCounter);
						if (findPoint(s) == null) {
							p.m_name = s;
						}
						pnameCounter++;
					}
					p.setColorDefault();

					if (addGraphicEntity(pointlist, p))
						addGraphicEntity(textlist, p.getNametag());				
				}
			}
			if (pointlist.size() == 2)
				optimizePolynomial();
			reCalculate();
		}
	}

	public static String getPointNameByCount(final int n) {
		final int in = (n) / 26;
		final int number = n - (in * 26);
		String s = "";
		if (in == 0) {
			final char[] c = new char[1];
			c[0] = (char) (number + 'A');
			s = new String(c);
		} else {
			final char[] c = new char[2];
			c[0] = (char) (number + 'A');
			c[1] = (char) ('0' + in);
			s = new String(c);
		}
		return s;
	}

	public void addAngleToList(final GEAngle ag) {
		if (addGraphicEntity(anglelist, ag))
			addGraphicEntity(textlist, ag.getText());
	}

	public void addLineToList(final GELine line) {
		if (line != null) {
			line.m_name = "l" + String.valueOf(linelist.size() + 1);
			addGraphicEntity(linelist, line);
		}
	}

	public void addPolygonToList(final GEPolygon p) {
		assert(p != null);
		p.m_name = "polygon" + String.valueOf(polygonlist.size() + 1);
		addGraphicEntity(polygonlist, p);
	}

	public void addCircleToList(final GECircle c) {
		c.m_name = "c" + String.valueOf(circlelist.size() + 1);
		addGraphicEntity(circlelist, c);
	}

	public void drawLineAndAdd(final GEPoint p1, final GEPoint p2) {
		if ((p1 == null) || (p2 == null) || (p1 == p2))
			return;

		if (findLineGivenTwoPoints(p1, p2) == null) {
			final GELine ln = new GELine(p1, p2);
			addLineToList(ln);
		}
	}


	public void addConstraintToList(final Constraint cs) {
		if (cs != null && !constraintlist.contains(cs))
			constraintlist.add(cs);
	}

	public void removeConstraintFromList(final Constraint cs) {
		constraintlist.remove(cs);
	}

	public void clearAllConstraints() {
		constraintlist.clear();
	}

	private boolean doesLineBetweenTwoPointsExist(final GEPoint p1, final GEPoint p2) {
		for (final GELine ln : linelist)
			if (ln.isCoincidentWith(p1) && ln.isCoincidentWith(p2))
				return true;
		return false;
	}

	private boolean doesCircleWithThreePointsExist(final GEPoint p1, final GEPoint p2, final GEPoint p3) {
		for (final GECircle c : circlelist)
			if (c.isCoincidentWith(p1) && c.isCoincidentWith(p2) && c.isCoincidentWith(p3))
				return true;
		return false;
	}

	private void selectByRect(final double x1, final double y1, final double x2, final double y2) {
		for (final GEPoint p : pointlist) {
			final double x = p.getx();
			final double y = p.gety();
			if ((((x - x1) * (x - x2)) < 0) && (((y - y1) * (y - y2)) < 0))
				addToSelectList(p);
		}

		for (final GELine ge : linelist)
			if (ge.isFullyCoincidentWith(SelectList))
				addToSelectList(ge);

		for (final GECircle ge : circlelist)
			if (ge.isFullyCoincidentWith(SelectList))
				addToSelectList(ge);

		for (final GEText t : textlist)
			if (t.getType() != GEText.NAME_TEXT && t.inRect(x1, y1, x2, y2))
				addToSelectList(t);
	}

	public void DWButtonUp(double x, double y) {
		bLeftButtonDown = false;

		if (SNAP && (CurrentAction != SELECT)) {
			final double[] r = getSnap(x, y);
			x = r[0];
			y = r[1];
		}
		CatchPoint.setXY(x, y);

		switch (CurrentAction) {
		case SELECT: {
			vx1 = x;
			vy1 = y;
		}
		break;
		case MOVE:
			break;
		case D_POINT:
			clearSelection();
			break;
		case H_LINE: {

			GEPoint p = null;
			if (STATUS == 2) {
				STATUS = 0;
				break;
			}
			final GEPoint pt = createTempPoint(x, y);
			p = SmartPoint(pt);
			if (p == FirstPnt) {
				STATUS = 0;
				break;
			}
			if (p == null)
				p = SmartgetApointFromXY(x, y);
			final Constraint cs = new Constraint(Constraint.HORIZONAL, FirstPnt, p);
			characteristicSetMethodAndAddPoly(false);
			addPointToList(p);
			addConstraintToList(cs);
			final GELine ln = new GELine(FirstPnt, p);
			addLineToList(ln);
			UndoAdded(ln.getDescription() + " is a horizonal line");
			FirstPnt = null;
			STATUS = 0;
			break;
		}
		case V_LINE: {
			GEPoint p = null;
			if (STATUS == 2) {
				STATUS = 0;
				break;
			}
			final GEPoint pt = createTempPoint(x, y);
			p = SmartPoint(pt);
			if (p == FirstPnt) {
				STATUS = 0;
				break;
			}
			if (p == null)
				p = SmartgetApointFromXY(x, y);
			final Constraint cs = new Constraint(Constraint.VERTICAL, FirstPnt, p);
			characteristicSetMethodAndAddPoly(false);
			addPointToList(p);
			addConstraintToList(cs);
			final GELine ln = new GELine(FirstPnt, p);
			addLineToList(ln);
			UndoAdded(ln.getDescription() + " is a vertical line");
			FirstPnt = null;
			STATUS = 0;
			break;
		}
		case D_LINE:
			break;
		case D_PARELINE:
			break;
		case D_PERPLINE:
			break;
		case PERPWITHFOOT: {

			if (STATUS == 1) {
				final GEPoint p1 = FirstPnt;
				final GEPoint pt = SmartPoint(CatchPoint);
				if (pt == p1)
					break;

				final GELine line = SmartPLine(CatchPoint);
				if (line == null)
					break;
				final GEPoint p = CreateANewPoint(0, 0);
				add_PFOOT(line, p1, p);
				FirstPnt = null;
				STATUS = 0;
			}
		}
		break;
		case D_PFOOT:
			break;
		case D_CIRCLE:
			break;
		case D_CIRCLEBYRADIUS:
			break;
		case D_PRATIO: {
		}
		break;
		case D_TRATIO: {

		}
		break;

		case D_MIDPOINT:
			break;
		case D_3PCIRCLE:
			break;
		case TRANSLATE:
			break;
		case D_SQUARE: {
			if (STATUS == 1) {
				final GELine line = (GELine) SelectList.get(0);
				final GEPoint[] pl = line.getTwoPointsOfLine();
				if (pl == null)
					break;
				addsquare(pl[0], pl[1], CatchPoint);
				clearSelection();
				STATUS = 0;
			}
		}
		break;
		case D_IOSTRI:
			break;
		}
		bLeftButtonDown = false;
	}

	public void smartPVDragLine() {
		if (SelectList.size() != 1)
			return;
		final GraphicEntity ge = SelectList.get(0);
		if (ge instanceof GEPoint) {
			final GEPoint pt = (GEPoint) ge;
			if (!pt.isAFixedPoint()) {
				for (final GELine ln : linelist) {
					if (!ln.containsPoints(pt))
						continue;
					final GEPoint pt2 = ln.getPointOtherThan(pt);
					if ((pt2 != null) && pt2.isAFreePoint()) {
						final double r1 = Math.abs(pt.getx() - pt2.getx());
						final double r2 = Math.abs(pt.gety() - pt2.gety());

						if (pt.isAFreePoint()) {
							if ((r1 < UtilityMiscellaneous.PIXEPS) && (r2 < UtilityMiscellaneous.PIXEPS))
								break;
							else if (r1 < UtilityMiscellaneous.PIXEPS) {
								pt.setXY(pt2.getx(), pt.gety());
								break;
							} else if (r2 < UtilityMiscellaneous.PIXEPS) {
								pt.setXY(pt.getx(), pt2.gety());
								break;
							}
						} else {
							// if (r1 < CMisc.PIXEPS && r2 < CMisc.PIXEPS) {
							// break;
							// } else if (r1 < CMisc.PIXEPS) {
							// pt.setXY(pt2.getx(), pt.gety());
							// break;
							// }
						}
					}
				}
			}
		}
	}

	public void DWMouseDrag(double x, double y) {

		if (SNAP && CurrentAction != SELECT) {
			final double[] r = getSnap(x, y);
			x = r[0];
			y = r[1];
		}
		isPointOnObject = false;
		CatchPoint.setXY(x, y);

		switch (CurrentAction) {
		case SELECT: { // TODO: Change the behavior to match most programs where the move and select is the same setting.
			if (Math.abs(vx1 - x) > 15 || Math.abs(vy1 - y) > 15) {
				clearSelection();
				selectByRect(vx1, vy1, x, y);
			}
		}
		break;
		case MOVE: {
			if (FirstPnt == null)
				break;

			ObjectLocationChanged(SelectList, FirstPnt, x, y);
			FirstPnt.setXY(x, y);
			smartPVDragLine();
			if (isRecal)
				reCalculate();
		}
		break;

		case D_POINT: {
			if (bLeftButtonDown && !SelectList.isEmpty()) {
				assert(SelectList.size() == 1);
				final GEPoint p = (GEPoint) SelectList.get(0);
				p.setXY(x, y);
				reCalculate();
			}
		}
		break;
		case H_LINE:
			SecondPnt.setXY(x, FirstPnt.gety());
			break;
		case V_LINE:
			SecondPnt.setXY(FirstPnt.getx(), y);
			break;
		case D_LINE:
			if (FirstPnt != null) {
				isPointOnObject = Smart(CatchPoint, x, y);
				if (!isPointOnObject) {
					// isSmartPoint = SmartLineType(FirstPnt.getx(), FirstPnt.gety(), CatchPoint);
				} else {
					// isSmartPoint = 0;
				}
			}
			break;
		case D_PARELINE:
		case D_PERPLINE:
		case D_ALINE:
		case D_CIRCLE:
		case PERPWITHFOOT:
			isPointOnObject = Smart(CatchPoint, x, y);
			break;
		case TRANSLATE: {
			final double dx = x - FirstPnt.getx();
			final double dy = y - FirstPnt.gety();
			FirstPnt.setXY(x, y);
			translate(dx, dy);
		}
		break;
		case D_CIRCLEBYRADIUS: {
			if (STATUS == 1)
				SecondPnt.setXY(x, y);
		}
		break;
		case D_PRATIO:
		case D_TRATIO:
		case D_3PCIRCLE:
		case D_SQUARE:
		case D_IOSTRI:
			break;

		}
	}

	/*
	 * private static int SmartLineType(double x1, double y1, CPoint p2) {
	 * double x2 = p2.getx(); double y2 = p2.gety();
	 * 
	 * if (Math.abs(x2 - x1) < CMisc.PIXEPS && Math.pow(x2 - x1, 2) +
	 * Math.pow(y2 - y1, 2) > 4 * CMisc.PIXEPS * CMisc.PIXEPS) { return 1;
	 * //vertical } else if (Math.abs(y2 - y1) < CMisc.PIXEPS && Math.pow(x2 -
	 * x1, 2) + Math.pow(y2 - y1, 2) > 4 * CMisc.PIXEPS * CMisc.PIXEPS) { return
	 * 2; //horizonal } else { return 0; }
	 * 
	 * }
	 * 
	 * private static int setSmartPVPointLocation(double x1, double y1, CPoint
	 * p2) { double x2 = p2.getx(); double y2 = p2.gety();
	 * 
	 * if (Math.abs(x2 - x1) < CMisc.PIXEPS && Math.pow(x2 - x1, 2) +
	 * Math.pow(y2 - y1, 2) > 4 * CMisc.PIXEPS * CMisc.PIXEPS) { p2.setXY(x1,
	 * y2); return 1; //vertical } else if (Math.abs(y2 - y1) < CMisc.PIXEPS &&
	 * Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) > 4 * CMisc.PIXEPS *
	 * CMisc.PIXEPS) { p2.setXY(x2, y1); return 2; //horizonal } else { return
	 * 0; }
	 * 
	 * }
	 */

	public void add_PFOOT(final GELine line, final GEPoint p1, GEPoint p) {
		final GELine line1 = new GELine(p1, p);
		// constraint c1 = new constraint(constraint.PONLINE, p, line, false);
		// constraint c2 = new constraint(constraint.PONLINE, p, line1, false);
		final GEPoint[] pl = line.getTwoPointsOfLine();
		Constraint cs = null;
		if (pl != null) {
			cs = new Constraint(Constraint.PFOOT, p, p1, pl[0], pl[1]);
			final GEPoint pu = addADecidedPointWithUnite(p);
			if (pu == null) {
				addPointToList(p);
				addLineToList(line1);
				// addConstraintToList(c1);
				// addConstraintToList(c2);
				addConstraintToList(cs);
				addPointToLineX(p, line);
				// line.addApoint(p);
				addCTMark(line, line1);
				// addObjectToList(m, otherlist);
				line1.addConstraint(cs);
				UndoAdded(line1.getSimpleName() + " perp "
						+ line.getSimpleName() + " with foot " + p.m_name);

			} else
				p = pu;
		} else {
			addPointToLine(p, line, false);
			cs = new Constraint(Constraint.PERPENDICULAR, line, line1);
			final GEPoint pu = addADecidedPointWithUnite(p);
			if (pu == null) {
				addPointToList(p);
				addLineToList(line1);
				// addConstraintToList(c1);
				// addConstraintToList(c2);
				addCTMark(line, line1);
				// addObjectToList(m, otherlist);
				addPointToLineX(p, line);
				addConstraintToList(cs);
				line1.addConstraint(cs);
				UndoAdded(line1.getSimpleName() + " perp "
						+ line.getSimpleName() + " with foot " + p.m_name);
			}
		}

	}

	private void translate(final double dx, final double dy) {
		if (!isFrozen()) {
			for (final GEPoint p : pointlist)
				p.move(dx, dy);

			for (final GEText t : textlist)
				t.move(dx, dy);

			reCalculate();
		}
	}

	private void ObjectLocationChanged(final ArrayList<GraphicEntity> list, final GEPoint old, final double x, final double y) {
		final double x0 = FirstPnt.getx();
		final double y0 = FirstPnt.gety();
		final double dx = x - x0;
		final double dy = y - y0;
		final int n = list.size();
		if (n == 0)
			return;

		if (cpfield != null && list.isEmpty())
			cpfield.drag(dx, dy);

		if (n == 1) {
			final GraphicEntity c = list.get(0);
			final int t = c.get_type();
			switch (t) {
			case GraphicEntity.POINT:
				final GEPoint p = (GEPoint) c;
				if (!p.isFrozen())
					p.setXY(x, y);
				return;
			case GraphicEntity.LINE:
				final GELine ln = (GELine) c;
				if (ln.areBothEndpointsFree())
					movePoints(ln.points, dx, dy);
				return;
			case GraphicEntity.CIRCLE:
				circleLocationChanged((GECircle) c, dx, dy);
				return;
			case GraphicEntity.ANGLE:
				final GEAngle ag = (GEAngle) c;
				ag.move(old.getx(), old.gety());
				return;
			case GraphicEntity.TEXT:
				final GEText ct = (GEText) c;
				ct.drag(x0, y0, dx, dy);
				return;
			case GraphicEntity.TMARK:
				final GETMark m = (GETMark) c;
				m.move(x0, y0);
				return;
			case GraphicEntity.DISTANCE:
				final GEDistance dis = (GEDistance) c;
				dis.drag(x, y);
				return;
			case GraphicEntity.POLYGON: {
				final GEPolygon cp = (GEPolygon) c;
				if (cp.ftype == 1) {
					final GECircle cx = fd_circleOR((GEPoint) cp.getElement(0),
							(GEPoint) cp.getElement(1),
							(GEPoint) cp.getElement(2));
					circleLocationChanged(cx, dx, dy);
				} else {
					final GEPoint p1 = (GEPoint) cp.getElement(0);
					final double xx = p1.getx();
					final double yy = p1.gety();
					if (cp.areAllPointsFree()) {
						movePoints(cp.points, dx, dy);
						p1.setXY(xx + dx, yy + dy);
					}
				}
			}
			}
			return;
		}

		if (!isFrozen()) {
			movePoints(pointlist, dx, dy);
		}
	}

	private static void circleLocationChanged(final GECircle c, final double dx, final double dy) {
		final GEPoint o = c.center();
		if (!o.isFrozen())
			o.move(dx, dy);
		movePoints(c.points, dx, dy);
	}

	private static void movePoints(final ArrayList<GEPoint> list, final double dx, final double dy) {
		for (final GEPoint ge : list)
			ge.move(dx, dy);
	}

	public void DWMouseMove(double x, double y) {
		if (SNAP && (CurrentAction != SELECT)) {
			final double[] r = getSnap(x, y);
			x = r[0];
			y = r[1];
		}

		MouseX = (int) x;
		MouseY = (int) y;

		CatchPoint.setXY(x, y);
		isPointOnObject = false;
		isPointOnIntersection = false;

		switch (CurrentAction) {
		case SELECT:
		case MOVE:
			if (cpfield != null)
				cpfield.mouseMove(x, y);
			break;
		case HIDEOBJECT: {
			final int n1 = CatchList.size();
			OnCatch(x, y);
			if ((CatchList.size() != 0) || (n1 != 0))
				if (panel != null)
					panel.repaint();
		}
		break;

		case D_LINE:
			if (STATUS == 1) {
				SmartmoveCatch(x, y, true, true, true);
				//				if (!isPointOnObject) {
				//				}
			} else if (STATUS == 0)
				SmartmoveCatch(x, y, true, true, true);
			break;

		case D_POLYGON:
			if (SecondPnt != null && !Smart(SecondPnt, x, y))
				SmartmoveCatch(x, y, true, true, true);
			else
				SmartmoveCatch(x, y, true, true, true);
			if (SecondPnt != null)
				SecondPnt.setXY(x, y);
			break;
		case D_POINT:
		case TRIANGLE:
		case D_PARELINE:
		case D_PERPLINE:
		case D_CIRCLE:

		case D_ALINE:
		case D_MIDPOINT:
		case D_3PCIRCLE:
		case D_PRATIO:
		case D_TRATIO:
		case D_SQUARE:
		case RECTANGLE:
		case DRAWTRIALL:
		case TRAPEZOID:
		case RA_TRAPEZOID:
		case PARALLELOGRAM:
		case DRAWTRISQISO:
		case SANGLE:
			SmartmoveCatch(x, y, true, true, true);
			break;
		case PERPWITHFOOT:
			if (STATUS == 0)
				SmartmoveCatch(x, y, true, true, true);
			else
				SmartmoveCatchLine(x, y);
			break;
		case D_IOSTRI:
			if (SelectList.size() == 2)
				moveCatch(catchX, catchY);
			else
				SmartmoveCatch(x, y, true, true, true);
			break;
		case D_PFOOT: {
			if (SelectList.size() == 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);

				final double[] r = get_pt_dmcr(p1.getx(), p1.gety(), p2.getx(), p2.gety(), x, y);
				x = r[0];
				y = r[1];
			}
			SmartmoveCatch(x, y, true, true, true);
		}
		break;
		case D_PTDISTANCE:
			if (SelectList.size() < 3)
				SmartmoveCatchPt(x, y);
			else
				SmartmoveCatch(x, y, false, true, true); // Catch lines and circles but not points.
			break;

		case D_ANGLE:
			if (STATUS == 1) {
				final GEAngle ag = (GEAngle) SelectList.get(0);
				ag.move(x, y);
			}
			break;
		case DEFINEPOLY: {
			if (STATUS == 1)
				FirstPnt.setXY(x, y);
			SmartmoveCatchPt(x, y);
		}
		case INCENTER:
		case BARYCENTER:
		case ORTHOCENTER:
		case CIRCUMCENTER:
			SmartmoveCatchPt(x, y);
			break;
		case D_TEXT: {
			CatchPoint.setXY(x, y);
			final GraphicEntity cc = selectFromList(textlist, x, y);
			CatchList.clear();
			if (cc != null)
				CatchList.add(cc);
		}
		break;
		case D_CCLINE: {
			CatchPoint.setXY(x, y);
			CatchList.clear();
			SelectFromAList(CatchList, circlelist, x, y);
		}
		break;
		case EQUIVALENCE: {
			if (STATUS == 2) {
				final GEPolygon p = (GEPolygon) SelectList.get(0);
				final GEPoint p1 = (GEPoint) SelectList.get(1);
				final GEPoint t1 = p.getPreviousPoint(p1);
				final GEPoint t2 = p.getNextPoint(p1);
				final double[] r = getPTInterSection(x, y, p1.getx(),
						p1.gety(), t1.getx(), t1.gety(), t2.getx(), t2.gety());
				p.setDraggedPoints(p1, null, r[0] - p1.getx(), r[1] - p1.gety());

			} else if (STATUS == 3) {
				final GEPolygon p = (GEPolygon) SelectList.get(0);
				final GEPoint p1 = (GEPoint) SelectList.get(1);
				final GEPoint p2 = (GEPoint) SelectList.get(2);
				p.setDraggedPoints(p1, p2, x - vx1, y - vy1);
			}
		}
		break;
		case FREE_TRANSFORM: {
			if (STATUS == 1)
				if (SelectList.size() == 2) {
					final GEPolygon poly = (GEPolygon) SelectList.get(0);
					final GEPoint pt = (GEPoint) SelectList.get(1);
					poly.setDraggedPoints(pt, null,
							CatchPoint.getx() - pt.getx(), CatchPoint.gety()
							- pt.gety());
				}
		}
		break;
		case TRANSFORM: {
			if (STATUS == 1) {
				x = x - vtrx;
				y = y - vtry;
				vx1 = x - catchX;
				vy1 = y - catchY;
			} else if (STATUS == 2)
				if ((FirstPnt != null) && (ThirdPnt != null))
					vangle = Math.PI
					+ GEAngle.get3pAngle(ThirdPnt.getx(),
							ThirdPnt.gety(), FirstPnt.getx() - vx1,
							FirstPnt.gety() - vy1, x - vx1, y - vy1);

		}
		break;
		}

		if ((CurrentAction != MOVE) && (CurrentAction != SELECT))
			panel.repaint();
	}

	public void SmartmoveCatchPt(final double x, final double y) {
		final GEPoint pt = SelectAPoint(x, y);
		CatchList.clear();
		if (pt != null) {
			isPointOnObject = true;
			CatchList.add(pt);
			CatchPoint.setXY(pt.getx(), pt.gety());
		}
	}

	public void SmartmoveCatchLine(final double x, final double y) {
		final GELine pt = SelectALine(x, y);
		CatchList.clear();
		if (pt != null) {
			isPointOnObject = true;
			CatchList.add(pt);
			pt.pointonline(CatchPoint);
		}
	}

	public void SmartmoveCatch(final double x, final double y, final boolean bCatchPoints, final boolean bCatchLines, final boolean bCatchCircles) {
		CatchList.clear();
		CatchType = 0;
		SelectAllFromXY(CatchList, x, y, 1);
		final int n = CatchList.size();
		if (n > 0) {
			isPointOnObject = true;
			if (n == 1) {
				final GraphicEntity ge = CatchList.get(0);
				if (ge instanceof GELine) {
					final GELine ln = (GELine) ge;
					if (bCatchLines) {
						ln.pointonline(CatchPoint);
						if (ln.pointonMiddle(CatchPoint))
							CatchType = 1;
					}
				} else if (ge instanceof GECircle) {
					final GECircle cr = (GECircle) ge;
					if (bCatchCircles)
						cr.pointStickToCircle(CatchPoint);
				} else if (ge instanceof GEPoint) {
					final GEPoint p = (GEPoint) ge;
					if (bCatchPoints)
						CatchPoint.setXY(p.getx(), p.gety());
				}
			} else if (bCatchPoints)
				get_Catch_Intersection(x, y);
		} else if (bCatchPoints && (bCatchLines || bCatchCircles))
			hvCatchPoint();
		mouseCatchX = (int) CatchPoint.getx();
		mouseCatchY = (int) CatchPoint.gety();
	}

	//	public void SmartmoveCatch(final double x, final double y, final int type) { // 0.
	//																					// All.
	//																					// 1.
	//																					// Point
	//																					// Only.
	//		// 2. Line Only . 3. Circle Only. 4. P and L 5. P and C . 6. L and C.
	//		CatchList.clear();
	//		CatchType = 0;
	//		SelectAllFromXY(CatchList, x, y, 1);
	//		final int n = CatchList.size();
	//		if (n > 0) {
	//			isPointOnObject = true;
	//			if (n == 1) {
	//				final GeometricEntity c = CatchList.get(0);
	//				if (c instanceof GELine) {
	//					final GELine ln = (GELine) c;
	//					if ((type == 0) || (type == 2) || (type == 4)
	//							|| (type == 6)) {
	//						ln.pointonline(CatchPoint);
	//						if (ln.pointonMiddle(CatchPoint))
	//							CatchType = 1;
	//					}
	//				} else if (c instanceof GECircle) {
	//					final GECircle cr = (GECircle) c;
	//					if ((type == 0) || (type == 3) || (type == 5)
	//							|| (type == 6))
	//						cr.pointStickToCircle(CatchPoint);
	//				} else if (c instanceof GEPoint) {
	//					final GEPoint p = (GEPoint) c;
	//					if ((type == 0) || (type == 1) || (type == 4)
	//							|| (type == 5))
	//						CatchPoint.setXY(p.getx(), p.gety());
	//				}
	//			} else if ((type == 0) || (type == 1) || (type == 4) || (type == 5))
	//				get_Catch_Intersection(x, y);
	//		} else if ((type == 0) || (type == 4) || (type == 5))
	//			hvCatchPoint();
	//		mouseCatchX = (int) CatchPoint.getx();
	//		mouseCatchY = (int) CatchPoint.gety();
	//	}

	public void moveCatch(final double x, final double y) {
		final int n = CatchList.size();
		CatchList.clear();
		GraphicEntity obj = SelectAPoint(x, y);
		if (obj == null)
			obj = SelectALine(x, y);
		if (obj == null)
			obj = SelectACircle(x, y);
		if (obj != null) {
			CatchList.add(obj);
			isPointOnObject = true;
		}
		if (n != 0)
			panel.repaint();
	}

	public void get_Catch_Intersection(double x, double y) {
		int k = 0;
		// CLine ln = null;
		// Circle c = null;
		Object o1, o2;
		o1 = o2 = null;
		for (final Object o : CatchList) {
			if (!(o instanceof GEPoint)) {
				if (o1 == null)
					o1 = o;
				else if (o2 == null)
					o2 = o;
				k++;
			}
		}
		if (k >= 2) {
			double[] r = null;
			if ((o1 instanceof GELine) && (o2 instanceof GELine))
				r = intersect_ll((GELine) o1, (GELine) o2);
			else if ((o1 instanceof GECircle) && (o2 instanceof GECircle))
				r = intersect_cc((GECircle) o1, (GECircle) o2);
			else if ((o1 instanceof GELine) && (o2 instanceof GECircle))
				r = intersect_lc((GELine) o1, (GECircle) o2);
			else if ((o1 instanceof GECircle) && (o2 instanceof GELine))
				r = intersect_lc((GELine) o2, (GECircle) o1);
			if ((r != null) && (r.length > 0)) {
				int d = -1;
				double len = Double.MAX_VALUE;
				final int l = r.length;
				int j = 0;
				for (j = 0; j < (l / 2); j++) {
					final double s = Math.pow(r[j * 2] - x, 2)
							+ Math.pow(r[(j * 2) + 1] - y, 2);
					if (s < len) {
						d = j;
						len = s;
					}
				}
				if (d >= 0) {
					x = r[d * 2];
					y = r[(d * 2) + 1];
					if (SelectAPoint(x, y) == null) {
						CatchPoint.setXY(x, y);
						isPointOnIntersection = true;
					}
				}
			} else
				isPointOnIntersection = true;
		}
	}

	public boolean Smart(final GEPoint p, final double x, final double y) { // set
		// p
		// to
		// a
		// point
		// on
		// obj
		// and
		// near
		// x,y
		p.setXY(x, y);
		final GEPoint pt = SmartPoint(p);
		if (pt != null)
			return true;
		final GELine line = SmartPLine(p);
		if (line != null)
			return true;
		final GECircle c = SmartPCircle(p);
		if (c != null)
			return true;
		return false;
	}

	public GEPoint SmartAddPoint(final double x, final double y) { // add a new
		// point to
		// drawing
		// with x,y
		final GEPoint pt = SelectAPoint(x, y);
		if (pt != null)
			return pt;

		final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
		SelectFromAList(v, linelist, x, y);
		SelectFromAList(v, circlelist, x, y);
		if (v.size() >= 2)
			return meetTwoObject(v.get(0), v.get(1), true, x, y);

		final GEPoint p = CreateANewPoint(x, y);
		addPointToList(p);
		p.setChosenColor(false); // XXX Check to see whether this does anything
		final int n = v.size();
		if (n == 0)
			setCatchHVPoint(p);
		else if (n == 1) {
			final Object obj = v.get(0);
			if (obj instanceof GELine) {
				final GELine ln = (GELine) obj;
				ln.pointOnLine(p);
				ln.pointonMiddle(p);
				addPointToLine(p, (GELine) obj, false);
			} else if (obj instanceof GECircle)
				addPointToCircle(p, (GECircle) obj, false);
			characteristicSetMethodAndAddPoly(false);
		}
		return p;
	}

	//	public GraphicEntity SmartPointOnWhich(final GEPoint p) {
	//		final GEPoint pt = SmartPoint(p);
	//		if (pt != null)
	//			return pt;
	//		final GELine line = SmartPLine(p);
	//		if (line != null)
	//			return line;
	//		final GECircle c = SmartPCircle(p);
	//		if (c != null)
	//			return c;
	//		return null;
	//	}

	public GELine SelectALine(final double x, final double y) {
		return SmartPointOnLine(x, y); // XXX this method seems redundant.
	}

	public GECircle SelectACircle(final double x, final double y) {
		for (final GECircle c : circlelist) {
			if (c.nearcircle(x, y, UtilityMiscellaneous.PIXEPS))
				return c;
		}
		return null;
	}

	public GEPoint SelectAPoint(final double x, final double y) {
		GEPoint pt = null;
		for (final GEPoint p : pointlist) {
			if (p.isLocatedNear(x, y)) {
				pt = p;
				break;
			}
		}
		return pt;
	}

	public void clearFlashAndAdd(final ArrayList<Flash> v) {
		clearFlash();
		flashlist.addAll(v);
	}

	public void clear_but_angle() {
		for (final Iterator<Flash> iter = flashlist.iterator(); iter.hasNext();) {
			final Flash ff = iter.next();
			if (ff instanceof FlashAngle)
				continue;
			ff.stop();
			iter.remove();
		}
	}

	public void doFlash() {
		for (final Iterator<Flash> iter = flashlist.iterator(); iter.hasNext();) {
			final Flash ff = iter.next();
			if (!ff.isfinished()) {
				ff.start();
				ff.stop();
			}
			if (ff.getvisibleType())
				iter.remove();
		}
	}

	public void clearFlash() {
		for (final Flash ff : flashlist)
			ff.stop();
		flashlist.clear();
	}

	public void addFlash(final Flash f) {
		if (f == null)
			return;

		clearFlash();
		flashlist.add(f);
		f.start();
	}

	public GEPoint getCommonPoint(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
		GELine ln1 = findLineGivenTwoPoints(p1, p2);
		GELine ln2 = findLineGivenTwoPoints(p3, p4);

		if (ln1 == null && ln2 == null) {
			if ((p1 == p3) || (p1 == p4))
				return p1;
			if ((p2 == p3) || (p2 == p4))
				return p2;
			return null;
		}

		if (ln1 != null && ln2 != null) {
			Collection<GEPoint> collectionPoints = new HashSet<GEPoint>();
			ln1.getCommonPoints(ln2, collectionPoints);
			return (collectionPoints.isEmpty()) ? null : (GEPoint) (collectionPoints.toArray())[0];
		}

		if (ln1 != null && ln2 == null) {
			if (ln1.isCoincidentWith(p3))
				return p3;
			if (ln1.isCoincidentWith(p4))
				return p4;
			return null;
		}

		if (ln1 == null && ln2 != null) {
			if (ln2.isCoincidentWith(p1))
				return p1;
			if (ln2.isCoincidentWith(p2))
				return p2;
			return null;
		}
		return null;
	}

	public void addCgFlash(final FlashCG f1, final FlashCG f2, final Flash f) {
		final int size = flashlist.size();
		int n = 0;
		for (final Flash fx : flashlist)
			if (fx instanceof FlashCG)
				n++;

		int i = 1;
		for (; true; i++) {
			int j = 0;
			for (j = 0; j < size; j++) {
				final Flash fx = flashlist.get(j);
				if (fx instanceof FlashCG) {
					final FlashCG fx1 = (FlashCG) fx;
					if (i == fx1.getDNum())
						break;
				}
			}
			if (j == size)
				break;
		}

		if (n == 0) {
			f1.setDNum(2);
			f2.setDNum(2);
		} else {
			f1.setDNum(i);
			f2.setDNum(i);
		}
		addFlashx(f1);
		addFlashx(f);
		addFlashx(f2);
	}

	public boolean isInAction() {
		return (STATUS != 0) || (SelectList.size() != 0);
	}

	public void startFlash() {
		for (final Flash fx : flashlist) {
			if (fx.isrRunning())
				return;
			if (!fx.isfinished()) {
				fx.start();
				return;
			}
		}
	}

	public void addFlash2(final Flash f) {
		for (int i = 0; i < flashlist.size(); i++)
			if (flashlist.get(i) instanceof FlashRedoStep) {
				flashlist.add(i, f);
				return;
			}
		addFlash1(f);
	}

	public void addFlash1(final Flash f) {

		addFlashx(f);
		if (flashlist.size() == 1)
			f.start();
	}

	public void addFlashx(final Flash f) {
		if (f == null)
			return;

		if (f instanceof FlashAngle) {
			final FlashAngle tf = (FlashAngle) f;
			int num = 0;

			final GEPoint pt = getCommonPoint(tf.p1, tf.p2, tf.p3, tf.p4);
			if (pt != null)
				for (final Flash obj : flashlist)
					if (obj instanceof FlashAngle) {
						final FlashAngle ff = (FlashAngle) obj;
						if (pt == getCommonPoint(ff.p1, ff.p2, ff.p3, ff.p4))
							num++;
					}
			int d = 0;
			d = num * 10;
			tf.setRadius(tf.getRadius() + d);
		}
		if (!flashlist.contains(f))
			flashlist.add(f);
	}

	public boolean isFlashFinished() {
		final int n = flashlist.size();
		if (n == 0)
			return true;
		if (n > 1)
			return false;
		final Flash f = flashlist.get(0);
		return (f.isfinished());

	}

	public void drawFlash(final Graphics2D g2) {
		if (flashlist.size() == 0)
			return;

		boolean r = false;
		for (final Iterator<Flash> iter = flashlist.iterator(); iter.hasNext();) {
			final Flash f = iter.next();
			if (f == null)
				iter.remove();
			if ((r == false) && !f.isrRunning() && !f.isfinished()) {
				f.start();
				f.draw(g2);
				r = true;
			} else if (f.isrRunning()) {
				f.draw(g2);
				r = true;
			}
			if (f.isfinished())
				if (f.getvisibleType())
					iter.remove();
				else
					f.draw(g2);
		}
		if (all_flash_finished())
			run_to_prove(null, null);// (UndoStruct) U_Obj);
	}

	/**
	 * Method that invokes drawing methods for all the objects in the current diagram.
	 */
	public void paintPoint(final Graphics2D g2) {
		drawGrid(g2);
		setAntiAlias(g2);
		if (undo != null)
			undo.draw(g2);

		drawList(polygonlist, g2);
		drawSelect(SelectList, g2);
		drawList(anglelist, g2);
		drawPerpFoot(g2, null, 0);
		drawList(tracelist, g2);
		drawList(distancelist, g2);
		drawList(anglelist, g2);

		drawList(circlelist, g2);
		drawList(linelist, g2);
		drawList(otherlist, g2);

		drawFlash(g2);

		drawList(pointlist, g2);
		drawList(textlist, g2);

		drawCurrentAct(g2);
		drawCatch(g2);

		if (cpfield != null)
			cpfield.draw(g2);

		drawTrackpt(g2);
	}

	public void drawTrackpt(final Graphics2D g2) {
		if (CTrackPt == null)
			return;

		CTrackPt.draw_ct(g2);

		for (final GETrace tr : tracelist)
			if (tr.isTracePt(CTrackPt)) {
				tr.addTracePoint((int) CTrackPt.getx(), (int) CTrackPt.gety());
				return;
			}
	}

	public GETrace getTraceByPt(final GEPoint pt) {
		for (final GETrace tr : tracelist)
			if (tr.isTracePt(pt))
				return tr;
		return null;
	}

	public static void setSmartPVLine(final GEPoint p1, final GEPoint p2) {
		if ((p1 == null) || (p2 == null))
			return;

		final double x1 = p1.getx();
		final double y1 = p1.gety();
		final double x2 = p2.getx();
		final double y2 = p2.gety();

		if ((Math.abs(x2 - x1) < UtilityMiscellaneous.PIXEPS)
				&& ((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) > (4 * UtilityMiscellaneous.PIXEPS * UtilityMiscellaneous.PIXEPS)))
			p2.setXY(x1, y2);
		else if ((Math.abs(y2 - y1) < UtilityMiscellaneous.PIXEPS)
				&& ((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) > (4 * UtilityMiscellaneous.PIXEPS * UtilityMiscellaneous.PIXEPS)))
			p2.setXY(x2, y1);

	}

	public void drawSmartPVLine(final GEPoint p1, final GEPoint p2,
			final Graphics2D g2) {
		int x, y;
		x = y = 0;
		if ((p1 == null) || (p2 == null))
			return;

		g2.setColor(Color.red);

		final int x1 = (int) p1.getx();
		final int y1 = (int) p1.gety();
		final int x2 = (int) p2.getx();
		final int y2 = (int) p2.gety();

		if ((Math.abs(x2 - x1) < UtilityMiscellaneous.PIXEPS)
				&& ((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) > (4 * UtilityMiscellaneous.PIXEPS * UtilityMiscellaneous.PIXEPS))) {
			x = x1;
			if (y2 > y1)
				y = (int) Height;
			else
				y = 0;
			p2.setXY(x1, y2);
		} else if ((Math.abs(y2 - y1) < UtilityMiscellaneous.PIXEPS)
				&& ((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) > (4 * UtilityMiscellaneous.PIXEPS * UtilityMiscellaneous.PIXEPS))) {
			y = y1;
			if (x2 > x1)
				x = (int) Width;
			else
				x = 0;
			p2.setXY(x2, y1);
		} else {
			g2.drawLine(x1, y1, x2, y2);
			return;
		}

		final float dash[] = { 2.0f };
		g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 5.0f, dash, 0.0f));
		g2.drawLine((int) p1.getx(), (int) p1.gety(), x, y);
	}

	public void drawCurrentAct(final Graphics2D g2) {
		if (SHOWOBJECT == CurrentAction)
			for (final Constraint cs : constraintlist) {
				if (cs.GetConstraintType() != Constraint.INVISIBLE)
					continue;
				final GraphicEntity c1 = (GraphicEntity) cs.getelement(0);
				if (c1.bVisible == false) {
					c1.setVisible(true);
					c1.draw(g2, true);
					c1.setVisible(false);
				}
			}
		setCurrentDrawEnvironment(g2);

		switch (CurrentAction) {
		case SELECT: {
			if (bLeftButtonDown) {
				g2.setColor(Color.black);
				g2.setStroke(UtilityMiscellaneous.DashedStroke);
				drawRect((int) vx1, (int) vy1, (int) CatchPoint.getx(), (int) CatchPoint.gety(), g2);
			}
		}
		break;
		case D_POINT: {
			drawCatchRect(g2);
			if (bLeftButtonDown)
				for (final GraphicEntity ge : SelectList) {
					if (ge instanceof GEPoint)
						drawPointNameLocation((GEPoint)ge, g2);
				}
		}
		break;
		case H_LINE:
		case V_LINE:
			if (STATUS == 1) {
				SecondPnt.draw(g2, false); // Before edit, this method called draw(g2) instead of draw(g2, false). I don't know why two different draw methods were implemented.
				drawSmartPVLine(FirstPnt, SecondPnt, g2);
			} else
				drawCatchRect(g2);
			break;
		case D_LINE: {
			drawSmartPVLine(FirstPnt, CatchPoint, g2);
			drawCatchRect(g2);
			break;
		}
		case D_PARELINE:
			if (SelectList.size() == 0) {
				drawCatchRect(g2);
				break;
			} else {
				final GELine line = (GELine) SelectList.get(0);
				GELine.drawPParaLine(line, CatchPoint, g2);
				drawPointOrCross(g2);
			}
			break;
		case D_PERPLINE:
			if (SelectList.size() == 0) {
				drawCatchRect(g2);
				break;
			} else {
				final GELine line = (GELine) SelectList.get(0);
				GELine.drawTPerpLine(line, CatchPoint, g2);
				drawPointOrCross(g2);
			}
			break;
		case D_ALINE: {
			int n = SelectList.size();
			if (n == 3)
				drawPointOrCross(g2);
			if (!bLeftButtonDown) {
				n = SelectList.size();
				if (n == 3) {
					final GELine ln1 = (GELine) SelectList.get(0);
					final GELine ln2 = (GELine) SelectList.get(1);
					final GELine ln3 = (GELine) SelectList.get(2);
					final double k = GELine.getALineK(ln1, ln2, ln3);
					drawAuxLine((int) CatchPoint.getx(), (int) CatchPoint.gety(), k, g2);
					drawPointOrCross(g2);
				}
			}
		}
		break;
		case PERPWITHFOOT: {
			if (STATUS == 1) {
				if (FirstPnt == null)
					break;
				g2.drawLine((int) FirstPnt.getx(), (int) FirstPnt.gety(),
						(int) CatchPoint.getx(), (int) CatchPoint.gety());
				drawPointOrCross(g2);
				if (CatchList.size() > 0) {
					final GELine ln = (GELine) CatchList.get(0);
					final double k0 = ln.getSlope();
					final GEPoint pt = ln.getFirstPoint();
					double x, y;
					final double x0 = pt.getx();
					final double y0 = pt.gety();
					final double x1 = FirstPnt.getx();
					final double y1 = FirstPnt.gety();

					if (Math.abs(k0) > UtilityMiscellaneous.MAX_SLOPE) {
						x = x0;
						y = y1;
					} else {
						x = ((k0 * (y1 - y0)) + (k0 * k0 * x0) + x1)
								/ (1 + (k0 * k0));
						y = y0 + (k0 * (x - x0));
					}
					g2.setColor(Color.red);
					g2.setStroke(UtilityMiscellaneous.DashedStroke);
					g2.drawLine((int) x1, (int) y1, (int) x, (int) y);

					if (ln.extent != GELine.ET_ENDLESS) {
						final GEPoint[] spt = ln.getTwoPointsOfLine();
						if ((spt != null) && (spt.length == 2)) {
							final double r1 = Math.pow(spt[0].getx() - x, 2)
									+ Math.pow(spt[0].gety() - y, 2);
							final double r2 = Math.pow(spt[1].getx() - x, 2)
									+ Math.pow(spt[1].gety() - y, 2);
							final double r = Math.pow(
									spt[1].getx() - spt[0].getx(), 2)
									+ Math.pow(spt[1].gety() - spt[0].gety(), 2);
							if ((r1 < r) && (r2 < r)) {
							} else if (r1 > r2)
								g2.drawLine((int) spt[1].getx(),
										(int) spt[1].gety(), (int) x, (int) y);
							else
								g2.drawLine((int) spt[0].getx(),
										(int) spt[0].gety(), (int) x, (int) y);

						}
					}
					g2.setStroke(UtilityMiscellaneous.NormalLineStroke);
					drawCross((int) x, (int) y, 5, g2);

				}
			} else
				drawCatchRect(g2);
		}
		break;
		case D_PTDISTANCE: {
			final int n = SelectList.size();

			if (n >= 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				GEPoint p3 = null;
				if (n == 2) {
					// if (CatchList.size() == 1) {
					// CClass c = (CClass) CatchList.get(0);
					// if (c instanceof CPoint)
					// p3 = (CPoint) c;
					// }
				} else if (n == 3)
					p3 = (GEPoint) SelectList.get(2);
				if (p3 != null) {
					final double radius = sdistance(p1, p2);
					final int x = (int) p3.getx();
					final int y = (int) p3.gety();
					g2.setStroke(UtilityMiscellaneous.DashedStroke);
					g2.setColor(Color.red);
					g2.drawOval((int) (x - radius), (int) (y - radius),
							(int) (2 * radius), (int) (2 * radius));
				}
			}
			drawCatchRect(g2);
		}
		break;
		case D_CIRCLE: {
			if (STATUS == 1) {
				drawcircle2p(FirstPnt.getx(), FirstPnt.gety(),
						CatchPoint.getx(), CatchPoint.gety(), g2);
				drawPointOrCross(g2);
			} else
				drawCatchRect(g2);
			break;
		}
		case D_SQUARE: {
			if (STATUS == 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				drawTipSquare(p1, p2, CatchPoint, g2);
			} else if (STATUS == 0) {
				if (SelectList.size() > 0) {
					final GEPoint p1 = (GEPoint) SelectList.get(0);
					drawSmartPVLine(p1, CatchPoint, g2);
				}
			} else if (STATUS == 1) {
				final GELine line = (GELine) SelectList.get(0);
				final GEPoint[] pl = line.getTwoPointsOfLine();
				if (pl == null)
					break;
				drawTipSquare(pl[0], pl[1], CatchPoint, g2);
			}
		}
		break;

		case D_PRATIO: {
			if (SelectList.size() == 2) {
				drawCatchRect(g2);
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				double dx = p2.getx() - p1.getx();
				double dy = p2.gety() - p1.gety();
				double ratio = 0;
				ratio = (v1 * 1.00) / v2;
				dx = dx * ratio;
				dy = dy * ratio;
				final double x = CatchPoint.getx();
				final double y = CatchPoint.gety();
				g2.setColor(Color.red);
				g2.drawLine((int) x, (int) y, (int) (x + dx), (int) (y + dy));
				drawCross((int) (x + dx), (int) (y + dy), 3, g2);
			}
		}
		break;
		case D_TRATIO: {
			if (SelectList.size() != 2)
				break;
			final GEPoint p1 = (GEPoint) SelectList.get(0);
			final GEPoint p2 = (GEPoint) SelectList.get(1);

			final double dx = p2.getx() - p1.getx();
			final double dy = p2.gety() - p1.gety();

			double ratio = 0;
			ratio = (v1 * 1.0) / v2;
			final double x1 = CatchPoint.getx() + (dy * ratio);
			final double y1 = CatchPoint.gety() - (dx * ratio);
			// double x2 = CatchPoint.getx() + dy * ratio;
			// double y2 = CatchPoint.gety() - dx * ratio;

			// double xx = SecondPnt.getx();
			// double yy = SecondPnt.gety();
			// double r1 = Math.pow(xx - x1, 2) + Math.pow(yy - y1, 2);
			// double r2 = Math.pow(xx - x2, 2) + Math.pow(yy - y2, 2);
			g2.setColor(Color.red);
			// if (r1 < r2) {
			g2.drawLine((int) x1, (int) y1, (int) CatchPoint.getx(),
					(int) CatchPoint.gety());
			drawCross((int) (x1), (int) (y1), 3, g2);

			// } else {
			// g2.drawLine((int) x2, (int) y2, (int) FirstPnt.getx(), (int)
			// FirstPnt.gety());
			// }
		}
		break;
		// ///////////////////////////////////////////////down;

		case DEFINEPOLY: {
			if ((STATUS == 1) && (SelectList.size() >= 1)) {
				final GEPolygon cp = (GEPolygon) SelectList.get(0);
				cp.draw(g2, FirstPnt);
			}
			drawCatchRect(g2);
		}
		break;
		case D_PFOOT: {
			final int n = SelectList.size();
			if (n == 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final double x = CatchPoint.getx();
				final double y = CatchPoint.gety();

				final double[] r = get_pt_dmcr(p1.getx(), p1.gety(), p2.getx(),
						p2.gety(), x, y);
				final double xr = r[0];
				final double yr = r[1];
				final double xx = (p1.getx() + p2.getx()) / 2;
				final double yy = (p1.gety() + p2.gety()) / 2;
				final double dis = sdistance(p1, p2);

				CatchPoint.setXY(xr, yr);
				drawCatchRect(g2);
				g2.setColor(Color.red);
				g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) p2.getx(),
						(int) p2.gety());
				g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) xr,
						(int) yr);
				g2.drawLine((int) xr, (int) yr, (int) p2.getx(),
						(int) p2.gety());
				g2.setStroke(UtilityMiscellaneous.DashedStroke);
				g2.drawOval((int) (xx - (dis / 2)), (int) (yy - (dis / 2)),
						(int) dis, (int) dis);
			} else {
				if (n == 1) {
					final GEPoint pt = (GEPoint) SelectList.get(0);
					drawSmartPVLine(pt, CatchPoint, g2);
				}
				drawCatchInterCross(g2);

			}
		}
		break;

		case MULSELECTSOLUTION: {
			for (int i = 0; i < solutionlist.size(); i++) {
				final GEPoint p = solutionlist.get(i);
				g2.setColor(Color.red);
				g2.drawOval((int) p.getx() - 18, (int) p.gety() - 18, 36, 36);
				p.draw(g2, true); // Before edit, this method called draw(g2) instead of draw(g2, true). I don't know why two different draw methods were implemented.
			}
		}
		break;
		case D_POLYGON: {
			if (SelectList.size() >= 1) {
				drawSmartPVLine(FirstPnt, SecondPnt, g2);
				if (SelectList.size() == (STATUS - 1)) {
					final GEPoint t1 = (GEPoint) (SelectList.get(0));
					g2.drawLine((int) t1.getx(), (int) t1.gety(),
							(int) SecondPnt.getx(), (int) SecondPnt.gety());
				}
				if (SelectList.size() >= 2) {
					GEPoint t1 = (GEPoint) SelectList.get(0);

					drawTipRect((int) t1.getx(), (int) t1.gety(), g2);

					for (int i = 1; i < SelectList.size(); i++) {
						final GEPoint tp = (GEPoint) SelectList.get(i);
						g2.drawLine((int) t1.getx(), (int) t1.gety(),
								(int) tp.getx(), (int) tp.gety());
						t1 = tp;
					}
				}
				// drawPointOrCross(g2);
			} // else
			// drawCatchInterCross(g2);
			drawCatchRect(g2);
		}
		break;
		case DRAWTRIALL: {
			if (STATUS == 1) {
				final GEPoint pt = (GEPoint) SelectList.get(0);
				drawSmartPVLine(pt, CatchPoint, g2);
			} else if (STATUS == 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final double x1 = p1.getx();
				final double y1 = p1.gety();
				final double x2 = p2.getx();
				final double y2 = p2.gety();
				final double xt = (x1 + x2) / 2;
				final double yt = (y1 + y2) / 2;
				final double dx = xt - x1;
				final double dy = yt - y1;

				final double xf = xt - (Math.sqrt(3) * dy);
				final double yf = yt + (Math.sqrt(3) * dx);

				final double xs = xt + (Math.sqrt(3) * dy);
				final double ys = yt - (Math.sqrt(3) * dx);

				final double xc = CatchPoint.getx();
				final double yc = CatchPoint.gety();
				g2.setColor(Color.red);

				if ((((xc - xf) * (xc - xf)) + ((yc - yf) * (yc - yf))) < (((xc - xs) * (xc - xs)) + ((yc - ys) * (yc - ys)))) {
					g2.drawLine((int) x1, (int) y1, (int) xf, (int) yf);
					g2.drawLine((int) x2, (int) y2, (int) xf, (int) yf);
				} else {
					g2.drawLine((int) x1, (int) y1, (int) xs, (int) ys);
					g2.drawLine((int) x2, (int) y2, (int) xs, (int) ys);
				}

			}
		}
		break;
		case RA_TRAPEZOID: {
			if ((STATUS == 0) && (SelectList.size() == 1)) {
				final GEPoint pt = (GEPoint) SelectList.get(0);
				drawSmartPVLine(pt, CatchPoint, g2);
				drawPointOrCross(g2);
			} else if (STATUS == 1) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final double xt = CatchPoint.getx();
				final double yt = CatchPoint.gety();
				final double x1 = p1.getx();
				final double y1 = p1.gety();
				final double x2 = p2.getx();
				final double y2 = p2.gety();
				double x, y;
				if (Math.abs(x2 - x1) < UtilityMiscellaneous.ZERO) {
					x = x1;
					y = yt;
				} else {
					final double k = (y2 - y1) / (x2 - x1);
					x = (((k * k * xt) + x1 + (k * y1)) - (k * yt))
							/ ((k * k) + 1);
					y = ((((k * k * y1) + yt) - (k * xt)) + (k * x1))
							/ ((k * k) + 1);
				}

				drawPointOrCross(g2);
				g2.setColor(Color.red);
				g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
				g2.drawLine((int) x1, (int) y1, (int) x, (int) y);
				g2.drawLine((int) xt, (int) yt, (int) x, (int) y);
				g2.drawLine((int) xt, (int) yt, (int) x2, (int) y2);
			} else
				drawCatchRect(g2);
		}
		break;
		case TRAPEZOID: {
			if (STATUS == 0) {
				if (SelectList.size() == 1) {
					final GEPoint pt = (GEPoint) SelectList.get(0);
					drawSmartPVLine(pt, CatchPoint, g2);
					drawPointOrCross(g2);
				} else if (SelectList.size() == 2) {
					final GEPoint pt = (GEPoint) SelectList.get(0);
					final GEPoint pt1 = (GEPoint) SelectList.get(1);
					g2.setColor(Color.red);
					g2.drawLine((int) pt.getx(), (int) pt.gety(),
							(int) pt1.getx(), (int) pt1.gety());
					drawSmartPVLine(pt1, CatchPoint, g2);
					drawPointOrCross(g2);
				} else
					drawCatchRect(g2);
			} else { // 1
				final GEPoint pt = (GEPoint) SelectList.get(0);
				final GEPoint pt1 = (GEPoint) SelectList.get(1);
				final GEPoint pt2 = (GEPoint) SelectList.get(2);
				final double x = CatchPoint.getx();
				final double y = (((pt.gety() - pt1.gety()) * (x - pt2.getx())) / (pt
						.getx() - pt1.getx())) + pt2.gety();

				g2.setColor(Color.red);
				g2.drawLine((int) pt.getx(), (int) pt.gety(), (int) pt1.getx(),
						(int) pt1.gety());
				g2.drawLine((int) pt2.getx(), (int) pt2.gety(),
						(int) pt1.getx(), (int) pt1.gety());
				g2.drawLine((int) pt.getx(), (int) pt.gety(), (int) x, (int) y);
				g2.drawLine((int) pt2.getx(), (int) pt2.gety(), (int) x,
						(int) y);
			}
		}
		break;

		case PARALLELOGRAM: {
			if (STATUS == 1) {
				final GEPoint pt = (GEPoint) SelectList.get(0);
				drawSmartPVLine(pt, CatchPoint, g2);
			} else if (STATUS == 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				drawSmartPVLine(p2, CatchPoint, g2);
				final double xt = (p1.getx() + CatchPoint.getx()) - p2.getx();
				final double yt = (p1.gety() + CatchPoint.gety()) - p2.gety();
				g2.drawLine((int) xt, (int) yt, (int) p1.getx(),
						(int) p1.gety());
				g2.drawLine((int) xt, (int) yt, (int) CatchPoint.getx(),
						(int) CatchPoint.gety());
				g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) p2.getx(),
						(int) p2.gety());
				drawPointOrCross(g2);
			}
		}
		break;
		case RECTANGLE: {
			if (STATUS == 1) {
				final GEPoint pt = (GEPoint) SelectList.get(0);
				drawSmartPVLine(pt, CatchPoint, g2);
			} else if (STATUS == 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final double x1 = p1.getx();
				final double y1 = p1.gety();
				final double x2 = p2.getx();
				final double y2 = p2.gety();

				final double xc = CatchPoint.getx();
				final double yc = CatchPoint.gety();

				final double dlx = x2 - x1;
				final double dly = y2 - y1;
				final double dl = (dlx * dlx) + (dly * dly);

				final double x = (((y2 - yc) * dlx * dly) + (dly * dly * xc) + (dlx
						* dlx * x2))
						/ dl;
				final double y = (((x2 - xc) * dlx * dly) + (dlx * dlx * yc) + (dly
						* dly * y2))
						/ dl;

				g2.setColor(Color.red);
				final double xt = (x + p1.getx()) - p2.getx();
				final double yt = (y + p1.gety()) - p2.gety();

				g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) p2.getx(),
						(int) p2.gety());
				g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) xt,
						(int) yt);
				g2.drawLine((int) p2.getx(), (int) p2.gety(), (int) x, (int) y);
				g2.drawLine((int) xt, (int) yt, (int) x, (int) y);

			}
		}
		break;
		case DRAWTRISQISO: {
			if (STATUS == 1) {
				final GEPoint pt = (GEPoint) SelectList.get(0);
				drawSmartPVLine(pt, CatchPoint, g2);
			} else if (STATUS == 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				final double x1 = p1.getx();
				final double y1 = p1.gety();
				final double x2 = p2.getx();
				final double y2 = p2.gety();
				final double xt = (x1 + x2) / 2;
				final double yt = (y1 + y2) / 2;
				final double dx = xt - x1;
				final double dy = yt - y1;

				final double xf = xt - dy;
				final double yf = yt + dx;

				final double xs = xt + dy;
				final double ys = yt - dx;

				final double xc = CatchPoint.getx();
				final double yc = CatchPoint.gety();
				g2.setColor(Color.red);

				if ((((xc - xf) * (xc - xf)) + ((yc - yf) * (yc - yf))) < (((xc - xs) * (xc - xs)) + ((yc - ys) * (yc - ys)))) {
					g2.drawLine((int) x1, (int) y1, (int) xf, (int) yf);
					g2.drawLine((int) x2, (int) y2, (int) xf, (int) yf);
				} else {
					g2.drawLine((int) x1, (int) y1, (int) xs, (int) ys);
					g2.drawLine((int) x2, (int) y2, (int) xs, (int) ys);
				}
			}
		}
		break;
		case D_IOSTRI: {
			if (SelectList.size() == 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);
				drawTipTriangle(p1, p2, CatchPoint, g2);
			} else {
				if (SelectList.size() == 1) {
					final GEPoint p = (GEPoint) SelectList.get(0);
					drawSmartPVLine(p, CatchPoint, g2);
				}
				drawCatchRect(g2);
			}
		}
		break;
		case TRIANGLE: {
			drawPointOrCross(g2);
			if ((STATUS == 1) && (SelectList.size() == 1)) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				CatchPoint.draw(g2, true);  // Before edit, this method called draw(g2) instead of draw(g2, false). I don't know why two different draw methods were implemented.
				g2.setColor(Color.red);
				g2.drawLine((int) CatchPoint.getx(), (int) CatchPoint.gety(),
						(int) p1.getx(), (int) p1.gety());
			} else if (STATUS == 2)
				if (SelectList.size() == 2) {
					final GEPoint p1 = (GEPoint) SelectList.get(0);
					final GEPoint p2 = (GEPoint) SelectList.get(1);

					CatchPoint.draw(g2, true);  // Before edit, this method called draw(g2) instead of draw(g2, true). I don't know why two different draw methods were implemented.
					g2.setColor(Color.red);
					g2.drawLine((int) CatchPoint.getx(),
							(int) CatchPoint.gety(), (int) p1.getx(),
							(int) p1.gety());
					g2.drawLine((int) CatchPoint.getx(),
							(int) CatchPoint.gety(), (int) p2.getx(),
							(int) p2.gety());
					g2.drawLine((int) p1.getx(), (int) p1.gety(),
							(int) p2.getx(), (int) p2.gety());
				}
		}
		break;
		case D_3PCIRCLE: {

			if (STATUS == 2) {
				final GEPoint p1 = (GEPoint) SelectList.get(0);
				final GEPoint p2 = (GEPoint) SelectList.get(1);

				final double x_1 = p1.getx();
				final double x_2 = p1.gety();
				final double x_3 = p2.getx();
				final double x_4 = p2.gety();
				final double x_5 = CatchPoint.getx();
				final double x_6 = CatchPoint.gety();

				final double m = (((2 * (x_3 - x_1) * x_6)
						+ (((-2 * x_4) + (2 * x_2)) * x_5) + (2 * x_1 * x_4)) - (2 * x_2 * x_3));

				double x = ((x_4 - x_2) * x_6 * x_6)
						+ ((((-1 * x_4 * x_4) - (x_3 * x_3)) + (x_2 * x_2) + (x_1 * x_1)) * x_6)
						+ ((x_4 - x_2) * x_5 * x_5) + (x_2 * x_4 * x_4)
						+ (((-1 * x_2 * x_2) - (x_1 * x_1)) * x_4)
						+ (x_2 * x_3 * x_3);

				x = ((-1) * x) / m;

				final double y = ((-1) * (((((2 * x_5) - (2 * x_1)) * x)
						- (x_6 * x_6) - (x_5 * x_5))
						+ (x_2 * x_2) + (x_1 * x_1)))
						/ (((2 * x_6) - (2 * x_2)));

				final double radius = Math.sqrt(Math.pow(x - x_1, 2)
						+ Math.pow(y - x_2, 2));

				g2.setStroke(UtilityMiscellaneous.DashedStroke);
				g2.setColor(Color.red);
				if ((Math.abs(x) < UtilityMiscellaneous.MAX_DRAW_LEN)
						&& (Math.abs(y) < UtilityMiscellaneous.MAX_DRAW_LEN)
						&& (radius < UtilityMiscellaneous.MAX_DRAW_LEN))
					g2.drawOval((int) (x - radius), (int) (y - radius),
							(int) (2 * radius), (int) (2 * radius));
				drawPointOrCross(g2);
			} else
				drawCatchRect(g2);

		}
		break;
		case SANGLE: {
			if (SelectList.size() == 2) {
				final GELine ln = (GELine) SelectList.get(0);
				final GEPoint p = (GEPoint) SelectList.get(1);
				final double k = ln.getSlope();
				final double k1 = Constraint.getSpecifiedAnglesMagnitude(STATUS);
				double kx1 = (k + k1) / (1 - (k * k1));
				double kx2 = (k - k1) / (1 + (k * k1));
				if (ln.isVertical()) {
					kx1 = -1 / k1;
					kx2 = 1 / k1;
				}

				final double x = CatchPoint.getx();
				final double y = CatchPoint.gety();

				final double r1 = GELine.distanceToPoint(p.getx(), p.gety(),
						kx1, x, y);
				final double r2 = GELine.distanceToPoint(p.getx(), p.gety(),
						kx2, x, y);
				g2.setColor(Color.red);

				if (r1 <= r2)
					GELine.drawXLine(p.getx(), p.gety(), kx1, g2);
				else
					GELine.drawXLine(p.getx(), p.gety(), kx2, g2);
			}
		}
		break;
		case ZOOM_OUT:
		case ZOOM_IN: {
			if (mouseInside) {
				g2.setStroke(UtilityMiscellaneous.DashedStroke);
				g2.setColor(Color.red);
				final int x = (int) CatchPoint.getx();
				final int y = (int) CatchPoint.gety();
				g2.drawLine(x, 0, x, (int) Height);
				g2.drawLine(0, y, (int) Width, y);
			}
		}
		break;
		case EQUIVALENCE: {
			if (STATUS == 2) {
				final GEPolygon p = (GEPolygon) SelectList.get(0);
				final GEPoint p1 = (GEPoint) SelectList.get(1);
				final GEPoint t1 = p.getPreviousPoint(p1);
				final GEPoint t2 = p.getNextPoint(p1);
				if ((p1 != null) && (t1 != null) && (t2 != null)) {
					drawAuxLine((int) p1.getx(), (int) p1.gety(),
							(t2.gety() - t1.gety()) / (t2.getx() - t1.getx()),
							g2);
					final double[] r = getPTInterSection(CatchPoint.getx(),
							CatchPoint.gety(), p1.getx(), p1.gety(), t1.getx(),
							t1.gety(), t2.getx(), t2.gety());
					drawCross((int) r[0], (int) r[1], 2, g2);
				}
			} else if (STATUS == 3) {

			}
		}
		break;
		case TRANSFORM: {
			if ((STATUS == 1) || (STATUS == 2) || (STATUS == 3)) {
				final GEPolygon p = (GEPolygon) SelectList.get(0);
				final double x1 = catchX + vx1;
				final double y1 = catchY + vy1;
				p.draw(g2, false, false, vx1, vy1, true, x1, y1, vangle);
				if ((STATUS == 2) && (FirstPnt != null)) {
					g2.setColor(Color.red);
					g2.drawLine((int) CatchPoint.getx(),
							(int) CatchPoint.gety(), (int) x1, (int) y1);
					drawCross((int) FirstPnt.getx(), (int) FirstPnt.gety(), 2,
							g2);
					if (ThirdPnt != null)
						drawCross((int) (ThirdPnt.getx() + vx1),
								(int) (ThirdPnt.gety() + vy1), 2, g2);
				}
			}
		}
		break;
		case MOVE: {

		}
		break;

		default:
			if (isPointOnObject)
				drawCatchRect(g2);
			break;
		}
		drawCatchObjName(g2);
	}

	public void setFirstPnt(final double x, final double y) {
		if (FirstPnt != null) {
			vtrx = x - FirstPnt.getx();
			vtry = y - FirstPnt.gety();
		}
	}

	public void setTransformStatus(final int t) {
		if (t == 0) {
			clearSelection();
			FirstPnt = SecondPnt = ThirdPnt = null;
		} else if (t == 1) {

		} else if (t == 2) {
			if (FirstPnt != null) {
				final GEPoint t1 = FirstPnt;
				t1.setXY(catchX + vx1, catchY + vy1);
				repaint();
			}
		}
		STATUS = t;
	}

	public void repaint() {
		panel.repaint();
	}

	static double[] getFootPosition(final double xc, final double yc,
			final double x1, final double y1, final double x2, final double y2) {
		final double dlx = x2 - x1;
		final double dly = y2 - y1;
		final double dl = (dlx * dlx) + (dly * dly);

		final double x = (((y2 - yc) * dlx * dly) + (dly * dly * xc) + (dlx
				* dlx * x2))
				/ dl;
		final double y = (((x2 - xc) * dlx * dly) + (dlx * dlx * yc) + (dly
				* dly * y2))
				/ dl;
		final double[] n = new double[2];
		n[0] = x;
		n[1] = y;
		return n;
	}

	static double[] getPTInterSection(final double x, final double y,
			final double xa, final double ya, final double x1, final double y1,
			final double x2, final double y2) {
		final double k = (y2 - y1) / (x2 - x1);
		double xt = ((y - ya) + (k * xa) + (x / k)) / (k + (1 / k));
		double yt = ((x - xa) + (ya / k) + (k * y)) / (k + (1 / k));
		if (Math.abs(1 / k) > UtilityMiscellaneous.MAX_SLOPE) {
			xt = x;
			yt = ya;
		}
		final double[] n = new double[2];
		n[0] = xt;
		n[1] = yt;
		return n;
	}

	public void addFlashPolygon(final GEPolygon p1, final GEPolygon p2,
			final int t, final boolean ct, final double xc, final double yc) {
		final int n = p1.getPtn();
		if (n != p2.getPtn())
			return;
		final FlashPolygon f = new FlashPolygon(panel, p1, p2, ct, xc, yc,
				p1.getColorIndex(), p2.getColorIndex(), t);
		addFlash2(f);
	}

	public static void drawAuxLine(final int x, final int y, final double k,
			final Graphics2D g2) {
		g2.setColor(Color.red);
		g2.setStroke(UtilityMiscellaneous.DashedStroke);
		final double max = UtilityMiscellaneous.MAX_DRAW_LEN;
		if (Math.abs(k) > UtilityMiscellaneous.MAX_K)
			g2.drawLine(x, 0, x, (int) max);
		else if ((k < 1) && (k > -1))
			g2.drawLine(0, (int) (y - (k * x)), (int) max,
					(int) (y + (k * (max - x))));
		else
			g2.drawLine((int) (x - (y / k)), 0, (int) (x + ((max - y) / k)),
					(int) max);
	}

	public GraphicEntity getFirstCatchObject() {
		if (CatchList.isEmpty())
			return null;
		return CatchList.get(0);
	}

	// ./public double get
	public boolean addisoAngle(final GEPoint p1, final GEPoint p2,
			final GEPoint p, final int type) {

		final double x0 = p1.getx();
		final double y0 = p1.gety();
		final double rx = p2.getx() - x0;
		final double ry = p2.gety() - y0;
		final double dx = p.getx() - x0;
		final double dy = p.gety() - y0;
		final double rr = Math.sqrt((rx * rx) + (ry * ry));
		final double cy = ry / rr;
		final double cx = rx / rr;
		double r;
		boolean isleft = false;

		if (Math.abs(rx) < UtilityMiscellaneous.ZERO) {
			if ((ry * (p1.getx() - p.getx())) > 0)
				isleft = false;
			else
				isleft = true;
			r = Math.abs(p1.getx() - p.getx());
		} else {
			final double k = ry / rx;
			r = Math.abs((((p.gety() - (k * p.getx())) + (k * p1.getx())) - p1
					.gety())) / Math.sqrt(1 + (k * k));
			isleft = (((rx * dy) - (ry * dx)) < 0);
		}

		final double x1 = (x0 + p2.getx()) / 2;
		final double y1 = (y0 + p2.gety()) / 2;

		double x2, y2;
		if (isleft) {
			x2 = (x1 + (r * cy));
			y2 = (y1 - (r * cx));
		} else {
			x2 = x1 - (r * cy);
			y2 = y1 + (r * cx);
		}

		final GELine ln = findLineGivenTwoPoints(p1, p2);
		p.setXY(x2, y2);
		if ((ln != null) && ln.nearline(x2, y2))
			return false;

		final GEPoint pt = SmartgetApointFromXY(x2, y2);
		GELine line = null;

		if (type == 0) { // iso
			final Constraint cs = new Constraint(Constraint.ISO_TRIANGLE, pt,
					p1, p2);
			addConstraintToList(cs);
			line = new GELine(pt, p1);
			addLineToList(line);
			line = new GELine(pt, p2);
			addLineToList(line);
			if (!doesLineBetweenTwoPointsExist(p1, p2)) {
				final GELine lp = new GELine(p1, p2);
				addLineToList(lp);
			}
			characteristicSetMethodAndAddPoly(false);
			UndoAdded("isoceles triangle " + p1.m_name + p2.m_name + pt.m_name);
		}

		return true;
	}

	public boolean addsquare(GEPoint p1, GEPoint p2, final GEPoint p) {
		GEPoint t1, t2;
		t1 = p1;
		t2 = p2;

		final double x0 = p1.getx();
		final double y0 = p1.gety();

		final double rx = p2.getx() - x0;
		final double ry = p2.gety() - y0;
		final double dx = p.getx() - x0;
		final double dy = p.gety() - y0;
		final double rr = Math.sqrt((rx * rx) + (ry * ry));
		double r;
		boolean isleft = false;
		if (Math.abs(rx) < UtilityMiscellaneous.ZERO) {
			if ((ry * (p1.getx() - p.getx())) > 0)
				isleft = false;
			else
				isleft = true;
			r = Math.abs(p1.getx() - p.getx());
		} else {
			final double k = ry / rx;
			r = Math.abs((((p.gety() - (k * p.getx())) + (k * p1.getx())) - p1
					.gety())) / Math.sqrt(1 + (k * k));
			isleft = (((rx * dy) - (ry * dx)) < 0); // ((ry * dx / rx - dy > 0
			// && ry / rx > 0) || (ry *
			// dx / rx - dy < 0 && ry /
			// rx < 0));
		}

		final int n = (int) (0.5 + (r / rr));
		if (Math.abs((n * rr) - r) > (2 * UtilityMiscellaneous.PIXEPS))
			return false;
		if (n == 0)
			return false;

		if (!doesLineBetweenTwoPointsExist(p1, p2)) {
			final GELine lp = new GELine(p1, p2);
			addLineToList(lp);
		}

		GEPoint pa1, pa2;
		pa1 = pa2 = null;

		// CLine line1 = new CLine(p1, CLine.LLine);
		// CLine line2 = new CLine(p2, CLine.LLine);

		Constraint cs1, cs2;
		for (int i = 0; i < n; i++) {
			pa1 = CreateANewPoint(0, 0);
			pa2 = CreateANewPoint(0, 0);
			GEPoint tp1, tp2;
			if (isleft) {
				cs2 = new Constraint(Constraint.NSQUARE, pa2, p2, p1);
				tp2 = addADecidedPointWithUnite(pa2);
				if (tp2 == null) {
					addConstraintToList(cs2);
					addPointToList(pa2);
				} else
					pa2 = tp2;

				cs1 = new Constraint(Constraint.PSQUARE, pa1, p1, p2);
				tp1 = addADecidedPointWithUnite(pa1);
				if (tp1 == null) {
					addConstraintToList(cs1);
					addPointToList(pa1);
				} else
					pa1 = tp1;

				addCTMark(p1, pa1, p2, pa1);
				final Constraint cs = new Constraint(Constraint.SQUARE, p1, p2,
						pa2, pa1);
				addConstraintToList(cs);

			} else {
				cs2 = new Constraint(Constraint.PSQUARE, pa2, p2, p1);
				tp2 = addADecidedPointWithUnite(pa2);
				if (tp2 == null) {
					addConstraintToList(cs2);
					addPointToList(pa2);
				} else
					pa2 = tp2;
				cs1 = new Constraint(Constraint.NSQUARE, pa1, p1, p2);
				tp1 = addADecidedPointWithUnite(pa1);
				if (tp1 == null) {
					addConstraintToList(cs1);
					addPointToList(pa1);
				} else
					pa1 = tp1;
				addCTMark(p1, pa1, p2, pa1);
				final Constraint cs = new Constraint(Constraint.SQUARE, p1, p2,
						pa2, pa1);
				addConstraintToList(cs);
			}

			// AddPointToLineX(pa1,line1);
			// AddPointToLineX(pa2,line2);
			add_line(p1, pa1);
			add_line(p2, pa2);
			add_line(pa1, pa2);
			addCTMark(findLineGivenTwoPoints(p1, p2), findLineGivenTwoPoints(p1, pa1));
			UndoAdded("SQUARE " + p1 + p2 + pa2 + pa1);
			p1 = pa1;
			p2 = pa2;
		}
		// addLineToList(line1);
		// addLineToList(line2);
		if ((pa1 != null) && (pa2 != null) && (findLineGivenTwoPoints(pa1, pa2) == null)) {
			final GELine line = new GELine(pa1, pa2);
			addLineToList(line);
		}
		UndoAdded("square " + t1.m_name + t2.m_name + pa2.m_name + pa1.m_name);

		return true;
	}

	public void add_line(final GEPoint p1, final GEPoint p2) {
		GELine ln = null;
		if ((ln = findLineGivenTwoPoints(p1, p2)) != null) {

			addPointToLine(p1, ln);
			addPointToLine(p2, ln);
			return;
		}
		ln = new GELine(p1, p2);
		addLineToList(ln);
	}

	public static void drawTrackPoint(final GEPoint p, final Graphics2D g2) {
		final int x = (int) p.getx();
		final int y = (int) p.gety();
		g2.setStroke(UtilityMiscellaneous.NormalLineStroke);
		g2.setColor(Color.white);
		g2.fillOval(x - 5, y - 5, 10, 10);
		g2.setColor(Color.black);
		g2.drawOval(x - 5, y - 5, 10, 10);
		p.prepareToBeDrawnAsUnselected(g2);

		g2.fillOval(x - 3, y - 3, 6, 6);

	}

	public static void drawselectPoint(final GEPoint p, final Graphics2D g2) {
		final int x = (int) p.getx();
		final int y = (int) p.gety();

		g2.setColor(UtilityMiscellaneous.SelectObjectColor);
		g2.fillOval(x - 7, y - 7, 14, 14);
	}

	public GEPoint SmartPoint(final GEPoint p) {
		final GEPoint pt = SelectAPoint((int) p.getx(), (int) p.gety());
		if (pt != null) {
			p.setXY(pt.getx(), pt.gety());
			return pt;
		}
		return null;
	}

	public GELine SmartPLine(final GEPoint p) {
		final GELine line = SmartPointOnLine(p.getx(), p.gety());
		if (line != null) {
			line.pointonline(p);
			return line;
		}
		return null;
	}

	public GECircle SmartPCircle(final GEPoint p) {
		for (final GECircle c : circlelist)
			if (c.visible() && c.nearcircle(p.getx(), p.gety(), UtilityMiscellaneous.PIXEPS)) {
				c.SmartPonc(p);
				return c;
			}
		return null;
	}

	/**
	 * Returns the <code>GELine</code> that is closest on the screen to the point (x,y).
	 * @param x
	 * @param y
	 * @return
	 */
	public GELine SmartPointOnLine(final double x, final double y) {
		double dis = Double.MAX_VALUE;
		GELine ln = null;
		for (final GELine line : linelist) {
			if (line.visible() && line.inside(x, y, UtilityMiscellaneous.PIXEPS)) {
				double d = GELine.distanceToPoint(line, x, y);
				if (d < dis) {
					dis = d;
					ln = line;
				}
			}
		}
		if (dis < UtilityMiscellaneous.PIXEPS)
			return ln;
		else
			return null;
	}

	public GEPoint CreateANewPoint(final double x, final double y) {
		if (paraCounter > 1023) {
			UtilityMiscellaneous.print("point overflow.");
			return null;
		}

		final param p1 = parameter[paraCounter - 1] = new param(paraCounter++, x);
		final param p2 = parameter[paraCounter - 1] = new param(paraCounter++, y);
		return new GEPoint(p1, p2);
	}

	public GEPoint createIntersectionPoint(final GELine line1, final GELine line2) {
		assert(line1 != null);
		assert(line2 != null);
		if (line1 != null && line2 != null && !line1.isCoincidentWith(line2)) {

			if (check_para(line1, line2)) {
				JOptionPane.showMessageDialog(gxInstance, DrawPanelFrame.getLanguage(1028,
						"The two lines you selected are parallel" + ", and thus don't intersect."),
						DrawPanelFrame.getLanguage(1029, "No intersection"),
						JOptionPane.ERROR_MESSAGE);
				return null;
			}

			final int size1 = line1.getPtsSize();
			final int size2 = line2.getPtsSize();

			if ((size1 <= 1) || (size2 <= 1)) {
				GEPoint p = CreateANewPoint(0, 0);

				addPointToLine(p, line1, false);
				addPointToLine(p, line2, false);

				characteristicSetMethodAndAddPoly(false);

				final GEPoint tp = addADecidedPointWithUnite(p);
				if (tp != null) {
					line2.points.remove(p);
					line1.points.remove(p);
					line2.add(tp);
					line1.add(tp);
					p = tp;
				} else
					addPointToList(p);
				reCalculate();
				UndoAdded(p.m_name + ": intersection of " + line1.getDescription2() + " and " + line2.getDescription2());
				return p;
			} else {
				GEPoint p = CreateANewPoint(0, 0);
				final Constraint cs1 = new Constraint(Constraint.INTER_LL, p, line1, line2);
				final GEPoint tp = addADecidedPointWithUnite(p);
				// poly.printpoly(p.x1.m);
				// poly.printpoly(p.y1.m);
				if (tp != null) {
					line2.add(tp);
					line1.add(tp);
					p = tp;
				} else {
					addPointToList(p);
					addConstraintToList(cs1);
					line1.add(p);
					line2.add(p);
					// charsetAndAddPoly(false);
				}
				UndoAdded(p.m_name + ": intersection of " + line1.getDescription2()
						+ " and " + line2.getDescription2());
				return p;
			}
		}
		return null;
	}

	public GEPoint MeetLCToDefineAPoint(final GELine line, final GECircle c,
			final boolean m, final double x, final double y) {
		GEPoint p = null;
		GEPoint p1 = null;

		if (!check_lc_inter(line, c)) {
			JOptionPane
			.showMessageDialog(
					gxInstance,
					DrawPanelFrame.getLanguage(1030,
							"The line and the circle you selected don't have any intersection"),
							DrawPanelFrame.getLanguage(1029, "No intersection"),
							JOptionPane.ERROR_MESSAGE);
			return null;
		}
		for (int i = 0; i < line.getPtsSize(); i++) {
			final Object obj = line.points.get(i);
			if (c.p_on_circle((GEPoint) obj))
				if (p == null)
					p = (GEPoint) obj;
				else
					p1 = (GEPoint) obj;
		}
		if ((p1 != null) && (p != null)) {
			JOptionPane
			.showMessageDialog(
					gxInstance,
					DrawPanelFrame.getLanguage(1031,
							"The two objects you selected already have two points as their intersections"),
							DrawPanelFrame.getLanguage(1032,
									"intersection already defined"),
									JOptionPane.WARNING_MESSAGE);
			return null;
		}

		if ((line.getPtsSize() < 2) || (c.points.size() == 0)) {
			final GEPoint pt = CreateANewPoint(0, 0);
			final Constraint cs1 = new Constraint(Constraint.PONCIRCLE, pt, c);
			addPointToLine(pt, line, false);
			if (m)
				pt.setXY(x, y);
			characteristicSetMethodAndAddPoly(true);
			if (m || mulSolutionSelect(pt)) {
				addConstraintToList(cs1);
				c.add(pt);
				addPointToList(pt);
				UndoAdded(pt.m_name + ": intersection of "
						+ line.getDescription2() + " and " + c.getDescription());
			} else {
				ErasedADecidedPoint(pt);
				line.points.remove(pt);
				gxInstance.setTipText("Line " + line.m_name + "  and Circle "
						+ c.m_name + "  can not intersect");
			}
			return pt;
		}

		final GEPoint pout = CreateANewPoint(0, 0);
		addPointToList(pout);
		final Constraint css = new Constraint(Constraint.INTER_LC, pout, line, c);
		characteristicSetMethodAndAddPoly(false);
		if (m)
			pout.setXY(x, y);

		// constraint cs1 = new constraint(constraint.PONLINE, pout, line,
		// false);
		// constraint cs2 = new constraint(constraint.PONCIRCLE, pout, c,
		// false);
		line.add(pout);
		c.add(pout);
		addPointToList(pout);
		addConstraintToList(css);
		// addConstraintToList(cs1);
		// addConstraintToList(cs2);

		UndoAdded(pout.m_name + ": intersection of " + line.getDescription2()
				+ " and " + c.getDescription());
		reCalculate();
		return pout;
	}

	public GEPoint MeetCCToDefineAPoint(final GECircle c1, final GECircle c2,
			final boolean m, final double x, final double y) {
		GEPoint p = null;
		GEPoint p1 = null;

		if (!check_cc_inter(c1, c2)) {
			JOptionPane.showMessageDialog(gxInstance, DrawPanelFrame.getLanguage(1033,
					"The circles you selected don't have any intersection"),
					DrawPanelFrame.getLanguage(1029, "No intersection"),
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		for (int i = 0; i < c1.points.size(); i++) {
			final Object obj = c1.points.get(i);
			if (c2.p_on_circle((GEPoint) obj))
				if (p == null)
					p = (GEPoint) obj;
				else
					p1 = (GEPoint) obj;
		}
		if ((p1 != null) && (p != null)) {
			JOptionPane
			.showMessageDialog(
					gxInstance,
					DrawPanelFrame.getLanguage(1034,
							"The two circles you selected already have two points as their intersections"),
							DrawPanelFrame.getLanguage(1032,
									"intersection already defined"),
									JOptionPane.WARNING_MESSAGE);
			return null;
		}

		if (p == null) {
			final GEPoint pt = CreateANewPoint(0, 0);
			// constraint cs = new constraint(constraint.PONCIRCLE, pt, c1);
			// constraint cs1 = new constraint(constraint.PONCIRCLE, pt, c2);
			final Constraint cs = new Constraint(Constraint.INTER_CC, pt, c1,
					c2);
			if (m)
				pt.setXY(x, y);
			characteristicSetMethodAndAddPoly(true);
			if (m || mulSolutionSelect(pt)) {
				addConstraintToList(cs);
				// addConstraintToList(cs1);
				c1.add(pt);
				c2.add(pt);
				addPointToList(pt);
				UndoAdded(pt.m_name + ": intersection of "
						+ c1.getDescription() + " and " + c2.getDescription());
			} else {
				ErasedADecidedPoint(pt);
				gxInstance.setTipText("Circle " + c1.m_name + "  and Circle "
						+ c2.m_name + "  can not intersect");
			}

			return pt;
		}
		final GEPoint pt = CreateANewPoint(0, 0);
		// constraint cs = new constraint(constraint.INTER_CC1, pt, p, c1, c2);

		// constraint cs2 = new constraint(constraint.PONCIRCLE, pt, c1, false);
		// constraint cs3 = new constraint(constraint.PONCIRCLE, pt, c2, false);
		final Constraint cs = new Constraint(Constraint.INTER_CC, pt, c1, c2);

		final GEPoint pu = addADecidedPointWithUnite(pt);
		if (pu == null) {
			addConstraintToList(cs);
			// addConstraintToList(cs2);
			// addConstraintToList(cs3);
			addPointToList(pt);
			c1.add(pt);
			c2.add(pt);
			characteristicSetMethodAndAddPoly(false);
			reCalculate();
			UndoAdded(pt.m_name + ": intersection of " + c1.getDescription()
					+ " and " + c2.getDescription());
		} else
			resetUndo();
		return pt;
	}

	private void addPointToLineX(final GEPoint p, final GELine ln) {
		assert(p != null);
		assert(ln != null);

		if (!ln.isCoincidentWith(p)) {
			ln.add(p);
			final Constraint cs = new Constraint(Constraint.PONLINE, p, ln, false);
			p.addConstraint(cs);
			addConstraintToList(cs);
		}
	}

	private void addPointToCircle(final GEPoint p, final GECircle c, final boolean bAddUndo) {
		assert(p != null);
		assert(c != null);

		c.add(p);
		final Constraint cs = new Constraint(Constraint.PONCIRCLE, p, c);
		addConstraintToList(cs);
		p.addConstraint(cs);
		if (bAddUndo)
			UndoAdded(p.TypeString() + " on " + c.getDescription());
	}

	//	private void addPointToCircle(final GEPoint p, final GECircle c) {
	//		addPointToCircle(p, c, true);
	//	}

	private void addPointToLine(final GEPoint p, final GELine line) {
		addPointToLine(p, line, true);
	}

	public void addPointToLine(final GEPoint p, final GELine line, final boolean bAddUndo) {
		assert(p != null);
		assert(line != null);

		if (!line.isCoincidentWith(p)) {

			line.add(p);

			if ((line.getPtsSize() > 2) || line.linetype == GELine.CCLine) {
				final Constraint cs = new Constraint(Constraint.PONLINE, p, line);
				addConstraintToList(cs);
				line.addConstraint(cs);
				p.addConstraint(cs);
			} else {
				switch (line.linetype) {
				case GELine.PLine: {
					Constraint cs = new Constraint(Constraint.PONLINE, p, line, false);
					addConstraintToList(cs);

					final Constraint cs1 = line.getConstraintByType(Constraint.PARALLEL);
					if (cs1 == null)
						break;
					cs1.PolyGenerate();
					p.addConstraint(cs);
					break;
				}
				case GELine.TLine: {
					final Constraint cs = new Constraint(Constraint.PONLINE, p, line, false);
					addConstraintToList(cs);

					final Constraint cs1 = line.getConstraintByType(Constraint.PERPENDICULAR);
					if (cs1 == null)
						break;
					cs1.PolyGenerate();
					p.addConstraint(cs);
					break;
				}
				case GELine.BLine: {
					Constraint cs = new Constraint(Constraint.PONLINE, p, line, false);
					addConstraintToList(cs);

					final Constraint cs1 = line.getConstraintByType(Constraint.BLINE);
					if (cs1 == null)
						break;
					cs1.PolyGenerate();
					break;
				}
				case GELine.ALine: {
					final Constraint cs = new Constraint(Constraint.PONLINE, p, line, false);
					addConstraintToList(cs);

					final Constraint cs1 = line.getConstraintByType(Constraint.ALINE);
					if (cs1 == null)
						break;
					cs1.setPolyGenerate(true);
					cs1.PolyGenerate();
					break;
				}
				case GELine.NTALine: {
					final Constraint cs1 = line.getConstraintByType(Constraint.NTANGLE);
					if (cs1 == null)
						break;
					final ArrayList<Object> v = new ArrayList<Object>();
					cs1.getAllElements(v);
					v.add(p);
					final Constraint cs = new Constraint(Constraint.NTANGLE, v);
					cs.PolyGenerate();
					addConstraintToList(cs);
					break;
				}
				case GELine.SLine: {
					final Constraint cs = new Constraint(Constraint.PONLINE, p, line, false);
					addConstraintToList(cs);

					final Constraint cs1 = line.getConstraintByType(Constraint.SANGLE);
					if (cs1 == null)
						break;
					line.add(p);
					cs1.PolyGenerate();
					addConstraintToList(cs1);
					break;
				}

				case GELine.ABLine:
				case GELine.TCLine: {
					final Constraint cs = new Constraint(Constraint.PONLINE, p, line, false);
					addConstraintToList(cs);

					final Constraint cs1 = line.getFirstConstraint();
					if (cs1 == null)
						break;
					line.add(p);
					cs1.PolyGenerate();
					p.addConstraint(cs1);
					addConstraintToList(cs1);
					break;
				}
				default:
					return;
				}
			}
			if (bAddUndo)
				UndoAdded(p.getDescription());
		}
	}

	public void GeneratePoly(final Constraint cs) {
		if (cs == null)
			return;
		cs.PolyGenerate();
		final TPoly pl = Constraint.getPolyListAndSetNull();
		TPoly tp = pl;
		while (tp.getNext() != null)
			tp = tp.getNext();
		tp.setNext(polylist);
		polylist = pl;
	}

	public GEPoint addADecidedPointWithUnite(final GEPoint p) {
		assert(p != null);
		addGraphicEntity(pointlist, p);
		characteristicSetMethodAndAddPoly(false);
		GEPoint pp = pointlist.get(pointlist.size() - 1);
		removeGraphicEntity(pointlist, pp);

		final GEPoint tp = checkCommonPoint(p);

		if (tp != null) {
			setVariable();
			eraseAPoly(p.x1.m);
			eraseAPoly(p.y1.m);
			UtilityMiscellaneous.showMessage("Point " + tp.m_name + " already exists");
		}
		return tp;
	}

	public void eraseAPoly(final TMono m) {
		TPoly t1 = null;
		TPoly t = polylist;
		while (t != null) {
			if (t.poly == m)
				if (t1 == null)
					polylist = polylist.next;
				else
					t1.next = t.next;
			t1 = t;
			t = t.getNext();
		}
	}

	/** 
	 * Determines whether GEPoint p is guaranteed by the geometric constraints to be colocated with
	 * one of the points in the geometric construction.
	 * @param p   The potentially new point being checked for redundancy
	 * @return    One of the GEPoints that makes p redundant; if p does not exist already, returns null.
	 */
	public GEPoint checkCommonPoint(final GEPoint p) { // geometrically coincident.

		TPoly plist = polylist;
		while (plist != null) {
			final TMono mm = plist.getPoly();
			final int v = PolyBasic.lv(mm);
			if (p.x1.xindex == v)
				p.x1.m = mm;
			else if (p.y1.xindex == v)
				p.y1.m = mm;
			plist = plist.getNext();
		}

		if (p.x1.m == null || p.y1.m == null)
			return null;

		for (final GEPoint t : pointlist) {
			if (t != p && p.isAtSameLocationAs(t.getx(), t.gety()) && decide_wu_identical(p, t))
				return t;
		}
		return null;
	}

	/** 
	 * Determines whether GEPoint p is by happenstance colocated with
	 * one of the points in the geometric construction.
	 * @param p   The potentially new point being checked for redundancy
	 * @return    One of the GEPoints that makes p redundant; if p does not exist already, returns null.
	 */
	public GEPoint isColocatedWithAnotherExistingPoint(final GEPoint p) { // physically coincident
		for (final GEPoint t : pointlist) {
			if (t != p && p.isAtSameLocationAs(t.getx(), t.gety()))
				return t;
		}
		return null;
	}

	public void setVariable() {
		TPoly plist = polylist;
		while (plist != null) {
			final int v = PolyBasic.lv(plist.getPoly());
			for (int i = 1; i < paraCounter; i++)
				if (((parameter[i] != null) && (parameter[i].xindex == v))) {
					parameter[i].Solved = true;
					parameter[i].m = plist.getPoly();
					break;
				}
			plist = plist.getNext();
		}

		for (final GEPoint p : pointlist)
			p.setColorDefault();
	}

	/*
	 * public TPoly add_cons_or_conclusion(int type, Object obj1, Object obj2,
	 * Object obj3, Object obj4) {
	 * 
	 * if (type == constraint.PARALLEL || type == constraint.PERPENDICULAR) {
	 * CPoint p1 = (CPoint) obj1; CPoint p2 = (CPoint) obj2; CPoint p3 =
	 * (CPoint) obj3; CPoint p4 = (CPoint) obj4; CLine line1, line2; line1 =
	 * line2 = null; for (int i = 0; i < linelist.size(); i++) { CLine ln =
	 * (CLine) linelist.get(i); if (ln.points.contains(p1) &&
	 * ln.points.contains(p2)) { if (line1 == null) { line1 = ln; } else { line2
	 * = ln; } } if (ln.points.contains(p3) && ln.points.contains(p4)) { if
	 * (line1 == null) { line1 = ln; } else { line2 = ln; } } } constraint cs =
	 * new constraint(type, line1, line2, null, null); } else { constraint cs =
	 * new constraint(type, obj1, obj2, obj3, obj4); }
	 * 
	 * TPoly plist = constraint.getPolyListAndSetNull(); return plist;
	 * 
	 * }
	 */

	public GEPolygon SelectAPolygon(final double x, final double y) {
		final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
		SelectFromAList(v, polygonlist, x, y);
		if (v.size() > 1)
			return (GEPolygon) popSelect(v, (int) x, (int) y);
		else if (v.size() == 1)
			return (GEPolygon) v.get(0);
		else
			return null;
	}

	public static GraphicEntity selectFromList(final ArrayList<? extends GraphicEntity> list, final double x, final double y) {
		if (list != null) {
			for (final GraphicEntity ge : list) {
				if (ge.isLocatedNear(x, y))
					return ge;
			}
		}
		return null;
	}

	public void changeFontSizeOfNameText(final int n) {
		for (final GEText t : textlist)
			if (t.getType() == GEText.NAME_TEXT && t.getFontSize() <= 5)
				return;

		for (final GEText t : textlist)
			if (t.getType() == GEText.NAME_TEXT)
				t.changeFontSize(n);

		panel.repaint();
	}

	public void re_generate_all_poly() {
		polylist = pblist = null;
		final ArrayList<Constraint> v = constraintlist;
		constraintlist = new ArrayList<Constraint>();
		GeoPoly.clearZ();
		optimizePolynomial();
		for (final Constraint cs : v) {
			if (cs.bPolyGenerate) {
				cs.clear_all_cons();
				cs.PolyGenerate();
				characteristicSetMethodAndAddPoly(true);
			}
			addConstraintToList(cs);
		}
	}

	public static DataOutputStream openOutputFile(final String path) throws IOException {
		final File f = new File(path);
		final boolean bExists = f.exists();
		if (bExists)
			f.delete();
		else
			f.createNewFile();
		final FileOutputStream fp = new FileOutputStream(f, bExists);
		final DataOutputStream out = new DataOutputStream(fp);
		return out;
	}

	//	boolean Save(final String name) throws IOException {
	//		final boolean r = Save(openOutputFile(name));
	//		file = new File(name);
	//		needSave = false;
	//		return r;
	//	}

	public void openFromXMLDocument(Element thisElement) {
		assert(thisElement != null);
		if (thisElement != null) {

			boolean bDocumentSemanticallyValid = true;

			docVersion = DrawPanelFrame.safeParseInt(thisElement.getAttribute("version"), 0);
			CurrentAction = MOVE;
			GridX = DrawPanelFrame.safeParseInt(thisElement.getAttribute("GridX"), GridX, 10, 160);
			GridY = DrawPanelFrame.safeParseInt(thisElement.getAttribute("GridY"), GridY, 10, 160);
			DRAWGRID = DrawPanelFrame.safeParseBoolean(thisElement.getAttribute("GridVisible"), false);
			SNAP = DrawPanelFrame.safeParseBoolean(thisElement.getAttribute("SnapToGrid"), false);

			HashMap<Integer, GraphicEntity> mapGE = new HashMap<Integer, GraphicEntity>();
			HashMap<Integer, Constraint> mapC = new HashMap<Integer, Constraint>();

			NodeList nlist = thisElement.getChildNodes();
			if (nlist != null) {
				for (int i = 0; i < nlist.getLength(); ++i) {
					Node nn = nlist.item(i);
					if (nn != null) {
						String sEntry = nn.getNodeName();
						if (sEntry.equalsIgnoreCase("points")) {
							pnameCounter = DrawPanelFrame.safeParseInt(nn.getTextContent(), -1);
							continue;
						}
						if (sEntry.equalsIgnoreCase("lines")) {
							plineCounter = DrawPanelFrame.safeParseInt(nn.getTextContent(), -1);
							continue;
						}
						if (sEntry.equalsIgnoreCase("circles")) {
							pcircleCounter = DrawPanelFrame.safeParseInt(nn.getTextContent(), -1);
							continue;
						}
						if (sEntry.equalsIgnoreCase("proof")) {
							cpfield = new ProofField((Element)nn);
							continue;
						}
						if (sEntry.equalsIgnoreCase("parameters")) {
							NodeList n2list = nn.getChildNodes();
							if (n2list != null) {
								//	System.out.println("Parameter " + parameter[0].xindex + " already had value " + parameter[0].value );
								parameter = new param[1024];
								paraCounter = 0;
								for (int i2 = 0; i2 < n2list.getLength(); ++i2) {
									Node nn2 = n2list.item(i2);
									assert(nn2 != null);
									if (nn2 != null && nn2 instanceof Element) {
										parameter[paraCounter++] = new param((Element)nn2);
										//System.out.println("Parameter " + paraCounter + " with index " + p.xindex + " loaded with value " + p.value );
									}
								}
								//System.out.println("Parameters loaded = " + paraCounter);
							}
							continue;
						}
						if (sEntry.equalsIgnoreCase("backup_parameters")) {
							NodeList n2list = nn.getChildNodes();
							if (n2list != null) {
								paraBackup = new double[1024];
								int paramindex = 0;
								for (int i2 = 0; i2 < n2list.getLength(); ++i2) {
									Node nn2 = n2list.item(i2);
									if (nn2 != null && nn2 instanceof Element) {
										paraBackup[paramindex++] = DrawPanelFrame.safeParseDouble(nn2.getTextContent(), 0.0d);
										//System.out.println("Parameter backup" + paramindex + " loaded with value " + d );
									}
								}
							}
							continue;
						}
						if (sEntry.equalsIgnoreCase("geometric_entities")) {
							NodeList n2list = nn.getChildNodes();
							if (n2list != null) {
								for (int i2 = 0; i2 < n2list.getLength() && bDocumentSemanticallyValid; ++i2) {
									Node nn2 = n2list.item(i2);
									if (nn2 != null && nn2 instanceof Element) {
										String sType = nn2.getNodeName();
										//Integer I = idFactory.createNewID();
										//assert(mapGE.containsKey(I));
										GraphicEntity ge = null;
										if (sType.equalsIgnoreCase("point")) {
											GEPoint pNew = new GEPoint(this, (Element)nn2);
											bDocumentSemanticallyValid &= pNew.isValid();
											addPointToList(pNew);
											ge = pNew;
											int index = ge.id();
											if (index <= 0 || mapGE.containsKey(index)) {
												bDocumentSemanticallyValid = false;
											} else {
												mapGE.put(index, ge);
											}
										}
									}
								}
								for (int i2 = 0; i2 < n2list.getLength() && bDocumentSemanticallyValid; ++i2) {
									Node nn2 = n2list.item(i2);
									if (nn2 != null && nn2 instanceof Element) {
										String sType = nn2.getNodeName();
										GraphicEntity ge = null;
										if (sType.equalsIgnoreCase("line")) {
											GELine geNew = new GELine(this, (Element)nn2, mapGE);
											bDocumentSemanticallyValid &= geNew.isValid();
											addLineToList(geNew);
											ge = geNew;
											int index = ge.id();
											if (index <= 0 || mapGE.containsKey(index)) {
												bDocumentSemanticallyValid = false;
											} else {
												mapGE.put(index, ge);
											}
										}
									}
								}
								for (int i2 = 0; i2 < n2list.getLength() && bDocumentSemanticallyValid; ++i2) {
									Node nn2 = n2list.item(i2);
									if (nn2 != null && nn2 instanceof Element) {
										String sType = nn2.getNodeName();

										//Integer I = idFactory.createNewID();
										//assert(mapGE.containsKey(I));
										GraphicEntity ge = null;
										if (sType.equalsIgnoreCase("point") || sType.equalsIgnoreCase("line")) {
											continue;
										}
										if (sType.equalsIgnoreCase("circle")) {
											GECircle geNew = new GECircle(this, (Element)nn2, mapGE);
											bDocumentSemanticallyValid &= geNew.isValid();
											addCircleToList(geNew);
											ge = geNew;
										}
										if (sType.equalsIgnoreCase("angle")) {
											GEAngle geNew = new GEAngle(this, (Element)nn2, mapGE);
											addAngleToList(geNew);
											ge = geNew;
										}
										if (sType.equalsIgnoreCase("distance")) {
											GEDistance geNew = new GEDistance(this, (Element)nn2, mapGE);
											addGraphicEntity(distancelist, geNew);
											ge = geNew;
										}
										if (sType.equalsIgnoreCase("polygon")) {
											GEPolygon geNew = new GEPolygon(this, (Element)nn2, mapGE);
											bDocumentSemanticallyValid &= geNew.isValid();
											addPolygonToList(geNew);
											ge = geNew;
										}
										if (sType.equalsIgnoreCase("text")) {
											GEText geNew = new GEText(this, (Element)nn2, mapGE);
											addGraphicEntity(textlist, geNew);
											ge = geNew;
										}
										if (sType.equalsIgnoreCase("trace")) {
											GETrace geNew = new GETrace(this, (Element)nn2, mapGE);
											addGraphicEntity(tracelist, geNew);
											ge = geNew;
										}
										if (sType.equalsIgnoreCase("other")) {
											System.out.println("File contained an unknown type of entity. It will be ignored.");
											//											GraphicEntity geNew = new GraphicEntity(this, (Element)nn2); // GraphicEntity is an abstract class and cannot serve as a generic entity.
											//											otherlist.add(geNew);
											//											ge = geNew;
										}
										if (ge == null) {
											System.err.println("Loaded file had an unknown entry type: "+sType+".");
										}
										if (ge != null && ge.isValid()) {
											Integer I = ge.id();
											assert(!mapGE.containsKey(I));
											if (mapGE.containsKey(I)) {
												System.err.println("Loaded file had multiple entities with the same ID (" + I + ").");
												bDocumentSemanticallyValid = false;
											}
											mapGE.put(I, ge);
										}
									}
								}
							}
							continue;
						}
					}
				}
				for (int i = 0; i < nlist.getLength(); ++i) {
					Node nn = nlist.item(i);
					if (nn != null && nn instanceof Element) {
						String sEntry = nn.getNodeName();
						if (sEntry.equalsIgnoreCase("constraints")) {
							NodeList n2list = nn.getChildNodes();
							if (n2list != null) {
								for (int i2 = 0; i2 < n2list.getLength() && bDocumentSemanticallyValid; ++i2) {
									Node nn2 = n2list.item(i2);
									if (nn2 != null && nn2 instanceof Element) {
										String sType = nn2.getNodeName();
										if (sType.equalsIgnoreCase("constraint")) {
											Constraint con = new Constraint(this, (Element)nn2, mapGE);
											bDocumentSemanticallyValid &= con.isValid();
											mapC.put(con.id(), con);
											constraintlist.add(con);
										}
									}
								}
							}
						}
					}
				}

				for (GEPoint p : pointlist) {
					bDocumentSemanticallyValid &= p.setConstraints(mapC);
				}
				if (pnameCounter < 0)
					pnameCounter = pointlist.size();
				assert(pointlist.size() == pnameCounter); // When loading a second file. Be sure to clear the counters and pointlists. This asserted when there were points and a new file was loaded.

				for (GELine l : linelist) {
					bDocumentSemanticallyValid &= l.setConstraints(mapC);
				}
				if (plineCounter < 0)
					plineCounter = linelist.size();
				assert(linelist.size() == plineCounter);

				for (GECircle c : circlelist) {
					bDocumentSemanticallyValid &= c.setConstraints(mapC);
				}
				if (pcircleCounter < 0)
					pcircleCounter = circlelist.size();
				assert(circlelist.size() == pcircleCounter);

				if (!bDocumentSemanticallyValid) {
					System.err.println("Loaded file had an invalid structure.");
				}
			}
			if (bDocumentSemanticallyValid) {
				re_generate_all_poly();
				reCalculate();
			}
		}
	}

	public void SaveIntoXMLDocument(Element rootElement) {
		assert(rootElement != null);
		if (rootElement != null) {
			Document doc = rootElement.getOwnerDocument();

			Element elementThis = doc.createElementNS(null, "DrawProcess");   // Create element for DrawProcess to save itself.
			rootElement.appendChild( elementThis );                           // Attach element to Root element

			elementThis.setAttribute("version", String.valueOf(docVersion));
			elementThis.setAttribute("GridVisible", String.valueOf(DRAWGRID));
			elementThis.setAttribute("SnapToGrid", String.valueOf(SNAP));
			elementThis.setAttribute("GridX", String.valueOf(GridX));
			elementThis.setAttribute("GridY", String.valueOf(GridY));

			Element elem = doc.createElement("points");
			elem.appendChild(doc.createTextNode(String.valueOf(pnameCounter)));
			elementThis.appendChild(elem);
			elem = doc.createElement("lines");
			elem.appendChild(doc.createTextNode(String.valueOf(plineCounter)));
			elementThis.appendChild(elem);
			elem = doc.createElement("circles");
			elem.appendChild(doc.createTextNode(String.valueOf(pcircleCounter)));
			elementThis.appendChild(elem);

			if (cpfield != null) {
				Element elementProveField = doc.createElement("proof");
				elementThis.appendChild(elementProveField);
				cpfield.SaveIntoXMLDocument(elementProveField);
			}

			Element elemParameter = doc.createElement("parameters");
			elementThis.appendChild(elemParameter);
			for (int i = 0; i < (paraCounter - 1); i++) {
				parameter[i].saveIntoXMLDocument(elemParameter);
			}

			elemParameter = doc.createElement("backup_parameters");
			elementThis.appendChild(elemParameter);
			for (int i = 0; i < (paraCounter - 1); i++) {
				Element elemDouble = doc.createElement("double");
				elemDouble.appendChild(doc.createTextNode(String.valueOf(paraBackup[i])));
				elemParameter.appendChild(elemDouble);
			}

			Element elementlist = doc.createElement("geometric_entities");
			elementThis.appendChild(elementlist);
			for (GraphicEntity ge : pointlist) {
				ge.saveIntoXMLDocument(elementlist, null);
			}
			for (GraphicEntity ge : linelist) {
				ge.saveIntoXMLDocument(elementlist, null);
			}
			for (GraphicEntity ge : circlelist) {
				ge.saveIntoXMLDocument(elementlist, null);
			}
			for (GraphicEntity ge : anglelist) {
				ge.saveIntoXMLDocument(elementlist, null);
			}
			for (GraphicEntity ge : distancelist) {
				ge.saveIntoXMLDocument(elementlist, null);
			}
			for (GraphicEntity ge : polygonlist) {
				ge.saveIntoXMLDocument(elementlist, null);
			}
			for (GraphicEntity ge : textlist) {
				ge.saveIntoXMLDocument(elementlist, null);
			}
			for (GraphicEntity ge : tracelist) {
				ge.saveIntoXMLDocument(elementlist, null);
			}
			for (GraphicEntity ge : otherlist) {
				ge.saveIntoXMLDocument(elementlist, null);
			}

			// Save constraints placed on points and lines
			if (constraintlist != null && !constraintlist.isEmpty()) {
				Element elemConstraints = doc.createElement("constraints");
				elementThis.appendChild(elemConstraints);
				for (final Constraint con : constraintlist) {
					con.saveIntoXMLDocument(elemConstraints);
				}
			}

			// Save undo structures
			//			if (undolist != null && !undolist.isEmpty()) {
			//				Element elementUndo = doc.createElement("undo");
			//				elementThis.appendChild(elementUndo);
			//				for (final UndoStruct undo : undolist) {
			//					undo.saveIntoXMLDocument(elementUndo);
			//				}
			//			}
		}
	}

	boolean write_ps(final String name, final int stype, final boolean ptf,
			final boolean pts) throws IOException { // 0: color 1: gray ; 2:
		// black and white

		final File f = new File(name);
		final boolean bExists = f.exists();
		if (bExists)
			f.delete();
		else
			f.createNewFile();
		try (final FileOutputStream fp = new FileOutputStream(f, bExists)) {

			final Calendar c = Calendar.getInstance();
			final String stime = "%Create Time: " + c.getTime().toString() + "\n";
			final String sversion = "%Created By: " + UtilityVersion.getNameAndVersion()
					+ "\n";

			String s = "%!PS-Adobe-2.0\n"
					+ stime
					+ sversion
					+ "\n"
					+ "%%BoundingBox: 0 500 400 650\n"
					+ "0.7 setlinewidth\n"
					+ "gsave\n20 700 translate\n.5 .5 scale\n"
					+ "/dash {[4 6] 0 setdash stroke [] 0 setdash} def\n"
					+ "/cir {0 360 arc} def\n"
					+ "/cirfill {0 360 arc 1.0 1.0 1.0 setrgbcolor [] 0 setdash} def\n"
					+ "/arcfill{arc 1.0 1.0 1.0 setrgbcolor [] 0 setdash} def\n"
					+ "/rm {moveto 4 4 rmoveto} def\n"
					+ "/circle {0 360 arc} def\n"
					+ "/black {0.0 0.0 0.0 setrgbcolor} def\n"
					+ "/mf {/Times-Roman findfont 15.71 scalefont setfont 0.0 0.0 0.0 setrgbcolor} def\n"
					+ "/nf {/Times-Roman findfont 11.00 scalefont setfont 0.0 0.0 0.0 setrgbcolor} def\n\n";

			fp.write(s.getBytes());
			SaveDrawAttr(fp, stype);

			fp.write("%define points\n".getBytes());

			for (final GEPoint pt : pointlist)
				pt.SavePS_Define_Point(fp);

			write_list_ps(fp, polygonlist, "%-----draw polygons\n", stype);
			write_list_ps(fp, anglelist, "%-----draw angles\n", stype);
			write_list_ps(fp, distancelist, "%-----draw measures\n", stype);
			write_list_ps(fp, otherlist, "%-----draw marks and other\n", stype);
			write_list_ps(fp, tracelist, "%-----draw trace list\n", stype);

			write_perp_foot(fp, stype);
			write_list_ps(fp, linelist, "%-----draw lines\n", stype);
			write_list_ps(fp, circlelist, "%-----draw circles\n", stype);
			if ((stype == 0) && ptf)
				for (final GEPoint pt : pointlist)
					pt.savePSOriginal(fp);
			else
				write_list_ps(fp, pointlist, "%-----draw points\n", stype);

			write_list_ps(fp, textlist, "%-----draw texts\n", stype);

			if ((cpfield != null) && pts)
				cpfield.SavePS(fp, stype);
			s = "grestore\nshowpage\n";
			fp.write(s.getBytes());
			fp.close();
		}
		return true;
	}

	void write_perp_foot(final FileOutputStream fp, final int stype)
			throws IOException {
		final ArrayList<Point> vlist = new ArrayList<Point>();
		drawPerpFoot(null, vlist, 1);
		if (vlist.size() == 0)
			return;

		fp.write("%----draw foot\n".getBytes());
		String s = "[] 0 setdash ";
		if (stype == 0)
			s += "0.5 setlinewidth 1.0 0.0 0.0 setrgbcolor \n";
		else
			s += "0.5 setlinewidth 0.0 0.0 0.0 setrgbcolor \n";
		fp.write(s.getBytes());
		for (int i = 0; i < (vlist.size() / 2); i++) {
			final Point p1 = vlist.get(2 * i);
			final Point p2 = vlist.get((2 * i) + 1);
			String st = p1.getX() + " " + (-p1.getY()) + " moveto " + p2.getX()
					+ " " + (-p2.getY()) + " lineto ";
			if ((i % 2) == 0)
				st += "\n";
			fp.write(st.getBytes());
		}
		fp.write("stroke \n".getBytes());
	}

	private static void write_list_ps(final FileOutputStream fp,
			final ArrayList<? extends GraphicEntity> vlist, final String discription,
			final int stype) throws IOException {
		if (!vlist.isEmpty())
			fp.write((discription).getBytes());
		for (final GraphicEntity p : vlist)
			p.SavePS(fp, stype);
	}

	private void SaveDrawAttr(final FileOutputStream fp, final int stype)
			throws IOException {
		final SortedSet<Integer> vc = new TreeSet<Integer>();
		final SortedSet<Integer> vd = new TreeSet<Integer>();
		final SortedSet<Integer> vw = new TreeSet<Integer>();

		getUDAFromList(vc, vd, vw, pointlist);
		getUDAFromList(vc, vd, vw, linelist);
		getUDAFromList(vc, vd, vw, circlelist);
		getUDAFromList(vc, vd, vw, anglelist);
		getUDAFromList(vc, vd, vw, distancelist);
		getUDAFromList(vc, vd, vw, polygonlist);
		getUDAFromList(vc, vd, vw, textlist);
		getUDAFromList(vc, vd, vw, tracelist);
		getUDAFromList(vc, vd, vw, otherlist);
		for (final GEAngle ag : anglelist)
			if (ag.getAngleType() == 3)
				vc.add(ag.getValue1());
		DrawData.SavePS(vc, vd, vw, fp, stype);
	}

	private static void getUDAFromList(final SortedSet<Integer> vc,
			final SortedSet<Integer> vd, final SortedSet<Integer> vw,
			final ArrayList<? extends GraphicEntity> vlist) {
		for (final GraphicEntity cc : vlist) {
			vc.add(cc.m_color);
			vd.add(cc.m_dash);
			vw.add(cc.m_width);
		}
	}

	@Override
	public int print(final Graphics graphics, final PageFormat pageFormat,
			final int pageIndex) throws PrinterException {
		if (pageIndex >= 1)
			return Printable.NO_SUCH_PAGE;
		final Graphics2D g2 = (Graphics2D) graphics;

		g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		final double w = pageFormat.getWidth() / Width;
		final double h = pageFormat.getHeight() / Height;

		if (w > h)
			g2.scale(h, h);
		else
			g2.scale(w, w);

		paintPoint(g2);
		return 0;
	}

	public void PrintContent() {
		final PrinterJob job = PrinterJob.getPrinterJob();
		final PageFormat landscape = job.defaultPage();
		landscape.setOrientation(PageFormat.LANDSCAPE);

		final PageFormat pf = new PageFormat();
		final Paper paper = new Paper();
		final double margin = 36; // half inch
		paper.setImageableArea(margin, margin, paper.getWidth() - (margin * 2),
				paper.getHeight() - (margin * 2));

		pf.setOrientation(PageFormat.LANDSCAPE);
		pf.setPaper(paper);
		job.setPrintable(this, pf);

		if (job.printDialog())
			try {
				job.print();
			} catch (final Exception exc) {
				UtilityMiscellaneous.print("Print Error. ------ " + exc.toString());
			}
	}

	public void addText(final GEText tx) {
		if (tx != null)
			if (addGraphicEntity(textlist, tx))
				UndoAdded(tx.TypeString());
	}

	public UndoStruct addProveToList() {
		final UndoStruct undo = new UndoStruct(UndoStruct.T_PROVE_NODE, 0);
		undolist.add(undo);
		return undo;
	}

	public UndoStruct addToProveToList() {
		final UndoStruct undo = new UndoStruct(UndoStruct.T_TO_PROVE_NODE, 0);
		undo.msg = "";
		undolist.add(undo);
		return undo;
	}

	public void addNodeToUndoList(final UndoStruct un) {
		undolist.add(un);

		// if (undolist.size() == 0) {
		// if (gxInstance.getUndoEditDialog().isVisible()) {
		// } else {
		// }
		// } else {
		// if (gxInstance != null &&
		// !gxInstance.getUndoEditDialog().isVisible()) {
		// UndoStruct u = (UndoStruct) undolist.get(undolist.size() - 1);
		// if (u.m_type == UndoStruct.T_UNDO_NODE) {
		// undolist.add(un);
		// } else {
		// u.childundolist.add(un);
		// }
		// } else {
		// }
		// }
	}

	public UndoStruct UndoAdded(final Object tip) {
		return UndoAdded(tip, true);

	}

	public UndoStruct UndoAdded(final Object tip, final boolean gr) {
		return UndoAdded(tip, gr, true);
	}

	public UndoStruct UndoAdded(final Object tip, final boolean gr,
			final boolean m) {

		if (UtilityMiscellaneous.id_count == currentUndo.id)
			return null;

		String message = tip.toString();
		if (message.length() != 0) {
			char c = message.charAt(0);
			if ((c >= 'a') && (c <= 'z')) { // force first letter to be uppercase
				c += 'A' - 'a';
				message = c + message.substring(1);
			}
		} // upper case first char of the message.

		redolist.clear();
		final UndoStruct Undo = currentUndo;
		Undo.action = CurrentAction;
		if (message.length() >= 1) {

			char c = message.charAt(0);
			if ((c >= 'a') && (c <= 'z')) {
				c = (char) ((c + 'A') - 'a');
				message = c + message.substring(1);
			}

			Undo.msg = message;
		}
		Undo.id_b = UtilityMiscellaneous.id_count;
		Undo.paraCounter_b = paraCounter;
		Undo.pnameCounter_b = pnameCounter;
		Undo.plineCounter_b = plineCounter;
		Undo.pcircleCounter_b = pcircleCounter;

		addNodeToUndoList(Undo);
		currentUndo = new UndoStruct(paraCounter);
		currentUndo.pnameCounter = pnameCounter;
		currentUndo.plineCounter = plineCounter;
		currentUndo.pcircleCounter = pcircleCounter;

		if (gxInstance != null) {
			if (m && gxInstance.hasManualInputBar()
					&& (gxInstance.getProveStatus() == 4)) { // deliver a copy of the undo structure to the manual input bar
				final PanelMProofInput input = gxInstance
						.getMannalInputToolBar();
				if (input != null)
					input.addUndo(Undo, message);
			}
			if (gr)
				gxInstance.getProofPanel().generate(); // not sure why the pprove needs to be generated (or even what that means)
			gxInstance.reloadLP();  // What is lp? (method refreshes an undo dialog box if it is visible.)
			gxInstance.setBKState(); // refresh state of undo and redo buttons to reflect the new item on the undo list.
		}

		return Undo;

	}

	public void UndoAdded() {
		UndoAdded("Not yet added ");
	}

	public void UndoAdded(final GraphicEntity cc) {
		UndoAdded(cc.TypeString() + ":  " + cc.getDescription());
	}

	public void UndoPure() {
		doFlash();
		while (true)
			if (false == Undo_stepPure()) {
				re_generate_all_poly();
				doFlash();
				return;
			}
	}

	public void Undo() {
		while (true)
			if (!Undo_step())
				return;
	}

	public void redo() {
		while (true)
			if (null == redo_step())
				return;
	}

	public boolean undo_step(final UndoStruct Undo) {
		return undo_step(Undo, true);
	}

	public boolean undo_step(final UndoStruct Undo, final boolean rg) {

		if ((Undo.m_type == UndoStruct.T_COMBINED_NODE)
				|| (Undo.m_type == UndoStruct.T_PROVE_NODE)) {
			for (final UndoStruct u : Undo.childundolist)
				undo_step(u);
			return true;
		}

		final int pc = Undo.id;
		final int pcb = Undo.id_b;
		// int para = Undo.paraCounter;
		// int parab = Undo.paraCounter_b;
		Undo.clear();
		// TPoly pl, tp;
		// pl = polylist;
		// tp = null;
		// while (pl != null) {
		// TMono m = pl.getPoly();
		// int tid = poly.lv(m);
		//
		// if (tid >= para && tid < parab) {
		// if (tp == null) {
		// polylist = null;
		// } else {
		// tp.setNext(null);
		// }
		// Undo.polylist = pl;
		// break;
		// }
		// tp = pl;
		// pl = pl.getNext();
		// }
		// pl = pblist;
		// tp = null;
		//
		// while (pl != null) {
		// TMono m = pl.getPoly();
		// int tid = poly.lv(m);
		//
		// if (tid >= para && tid < parab) {
		// if (tp == null) {
		// pblist = null;
		// } else {
		// tp.setNext(null);
		// }
		// Undo.pblist = pl;
		// break;
		// }
		// tp = pl;
		// pl = pl.getNext();
		// }

		moveUndoObjectFromList(Undo.pointlist, pointlist, pc, pcb);
		moveUndoObjectFromList(Undo.linelist, linelist, pc, pcb);
		moveUndoObjectFromList(Undo.circlelist, circlelist, pc, pcb);
		moveUndoObjectFromList(Undo.anglelist, anglelist, pc, pcb);
		moveUndoObjectFromList(Undo.distancelist, distancelist, pc, pcb);
		moveUndoObjectFromList(Undo.polygonlist, polygonlist, pc, pcb);
		moveUndoObjectFromList(Undo.textlist, textlist, pc, pcb);
		moveUndoObjectFromList(Undo.tracelist, tracelist, pc, pcb);
		moveUndoObjectFromList(Undo.otherlist, otherlist, pc, pcb);

		for (int i = 0; i < constraintlist.size(); i++) {
			final Constraint cs = constraintlist.get(i);

			if ((cs.id >= pc) && (cs.id < pcb)) {
				Undo.constraintlist.add(cs);
				removeConstraintFromList(cs);
				i--;
				final int type = cs.GetConstraintType();
				switch (type) {
				case Constraint.PONLINE: {
					final GEPoint p = (GEPoint) cs.getelement(0);
					final GELine ln = (GELine) cs.getelement(1);
					ln.points.remove(p);
				}
				break;
				case Constraint.PONCIRCLE: {
					final GEPoint p = (GEPoint) cs.getelement(0);
					final GECircle c = (GECircle) cs.getelement(1);
					c.points.remove(p);
				}
				break;
				case Constraint.VISIBLE: {
					final GraphicEntity cc = (GraphicEntity) cs.getelement(0);
					cc.setVisible(false);
				}
				break;
				case Constraint.INVISIBLE: {
					final GraphicEntity cc = (GraphicEntity) cs.getelement(0);
					cc.setVisible(true);
				}
				break;
				case Constraint.INTER_LL: {
					final GEPoint p = (GEPoint) cs.getelement(0);
					final GELine ln1 = (GELine) cs.getelement(1);
					final GELine ln2 = (GELine) cs.getelement(2);
					ln1.points.remove(p);
					ln2.points.remove(p);
				}
				break;
				case Constraint.INTER_LC: {
					final GEPoint p = (GEPoint) cs.getelement(0);
					final GELine ln1 = (GELine) cs.getelement(1);
					final GECircle c2 = (GECircle) cs.getelement(2);
					ln1.points.remove(p);
					c2.points.remove(p);
				}
				break;
				case Constraint.INTER_CC: {
					final GEPoint p = (GEPoint) cs.getelement(0);
					final GECircle c1 = (GECircle) cs.getelement(1);
					final GECircle c2 = (GECircle) cs.getelement(2);
					c1.points.remove(p);
					c2.points.remove(p);
				}
				break;
				case Constraint.EQUIVALENCE1:
				case Constraint.EQUIVALENCE2:
				case Constraint.TRANSFORM:
				case Constraint.TRANSFORM1: {
					final GEPolygon p1 = (GEPolygon) cs.getelement(0);
					// CPolygon p2 = (CPolygon) cs.getelement(1);
					p1.setVisible(true);

				}
				break;

				}
			}
		}
		if (rg) {
			re_generate_all_poly();
			reCalculate();
		}
		currentUndo.id = Undo.id;
		currentUndo.msg = Undo.msg;
		currentUndo.paraCounter = Undo.paraCounter;
		currentUndo.pnameCounter = Undo.pnameCounter;
		currentUndo.plineCounter = Undo.plineCounter;
		currentUndo.pcircleCounter = Undo.pcircleCounter;

		UtilityMiscellaneous.id_count = Undo.id;
		paraCounter = Undo.paraCounter;
		pnameCounter = Undo.pnameCounter;
		plineCounter = Undo.plineCounter;
		pcircleCounter = Undo.pcircleCounter;
		return true;
	}

	public boolean isRedoAtEnd() {
		return redolist.size() == 0;
	}

	public boolean Undo_stepPure() {
		undo = null;
		if (UtilityMiscellaneous.id_count != currentUndo.id)
			UndoAdded();
		final int size = undolist.size();
		if (size == 0)
			return false;
		clearSelection();
		CatchList.clear();
		final UndoStruct Undo = undolist.get(size - 1);
		undolist.remove(Undo);
		undo_step(Undo, false);
		redolist.add(Undo);
		return true;

	}

	public boolean Undo_step() {

		// clearSelection();
		// STATUS = 0;
		// FirstPnt = SecondPnt = null;
		cancelCurrentAction();

		undo = null;
		if (UtilityMiscellaneous.id_count != currentUndo.id)
			UndoAdded();
		final int size = undolist.size();
		if (size == 0)
			return false;
		clearSelection();
		CatchList.clear();
		final UndoStruct Undo = undolist.get(size - 1);
		undolist.remove(Undo);
		undo_step(Undo);
		redolist.add(Undo);
		clearFlash();

		if ((gxInstance != null) && gxInstance.hasManualInputBar()
				&& (gxInstance.getProveStatus() == 4)) {
			final PanelMProofInput input = gxInstance.getMannalInputToolBar();
			if (input != null)
				input.undoStep(Undo);
		}

		if (gxInstance != null)
			gxInstance.getProofPanel().generate();
		return true;

	}

	public int getUndolistSize() {
		return undolist.size();
	}

	public int getRedolistSize() {
		return redolist.size();
	}

	public UndoStruct getNextRedoStruct() {
		if (redolist.size() == 0)
			return null;
		return redolist.get(redolist.size() - 1);
	}

	/*
	 * public void moveUndoObjectFromList(ArrayList<CClass> v1, ArrayList<?> v2,
	 * int pc1, int pc2) { for (int i = 0; i < v2.size(); i++) { CClass cc =
	 * (CClass) v2.get(i); if (cc.m_id >= pc1 && cc.m_id < pc2) { v1.add(cc);
	 * v2.remove(i); i--; } } }
	 */

	public static <T extends GraphicEntity> void moveUndoObjectFromList(
			final ArrayList<T> listTo, final ArrayList<T> listFrom,
			final int pc1, final int pc2) {
		for (final Iterator<T> iter = listFrom.iterator(); iter.hasNext();) {
			final T obj2 = iter.next();
			if ((obj2 != null) && (obj2.m_id >= pc1) && (obj2.m_id < pc2)) {
				listTo.add(obj2);
				iter.remove();
			}
		}
	}

	public static <T extends GraphicEntity> void selectUndoObjectFromList(
			final ArrayList<GraphicEntity> listTo, final Collection<T> listFrom,
			final int pc1, final int pc2) {
		for (final T obj2 : listFrom) {
			if ((obj2 != null) && (obj2.m_id >= pc1) && (obj2.m_id < pc2))
				listTo.add(obj2);
		}
	}

	public void setUndoStructForDisPlay(final UndoStruct u,
			final boolean compulsory_flash) {
		undo = u;
		if (((u != null) && u.flash) || compulsory_flash) {
			final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
			u.getAllObjects(this, v);
			final FlashObject f = new FlashObject(panel);
			f.setAt(panel, v);
			addFlash(f);
		}
	}

	public void flash_node_by_id(final int id) {
		for (final UndoStruct u : undolist)
			if ((id >= u.id) && (id < u.id_b)) {
				setUndoStructForDisPlay(u, true);
				break;
			}
	}

	public FlashObject getObjectFlash(final GraphicEntity cc) {
		final FlashObject f = new FlashObject(panel);
		f.addFlashObject(cc);
		return f;
	}

	public void setObjectListForFlash(final GraphicEntity cc) {
		final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
		v.add(cc);
		setObjectListForFlash(v);
	}

	public void setObjectListForFlash(final ArrayList<GraphicEntity> list,
			final JPanel p) {

		final FlashObject f = new FlashObject(panel);
		f.setAt(p, list);
		addFlash(f);
	}

	public void setObjectListForFlash(final ArrayList<GraphicEntity> list) {
		setObjectListForFlash(list, panel);
	}

	public void setUndoListForFlash(final ArrayList<UndoStruct> list) {
		final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
		for (final UndoStruct u : list)
			u.getAllObjects(this, v);
		setObjectListForFlash(v);
	}

	public void setUndoListForFlash1(final ArrayList<UndoStruct> list) {
		final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
		for (final UndoStruct u : list)
			u.getAllObjects(this, v);
		final FlashObject f = new FlashObject(panel);
		f.setAt(panel, v);
		addFlash1(f);
	}

	public void stopUndoFlash() {
		clearFlash();
	}

	public boolean redo_till_specified_idcount(final int id) {
		UndoStruct Undo;
		if (redolist.size() == 0)
			return false;
		while (true) {
			Undo = redolist.get(redolist.size() - 1);
			if (Undo.id_b <= id)
				redo_step();
			else
				break;
			if (redolist.size() == 0)
				break;
		}
		return true;
	}

	public boolean redo_step(final UndoStruct Undo) {

		if ((Undo.m_type == UndoStruct.T_COMBINED_NODE)
				|| (Undo.m_type == UndoStruct.T_PROVE_NODE)) {
			for (int i = 0; i < Undo.childundolist.size(); i++) {
				final UndoStruct u = Undo.childundolist.get(i);
				redo_step(u);
			}
			return true;
		}

		for (final Object o : Undo.pointlist)
			if (o instanceof GEPoint)
				addPointToList((GEPoint) o);
		for (final Object o : Undo.linelist)
			if (o instanceof GELine)
				addLineToList((GELine) o);
		for (final Object o : Undo.circlelist)
			if (o instanceof GECircle)
				addCircleToList((GECircle) o);
		for (final Object o : Undo.anglelist)
			if (o instanceof GEAngle)
				addAngleToList((GEAngle) o);

		for (final Constraint cs : Undo.constraintlist) {
			addConstraintToList(cs);

			final int type = cs.GetConstraintType();
			switch (type) {
			case Constraint.PONLINE: {
				final GEPoint p = (GEPoint) cs.getelement(0);
				final GELine ln = (GELine) cs.getelement(1);
				ln.add(p);
			}
			break;
			case Constraint.PONCIRCLE: {
				final GEPoint p = (GEPoint) cs.getelement(0);
				final GECircle c = (GECircle) cs.getelement(1);
				c.add(p);
			}
			break;
			case Constraint.VISIBLE: {
				final GraphicEntity cc = (GraphicEntity) cs.getelement(0);
				cc.setVisible(true);
			}
			break;
			case Constraint.INVISIBLE: {
				final GraphicEntity cc = (GraphicEntity) cs.getelement(0);
				cc.setVisible(false);
			}
			break;
			case Constraint.INTER_LL: {
				final GEPoint p = (GEPoint) cs.getelement(0);
				final GELine ln1 = (GELine) cs.getelement(1);
				final GELine ln2 = (GELine) cs.getelement(2);
				ln1.add(p);
				ln2.add(p);
			}
			break;
			case Constraint.INTER_LC: {
				final GEPoint p = (GEPoint) cs.getelement(0);
				final GELine ln1 = (GELine) cs.getelement(1);
				final GECircle c2 = (GECircle) cs.getelement(2);
				ln1.add(p);
				c2.add(p);
			}
			break;
			case Constraint.INTER_CC: {
				final GEPoint p = (GEPoint) cs.getelement(0);
				final GECircle c1 = (GECircle) cs.getelement(1);
				final GECircle c2 = (GECircle) cs.getelement(2);
				c1.add(p);
				c2.add(p);
			}
			break;
			case Constraint.TRANSFORM: {
				final GEPolygon p1 = (GEPolygon) cs.getelement(0);
				final GEPolygon p2 = (GEPolygon) cs.getelement(1);
				final GEPoint p = (GEPoint) cs.getelement(2);
				if (p == null)
					addFlashPolygon(p1, p2, 0, false, 0, 0);
				else
					addFlashPolygon(p1, p2, 0, true, p.getx(), p.gety());

				if (cs.proportion == 1)
					p1.setVisible(false);
			}
			break;
			case Constraint.TRANSFORM1: {
				final GEPolygon p1 = (GEPolygon) cs.getelement(0);
				final GEPolygon p2 = (GEPolygon) cs.getelement(1);
				addFlashPolygon(p1, p2, 1, false, 0, 0);
				p1.setVisible(false);

			}
			break;
			case Constraint.EQUIVALENCE1:
			case Constraint.EQUIVALENCE2: {
				final GEPolygon p1 = (GEPolygon) cs.getelement(0);
				final GEPolygon p2 = (GEPolygon) cs.getelement(1);
				final GEPoint p = (GEPoint) cs.getelement(2);
				if (p == null)
					addFlashPolygon(p1, p2, 1, false, 0, 0);
				else
					addFlashPolygon(p1, p2, 1, true, p.getx(), p.gety());
				p1.setVisible(false);

			}
			break;
			case Constraint.TRATIO:
			case Constraint.PRATIO: {
				// CPoint p1 = (CPoint) cs.getelement(0);
				// CPoint p2 = (CPoint) cs.getelement(1);
				// CPoint p3 = (CPoint) cs.getelement(2);
				// CPoint p4 = (CPoint) cs.getelement(3);
				// JSegmentMoveingFlash f = new JSegmentMoveingFlash(panel, p1,
				// p2, p4, p3, 0, 0);
				// addFlash1(f);
			}
			break;
			}
		}
		for (final Object o : Undo.distancelist)
			if (o instanceof GEDistance)
				addGraphicEntity(distancelist, (GEDistance) o);
		for (final Object o : Undo.polygonlist)
			if (o instanceof GEPolygon)
				addGraphicEntity(polygonlist, (GEPolygon) o);
		for (final Object o : Undo.textlist)
			if (o instanceof GEText)
				addGraphicEntity(textlist, (GEText) o);
		for (final Object o : Undo.otherlist)
			if (o instanceof GraphicEntity)
				addGraphicEntity(otherlist, (GraphicEntity) o);
		re_generate_all_poly();
		reCalculate();

		currentUndo.id = Undo.id_b;
		currentUndo.paraCounter = Undo.paraCounter_b;
		currentUndo.pnameCounter = Undo.pnameCounter_b;
		currentUndo.plineCounter = Undo.plineCounter_b;
		currentUndo.pcircleCounter = Undo.pcircleCounter_b;

		UtilityMiscellaneous.id_count = Undo.id_b;
		paraCounter = Undo.paraCounter_b;
		pnameCounter = Undo.pnameCounter_b;
		plineCounter = Undo.plineCounter_b;
		pcircleCounter = Undo.pcircleCounter_b;
		return true;
	}

	public boolean already_redo(final UndoStruct u) {
		return !redolist.contains(u);
	}

	public UndoStruct redo_step(final boolean cf) {
		if (redolist.isEmpty())
			return null;

		if (cf)
			clearFlash();
		clearSelection();
		CatchList.clear();

		final UndoStruct Undo = redolist.get(redolist.size() - 1);
		redolist.remove(Undo);
		redo_step(Undo);
		undolist.add(Undo);
		if (gxInstance != null)
			gxInstance.getProofPanel().generate();
		return Undo;
	}

	public UndoStruct redo_step() {
		return redo_step(true);
	}

	public GEPoint fd_point(final int index) {
		if (index <= 0)
			return null;
		final Pro_point p = gterm().getProPoint(index);
		if (p != null) {
			final String s = p.name;
			for (final GEPoint t : pointlist)
				if (t.equals(s))
					return t;
		}
		if ((index >= 1) && (index <= pointlist.size()))
			return pointlist.get(index - 1);

		return null;
	}

	public GEAngle fd_angle_4p(final GEPoint p1, final GEPoint p2,
			final GEPoint p3, final GEPoint p4) {
		for (final GEAngle ag : anglelist)
			if (ag.isSame(p1, p2, p3, p4))
				return ag;
		return null;
	}

	public GEAngle fd_angle_m(final GEPoint p1, final GEPoint p2, final GEPoint p3,
			final GEPoint p4) {
		for (final GEAngle ag : anglelist)
			if (ag.isSame(p1, p2, p3, p4))
				return ag;
		return null;
	}

	public GEAngle fd_angle(final GEAngle ag) {
		for (final GEAngle g : anglelist)
			if (g.sameAngle(ag))
				return g;
		return null;
	}

	public GECircle add_rcircle(final int o, final int a, final int b) {

		if ((o == 0) || (a == 0) || (b == 0))
			return null;
		if (fd_rcircle(o, a, b) != null)
			return fd_rcircle(o, a, b);

		int op = a;
		if (o == a)
			op = b;

		return addCr(o, op);
	}

	public GECircle addCr(final int o, final int a) {
		final GEPoint p1 = fd_point(o);
		GECircle c = null;

		if ((c = fd_circle(o, a)) != null)
			return c;
		c = new GECircle(p1, fd_point(a));
		addCircleToList(c);
		return c;
	}

	public GECircle fd_rcircle(final int o, final int a, final int b) {
		if ((o == 0) || (a == 0) || (b == 0))
			return null;
		final GEPoint p1 = fd_point(o);
		final GEPoint p2 = fd_point(a);
		final GEPoint p3 = fd_point(b);
		if ((p1 == null) || (p2 == null) || (p3 == null))
			return null;
		GEPoint op = p2;
		if (o == a)
			op = p3;

		for (final GECircle cc : circlelist)
			if (cc.points.contains(op) && (cc.o == p1))
				return cc;
		return null;
	}

	public GECircle fd_circle(final int o, final int a) {
		if ((o == 0) || (a == 0))
			return null;
		final GEPoint p1 = fd_point(o);
		final GEPoint p2 = fd_point(a);
		if ((p1 == null) || (p2 == null))
			return null;

		for (final GECircle cc : circlelist)
			if (cc.points.contains(p2) && (cc.o == p1))
				return cc;
		return null;
	}

	public GECircle fd_circleOR(final GEPoint o, final GEPoint p1, final GEPoint p2) {
		for (final GECircle cc : circlelist) {
			if (cc.o != o)
				continue;
			final GEPoint[] pp = cc.getRadiusPoint();
			if ((pp[0] == p1) && (pp[1] == p2))
				return cc;
		}
		return null;
	}

	public GECircle fd_circle(final int a, final int b, final int c) {
		final GEPoint p1 = fd_point(a);
		final GEPoint p2 = fd_point(b);
		final GEPoint p3 = fd_point(c);
		for (final GECircle cc : circlelist)
			if (cc.points.contains(p1) && cc.points.contains(p2)
					&& cc.points.contains(p3))
				return cc;
		return null;

	}

	public GEEqualDistanceMark addedMark(final int a, final int b) {
		final GEPoint p1 = fd_point(a);
		final GEPoint p2 = fd_point(b);
		if ((p1 != null) && (p2 != null) && (fd_edmark(p1, p2) == null)) {
			final GEEqualDistanceMark ed = new GEEqualDistanceMark(p1, p2);
			addGraphicEntity(otherlist, ed);
			return ed;
		}
		return null;
	}

	public GEEqualDistanceMark addedMark(final GEPoint p1, final GEPoint p2) {
		if ((p1 != null) && (p2 != null) && (fd_edmark(p1, p2) == null)) {
			final GEEqualDistanceMark ed = new GEEqualDistanceMark(p1, p2);
			addGraphicEntity(otherlist, ed);
			return ed;
		}
		return null;
	}

	public void addedMarks(final GEPoint p1, final GEPoint p2, final GEPoint p3, final GEPoint p4) {
		final GEEqualDistanceMark e1 = addedMark(p1, p2);
		final GEEqualDistanceMark e2 = addedMark(p3, p4);
		int n = getEMarkNum();
		n = n / 2;
		if (e1 != null)
			e1.setdnum(n);
		if (e2 != null)
			e2.setdnum(n);
	}

	int aux_angle = 0;
	int aux_polygon = 0;
	int aux_mark = 0;

	public void resetAux() {
		UndoAdded();
		Undo_step();
		aux_angle = 0;
		aux_polygon = 0;
		aux_mark = 0;
	}

	public void flashStep(final ArrayList<GraphicEntity> v) {
		setObjectListForFlash(v);
	}

	public String getAngleSimpleName() {
		final int n = anglelist.size() + 1;
		String sn;
		final char[] ch = new char[1];
		ch[0] = (char) (('A' + n) - 10);
		if (n >= 10)
			sn = new String(ch);
		else
			sn = new Integer(n).toString();
		return sn;
	}

	public void setCurrentDrawPanel(final JPanel p) {
		panel = p;
	}

	public DrawPanel(final DrawPanelFrame gx) {
		super();
		gxInstance = gx;
	}

	//	public void setCurrentInstance(final DrawPanelFrame gx) {
	//		gxInstance = gx;
	//	}

	public param getANewParam() {
		// int n = paraCounter;
		final param p1 = parameter[paraCounter - 1] = new param(paraCounter++,
				0);
		return p1;
	}

	public int add_sp_angle_value(final int n) {
		// int n = paraCounter;
		final param p1 = parameter[paraCounter - 1] = new param(paraCounter++, 0);
		p1.value = Constraint.getSpecifiedAnglesMagnitude(n);
		// p1.setParameterStatic();
		return p1.xindex;
	}

	String get_cir_center_name() {
		int k = 0;
		while (true) {
			String s = "O";
			if (k != 0)
				s += k;
			boolean e = false;
			for (final GEPoint pt : pointlist) {
				final String st = pt.getname();
				if ((st != null) && st.equalsIgnoreCase(s)) {
					e = true;
					break;
				}
			}
			if (e == false)
				break;
			k++;
		}
		if (k == 0)
			return "O";
		else
			return "O" + k;
	}

	public void add_to_undolist(final UndoStruct u) {
		if (!undolist.contains(u))
			undolist.add(u);
	}

	public void remove_from_undolist(final UndoStruct u) {
		undolist.remove(u);
	}

	public void add_to_redolist(final UndoStruct u) {
		if (!redolist.contains(u))
			redolist.add(u);

	}

	public void remove_from_redolist(final UndoStruct u) {
		redolist.remove(u);
	}

	public boolean need_save() {
		return (pointlist.size() > 0) || (textlist.size() > 0)
				|| (otherlist.size() > 0) || (paraCounter > 1);
	}

	// ////////////////////////////////////////////////////////////////////////////////

	public GEPolygon fd_polygon(final GECircle c) {
		for (final GEPolygon cp : polygonlist) {
			if (cp.ftype != 1)
				continue;

			final GraphicEntity x = cp.getElement(0);
			if (x != c.o)
				continue;

			final GEPoint[] pp = c.getRadiusPoint();
			if ((pp[0] == cp.getElement(1)) && (pp[1] == cp.getElement(2)))
				return cp;
		}
		return null;
	}

	// ///////////////////////////////////////////////////////////////////

	public TMono reduce(final TMono m) {
		return PolyBasic.reduce(PolyBasic.p_copy(m), parameter);
	}

	public void onDBClick(final GraphicEntity c) {
		if (c == null)
			return;
		final int t = c.get_type();
		switch (t) {
		case GraphicEntity.TEXT:
			final GEText tx = (GEText) c;
			if (tx.getType() == GEText.VALUE_TEXT) {
				final DialogTextValueEditor dlg = new DialogTextValueEditor(gxInstance);
				gxInstance.centerDialog(dlg);
				dlg.setText(tx);
				dlg.setVisible(true);
				break;

			} else {
				dialog_addText(tx, tx.getX(), tx.getY());
				break;
			}

		default:
			viewElement(c);
		}

	}

	public static double roundn(final double r, int n) {
		if (n <= 0)
			return r;

		double k = 1.0;
		while (n-- > 0)
			k *= 10;

		return (int) (r * k) / k;
	}

	public double calculate(final MathHelper ct) {
		if (ct == null)
			return 0.0;

		switch (ct.TYPE) {
		case MathHelper.PLUS:
			return calculate(ct.left) + calculate(ct.right);
		case MathHelper.MINUS:
			return calculate(ct.left) - calculate(ct.right);
		case MathHelper.MUL:
			return calculate(ct.left) * calculate(ct.right);
		case MathHelper.DIV:
			return calculate(ct.left) / calculate(ct.right);
		case MathHelper.SQRT:
			return Math.sqrt(calculate(ct.left));
		case MathHelper.SIN:
			return Math.sin(calculate(ct.left));
		case MathHelper.COS:
			return Math.cos(calculate(ct.left));
		case MathHelper.EXP:
			return Math.pow(calculate(ct.left), calculate(ct.right));
		case MathHelper.NODE:
			return parameter[ct.index - 1].value;
		case MathHelper.VALUE:
			return ct.dvalue;
		case MathHelper.PI:
			return Math.PI;
		case MathHelper.E:
			return Math.E;
		case MathHelper.FUNC:
			return MathHelper.calcFunction(ct.value, calculate(ct.left));
		case MathHelper.PARAM: {
			final MathHelper t = fd_para(ct.sname);
			if (t == null)
				return 0.0;
			else
				return t.dvalue;
		}

		}
		return 0.0;

	}

	MathHelper fd_para(final String s) {
		for (final GEText t : textlist)
			if ((t.getType() == GEText.VALUE_TEXT)
					&& t.getname().equalsIgnoreCase(s))
				return t.tvalue;
		return null;
	}

	final public void addCalculationPX(final GEPoint p) {
		if (p == null)
			return;
		final GEText tx = new GEText(5, 2, "x" + p.x1.xindex);
		tx.setTextType(GEText.VALUE_TEXT);
		tx.m_name = p.m_name + ".x";
		tx.m_width = 2;
		tx.m_dash = 3;

		if (tx.tvalue != null)
			tx.tvalue.calculate(this);
		getTextLocation(tx);
		addText(tx);

	}

	final public void addCalculationPY(final GEPoint p) {
		if (p == null)
			return;
		final GEText tx = new GEText(5, 2, "x" + p.y1.xindex);
		tx.setTextType(GEText.VALUE_TEXT);
		tx.m_name = p.m_name + ".x";
		tx.m_width = 2;
		tx.m_dash = 3;

		if (tx.tvalue != null)
			tx.tvalue.calculate(this);
		getTextLocation(tx);
		addText(tx);
	}

	final public void addCalculationPolygon(final GEPolygon poly) {
		if (poly == null)
			return;
		String area = getLanguage(461, "Area") + " ";
		final int n = poly.getPtn();
		for (int i = 0; i < (n - 1); i++)
			area += poly.getElement(i);

		final GEText tx = new GEText(5, 2, "");
		tx.setTextType(GEText.VALUE_TEXT);
		tx.m_name = area;
		tx.m_width = 1;
		tx.m_dash = 3;
		tx.father = poly;

		if (tx.tvalue != null)
			tx.tvalue.calculate(this);
		getTextLocation(tx);
		addText(tx);
	}

	final public void addLineSlope(final GELine ln) {
		if (ln == null)
			return;
		final GEPoint[] pp = ln.getTwoPointsOfLine();
		if (pp == null)
			return;
		if (pp.length != 2)
			return;
		final String s = "(x" + pp[0].x1.xindex + " - x" + pp[1].x1.xindex
				+ ") / (x" + pp[0].y1.xindex + " - x" + pp[1].y1.xindex + ")";
		final GEText tx = new GEText(5, 2, s);
		tx.setTextType(GEText.VALUE_TEXT);
		tx.m_name = "slope_" + ln.getname();
		tx.m_width = 1;
		tx.m_dash = 3;
		tx.father = ln;

		if (tx.tvalue != null)
			tx.tvalue.calculate(this);
		getTextLocation(tx);
		addText(tx);
	}

	final public void addCalculationCircle(final GECircle c, final int t) {
		if (c == null)
			return;
		final GEPoint[] pp = c.getRadiusPoint();
		if ((pp == null) || (pp.length == 0))
			return;
		String s, sname;
		if (t == 0) {
			s = MathHelper.SPI + " * " + "((x" + pp[0].x1.xindex + " - x"
					+ pp[1].x1.xindex + ")^2 + (x" + pp[0].y1.xindex + " - x"
					+ pp[1].y1.xindex + ")^2)";
			sname = getLanguage(461, "Area") + " " + c.m_name;
		} else if (t == 1) {
			s = "2 * " + MathHelper.SPI + " * sqrt((x" + pp[0].x1.xindex
					+ " - x" + pp[1].x1.xindex + ")^2 + (x" + pp[0].y1.xindex
					+ " - x" + pp[1].y1.xindex + ")^2)";
			sname = getLanguage(460, "Girth") + " " + c.m_name;
		} else {
			s = "sqrt((x" + pp[0].x1.xindex + " - x" + pp[1].x1.xindex
					+ ")^2 + (x" + pp[0].y1.xindex + " - x" + pp[1].y1.xindex
					+ ")^2)";
			sname = getLanguage(4004, "Radius") + " " + c.m_name;
		}

		final GEText tx = new GEText(5, 2, s);
		tx.setTextType(GEText.VALUE_TEXT);
		tx.m_name = sname;
		tx.m_width = 1;
		tx.m_dash = 3;
		tx.father = c;

		if (tx.tvalue != null)
			tx.tvalue.calculate(this);
		getTextLocation(tx);
		addText(tx);
	}

	public void getTextLocation(final GEText t1) {
		int n = 0;
		int n1 = 5;
		for (int i = 0; i < textlist.size(); i++) {
			final GEText t = textlist.get(i);
			if (t.getType() == 3) {
				n = (int) (t.getY() + t.height + 3);
				n1 = t.getX();
			}
		}
		t1.setXY(n1, n);
	}

	public void flashAllNonVisibleObjects() {
	}

	public void cancelCurrentAction() {

		final int type = CurrentAction;
		if ((type != MOVE) && (CurrentAction == CONSTRUCT_FROM_TEXT))
			clearFlash();

		CurrentAction = type;
		clearSelection();

		if (type == SETTRACK)
			CTrackPt = null;

		FirstPnt = SecondPnt = null;
		if (type != D_POLYGON)
			STATUS = 0;
		CatchList.clear();
		vtrx = vtry = vx1 = vy1 = vangle = 0;
		if ((panel != null) && (gxInstance != null))
			panel.repaint();

		if (null != UndoAdded("", false, false))
			Undo_stepPure();
	}

	public void hightlightAllInvisibleObject() {
		final Collection<Object> v = new ArrayList<Object>();
		getAllSolidObj(v);

		SelectList.clear();
		for (final Object o : v) {
			if ((o != null)
					&& ((o instanceof GraphicEntity) && !((GraphicEntity) o).visible()))
				SelectList.add((GraphicEntity) o);
		}
	}

	public static int getActionType(final int ac) // -1. ByPass Action; 0.
	// defalut;
	// 1. Draw Action + point; 2: draw action line + circle
	// 3: fill action 4: angle 5: move/select/intersect
	{
		switch (ac) {
		case D_POINT:
			return 1;
		case D_LINE:
		case D_PARELINE:
		case D_PERPLINE:
		case PERPWITHFOOT:
		case D_POLYGON:
		case D_CIRCLE:
		case D_3PCIRCLE:
			return 2;
		case D_MIDPOINT:
			return 1;
		case D_PSQUARE:
			return 2;
		case D_TEXT:
			return 0;
		case D_PFOOT:
			return 2;
		case D_CIRCLEBYRADIUS:
		case D_PTDISTANCE:
		case D_CCLINE:
		case D_SQUARE:
			return 1;
		case LRATIO:
		case D_PRATIO:
			return 2;
		case CIRCUMCENTER:
		case BARYCENTER:
		case ORTHOCENTER:
		case INCENTER:
			return 1;
		case D_TRATIO:
			return 2;
		case D_ANGLE:
			return 4;
		case SETEQANGLE:
		case MEET:
			return 5;
		case D_IOSTRI:
			return 2;
		case MIRROR:
			return 5;
		case DISTANCE:
			return 0;
		case H_LINE:
		case V_LINE:
		case D_ALINE:
		case D_ABLINE:
		case D_BLINE:
		case D_CIR_BY_DIM:
		case D_TCLINE:
			return 2;
		case CCTANGENT:
		case SELECT:
		case MOVE:
		case VIEWELEMENT:
		case TRANSLATE:
		case ZOOM_IN:
		case ZOOM_OUT:
		case SETTRACK:
		case ANIMATION:
			return 5;
		case DEFINEPOLY:
			return 3;
		case MULSELECTSOLUTION:
		case MOVENAME:
		case AUTOSHOWSTEP:
			return 5;
		case EQMARK:
		case PROVE:
			return 3;
		case TRIANGLE:
			return 2;
		case HIDEOBJECT:
			return 5;
		case DRAWTRIALL:
		case DRAWTRISQISO:
		case PARALLELOGRAM:
		case RECTANGLE:
		case TRAPEZOID:
		case RA_TRAPEZOID:
			return 2;
		case SETEQSIDE:
		case SHOWOBJECT:
		case SETEQANGLE3P:
		case SETCCTANGENT:
		case NTANGLE:
			return 5;
		case SANGLE:
		case RATIO:
			return 5;
		case RAMARK:
			return 2;
		case TRANSFORM:
		case EQUIVALENCE:
		case FREE_TRANSFORM:
			return 5;
		case LOCUS:
			return 2;
		case ARROW:
			return 2;
		case CONSTRUCT_FROM_TEXT:
			return 5;
		}
		return -1;
	}

	public void addCTMark(final GELine ln1, final GELine ln2) {
		if ((ln1 == null) || (ln2 == null))
			return;
		if (!GELine.isPerp(ln1, ln2)) {
			JOptionPane.showMessageDialog(gxInstance,
					"The selected two lines are not perpendicular");
			return;
		}

		final GETMark m = new GETMark(ln1, ln2);
		addGraphicEntity(otherlist, m);
	}

	public void addCTMark(final GEPoint p1, final GEPoint p2, final GEPoint p3,
			final GEPoint p4) {
		addCTMark(findLineGivenTwoPoints(p1, p2), findLineGivenTwoPoints(p3, p4));
	}

	public void PolygonTransPointsCreated(final GEPolygon poly) {
		GEPoint pt0, pt1;
		pt0 = pt1 = null;

		final int n = poly.getPtn();
		final double cx = catchX + vx1;
		final double cy = catchY + vy1;
		final double sin = Math.sin(vangle);
		final double cos = Math.cos(vangle);

		for (int i = 0; i < n; i++) {
			final GEPoint t = poly.getPoint(i);
			double tx = (t.getx() + vx1);
			double ty = (t.gety() + vy1);

			tx -= cx;
			ty -= cy;
			final double mx = ((tx) * cos) - ((ty) * sin);
			final double my = ((tx) * sin) + ((ty) * cos);
			tx = mx + cx;
			ty = my + cy;

			final GEPoint t1 = SelectAPoint(tx, ty);
			if ((t1 != null) && (t1 != t)) {
				pt0 = t;
				pt1 = t1;
			}
		}
		if ((pt0 == null) || (pt1 == null))
			return;

		for (int i = 0; i < n; i++) {
			final GEPoint t = poly.getPoint(i);
			double tx = (t.getx() + vx1);
			double ty = (t.gety() + vy1);

			tx -= cx;
			ty -= cy;
			final double mx = ((tx) * cos) - ((ty) * sin);
			final double my = ((tx) * sin) + ((ty) * cos);
			tx = mx + cx;
			ty = my + cy;

			addOrientedSegment(pt0, pt1, t, tx, ty);
			// CPoint t1 = SmartgetApointFromXY(tx, ty);
		}
	}

	public void addOrientedSegment(final GEPoint p1, final GEPoint p2,
			final GEPoint px, final double x, final double y) {
		final GEPoint p = CreateANewPoint(x, y);

		final Constraint cs = new Constraint(Constraint.PRATIO, p, px, p1, p2,
				new Integer(1), new Integer(1));
		final GEPoint pu = addADecidedPointWithUnite(p);
		if (pu == null) {
			addConstraintToList(cs);
			addPointToList(p);
		}
	}

	public void setTextPositionAutomatically(final GEText tex) {
		if (tex.getType() != GEText.NAME_TEXT)
			return;

		// Point o = tex.getLocation();
		// double w = tex.w;
		// double h = tex.height;
		final double r = 15;

		final GraphicEntity c = tex.father;
		if ((c != null) && (c.get_type() == GraphicEntity.POINT)) {
			final GEPoint px = (GEPoint) c;
			final double x = tex.getX();
			final double y = tex.getY();
			final double dx = x;// - px.getx();
			final double dy = y;// - px.gety();
			final double dpi = Math.PI / 16;
			boolean bfound = false;
			double sx, sy;
			sx = sy = 0;
			for (int i = 0; i < 16; i++) {
				if (bfound)
					break;
				double sta = i * dpi;
				for (int j = 0; j < 2; j++) {
					sx = (dx * Math.cos(sta)) - (dy * Math.sin(sta));
					sy = (dx * Math.sin(sta)) + (dy * Math.cos(sta));
					if (!intsWithCircle(sx + px.getx(), sy + px.gety(), r)) {
						bfound = true;
						break;
					}
					if (bfound)
						break;
					sta *= -1;
				}
			}
			if (bfound)
				tex.setXY((int) (sx), (int) (sy));
		}

	}

	public boolean intsWithCircle(final double ptx, final double pty,
			final double r) {
		for (final GEPoint p : pointlist) {
			double ds = Math.pow(p.getx() - ptx, 2)
					+ Math.pow(p.gety() - pty, 2);
			ds = Math.sqrt(ds);
			if (ds < r)
				return true;
		}

		for (final GELine ln : linelist) {
			final double d = ln.distance(ptx, pty);
			if (d < r)
				if (ln.isOnMiddle(ptx, pty))
					return true;
		}
		for (final GECircle c : circlelist) {
			final double d = Math.sqrt(Math.pow(c.o.getx() - ptx, 2)
					+ Math.pow(c.o.gety() - pty, 2));
			if (Math.abs(d - c.getRadius()) < r)
				return true;
		}
		return false;
	}
}
