package wprover;

import gprover.cond;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.w3c.dom.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2005-8-23
 * Time: 21:54:51
 * To change this template use File | Settings | File Templates.
 */
public class ProofText {
    final private static int HSpace = 4;
    private static int D_ROW = 0;
    private static Image arrow;
    private int m_row = -1;
    private boolean isMouseOnArrow = false;
    private double ax, ay;

    private static JPanel d;

    private String head;
    private String msg;
    private String rule = "";
    private String rpath = "";

    private Font font = null;
    private double x = 0;
    private double y = 0;

    final private static Color defaultHeaderColor = Color.blue;
    final private static Color defaultMessageColor = Color.black;
    private Color chead = defaultHeaderColor;
    private Color cmsg = defaultMessageColor;
    
    private double height;
    private double width;
    private double whead;

    final private static boolean defaultVisible = true;
    final private static boolean defaultExpanded = false;
    private boolean visible = defaultVisible;
    private boolean isexpand = defaultExpanded;

    private ProofField proofFieldNested;

    private UndoStruct m_undo = null;
    private cond m_co = null;
    private String bidx = "";

    public ProofText() {
        head = "";
        msg = "";
    }

    public ProofText(String s1, String s2) {
        head = s1;
        msg = s2;
    }

    public ProofText(UndoStruct un) {
        head = "";
        msg = un.msg;
        if ((un.m_type == UndoStruct.T_COMBINED_NODE || un.m_type == UndoStruct.T_PROVE_NODE) && !un.childundolist.isEmpty()) {
            proofFieldNested = new ProofField(un.childundolist);
        }
        m_undo = un;
    }

    public ProofText(ArrayList<cond> vvv, cond co, int index, boolean gc) {
        m_co = co;

        if (index >= 0)
            head = new Integer(index + 1).toString();
        else
            head = "";

        if (index >= 0) {
            if (index < 10)
                head += "  ";
            else
                head += " ";
        }
        int n = co.getNo();
        cond c = co.getPCO();
        boolean cons = true;
        ArrayList<cond> vv = new ArrayList<cond>();
        while (c != null) {
            if (c.getNo() != 0) {
                cons = false;
                break;
            }
            vv.add(c);
            c = c.nx;
        }
        if (co.getPCO() == null) {
            msg = co.getText();
        } else if (cons) {
            msg = co.getText();
            cmsg = defaultMessageColor;
            proofFieldNested = new ProofField(vv, false);
        } else if (n > 0) {
            msg = "Hence " + co.getText();
            cmsg = defaultMessageColor;

            cond tc = co.getPCO();
            String dix = "  by ";
            int nco = 0;
            while (tc != null) {
                int j;
                if (tc.getNo() != 0)
                    for (j = 0; j < vvv.size(); j++) {
                        cond c1 = vvv.get(j);
                        if (tc.getNo() == c1.getNo())
                            break;
                    }
                else {
                    int k = vvv.indexOf(co);
                    for (j = k; j >= 0; j--)
                        if (vvv.get(j) == tc)
                            break;
                }
                dix += (j + 1);
                nco++;
                tc = tc.nx;
                if (tc != null)
                    dix += ",";

            }
            if (nco > 1) {
                //dix += ")";
                bidx = "   " + dix;
                msg += bidx;
            }
        } else {
            msg = co.getText();
        }
    }

    public ProofText(UndoStruct un, int index) {
        head = new Integer(index + 1).toString() + ":  ";
        msg = un.msg;
        if ((un.m_type == UndoStruct.T_COMBINED_NODE || un.m_type == UndoStruct.T_PROVE_NODE) && !un.childundolist.isEmpty()) {
            proofFieldNested = new ProofField(un.childundolist);
        }
        m_undo = un;
    }

    public void loadFromXMLDocument(Element thisElement) {
    	if (thisElement.getTagName().equalsIgnoreCase("proof_text")) {
	    	head = thisElement.getAttribute("head");
	    	rule = thisElement.getAttribute("rule");
	    	rpath = thisElement.getAttribute("rulepath");
	    	visible = DrawPanelFrame.safeParseBoolean(thisElement.getAttribute("visible"), defaultVisible);
	    	isexpand = DrawPanelFrame.safeParseBoolean(thisElement.getAttribute("expanded"), defaultExpanded);
	    	cmsg = DrawPanelFrame.safeParseColor(thisElement.getAttribute("head_color"), defaultHeaderColor);
	    	cmsg = DrawPanelFrame.safeParseColor(thisElement.getAttribute("message_color"), defaultMessageColor);
	    	x = DrawPanelFrame.safeParseDouble(thisElement.getAttribute("x"), 0);
	    	y = DrawPanelFrame.safeParseDouble(thisElement.getAttribute("y"), 0);
	    	
	    	NodeList nl = thisElement.getChildNodes();
	    	if (nl != null) {
	    		for (int i = 0; i < nl.getLength(); ++i) {
	    			Node nn = nl.item(i);
	    			if (nn instanceof Element) {
	    				Element ee = (Element)nn;
	    				if (ee.getTagName().equalsIgnoreCase("proof")) {
	    					proofFieldNested = new ProofField(ee);
	    					return;
	    				}
	    			}
	    		}
	    	}
    	}
    }

