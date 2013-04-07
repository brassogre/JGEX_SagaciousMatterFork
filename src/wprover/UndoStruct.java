package wprover;

import java.awt.Graphics2D;
import java.io.*;
import java.util.ArrayList;

import maths.TPoly;

/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2005-8-11
 * Time: 9:34:44
 * To change this template use File | Settings | File Templates.
 */

class UndoStruct {
    static public int INDEX = 0;

    final public static int T_UNDO_NODE = 0;
    final public static int T_TO_PROVE_NODE = 1;
    final public static int T_PROVE_NODE = 2;

    final public static int T_COMBINED_NODE = 3;

    final public static int T_ROOT = 98;
    final public static int T_PLAIN_TEXT = 99;


    public UndoStruct() {
    }

    public UndoStruct(int pc) {
        m_id = INDEX++;

        m_type = T_UNDO_NODE;
        paraCounter = pc;
        id = UtilityMiscellaneous.id_count;
    }

    public UndoStruct(int type, int pc) {
        m_id = INDEX++;
        m_type = type;
        paraCounter = pc;
        id = UtilityMiscellaneous.id_count;
    }

    int m_id;

    int m_type;
    boolean done = false;
    boolean flash = false;
    int action;
    int id = 0;
    int current_id = 0;
    int paraCounter = 1;
    int pnameCounter = 0;
    int plineCounter = 0;
    int pcircleCounter = 0;

    String msg = new String(); // information about this step;

    int id_b = 0;
    int paraCounter_b = 1;
    int pnameCounter_b = 0;
    int plineCounter_b = 0;
    int pcircleCounter_b = 0;


    //////////////////////////////////////////////////////
    TPoly polylist = null;
    TPoly pblist = null;

    ArrayList<GEPoint> pointlist = new ArrayList<GEPoint>();
    ArrayList<GELine> linelist = new ArrayList<GELine>();
    ArrayList<GECircle> circlelist = new ArrayList<GECircle>();
    ArrayList<GEAngle> anglelist = new ArrayList<GEAngle>();
    ArrayList<Constraint> constraintlist = new ArrayList<Constraint>();
    ArrayList<GEDistance> distancelist = new ArrayList<GEDistance>();
    ArrayList<GEPolygon> polygonlist = new ArrayList<GEPolygon>();
    ArrayList<GEText> textlist = new ArrayList<GEText>();
    ArrayList<GETrace> tracelist = new ArrayList<GETrace>();
    ArrayList<GraphicEntity> otherlist = new ArrayList<GraphicEntity>();

    ////////////////////////////
    ArrayList<GraphicEntity> objectlist = new ArrayList<GraphicEntity>(); // object related to this node.
    ArrayList<UndoStruct> childundolist = new ArrayList<UndoStruct>();

    ////////////////////////////////////////////// added 2006.7.8.
    ArrayList<Object> dlist = new ArrayList<Object>();

    public void adddOjbect(Object obj) {
        dlist.add(obj);
    }

    @Override
    public String toString() {
        if (dlist.isEmpty()) {
            return msg;
        } else {
            if (action == DrawPanelBase.D_PARELINE) {
                GELine ln1 = ((GELine) dlist.get(0));
                GELine ln2 = ((GELine) dlist.get(1));
                GEPoint p = ((GEPoint) dlist.get(2));
                return ln1.getDescription2() + " paral " + ln2.getDescription2() +
                        " passing " + p.getname();

            } else if (action == DrawPanelBase.D_PERPLINE) {
                GELine ln1 = ((GELine) dlist.get(0));
                GELine ln2 = ((GELine) dlist.get(1));
                GEPoint p = ((GEPoint) dlist.get(2));
                return ln1.getDescription2() + " perp " + ln2.getDescription2() +
                        " passing " + p.getname();
            } else {
                return msg;
            }
        }
    }

    public void clear() {
        polylist = pblist = null;
        pointlist.clear();
        linelist.clear();
        circlelist.clear();
        anglelist.clear();
        distancelist.clear();
        constraintlist.clear();
        textlist.clear();
        polygonlist.clear();
        otherlist.clear();

    }

    public void draw(Graphics2D g2) {
        drawlist(polygonlist, g2);
        drawlist(tracelist, g2);
        drawlist(distancelist, g2);
        drawlist(anglelist, g2);
        drawlist(linelist, g2);
        drawlist(circlelist, g2);
        drawlist(pointlist, g2);
        drawlist(textlist, g2);
        drawlist(otherlist, g2);
    }

