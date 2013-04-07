package wprover;

import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Mar 25, 2007
 * Time: 3:52:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class FlashRedoStep extends Flash {


    DrawPanel dp;

    public FlashRedoStep(JPanel p, DrawPanel dp) {
        super(p);
        this.dp = dp;
        vType = true;
    }

    public boolean draw(Graphics2D g2) {
        return true;
    }

    public void stop() {
        if (finished)
            return;

        dp.redo_step(false);
        finished = true;
    }

    public void start() {
        stop();
    }

    public boolean isrRunning() {
        return false;

    }

    public boolean isfinished() {
        return finished;
    }
}
