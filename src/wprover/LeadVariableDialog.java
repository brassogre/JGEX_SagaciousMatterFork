package wprover;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import maths.*;

public class LeadVariableDialog extends JBaseDialog implements MouseListener, ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8390958691846926357L;
	private JTabbedPane tpane;
    private JTable table;
    private LVTableModel model;
    private InspectPanel ipane = null;
    private ArrayList<ArrayList<Object>> vdata = new ArrayList<ArrayList<Object>>();
    private GExpert gxInstance;
    private JButton bdtail;
    protected static GeoPoly poly = GeoPoly.getPoly();
    protected static CharacteristicSetMethod charset = CharacteristicSetMethod.getinstance();

    public LeadVariableDialog(GExpert f) {

        super(f.getFrame());
        gxInstance = f;
        setTitle(getLanguage(154, "Leading Variable"));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        table = new JTable((model = new LVTableModel()));
        table.addMouseListener(this);
        table.setDragEnabled(true);
        TableColumn c1 = table.getColumnModel().getColumn(0);
        c1.setMaxWidth(60);
        TableColumn c2 = table.getColumnModel().getColumn(1);
        c2.setMaxWidth(60);
        TableColumn c3 = table.getColumnModel().getColumn(2);
        c3.setMaxWidth(60);
        JScrollPane pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (table.getSelectedRow() != -1)
                    bdtail.setEnabled(true);
                else bdtail.setEnabled(false);
            }

        });
        tpane = new JTabbedPane(JTabbedPane.BOTTOM);
        tpane.addTab(getLanguage("Variables"), pane);
        panel.add(tpane);

        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.RIGHT)) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 6659550092271205939L;

			public Dimension getMaximumSize() {
                Dimension dm = super.getPreferredSize();
                dm.setSize(Integer.MAX_VALUE, dm.getHeight());
                return dm;
            }
        };
        JButton bb = new JButton(getLanguage(241, "Reduce"));
        JButton b = new JButton(getLanguage(242, "Detail"));
        JButton b1 = new JButton(getLanguage(243, "Reload"));
        JButton b2 = new JButton(getLanguage("Close"));
        bb.setActionCommand("Reduce");
        b.setActionCommand("Detail");
        b1.setActionCommand("Reload");
        b2.setActionCommand("Close");

        bb.addActionListener(this);
        b.addActionListener(this);
        b1.addActionListener(this);
        b2.addActionListener(this);
        p2.add(Box.createHorizontalGlue());
        p2.add(bb);
        p2.add(b);
        p2.add(b1);
        p2.add(b2);
        bdtail = b;
        b.setEnabled(false);
        panel.add(p2);

