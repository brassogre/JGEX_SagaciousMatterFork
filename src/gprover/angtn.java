package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 16, 2006
 * Time: 2:21:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class angtn extends cclass {
    int lemma;
    public l_line ln1, ln2, ln3, ln4;
    public int t1, t2;
    cond co;
    angtn nx;

    public angtn(l_line l1, l_line l2, l_line l3, l_line l4) {
        this();
        ln1 = l1;
        ln2 = l2;
        ln3 = l3;
        ln4 = l4;
    }

    public angtn() {
        ln1 = ln2 = ln3 = ln4 = null;
        co = null;
        nx = null;
        t1 = t2 = lemma = 0;

    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((co == null) ? 0 : co.hashCode());
		result = prime * result + lemma;
		result = prime * result + ((ln1 == null) ? 0 : ln1.hashCode());
		result = prime * result + ((ln2 == null) ? 0 : ln2.hashCode());
		result = prime * result + ((ln3 == null) ? 0 : ln3.hashCode());
		result = prime * result + ((ln4 == null) ? 0 : ln4.hashCode());
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
		result = prime * result + t1;
		result = prime * result + t2;
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
		if (!(obj instanceof angtn))
			return false;
		angtn other = (angtn) obj;
		if (co == null) {
			if (other.co != null)
				return false;
		} else if (!co.equals(other.co))
			return false;
		if (lemma != other.lemma)
			return false;
		if (ln1 == null) {
			if (other.ln1 != null)
				return false;
		} else if (!ln1.equals(other.ln1))
			return false;
		if (ln2 == null) {
			if (other.ln2 != null)
				return false;
		} else if (!ln2.equals(other.ln2))
			return false;
		if (ln3 == null) {
			if (other.ln3 != null)
				return false;
		} else if (!ln3.equals(other.ln3))
			return false;
		if (ln4 == null) {
			if (other.ln4 != null)
				return false;
		} else if (!ln4.equals(other.ln4))
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
		return true;
	}
}