	public void SaveIntoXMLDocument(Element rootElement) {
		assert(rootElement != null);
		if (rootElement != null) {
			Document doc = rootElement.getOwnerDocument();

			Element elementThis = doc.createElement("proof_text");
			rootElement.appendChild(elementThis);

    		elementThis.setAttribute("head", head);
    		if (!rule.isEmpty())
    			elementThis.setAttribute("rule", rule);
    		if (!rpath.isEmpty())
    			elementThis.setAttribute("rulepath", rpath);
    		if (chead != defaultHeaderColor)
    			elementThis.setAttribute("head_color", String.valueOf(chead.getRGB()));
    		if (cmsg != defaultMessageColor)
    			elementThis.setAttribute("message_color", String.valueOf(cmsg.getRGB()));
    		if (visible != defaultVisible)
    			elementThis.setAttribute("visible", String.valueOf(visible));
    		if (isexpand != defaultExpanded)
    			elementThis.setAttribute("expanded", String.valueOf(isexpand));
    		if (x != 0 || y != 0) {
    			elementThis.setAttribute("x", String.valueOf(x));
    			elementThis.setAttribute("y", String.valueOf(y));
    		}
    		
    		if (proofFieldNested != null) {
				Element elementProveField = doc.createElement("proof");
				elementThis.appendChild(elementProveField);
				proofFieldNested.SaveIntoXMLDocument(elementProveField);
    		}
		}
	}
    
    
    
    
    
    
    
    public static void setArrowImage(Image ico) {
        arrow = ico;
    }

    public static void setDrawPanel(JPanel panel) {
        d = panel;
    }

    public void setExpanded(boolean b) {
        isexpand = b;
    }

    public boolean isExpanded() {
        return isexpand;
    }

    public cond getcond() {
        return m_co;
    }

    public void setFontSize(int size) {
        font = new Font(font.getName(), font.getStyle(), size);
        if (proofFieldNested != null)
            proofFieldNested.setFontSize(size);
    }


    public void setIndex(int index) {
        head = new Integer(index + 1).toString() + ":  ";
        if (proofFieldNested != null)
            proofFieldNested.reGenerateIndex();
    }

    public void setVisible(boolean v) {
        visible = v;
    }

    public boolean isVisible() {
        return visible;
    }

	public static void resetRow() {
        D_ROW = 0;
    }

    public static int getRow() {
        return D_ROW;
    }

    public UndoStruct getUndoStruct() {
        return m_undo;
    }

    public Rectangle getRectangle() {
        return new Rectangle((int) x, (int) y, (int) width, (int) height);
    }

    public Color getCaptainColor() {
        return chead;
    }

    public void setRule(String r) {
        rule = r;
    }

    public String getRule() {
        return rule;
    }

    public void setRulePath(String path) {
        rpath = path;
    }

    public String getRulePath() {
        return rpath;
    }

    public void setCaptainColor(Color c) {
        chead = c;
    }

    public Color getMessageColor() {
        return cmsg;
    }

    public void setMessageColor(Color c) {
        cmsg = c;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font f) {
        font = f;
    }

    public String getHead() {
        return head;
    }
 
    public void setHead(String s) {
        head = s;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String s) {
        msg = s + "  " + this.bidx;
    }

    public void getObjectList(ArrayList<GraphicEntity> list) {
        if (m_undo != null)
        	m_undo.getObjectList(list);
    }

    public void setObjectList(ArrayList<GraphicEntity> list) {
        if (m_undo != null) {
            m_undo.setObjectList(list);
        }
    }

    public void setWidth(double ww) {
        width = ww;
    }

    public double getWidth() {
        return width;
    }

    public double getHeadLength() {
        return whead;
    }

    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String TypeString() {
        return "proof_text";
    }

    public String getDescription() {
        return this.TypeString();
    }

