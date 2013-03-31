package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 17, 2006
 * Time: 11:06:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class angtr extends cclass {
    public int v, t1, t2;
    public l_line l1;
    public l_line l2;
    cond co;
    angtr nx;

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
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
		result = prime * result + t1;
		result = prime * result + t2;
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
		if (!(obj instanceof angtr))
			return false;
		angtr other = (angtr) obj;
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
		if (nx == null) {
			if (other.nx != null)
				return false;
		} else if (!nx.equals(other.nx))
			return false;
		if (t1 != other.t1)
			return false;
		if (t2 != other.t2)
			return false;
		if (v != other.v)
			return false;
		return true;
	}

	public angtr() {
        l1 = l2 = null;
        co = null;
        nx = null;
        v = 0;
    }

    public int get_lpt1() {
        if (t1 != 0) return t1;
        return l_line.get_lpt1(l1, v);
    }

    public int get_lpt2() {
        if (t2 != 0) return t2;
        return l_line.get_lpt1(l2, v);
    }
}
