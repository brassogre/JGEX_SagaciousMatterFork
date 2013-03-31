package maths;

public class TPoly {
    public TMono poly;
    public TPoly next;

    public TPoly() {
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((next == null) ? 0 : next.hashCode());
		result = prime * result + ((poly == null) ? 0 : poly.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TPoly))
			return false;
		TPoly other = (TPoly) obj;
		if (next == null) {
			if (other.next != null)
				return false;
		} else if (!next.equals(other.next))
			return false;
		if (poly == null) {
			if (other.poly != null)
				return false;
		} else if (!poly.equals(other.poly))
			return false;
		return true;
	}

	public TPoly getNext() {
        return next;
    }

    public TMono getPoly() {
        return poly;
    }

    public void setNext(TPoly n) {
        next = n;
    }

    public void setPoly(TMono p) {
        poly = p;
    }

    public int length() {
        TPoly tp = this;
        int i = 0;

        while (tp != null) {
            tp = tp.next;
            i++;
        }
        return i;

    }

    public long callength() {
        TPoly p = this;
        long len = 0;
        while (p != null) {
            long l = TPoly.plength(p.getPoly()) + 1;
            len = len + l;
            p = p.getNext();
        }
        return len;
    }

    private static long plength(TMono p) {
        TMono pt;
        int i;

        pt = p;
        i = -1;
        while (pt != null) {
            ++i;
            pt = pt.next;
        }
        return i;
    }

}
