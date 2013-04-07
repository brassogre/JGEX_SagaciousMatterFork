package wprover;

import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.w3c.dom.*;

public class GETrace extends GraphicEntity {
    private static final int MAX_POINT = 501;
    private final static int MAXLEN = 300;

    private GEPoint point, po;
    private GraphicEntity oObj;

    private int Num = 40;
    private int[] PX, PY;
    private int Radius = 2;
    private boolean dlns;


    public GETrace(GEPoint p) {
        super(GraphicEntity.TRACE);
        m_name = "Trace of " + p;
        PX = new int[MAX_POINT];
        PY = new int[MAX_POINT];
        point = p;
        Num = -1;
    }

    public GETrace(GEPoint p, GEPoint po, GELine o) {
        super(GraphicEntity.TRACE);
        m_name = "Locus of " + p + " when " + po + " is on" + o;
        PX = new int[MAX_POINT];
        PY = new int[MAX_POINT];
        point = p;
        this.po = po;
        oObj = o;
    }

    public GETrace(GEPoint p, GEPoint po, GECircle o) {
        super(GraphicEntity.TRACE);
        m_name = "Locus of " + p + " when " + po + " is on" + o;
        PX = new int[MAX_POINT];
        PY = new int[MAX_POINT];
        point = p;
        this.po = po;
        oObj = o;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Num;
		result = prime * result + Arrays.hashCode(PX);
		result = prime * result + Arrays.hashCode(PY);
		result = prime * result + Radius;
		result = prime * result + (dlns ? 1231 : 1237);
		result = prime * result + ((oObj == null) ? 0 : oObj.hashCode());
		result = prime * result + ((po == null) ? 0 : po.hashCode());
		result = prime * result + ((point == null) ? 0 : point.hashCode());
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
		if (!(obj instanceof GETrace))
			return false;
		GETrace other = (GETrace) obj;
		if (Num != other.Num)
			return false;
		if (!Arrays.equals(PX, other.PX))
			return false;
		if (!Arrays.equals(PY, other.PY))
			return false;
		if (Radius != other.Radius)
			return false;
		if (dlns != other.dlns)
			return false;
		if (oObj == null) {
			if (other.oObj != null)
				return false;
		} else if (!oObj.equals(other.oObj))
			return false;
		if (po == null) {
			if (other.po != null)
				return false;
		} else if (!po.equals(other.po))
			return false;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		return true;
	}

	public boolean isTracePt(GEPoint pt) {
        return point == pt && po == null && oObj == null;
    }

    public void setDLns(boolean r) {
        dlns = r;
    }

    public boolean isDrawLines() {
        return dlns;
    }

    public void setNumPts(int n) {
    	Num =  (n < MAX_POINT) ? n : MAX_POINT;
    }

    public void draw(Graphics2D g2, boolean selected) {
        if (!isdraw()) return;

        int radius = Radius;

        if (selected) {
            g2.setColor(UtilityMiscellaneous.SelectObjectColor);
            g2.setStroke(UtilityMiscellaneous.SelectObjectStroke);
            radius = Radius + 2;
        } else
            prepareToBeDrawnAsUnselected(g2);

        for (int i = 0; i < Num; i++) {
            if (!dlns)
                g2.fillOval(PX[i] - radius / 2, PY[i] - radius / 2, radius, radius);
            if (dlns) {
                if (oObj != null && oObj.get_type() == GraphicEntity.CIRCLE)
                    drawALN(PX[i], PY[i], PX[(i + 1) % Num], PY[(i + 1) % Num], g2);
                else if (i < Num - 1)
                    drawALN(PX[i], PY[i], PX[(i + 1) % Num], PY[(i + 1) % Num], g2);
            }
        }
    }

    public static void drawALN(int x, int y, int x1, int y1, Graphics2D g2) {

        if ((x1 < 0 || x1 > 1000) && (x < 0 || x > 1000))
            return;

        if ((y1 < 0 || y1 > 1000) && (y < 0 || y > 1000))
            return;

        int dx = x - x1;
        int dy = y - y1;
        if(dx > MAXLEN || dx < - MAXLEN || dy > MAXLEN || dy < -MAXLEN)
            return;

        g2.drawLine(x, y, x1, y1);
    }

//    public void draw(Graphics2D g2) {
//        draw(g2, false);
//    }

