package wprover;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: 2008-1-16
 * Time: 22:05:51
 * To change this template use File | Settings | File Templates.
 */
public class DialogVFont extends DialogBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8188002468756301922L;

	public DialogVFont(DrawPanelFrame exp) {
        super(exp.getFrame(), "Font for Proof Panel");
        //Frame f = exp.getFrame();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p1.setBorder(BorderFactory.createTitledBorder("Font for theorem"));
        JLabel label = new JLabel("Preview Label");
        label.setBackground(Color.white);
        p1.add(label);
        p1.add(Box.createHorizontalGlue());
        p1.add(new JButton("Font"));
        panel.add(p1);
        this.add(panel);

        this.setSize(400, 300);
    }
}
