package wprover;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.MenuSelectionManager;
import javax.swing.border.*;

class ColorMenu extends JPopupMenu
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6430533757897478295L;

	protected Border unselectedBorder;

    protected Border selectedBorder;

    protected Border activeBorder;

    protected HashMap<Color, ColorPane> paneTable;

    protected ColorPane colorPane;

    public ColorMenu(String name)
    {
        super(name);
        unselectedBorder = new CompoundBorder(new MatteBorder(1, 1, 1, 1,
                getBackground()), new BevelBorder(BevelBorder.LOWERED,
                        Color.white, Color.gray));
        selectedBorder = new CompoundBorder(new MatteBorder(1, 1, 1, 1,
                Color.red), new MatteBorder(1, 1, 1, 1, getBackground()));
        activeBorder = new CompoundBorder(new MatteBorder(1, 1, 1, 1,
                Color.blue), new MatteBorder(1, 1, 1, 1, getBackground()));

        JPanel p = new JPanel();
        p.setBorder(new EmptyBorder(5, 5, 5, 5));
        p.setLayout(new GridLayout(8, 8));
        paneTable = new HashMap<Color, ColorPane>();

        int[] values = new int[]{0, 128, 192, 255};

        for (int r = 0; r < values.length; r++)
        {
            for (int g = 0; g < values.length; g++)
            {
                for (int b = 0; b < values.length; b++)
                {
                    Color c = new Color(values[r], values[g], values[b]);
                    ColorPane pn = new ColorPane(c);
                    p.add(pn);
                    paneTable.put(c, pn);
                }
            }
        }
        add(p);
    }

    public void setColor(Color c)
    {
        Object obj = paneTable.get(c);
        if (obj == null)
            return;
        if (colorPane != null)
            colorPane.setSelected(false);
        colorPane = (ColorPane) obj;
        colorPane.setSelected(true);
    }

    public Color getColor()
    {
        if (colorPane == null)
            return null;
        return colorPane.getColor();
    }

    public void doSelection()
    {
    }

    public void HideMenu()
    {
        this.setVisible(false);
    }

    class ColorPane extends JPanel implements MouseListener
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = -698585888096525087L;

		protected Color color;

        protected boolean isSelected;

        public ColorPane(Color c)
        {
            color = c;
            setBackground(c);
            setBorder(unselectedBorder);
            String msg = "R " + c.getRed() + ", G " + c.getGreen() + ", B "
                    + c.getBlue();
            setToolTipText(msg);
            addMouseListener(this);
        }

        public Color getColor()
        {
            return color;
        }

        public Dimension getPreferredSize()
        {
            return new Dimension(25, 25);
        }

        public Dimension getMaximumSize()
        {
            return getPreferredSize();
        }

        public Dimension getMinimumSize()
        {
            return getPreferredSize();
        }

        public void setSelected(boolean selected)
        {
            isSelected = selected;
            if (isSelected)
                setBorder(selectedBorder);
            else
                setBorder(unselectedBorder);
        }

        public boolean isSelected()
        {
            return isSelected;
        }

        public void mousePressed(MouseEvent e)
        {
        }

        public void mouseClicked(MouseEvent e)
        {
        }

        public void mouseReleased(MouseEvent e)
        {
            setColor(color);
            MenuSelectionManager.defaultManager().clearSelectedPath();
            doSelection();
            HideMenu();
        }

        public void mouseEntered(MouseEvent e)
        {
            setBorder(activeBorder);
        }

        public void mouseExited(MouseEvent e)
        {
            setBorder(isSelected ? selectedBorder : unselectedBorder);
        }
    }
}