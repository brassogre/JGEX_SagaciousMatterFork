package wprover;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-5-9
 * Time: 14:59:57
 * To change this template use File | Settings | File Templates.
 */
public class FlashAng extends Flash implements ActionListener {
    int type;
    GEPoint o1, a1, b1, o2, a2, b2;
    int status = 0;
    int Radius = RAD;

    public FlashAng(JPanel panel, GEPoint o1, GEPoint a1, GEPoint b1, GEPoint o2,
                     GEPoint a2, GEPoint b2) {
        super(panel);

        timer = new Timer(2 * TIME_INTERVAL, this);
        this.o1 = o1;
        this.a1 = a1;
        this.b1 = b1;
        this.o2 = o2;
        this.a2 = a2;
        this.b2 = b2;

    }

    public void updateTimer() {
        if (timer != null)
            timer.setDelay(2 * UtilityMiscellaneous.getFlashInterval());
    }

    public boolean draw(Graphics2D g2) {
        if (type == 0) {
            g2.setColor(color);
            if (DrawPanelBase.check_para(a1, b1, a2, b2)) {
                g2.setStroke(BStroke2);
                g2.drawLine((int) a1.getx(), (int) a1.gety(), (int) b1.getx(),
                        (int) b1.gety());
                g2.drawLine((int) a2.getx(), (int) a2.gety(), (int) b2.getx(),
                        (int) b2.gety());
            } else {
                if (status == 0) {
                    drawAngle(o1, a1, o1, b1, Radius, g2);
                    setDrawDash(g2);
                    g2.drawLine((int) o1.getx(), (int) o1.gety(),
                            (int) a1.getx(),
                            (int) a1.gety());
                    g2.drawLine((int) o1.getx(), (int) o1.gety(),
                            (int) b1.getx(),
                            (int) b1.gety());
                } else {
                    drawAngle(o2, a2, o2, b2, Radius, g2);
                    setDrawDash(g2);
                    g2.drawLine((int) o2.getx(), (int) o2.gety(),
                            (int) a2.getx(),
                            (int) a2.gety());
                    g2.drawLine((int) o2.getx(), (int) o2.gety(),
                            (int) b2.getx(),
                            (int) b2.gety());
                }
            }
        }

        return true;

    }

    public void actionPerformed(ActionEvent e) {
        if (type == 0) {
            if (status == 0) {
                Radius -= 5;
                if (Radius <= 0) {
                    status = 1;
                    Radius = 0;
                }
            } else {
                Radius += 5;
                if (Radius >= RAD) {
                    super.stop();
                    Radius = RAD;
                }
            }
        }
        panel.repaint();

    }


}