    public ProofText selectChild(double x1, double y1, boolean onselect) {
        if (proofFieldNested != null)
            return proofFieldNested.select(x1, y1, onselect);
        return null;
    }

    public void clearSelection() {
        if (proofFieldNested != null)
            proofFieldNested.clearSelection();
    }

    public void expandAll() {
        if (this.isexpand)
            this.setExpanded(false);
        else
            this.setExpanded(true);

        if (proofFieldNested != null)
            proofFieldNested.expandAll();
    }

    public void toggleExpanded() {
        setExpanded(!isexpand);
    }

    public ProofText redo_invisible_head(DrawPanel dp) {
        if (proofFieldNested == null) return this;
        if (!this.isexpand) return this;

        ProofText ct = proofFieldNested.redo_invisible_head(dp);
        if (ct == null)
            return this;
        else
            return ct;
    }

    public void regenerateAll() {
        if (m_undo != null) {
            this.msg = m_undo.msg;
        }
    }

    public void getFlashObjectList(ArrayList<GraphicEntity> v, DrawPanel dp) {
        if (m_undo.m_type != UndoStruct.T_PROVE_NODE) {
            m_undo.getAllObjects(dp, v);
            return;
        }

        if (isexpand) {
            m_undo.getObjectList(v);
        } else
            m_undo.getAllObjects(dp, v);
    }

//    public CProveText next(drawProcess dp)
//    {
//

//    }

    public ProofText findPText(UndoStruct un) {
        if (un == null)
            return null;
        if (un == m_undo)
            return this;
        if (proofFieldNested == null)
            return null;

        return proofFieldNested.findPText(un);
    }


    public ProofText next_prove_step(DrawPanel dp, ProofText cpt, Boolean find) {

        if (!find) {
            if (cpt == this) {
                find = true; // XXX Code here needs to be checked for whether it obeys the correct logic.
                // It previously used a Boolean object whose value could be changed from within this function.
                // This is the only line that ever changed its value.
                if (this.visible) {
                    if (proofFieldNested != null && isexpand)
                        return proofFieldNested.next_prove_step(dp, cpt, find);

                    return null;
                } else
                    return null;
            } else {
                if (proofFieldNested != null)
                	return proofFieldNested.next_prove_step(dp, cpt, find);
                return null;
            }
        } else {
            if (this.visible) {
                if (!this.isexpand || m_undo.m_type == UndoStruct.T_UNDO_NODE)//||m_undo.m_type ==UndoStruct.T_COMBINED_NODE)
                {
//                   dp.redo_step(m_undo);
                }
                return this;
            } else {
                {
//                  dp.redo_step(m_undo);
                }
                return null;
            }

        }
    }

    public boolean select(double x1, double y1) {
        double dx = x1 - x;
        double dy = y1 - y;

        if (dx > 0 && dx < width && dy > 0 && dy < height)
            return true;
        else
            return false;
    }

    public Point getPopExLocation() {
        return new Point((int) (ax + 16), (int) (ay + 16));
    }

    public ProofText mouseMove(double x, double y) {
        if (!visible) return null;

        double dx = x - ax;
        double dy = y - ay;
        this.isMouseOnArrow = dx >= 0 && dx <= 16 && dy >= 0 && dy <= 16;
        if (isMouseOnArrow)
            return this;
        if (proofFieldNested != null)
            return proofFieldNested.mouseMove(x, y);
        else
            return null;
    }

    public ProofText selectAll(double x1, double y1) {
        if (this.select(x1, y1)) return this;

        if (this.isExpanded())
            return this.selectChild(x1, y1, true);

        return null;
    }

    public void move(double dx, double dy) {
        x = x + (int) dx;
        y = y + (int) dy;
    }

    public void setCurrentPosition(Point p) {
        x = p.x;
        y = p.y;
    }

    public void getNextPosition(Point p) {
        p.setLocation((int) x, (int) (y + height));
    }

    public Point getNextPositionFromFirstNode() {
        return new Point((int) (x + whead), (int) (y + height));
    }

    public double getHeadwidth() {
        return whead;
    }

    public boolean run_to_begin(DrawPanel dp) {
        if (m_undo == null) return false;
        if (proofFieldNested != null)
            proofFieldNested.run_to_begin(dp);
        else if (m_undo.m_type == UndoStruct.T_UNDO_NODE || m_undo.m_type == UndoStruct.T_COMBINED_NODE)
            dp.undo_step(m_undo);
        return true;
    }

