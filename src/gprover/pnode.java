package gprover;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 30, 2006
 * Time: 12:40:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class pnode {

    int id;

    int n = 0;
    long[] val = new long[30];
    pnode nx;

    public void add(long x) {
        val[n++] = x;
    }

    public void add(cclass x) {
        val[n++] = x.id;
    }

    public long get(int d) {
        return val[d];
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + n;
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
		result = prime * result + Arrays.hashCode(val);
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
		if (!(obj instanceof pnode))
			return false;
		pnode other = (pnode) obj;
		if (id != other.id)
			return false;
		if (n != other.n)
			return false;
		if (nx == null) {
			if (other.nx != null)
				return false;
		} else if (!nx.equals(other.nx))
			return false;
		if (!Arrays.equals(val, other.val))
			return false;
		return true;
	}
}
