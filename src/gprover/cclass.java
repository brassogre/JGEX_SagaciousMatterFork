/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-15
 * Time: 13:28:06
 * To change this template use File | Settings | File Templates.
 */
package gprover;

public class cclass {
	final public static int MAX_GEO = 40;
    public static long id_count = 0;
    long id = id_count++;
    long dep = gib.depth;
    int type;
    String text;

    public String toString() {
        return text;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (dep ^ (dep >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		if (!(obj instanceof cclass))
			return false;
		cclass other = (cclass) obj;
		if (dep != other.dep)
			return false;
		if (id != other.id)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