    public boolean undo_default(DrawPanel dp) {
        if (m_undo == null) return false;
        if (proofFieldNested != null)
            proofFieldNested.undo_default(dp);
        if (m_undo.m_type == UndoStruct.T_UNDO_NODE || m_undo.m_type == UndoStruct.T_COMBINED_NODE)
            dp.undo_step(m_undo);
        return true;
    }

    public boolean undo_to_head(DrawPanel dp) {
        if (m_undo == null) return false;
        if (proofFieldNested != null)
            proofFieldNested.undo_to_head(dp);
        if (m_undo.m_type == UndoStruct.T_UNDO_NODE || m_undo.m_type == UndoStruct.T_COMBINED_NODE)
            dp.undo_step(m_undo);
        return true;
    }

    public void draw(Graphics2D g2, boolean selected) {
        if (selected == false)
            this.draw(g2);
        else {
            Rectangle rc = new Rectangle((int) (x - 2), (int) (y + 2), (int) width + 4, (int) height + 2);
            g2.setStroke(new BasicStroke(0.5f));
            g2.setColor(new Color(204, 255, 204));
            g2.fill(rc);
            g2.setColor(Color.black);
            g2.draw(rc);
        }
    }

    public void drawChild(Graphics2D g2, Point p) {
        if (proofFieldNested != null) {
            proofFieldNested.draw(g2, p);
        }
    }

    public ProofText fd_text(int i) {
        if (i == this.m_row)
            return this;
        if (proofFieldNested != null)
            return proofFieldNested.fd_text(i);
        else return null;
    }

    public void setStepRowDefault() {
        this.m_row = -1;
        if (proofFieldNested != null)
            proofFieldNested.setStepRowDefault();
    }

    public void draw(Graphics2D g2) {
        if (head == null) return;
        m_row = D_ROW++;

		if (font == null) {
			font = new Font("Dialog", Font.PLAIN, 14);
		}
        
        g2.setFont(font);

        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics(head, frc);
        double h = lm.getHeight();
        double w = 0;
        Rectangle2D r1 = font.getStringBounds(head, frc);
        g2.setColor(chead);

        g2.drawString(head, (float) x, (float) (y + h));
        double tw = r1.getWidth();

        height = h;
        whead = w = tw;
        if (msg == null || msg.length() == 0) return;


        g2.setColor(cmsg);
        String[] sl = msg.split("\n");
        double start = x + tw + HSpace;
        for (int i = 0; i < sl.length; i++) {
            Rectangle2D r2 = font.getStringBounds(sl[i], frc);
            if (r2.getWidth() > w)
                w = r2.getWidth();
            g2.drawString(sl[i], (float) (start), (float) (y + (i + 1) * h));
        }
        height = h * sl.length;
        w = w + tw;
        ax = x + w + 10;
        ay = y + (h - 16);

        if (rule.length() > 0)
            if (isMouseOnArrow) {
                g2.setColor(Color.black);
                g2.drawRect((int) ax, (int) ay, 16, 16);
                g2.drawImage(arrow, (int) (ax), (int) (ay), Color.pink, d);
            } else
                g2.drawImage(arrow, (int) ax, (int) ay, d);

    }

    //////////////////////////////////////////////////////////////////////////
    public void saveText(DataOutputStream out, int space) throws IOException {
        if (m_undo.m_type == UndoStruct.T_TO_PROVE_NODE || m_undo.m_type == UndoStruct.T_PROVE_NODE) {
            if (msg != null && msg.length() != 0) {
                String tab = "";
                for (int i = 0; i < space; i++)
                    tab += " ";
                tab += head;
                String str = tab + msg + "\n";
                byte[] bt = str.getBytes();
                out.write(bt, 0, bt.length);
            }
            if (proofFieldNested != null)
                proofFieldNested.saveText(out, space + 5);
        }

    }

    public void SavePS(FileOutputStream fp, int stype, int ntype) throws IOException // 0 0. 1 20. 2 25.
    {
        if (visible == false) return;
        if (head == null) return;

        String sf = "/Times-Roman findfont " + font.getSize() + " fzoff add scalefont setfont\n";
        fp.write(sf.getBytes());

        if (head.length() != 0) {
            this.SavePsColor(chead, fp, stype);
            String sh = " " + x + " " + (-y) + " yoff add moveto (" + head + ") show\n";
            fp.write(sh.getBytes());
        }

        this.SavePsColor(cmsg, fp, stype);
        String[] sm = msg.split("\n");
        int sx = (int) (x + whead);
        String s1 = null;

        if (ntype == 1)
            s1 = " " + sx + " 20 add " + (-(int) y) + " yoff add moveto (" + sm[0] + ") show\n";
        else if (ntype == 2)
            s1 = " " + sx + " 25 add " + (-(int) y) + " yoff add moveto (" + sm[0] + ") show\n";
        else
            s1 = " " + sx + " " + (-(int) y) + " yoff add moveto (" + sm[0] + ") show\n";

        fp.write(s1.getBytes());
        for (int i = 1; i < sm.length; i++) {
            String sp = (int) (x + whead) + " " + (-(int) (y + height * i)) + " yoff add moveto (" + sm[i] + ") show\n";
            fp.write("   /yoff  yoff ystep add def\n".getBytes());
            fp.write(sp.getBytes());
        }
        fp.write("   /yoff  yoff ystep add def\n".getBytes());
        if (proofFieldNested != null && this.isexpand)
            proofFieldNested.SavePS(fp, stype);
    }