    public UndoStruct getUndoStructByid(int id) {
        if (this.m_id == id) {
            return this;
        } else {
            for (int i = 0; i < childundolist.size(); i++) {
                UndoStruct un = childundolist.get(i);
                UndoStruct u1 = un.getUndoStructByid(id);
                if (u1 != null) {
                    return u1;
                }
            }
            return null;
        }
    }

    public void setInFlashing(boolean inflash) {
        setListInFlashing(objectlist, inflash);
    }

    private static void setListInFlashing(ArrayList<GraphicEntity> v, boolean inflash) {
        for (GraphicEntity cc : v) {
            cc.setAsFlashing(inflash);
        }
    }

    private static void drawlist(ArrayList<?> v, Graphics2D g2) {
		if (v != null) {
		    for (Object o : v) {
			if (o != null && o instanceof GraphicEntity)
			    ((GraphicEntity)o).draw(g2, false);
		    }
		}
    }

    public void addRelatedObject(GraphicEntity cc) {
        if (!objectlist.contains(cc)) {
            objectlist.add(cc);
        }
    }

    public boolean isNodeValued() {
        // return pointlist.size() != 0 ;
        if (m_id - id_b != 0) return true;

        return pnameCounter - pnameCounter_b != 0
                || plineCounter_b - plineCounter != 0
                || plineCounter_b - plineCounter != 0;
    }


    public void merge(UndoStruct undo1, UndoStruct undo2) {

        action = -1; // combined.
        msg = "cb";

        id = undo1.id;
        current_id = undo1.current_id;
        paraCounter = undo1.paraCounter;
        pnameCounter = undo1.pnameCounter;
        plineCounter = undo1.plineCounter;
        pcircleCounter = undo1.pcircleCounter;

        id_b = undo2.id_b;
        paraCounter_b = undo2.paraCounter_b;
        pnameCounter_b = undo2.pnameCounter_b;
        plineCounter_b = undo2.plineCounter_b;
        pcircleCounter_b = undo2.pcircleCounter_b;

    }

    public void addchild(UndoStruct u) {
        if (!childundolist.contains(u)) {
            childundolist.add(u);
        }
    }

    public void setObjectList(ArrayList<GraphicEntity> v) {
        objectlist.clear();
		if (v != null ) {
		    for (GraphicEntity cc : v) {
				if (!objectlist.contains(cc)) {
				    objectlist.add(cc);
				}
		    }
		}
    }

    public void getObjectList(ArrayList<GraphicEntity> v) {
	if (v != null ) {
	    v.addAll(objectlist);
	}
    }

    public void getAllObjects(DrawPanel dp, ArrayList<GraphicEntity> v) {

        if (this.m_type == T_UNDO_NODE) {
            DrawPanel.selectUndoObjectFromList(v, dp.pointlist, id, id_b);
            DrawPanel.selectUndoObjectFromList(v, dp.linelist, id, id_b);
            DrawPanel.selectUndoObjectFromList(v, dp.circlelist, id, id_b);
            DrawPanel.selectUndoObjectFromList(v, dp.anglelist, id, id_b);
            DrawPanel.selectUndoObjectFromList(v, dp.distancelist, id, id_b);
            DrawPanel.selectUndoObjectFromList(v, dp.polygonlist, id, id_b);
            DrawPanel.selectUndoObjectFromList(v, dp.textlist, id, id_b);
            DrawPanel.selectUndoObjectFromList(v, dp.tracelist, id, id_b);
            DrawPanel.selectUndoObjectFromList(v, dp.otherlist, id, id_b);

        } else if (this.m_type == UndoStruct.T_COMBINED_NODE) {
            for (int i = 0; i < childundolist.size(); i++) {
                UndoStruct un = childundolist.get(i);
                un.getAllObjects(dp, v);
            }
        } else if (this.m_type == UndoStruct.T_PROVE_NODE) {
            for (int i = 0; i < childundolist.size(); i++) {
                UndoStruct un = childundolist.get(i);
                un.getAllObjects(dp, v);
            }
        }

        v.addAll(objectlist);
    }

    public static void SaveList(DataOutputStream out, ArrayList<? extends GraphicEntity> v) throws IOException {
        int n = v.size();
//        if (n > 999) {
//            int k = 0;
//        }
        out.writeInt(n);
        for (GraphicEntity cc : v)
            out.writeInt(cc.m_id);
    }

