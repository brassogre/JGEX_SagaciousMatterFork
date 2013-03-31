package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-5-4
 * Time: 11:32:50
 * To change this template use File | Settings | File Templates.
 */
public class xterm {
    public var var;                //   variable.
    long c;                 //   value is an Integer.
    dterm ps;              //  prefix
    xterm p;                //
    String sd;

    public xterm() {
        var = null;
        c = 0;
        ps = null;
        p = null;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (c ^ (c >>> 32));
		result = prime * result + ((p == null) ? 0 : p.hashCode());
		result = prime * result + ((ps == null) ? 0 : ps.hashCode());
		result = prime * result + ((sd == null) ? 0 : sd.hashCode());
		result = prime * result + ((var == null) ? 0 : var.hashCode());
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
		if (!(obj instanceof xterm))
			return false;
		xterm other = (xterm) obj;
		if (c != other.c)
			return false;
		if (p == null) {
			if (other.p != null)
				return false;
		} else if (!p.equals(other.p))
			return false;
		if (ps == null) {
			if (other.ps != null)
				return false;
		} else if (!ps.equals(other.ps))
			return false;
		if (sd == null) {
			if (other.sd != null)
				return false;
		} else if (!sd.equals(other.sd))
			return false;
		if (var == null) {
			if (other.var != null)
				return false;
		} else if (!var.equals(other.var))
			return false;
		return true;
	}

	public long getPV() {
        if (ps == null || ps.p == null) return 0;
        return ps.p.c;
    }

    public String toString() {
        return sd;
    }

    public void cutMark() {
        if (sd != null && sd.trim().startsWith("+"))
            sd = sd.trim().substring(1);
    }

    public String getString() {
        if (sd == null) return null;
        String t = sd.trim();
        if (t.startsWith("+"))
            return t.substring(1).trim();
        return t;
    }

    public int getTermNumber() {
        xterm t = this;
        int n = 0;
        while (t != null) {
            dterm d = t.ps;
            if (d == null || d.nx == null) return n;
            t = d.nx.p;
            n++;
        }
        return n;
    }
}
