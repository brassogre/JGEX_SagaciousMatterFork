package wprover;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.w3c.dom.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2005-1-25
 * Time: 20:24:52
 * To change this template use File | Settings | File Templates.
 */
public class GEPolygon extends GraphicEntity implements Pointed {
    int ftype = 0; // 0: polygon, 1:circle.

    int type = 0; // 0,fill; 1,grid;
    int grid = 12;
    int slope = 0;
    private boolean showArea = false;
    ArrayList<GEPoint> points = new ArrayList<GEPoint>();

    private GEPoint pt1, pt2;
    private ArrayList<GEPoint> vtrlist = new ArrayList<GEPoint>();
    private double pdx, pdy;
    private double area;

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(area);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ftype;
		result = prime * result + grid;
		temp = Double.doubleToLongBits(pdx);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(pdy);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		result = prime * result + ((pt1 == null) ? 0 : pt1.hashCode());
		result = prime * result + ((pt2 == null) ? 0 : pt2.hashCode());
		result = prime * result + (showArea ? 1231 : 1237);
		result = prime * result + slope;
		result = prime * result + type;
		result = prime * result + ((vtrlist == null) ? 0 : vtrlist.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof GEPolygon))
			return false;
		GEPolygon other = (GEPolygon) obj;
		if (Double.doubleToLongBits(area) != Double
				.doubleToLongBits(other.area))
			return false;
		if (ftype != other.ftype)
			return false;
		if (grid != other.grid)
			return false;
		if (Double.doubleToLongBits(pdx) != Double.doubleToLongBits(other.pdx))
			return false;
		if (Double.doubleToLongBits(pdy) != Double.doubleToLongBits(other.pdy))
			return false;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		if (pt1 == null) {
			if (other.pt1 != null)
				return false;
		} else if (!pt1.equals(other.pt1))
			return false;
		if (pt2 == null) {
			if (other.pt2 != null)
				return false;
		} else if (!pt2.equals(other.pt2))
			return false;
		if (showArea != other.showArea)
			return false;
		if (slope != other.slope)
			return false;
		if (type != other.type)
			return false;
		if (vtrlist == null) {
			if (other.vtrlist != null)
				return false;
		} else if (!vtrlist.equals(other.vtrlist))
			return false;
		return true;
	}

	@Override
    public boolean isCoincidentWith(Pointed pointedEntity) {
    	return pointedEntity.isCoincidentWith(points);
    }
    
    @Override
    public boolean isCoincidentWith(ArrayList<GEPoint> pList) {
    	if (pList != null) {
    		for (GEPoint pp : pList) {
    			if (points.contains(pp))
    				return true;
    		}
    	}
    	return false;
    }
    
    @Override
    public boolean isCoincidentWith(GEPoint pp) {
    	return (pp != null && points.contains(pp));
    }
    
    @Override
    public boolean isFullyCoincidentWith(final ArrayList<GraphicEntity> pList) {
    	if (pList != null) {
    		for (GEPoint pp : points) {
    			if (!pList.contains(pp))
    				return false;
    		}
        	return true;
    	}
    	return false;
    }

    @Override
    public GEPoint getCommonPoints(Pointed pointedEntity, Collection<GEPoint> collectionPoints) {
    	return pointedEntity.getCommonPoints(points, collectionPoints);
    }
    
    @Override
    public GEPoint getCommonPoints(ArrayList<GEPoint> pList, Collection<GEPoint> collectionPoints) {
    	GEPoint pCommon = null;
		for (GEPoint pp : pList) {
			if (points.contains(pp)) {
				pCommon = pp;
				if (collectionPoints != null)
					collectionPoints.add(pCommon);
			}
		}
    	return pCommon;
    }
    
    @Override
    public GEPoint getCommonPoints(GEPoint pp, Collection<GEPoint> collectionPoints) {
    	GEPoint pCommon = null;
		if (points.contains(pp)) {
			pCommon = pp;
			if (collectionPoints != null)
				collectionPoints.add(pCommon);
		}
    	return pCommon;
    }
    
    
    public void draw(Graphics2D g2, boolean selected, boolean overlap, double dx, double dy,
                     boolean rotate, double x, double y, double ang) {
        if (!isdraw()) return;
        int n = points.size();
        if (n == 0) return;
        if (points.get(0) != points.get(n - 1))
            return;

        int[] xpoints = new int[n];
        int[] ypoints = new int[n];

        for (int i = 0; i < n; i++) {
            GEPoint pt = points.get(i);
            double tx = (pt.getx() + dx);
            double ty = (pt.gety() + dy);
            if (pt == pt1 || pt == pt2) {
                tx += pdx;
                ty += pdy;
            } else {
                for (int j = 0; j < vtrlist.size() / 2; j++) {
                    GEPoint t1 = vtrlist.get(j * 2);
                    GEPoint t2 = vtrlist.get(j * 2 + 1);
                    if (t1 == pt) {
                        tx = t2.getx() + dx;
                        ty = t2.gety() + dx;
                        break;
                    }
                }
            }

            if (rotate) {
                double sin = Math.sin(ang);
                double cos = Math.cos(ang);
                tx -= x;
                ty -= y;
                double mx = (tx) * cos - (ty) * sin;
                double my = (tx) * sin + (ty) * cos;
                tx = mx + x;
                ty = my + y;
            }
            xpoints[i] = (int) tx;
            ypoints[i] = (int) ty;
        }

        this.prepareToBeDrawnAsUnselected(g2);


        Composite ac = null;
        if (overlap) {
            ac = g2.getComposite();
            g2.setComposite(UtilityMiscellaneous.getFillComposite());
        }

        this.prepareToBeDrawnAsUnselected(g2);

        if (ftype == 0) {
            if (type == 0)
                g2.fillPolygon(xpoints, ypoints, n);
            else
                drawGrid(g2, xpoints, ypoints, n, 0, type);

            if (overlap)
                g2.setComposite(ac);

            if (rotate) {
                g2.setColor(Color.black);
                g2.setStroke(UtilityMiscellaneous.DashedStroke);
            } else {
                if (selected)
                    g2.setStroke(UtilityMiscellaneous.SelectPolygonStroke);

                g2.setColor(super.getColor().darker());
            }
            area = area(xpoints, ypoints, n);
            g2.drawPolygon(xpoints, ypoints, n);

        } else if (ftype == 1) {
            if (points.size() >= 3) {
                double r = Math.sqrt(Math.pow(xpoints[1] - xpoints[2], 2) + Math.pow(ypoints[1] - ypoints[2], 2));
                int r1 = (int) r;

                if (type == 0)
                    g2.fillOval(xpoints[0] - r1, ypoints[0] - r1, 2 * r1, 2 * r1);
                else {
                }
                if (selected) {
                    g2.setStroke(UtilityMiscellaneous.SelectPolygonStroke);
                    g2.setColor(super.getColor().darker());
                    g2.drawOval(xpoints[0] - r1, ypoints[0] - r1, 2 * r1, 2 * r1);
                }
            }
        }
        if (ac != null)
            g2.setComposite(ac);
    }

    public GraphicEntity getElement(int n) {
        if (n < 0 || n >= points.size())
            return null;
        return points.get(n);
    }

    public double getArea() {
        return area;
    }

    public boolean areAllPointsFree() {
        for (int i = 0; i < points.size(); i++) {
            GEPoint pt = points.get(i);
            if (!pt.isAFreePoint())
                return false;
        }
        return true;
    }

    public static double signArea(int x1, int y1, int x2, int y2, int x3, int y3) {
        return ((x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2)) / 2;
    }

    public static double area(int xPoints[], int yPoints[], int nPoints) {
        if (nPoints < 4) return 0.0;

        double r = 0.0;
        for (int i = 1; i <= nPoints - 3; i++) {
            r += signArea(xPoints[0], yPoints[0], xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }
        return r;
    }

    public boolean isEqual(GECircle c) {
        if (c == null) return false;
        if (ftype != 1) return false;
        if (points.size() < 3) return false;
        GEPoint pp[] = c.getRadiusPoint();
        if (pp.length != 2) return false;

        return c.o == points.get(0) &&
                ((pp[0] == points.get(1) && pp[1] == points.get(2))
                        || (pp[0] == points.get(2) && pp[1] == points.get(1)));


    }

    public static void drawAreaText(Graphics2D g2, int xPoints[], int yPoints[], int nPoints) {
        if (nPoints < 4) return;
        double r = area(xPoints, yPoints, nPoints);
        r = Math.abs(r);

        int x, y;
        x = y = 0;

        for (int i = 0; i <= nPoints - 2; i++) {
            x += xPoints[i];
            y += yPoints[i];
        }
        x = x / (nPoints - 1);
        y = y / (nPoints - 1);
        g2.drawString("Area = " + r, x, y);
    }

    public void setShowArea(boolean r) {
        showArea = r;
    }


    public void draw(Graphics2D g2, boolean selected) {
        draw(g2, selected, true, 0, 0, false, 0, 0, 0);
    }

//    public void draw(Graphics2D g2) {
//        this.draw(g2, false);
//    }

    public int getPtn() {
        return points.size();
    }

    public GEPoint getPoint(int n) {
        return points.get(n);
    }

    public GEPolygon() {
        super(GraphicEntity.POLYGON);
        pt1 = pt2 = null;
        pdx = pdy = 0;
        //m_color =drawData.
    }

    public GEPolygon(GECircle c) {
        this();
        this.ftype = 1;
        points.add(c.o);
        GEPoint[] pp = c.getRadiusPoint();
        points.add(pp[0]);
        points.add(pp[1]);
        points.add(c.o);
    }

    public void setDraggedPoints(GEPoint p1, GEPoint p2, double x, double y) {
        pt1 = p1;
        pt2 = p2;
        pdx = x;
        pdy = y;
    }

    public void setDraggedPointsNull() {
        pt1 = null;
        pt2 = null;
        pdx = 0;
        pdy = 0;
        vtrlist.clear();
    }

    public void copy(GEPolygon c) {
        super.copy(c);
        grid = c.grid;
        slope = c.slope;
        this.type = c.type;
    }

    public void addDraggedPoints(GEPoint p1, GEPoint p2) {
        vtrlist.add(p1);
        vtrlist.add(p2);
    }

    public boolean allDragged() {
        return vtrlist.size() == (points.size() - 1) * 2;
    }

    public void getDraggedPoints(ArrayList<GraphicEntity> list) {
        list.addAll(vtrlist);
    }

    public void getTransformedPoints(ArrayList<GraphicEntity> vlist) {
        if (vlist != null) {
	    for (int i = 0; i < points.size(); i++) {
		GEPoint pt = points.get(i);
		boolean fd = false;
		for (int j = 0; j < vtrlist.size() / 2; j++) {
		    GEPoint t1 = vtrlist.get(j * 2);
		    GEPoint t2 = vtrlist.get(j * 2 + 1);
		    if (pt == t1) {
			vlist.add(t2);
			fd = true;
			break;
		    }
		}
		if (!fd)
		    vlist.add(pt);
	    }
	}
    }

    public String getPolygonTypeString() {
        String ds;

        int size = points.size() - 1;
        if (ftype == 0) {
            if (size == 3)
                ds = Language.getLs(71, "triangle ");
            else if (size == 4)
                ds = Language.getLs(76, "quadrangle ");
            else if (size == 5)
                ds = Language.getLs(82, "pentagon ");
            else if (size == 6)
                ds = "hexagon ";
            else
                ds = "polygon";
            return ds;
        } else {
            return Language.getLs(50, "Circle");
        }

    }

    public String TypeString() {
        return getPolygonTypeString();
    }

    public boolean containPnt(GEPoint p) {
        return points.contains(p);
    }

    public boolean check_rdeq(ArrayList<?> v) {  // n --- n-1;
        if (!bVisible) return false;

        int n = points.size();
        if (n != v.size() + 1) return false;
        n--;
        int i, j;

        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                if (points.get(j) != v.get((j + i) % n))
                    break;
            }
            if (j == n)
                return true;
            else {
                for (j = 0; j < n; j++) {
                    if (points.get(j) != v.get((i - j + n) % n))
                        break;
                }
                if (j == n)
                    return true;
            }
        }

        return false;
    }

    public boolean check_eq(Collection<? extends GraphicEntity> v) {
    	return points.equals(v);
    }
