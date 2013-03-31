package wprover;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: 2007-5-9
 * Time: 15:30:31
 * To change this template use File | Settings | File Templates.
 */
public class grule {
    public int type;
    public int rx;  // 0. GDD,  1. FULL
    public String name;
    public String head;
    public String description;
    public String exstring;

    public grule(int t, String t1, String t2, String t3, boolean bFull) {
        type = t;
        head = t1;
        description = t2;
        exstring = t3;
        if (t1.contains("#")) {
            String[] s = t1.split("#");
            name = s[1];
        }
        rx = bFull ? 1 : 0;
    }

    public boolean isGDDRule() {
        return rx == 0;
    }

    public boolean isFullRule() {
        return rx == 1;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((exstring == null) ? 0 : exstring.hashCode());
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + rx;
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
		if (!(obj instanceof grule))
			return false;
		grule other = (grule) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (exstring == null) {
			if (other.exstring != null)
				return false;
		} else if (!exstring.equals(other.exstring))
			return false;
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (rx != other.rx)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
