package wprover;

import gprover.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 26, 2006
 * Time: 5:52:40 PM
 * To change this template use File | Settings | File Templates.
 */             
public class AttrToCondDialog extends JBaseDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1604108363605711995L;
	final private static int ROW = 5;
    JButton bok, bcancel;
    cond co = null;
    JScrollPane topPane;
    JPanel buttonPane;
    JPanel panel = new JPanel();
    JPanel contentPane;


    public AttrToCondDialog(GExpert gx, String s) {
        super(gx.getFrame(), s, true);
        if (CMisc.isApplication())
            this.setAlwaysOnTop(true);
//        if (gx != null)
//            gx.addDependentDialog(this);

        buttonPane = new ButtonPane();
        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
    }


    public cond getReturnedCond() {
        return co;
    }

    public AttrToCondDialog(GExpert gx, l_line ln) {
        this(gx, "Please select three points");

        topPane = new JScrollPane(new LnPanel(ln)) {//, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS) {

            /**
			 * 
			 */
			private static final long serialVersionUID = 352511380739318095L;

			public Dimension getPreferredSize() {
                Dimension dm = super.getPreferredSize();
                dm.setSize(dm.getWidth(), Integer.MAX_VALUE);
                return dm;
            }
        };
        addComp();
    }


    public AttrToCondDialog(GExpert gx, a_cir cr) {
        this(gx, "Please select four points");

        topPane = new JScrollPane(new CirPanel(cr)) {//, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS) {

            /**
			 * 
			 */
			private static final long serialVersionUID = -5097129910694688319L;

			public Dimension getPreferredSize() {
                Dimension dm = super.getPreferredSize();
                dm.setSize(dm.getWidth(), Integer.MAX_VALUE);
                return dm;
            }
        };
        addComp();
    }

    public AttrToCondDialog(GExpert gx, angst st) {
        this(gx, "please select two angles");

        topPane = new JScrollPane(new AnglePane(st)) {
            //, JScrollPane.j, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS) {

            /**
			 * 
			 */
			private static final long serialVersionUID = 6826532631962439873L;

			public Dimension getPreferredSize() {
                Dimension dm = super.getPreferredSize();
                dm.setSize(dm.getWidth(), Integer.MAX_VALUE);
                return dm;
            }
        };
        addComp();
    }

    public AttrToCondDialog(GExpert gx, s_tris st) {
        this(gx, "please select two triangles");

        topPane = new JScrollPane(new StriPane(st)) {//, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS) {

            /**
			 * 
			 */
			private static final long serialVersionUID = -7757047255666487252L;

			public Dimension getPreferredSize() {
                Dimension dm = super.getPreferredSize();
                dm.setSize(dm.getWidth(), Integer.MAX_VALUE);
                return dm;
            }
        };
        addComp();
    }

    public AttrToCondDialog(GExpert gx, c_segs cg) {
        this(gx, "please select two segments");

        topPane = new JScrollPane(new CongPane(cg)) {//, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS) {

            /**
			 * 
			 */
			private static final long serialVersionUID = 498889366920871609L;

			public Dimension getPreferredSize() {
                Dimension dm = super.getPreferredSize();
                dm.setSize(dm.getWidth(), Integer.MAX_VALUE);
                return dm;
            }
        };
        addComp();
    }

    public AttrToCondDialog(GExpert gx, p_line pn) {
        this(gx, "please select two two lines");

        topPane = new JScrollPane(new PnPane(pn)) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 5341752045350499629L;

			public Dimension getPreferredSize() {
                Dimension dm = super.getPreferredSize();
                dm.setSize(dm.getWidth(), Integer.MAX_VALUE);
                return dm;
            }
        };
        addComp();
    }

    public void addComp() {
        Window gx = this.getOwner();
        contentPane.add(topPane);
        contentPane.add(buttonPane);
        this.add(contentPane);
        this.setSize(400, 200);
        this.setLocation(gx.getX(), gx.getY() + gx.getHeight() - this.getHeight());
    }


    class ButtonPane extends JPanel implements ActionListener {

        /**
		 * 
		 */
		private static final long serialVersionUID = 5449249960412560726L;

		public ButtonPane() {
            this.add(Box.createHorizontalGlue());
            bok = new JButton("OK");
            bok.setEnabled(false);
            bcancel = new JButton("Cancel");
            bok.addActionListener(this);
            bcancel.addActionListener(this);
            this.add(bok);
            this.add(bcancel);
        }

        public void actionPerformed(ActionEvent e) {
            Object o = e.getSource();
            if (o == bcancel)
                co = null;
            AttrToCondDialog.this.setVisible(false);
        }
    }


    class CirPanel extends JPanel implements ItemListener {

        /**
		 * 
		 */
		private static final long serialVersionUID = -462823505242182419L;
		//private a_cir cir = null;

        public CirPanel(a_cir cir) {
            //CirPanel.this.cir = cir;

            this.setLayout(new GridLayout(0, ROW));

            for (int i = 0; i <= cir.no; i++) {
                String p = Prover.get_pt_name(cir.pt[i]);
                if (p != null) {
                    JCheckBox bx = new JCheckBox(p.toString());
                    this.add(bx);
                    bx.addItemListener(this);
                }
            }
        }

        public cond getReturnCond() {

            cond c = new cond();
            c.pred = gib.CO_CYCLIC;
            int n = 1;
            for (int i = 0; i < this.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox) this.getComponent(i);
                if (b.isSelected()) {
                    c.p[n++] = Prover.get_pt_id(b.getText());
                }
            }

            return c;
        }


        public void itemStateChanged(ItemEvent e) {
            int n = 0;
            for (int i = 0; i < this.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox) this.getComponent(i);
                if (b.isSelected())
                    n++;
            }
            if (n == 4) {
                bok.setEnabled(true);
                co = getReturnCond();
            } else {
                bok.setEnabled(false);
            }
        }
    }


    class LnPanel extends JPanel implements ItemListener {

        /**
		 * 
		 */
		private static final long serialVersionUID = -2203890623289552894L;
		//private l_line ln = null;

        public LnPanel(l_line ln) {
            //this.ln = ln;
            this.setLayout(new GridLayout(0, ROW));

            for (int i = 0; i <= ln.no; i++) {
                String p = Prover.get_pt_name(ln.pt[i]);
                if (p != null) {
                    JCheckBox bx = new JCheckBox(p.toString());
                    this.add(bx);
                    bx.addItemListener(this);
                }
            }
        }

        public Dimension getPreferredSize() {
            return super.getPreferredSize();
        }

        public cond getReturnCond() {

            cond c = new cond();
            c.pred = gib.CO_COLL;
            int n = 0;
            for (int i = 0; i < this.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox) this.getComponent(i);
                if (b.isSelected()) {
                    c.p[n++] = Prover.get_pt_id(b.getText());
                }
            }

            return c;
        }


        public void itemStateChanged(ItemEvent e) {
            int n = 0;
            for (int i = 0; i < this.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox) this.getComponent(i);
                if (b.isSelected())
                    n++;
            }
            if (n == 3) {
                bok.setEnabled(true);
                co = getReturnCond();
            } else {
                bok.setEnabled(false);
            }
        }
    }


    class AnglePane extends JPanel implements ItemListener {

        /**
		 * 
		 */
		private static final long serialVersionUID = -4601581065058858462L;
		private angst as = null;

        public AnglePane(angst as) {
            this.as = as;
            this.setLayout(new GridLayout(0, ROW));

            for (int i = 0; i < as.no; i++) {
                l_line l1 = as.ln1[i];
                l_line l2 = as.ln2[i];

                int t = l_line.inter_lls(l1, l2);
                int t1 = l_line.get_lpt1(l1, t);
                int t2 = l_line.get_lpt1(l2, t);

                if (t != 0 && t1 != 0 && t2 != 0) {
                    String s1 = Prover.get_pt_name(t1);
                    String s2 = Prover.get_pt_name(t);
                    String s3 = Prover.get_pt_name(t2);
                    JCheckBox bx = new JCheckBox(s1 + s2 + s3);
                    this.add(bx);
                    bx.addItemListener(this);
                }
            }
        }

        public cond getReturnCond() {

            cond c = new cond();
            c.pred = gib.CO_ACONG;
            int n1, n2;
            n1 = n2 = -1;

            for (int i = 0; i < this.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox) this.getComponent(i);
                if (b.isSelected()) {
                    if (n1 == -1)
                        n1 = i;
                    else
                        n2 = i;
                }
            }

            l_line l1 = as.ln1[n1];
            l_line l2 = as.ln2[n1];
            int t = l_line.inter_lls(l1, l2);
            c.p[0] = l_line.get_lpt1(l1, t);
            c.p[1] = t;
            c.p[2] = t;
            c.p[3] = l_line.get_lpt1(l2, t);

            l1 = as.ln1[n2];
            l2 = as.ln2[n2];
            t = l_line.inter_lls(l1, l2);
            c.p[4] = l_line.get_lpt1(l1, t);
            c.p[5] = t;
            c.p[6] = t;
            c.p[7] = l_line.get_lpt1(l2, t);

            return c;
        }


        public void itemStateChanged(ItemEvent e) {
            int n = 0;
            for (int i = 0; i < this.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox) this.getComponent(i);
                if (b.isSelected())
                    n++;
            }
            if (n == 2) {
                bok.setEnabled(true);
                co = getReturnCond();
            } else {
                bok.setEnabled(false);
            }
        }
    }


    class StriPane extends JPanel implements ItemListener {

        /**
		 * 
		 */
		private static final long serialVersionUID = -6428842023188179783L;
		private s_tris st = null;

        public StriPane(s_tris st) {
            this.st = st;
            this.setLayout(new GridLayout(0, ROW));

            for (int i = 0; i <= st.no; i++) {

                int t1 = st.p1[i];
                int t2 = st.p2[i];
                int t3 = st.p3[i];

                if (t1 != 0 && t2 != 0 && t3 != 0) {
                    String s1 = Prover.get_pt_name(t1);
                    String s2 = Prover.get_pt_name(t2);
                    String s3 = Prover.get_pt_name(t3);

                    JCheckBox bx = new JCheckBox(s1 + s2 + s3);
                    this.add(bx);
                    bx.addItemListener(this);
                }
            }
        }

        public cond getReturnCond() {

            cond c = new cond();
            if (st.st == 0)
                c.pred = gib.CO_STRI;
            else
                c.pred = gib.CO_CTRI;

            int n1, n2;
            n1 = n2 = -1;

            for (int i = 0; i < this.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox) this.getComponent(i);
                if (b.isSelected()) {
                    if (n1 == -1)
                        n1 = i;
                    else
                        n2 = i;
                }
            }

            c.p[0] = st.p1[n1];
            c.p[1] = st.p2[n1];
            c.p[2] = st.p3[n1];
            c.p[3] = st.p1[n2];
            c.p[4] = st.p2[n2];
            c.p[5] = st.p3[n2];

            return c;
        }


        public void itemStateChanged(ItemEvent e) {
            int n = 0;
            for (int i = 0; i < this.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox) this.getComponent(i);
                if (b.isSelected())
                    n++;
            }
            if (n == 2) {
                bok.setEnabled(true);
                co = getReturnCond();
            } else {
                bok.setEnabled(false);
            }
        }
    }

    class CongPane extends JPanel implements ItemListener {

        /**
		 * 
		 */
		private static final long serialVersionUID = -1384268480174880867L;
		private c_segs cg = null;

        public CongPane(c_segs cg) {
            this.cg = cg;
            this.setLayout(new GridLayout(0, ROW));

            for (int i = 0; i <= cg.no; i++) {

                int t1 = cg.p1[i];
                int t2 = cg.p2[i];

                if (t1 != 0 && t2 != 0) {
                    String s1 = Prover.get_pt_name(t1);
                    String s2 = Prover.get_pt_name(t2);

                    JCheckBox bx = new JCheckBox(s1 + s2);
                    this.add(bx);
                    bx.addItemListener(this);
                }
            }
        }

        public cond getReturnCond() {

            cond c = new cond();
            c.pred = gib.CO_CONG;
            int n1, n2;
            n1 = n2 = -1;

            for (int i = 0; i < this.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox) this.getComponent(i);
                if (b.isSelected()) {
                    if (n1 == -1)
                        n1 = i;
                    else
                        n2 = i;
                }
            }

            c.p[0] = cg.p1[n1];
            c.p[1] = cg.p2[n1];
            c.p[2] = cg.p1[n2];
            c.p[3] = cg.p2[n2];
            return c;
        }


        public void itemStateChanged(ItemEvent e) {
            int n = 0;
            for (int i = 0; i < this.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox) this.getComponent(i);
                if (b.isSelected())
                    n++;
            }
            if (n == 2) {
                bok.setEnabled(true);
                co = getReturnCond();
            } else {
                bok.setEnabled(false);
            }
        }
    }

    class PnPane extends JPanel implements ItemListener {

        /**
		 * 
		 */
		private static final long serialVersionUID = -5305822501261239814L;
		private p_line pn = null;

        public PnPane(p_line pn) {
            this.pn = pn;
            this.setLayout(new GridLayout(0, ROW));

            for (int i = 0; i <= pn.no; i++) {

                int t1 = pn.ln[i].pt[0];
                int t2 = pn.ln[i].pt[1];

                if (t1 != 0 && t2 != 0) {
                    String s1 = Prover.get_pt_name(t1);
                    String s2 = Prover.get_pt_name(t2);
                    JCheckBox bx = new JCheckBox(s1 + s2);
                    add(bx);
                    bx.addItemListener(this);
                }
            }
        }

        public cond getReturnCond() {

            cond c = new cond();
            c.pred = gib.CO_PARA;
            int n1, n2;
            n1 = n2 = -1;

            for (int i = 0; i < this.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox) getComponent(i);
                if (b.isSelected()) {
                    if (n1 == -1)
                        n1 = i;
                    else
                        n2 = i;
                }
            }

            c.p[0] = pn.ln[n1].pt[0];
            c.p[1] = pn.ln[n1].pt[1];
            c.p[2] = pn.ln[n2].pt[0];
            c.p[3] = pn.ln[n2].pt[1];
            return c;
        }


        public void itemStateChanged(ItemEvent e) {
            int n = 0;
            for (int i = 0; i < this.getComponentCount(); i++) {
                JCheckBox b = (JCheckBox) this.getComponent(i);
                if (b.isSelected())
                    n++;
            }
            if (n == 2) {
                bok.setEnabled(true);
                co = getReturnCond();
            } else {
                bok.setEnabled(false);
            }
        }
    }

}