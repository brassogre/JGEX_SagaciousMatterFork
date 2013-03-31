package gprover;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-4-14
 * Time: 13:31:19
 * To change this template use File | Settings | File Templates.
 */
public class var {
    int nm;
    char[] p = new char[9];
    public int[] pt = new int[4];
    var nx;

    String sd = null;

    public var() {

    }

    public var(int n, int p1, int p2, int p3, int p4) {
        nm = n;
        pt[0] = p1;
        pt[1] = p2;
        pt[2] = p3;
        pt[3] = p4;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + nm;
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
		result = prime * result + Arrays.hashCode(p);
		result = prime * result + Arrays.hashCode(pt);
		result = prime * result + ((sd == null) ? 0 : sd.hashCode());
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
		if (!(obj instanceof var))
			return false;
		var other = (var) obj;
		if (nm != other.nm)
			return false;
		if (nx == null) {
			if (other.nx != null)
				return false;
		} else if (!nx.equals(other.nx))
			return false;
		if (!Arrays.equals(p, other.p))
			return false;
		if (!Arrays.equals(pt, other.pt))
			return false;
		if (sd == null) {
			if (other.sd != null)
				return false;
		} else if (!sd.equals(other.sd))
			return false;
		return true;
	}

	public void revert() {
        int k = pt[0];
        pt[0] = pt[2];
        pt[2] = k;

        k = pt[1];
        pt[1] = pt[3];
        pt[3] = k;
    }

    public var(var v) {
        nm = v.nm;
        pt[0] = v.p[0];
        pt[1] = v.p[1];
        pt[2] = v.p[2];
        pt[3] = v.p[3];
    }

    public String toString() {
        return sd;
    }

    public void setString(String s) {
        sd = s;
    }
}
