package wprover;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2005-7-19
 * Time: 11:42:51
 * To change this template use File | Settings | File Templates.
 */
public class DialogSelectGraphicEntity extends DialogBase implements
        ActionListener, ListSelectionListener, MouseListener, MouseMotionListener, KeyListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1931413207100533999L;
	private int oldx;
    private int oldy;
    private JList<String> list;
    private DefaultListModel<String> listModel;
    private static final String str = "Cancel";
    private JButton cancle_button;
    private ArrayList<GraphicEntity> selectedlist;
    Object selected = null;
    DrawPanelFrame gxInstance;

    private static Font listFont = new Font("Arial", Font.PLAIN, 12);

    public DialogSelectGraphicEntity(DrawPanelFrame owner, ArrayList<GraphicEntity> vlist) {
        super(owner.getFrame(), "Select");
        gxInstance = owner;

        this.setModal(true);
        listModel = new DefaultListModel<String>();
        list = new JList<String>(listModel);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        list.addMouseListener(this);
        list.addMouseMotionListener(this);
        JScrollPane listScrollPane = new JScrollPane(list);
        list.setFont(listFont);
//        list.addKeyListener(this);
//        this.addKeyListener(this);

        cancle_button = new JButton(str);
        cancle_button.addActionListener(this);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(listScrollPane, BorderLayout.CENTER);
        panel.add(cancle_button, BorderLayout.PAGE_END);
        getContentPane().add(panel);

        this.setSize(new Dimension(130, 150));
        selectedlist = new ArrayList<GraphicEntity>();
        addItem(vlist);
    }

    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
        Rectangle rc = list.getCellBounds(0, 0);
        if (rc == null)
            return;

        double r = rc.getHeight();
        double r1 = e.getY();
        int n = (int) (r1 / r);
        if (n < 0)
            n = 0;
        else if (n >= listModel.getSize())
            n = listModel.getSize() - 1;

        list.setSelectedIndex(n);
    }

    public void addItem(ArrayList<GraphicEntity> v) {
        listModel.clear();
        selectedlist.clear();

        for (int i = 0; i < v.size(); i++) {
            GraphicEntity cc = v.get(i);
            listModel.addElement(cc.getDescription());
            selectedlist.add(cc);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        int n = list.getSelectedIndex();
        int len = selectedlist.size();
        if (n >= 0 && n < len)
            gxInstance.dp.setObjectListForFlash(selectedlist.get(n));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancle_button) {
            selectedlist.clear();
            listModel.clear();
            setVisible(false);
        }
    }

    public Object getSelected() {
        return selected;
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void popSelect(int x, int y) {
        oldx = x;
        oldy = y;
        setLocation(oldx, oldy);
        setFocusable(true);
        setVisible(true);

    }

    public void mousePressed(MouseEvent e) {
        int index = list.getSelectedIndex();
        if (index < 0 || index >= selectedlist.size())
            return;
        selected = selectedlist.get(index);
        setVisible(false);
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            setVisible(false);
    }

    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
    }
}
