package wprover;

import gprover.cndg;
import gprover.cons;
import gprover.gib;
import gprover.gterm;

import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

import maths.PolyBasic;
import maths.TMono;

public class DialogNondegeneracy extends DialogBase implements ActionListener, MouseMotionListener, MouseListener,
        ChangeListener, TableModelListener, ListSelectionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1434997182711111052L;
	private DrawPanel dp;
    private static int WD = 700;
    private static int HD = 500;
    //private gterm gt;
    JTable tabel1, tabel2, tabel3;
    ndgTableModel model1;
    ndgTableModel1 model2, model3;
    DefaultListSelectionModel lselect1;

    private JSplitPane spane;
    private JTabbedPane tt;
    JSplitPane sptop, sp1;

    private DrawPanelFrame gxInstance;

    public DialogNondegeneracy(DrawPanelFrame gx, gterm gt, DrawPanel dp) {
        super(gx.getFrame(), DrawPanelFrame.getLanguage(1116, "Nondegenerate Conditions"));
        gxInstance = gx;

        //this.gt = gt;
        this.dp = dp;

        tt = new JTabbedPane(SwingConstants.BOTTOM, JTabbedPane.WRAP_TAB_LAYOUT);


        tt.addChangeListener(this);
        this.getConstructions();

        model1 = new ndgTableModel();
        String c1 = DrawPanelFrame.getLanguage(1138, "The Simplified Nondegenerate Conditions");
        String c2 = DrawPanelFrame.getLanguage(1139, "The Final Nondegenerate Conditions");
        model2 = new ndgTableModel1(c1);
        model3 = new ndgTableModel1(c2);
        tabel1 = new JTable(model1);
        tabel2 = new JTable(model2);
        tabel3 = new JTable(model3);

        tabel1.setDragEnabled(true);


        tabel1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model1.addTableModelListener(this);
        lselect1 = new DefaultListSelectionModel();
        lselect1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabel1.setSelectionModel(lselect1);
        lselect1.addListSelectionListener(this);

//        super.getPreferredSize()
//        tabel1.setBorder(BorderFactory.createTitledBorder(""));
        JScrollPane pane = new JScrollPane(tabel1, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER) {
            /**
			 * 
			 */
			private static final long serialVersionUID = -3412378034959285677L;

			public Dimension getPreferredSize() {
                Dimension dm = tabel1.getPreferredSize();
                dm.setSize(dm.getWidth(), dm.getHeight() + 30);
                return dm;
            }

        };
        JScrollPane pane2 = new JScrollPane(tabel2, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER) {
            /**
			 * 
			 */
			private static final long serialVersionUID = -8546563629488119794L;

			public Dimension getPreferredSize() {
                Dimension dm = tabel2.getPreferredSize();
                dm.setSize(dm.getWidth(), dm.getHeight() + 30);
                return dm;
            }

        };
        JScrollPane pane3 = new JScrollPane(tabel3, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 7065984044462963341L;

			public Dimension getPreferredSize() {
                Dimension dm = tabel3.getPreferredSize();

                dm.setSize(dm.getWidth(), dm.getHeight() + 30);
                return dm;
            }
        };

        sp1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pane2, pane3);

        JSplitPane panelx = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pane, sp1);
        sptop = panelx;

        this.getContentPane().add(new JScrollPane(panelx));
        panelx.resetToPreferredSizes();
        panelx.revalidate();
        spane = panelx;

        this.setSize(WD, HD);
        sp1.setDividerLocation(WD / 2 - 10);
        sp1.resetToPreferredSizes();

        int w = gx.getWidth();
        int h = gx.getHeight();
        int x = gx.getX();
        int y = gx.getY();
        this.setLocation(x + w / 2 - WD / 2, y + h / 2 - HD / 2);

    }

    public void setSelectIndex(int t) {
        tt.setSelectedIndex(t);
    }

    public void valueChanged(ListSelectionEvent e) {
        int n = tabel1.getSelectedRow();
        cons c = (cons) model1.getValueAt(n, 0);
        if (gxInstance != null)
            gxInstance.getProofPanel().setSelectedConstruction(c);
        Object o = model1.getValueAt(n, 1);
        if (o != null && o instanceof cndg) {

            cndg dd = (cndg) o;

            for (int i = 0; i < model2.getRowCount(); i++) {
                if (model2.getValueAt(i, 0) == dd.equ) {
                    tabel2.getSelectionModel().setSelectionInterval(i, i);
                    break;
                }
            }
        }
    }


    public void tableChanged(TableModelEvent e) {

    }

    public void stateChanged(ChangeEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void actionPerformed(ActionEvent e) {
        this.setVisible(false);
    }

    public void getConstructions() {
    }


    public void setValue(ArrayList<cons> v1, ArrayList<cndg> v2, ArrayList<cndg> v3, ArrayList<cndg> v4) {   // cndg, m1,m2
        model1.reset();
        model2.reset();
        model3.reset();

        for (int i = 0; i < v1.size(); i++) {
            cons c = v1.get(i);
            cndg d = fd_ndg(c, v2);
            model1.addElement(c, d);
        }

        for (int i = 0; i < v3.size(); i++) {
            cndg c = v3.get(i);
            model2.addElement(c);
        }


        for (int i = 0; i < v4.size(); i++) {
            cndg c = v4.get(i);
            model3.addElement(c);
        }
        model3.addElement(" ");

        for (int i = 0; i < v4.size(); i++) {
            cndg c = v4.get(i);
            model3.addElement(PolyBasic.getAllPrinted(getTMono(c)));
        }


        spane.resetToPreferredSizes();
    }

    public int dxindex(int n) {
        GEPoint pt = dp.fd_point(n);
        if (pt == null)
            return 0;
        return pt.x1.xindex;
    }

    public int dyindex(int n) {
        GEPoint pt = dp.fd_point(n);
        return pt.y1.xindex;
    }

    public TMono getTMono(cndg d) {
        if (d == null)
            return null;

        switch (d.type) {
            case gib.NDG_COLL:
                return GeoPoly.collinear(dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]), dxindex(d.p[2]), dyindex(d.p[2]));
            case gib.NDG_NEQ:
            case gib.NDG_NON_ISOTROPIC:
                return GeoPoly.perpendicular(dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]),
                        dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]));
            case gib.NDG_PARA:
                return GeoPoly.parallel(dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]),
                        dxindex(d.p[2]), dyindex(d.p[2]), dxindex(d.p[3]), dyindex(d.p[3]));
            case gib.NDG_PERP:
                return GeoPoly.perpendicular(dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]),
                        dxindex(d.p[2]), dyindex(d.p[2]), dxindex(d.p[3]), dyindex(d.p[3]));

            case gib.NDG_CYCLIC:
                return GeoPoly.cyclic(dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]),
                        dxindex(d.p[2]), dyindex(d.p[2]), dxindex(d.p[3]), dyindex(d.p[3]));
            case gib.NDG_CONG:
                return GeoPoly.eqdistance(dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]),
                        dxindex(d.p[2]), dyindex(d.p[2]), dxindex(d.p[3]), dyindex(d.p[3]));
            default:
                System.out.println("Error NDG type: " + d.type);
        }
        return null;
    }

    public static cndg fd_ndg(cons c, ArrayList<cndg> v2) {
        for (cndg d : v2) {
            if (d.dep == c)
                return d;
        }
        return null;
    }

    class ndgTableModel extends DefaultTableModel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 2677245548322653740L;
		String c1 = DrawPanelFrame.getLanguage(1137, "Construction");
        String c2 = DrawPanelFrame.getLanguage(1116, "Nondegenerate Condition");

        Object[] cons = new Object[100];
        Object[] ndgs = new Object[100];
        int num = 0;


        public ndgTableModel() {
        }

        public void addElement(Object o1, Object o2) {
            if (o2 == null)
                o2 = "";

            cons[num] = o1;
            ndgs[num] = o2;
            num++;
            this.fireTableDataChanged();

        }

        public void reset() {
            num = 0;
            this.fireTableDataChanged();
        }

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return num;
        }

        public String getColumnName(int col) {
            if (col == 0)
                return c1;
            else return c2;
        }

        public Object getValueAt(int row, int col) {
            if (col == 0)
                return cons[row];
            else return ndgs[row];
        }

        public Class<? extends Object> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void setValueAt(Object value, int row, int col) {
        }
    }


    class ndgTableModel1 extends DefaultTableModel {
        /**
		 * 
		 */
		private static final long serialVersionUID = -1351590922334483701L;

		String c1 = null;

        Object[] ndgs = new Object[100];
        int num = 0;


        public ndgTableModel1() {
        }

        public ndgTableModel1(String s) {
            c1 = s;
        }

        public void addElement(Object o1) {
            ndgs[num] = o1;
            num++;
        }

        public void reset() {
            num = 0;
            this.fireTableDataChanged();
        }

        public int getColumnCount() {
            return 1;
        }

        public int getRowCount() {
            return num;
        }

        public String getColumnName(int col) {
            return c1;
        }

        public Object getValueAt(int row, int col) {
            return ndgs[row];
        }

        public Class<? extends Object> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void setValueAt(Object value, int row, int col) {
        }
    }

}