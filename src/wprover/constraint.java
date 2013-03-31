package wprover;

import gprover.cons;
import gprover.gib;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

import maths.*;

import org.w3c.dom.*;


public class constraint {
    final public static int NULLTYPE = 0;
    final public static int PONLINE = 11;  // colinear
    final public static int PONCIRCLE = 12; // circle
    final public static int PARALLEL = 2;            // para
    final public static int PERPENDICULAR = 3;              // perp
    final public static int PFOOT = 31;                            // foot
    final public static int EQDISTANCE = 4;                    // eqdistance PC

    final public static int COLLINEAR = 1;
    final public static int PERPBISECT = 5;

    final public static int MIDPOINT = 6;      // midpoint

    final public static int EQANGLE = 7;
    final public static int LCTANGENT = 9;
    final public static int CCTANGENT = 10;

    final public static int LRATIO = 19;
    final public static int RCIRCLE = 20;
    final public static int CIRCUMCENTER = 21;
    final public static int BARYCENTER = 22;
    final public static int ORTHOCENTER = 37;
    final public static int INCENTER = 44;
    final public static int BISECT = 13;
    final public static int CCLine = 14;
    final public static int TRATIO = 15;

    final public static int PRATIO = 17;
    final public static int MIRROR = 18;
    final public static int HORIZONAL = 23;
    final public static int VERTICAL = 24;
    final public static int VISIBLE = 25;
    final public static int INVISIBLE = 26;
    final public static int NRATIO = 27;
    final public static int LC_MEET = 28;
    final public static int EQANGLE3P = 29;
    final public static int SPECIFIC_ANGLE = 30;
    final public static int SYMPOINT = 36;
    final public static int P_O_A = 38;
    final public static int INTER_CC1 = 32;
    final public static int PSQUARE = 33;
    final public static int NSQUARE = 34;
    final public static int NTANGLE = 35;
    final public static int PETRIANGLE = 39;
    final public static int NETRIANGLE = 40;
    final public static int CONSTANT = 43;
    final public static int SQUARE = 41;
    final public static int ALINE = 42;
    final public static int PSYM = 45;
    final public static int INTER_LL = 46;
    final public static int INTER_LC = 47;
    final public static int INTER_CC = 48;

    final public static int TRIANGLE = 59;
    final public static int ISO_TRIANGLE = 50;
    final public static int EQ_TRIANLE = 51;
    final public static int RIGHT_ANGLED_TRIANGLE = 52;
    final public static int ISO_RIGHT_ANGLED_TRIANGLE = 53;
    final public static int QUADRANGLE = 54;
    final public static int PARALLELOGRAM = 55;
    final public static int TRAPEZOID = 56;
    final public static int RIGHT_ANGLE_TRAPEZOID = 57;
    final public static int RECTANGLE = 58;
    final public static int PENTAGON = 60;
    final public static int POLYGON = 64;
    final public static int SANGLE = 65;
    final public static int ANGLE_BISECTOR = 66;
    final public static int BLINE = 67;
    final public static int TCLINE = 68;
    final public static int RATIO = 69;
    final public static int TRANSFORM = 70;
    final public static int EQUIVALENCE1 = 71;
    final public static int EQUIVALENCE2 = 72;
    final public static int TRANSFORM1 = 73;


    final public static int LINE = 61;
    final public static int CIRCLE = 62;
    final public static int CIRCLE3P = 63;
    final public static int CCTANGENT_LINE = 74;
    final public static int ONSCIRCLE = 75;
    final public static int ON_ABLINE = 76;
    final public static int ONDCIRCLE = 77;
    final public static int ONRCIRCLE = 78;
    final public static int ONALINE = 79;

    private static TPoly polylist = null;

    int id = CMisc.id_count++;
    private int ConstraintType = 0;
    private ArrayList<Object> elementlist = new ArrayList<Object>();
    int proportion = 1;
    private boolean bIsValidEntity = true;
    boolean bPolyGenerate = true;

    cons csd = null;
    cons csd1 = null;

    public constraint(int type, Object obj1, Object obj2, boolean gpoly) {
    	bIsValidEntity = true;
    	ConstraintType = type;
        elementlist.add(obj1);
        elementlist.add(obj2);
        bPolyGenerate = gpoly;
        if (bPolyGenerate) {
            PolyGenerate();
        }
    }