////////////////////////////////////////////////////////////

    void setType(int type) {
        this.type = type;
    }


    int getType() {
        return type;
    }

    void setGrid(int grid) {
        this.grid = grid;
    }

    void setSlope(int s) {
        this.slope = s;
    }

    public void setPoints(ArrayList<GraphicEntity> v) {
        points.clear();
	for (GraphicEntity cc : v) {
	    if (cc != null && cc instanceof GEPoint)
		points.add((GEPoint)cc);
	}
    }
    public boolean addAPoint(GEPoint p) {
        if (p == null) return false;
        if (points.size() > 1 && p == points.get(0)) {
            points.add(p);
            return true;
        } else if (points.contains(p))
            return false;
        else
            points.add(p);
        return false;
    }


    public boolean isLocatedNear(double x, double y) {
        if (bVisible == false) return false;

        if (ftype == 0) {
            Polygon poly = new Polygon();

            for (int i = 0; i < points.size(); i++) {
                GEPoint pt = points.get(i);
                poly.addPoint((int) pt.getx(), (int) pt.gety());
            }

            return poly.contains(x, y);
        } else {
            GEPoint p1 = points.get(0);
            GEPoint p2 = points.get(1);
            GEPoint p3 = points.get(2);
            double r = (Math.pow(p2.getx() - p3.getx(), 2) + Math.pow(p2.gety() - p3.gety(), 2));

            return Math.pow(x - p1.getx(), 2) + Math.pow(y - p1.gety(), 2) < r;
        }
    }


    public GEPoint getPreviousePoint(GEPoint p) {
        for (int i = 0; i < points.size() - 1; i++) {
            if (p == points.get(i + 1))
                return points.get(i);
        }
        return null;
    }

    public GEPoint getNextPoint(GEPoint p) {
        for (int i = 1; i < points.size(); i++) {
            if (p == points.get(i - 1))
                return points.get(i);
        }
        return null;
    }

    public void draw(Graphics2D g2, GEPoint p) {
        if (bVisible == false) return;

        if (points.size() == 0) return;

        int n = points.size() + 2;
        int[] xpoints = new int[n];
        int[] ypoints = new int[n];

        for (int i = 0; i < points.size(); i++) {
            GEPoint pt = points.get(i);
            xpoints[i] = (int) pt.getx();
            ypoints[i] = (int) pt.gety();
        }
        xpoints[n - 2] = (int) p.getx();
        ypoints[n - 2] = (int) p.gety();

        GEPoint pt = points.get(0);
        xpoints[n - 1] = (int) pt.getx();
        ypoints[n - 1] = (int) pt.gety();

        this.prepareToBeDrawnAsUnselected(g2);


        if (type == 0)
            g2.fillPolygon(xpoints, ypoints, n);
        else
            drawGrid(g2, xpoints, ypoints, n, 0, type);

        g2.setColor(super.getColor().darker().darker());


        g2.drawPolygon(xpoints, ypoints, n);

        g2.drawLine(xpoints[0], ypoints[0], (int) p.getx(), (int) p.gety());
        g2.drawLine(xpoints[n - 1], ypoints[n - 1], (int) p.getx(), (int) p.gety());


    }

    public double getCentroidX() {
        ArrayList<GEPoint> v = points;

        double dx1 = 0;
        int n = v.size();
        for (int i = 0; i < n; i++) {
            GEPoint pt = v.get(i);
            dx1 += pt.getx();
        }
        dx1 /= n;
        return dx1;
    }

    public double getCentroidY() {
        ArrayList<GEPoint> v = points;
        double dy1 = 0;
        int n = v.size();
        for (int i = 0; i < n; i++) {
            GEPoint pt = v.get(i);
            dy1 += pt.gety();
        }
        dy1 /= n;
        return dy1;
    }

    public String getDescription() {
        String s = new String();
        if (points.size() < 4) return "";

        int size = points.size() - 1;

        for (int i = 0; i < size; i++) {
            GraphicEntity cc = points.get(i);
            s += cc.m_name;
        }
        if (ftype == 0)
            return TypeString() + s;
        else return TypeString() + "(" + s + ")";
    }


    public ArrayList<Point> drawGrid(Graphics2D g2, int[] xpoints, int[] ypoints, int n, int dtype, int gtype) // type 0: draw ; 1: print ps
    {
        ArrayList<Point> vpl = null;
        if (dtype == 1)
            vpl = new ArrayList<Point>();

        if (n <= 3) return vpl;


        double k = Math.tan(slope * Math.PI / 180);
        int step = this.grid;

        int xmax, ymax;
        xmax = ymax = Integer.MIN_VALUE;
        int xmin, ymin;
        xmin = ymin = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            if (xpoints[i] > xmax)
                xmax = xpoints[i];
            if (ypoints[i] > ymax)
                ymax = ypoints[i];
            if (xpoints[i] < xmin)
                xmin = xpoints[i];
            if (ypoints[i] < ymin)
                ymin = ypoints[i];
        }

        double[] ov = new double[n];

        if (Math.abs(k) > 1 / UtilityMiscellaneous.ZERO || Math.abs(k) < UtilityMiscellaneous.ZERO) {

            if (gtype == 1 || gtype == 2) {
                double x = (xmin / step) * step - step + 0.1;
                double y;
                while (x <= xmax) {
                    int np = 0;
                    for (int i = 0; i < n - 1; i++) {
                        if (xpoints[i + 1] == xpoints[i]) continue;
                        y = ypoints[i + 1] -
                                (xpoints[i + 1] - x) * (ypoints[i + 1] - ypoints[i]) / (xpoints[i + 1] - xpoints[i]);
                        if ((y - ypoints[i + 1]) * (y - ypoints[i]) < 0 || (x - xpoints[i + 1]) * (x - xpoints[i]) < 0)
                            np = add_sort(y, ov, np);
                    }
                    int dx = (int) x;
                    for (int j = 0; j < np - 1; j += 2) {
                        if (dtype == 0)
                            g2.drawLine(dx, (int) ov[j], dx, (int) ov[j + 1]);
                        else if (dtype == 1) {
                            vpl.add(new Point(dx, (int) ov[j]));
                            vpl.add(new Point(dx, (int) ov[j + 1]));
                        }
                    }
                    x += step;
                }
            }
            if (gtype == 1 || gtype == 3) {
                double y = (ymin / step) * step - step + 0.1;
                double x;
                while (y <= ymax) {
                    int np = 0;
                    for (int i = 0; i < n - 1; i++) {
                        if (ypoints[i + 1] == ypoints[i])
                            continue;
                        x = xpoints[i + 1] -
                                (ypoints[i + 1] - y) * (xpoints[i + 1] - xpoints[i]) / (ypoints[i + 1] - ypoints[i]);
                        if ((y - ypoints[i + 1]) * (y - ypoints[i]) < 0 || (x - xpoints[i + 1]) * (x - xpoints[i]) < 0)
                            np = add_sort(x, ov, np);
                    }
                    int dy = (int) y;
                    for (int j = 0; j < np - 1; j += 2) {
                        if (dtype == 0)
                            g2.drawLine((int) ov[j], dy, (int) ov[j + 1], dy);
                        else if (dtype == 1) {
                            vpl.add(new Point((int) ov[j], dy));
                            vpl.add(new Point((int) ov[j + 1], dy));
                        }
                    }
                    y += step;
                }
            }

        } else {

            if (gtype == 1 || gtype == 2) {
                double stepc = step * Math.sqrt(1 + k * k);
                double c1 = ymax - k * xmax;
                double c2 = ymax - k * xmin;
                double c3 = ymin - k * xmax;
                double c4 = ymin - k * xmin;
                double cmax = Math.max(c1, Math.max(c2, Math.max(c3, c4)));
                double cmin = Math.min(c1, Math.min(c2, Math.min(c3, c4)));
                double c = ((int) (cmin / stepc)) * stepc - stepc + stepc / 2;
                double x;

                while (c <= cmax) {
                    int np = 0;
                    for (int i = 0; i < n - 1; i++) {
                        if (xpoints[i + 1] == xpoints[i])
                            x = xpoints[i];
                        else {
                            double kl = ((double) (ypoints[i + 1] - ypoints[i])) / (xpoints[i + 1] - xpoints[i]);
                            x = (ypoints[i + 1] - kl * xpoints[i + 1] - c) / (k - kl);
                        }

                        if ((x - xpoints[i]) * (x - xpoints[i + 1]) < 0)
                            np = add_sort(x, ov, np);
                    }
                    for (int j = 0; j < np - 1; j += 2)
                        if (dtype == 0)
                            g2.drawLine((int) ov[j], (int) (k * ov[j] + c), (int) ov[j + 1], (int) (k * ov[j + 1] + c));
                        else if (dtype == 1) {
                            vpl.add(new Point((int) ov[j], (int) (k * ov[j] + c)));
                            vpl.add(new Point((int) ov[j + 1], (int) (k * ov[j + 1] + c)));
                        }
                    c += stepc;
                }
            }

            if (gtype == 1 || gtype == 3) {
                k = -1.0 / k;

                double stepc = step * Math.sqrt(1 + k * k);
                double c1 = ymax - k * xmax;
                double c2 = ymax - k * xmin;
                double c3 = ymin - k * xmax;
                double c4 = ymin - k * xmin;
                double cmax = Math.max(c1, Math.max(c2, Math.max(c3, c4)));
                double cmin = Math.min(c1, Math.min(c2, Math.min(c3, c4)));
                double c = ((int) (cmin / stepc)) * stepc - stepc + stepc / 2;
                double x;
                while (c <= cmax) {
                    int np = 0;
                    for (int i = 0; i < n - 1; i++) {
                        if (xpoints[i + 1] == xpoints[i])
                            x = xpoints[i];
                        else {
                            double kl = ((double) (ypoints[i + 1] - ypoints[i])) / (xpoints[i + 1] - xpoints[i]);
                            x = (ypoints[i + 1] - kl * xpoints[i + 1] - c) / (k - kl);
                        }

                        if ((x - xpoints[i]) * (x - xpoints[i + 1]) < 0)
                            np = add_sort(x, ov, np);
                    }
                    for (int j = 0; j < np - 1; j += 2)
                        if (dtype == 0)
                            g2.drawLine((int) ov[j], (int) (k * ov[j] + c), (int) ov[j + 1], (int) (k * ov[j + 1] + c));
                        else if (dtype == 1) {
                            vpl.add(new Point((int) ov[j], (int) (k * ov[j] + c)));
                            vpl.add(new Point((int) ov[j + 1], (int) (k * ov[j + 1] + c)));
                        }
                    c += stepc;
                }
            }
        }
        return vpl;
    }

    private static int add_sort(double a, double[] b, int n) {
        for (int i = 0; i < n; i++) {
            if (a < b[i]) {
                for (int j = n - 1; j >= i; j--)
                    b[j + 1] = b[j];
                b[i] = a;
                return n + 1;
            }
        }
        b[n] = a;
        return n + 1;

    }

    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (bVisible == false) return;

        int n = points.size();

        if (n == 0) return;
        if (points.get(0) != points.get(n - 1))
            return;

        int[] xpoints = new int[n];
        int[] ypoints = new int[n];

        for (int i = 0; i < n; i++) {
            GEPoint pt = points.get(i);
            xpoints[i] = (int) pt.getx();
            ypoints[i] = (int) pt.gety();
        }


        String s = "";
    	boolean bFirst = true;
        for (GEPoint pt : points) {
        	s += pt.getname() + ((bFirst) ? " moveto " : " lineto ");
        }

        if (type == 0) {
            s += "closepath";
            if (stype != 2)
                s += " Color" + new Integer(m_color).toString() + " ";
            else {
                Color c = super.getColor();
                double gray = (int) ((0.11 * c.getRed() + 0.59 * c.getGreen() + 0.3 * c.getBlue()) / 2.55) / 100.0;
                s += " " + gray + " " + gray + " " + gray + " setrgbcolor ";
            }
            s += "fill stroke\n";
            fp.write(s.getBytes());
        } else {
            fp.write(s.getBytes());
            ArrayList<Point> vp = drawGrid(null, xpoints, ypoints, n, 1, type);
            String st = "";
            for (int i = 0; i < vp.size() / 2; i++) {
                Point p1 = vp.get(2 * i);
                Point p2 = vp.get(2 * i + 1);
                st += GEPolygon.getPSLineString((int) p1.getX(), -(int) p1.getY(), (int) p2.getX(), -(int) p2.getY());
                if (i % 3 == 0)
                    st += "\n";
            }
            fp.write(st.getBytes());
            this.saveSuper(fp);
        }

    }
    
    public GEPolygon(DrawPanel dp, final Element thisElement, Map<Integer, GraphicEntity> mapGE) {
    	super(dp, thisElement);

		showArea = DrawPanelFrame.safeParseBoolean(thisElement.getAttribute("show_area"), false);
		ftype = DrawPanelFrame.safeParseInt(thisElement.getAttribute("polygon_type"), 0, 0, 1);
		type = DrawPanelFrame.safeParseInt(thisElement.getAttribute("grid_type"), 0);
		grid = DrawPanelFrame.safeParseInt(thisElement.getAttribute("grid"), 0);
		slope = DrawPanelFrame.safeParseInt(thisElement.getAttribute("slope"), 0);

		NodeList elist = thisElement.getChildNodes();
		for (int i = 0; i < elist.getLength(); ++i) {
			Node nn = elist.item(i);
            if (nn != null) {
            	String s = nn.getNodeName();
            	if (s.equalsIgnoreCase("points")) {
            		NodeList nl = nn.getChildNodes();
            		for (int ii = 0; ii < nl.getLength(); ++ii) {
            			Node nnn = nl.item(ii);
                        if (nnn != null && nnn instanceof Element) {
                        	s = nnn.getNodeName();
                        	if (s.equalsIgnoreCase("point")) {
                        		int pIndex = DrawPanelFrame.safeParseInt(((Element)nnn).getTextContent(), 0);
			            		GraphicEntity p = mapGE.get(pIndex);
			            		bIsValidEntity &= (p != null);
		                        if (p != null && !points.contains(p) && p instanceof GEPoint) {
		                			points.add((GEPoint)p);
		                        }
                        	}
                        }
            		}
            	}
            }
		}
    }    
    
    @Override
    public Element saveIntoXMLDocument(Element rootElement, String sTypeName) {
    	assert(rootElement != null);
    	if (rootElement != null) {
    		Document doc = rootElement.getOwnerDocument();

    		Element elementThis = super.saveIntoXMLDocument(rootElement, "polygon");

    		elementThis.setAttribute("show_area", String.valueOf(showArea));
    		elementThis.setAttribute("polygon_type", String.valueOf(ftype));
    		elementThis.setAttribute("grid_type", String.valueOf(type));
    		elementThis.setAttribute("grid", String.valueOf(grid));
    		elementThis.setAttribute("slope", String.valueOf(slope));

    		if (points != null && !points.isEmpty()) {
    			Element ePoints = doc.createElement("points");
        		elementThis.appendChild(ePoints);
        		
        		for (GEPoint p : points) {
        			if (p != null) {
        				Element child = doc.createElement("point");
        				child.setTextContent(String.valueOf(p.m_id));
        				ePoints.appendChild(child);
        			}
        		}
    		}
    		
    		return elementThis;
    	}
    	return null;
    }


