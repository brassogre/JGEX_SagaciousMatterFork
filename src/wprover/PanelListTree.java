package wprover;

import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 25, 2006
 * Time: 7:15:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class PanelListTree extends JScrollPane{

    /**
	 * 
	 */
	private static final long serialVersionUID = -5224385901048097087L;


	public PanelListTree()
    {
        super(new JTree());
    }


    class listTree extends JTree
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = -7185735152882135269L;

		public listTree()
        {
        }
    }
}
