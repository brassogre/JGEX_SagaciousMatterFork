package gprover;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 19, 2006
 * Time: 9:27:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class l_list extends cclass {
    final public static int VALUE = 0;
    final public static int LINE = 1;
    final public static int ANGLE = 2;

    final public static int MAX_MDE = 10;

    boolean solved = false;

    int type = -1;
    public mnde[] md;
    public mnde[] mf;
    public int nd, nf;
    int npt, pt;

    public rule[] rl;
    public l_list fr;
    l_list nx;


    public l_list() {
        md = new mnde[MAX_MDE];
        mf = new mnde[MAX_MDE];
        rl = new rule[MAX_MDE];
        nd = nf = 0;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fr == null) ? 0 : fr.hashCode());
		result = prime * result + Arrays.hashCode(md);
		result = prime * result + Arrays.hashCode(mf);
		result = prime * result + nd;
		result = prime * result + nf;
		result = prime * result + npt;
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
		result = prime * result + pt;
		result = prime * result + Arrays.hashCode(rl);
		result = prime * result + (solved ? 1231 : 1237);
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
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof l_list))
			return false;
		l_list other = (l_list) obj;
		if (fr == null) {
			if (other.fr != null)
				return false;
		} else if (!fr.equals(other.fr))
			return false;
		if (!Arrays.equals(md, other.md))
			return false;
		if (!Arrays.equals(mf, other.mf))
			return false;
		if (nd != other.nd)
			return false;
		if (nf != other.nf)
			return false;
		if (npt != other.npt)
			return false;
		if (nx == null) {
			if (other.nx != null)
				return false;
		} else if (!nx.equals(other.nx))
			return false;
		if (pt != other.pt)
			return false;
		if (!Arrays.equals(rl, other.rl))
			return false;
		if (solved != other.solved)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public int get_npt() {
        int num = 0;
        int t = 0;

        for (int i = 0; i < nd; i++) {
            int k = 1;
            int n = md[i].tr.v;
            for (int j = i + 1; j < nd; j++) {
                if (n == md[j].tr.v)
                    k++;
            }
            if (k > num) {
                num = k;
                t = n;
            }
        }
        npt = num;
        pt = t;
        return num;
    }

    public void cp(l_list ls) {
        type = ls.type;
        nd = ls.nd;
        nf = ls.nf;
        for (int i = 0; i < nd; i++) {
            md[i] = new mnde();
            md[i].cp(ls.md[i]);
        }
        for (int i = 0; i < nf; i++) {
            mf[i] = new mnde();
            mf[i].cp(ls.mf[i]);
        }

    }

    public void sub1(angtr t1, angtr t2) {
        for (int i = 0; i < nd; i++) {
            if (md[i].tr == t1) {
                md[i].tr = t2;
                return;
            }
        }
    }

    public void add_md(mnde m) {
        if (m == null) return;

        int i;
        for (i = 0; i < MAX_MDE && md[i] != null; i++) ;
        md[i] = m;
        nd = i + 1;
    }

    public void add_mf(mnde m) {
        if (m == null) return;
        int i;
        for (i = 0; i < MAX_MDE && mf[i] != null; i++) ;
        mf[i] = m;
        nf = i + 1;

    }

    public void add_rule(rule r) {
        int i;
        for (i = 0; i < MAX_MDE && rl[i] != null; i++) ;
        rl[i] = r;
    }

    public String toString() {
        return text;
    }

}