//    public void Save(DataOutputStream out) throws IOException {
//        super.Save(out);
//        out.writeBoolean(showArea);
//        out.writeInt(ftype);
//        out.writeInt(type);
//        out.writeInt(grid);
//        out.writeInt(slope);
//
//        out.writeInt(points.size());
//        for (GEPoint p : points) {
//            out.writeInt(p.m_id);
//        }
//    }
//
//    public void Load(DataInputStream in, drawProcess dp) throws IOException {
//        if (CMisc.version_load_now < 0.010) {
//            m_id = in.readInt();
//            int size = in.readInt();
//            for (int i = 0; i < size; i++) {
//                int d = in.readInt();
//                points.add(dp.getPointById(d));
//            }
//            drawType drawt = new drawType();
//            drawt.Load(in);
//            m_color = drawt.color_index;
//            {
//                if (m_color == 1)
//                    m_color = 3;
//                else if (m_color == 2)
//                    m_color = 5;
//                else if (m_color == 3)
//                    m_color = 11;
//                else if (m_color == 7)
//                    m_color = 8;
//            }
//            m_dash = drawt.dash;
//            m_width = drawt.width;
//        } else {
//            super.Load(in, dp);
//            if (CMisc.version_load_now >= 0.051) {
//                showArea = in.readBoolean();
//                ftype = in.readInt();
//            }
//            type = in.readInt();
//            grid = in.readInt();
//            slope = in.readInt();
//            int size = in.readInt();
//            for (int i = 0; i < size; i++) {
//                int d = in.readInt();
//                points.add(dp.getPointById(d));
//            }
//
//
//        }
//    }

}
