package wprover;

import UI.GifEncoder;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: 2008-6-28
 * Time: 14:58:12
 * To change this template use File | Settings | File Templates.
 */

public class DialogGifProcess extends DialogBase implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1537290597241595179L;
	JProgressBar progress;
    int total;
    JLabel label;
    public DataOutputStream out;
    public DrawPanelFrame gxInstance;
    public Animation am;
    public DrawPanelExtended dp;
    public Rectangle rect;
    public GifEncoder en;


    public DialogGifProcess(Frame f) {
        super(f, false);
        this.setTitle("Building GIF File");
        progress = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        progress.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        progress.setBorderPainted(true);
        progress.setStringPainted(true);
        label = new JLabel("0 frame(s) added");
        JPanel pp = new JPanel();
        pp.setLayout(new FlowLayout());
        pp.add(Box.createHorizontalStrut(150));
        pp.add(label);

        JPanel panel = new JPanel();
        panel.add(Box.createVerticalStrut(10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(progress);
        panel.add(pp);
        panel.add(Box.createVerticalStrut(10));

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
        panel1.add(Box.createHorizontalGlue());
        JButton b1 = new JButton("Cancel");
        panel1.add(b1);
        b1.addActionListener(this);
        panel.add(panel1);
        panel.add(Box.createVerticalStrut(10));
        panel.setBorder((BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        this.getContentPane().add(panel);
        this.pack();
    }

    boolean finished = false;

    public void setRun() {
        Saver sv = new Saver();
        Thread t = new Thread(sv, "Progress");
        finished = false;
        t.start();
    }

    class Saver implements Runnable {

        public void run() {
            double []r = dp.getParameter();
            
            am.minwd = rect.getX() +5;
            am.minht = rect.getY() +5;
            am.width = rect.getX() + rect.getWidth() -5;
            am.height = rect.getY() + rect.getHeight() -5;

            am.reCalculate();
            total = am.getRounds();

            DialogGifProcess.this.setVisible(true);
            dp.setCalMode1();
            try {
                int n = total;
                while (n >= 0) {
                    am.onTimer();
                    if (!dp.reCalculate()) {
                        am.resetXY();
                    }
                    DialogGifProcess.this.setValue(total - n + 1);
                    en.addFrame(gxInstance.getBufferedImage(rect));
                    n--;
                    if (finished)
                        break;
                }
                en.finish();
                out.close();

            } catch (IOException ee) {
                System.out.println(ee.getMessage());
            }
            DialogGifProcess.this.setVisible(false);
            dp.setCalMode0();
            dp.setParameter(r);
        }
    }

    public void setTotal(int n) {
        this.total = n;
    }

    public void setValue(int n) {
        progress.setValue(n * 100 / total);
        label.setText(n + " frame(s) added");
    }


    public void actionPerformed(ActionEvent e) {
        finished = true;
        this.setVisible(false);
    }
}