//        tpane.addTab(getLanguage(244, "NDGs"), new JScrollPane(new ndgPanel(),
//                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        setSize(600, 420);
        setLocation(f.getX(), f.getY());
        getContentPane().add(panel);
    }


    String getLanguage(String s) {
        if (gxInstance != null)
            return GExpert.getLanguage(s);
        return s;
    }

    public String getLanguage(int n, String s) {
        String s1 = "";
        if (gxInstance != null)
            s1 = GExpert.getLanguage(n);
        if (s1 != null && s1.length() > 0)
            return s1;
        return s;
    }

    public void loadVariable(ArrayList<GEPoint> s, boolean r) {
        try {
            loadAllPoints(s, r);
        } catch (OutOfMemoryError ee) {
            JOptionPane.showMessageDialog(gxInstance, ee.getMessage() + "\nOut of Memory");
        }
    }

    public void loadAllPoints(ArrayList<GEPoint> v, boolean r) {
//        vdata.clear();
        ArrayList<ArrayList<Object>> vvdata = new ArrayList<ArrayList<Object>>();

        for (GEPoint p : v) {
            param p1 = p.x1;
            param p2 = p.y1;
            if (p1 != null || p2 != null) {
                ArrayList<Object> o1 = new ArrayList<Object>();
                ArrayList<Object> o2 = new ArrayList<Object>();
                o1.add(p);
                o2.add("");

                o1.add(p1);
                o2.add(p2);
                o1.add(new Integer(PolyBasic.plength(p1.m)).toString());
                o2.add(new Integer(PolyBasic.plength(p2.m)).toString());

                if (!r) {
                    if (p1.m != null)
                        o1.add(PolyBasic.printSPoly(p1.m));
                    else o1.add("");
                    if (p2.m != null)
                        o2.add(PolyBasic.printSPoly(p2.m));
                    else o2.add("");
                } else {
                    param[] pm = gxInstance.dp.parameter;
                    if (p1.m != null)
                        o1.add(PolyBasic.printSPoly(PolyBasic.reduce(PolyBasic.pcopy(p1.m), pm)));
                    else o1.add("");
                    if (p2.m != null)
                        o2.add(PolyBasic.printSPoly(PolyBasic.reduce(PolyBasic.pcopy(p2.m), pm)));
                    else o2.add("");
//                    o2.add(poly.printSPoly(p2.m));
                }
                vvdata.add(o1);
                vvdata.add(o2);
            }
        }
        vdata.clear();
        vdata.addAll(vvdata);
        model.setDataList(vvdata);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Reload")) {
	    ArrayList<GEPoint> list = new ArrayList<GEPoint>();
	    gxInstance.dp.getPointList(list);
            this.loadVariable(list, false);
        } else if (s.equals("Close")) {
            this.setVisible(false);
        } else if (s.equals("Detail")) {
            inspectTerm();
        } else if (s.equals("Reduce")) {
            if (tpane.getSelectedIndex() == 0) {
		ArrayList<GEPoint> list = new ArrayList<GEPoint>();
		gxInstance.dp.getPointList(list);
                this.loadVariable(list, true);
	    } else {
                ipane.reduce();
	    }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == table) {
            if (e.getClickCount() >= 2)
                inspectTerm();
        }
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


    public void inspectTerm() {

        if (tpane.getTabCount() > 1) {
            tpane.removeTabAt(1);
            ipane = null;
        }

        int n = table.getSelectedRow();
        if (n < 0 || n >= model.getRowCount()) return;
        ArrayList<Object> v = vdata.get(n);

        Object p = getPoints(n);

        if (ipane == null) {
            ipane = new InspectPanel();
            tpane.addTab("" + p, new JScrollPane(ipane,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        } else
            tpane.setTitleAt(1, "" + p);
        ipane.loadValue(n, p, v);
        tpane.setSelectedIndex(1);
    }

    public Object getPoints(int n) {
        if (n % 2 != 0)
            n -= 1;
        ArrayList<Object> v1 = vdata.get(n);
        return v1.get(0);
    }

    class LVTableModel extends DefaultTableModel {
        /**
		 * 
		 */
		private static final long serialVersionUID = -7098982296077402860L;
		ArrayList<ArrayList<Object>> vlist = new ArrayList<ArrayList<Object>>();
        ArrayList<ArrayList<Object>> vdlist = new ArrayList<ArrayList<Object>>();

        public LVTableModel() {
        	ArrayList<Object> v = new ArrayList<Object>();
            v.add(getLanguage(245, "Name"));
            v.add(getLanguage(246, "Variable"));
            v.add(getLanguage(247, "Length"));
            v.add(getLanguage(248, "Polynomial"));
            vlist.add(v);
        }

        public void setDataList(ArrayList<ArrayList<Object>> v) {
            vdlist.clear();
            vdlist.addAll(v);
            fireTableDataChanged();
        }

        public int getColumnCount() {
            if (vlist == null)
                return 0;
            return vlist.size();
        }

        public int getRowCount() {
            if (vdlist == null)
                return 0;
            return vdlist.size();
        }

        public String getColumnName(int col) {
            return vlist.get(col).toString();
        }

        public Object getValueAt(int row, int col) {
            ArrayList<Object> v = vdlist.get(row);
            return v.get(col);
        }

        public Class<?> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void setValueAt(Object value, int row, int col) {
        }
    }

    class InspectPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 4711579352784827467L;
		private JTable table1;
        private JEditorPane pane;
        private inspectTableModel model1;
        private TextPopupMenu pop;
        private TMono mx = null;

        public InspectPanel() {
            super();
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            table1 = new JTable((model1 = new inspectTableModel()));
            TableColumn b = table1.getColumnModel().getColumn(0);
            b.setPreferredWidth(100);
            b.setWidth(100);
            b.setMaxWidth(100);
            this.setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 1));
            this.add(table1);

            pane = new JEditorPane();
            pop = new TextPopupMenu();

            pane.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        String select = pane.getSelectedText();
                        if (select != null && select.length() > 0)
                            pop.setStatus(true);
                        else pop.setStatus(false);
                        pop.show(pane, e.getX(), e.getY());
                    }
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }
            });

            pane.setAutoscrolls(false);
            pane.setBorder(BorderFactory.createTitledBorder(getLanguage(248, "Polynomial")));
            this.add(pane);
        }

        public void reduce() {
            if (mx != null) {
                TMono m = PolyBasic.reduce(PolyBasic.p_copy(mx), gxInstance.dp.parameter);
                String s1 = PolyBasic.getExpandedPrint(m);
                pane.setText(s1);
            }
        }

        public void loadValue(int n, Object o, ArrayList<Object> v) {
            Object o1 = o;
            Object o2 = v.get(1);
            Object o3 = v.get(2);
            //Object o4 = v.get(3);
            Object[] ls = new Object[3];
            ls[0] = o1;
            ls[1] = o2;
            ls[2] = o3;
            model1.setValueAt(o1, 0, 1);
            model1.setValueAt(o2, 1, 1);
            model1.setValueAt(o3, 2, 1);

            GEPoint p = (GEPoint) o;
            TMono m = null;
            if (n % 2 == 0)
                m = p.x1.m;
            else m = p.y1.m;
            mx = m;

//            m = basic.reduce(m, gxInstance.dp.parameter);
            String s1 = PolyBasic.getAllPrinted(m);//.getExpandedPrint(m);
            pane.setText(s1);
        }

        class TextPopupMenu extends JPopupMenu {
            /**
			 * 
			 */
			private static final long serialVersionUID = -8696787341997631321L;
			JMenuItem m1, m2, m3, m4;

            public TextPopupMenu() {
                m1 = new JMenuItem(getLanguage(249, "Cut"));
                m2 = new JMenuItem(getLanguage(250, "Copy"));
                m3 = new JMenuItem(getLanguage(251, "Paste"));
                m4 = new JMenuItem(getLanguage(252, "Delete"));
                ActionListener ls = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String s = e.getActionCommand();
                        if (s.equals("Cut"))
                            pane.cut();
                        else if (s.equals("Copy"))
                            pane.copy();
                        else if (s.equals("Paste"))
                            pane.paste();
                        else if (s.equals("Delete"))
                            pane.cut();
                    }
                };
                m1.setActionCommand("Cut");
                m2.setActionCommand("Copy");
                m3.setActionCommand("Paste");
                m4.setActionCommand("Delete");
                m1.addActionListener(ls);
                m2.addActionListener(ls);
                m3.addActionListener(ls);
                m4.addActionListener(ls);
                this.add(m1);
                this.add(m2);
                this.add(m3);
                this.add(m4);
            }


            public void setStatus(boolean r) {
                if (r) {
                    m1.setEnabled(true);
                    m2.setEnabled(true);
                } else {
                    m1.setEnabled(false);
                    m2.setEnabled(false);
                }
            }

        }
    }


    class inspectTableModel extends AbstractTableModel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 4322085185623836810L;
		private String[] names = {"", ""};
        private Object[][] data = {
                {getLanguage("Point"), new String()},
                {getLanguage(246, "Variable"), new String()},
                {getLanguage(247, "Length"), new String()},
        };

        public int getColumnCount() {
            return names.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return names[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public Class<?> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }


    class ndgPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 6751095939598408410L;
		private JTable tablen;
        private ndgModel modeln;
        //private ArrayList<TMono> vndgs = new ArrayList<TMono>();

        public ndgPanel() {
            super();

            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            tablen = new JTable((modeln = new ndgModel()));
            TableColumn b = tablen.getColumnModel().getColumn(0);
            b.setPreferredWidth(30);
            b.setWidth(30);
            b.setMaxWidth(30);
            this.setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 1));
            this.add(tablen);

            TPoly poly = gxInstance.dp.getPBList();

            TMono m1, m2;
            m1 = m2 = null;

            if (poly == null)
                return;
            int nn = poly.getPoly().x;

            ArrayList<TMono> v = new ArrayList<TMono>();
            while (poly != null) {
                m1 = poly.getPoly();
                if (m1 != null)
                    v.add(0, PolyBasic.p_copy(m1));
                poly = poly.next;
            }

            ArrayList<TMono> vlist = new ArrayList<TMono>();

            for (int n = 1; n < nn / 2 + 1; n++) {
                m1 = m2 = null;

                for (TMono m : v) {
                    if (m.x == 2 * n || m.x == 2 * n - 1) {
                        if (m1 == null)
                            m1 = m;
                        else m2 = m;
                    }
                }

                if (m1 != null && m2 != null) {
                    TMono t = PolyBasic.ll_delta(2 * n, m1, m2);
                    t = gxInstance.dp.reduce(t);
                    vlist.add(t);
                }
                if (m1 != null)
                    v.remove(m1);
                if (m2 != null)
                    v.remove(m2);
            }
            PolyBasic.ndg_reduce(vlist);
            for (TMono t : vlist) {
                modeln.addData(" " + PolyBasic.getExpandedPrint(t));
            }
        }


    }


    class ndgModel extends DefaultTableModel {
        /**
		 * 
		 */
		private static final long serialVersionUID = -4251923684577018685L;
		private ArrayList<Object> vlist = new ArrayList<Object>();

        public ndgModel() {
        }

        public void setDatalist(ArrayList<Object> v) {
            vlist.addAll(v);
        }

        public void addData(Object o) {
            if (o != null)
                vlist.add(o);
        }

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            if (vlist == null)
                return 0;
            return vlist.size();
        }

        public String getColumnName(int col) {
            return "";
        }

        public Object getValueAt(int row, int col) {
            if (col == 0)
                return new Integer(row + 1);
            else
                return vlist.get(row);
        }

        public Class<?> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 0)
                return false;
            else return true;
        }

        public void setValueAt(Object value, int row, int col) {
        }
    }
}

