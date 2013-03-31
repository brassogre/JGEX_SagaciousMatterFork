package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-5-4
 * Time: 11:32:44
 * To change this template use File | Settings | File Templates.
 */

public class dterm
{
      public int deg;          //degree
      public xterm p;         //A term
      public dterm nx;       // All next terms.

      public String text;

      public dterm()
      {
            deg = 0;
            p = null;
            nx = null;
            text = null;
      }

      /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + deg;
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
		result = prime * result + ((p == null) ? 0 : p.hashCode());
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
		if (!(obj instanceof dterm))
			return false;
		dterm other = (dterm) obj;
		if (deg != other.deg)
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
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	public String toString()
      {
            return text;
      }
}
