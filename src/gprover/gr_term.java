package gprover;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-4-17
 * Time: 13:16:18
 * To change this template use File | Settings | File Templates.
 */
public class gr_term {

    private int ptn = -1;

    long c1;
    dterm ps1;
    long c2;
    dterm ps2;
    int c;                      /* construction */

    public el_term el;       /* eliminatns */
    public dterm ps;                 /* simplicifiers*/

    public gr_term nx;


    public String text = "";

    public String toString() {
        return text;
    }

    public dterm getps1() {
        return ps1;
    }

    public xterm getds1() {
        if (ps1 != null)
            return ps1.p;
        else return null;
    }

    public void setPTN(int n) {
        ptn = n;
    }

    public int getPTN() {
        return ptn;
    }

    public boolean isZero() {
        if (ps1 == null) {
            if (c1 == 0)
                return true;
            else return false;
        }
        xterm p = ps1.p;
        if (p.var == null && p.c == 0) return true;
        return false;
    }

    public ArrayList<xterm> getAllxterm() {
        ArrayList<xterm> v = new ArrayList<xterm>();
        if (ps1 != null && ps1.p != null) {
            xterm x = ps1.p;
            while (x != null) {
                v.add(x);
                dterm d = x.ps;
                if (d != null)
                    d = d.nx;
                if (d != null)
                    x = d.p;
                else break;
            }
        }
        if (v.size() > 0) {
            xterm x = v.get(0);
            x.cutMark();
        }
        return v;
    }

    public ArrayList<var> getAllvars() {
        ArrayList<var> v = new ArrayList<var>();
        getPSVar(v, ps1);
        getPSVar(v, ps2);
        return v;
    }

    void getPSVar(ArrayList<var> v, dterm d) {
        while (d != null) {
            getPVar(v, d.p);
            d = d.nx;
        }
    }

    void getPVar(ArrayList<var> v, xterm x) {
        if (x == null) return;
        if (x.var != null)
            v.add(x.var);
        getPSVar(v, x.ps);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + c;
		result = prime * result + (int) (c1 ^ (c1 >>> 32));
		result = prime * result + (int) (c2 ^ (c2 >>> 32));
		result = prime * result + ((el == null) ? 0 : el.hashCode());
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
		result = prime * result + ((ps == null) ? 0 : ps.hashCode());
		result = prime * result + ((ps1 == null) ? 0 : ps1.hashCode());
		result = prime * result + ((ps2 == null) ? 0 : ps2.hashCode());
		result = prime * result + ptn;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		if (!(obj instanceof gr_term))
			return false;
		gr_term other = (gr_term) obj;
		if (c != other.c)
			return false;
		if (c1 != other.c1)
			return false;
		if (c2 != other.c2)
			return false;
		if (el == null) {
			if (other.el != null)
				return false;
		} else if (!el.equals(other.el))
			return false;
		if (nx == null) {
			if (other.nx != null)
				return false;
		} else if (!nx.equals(other.nx))
			return false;
		if (ps == null) {
			if (other.ps != null)
				return false;
		} else if (!ps.equals(other.ps))
			return false;
		if (ps1 == null) {
			if (other.ps1 != null)
				return false;
		} else if (!ps1.equals(other.ps1))
			return false;
		if (ps2 == null) {
			if (other.ps2 != null)
				return false;
		} else if (!ps2.equals(other.ps2))
			return false;
		if (ptn != other.ptn)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}


}
