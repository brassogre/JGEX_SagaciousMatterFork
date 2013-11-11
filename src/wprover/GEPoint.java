package wprover;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.JTable;

import maths.param;

import org.w3c.dom.*;

/**
 * GEPoint encapsulates display attributes of a point, like screen location, size, and a nametag.
 * It contains a reference to constraints that it must obey.
 * @author Ye
 * 
 */
public class GEPoint extends GraphicEntity implements Pointed {
    private int type = 0;
    public param x1 = null;
    public param y1 = null;
    private HashSet<Constraint> cons = new HashSet<Constraint>();
    private boolean hasSetColor = false;
    private int m_radius = -1; //default
    private boolean frozen = false;
    public GEText textNametag = null;
    private HashSet<Integer> setConstraintIndices = new HashSet<Integer>();

    public GEPoint() {
        super(GraphicEntity.POINT);
        textNametag = new GEText(this, 5, -20, GEText.NAME_TEXT);
    }

    public GEPoint(String sName) {
        super(GraphicEntity.TEMP_POINT);
        m_name = sName;
    }
    
    public GEPoint(int type, param X, param Y) {
        super(type);
        x1 = X;
        y1 = Y;
        textNametag = null;
    }

	public GEPoint(String Name, param X, param Y) {
    	this(X, Y);
        m_name = Name;
    }

    public GEPoint(param X, param Y) {
    	this();
        x1 = X;
        y1 = Y;
    }

