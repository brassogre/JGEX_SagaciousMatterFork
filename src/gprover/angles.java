
/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-14
 * Time: 21:34:29
 * To change this template use File | Settings | File Templates.
 */
package gprover;

public class angles extends cclass
{
   // int type;
    int lemma;
    cond co;
    int sa;
    public l_line l1,l2, l3, l4;
    angles nx;
    int atp = 0;

    public angles(l_line l1, l_line l2, l_line l3, l_line l4)
    {
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
        this.l4 = l4;
    }
    public angles()
    {
        type = lemma = sa = 0;
        co = null;
        nx = null;
        l1 = l2 = l3 = l4 = null;
    }
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + atp;
		result = prime * result + ((co == null) ? 0 : co.hashCode());
		result = prime * result + ((l1 == null) ? 0 : l1.hashCode());
		result = prime * result + ((l2 == null) ? 0 : l2.hashCode());
		result = prime * result + ((l3 == null) ? 0 : l3.hashCode());
		result = prime * result + ((l4 == null) ? 0 : l4.hashCode());
		result = prime * result + lemma;
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
		result = prime * result + sa;
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
		if (!(obj instanceof angles))
			return false;
		angles other = (angles) obj;
		if (atp != other.atp)
			return false;
		if (co == null) {
			if (other.co != null)
				return false;
		} else if (!co.equals(other.co))
			return false;
		if (l1 == null) {
			if (other.l1 != null)
				return false;
		} else if (!l1.equals(other.l1))
			return false;
		if (l2 == null) {
			if (other.l2 != null)
				return false;
		} else if (!l2.equals(other.l2))
			return false;
		if (l3 == null) {
			if (other.l3 != null)
				return false;
		} else if (!l3.equals(other.l3))
			return false;
		if (l4 == null) {
			if (other.l4 != null)
				return false;
		} else if (!l4.equals(other.l4))
			return false;
		if (lemma != other.lemma)
			return false;
		if (nx == null) {
			if (other.nx != null)
				return false;
		} else if (!nx.equals(other.nx))
			return false;
		if (sa != other.sa)
			return false;
		return true;
	}

}
