package wprover;

import gprover.cons;
import gprover.gib;

import java.awt.*;
import java.io.File;
import java.util.*;

import javax.swing.JOptionPane;

import maths.*;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-5-2
 * Time: 21:27:59
 * To change this template use File | Settings | File Templates.
 */
public class DrawPanelBase {
    final public static int D_POINT = 1;
    final public static int D_LINE = 2;
    final public static int D_PARELINE = 3;
    final public static int D_PERPLINE = 4;
    final public static int PERPWITHFOOT = 5;
    final public static int D_POLYGON = 6;
    final public static int D_CIRCLE = 9;
    final public static int D_3PCIRCLE = 10;
    final public static int D_MIDPOINT = 15;
    final public static int D_PSQUARE = 16;
    final public static int D_TEXT = 23;
    final public static int D_PFOOT = 25;
    final public static int D_CIRCLEBYRADIUS = 28;
    final public static int D_PTDISTANCE = 29;
    final public static int D_CCLINE = 21;
    final public static int D_SQUARE = 19;
    final public static int LRATIO = 30;
    final public static int D_PRATIO = 31;
    final public static int CIRCUMCENTER = 32;
    final public static int BARYCENTER = 33;
    final public static int ORTHOCENTER = 46;
    final public static int INCENTER = 47;
    final public static int D_TRATIO = 36;
    final public static int D_ANGLE = 17;
    final public static int SETEQANGLE = 18;
    final public static int MEET = 20;
    final public static int D_IOSTRI = 22;
    final public static int MIRROR = 24;
    final public static int DISTANCE = 26;

    final public static int H_LINE = 44;
    final public static int V_LINE = 45;
    final public static int D_ALINE = 50;
    final public static int D_ABLINE = 51; // Angle bisector line
    final public static int D_BLINE = 52;

    final public static int D_CIR_BY_DIM = 53;
    final public static int D_TCLINE = 54; // Line tangent to a circle
    final public static int CCTANGENT = 55;


    protected ArrayList<GEPoint> pointlist = new ArrayList<GEPoint>();
    protected ArrayList<GELine> linelist = new ArrayList<GELine>();
    protected ArrayList<GECircle> circlelist = new ArrayList<GECircle>();
    protected ArrayList<GEAngle> anglelist = new ArrayList<GEAngle>();
    protected ArrayList<Constraint> constraintlist = new ArrayList<Constraint>();
    protected ArrayList<GEDistance> distancelist = new ArrayList<GEDistance>();
    protected ArrayList<GEPolygon> polygonlist = new ArrayList<GEPolygon>();
    protected ArrayList<GEText> textlist = new ArrayList<GEText>();
    protected ArrayList<GETrace> tracelist = new ArrayList<GETrace>();
    protected ArrayList<GraphicEntity> otherlist = new ArrayList<GraphicEntity>();
    protected HashMap<Integer, GraphicEntity> gemap = new HashMap<Integer, GraphicEntity>();

    ArrayList<Flash> flashlist = new ArrayList<Flash>();


    protected ArrayList<GraphicEntity> SelectList = new ArrayList<GraphicEntity>();
    protected ArrayList<GraphicEntity> CatchList = new ArrayList<GraphicEntity>();
    protected GEPoint CatchPoint = createTempPoint(0, 0);

    protected int MouseX, MouseY, mouseCatchX, mouseCatchY;
    protected int CatchType = 0;  // 1. middle ,  2. x pt,  3. y pt, 4: x & y.


	protected param[] parameter = new param[1024];
    protected double[] pptrans = new double[4];

    protected double[] paraBackup = new double[1024];
    protected TPoly polylist = null;
    protected TPoly pblist = null;

    protected int CurrentAction = 0;

    protected String name = "";
    protected double Width = 0;
    protected double Height = 0;

    protected int GridX = 40;
    protected int GridY = 40;
    protected boolean DRAWGRID = false;
    protected boolean SNAP = false;

    protected Color gridColor = UtilityMiscellaneous.getGridColor(); //APPLET ONLY

    protected int pnameCounter = 0;
    protected int plineCounter = 1;
    protected int pcircleCounter = 1;

    protected boolean isPointOnObject = false;
    protected boolean isPointOnIntersection = false;
    protected double catchX, catchY;
    protected File file;
    protected boolean mouseInside = false;

    protected int STATUS = 0;

    protected DrawPanelFrame gxInstance;
    protected Language lan;

	/** 
	 * POOL is a constant matrix whose rows are filled with an integer (in the first column) representing a kind of geometric construction
	 * and whose other columns are filled with an integer representing the kind of argument that can be used for that geometric construction.
	 * <p>
	 * 1 designates a point, 2 designates a line, 3 designates a circle, 4 designates either a circle or line
	 * <p>
	 * For example, the row {D_PERPLINE, 2, 1} designates that a construction of a perpendicular from a line requires the specification
	 * of a line as the first argument, and a point as the second argument.
	 * 
	 * POOL in effect serves as a map whose key are the constants defined in the <code>drawbase</code> class, and whose values are a list of argument types
	 * that can be used to limit the kinds of entities the user is allowed to select to define his desired construction.
	 * 
	 * This matrix ought to be replaced with a HashMap<String, ArrayList<HashSet<enum>>> for better reliability as changes are made to the kinds of constructions available.
	 */
    final public static int[][] POOL = 
            {
                    {D_LINE, 1, 1},
                    {D_PARELINE, 2, 1}, // Line parallel to the chosen line through the chosen point.
                    {D_PERPLINE, 2, 1}, // Line perpendicular to the chosen line through the chosen point.
                    {PERPWITHFOOT, 1, 2},
                    {D_POLYGON, 1},
                    {D_CIRCLE, 1, 1}, // Circle with selected center and one point on the circumference.
                    {D_3PCIRCLE, 1, 1, 1}, // Circle whose circumference strikes all three selected points.
                    {D_MIDPOINT, 1, 1},
                    {D_PFOOT, 1, 1},
                    {D_CIRCLEBYRADIUS, 1, 1, 1},
                    {D_PTDISTANCE, 1, 1, 1, 4},
                    {D_CCLINE, 3, 3}, // Line passing through the centers of two selected circles.
                    {D_ABLINE, 2, 2},
                    {D_PRATIO, 1, 1, 1},
                    {D_ALINE, 2, 2, 2, 1},
                    {D_BLINE, 1, 1},
                    {D_TCLINE, 3, 1}, // Tangent to a circle at selected point.
                    {MEET, 4, 4},
                    {PERPWITHFOOT, 1, 2},
                    {D_ANGLE, 2, 2},
                    {60, 1, 4},
                    {79, 3, 3},
                    {76, 1, 1, 1, 1},
                    {D_SQUARE, 1, 1},
                    {D_CCLINE, 3, 3},
                    {D_IOSTRI, 1, 1},
                    {MIRROR, 4, 2},
//                    {D_PFOOT, 1, 1},
                    {87, 1, 1, 4},
                    {43, 1},
                    {D_PTDISTANCE, 1, 1},
                    {CIRCUMCENTER, 1, 1, 1},
                    {BARYCENTER, 1, 1, 1},
                    {ORTHOCENTER, 1, 1, 1},
                    {INCENTER, 1, 1, 1},
                    {DISTANCE, 1, 1},
                    {65, 1, 1},
                    {72, 1, 1, 1},
                    {73, 1, 1},
                    {74, 1, 1, 1, 1},
                    {75, 1, 1},
                    {82, 1, 1, 1, 1, 1, 1, 1, 1},
                    {81, 2, 1}

//                    {29, 1, 1, 1, 4}
//                    {D_PFOOT, 1, 1}


            };


