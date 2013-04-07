package wprover;

import UI.DropShadowBorder;
import UI.EntityButtonUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: 2008-4-10
 * Time: 13:30:36
 * To change this template use File | Settings | File Templates.
 */
public class PopupMenuRunning extends JPopupMenu implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7774055290043517155L;
	public static Timer timer = null;
    private int counter = 0;
    private JLabel label;
    private JLabel labelt;
    private String str;
    private long start_time;
    private PanelGB panegb;

    private static Color color = new Color(206, 223, 242);


    public PopupMenuRunning(DrawPanelFrame gx, String s) {
        this.setBorder(BorderFactory.createCompoundBorder(
                new DropShadowBorder(), BorderFactory.createLineBorder(color, 10)));

        counter = 0;
        str = s;
        label = new JLabel(s);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        labelt = new JLabel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -8485154110068594335L;

			public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                if (d.getWidth() < 20)
                    d.setSize(20, d.getHeight());
                return d;
            }
        };

        JButton b = new JButton(DrawPanelFrame.createImageIcon("images/other/stop1.gif"));
        b.setUI(new EntityButtonUI());
        panel.add(b);
        b.setToolTipText("Stop the running.");
        b.addActionListener(this);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(label);
        panel.add(Box.createHorizontalGlue());
        panel.add(labelt);
        this.add(panel);

    }

    public void menuSelectionChanged(boolean isIncluded) {
    }

    public void setPanelGB(PanelGB gb) {
        panegb = gb;
    }

    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.setSize(300, d.getHeight());
        return d;
    }

    public void setShownString(String s) {
        str = s;
    }

    public void startCount() {
        start_time = System.currentTimeMillis();
    }

    public static PopupMenuRunning startTimer(DrawPanelFrame gx, String s) {
        if (timer != null && timer.isRunning())
            return null;

        PopupMenuRunning r = new PopupMenuRunning(gx, s);
        r.setShownString(s);

        Dimension dm = r.getPreferredSize();
        Frame f = gx.getFrame();

        int x = (int) (f.getWidth() - dm.getWidth()) / 2;
        int y = (int) (f.getHeight() - dm.getHeight()) / 2;
        r.setLocation(x, y);

        if (timer == null) {
            timer = new Timer(300, r);
            timer.start();
        }

        r.show(gx, x, y);
        r.startCount();

        return r;
    }

    public void stopTimer() {
        if (timer != null)
            timer.stop();
        label.setText("");
        this.setVisible(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            counter++;
            String s = str;

            if (counter > 1) {
                int n = (counter - 1) % 8;
                for (int i = 0; i < n; i++)
                    s += ".";
                label.setText(s);
            }
            long t = System.currentTimeMillis();
            long m = (t - start_time) / 1000;
            labelt.setText(Long.toString(m) + " Seconds");
        } else {
            timer.stop();
            this.setVisible(false);
            if (panegb != null)
                panegb.stopRunning();
        }
    }
}
