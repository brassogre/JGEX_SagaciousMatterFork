package wprover;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2005-8-23
 * Time: 14:50:02
 * To change this template use File | Settings | File Templates.
 */
public class GEEqualDistanceMark extends GraphicEntity {
    private static int DEFAULT_LEN = 8;
    private static int DEFAULT_GAP = 6;


    GEPoint p1, p2;
    private int length = DEFAULT_LEN;
    private int dnum = 2;

    public GEEqualDistanceMark() {
        super(GraphicEntity.EQMARK);
        m_dash = 0;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + dnum;
		result = prime * result + length;
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
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
		if (!(obj instanceof GEEqualDistanceMark))
			return false;
		GEEqualDistanceMark other = (GEEqualDistanceMark) obj;
		if (dnum != other.dnum)
			return false;
		if (length != other.length)
			return false;
		if (p1 == null) {
			if (other.p1 != null)
				return false;
		} else if (!p1.equals(other.p1))
			return false;
		if (p2 == null) {
			if (other.p2 != null)
				return false;
		} else if (!p2.equals(other.p2))
			return false;
		return true;
	}

	public int getLength() {
        return length;
    }

    public void setLength(int n) {
        length = n;
    }

    public int getNum() {
        return dnum;
    }

    public void setNum(int n) {
        dnum = n;
    }

    public GEEqualDistanceMark(GEPoint pp1, GEPoint pp2) {
        super(GraphicEntity.EQMARK);
        m_color = 3;
        m_dash = 0;
        p1 = pp1;
        p2 = pp2;
    }

    public GEEqualDistanceMark(GEPoint pp1, GEPoint pp2, int d) {
        super(GraphicEntity.EQMARK);
        m_color = 3;
        m_dash = 0;
        p1 = pp1;
        p2 = pp2;
        dnum = d;
    }

    public void setdnum(int d) {
        dnum = d;
    }

    @Override
    public String TypeString() {
        String st = Language.getLs(125, "Equal Mark");
        return st;
    }

    @Override
    public String getDescription() {
        String st = Language.getLs(125, "Equal Mark");
        return st + " " + p1.m_name + " " + p2.m_name;
    }

    public void drawALine(double x, double y, double dx, double dy, Graphics2D g2) {
        double xx1 = x - dy * length;
        double yy1 = y + dx * length;

        double xx2 = x + dy * length;
        double yy2 = y - dx * length;
        g2.drawLine((int) xx1, (int) yy1, (int) xx2, (int) yy2);

    }

    void draw(Graphics2D g2, GEPoint d) {


    }

    @Override
    public void draw(Graphics2D g2, boolean selected) {
        if (!isdraw()) return;

        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();
        double dy = y2 - y1;
        double dx = x2 - x1;

        if (dy * dy + dx * dx < DEFAULT_LEN * DEFAULT_LEN)
            return;

        double x = (x1 + x2) / 2;
        double y = (y1 + y2) / 2;

        double len = Math.sqrt(dx * dx + dy * dy);
        dx /= len;
        dy /= len;


        if (selected) {
            g2.setStroke(CMisc.SelectObjectStroke);
            g2.setColor(CMisc.SelectObjectColor);
        } else
            this.prepareToBeDrawnAsUnselected(g2);

        //g2.setColor(this.getColor());
        if (dnum % 2 == 0) {
            int dgap = DEFAULT_GAP / 2;

            for (int i = 0; i < dnum / 2; i++) {
                double xx = x + dgap * dx;
                double yy = y + dgap * dy;
                this.drawALine(xx, yy, dx, dy, g2);
                xx = x - dgap * dx;
                yy = y - dgap * dy;
                this.drawALine(xx, yy, dx, dy, g2);
                dgap += DEFAULT_GAP;
            }
        } else {
            int dgap = DEFAULT_GAP;
            this.drawALine(x, y, dx, dy, g2);
            for (int i = 0; i < dnum / 2; i++) {
                double xx = x + dgap * dx;
                double yy = y + dgap * dy;
                this.drawALine(xx, yy, dx, dy, g2);
                xx = x - dgap * dx;
                yy = y - dgap * dy;
                this.drawALine(xx, yy, dx, dy, g2);
                dgap += DEFAULT_GAP;
            }
        }

        if (!this.isdraw()) {
            g2.setColor(Color.white);
            g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        }
    }

    @Override
    public boolean isLocatedNear(double x, double y) {
        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();

        double xx = (x1 + x2) / 2;
        double yy = (y1 + y2) / 2;

        x = x - xx;
        y = y - yy;

        double fx = (double) dnum * length / 2;
        double fy = length / 2;

        double dx = x1 - x2;
        double dy = y1 - y2;

        double len = Math.sqrt(dx * dx + dy * dy);
        dx /= len;
        dy /= len;

        double a = (x * dx + y * dy) / (dx * dx + dy * dy);
        if (Math.abs(a) > fx + CMisc.PIXEPS)
            return false;
        double b = (y * dx - x * dy) / (dx * dx + dy * dy);
        if (Math.abs(b) > fy + CMisc.PIXEPS)
            return false;
        return true;
    }

    public void savePsLine(double x, double y, double dx, double dy, FileOutputStream fp) throws IOException {
        int xx1 = (int) (x - dy * length);
        int yy1 = (int) (y + dx * length);

        int xx2 = (int) (x + dy * length);
        int yy2 = (int) (y - dx * length);

        String s = "";
        s += xx1 + " " + (-yy1) + " moveto " + xx2 + " " + (-yy2) + " lineto ";
        fp.write(s.getBytes());

    }

    @Override
    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (!isdraw()) return;
        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();
        double dy = y2 - y1;
        double dx = x2 - x1;

        if (dy * dy + dx * dx < DEFAULT_LEN * DEFAULT_LEN)
            return;

        double x = (x1 + x2) / 2;
        double y = (y1 + y2) / 2;

        double len = Math.sqrt(dx * dx + dy * dy);
        dx /= len;
        dy /= len;

        if (dnum % 2 == 0) {
            int dgap = DEFAULT_GAP / 2;

            for (int i = 0; i < dnum / 2; i++) {
                double xx = x + dgap * dx;
                double yy = y + dgap * dy;

                this.savePsLine(xx, yy, dx, dy, fp);
                xx = x - dgap * dx;
                yy = y - dgap * dy;
                this.savePsLine(xx, yy, dx, dy, fp);
                dgap += DEFAULT_GAP;
            }
        } else {
            int dgap = DEFAULT_GAP;
            this.savePsLine(x, y, dx, dy, fp);
            for (int i = 0; i < dnum / 2; i++) {
                double xx = x + dgap * dx;
                double yy = y + dgap * dy;
                this.savePsLine(xx, yy, dx, dy, fp);
                xx = x - dgap * dx;
                yy = y - dgap * dy;
                this.savePsLine(xx, yy, dx, dy, fp);
                dgap += DEFAULT_GAP;
            }
        }
        this.saveSuper(fp);
    }

 // TODO: Implement load and save methods
    
//    @Override
//    public void Save(DataOutputStream out) throws IOException {
//        super.Save(out);
//        out.writeInt(p1.m_id);
//        out.writeInt(p2.m_id);
//        out.writeInt(length);
//        out.writeInt(dnum);
//
//    }
//
//    @Override
//    public void Load(DataInputStream in, drawProcess dp) throws IOException {
//        super.Load(in, dp);
//        int id = in.readInt();
//        p1 = dp.getPointById(id);
//        id = in.readInt();
//        p2 = dp.getPointById(id);
//        length = in.readInt();
//        dnum = in.readInt();
//    }

}
