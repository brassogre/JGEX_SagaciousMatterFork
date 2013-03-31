package gprover;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Oct 4, 2006
 * Time: 7:13:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class auxpt {
    String name;
    int type;
    ArrayList<Pro_point> vptlist = new ArrayList<Pro_point>();
    String str;

    public auxpt(int t) {
        type = t;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((str == null) ? 0 : str.hashCode());
		result = prime * result + type;
		result = prime * result + ((vptlist == null) ? 0 : vptlist.hashCode());
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
		if (!(obj instanceof auxpt))
			return false;
		auxpt other = (auxpt) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (str == null) {
			if (other.str != null)
				return false;
		} else if (!str.equals(other.str))
			return false;
		if (type != other.type)
			return false;
		if (vptlist == null) {
			if (other.vptlist != null)
				return false;
		} else if (!vptlist.equals(other.vptlist))
			return false;
		return true;
	}

	public String getConstructedPoint() {
        return vptlist.get(0).toString();
    }

    public int getAux() {
        return type;
    }

    public void addAPt(Pro_point pt) {
        for (int i = 0; i < vptlist.size(); i++)
            if (pt == vptlist.get(i))
                return;
        vptlist.add(pt);
    }

    public int getPtsNo() {
        return vptlist.size();
    }

    public Pro_point getPtsbyNo(int n) {
        return vptlist.get(n);
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < vptlist.size(); i++) {
            Pro_point pt = vptlist.get(i);
            s += pt.getText();
        }
        return "(A" + type + " ): " + s;
    }
}
