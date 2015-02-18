package wprover;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedSet;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Encapsulates data concerning the various colors, pen thicknesses, dash lengths, etc.
 *
 */
public class DrawData {
    public final static int RED = 3;
    public final static int DASH8 = 15;
    public final static int WIDTH2 = 2;
    public final static int LIGHTCOLOR = 18;

    private static @NonNull DrawData dd = new DrawData();

    public final @NonNull ArrayList<Color> colorlist = new ArrayList<Color>();
    public final @NonNull ArrayList<Double> dashlist = new ArrayList<Double>();
    public final @NonNull ArrayList<Double> widthlist = new ArrayList<Double>();

    int default_color_num;

    private DrawData() {
        Color[] color = {
            Color.blue,
            new Color(0, 255, 255),

            new Color(128, 0, 0),
            Color.red,

            new Color(0, 128, 0),
            Color.green,
            new Color(0, 128, 192),
            new Color(128, 128, 255),
            new Color(255, 0, 255),
            new Color(255, 128, 0),
            new Color(128, 128, 0),
            new Color(255, 255, 0),
            Color.orange,
            Color.white,
            Color.lightGray,
            Color.gray,
            Color.black,
            new Color(204, 255, 204),
            new Color(255, 204, 204),
            new Color(204, 204, 255),
            new Color(204, 255, 255),
            new Color(255, 204, 255),
            new Color(255, 255, 204)
        };

        default_color_num = color.length;
 
        for (int i = 0; i < color.length; i++) {
            colorlist.add(color[i]);
        }


        dashlist.add(new Double(0));
        for (int i = 1; i < 10; i++) {
            dashlist.add(new Double(i));
            dashlist.add(new Double(i + 0.5));
        }

        double[] wid =
                {
                    0.5, 0.8, 1.0, 1.3, 1.5, 1.8
                };
        for (int i = 0; i < wid.length; i++)
            widthlist.add(new Double(wid[i]));
        for (int i = 2; i < 20; i++) {
            widthlist.add(new Double(i));
            widthlist.add(new Double(i + 0.5));
        }
    }

    public static int getDefaultColorCount() {
        return dd.default_color_num;
    }

    public static int getWidthCounter() {
        return dd.widthlist.size();
    }

    public static double getWidth(int index) {
        return dd.widthlist.get(index).doubleValue();
    }

    public static int getDashCounter() {
        return dd.dashlist.size();
    }

    public static double getDash(int index) {
        return dd.dashlist.get(index).doubleValue();
    }

    public static int getColorCounter() {
        return dd.colorlist.size();
    }

    public static int getNextColor(int n) {
        return n % getColorCounter();
    }

    public static @NonNull Color getColorSinceRed(int n) {
        return getColor(anglecolor + n);
    }

    public static @NonNull Color getColor(int index) {
        int n = dd.colorlist.size();
        if (index < 0 )
        	throw new NullPointerException();
        return dd.colorlist.get(index % n);
    }

    public static @NonNull Color getCtColor(int index) {
        Color c = dd.colorlist.get(index);
        return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
    }

    public static int addColor(@NonNull Color co) {
        for (int i = 0; i < dd.colorlist.size(); i++) {
            Color c = dd.colorlist.get(i);
            if (c.getRGB() == co.getRGB())
                return i + 1;
        }

        dd.colorlist.add(co);
        return dd.colorlist.size();
    }

    ////////////////////////////////////////////////////////////////////////



    public static int cindex = 0;
    public static int dindex = 0;
    public static int windex = 2;

    final public static int pointcolor = 3;
    final public static int pointcolor_half_decided = 5;
    final public static int pointcolor_decided = 14;

    public static int polygoncolor = 17;
    public static int anglecolor = 3;
    public static int anglewidth = 2;
    public static int angledash = 0;
    public static int tractcolor = 3;

    public static void setDefaultStatus() {
        cindex = 0;
        dindex = 0;
        windex = 2;
    }

    public static void setProveStatus() {
        cindex = 3;
        dindex = 9;
        windex = 0;
    }

    public static void setAuxStatus() {
        cindex = RED;
        dindex = DASH8;
        windex = 2;
    }

    public static void setDefaultColor(int id) {
        if (id < 0) return;
        cindex = id;
    }

    public static int getColorIndex(@NonNull Color color) {
        for (int i = 0; i < dd.colorlist.size(); i++) {
            Color c = dd.colorlist.get(i);
            if (c.getRGB() == color.getRGB())
                return i;
        }
        return -1;
    }

    public static Color getCurrentColor() {
        return getColor(cindex);
    }

    public static void setDefaultPolygonColor(int index) {
        polygoncolor = index;
    }

    public static void reset() {
        dd = new DrawData();
    }


    
    /////////////////////////////////////////////////////////////////////


    public static void SavePS(SortedSet<Integer> vc, SortedSet<Integer> vd, SortedSet<Integer> vw, FileOutputStream fp, int stype) throws IOException {

        fp.write("%-----define color, dash and width\n".getBytes());

        for (Integer In : vc) {
            Color c = dd.colorlist.get(In.intValue());
            if (stype == 0) {
                String rs = new Double(((1000 * c.getRed()) / 255) / 1000.0).toString();
                String rg = new Double((((1000 * c.getGreen()) / 255)) / 1000.0).toString();
                String rb = new Double(( ((1000 * c.getBlue()) / 255)) / 1000.0).toString();
                String s = "/Color" + In.toString() + "{" + rs
                        + " " + rg + " " + rb
                        + " " + "setrgbcolor" + "} " + " def " + "\n";
                fp.write(s.getBytes());
            } else if (stype == 1) {
                double gray = (0.11 * c.getRed() + 0.59 * c.getGreen() + 0.3 * c.getBlue()) / 255;
                String s = "/Color" + In.toString() + "{" + gray
                        + " " + gray + " " + gray
                        + " " + "setrgbcolor" + "} " + " def " + "\n";
                fp.write(s.getBytes());

            } else if (stype == 2) {
                String s = "/Color" + In.toString() + "{" + 0.0
                        + " " + 0.0 + " " + 0.0
                        + " " + "setrgbcolor" + "} " + " def " + "\n";
                fp.write(s.getBytes());
            }
        }

        for (Integer In : vd) {
            Double db = dd.dashlist.get(In.intValue());
            int v = (int) (db.doubleValue());

            String s;

            s = "/Dash" + In.toString() + " ";
            if (v == 0)
                s += " {[] 0 setdash} def" + "\n";
            else
                s += " {[" + db.toString()
                        + " " + db.toString() + "] 0 " + "setdash" + "} def" + "\n";
            fp.write(s.getBytes());
        }

        for (Integer In : vw) {
            Double db = dd.widthlist.get(In.intValue());
            String s = "/Width" + In.toString() + " {" + db.toString() + " setlinewidth} def " + "\n";
            fp.write(s.getBytes());
        }
    }

}



