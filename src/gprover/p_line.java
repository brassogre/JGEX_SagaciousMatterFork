/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-14
 * Time: 21:33:19
 * To change this template use File | Settings | File Templates.
 */
package gprover;

import java.util.Arrays;

public class p_line extends cclass {
    int lemma;
    cond co;
    public int no;
    public l_line[] ln;

    p_line nx;

    public p_line(l_line l1, l_line l2) {
        this();
        ln[0] = l1;
        ln[1] = l2;
        no = 1;
    }

    public p_line() {
        type = lemma = no = 0;
        co = null;
        ln = new l_line[MAX_GEO];
        nx = null;
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
		result = prime * result + Arrays.hashCode(ln);
		result = prime * result + no;
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
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
		if (!(obj instanceof p_line))
			return false;
		p_line other = (p_line) obj;
		if (co == null) {
			if (other.co != null)
				return false;
		} else if (!co.equals(other.co))
			return false;
		if (lemma != other.lemma)
			return false;
		if (!Arrays.equals(ln, other.ln))
			return false;
		if (no != other.no)
			return false;
		if (nx == null) {
			if (other.nx != null)
				return false;
		} else if (!nx.equals(other.nx))
			return false;
		return true;
	}
}
