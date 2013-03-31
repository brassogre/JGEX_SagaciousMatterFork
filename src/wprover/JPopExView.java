package wprover;


/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2005-11-6
 * Time: 20:57:00
 * To change this template use File | Settings | File Templates.
 */
public class JPopExView extends JBaseDialog
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2973953745363435028L;
	GExpert gxInstance;
    JExPanel panel;

    public JPopExView(GExpert exp)
    {
        super(exp.getFrame());
        gxInstance = exp;
        this.setSize(600, 400);
        panel = new JExPanel();
        this.add(panel);
    }

//    public boolean loadRule(String s)
//    {
//        this.setTitle(s);
//
//        String f = GExpert.getUserDir();
//        String sp = GExpert.getFileSeparator();
//
//        drawProcess dp = new drawProcess();
//        dp.clearAll();
//        try
//        {
//            dp.Load(f + sp +"rules" + sp + s);
//            panel.setdrawP(dp);
//        } catch (IOException ee)
//        {
//            CMisc.eprint(panel, "can not load rule: " + sp);
//            return false;
//        }
//        return true;
//    }


}