	public constraint(drawProcess dp, Element thisElement, Map<Integer, GraphicEntity> mapGE) {
		assert(thisElement != null);
		bIsValidEntity = false;
		if (thisElement != null) {
			bIsValidEntity = true;
			id = GExpert.safeParseInt(thisElement.getAttribute("id"), 0);
			bIsValidEntity &= (id > 0);
			ConstraintType = GExpert.safeParseInt(thisElement.getAttribute("type"), 0);
			proportion = GExpert.safeParseInt(thisElement.getAttribute("proportion"), 1);
			bPolyGenerate = GExpert.safeParseBoolean(thisElement.getAttribute("poly_generate"), false);
			
			NodeList elist = thisElement.getChildNodes();
			for (int i = 0; i < elist.getLength() && bIsValidEntity; ++i) {
				Node nn = elist.item(i);
	            if (nn != null && nn instanceof Element) {
	            	String s = nn.getNodeName();
	            	if (s.equalsIgnoreCase("nullparameter")) {
	            		elementlist.add(null);
	            		continue;
	            	}
            		int ii = GExpert.safeParseInt(((Element)nn).getTextContent(), 0); // Should this default value be zero?
	            	if (s.equalsIgnoreCase("parameter")) {
	            		param pp = dp.getParameterByindex(ii);
	            		if (pp == null)
	            			bIsValidEntity = false;
	            		else
	            			elementlist.add(pp);
	            		continue;
	            	}
	            	if (s.equalsIgnoreCase("integer")) {
	            		elementlist.add(ii);
	            		continue;
	            	}
	            	
	            	// Seek the pointer in the provided map
	            	if (s.equalsIgnoreCase("graphic_entity")) {
	            		GraphicEntity ge = mapGE.get(ii);
	            		if (ge == null)
	            			bIsValidEntity = false; // This chooses to mark the whole file as invalid if the constraint references some entity that isn't identified in the xml document.
	            		else
	            			elementlist.add(ge);
	            	}
	            }
	        }
//	        if (bPolyGenerate) {
//	            PolyGenerate();
//	        }
	    }
	}
	
	
	
//	public constraint(Element thisElement, Map<Integer, GraphicEntity> mapGE) {
//    	bIsValidEntity = true; // check for whether the tagname of thisElement is "constraint"
//    	ConstraintType = GExpert.safeParseInt(thisElement.getAttribute("type"), 0);
//    	id = GExpert.safeParseInt(thisElement.getAttribute("type"), 0);
//    	is_poly_genereate = GExpert.safeParseBoolean(thisElement.getAttribute("is_poly_genereate"), true);
//
//    	NodeList elist = thisElement.getChildNodes();
//		for (int i = 0; i < elist.getLength(); ++i) {
//			Node nn = elist.item(i);
//            if (nn != null && nn instanceof Element) {
//            	String s = nn.getNodeName();
//            	if (s.equalsIgnoreCase("object")) {
//            		int ii = GExpert.safeParseInt(((Element)nn).getTextContent(), 0);
//            		GraphicEntity ge = mapGE.get(ii);
//            		if (ge == null)
//            			bIsValidEntity = false;
//            		else {
//            			elementlist.add(ge);
//            		}
//            	}
//            }
//		}
//        if (is_poly_genereate) {
//            PolyGenerate();
//        }
//    }
    
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ConstraintType;
		result = prime * result + (bIsValidEntity ? 1231 : 1237);
		result = prime * result + ((csd == null) ? 0 : csd.hashCode());
		result = prime * result + ((csd1 == null) ? 0 : csd1.hashCode());
		result = prime * result + ((elementlist == null) ? 0 : elementlist.hashCode());
		result = prime * result + id;
		result = prime * result + (bPolyGenerate ? 1231 : 1237);
		result = prime * result + proportion;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof constraint))
			return false;
		constraint other = (constraint) obj;
		if (ConstraintType != other.ConstraintType)
			return false;
		if (bIsValidEntity != other.bIsValidEntity)
			return false;
		if (csd == null) {
			if (other.csd != null)
				return false;
		} else if (!csd.equals(other.csd))
			return false;
		if (csd1 == null) {
			if (other.csd1 != null)
				return false;
		} else if (!csd1.equals(other.csd1))
			return false;
		if (elementlist == null) {
			if (other.elementlist != null)
				return false;
		} else if (!elementlist.equals(other.elementlist))
			return false;
		if (id != other.id)
			return false;
		if (bPolyGenerate != other.bPolyGenerate)
			return false;
		if (proportion != other.proportion)
			return false;
		return true;
	}

	public final boolean isValid() {
        return bIsValidEntity;
    }

    public constraint(int type, Object...objlist) {
    	bIsValidEntity = true;
        ConstraintType = type;
        for (Object o : objlist) {
        	if (o != null) {
        		elementlist.add(o);
        	}
        }
        if (objlist.length <= 1)
        	return;
        if (bPolyGenerate || type == VERTICAL || type == HORIZONAL || type == COLLINEAR || type == PONCIRCLE
                || type == PONLINE || type == EQANGLE || type == CCTANGENT
                || type == PERPBISECT || type == LINE || type == CIRCLE)
            PolyGenerate();
        else if (type == PERPENDICULAR && objlist.length >=2) {
            GELine line1 = (GELine) (objlist[0]);
            GELine line2 = (GELine) (objlist[1]);
            if (line1.points.size() >= 2 && line2.points.size() >= 2)
                PolyGenerate();
        }
    }
    
    public constraint(int type, int prop, Object...objlist) {
    	bIsValidEntity = true;
        ConstraintType = type;
        proportion = prop;
        for (Object o : objlist) {
        	if (o != null) {
        		elementlist.add(o);
        	}
        }
        if (bPolyGenerate)
            PolyGenerate();
    }
    public constraint(int type, ArrayList<Object> olist) {
    	bIsValidEntity = true;
        ConstraintType = type;
        elementlist.addAll(olist);
        bPolyGenerate = true;
        PolyGenerate();
    }
    
    public void addelement(Object obj) {
        if (obj != null)
            elementlist.add(obj);
    }

    public static TPoly getPolyListAndSetNull() {
        TPoly pl = polylist;
        polylist = null;
        TPoly p2 = pl;
        while (p2 != null) {
            PolyBasic.coefgcd(p2.poly);
            p2 = p2.next;
        }
        return pl;
    }

    public int GetConstraintType() {
        return ConstraintType;
    }

    public Object getelement(int i) {
        if (i >= 0 && i < elementlist.size())
            return elementlist.get(i);
        else
            return null;
    }

    public GEPoint getLPoints2(GEPoint p1, GEPoint p2) {
        for (int i = 0; i < elementlist.size(); i++) {
            GEPoint p = (GEPoint) elementlist.get(i); // XXX Are all members of elementlist points. Not so according to the loading functions. If so, why not make them GEPoints instead of Objects?
            if (p != p1 && p != p2)
                return p;
        }
        return null;
    }

    public boolean cotainPoints(GEPoint p1, GEPoint p2) {
        return elementlist.contains(p1) && elementlist.contains(p2);
    }

    public void getAllElements(Collection<Object> v) {
        if (v != null)
        	v.addAll(elementlist);
    }


    public void calculate(param[] para) {
        if (polylist == null) return;
        PolyBasic.calculv(polylist.getPoly(), para);
        bPolyGenerate = false;
    }

    public void setPolyGenerate(boolean r) {
        bPolyGenerate = r;
    }

    public void addElement(Object obj) {
        elementlist.add(obj);
    }

    String getMessage() {
        return toString();
    }

    public String toString() {
        if (csd != null)
            return csd.toDString();

        if (ConstraintType == constraint.SPECIFIC_ANGLE) {
            param p = (param) elementlist.get(0);
            return "x" + p.xindex + "| angle = " + p.type;
        }

        int num = elementlist.size();
        GraphicEntity e1, e2, e3, e4;
        e1 = e2 = e3 = e4 = null;

        for (int i = 0; i < num; i++) {
            if (i == 0)
                e1 = (GraphicEntity) elementlist.get(0);
            else if (i == 1)
                e2 = (GraphicEntity) elementlist.get(1);
            else if (i == 2)
                e3 = (GraphicEntity) elementlist.get(2);
            else if (i == 3)
                e4 = (GraphicEntity) elementlist.get(3);
        }

        switch (ConstraintType) {
            case PONLINE: {
                return e1.TypeString() + " on " + e2.getDescription();
            }
            case PONCIRCLE: {
                return e1.TypeString() + " on " + e2.getDescription();
            }
            case PARALLEL: {
                return "parallel " + e1.getDescription() + " " + e2.getDescription();
            }
            case PERPENDICULAR:
                return e1.TypeString() + " perpendicular to " + e2.TypeString();
            case PFOOT:
                return "Foot " + e1.TypeString() + " " + e2.TypeString() + " " + e3.TypeString() + " " + e4.TypeString();
            case MIDPOINT:
                return e1.TypeString() + "is the middle point of " + e2.TypeString() + " and " + e3.TypeString();
            case EQDISTANCE:
                return e1.TypeString() + " equal to " + e2.TypeString();
            case EQANGLE:
                return e1.TypeString() + " equal to " + e2.TypeString();
            case COLLINEAR:
                return e1.TypeString() + " " + e2.TypeString() + " are collinear";
            case CCTANGENT:
                return e1.TypeString() + " is tangent to " + e2.TypeString();
            case CCLine:
                return e1.TypeString() + " is the axes of " + e2.TypeString() + " and " + e3.TypeString();
            case TRATIO:
                return "T(" + e4.m_name + e3.m_name + ") / " + "T(" + e1.m_name + e2.m_name + ") = 1 : " + new Integer(proportion).toString();
            case PERPBISECT:
                return e1.TypeString() + " is on the perpendicular bisector of " + e2.m_name + " " + e3.TypeString();
            case MIRROR:

            case PRATIO:
                return "o(" + e1.m_name + e2.m_name + ") / " + "o(" + e3.m_name + e4.m_name + ") = 1 : " + new Integer(proportion).toString();
            case LRATIO:
                return "";
            case CIRCUMCENTER:
                return e1.TypeString() + "is the circumcenter of " + e2.getname() + e3.getname() + e4.getname();
            case BARYCENTER:
                return e1.TypeString() + " is the barycenter" + e2.getname() + e3.getname() + e4.getname();
            case LCTANGENT:
                return e1.TypeString() + " tangent to " + e2.TypeString();
            case HORIZONAL:
                return "set " + e1.TypeString() + e2.TypeString() + " a horizonal line";
            case VERTICAL:
                return "set " + e1.TypeString() + e2.TypeString() + " a vertical line";

        }

        return new String();
    }

    public void clear_all_cons() {
        csd = null;
        csd1 = null;
    }

    TPoly PolyGenerate() {
        TMono tm = null;
        TPoly tp = null;

        switch (ConstraintType) {
            case LINE:
//                add_des(gib.C_LINE, elementlist);
                break;
            case CIRCLE:
                add_des(gib.C_CIRCLE, elementlist);
                break;
            case TRIANGLE:
                add_des(gib.C_TRIANGLE, elementlist);
                break;
            case QUADRANGLE:
                add_des(gib.C_QUADRANGLE, elementlist);
                break;
            case PENTAGON:
                add_des(gib.C_PENTAGON, elementlist);
                break;
            case POLYGON:
                add_des(gib.C_POLYGON, elementlist);
                break;
            case RIGHT_ANGLED_TRIANGLE:
                tm = PolyRightTriangle();
                break;
            case RIGHT_ANGLE_TRAPEZOID:
                tp = PolyRTrapezoid();
                break;
            case PARALLELOGRAM:
                tp = PolyParallelogram();
                break;
            case TRAPEZOID:
                tm = PolyTrapezoid();
                break;
            case RECTANGLE:
                tp = PolyRectangle();
                break;
            case SQUARE:
                add_des(gib.C_SQUARE, elementlist);
                break;
            case PONLINE:  // p_o_L
                tm = PolyOnLine();
                break;
            case PONCIRCLE:        // p_o_C
                tm = PolyOnCircle();
                break;
            case INTER_LL:
                tp = PolyIntersection_ll();
                break;
            case INTER_LC:
                tp = PolyIntersection_lc();
                break;
            case INTER_CC:
                tp = PolyIntersection_CC();
                break;
            case PARALLEL: // P_O_P
                tm = PolyParel();
                break;
            case PERPENDICULAR:    // p_o_T
                tm = PolyPerp();
                break;
            case PFOOT:   // LT
                tp = PolyPfoot();
                break;
            case MIDPOINT:     // MID
                tp = PolyMidPoint();
                break;
            case EQDISTANCE:         //
                tm = PolyEqidstance();
                break;
            case EQANGLE:              // P_O_A
                tm = PolyEqAngle();
                break;
            case COLLINEAR:         // P_O_L
                tm = collinear();
                break;
            case CCTANGENT:
                tm = PolyCCTangent();
                break;
            case CCLine: {
            }
            break;
            case TRATIO:
                tp = PolyTRatio();
                break;
            case PERPBISECT: // p_O_B
                tm = PolyPerpBisect();
                break;
            case ISO_TRIANGLE:
                tm = PolyISOTriangle();
                break;
            case MIRROR:      // P_REF
                tp = PolyMirror();
                break;
            case PRATIO:
                tp = PolyPratio();
                break;
            case LRATIO:
                tp = PolyPropPoint();
                break;
            case CIRCUMCENTER:        // P_CIR
                tp = PolyCircumCenter();
                break;
            case BARYCENTER:
                tp = PolyBaryCenter();
                break;
            case LCTANGENT:
                tp = PolyLCTangent();
                break;
            case HORIZONAL:
                tm = PolyHorizonal();
                break;
            case VERTICAL:
                tm = PolyVertical();
                break;
            case NRATIO:
                tm = polyMulside();
                break;
            case LC_MEET:
                tp = PolyLCMeet();
                break;
            case EQANGLE3P:
                tm = PolyEqAngle3P();
                break;
            case SPECIFIC_ANGLE:
                tm = PolySpecifiAngle();
                break;
            case INTER_CC1:
                tp = PolyInterCC();
                break;
            case PSQUARE:
                tp = PolyPsquare();
                break;
            case NSQUARE:
                tp = PolyQsquare();
                break;
            case NTANGLE:
                tm = PolyNTAngle();
                break;
            case SYMPOINT:
                tp = PolySymPoint();
                break;
            case ORTHOCENTER:
                tp = PolyOrthoCenter();
                break;
            case INCENTER:
                tp = PolyInCenter();
                break;
            case P_O_A:
                tm = polyPonALine();
                break;
            case PETRIANGLE:
                tp = PolyPeTriangle();
                break;
            case NETRIANGLE:
                break;
            case ALINE:
                tm = PolyALine();
                break;
            case SANGLE:
                tm = PolySAngle();
                break;
            case PSYM:
                tp = PolyPsym();
                break;
            case ANGLE_BISECTOR:
                tm = PolyAngleBisector();
                break;
            case CIRCLE3P:
                tp = PolyCircle3P();
                break;
            case BLINE:
                tm = PolyBLine();
                break;
            case TCLINE:
                tm = PolyTCLine();
                break;
            case RATIO:
                tm = PolyRatio();
                break;
            case CCTANGENT_LINE:
                tp = PolyCCTangentLine();
                break;
            case ONSCIRCLE:
                tm = PolyONSCircle();
                break;
            case ON_ABLINE:
                tm = PolyONABLine();
                break;
            case ONDCIRCLE:
                tm = PolyONDCircle();
                break;
            case ONRCIRCLE:
                tm = PolyONRCircle();
                break;
            case ONALINE:
                tm = PolyONALine();
                break;
            case CONSTANT:
                tm = PolyConstant();
                break;
        }

        if (tm != null) {
            TPoly t = new TPoly();
            t.setPoly(tm);
            addPoly(t);
        }
        
        if (tp != null)
            addPoly(tp);
        
        return polylist;
    }

    /**
     * Inserts the <code>TPoly</code> tp at the front of <code>polylist</code>.
     * @param tp
     */
    public static void addPoly(TPoly tp) {
        TPoly t = tp;
        while (t.getNext() != null)
            t = t.getNext();
        t.setNext(polylist);
        polylist = tp;
    }

    public static boolean optimizePolygon(TPoly p) {
        boolean a = false;
        while (p != null) {
            TMono m = p.getPoly();
            if (m != null && PolyBasic.plength(m) == 1) {
                boolean d = GeoPoly.addZeroN(m.x);
                if (d)
                    a = true;
            }
            p = p.getNext();
        }
        return a;
    }

    TMono PolyConstant() {
        String t1 = elementlist.get(0).toString();
        String t2 = elementlist.get(1).toString();
        param p1 = (param) elementlist.get(2);

        return parseTMonoString(t1, t2, p1.xindex);
    }

    TMono PolyONALine() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        GEPoint p5 = (GEPoint) elementlist.get(4);
        GEPoint p6 = (GEPoint) elementlist.get(5);
        if (elementlist.size() > 6) {
            GEPoint p7 = (GEPoint) elementlist.get(6);
            GEPoint p8 = (GEPoint) elementlist.get(7);
            add_des(gib.C_O_A, p1, p2, p3, p4, p5, p6, p7, p8);
            return GeoPoly.eqangle(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                    p4.x1.xindex, p4.y1.xindex, p5.x1.xindex, p5.y1.xindex, p6.x1.xindex, p6.y1.xindex,
                    p7.x1.xindex, p7.y1.xindex, p8.x1.xindex, p8.y1.xindex);

        } else {
            add_des(gib.C_O_A, p1, p2, p3, p4, p5, p6);
            return GeoPoly.eqangle(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                    p4.x1.xindex, p4.y1.xindex, p5.x1.xindex, p5.y1.xindex, p6.x1.xindex, p6.y1.xindex);
        }
    }

    TMono PolyONRCircle() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        add_des(gib.C_O_R, p1, p2, p3, p4);
        return GeoPoly.eqdistance(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p4.x1.xindex, p4.y1.xindex, p3.x1.xindex, p3.y1.xindex);
    }

    TMono PolyONDCircle() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        add_des(gib.C_O_D, p1, p2, p3);
        return GeoPoly.perpendicular(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p1.x1.xindex, p1.y1.xindex, p3.x1.xindex, p3.y1.xindex);
    }

    TMono PolyONABLine() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        add_des(gib.C_O_AB, p1, p2, p3, p4);
        return GeoPoly.eqangle(p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                p1.x1.xindex, p1.y1.xindex, p1.x1.xindex, p1.y1.xindex,
                p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
    }

    TMono PolyONSCircle() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        add_des(gib.C_O_S, p1, p2, p3, p4);
        return GeoPoly.cyclic(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
    }

    TPoly PolyCCTangentLine() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GECircle c1 = (GECircle) elementlist.get(2);
        GECircle c2 = (GECircle) elementlist.get(3);
        GEPoint t1 = c1.getSidePoint();
        GEPoint t2 = c2.getSidePoint();
        GEPoint o1 = c1.o;
        GEPoint o2 = c2.o;

        TMono m1 = GeoPoly.perpendicular(o1.x1.xindex, o1.y1.xindex, p1.x1.xindex, p1.y1.xindex, p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex);
        TMono m2 = GeoPoly.perpendicular(o2.x1.xindex, o2.y1.xindex, p2.x1.xindex, p2.y1.xindex, p2.x1.xindex, p2.y1.xindex, p1.x1.xindex, p1.y1.xindex);
        TMono m3 = eqdistance(o1, p1, o1, t1);
        TMono m4 = eqdistance(o2, p2, o2, t2);
        TPoly poly1 = NewTPoly(m1, m2);
        TPoly poly2 = NewTPoly(m3, m4);
        poly1.getNext().setNext(poly2);
        return poly1;

    }

    TMono PolyRatio() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        GEPoint p5 = (GEPoint) elementlist.get(4);
        GEPoint p6 = (GEPoint) elementlist.get(5);
        GEPoint p7 = (GEPoint) elementlist.get(6);
        GEPoint p8 = (GEPoint) elementlist.get(7);
        add_des(gib.C_RATIO, p1, p2, p3, p4, p5, p6, p7, p8);
        return GeoPoly.ratio(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex,
                p5.x1.xindex, p5.y1.xindex, p6.x1.xindex, p6.y1.xindex,
                p7.x1.xindex, p7.y1.xindex, p8.x1.xindex, p8.y1.xindex);
    }

    TMono PolyBLine() {
        GEPoint p1 = (GEPoint) elementlist.get(1);
        GEPoint p2 = (GEPoint) elementlist.get(2);
        GELine ln = (GELine) elementlist.get(0);
        int n = ln.getPtsSize();
        if (n == 0 || n > 2) return null;
        {
            GEPoint p = ln.getPoint(n - 1);
            add_des(gib.C_O_B, p, p1, p2);
            return GeoPoly.bisect1(p.x1.xindex, p.y1.xindex, p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex);
        }

    }

    TMono PolyTCLine() {
        GECircle c = (GECircle) elementlist.get(0);
        GELine ln = (GELine) elementlist.get(1);

        if (ln.getPtsSize() < 2) return null;
        
        GEPoint p1 = ln.getCommonPoints(c, null);
    	GEPoint p = ln.get_Lpt1(p1);
        GEPoint p2 = c.o;
        add_des(gib.C_LC_TANGENT, p, p1, p2);

        return GeoPoly.perpendicular(p.x1.xindex, p.y1.xindex, p1.x1.xindex, p1.y1.xindex,
                p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex);
    }

    TPoly PolyCircle3P() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        add_des(gib.C_CIRCUM, p1, p2, p3, p4);

        return GeoPoly.circumcenter(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                p4.x1.xindex, p4.y1.xindex);
    }

    TMono PolyAngleBisector() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GELine ln = (GELine) elementlist.get(3);
        GEPoint p = ln.getSecondPoint(p2);
        if (p != null) {
            add_des(gib.C_ANGLE_BISECTOR, p, p1, p2, p3);

            return GeoPoly.eqangle(p2.x1.xindex, p2.y1.xindex, p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p.x1.xindex, p.y1.xindex,
                    p2.x1.xindex, p2.y1.xindex, p.x1.xindex, p.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex);
        }
        return null;
    }

    TPoly PolyPsym()  // p1 is the mirror of p2 through p3
    {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        add_des(gib.C_REF, p1, p2, p3);
        TMono m1 = GeoPoly.midpoint(p2.x1.xindex, p3.x1.xindex, p1.x1.xindex); // p3 in mid of p1, p2
        TMono m2 = GeoPoly.midpoint(p2.y1.xindex, p3.y1.xindex, p1.y1.xindex);
        return NewTPoly(m1, m2);
    }

    TPoly PolySquare() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        add_des(gib.C_SQUARE, p1, p2, p3, p4);
        TPoly t1 = GeoPoly.squarept1(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, proportion);
        TPoly t2 = GeoPoly.squarept2(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p1.x1.xindex, p1.y1.xindex, p4.x1.xindex, p4.y1.xindex, proportion);
        //TPoly t = t2;
        while (t2.getNext() != null)
            t2 = t2.getNext();
        t2.setNext(t1);
        return t1;
    }

    TPoly PolyPeTriangle() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        add_des(gib.C_EQ_TRI, p1, p2, p3);
        return GeoPoly.pn_eq_triangle(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, true);
    }

    TMono polyPonALine() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        GEPoint p5 = (GEPoint) elementlist.get(4);
        GEPoint p6 = (GEPoint) elementlist.get(5);
        add_des(gib.C_O_A, p1, p2, p3, p4, p5);
        return GeoPoly.eqangle(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                p4.x1.xindex, p4.y1.xindex, p5.x1.xindex, p5.y1.xindex, p6.x1.xindex, p6.y1.xindex);
    }

    TPoly PolyInCenter() {
        GEPoint p = (GEPoint) elementlist.get(0);
        GEPoint p1 = (GEPoint) elementlist.get(1);
        GEPoint p2 = (GEPoint) elementlist.get(2);
        GEPoint p3 = (GEPoint) elementlist.get(3);
        add_des(gib.C_ICENT, p, p1, p2, p3);
        TMono m1 = GeoPoly.eqangle(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p.x1.xindex, p.y1.xindex, p.x1.xindex, p.y1.xindex,
                p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex);

        TMono m2 = GeoPoly.eqangle(p1.x1.xindex, p1.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                p.x1.xindex, p.y1.xindex, p.x1.xindex, p.y1.xindex,
                p3.x1.xindex, p3.y1.xindex, p2.x1.xindex, p2.y1.xindex);

        return NewTPoly(m1, m2);
    }

    TPoly PolyOrthoCenter() {
        GEPoint p = (GEPoint) elementlist.get(0);
        GEPoint p1 = (GEPoint) elementlist.get(1);
        GEPoint p2 = (GEPoint) elementlist.get(2);
        GEPoint p3 = (GEPoint) elementlist.get(3);
        TMono m1 = GeoPoly.perpendicular(p.x1.xindex, p.y1.xindex, p1.x1.xindex, p1.y1.xindex,
                p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex);
        TMono m2 = GeoPoly.perpendicular(p.x1.xindex, p.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p1.x1.xindex, p1.y1.xindex, p3.x1.xindex, p3.y1.xindex);

        add_des(gib.C_ORTH, p, p1, p2, p3);

        return NewTPoly(m1, m2);

    }

    TPoly PolySymPoint() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint po = (GEPoint) elementlist.get(1);
        GEPoint p2 = (GEPoint) elementlist.get(2);

        TMono m1 = GeoPoly.midpoint(p1.x1.xindex, po.x1.xindex, p2.x1.xindex);
        TMono m2 = GeoPoly.midpoint(p1.y1.xindex, po.y1.xindex, p2.y1.xindex);

        return NewTPoly(m1, m2);
    }

    TMono polyMulside() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        Integer i1 = (Integer) elementlist.get(4);
        Integer i2 = (Integer) elementlist.get(5);
        add_des(gib.C_NRATIO, p1, p2, p3, p4, i1, i2);
        return GeoPoly.p_p_mulside(p1, p2, p3, p4, i1.intValue(), i2.intValue());
    }

    TMono PolyHorizonal() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        return GeoPoly.p_p_horizonal(p1, p2);
    }

    TMono PolyVertical() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        return GeoPoly.p_p_vertical(p1, p2);
    }


    TPoly PolyLCTangent() {
        GELine line1;
        GECircle c;
        Object obj1 = elementlist.get(0);
        Object obj2 = elementlist.get(1);
        if (obj1 instanceof GELine) {
            line1 = (GELine) obj1;
            c = (GECircle) obj2;
        } else {
            line1 = (GELine) obj2;
            c = (GECircle) obj1;
        }

        GEPoint[] pl = line1.getTwoPointsOfLine();
        GEPoint[] cl = c.getRadiusPoint();
        if (pl == null) return null;
        TMono m = GeoPoly.l_c_tangent(pl[0], pl[1], cl[0], cl[1], c.o);
        TPoly tp = new TPoly();
        tp.setNext(null);
        tp.setPoly(m);

        return tp;
    }

    TPoly PolyCircumCenter() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);

        add_des(gib.C_CIRCUM, p1, p2, p3, p4);

        return GeoPoly.circumcenter(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                p4.x1.xindex, p4.y1.xindex);
    }

    TPoly PolyBaryCenter() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        add_des(gib.C_CENT, p1, p2, p3, p4);
        return GeoPoly.barycenter(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                p4.x1.xindex, p4.y1.xindex);
    }


    TPoly PolyPropPoint() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        Integer i1 = (Integer) elementlist.get(3);
        Integer i2 = (Integer) elementlist.get(4);

        add_des(gib.C_LRATIO, p1, p2, p1, p3, i1, i2);

        return GeoPoly.prop_point(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, i1.intValue(), i2.intValue());
    }

    TPoly PolyPratio() {  // p1   is p2 + /p3p4/
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        int r1 = 1;
        int r2 = 1;
        if (elementlist.size() == 6) {
            Integer i1 = (Integer) elementlist.get(4);
            Integer i2 = (Integer) elementlist.get(5);
            r1 = i1.intValue();
            r2 = i2.intValue();
            add_des(gib.C_PRATIO, p1, p2, p3, p4, i1, i2);
        } else
            add_des(gib.C_PRATIO, p1, p2, p3, p4);

        return GeoPoly.Pratio(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                p4.x1.xindex, p4.y1.xindex, r1, r2);
    }

    TPoly PolyInterCC() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GECircle c1 = (GECircle) elementlist.get(2);
        GECircle c2 = (GECircle) elementlist.get(3);
        GEPoint p3 = c1.o;
        GEPoint p4 = c2.o;
