package gprover;

import java.util.Arrays;


public class ndgcs {
    ndgcs parent = null;
    cndg nd = null;
    int ntype = 0; // 0. Normal,  1. exists.
    boolean leaf = false;
    boolean valid = true;

    int no = -1;
    ndgcs[] child = new ndgcs[3];
    cons[] allcns = new cons[100];


    public void add(cons c) {
        if (c != null) {
            allcns[++no] = c;
        }
    }


    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(allcns);
		result = prime * result + Arrays.hashCode(child);
		result = prime * result + (leaf ? 1231 : 1237);
		result = prime * result + ((nd == null) ? 0 : nd.hashCode());
		result = prime * result + no;
		result = prime * result + ntype;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + (valid ? 1231 : 1237);
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
		if (!(obj instanceof ndgcs))
			return false;
		ndgcs other = (ndgcs) obj;
		if (!Arrays.equals(allcns, other.allcns))
			return false;
		if (!Arrays.equals(child, other.child))
			return false;
		if (leaf != other.leaf)
			return false;
		if (nd == null) {
			if (other.nd != null)
				return false;
		} else if (!nd.equals(other.nd))
			return false;
		if (no != other.no)
			return false;
		if (ntype != other.ntype)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (valid != other.valid)
			return false;
		return true;
	}


	public int getMaxCnsInt() {
        int n = 0;

        for (int i = 0; i <= no; i++) {
            if (allcns[i] == null)
                continue;
            int k = allcns[i].getLastPt();
            if (k > n)
                n = k;
        }
        return n;
    }

    public void add(int i, cons c) {
        if (c != null) {
            allcns[i] = c;
        }
    }

    public int getNotNullNum() {
        int k = 0;
        for (int i = 0; i <= no; i++) {
            if (allcns[i] != null)
                k++;
        }
        return k;
    }

    public void addChild(ndgcs c) {
        for (int i = 0; i < child.length; i++) {
            if (child[i] == null) {
                child[i] = c;
                break;
            }
        }
    }

    public void replace(int m, int n) {
        for (int i = 0; i <= no; i++) {
            cons c1 = allcns[i];
            if (c1 != null)
                c1.replace(m, n);
        }
    }

    public void sort() {

        for (int i = 0; i <= no; i++) {
            cons c1 = allcns[i];
            for (int j = 0; j < i; j++) {
                cons c2 = allcns[j];
                if (c1 != null && c2 != null && compare(c1, c2) < 0) {
                    for (int k = i - 1; k >= j; k--)
                        allcns[k + 1] = allcns[k];
                    allcns[j] = c1;
                    break;
                }
            }
        }

    }

    public static int compare(cons c1, cons c2) {
        int n1 = c1.getLastPt();
        int n2 = c2.getLastPt();
        if (n1 == n2) {
            if (c1.type > c2.type)
                return 1;
            if (c1.type < c2.type)
                return -1;
            return compare1(c1, c2, n1);
        }

        if (n1 > n2)
            return 1;

        return 0;
    }

    private static int compare1(cons c1, cons c2, int n)  // euql type;
    {
        while (n > 0) {
            int n1 = c1.getLessPt(n);
            int n2 = c2.getLessPt(n);
            if (n1 == n2) {
                n = n1;
            } else if (n1 > n2)
                return 1;
            else return -1;
        }
        return 0;
    }

    public void rm_common() {
        for (int i = 0; i <= no; i++) {
            cons c = allcns[i];
            if (c == null)
                continue;
            for (int j = 0; j < i; j++) {
                cons c1 = allcns[j];
                if (c1 != null && c1.isEqual(c)) {
                    allcns[i] = null;
                    break;
                }
            }
        }
    }

    public void reduce() {
        if (nd == null)
            return;
        int a = nd.p[0];
        int b = nd.p[1];

        if (nd.type == gib.NDG_NEQ || nd.type == gib.NDG_NON_ISOTROPIC) {
            for (int i = 0; i <= no; i++) {
                cons c = allcns[i];
                if (c == null)
                    continue;
                c.replace(b, a);   // replace b with a.
                c.reorder();
            }
        }
    }

    public void reorder() {
        for (int i = 0; i <= no; i++) {
            cons c = allcns[i];
            if (c == null)
                continue;
            c.reorder();
        }
    }

    public ndgcs() {
    }

    public ndgcs(ndgcs c) {
        parent = c.parent;
        nd = c.nd;
        no = c.no;
        for (int i = 0; i <= no; i++) {
            if (c.allcns[i] != null)
                allcns[i] = new cons(c.allcns[i]);
        }
    }

    public int getCSindex() {
        int a = -1;
        for (int d = 0; d < child.length; d++) {
            if (child[d] != null)
                a = d;
        }
        return a;
    }
}
