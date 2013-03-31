package wprover;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-5-3
 * Time: 12:35:47
 * To change this template use File | Settings | File Templates.
 */
public class J_Line {
    private boolean ext = false;

    ArrayList<GEPoint> vlist = new ArrayList<GEPoint>();

    public J_Line() {
    }

    public void setDrawInfinite(boolean inf) {
        ext = inf;
    }

    public void addAPoint(GEPoint p) {
        if (p != null && !vlist.contains(p)) {
            vlist.add(p);
        }
    }

    public static boolean draw(Graphics2D g2) {
        return true;
    }

    public void drawLine(Graphics2D g2) {
        GEPoint[] pl = getMaxMinPoint();
        if (pl != null) {
            if (!ext) {
                g2.drawLine((int) pl[0].getx(), (int) pl[0].gety(),
                        (int) pl[1].getx(), (int) pl[1].gety());
            } else {

                if (Math.abs(pl[0].getx() - pl[1].getx()) < CMisc.ZERO) {
                    double x = pl[0].getx();
                    double y1 = 0;
                    double y2 = 2000;
                    g2.drawLine((int) x, (int) y1, (int) x, (int) y2);

                } else {
                    double k = (pl[1].gety() - pl[0].gety()) /
                            (pl[1].getx() - pl[0].getx());
                    double x1 = 0;
                    double x2 = 2000;
                    double y1 = k * (0 - pl[0].getx()) + pl[0].gety();
                    double y2 = k * (x2 - pl[0].getx()) + pl[0].gety();
                    g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                }

            }
        }
//        drawPt(g2);

    }


    public void drawPt(Graphics2D g2) {
        for (int i = 0; i < vlist.size(); i++) {
            GEPoint pt = vlist.get(i);
            int x = (int) pt.getx();
            int y = (int) pt.gety();
            int r = pt.getRadius();
            g2.drawOval(x - r - 1, y - r - 1, 2 * r + 1, 2 * r + 1);
        }
    }

    public void fillPt(Graphics2D g2) {
        g2.setColor(Color.white);

        for (int i = 0; i < vlist.size(); i++) {
            GEPoint pt = vlist.get(i);
            int x = (int) pt.getx();
            int y = (int) pt.gety();
            int r = pt.getRadius();
            g2.fillOval(x - r - 1, y - r - 1, 2 * r + 1, 2 * r + 1);
        }

    }

    public void setInFlashMode(boolean t) {
        for (int i = 0; i < vlist.size(); i++) {
            GEPoint pt = vlist.get(i);
            pt.setAsFlashing(t);
        }
    }

    public boolean containPt(GEPoint pt) {
        return vlist.contains(pt);
    }

    public GEPoint[] getMaxMinPoint() {
        if (vlist.size() < 2) {
            return null;
        }

        GEPoint p1, p2;
        p1 = vlist.get(0);
        if (p1 == null) {
            return null;
        }

        p2 = null;
        for (int i = 1; i < vlist.size(); i++) {
            GEPoint p = vlist.get(i);

            if (p.getx() < p1.getx()) {
                if (p2 == null) {
                    p2 = p1;
                    p1 = p;
                } else {
                    p1 = p;
                }
            } else if (p2 == null || p.getx() > p2.getx()) {
                p2 = p;
            }
        }

        if (Math.abs(p1.getx() - p2.getx()) < 0.00001) {
            p1 = vlist.get(0);
            p2 = null;
            for (int i = 1; i < vlist.size(); i++) {
                GEPoint p = vlist.get(i);
                if (p.gety() < p1.gety()) {
                    if (p2 == null) {
                        p2 = p1;
                        p1 = p;
                    } else {
                        p1 = p;
                    }
                } else if (p2 == null || p.gety() > p2.gety()) {
                    p2 = p;
                }
            }

        }

        GEPoint[] pl = new GEPoint[2];
        pl[0] = p1;
        pl[1] = p2;
        return pl;
    }
}
