package wprover;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class RuleListDialog extends JBaseDialog {
	
	private static final long serialVersionUID = 1097859396464222155L;
	private GExpert gxInstance;
    private JScrollPane scroll;
    private RuleViewPane rpane;
    private ruleDpanel dpane;
    private JPanel split;

    public RuleListDialog(GExpert gx) {
        super(gx.getFrame());
        gxInstance = gx;
        init();
    }

    public RuleListDialog(GApplet1 gx) {
        super(gx.getFrame());
        gxInstance = null;
        init();
    }


    public void init() {
        if (gxInstance != null && CMisc.isApplication())
            setAlwaysOnTop(true); //        if (gxInstance != null) gxInstance.addDependentDialog(this);

        setTitle("Rules");
        setModal(false);

        scroll = new JScrollPane((rpane = new RuleViewPane(gxInstance)));
        dpane = new ruleDpanel();

        split = new JPanel();
        split.setLayout(new BoxLayout(split, BoxLayout.Y_AXIS));
        split.add(scroll);
        split.add(dpane);
        getContentPane().add(split);
        
        setSize(500, 500);
        if (gxInstance != null)
            setLocation(gxInstance.getX(), gxInstance.getY() + gxInstance.getHeight() - 500);
    }

    public boolean loadRule(boolean bFullAngleMethod, int n) {
        grule r;
        if (!bFullAngleMethod)
            r = RuleList.getGrule(n);
        else
            r = RuleList.getFrule(n);

        if (r == null)
            return false;

        if (!bFullAngleMethod)
            this.setTitle("Rule " + n + " for GDD Method");
        else
            this.setTitle("Rule " + n + " for Full Angle Method");

        dpane.setRule(bFullAngleMethod, r);
        //boolean rf = rpane.loadRule(bFullAngleMethod, n);
        rpane.centerAllObject();
        rpane.scrollToCenter();
        return false;
        //return rf;
    }

    
    class ruleDpanel extends JPanel implements MouseListener {

		private static final long serialVersionUID = -4955553940870665769L;
		private JLabel label1, label2;
        private JEditorPane epane;
        private boolean bRuleTypeFullAngle;
        private int ruleNumber;

	@Override
        public Dimension getMaximumSize() {
            Dimension dm = super.getMaximumSize();
            Dimension dm2 = super.getPreferredSize();
            dm2.setSize(dm.getWidth(), dm2.getHeight());
            return dm2;
        }

        public ruleDpanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING));
            label1 = new JLabel();
            label1.setForeground(new Color(0, 128, 0));
            label1.addMouseListener(this);
            label2 = new JLabel();
            epane = new JEditorPane();
            epane.setEditable(false);
            p.add(label1);
            p.add(Box.createHorizontalStrut(5));
            p.add(label2);
            this.add(p);
            this.add(epane);
        }

        public void setRule(boolean bFullAngleMethod, grule r) {
        	bRuleTypeFullAngle = bFullAngleMethod;
        	ruleNumber = r.type;

            String sh;
            if (!bFullAngleMethod)
                sh = "RULE " + r.type; //+ " for Deductive Database Method";
            else
            	sh = "RULE " + r.type; //+ " for Full Angle Method";
            label1.setText(sh);
            label2.setText(r.name);
            label1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            String s = r.description;
            if (r.exstring != null)
                s += '\n' + r.exstring;
            epane.setText(s);
        }

	@Override
        public void mouseClicked(MouseEvent e) {
            String dr = GExpert.getUserDir();
            String sp = GExpert.getFileSeparator();
            if (!bRuleTypeFullAngle)
                GExpert.openURL("file:///" + dr + sp + "help" + sp + "GDD" + sp + "r" + ruleNumber + ".html");
            else
                GExpert.openURL("file:///" + dr + sp + "help" + sp + "FULL" + sp + "r" + ruleNumber + ".html");

        }

	@Override
        public void mousePressed(MouseEvent e) {
        }

	@Override
        public void mouseReleased(MouseEvent e) {
        }

	@Override
        public void mouseEntered(MouseEvent e) {
        }

	@Override
        public void mouseExited(MouseEvent e) {
        }

    }

    class RuleViewPane extends JPanel implements MouseListener, ComponentListener, MouseMotionListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = 6977887905457579165L;
		drawTextProcess dx;
        int xx, yy;
        double scale = 1.0;
        protected Rectangle rc = new Rectangle(0, 0, 0, 0);

	@Override
        public Dimension getPreferredSize() {
            return new Dimension((int) rc.getWidth() + 20, (int) rc.getHeight() + 20);
        }

        public void resetSize() {
            if (dx == null) return;
            ArrayList<GEPoint> v1 = dx.pointlist;
            Rectangle rcc = this.getPointsBounds(v1);
            double rx = RuleViewPane.this.getWidth();
            double ry = RuleViewPane.this.getHeight();
            if (rcc.getWidth() > rx || rcc.getHeight() > ry) {
                double r1 = rx / (rcc.getWidth() * 1.1);
                double r2 = ry / (rcc.getHeight() * 1.1);
                scale = r1 < r2 ? r1 : r2;
            } else
                scale = 1.0;
            centerAllObject();
            scrollToCenter();
        }

	@Override
        public void componentResized(ComponentEvent e) {
            resetSize();
        }

        public void scrollToCenter() {
            Rectangle rc1 = scroll.getViewport().getBounds();
            JScrollBar b1 = scroll.getHorizontalScrollBar();
            b1.setValue((int) ((b1.getMaximum() - rc1.getWidth()) / 2));
            b1 = scroll.getVerticalScrollBar();
            b1.setValue(((int) (b1.getMaximum() - rc1.getHeight()) / 2));
        }

	@Override
        public void componentMoved(ComponentEvent e) {
        }

	@Override
        public void componentShown(ComponentEvent e) {
        }

	@Override
        public void componentHidden(ComponentEvent e) {
        }

        public void centerAllObject() {
            ArrayList<GEPoint> v1 = dx.pointlist;
            getPointsBounds(v1);

            xx = (int) ((this.getWidth() - rc.getWidth() * scale) / 2 - rc.getX() * scale);
            yy = (int) ((this.getHeight() - rc.getHeight() * scale) / 2 - rc.getY() * scale);
        }

