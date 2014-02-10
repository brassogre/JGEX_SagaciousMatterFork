package wprover;

import org.eclipse.jdt.annotation.NonNull;

import maths.TMono;

/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2005-7-28
 * Time: 13:31:13
 */
public class MathHelper {

    final static String SPI = "��";
    final static String SE = "e";
    final public static String[] sfunction = {"sin", "cos", "tan", "arcsin", "arccos", "arctan",
            "abs", "sqrt", "ln", "log", "sgn", "round", "trunc", "area"};

    final static int PLUS = 1;
    final static int MINUS = 2;
    final static int MUL = 3;
    final static int DIV = 4;
    final static int SQRT = 5;
    final static int SQURAR = 6;
    final static int CUBE = 7;
    final static int SIN = 8;
    final static int COS = 9;
    final static int TAN = 10;
    final static int CTAN = 11;

    final static int EXP = 12;
    final static int NODE = 13;
    final static int VALUE = 14;
    final static int FUNC = 15;
    final static int PARAM = 16;
    final static int AREA = 17;


    final static int PI = 20;
    final static int E = 21;


    int TYPE;

    String sname = "";
    int value = 1;
    int index = -1;
    double dvalue;

    MathHelper left;
    MathHelper right;

    public MathHelper() {
    }

    private MathHelper(int t) {
        TYPE = t;
    }


    public static MathHelper parseString(String str) {
        TMono index = new TMono(0, 0, 0);

        return parseEntityA(str.toCharArray(), index);
    }

//    public static CTextValue parse(byte[] src, TMono index) {
//        return null;
//    }

    public static int getFunction(String s) {
        for (int i = 0; i < sfunction.length; i++) {
            if (s.equalsIgnoreCase(sfunction[i]))
                return i;
        }
        return -1;
    }

    public static String parseFunction(char[] src, TMono index) {
        parseSpace(src, index);
        int i = index.x;
        String s = new String();

        if (src[i] == 'x' || src[i] == 'X') return s;

        while ((src[i] >= '0' && src[i] <= '9') || (src[i] >= 'a' && src[i] <= 'z') || (src[i] >= 'A' && src[i] <= 'Z')) {
            char[] bb = new char[1];
            bb[0] = src[i];
            s += new String(bb);
            i++;
            if (i >= src.length) break;
        }
        index.x = i;
        return s;
    }


    private static MathHelper parseEntityA(char[] src, TMono index) {

        MathHelper ct1 = parseEntityB(src, index);
        parseSpace(src, index);

        int i = index.x;
        if (i >= src.length)
            return ct1;

        char b = src[i];
        while (b == '+' || b == '-') {

            index.x++;

            MathHelper ct2 = parseEntityB(src, index);
            if (ct2 == null)
                break;

            MathHelper ct = new MathHelper();
            if (b == '+')
                ct.TYPE = PLUS;
            else if (b == '-')
                ct.TYPE = MINUS;
            ct.left = ct1;
            ct.right = ct2;
            ct1 = ct;
            parseSpace(src, index);
            i = index.x;
            if (i >= src.length)
                break;
            b = src[i];
        }
        return ct1;
    }

    private static MathHelper parseEntityB(char[] src, TMono index) {

        parseSpace(src, index);
        MathHelper ct1 = parseEntityC(src, index);
        parseSpace(src, index);
        if (index.x >= src.length)
            return ct1;

        char b = src[index.x];
        while (b == '*' || b == '/') {
            index.x++;
            MathHelper ct2 = parseEntityC(src, index);
            if (ct2 == null)
                break;

            MathHelper ct = new MathHelper();
            if (b == '*')
                ct.TYPE = MUL;
            else if (b == '/')
                ct.TYPE = DIV;

            ct.left = ct1;
            ct.right = ct2;
            ct1 = ct;
            parseSpace(src, index);
            int i = index.x;
            if (i >= src.length)
                break;
            b = src[i];
        }
        return ct1;
    }

    private static MathHelper parseEntityC(char[] src, TMono index) { // ^
        parseSpace(src, index);
        MathHelper t1 = parseEntityD(src, index);
        parseSpace(src, index);
        int i = index.x;
        if (i >= src.length)
            return t1;

        MathHelper vl = null;

        char b = src[i];
        while (b == '^') {
            index.x++;
            MathHelper t2 = parseEntityD(src, index);

            if (vl == null) {
                vl = new MathHelper(EXP);
                vl.left = t1;
                vl.right = t2;
            } else {
                MathHelper v = new MathHelper(EXP);
                MathHelper t = vl;
                while (t.right.right != null)
                    t = t.right;
                v.left = t.right;
                v.right = t2;
                t.right = v;
            }

            parseSpace(src, index);
            i = index.x;
            if (i >= src.length)
                break;
            b = src[i];
        }
        if (vl != null)
            return vl;
        else return t1;
    }

