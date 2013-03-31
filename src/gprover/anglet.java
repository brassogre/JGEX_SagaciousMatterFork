package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 4, 2006
 * Time: 1:57:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class anglet extends cclass {   // angle with intersection;
    public int lemma;
    public int p;
    public l_line l1;
    public l_line l2;
    public int v;
    cond co;
    anglet nx;

    public anglet() {
        p = 0;
        l1 = l2 = null;
        v = 0;
        nx = null;
    }

    public anglet(int p, l_line l1, l_line l2, int v) {
        this();
        this.p = p;
        this.l1 = l1;
        this.l2 = l2;
        this.v = v;
    }


    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((co == null) ? 0 : co.hashCode());
		result = prime * result + ((l1 == null) ? 0 : l1.hashCode());
		result = prime * result + ((l2 == null) ? 0 : l2.hashCode());
		result = prime * result + lemma;
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
		result = prime * result + p;
		result = prime * result + v;
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
		if (!(obj instanceof anglet))
			return false;
		anglet other = (anglet) obj;
		if (co == null) {
			if (other.co != null)
				return false;
		} else if (!co.equals(other.co))
			return false;
		if (l1 == null) {
			if (other.l1 != null)
				return false;
		} else if (!l1.equals(other.l1))
			return false;
		if (l2 == null) {
			if (other.l2 != null)
				return false;
		} else if (!l2.equals(other.l2))
			return false;
		if (lemma != other.lemma)
			return false;
		if (nx == null) {
			if (other.nx != null)
				return false;
		} else if (!nx.equals(other.nx))
			return false;
		if (p != other.p)
			return false;
		if (v != other.v)
			return false;
		return true;
	}

	public int get_pt1() {
        if (l1.pt[0] == p)
            return l1.pt[1];
        else
            return l1.pt[0];
    }

    public int get_pt2() {
        if (l2.pt[0] == p)
            return l2.pt[1];
        else
            return l2.pt[0];
    }

    public int get_val(int p1, int p2) {
        if (l1.on_ln(p1) && l2.on_ln(p2)) return v;
        if (l1.on_ln(p2) && l2.on_ln(p1)) return -v;
        return 9999;                // shall never happen.
    }
}
