package gprover;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-4-17
 * Time: 13:17:41
 * To change this template use File | Settings | File Templates.
 */
public class el_term {
    final public static int EL_CYCLIC = 11;
    final public static int EL_PARA = 2;
    final public static int EL_PERP = 3;
    public int etype = 0;
    public var v;
    public xterm p1, p2, p;
    public int np = 1;
    public cond co;
    public el_term nx;
    public String text = "";

    public el_term et;

    public el_term() {
    }

    public void setText(String s) {
        text = s;
    }

    public String toString() {
        return text;
    }

    public ArrayList<xterm> getAllxterm() {
        ArrayList<xterm> vv = new ArrayList<xterm>();
        vv.add(p);

        xterm x = p1;
        while (x != null) {
            vv.add(x);
            dterm d = x.ps;
            if (d != null)
                d = d.nx;
            if (d != null)
                x = d.p;
            else
                break;
        }
        
        if (vv.size() > 0) {
            x = vv.get(0);
            x.cutMark();
        }
        return vv;
    }

    public int getEType() {
        return etype;
    }

    public ArrayList<cond> getAllCond() {
        ArrayList<cond> vv = new ArrayList<cond>();
        if (co != null) {
            cond c = co;
            while (c != null) {
                vv.add(c);
                c = c.nx;
            }
        }
        if (et != null) {
            el_term e = et;
            while (e != null) {
                if (e.co != null) {
                    cond c = e.co;
                    while (c != null) {
                        vv.add(c);
                        c = c.nx;
                    }
                }
                e = e.nx;
            }
        }
        return vv;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((co == null) ? 0 : co.hashCode());
		result = prime * result + ((et == null) ? 0 : et.hashCode());
		result = prime * result + etype;
		result = prime * result + np;
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
		result = prime * result + ((p == null) ? 0 : p.hashCode());
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((v == null) ? 0 : v.hashCode());
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
		if (!(obj instanceof el_term))
			return false;
		el_term other = (el_term) obj;
		if (co == null) {
			if (other.co != null)
				return false;
		} else if (!co.equals(other.co))
			return false;
		if (et == null) {
			if (other.et != null)
				return false;
		} else if (!et.equals(other.et))
			return false;
		if (etype != other.etype)
			return false;
		if (np != other.np)
			return false;
		if (nx == null) {
			if (other.nx != null)
				return false;
		} else if (!nx.equals(other.nx))
			return false;
		if (p == null) {
			if (other.p != null)
				return false;
		} else if (!p.equals(other.p))
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
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (v == null) {
			if (other.v != null)
				return false;
		} else if (!v.equals(other.v))
			return false;
		return true;
	}
}