    public static void ReadList(DataInputStream in, DrawPanel dp, ArrayList<GraphicEntity> list) throws IOException {
        int size = in.readInt();

        for (int i = 0; i < size; i++) {
            int iii = in.readInt();
            GraphicEntity cc = dp.getObjectById(iii);
            if (cc != null) {
                list.add(cc);
            }
        }
    }

//    public void saveIntoXMLDocument(Element rootElement) {
//    	assert(rootElement != null);
//    	if (rootElement != null) {
//    		Document doc = rootElement.getOwnerDocument();
//
//    		Element elementThis = doc.createElement("undo_structure");
//
//    		elementThis.setAttribute("m_id", String.valueOf(m_id));
//    		elementThis.setAttribute("id", String.valueOf(id));
//    		elementThis.setAttribute("current_id", String.valueOf(current_id));
//    		elementThis.setAttribute("type", String.valueOf(m_type));
//    		elementThis.setAttribute("done", String.valueOf(done));
//    		elementThis.setAttribute("flash", String.valueOf(flash));
//    		elementThis.setAttribute("action", String.valueOf(action));
//
//    		Element ee = doc.createElement("parameters");
//    		elementThis.appendChild(ee);
//
//    		for (Param param : parameterlist) {
//    			if (p != null)
//    				ePoints.appendChild( doc.createTextNode(String.valueOf(p.m_id)) );
//    		}
//
//    		if (childundolist != null && !childundolist.isEmpty()) {
//    	        for (UndoStruct uu : childundolist) {
//    				if (uu != null) {
//    					uu.saveIntoXMLDocument(elementThis);
//    				}
//    			}
//    		}
//    		return elementThis;
//    	}
//    	return null;
//    }
    
    public void Save(DataOutputStream out) throws IOException {

        out.writeInt(m_id);
        out.writeInt(m_type);
        out.writeBoolean(done);
        out.writeBoolean(flash);
        out.writeInt(action);

        out.writeInt(id);
        out.writeInt(current_id);
        out.writeInt(paraCounter);
        out.writeInt(pnameCounter);
        out.writeInt(plineCounter);
        out.writeInt(pcircleCounter);

        // for 0.11
        int n = 0;
        if (msg != null) {
            byte[] b = msg.getBytes();
            n = b.length;
            out.writeInt(n);
            if (n > 0)
                out.write(b);
        } else
            out.writeInt(n);

//        if (msg != null && n > 0) {
//            byte[] b = msg.getBytes();
//            out.write(b);
//        }

        out.writeInt(id_b);
        out.writeInt(paraCounter_b);
        out.writeInt(pnameCounter_b);
        out.writeInt(plineCounter_b);
        out.writeInt(pcircleCounter_b);

        SaveList(out, objectlist);

        out.writeInt(childundolist.size());

        if (childundolist.size() != 0) {
            for (int i = 0; i < childundolist.size(); i++) {
                UndoStruct u = childundolist.get(i);
                u.Save(out);
            }
        }
    }

    public void Load(DataInputStream in, DrawPanel dp) throws IOException {
        if (UtilityMiscellaneous.version_load_now >= 0.019) {
            m_id = in.readInt();

        } else {
            m_id = UtilityMiscellaneous.id_count++;
        }

        if (UtilityMiscellaneous.version_load_now >= 0.015) {
            m_type = in.readInt();
        }
        if (UtilityMiscellaneous.version_load_now > 0.01) {
            this.done = in.readBoolean();
            this.flash = in.readBoolean();
            action = in.readInt();
        }

        if (UtilityMiscellaneous.version_load_now < 0.01) {
            int size = in.readInt();
            byte[] str = new byte[size];
            in.read(str, 0, size);
        }
        id = in.readInt();
        current_id = in.readInt();
        paraCounter = in.readInt();
        pnameCounter = in.readInt();
        plineCounter = in.readInt();
        pcircleCounter = in.readInt();

        // for 0.01
        if (UtilityMiscellaneous.version_load_now > 0.010) {
            int size = in.readInt();
            if (size > 0) {
                byte[] str = new byte[size];
                in.read(str, 0, str.length);
                msg = new String(str);
            }
        }

        id_b = in.readInt();
        paraCounter_b = in.readInt();
        pnameCounter_b = in.readInt();
        plineCounter_b = in.readInt();
        pcircleCounter_b = in.readInt();

        if (UtilityMiscellaneous.version_load_now >= 0.016) {
	    ReadList(in, dp, objectlist);
        }

        if (UtilityMiscellaneous.version_load_now >= 0.012) {
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                UndoStruct u = new UndoStruct(-1, -1);
                u.Load(in, dp);
                childundolist.add(u);
            }
            if (m_type == T_UNDO_NODE && childundolist.size() > 0) {
                m_type = T_COMBINED_NODE;
            }
        }
    }
}
