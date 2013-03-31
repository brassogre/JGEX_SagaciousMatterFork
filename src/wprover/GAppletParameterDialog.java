package wprover;

import javax.swing.JDialog;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2009-3-2
 * Time: 17:27:50
 * To change this template use File | Settings | File Templates.
 */
public class GAppletParameterDialog extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7152612563351597890L;

	public  GAppletParameterDialog(GExpert gx)
    {
        super(gx.getFrame(),"Applet Exporter");
        
    }
}