    final public int getPooln(int a) {
        if (a == D_POLYGON) {
            if (STATUS < 10)
                return STATUS;
            else return 0;
        }

        for (int i = 0; i < POOL.length; i++) {
            if (POOL[i][0] == a)
                return POOL[i].length - 1;
        }

        return -1;
    }

    public static int getPoolA(int a, int index) {
        for (int i = 0; i < POOL.length; i++) {
            if (POOL[i][0] == a) {
                if (POOL[i].length > index)
                    return POOL[i][index];
            }
        }
        return 1;
    }

    public void setLanguage(Language l) {
        lan = l;
    }

    public String getLanguage(int n, String s) {
        if (lan == null)
            return s;

        String s1 = lan.getString(n);
        if (s1 != null && !s1.isEmpty())
            return s1;
        return s;
    }

    public void DWButtonDown(double x, double y) {
        switch (CurrentAction) {
        }
    }

    public void setMouseInside(boolean t) {
        mouseInside = t;
    }

    final public static GEPoint createTempPoint(double x, double y) {
        param p1 = new param(-1, x);
        param p2 = new param(-1, y);
        return new GEPoint(GraphicEntity.TEMP_POINT, p1, p2);
    }

    public GELine findLineGivenTwoPoints(GEPoint p1, GEPoint p2) {
        if (p1 != null && p2 != null) {
	        for (GELine ln : linelist) {
	            if (ln.points.contains(p1) && ln.points.contains(p2)) {
	                return ln;
	            }
	        }
        }
        return null;
    }

    final public static void setAntiAlias(Graphics2D g2) {
        if (UtilityMiscellaneous.AntiAlias) {
            RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHints(qualityHints);
        } else {
            RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setRenderingHints(qualityHints);
        }
    }

    void paint(Graphics2D g2) {
        drawList(polygonlist, g2);
        drawSelect(SelectList, g2);
        drawPerpFoot(g2, null, 0);
        drawList(tracelist, g2);
        drawList(distancelist, g2);
        drawList(anglelist, g2);
        drawList(circlelist, g2);
        drawList(linelist, g2);
        drawList(pointlist, g2);
        drawList(textlist, g2);
        drawList(otherlist, g2);
        drawCatch(g2);
    }

    final public void drawPerpFoot(Graphics2D g2, ArrayList<Point> vlist, int type) { // 0: draw ,1: ps
        for (Constraint cs : constraintlist) {
            double x, y;
            int n = cs.GetConstraintType();
            switch (n) {

                case Constraint.PERPENDICULAR: {
                    if (cs.getelement(0) instanceof GEPoint)
                        continue;

                    GEPoint p1, p2;
                    GELine line1 = (GELine) cs.getelement(0);
                    GELine line2 = (GELine) cs.getelement(1);
                    if (!line1.isdraw() || !line2.isdraw())
                        continue;
                    GEPoint pt = null;
                    if ((pt = GELine.commonPoint(line1, line2)) == null) {
                        double lc[] = GELine.Intersect(line1, line2);
                        if (lc == null) {
                            continue;
                        }
                        x = lc[0];
                        y = lc[1];
                        if (!line1.inside(x, y) || !(line2.inside(x, y))) {
                            continue;
                        }
                        p1 = line1.getMaxXPoint();
                        p2 = line2.getMaxXPoint();
                    } else {
                        x = pt.getx();
                        y = pt.gety();
                        p1 = line1.getAPointBut(pt);
                        p2 = line2.getAPointBut(pt);
                    }
                    drawTTFoot(type, vlist, g2, x, y, null, p1, p2);
                }
                break;
                case Constraint.PFOOT: {
                    GEPoint PC = null;
                    GEPoint p1, p2;

                    PC = (GEPoint) cs.getelement(0);
                    x = PC.getx();
                    y = PC.gety();
                    p1 = (GEPoint) cs.getelement(1);
                    GEPoint tp1 = (GEPoint) cs.getelement(2);
                    GEPoint tp2 = (GEPoint) cs.getelement(3);
                    if (tp1.getx() > tp2.getx()) {
                        p2 = tp1;
                    } else {
                        p2 = tp2;
                    }
                    if (!this.find_tmark(PC, tp1, PC, tp2))
                        drawTTFoot(type, vlist, g2, x, y, PC, p1, p2);
                }
                break;
                case Constraint.SQUARE:
                case Constraint.RECTANGLE: {
                    GEPoint p1 = (GEPoint) cs.getelement(0);
                    GEPoint p2 = (GEPoint) cs.getelement(1);
                    GEPoint p3 = (GEPoint) cs.getelement(2);
                    GEPoint p4 = (GEPoint) cs.getelement(3);

                    drawTTFoot(type, vlist, g2, p1.getx(), p1.gety(), p1, p2, p4);
                    drawTTFoot(type, vlist, g2, p2.getx(), p2.gety(), p2, p1, p3);
                    drawTTFoot(type, vlist, g2, p3.getx(), p3.gety(), p3, p2, p4);
                    drawTTFoot(type, vlist, g2, p4.getx(), p4.gety(), p4, p1, p3);

                }
                break;
                case Constraint.RIGHT_ANGLED_TRIANGLE: {
                    GEPoint p1 = (GEPoint) cs.getelement(0);
                    GEPoint p2 = (GEPoint) cs.getelement(1);
                    GEPoint p3 = (GEPoint) cs.getelement(2);
                    drawTTFoot(type, vlist, g2, p1.getx(), p1.gety(), p1, p2, p3);

                }
                break;
                case Constraint.RIGHT_ANGLE_TRAPEZOID: {
                    GEPoint p1 = (GEPoint) cs.getelement(0);
                    GEPoint p2 = (GEPoint) cs.getelement(1);
                    GEPoint p3 = (GEPoint) cs.getelement(2);
                    GEPoint p4 = (GEPoint) cs.getelement(3);
                    drawTTFoot(type, vlist, g2, p1.getx(), p1.gety(), p1, p2, p4);
                    drawTTFoot(type, vlist, g2, p4.getx(), p4.gety(), p4, p1, p3);
                }
                break;
                default:
                    break;
            }
        }

    }

    final public int getNumberOfPoints() {
        return pointlist.size();
    }

    final public void getPointList(Collection<GEPoint> c) {
        if (c != null)
        	c.addAll(pointlist);
    }

    final public void drawList(ArrayList<? extends GraphicEntity> list, Graphics2D g2) {
        for (GraphicEntity ge : list) {
	    	if (ge != null)
	    		ge.draw(g2, SelectList.contains(ge));
        }
    }

