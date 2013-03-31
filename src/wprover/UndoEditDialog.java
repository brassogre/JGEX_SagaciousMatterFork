package wprover;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2005-8-12
 * Time: 11:31:35
 * To change this template use File | Settings | File Templates.
 */
public class UndoEditDialog extends JBaseDialog implements WindowListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7906773981009770576L;
	private ListTree treepanel;

    public UndoEditDialog(GExpert owner) {
        super(owner.getFrame());
        this.setTitle(GExpert.getLanguage(157, "Construct History"));
        treepanel = new ListTree(owner);
        this.setContentPane(treepanel);
        this.setSize(new Dimension(430, 600));
        owner.centerDialog(this);
        this.addWindowListener(this);
    }


    public ListTree getTreePanel() {
        return treepanel;
    }

    public void showDialog() {
        this.setVisible(true);
        treepanel.reload();
    }

    public void windowOpened(WindowEvent e) {

    }

    public void windowClosing(WindowEvent e) {

    }

    public void windowClosed(WindowEvent e) {
    }


    public void windowIconified(WindowEvent e) {

    }


    public void windowDeiconified(WindowEvent e) {

    }


    public void windowActivated(WindowEvent e) {

    }

    public void windowDeactivated(WindowEvent e) {

    }
}

