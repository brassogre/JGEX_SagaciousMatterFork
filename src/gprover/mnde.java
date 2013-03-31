package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 17, 2006
 * Time: 5:00:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class mnde {
    int t, type;
    public angtr tr;
    mnde nx;
    public mnde()
    {
        t = 1;
        type = 0;
        tr = null;
        nx = null;
        
    }
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
		result = prime * result + t;
		result = prime * result + ((tr == null) ? 0 : tr.hashCode());
		result = prime * result + type;
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
		if (!(obj instanceof mnde))
			return false;
		mnde other = (mnde) obj;
		if (nx == null) {
			if (other.nx != null)
				return false;
		} else if (!nx.equals(other.nx))
			return false;
		if (t != other.t)
			return false;
		if (tr == null) {
			if (other.tr != null)
				return false;
		} else if (!tr.equals(other.tr))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	public void cp(mnde m)  // Convert this into a copy of passed parameter
    {
        t = m.t;
        type = m.type;
        tr = m.tr;
    }
}
