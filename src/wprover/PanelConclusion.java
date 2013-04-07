package wprover;

import gprover.Cm;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-7-5
 * Time: 13:25:43
 * To change this template use File | Settings | File Templates.
 */
public class PanelConclusion extends JPanel implements ActionListener, ItemListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5208151958724830901L;
	public static ImageIcon icon_Right = DrawPanelFrame.createImageIcon("images/dtree/right.gif");
    public static ImageIcon icon_Wrong = DrawPanelFrame.createImageIcon("images/dtree/wrong.gif");
    public static ImageIcon icon_Question = DrawPanelFrame.createImageIcon("images/dtree/question.gif");

    private JComboBox<String> bt;
    private ArrayList<JComboBox<GEPoint>> vlist = new ArrayList<JComboBox<GEPoint>>();
    private ArrayList<JComboBox<GEPoint>> vlist1 = new ArrayList<JComboBox<GEPoint>>();
    private JLabel ltext1;
    private JButton bbok, bbcancel;
    private JPanel bpanel;
    private TreeCellAssertPanel asspane, asspane_temp;

    private DrawPanelFrame gxInstance;
    private massertion ass, ass_show, ass_temp;
    private JPanel contentPane;

    private PanelMProofInput ipanel = null;

    public PanelConclusion(DrawPanelFrame gx) {
        gxInstance = gx;
        init();
        bt.setSelectedIndex(-1);
    }

    public PanelConclusion(DrawPanelFrame gx, PanelMProofInput ipanel) {
        this(gx);
        this.ipanel = ipanel;
    }

    private void init() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        bt = new JComboBox<String>(massertion.cStrings) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 5568259248271834002L;

			public Dimension getMaximumSize() {
                return bt.getPreferredSize();
            }
        };

        bt.setMaximumRowCount(40);
        this.add(bt);
        bt.addItemListener(this);
        JPanel topPane = new JPanel();
        topPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        JPanel topPane1 = new JPanel();
        topPane1.setLayout(new FlowLayout(FlowLayout.CENTER));

        for (int i = 0; i < 4; i++) {
            JComboBox<GEPoint> b = new JComboBox<GEPoint>();
            b.addItemListener(this);
            topPane.add(b);
            vlist.add(b);
        }

        for (int i = 0; i < 4; i++) {
            JComboBox<GEPoint> b = new JComboBox<GEPoint>();
            b.addItemListener(this);
            topPane1.add(b);
            vlist1.add(b);
        }
        contentPane.add(topPane);
        contentPane.add(topPane1);

        JPanel textPane = new JPanel();
        textPane.setLayout(new BoxLayout(textPane, BoxLayout.X_AXIS));

        ltext1 = new JLabel();
        setLtext1Value(true);
        asspane = new TreeCellAssertPanel();

        textPane.add(Box.createHorizontalStrut(3));
        textPane.add(asspane);
        textPane.add(Box.createHorizontalGlue());
        textPane.add(ltext1);
        contentPane.add(textPane);

        bpanel = new JPanel();
        bpanel.setBorder(BorderFactory.createTitledBorder("Do you mean...."));
        bpanel.setLayout(new BoxLayout(bpanel, BoxLayout.Y_AXIS));

        asspane_temp = new TreeCellAssertPanel();
        bpanel.add(asspane_temp);
        bbok = new JButton("Yes");
        bbok.addActionListener(this);
        bbcancel = new JButton("Cancel");
        bbcancel.addActionListener(this);
        JPanel pt = new JPanel();
        pt.setLayout(new BoxLayout(pt, BoxLayout.X_AXIS));
        pt.add(Box.createHorizontalGlue());
        bbok.setAlignmentX(Component.RIGHT_ALIGNMENT);
        bbcancel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        pt.add(bbok);
        pt.add(bbcancel);
        bpanel.add(pt);
        bpanel.setVisible(false);
        contentPane.add(bpanel);

        this.add(contentPane);
        unselectAll();
        ass_show = new massertion(0);
    }

    public void setTypeSelection(int k) {
        bt.setSelectedIndex(k);
        this.revalidateValidState();
    }

    public void setUserObject(massertion as) {
        unselectAll();
        ass = as;

        if (as != null) {
            bt.setSelectedIndex(as.getAssertionType());
            for (int i = 0; i < as.getobjNum(); i++)
                this.selectAPoint((GEPoint) as.getObject(i));
        } else {
            bt.setSelectedIndex(-1);
        }


    }

    public mobject getUserObject() {
        if (ass == null)
            ass = new massertion(bt.getSelectedIndex());
        else
            ass.setAssertionType(bt.getSelectedIndex());
        ass.clearObjects();
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox<GEPoint> b = vlist.get(i);
            if (b.isEnabled() && b.isVisible() && b.getSelectedIndex() >= 0) {
                ass.addObject((GEPoint) b.getSelectedItem());
            } else
                break;
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox<GEPoint> b = vlist1.get(i);
            if (b.isEnabled() && b.isVisible() && b.getSelectedIndex() >= 0) {
                ass.addObject((GEPoint) b.getSelectedItem());
            } else
                break;
        }
        return ass;
    }

    public void update() {
	ArrayList<GEPoint> list = new ArrayList<GEPoint>();
	gxInstance.dp.getPointList(list);
        setPoints(list);
    }

    private void setLtext1Value(boolean t) {
        if (t) {
            ltext1.setText("");
            ltext1.setIcon(icon_Right);
        } else {
            ltext1.setText("");
            ltext1.setIcon(icon_Wrong);
        }
    }

    public int getPointsCount() {
        JComboBox<GEPoint> b = vlist.get(0);
        return b.getItemCount();
    }

    public void setPoints(ArrayList<GEPoint> v) {
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox<GEPoint> b = vlist.get(i);
            b.removeAllItems();
            for (int j = 0; j < v.size(); j++) {
                GEPoint obj = v.get(j);
                b.addItem(obj);
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox<GEPoint> b = vlist1.get(i);
            b.removeAllItems();
            for (int j = 0; j < v.size(); j++) {
                GEPoint p = v.get(j);
                b.addItem(p);
            }
        }
    }

    private int ptLeftTobeSelect() {
        int n = this.getStatePointsCount();

        for (int i = 0; i < vlist.size(); i++) {
            JComboBox<GEPoint> b = vlist.get(i);
            if (b.isEnabled() && b.getSelectedIndex() >= 0) {
                n--;
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox<GEPoint> b = vlist1.get(i);
            if (b.isEnabled() && b.getSelectedIndex() >= 0) {
                n--;
            }
        }
        return n;
    }

    public void revalidateValidState() {
        if (inputFinished()) {
            boolean v = checkValid();
            setLtext1Value(v);
            if (v)
                bt.setEnabled(true);
            if (v == false) {
                showCorrentOrder();
            }
        } else
            ltext1.setIcon(icon_Question);
    }

    public void itemStateChanged(ItemEvent e) {
        if (!this.isVisible()) return;

        Object source = e.getSource();
        if (source == bt) {
            this.unselectAll();
            int id = bt.getSelectedIndex();
            setItemChanged(id);
            if (id == bt.getItemCount() - 1) {
            }
        }
        showTipText();
        if (inputFinished()) {
            createAssertion();
            boolean v = checkValid();
            setLtext1Value(v);
            asspane.setAssertion(ass_show);
            if (v)
                bt.setEnabled(true);
            if (v == false) {
                showCorrentOrder();
            }
        } else
            ltext1.setIcon(icon_Question);

        updateBState();

    }

    public void updateBState() {
        if (ipanel != null) {
            if (inputFinished() || bt.getSelectedIndex() == massertion.CONVEX)
                ipanel.setBState(true);
            else
                ipanel.setBState(false);
        }
    }

    private boolean createAssertion() {
        if (ass_show == null)
            ass_show = new massertion(bt.getSelectedIndex());
        else
            ass_show.setAssertionType(bt.getSelectedIndex());

        ass_show.clearObjects();

        for (int i = 0; i < vlist.size(); i++) {
            JComboBox<GEPoint> b = vlist.get(i);
            if (b.isEnabled() && b.isVisible()) ass_show.addObject((GEPoint) b.getSelectedItem());
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox<GEPoint> b = vlist1.get(i);
            if (b.isEnabled() && b.isVisible()) ass_show.addObject((GEPoint) b.getSelectedItem());
        }
        return ass_show.checkValid();
    }

    private boolean inputFinished() {
//        return 0 == ptLeftTobeSelect();
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox<GEPoint> b = vlist.get(i);
            if (b.isEnabled() && b.getSelectedIndex() < 0) {
                return false;
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox<GEPoint> b = vlist1.get(i);
            if (b.isEnabled() && b.getSelectedIndex() < 0) {
                return false;
            }
        }
        return true;
    }

    private void showTipText() {
        int n = ptLeftTobeSelect();

        if (n == 0) {
        } else {
            if (n > 0)
                asspane.setText("(" + n + " points left)");
            else
                asspane.setText("please select");
            asspane.repaint();
        }
    }

    public void selectAPoint(GEPoint p) {

        for (int i = 0; i < vlist.size(); i++) {
            JComboBox<GEPoint> b = vlist.get(i);
            if (b.isEnabled() && b.getSelectedIndex() < 0) {
                b.setSelectedItem(p);
                return;
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox<GEPoint> b = vlist1.get(i);
            if (b.isEnabled() && b.getSelectedIndex() < 0) {
                b.setSelectedItem(p);
                return;
            }
        }
        asspane.repaint();
//        this.repaint();
    }

    private void unselectAll() {
        for (JComboBox<GEPoint> b : vlist) {
            b.setSelectedIndex(-1);
        }
        for (JComboBox<GEPoint> b : vlist1) {
            b.setSelectedIndex(-1);
        }
    }

    private int getStatePointsCount() {
        switch (bt.getSelectedIndex()) {
            case massertion.COLL:
            case massertion.MID:
            case massertion.R_TRIANGLE:
            case massertion.ISO_TRIANGLE:
            case massertion.R_ISO_TRIANGLE:
            case massertion.BETWEEN:
            case massertion.EQ_TRIANGLE:
                return 3;
            case massertion.PARA:
            case massertion.PERP:
            case massertion.EQDIS:
            case massertion.CYCLIC:
            case massertion.DISLESS:
            case massertion.PERPBISECT:
            case massertion.PARALLELOGRAM:
                return 4;
            case massertion.EQANGLE:
            case massertion.SIM:
            case massertion.CONG:
            case massertion.ANGLESS:
            case massertion.CONCURRENT:
                return 6;
        }
        return -1;
    }

    private void setItemChanged(int id) {
        switch (id) {
            case massertion.COLL:
            case massertion.MID:
            case massertion.R_TRIANGLE:
            case massertion.ISO_TRIANGLE:
            case massertion.R_ISO_TRIANGLE:
            case massertion.EQ_TRIANGLE:
                this.setVisibleBox(3);
                break;

            case massertion.PARA:
            case massertion.PERP:
            case massertion.EQDIS:
            case massertion.DISLESS:
            case massertion.PERPBISECT:
            case massertion.OPPOSITE_SIDE:
            case massertion.SAME_SIDE:
                this.setVisibleBox1(4);
                break;
            case massertion.CYCLIC:
            case massertion.PARALLELOGRAM:
            case massertion.TRAPEZOID:
            case massertion.RECTANGLE:
            case massertion.SQUARE:
                this.setVisibleBox(4);
                break;
            case massertion.EQANGLE:
            case massertion.SIM:
            case massertion.CONG:
            case massertion.ANGLESS:
            case massertion.CONCURRENT:
                this.setVisibleBox1(6);
                break;
            case massertion.ANGLE_INSIDE:
            case massertion.ANGLE_OUTSIDE:
            case massertion.TRIANGLE_INSIDE:

                this.setVisibleBox2(3);
                break;
            case massertion.BETWEEN:
                this.setVisibleBox2(2);
                break;
            case massertion.PARA_INSIDE:
                this.setVisibleBox2(4);
                break;
            case massertion.CONVEX:
                this.setVisibleBox1(8);
                break;
            default:
                UtilityMiscellaneous.print("massertion " + id + " not found");
                break;

        }
    }

    public void actionPerformed(ActionEvent e) {
        //String command = e.getActionCommand();
        Object obj = e.getSource();
        if (obj == bbok) {
            this.unselectAll();
            ass.clearObjects();
            ass.addAll(ass_temp);
            int n = ass_temp.getobjNum();
            for (int i = 0; i < n; i++) {
                GEPoint pt = (GEPoint) ass_temp.getObject(i);
                ass.addObject(pt);
                this.selectAPoint(pt);
            }
        } else {
        }
        bpanel.setVisible(false);

    }

    private void setVisibleBox2(int num) {
        int k = 1;
        for (JComboBox<GEPoint> obj : vlist) {
        	obj.setEnabled(k > 0);
        	obj.setVisible(k-- > 0);
        }

        k = num;
        for (JComboBox<GEPoint> obj : vlist1) {
        	obj.setEnabled(k > 0);
        	obj.setVisible(k-- > 0);
        }
    }

    private void setVisibleBox1(int num) {
        int k = num / 2;
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox<GEPoint> obj = vlist.get(i);
            if (i < k) {
                obj.setEnabled(true);
                obj.setVisible(true);
            } else {
                obj.setEnabled(false);
                obj.setVisible(false);
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox<GEPoint> obj = vlist1.get(i);
            if (i < k) {
                obj.setEnabled(true);
                obj.setVisible(true);
            } else {
                obj.setEnabled(false);
                obj.setVisible(false);
            }
        }
    }

    private void setVisibleBox(int num) {
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox<GEPoint> obj = vlist.get(i);
            if (i < num) {
                obj.setEnabled(true);
                obj.setVisible(true);
            } else {
                obj.setEnabled(false);
                obj.setVisible(false);
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox<GEPoint> obj = vlist1.get(i);
            obj.setEnabled(false);
            obj.setVisible(false);
        }
    }

    private String vs(int id1, int id2) {
        if (id1 == 0) {
            JComboBox<GEPoint> box = (vlist.get(id2));
            if (box.getItemCount() <= id2)
                return "";
            else {
                Object obj = box.getSelectedItem();
                if (obj == null)
                    return null;
                else
                    return obj.toString();
            }

        } else {
            JComboBox<GEPoint> box = (vlist1.get(id2));
            if (box.getItemCount() <= id2)
                return "";
            else {
                Object obj = box.getSelectedItem();
                if (obj == null)
                    return null;
                else
                    return obj.toString();
            }
        }

    }

    private GEPoint vspt(int id1, int id2) {
        if (id1 == 0) {
            JComboBox<GEPoint> box = (vlist.get(id2));
            if (box.getItemCount() <= id2)
                return null;
            else {
                Object obj = box.getSelectedItem();
                if (obj == null)
                    return null;
                else
                    return (GEPoint) obj;
            }
        } else {
            JComboBox<GEPoint> box = (vlist1.get(id2));
            if (box.getItemCount() <= id2)
                return null;
            else {
                Object obj = box.getSelectedItem();
                if (obj == null)
                    return null;
                else
                    return (GEPoint) obj;
            }
        }
    }

    private boolean checkValid() {
        int id = bt.getSelectedIndex();
        if (!inputFinished()) return false;
        switch (id) {
            case 0:
                return DrawPanelBase.check_Collinear(vspt(0, 0), vspt(0, 1), vspt(0, 2));
            case 1:
                return DrawPanelBase.check_para(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case 2:
                return DrawPanelBase.check_perp(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case 3:
                return DrawPanelBase.check_mid(vspt(0, 0), vspt(0, 1), vspt(0, 2));
            case 4:
                return DrawPanelBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case 5:
                return DrawPanelBase.check_cyclic(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 3));
            case 6:
                return DrawPanelBase.check_eqangle(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2));
            case 7:
                return DrawPanelBase.check_congtri(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2));

            case 8:
                return DrawPanelBase.check_simtri(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2));

            case 9:
                return DrawPanelBase.check_distance_less(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case 10:
                return DrawPanelBase.check_angle_less(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2));
            case 11:
//                return drawbase.check_concurrent();
            case 12:
                return DrawPanelBase.check_perp(vspt(0, 0), vspt(1, 1), vspt(1, 0), vspt(0, 1));
            case 13:
                return DrawPanelBase.check_para(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 3))
                        && DrawPanelBase.check_para(vspt(0, 0), vspt(0, 3), vspt(0, 1), vspt(0, 2));
            case massertion.R_TRIANGLE:
                return DrawPanelBase.check_perp(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2));
            case massertion.ISO_TRIANGLE:
                return DrawPanelBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2));
            case massertion.R_ISO_TRIANGLE:
                return DrawPanelBase.check_perp(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2))
                        && DrawPanelBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2));
            case massertion.EQ_TRIANGLE:
                return DrawPanelBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2))
                        && DrawPanelBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(0, 1), vspt(0, 2));
            case massertion.TRAPEZOID:
                return DrawPanelBase.check_para(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 3));
            case massertion.RECTANGLE:
                return DrawPanelBase.check_para(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 3))
                        && DrawPanelBase.check_perp(vspt(0, 0), vspt(0, 1), vspt(0, 1), vspt(0, 2));
            case massertion.SQUARE:
                return DrawPanelBase.check_para(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 3))
                        && DrawPanelBase.check_perp(vspt(0, 0), vspt(0, 1), vspt(0, 1), vspt(0, 2))
                        && DrawPanelBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2));
            case massertion.BETWEEN:
                return DrawPanelBase.check_between(vspt(0, 0), vspt(1, 0), vspt(1, 1));
            case massertion.ANGLE_INSIDE:
                return DrawPanelBase.check_angle_less(vspt(0, 0), vspt(1, 1), vspt(1, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2))
                        && DrawPanelBase.check_angle_less(vspt(0, 0), vspt(1, 1), vspt(1, 0), vspt(1, 0), vspt(1, 1), vspt(1, 2));

            case massertion.ANGLE_OUTSIDE:
                return !(DrawPanelBase.check_angle_less(vspt(0, 0), vspt(1, 1), vspt(1, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2))
                        && DrawPanelBase.check_angle_less(vspt(0, 0), vspt(1, 1), vspt(1, 0), vspt(1, 0), vspt(1, 1), vspt(1, 2)));
            case massertion.TRIANGLE_INSIDE:
                return DrawPanelBase.check_triangle_inside(vspt(0, 0), vspt(1, 0), vspt(1, 1), vspt(1, 2));
            case massertion.PARA_INSIDE:
                return DrawPanelBase.check_triangle_inside(vspt(0, 0), vspt(1, 0), vspt(1, 1), vspt(1, 2))
                        || DrawPanelBase.check_triangle_inside(vspt(0, 0), vspt(1, 0), vspt(1, 2), vspt(1, 3));

            case massertion.OPPOSITE_SIDE:
                return !DrawPanelBase.check_same_side(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case massertion.SAME_SIDE:
                return DrawPanelBase.check_same_side(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case massertion.CONVEX:
                return true;

        }
        return true;
    }


    public massertion getProveM() {
        int id = bt.getSelectedIndex();
        if (id < 0) return null;
        massertion ass = new massertion(id);
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox<GEPoint> b = vlist.get(i);
            if (b.isEnabled() && b.getSelectedIndex() < 0) {
                ass.addItem(b.getSelectedItem());
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox<GEPoint> b = vlist1.get(i);
            if (b.isEnabled() && b.getSelectedIndex() < 0) {
                ass.addItem(b.getSelectedItem());
            }
        }
        return ass;
    }

    public String getProve() {
        JComboBox<GEPoint> box1 = vlist.get(0);
        if (box1.getItemCount() == 0) return "";
        if (!this.inputFinished()) return "";

        int id = bt.getSelectedIndex();

        switch (id) {
            case massertion.COLL:
                return Cm.PC_COLL + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + ";";
            case massertion.PARA:
                return Cm.PC_PARA + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(1, 0) + " " + vs(1, 1) + ";";
            case massertion.PERP:
                return Cm.PC_PERP + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(1, 0) + " " + vs(1, 1) + ";";
            case massertion.MID:
                return Cm.PC_MIDP + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + ";";
            case massertion.CYCLIC:
                return Cm.PC_CYCLIC + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + " " + vs(0, 3) + ";";
            case massertion.EQDIS:
                return Cm.PC_CONG + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(1, 0) + " " + vs(1, 1) + ";";
            case massertion.EQANGLE:
                return Cm.PC_ACONG + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + " " + vs(0, 3) + " " + vs(1, 0) + " " + vs(1, 1) + " " + vs(1, 2) + " " + vs(1, 3) + ";";
            case massertion.SIM:
                return Cm.PC_STRI + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + " " + vs(1, 0) + " " + vs(1, 1) + " " + vs(1, 2) + ";";
            case massertion.CONG:
                return Cm.PC_CTRI + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + " " + vs(1, 0) + " " + vs(1, 1) + " " + vs(1, 2) + ";";
        }
        return "Conclusion is Not Yet Supported";
    }

    public String getProveDescription() {
        JComboBox<GEPoint> box1 = vlist.get(0);
        if (box1.getItemCount() == 0) return "";
        if (!this.inputFinished()) return "";

        int id = bt.getSelectedIndex();

        switch (id) {
            case massertion.COLL:
                return vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + " are collinear;";
            case massertion.PARA:
                return vs(0, 0) + " " + vs(0, 1) + " is Parallel to " + vs(1, 0) + " " + vs(1, 1) + ";";
            case massertion.PERP:
                return vs(0, 0) + " " + vs(0, 1) + " is Perpendicular to " + vs(1, 0) + " " + vs(1, 1) + ";";
            case massertion.MID:
                return vs(0, 0) + " is the midpoint of " + vs(0, 1) + " " + vs(0, 2) + ";";
            case massertion.CYCLIC:
                return vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + " " + vs(0, 3) + " are cyclic;";
            case massertion.EQDIS:
                return vs(0, 0) + " " + vs(0, 1) + " equals to " + vs(1, 0) + " " + vs(1, 1) + ";";
            case massertion.EQANGLE:
                return Cm.s2078 + vs(0, 0) + vs(0, 1) + vs(0, 2) + " = " + Cm.s2078 + vs(1, 0) + vs(1, 1) + vs(1, 2);

            case massertion.SIM:
                return "tri " + vs(0, 0) + vs(0, 1) + vs(0, 2) + " is similiar to " + "tri " + vs(1, 0) + vs(1, 1) + vs(1, 2);
            case massertion.CONG:
                return "tri " + vs(0, 0) + vs(0, 1) + vs(0, 2) + " is equal to " + "tri " + vs(1, 0) + vs(1, 1) + vs(1, 2);
        }
        return "Conclusion is Not Yet Supported";
    }

    private void showCorrentOrder() {
        JComboBox<GEPoint> box1 = vlist.get(0);
        if (box1.getItemCount() == 0) return;
        if (!this.inputFinished()) return;

        int id = bt.getSelectedIndex();

        boolean t = false;

        if (id == massertion.SIM || id == massertion.CONG) {
            int i, j, k;
            i = j = k = 0;
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    if (i != j)
                        for (k = 0; k < 3; k++) {
                            if (i != k && j != k) {
                                if (id == massertion.SIM)
                                    t = DrawPanelBase.check_simtri(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, i), vspt(1, j), vspt(1, k));
                                else if (id == massertion.CONG)
                                    t = DrawPanelBase.check_congtri(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, i), vspt(1, j), vspt(1, k));
                                if (t)
                                    break;
                            }
                        }
                    if (t) break;
                }
                if (t) break;
            }
            if (t) {
                if (ass_temp == null)
                    ass_temp = new massertion(bt.getSelectedIndex());
                ass_temp.clearObjects();
                ass_temp.addObject(vspt(0, 0));
                ass_temp.addObject(vspt(0, 1));
                ass_temp.addObject(vspt(0, 2));
                ass_temp.addObject(vspt(1, i));
                ass_temp.addObject(vspt(1, j));
                ass_temp.addObject(vspt(1, k));
                asspane_temp.setAssertion(ass_temp);
                bpanel.setVisible(true);
            }
        }


    }

    public void cancel() {
        ass = null;
        this.unselectAll();
    }

}
