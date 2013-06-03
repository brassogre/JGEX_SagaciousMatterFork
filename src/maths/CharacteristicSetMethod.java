package maths;

public class CharacteristicSetMethod {
    final private static boolean DEBUG = false;
    //private static CharacteristicSetMethod charset = new CharacteristicSetMethod();
    private static int REDUCE_LEN = 2;

    public static boolean debug() {
        return DEBUG;
    }

//    public static CharacteristicSetMethod getinstance() {
//        return charset;
//    }

    public static long freemonos() {
        return 0;
    }

    public static TPoly charset(TPoly pp) {
        TPoly rm, ch, chend, p, output;
        output = null;
        p = pp;
        rm = p;

        if (rm == null) return pp;
        pp = reduceReverse(pp);

        while (rm != null) {
            TPoly tp;
            ch = chend = tp = rm;

            int vra = PolyBasic.lv(tp.getPoly());
            tp = tp.getNext();
            if (tp == null) {
                output = PolyBasic.ppush(rm.getPoly(), output);
                break;
            }
            int v = PolyBasic.lv(tp.getPoly());

            while (vra == v) {
                chend = tp;
                tp = tp.getNext();
                if (tp == null)
                    break;
                v = PolyBasic.lv(tp.getPoly());
            }
            chend.setNext(null);
            rm = tp;


            if (ch == chend) {
                output = PolyBasic.ppush(ch.getPoly(), output);
            } else {
                TPoly poly = null;

                while (ch.getNext() != null) {
                    TMono divor = PolyBasic.getMinV(vra, ch);
                    do {
                        TMono out;
                        TMono div = ch.getPoly();
                        if (div == divor) continue;
                        out = PolyBasic.prem(div, PolyBasic.p_copy(divor));

                        int a = PolyBasic.lv(out);
                        if (a == 0) {
                            if (DEBUG) {
                                System.out.println("Condition redundancy---------------------:");
                                PolyBasic.print(div);
                                PolyBasic.print(divor);
                            }
                        } else if (vra > a)
                            rm = PolyBasic.ppush(out, rm);
                        else
                            poly = PolyBasic.addpoly(out, poly);
                    } while ((ch = ch.getNext()) != null);
                    if (poly == null) {
                        output = PolyBasic.ppush(divor, output);
                        break;
                    } else {
                        poly = PolyBasic.addpoly(divor, poly);
                        ch = poly;
                        poly = null;
                    }
                }

            }
        }

//        this.printpoly(output);
        reduce(output);
        TPoly tp = reverse(output);
        if (!cfinished(tp))
            tp = charset(tp);

        TPoly p1 = tp;
        while (p1 != null) {
            TMono m = p1.getPoly();
            PolyBasic.factor1(m);
            PolyBasic.coefgcd(m);
            p1 = p1.getNext();
        }
        return tp;
    }

    public static TPoly reduceReverse(TPoly poly) // 1, 2, 3, 4, 5
    {
        poly = reverse(poly);
        reduce(poly);
        poly = reverse(poly);
        return poly;
    }

    public static void reduce(TPoly poly) {    // n ,n-1,,,,,,, 1.
    	if (poly != null)
    		poly.reduce(REDUCE_LEN);
    	
//        TPoly p1 = poly;
//        while (p1 != null) {
//            TMono m = p1.poly;
//            if (PolyBasic.plength(m) <= REDUCE_LEN) {
//                TPoly tx = poly;
//                while (tx != null && tx != p1) {
//                    TMono m2 = tx.poly;
//                    tx.poly = PolyBasic.prem(m2, PolyBasic.p_copy(m));
//                    tx = tx.next;
//                }
//            }
//            p1 = p1.next;
//        }
    }

    public static boolean cfinished(TPoly pp) {
    	if (pp != null) {
    		int a = PolyBasic.lv(pp.getPoly());
    		pp = pp.getNext();
    		while (pp != null) {
    			int n = PolyBasic.lv(pp.getPoly());
    			if (a == n)
    				return false;
    			else {
    				a = n;
    				pp = pp.getNext();
    			}
    		}
    	}
    	return true;
    }

    public static void printpoly(TPoly pp) {
        int i = 0;
        while (pp != null) {
            if (pp.getPoly() != null) {
                System.out.print("f" + String.valueOf(i++) + "= ");
                PolyBasic.print(pp.getPoly());
            }
            pp = pp.getNext();
        }
    }

    /**
     * Returns a TPoly that has the reverse order of the given TPoly.
     * @param pp The polynomial whose terms are to be used in constructing the reverse.
     * @return A copy of pp in reverse.
     */
    public static TPoly reverse(TPoly pp) {
        TPoly out = null;
        while (pp != null) {
            TPoly p = pp;
            pp = pp.getNext();

            if (out == null) {
                out = p;
                out.setNext(null);
            } else {
                p.setNext(out);
                out = p;
            }
        }
        return out;
    }

    public static TPoly pushpoly(TMono p, TPoly pp) {
        TPoly pt = new TPoly(p, pp);
        return pt;
    }

}
