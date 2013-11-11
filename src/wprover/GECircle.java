package wprover;

import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.eclipse.jdt.annotation.NonNull;
import org.w3c.dom.*;

/**
 * Created by IntelliJ IDEA.
 * User: ${Yezheng}
 * Date: 2004-12-9
 * Time: 12:29:48
 * To change this template use File | Settings | File Templates.
 */
public class GECircle extends GraphicEntity implements Pointed {
    public static int PCircle = 0;
    public static int RCircle = 1;
    public static int SCircle = 2;

    int circle_type = PCircle;

    public GEPoint o = new GEPoint(); // o represents the center of the circle
    public final @NonNull ArrayList<GEPoint> points = new ArrayList<GEPoint>(); // contains points that are on the circumference.
    final @NonNull ArrayList<Constraint> cons = new ArrayList<Constraint>();
    private HashSet<Integer> setConstraintIndices = null;

    public GECircle(DrawPanel dp, final Element thisElement, Map<Integer, GraphicEntity> mapGE) {
    	super(dp, thisElement);
		circle_type = DrawPanelFrame.safeParseInt(thisElement.getAttribute("circle_type"), PCircle);
		int center_index = DrawPanelFrame.safeParseInt(thisElement.getAttribute("center"), 0);
		GraphicEntity ge = mapGE.get(center_index);
		if (ge == null || !(ge instanceof GEPoint))
			bIsValidEntity = false;
		else
			o = (GEPoint)ge;
		
		setConstraintIndices = new HashSet<Integer>();
		
		NodeList elist = thisElement.getChildNodes();		
		for (int i = 0; i < elist.getLength(); ++i) {
			Node nn = elist.item(i);
            if (nn != null) {
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


    
    
    
    
    
    
    
    /**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + circle_type;
		// result = prime * result + ((cons == null) ? 0 : cons.hashCode());
		result = prime * result + ((o == null) ? 0 : o.hashCode());
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		result = prime
				* result
				+ ((setConstraintIndices == null) ? 0 : setConstraintIndices
						.hashCode());
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
		if (!(obj instanceof GECircle))
			return false;
		GECircle other = (GECircle) obj;
		if (circle_type != other.circle_type)
			return false;
		if (cons == null) {
			if (other.cons != null)
				return false;
		} else if (!cons.equals(other.cons))
			return false;
		if (o == null) {
			if (other.o != null)
				return false;
		} else if (!o.equals(other.o))
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

	public final int numPoints() {
        return points.size();
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
    public boolean isCoincidentWith(final GEPoint pp) {
    	return (pp != null && points.contains(pp));
    }
    
    @Override
    public boolean isFullyCoincidentWith(final ArrayList<GraphicEntity> pList) {
    	if (pList != null) {
    		for (GEPoint pp : points) {
    			if (!pList.contains(pp))
    				return false;
    		}
        	return pList.contains(o);
    	}
    	return false;
    }


    public final GEPoint center() {
    	return o;
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

    public final boolean p_on_circle(final GEPoint pp) {
        for (GEPoint p : points) {
            if (p == pp)
                return true;
        }
        return false;
    }

    public void draw(Graphics2D g2, boolean selected) {
        if (!isdraw()) return;
        if (selected) {
            g2.setColor(UtilityMiscellaneous.SelectObjectColor);
            g2.setStroke(UtilityMiscellaneous.SelectObjectStroke);
        } else
            super.prepareToBeDrawnAsUnselected(g2);

        double x1, y1, r;
        x1 = o.x1.value;
        y1 = o.y1.value;
        r = getRadius();
        if (r < UtilityMiscellaneous.MAX_DRAW_LEN)
            g2.drawOval((int) (x1 - r), (int) (y1 - r), 2 * (int) r, 2 * (int) r);
        else {
            if (points.size() < 2) return;
            GEPoint p1, p2;
            p1 = p2 = null;
            double len = 0.00;
            for (int i = 0; i < points.size(); i++) {
                GEPoint tp1 = points.get(i);
                for (int j = 1; j < points.size(); j++) {

                    GEPoint tp2 = points.get(j);
                    if (tp1 == tp2) continue;
                    double tlen = Math.pow(tp1.getx() - tp2.getx(), 2) + Math.pow(tp1.gety() - tp2.gety(), 2);
                    if (tlen > len) {
                        len = tlen;
                        p1 = tp1;
                        p2 = tp2;
                    }
                }
            }
            if (p1 == null || p2 == null) return;
            double dx = p2.getx() - p1.getx();
            double dy = p2.gety() - p1.gety();
            double sl = Math.sqrt(dx * dx + dy * dy);
            x1 = p1.getx() - dx * 2000 / sl;
            y1 = p1.gety() - dy * 2000 / sl;
            double x2 = p1.getx() + dx * 2000 / sl;
            double y2 = p1.gety() + dy * 2000 / sl;
            g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);


        }


    }

//    public void draw(Graphics2D g2) {
//        this.draw(g2, false);
//    }

    public String TypeString() {
        if (m_name == null) return Language.getLs(50, "circle ");
        return Language.getLs(50, "circle ") + m_name;
    }

    public String getDescription() {
        String st =  Language.getLs(50, "circle ");

        if (circle_type == PCircle)
            return st + "(" + o.m_name + "," + o.m_name + getSidePoint().getname() + ")";
        else if (circle_type == SCircle) {
            GEPoint p1, p2, p3;
            p1 = p2 = p3 = null;
            if (points.size() < 3)
            	return st;

            p1 = points.get(0);
            p2 = points.get(1);
            p3 = points.get(2);
            return st + "(" + o.getname() + "," + p1.getname() + p2.getname() + p3.getname() + ")";
        } else if (circle_type == RCircle) {
            Constraint cs = cons.get(0);
            if (cs.GetConstraintType() == Constraint.RCIRCLE) {
                GraphicEntity p1 = (GraphicEntity) cs.getelement(0);
                GraphicEntity p2 = (GraphicEntity) cs.getelement(1);
                return st + "(" + o.m_name + "," + p1.getname() + p2.getname() + ")";
            }
        }
        return TypeString();
    }

    public boolean isLocatedNear(double x, double y) {
        if (!bVisible) return false;
        double ox, oy;
        ox = o.getx();
        oy = o.gety();

        double r = getRadius();
        double len = Math.sqrt(Math.pow(x - ox, 2) + Math.pow(y - oy, 2));
        if (Math.abs(r - len) < UtilityMiscellaneous.PIXEPS)
            return true;
        return false;

    }

    public void setType(int t) {
        circle_type = t;
    }

    public String getAllPointName() {
        String s = new String();
        for (int i = 0; i < points.size(); i++) {
            GEPoint p = points.get(i);
            if (i == 0)
                s += p.m_name;
            else
                s += ", " + p.m_name;
        }
        return s;
    }


    public GEPoint getSidePoint() // point that with least number
    {
        GEPoint pt = null;

        for (int i = 0; i < points.size(); i++) {
            GEPoint p = points.get(i);
            if (p == null)
                continue;
            if (pt == null)
                pt = p;
            else if (pt.x1.xindex > p.x1.xindex)
                pt = p;

        }
        return pt;
    }

    public double getCenterOX() {
        return o.getx();
    }

    public double getCenterOY() {
        return o.gety();
    }

    public double getRadius() {
        if (circle_type == RCircle) {
            //constraint cs = null;
            for (int i = 0; i < cons.size(); i++) {
                Constraint c = cons.get(i);
                if (c.GetConstraintType() == Constraint.RCIRCLE) {
                    GEPoint p1 = (GEPoint) c.getelement(0);
                    GEPoint p2 = (GEPoint) c.getelement(1);
                    return Math.sqrt(Math.pow(p1.getx() - p2.getx(), 2) + Math.pow(p1.gety() - p2.gety(), 2));
                }
            }
            return -1;
        } else {
            GEPoint p = getSidePoint();
            return Math.sqrt(Math.pow(p.getx() - o.getx(), 2) + Math.pow(p.gety() - o.gety(), 2));
        }
    }

    public GEPoint[] getRadiusPoint() {
        GEPoint[] pl = new GEPoint[2];
        if (circle_type == GECircle.RCircle) {
            for (int i = 0; i < cons.size(); i++) {
                Constraint cs = cons.get(i);
                if (cs.GetConstraintType() == Constraint.RCIRCLE) {
                    pl[0] = (GEPoint) cs.getelement(0);
                    pl[1] = (GEPoint) cs.getelement(1);
                    return pl;
                }
            }

        } else if (circle_type == GECircle.PCircle | circle_type == GECircle.SCircle) {
            pl[0] = o;
            pl[1] = this.getSidePoint();
        }
        return pl;

    }

    public GECircle() {
        super(GraphicEntity.CIRCLE);
    }

    public GECircle(GEPoint O, GEPoint A, GEPoint B, GEPoint C) {
        super(GraphicEntity.CIRCLE);
        circle_type = SCircle;
        this.o = O;
        points.add(A);
        points.add(B);
        points.add(C);
    }

    public GECircle(GEPoint O, GEPoint A, GEPoint B) {
        super(GraphicEntity.CIRCLE);
        this.o = O;
        points.add(A);
        points.add(B);
    }

    public GECircle(GEPoint O, GEPoint A) {
        super(GraphicEntity.CIRCLE);
        this.o = O;
        points.add(A);
    }

    public GECircle(int t, GEPoint O) {
        super(GraphicEntity.CIRCLE);
        this.o = O;
        this.circle_type = t;
    }

    public void addConstraint(Constraint cs) {
        cons.add(cs);
    }

    public boolean hasPoint(GEPoint p) {
        for (GEPoint pt : points) {
            if (p.hasSameCoordinatesAs(pt))
            	return true;
        }
        return false;
    }

    public GEPoint getPointOtherThan(GEPoint t) {
        GEPoint p = null;
        for (GEPoint pt : points) {
            if (pt != t && (p == null || p.x1.xindex > pt.x1.xindex))
                p = pt;
        }
        return p;
    }
    
    public void add(GEPoint p) {
        if (!points.contains(p))
            points.add(p);
    }

    public void pointStickToCircle(GEPoint p) {
        double x = p.getx();
        double y = p.gety();

        double xo = o.x1.value;
        double yo = o.y1.value;
        double R = getRadius();
        double R1 = Math.sqrt((xo - x) * (xo - x) + (yo - y) * (yo - y));

        double y1 = yo + (y - yo) * R / R1;
        double x1 = xo + (x - xo) * R / R1;
        p.setXY(x1, y1);

    }

    public boolean on_circle(double x, double y) {
        return this.isLocatedNear(x, y);
    }

    public boolean nearcircle(double x, double y, double eps) {
        double ox, oy;
        ox = o.getx();
        oy = o.gety();

        double r = getRadius();
        double len = Math.sqrt(Math.pow(x - ox, 2) + Math.pow(y - oy, 2));
        if (Math.abs(r - len) < eps)
            return true;
        return false;
    }

    public void SmartPonc(GEPoint p) {
        //    CPoint pt = (CPoint) points.get(0);
        double ox, oy, x, y;
        ox = o.getx();
        oy = o.gety();
        x = p.getx();
        y = p.gety();

        double r = this.getRadius();//Math.sqrt(Math.pow(pt.getx() - ox, 2) + Math.pow(pt.gety() - oy, 2));
        double len = Math.sqrt(Math.pow(p.getx() - ox, 2) + Math.pow(p.gety() - oy, 2));


        if (Math.abs(x - o.getx()) < 0.001) {
            if (y > oy)
                p.setXY(ox, oy + r);
            else
                p.setXY(ox, oy - r);
        } else {
            double k = r / len;
            p.setXY(ox + k * (x - ox), oy + k * (y - oy));
        }
    }

    public static HashSet<GEPoint> CommonPoints(GECircle c1, GECircle c2) {
    	HashSet<GEPoint> vlist = new HashSet<GEPoint>();
        for (GEPoint p : c1.points) {
            if (c2.points.contains(p)) {
                vlist.add(p);
            }
        }
        return vlist;
    }

    public boolean Tangent(Object obj) {
        if (obj instanceof GELine) {
            return true;
        } else if (obj instanceof GECircle) {
            GECircle c2 = (GECircle) obj;
            GEPoint p1 = this.getSidePoint();
            GEPoint p2 = c2.getSidePoint();
            double r1 = Math.sqrt(Math.pow(this.o.getx() - p1.getx(), 2) + Math.pow(this.o.gety() - p1.gety(), 2));
            double r2 = Math.sqrt(Math.pow(c2.o.getx() - p2.getx(), 2) + Math.pow(c2.o.gety() - p2.gety(), 2));
            double d = Math.sqrt(Math.pow(this.o.getx() - c2.o.getx(), 2) + Math.pow(this.o.gety() - c2.o.gety(), 2));
            if (Math.abs(r1 + r2 - d) < UtilityMiscellaneous.PIXEPS)
                return true;
            else
                return false;
        } else
            return false;
    }

    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (!bVisible) return;

        float r = (float) (((int) (getRadius() * 100)) / 100.0);
        String s = "newpath " + o.m_name + " " +
                new Float(r).toString() + " circle";
        fp.write(s.getBytes());
        this.saveSuper(fp);
    }

@Override
public Element saveIntoXMLDocument(Element rootElement, String sTypeName) {
	assert(rootElement != null);
	if (rootElement != null) {
		Document doc = rootElement.getOwnerDocument();

		Element elementThis = super.saveIntoXMLDocument(rootElement, "circle");
		
		elementThis.setAttribute("center", String.valueOf(o.m_id));
		elementThis.setAttribute("circle_type", String.valueOf(circle_type));

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

public boolean hasCenter(GEPoint centerpoint) {
	return o.equals(centerpoint);
}

//@Override
//public void Save(DataOutputStream out) throws IOException {
//        super.Save(out);
//
//        out.writeInt(circle_type);
//        out.writeInt(o.m_id);
//        out.writeInt(points.size());
//        for (int i = 0; i < points.size(); i++) {
//            GEPoint p = points.get(i);
//            out.writeInt(p.m_id);
//        }
//        out.writeInt(cons.size());
//        for (int i = 0; i < cons.size(); i++) {
//            constraint cs = cons.get(i);
//            out.writeInt(cs.id);
//        }
//    }

//    public void Load(DataInputStream in, drawProcess dp) throws IOException {
//        if (CMisc.version_load_now < 0.010) {
//            m_id = in.readInt();
//
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
//
//            circle_type = in.readInt();
//            int size = in.readInt();
//            m_name = new String();
//            for (int i = 0; i < size; i++)
//                m_name += in.readChar();
//            int d = in.readInt();
//            o = dp.getPointById(d);
//
//            size = in.readInt();
//            for (int i = 0; i < size; i++) {
//                int dx = in.readInt();
//                points.add(dp.getPointById(dx));
//            }
//            size = in.readInt();
//            for (int i = 0; i < size; i++) {
//                int dx = in.readInt();
//                cons.add(dp.getConstraintByid(dx));
//            }
//        } else {
//            super.Load(in, dp);
//
//            circle_type = in.readInt();
//
//            int d = in.readInt();
//            o = dp.getPointById(d);
//
//            int size = in.readInt();
//            for (int i = 0; i < size; i++) {
//                int dx = in.readInt();
//                points.add(dp.getPointById(dx));
//            }
//            size = in.readInt();
//            for (int i = 0; i < size; i++) {
//                int dx = in.readInt();
//                cons.add(dp.getConstraintByid(dx));
//            }
//
//        }
//		}


}
