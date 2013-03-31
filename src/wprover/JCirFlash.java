package wprover;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-5-3
 * Time: 12:12:16
 * To change this template use File | Settings | File Templates.
 */
public class JCirFlash extends JFlash implements ActionListener {

    GEPoint o;
    ArrayList<GEPoint> vlist = new ArrayList<GEPoint>();

    public JCirFlash(JPanel p) {
        super(p);
        timer = new Timer(TIME_INTERVAL, this);
    }

    public boolean draw(Graphics2D g2) {


        if (o != null && vlist.size() >= 1) {
            GEPoint p1 = vlist.get(0);
            double radius = Math.sqrt(Math.pow(o.getx() - p1.getx(), 2) + Math.pow(o.gety() - p1.gety(), 2));
            g2.setStroke(BStroke2);
            g2.setColor(Color.white);
            g2.drawOval((int) (o.getx() - radius), (int) (o.gety() - radius), (int) (2 * radius), (int) (2 * radius));
            if (n % 2 == 0) {
                super.setDrawDashFb2(g2);
                g2.drawOval((int) (o.getx() - radius), (int) (o.gety() - radius), (int) (2 * radius), (int) (2 * radius));
            }

        } else if (vlist.size() >= 3) {
            GEPoint p1 = vlist.get(0);
            GEPoint p2 = vlist.get(1);
            GEPoint p3 = vlist.get(2);

            double x_1 = p1.getx();
            double x_2 = p1.gety();
            double x_3 = p2.getx();
            double x_4 = p2.gety();
            double x_5 = p3.getx();
            double x_6 = p3.gety();
            if (isZero(x_6 - x_2)) {
                double t = x_3;
                x_3 = x_5;
                x_5 = t;
                t = x_4;
                x_4 = x_6;
                x_6 = t;
            }

            double m = (2 * (x_3 - x_1) * x_6 + (-2 * x_4 + 2 * x_2) * x_5 + 2 * x_1 * x_4 - 2 * x_2 * x_3);

            double x = (x_4 - x_2) * x_6 * x_6
                    + (-1 * x_4 * x_4 - x_3 * x_3 + x_2 * x_2 + x_1 * x_1) * x_6
                    + (x_4 - x_2) * x_5 * x_5 + x_2 * x_4 * x_4
                    + (-1 * x_2 * x_2 - x_1 * x_1) * x_4 + x_2 * x_3 * x_3;

            x = (-1) * x / m;

            double y = (-1) * ((2 * x_5 - 2 * x_1) * x
                    - x_6 * x_6 - x_5 * x_5 + x_2 * x_2 + x_1 * x_1) / ((2 * x_6 - 2 * x_2));

            double radius = Math.sqrt(Math.pow(x - x_1, 2) + Math.pow(y - x_2, 2));

            g2.setStroke(BStroke2);
            g2.setColor(Color.white);
            g2.drawOval((int) (x - radius), (int) (y - radius), (int) (2 * radius), (int) (2 * radius));
            if (n % 2 == 0) {
                super.setDrawDashFb2(g2);
                g2.setColor(Color.red);
                g2.drawOval((int) (x - radius), (int) (y - radius), (int) (2 * radius), (int) (2 * radius));
            }
        }


        if (n % 2 == 0) {
            if (o != null)
                o.draw(g2, false);  // Before edit, this method called draw(g2) instead of draw(g2, false). I don't know why two different draw methods were implemented.
            for (GEPoint pt : vlist)
                pt.drawWithNametag(g2);
        }
        return true;
    }

    public void setCenter(GEPoint t) {
        if (t != null)
            o = t;
    }

//    public void addAPoint(CPoint pt) {
//
//        if (pt != null && !vlist.contains(pt))
//            vlist.add(pt);
//    }

    public void addAPoint(GEPoint pt) {
        if (pt != null && !vlist.contains(pt))
            vlist.add(pt);
    }

    public void actionPerformed(ActionEvent e) {
        n--;
        if (n <= 0) super.stop();

        for (GEPoint pt : vlist) {
            if (n % 2 != 0)
                pt.setAsFlashing(true);
            else
                pt.setAsFlashing(false);
        }

        panel.repaint();
    }

    public void stop() {
        super.stop();
        for (GEPoint pt : vlist) {
            pt.setAsFlashing(false);
        }
    }


}
