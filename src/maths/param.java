package maths;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import wprover.GExpert;

public class param extends Object {
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (Solved ? 1231 : 1237);
		result = prime * result + type;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + xindex;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
        assert(value == value);
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof param))
			return false;
		param other = (param) obj;
		if (Solved != other.Solved)
			return false;
		if (type != other.type)
			return false;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		if (xindex != other.xindex)
			return false;
		return true;
	}

	public static final int VARIABLE = 0;
    public static final int STATIC = 1;

    public int type;
    public int xindex;
    public double value;

    public boolean Solved = false;
    public TMono m = null; // This is used only in drawProcess.java, PolyBasic.java, and LeadVariableDialog.java

    public param() {
		type = VARIABLE;
        xindex = 0;
        value = 0;
    }

    public param(int index, double val) {
    	type = VARIABLE;
        xindex = index;
        value = val;
    }

    public param(final Element thisElement) {
    	this();
    	assert(thisElement != null);
    	if (thisElement != null) {
    		type = GExpert.safeParseInt(thisElement.getAttribute("parameter_type"), VARIABLE);
    		xindex = GExpert.safeParseInt(thisElement.getAttribute("x"), 0);
    		value = GExpert.safeParseDouble(thisElement.getAttribute("value"), 0);
    		Solved = GExpert.safeParseBoolean(thisElement.getAttribute("solved"), false);
    	}
        assert(value == value);
    }
    
    public void setParameterStatic() {
        type = STATIC;
    }

    @Override
    public String toString() {
        assert(value == value);
        String s = "x" + xindex;
        return s;
    }

    public String getString() {
        assert(value == value);
        String s = " x=" + xindex;
        return s;
    }

    public void saveIntoXMLDocument(Element rootElement) {
    	assert(rootElement != null);
        assert(value == value);
    	if (rootElement != null) {
    		Document doc = rootElement.getOwnerDocument();
    		Element elementThis = doc.createElement("parameter");
    		rootElement.appendChild(elementThis);

    		elementThis.setAttribute("type", String.valueOf(type));
    		elementThis.setAttribute("x", String.valueOf(xindex));
    		elementThis.setAttribute("value", String.valueOf(value));
    		elementThis.setAttribute("solved", String.valueOf(Solved));
    	}
    }
    
}
