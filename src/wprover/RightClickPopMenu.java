package wprover;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class RightClickPopMenu extends JPopupMenu implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4216200417324830232L;
	private GExpert gxInstance;
    private GraphicEntity cc;

    public RightClickPopMenu(GraphicEntity c, GExpert gx) {
        this.gxInstance = gx;
        this.cc = c;
        this.setForeground(Color.white);


        JMenuItem item = addAMenuItem(getLanguage(3205, "Cancel Action"), true);
        item.setActionCommand("Cancel Action");
        item.setMnemonic(KeyEvent.VK_ESCAPE);
        KeyStroke ctrlP = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        item.setAccelerator(ctrlP);
//        item = addAMenuItem(getLanguage(10, "Move"), true);
//        item.setActionCommand("Move");

        addFreezeMenu();
        this.addSeparator();

        int d = 0;
        if (c != null)
            d = c.getColorIndex();
        addAColorMenuItem(getLanguage(350, "Color"), c != null, d);

        addSpecificMenu(c);
        this.addSeparator();
        item = addAMenuItem(getLanguage(309, "Property"), c != null);
        item.setActionCommand("Property");
    }

    public String getLanguage(int n, String s) {
        if (gxInstance == null)
            return s;
        return GExpert.getLanguage(n, s);
    }

    public void addSpecificMenu(GraphicEntity c) {
        if (c == null) return;

        JMenuItem item;

        int t = c.get_type();
        switch (t) {
            case GraphicEntity.TEXT:
                item = addAMenuItem(getLanguage(4021, "Edit Text"), true);
                item.setActionCommand("Edit Text");
                addFontSizeMenuItem((GEText) c);
                break;
            case GraphicEntity.POINT: {
                GEPoint p = (GEPoint) cc;
                if (!p.isFrozen()) {
                    item = addAMenuItem(getLanguage(4022, "Freeze"), true);
                    item.setActionCommand("Freeze");
                } else {
                    item = addAMenuItem(getLanguage(4023, "Unfreeze"), true);
                    item.setActionCommand("Unfreeze");
                }
                if (gxInstance.dp.getTraceByPt(p) != null) {
                    item = addAMenuItem(getLanguage(4024, "Stop Trace"), true);
                    item.setActionCommand("Stop Trace");
                } else {
                    item = addAMenuItem(getLanguage(120, "Trace"), true);
                    item.setActionCommand("Trace");
                }
                item = addAMenuItem(getLanguage(4005, "X Coordinate"), true);
                item.setActionCommand("X Coordinate");
                item = addAMenuItem(getLanguage(4006, "Y Coordinate"), true);
                item.setActionCommand("Y Coordinate");
            }
            break;
            case GraphicEntity.LINE:
                item = addAMenuItem("Slope", true);
                item.setActionCommand("Slope");
                break;
            case GraphicEntity.CIRCLE:
                item = addAMenuItem(getLanguage(461, "Area"), true);
                item.setActionCommand("Area");

                item = addAMenuItem(getLanguage(460, "Girth"), true);
                item.setActionCommand("Girth");

                item = addAMenuItem(getLanguage(4004, "Radius"), true);
                item.setActionCommand("Radius");

                break;
            case GraphicEntity.POLYGON:
                item = addAMenuItem(getLanguage(461, "Area"), true);
                item.setActionCommand("Area");
                break;
        }
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Property"))
            gxInstance.dp.viewElement(cc);
        else if (command.equals("Edit Text")) {
            GEText t = (GEText) cc;
            Point p = t.getLocation();
            gxInstance.dp.dialog_addText(t, (int) p.getX(), (int) p.getY());
        } else if (command.equals("Color")) {
            JMenuItem it = (JMenuItem) e.getSource();
            Color c = it.getForeground();
            int ci = drawData.getColorIndex(c);
            cc.setColor(ci);
            gxInstance.d.repaint();
        } else if (command.equals("Move")) {
            gxInstance.setActionMove();
        } else if (command.equals("Cancel Action")) {
            gxInstance.onKeyCancel();
        } else if (command.equals("Freeze"))
            ((GEPoint) cc).setFrozen(true);
        else if (command.equals("Unfreeze"))
            ((GEPoint) cc).setFrozen(false);
        else if (command.equals("Stop Trace"))
            gxInstance.dp.stopTrack();
        else if (command.equals("Trace")) {
            gxInstance.dp.startTrackPt((GEPoint) cc);
        } else if (command.equals("X Coordinate")) {
            gxInstance.dp.addCalculationPX((GEPoint) cc);
        } else if (command.equals("Y Coordinate")) {
            gxInstance.dp.addCalculationPY((GEPoint) cc);

        } else if (command.equals("Area")) {

            if (cc instanceof GECircle)
                gxInstance.dp.addCalculationCircle((GECircle) cc, 0);
            else {
                GEPolygon cp = (GEPolygon) cc;
                if (cp.ftype == 1) {
                    GECircle c = gxInstance.dp.fd_circleOR((GEPoint) cp.getElement(0), (GEPoint) cp.getElement(1), (GEPoint) cp.getElement(2));
                    if (c != null)
                        gxInstance.dp.addCalculationCircle(c, 0);
                } else {
                    gxInstance.dp.addCalculationPolygon((GEPolygon) cc);
                }
            }

        } else if (command.equals("Girth")) {
            gxInstance.dp.addCalculationCircle((GECircle) cc, 1);

        } else if (command.equals("Radius")) {
            gxInstance.dp.addCalculationCircle((GECircle) cc, 2);
        } else if (command.equals("Slope")) {
            gxInstance.dp.addLineSlope((GELine) cc);
        } else if (command.equals("Unfreeze All Points"))
            gxInstance.dp.unfreezeAllPoints();


    }

    private JMenuItem addAMenuItem(String s, boolean t) {
        JMenuItem item = new JMenuItem(s);
        item.setEnabled(t);
        item.addActionListener(this);
        add(item);
        return item;
    }

    JRadioButtonMenuItem t1, t2;

    private void addFontSizeMenuItem(GEText t) {
        int f = t.getFontSize();
        int[] fz = CMisc.getFontSizePool();
        int n = fz.length;
        Font fx = t.getFont();

        ActionListener ls = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = e.getActionCommand();
                GEText t = (GEText) cc;
                Object obj = e.getSource();

                if (obj == t1) {
                    t.setPlain();
                } else if (obj == t2) {
                    t.setBold();
                } else {
                    int n = Integer.parseInt(s.trim());
                    t.setFontSize(n);
                }
            }
        };

        JMenu m = new JMenu(getLanguage(4027, "Font Size"));
        for (int i = 0; i < n; i++) {
            JMenuItem item = new JMenuItem(" " + fz[i] + " ");
            item.addActionListener(ls);
            item.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            if (fz[i] == f)
                item.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.gray, 1), new LineBorder(Color.white, 1)));
            m.add(item);
        }
        t1 = new JRadioButtonMenuItem(getLanguage(4025, "Plain"));
        t2 = new JRadioButtonMenuItem(getLanguage(4026, "Bold"));

        ButtonGroup g = new ButtonGroup();
        g.add(t1);
        g.add(t2);
        if (fx.isPlain())
            t1.setSelected(true);
        t1.addActionListener(ls);
        if (fx.isBold())
            t2.setSelected(true);
        t2.addActionListener(ls);
        this.add(t1);
        this.add(t2);
        this.add(m);
    }

    private void addFreezeMenu() {
        if (gxInstance.dp.containFreezedPoint()) {
            JMenuItem item = new JMenuItem("Unfreeze All Points");
            item.setEnabled(true);
            item.addActionListener(this);
            this.add(item);
        }
    }

    private void addAColorMenuItem(String s, boolean t, int d) {
        JMenu item = new JMenu(s);
        item.setEnabled(t);
        add(item);
        if (!t)
            return;

        int n = drawData.getColorCounter();
        Dimension dm = new Dimension(90, 15);
        for (int i = 0; i < n; i++) {
            JMenuItem it = new JMenuItem();
            Color c = drawData.getColor(i);
            it.add(new colorPanel(c));
            it.setForeground(c);
            it.setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 1));
            it.setPreferredSize(dm);
            it.setActionCommand("Color");
            it.addActionListener(this);
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
            it.setToolTipText("r = " + r + ", g = " + g + ", b = " + b);
            if (d == i && t) {
                it.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.red, 1), new LineBorder(Color.white, 1)));
            }
            item.add(it);
        }
    }

    class colorPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 8166099903446827896L;

		public colorPanel(Color c) {
            this.setForeground(c);
            this.setBackground(c);
        }
    }

}
