package wprover;

import gprover.Cm;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class DialogNumericalCheck extends DialogBase implements DiagramUpdater, ItemListener, WindowListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5440131591830059988L;
	private JComboBox<String> bx;
    private ArrayList<JComboBox<GEPoint>> bxs = new ArrayList<JComboBox<GEPoint>>(); // bx1, bx2, bx3, bx4, bx5, bx6;
    private DrawPanelFrame gxInstance;

    private ImageIcon icon1, icon2;

    private ArrayList<JLabel> labels = new ArrayList<JLabel>(); //label1, label2, label3, label4, label5, label6;

    private JPanel cards;
    int TYPE;


    public static String[] ST = {"Collinear", "Parallel", "Perpendicular", "Cyclic", "Equal Distance", "Equal Angle"};
    public static int[] SN = {3, 4, 4, 4, 4, 6};

    public DialogNumericalCheck(DrawPanelFrame gx) {
        super(gx.getFrame());
        gxInstance = gx;
        this.setTitle(DrawPanelFrame.getLanguage("Numerical Check"));
        this.addWindowListener(this);

        icon1 = DrawPanelFrame.createImageIcon("images/ptree/hook.gif");
        icon2 = DrawPanelFrame.createImageIcon("images/ptree/cross.gif");


        JPanel top = new JPanel();

        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String[] sss = new String[ST.length];
        for (int i = 0; i < ST.length; i++)
            sss[i] = DrawPanelFrame.getLanguage(ST[i]);

        bx = new JComboBox<String>(sss);
        panel.add(bx);
        bx.addItemListener(this);

        ArrayList<GEPoint> vv = new ArrayList<GEPoint>();
        gxInstance.dp.getPointList(vv);

        for (int i = 0; i < 6; i++) {
            bxs.add( new JComboBox<GEPoint>((GEPoint[]) vv.toArray()));
            panel.add(bxs.get(i));
            panel.add(Box.createHorizontalStrut(3));
            bxs.get(i).addItemListener(this);
        }

        top.add(panel);
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(6, 1));
        for (int i = 0; i < 6; i++) {
        	JLabel lb = new JLabel();
            labels.add(lb);
            panel2.add(lb);
        }

        top.add(panel2);
        cards = new JPanel(new CardLayout(1, 1));
        cards.add(ST[0], new Panel_Coll());
        cards.add(ST[1], new Panel_Para());
        cards.add(ST[2], new Panel_Perp());
        cards.add(ST[3], new Panel_Cyclic());
        cards.add(ST[4], new Panel_EQDis());
        cards.add(ST[5], new Panel_EQAng());
        top.add(cards);

        JPanel p3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        ActionListener ls = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = e.getActionCommand();
                if (s.equals("Clear")) {
                    DialogNumericalCheck.this.unSelectAllPoints();
                } else if (s.equals("Close")) {
                    DialogNumericalCheck.this.setVisible(false);
                }
            }
        };
        JButton b1 = new JButton(DrawPanelFrame.getLanguage("Clear"));
        JButton b2 = new JButton(DrawPanelFrame.getLanguage("Close"));
        b1.setActionCommand("Clear");
        b2.setActionCommand("Close");

        p3.add(b1);
        p3.add(b2);
        b1.addActionListener(ls);
        b2.addActionListener(ls);
        top.add(Box.createVerticalGlue());
        top.add(p3);
        setVisibleStatus(1);
        bx.setSelectedIndex(-1);
        unSelectAllPoints();
        getContentPane().add(top);
        setSize(400, 250);
        UpdateDiagram();
        gxInstance.dp.addDiagramUpdaterListener(this);
    }


    public void UpdateDiagram() {
        for (int i = 0; i < 6; i++) {
        	labels.get(i).setText(getStringFromPoint((bxs.get(i).getItemAt(i))));
        }

        int n = bx.getSelectedIndex();
        if (n >= 0) {
            Panel_Basic b = (Panel_Basic) cards.getComponent(n);
            b.DiagramUpdate();
        }
    }

    public void addSelectPoint(GEPoint p) {
        for (int i = 0; i < 6; i++) {
            if (bxs.get(i).getSelectedIndex() < 0) {
                bxs.get(i).setSelectedItem(p);
                return;
            }
        }

    }

    public void itemStateChanged(ItemEvent e) {
        Object o = e.getSource();
        if (o == bx) {
            int n = bx.getSelectedIndex();
            setVisibleStatus(n);
            unSelectAllPoints();

            if (n >= 0) {
                CardLayout cl = (CardLayout) (cards.getLayout());
                cl.show(cards, ST[n]);
            }
        } else
            UpdateDiagram();
    }

    public void setVisibleStatus(int n) {
        int num = 0;
        if (n == -1)
            num = 4;
        else num = SN[n];
        
        int i = 0;
    	for (JComboBox<GEPoint> jcbp : bxs) {
    		jcbp.setVisible(i++ < num);
    	}
    	i = 0;
    	for (JLabel jl : labels) {
    		jl.setVisible(i++ < num);
    	}
    }

    public void unSelectAllPoints() { // XXX Unfinished
//	ArrayList<CPoint> v = new ArrayList<CPoint>();
//	gxInstance.dp.getPointList(v);
	
//        for (int i = 0; i < bxs.length; i++) {
//        }

        for (JComboBox<GEPoint> jcb: bxs) {
            jcb.setSelectedIndex(-1);
        }
    }

    public GEPoint getPt(int n) {
        return (n >= 0 && n < 6) ? bxs.get(n).getItemAt(n) : null;
    }

    public boolean check_filled() {
        for (JComboBox<GEPoint> jcb : bxs) {
            if (jcb.isVisible() && jcb.getSelectedIndex() == -1)
            	return false;
        }
        return true;
    }

    public static String getStringFromPoint(GEPoint p) {
        if (p == null) return "      ";

        float x = (float) p.getx();
        float y = (float) p.gety();

        return " " + p.getname() + ":    x = " + x + ",  y = " + y;
    }


    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
        gxInstance.dp.RemoveDiagramUpdaterListener(this);
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }


    public void windowDeactivated(WindowEvent e) {
    }


    /////////////////////////////////////////////////////////////////////
    class Panel_Button extends JPanel {

        /**
		 * 
		 */
		private static final long serialVersionUID = -3274454710738183698L;

		public Panel_Button() {
            this.setLayout(new FlowLayout(FlowLayout.LEADING));
        }
    }

    abstract class Panel_Basic extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = -2619429396592640322L;
		JLabel lex, ltex;
        String pstring;

        public Panel_Basic(String s) {
            pstring = s;
            this.setLayout(new FlowLayout(FlowLayout.LEFT));
            lex = new JLabel();
            this.add(lex);

            ltex = new JLabel();
            this.add(ltex);

        }

        private void setValue(boolean t) {
            if (t) {
                ltex.setIcon(icon1);
                ltex.setText(DrawPanelFrame.getLanguage("TRUE"));
                ltex.setForeground(Color.green.darker());
            } else {
                ltex.setIcon(icon2);
                ltex.setText(DrawPanelFrame.getLanguage("FALSE"));
                ltex.setForeground(Color.red.darker());
            }
        }

        private void reset() {
            ltex.setText("");
            ltex.setIcon(null);
        }

        public void DiagramUpdate() {
            boolean t = check_filled();
            if (!t)
                reset();
            else
                setValue(Cal_Value());
            String s = getLex();
            if (t)
                s += "        ";

            lex.setText(s);
        }

        abstract public boolean Cal_Value();

        abstract public String getLex();

        public String toString() {
            return pstring;
        }
    }

    class Panel_Coll extends Panel_Basic {

        /**
		 * 
		 */
		private static final long serialVersionUID = -286051979079038773L;

		public Panel_Coll() {
            super(DrawPanelFrame.getLanguage(ST[0]));
        }

        public String getLex() {
            if (check_filled())
                return (pstring + ": " + getPt(0) + " " + getPt(1) + " " + getPt(2));
            else
                return ("");
        }

        public boolean Cal_Value() {
            return DrawPanelBase.check_Collinear(getPt(0), getPt(1), getPt(2));
        }

    }

    class Panel_Para extends Panel_Basic {
        /**
		 * 
		 */
		private static final long serialVersionUID = 7503706920027056159L;

		public Panel_Para() {
            super(ST[1]);
        }

        public boolean Cal_Value() {
            return DrawPanelBase.check_para(getPt(0), getPt(1), getPt(2), getPt(3));
        }

        public String getLex() {
            if (check_filled())
                return (getPt(0) + "" + getPt(1) + Cm.s2079 + getPt(2) + "" + getPt(3));
            else
                return ("");
        }
    }

    class Panel_Perp extends Panel_Basic {
        /**
		 * 
		 */
		private static final long serialVersionUID = 2868975705564965978L;

		public Panel_Perp() {
            super(ST[2]);
        }

        public boolean Cal_Value() {
            return DrawPanelBase.check_perp(getPt(0), getPt(1), getPt(2), getPt(3));
        }

        public String getLex() {
            if (check_filled())
                return (getPt(0) + "" + getPt(1) + Cm.s2077 + getPt(2) + "" + getPt(3));
            else
                return ("");
        }
    }

    class Panel_Cyclic extends Panel_Basic {
        /**
		 * 
		 */
		private static final long serialVersionUID = -5833301320000633717L;

		public Panel_Cyclic() {
            super(DrawPanelFrame.getLanguage(ST[3]));
        }

        public boolean Cal_Value() {
            return DrawPanelBase.check_cyclic(getPt(0), getPt(1), getPt(2), getPt(3));
        }

        public String getLex() {
            if (check_filled())
                return (pstring + ": " + getPt(0) + ", " + getPt(1) + ", " + getPt(2) + ", " + getPt(3));
            else
                return ("");
        }
    }

    class Panel_EQDis extends Panel_Basic {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1036007181368748932L;

		public Panel_EQDis() {
            super(ST[4]);
        }

        public boolean Cal_Value() {
            return DrawPanelBase.check_eqdistance(getPt(0), getPt(1), getPt(2), getPt(3));
        }

        public String getLex() {
            if (check_filled())
                return (getPt(0) + "" + getPt(1) + " = " + getPt(2) + "" + getPt(3));
            else
                return ("");
        }
    }

    class Panel_EQAng extends Panel_Basic {
        /**
		 * 
		 */
		private static final long serialVersionUID = 3913806463588708705L;

		public Panel_EQAng() {
            super(ST[5]);
        }

        public boolean Cal_Value() {
            return DrawPanelBase.check_eqangle(getPt(0), getPt(1), getPt(2), getPt(3), getPt(4), getPt(5));
        }

        public String getLex() {
            if (check_filled())
                return (Cm.sangle + getPt(0) + "" + getPt(1) + "" + getPt(2) + " = " + Cm.sangle + getPt(3) + "" + getPt(4) + "" + getPt(5));
            else
                return ("");
        }
    }
}