     public GEPoint(DrawPanel dp, final Element thisElement) {
    	super(dp, thisElement);

		int xindex = DrawPanelFrame.safeParseInt(thisElement.getAttribute("x"), 0);
		x1 = dp.getParameterByindex(xindex);
		bIsValidEntity &= (x1 != null);

		int yindex = DrawPanelFrame.safeParseInt(thisElement.getAttribute("y"), 0);
		y1 = dp.getParameterByindex(yindex);
		bIsValidEntity &= (y1 != null);
			
		//y1.value = GExpert.safeParseDouble(thisElement.getAttribute("yy"), 0);
		m_radius = DrawPanelFrame.safeParseInt(thisElement.getAttribute("radius"), -1);
		frozen = DrawPanelFrame.safeParseBoolean(thisElement.getAttribute("frozen"), false);
		setChosenColor(DrawPanelFrame.safeParseBoolean(thisElement.getAttribute("hasSetColor"), false));
		setColorDefault();
		textNametag = new GEText(this, 5, -20, GEText.NAME_TEXT); // XXX This method does not create a GEText with the same id number as is listed in the XML file.
		
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
            }
		}
		
		assert(bIsValidEntity);
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
     * This is a standard override of hashCode()
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + POINT_RADIUS;
		//result = prime * result + ((cons == null) ? 0 : cons.hashCode());
		result = prime * result + (frozen ? 1231 : 1237);
		result = prime * result + (hasSetColor ? 1231 : 1237);
		result = prime * result + m_radius;
		result = prime
				* result
				+ ((setConstraintIndices == null) ? 0 : setConstraintIndices
						.hashCode());
		result = prime * result
				+ ((textNametag == null) ? 0 : textNametag.hashCode());
		result = prime * result + type;
		result = prime * result + ((x1 == null) ? 0 : x1.hashCode());
		result = prime * result + ((y1 == null) ? 0 : y1.hashCode());
		return result;
	}

	/**
	 * This is a standard override of equals(java.lang.Object)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof GEPoint))
			return false;
		GEPoint other = (GEPoint) obj;
		if (cons == null) {
			if (other.cons != null)
				return false;
		} else if (!cons.equals(other.cons))
			return false;
		if (frozen != other.frozen)
			return false;
		if (hasSetColor != other.hasSetColor)
			return false;
		if (m_radius != other.m_radius)
			return false;
		if (setConstraintIndices == null) {
			if (other.setConstraintIndices != null)
				return false;
		} else if (!setConstraintIndices.equals(other.setConstraintIndices))
			return false;
		if (textNametag == null) {
			if (other.textNametag != null)
				return false;
		} else if (!textNametag.equals(other.textNametag))
			return false;
		if (type != other.type)
			return false;
		if (x1 == null) {
			if (other.x1 != null)
				return false;
		} else if (!x1.equals(other.x1))
			return false;
		if (y1 == null) {
			if (other.y1 != null)
				return false;
		} else if (!y1.equals(other.y1))
			return false;
		return true;
	}

	public void setGEText(GEText ge) {
		textNametag = ge;
	}
	
    public GEText getNametag() {
        if (textNametag == null)
        	textNametag = new GEText(this, 7, -24, GEText.NAME_TEXT);
        
        return textNametag;
    }

    @Override
    public void stopFlash() {
        if (textNametag != null)
            textNametag.stopFlash();
        super.stopFlash();
    }

    @Override
    public void setAsFlashing(boolean flash) {
        super.setAsFlashing(flash);
        if (textNametag != null) {
        	textNametag.setAsFlashing(flash);
        }
    }

    public boolean isAFixedPoint() {
        return x1.Solved && y1.Solved;
    }

    public boolean isAFreePoint() {
        return !x1.Solved && !y1.Solved;
    }

    @Override
    public void setColor(int c) {
        super.setColor(c);
        this.setChosenColor(true);
    }

    @Override
    public boolean isLocatedNear(double x, double y) {
    	assert(x1.value == x1.value);
    	assert(y1.value == y1.value);
    	double dx = getx() - x; // TODO: Convert GEPoint to use Point2D
        dx *= dx;
        double dy = gety() - y;
        dy *= dy;
        
        return (bVisible && dx + dy < UtilityMiscellaneous.PIXEPS_PT2);
    }

    @Override
    public void draw(Graphics2D g2, boolean bSelected) {
        int radius = getRadius();
        assert(x1.value == x1.value);
        assert(y1.value == y1.value);
        int x = (int) getx() - radius;
        int y = (int) gety() - radius;
        if (bSelected) {
        	prepareToBeDrawnAsSelected(g2);
        	g2.drawOval(x, y, 2 * radius, 2 * radius);
        }
        prepareToBeDrawnAsUnselected(g2);
        g2.setColor(getColor());
        g2.fillOval(x, y, 2 * radius, 2 * radius);
        g2.setColor(Color.BLACK);
        g2.drawOval(x, y, 2 * radius, 2 * radius);
    }

    @Override
    public boolean isCoincidentWith(Pointed pointedEntity) {
    	return pointedEntity.isCoincidentWith(this);
    }
    
    @Override
    public boolean isCoincidentWith(ArrayList<GEPoint> pList) {
    	return (pList != null && pList.contains(this));
    }
    
    @Override
    public boolean isFullyCoincidentWith(final ArrayList<GraphicEntity> pList) {
    	return (pList != null && pList.contains(this));
    }
    
    @Override
    public boolean isCoincidentWith(GEPoint p) {
    	return (p != null && this.equals(p));
    }
    
    @Override
    public GEPoint getCommonPoints(Pointed pointedEntity, Collection<GEPoint> collectionPoints) {
    	return pointedEntity.getCommonPoints(this, collectionPoints);
    }
    
    @Override
    public GEPoint getCommonPoints(GEPoint pp, Collection<GEPoint> collectionPoints) {
    	GEPoint pCommon = (this.equals(pp)) ? this : null;
		if (collectionPoints != null)
			collectionPoints.add(pCommon);
		return pCommon;
    }
    
    @Override
    public GEPoint getCommonPoints(ArrayList<GEPoint> pList, Collection<GEPoint> collectionPoints) {
    	GEPoint pCommon = null;
		if (pList != null && pList.contains(this)) {
			pCommon = this;
			if (collectionPoints != null)
				collectionPoints.add(pCommon);
		}
    	return pCommon;
    }
    
    public int POINT_RADIUS = UtilityMiscellaneous.getPointRadius();

    public int getRadius() {
    	assert(x1.value == x1.value);
    	assert(y1.value == y1.value);
    	int radius = m_radius;
        if (radius < 0) {
            if (UtilityMiscellaneous.isApplication())
                radius = UtilityMiscellaneous.getPointRadius();
            else
                radius = POINT_RADIUS;      //APPLET ONLY
        }
        return radius;
    }
    
	public void initializePropertyPanel(JTable table) {
    	assert(x1.value == x1.value);
    	assert(y1.value == y1.value);
    	table.setValueAt(m_name, 0, 1);
		table.setValueAt(new Integer(m_radius), 1, 1);
		table.setValueAt(new Double(PanelProperty.round(getx())), 2, 1);
		table.setValueAt(new Double(PanelProperty.round(gety())), 3, 1);
		table.setValueAt(isFrozen(), 4, 1);
	}
    
    public void setRadius(int r) {
        m_radius = r;
        if (m_radius <= 0)
            m_radius = -1;
        if (m_radius > 30)
        	m_radius = 30;
    }

    /*public void draw(Graphics2D g2) {
        if (!isdraw()) {
            return;
        }
        int x = (int) getx();
        int y = (int) gety();
        int radius = getRadius();

        if (radius <= 1) return;

        if (radius < 3) {
            setDraw(g2);
            g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
            return;
        }
        setDraw(g2);
        g2.setColor(new Color(0, 0, 0));
        g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);


//        g2.setColor(g2.getBackground());
//        g2.fillOval(x - radius + 1, y - radius + 1, 2 * radius - 2, 2 * radius - 2);
        setDraw(g2);
        g2.fillOval(x - radius + 1, y - radius + 1, 2 * radius - 2, 2 * radius - 2);
    }*/

    public void drawA0(Graphics2D g2) {
    	assert(x1.value == x1.value);
    	assert(y1.value == y1.value);
    	if (!isdraw()) {
            return;
        }
        int radius = getRadius();
        int x = (int) getx() - radius;
        int y = (int) gety() - radius;
        prepareToBeDrawnAsUnselected(g2);
        
        // Draw a circle filled in with color and with a black border
        g2.setColor(Color.black);
        g2.fillOval(x, y, 2 * radius, 2 * radius);
        g2.setColor(super.getColor());
        g2.fillOval(x + 1, y + 1, 2 * radius - 2, 2 * radius - 2);
    }

    public void drawWithNametag(Graphics2D g2) {
        this.drawA0(g2);
        if (textNametag != null) {
            textNametag.draw(g2);
        }
    }

    public void draw_ct(Graphics2D g2) {
    	assert(x1.value == x1.value);
    	assert(y1.value == y1.value);
    	prepareToBeDrawnAsUnselected(g2);
        int radius = UtilityMiscellaneous.getPointRadius() + 2;
        int x = (int) (getx() - radius);
        int y = (int) (gety() - radius);

        g2.setColor(Color.white);
        g2.fillOval(x + 1, y + 1, 2 * radius - 2, 2 * radius - 2);

        g2.setColor(Color.black);
        g2.drawOval(x, y, 2 * radius, 2 * radius);
        radius -= 3;
        g2.drawOval(x+3, y+3, 2 * radius, 2 * radius);
    }

    public String TypeString() {
        String s1 = Language.getLs(33, "Point");

        if (m_name == null) {
            return s1;
        }
        return s1 + " " + m_name;
    }

    public String getDescription() {
        if (this.isAFreePoint()) {
            String s1 = Language.getLs(1052, "Free Point");
            return s1 + " " + m_name;
        } else {
            String s1 = Language.getLs(33, "Point");
            return s1 + " " + m_name;
        }
    }

    //////////////////////////////////////////////////////

    public void setColorDefault() {
        if (hasChosenColor()) {
            return;
        }
        if (!x1.Solved && !y1.Solved) {
            m_color = DrawData.pointcolor;
        } else if (x1.Solved && y1.Solved) {
            m_color = DrawData.pointcolor_decided;
        } else {
            m_color = DrawData.pointcolor_half_decided;
        }
    }

    public void addConstraint(Constraint cs) {
    	assert(x1.value == x1.value);
    	assert(y1.value == y1.value);
    	if (cs != null) {
    		if (cons == null)
    			cons = new HashSet<Constraint>();
    		cons.add(cs);
    	}
    }

    public boolean doConstraintsAllowCoordinates(double x, double y) {
    	assert(x1.value == x1.value);
    	assert(y1.value == y1.value);
    	if (cons != null) {
    		for (Constraint cs : cons) {
    			if (!cs.check_constraint(x, y))
    				return false;
    		}
    	}
        return true;
    }

    public boolean hasSameCoordinatesAs(GEPoint p) {
    	assert(x1.value == x1.value);
    	assert(y1.value == y1.value);
    	return (p != null && (p.x1 == this.x1) && (p.y1 == this.y1));
    }

    public GEPoint getPointOtherThan(GEPoint t) {
        return (t == this) ? null : this;
    }
    
    public boolean hasCoordinates(int x, int y) {
    	assert(x1.value == x1.value);
    	assert(y1.value == y1.value);
    	return ((x == this.x1.xindex) && (y == this.y1.xindex));
    }

    public boolean isAtSameLocationAs(double x, double y) {
        return (Math.abs(x - getx()) < UtilityMiscellaneous.ZERO && Math.abs(y - gety()) < UtilityMiscellaneous.ZERO);
    }

    public double getx() {
        if (x1 == null) {
            System.err.println("CPoint error, x1 undefined");
            return -1;
        }
    	assert(x1.value == x1.value);
    	assert(y1.value == y1.value);
    	return x1.value;
    }

    public int getTx() {
        return textNametag.getX();
    }

    public int getTy() {
        return textNametag.getY();
    }

    public double gety() {
        if (y1 == null) {
        	System.err.println("CPoint error, y1 undefined");
            return -1;
        }
    	assert(x1.value == x1.value);
    	assert(y1.value == y1.value);
    	return y1.value;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean r) {
        frozen = r;
    }

    public void setXY(double x, double y) {
        if (x1 == null) {
        	x1 = new param();
        }
        if (y1 == null) {
        	y1 = new param();
        }
        x1.value = x;
        y1.value = y;
    	assert(x1.value == x1.value);
    	assert(y1.value == y1.value);
    }

    public void move(final double dx, final double dy) {
    	if (!frozen) {
        	assert(x1.value == x1.value);
        	assert(y1.value == y1.value);
        	x1.value += dx;
	    	y1.value += dy;
	    	assert(x1.value == x1.value);
	    	assert(y1.value == y1.value);
	    }
    }
    
    public void setFillColor(int index) {
        m_color = index;
    }

    @Override
    public String toString() {
        return m_name;
    }

    public void SavePS_Define_Point(FileOutputStream fp) throws IOException {
        String st = m_name;

        if (st.isEmpty() || st.trim().length() == 0)
            st = "POINT" + m_id;


        String s = '/' + st + " {";
        fp.write(s.getBytes());

        float x1 = (float) (((int) (this.x1.value * 100)) / 100.0);
        float y1 = (float) (((int) (this.y1.value * 100)) / 100.0);

        fp.write((new Float(x1).toString() + ' ').getBytes());

        fp.write((new Float(-y1).toString() +
                "} def \n").getBytes());

    }

    @Override
    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (bVisible == false) {
            return;
        }

        int n = getRadius();
        if (n == 0)
            return;

        String st = m_name;

        if (st.length() == 0 || st.trim().length() == 0)
            st = "POINT" + m_id;

        String s = st + " " + n + " cirfill fill " + st + " " + n + " cir black" + " stroke \n";
        fp.write(s.getBytes());
    }

    public void savePSOriginal(FileOutputStream fp) throws IOException {
        if (bVisible == false) {
            return;
        }

        int n = getRadius();

        String st = m_name;

        if (st.length() == 0 || st.trim().length() == 0)
            st = "POINT" + m_id;

        String s = st + " " + n + " cirfill ";
        fp.write(s.getBytes());
        this.saveSuperColor(fp);
        s = " fill " + st + " " + n + " cir black" + " stroke \n";
        fp.write(s.getBytes());
    }

    @Override
    public Element saveIntoXMLDocument(Element rootElement, String sTypeName) {
    	assert(rootElement != null);
    	if (rootElement != null) {
    		Document doc = rootElement.getOwnerDocument();

    		Element elementThis = super.saveIntoXMLDocument(rootElement, "point");

    		elementThis.setAttribute("x", String.valueOf(x1.xindex));
    		elementThis.setAttribute("y", String.valueOf(y1.xindex));
    		if (m_radius >= 0)
    			elementThis.setAttribute("size", String.valueOf(m_radius));
    		if (frozen)
    			elementThis.setAttribute("frozen", String.valueOf(frozen));
    		if (hasSetColor)
    			elementThis.setAttribute("hasSetColor", String.valueOf(hasSetColor));
    		
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
    
//    @Override
//    public void Save(DataOutputStream out) throws IOException {
//        super.Save(out);
//        out.writeInt(type);
//        out.writeInt(x1.xindex);
//        out.writeInt(y1.xindex);
//        out.writeInt(/*OnCircleOrOnLine*/0);
//        int size = cons.size();
//        out.writeInt(size);
//        for (constraint cs : cons) {
//            if (cs != null)
//                out.writeInt(cs.id);
//            else
//            	out.writeInt(-1);
//        }
//        out.writeBoolean(bVisible);
//        out.writeInt(m_radius);
//        out.writeBoolean(frozen);
//    }

//    @Override
//    public void Load(DataInputStream in, drawProcess dp) throws IOException {
//        if (CMisc.version_load_now < 0.01) {
//            m_id = in.readInt();
//            drawType drawt;
//            if (in.readInt() == 0) {
//                drawt = null;
//            } else {
//                drawt = new drawType();
//                drawt.Load(in);
//                m_color = drawt.color_index;
//                m_dash = drawt.dash;
//                m_width = drawt.width;
//            }
//
//            int len = in.readInt();
//            m_name = new String();
//            for (int i = 0; i < len; i++) {
//                m_name += in.readChar();
//            }
//            type = in.readInt();
//
//            int ix = in.readInt();
//            x1 = dp.getParameterByindex(ix);
//            int iy = in.readInt();
//            y1 = dp.getParameterByindex(iy);
//            /*OnCircleOrOnLine = */
//            in.readInt();
//            int size = in.readInt();
//            for (int i = 0; i < size; i++) {
//                int id = in.readInt();
//                cons.add(dp.getConstraintByid(id));
//            }
//            bVisible = in.readBoolean();
//
//            this.textNametag = new CText(this, 5, -5, CText.NAME_TEXT);
//            dp.addObjectToList(textNametag, dp.textlist);
//
//        } else {
//            super.Load(in, dp);
//
//            type = in.readInt();
//            int ix = in.readInt();
//            x1 = dp.getParameterByindex(ix);
//            int iy = in.readInt();
//            y1 = dp.getParameterByindex(iy);
//            /*OnCircleOrOnLine = */
//            in.readInt();
//            int size = in.readInt();
//            for (int i = 0; i < size; i++) {
//                int id = in.readInt();
//                constraint cs = dp.getConstraintByid(id);
//                addcstoPoint(cs);
//            }
//            bVisible = in.readBoolean();
//            this.setChosenColor(true);
//            if (CMisc.version_load_now >= 0.043)
//                m_radius = in.readInt();
//            else
//                m_radius = -1;// default.
//            if (CMisc.version_load_now >= 0.050)
//                frozen = in.readBoolean();
//        }
//    }

	public boolean hasChosenColor() {
		return hasSetColor;
	}

	public void setChosenColor(boolean b) {
		hasSetColor = b;
	}


}