//        public boolean loadRule(boolean bFullAngleMethod, int n) {
//            String s = new Integer(n).toString();
//
//            try {
//                GeoPoly.clearZeroN();
//
//                if (n < 10)
//                    s = "0" + s;
//
//                if (CMisc.isApplication()) {
//                    String sh;
//                    if (!bFullAngleMethod)
//                        sh = "examples/rules/GDD/" + s + ".gex";
//                    else sh = "examples/rules/FULL/" + s + ".gex";
//                    dx.Load(sh);
//                } else {
//                    String sh;
//                    if (!bFullAngleMethod)
//                        sh = "Examples/Rules/GDD/" + s + ".gex";
//                    else sh = "Examples/Rules/FULL/" + s + ".gex";
//                    loadRemote(sh);
//                }
//                return true;
//            } catch (IOException ee) {
//                JOptionPane.showMessageDialog(gxInstance, "Can not find file " + s + ".gex", "Not Found", JOptionPane.ERROR_MESSAGE);
//                return false;
//            }
//
//        }

//        private void loadRemote(String sh) {
//            try {
//                URL ul = new URL(/*gxInstance.getDocumentBase()*/ CMisc.getHomeDirectory(), sh);
//                URLConnection urlc = ul.openConnection();
//                urlc.connect();
//                InputStream instream = urlc.getInputStream();
//                @SuppressWarnings("resource")
//				DataInputStream in = new DataInputStream(instream);
//                dx.Load(in);
//            }
//            catch (IOException ee) {
//            	System.err.println("Failed attempt to load the list of rules from the remote source.");
//            }
//        }

        public RuleViewPane(GExpert gx) {
            gxInstance = gx;
            xx = yy = 0;
            dx = new drawTextProcess();
            dx.setCurrentDrawPanel(this);
            dx.setRecal(false);
            dx.SetCurrentAction(drawProcess.MOVE);
            this.addMouseListener(this);
            this.setBackground(Color.white);
            this.addMouseMotionListener(this);
            this.addComponentListener(this);
            this.addMouseWheelListener(new MouseWheelListener() {
		@Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int n = e.getWheelRotation();
                    if (scale < 0.4 && n < 0 || scale > 3.0 && n > 0) return;
                    scale = scale + n * 0.04;
                    if (dx == null) return;
                    centerAllObject();
                    RuleViewPane.this.repaint();
                }
            });
            //this.setBackground(Color.lightGray);
        }

        public Rectangle getPointsBounds(ArrayList<GEPoint> v) {
            if (!v.isEmpty()) {
		GEPoint p1 = v.get(0);
		double x, y, x1, y1;
		x = x1 = p1.getx();
		y = y1 = p1.gety();
		for (int i = 1; i < v.size(); i++) {
		    GEPoint p = v.get(i);
		    double x0 = p.getx();
		    double y0 = p.gety();
		    if (x0 > x)
			x = x0;
		    else if (x0 < x1)
			x1 = x0;
		    if (y0 > y)
			y = y0;
		    else if (y0 < y1)
			y1 = y0;
		}
		rc.setBounds((int) x1, (int) y1, (int) (x - x1), (int) (y - y1));
	    }
	    return rc;
        }

	@Override
        public void mouseDragged(MouseEvent e) {
            dx.DWMouseDrag((e.getX() - xx) / scale, (e.getY() - yy) / scale);
            dx.reCalculate();
            dx.recal_allFlash();
            this.repaint();
        }

	@Override
        public void mouseMoved(MouseEvent e) {
            dx.DWMouseMove((e.getX() - xx) / scale, (e.getY() - yy) / scale);
            dx.reCalculate();
            this.repaint();
        }

	@Override
        public void mouseClicked(MouseEvent e) {
        }


	@Override
        public void mousePressed(MouseEvent e) {
            dx.DWButtonDown((e.getX() - xx) / scale, (e.getY() - yy) / scale);
            dx.reCalculate();
            dx.recal_allFlash();
            this.repaint();
        }


	@Override
        public void mouseReleased(MouseEvent e) {
            dx.DWButtonUp((e.getX() - xx) / scale, (e.getY() - yy) / scale);
            dx.reCalculate();
            dx.recal_allFlash();
            this.repaint();
        }


	@Override
        public void mouseEntered(MouseEvent e) {
        }


	@Override
        public void mouseExited(MouseEvent e) {
        }


	@Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            resetSize();

            g2.translate(xx, yy);
            g2.scale(scale, scale);
            dx.paintPoint(g2);
        }
    }
}
