package wprover;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.*;

import org.eclipse.jdt.annotation.NonNull;
import org.w3c.dom.*;

/**
 * Created by IntelliJ IDEA.
 * User: ${Yezheng}
 * Date: 2004-12-9
 */
public class GELine extends GraphicEntity implements Pointed {
    final public static int LLine = 0; // Line between two points
    final public static int PLine = 1; // Parallel to another designated line.
    final public static int TLine = 2; // Perpendicular to another designated line.
    final public static int BLine = 3; // Perpendicular bisector: defined by the two endpoints of the line to be bisected.
    final public static int CCLine = 4; // Line between the centers of two circles
    final public static int NTALine = 5; // Not referenced in this class. Introduced in DrawPanel when the action "eqanle added" takes place. Similar to ALine.
    final public static int ALine = 6; // The ALine of lines (e,f,g) is the line that has an angle with respect to g that matches the angle from f to e and starts from the initial endpoint of g.
    final public static int SLine = 7; // Line that is a specified angle from another designated line.
    final public static int ABLine = 8; // Angle bisector
    final public static int TCLine = 9; // Tangent to a circle

    final public static int ET_NORMAL = 0;
    final public static int ET_EXTENSION = 1;
    final public static int ET_ENDLESS = 2;

    int linetype = 0; // linetype: 1)LLine 2)PLine 3)TLine 4)BLine, etc.
    int ext_type = 0; // 0: normal, 1: extension ; 2: endless.
    int extent = UtilityMiscellaneous.LINDE_DRAW_EXT;


    final @NonNull ArrayList<GEPoint> points = new ArrayList<GEPoint>();
    private final @NonNull Set<Constraint> cons = new HashSet<Constraint>();
    private final @NonNull HashSet<Integer> setConstraintIndices = new HashSet<Integer>();

    final static int Width = 3000;
    final static int Height = 2000; // should be modified here.

