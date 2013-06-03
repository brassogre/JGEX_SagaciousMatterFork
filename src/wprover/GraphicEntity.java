package wprover;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * User: Ye
 * Date: 2005-7-8
 */
// class as the baseclass for all geometry items.

abstract public class GraphicEntity {

    final public static int MIN_TYPE = 1;
    final public static int POINT = 1;
    final public static int LINE = 2;
    final public static int CIRCLE = 3;
    final public static int TRACE = 4;
    final public static int DISTANCE = 5;
    final public static int ANGLE = 6;
    final public static int POLYGON = 7;
    final public static int TEXT = 8;
    final public static int TVALUE = 9;
    final public static int PTEXT = 10;
    final public static int EQMARK = 11;
    final public static int TMARK = 12;
    final public static int ARROW = 13;
    final public static int TEMP_POINT = 14;
    final public static int GENERIC_PARAM = 15;
    final public static int MAX_TYPE = 15;

    int m_id;
    int m_type;
    String m_name;
    int m_color;
    
    int m_dash;    //if any
    final private static int MIN_DASH = 0;
    final private static int MAX_DASH = 10;

    int m_width;   //if any
    final private static int MIN_WIDTH = 0;
    final private static int MAX_WIDTH = 20;

    boolean bVisible = true;
    boolean bFlashing = false;
    boolean bIsValidEntity = true;

    
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (bFlashing ? 1231 : 1237);
		result = prime * result + (bIsValidEntity ? 1231 : 1237);
		result = prime * result + (bVisible ? 1231 : 1237);
		result = prime * result + m_color;
		result = prime * result + m_dash;
		result = prime * result + m_id;
		result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
		result = prime * result + m_type;
		result = prime * result + m_width;
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
		if (!(obj instanceof GraphicEntity))
			return false;
		GraphicEntity other = (GraphicEntity) obj;
		if (bFlashing != other.bFlashing)
			return false;
		if (bIsValidEntity != other.bIsValidEntity)
			return false;
		if (bVisible != other.bVisible)
			return false;
		if (m_color != other.m_color)
			return false;
		if (m_dash != other.m_dash)
			return false;
		if (m_id != other.m_id)
			return false;
		if (m_name == null) {
			if (other.m_name != null)
				return false;
		} else if (!m_name.equals(other.m_name))
			return false;
		if (m_type != other.m_type)
			return false;
		if (m_width != other.m_width)
			return false;
		return true;
	}

	abstract public String TypeString();

    abstract public String getDescription();

    abstract public void draw(Graphics2D g2, boolean selected);

    abstract public boolean isLocatedNear(double x, double y);

    abstract public void SavePS(FileOutputStream fp, int stype) throws IOException;

    public GraphicEntity(DrawPanel dp, Element thisElement) {
    	bIsValidEntity = true;
		m_id = DrawPanelFrame.safeParseInt(thisElement.getAttribute("id"), 0);
		m_type = DrawPanelFrame.safeParseInt(thisElement.getAttribute("type"), 0);
		m_name = thisElement.getAttribute("name");
		setDefaultAttributes();
		m_color = DrawPanelFrame.safeParseInt(thisElement.getAttribute("color"), 1);
		m_dash = DrawPanelFrame.safeParseInt(thisElement.getAttribute("dash"), DrawData.dindex);
		m_width = DrawPanelFrame.safeParseInt(thisElement.getAttribute("width"), DrawData.windex);
		bVisible = DrawPanelFrame.safeParseBoolean(thisElement.getAttribute("visible"), true);
		bFlashing = DrawPanelFrame.safeParseBoolean(thisElement.getAttribute("flashing"), false);
		isValid();
	}
    
    public Element saveIntoXMLDocument(Element docElement, String sTypeName) {
		assert(docElement != null);
		if (docElement != null) {
			Document doc = docElement.getOwnerDocument();
			
			if (sTypeName == null)
				sTypeName = "geometric_entity";
			Element elementThis = doc.createElement(sTypeName);
			docElement.appendChild(elementThis);
			
			elementThis.setAttribute("id", String.valueOf(m_id));
			elementThis.setAttribute("type", String.valueOf(m_type));
			if (m_name != null && !m_name.isEmpty())
				elementThis.setAttribute("name", m_name);
			elementThis.setAttribute("color", String.valueOf(m_color));
			if (m_dash != DrawData.dindex)
				elementThis.setAttribute("dash", String.valueOf(m_dash));
			if (m_width != DrawData.windex)
				elementThis.setAttribute("width", String.valueOf(m_width));
			if (!bVisible)
				elementThis.setAttribute("visible", String.valueOf(bVisible));
			if (bFlashing)
				elementThis.setAttribute("flashing", String.valueOf(bFlashing));

			return elementThis;
		}
		return null;
	}
    
    public final int id() {
    	return m_id;
    }
    
    public boolean isValid() {
    	if (bIsValidEntity) {
    		bIsValidEntity &= (m_id > 0);
    		bIsValidEntity &= (m_type >= MIN_TYPE && m_type <= MAX_TYPE);
    		bIsValidEntity &= (m_width >= MIN_WIDTH && m_width <= MAX_WIDTH);
    		bIsValidEntity &= (m_dash >= MIN_DASH && m_dash <= MAX_DASH);
    	}
    	return bIsValidEntity;
    }
    
    public final int get_type() {
        return m_type;
    }

    public void setAsFlashing(boolean flash) {
    	bFlashing = flash;
    }

    public void stopFlash() {
        bFlashing = false;
    }

    public boolean isdraw() {
        return (bVisible || bFlashing);
    }

    public boolean visible() {
        return bVisible;
    }

    public GraphicEntity(GraphicEntity c) {
        m_type = c.m_type;
        m_id = UtilityMiscellaneous.getObjectId();
        m_dash = c.m_dash;
        m_width = c.m_width;
        m_color = c.m_color;
    }

    public GraphicEntity(int type) {
        m_type = type;
        m_id = (type != TEMP_POINT) ? UtilityMiscellaneous.getObjectId() : 0;
        setDefaultAttributes();
    }
    
    private void setDefaultAttributes() {
        m_dash = DrawData.dindex;
        m_width = DrawData.windex;
        m_color = DrawData.pointcolor;

        switch(m_type) {
        	case TEMP_POINT:
        		m_color = DrawData.pointcolor;
                break;
        	case POINT:
        		m_color = DrawData.pointcolor;
        		m_width = 2;
        		break;
        	case ANGLE:
        		m_color = DrawData.anglecolor;
                m_dash = DrawData.angledash;
                m_width = DrawData.anglewidth;
                break;
        	case POLYGON:
                m_color = DrawData.polygoncolor;
                break;
        	case TRACE:
                m_color = DrawData.tractcolor;
                break;
        	case ARROW:
                m_color = 16;
                break;
        	case EQMARK:
                m_width = 3;
                m_color = 3;
                break;
            default:
               	m_color = DrawData.cindex;      	
        };
    }

    public void setAttrAux() {
        m_color = DrawData.RED;
        m_dash = DrawData.DASH8;
        m_width = DrawData.WIDTH2;
    }

    public void copy(GraphicEntity c) {
        if (c != null) {
	        this.m_color = c.m_color;
	        this.m_dash = c.m_dash;
	        this.m_width = c.m_width;
        }
    }

    public void setDash(int d) {
        m_dash = d;
    }

    public void setWidth(int index) {
        m_width = index;
    }

    public String getname() {
        return m_name;
    }

    public boolean hasNameSet() {
        return m_name != null && !m_name.isEmpty();
    }

    public Color getColor() {
        return DrawData.getColor(m_color);
    }

    public int getColorIndex() {
        return m_color;
    }

    public void setColor(int c) {
        m_color = c;
    }

    void move(double dx, double dy) {
    }

    void setVisible(boolean v) {
        bVisible = v;
    }

    public String toString() {
        return m_name;
    }

    /** 
     * Prepares the current graphics context <code>g2</code> to paint this <code>GeometricEntity</code> in
     * a way that designates to the user that <code>this</code> is one of the entities selected by the user.
     * <p>
     * At present, it uses a larger than normal stroke width and CMisc.<code>SelectObjectColor</code> to indicate being selected.
     * 
     * @param g2 The current graphics context
     */
    void prepareToBeDrawnAsSelected(Graphics2D g2) {
        float w = (float) DrawData.getWidth(m_width);
        g2.setStroke(new BasicStroke(w + 5));
        g2.setColor(UtilityMiscellaneous.SelectObjectColor);
    }

    /** 
     * Prepares the current graphics context <code>g2</code> to paint this <code>GeometricEntity</code> in
     * a way that designates to the user that <code>this</code> has not been selected by the user.
     * <p>
     * At present, it uses the default stroke width and color.
     * 
     * @param g2 The current graphics context
     */
    void prepareToBeDrawnAsUnselected(Graphics2D g2) {
        float w = (float) DrawData.getWidth(m_width);
        if (m_dash > 0) {
            float d = (float) DrawData.getDash(m_dash);
            float dash[] = {d};
            g2.setStroke(new BasicStroke(w, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash, 0.0f));
        } else
            g2.setStroke(new BasicStroke(w));

        Color c = DrawData.getColor(m_color);
        if (UtilityMiscellaneous.ColorMode == 1) {
            float gray = (float) (0.11 * c.getRed() + 0.59 * c.getGreen() + 0.3 * c.getBlue()) / 255.0f;
            c = new Color(gray, gray, gray);
        }

        double r = UtilityMiscellaneous.getAlpha();
        Color cc = (r == 1.0) ? c : new Color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, (float) r);
        g2.setPaint(cc);
    }

    public void saveSuperColor(FileOutputStream fp) throws IOException {
        String s = " Color" + new Integer(m_color).toString() + " ";
        fp.write(s.getBytes());
    }

    public void saveSuper(FileOutputStream fp) throws IOException {
        String s = " Color" + new Integer(m_color).toString() + " Dash" + new Integer(m_dash).toString() + " Width" + new Integer(m_width).toString() + " stroke \n";
        fp.write(s.getBytes());
    }

    public static String getPSLineString(int x1, int y1, int x2, int y2) {
        String s = x1 + " " + y1 + " moveto " + x2 + " " + y2 + " lineto ";
        return s;
    }

}