    private static MathHelper parseEntityD(char[] src, TMono index) { // (), x1,value.


        parseSpace(src, index);
        int i = index.x;
        if (i >= src.length)
            return null;

        if (src[i] == '(') {
            index.x = i + 1;
            MathHelper ct = parseEntityA(src, index);
            parseSpace(src, index);
            index.x++;
            return ct;
        } else if (src[i] == 'x' || src[i] == 'X') {
            index.x++;
            MathHelper t1 = new MathHelper(NODE);
            int v = (int) parseInt(src, index);
            t1.TYPE = NODE;
            t1.index = v;
            return t1;
        } else if (src[i] >= '0' && src[i] <= '9') {
            double v = parseInt(src, index);
            MathHelper t1 = new MathHelper(NODE);
            t1.dvalue = v;
            t1.TYPE = VALUE;
            return t1;
        } else if (src[i] == 960){
            MathHelper t1 = new MathHelper(PI);
            index.x++;
            return t1;
        } else if (src[i] == 'e') {
            MathHelper t1 = new MathHelper(E);
            index.x++;
            return t1;
        } else {
            String s = parseFunction(src, index);
            if (s.length() != 0) {
                int fn = getFunction(s);
                parseSpace(src, index);

                int d = index.x;
//                index.x++;
//
//                int d = index.x;
                if (d < src.length && src[d] == '(') { // function.
                    MathHelper t1 = new MathHelper();
                    t1.TYPE = FUNC;
                    t1.value = fn;
                    index.x++;      //(
                    parseSpace(src, index);
                    t1.left = parseEntityA(src, index);
                    parseSpace(src, index);
                    index.x++;      //)
                    return t1;
                } else // parameter.
                {
                    MathHelper t1 = new MathHelper();
                    t1.TYPE = PARAM;
                    t1.sname = s;
                    return t1;
                }
            }
        }

        UtilityMiscellaneous.print("Error input polynomial type in CTextValue");
        return null;

    }

/*    private static char parseByte(char[] src, TMono index) {
        parseSpace(src, index);
        int i = index.x;
        if (i >= src.length) return 0;

        char b = src[i];
        if (b != '+' && b != '-') {
            index.x = i;
            return 0;
        }
        index.x++;

        parseSpace(src, index);
        return b;
    }*/

    /*private static char parseByteB(char[] src, TMono index) {
        parseSpace(src, index);
        int i = index.x;
        if (i >= src.length) return 0;

        if (src[i] == '*' || src[i] == '/') {
            index.x++;
            parseSpace(src, index);
            return src[i];
        }
        return 0;
    }*/

    private static double parseInt(char[] src, TMono index) {

        parseSpace(src, index);

        int i = index.x;
        if (i >= src.length)
            return 0.0;

        char b = src[i];
        double v = 0;

        double d = 0.1;

        int step = 0; // 0. Befor  1.after.
        while (b >= '0' && b <= '9' || b == '.') {
            if (b == '.')
                step = 1;
            else if (step == 0) {
                v *= 10;
                v += (b - '0');
            } else {
                v += (b - '0') * d;
                d *= 0.1;
            }

            i++;
            index.x++;
            if (i >= src.length) break;

            b = src[i];
        }

        parseSpace(src, index);
        return v;
    }

    private static void parseSpace(char[] src, TMono index) {

        int i = index.x;

        if (i >= src.length) return;

        while (i < src.length && src[i] == ' ')
            i++;

        index.x = i;

    }

/*    private static String getAString(char[] src, TMono index) {
        parseSpace(src, index);
        int i = index.x;
        String s = new String();

        if (src == null || i >= src.length) return s;

        while ((src[i] >= 'a' && src[i] <= 'z') || (src[i] >= 'A' && src[i] <= 'Z')) {
            s += src[i];
            i++;
            if (i >= src.length) break;
        }

        if (s.length() != 0) {
            index.x = i;
            return s;
        }


        if (src[i] == '(' || src[i] == ')' || src[i] == '*' || src[i] == '/' || src[i] == '^') {
            s += src[i];
            index.x = i + 1;
            return s;
        }

        while (src[i] >= '0' && src[i] <= '9') {
            s += src[i];
            i++;
            if (i >= src.length) break;

        }
        if (s.length() != 0) {
            index.x = i;
            return s;
        }
        parseSpace(src, index);

        return s;
    }*/

    /**
     * Rounds off a double to three decimal places.
     * @param r a double representing the quantity to be rounded off.
     * @return the value of r rounded off to three decimal places.
     */
    public static double round3(double r) {
        return Math.round(r * 1000 + 0.1) / 1000.0;
    }

    public void calculate(DrawPanel dp) {
        double r = calcValue(this, dp);
        dvalue = round3(r);
    }

    public static double calcValue(@NonNull MathHelper ct, @NonNull DrawPanel dp) {
        return dp.calculate(ct);
    }

//    "sin", "cos", "tan", "arcsin", "arccos", "arctan",

    //            "abs", "sqrt", "ln", "log", "sgn", "round", "trunc"}

    public static double calcFunction(int n, double v) {
        switch (n) {
            case 0:
                return Math.sin(v);
            case 1:
                return Math.cos(v);
            case 2:
                return Math.tan(v);
            case 3:
                return Math.asin(v);
            case 4:
                return Math.acos(v);
            case 5:
                return Math.atan(v);
            case 6:
                return Math.abs(v);
            case 7:
                return Math.sqrt(v);
            case 8:
                return Math.log10(v);
            case 9:
                return Math.log(v);
            case 10:
                return Math.signum(v);
            case 11:
                return Math.round(v);
            case 12:
                return v;//////???
        }
        return v;
    }
}
