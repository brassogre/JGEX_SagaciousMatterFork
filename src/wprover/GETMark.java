package wprover;

import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;

public class GETMark extends GraphicEntity {

    GELine ln1, ln2;
    private double tx, ty;
    private int length = -1;


    // Position;
    int pos1x, pos1y, pos2x, pos2y;
    int pos3x, pos3y, pos4x, pos4y;


    public GETMark() {
        super(TMARK);
        m_color = 3;
        m_dash = 0;
        m_color = drawData.RED;
    }

    public GETMark(GELine ln1, GELine ln2) {
        super(TMARK);

        m_color = 3;
        m_dash = 0;
        this.ln1 = ln1;
        this.ln2 = ln2;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + length;
		result = prime * result + ((ln1 == null) ? 0 : ln1.hashCode());
		result = prime * result + ((ln2 == null) ? 0 : ln2.hashCode());
		result = prime * result + pos1x;
		result = prime * result + pos1y;
		result = prime * result + pos2x;
		result = prime * result + pos2y;
		result = prime * result + pos3x;
		result = prime * result + pos3y;
		result = prime * result + pos4x;
		result = prime * result + pos4y;
		long temp;
		temp = Double.doubleToLongBits(tx);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(ty);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (!(obj instanceof GETMark))
			return false;
		GETMark other = (GETMark) obj;
		if (length != other.length)
			return false;
		if (ln1 == null) {
			if (other.ln1 != null)
				return false;
		} else if (!ln1.equals(other.ln1))
			return false;
		if (ln2 == null) {
			if (other.ln2 != null)
				return false;
		} else if (!ln2.equals(other.ln2))
			return false;
		if (pos1x != other.pos1x)
			return false;
		if (pos1y != other.pos1y)
			return false;
		if (pos2x != other.pos2x)
			return false;
		if (pos2y != other.pos2y)
			return false;
		if (pos3x != other.pos3x)
			return false;
		if (pos3y != other.pos3y)
			return false;
		if (pos4x != other.pos4x)
			return false;
		if (pos4y != other.pos4y)
			return false;
		if (Double.doubleToLongBits(tx) != Double.doubleToLongBits(other.tx))
			return false;
		if (Double.doubleToLongBits(ty) != Double.doubleToLongBits(other.ty))
			return false;
		return true;
	}

	@Override
    public String TypeString() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    void move(double dx, double dy) {
        double r[] = GELine.Intersect(ln1, ln2);
        if (r == null || r.length == 0)
            return;

        int len = (int) (Math.sqrt(Math.pow(r[0] - dx, 2) + Math.pow(r[1] - dy, 2)));
        //int len1 = (int) (Math.sqrt(Math.pow(tx - dx, 2) + Math.pow(ty - dy, 2)));
        double ddx = tx - r[0];
        double ddy = ty - r[1];
        double ddx1 = dx - r[0];
        double ddy1 = dy - r[1];

        if (ddx * ddx1 < 0 && ddy * ddy1 < 0)
            length = CMisc.FOOT_MARK_LENGTH;
        else if (len > 10 && len < 40)
            length = len;
    }

    @Override
    public void draw(Graphics2D g2, boolean selected) {
        if (!isdraw())
            return;
        if (!selected)
            super.prepareToBeDrawnAsUnselected(g2);
        if (!GELine.isPerp(ln1, ln2))
            return;

        double r[] = GELine.Intersect(ln1, ln2);
        if (r != null && r.length == 2) {
            if (ln1.inside(r[0], r[1]) && ln2.inside(r[0], r[1])) {
                GEPoint p = GELine.commonPoint(ln1, ln2);
                if (p == null)
                    drawTTFoot(g2, r[0], r[1], ln1.getfirstPoint(), ln2.getfirstPoint(), selected);
                else
                    drawTTFoot(g2, r[0], r[1], ln1.getSecondPoint(p), ln2.getSecondPoint(p), selected);
            }
        }
    }

    @Override
    public boolean isLocatedNear(double x, double y) {
        boolean xr = Math.pow(tx - x, 2) + Math.pow(ty - y, 2) < CMisc.PIXEPS * CMisc.PIXEPS;
        return xr;
    }

    @Override
    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (!bVisible) return;

        String st1 = pos1x + " " + -pos1y + " moveto " + pos2x + " " + -pos2y + " lineto \n";
        fp.write(st1.getBytes());
        String st2 = pos3x + " " + -pos3y + " moveto " + pos4x + " " + -pos4y + " lineto \n";
        fp.write(st2.getBytes());
        String st3 = "Color" + m_color + " stroke\n";
        fp.write(st3.getBytes());

    }

    public void drawTTFoot(Graphics2D g2, double x, double y, GEPoint p1, GEPoint p2, boolean select) {
        if (p1 == null || p2 == null) return;

        double step = CMisc.FOOT_MARK_LENGTH;
        if (length > 0)
            step = length;

        double dx = p1.getx() - x;
        double dy = p1.gety() - y;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0.0) return;
        dx = (dx / len) * step;
        dy = (dy / len) * step;

        double dx1, dy1;
        dx1 = p2.getx() - x;
        dy1 = p2.gety() - y;
        len = Math.sqrt(dx1 * dx1 + dy1 * dy1);
        if (len == 0.0) return;
        dx1 = (dx1 / len) * step;
        dy1 = (dy1 / len) * step;

        double fx = x;
        double fy = y;
        double ex = fx + dx1 + dx;
        double ey = fy + dy1 + dy;
        tx = ex;
        ty = ey;

        super.prepareToBeDrawnAsUnselected(g2);
        if (select) {
            super.prepareToBeDrawnAsSelected(g2);
        }

        g2.drawLine((int) (fx + dx), (int) (fy + dy), (int) (ex), (int) (ey));
        g2.drawLine((int) (fx + dx1), (int) (fy + dy1), (int) (ex), (int) (ey));

        pos1x = (int) (fx + dx);
        pos1y = (int) (fy + dy);
        pos2x = (int) (ex);
        pos2y = (int) (ey);

        pos3x = (int) (fx + dx1);
        pos3y = (int) (fy + dy1);
        pos4x = (int) (ex);
        pos4y = (int) (ey);
    }

 // TODO: Implement load and save methods
    
//    public void Save(DataOutputStream out) throws IOException {
//        super.Save(out);
//        out.writeInt(ln1.m_id);
//        out.writeInt(ln2.m_id);
//        out.writeInt(length);
//    }
//
//    public void Load(DataInputStream in, drawProcess dp) throws IOException {
//        super.Load(in, dp);
//        int d = in.readInt();
//        ln1 = dp.getLineByid(d);
//        d = in.readInt();
//        ln2 = dp.getLineByid(d);
//        if (CMisc.version_load_now >= 0.053)
//            length = in.readInt();
//        else
//            length = -1;
//    }
}