    final public static void drawPointNameLocation(GEPoint p, Graphics2D g2) {
        g2.drawString("(x: " + new Integer((int) p.getx()).toString() + ", y: " +
                new Integer((int) p.gety()).toString() + ")",
                (int) p.getx() + 23, (int) p.gety() - 5);
    }

    final public static void setCurrentDrawEnvironment(Graphics2D g2) {
        g2.setColor(DrawData.getCurrentColor());
        g2.setStroke(UtilityMiscellaneous.NormalLineStroke);
    }


    final public void drawGrid(Graphics2D g2) {
        if (DRAWGRID || SNAP) {
	        if (UtilityMiscellaneous.isApplication())
	            g2.setColor(UtilityMiscellaneous.getGridColor());
	        else
	            g2.setColor(gridColor); //APPLET ONLY.
	
	        int x = 0;
	        while (x < Width) {
	        	g2.drawLine(x, 0, x, (int) Height);
	        	x += GridX;
	        }
	
	        int y = 0;
	        while (y < Height) {
	        	g2.drawLine(0, y, (int) Width, y);
	        	y += GridY;
	        }
        }
    }

    final public void drawTipTriangle(GEPoint p1, GEPoint p2, GEPoint p,
                                      Graphics2D g2) {

        double x0 = p1.getx();
        double y0 = p1.gety();

        double rx = p2.getx() - x0;
        double ry = p2.gety() - y0;
        double dx = p.getx() - x0;
        double dy = p.gety() - y0;
        double rr = Math.sqrt(rx * rx + ry * ry);
        double cy = ry / rr;
        double cx = rx / rr;
        double r;
        boolean isleft = false;

        if (Math.abs(rx) < UtilityMiscellaneous.ZERO) {
        	isleft = !(ry * (p1.getx() - p.getx()) > 0);
            r = Math.abs(p1.getx() - p.getx());
        } else {
            double k = ry / rx;
            r = Math.abs((p.gety() - k * p.getx() + k * p1.getx() - p1.gety())) / Math.sqrt(1 + k * k);
            isleft = (rx * dy - ry * dx < 0);
        }

        double x1 = (x0 + p2.getx()) / 2;
        double y1 = (y0 + p2.gety()) / 2;
        double x2, y2;
        if (isleft) {
            x2 = (x1 + r * cy);
            y2 = (y1 - r * cx);
        } else {
            x2 = x1 - r * cy;
            y2 = y1 + r * cx;
        }
        CatchPoint.setXY(x2, y2);
        drawCatchRect(g2);

        g2.setColor(Color.red);
        g2.drawLine((int) (x0), (int) (y0), (int) (x2), (int) (y2));
        g2.drawLine((int) p2.getx(), (int) p2.gety(), (int) (x2), (int) (y2));
        g2.drawLine((int) (p1.getx()), (int) (p1.gety()), (int) (p2.getx()), (int) (p2.gety()));
        g2.setStroke(UtilityMiscellaneous.DashedStroke);

        if (Math.abs(cy) < UtilityMiscellaneous.ZERO) {
            g2.drawLine((int) x1, 0, (int) x1, (int) this.Height);
        } else {
            double k = -cx / cy;
            g2.drawLine((0), (int) (y1 - x1 * k), (int) (this.Width),
                    (int) (y1 + (this.Width - x1) * k));
        }
        catchX = x2;
        catchY = y2;
    }

    final public static void drawCross(int x, int y, int w, Graphics2D g2) {
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawLine(x - w, y - w, x + w, y + w);
        g2.drawLine(x + w, y - w, x - w, y + w);
    }

