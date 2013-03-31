package maths;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: 2007-5-28
 * Time: 22:11:39
 * To change this template use File | Settings | File Templates.
 */
public class GMono {
    public int x = 0;
    public int deg = 0;
    public BigFraction val = null;
    public TMono coef = null;
    public TMono next = null;

    public GMono() {

    }

    public GMono(int x, int val, int deg) {
        this.x = x;
        this.deg = deg;
        if (x == 0 || deg == 0) {
            this.x = 0;
            this.deg = 0;
            this.val = new BigFraction(val,1);
        } else {
            this.coef = new TMono(0, val, 0);
        }

    }

    public GMono(int x, TMono coef, int deg) {
        this.x = x;
        this.deg = deg;
        this.coef = coef;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coef == null) ? 0 : coef.hashCode());
		result = prime * result + deg;
		result = prime * result + ((next == null) ? 0 : next.hashCode());
		result = prime * result + ((val == null) ? 0 : val.hashCode());
		result = prime * result + x;
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
		if (!(obj instanceof GMono))
			return false;
		GMono other = (GMono) obj;
		if (coef == null) {
			if (other.coef != null)
				return false;
		} else if (!coef.equals(other.coef))
			return false;
		if (deg != other.deg)
			return false;
		if (next == null) {
			if (other.next != null)
				return false;
		} else if (!next.equals(other.next))
			return false;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		if (x != other.x)
			return false;
		return true;
	}
    
}
