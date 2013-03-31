package UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;


public class SolidBorder implements Border {
    protected Color topColor = Color.white;
    protected Color bottomColor = Color.gray;

    public SolidBorder() {
    }


    public Insets getBorderInsets(Component c) {
        return new Insets(2, 2, 2, 2);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        width--;
        height--;
        g.setColor(topColor);
        g.drawLine(x, y + height, x, y);
        g.drawLine(x, y, x + width, y);
        g.setColor(bottomColor);
        g.drawLine(x + width, y, x + width, y + height);
        g.drawLine(x, y + height, x + width, y + height);
    }

}
