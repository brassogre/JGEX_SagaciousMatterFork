package wprover;

import java.awt.Color;
import javax.swing.JPanel;

/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2005-7-16
 * Time: 15:47:43
 * To change this template use File | Settings | File Templates.
 */
public class DialogProperty extends DialogBase
{
      /**
	 * 
	 */
	private static final long serialVersionUID = -4873735607440890404L;

	public DialogProperty(DrawPanelFrame owner,JPanel panel)
      {
          super(owner.getFrame(),false);
          this.setTitle("Property");
          this.setSize(370,310);
          getContentPane().add(panel);
          this.setBackground(Color.white);
      }
}