    public void drawCatchRect(Graphics2D g2) {
        if (!isPointOnObject || !mouseInside) return;
        int x = (int) CatchPoint.getx();
        int y = (int) CatchPoint.gety();
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(1.0f));
        if (!isPointOnIntersection) {
            drawRect(x - 5, y - 5, x + 5, y + 5, g2);
            if (CatchType == 1)
                g2.drawString("Middle Point", x + 10, y);
        } else {
            drawCatchInterCross(g2);
        }
    }

    public void drawCatchInterCross(Graphics2D g2) {
        if (!isPointOnIntersection) return;
        int x = (int) CatchPoint.getx();
        int y = (int) CatchPoint.gety();
        g2.setColor(Color.red);
        DrawPanelBase.drawCross(x, y, 5, g2);
        g2.setFont(UtilityMiscellaneous.font);
        g2.drawString(getLanguage(232, "Intersection"), x + 10, y);
    }

    public static void drawTipRect(int x, int y, Graphics2D g2) {
        g2.setColor(Color.red);
        DrawPanelBase.drawRect(x - 5, y - 5, x + 5, y + 5, g2);
    }

    public void drawCatchCross(Graphics2D g2) {
        if (!isPointOnObject || !mouseInside) return;
        int x = (int) CatchPoint.getx();
        int y = (int) CatchPoint.gety();
        drawCross(x, y, 5, g2);
    }

    public void drawPointOrCross(Graphics2D g2) {
        if (isPointOnObject) {
            if (!isPointOnIntersection)
                DrawPanelBase.drawCross((int) CatchPoint.getx(), (int) CatchPoint.gety(), 5, g2);
            else
                drawCatchInterCross(g2);
        } else {
            drawpoint(CatchPoint, g2);
        }
    }

    public void drawCatchObjName(Graphics2D g2) {
        if (CatchList.size() != 1)
            return;
        if (!UtilityMiscellaneous.DRAW_CATCH_OBJECT_NAME)
            return;
        GraphicEntity c = CatchList.get(0);
        g2.setColor(Color.red);
        g2.setFont(UtilityMiscellaneous.font);
        String s = c.getname();
        if (s != null)
            g2.drawString(s, MouseX + 16, MouseY + 20);
    }

    final public static void drawTipSquare(GEPoint p1, GEPoint p2, GEPoint p,
                                    Graphics2D g2) {
        double x0 = p1.getx();
        double y0 = p1.gety();

        double rx = p2.getx() - x0;
        double ry = p2.gety() - y0;
        double dx = p.getx() - x0;
        double dy = p.gety() - y0;
        double rr = Math.sqrt(rx * rx + ry * ry);
        double cy = ry / rr;
        double cx = rx / rr;
        double r;
        boolean isleft = false;

        if (Math.abs(rx) < UtilityMiscellaneous.ZERO) {
            if (ry * (p1.getx() - p.getx()) > 0) {
                isleft = false;
            } else {
                isleft = true;
            }
            r = Math.abs(p1.getx() - p.getx());
        } else {
            double k = ry / rx;
            r = Math.abs((p.gety() - k * p.getx() + k * p1.getx() - p1.gety())) /
                    Math.sqrt(1 + k * k);
            isleft = (rx * dy - ry * dx < 0); //((ry * dx / rx - dy > 0 && ry / rx > 0) || (ry * dx / rx - dy < 0 && ry / rx < 0));
        }

        int n = (int) (r / rr) + 1;
        if (Math.abs(n * rr - r) < 2 * UtilityMiscellaneous.PIXEPS) {
            r = rr * n;
        }

        g2.setColor(Color.red);
        g2.drawLine((int) x0, (int) y0, (int) p2.getx(), (int) p2.gety());
        if (isleft) {
            for (int i = 1; i <= n; i++) {
                g2.drawLine((int) x0, (int) y0, (int) (x0 + i * ry),
                        (int) (y0 - i * rx));
                g2.drawLine((int) (x0 + i * ry), (int) (y0 - i * rx),
                        (int) (x0 + i * ry + rx), (int) (y0 - i * rx + ry));
                g2.drawLine((int) (x0 + rx), (int) (y0 + ry),
                        (int) (x0 + i * ry + rx), (int) (y0 - i * rx + ry));
            }
            g2.drawLine((int) (p1.getx() + r * cy), (int) (p1.gety() - r * cx),
                    (int) (p2.getx() + r * cy), (int) (p2.gety() - r * cx));
        } else {
            for (int i = 1; i <= n; i++) {
                g2.drawLine((int) x0, (int) y0, (int) (x0 - i * ry),
                        (int) (y0 + i * rx));
                g2.drawLine((int) (x0 + rx), (int) (y0 + ry),
                        (int) (x0 + rx - i * ry), (int) (y0 + ry + i * rx));
                g2.drawLine((int) (x0 + rx - i * ry), (int) (y0 + ry + i * rx),
                        (int) (x0 - i * ry), (int) (y0 + i * rx));
            }
            g2.drawLine((int) (p1.getx() - r * cy), (int) (p1.gety() + r * cx),
                    (int) (p2.getx() - r * cy), (int) (p2.gety() + r * cx));
        }
    }

    protected boolean footMarkShown = UtilityMiscellaneous.isFootMarkShown();
    protected double footMarkLength = UtilityMiscellaneous.FOOT_MARK_LENGTH;

    public void drawTTFoot(int type, ArrayList<Point> vlist, Graphics2D g2, double x, double y, GEPoint pc, GEPoint p1, GEPoint p2) {
        if (p1 == null || p2 == null) return;

        if (UtilityMiscellaneous.isApplication() && !UtilityMiscellaneous.isFootMarkShown()) return;

        if (UtilityMiscellaneous.isApplet() && !footMarkShown) return; //APPLET ONLY.

        if (!isLineDrawn(pc, p1) || !isLineDrawn(pc, p2))
            return;
        if (this.findCTMark(pc, p1, pc, p2) != null)
            return;

        double step = footMarkLength;  //APPLET ONLY.
        if (UtilityMiscellaneous.isApplication())
            step = UtilityMiscellaneous.FOOT_MARK_LENGTH;

        double dx = p1.getx() - x;
        double dy = p1.gety() - y;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0.0) return;
        dx = (dx / len) * step;
        dy = (dy / len) * step;

        double dx1, dy1;
        dx1 = p2.getx() - x;
        dy1 = p2.gety() - y;
        len = Math.sqrt(dx1 * dx1 + dy1 * dy1);
        if (len == 0.0) return;
        dx1 = (dx1 / len) * step;
        dy1 = (dy1 / len) * step;

        double fx = x;
        double fy = y;
        double ex = fx + dx1 + dx;
        double ey = fy + dy1 + dy;
        if (type == 0) {
            g2.setColor(Color.red);
            g2.setStroke(UtilityMiscellaneous.NormalLineStroke);
            g2.drawLine((int) (fx + dx), (int) (fy + dy), (int) (ex), (int) (ey));
            g2.drawLine((int) (fx + dx1), (int) (fy + dy1), (int) (ex), (int) (ey));
        } else {
            Point m1 = new Point((int) (fx + dx), (int) (fy + dy));
            Point m2 = new Point((int) (ex), (int) (ey));
            Point m3 = new Point((int) (fx + dx1), (int) (fy + dy1));
            Point m4 = new Point((int) (ex), (int) (ey));
            vlist.add(m1);
            vlist.add(m2);
            vlist.add(m3);
            vlist.add(m4);
        }
    }

    public void drawCatch(Graphics2D g2) {
        int size = CatchList.size();

        int x = (int) CatchPoint.getx();
        int y = (int) CatchPoint.gety();

        GraphicEntity cc = null;
        if (size == 0) {
            if (UtilityMiscellaneous.SMART_HV_LINE_CATCH) {
                if (CatchType == 2 || CatchType == 4) {
                    GEPoint pt = this.getCatchHVPoint(2);
                    if (pt != null) {
                        g2.setColor(Color.red);
                        g2.setStroke(UtilityMiscellaneous.DashedStroke);
                        g2.drawLine((int) pt.getx(), (int) pt.gety(), (int) pt.getx(), y);
                    }
                }
                if (CatchType == 3 || CatchType == 4) {
                    GEPoint pt = this.getCatchHVPoint(3);
                    if (pt != null) {
                        g2.setColor(Color.red);
                        g2.setStroke(UtilityMiscellaneous.DashedStroke);
                        g2.drawLine((int) pt.getx(), (int) pt.gety(), x, (int) pt.gety());
                    }
                }
            }
        } else if (size == 1) {
            cc = CatchList.get(0);
            cc.prepareToBeDrawnAsUnselected(g2);
            if (cc.m_type == GraphicEntity.POLYGON) {
                Color c = cc.getColor();
                g2.setColor(new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue()));
            }
        } else {
            if (!isPointOnIntersection) {
                g2.setFont(UtilityMiscellaneous.font);
                g2.setColor(Color.red);
                g2.drawString("(" + size + ") Which?", x + 10, y + 25);
            }
        }

    }

    public boolean isLineDrawn(GEPoint p1, GEPoint p2) {
        GELine ln = findLineGivenTwoPoints(p1, p2);
        return ln != null && ln.isdraw();
    }

    public static void drawSelect(ArrayList<GraphicEntity> list, Graphics2D g2) {
        for (GraphicEntity ge : list) {
            if (ge != null)
                ge.draw(g2, true);
        }
    }


    public static void drawRect(int x, int y, int x1, int y1, Graphics2D g2) {
        g2.drawLine(x, y, x1, y);
        g2.drawLine(x, y, x, y1);
        g2.drawLine(x, y1, x1, y1);
        g2.drawLine(x1, y, x1, y1);
    }


    public static void drawcircle2p(double x1, double y1, double x2, double y2, Graphics2D g2) {
        int r = (int) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        g2.drawOval((int) (x1 - r), (int) (y1 - r), 2 * r, 2 * r);
    }


    public static void drawpoint(GEPoint p, Graphics2D g2) {
        p.draw(g2, false); // Before edit, this method called draw(g2) instead of draw(g2, false). I don't know why two different draw methods were implemented.
    }

    public void addLine(GELine ge) {
        addGraphicEntity(linelist, ge);
    }

    public void addCircle(GECircle ge) {
        addGraphicEntity(circlelist, ge);
    }

    public void selectGraphicEntity(GraphicEntity ge) {
    	SelectList.add(ge);
    }    
    
    public <T extends GraphicEntity> boolean addGraphicEntity(Collection<T> collection, T ge) {
        Integer id = ge.id();
        GraphicEntity existingGe = gemap.get(id);
        if (existingGe == null) {
        	assert(!gemap.containsValue(ge));
            gemap.put(id, ge);
            return collection.add(ge);
        } else {
        	if (existingGe != ge)
        		System.err.println(); // This is triggered (sometimes) when user already has some geometry on the board and loads a new file, other times when a point is added where there is already an intersection (without the GEPoint)
        }
        return false;
    }

    public <T extends GraphicEntity> GraphicEntity removeGraphicEntity(Collection<T> collection, T ge) {
        Integer id = ge.id();
        GraphicEntity existingGe = gemap.get(id);
        if (existingGe == null) {
        	assert(!gemap.containsValue(ge));
        	return null;
        } else {
        	assert(existingGe == ge);
            collection.remove(ge);
            return gemap.remove(id);
        }
    }    
    
    public GEPoint findPoint(String name) {
        for (GEPoint p : pointlist) {
            if (p != null) {
            	String ss = p.getname();
            	if (ss != null && ss.equals(name))
            		return p;
            }
        }
        return null;
    }

    public GECircle fd_circle(GEPoint centerpoint, GEPoint circumferencepoint) {
        if (centerpoint != null && circumferencepoint != null) {
	        for (GECircle ge : circlelist) {
	            if (ge.isCoincidentWith(circumferencepoint) && ge.hasCenter(centerpoint)) {
	                return ge;
	            }
	        }
        }
        return null;
    }


    int getEMarkNum() {
        int k = 0;
        for (int i = 0; i < otherlist.size(); i++) {
            if (otherlist.get(i) instanceof GEEqualDistanceMark) {
                k++;
            }
        }
        return k;
    }

    public Rectangle getBounds() {

        Rectangle rc = new Rectangle(0, 0, 0, 0);
        ArrayList<GEPoint> v = pointlist;
        double x, y, x1, y1;
        x = y = Integer.MIN_VALUE;
        x1 = y1 = Integer.MAX_VALUE;

        if (v.size() != 0) {
            for (int i = 0; i < v.size(); i++) {
                GEPoint p = v.get(i);
                double x0 = p.getx();
                double y0 = p.gety();
                if (x1 > x0) {
                    x1 = x0;
                }
                if (x < x0) {
                    x = x0;
                }
                if (y1 > y0) {
                    y1 = y0;
                }
                if (y < y0) {
                    y = y0;
                }
            }
            for (GECircle c : circlelist) {
                double r = c.getRadius();

                if (x1 > c.o.getx() - r) {
                    x1 = c.o.getx() - r;
                }
                if (y1 > c.o.gety() - r) {
                    y1 = c.o.gety() - r;
                }
                if (x < c.o.getx() + r) {
                    x = c.o.getx() + r;
                }
                if (y < c.o.gety() + r) {
                    y = c.o.gety() + r;
                }
            }
        }

        for (GEText t : textlist) {
            Dimension dm = t.getTextDimension();
            int w = (int) dm.getWidth();
            int h = (int) dm.getHeight();
            int xt = t.getSX();
            int yt = t.getSY();
            if (x < xt + w) {
                x = xt + w;
            }
            if (y < yt + h) {
                y = yt + h;
            }
            if (x1 > xt) {
                x1 = xt;
            }
            if (y1 > yt) {
                y1 = yt;
            }
        }
        if (x1 < 0)
            x1 = 0;
        if (y1 < 0)
            y1 = 0;
        if (x > Width)
            x = Width;
        if (y > Height)
            y = Height;
        rc.setBounds((int) x1, (int) y1, (int) (x - x1), (int) (y - y1));
        return rc;
    }

    static boolean check_Collinear(GEPoint p1, GEPoint p2, GEPoint p3) {
        if (p1 == null || p2 == null || p3 == null) {
            return false;
        }
        return isZero((p2.getx() - p1.getx()) * (p3.gety() - p2.gety()) - (p2.gety() - p1.gety()) * (p3.getx() - p2.getx()));
    }

    static boolean check_Collinear(double x1, double y1, double x2, double y2, double x3, double y3) {
        return isZero((x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2));
    }

    public static double signArea(double x1, double y1, double x2, double y2, double x3, double y3) {
        return (x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2);
    }

    static boolean check_between(GEPoint p1, GEPoint p2, GEPoint p3) {
        if (p1 == null || p2 == null || p3 == null) {
            return false;
        }
        if (!check_Collinear(p1, p2, p3))
            return false;

        return (p1.gety() - p2.gety()) * (p1.gety() - p3.gety()) < 0
                || (p1.getx() - p2.getx()) * (p1.getx() - p3.getx()) < 0;
    }

    static boolean check_para(GELine ln1, GELine ln2) {
        double k1 = ln1.getSlope();
        double k2 = ln2.getSlope();
        return isZero(k1 - k2);
    }

    static boolean check_para(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        return isZero((p2.getx() - p1.getx()) * (p4.gety() - p3.gety()) -
                (p2.gety() - p1.gety()) * (p4.getx() - p3.getx()));
    }

    static boolean check_perp(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        return isZero(Math.abs((p2.getx() - p1.getx()) * (p4.getx() - p3.getx()) +
                (p2.gety() - p1.gety()) * (p4.gety() - p3.gety())));
    }

    static boolean check_mid(GEPoint p, GEPoint p1, GEPoint p2) {
        if (p == null || p1 == null || p2 == null) {
            return false;
        }
        return check_Collinear(p1, p2, p) && check_eqdistance(p, p1, p, p2);
    }

    static boolean check_cyclic(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        double k1 = (p2.gety() - p1.gety()) / (p2.getx() - p1.getx());
        double k2 = (p4.gety() - p3.gety()) / (p4.getx() - p3.getx());
        k1 = -1 / k1;
        k2 = -1 / k2;
        double x1 = (p1.getx() + p2.getx()) / 2;
        double y1 = (p1.gety() + p2.gety()) / 2;
        double x2 = (p3.getx() + p4.getx()) / 2;
        double y2 = (p3.gety() + p4.gety()) / 2;
        double x = (y2 - y1 + k1 * x1 - k2 * x2) / (k1 - k2);
        double y = y1 + k1 * (x - x1);

        double t1 = Math.pow(p1.getx() - x, 2) + Math.pow(p1.gety() - y, 2);
        double t2 = Math.pow(p2.getx() - x, 2) + Math.pow(p2.gety() - y, 2);
        double t3 = Math.pow(p3.getx() - x, 2) + Math.pow(p3.gety() - y, 2);
        double t4 = Math.pow(p4.getx() - x, 2) + Math.pow(p4.gety() - y, 2);
        return isZero(t1 - t2) && isZero(t2 - t3) && isZero(t3 - t4);
    }

    static boolean check_eqdistance(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        return isZero(sdistance(p1, p2) - sdistance(p3, p4));
    }

    static boolean check_eqdistance(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        return isZero(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) - Math.pow(x3 - x4, 2) - Math.pow(y3 - y4, 2));

    }

    static boolean check_eqdistance(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4, int t1, int t2) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        return isZero(sdistance(p1, p2) * t2 - sdistance(p3, p4) * t1);
    }

    static boolean check_eqangle(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4, GEPoint p5, GEPoint p6) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        double t1 = GEAngle.getAngleValue(p1, p2, p3);
        double t2 = GEAngle.getAngleValue(p4, p5, p6);
        return isZero(t1 - t2);
    }

    static boolean check_eqangle(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4, GEPoint p5, GEPoint p6, GEPoint p7, GEPoint p8) {
        if (p1 == null || p2 == null || p3 == null || p4 == null || p5 == null || p6 == null || p7 == null || p8 == null) {
            return false;
        }
        double t1 = GEAngle.getAngleValue(p1, p2, p3, p4);
        double t2 = GEAngle.getAngleValue(p5, p6, p7, p8);
        return isZero(t1 - t2);
    }


    public static boolean check_same_side(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        if (p4 == null || p1 == null || p2 == null || p3 == null) {
            return false;
        }
        return collv(p1, p2, p3) * collv(p1, p2, p4) > 0;
    }

    public static double collv(GEPoint A, GEPoint B, GEPoint C) {
        double d1 = (B.getx() - A.getx()) * (C.gety() - A.gety()) -
                (B.gety() - A.gety()) * (C.getx() - A.getx());
        return d1;
    }

    public static boolean check_triangle_inside(GEPoint p, GEPoint p1, GEPoint p2, GEPoint p3) {
        if (p == null || p1 == null || p2 == null || p3 == null) {
            return false;
        }

        double d1 = collv(p, p1, p2);
        double d2 = collv(p, p2, p3);
        double d3 = collv(p, p3, p1);

        return d1 * d2 > 0 && d2 * d3 > 0 && d1 * d3 > 0;
    }

    public static double areaTriangle(GEPoint p1, GEPoint p2, GEPoint p3) {
        double a = DrawPanelBase.sdistance(p1, p2);
        double b = DrawPanelBase.sdistance(p1, p3);
        double c = DrawPanelBase.sdistance(p3, p2);

        return Math.sqrt(a * a * c * c - Math.pow(a * a + c * c - b * b, 2) / 4);
    }

    static boolean check_angle_less(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4, GEPoint p5, GEPoint p6) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        double t1 = GEAngle.getAngleValue(p1, p2, p3);
        double t2 = GEAngle.getAngleValue(p4, p5, p6);
        return Math.abs(t1) > Math.abs(t2);
    }

    static boolean check_distance_less(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        return (sdistance(p1, p2) < sdistance(p3, p4));
    }

    static boolean check_bisect(GEPoint p1, GEPoint p2, GEPoint p3) {
        if (p1 == null || p2 == null || p3 == null) {
            return false;
        }
        return isZero(sdistance(p1, p2) - sdistance(p1, p3));
    }

    static boolean check_simtri(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4,
                                GEPoint p5, GEPoint p6) {
        if (p1 == null || p2 == null || p3 == null || p4 == null || p5 == null ||
                p6 == null) {
            return false;
        }
        double r1 = sdistance(p1, p2);
        double r2 = sdistance(p1, p3);
        double r3 = sdistance(p2, p3);
        double r4 = sdistance(p4, p5);
        double r5 = sdistance(p4, p6);
        double r6 = sdistance(p5, p6);
        double t1 = r1 / r4;
        double t2 = r2 / r5;
        double t3 = r3 / r6;
        return isZero(t1 - t2) && isZero(t1 - t3) && isZero(t2 - t3);
    }

    static boolean check_congtri(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4,
                                 GEPoint p5, GEPoint p6) {
        if (p1 == null || p2 == null || p3 == null || p4 == null || p5 == null ||
                p6 == null) {
            return false;
        }
        double r1 = sdistance(p1, p2);
        double r2 = sdistance(p1, p3);
        double r3 = sdistance(p2, p3);
        double r4 = sdistance(p4, p5);
        double r5 = sdistance(p4, p6);
        double r6 = sdistance(p5, p6);

        return isZero(r1 - r4) && isZero(r2 - r5) && isZero(r3 - r6);
    }

    static boolean check_lc_tangent(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        return false;
    }

    static boolean check_cc_tangent(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        return isZero(sdistance(p1, p2) - sdistance(p3, p4));
    }

    static double sdistance(GEPoint p1, GEPoint p2) {
        return Math.sqrt(Math.pow(p1.getx() - p2.getx(), 2) + Math.pow(p1.gety() - p2.gety(), 2));
    }

    static boolean nearPt(double x, double y, GEPoint pt) {
        return Math.abs(x - pt.getx()) < UtilityMiscellaneous.PIXEPS && Math.abs(y - pt.gety()) < UtilityMiscellaneous.PIXEPS;
    }


    static double[] get_pt_dmcr(double x1, double y1, double x2, double y2, double x, double y) {

        double dis = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        double xx = (x1 + x2) / 2;
        double yy = (y1 + y2) / 2;
        double rs = Math.sqrt((Math.pow(xx - x, 2) + Math.pow(yy - y, 2)));
        double dx = (x - xx) / rs;
        double dy = (y - yy) / rs;
        double xr = xx + dx * dis / 2;
        double yr = yy + dy * dis / 2;
        double[] r = new double[2];

        if (near(xr, yr, x1, y1) || near(xr, yr, x2, y2)
                || DrawPanelBase.check_Collinear(x1, y1, x2, y2, xr, yr)) {
        } else if (Math.abs(xr - x1) < UtilityMiscellaneous.PIXEPS) {
            xr = x1;
            yr = y2;
        } else if (Math.abs(xr - x2) < UtilityMiscellaneous.PIXEPS) {
            xr = x2;
            yr = y1;
        } else if (Math.abs(yr - y1) < UtilityMiscellaneous.PIXEPS) {
            yr = y1;
            xr = x2;
        } else if (Math.abs(yr - y2) < UtilityMiscellaneous.PIXEPS) {
            yr = y2;
            xr = x1;
        }
        r[0] = xr;
        r[1] = yr;
        return r;
    }

    static double[] intersect_lc(GELine ln, GECircle cr) {
        double r2 = cr.getRadius();
        r2 *= r2;
        double k = ln.getSlope();
        GEPoint p = ln.getFirstPoint();
        if (p == null) return null;
        GEPoint o = cr.o;
        double x2 = p.getx();
        double y2 = p.gety();
        double x3 = o.getx();
        double y3 = o.gety();


        if (Math.abs(k) < UtilityMiscellaneous.MAX_SLOPE) {
            double t = y2 - y3 - k * x2;
            double a = k * k + 1;
            double b = 2 * k * t - 2 * x3;
            double c = t * t + x3 * x3 - r2;
            double d = b * b - 4 * a * c;
            if (d < 0) return new double[0];
            d = Math.sqrt(d);
            double t1 = (-b + d) / (2 * a);
            double t2 = (-b - d) / (2 * a);
            double m1 = (t1 - x2) * k + y2;
            double m2 = (t2 - x2) * k + y2;
            double[] r = new double[4];
            r[0] = t1;
            r[1] = m1;
            r[2] = t2;
            r[3] = m2;
            return r;

        } else {
            double t1 = x2;
            double dl = r2 - (t1 - x3) * (t1 - x3);
            if (dl < 0) return null;
            double d = Math.sqrt(dl);
            double m1 = y3 + d;
            double t2 = t1;
            double m2 = y3 - d;
            double[] r = new double[4];
            r[0] = t1;
            r[1] = m1;
            r[2] = t2;
            r[3] = m2;
            return r;
        }
    }

    public static double[] intersect_ll(GELine ln1, GELine ln2) {
        GEPoint p1 = ln1.getFirstPoint();
        double k1 = ln1.getSlope();
        GEPoint p2 = ln2.getFirstPoint();
        double k2 = ln2.getSlope();
        if (p1 == null || p2 == null) return null;
        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();
        double x, y;
        if (Math.abs(k1) > UtilityMiscellaneous.MAX_SLOPE) {
            x = x1;
            y = y2 + k2 * (x - x2);
        } else if (Math.abs(k2) > UtilityMiscellaneous.MAX_SLOPE) {
            x = x2;
            y = y1 + k1 * (x - x1);
        } else {
            x = (y2 - y1 + k1 * x1 - k2 * x2) / (k1 - k2);
            y = y1 + k1 * (x - x1);
        }
        double[] r = new double[2];
        r[0] = x;
        r[1] = y;
        return r;
    }

    public static double[] intersect_cc(GECircle c1, GECircle c2) {
        double r1 = c1.getRadius();
        GEPoint o1 = c1.o;
        double r2 = c2.getRadius();
        GEPoint o2 = c2.o;

        double x1 = o1.getx();
        double y1 = o1.gety();
        double x2 = o2.getx() - x1;
        double y2 = o2.gety() - y1;
        double a = 2 * x2;
        double b = 2 * y2;
        double c = -x2 * x2 - y2 * y2 - r1 * r1 + r2 * r2;
        double ma = a * a + b * b;
        double d = 4 * a * a * (r1 * r1 * (ma) - c * c);
        if (d < 0) return null;
        d = Math.sqrt(d);
        if (a != 0) {
            double yt1 = (-2 * b * c + d) / (2 * (ma));
            double yt2 = (-2 * b * c - d) / (2 * (ma));
            double xt1 = -(b * yt1 + c) / a;
            double xt2 = -(b * yt2 + c) / a;
            double[] r = new double[4];
            r[0] = xt1 + x1;
            r[1] = yt1 + y1;
            r[2] = xt2 + x1;
            r[3] = yt2 + y1;
            return r;
        } else {
            double yt1, yt2;
            yt1 = yt2 = -c / b;
            double xt1 = Math.sqrt(r1 * r1 - yt1 * yt1);
            double xt2 = -xt1;
            double[] r = new double[4];
            r[0] = xt1 + x1;
            r[1] = yt1 + y1;
            r[2] = xt2 + x1;
            r[3] = yt2 + y1;
            return r;
        }
    }

    protected static boolean check_cc_inter(GECircle c1, GECircle c2) {
        double r1 = c1.getRadius();
        double r2 = c2.getRadius();
        double r = Math.sqrt(Math.pow(c1.getCenterOX() - c2.getCenterOX(), 2) +
                Math.pow(c1.getCenterOY() - c2.getCenterOY(), 2));
        double rx = r - r1 - r2;
        double rx1 = r1 - r - r2;
        double rx2 = r2 - r - r1;

        return rx < 0.1 && rx1 < 0.1 && rx2 < 0.1;
    }

    protected static boolean check_lc_inter(GELine ln, GECircle c2) {
        double r1 = ln.distance(c2.getCenterOX(), c2.getCenterOY());
        double r2 = c2.getRadius();
        return (r2 - r1) > 0;
    }

    protected static boolean isZero(double r) {
        return Math.abs(r) < UtilityMiscellaneous.ZERO;
    }

    protected static boolean near(double x, double y, double x1, double y1) {
        return Math.abs(Math.pow(x - x1, 2) + Math.pow(y - y1, 2)) < UtilityMiscellaneous.PIXEPS * UtilityMiscellaneous.PIXEPS;
    }

    public static void set_eps(double r) {
    }

    protected GEPoint[] getPoints(cons c) {

        GEPoint[] pp = new GEPoint[8];
        int i = 0;
        while (true) {
            Object p1 = c.getPTN(i);
            if (p1 == null)
                break;

            pp[i] = findPoint(p1.toString());
            if (pp[i] == null) {
                JOptionPane.showMessageDialog(null, "Cannot find point " + p1 + "\nPlease construct the diagram", "Warning", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            i++;
        }
        return pp;
    }

//    public int dxindex(int n)
//    {
//        gt
//    }
//
//    public TMono getTMono(cndg d)
//    {
//        if(d == null)
//            return null;
//
//

    //    }

    protected TMono getTMono(cons c) {
        if (c == null) return null;

        GEPoint[] pp = getPoints(c);
        if (pp == null) return null;
        TMono m = null;

        switch (c.type) {
            case gib.CO_COLL:
                m = GeoPoly.collinear(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex);
                break;
            case gib.CO_PARA:
                m = GeoPoly.parallel(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex, pp[3].x1.xindex, pp[3].y1.xindex);
                break;
            case gib.CO_PERP:
                m = GeoPoly.perpendicular(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex, pp[3].x1.xindex, pp[3].y1.xindex);
                break;
            case gib.CO_MIDP:
                m = GeoPoly.midpoint(pp[1].x1.xindex, pp[0].x1.xindex, pp[2].x1.xindex);
                break;
            case gib.CO_CYCLIC:
                m = GeoPoly.cyclic(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex, pp[3].x1.xindex, pp[3].y1.xindex);
                break;
            case gib.CO_CONG:
                m = GeoPoly.eqdistance(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex, pp[3].x1.xindex, pp[3].y1.xindex);
                break;
            case gib.CO_ACONG: {
                if (pp[6] != null && pp[7] != null)
                    m = GeoPoly.eqangle(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex, pp[3].x1.xindex, pp[3].y1.xindex,
                            pp[4].x1.xindex, pp[4].y1.xindex, pp[5].x1.xindex, pp[5].y1.xindex, pp[6].x1.xindex, pp[6].y1.xindex, pp[7].x1.xindex, pp[7].y1.xindex);
                else
                    m = GeoPoly.eqangle(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex,
                            pp[3].x1.xindex, pp[3].y1.xindex, pp[4].x1.xindex, pp[4].y1.xindex, pp[5].x1.xindex, pp[5].y1.xindex);
            }
            break;
            case gib.CO_PBISECT:
                break;
            case gib.CO_STRI:
                break;
            case gib.CO_CTRI:
                break;
        }

        if (m == null) return m;
        TMono m1 = m;
        while (m1.coef != null)
            m1 = m1.coef;
        if (m1.value() < 0)
            m = PolyBasic.cp_times(-1, m);
        return m;
    }

    public boolean decide_wu_identical(GEPoint p1, GEPoint p2) {

        TMono m1 = GeoPoly.ppdd(p1.x1.xindex, p2.x1.xindex); //poly.pdif(poly.pcopy(p1.x1.m), poly.pcopy(p2.x1.m));
        TMono m2 = GeoPoly.ppdd(p1.y1.xindex, p2.y1.xindex); //poly.pdif(poly.pcopy(p1.y1.m), poly.pcopy(p2.y1.m));
        return div_set(m1) && div_set(m2);

    }

    public boolean div_set(TMono m1) {
        if (m1 == null)
            return true;

        TPoly p1 = polylist;

        if (PolyBasic.pzerop(m1))
            return true;
        while (p1 != null) {
            TMono t = p1.poly;
            if (t.x == m1.x)
                break;
            p1 = p1.next;
        }

        while (true) {
            TMono m = p1.poly;
            TMono md = PolyBasic.pcopy(m);
            m1 = PolyBasic.prem(m1, md);
            if (PolyBasic.pzerop(m1))
                return true;
            TPoly p2 = polylist;
            if (p1 == p2)
                break;

            while (p2 != null) {
                if (p2.next == p1)
                    break;
                p2 = p2.next;
            }
            p1 = p2;
        }

        UtilityMiscellaneous.print("======================");
        PolyBasic.printpoly(m1);
        DrawPanelBase.printPoly(polylist);
        return false;
    }

    public static void printPoly(TPoly p) {
        while (p != null) {
            PolyBasic.printpoly(p.getPoly());
            p = p.getNext();
        }
    }


    public static boolean verify_ndg(TMono m) {
        if (m == null)
            return true;
        //TPoly p1 = polylist;
        if (PolyBasic.pzerop(m))
            return false;


        return true;
    }

    public GETMark findCTMark(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        for (GraphicEntity c : otherlist) {
            if (c.get_type() == GraphicEntity.TMARK) {
                GETMark m = (GETMark) c;
                if (m.ln1.containsPoints(p1, p2) && m.ln2.containsPoints(p3, p4))
                    return m;
                if (m.ln2.containsPoints(p1, p2) && m.ln1.containsPoints(p3, p4))
                    return m;

            }
        }
        return null;
    }

    public boolean find_tmark(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        for (int i = 0; i < flashlist.size(); i++) {
            Flash f = flashlist.get(i);
            if (f instanceof FlashTLine) {
                FlashTLine t = (FlashTLine) f;
                if (t.containPt(p1) && t.containPt(p2) && t.containPt(p3) && t.containPt(p4))
                    return true;
            }
        }
        return false;
    }

    public boolean containFreezedPoint() {
        for (int i = 0; i < pointlist.size(); i++) {
            GEPoint p = pointlist.get(i);
            if (p.isFrozen()) {
                return true;
            }
        }
        return false;
    }

    public void unfreezeAllPoints() {
        for (int i = 0; i < pointlist.size(); i++) {
            GEPoint p = pointlist.get(i);
            if (p.isFrozen()) {
                p.setFrozen(false);
            }
        }
    }

    public boolean isFrozen() {
        for (int i = 0; i < pointlist.size(); i++) {
            GEPoint p = pointlist.get(i);
            if (p.isFrozen()) {
                gxInstance.setTextLabel2("The diagram is freezed, use right click menu to unfreeze!");
                return true;
            }
        }
        return false;
    }

    public void setAllFreezed() {
        for (int i = 0; i < pointlist.size(); i++) {
            {
                GEPoint p = pointlist.get(i);
                p.setFrozen(true);
            }
        }
    }


    public void zoom_out(double x, double y, int zz) {

        if (isFrozen())
            return;

        double r = UtilityMiscellaneous.ZOOM_RATIO;
        r = 1 + (r - 1) / zz;

        for (int i = 0; i < pointlist.size(); i++) {
            GEPoint p = pointlist.get(i);
            p.setXY(p.getx() * 1.0 / r + (1.0 - 1.0 / r) * x, p.gety() * 1.0 / r + (1.0 - 1.0 / r) * y);
        }
    }

    public void zoom_in(double x, double y, int zz) {
        if (isFrozen())
            return;

        double r = UtilityMiscellaneous.ZOOM_RATIO;
        r = 1 + (r - 1) / zz;
        for (int i = 0; i < pointlist.size(); i++) {
            GEPoint p = pointlist.get(i);
            p.setXY(p.getx() * r + (1.0 - r) * x, p.gety() * r + (1.0 - r) * y);
        }
    }

    public void hvCatchPoint() {
        for (int i = 0; i < pointlist.size(); i++) {
            GEPoint pt = pointlist.get(i);
            if (DrawPanelBase.sdistance(CatchPoint, pt) > UtilityMiscellaneous.PIXEPS_PT) {
                if (Math.abs(pt.getx() - CatchPoint.getx()) < UtilityMiscellaneous.PIXEPS_PT / 2) {
                    if (CatchType == 3) {
                        CatchType = 4;
                        return;
                    } else CatchType = 2;
                } else if (Math.abs(pt.gety() - CatchPoint.gety()) < UtilityMiscellaneous.PIXEPS_PT / 2) {
                    if (CatchType == 2) {
                        CatchType = 4;
                        return;
                    } else
                        CatchType = 3;
                    return;
                }
            }
        }
    }

    public GEPoint getCatchHVPoint(int CatchType) {
        for (int i = 0; i < pointlist.size(); i++) {
            GEPoint pt = pointlist.get(i);
            if (DrawPanelBase.sdistance(CatchPoint, pt) > UtilityMiscellaneous.PIXEPS_PT) {
                if (CatchType == 2 && Math.abs(pt.getx() - CatchPoint.getx()) < UtilityMiscellaneous.PIXEPS_PT / 2) {
                    return pt;
                } else if (CatchType == 3 && Math.abs(pt.gety() - CatchPoint.gety()) < UtilityMiscellaneous.PIXEPS_PT / 2) {
                    return pt;
                }
            }
        }
        return null;
    }

    public void setCatchHVPoint(GEPoint pv) {
        if (CatchType != 2 && CatchType != 3 && CatchType != 4)
            return;

        for (int i = 0; i < pointlist.size(); i++) {
            GEPoint pt = pointlist.get(i);
            if (DrawPanelBase.sdistance(pv, pt) > UtilityMiscellaneous.PIXEPS_PT) {
                if ((CatchType == 2 || CatchType == 4) && Math.abs(pt.getx() - pv.getx()) < UtilityMiscellaneous.PIXEPS_PT / 2) {
                    pv.setXY(pt.getx(), pv.gety());
                    return;
                }
                if ((CatchType == 3 || CatchType == 4) && Math.abs(pt.gety() - pv.gety()) < UtilityMiscellaneous.PIXEPS_PT / 2) {
                    pv.setXY(pv.getx(), pt.gety());
                    return;
                }
            }
        }
    }


}
