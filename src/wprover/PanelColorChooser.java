package wprover;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.*;
import javax.swing.table.TableCellRenderer;

/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2005-8-23
 * Time: 22:39:11
 * To change this template use File | Settings | File Templates.
 */
public class PanelColorChooser extends JPanel implements TableCellRenderer
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 7200986043770950561L;

	protected Border unselectedBorder;

    protected Border selectedBorder;

    protected Border activeBorder;

    private HashMap<Color, ColorPane> paneTable;

    boolean select = false;

    public PanelColorChooser()
    {

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

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
           select = isSelected;
           return this;
       }





    class ColorPane extends JPanel implements MouseListener
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = 5043346145790172319L;

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

	@Override
        public Dimension getPreferredSize()
        {
            return new Dimension(25, 25);
        }

	@Override
        public Dimension getMaximumSize()
        {
            return getPreferredSize();
        }

	@Override
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

	@Override
        public void mousePressed(MouseEvent e)
        {
        }

	@Override
        public void mouseClicked(MouseEvent e)
        {
        }

	@Override
        public void mouseReleased(MouseEvent e)
        {
        }

	@Override
        public void mouseEntered(MouseEvent e)
        {
        }

	@Override
        public void mouseExited(MouseEvent e)
        {
            setBorder(isSelected ? selectedBorder : unselectedBorder);
        }
    }
}