    /**
	 * @see java.lang.Object#hashCode()
	 **/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		//result = prime * result + ((cons == null) ? 0 : cons.hashCode());
		result = prime * result + ext_type;
		result = prime * result + extent;
		result = prime * result + linetype;
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		result = prime * result + ((setConstraintIndices == null) ? 0 : setConstraintIndices.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 **/
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof GELine))
			return false;
		GELine other = (GELine) obj;
		if (cons == null) {
			if (other.cons != null)
				return false;
		} else if (!cons.equals(other.cons))
			return false;
		if (ext_type != other.ext_type)
			return false;
		if (extent != other.extent)
			return false;
		if (linetype != other.linetype)
			return false;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		if (setConstraintIndices == null) {
			if (other.setConstraintIndices != null)
				return false;
		} else if (!setConstraintIndices.equals(other.setConstraintIndices))
			return false;
		return true;
	}

	@Override
    public boolean isCoincidentWith(Pointed pointedEntity) {
    	return pointedEntity.isCoincidentWith(points);
    }
    
    @Override
    public boolean isCoincidentWith(final ArrayList<GEPoint> pList) {
    	if (pList != null) {
    		for (GEPoint pp : pList) {
    			if (points.contains(pp))
    				return true;
    		}
    	}
    	return false;
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
    public boolean isCoincidentWith(final GEPoint pp) {
    	return (pp != null && points.contains(pp));
    }

	public void initializePropertyPanel(JTable table, JSlider slider, JButton button1, JButton button2, JButton button3) {
		table.setValueAt(m_name, 0, 1);
		final GEPoint[] pl = getMaxMinPoint();
		table.setValueAt(getAllPointNames(), 1, 1);
		if (pl != null) {
			table.setValueAt(new Double(PanelProperty.round(pl[0].getx())), 2, 1);
			table.setValueAt(new Double(PanelProperty.round(pl[0].gety())), 3, 1);
			table.setValueAt(new Double(PanelProperty.round(pl[1].getx())), 4, 1);
			table.setValueAt(new Double(PanelProperty.round(pl[1].gety())), 5, 1);
		}
		button1.setBackground((ext_type == 0) ? Color.lightGray: Color.white);
		button2.setBackground((ext_type == 1) ? Color.lightGray: Color.white);
		button3.setBackground((ext_type != 0 && ext_type != 1) ? Color.lightGray: Color.white);
		slider.setValue(extent);
	}
	
    public GEPoint getCommonPoints(Pointed pointedEntity, Collection<GEPoint> collectionPoints) {
    	return pointedEntity.getCommonPoints(points, collectionPoints);
    }
    
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

    public GEPoint getCommonPoints(GEPoint pp, Collection<GEPoint> collectionPoints) {
    	GEPoint pCommon = null;
		if (points.contains(pp)) {
			pCommon = pp;
			if (collectionPoints != null)
				collectionPoints.add(pCommon);
		}
    	return pCommon;
    }
    
    public void draw(Graphics2D g2, boolean selected) {
        if (!isdraw()) return;

        if (selected) {
            prepareToBeDrawnAsSelected(g2);
        } else
            prepareToBeDrawnAsUnselected(g2);

        switch (linetype) {
            case GELine.LLine:
                drawLLine(this, g2);
                break;
            case GELine.PLine:
                drawPLine(this, g2);
                break;
            case GELine.TLine:
                drawTLine(this, g2);
                break;
            case GELine.BLine:
                drawBLine(this, g2);
                break;
            case GELine.CCLine:
                drawCCLine(this, g2);
                break;
            case GELine.ALine:
                drawALine(this, g2);
                break;
            case GELine.SLine:
                drawSLine(this, g2);
                break;
            case GELine.ABLine:
                drawABLine(this, g2);
                break;
            case GELine.TCLine:
                drawTCLine(this, g2);
                break;
        }
    }

//    public void draw(Graphics2D g2) {
//        draw(g2, false);
//    }

    public String TypeString() {
        String st = Language.getLs(40, "line");
        if (m_name == null) return st;
        return st + " " + m_name;
    }

    public String getSimpleName() {
        GEPoint pl[] = this.getTwoPointsOfLine();

        String s = new String();
        if (pl == null) {
            for (int i = 0; i < points.size(); i++) {
                s += points.get(i);
            }
        } else {
            s += pl[0];
            if (s.length() > 1)
                s += " ";
            s += pl[1];
        }
        return s;
    }

    public String getDescription() {
        String s = this.getSimpleName();
        String st = Language.getLs(40, "line ");
        return st + s;
    }

    public void setExtent(int n) {
        extent = n;
    }

    ///////////////////////////////////////////////////////////////////////////////
    public static void drawALine(GELine line, Graphics2D g2) {
        if (line.getPtsSize() >= 2) {
            drawLLine(line, g2);
            return;
        }
        double k = line.getSlope();
        GEPoint pt = line.getFirstPoint();
        drawXLine(pt.getx(), pt.gety(), k, g2);
    }

    public static void drawABLine(GELine line, Graphics2D g2) {
        if (line.getPtsSize() >= 2) {
            drawLLine(line, g2);
            return;
        }
        double k = line.getSlope();
        GEPoint pt = line.getFirstPoint();
        drawXLine(pt.getx(), pt.gety(), k, g2);
    }

    public static void drawTCLine(GELine line, Graphics2D g2) {
        if (line.getPtsSize() >= 2) {
            drawLLine(line, g2);
            return;
        }
        double k = line.getSlope();
        GEPoint pt = line.getFirstPoint();
        drawXLine(pt.getx(), pt.gety(), k, g2);
    }

    public static void drawBLine(GELine line, Graphics2D g2) {
        if (line.getPtsSize() >= 2) {
            drawLLine(line, g2);
            return;
        }
        Constraint cs = line.getConstraintByType(Constraint.BLINE);
        GEPoint p1 = (GEPoint) cs.getelement(1);
        GEPoint p2 = (GEPoint) cs.getelement(2);

        double k = line.getSlope();
        double x = (p1.getx() + p2.getx()) / 2.0;
        double y = (p1.gety() + p2.gety()) / 2.0;
        drawXLine(x, y, k, g2);
    }

    public static void drawCCLine(GELine line, Graphics2D g2) {
        if (line.getPtsSize() >= 2) {
            drawLLine(line, g2);
            return;
        }
        Constraint cs = line.getFirstConstraintOfType(Constraint.CCLine);
        if (cs == null)
        	return;
        GECircle c1 = (GECircle) cs.getelement(1);
        GECircle c2 = (GECircle) cs.getelement(2);
        GEPoint p1 = c1.o;
        GEPoint p2 = c2.o;

        double xa = 0;
        double xb = Width;


        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();

        double r1 = c1.getRadius();
        double r2 = c2.getRadius();

        double a = (x1 * x1 - x2 * x2 + y1 * y1 - y2 * y2 - r1 * r1 + r2 * r2);

        double ya, yb;
        if (Math.abs(y1 - y2) < UtilityMiscellaneous.ZERO) {
            xa = xb = -(a) / (2 * (x2 - x1));
            ya = 0;
            yb = Height;
        } else {
            ya = (a - 2 * xa * (x1 - x2)) / (2 * (y1 - y2));
            yb = (a - 2 * xb * (x1 - x2)) / (2 * (y1 - y2));
        }

        g2.drawLine((int) xa, (int) ya, (int) xb, (int) yb);
    }

    public static void drawSLine(GELine line, Graphics2D g2) {
        if (line.getPtsSize() >= 2) {
            drawLLine(line, g2);
            return;
        }
        Constraint cs = line.getFirstConstraint();
        //GELine l = (GELine) cs.getelement(0);
        GELine l1 = (GELine) cs.getelement(1);
        GEPoint p = l1.getFirstPoint();

        double k = line.getSlope();
        drawXLine(p.getx(), p.gety(), k, g2);
    }

    /**
     * Draws a line on the screen at the point (x0,y0) with slope k.
     * @param x0 The x-coordinate of one point on the line.
     * @param y0 The y-coordinate of one point on the line.
     * @param k The slope of the line to be drawn.
     * @param g2 The Graphics2D context in which to draw the line.
     */
    public static void drawXLine(double x0, double y0, double k, Graphics2D g2) {
        if (Math.abs(1 / k) < UtilityMiscellaneous.ZERO) {
        	// Draw a vertical line.
            g2.drawLine((int) x0, 0, (int) x0, Height);
        } else if (Math.abs(k) < UtilityMiscellaneous.ZERO) {
        	// Draw a horizontal line.
            g2.drawLine(0, (int) y0, Width, (int) y0);
        } else {
        	// Draw a slanted line.
            double y1 = 0;
            double y2 = Height;
            double x1 = (y1 - y0 + k * x0) / k;
            double x2 = (y2 - y0 + k * x0) / k;
            g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        }
    }

    public static void drawTLine(GELine line, Graphics2D g2) {
        int nPointCount = line.getPtsSize();
        
        if (nPointCount == 0)
            return;

        if (nPointCount >= 2) {
            drawLLine(line, g2);
            return;
        }

        Constraint cs = line.getFirstConstraint();
        GELine l = (GELine) cs.getelement(1);
        GEPoint p = line.getFirstPoint();
        double k = l.getSlope();
        drawXLine(p.getx(), p.gety(), -1 / k, g2);
    }

    public static void drawPLine(GELine line, Graphics2D g2) {
        int nPointCount = line.getPtsSize();
        
        if (nPointCount == 0)
            return;

        if (nPointCount >= 2) {
            drawLLine(line, g2);
            return;
        }

        Constraint cs = line.getFirstConstraint();
        GELine l = (GELine) cs.getelement(1);

        GEPoint p = line.getFirstPoint();
        drawXLine(p.getx(), p.gety(), l.getSlope(), g2);
    }

    public static void drawLLine(GELine line, Graphics2D g2) {

        GEPoint[] pl = line.getMaxMinPoint();
        if (pl == null) return;

        if (line.ext_type == 0) {
            g2.draw(new Line2D.Double(pl[0].getx(), pl[0].gety(), pl[1].getx(), pl[1].gety()));
            
        } else {

	        double dx = pl[1].getx() - pl[0].getx();
	        double dy = pl[1].gety() - pl[0].gety();
	        double dlt = Math.sqrt(dx * dx + dy * dy);
	
	        if (line.ext_type == 1) {
	            dx = dx * line.extent / dlt;
	            dy = dy * line.extent / dlt;
	
	            g2.draw(new Line2D.Double(pl[0].getx() - dx, pl[0].gety() - dy, pl[1].getx() + dx, pl[1].gety() + dy));
	        } else if (line.ext_type == 2) {
	            @SuppressWarnings("unused")
				int len = Width > Height ? Width : Height;
	            dx = len * dx / dlt;
	            dy = len * dy / dlt;
	
	            g2.draw(new Line2D.Double(pl[0].getx() - dx, pl[0].gety() - dy, pl[1].getx() + dx, pl[1].gety() + dy));
	        }
        }
    }

    ///   /////////////////////////////////////

    public static void drawPParaLine(GELine line, GEPoint pt, Graphics2D g2) {
        if (line.isVertical()) {
            double x = pt.getx();
            double y1 = 0;
            double y2 = Height;
            g2.drawLine((int) x, (int) y1, (int) x, (int) y2);

        } else {
            double k = line.getSlope();
            double x1 = 0;
            double x2 = Width;
            double y1 = k * (0 - pt.getx()) + pt.gety();
            double y2 = k * (x2 - pt.getx()) + pt.gety();
            g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        }
    }

    public static void drawTPerpLine(GELine line, GEPoint pt, Graphics2D g2) {
        if (line.isHorizonal()) {
            double x = pt.getx();
            double y1 = 0;
            double y2 = Height;
            g2.drawLine((int) x, (int) y1, (int) x, (int) y2);
        } else if (line.isVertical()) {
            g2.drawLine(0, (int) pt.gety(), Width, (int) pt.gety());
        } else {
            double k = line.getSlope();
            k = -1.0 / k;
            double y1 = 0;
            double y2 = Height;
            double x1 = (y1 - pt.gety() + k * pt.getx()) / k;
            double x2 = (y2 - pt.gety() + k * pt.getx()) / k;
            g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        }
    }
    /////////////////////////////////////////////

    public String getAllPointNames() {
        String s = new String();
        for (GEPoint p : points) {
        	if (!s.isEmpty())
        		s += ", ";
        	s += p.m_name;
        }
        return s;
    }

    public GEPoint getPointOtherThan(GEPoint t) {
        GEPoint p = null;
        for (GEPoint pt : points) {
            if (pt != t && (p == null || p.x1.xindex > pt.x1.xindex))
                p = pt;
        }
        return p;
    }

    public GEPoint getFirstPoint() {
        GEPoint p = null;
        for (GEPoint pt : points) {
            if (p == null || p.x1.xindex > pt.x1.xindex)
                p = pt;
        }
        return p;
    }

    public GEPoint getAPointBut(GEPoint t) {
        for (GEPoint pt : points) {
            if (pt != t)
            	return pt;
        }
        return null;
    }

    public GEPoint getMaxXPoint() {
        GEPoint p = null;
        for (GEPoint pt : points) {
            if (p == null || p.getx() < pt.getx())
                p = pt;
        }
        return p;
    }

    public boolean areBothEndpointsFree() {
        GEPoint p1, p2;
        p1 = p2 = null;

        for (GEPoint p : points) {
            if (p1 == null)
                p1 = p;
            else if (p.x1.xindex < p1.x1.xindex) {
                p2 = p1;
                p1 = p;
            } else if (p2 == null || p.x1.xindex < p2.x1.xindex)
                p2 = p;
        }
        if (p1 == null || p2 == null)
            return false;
        return p1.isAFreePoint() && p2.isAFreePoint();
    }

    public boolean isParallel(GELine line) {
        if (linetype == PLine) {
	        for (Constraint cs : cons) {
	            if (cs.GetConstraintType() == Constraint.PARALLEL) {
		            GELine line1 = (GELine) cs.getelement(0);
		            GELine line2 = (GELine) cs.getelement(1);
		            if (line2 == this) {
		                line2 = line1;
		                line1 = this;
		            }
		            if (line2 == line)
		                return true;
		            return line2.isParallel(line);
	            }
	        }
        }
        return false;
    }

    public boolean isVertical(GELine line) {
        for (Constraint cs : cons) {
            if (cs.GetConstraintType() == Constraint.PERPENDICULAR) {
	            GELine line1 = (GELine) cs.getelement(0);
	            GELine line2 = (GELine) cs.getelement(1);
	            if (line2 == this) {
	                line2 = line1;
	                line1 = this;
	            }
	            return (line2 == line && line1 == this);
	        }
        }
        return false;
    }

    public GEPoint get_Lpt1(GEPoint px) {
        GEPoint p1 = null;
        if (px != null) {
        	for (GEPoint p : points) {
	            if (p != px && (p1 == null || p.x1.xindex < p1.x1.xindex))
	                p1 = p;
            }
        }
        return p1;
    }

    public GEPoint get_Lptv(GEPoint px, double x, double y) {      // Vector (x,y),px == (x,y),p
        GEPoint p1 = null;
        if (px != null) {
	        double x0 = px.getx();
	        double y0 = px.gety();
	        for (GEPoint p : points) {
	            if (!(px != p && ((x0 - x) * (x0 - p.getx()) < 0 || (y0 - y) * (y0 - p.gety()) < 0))) {
	            	if (p != px && (p1 == null ||p.x1.xindex < p1.x1.xindex))
	            		p1 = p;
	            }
	        }
        }
        return p1;
    }

    public GEPoint[] getTwoPointsOfLine() {
        GEPoint p1, p2;
        p1 = p2 = null;

        for (GEPoint p : points) {
            if (p1 == null)
                p1 = p;
            else if (p.x1.xindex < p1.x1.xindex) {
                p2 = p1;
                p1 = p;
            } else if (p2 == null || p.x1.xindex < p2.x1.xindex)
                p2 = p;
        }
        if (p1 == null || p2 == null)
            return null;
        GEPoint[] pl = new GEPoint[2];
        pl[0] = p1;
        pl[1] = p2;
        return pl;
    }

    public String getDescription2() {
        GEPoint[] s = this.getTwoPointsOfLine();
        if (s == null) return m_name;
        return s[0].m_name + s[1].m_name;
    }


    public GEPoint[] getMaxMinPoint() {
        return getMaxMinPoint(true);
    }

    public GEPoint[] getMaxMinPoint(boolean ckv) {
        if (points == null || points.size() < 2)
        	return null;

        GEPoint p1, p2;
        p1 = points.get(0);
        if (p1 == null)
        	return null;

        p2 = null;
        for (int i = 1; i < points.size(); ++i) {
        	GEPoint p = points.get(i);
            if (p == null) continue;
            if (ckv && !p.bVisible) continue;

            if (p.x1.value < p1.x1.value) {
                if (p2 == null) {
                    p2 = p1;
                    p1 = p;
                } else
                    p1 = p;
            } else if (p2 == null || p.x1.value > p2.x1.value)
                p2 = p;
        }

        if (p1.x1 == null || p2 == null || p2.x1 == null)
        	return null;

        if (Math.abs(p1.x1.value - p2.x1.value) < UtilityMiscellaneous.ZERO) {
            p1 = points.get(0);
            p2 = null;
            for (int i = 1; i < points.size(); ++i) {
            	GEPoint p = points.get(i);
                if (p.y1.value < p1.y1.value) {
                    if (p2 == null) {
                        p2 = p1;
                        p1 = p;
                    } else
                        p1 = p;
                } else if (p2 == null || p.y1.value > p2.y1.value)
                    p2 = p;
            }

        }


        GEPoint[] pl = new GEPoint[2];
        pl[0] = p1;
        pl[1] = p2;
        return pl;
    }

    public Constraint getFirstConstraint() {
    	if (cons.isEmpty())
    		return null;
    	return cons.iterator().next();
    }
    
    public Constraint getFirstConstraintOfType(int cType) {
    	for (Constraint c : cons)
    		if (c.GetConstraintType() == cType)
    			return c;
    	return null;
    }
    
    
    public Constraint getConstraintByType(int t) {
        for (Constraint c : cons) {
            if (c.GetConstraintType() == t)
            	return c;
        }
        return null;
    }

    public final boolean containsPoints(GEPoint...p) {
    	for (int i = 0; i < p.length; ++i)
    		if (!points.contains(p[i]))
    			return false;
    	return true;
    }

    /**
     * Returns the number of constraints imposed on this line.
     * @return The number of constraints attached to this line.
     */
    public final int getconsSize() {
        return cons.size();
    }

    /**
     * Returns the number of points in this line.
     * @return The number of points explicitly associated with this line.
     */
    public final int getPtsSize() {
        return points.size();
    }

    public GEPoint getPoint(int n) {
        return (n < 0 || points == null || n >= points.size()) ? null : points.get(n);
    }


    public boolean isVertical() {
        if (points != null && points.size() >= 2) {
            GEPoint p1 = points.get(0);
            GEPoint p2 = points.get(1);
            return (Math.abs(p2.getx() - p1.getx()) < UtilityMiscellaneous.HV_ZERO);
        }

        for (Constraint cs : cons) {
            switch (cs.GetConstraintType()) {
                case Constraint.PARALLEL: {
                    GELine line = (GELine) cs.getelement(1);
                    return line.isVertical();
                }
                case Constraint.PERPENDICULAR: {
                    GELine line = (GELine) cs.getelement(1);
                    return line.isHorizonal();
                }
                case Constraint.CCLine: {
                    GECircle c1 = (GECircle) cs.getelement(1);
                    GECircle c2 = (GECircle) cs.getelement(2);
                    return (Math.abs(c1.o.getx() - c2.o.getx()) < UtilityMiscellaneous.HV_ZERO);
                }
                case Constraint.ALINE: {
                    GELine ln0 = (GELine) cs.getelement(0);
                    GELine ln1 = (GELine) cs.getelement(1);
                    GELine ln2 = (GELine) cs.getelement(2);
                    double k = GELine.getALineK(ln0, ln1, ln2);
                    return (Math.abs(k) > UtilityMiscellaneous.MAX_K);
                }
            }
        }
        return false;
    }

    public boolean isHorizonal() {
        if (linetype == GELine.LLine && points != null && points.size() >= 2) {
            GEPoint p1 = points.get(0);
            GEPoint p2 = points.get(1);
            return (Math.abs(p2.gety() - p1.gety()) < UtilityMiscellaneous.HV_ZERO);
        }

        for (Constraint cs : cons) {
            switch (cs.GetConstraintType()) {
                case Constraint.PARALLEL: {
                    GELine line = (GELine) cs.getelement(1);
                    return line.isHorizonal();
                }
                case Constraint.PERPENDICULAR: {
                    GELine line = (GELine) cs.getelement(1);
                    return line.isVertical();
                }
                case Constraint.CCLine: {
                    GECircle c1 = (GECircle) cs.getelement(1);
                    GECircle c2 = (GECircle) cs.getelement(2);
                    return (Math.abs(c1.o.gety() - c2.o.gety()) < UtilityMiscellaneous.HV_ZERO);
                }
            }
        }
        return false;
    }

/**
 *     Returns the slope of this line.
 */
    public double getSlope() {
    	if (points != null && points.size() >= 2) {
            GEPoint p1 = points.get(0);
            GEPoint p2 = points.get(1);
            return (p2.gety() - p1.gety()) / (p2.getx() - p1.getx());
        }

        for (Constraint cs : cons) {
            switch (cs.GetConstraintType()) {
                case Constraint.PARALLEL: {
                    GELine line = (GELine) cs.getelement(1);
                    return line.getSlope();
                }
                case Constraint.PERPENDICULAR: {
                    GELine line = (GELine) cs.getelement(1);
                    return -1.0 / line.getSlope();
                }
                case Constraint.CCLine: {
                    GECircle c1 = (GECircle) cs.getelement(1);
                    GECircle c2 = (GECircle) cs.getelement(2);
                    return ((c1.o.getx() - c2.o.getx()) / (c1.o.gety() - c2.o.gety()));
                }
                case Constraint.ALINE: {
                    GELine ln0 = (GELine) cs.getelement(0);
                    GELine ln1 = (GELine) cs.getelement(1);
                    GELine ln2 = (GELine) cs.getelement(2);
                    double k = GELine.getALineK(ln0, ln1, ln2);
                    return k;
                }
                case Constraint.NTANGLE: {
                    GELine ln = (GELine) cs.getelement(0);
                    GELine ln1 = (GELine) cs.getelement(1);
                    GELine ln2 = (GELine) cs.getelement(2);
                    GELine ln3 = (GELine) cs.getelement(3);
                    GEPoint pt = (GEPoint) cs.getelement(4);
                    GEPoint[] l1 = ln.getTwoPointsOfLine();
                    GEPoint[] l2 = ln1.getTwoPointsOfLine();
                    GEPoint[] l3 = ln2.getTwoPointsOfLine();
                    if (l1 == null || l2 == null || l3 == null) break;
                    GEPoint c = ln3.getFirstPoint();
                    if (c == pt) break;
                    double k1 = ln.getSlope();
                    double k2 = ln1.getSlope();
                    double k3 = ln2.getSlope();
                    double k = (k3 * k2 * k1 + k3 + k2 - k1) / (1 + k3 * k1 + k2 * k1 - k3 * k2);
                    return k;
                }
                case Constraint.SANGLE: {
                    GELine ln = (GELine) cs.getelement(0);
                    Integer I = (Integer) cs.getelement(2);
                    double k = ln.getSlope();
                    int v = I.intValue();
                    double k1 = -Constraint.getSpecifiedAnglesMagnitude(v);
                    if (ln.isVertical()) {
                        return -1 / k1;
                    } else
                        return (k1 + k) / (1 - k1 * k);
                }
                case Constraint.BLINE: {
                    GEPoint p1 = (GEPoint) cs.getelement(1);
                    GEPoint p2 = (GEPoint) cs.getelement(2);
                    return -(p1.getx() - p2.getx()) / (p1.gety() - p2.gety());
                }
                case Constraint.TCLINE: {
                    //CLine ln = (CLine) cs.getelement(1);
                    GECircle c = (GECircle) cs.getelement(0);
                    GEPoint p2 = (GEPoint) cs.getelement(2);
                    GEPoint p1 = c.o;
                    return -(p1.getx() - p2.getx()) / (p1.gety() - p2.gety());
                }
                case Constraint.ANGLE_BISECTOR:
                    GEPoint p1 = (GEPoint) cs.getelement(0);
                    GEPoint p2 = (GEPoint) cs.getelement(1);
                    GEPoint p3 = (GEPoint) cs.getelement(2);

                    double k1 = (p2.gety() - p1.gety()) / (p2.getx() - p1.getx());
                    double k2 = (p2.gety() - p3.gety()) / (p2.getx() - p3.getx());
                    if (k1 > UtilityMiscellaneous.MAX_SLOPE)
                        k1 = UtilityMiscellaneous.MAX_SLOPE;
                    else if (k1 < -UtilityMiscellaneous.MAX_SLOPE)
                        k1 = -UtilityMiscellaneous.MAX_SLOPE;

                    if (k2 > UtilityMiscellaneous.MAX_SLOPE)
                        k2 = UtilityMiscellaneous.MAX_SLOPE;
                    else if (k2 < -UtilityMiscellaneous.MAX_SLOPE)
                        k2 = -UtilityMiscellaneous.MAX_SLOPE;
                    double a = k1 + k2;
                    if (a == 0) {
                        a = 10E-6;
                    }

                    double b = -2 * (k1 * k2 - 1) / a;
                    double c = -1;
                    a = 1;

                    double d = Math.sqrt(b * b - 4 * c);

                    k1 = (-b + d) / 2;
                    k2 = (-b - d) / 2;

                    double x0 = p2.getx();
                    double y0 = p2.gety();

                    double x1 = -0.4455;
                    double y1 = y0 + k1 * (x1 - x0);
                    if (cs.check_constraint(x1, y1)) return k1;

                    y1 = y0 + k2 * (x1 - x0);
                    if (cs.check_constraint(x1, y1))
                        return k2;
                    return 0.0;
            }
        }
        return 0.0;
    }

    public static double getALineK(GELine ln1, GELine ln2, GELine ln3) {
        GEPoint lp1[] = ln1.getTwoPointsOfLine();
        GEPoint lp2[] = ln2.getTwoPointsOfLine();
        GEPoint lp3[] = ln3.getTwoPointsOfLine();
        return getALineK(lp1[0], lp1[1], lp2[0], lp2[1], lp3[0], lp3[1]);
    }

    public static double getALineK(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4, GEPoint p5, GEPoint p6) {
        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();
        double x3 = p3.getx();
        double y3 = p3.gety();
        double x4 = p4.getx();
        double y4 = p4.gety();
        double x5 = p5.getx();
        double y5 = p5.gety();
        double x6 = p6.getx();
        double y6 = p6.gety();
        double t1 = (y6 - y5) * ((y2 - y1) * (y4 - y3) + (x2 - x1) * (x4 - x3)) + (y4 - y3) * (x6 - x5) * (x2 - x1) - (y2 - y1) * (x6 - x5) * (x4 - x3);
        double t2 = (x6 - x5) * (x4 - x3) * (x2 - x1) + (y2 - y1) * (y4 - y3) * (x6 - x5) - (y6 - y5) * (y4 - y3) * (x2 - x1) + (y6 - y5) * (y2 - y1) * (x4 - x3);
        return t1 / t2;
    }

    public void add(@NonNull GEPoint a) {
    	if (!points.contains(a)) // TODO: Should convert points to a set because they are not kept in linear order.
    		points.add(a);
    }

    public void addConstraint(@NonNull Constraint cs) {
    	if (!cons.contains(cs))
    		cons.add(cs);
    }

    public void clearpoints() {
        points.clear();
    }

    public GELine(int t, GEPoint...gepoints) {
        super(GraphicEntity.LINE);
        linetype = t;
        points.clear();
        if (gepoints != null) {
            for (GEPoint p : gepoints) {
            	if (p != null && !points.contains(p))
            		add(p);
            }
        }
    }

    public GELine(GEPoint...gepoints) {
        super(GraphicEntity.LINE);
        linetype = LLine;
        points.clear();
        if (gepoints != null) {
            for (GEPoint p : gepoints) {
            	if (p != null && !points.contains(p))
            		add(p);
            }
        }
    }
    
    public GELine(DrawPanel dp, final Element thisElement, Map<Integer, GraphicEntity> mapGE) {
    	super(dp, thisElement);

    	linetype = DrawPanelFrame.safeParseInt(thisElement.getAttribute("line_type"), 0);
		ext_type = DrawPanelFrame.safeParseInt(thisElement.getAttribute("ext_type"), 0);
		extent = DrawPanelFrame.safeParseInt(thisElement.getAttribute("extent"), 50);
		NodeList elist = thisElement.getChildNodes();
		
		for (int i = 0; i < elist.getLength(); ++i) {
			Node nn = elist.item(i);
            if (nn != null && nn instanceof Element) {
            	String s = nn.getNodeName();
            	if (s.equalsIgnoreCase("constraints")) {
            		NodeList nl = nn.getChildNodes();
            		for (int ii = 0; ii < nl.getLength(); ++ii) {
            			Node nnn = nl.item(ii);
                        if (nnn != null && nnn instanceof Element) {
                        	s = nnn.getNodeName();
                        	if (s.equalsIgnoreCase("constraint")) {
                        		int cIndex = DrawPanelFrame.safeParseInt(((Element)nnn).getTextContent(), 0);
                        		setConstraintIndices.add(cIndex);
                        	}
                        }
            		}
            	}
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
    
    public final boolean sameLine(GEPoint A, GEPoint B) {
        int counter = 0;
        for (GEPoint p : points) {
            if (isEqual(A, p) || isEqual(B, p)) {
                ++counter;
            }
        }
        return (counter == 2);
    }

    public final boolean pointOnLine(GEPoint p) {
        return points.contains(p);
    }

    public final boolean pointOnLineN(GEPoint p) {
        return distance(p.getx(), p.gety()) < UtilityMiscellaneous.ZERO;
    }

    public final boolean pointOnLine(double x, double y) {
        return distance(x, y) < UtilityMiscellaneous.ZERO;
    }

    public final static boolean isEqual(GEPoint A, GEPoint B) {
    	return (A.x1.xindex == B.x1.xindex && A.y1.xindex == B.y1.xindex);
    }

    public GEPoint getSideMostPoint(GEPoint p, int x, int y) {
        GEPoint pp = null;
        for (GEPoint tp : points) {
            if ((x - tp.getx()) * (p.getx() - tp.getx()) > 0 || (y - tp.gety()) * (p.gety() - tp.gety()) > 0
                    && (pp == null || pp.x1.xindex > tp.x1.xindex))
                pp = tp;
        }
        return pp;
    }

    public static boolean mouse_on_line(double x, double y, double x1, double y1, double x2, double y2) {
        double k = -(y2 - y1) / (x2 - x1);

        if (Math.abs(k) > UtilityMiscellaneous.ZERO && Math.abs(1 / k) < UtilityMiscellaneous.ZERO) {
            return Math.abs(x - x1) < UtilityMiscellaneous.PIXEPS;
        }
        double len = Math.abs(y + k * x - y1 - k * x1) / Math.sqrt(1 + k * k);
        return len < UtilityMiscellaneous.PIXEPS;
    }

    public final static double distanceToPoint(GELine ln, double x, double y) {

        int n = ln.getPtsSize();
        if (n < 2) {
            if (ln.linetype == CCLine) {
            	Constraint cs = null;
                for (Constraint cs1 : ln.cons) {
                	if (cs1.GetConstraintType() == Constraint.CCLine)
                		cs = cs1;
                }
                if (cs == null)
                	return -1.0;
                
                GECircle c1 = (GECircle) cs.getelement(1);
                GECircle c2 = (GECircle) cs.getelement(2);
                GEPoint p1 = c1.o;
                GEPoint p2 = c2.o;

                double x1 = p1.getx();
                double y1 = p1.gety();
                double x2 = p2.getx();
                double y2 = p2.gety();
                double r1 = c1.getRadius();
                double r2 = c2.getRadius();
                double r = Math.abs(-2 * x * (x1 - x2) + x1 * x1 - x2 * x2 - 2 * y * (y1 - y2) + y1 * y1 - y2 * y2 - r1 * r1 + r2 * r2);
                r = r / (2 * Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
                return r;
            } else if (ln.linetype == BLine) {
                Constraint cs = ln.getConstraintByType(Constraint.BLINE);
                GEPoint p1 = (GEPoint) cs.getelement(1);
                GEPoint p2 = (GEPoint) cs.getelement(2);
                double x0 = (p1.getx() + p2.getx()) / 2;
                double y0 = (p1.gety() + p2.gety()) / 2;

                double k = -ln.getSlope();
                if (Math.abs(k) > UtilityMiscellaneous.ZERO && Math.abs(1 / k) < UtilityMiscellaneous.ZERO) {
                    return Math.abs(x - x0);
                }
                double len = Math.abs(y + k * x - y0 - k * x0) / Math.sqrt(1 + k * k);
                return len;
            }
        }

        GEPoint pt = ln.getFirstPoint();
        if (pt == null) {
            return Double.MAX_VALUE;
        }
        double k = -ln.getSlope();

        if (Math.abs(k) > UtilityMiscellaneous.ZERO && Math.abs(1 / k) < UtilityMiscellaneous.ZERO) {
            return Math.abs(x - pt.getx());
        }
        double len = Math.abs(y + k * x - pt.gety() - k * pt.getx()) / Math.sqrt(1 + k * k);
        return len;

    }


    public final static double distanceToPoint(GEPoint p, GEPoint p1, GEPoint p2) {
        return distanceToPoint(p.getx(), p.gety(), (p1.getx() - p2.getx()) / (p1.gety() - p2.gety()), p1.getx(), p1.gety());
    }

    public final static double distanceToPoint(double x1, double y1, double k, double x, double y) {
        k = -k;

        if (Math.abs(k) > UtilityMiscellaneous.ZERO && Math.abs(1 / k) < UtilityMiscellaneous.ZERO) {
            return Math.abs(x - x1);
        }
        double len = Math.abs(y + k * x - y1 - k * x1) / Math.sqrt(1 + k * k);
        return len;
    }

    public final boolean inside(double x, double y) {
        if (this.ext_type == ET_ENDLESS)
            return true;

        GEPoint p1, p2;
        p1 = p2 = null;

        GEPoint pl[] = this.getMaxMinPoint();
        if (pl == null)
            return true;

        p1 = pl[0];
        p2 = pl[1];

        double x1, y1, x2, y2;
        x1 = p1.getx();
        y1 = p1.gety();

        x2 = p2.getx();
        y2 = p2.gety();

        if (ext_type == ET_EXTENSION) {
            int len = extent;
            double dx = x2 - x1;
            double dy = y2 - y1;
            double l1 = Math.sqrt(dx * dx + dy * dy);
            dx = len * dx / l1;
            dy = len * dy / l1;
            x1 -= dx;
            y1 -= dy;
            x2 += dx;
            y2 += dy;
        }

        double e1 = (x - x1) * (x - x2);
        double e2 = (y - y1) * (y - y2);

        if (Math.abs(e1) < UtilityMiscellaneous.ZERO && Math.abs(e2) < UtilityMiscellaneous.ZERO)
            return true;
        if (Math.abs(e1) < UtilityMiscellaneous.ZERO && e2 < 0 || Math.abs(e2) < UtilityMiscellaneous.ZERO && e1 < 0)
            return true;
        return (e1 <= 0 && e2 <= 0);
    }

    public final boolean inside(double x, double y, double eps) {
        if (this.ext_type == ET_ENDLESS)
            return true;

        GEPoint p1, p2;
        p1 = p2 = null;

        GEPoint pl[] = this.getMaxMinPoint();
        if (pl == null)
            return true;

        p1 = pl[0];
        p2 = pl[1];

        double x1, y1, x2, y2;
        x1 = p1.getx();
        y1 = p1.gety();

        x2 = p2.getx();
        y2 = p2.gety();

        if (ext_type == ET_EXTENSION) {
            int len = extent;
            double dx = x2 - x1;
            double dy = y2 - y1;
            double l1 = Math.sqrt(dx * dx + dy * dy);
            dx = len * dx / l1;
            dy = len * dy / l1;
            x1 -= dx;
            y1 -= dy;
            x2 += dx;
            y2 += dy;
        }

        double e1 = (x - x1) * (x - x2);
        double e2 = (y - y1) * (y - y2);

        eps *= eps;
        if (Math.abs(e1) < eps && Math.abs(e2) < eps)
            return true;
        if (Math.abs(e1) < eps && e2 < 0 || Math.abs(e2) < eps && e1 < 0)
            return true;
        return (e1 <= 0 && e2 <= 0);
    }

    public final boolean nearline(double x, double y) {     // is the point near the line
        return distanceToPoint(this, x, y) < UtilityMiscellaneous.PIXEPS;
    }

    public boolean isLocatedNear(double x, double y) {
        if (!bVisible) return false;

        if (inside(x, y, UtilityMiscellaneous.PIXEPS)) {
            double d = distanceToPoint(this, x, y);
            if (d < UtilityMiscellaneous.PIXEPS)
                return true;
        }
        return false;
    }

    public final double distance(double x, double y) {
        return distanceToPoint(this, x, y);
    }

    public boolean isOnMiddle(double x, double y) {
        if (points.size() != 2)
            return false;
        GEPoint p1 = points.get(0);
        GEPoint p2 = points.get(1);

        double dx = (p1.getx() + p2.getx()) / 2;
        double dy = (p1.gety() + p2.gety()) / 2;
        if (Math.abs(p1.getx() - p2.getx()) < UtilityMiscellaneous.PIXEPS &&
                Math.abs(p1.gety() - p2.gety()) < UtilityMiscellaneous.PIXEPS)
            return false;

        return Math.abs(x - dx) < UtilityMiscellaneous.PIXEPS && Math.abs(y - dy) < UtilityMiscellaneous.PIXEPS;
    }

    public final boolean pointonMiddle(GEPoint pt) {
        if (points.size() != 2)
            return false;
        GEPoint p1 = points.get(0);
        GEPoint p2 = points.get(1);

        double dx = (p1.getx() + p2.getx()) / 2;
        double dy = (p1.gety() + p2.gety()) / 2;
        if (Math.abs(p1.getx() - p2.getx()) < UtilityMiscellaneous.PIXEPS &&
                Math.abs(p1.gety() - p2.gety()) < UtilityMiscellaneous.PIXEPS)
            return false;

        if (!(Math.abs(pt.getx() - dx) < UtilityMiscellaneous.PIXEPS && Math.abs(pt.gety() - dy) < UtilityMiscellaneous.PIXEPS))
            return false;
        pt.setXY(dx, dy);
        return true;
    }

    public void pointonline(GEPoint pt) {      //set the location of the point to line
        if (this.linetype == CCLine)
            return;

        double x1, y1, x3, y3, xt, yt;

        GEPoint p = this.getFirstPoint();
        x1 = y1 = 0;
        if (p == null) {
            if (linetype == BLine) {
                Constraint cs = getConstraintByType(Constraint.BLINE);
                GEPoint p1 = (GEPoint) cs.getelement(1);
                GEPoint p2 = (GEPoint) cs.getelement(2);
                x1 = (p1.getx() + p2.getx()) / 2;
                y1 = (p1.gety() + p2.gety()) / 2;
            }
        } else {
            x1 = p.getx();
            y1 = p.gety();
        }

        x3 = pt.x1.value;
        y3 = pt.y1.value;

        if (this.linetype == PLine || this.linetype == TLine || this.linetype == CCLine || linetype == ALine) {

            if (this.isVertical()) {
                xt = x1;
                yt = y3;
            } else if (this.isHorizonal()) {
                xt = x3;
                yt = y1;
            } else {
                double k = this.getSlope();
                xt = ((y3 - y1) * k + x1 * k * k + x3) / (1 + k * k);
                yt = y1 + (xt - x1) * k;
            }
        } else {
            if (this.isVertical()) {
                xt = x1;
                yt = y3;
            } else {
                double k = this.getSlope();

                double x0 = pt.getx();
                double y0 = pt.gety();

                xt = (k * (y0 - y1) + k * k * x1 + x0) / (k * k + 1);
                yt = y1 + k * (xt - x1);
            }
        }
        pt.setXY(xt, yt);
    }

    public static GEPoint[] commonPoint(GELine ln, GECircle c) {
        GEPoint t1, t2;
        t1 = t2 = null;

        for (int i = 0; i < ln.points.size(); i++) {
            GEPoint p1 = ln.points.get(i);
            for (int j = 0; j < c.points.size(); j++) {
                GEPoint p2 = c.points.get(j);
                if (p1 == p2) {
                    if (t1 == null)
                        t1 = p1;
                    else
                        t2 = p1;
                }
            }
        }

        if (t1 == null)
            return new GEPoint[0];
        else if (t2 == null) {
            GEPoint[] l = new GEPoint[1];
            l[0] = t1;
            return l;
        } else {
            GEPoint[] l = new GEPoint[2];
            l[0] = t1;
            l[1] = t2;
            return l;
        }
    }

    public static GEPoint commonPoint(GELine line0, GELine line1) {
        for (GEPoint p1 : line0.points) {
            for (GEPoint p2 : line1.points) {
                if (p1 == p2)
                    return p1;
            }
        }
        return null;
    }

    public boolean sameLine(GELine line2) {
        if (line2 == null)
        	return false;
        if (points.size() != line2.points.size())
        	return false;

        return points.containsAll(line2.points);
    }

    public static double[] Intersect(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        double result[] = new double[2];
        if (Math.abs(p1.getx() - p2.getx()) < UtilityMiscellaneous.ZERO) {
            if (Math.abs(p3.getx() - p4.getx()) < UtilityMiscellaneous.ZERO)
                return null;

            double k = (p4.gety() - p3.gety()) / (p4.getx() - p3.getx());
            result[0] = p1.getx();
            result[1] = k * (p1.getx() - p3.getx()) + p3.gety();
            return result;
        }
        if (Math.abs(p3.getx() - p4.getx()) < UtilityMiscellaneous.ZERO) {
            double k0 = (p2.gety() - p1.gety()) / (p2.getx() - p1.getx());
            result[0] = p3.getx();
            result[1] = k0 * (p3.getx() - p1.getx()) + p1.gety();
            return result;
        }
        double k0 = (p2.gety() - p1.gety()) / (p2.getx() - p1.getx());
        double k1 = (p4.gety() - p3.gety()) / (p4.getx() - p3.getx());
        double x = (p3.gety() - p3.gety() + k0 * p1.getx() - k1 * p3.getx()) / (k0 - k1);
        double y = k0 * (x - p1.getx()) + p1.gety();
        result[0] = x;
        result[1] = y;
        return result;
    }

    public static boolean isVerticalSlop(double r) {
        return Math.abs(r) > UtilityMiscellaneous.MAX_SLOPE;
    }

    public static boolean isPerp(GELine line0, GELine line1) {
        if (line0 == null || line1 == null)
            return false;
        double k0 = line0.getSlope();
        double k1 = line1.getSlope();
        if (Math.abs(k0) < UtilityMiscellaneous.ZERO) {
            return Math.abs(k1) > 99;
        }
        if (Math.abs(k1) < UtilityMiscellaneous.ZERO)
            return Math.abs(k0) > 99;
        return Math.abs(k0 * k1 + 1) < UtilityMiscellaneous.ZERO;
    }

    public static double[] Intersect(GELine line0, GELine line1) {
        if (line0 == null || line1 == null)
            return null;

        double result[] = new double[2];
        double k0 = line0.getSlope();
        double k1 = line1.getSlope();

        if (line0.isVertical() || isVerticalSlop(k0)) {
            if (line1.isVertical() || isVerticalSlop(k1))
                return null;
            double k = line1.getSlope();
            GEPoint p0 = line0.points.get(0);
            GEPoint p1 = line1.points.get(0);
            result[0] = p0.getx();
            result[1] = k * (p0.getx() - p1.getx()) + p1.gety();
            return result;
        }


        if (line1.isVertical() || isVerticalSlop(k1)) {
            GEPoint p1 = line0.getFirstPoint();
            GEPoint p = line1.getFirstPoint();
            result[0] = p.getx();
            result[1] = k0 * (p.getx() - p1.getx()) + p1.gety();
            return result;
        }

        GEPoint p0 = line0.getFirstPoint();
        GEPoint p1 = line1.getFirstPoint();
        if (Math.abs(k0 - k1) > UtilityMiscellaneous.ZERO) {
            double x = (p1.gety() - p0.gety() + k0 * p0.getx() - k1 * p1.getx()) / (k0 - k1);
            double y = k0 * (x - p0.getx()) + p0.gety();
            result[0] = x;
            result[1] = y;
        } else {
            double x = (p0.getx() + p1.getx()) / 2;
            double y = (p0.gety() + p1.gety()) / 2;
            result[0] = 999999;
            result[1] = (result[0] - x) * k0 + y;
        }
        return result;
    }

    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (!bVisible) return;

        GEPoint pl[] = this.getMaxMinPoint();
        if (pl != null) {
            String s = pl[0].m_name + " moveto " + pl[1].m_name + " lineto ";
            fp.write(s.getBytes());
            super.saveSuper(fp);
        }

    }

@Override
public Element saveIntoXMLDocument(Element rootElement, String sTypeName) {
	assert(rootElement != null);
	if (rootElement != null) {
		Document doc = rootElement.getOwnerDocument();

		Element elementThis = super.saveIntoXMLDocument(rootElement, "line");

		if (linetype != 0)
			elementThis.setAttribute("line_type", String.valueOf(linetype));
		if (ext_type != 0)
			elementThis.setAttribute("extension_type", String.valueOf(ext_type));
		if (extent != 50)
			elementThis.setAttribute("extent", String.valueOf(extent));

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
		
		if (cons != null && !cons.isEmpty()) {
			Element eCons = doc.createElement("constraints");
    		elementThis.appendChild(eCons);
    		
    		for (Constraint cs : cons) {
    			if (cs != null) {
    				Element child = doc.createElement("constraint");
    				child.setTextContent(String.valueOf(cs.id));
    				eCons.appendChild(child);
    			}
    		}
		}
		return elementThis;
	}
	return null;
}

public boolean setConstraints(Map<Integer, Constraint> mapConstraints) {
	assert(mapConstraints != null);
	boolean bAllConstraintsFoundInMap = true;
	if (mapConstraints != null) {
		for (Integer Index : setConstraintIndices) {
			Constraint cs = mapConstraints.get(Index);
			if (cs == null) {
				bAllConstraintsFoundInMap = false;
				System.err.println("Null constraint added.");
				bIsValidEntity = false;
			}
			cons.add(cs);
		}
	}
	return bAllConstraintsFoundInMap;
}

//public void Save(DataOutputStream out) throws IOException {
//        super.Save(out);
//
//        out.writeInt(linetype);
//        out.writeInt(ext_type);
//        out.writeInt(points.size());
//        for (GEPoint p : points) {
//            out.writeInt(p.m_id);
//        }
//        out.writeInt(cons.size());
//        for (constraint cs : cons) {
//            if (cs != null)
//                out.writeInt(cs.id);
//            else
//                out.writeInt(-1);
//        }
//        out.writeInt(extent);
//    }
//
//    public void Load(DataInputStream in, drawProcess dp) throws IOException {
//        if (CMisc.version_load_now < 0.01) {
//            m_id = in.readInt();
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
//
//            m_name = new String();
//            int size = in.readInt();
//            for (int i = 0; i < size; i++)
//                m_name += in.readChar();
//            linetype = in.readInt();
//
//            size = in.readInt();
//            for (int i = 0; i < size; i++) {
//                int d = in.readInt();
//                this.addApoint(dp.getPointById(d));
//            }
//            size = in.readInt();
//
//            int nc = 0;
//            for (int i = 0; i < size; i++) {
//                int d = in.readInt();
//                constraint c = dp.getConstraintByid(d);
//                if (c == null)
//                    nc++;
//                else
//                    cons.add(c);
//            }
//            size -= nc;
//        } else {
//            super.Load(in, dp);
//            linetype = in.readInt();
//            ext_type = in.readInt();
//            int size = in.readInt();
//            for (int i = 0; i < size; i++) {
//                int d = in.readInt();
//                GEPoint tp = dp.getPointById(d);
//                if (tp == null) {
////                    CMisc.print("can not find point " + d);
//                } else
//                    this.addApoint(tp);
//            }
//            size = in.readInt();
//            for (int i = 0; i < size; i++) {
//                int d = in.readInt();
//                cons.add(dp.getConstraintByid(d));
//            }
//        }
//
//        if (CMisc.version_load_now >= 0.045)
//            extent = in.readInt();
//    }

}

