package wprover;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;

public class PanelDraw extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener {

    /**
	 * This window serves as a layer on top of the main drawing panel
	 * in order to display a <code>drawTextProcess</code>.
	 * This captures mouse events and passes them along to the
	 * <code>drawTextProcess</code> when 
	 */
	private static final long serialVersionUID = -4562700852339582037L;
    DrawPanelExtended dp;
    public enum enumInputType {DRAW, CONSTRUCTION};
    private enumInputType iType = enumInputType.DRAW;
    DrawPanelFrame gxInstance;

    public void SetInputTypeToConstruction(boolean b) {
    	iType = b ? enumInputType.CONSTRUCTION : enumInputType.DRAW;
    	if (b)
    		dp.SetCurrentAction(DrawPanel.CONSTRUCT_FROM_TEXT);
    }

    public void clearAll() {
        removeAll();
        repaint();
    }

    public void setStep(double step) {
        dp.animate.Setstep(step);
    }

    public boolean userActionMayProceed() {
        return gxInstance == null || !gxInstance.getpprove().isProverRunning();
    }

    public PanelDraw(DrawPanelFrame gx) {
        gxInstance = gx;
        
        dp = new DrawPanelExtended();
        dp.setCurrentInstance(gx);

        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
        setDoubleBuffered(true);
        setLayout(null);
        addComponentListener(this);
//        this.setBorder(BorderFactory.createLineBorder(Color.lightGray));
//        this.setBorder(new GBevelBorder(GBevelBorder.RAISED));
        setBackground(UtilityMiscellaneous.getBackGroundColor());
    }


    public void repaintAndCalculate() {
        dp.reCalculate();
        repaint();
    }


    public void onAnimate(int type) {
        if (type == 1) //start
            dp.animationStart();
        else if (type == 2)
            dp.animationStop();
        else if (type == 0)
            dp.animationOntime();

        repaint();
    }

/*    public Dimension getPreferredSize() {
//        Rectangle rc = dp.getBounds();
//
//        int x = (int) (Math.abs(rc.getX() + rc.getWidth())) + 20;
//        int y = (int) (Math.abs(rc.getY() + rc.getHeight())) + 20;
//        return new Dimension(x, y);
        return super.getPreferredSize();
    }*/

    public void mousePressed(MouseEvent e) {
        if (!userActionMayProceed())
            return;

        int button = e.getButton();
        if (button == MouseEvent.BUTTON3) {
            dp.DWMouseRightDown(e.getX(), e.getY());
            return;
        }
        if (iType == enumInputType.DRAW) {
            if (dp.GetCurrentAction() == DrawPanel.CONSTRUCT_FROM_TEXT) {
                dp.mouseDown(e.getX(), e.getY());
            } else
                dp.DWButtonDown(e.getX(), e.getY());
            repaint();
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (!userActionMayProceed())
            return;

        int button = e.getButton();
        if (button == MouseEvent.BUTTON3) {
            return;
        }
        if (iType == enumInputType.DRAW) {
            dp.DWMouseDrag(e.getX(), e.getY());
            repaint();
        }
    }


    public void mouseReleased(MouseEvent e) {
        if (!userActionMayProceed())
            return;

        int button = e.getButton();
        if (button == MouseEvent.BUTTON3)
            return;

        if (iType == enumInputType.DRAW) {
            dp.DWButtonUp(e.getX(), e.getY());
            repaint();
        }
    }

    public void mouseMoved(MouseEvent e) {
       int button = e.getButton();
        if (button == MouseEvent.BUTTON3)
            return;

        if (iType == enumInputType.DRAW) {
            dp.DWMouseMove(e.getX(), e.getY());
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (!userActionMayProceed())
            return;

        if (e.getButton() == MouseEvent.BUTTON3)
            dp.DWMouseRightClick(e.getX(), e.getY());
//        else if (e.getClickCount() > 1)
//            dp.DWMouseDbClick(e.getX(), e.getY());
    }

    public void mouseExited(MouseEvent e) {
//        if (!this.actionNeedProceed())
//            return;

        dp.setMouseInside(false);
        this.repaint();
    }

    public void mouseEntered(MouseEvent e) {
//        if (!this.actionNeedProceed())
//            return;

        dp.setMouseInside(true);
        this.repaint();
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
//        if (!this.actionNeedProceed())
//            return;

    	dp.DWMouseWheel(e.getX(), e.getY(), e.getScrollAmount(), e.getWheelRotation());
        this.repaint();
    }


    public void componentResized(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension dm = getSize();
        dp.SetDimension(dm);
        dp.paintPoint((Graphics2D)g);
//        paintBD(g);
    }


//    final private static BasicStroke bstroke = new BasicStroke(1.0f);

//    public void paintBD(Graphics g) {
//        Graphics2D g2 = (Graphics2D) g;
//        g2.setStroke(bstroke);
//        g2.setColor(Color.LIGHT_GRAY);
//        int w = this.getWidth() - 1;
//        int h = this.getHeight() - 1;
//
//        g2.drawLine(0, h, w, h);
//        g2.drawLine(w, 0, w, h);
//    }
}
