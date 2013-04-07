package wprover;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 25, 2006
 * Time: 7:23:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PanelvcellRender1 extends PanelvcellRender implements TreeCellRenderer {


      /**
	 * 
	 */
	private static final long serialVersionUID = 3725001604519475262L;

	public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected,
                                                  boolean expanded,
                                                  boolean leaf, int row,
                                                  boolean hasFocus) {
        Component returnValue = null;
        //   this.setBorder(eborder);
        //this.setBackground(Color.white);
        boolean crsp = false;

        if ((value != null) && (value instanceof DefaultMutableTreeNode)) {

            //DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            this.removeAll();
            //Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            PanelvcellRender cell = this;
            

            //if (cell != null) {
                if (selected || crsp) {
                    cell.setBackground(cell.backgroundSelectionColor);
                } else {
                    cell.setBackground(cell.backgroundNonSelectionColor);
                }
                cell.setEnabled(tree.isEnabled());
                returnValue = cell;
            //}
        }
        if (returnValue == null) {
            returnValue = PanelvcellRender.defaultRenderer.
                    getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }
        return returnValue;
    }
}
