package wprover;

/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2005-8-12
 * Time: 11:06:53
 * To change this template use File | Settings | File Templates.
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ListTree extends JTabbedPane
        implements ActionListener, MouseListener, ListSelectionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 789086010064494837L;
	public DrawPanelFrame gxInstance;
    public ArrayList<UndoStruct> undolist;
    private JList<UndoStruct> list;
    private JList<GraphicEntity> listx;
    private DefaultListModel<UndoStruct> model; 
    private DefaultListModel<GraphicEntity> modelx;
    private PanelProperty prop;


    public ListTree(DrawPanelFrame gx) {
        super(JTabbedPane.BOTTOM);

        JPanel pane1 = new JPanel();
        pane1.setLayout(new BoxLayout(pane1, BoxLayout.Y_AXIS));
        gxInstance = gx;
        undolist = new ArrayList<UndoStruct>();
        model = new DefaultListModel<UndoStruct>();
        list = new JList<UndoStruct>(model);
        list.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        pane1.add(new JScrollPane(list));

        ListCellRenderer<Object> rener = new DefaultListCellRenderer() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -3794192645189245455L;

			public Component getListCellRendererComponent(
                    JList<? extends Object> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                		DefaultListCellRenderer d = (DefaultListCellRenderer)
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                		d.setText((1 + index) + ". \t" + value.toString());
                return d;
            }

        };
        list.setCellRenderer(rener);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        this.addTab(DrawPanelFrame.getLanguage(157, "Construct History"), pane1);

        modelx = new DefaultListModel<GraphicEntity>();
        listx = new JList<GraphicEntity>(modelx) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 6880508582753682860L;

			public Dimension getPreferredSize() {
                Dimension dm = super.getPreferredSize();
                double w = dm.getWidth();
                if (w < 100)
                    w = 100;
                dm.setSize(w, dm.getHeight());
                return dm;
            }
        };
        listx.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        ListCellRenderer<Object> rener1 = new DefaultListCellRenderer() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1853307522105680145L;

			public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                DefaultListCellRenderer d = (DefaultListCellRenderer)
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                GraphicEntity c = (GraphicEntity) value;

                d.setText(c.getDescription());
                return d;
            }

        };

        prop = new PanelProperty(gx.d, DrawPanelFrame.getLan());
        prop.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        JSplitPane pane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        pane2.setLeftComponent(new JScrollPane(listx));
        listx.setCellRenderer(rener1);
        pane2.setRightComponent(prop);
        listx.addListSelectionListener(this);
        this.addTab(DrawPanelFrame.getLanguage(3115, "Objects"), pane2);
    }


    public void actionPerformed(ActionEvent e) {
    }


    /**
     * Remove the currently selected node.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == list)
            gxInstance.dp.setUndoStructForDisPlay((list.getSelectedValue()), true);
        else {
            GraphicEntity c = listx.getSelectedValue();
            if (c != null) {
                prop.SetPanelType(c);
                gxInstance.dp.setObjectListForFlash(c);
            }
        }

    }


    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 3) {


        } else {

        }
    }


    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void clearAllTrees() {


    }

    public void reload() {
        undolist.clear();
        model.removeAllElements();
        modelx.removeAllElements();

        DrawPanel dp = gxInstance.dp;

        undolist.addAll(dp.undolist);

	for (int i = 0; i < undolist.size(); i++)
            model.addElement(undolist.get(i));

        ArrayList<Object> vx = new ArrayList<Object>();
	dp.getAllSolidObj(vx);

        for (Object o : vx) {
            if (o != null && o instanceof GraphicEntity)
                modelx.addElement((GraphicEntity)o);
        }
    }

}