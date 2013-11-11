package maths;

/**
 * I think <code>TPoly</code> represents a triangular sequence of polynomial equations 
 * (as a directed tail-recursive list of polynomial functions, construed as being equal to zero).
 * 
 * In Wu's method, one represents geometric constraints in terms of polynomial equations where
 * each polynomial in the tail-recursive list has exactly one variable that does not appear in the tail.
 */
public class TPoly {
    public TMono poly;
    public TPoly next;

    public TPoly() {
        poly = null;
        next = null;
    }

    public TPoly(TMono m) {
    	next = null;
    	poly = m;
    }
    
    public TPoly(TMono m, TPoly t) {
    	next = t;
    	poly = m;
    }
    
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((next == null) ? 0 : next.hashCode());
		result = prime * result + ((poly == null) ? 0 : poly.hashCode());
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
		if (!(obj instanceof TPoly))
			return false;
		TPoly other = (TPoly) obj;
		if (next == null) {
			if (other.next != null)
				return false;
		} else if (!next.equals(other.next))
			return false;
		if (poly == null) {
			if (other.poly != null)
				return false;
		} else if (!poly.equals(other.poly))
			return false;
		return true;
	}

	public TPoly getNext() {
        return next;
    }

    public TMono getPoly() {
        return poly;
    }

    public void setNext(TPoly n) {
        next = n;
    }

    public void setPoly(TMono p) {
        poly = p;
    }

    public int length() {
    	return (next == null) ? 1 : 1 + next.length();
    }

    public long callength() {
        TPoly p = this;
        long len = 0;
        do {
        	// Iterate through each polynomial expression in the tail (inclusive of this).
            TPoly pp = p;
            while (pp != null) {
            	// Count every term in every polynomial expression.
                ++len;
                pp = pp.next;
            }
            p = p.getNext();
        } while (p != null);
        return len;
    }

    public void reduce(int length) { // Original code was in CharacteristicSetMethod.reduce(TPoly poly)
		if (next != null)
			next.reduce(length);
		if (PolyBasic.plength(poly) <= length) {
			for (TPoly tx = next; tx != null && tx != this; tx = tx.next ) {
				tx.poly = PolyBasic.prem(tx.poly, PolyBasic.p_copy(poly));
			}
		}
    }
    
}