    public void SavePsColor(Color c, FileOutputStream fp, int stype) throws IOException {
        if (stype == 0)  //color
        {
            double r = ((double) (100 * c.getRed() / 255)) / 100;
            double g = ((double) (100 * c.getGreen() / 255)) / 100;
            double b = ((double) (100 * c.getBlue() / 255)) / 100;
            String s = new Double(r).toString() + " " + new Double(g).toString() + " " + new Double(b).toString();
            s += " setrgbcolor ";
            fp.write(s.getBytes());
        } else if (stype == 1)  //gray
        {
            String s = "";
            double gray = (int) ((0.11 * c.getRed() + 0.59 * c.getGreen() + 0.3 * c.getBlue()) / 2.55) / 100.0;
            s += " " + gray + " " + gray + " " + gray + " setrgbcolor ";
            fp.write(s.getBytes());
        } else if (stype == 2)  // black & white
        {
            String s = "0.0 0.0 0.0 setrgbcolor ";
            fp.write(s.getBytes());
        }
    }

    public void WriteString(DataOutputStream out, String s) throws IOException {
        out.writeInt(s.length());
        out.writeChars(s);
    }

    public void WriteFont(DataOutputStream out, Font f) throws IOException {
        String s = f.getName();
        WriteString(out, s);
        out.writeInt(f.getStyle());
        out.writeInt(f.getSize());
    }

    public String ReadString(DataInputStream in) {
        String s = new String();
        try {
        	int size = in.readInt();
	        for (int i = 0; i < size; i++)
	            s += in.readChar();
        } catch (IOException e) {
        	System.out.println("The file being loaded does not adhere to the required format.");
        }
        return s;
    }

    public Font ReadFont(DataInputStream in) throws IOException {
        String name = ReadString(in);
        int stye = in.readInt();
        int size = in.readInt();

        return new Font(name, stye, size);
    }
    
	
	public void Save(DataOutputStream out) throws IOException {
        this.WriteString(out, head);
        this.WriteString(out, rule);
        this.WriteString(out, rpath);

        this.WriteFont(out, font);

        out.writeInt(chead.getRGB());
        out.writeInt(cmsg.getRGB());

        out.writeDouble(x);
        out.writeDouble(y);

        out.writeBoolean(visible);
        out.writeBoolean(isexpand);

        if (proofFieldNested != null) {
            out.writeBoolean(true);
            proofFieldNested.Save(out);
        } else
            out.writeBoolean(false);

        if (m_undo == null)
            out.writeBoolean(false);
        else {
            out.writeBoolean(true);
            out.writeInt(m_undo.m_id);
        }
    }

    public void Load(DataInputStream in, DrawPanel dp) throws IOException {

        head = this.ReadString(in);
        msg = this.ReadString(in);
        if (UtilityMiscellaneous.version_load_now >= 0.033)
            rule = this.ReadString(in);
        else
            rule = "";
        if (UtilityMiscellaneous.version_load_now >= 0.034)
            rpath = this.ReadString(in);
        else {
            if (rule.length() > 0) {
                String sp = System.getProperty("file.separator");
                rpath = "rules" + sp + rule + ".gex";
            } else
                rpath = "";
        }
        font = this.ReadFont(in);
        int c = in.readInt();
        chead = new Color(c);
        c = in.readInt();
        cmsg = new Color(c);

        x = in.readDouble();
        y = in.readDouble();

        visible = in.readBoolean();
        isexpand = in.readBoolean();

        boolean cp = in.readBoolean();
        if (cp) {
            proofFieldNested = new ProofField();
            proofFieldNested.Load(in, dp);
        }

        boolean isu = in.readBoolean();
        if (isu) {
            int id = in.readInt();
            m_undo = dp.getUndoById(id);
        } else
            m_undo = null;
    }
}