    public String TypeString() {
        if (m_name == null) return "Trace";
        return "Trace " + m_name;
    }

    public String getDescription() {
        return "Trace " + point.TypeString();
    }

    public boolean isLocatedNear(double x, double y) {
        if (!isdraw()) return false;
        double r2 = UtilityMiscellaneous.PIXEPS * UtilityMiscellaneous.PIXEPS;

        for (int i = 0; i < Num; i++)
            if (Math.pow(PX[i] - x, 2) + Math.pow(PY[i] - y, 2) < r2)
                return true;
        return false;
    }

    public void move(double dx, double dy) {
        for (int i = 0; i < Num; i++) {
            PX[i] += dx;
            PY[i] += dy;
        }
    }

    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (!bVisible) return;

         for (int i = 0; i < Num; i++) {
                    if (dlns) {
                if (oObj != null && oObj.get_type() == GraphicEntity.CIRCLE || i < Num -1)
                {

                    int pos1x = PX[i];
                    int pos1y = PY[i];
                    int pos2x = PX[(i + 1) % Num];
                    int pos2y = PY[(i + 1) % Num];


                        String st1 = pos1x + " " + -pos1y + " moveto " + pos2x + " " + -pos2y + " lineto \n";
                        fp.write(st1.getBytes());
                        String st3 = "Color" + m_color + " stroke\n";
                        fp.write(st3.getBytes());
                }

            }
        }
    }

    public GETrace(DrawPanel dp, final Element thisElement, Map<Integer, GraphicEntity> mapGE) {
    	super(dp, thisElement);

		Num = DrawPanelFrame.safeParseInt(thisElement.getAttribute("number"), 40, 10, 1000);
		Radius = DrawPanelFrame.safeParseInt(thisElement.getAttribute("radius"), 2);
		dlns = DrawPanelFrame.safeParseBoolean(thisElement.getAttribute("draw_lines"), true);
		
		int index = DrawPanelFrame.safeParseInt(thisElement.getAttribute("oObj"), 0);
		GraphicEntity ge = mapGE.get(index);
		po = null;
		oObj = null;
		if (ge != null) {
			if (ge instanceof GEPoint)
				po = (GEPoint)ge;
			else
				oObj = ge;
		}
		if (ge == null)
			bIsValidEntity = false;
		
		index = DrawPanelFrame.safeParseInt(thisElement.getAttribute("point"), 0);
		ge = mapGE.get(index);
		if (ge != null && ge instanceof GEPoint)
			point = (GEPoint)ge;
		else
			bIsValidEntity = false;
		
        PX = new int[MAX_POINT];
        PY = new int[MAX_POINT];

        NodeList elist = thisElement.getChildNodes();		
		for (int i = 0; i < elist.getLength(); ++i) {
			Node nn = elist.item(i);
            if (nn != null) {
            	String s = nn.getNodeName();
            	if (s.equalsIgnoreCase("trace_points")) {
            		NodeList e2list = nn.getChildNodes();		
            		for (int i2 = 0; i2 < e2list.getLength() && i2 < MAX_POINT; ++i2) {
            			Node nn2 = e2list.item(i);
                        if (nn2 != null) { 
                        	String s2 = nn2.getNodeName();
                        	if (s2.equalsIgnoreCase("trace_location")) {
                        		PX[i2] = DrawPanelFrame.safeParseInt(thisElement.getAttribute("x"), 0);
                        		PY[i2] = DrawPanelFrame.safeParseInt(thisElement.getAttribute("y"), 0);
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

    		Element elementThis = super.saveIntoXMLDocument(rootElement, "trace");

    		elementThis.setAttribute("number", String.valueOf(Num));
    		elementThis.setAttribute("radius", String.valueOf(Radius));
    		elementThis.setAttribute("draw_lines", String.valueOf(dlns));
    		if (po != null)
    			elementThis.setAttribute("oObj", String.valueOf(po.m_id));
    		if (oObj != null)
    			elementThis.setAttribute("oObj", String.valueOf(oObj.m_id));
    		if (point != null)
    			elementThis.setAttribute("point", String.valueOf(point.m_id));

    		Element ePoints = doc.createElement("trace_points");
    		elementThis.appendChild(ePoints);

            for (int i = 0; i < Num; i++) {
            	Element e = doc.createElement("trace_location"+String.valueOf(i));
            	ePoints.appendChild(e);
            	e.setAttribute("x", String.valueOf(PX[i]));
            	e.setAttribute("y", String.valueOf(PY[i]));
            }
    		return elementThis;
    	}
    	return null;
    }

//    public void Save(DataOutputStream out) throws IOException {
//        super.Save(out);
//        out.writeInt(point.m_id);
//        out.writeInt(Num);
//        for (int i = 0; i < Num; i++) {
//            out.writeInt(PX[i]);
//            out.writeInt(PY[i]);
//        }
//
//        int oid, mid;
//        oid = mid = -1;
//        if (po != null)
//            oid = po.m_id;
//        if (oObj != null)
//            mid = oObj.m_id;
//        out.writeInt(oid);
//        out.writeInt(mid);
//
//        out.writeInt(Num);
//        out.writeInt(Radius);
//        out.writeBoolean(dlns);
//    }
//
//    public void Load(DataInputStream in, drawProcess dp) throws IOException {
//        super.Load(in, dp);
//        if (CMisc.version_load_now >= 0.011) {
//            int id = in.readInt();
//            point = dp.getPointById(id);
//        }
//        Num = in.readInt();
//        for (int i = 0; i < Num; i++) {
//            PX[i] = in.readInt();
//            PY[i] = in.readInt();
//        }
//        if (CMisc.version_load_now >= 0.044) {
//            int id = in.readInt();
//            po = dp.getPointById(id);
//            id = in.readInt();
//            oObj = dp.getObjectById(id);
//            Num = in.readInt();
//            Radius = in.readInt();
//            dlns = in.readBoolean();
//        }
//    }

    ///////////////////////////////////////////////////////


    public void setTracePoint(int i, double x, double y) {
    	assert(i >= 0 && i < Num);
        if (i >= 0 && i < Num) {
        	PX[i] = (int) x;
	        PY[i] = (int) y;	        
	        soft(i);
        }
    }

//    public void addTracePoint(int x, int y, int i) {
//        PX[i] = x;
//        PY[i] = y;
//        soft(i);
//    }

    public void addTracePoint(int x, int y) {

        for (int i = 0; i <= Num; i++)
            if (PX[i] == x && PY[i] == y)
                return;
        if (Num >= MAX_POINT - 1)
            return;

        Num++;
        PX[Num] = x;
        PY[Num] = y;
    }


    public void softEdge() {

        for (int i = 2; i < Num - 1; i++) {
            int x = PX[i];
            int y = PY[i];

            int x0 = PX[i + 1];
            int y0 = PY[i + 1];

            int x1 = PX[i - 1];
            int y1 = PY[i - 1];

            int mx = Math.abs(x1 - x0);
            int my = Math.abs(y1 - y0);


            if (Math.abs(x - x0) > mx && Math.abs(x - x1) > mx
                    || Math.abs(y - y0) > my && Math.abs(y - y1) > my) {
                PX[i] = (x0 + x1) / 2;
                PY[i] = (y0 + y1) / 2;
            }
        }
    }

    public void soft(int i) {
    }


    public void trans(double dx, double dy) {
        for (int i = 0; i < Num; i++) {
            PX[i] += dx;
            PY[i] += dy;
        }
    }

    public double Roud_length() {
        if (Num == 0) return 0.0d;

        double len = 0;
        int x, y;
        x = y = 0;

        for (int i = 0; i < Num; i++) {
            if (i != 0)
                len += Math.sqrt((x - PX[i]) * (x - PX[i]) + (y - PY[i]) * (y - PY[i]));
            x = PX[i];
            y = PY[i];
        }
        len += Math.sqrt((x - PX[0]) * (x - PX[0]) + (y - PY[0]) * (y - PY[0]));
        return len;
    }

    int getPtxi(int i) {
        return PX[i];
    }

    int getPtyi(int i) {
        return PY[i];
    }

    public GEPoint getPoint() {
        return point;
    }

    public GEPoint getonPoint() {
        return po;
    }

    public GraphicEntity getOnObject() {
        return oObj;
    }

    public int getPointSize() {
        return Num;
    }


}
