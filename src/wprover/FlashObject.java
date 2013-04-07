package wprover;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2005-8-11
 * Time: 13:28:05
 * To change this template use File | Settings | File Templates.
 */
public class FlashObject extends Flash implements ActionListener {
    //private static int TIMERS = 130;
    private static int MAXFLASHTIMES = 12;
    private ArrayList<GraphicEntity> vlist;
    private int count = 0;

    public FlashObject(JPanel p) {
        super(p);
        panel = p;
        vlist = new ArrayList<GraphicEntity>();
        timer = new Timer(TIME_INTERVAL, this);
        vType = true;
    }

    public boolean draw(Graphics2D g2) {
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        if (count % 2 == 0)
            setListInFlashing(vlist, false);
        else
            setListInFlashing(vlist, true);
        if (panel != null)
            panel.repaint();
        count++;
        if (count == MAXFLASHTIMES)
            this.stop();

    }

    private static void setListInFlashing(ArrayList<GraphicEntity> v, boolean inflash) {
        for (GraphicEntity cc : v) {
            cc.setAsFlashing(inflash);
        }
    }

    private static void stopListFlash(ArrayList<GraphicEntity> vlist2) {
        for (GraphicEntity cc : vlist2) {
            cc.stopFlash();
        }
    }

    public void setAt(JPanel p, ArrayList<GraphicEntity> list) {
        panel = p;
        stopListFlash(vlist);
        vlist.clear();
        for (GraphicEntity cc : vlist) {
            if (cc != null && cc.visible())
                vlist.add(cc);
        }
    }

    public void addFlashObject(GraphicEntity obj) {
        if (obj != null && !vlist.contains(obj) && obj.visible())
            vlist.add(obj);
    }

    public void start() {
        if (vlist.isEmpty()) {
            stop();
            return;
        }

        count = 0;
        setListInFlashing(vlist, true);
        super.start();
    }

    public void stop() {
        stopListFlash(vlist);
        super.stop();
    }
}
