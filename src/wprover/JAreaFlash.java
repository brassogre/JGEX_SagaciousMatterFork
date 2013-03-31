package wprover;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-7-24
 * Time: 16:32:16
 * To change this template use File | Settings | File Templates.
 */
public class JAreaFlash extends JFlash implements ActionListener{
    private ArrayList<GEPoint> vlist = new ArrayList<GEPoint>();
    private int lghtcolor = drawData.LIGHTCOLOR;

    public JAreaFlash(JPanel p,int cindex)
    {
        super(p);

        lghtcolor = drawData.LIGHTCOLOR + cindex-1;
        timer = new Timer(TIME_INTERVAL, this);
    }
    
    @Override
    public  boolean draw(Graphics2D g2)
    {
        if (n % 2 != 0) return true;

        int nn = vlist.size();
        Composite ac = g2.getComposite();
        g2.setComposite(CMisc.getFillComposite());
        if(nn == 0) return true;
        
        int []x = new int[nn];
        int []y = new int[nn];
        for(int i=0; i <nn; i++)
        {
            GEPoint p1 = vlist.get(i);
            x[i] = (int)p1.getx();
            y[i] = (int)p1.gety();
        }
        g2.setColor(Color.black);
        g2.drawPolygon(x,y,nn);
        g2.setColor(drawData.getColor(lghtcolor));
        g2.fillPolygon(x,y,nn);
        g2.setComposite(ac);
        return true;
    }

    public void addAPoint(GEPoint p)
    {
        if(p != null) vlist.add(p);
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
        n--;
        if (n <= 0) super.stop();
        panel.repaint();
    }


}
