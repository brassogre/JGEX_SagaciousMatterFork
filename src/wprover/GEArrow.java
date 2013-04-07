package wprover;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2009-12-13
 * Time: 13:43:52
 * To change this template use File | Settings | File Templates.
 */                  

public class GEArrow extends GraphicEntity {
    public static int ANGLE = 30;
    public static int LENGTH = 12;
                                        
    GEPoint st, ed;
    int angle = ANGLE;
    int length = LENGTH;


    public GEArrow(GEPoint p1, GEPoint p2) {
        super(GraphicEntity.ARROW);
        st = p1;
        ed = p2;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + angle;
		result = prime * result + ((ed == null) ? 0 : ed.hashCode());
		result = prime * result + length;
		result = prime * result + ((st == null) ? 0 : st.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof GEArrow))
			return false;
		GEArrow other = (GEArrow) obj;
		if (angle != other.angle)
			return false;
		if (ed == null) {
			if (other.ed != null)
				return false;
		} else if (!ed.equals(other.ed))
			return false;
		if (length != other.length)
			return false;
		if (st == null) {
			if (other.st != null)
				return false;
		} else if (!st.equals(other.st))
			return false;
		return true;
	}

	public final String TypeString() {
        return "Arrow" + m_id;
    }

    public final String getDescription() {
        return "Arrow(" + st + ed + ")";
    }

    public void draw(Graphics2D g2) {
        draw(g2, false);
    }


    public void draw(Graphics2D g2, boolean selected) {
        if (!isdraw()) return;

        double x1, y1, x2, y2;
        x1 = st.getx();
        y1 = st.gety();
        x2 = ed.getx();
        y2 = ed.gety();

        double x = x2 - x1;
        double y = y2 - y1;
        if (x == 0 && y == 0) return;

        double dis = Math.sqrt(x * x + y * y);
        double dx = x / dis;
        double dy = y / dis;
        double xx1 = x1;
        double yy1 = y1;
        double xx2 = x2;
        double yy2 = y2;
        double sin = Math.sin(angle * Math.PI / 180);
        double cos = Math.cos(angle * Math.PI / 180);
        double ddx = dx * length;
        double ddy = dy * length;
        double px1 = xx1 + ddx * cos - ddy * sin;
        double py1 = yy1 + ddx * sin + ddy * cos;
        double px2 = xx1 + ddx * cos + ddy * sin;
        double py2 = yy1 - ddx * sin + ddy * cos;


        ddx = -ddx;
        ddy = -ddy;
//        double qx1 = xx2 + ddx * cos - ddy * sin;
//        double qy1 = yy2 + ddx * sin + ddy * cos;
//        double qx2 = xx2 + ddx * cos + ddy * sin;
//        double qy2 = yy2 - ddx * sin + ddy * cos;


        if (!selected) {
            //g2.setStroke(new BasicStroke(1));
            this.prepareToBeDrawnAsUnselected(g2);
            //g2.setColor(Color.cyan);
        } else {
            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.pink);
        }
        //   g2.drawLine((int) x1, (int) y1, (int) (xx1 + 8 * dy), (int) (yy1 - 8 * dx));
        //   g2.drawLine((int) x2, (int) y2, (int) (xx2 + 8 * dy), (int) (yy2 - 8 * dx));
        g2.drawLine((int) xx1, (int) yy1, (int) px1, (int) py1);
        g2.drawLine((int) xx1, (int) yy1, (int) px2, (int) py2);
        //      g2.drawLine((int) xx2, (int) yy2, (int) qx1, (int) qy1);
        //      g2.drawLine((int) xx2, (int) yy2, (int) qx2, (int) qy2);

        g2.drawLine((int) xx1, (int) yy1, (int) xx2, (int) yy2);
        g2.setColor(Color.black);
    }

    public boolean isLocatedNear(double x, double y) {
        double x1, y1, x2, y2;
        x1 = st.getx();
        y1 = st.gety();

        x2 = ed.getx();
        y2 = ed.gety();

        boolean inside = false;

        if (Math.abs(x1 - x2) < UtilityMiscellaneous.PIXEPS) {
            inside = ((y - y1) * (y - y2) <= 0);
        } else
            inside = ((x - x1) * (x - x2) <= 0);
        if (!inside)
            return false;
        double d = distance(x, y);
        if (d < UtilityMiscellaneous.PIXEPS)
            return true;
        return false;
    }

    public final double getK() {
        GEPoint p1 = st;
        GEPoint p2 = ed;
        return (p2.gety() - p1.gety()) / (p2.getx() - p1.getx());
    }

    public final double distance(double x, double y) {
        double k = -getK();
        GEPoint pt = st;

        if (Math.abs(k) > UtilityMiscellaneous.ZERO && Math.abs(1 / k) < UtilityMiscellaneous.ZERO) {
            return Math.abs(x - pt.getx());
        }
        double len = Math.abs(y + k * x - pt.gety() - k * pt.getx()) / Math.sqrt(1 + k * k);
        return len;
    }

    public void SavePS(FileOutputStream fp, int stype) throws IOException {

    }

// TODO: Implement load and save methods    
    
//    public void Save(DataOutputStream out) throws IOException {
//        super.Save(out);
//        out.writeInt(st.m_id);
//        out.writeInt(ed.m_id);
//        out.writeInt(angle);
//        out.writeInt(length);
//    }
//
//    public void Load(DataInputStream in, drawProcess dp) throws IOException {
//        super.Load(in, dp);
//        st = dp.getPointById(in.readInt());
//        ed = dp.getPointById(in.readInt());
//        angle = in.readInt();
//        length = in.readInt();
//    }
}
