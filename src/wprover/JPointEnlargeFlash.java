package wprover;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: 2008-1-11
 * Time: 23:41:44
 * To change this template use File | Settings | File Templates.
 */
public class JPointEnlargeFlash extends JFlash implements ActionListener {

    private GEPoint point;
    private static final int LENG = 8;
    private int old_radius_of_point = 0;

    public JPointEnlargeFlash(JPanel p, GEPoint pt) {
        super(p);
        point = pt;
        timer = new Timer(TIME_INTERVAL, this);
        old_radius_of_point = pt.getRadius();
    }

    public boolean draw(Graphics2D g2) {
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        n--;
        if (n <= 0) stop();
        if (n % 2 != 0) {
        	point.setRadius(LENG);
        } else {
        	point.setRadius(old_radius_of_point);
        }
        panel.repaint();
    }

    public void stop() {
    	point.setRadius(old_radius_of_point);
        super.stop();
    }

}