//        add_des(gib.C_O_C, p1, p3, p2);
//        add_des(gib.C_O_C, p1, p4, p2);
        add_des(gib.C_I_CC, p1, p3, p2, p4, p2);

        return GeoPoly.mirrorPL(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                p4.x1.xindex, p4.y1.xindex);
    }

    TPoly PolyMirror()  // p1 to p2 through obj
    {
        Object obj = elementlist.get(2);
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);

        if (obj instanceof GELine) {
            GELine line = (GELine) obj;
            GEPoint[] pl = line.getTwoPointsOfLine();
            GEPoint p3 = pl[0];
            GEPoint p4 = pl[1];

            add_des(gib.C_SYM, p1, p2, p3, p4);
            return GeoPoly.mirrorPL(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                    p4.x1.xindex, p4.y1.xindex);
        } else {
            GEPoint p3 = (GEPoint) elementlist.get(2);
            GEPoint p4 = (GEPoint) elementlist.get(3);

            add_des(gib.C_SYM, p1, p2, p3, p4);

            return GeoPoly.mirrorPL(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                    p4.x1.xindex, p4.y1.xindex);
        }

    }

    TMono PolyISOTriangle() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);

        add_des(gib.C_ISO_TRI, p1, p2, p3);

        return GeoPoly.bisect(p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, p1.x1.xindex, p1.y1.xindex);
    }

    TMono PolyPerpBisect() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        add_des(gib.C_O_B, p1, p2, p3);


        return GeoPoly.bisect(p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, p1.x1.xindex, p1.y1.xindex);
    }

    TPoly PolyTRatio() {
        int n = elementlist.size();
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        Integer I, I1;
        if (n == 6) {
            I = (Integer) elementlist.get(4);
            I1 = (Integer) elementlist.get(5);
        } else {
            I = new Integer(1);
            I1 = new Integer(1);
        }
        add_des(gib.C_TRATIO, p1, p2, p3, p4, I, I1);
        int t1 = I.intValue();
        int t2 = I1.intValue();

        return GeoPoly.Tratio(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex, t1, t2);

    }

    TPoly PolyNTSegment() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        //des = new Act(Act.QSQUAR, p1, p2, p3, p4);
        return GeoPoly.squarept2(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex, proportion);

    }

    TMono PolyNTAngle() {
        GELine ln1 = (GELine) elementlist.get(0);
        GELine ln2 = (GELine) elementlist.get(1);
        GELine ln3 = (GELine) elementlist.get(2);
        GELine ln = (GELine) elementlist.get(3);
        GEPoint pt = (GEPoint) elementlist.get(4);
        GEPoint[] l1 = ln1.getTwoPointsOfLine();
        GEPoint[] l2 = ln2.getTwoPointsOfLine();
        GEPoint[] l3 = ln3.getTwoPointsOfLine();
        if (l1 == null || l2 == null || l3 == null) return null;
        GEPoint c = ln.getfirstPoint();
        if (c == pt) return null;
        return GeoPoly.eqangle(l1[0].x1.xindex, l1[0].y1.xindex, l1[1].x1.xindex, l1[1].y1.xindex,
                l2[0].x1.xindex, l2[0].y1.xindex, l2[1].x1.xindex, l2[1].y1.xindex,
                l3[0].x1.xindex, l3[0].y1.xindex, l3[1].x1.xindex, l3[1].y1.xindex,
                c.x1.xindex, c.y1.xindex, pt.x1.xindex, pt.y1.xindex);
    }

    TPoly PolyPsquare() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = p2;
        GEPoint p4 = (GEPoint) elementlist.get(2);

        return GeoPoly.squarept1(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex, proportion);

    }

    TPoly PolyQsquare() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = p2;
        GEPoint p4 = (GEPoint) elementlist.get(2);
        return GeoPoly.squarept2(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex, proportion);

    }

    TMono PolyCCLine() {
        GEPoint p = (GEPoint) getelement(0);
        GECircle c1 = (GECircle) getelement(1);
        GECircle c2 = (GECircle) getelement(2);
        GEPoint po1 = c1.o;
        GEPoint po2 = c2.o;
        GEPoint pc1 = c1.getSidePoint();
        GEPoint pc2 = c2.getSidePoint();

        return GeoPoly.ccline(p.x1.xindex, p.y1.xindex, po1.x1.xindex, po1.y1.xindex, pc1.x1.xindex, pc1.y1.xindex,
                po2.x1.xindex, po2.y1.xindex, pc2.x1.xindex, pc2.y1.xindex);

    }

    TMono PolySAngle() {
        GELine ln1 = (GELine) elementlist.get(0);
        GELine ln2 = (GELine) elementlist.get(1);
        Integer I = (Integer) elementlist.get(2);

        GEPoint p = GELine.commonPoint(ln1, ln2);
        GEPoint lp = ln1.get_Lpt1(p);
        GEPoint lp1 = ln2.get_Lpt1(p);
        if (p != null && lp != null && lp1 != null) {
            add_des(gib.C_SANGLE, lp, p, lp1, new Integer(Math.abs(I.intValue())));
            return GeoPoly.sangle(lp.x1.xindex, lp.y1.xindex, p.x1.xindex, p.y1.xindex, lp1.x1.xindex, lp1.y1.xindex, proportion);
        } else
            return null;
    }

    TMono PolyALine() {
        GELine ln1 = (GELine) elementlist.get(0);
        GELine ln2 = (GELine) elementlist.get(1);
        GELine ln3 = (GELine) elementlist.get(2);
        GELine ln4 = (GELine) elementlist.get(3);
        GEPoint p1, p2, p3, p4, c1, c2;

        c1 = GELine.commonPoint(ln1, ln2);
        GEPoint[] lp = ln1.getTwoPointsOfLine();
        if (lp[0] == c1)
            p1 = lp[1];
        else
            p1 = lp[0];

        lp = ln2.getTwoPointsOfLine();
        if (lp[0] == c1)
            p2 = lp[1];
        else
            p2 = lp[0];
        c2 = GELine.commonPoint(ln3, ln4);
        lp = ln3.getTwoPointsOfLine();
        if (lp[0] == c2)
            p3 = lp[1];
        else
            p3 = lp[0];

        lp = ln4.getTwoPointsOfLine();

        if (lp != null) {
            if (lp[0] == c2)
                p4 = lp[1];
            else
                p4 = lp[0];
            add_des(gib.C_O_A, p4, c2, p3, p2, c1, p1);
            return GeoPoly.eqangle(p4.x1.xindex, p4.y1.xindex, c2.x1.xindex, c2.y1.xindex,
                    p3.x1.xindex, p3.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                    c1.x1.xindex, c1.y1.xindex, p1.x1.xindex, p1.y1.xindex);
        }
        return null;
    }

    TMono PolyEqAngle() {
        if (elementlist.size() == 2) {
            GEAngle ag1 = (GEAngle) elementlist.get(0);
            GEAngle ag2 = (GEAngle) elementlist.get(1);

            GEPoint pa = GELine.commonPoint(ag1.lstart, ag1.lend);
            GEPoint pb = GELine.commonPoint(ag2.lstart, ag2.lend);
            GEPoint pa1 = ag1.pstart;
            GEPoint pa2 = ag1.pend;
            GEPoint pb1 = ag2.pstart;
            GEPoint pb2 = ag2.pend;
            GELine ln1 = ag1.lstart;
            GELine ln2 = ag1.lend;
            GELine ln3 = ag2.lstart;
            GELine ln4 = ag2.lend;
            GEPoint[] lp1 = ln1.getTwoPointsOfLine();
            GEPoint[] lp2 = ln2.getTwoPointsOfLine();
            GEPoint[] lp3 = ln3.getTwoPointsOfLine();
            GEPoint[] lp4 = ln4.getTwoPointsOfLine();
            if (lp1 == null || lp2 == null || lp3 == null || lp4 == null) return null;

            add_des(gib.C_EQANGLE, pa1, pa, pa2, pb1, pb, pb2);
            return GeoPoly.eqangle(lp1[0].x1.xindex, lp1[0].y1.xindex, lp1[1].x1.xindex, lp1[1].y1.xindex,
                    lp2[0].x1.xindex, lp2[0].y1.xindex, lp2[1].x1.xindex, lp2[1].y1.xindex,
                    lp3[0].x1.xindex, lp3[0].y1.xindex, lp3[1].x1.xindex, lp3[1].y1.xindex,
                    lp4[0].x1.xindex, lp4[0].y1.xindex, lp4[1].x1.xindex, lp4[1].y1.xindex);
        } else // four lines.
        {

            GELine ln1 = (GELine) elementlist.get(0);
            GELine ln2 = (GELine) elementlist.get(1);
            GELine ln3 = (GELine) elementlist.get(2);
            GELine ln4 = (GELine) elementlist.get(3);

            GEPoint pa = GELine.commonPoint(ln1, ln2);
            GEPoint pb = GELine.commonPoint(ln3, ln4);

            GEPoint pa1 = ln1.getSecondPoint(pa);
            GEPoint pa2 = ln2.getSecondPoint(pa);
            GEPoint pb1 = ln3.getSecondPoint(pb);
            GEPoint pb2 = ln4.getSecondPoint(pb);

            GEPoint[] lp1 = ln1.getTwoPointsOfLine();
            GEPoint[] lp2 = ln2.getTwoPointsOfLine();
            GEPoint[] lp3 = ln3.getTwoPointsOfLine();
            GEPoint[] lp4 = ln4.getTwoPointsOfLine();
            if (lp1 == null || lp2 == null || lp3 == null || lp4 == null) return null;

            add_des(gib.C_EQANGLE, pa1, pa, pa2, pb1, pb, pb2);
            return GeoPoly.eqangle(lp1[0].x1.xindex, lp1[0].y1.xindex, lp1[1].x1.xindex, lp1[1].y1.xindex,
                    lp2[0].x1.xindex, lp2[0].y1.xindex, lp2[1].x1.xindex, lp2[1].y1.xindex,
                    lp3[0].x1.xindex, lp3[0].y1.xindex, lp3[1].x1.xindex, lp3[1].y1.xindex,
                    lp4[0].x1.xindex, lp4[0].y1.xindex, lp4[1].x1.xindex, lp4[1].y1.xindex);

        }
    }

    TMono PolyEqAngle3P() {
        GEAngle ag1 = (GEAngle) elementlist.get(0);
        GEAngle ag2 = (GEAngle) elementlist.get(1);
        GEAngle ag3 = (GEAngle) elementlist.get(2);
        param pm = (param) elementlist.get(3);

        GEPoint p1 = ag1.pstart;
        GEPoint p2 = ag1.getVertex();
        GEPoint p3 = ag1.pend;
        GEPoint p4 = ag2.pstart;
        GEPoint p5 = ag2.getVertex();
        GEPoint p6 = ag2.pend;
        GEPoint p7 = ag3.pstart;
        GEPoint p8 = ag3.getVertex();
        GEPoint p9 = ag3.pend;

        add_des(gib.C_EQANGLE3P, p1, p2, p3, p4, p5, p6, p7, p8, p9, pm.type);

        return GeoPoly.eqangle3p(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                p4.x1.xindex, p4.y1.xindex, p5.x1.xindex, p5.y1.xindex, p6.x1.xindex, p6.y1.xindex,
                p7.x1.xindex, p7.y1.xindex, p8.x1.xindex, p8.y1.xindex, p9.x1.xindex, p9.y1.xindex,
                pm.xindex);

    }

    TMono PolySpecifiAngle() {
        param pm = (param) elementlist.get(0);
        return GeoPoly.specificangle(pm.xindex, proportion);
    }

    TMono PolyEqidstance() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);

        add_des(gib.C_EQDISTANCE, p1, p2, p3, p4);
        return GeoPoly.eqdistance(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);

    }

    TPoly PolyMidPoint() {
        GEPoint po = (GEPoint) elementlist.get(0);
        GEPoint p1 = (GEPoint) elementlist.get(1);
        GEPoint p2 = (GEPoint) elementlist.get(2);

        TMono m1 = GeoPoly.midpoint(p1.x1.xindex, po.x1.xindex, p2.x1.xindex);
        TMono m2 = GeoPoly.midpoint(p1.y1.xindex, po.y1.xindex, p2.y1.xindex);

        add_des(gib.C_MIDPOINT, po, p1, p2);

        TPoly poly = new TPoly();
        poly.setPoly(m1);
        TPoly poly2 = new TPoly();
        poly2.setPoly(m2);
        poly2.setNext(poly);
        return poly2;

    }


    TPoly PolyRectangle() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        add_des(gib.C_RECTANGLE, p1, p2, p3, p4);

        TMono m1 = GeoPoly.parallel(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
        TMono m2 = GeoPoly.perpendicular(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex);
        TMono m3 = GeoPoly.perpendicular(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p1.x1.xindex, p1.y1.xindex, p4.x1.xindex, p4.y1.xindex);
        TPoly p = NewTPoly(m1, m2);
        TPoly pp = new TPoly();
        pp.setPoly(m3);
        pp.setNext(p);
        return pp;
    }

    TMono PolyTrapezoid() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        add_des(gib.C_TRAPEZOID, p1, p2, p3, p4);
        TMono m1 = GeoPoly.parallel(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
        return m1;
    }

    TPoly PolyParallelogram() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);

        int n = p4.x1.xindex;
        if (n > p1.x1.xindex && n > p2.x1.xindex) {
            GEPoint pt = p4;
            p4 = p1;
            p1 = pt;
            pt = p3;
            p3 = p2;
            p2 = pt;
        }
        add_des(gib.C_PARALLELOGRAM, p1, p2, p3, p4);
        TMono m1 = GeoPoly.parallel(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
        TMono m2 = GeoPoly.parallel(p1.x1.xindex, p1.y1.xindex, p4.x1.xindex, p4.y1.xindex,
                p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex);
        return NewTPoly(m1, m2);

    }

    TPoly PolyRTrapezoid() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);
        GEPoint p4 = (GEPoint) elementlist.get(3);
        add_des(gib.C_R_TRAPEZOID, p1, p2, p3, p4);
        TMono m1 = GeoPoly.parallel(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex,
                p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
        TMono m2 = GeoPoly.perpendicular(p1.x1.xindex, p1.y1.xindex, p4.x1.xindex, p4.y1.xindex,
                p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex);
        return NewTPoly(m1, m2);

    }

    TMono PolyRightTriangle() {
        GEPoint po = (GEPoint) elementlist.get(0);
        GEPoint p1 = (GEPoint) elementlist.get(1);
        GEPoint p2 = (GEPoint) elementlist.get(2);
        add_des(gib.C_R_TRI, po, p1, p2);
        return GeoPoly.perpendicular(po.x1.xindex, po.y1.xindex, p1.x1.xindex, p1.y1.xindex,
                po.x1.xindex, po.y1.xindex, p2.x1.xindex, p2.y1.xindex);
    }

    TMono PolyOnLine() {
        GEPoint p = (GEPoint) getelement(0);
        GELine line = (GELine) getelement(1);
        int x, y;
        x = p.x1.xindex;
        y = p.y1.xindex;

        if (line.linetype == GELine.CCLine) {
            constraint cs = line.getcons(0);

            GECircle c1 = (GECircle) cs.getelement(1);
            GECircle c2 = (GECircle) cs.getelement(2);
            GEPoint po1 = c1.o;
            GEPoint po2 = c2.o;
            GEPoint pc1 = c1.getSidePoint();
            GEPoint pc2 = c2.getSidePoint();
            //?????
            return GeoPoly.ccline(p.x1.xindex, p.y1.xindex, po1.x1.xindex, po1.y1.xindex, pc1.x1.xindex, pc1.y1.xindex,
                    po2.x1.xindex, po2.y1.xindex, pc2.x1.xindex, pc2.y1.xindex);


        } else {
            GEPoint[] plist = line.getTwoPointsOfLine();
            if (plist == null) return null;
            GEPoint p1, p2;
            p1 = plist[0];
            p2 = plist[1];
            add_des(gib.C_O_L, p, p1, p2);
            return GeoPoly.collinear(x, y, p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex);
        }
    }

    TPoly PolyIntersection_CC() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GECircle c1 = (GECircle) elementlist.get(1);
        GECircle c2 = (GECircle) elementlist.get(2);
        HashSet<GEPoint> v = GECircle.CommonPoints(c1, c2);
        //TPoly tp = null;
        Iterator<GEPoint> iter = v.iterator();
        GEPoint t1 = (iter.hasNext()) ? iter.next() : null;
        GEPoint t2 = (iter.hasNext()) ? iter.next() : null;
        
        GEPoint p2 = (t1 != p1) ? t1 : t2;

        if (p2 != null && p2.x1.xindex < p1.x1.xindex) {
            GEPoint p3 = c1.o;
            GEPoint p4 = c2.o;
            add_des(gib.C_I_CC, p1, p3, p2, p4, p2);
            return GeoPoly.mirrorPL(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex,
                    p4.x1.xindex, p4.y1.xindex);
        }

        p2 = c1.getSidePoint();
        GEPoint p3 = c2.getSidePoint();
        if (p2 != null && p3 != null) {
            add_des(gib.C_I_CC, p1, c1.o, p2, c2.o, p3);
            TMono m1 = GeoPoly.eqdistance(p1.x1.xindex, p1.y1.xindex, c1.o.x1.xindex, c1.o.y1.xindex,
                    p2.x1.xindex, p2.y1.xindex, c1.o.x1.xindex, c1.o.y1.xindex);
            TMono m2 = GeoPoly.eqdistance(p1.x1.xindex, p1.y1.xindex, c2.o.x1.xindex, c2.o.y1.xindex,
                    p3.x1.xindex, p3.y1.xindex, c2.o.x1.xindex, c2.o.y1.xindex);
            return NewTPoly(m1, m2);
        } else {
            GEPoint[] l1 = c1.getRadiusPoint();
            GEPoint[] l2 = c2.getRadiusPoint();
            add_des(gib.C_I_RR, p1, c1.o, l1[0], l1[1], c2.o, l2[0], l2[1]);

            TMono m1 = GeoPoly.eqdistance(p1.x1.xindex, p1.y1.xindex, c1.o.x1.xindex, c1.o.y1.xindex,
                    l1[0].x1.xindex, l1[0].y1.xindex, l1[1].x1.xindex, l1[1].y1.xindex);
            TMono m2 = GeoPoly.eqdistance(p1.x1.xindex, p1.y1.xindex, c2.o.x1.xindex, c2.o.y1.xindex,
                    l2[0].x1.xindex, l2[0].y1.xindex, l2[1].x1.xindex, l2[1].y1.xindex);
            return NewTPoly(m1, m2);
        }
    }

    TPoly PolyIntersection_ll() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GELine ln1 = (GELine) elementlist.get(1);
        GELine ln2 = (GELine) elementlist.get(2);
        if (compareLN(ln1, ln2)) {
            GELine ln = ln1;
            ln1 = ln2;
            ln2 = ln;
        }

        GEPoint[] ps1 = ln1.getTwoPointsOfLine();
        if (ps1 == null) return null;
        GEPoint[] ps2 = ln2.getTwoPointsOfLine();
        if (ps2 == null) return null;
        add_des(gib.C_I_LL, p1, ps1[0], ps1[1], ps2[0], ps2[1]);
        TMono m1 = GeoPoly.collinear(p1.x1.xindex, p1.y1.xindex, ps1[0].x1.xindex, ps1[0].y1.xindex, ps1[1].x1.xindex, ps1[1].y1.xindex);
        addZeron(m1);
        TMono m2 = GeoPoly.collinear(p1.x1.xindex, p1.y1.xindex, ps2[0].x1.xindex, ps2[0].y1.xindex, ps2[1].x1.xindex, ps2[1].y1.xindex);
        return NewTPoly(m1, m2);
    }

    TPoly PolyIntersection_lc() {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GELine ln = (GELine) elementlist.get(1);
        GECircle c = (GECircle) elementlist.get(2);
        GEPoint[] ls = GELine.commonPoint(ln, c);
        GEPoint[] np = ln.getTwoPointsOfLine();
        GEPoint o = c.o;

        GEPoint p2;

        if (ls.length == 2) {
            if (p1 == ls[0])
                p2 = ls[1];
            else
                p2 = ls[0];
        } else if (ls.length == 1 && ls[0] != p1)
            p2 = ls[0];
        else
            p2 = null;

        if (p2 != null && p1.x1.xindex > p2.x1.xindex) {
            add_des(gib.C_I_LC, p1, np[0], np[1], c.o, c.getSidePoint());
            GEPoint pl = ln.getSecondPoint(p2);
            return GeoPoly.LCMeet(o.x1.xindex, o.y1.xindex, p2.x1.xindex,
                    p2.y1.xindex, pl.x1.xindex, pl.y1.xindex, p1.x1.xindex, p1.y1.xindex);
        }

        GEPoint pl = c.getSidePoint();
        if (pl != null) {
            add_des(gib.C_I_LC, p1, np[0], np[1], c.o, c.getSidePoint());
            TMono m1 = GeoPoly.collinear(p1.x1.xindex, p1.y1.xindex, np[0].x1.xindex, np[0].y1.xindex, np[1].x1.xindex, np[1].y1.xindex);
            addZeron(m1);
            TMono m2 = GeoPoly.eqdistance(o.x1.xindex, o.y1.xindex, p1.x1.xindex, p1.y1.xindex, o.x1.xindex, o.y1.xindex, pl.x1.xindex, pl.y1.xindex);
            return NewTPoly(m1, m2);
        } else {
            GEPoint[] ll = c.getRadiusPoint();
            add_des(gib.C_I_LR, p1, np[0], np[1], c.o, ll[0], ll[1]);
            TMono m1 = GeoPoly.collinear(p1.x1.xindex, p1.y1.xindex, np[0].x1.xindex, np[0].y1.xindex, np[1].x1.xindex, np[1].y1.xindex);
            addZeron(m1);
            TMono m2 = GeoPoly.eqdistance(o.x1.xindex, o.y1.xindex, p1.x1.xindex, p1.y1.xindex, ll[0].x1.xindex, ll[0].y1.xindex, ll[1].x1.xindex, ll[1].x1.xindex);
            return NewTPoly(m1, m2);
        }

    }

    TMono collinear() {
        GEPoint p1, p2, p3;
        p1 = p2 = p3 = null;

        for (int i = 0; i < elementlist.size(); i++) {
            GEPoint p = (GEPoint) getelement(i);
            if (p == null) continue;

            if (p1 == null)
                p1 = p;
            else if (p1.x1.xindex < p.x1.xindex) {
                if (p2 == null)
                    p2 = p1;
                else
                    p3 = p1;
                p1 = p;

            } else if (p2 == null)
                p2 = p;
            else
                p3 = p;
        }

        add_des(gib.C_O_L, p1, p2, p3);
        return GeoPoly.collinear(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex);
    }

    TPoly PolyLCMeet() {

        GEPoint pl = null;
        GEPoint p = (GEPoint) getelement(0);
        GEPoint pc = (GEPoint) getelement(1);
        GELine ln = (GELine) getelement(2);
        GECircle c = (GECircle) getelement(3);
        GEPoint o = c.o;
        ArrayList<GEPoint> pts = ln.points;
        for (int i = 0; i < pts.size(); i++)
            if (pts.get(i) != pc) {
                pl = pts.get(i);
                break;
            }
        if (pl == null) return null;
        add_des(gib.C_I_LC, p, pc, pl, o, pc);
        return GeoPoly.LCMeet(o.x1.xindex, o.y1.xindex, pc.x1.xindex, pc.y1.xindex, pl.x1.xindex, pl.y1.xindex, p.x1.xindex, p.y1.xindex);
    }

    TMono PolyOnCircle() {
        GEPoint p = (GEPoint) getelement(0);
        GECircle c = (GECircle) getelement(1);
        GEPoint o = c.o;

        if (c.circle_type == GECircle.RCircle && (c.points.size() == 0 || c.points.size() == 1 && c.points.contains(p))) {
            constraint cs = null;
            for (int i = 0; i < c.cons.size(); i++) {
                constraint cc = c.cons.get(i);
                if (cc.GetConstraintType() == constraint.RCIRCLE && c == cc.getelement(2)) {
                    cs = cc;
                    break;
                }
            }
            if (cs == null) return null;
            GEPoint p1 = (GEPoint) cs.getelement(0);
            GEPoint p2 = (GEPoint) cs.getelement(1);

            add_des(gib.C_O_R, p, o, p1, p2);

            return GeoPoly.eqdistance(o.x1.xindex, o.y1.xindex, p.x1.xindex, p.y1.xindex, p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex);
        } else if (c.points.size() != 0)//|| c.type == Circle.PCircle || c.type == Circle.SCircle)
        {
            GEPoint pt = c.getSidePoint();
            if (pt == null)
                return null;

            add_des(gib.C_O_C, p, o, pt);

            return GeoPoly.eqdistance(o.x1.xindex, o.y1.xindex, p.x1.xindex, p.y1.xindex, o.x1.xindex, o.y1.xindex, pt.x1.xindex, pt.y1.xindex);

        } else {
            CMisc.print("ERROR CIRCLE CONSTRAINT");
        }
        return null;
    }

    TMono PolyPerp() {
        if (elementlist.size() == 2) {
            GELine line1 = (GELine) getelement(0);
            GELine line2 = (GELine) getelement(1);

            if (line1.points.size() < 2)
                return null;
            GEPoint[] pl1 = line1.getTwoPointsOfLine();
            GEPoint[] pl2 = line2.getTwoPointsOfLine();

            if (pl1 == null || pl2 == null)
                return null;

            add_desx1(gib.C_O_T, pl1[0], pl1[1], pl2[0], pl2[1]);

            return GeoPoly.perpendicular(pl1[0].x1.xindex, pl1[0].y1.xindex, pl1[1].x1.xindex, pl1[1].y1.xindex,
                    pl2[0].x1.xindex, pl2[0].y1.xindex, pl2[1].x1.xindex, pl2[1].y1.xindex);
        } else if (elementlist.size() == 4) {
            GEPoint p1 = (GEPoint) elementlist.get(0);
            GEPoint p2 = (GEPoint) elementlist.get(1);
            GEPoint p3 = (GEPoint) elementlist.get(2);
            GEPoint p4 = (GEPoint) elementlist.get(3);
            int x1, y1, x2, y2, x3, y3, x4, y4;
            x1 = p1.x1.xindex;
            y1 = p1.y1.xindex;
            x2 = p2.x1.xindex;
            y2 = p2.y1.xindex;
            x3 = p3.x1.xindex;
            y3 = p3.y1.xindex;
            x4 = p4.x1.xindex;
            y4 = p4.y1.xindex;
            add_des(gib.C_O_T, p1, p2, p3, p4);
            return GeoPoly.perpendicular(x1, y1, x2, y2, x3, y3, x4, y4);
        }
        return null;
    }

    TPoly PolyPfoot() {
        GEPoint p1 = (GEPoint) getelement(0);
        GEPoint p2 = (GEPoint) getelement(1);
        GEPoint p3 = (GEPoint) getelement(2);
        GEPoint p4 = (GEPoint) getelement(3);
        add_des(gib.C_FOOT, p1, p2, p3, p4);
        TMono m1 = GeoPoly.collinear(p1.x1.xindex, p1.y1.xindex, p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
        constraint.addZeron(m1);
        TMono m2 = GeoPoly.perpendicular(p1.x1.xindex, p1.y1.xindex,
                p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
        return mpoly(m1, m2);
    }

    TMono PolyParel() {
        GELine line1 = (GELine) getelement(0);
        GELine line2 = (GELine) getelement(1);

        if (line1.points.size() < 2)
            return null;
        GEPoint[] pl1 = line1.getTwoPointsOfLine();
        GEPoint[] pl2 = line2.getTwoPointsOfLine();

        if (pl1 == null || pl2 == null)
            return null;

        add_desx1(gib.C_O_P, pl1[0], pl1[1], pl2[0], pl2[1]);

        return GeoPoly.parallel(pl1[0].x1.xindex, pl1[0].y1.xindex, pl1[1].x1.xindex, pl1[1].y1.xindex,
                pl2[0].x1.xindex, pl2[0].y1.xindex, pl2[1].x1.xindex, pl2[1].y1.xindex);
    }

    TMono PolyCCTangent() {
        GECircle c1 = (GECircle) getelement(0);
        GECircle c2 = (GECircle) getelement(1);

        GEPoint[] pl1 = c1.getRadiusPoint();
        GEPoint[] pl2 = c2.getRadiusPoint();
        add_des(gib.C_CCTANGENT, c1.o, pl1[0], pl1[1], c2.o, pl2[0], pl2[1]);

        return GeoPoly.c_c_tangent(pl1[0], pl1[1], c1.o, pl2[0], pl2[1], c2.o);
    }

    public static boolean PolyConstraint(int type, GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        int x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = p1.x1.xindex;
        y1 = p1.y1.xindex;
        x2 = p2.x1.xindex;
        y2 = p2.y1.xindex;
        x3 = p3.x1.xindex;
        y3 = p3.y1.xindex;
        TMono mpoly = null;
        TMono mpoly1 = null;
        switch (type) {
            case constraint.COLLINEAR: // 3 obj
                mpoly = GeoPoly.collinear(x1, y1, x2, y2, x3, y3);
                break;
            case constraint.PARALLEL: // 4 obj
                x4 = p4.x1.xindex;
                y4 = p4.y1.xindex;
                mpoly = GeoPoly.parallel(x1, y1, x2, y2, x3, y3, x4, y4);
                break;
            case constraint.PERPENDICULAR:    //4
                x4 = p4.x1.xindex;
                y4 = p4.y1.xindex;
                mpoly = GeoPoly.perpendicular(x1, y1, x2, y2, x3, y3, x4, y4);
                break;
            case constraint.EQDISTANCE:       //4
                x4 = p4.x1.xindex;
                y4 = p4.y1.xindex;
                mpoly = GeoPoly.eqdistance(x1, y1, x2, y2, x3, y3, x4, y4);
                break;
            case constraint.BISECT:
                mpoly = GeoPoly.bisect(x1, y1, x2, y2, x3, y3);
                break;
            case constraint.MIDPOINT:
                mpoly = GeoPoly.midpoint(x1, x2, x3);
                mpoly1 = GeoPoly.midpoint(y1, y2, y3);
            default:
                break;
        }

        if (mpoly == null)
            return false;
        TPoly tp = new TPoly();
        tp.setPoly(mpoly);
        tp.setNext(polylist);
        polylist = tp;
        if (type == constraint.MIDPOINT) {
            tp = new TPoly();
            tp.setPoly(mpoly1);
            tp.setNext(polylist);
            polylist = tp;
        }
        return true;
    }

    public TPoly NewTPoly(TMono m1, TMono m2) {
        TPoly poly = new TPoly();
        poly.setPoly(m1);
        TPoly poly2 = new TPoly();
        poly2.setPoly(m2);
        poly2.setNext(poly);
        return poly2;
    }

    

	
//	public constraint(Element thisElement, final Map<Integer, Object> mapObj) {
//		super();
//
//		bIsValidEntity = false;
//		if (thisElement != null) {
//
//			bIsValidEntity = true;
//			id = GExpert.safeParseInt(thisElement.getAttribute("id"), 0);
//			ConstraintType = GExpert.safeParseInt(thisElement.getAttribute("type"), 0);
//			is_poly_genereate = GExpert.safeParseBoolean(thisElement.getAttribute("poly_genereate"), true);
//		}
//		verifyValidity();
//	}

	public void saveIntoXMLDocument(Element rootElement) {
		assert(rootElement != null);
		if (rootElement != null) {
			Document doc = rootElement.getOwnerDocument();
			
			Element thisElement = doc.createElement("constraint");
			thisElement.setAttribute("id", String.valueOf(id));
			thisElement.setAttribute("type", String.valueOf(ConstraintType));
			thisElement.setAttribute("proportion", String.valueOf(proportion));
			if (bPolyGenerate)
				thisElement.setAttribute("poly_generate", String.valueOf(bPolyGenerate));
			rootElement.appendChild(thisElement);

	        for (Object obj : elementlist) {
	            if (obj == null) {
	    			Element elemParam = doc.createElement("nullparameter");
	    			thisElement.appendChild(elemParam);
	            } else if (obj instanceof param) {
	    			Element elemParam = doc.createElement("parameter");
	    			elemParam.setTextContent(String.valueOf(((param) obj).xindex));
	    			thisElement.appendChild(elemParam);
	            } else if (obj instanceof Integer) {
	    			Element elemInt = doc.createElement("integer");
	    			elemInt.setTextContent(String.valueOf(obj));
	    			thisElement.appendChild(elemInt);
	            } else {
	    			Element elemGE = doc.createElement("graphic_entity");
	    			elemGE.setTextContent(String.valueOf(((GraphicEntity) obj).m_id));
	    			thisElement.appendChild(elemGE);
	            }
	        }
		}
	}

//	public void Save(DataOutputStream out) throws IOException {
//        out.writeInt(id);
//        out.writeInt(ConstraintType);
//        int size = elementlist.size();
//        out.writeInt(size);
//        for (int i = 0; i < size; i++) {
//            Object obj = elementlist.get(i);
//            if (obj == null) {
//                CMisc.print("Constraint Null");
//            } else if (obj instanceof GEPoint) {
//                GEPoint p = (GEPoint) obj;
//                out.writeInt(1);
//                out.writeInt(p.m_id);
//            } else if (obj instanceof GELine) {
//                GELine ln = (GELine) obj;
//                out.writeInt(2);
//                out.writeInt(ln.m_id);
//            } else if (obj instanceof GECircle) {
//                GECircle c = (GECircle) obj;
//                out.writeInt(3);
//                out.writeInt(c.m_id);
//            } else if (obj instanceof GEDistance) {
//                GEDistance dis = (GEDistance) obj;
//                out.writeInt(4);
//                out.writeInt(dis.m_id);
//            } else if (obj instanceof GEAngle) {
//                GEAngle ag = (GEAngle) obj;
//                out.writeInt(5);
//                out.writeInt(ag.m_id);
//            } else if (obj instanceof param) {
//                param pm = (param) obj;
//                out.writeInt(6);
//                out.writeInt(pm.xindex);
//            } else if (obj instanceof Integer) {
//                Integer I = (Integer) obj;
//                out.writeInt(20);
//                out.writeInt(I.intValue());
//            } else if (obj instanceof GEPolygon) {
//                GEPolygon p = (GEPolygon) obj;
//                out.writeInt(7);
//                out.writeInt(p.m_id);
//            } else {
//                GraphicEntity cc = (GraphicEntity) obj;
//                out.writeInt(99);
//                out.writeInt(cc.m_id);
//            }
//
//        }
//        out.writeInt(proportion);
//        out.writeBoolean(bPolyGenerate);
//    }

    private void add_des(cons s) {
        if (csd == null)
            csd = s;
        else
            csd1 = s;
    }


    public void add_des(int t, GEPoint p1, GEPoint p2, GEPoint p3, Object obj) {
        cons csd = new cons(t);
        csd.setId(id);
        csd.add_pt(p1);
        csd.add_pt(p2);
        csd.add_pt(p3);
        csd.add_pt(obj);
        add_des(csd);
    }

    public void add_des(int t, GEPoint p1, GEPoint p2, GEPoint p3) {

        cons csd = new cons(t);
        csd.setId(id);
        csd.add_pt(p1);
        csd.add_pt(p2);
        csd.add_pt(p3);
        add_des(csd);
    }

    public void add_des(int t, ArrayList<Object> v) {
        cons csd = new cons(t);
        csd.setId(id);
        for (Object o : v)
            csd.add_pt(o);
        add_des(csd);
    }

    public static boolean less(GEPoint a, GEPoint b) {
        return a.x1.xindex < b.x1.xindex;
    }

    public void add_desx1(int t, GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {   //parallel ,perpendicular
        if (less(p1, p2)) {
            GEPoint a = p1;
            p1 = p2;
            p2 = a;
        }
        if (less(p3, p4)) {
            GEPoint a = p3;
            p3 = p4;
            p4 = a;
        }
        if (less(p1, p3)) {
            GEPoint a = p1;
            p1 = p3;
            p3 = a;
            a = p2;
            p2 = p4;
            p4 = a;
        }
        add_des(t, p1, p2, p3, p4);
    }

    public void add_des(int t, GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {

        cons csd = new cons(t);
        csd.setId(id);
        csd.add_pt(p1);
        csd.add_pt(p2);
        csd.add_pt(p3);
        csd.add_pt(p4);
        add_des(csd);
    }

    public void add_des(int t, Object p1, Object p2, Object p3, Object p4, Object p5) {
        cons csd = new cons(t);
        csd.setId(id);
        csd.add_pt(p1);
        csd.add_pt(p2);
        csd.add_pt(p3);
        csd.add_pt(p4);
        csd.add_pt(p5);
        add_des(csd);
    }

    public void add_des(int t, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        cons csd = new cons(t);
        csd.setId(id);
        csd.add_pt(p1);
        csd.add_pt(p2);
        csd.add_pt(p3);
        csd.add_pt(p4);
        csd.add_pt(p5);
        csd.add_pt(p6);
        add_des(csd);
    }

    public void add_des(int t, Object p1, Object p2, Object p3,
                        Object p4, Object p5, Object p6, Object p7, Object p8) {
        cons csd = new cons(t);
        csd.setId(id);
        csd.add_pt(p1);
        csd.add_pt(p2);
        csd.add_pt(p3);
        csd.add_pt(p4);
        csd.add_pt(p5);
        csd.add_pt(p6);
        csd.add_pt(p7);
        csd.add_pt(p8);
        add_des(csd);
    }

    public void add_des(int t, Object p1, Object p2, Object p3,
                        Object p4, Object p5, Object p6, Object p7, Object p8, Object p9, Object p10) {
        cons csd = new cons(t);
        csd.setId(id);
        csd.add_pt(p1);
        csd.add_pt(p2);
        csd.add_pt(p3);
        csd.add_pt(p4);
        csd.add_pt(p5);
        csd.add_pt(p6);
        csd.add_pt(p7);
        csd.add_pt(p8);
        csd.add_pt(p9);
        csd.add_pt(p10);
        add_des(csd);
    }

    public void add_des(int t, Object p1, Object p2, Object p3,
                        Object p4, Object p5, Object p6, Object p7) {
        cons csd = new cons(t);
        csd.setId(id);
        csd.add_pt(p1);
        csd.add_pt(p2);
        csd.add_pt(p3);
        csd.add_pt(p4);
        csd.add_pt(p5);
        csd.add_pt(p6);
        csd.add_pt(p7);
        add_des(csd);
    }


    public void Load(DataInputStream in, drawProcess dp) throws IOException {
        id = in.readInt();
        ConstraintType = in.readInt();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            int t = in.readInt();
            int d = in.readInt();

            switch (t) {
                case 1:
                    elementlist.add(dp.getPointById(d));
		    break;
                case 2:
                    elementlist.add(dp.getLineByid(d));
                    break;
                case 3:
                    elementlist.add(dp.getCircleByid(d));
                    break;
                case 4:
                    elementlist.add(dp.getObjectById(d));
                    break;
                case 5:
                    elementlist.add(dp.getAngleByid(d));
                    break;
                case 6:
                    elementlist.add(dp.getParameterByindex(d));
                    break;
                case 7:
                    elementlist.add(dp.getObjectById(d));
                    break;
                case 20:
                    elementlist.add(new Integer(d));
                    break;
                default:
                    elementlist.add(dp.getObjectById(d));
                    break;
            }
        }
        proportion = in.readInt();
        bPolyGenerate = in.readBoolean();
        if (CMisc.version_load_now <= 0.032) {
            if (ConstraintType == 16) {
                ConstraintType = NSQUARE;
                elementlist.remove(1);
            }

        }
    }

    int pidx(GEPoint p) {
        return p.x1.xindex;
    }

    int pidy(GEPoint p) {
        return p.y1.xindex;
    }

    TPoly mpoly(TMono m1, TMono m2) {
        TPoly p1 = new TPoly();
        p1.setPoly(m1);
        TPoly p2 = new TPoly();
        p2.setPoly(m2);
        p2.setNext(p1);
        return p2;
    }

    public static double get_sp_ag_value(int v) {
        double val = 0;
        if (v == 90)
            val = 0xfffff;
        else
            val = Math.tan((v * Math.PI) / 180.0);

        return val;
    }

    public double get_sangle_v() {
        return get_sp_ag_value(proportion);
    }

    public boolean check_constraint(double x, double y) {
        switch (ConstraintType) {
            case ANGLE_BISECTOR:
                return check_agbisector(x, y);
            case INCENTER:
                return check_incenter(x, y);
            default:
                return true;
        }
    }

    public boolean check_incenter(double x, double y) {
        GEPoint p1 = (GEPoint) elementlist.get(1);
        GEPoint p2 = (GEPoint) elementlist.get(2);
        GEPoint p3 = (GEPoint) elementlist.get(3);
        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();
        double x3 = p3.getx();
        double y3 = p3.gety();

        double r1 = dr_pr(x2, y2, x1, y1, x, y);
        double r2 = dr_pr(x2, y2, x, y, x3, y3);

        double r3 = dr_pr(x3, y3, x1, y1, x, y);
        double r4 = dr_pr(x3, y3, x, y, x2, y2);
        return r1 * r2 > 0 && r3 * r4 > 0;
    }

    public boolean check_agbisector(double x, double y) {
        GEPoint p1 = (GEPoint) elementlist.get(0);
        GEPoint p2 = (GEPoint) elementlist.get(1);
        GEPoint p3 = (GEPoint) elementlist.get(2);

        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();
        double x3 = p3.getx();
        double y3 = p3.gety();

        double r1 = dr_pr(x2, y2, x1, y1, x, y);
        double r2 = dr_pr(x2, y2, x, y, x3, y3);
        return r1 * r2 > 0;
    }

    public static double dr_pr(double x1, double y1, double x2, double y2, double x, double y) {
        return (y2 - y1) * (x - x2) - (y - y2) * (x2 - x1);
    }

    public static TMono eqdistance(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        return GeoPoly.eqdistance(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
    }

    public static boolean compareLN(GELine ln1, GELine ln2) {
        return (ln1.getfirstPoint().m_id > ln2.getfirstPoint().m_id);
    }

    public static TMono parseTMonoString(String name, String func, int x) {
        parser p = new parser(name, func, x);
        TMono m = p.parse();
        return m;
    }

    public static boolean addZeron(TMono m1) {
        if (m1 != null && PolyBasic.plength(m1) == 1)
            return GeoPoly.addZeroN(m1.x);
        return false;
    }

	public final int id() {
		return id;
	}
}