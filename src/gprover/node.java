package gprover;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 17, 2006
 * Time: 4:27:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class node {
    int[] p = new int[3];
    node nx;
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nx == null) ? 0 : nx.hashCode());
		result = prime * result + Arrays.hashCode(p);
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
		if (!(obj instanceof node))
			return false;
		node other = (node) obj;
		if (nx == null) {
			if (other.nx != null)
				return false;
		} else if (!nx.equals(other.nx))
			return false;
		if (!Arrays.equals(p, other.p))
			return false;
		return true;
	}
    
    
}
