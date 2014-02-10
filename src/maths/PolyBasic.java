package maths;

import java.math.BigInteger;
import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNull;

public class PolyBasic {
	private static int MAXSTR = 100;
	private static double ZERO = 10E-6;
	private static boolean BB_STOP = false;
	private static boolean RM_SCOEF = true;

	public static void setbbStop(final boolean t) {
		BB_STOP = t;
	}

	public static void setRMCOEF(final boolean s) {
		RM_SCOEF = s;
	}

	private static TMono pp_plus(TMono p1, TMono p2, final boolean es) {

		if (p1 == null)
			return p2;
		if (p2 == null)
			return p1;

		if ((p1.x < p2.x) || ((p1.x == p2.x) && (p1.deg < p2.deg))) {
			final TMono t = p1;
			p1 = p2;
			p2 = t;
		}
		TMono poly = p1;

		if (p1.x > p2.x)// ||(p1.x == p2.x && p1.deg > p2.deg)) append to the
			// last one.
		{
			while (p1.next != null && p1.deg != 0)
				p1 = p1.next;

			if (p1.deg != 0) {
				p1.next = new TMono();
				p1.next.x = p1.x;
				p1 = p1.next;
				p1.coef = p2;
				p1.next = null;
			} else {
				p1.coef = pp_plus(p1.coef, p2, true);
				p1.next = null;
			}
			if (p1.coef == null)
				if (poly == p1)
					poly = null;
				else {
					TMono t = poly;
					while ((t != null) && (t.next != p1))
						t = t.next;
					t.next = null;
				}
		} else if (p1.deg > p2.deg)
			p1.next = pp_plus(p1.next, p2, false);
		else if ((p1.x == 0)) {
			final BigInteger v = p1.val.add(p2.val);
			if (v.compareTo(BigInteger.ZERO) == 0)
				return null;
			p1.val = v;
		} else { // p1.deg == p2.deg
			p1.coef = pp_plus(p1.coef, p2.coef, true);
			p1.next = pp_plus(p1.next, p2.next, false);
			if (p1.coef == null)
				if (poly == p1)
					poly = poly.next;
				else {
					TMono t = poly;
					while ((t != null) && (t.next != p1))
						t = t.next;
					t.next = p1.next;
				}

		}

		if ((poly != null) && (poly.coef == null) && (poly.deg != 0))
			poly = poly.next;

		while ((es == true) && (poly != null) && (poly.deg == 0)
				&& (poly.x != 0))
			poly = poly.coef;

		return poly;
	}

	private static TMono pp_minus(final TMono p1, final TMono p2) {
		final TMono m = pp_plus(p1, cp_times(-1, p2), true);
		return m;
	}

	public static TMono cp_times(final long c, final TMono p1) {
		return cp_times(BigInteger.valueOf(c), p1);
	}

	/**
	 * Multiplies a polynomial by a constant returns the result.
	 * @param c
	 * @param p1
	 * @return The product of <param>c</param> and <param>p1</param>.
	 */
	private static TMono cp_times(final @NonNull BigInteger c, final TMono p1) {
		if ((p1 == null) || (c.compareTo(BigInteger.ZERO) == 0))
			return null;
		if (c.compareTo(BigInteger.ONE) == 0)
			return p1;

		if ((p1.x == 0)) {
			p1.val = p1.val.multiply(c);
			return p1;
		}
		TMono m = p1;
		while (m != null) {
			m.coef = cp_times(c, m.coef);
			m = m.next;
		}
		return p1;
	}

	private static TMono pp_times(TMono p1, TMono p2) // (m X n)
	{
		if ((p1 == null) || (p2 == null))
			return null;
		if ((p2.x == 0))
			return cp_times(p2.val, p1);

		if ((p1.x == 0))
			return cp_times(p1.val, p2);

		if (p1.x > p2.x) {
			TMono tp = p1;
			p1 = p2;
			p2 = tp;
		}

		TMono poly = null;
		while (p1 != null) {
			TMono tp = p1;
			p1 = p1.next;
			tp.next = null;
			TMono tt;

			if (p1 != null)
				tt = mp_times(tp, p_copy(p2));
			else {
				if ((tp.deg == 0) && (tp.x != 0))
					tp = tp.coef;
				tt = mp_times(tp, p2);
			}

			while ((tt != null) && (tt.deg == 0) && (tt.x != 0))
				tt = tt.coef;
			while ((tt != null) && (tt.x != 0) && (tt.coef == null))
				tt = tt.next;

			poly = pp_plus(poly, tt, true);
		}
		return poly;
	}

	private static TMono mp_times(final TMono p1, TMono p2) throws IllegalArgumentException // throws IllegalArgumentException if p1.x > p2.x
	{
		final TMono poly = p2;

		if ((p1 == null) || (p2 == null))
			return null;
		if (isConstant(p1))
			return cp_times(p1.val, p2);

		if (p1.x == p2.x)
			while (p2 != null) {
				p2.deg += p1.deg;
				if (p2.next != null)
					p2.coef = pp_times(p_copy(p1.coef), p2.coef);
				else
					p2.coef = pp_times(p1.coef, p2.coef);

				p2 = p2.next;
			}
		else if (p1.x < p2.x)
			while (p2 != null) {
				//if (p2.next != null) // XXX Why was this if-then used?
					p2.coef = pp_times(p_copy(p1), p2.coef);
				//else
					//p2.coef = pp_times(p_copy(p1), p2.coef);
				p2 = p2.next;
			}
		else {
			throw new IllegalArgumentException();
		}
		return (poly);
	}

	public static boolean check_zero(@NonNull TMono m) {
		if ((m != null) && (m.x == 0) && (m.value() == 0))
			return true;

		while (m != null) {
			if (check_zero(m.coef))
				return true;
			m = m.next;
		}
		return false;
	}

	public static int deg(final @NonNull TMono p, final int x) {
		if (p.x == x)
			return p.deg;
		if (p.x > x) {
			final int d1 = (p.coef != null) ? deg(p.coef, x) : 0;
			final int d2 = (p.next != null) ? deg(p.next, x) : 0;
			return d1 > d2 ? d1 : d2;
		}
		return 0;
	}

	public static TMono reduce(TMono m, final param[] p) {
		if (m == null)
			return null;

		final int x = m.x;
		m = p_copy(m);

		int n = 0;
		for (; n < p.length; ++n) {
			final param pm = p[n];
			if (pm == null)
				break;
			else if (pm.xindex == x) {
				--n;
				break;
			} else if (pm.xindex > x)
				break;
		}

		for (int i = n; i >= 0; --i) {
			final param pm = p[i];
			if ((pm != null) && (pm.m != null))
				m = prem(m, p_copy(pm.m));
		}
		return m;
	}

	/**
	 * This method simplifies a TMono to low degree. Note here we don't remove coef.
	 * @param m
	 * @param p
	 * @return
	 */
	public static TMono simplify(TMono m, final param[] p) {
		if (m == null)
			return null;

		final int x = m.x;
		m = p_copy(m);

		int n = 0;
		for (; n < p.length; n++) {
			final param pm = p[n];
			if (pm == null)
				break;
			else if (pm.xindex == x)
				break;
			else if (pm.xindex > x)
				break;
		}

		for (int i = n; i >= 0; --i) {
			final param pm = p[i];
			if ((pm != null) && (pm.m != null))
				m = prem(m, p_copy(pm.m));
		}
		return m;
	}

	/**
	 * This method appears to return some sort of "reduction" of p1 and p2.
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static TMono prem(final TMono p1, final TMono p2) {
		if (p1 == null)
			return p1;
		if (p2 == null)
			return p1;
		if (p1.x < p2.x)
			return p1;

		TMono result;
		if (p1.x == p2.x) {
			final TMono m = p_copy(p1.coef);
			final TMono n = p_copy(p2.coef);
			result = prem1(p1, p2);
			result = factor_remove(result, m);
			result = factor_remove(result, n);
		} else
			result = prem3(p1, p2);

		coefgcd(result);
		factor1(result);
		return result;
	}

	/*
	 * private int degree(TMono m, int x) { if (m == null || m.x < x) return 0;
	 * while (m != null && m.x > x) { m = m.coef; } if (m != null && m.x == x)
	 * return m.deg; return 0; }
	 */

	private static boolean can_prem3(final TMono p1, final TMono p2) {
		if ((p1 == null) || (p2 == null))
			return false;
		return prem3(p1, p2, false) == null;
	}

	private static TMono prem3(final TMono p1, final TMono p2) {
		return prem3(p1, p2, true);
	}

	private static TMono prem3(TMono p1, final TMono p2, final boolean r /*
	 * do
	 * all
	 */) { // p1.x
		// >
		// p2.x.
		if (p2 == null)
			return p1;
		final int x = p2.x;
		if (x == 0)
			return p1;
		final TMono[] mm = new TMono[2];
		getm2(p1, p2, mm);// , x, p2.deg, mm);
		// boolean rx = false;

		while ((mm[0] != null) && (mm[1] != null)) {
			// int d1 = degree(mm[0], x);
			// int d2 = degree(p2, x);
			if (p1 != null) // if (d1 >= d2) {
				// int dd = d1 - d2;
			{
				// TMono l1 = ptimes(p_copy(mm[0].coef), mm[1]);
				// if (rx) {
				// print(p1);
				// print(p2);
				// print(mm[0]);
				// print(mm[1]);
				// }
				final TMono t1 = pp_times(p1, mm[0]);
				final TMono t2 = pp_times(p_copy(p2), mm[1]);
				// if(rx)
				// {
				// print(t1);
				// print(t2);
				// }
				p1 = pp_minus(t1, t2);
				coefgcd(p1);
				// if (rx) {
				// print(p1);
				// System.out.println("\n");
				// }
				if (p1 == null)
					break;
			}
			if (p1 == null)
				break;
			mm[0] = mm[1] = null;
			getm2(p1, p2, mm);// x, p2.deg, mm); // mm[0]: coef , mm[1]. p.x
			if (!r && (mm[1] != null) && (mm[1].x < p1.x))
				return p1;

		}
		return p1;
	}

	private static void getm2(TMono p1, final TMono p2, final TMono[] mm) {// int
		// x,
		// int
		// deg,
		// TMono[]
		// mm)
		// {
		if ((p1 == null) || (p2 == null) || (p1.x < p2.x)
				|| ((p1.x == p2.x) && (p1.deg < p2.deg)))
			return;

		while (p1 != null) {
			if ((p1.x < p2.x) || ((p1.x == p2.x) && (p1.deg < p2.deg)))
				return;

			if ((p1.x == p2.x) && (p1.deg >= p2.deg)) {
				mm[0] = pth(0, 1, 0);
				mm[1] = pth(0, 1, 0);
				get_mm(p1, p2, mm);
				final int dd = p1.deg - p2.deg;
				mm[0] = p_copy(p2.coef);
				mm[1] = p_copy(p1.coef);
				if (dd > 0)
					mm[1] = ptimes(mm[1], pth(p1.x, 1, dd));
				return;
			} else {
				getm2(p1.coef, p2, mm);
				if ((mm[0] != null) && (mm[1] != null)) {
					mm[1] = ptimes(mm[1], pth(p1.x, 1, p1.deg));
					return;
				}
			}

			p1 = p1.next;
			if ((p1 != null) && (p1.deg == 0))
				p1 = p1.coef;
		}

		// if (p.x > x) {
		// while (p != null) {
		// getm2(p.coef, x, deg, mm);
		// if (mm[0] != null) {
		// TMono m = pth(p.x, 1, p.deg);
		// mm[1] = ptimes(mm[1], m);
		// return;
		// }
		// p = p.next;
		// }
		// } else if (p.deg >= deg) {
		// mm[0] = p;
		// mm[1] = pth(0, 1, 0);
		// return;
		// }
	}

	private static TMono prem1(TMono p1, TMono p2) {

		if (p1 == null)
			return null;

		TMono result = null;
		if (p1.x < p2.x)
			result = p1;
		else if (p1.x > p2.x) {
			result = p1;
			while (p1 != null) {
				final TMono m1 = prem1(p1.coef, (p2));
				p1.coef = m1;
				p1 = p1.next;
			}
		} else if (p1.x == p2.x) {

			if (p1.deg < p2.deg) {
				final TMono t = p1;
				p1 = p_copy(p2);
				p2 = t;
			} else
				p2 = p_copy(p2);

			if (p1.deg >= p2.deg) {
				final int x = p2.x;

				while (p1.x >= x) {
					final int d1 = deg(p1);
					final int d2 = deg(p2);
					if (d1 < d2)
						/*
						 * TMono m = p1; //comment by ye. 5.31. p1 = p2; p2 = m;
						 * int t = d1; d1 = d2; d2 = t;
						 */
						break;
					if (d1 >= d2) {
						final int dd = d1 - d2;
						TMono tp;
						if (dd > 0) {
							tp = pth(x, 1, dd);
							tp = pp_times(tp, p_copy(p2));
						} else
							tp = p_copy(p2);
						p1 = pp_minus(pp_times(p1.next, tp.coef),
								pp_times(p1.coef, tp.next));
						coefgcd(p1);
						if (p1 == null)
							break;
					}
				}
				result = p1;
			}
		}
		while ((result != null) && (result.x != 0) && (result.coef == null))
			result = result.next;

		while ((result != null) && (result.deg == 0) && (result.x != 0))
			result = result.coef;

		// print(result);
		return result;
	}

	public static void div_factor1(TMono m, final int x, final int n) {
		if (x > m.x)
			return;

		if (x == m.x)
			while ((m != null) && (m.deg != 0)) {
				m.deg -= n;
				// if (m.deg < 0) {
				// int k = 0;
				// }
				m = m.next;
			}
		else
			while (m != null) {
				final TMono mx = m.coef;
				if ((mx != null) && (mx.x != 0) && (mx.deg != 0)) {
					div_factor1(mx, x, n);
					if (mx.deg == 0)
						m.coef = mx.coef;
				}
				m = m.next;
				if ((m != null) && (m.x == 0))
					m = m.coef;
			}

	}

	public static void factor2(final TMono m1) {
		TMono m = get_factor2(m1);
		if (m == null)
			return;
		while (m != null) {
			if (m.x != 0)
				div_factor1(m1, m.x, m.deg);
			m = m.coef;
		}
	}

	public static TMono get_factor2(TMono m) {
		if (m == null)
			return null;

		TMono mx = null;
		final TMono m1 = m;
		long n = 0;
		while (m != null) {
			if (m.x != 0)
				n = factor_contain(m.x, m.deg, m1);
			if (n != 0)
				if (mx == null)
					mx = new TMono(m.x, 1, (int) n);
				else
					mx = pp_times(mx, new TMono(m.x, 1, (int) n));
			m = m.coef;
		}
		return mx;
	}

	public static void factor1(final TMono m1) {
		if (!RM_SCOEF)
			return;

		TMono m = get_factor1(m1);
		if (m == null)
			return;
		while (m != null) {
			if (m.x != 0)
				div_factor1(m1, m.x, m.deg);
			m = m.coef;
		}
	}

	public static TMono get_factor1(TMono m) {
		if (m == null)
			return null;

		TMono mx = null;
		final TMono m1 = m;
		m = m.coef;
		long n = 0;
		while (m != null) {
			if (m.x != 0)
				n = factor_contain(m.x, m.deg, m1);
			if (n != 0)
				if (mx == null)
					mx = new TMono(m.x, 1, (int) n);
				else
					mx = pp_times(mx, new TMono(m.x, 1, (int) n));
			m = m.coef;
		}
		return mx;
	}

	/*
	 * private boolean m_contain(long x, long d, TMono m) { if (m == null || x >
	 * m.x || x == m.x && d > m.deg) return false; if (m.x == x && m.deg <= d)
	 * return true;
	 * 
	 * while (m != null) { if (m_contain(x, d, m.coef)) return true; m = m.next;
	 * } return false; }
	 */

	private static long factor_contain(final long x, long n, TMono m) {
		if (m == null)
			return n;

		if (x > m.x)
			return 0;
		else if (x == m.x) {
			while (m.next != null)
				m = m.next;
			if ((m.x == 0) && (m.coef != null))
				return 0;
			else if ((m.deg == 0) && (m.coef == null))
				return n;
			else
				return Math.min(m.deg, n);
		} else if (x < m.x)
			while (m != null) {
				final long t = factor_contain(x, n, m.coef);
				if (t == 0)
					return 0;
				else if (t < n)
					n = t;
				m = m.next;
			}
		return n;
	}

	public static TMono factor_remove(final TMono p1, final TMono p2) { // p1
		// ,p2
		// be
		// destroyed.
		if ((p1 == null) || (p2 == null) || (plength(p1) > 1000))
			return p1;

		if (p1.x == p2.x)
			return p1;

		if (plength(p2) <= 1)
			return p1;

		if (isConstant(p1) || isConstant(p2))
			return p1;
		coefgcd(p2);
		factor1(p1);
		factor1(p2);
		final boolean r = false;
		if (r) {
			print(p1);
			print(p2);
		}

		if (can_prem3(p_copy(p1), p2)) {

			if (CharacteristicSetMethod.debug()) {
				System.out.println("p1 can be factored.");
				System.out.print("p1 = ");
				print(p1);
				System.out.print("p2 = ");
				print(p2);
			}
			final TMono tp2 = p_copy(p2);
			final TMono tp = p_copy(p1);
			final TMono m = div((p1), (p2));
			final TMono rm = pdif(tp, ptimes(tp2, p_copy(m)));
			if (rm != null)
				if (CharacteristicSetMethod.debug()) {
					System.out.print("***********rm = ");
					print(rm);
				}
			if (CharacteristicSetMethod.debug()) {
				System.out.print("result = ");
				print(m);
			}
			return m;
		}
		return p1;
	}

	static TMono div(TMono m, final TMono d) {
		if ((m == null) || (d == null))
			return m;
		if (m.x < d.x)
			return null;
		if ((m.x == d.x) && (m.deg < d.deg))
			return null;

		if ((m.x == 0) && (d.x == 0)) {
			final BigInteger n = m.val.divide(d.val);
			return pth(0, n, 0);
		}

		TMono result = null;

		if (m.x > d.x)
			while (m != null) {
				TMono t = div(m.coef, d);
				if (m.deg != 0)
					t = ptimes(t, pth(m.x, 1, m.deg));
				result = padd(result, t);
				m = m.next;
			}
		else // m.x == d.x;
		{
			final int x = m.x;
			while (m != null) {
				final int dd = m.deg - d.deg;
				if (dd < 0)
					return null;
				TMono m1 = div(m.coef, d.coef);
				if (m1 == null)
					return null; // failed.

				m1 = ptimes(m1, pth(x, 1, dd));
				result = padd(result, p_copy(m1));
				TMono mx = d.next;
				if ((mx != null) && (mx.x != 0) && (mx.deg == 0))
					mx = mx.coef;

				m = pdif(m.next, ptimes((m1), p_copy(d.next)));
			}
		}
		while ((result != null) && (result.x != 0) && (result.coef == null))
			result = result.next;

		while ((result != null) && (result.deg == 0) && (result.x != 0))
			result = result.coef;
		return result;
	}

	public static TMono p_copy(final TMono p) {
		if (p == null)
			return null;

		final TMono p1 = new TMono();
		p1.x = p.x;
		p1.deg = p.deg;
		p1.val = p.val;
		p1.coef = p_copy(p.coef);
		p1.next = p_copy(p.next);
		return p1;
	}

	private static int pp_compare(final TMono p1, final TMono p2) {
		if ((p1 == null) && (p2 == null))
			return 0;
		if ((p1 == null) && (p2 != null))
			return -1;
		if ((p1 != null) && (p2 == null))
			return 1;

		if ((p1.x < p2.x) || ((p1.x == p2.x) && (p1.deg < p2.deg)))
			return -1;
		if ((p1.x > p2.x) || ((p1.x == p2.x) && (p1.deg > p2.deg)))
			return 1;
		final int c = pp_compare(p1.coef, p2.coef);
		if (c != 0)
			return c;
		return pp_compare(p1.next, p2.next);
	}

	public static void ppush(final TMono m, final ArrayList<TMono> v) {
		if (m == null)
			return;

		for (int i = 0; i < v.size(); i++) {
			final TMono m1 = v.get(i);
			final int n = (pp_compare2(m, m1));
			if (n > 0) {
				v.add(i, m);
				return;
			}
		}
		v.add(m);
	}

	private static int pp_compare2(final TMono p1, final TMono p2) {
		if ((p1 == null) && (p2 == null))
			return 0;
		if ((p1 == null) && (p2 != null))
			return -1;
		if ((p1 != null) && (p2 == null))
			return 1;
		if ((p1.x == 0) && (p2.x == 0))
			return 0;

		final int x1 = p1.x;
		final int x2 = p2.x;

		if ((x1 == 0) && (x2 != 0))
			return -1;
		if ((x1 != 0) && (x2 == 0))
			return 1;

		if ((p1.x > p2.x) || ((p1.x == p2.x) && (p1.deg > p2.deg)))
			return 1;
		if ((p1.x < p2.x) || ((p1.x == p2.x) && (p1.deg < p2.deg)))
			return -1;
		int n = pp_compare2(p1.coef, p2.coef);
		if (n == 0)
			n = pp_compare2(p1.next, p2.next);
		return n;
	}

	/*
	 * private int pp_compare1(TMono p1, TMono p2) { if (p1 == null && p2 ==
	 * null) return 0; if (p1 == null && p2 != null) return -1; if (p1 != null
	 * && p2 == null) return 1; int x = Math.max(p1.x, p2.x) + 1; while (x != 0)
	 * { int x1 = getNextX(x, -1, p1); int x2 = getNextX(x, -1, p2); if (x1 >
	 * x2) return 1; else if (x1 < x2) return -1; else { int d1 =
	 * this.getMaxDeg(x1, 1000, -1, p1); int d2 = this.getMaxDeg(x2, 1000, -1,
	 * p2); if (d1 > d2) return 1; else if (d1 < d2) return -1; else { int d =
	 * d1; while (d != 0) { d1 = this.getMaxDeg(x1, d, -1, p1); d2 =
	 * this.getMaxDeg(x2, d, -1, p2); if (d1 > d2) return 1; else if (d1 < d2)
	 * return -1; d--; } } x = x1; } } return 1; }
	 * 
	 * private int getNextX(int x, int x1, TMono m) { // x1 < x . if (m == null)
	 * return x1; if (m.x <= x1) return x1; while (m != null) { if (m.x <= x1)
	 * return x1;
	 * 
	 * if (m.x < x && m.x > x1) x1 = m.x; x1 = getNextX(x, x1, m.coef); m =
	 * m.next; if (m == null) break; if (m.deg == 0) m = m.coef; } return x1; }
	 * 
	 * private int getMaxDeg(int x, int dmax, int d, TMono m) { // dd < d , dd >
	 * d1; return dd; if (m == null) return d; if (m.x < x) return d;
	 * 
	 * while (m != null) { if (m.x < x) return d;
	 * 
	 * if (m.x == x && m.deg > d && m.deg < dmax) d = m.deg; d = getMaxDeg(x,
	 * dmax, d, m.coef); m = m.next; if (m == null) break;
	 * 
	 * if (m.deg == 0) m = m.coef; } return d; }
	 */

	private static boolean isConstant(final TMono p) {
		if (p == null)
			return false;
		if (p.x == 0)
			return true;
		if (p.deg == 0)
			return isConstant(p.coef);
		return false;
	}

	public void dprint(final TMono p, final int dx) {
		upValueTM(p, -dx);
		final String s = getExpandedPrint(p);
		System.out.println(s);
		// print(p);
		upValueTM(p, dx);
	}

	public static void print(final TMono p) {
		if (p == null)
			return;

		// int v = PolyBasic.lv(p);
		// int d = PolyBasic.deg(p);

		System.out.print(String_p_print(p, false, true, true));

		// p_print(p, false, true);
		System.out.print("\n");
	}

	/**
	 * Outputs a string representation of p to the system console.
	 * @param p   The <code>TMono</code> to be printed
	 */
	public static void sprint(final TMono p) {
		p_print(p, false, true);
	}

	/**
	 * Outputs a string representation of p to the system console
	 * @param p   The <code>TMono</code> to be printed
	 * @param bInsideParentheses  Enclose the string in parentheses
	 * @param bFirst   	The TMono is the first in a sequence of terms so suppress the introduction of a "+" sign at the front.
	 * @see #sprint(TMono)
	 */
	private static void p_print(TMono p, boolean bInsideParentheses, final boolean bFirst) {
		if (p == null)
			return;
		if (p.next == null)
			bInsideParentheses = false;

		if (bInsideParentheses) {
			if (bFirst)
				System.out.print("(");
			else
				System.out.print(" + (");
			m_print(p, true);
			p = p.next;
			while (p != null) {
				m_print(p, false);
				p = p.next;
			}
			System.out.print(")");
		} else {
			if (!bFirst) {
				while (p != null) {
					m_print(p, false);
					p = p.next;
				}
			}
			else {
				m_print(p, true);
				p = p.next;
				while (p != null) {
					m_print(p, false);
					p = p.next;
				}
			}
		}
	}

	/**
	 * Outputs a string representation of p to the system console. This method differs by printing the relevant variables as "x", "x^2", "x^3" etc.
	 * @param p   The <code>TMono</code> to be printed
	 * @param bFirst   	The TMono is the first in a sequence of terms so suppress the introduction of a "+" sign at the front.
	 * @see #sprint(TMono)
	 */
	private static void m_print(final TMono p, final boolean bFirst) {
		if (p.x == 0) {
			if (!bFirst) {
				if (p.value() > 0)
					System.out.print(" + ");
				else
					System.out.print(" - ");
				final long t = Math.abs(p.value());
				if (t != 1)
					System.out.print(t);
			} else if (p.value() != 1)
				System.out.print(p.value());
		} else if (p.deg == 0)
			p_print(p.coef, false, bFirst);
		else {
			p_print(p.coef, true, bFirst);
			if (p.coef == null)
				System.out.print("0");
			if (p.x >= 0) {
				if (p.deg != 1)
					System.out.print("x" + p.x + "^" + p.deg);
				else
					System.out.print("x" + p.x);
			} else if (p.deg != 1)
				System.out.print("u" + (-p.x) + "^" + p.deg);
			else
				System.out.print("u" + (-p.x));
		}
	}

	public static TMono pth(final int x, final int c, final int d) {
		return new TMono(x, c, d);
	}

	public static TMono pth(final int x, final BigInteger c, final int d) {
		return new TMono(x, c, d);
	}

	public static int deg(final TMono p) {
		return (p != null) ? p.deg : 0;
	}

	public static int lv(final TMono p) {
		return (p != null) ? p.x : 0;
	}

//	public static TMono pzero() {
//		return null;
//	}

	/**
	 * Returns the number of terms in the polynomial represented by m.
	 * @param m   the polynomial being evaluated 
	 * @return an <code>int</code> representation of the number of terms in m 
	 */
	public static int plength(final TMono m) {
		if (m == null)
			return 0;

		if (isConstant(m))
			return 1;
		
		return plength(m.coef) + plength(m.next);
	}

//	static boolean pzerom(final TMono m) {
//		return (m == null || !pzerop(m.coef) && m.next == null);
//	}

	public static boolean pzerop(final TMono m) {
		if (m == null)
			return true;

		if (isConstant(m))
			return m.value() == 0;
		return pzerop(m.coef) && pzerop(m.next);
	}

	static TPoly addpoly(final TMono t, final TPoly p) {
		final TPoly poly = new TPoly(t, p);
		return poly;
	}

	TPoly addPolytoList(TPoly pl, TPoly pp) {
		if (pl == null)
			return pp;

		while (pl != null) {
			pp = ppush(pl.getPoly(), pp);
			pl = pl.getNext();
		}
		return pp;
	}

	public static TPoly ppush(final TMono t, final TPoly pp) { // n, n-1, ..., 2, 1.
		if (t == null)
			return pp;

		final int vra = PolyBasic.lv(t);
		final TPoly poly = new TPoly(t);

		if (pp == null)
			return poly;

		TPoly former = null;
		TPoly p = pp;

		while (p != null) {
			final int lee = PolyBasic.lv(p.getPoly());
			if (lee > vra) {
				former = p;
				p = p.getNext();
			} else
				break;
		}
		if ((p == null) || (PolyBasic.lv(p.getPoly()) < vra)) {
			poly.setNext(p);
			if (former == null)
				return poly;
			else {
				former.setNext(poly);
				return pp;
			}
		}
		// else ==
		while (p != null)
			if (pp_compare(p.getPoly(), poly.getPoly()) < 0) {
				if (former == null) {
					poly.setNext(p);
					return poly;
				} else {
					former.setNext(poly);
					poly.setNext(p);
					return pp;
				}
			} else {
				former = p;
				p = p.getNext();
			}
		if (former == null) {
			poly.setNext(p);
			return poly;
		}
		former.setNext(poly);
		return pp;
	}

	static double calpoly(TMono m, final param[] p) {
		if (m == null || p == null)
			return 0.0;

		if (isConstant(m))
			return m.value();
		
		double r = 0.0;

		while (m != null) {
			final double v = calpoly(m.coef, p);
			final int id = m.x - 1;
			if (id < 0 || id >= p.length || p[m.x - 1] == null)
				return 0.0;
			r += Math.pow(p[m.x - 1].value, m.deg) * v;
			m = m.next;
		}
		return r;
	}

	public static double[] calculv(TMono mm, final param[] p) {
		int x, d;
		double[] result = null;
		if (mm == null)
			return result;

		x = lv(mm);
		d = deg(mm, x);

		if (d == 1) {
			double val = calpoly(mm.coef, p);
			if (ZERO(val))
				return new double[0];

			result = new double[1];
			final double v1 = calpoly(mm.next, p);
			val = ((-1) * v1) / val;
			result[0] = val;
			return result;
		} else if (d == 2) {

			final TMono a1 = mm.coef;
			TMono b1;
			mm = mm.next;
			if ((mm != null) && (deg(mm) == 1)) {
				b1 = mm.coef;
				mm = mm.next;
			} else
				b1 = null;

			TMono b2;
			if ((mm != null) && (deg(mm) == 0))
				b2 = mm.coef;
			else
				b2 = null;

			final double aa = calpoly(a1, p);
			final double bb1 = calpoly(b1, p);
			final double bb2 = calpoly(b2, p);
			
			if (ZERO(aa))
				return new double[0];

			result = poly_solve_quadratic(aa, bb1, bb2);

		} else if (d == 3 || d == 4) {
			TMono a1, b1, c1, d1, e1;
			a1 = b1 = c1 = d1 = e1 = null;
			while (mm != null) {
				switch (deg(mm)) {
				case 0:
					d1 = mm.coef;
					break;
				case 1:
					c1 = mm.coef;
					break;
				case 2:
					b1 = mm.coef;
					break;
				case 3:
					a1 = mm.coef;
					break;
				case 4:
					e1 = mm.coef;
					break;
				default:
					return null;
				}
				mm = mm.next;
			}
			final double aa = calpoly(a1, p);
			final double bb = calpoly(b1, p);
			final double cc = calpoly(c1, p);
			final double dd = calpoly(d1, p);
			final double ee = calpoly(e1, p);
			
			if (d == 3 && aa != 0.0)
				result = poly_solve_cubic(1, bb / aa, cc / aa, dd / aa);
			else if (d == 4 && ee != 0)
				result = poly_solve_quartic(aa / ee, bb / ee, cc / ee, dd / ee);
		}
		return result;
	}

	public static double[] calculv_2v(final TMono mm, final param[] p) {
		if (mm.next != null)
			return calculv(mm.next.coef, p);
		else
			return null;

	}

	// public double[] calculate_onlinex(TMono mm, param[] p, int dx, int dy) {
	// CLine ln = this.

	// }

	public static double[] calculate_online(final TMono mm, final param[] p,
			final int dx, final int dy) {
		if ((mm.deg != 1) && (mm.x != dy))
			return null;

		final double a = calpoly(mm.coef, p);
		double c = 0.0;
		double b = 0.0;

		if ((mm.deg != 1) && (mm.x != dx))
			return null;

		if (mm.next != null) {
			TMono m1 = mm.next.coef;
			if (m1.x != dx)
				return null;

			b = calpoly(m1.coef, p);
			if (m1.next != null) {
				m1 = m1.next.coef;
				c = calpoly(m1, p);
			}
		} else
			return null;

		final double md = (b * b) + (a * a);
		final double x = p[dx - 1].value;
		final double y = p[dy - 1].value;

		if (Math.abs(md) < ZERO)
			return null;
		final double[] result = new double[2];

		result[0] = ((a * a * x) - (a * b * y) - (b * c)) / md;
		result[1] = ((b * b * y) - (a * b * x) - (a * c)) / md;
		return result;
	}

	public static double[] calculate_oncr(TMono mm, final param[] p, final int dx,
			final int dy) {

		if ((mm.deg != 2) && (mm.x != dy))
			return null;
		final double b2 = calpoly(mm.coef, p);
		mm = mm.next;
		double b1 = 0.0;
		if ((mm != null) && (mm.deg == 1) && (mm.x == dy)) {
			b1 = calpoly(mm.coef, p);
			mm = mm.next;
		}
		if (mm == null)
			return null;

		mm = mm.coef;
		if ((mm.deg != 2) && (mm.x != dx))
			return null;
		final double a2 = calpoly(mm.coef, p);
		if ((Math.abs(a2) < ZERO) || (Math.abs(b2) < ZERO)
				|| (Math.abs(a2 - b2) > ZERO))
			return null;

		mm = mm.next;
		double a1 = 0.0;
		if ((mm != null) && (mm.deg == 1) && (mm.x == dx)) {
			a1 = calpoly(mm.coef, p);
			mm = mm.next;
		}
		double c = 0.0;
		if (mm != null) {
			mm = mm.coef;
			c = calpoly(mm, p);
		}

		final double a = a1 / a2;
		final double b = b1 / a2;
		c = c / a2;
		final double x = p[dx - 1].value;
		final double y = p[dy - 1].value;

		final double yd = y + (b / 2);
		final double xd = x + (a / 2);
		final double r = Math.sqrt((((a * a) / 4) + ((b * b) / 4)) - c);
		final double ln = Math.sqrt((xd * xd) + (yd * yd));
		final double[] result = new double[2];
		// result[0] = -a / 2 + xd * r / ln; // No protection was in place
		// against dividing by zero
		// result[1] = -b / 2 + yd * r / ln;
		result[0] = -a / 2;
		if (((xd * r) != 0) && (ln != 0))
			result[0] += (xd * r) / ln;
		result[1] = -b / 2;
		if (((yd * r) != 0) && (ln != 0))
			result[1] += (yd * r) / ln;
		return result;
	}

	public static double[] calculv2poly(TMono mm1, TMono mm2, final param[] p) // from
	// two
	// poly
	{
		int x, d;
		double[] result;

		TMono a1, b1, c1, m1;

		x = lv(mm1);
		if (deg(mm1, x) < deg(mm2, x)) {
			final TMono m = mm1;
			mm1 = mm2;
			mm2 = m;
		}

		m1 = mm1;
		d = deg(m1, x);

		if (d == 2) {
			a1 = m1.coef;
			m1 = m1.next;
			if (deg(m1) == 1) {
				b1 = m1.coef;
				m1 = m1.next;
			} else
				b1 = null;

			if (m1 != null)
				c1 = m1.coef;
			else
				c1 = null;

			final double ra1 = calpoly(a1, p);
			final double rb1 = calpoly(b1, p);
			final double rc1 = calpoly(c1, p);
			double dl = (rb1 * rb1) - (4 * ra1 * rc1);

			if (Math.abs(dl) < ZERO) {
				result = new double[1];
				result[0] = ((-1) * rb1) / (2 * ra1);
				return result;
			}
			if (dl < 0)
				return null;
			dl = Math.sqrt(dl);
			if (Math.abs(ra1) < ZERO)
				return null;

			result = new double[2];
			result[0] = (((-1) * rb1) + dl) / (2 * ra1);
			result[1] = (((-1) * rb1) - dl) / (2 * ra1);
			return result;
		}

		result = calculv(mm1, p);
		if ((result == null) || (result.length == 0))
			result = calculv(mm2, p);
		if ((result == null) || (result.length == 0))
			// System.out.println("parell two line");
			return null;
		return result;

	}

	public static TMono pRtimes(final TMono p1, final TMono p2) {
		return pp_times(p1, p2);
	}

	public TMono pQtimer(final TMono t1, final TMono t2, final TMono t3,
			final TMono t4) {
		return PolyBasic.pp_times(p_copy(t1),
				pp_times(p_copy(t2), pp_times(p_copy(t3), p_copy(t4))));
	}

	public static TMono padd(final TMono p1, final TMono p2) { // add
		final TMono m = (pp_plus(p1, p2, true));
		return m;
	}

	public static TMono pdif(final TMono p1, final TMono p2) {// minus
		final TMono m = (pp_minus(p1, p2));
		return m;

	}

	static TMono redundancy(final TMono m) {
		return m;
	}

	static boolean isZero(final TMono m) {
		if (m == null)
			return true;
		if (isConstant(m))
			if (m.value() == 0)
				return true;
			else
				return false;

		if ((m.x != 0) && (m.coef == null))
			return isZero(m.next);
		return false;
	}

	public static TMono pcopy(final TMono p) {
		return p_copy(p);
	}

	public static TMono ptimes(final TMono p1, final TMono p2) {
		return pp_times(p1, p2);
	}

	public static TMono pctimes(final TMono p, final long c) {
		return cp_times(BigInteger.valueOf(c), p);
	}

	static void pr(final TMono m) {
		print(m);
	}

	public static void printpoly(final TMono m) {
		print(m);
	}

	public static TMono getMinV(final int x, TPoly p) {
		TMono poly = null;
		int exp = 0;
		while (p != null) {
			final TMono m = p.getPoly();
			if ((m == null) || (m.x != x)) {
				p = p.getNext();
				continue;
			}

			final int e = m.deg;
			if ((e > 0) && ((exp == 0) || (e < exp))) {
				exp = e;
				poly = p.getPoly();
			}
			p = p.getNext();
		}
		return poly;

	}

	public static TPoly OptimizePoly(TPoly poly) {
		final TPoly t = poly;
		while (poly != null) {
			TMono m = poly.getPoly();
			m = opt(m);
			poly.setPoly(m);
			poly = poly.getNext();
		}
		return t;
	}

	private static TMono opt(TMono m) {
		if (m == null)
			return null;

		if (isConstant(m))
			return m;

		if (m.x <= 3 && m.x > 0)
			return null;

		m.coef = opt(m.coef);
		m.next = opt(m.next);

		if (m.coef == null && m.deg != 0)
			m = m.next;
		if (isZero(m))
			return null;

		return m;
	}

	public static String printHead(final TMono m) {
		if (m == null)
			return "0";
		final int v = PolyBasic.lv(m);
		final int d = PolyBasic.deg(m);
		if (d != 1)
			return ("x" + v + "^" + d);
		else
			return "x" + v;
	}

	public static String printSPoly(final TMono m) {
		return printSPoly(m, MAXSTR);
	}

	public static String printNPoly(final TMono m) {
		if (m == null)
			return "";

		String s = String_p_print(m, false, true, true);
		if (s.length() > MAXSTR)
			return s.substring(0, MAXSTR) + ".... != 0";
		else
			s += " !=0";
		return s;
	}

	public static String printNPoly(final TMono m1, final TMono m2) {
		final int n1 = plength(m1);
		final int n2 = plength(m2);
		String s1 = String_p_print(m1, false, true, true);
		String s2 = String_p_print(m2, false, true, true);
		if (n1 > 1)
			s1 = "(" + s1 + ")";
		if (n2 > 1)
			s2 = " (" + s2 + ")";
		return s1 + s2 + " != 0";
	}

	public static String printSPoly(final TMono m, final int n) {
		if (m == null)
			return "0";

		String s = String_p_print(m, false, true, true);
		if (s.length() > n)
			return s.substring(0, n) + "... = 0";
		else
			s += " =0";
		return s;
	}

	public static String printMaxstrPoly(final TMono m) {
		if (m == null)
			return "0";

		final String s = String_p_print(m, false, true, true);
		if (s.length() > MAXSTR)
			return s.substring(0, MAXSTR) + "... 0";
		return s;
	}

	public static String printSPoly1(final TMono m, final int n) {
		if (m == null)
			return "";

		final String s = StringPrint(m);
		if (s.length() > n)
			return s.substring(0, n) + "....";

		return s;
	}

	public static String StringPrint(final TMono p) {
		final StringBuffer buffer = new StringBuffer();
		String_p_print(p, true, buffer);
		return buffer.toString();
	}

	public static void String_p_print(TMono p, final boolean nn,
			final StringBuffer buffer) {
		if (p == null)
			return;
		while (p != null) {
			if (String_mprint(p.coef, buffer))
				buffer.insert(0, '+');
			buffer.append("x" + p.x + "" + p.deg);
			if (p.deg == 0)
				p = p.coef;
			else
				p = p.next;
		}
	}

	public static String String_p_print(TMono p, boolean ce,
			final boolean first, final boolean nn) {
		if (p == null)
			return "";
		if (p.next == null)
			ce = false;
		String s = "";

		if (ce) {
			if (first)
				s += ("(");
			else
				s += (" + (");
			s += (String_m_print(p, true, nn));
			p = p.next;
			while (p != null) {
				if (s.length() > MAXSTR)
					return s;
				s += String_m_print(p, false, true);
				p = p.next;
			}
			s += (")");
		} else if (!first)
			while (p != null) {
				if (s.length() > MAXSTR)
					return s;
				s += String_m_print(p, false, nn);
				p = p.next;
			}
		else {
			s += String_m_print(p, true, nn);
			p = p.next;
			while (p != null) {
				if (s.length() > MAXSTR)
					return s;
				s += String_m_print(p, false, nn);
				p = p.next;
			}
		}
		return s;
	}

	public static boolean String_mprint(TMono m, final StringBuffer buffer) {
		boolean br = false;
		if (m == null)
			return br;

		if (m.next != null)
			br = true;
		if (br)
			buffer.append("(");
		while (m != null) {
			String_mprint(m.coef, buffer);
			if (String_mprint(m.coef, buffer))
				buffer.insert(0, '+');
			buffer.append(m.x + "" + m.deg);
			if (m.deg == 0)
				m = m.coef;
			else
				m = m.next;
		}
		if (br)
			buffer.append(")");
		return br;
	}

	public static String getExpandedPrint(final TMono p) {
		final String r = ep_print(p, "", true);
		if ((r != null) && (r.endsWith("-") || r.endsWith("+")))
			return r + "1";
		return r;
	}

	private static String ep_print(TMono p, final String s, boolean f) {
		String st = "";
		while (p != null) {
			if ((p.next == null) && (p.deg == 0) && (p.x != 0))
				p = p.coef;
			if (p == null)
				break;

			st += eprint(p, s, f);
			f = false;
			if ((p.next == null) && (p.deg == 0))
				p = p.coef;
			else
				p = p.next;
		}
		return st;
	}

	private static String eprint(final TMono p, String s, final boolean f) {
		if (p == null)
			return "";
		if (p.x == 0) // int value;
		{
			if (p.value() == 1) {
				if (f)
					return s;
				else
					return "+" + s;
			} else if (p.value() == -1)
				return "-" + s;
			else if (f || (p.value() < 0))
				return p.value() + "*" + s;
			else
				return "+" + p.value() + "*" + s;

		} else if (p.deg == 0)
			return ep_print(p.coef, s, f);
		else {
			String n = "";
			if (p.x > 0) {
				if (p.deg > 1)
					n = "x" + p.x + "^" + p.deg;
				else
					n = "x" + p.x;
			} else if (p.x < 0)
				if (p.deg > 1)
					n = "u" + (-p.x) + "^" + p.deg;
				else
					n = "u" + (-p.x);
			if (s.length() == 0)
				s = n;
			else
				s = n + "*" + s;
			return ep_print(p.coef, s, f);
		}
	}

	public static String getAllPrinted(TMono p, final boolean b) {
		final int n = MAXSTR;
		MAXSTR = 1000000;
		String s = "";
		boolean f = true;
		while (p != null) {
			if (p.deg != 0)
				if (f)
					s += String_m_print(p, f, true);
				else if (b)
					s += "\n" + String_m_print(p, f, true);
				else
					s += String_m_print(p, f, true);
			if (f)
				f = false;

			if ((p.next == null) && (p.deg == 0))
				p = p.coef;
			else
				p = p.next;
		}
		MAXSTR = n;
		if (b)
			return s + "\n = 0";
		else
			return s;
	}

	public static String getAllPrinted(final TMono p) {
		return getAllPrinted(p, true);
	}

	private static String String_m_print(final TMono p, final boolean first,
			final boolean nn) {

		String s = new String();

		if (p.x == 0) {
			if (nn) {
				final long t = p.value();
				if (t > 0)
					s += "+" + t;
				else
					s += t;
			} else if (first != true) {
				if (p.value() > 0)
					s += (" + ");
				else
					s += (" - ");
				final long t = Math.abs(p.value());
				if (t != 1)
					s += (t);
			} else {
				final long t = p.value();
				if (t == -1)
					s += "-";
				else if (t != 1)
					s += (t);
			}
		} else if (p.deg == 0)
			s += String_p_print(p.coef, false, first, nn);
		else {
			s += String_p_print(p.coef, true, first, false);
			if (p.x >= 0) {
				if (p.deg != 1)
					s += ("x" + p.x + "^" + p.deg);
				else
					s += ("x" + p.x);
			} else if (p.deg != 1)
				s += ("u" + (-p.x) + "^" + p.deg);
			else
				s += ("u" + (-p.x));
		}
		return s;
	}

	public static BigInteger coefgcd(final TMono p) {
		if (p == null)
			return BigInteger.ONE;

		BigInteger c = coefgcd(p, BigInteger.ZERO);

		TMono m = p;
		while ((m != null) && (m.x != 0))
			m = m.coef;
		if (m == null)
			return c;

		if (m.val.compareTo(BigInteger.ZERO) < 0)
			c = c.negate();

		if (c.compareTo(BigInteger.ONE) != 0)
			coef_div(p, c);
		return c;
	}

	private static boolean coef_div(final TMono m, final BigInteger c) {
		if (m == null)
			return true;
		if (m.x == 0) {
			m.val = m.val.divide(c);
			return true;
		} else {
			if (coef_div(m.coef, c))
				return coef_div(m.next, c);
			return false;
		}

	}

	static BigInteger gcd(final BigInteger a, final BigInteger b) {
		return a.gcd(b);
	}

	/*
	 * private static long gcd(long a, long b) { long t;
	 * 
	 * a = Math.abs(a); b = Math.abs(b); while (b != 0) { t = b; b = a % b; a =
	 * t; } return a; }
	 */

	private static BigInteger coefgcd(TMono p, BigInteger c) {

		if (p == null)
			return c;
		if (c.compareTo(BigInteger.ONE) == 0)
			return c;

		while (p != null) {
			if ((p.x == 0)) {
				if (c.compareTo(BigInteger.ZERO) == 0)
					c = p.val;
				else
					c = gcd(c, p.val);
			} else {
				final BigInteger cc = coefgcd(p.coef, c);
				c = gcd(c, cc);
			}
			if (c.compareTo(BigInteger.ONE) == 0)
				return c;

			if ((p.x != 0) && (p.deg == 0))
				p = p.coef;
			else
				p = p.next;
		}
		return c;
	}

	private static boolean ZERO(final double r) {
		return Math.abs(r) < ZERO;
	}

	private static double[] poly_solve_quadratic(double aa, double bb1,
			double bb2) {

		double[] result;
		double mo = Math.pow(Math.abs(aa * bb1 * bb2), 1.0 / 3);
		if (ZERO(mo))
			mo = 1.0;
		aa = aa / mo;
		bb1 = bb1 / mo;
		bb2 = bb2 / mo;

		double dl = ((bb1 * bb1) - (4 * aa * bb2));

		final double tdl = dl;// / (mo * mo);

		if (Math.abs(tdl) < ZERO) {
			result = new double[1];
			result[0] = ((-1) * bb1) / (2 * aa);
			return result;
		}

		if (dl < 0)
			return null;
		dl = Math.sqrt(dl);
		result = new double[2];

		final double x1 = (((-1) * bb1) + dl) / (2 * aa);
		final double x2 = (((-1) * bb1) - dl) / (2 * aa);

		result[0] = x1;
		result[1] = x2;
		return result;
	}

	static double[] poly_solve_cubic(final double a, final double b,
			final double c, final double d) {
		double p = (((3 * c) / a) - ((b * b) / (a * a))) / 3;
		final double q = (((2 * Math.pow(b / a, 3)) - ((9 * b * c) / a / a)) + ((27 * d) / a)) / 27;

		final double D = Math.pow(p / 3, 3) + Math.pow(q / 2, 2);

		if (D >= 0) {
			final double u = cubic_root((-q / 2) + Math.sqrt(D));
			final double v = cubic_root((-q / 2) - Math.sqrt(D));
			final double y1 = u + v;
			// double y2 = -(u + v) / 2 + i(u - v) * sqrt(3) / 2
			// double y3 = -(u + v) / 2 - i(u - v) * sqrt(3) / 2
			final double[] r = new double[1];
			r[0] = y1 - (b / a / 3);
			return r;

		} else if (D < 0) {
			p = Math.abs(p);
			final double phi = Math.acos(-q / 2 / Math.sqrt((p * p * p) / 27));
			final double pi = Math.PI;
			final double y1 = 2 * Math.sqrt(p / 3) * Math.cos(phi / 3);
			final double y2 = -2 * Math.sqrt(p / 3) * Math.cos((phi + pi) / 3);
			final double y3 = -2 * Math.sqrt(p / 3) * Math.cos((phi - pi) / 3);
			// x = y - b / a / 3
			final double t = b / a / 3;
			final double[] r = new double[3];
			r[0] = y1 - t;
			r[1] = y2 - t;
			r[2] = y3 - t;
			return r;
		}
		return null;
	}

	private static double cubic_root(final double r) {
		double r1 = Math.pow(Math.abs(r), 1.0 / 3.0);
		if (r < 0)
			r1 = -r1;
		return r1;
	}

	// double[] cal_e4(double A, double B, double C, double D, double E, double
	// rt) {
	// double a = -3 * B * B / (8 * A * A) + C / A;
	// double b = B * B * B / (8 * A * A * A) - B * C / (2 * A * A) + D / A;
	// double c = -3 * B * B * B * B / (256 * A * A * A * A) + C * B * B / (16 *
	// A * A * A) - B * D / (4 * A * A) + E / A;
	// double p = -a * a / 12 - c;
	// double q = -a * a * a / 108 + a * c / 3 - b * b / 8;
	// double r = q / 2 + Math.sqrt(q * q / 4 + p * p * p / 27);
	// double u = Math.pow(r, 1 / 3.0);
	// double y = -5 / 6 * a - u;
	// if (Math.abs(u) > ZERO)
	// y += p / (3 * u);
	//
	// double w = Math.sqrt(a + 2 * y);
	// double t1 = -(3 * a + 2 * y + 2 * b / w);
	// double t2 = -(3 * a + 2 * y - 2 * b / w);
	// int n = 0;
	// double v1, v2, v3, v4;
	// if (t1 < 0 && t2 < 0)
	// return null;
	// else if (t1 > 0 && t2 > 0)
	// n = 4;
	// else
	// n = 2;
	//
	// int i = 0;
	// double d[] = new double[n];
	//
	// if (t1 > 0) {
	// v1 = -b / (4 * a) + (w + Math.sqrt(t1)) / 2;
	// v2 = -b / (4 * a) + (w - Math.sqrt(t1)) / 2;
	// d[i++] = v1 / rt;
	// d[i++] = v2 / rt;
	//
	// }
	//
	// if (t2 > 0) {
	// v3 = -b / (4 * a) + (-w + Math.sqrt(t2)) / 2;
	// v4 = -b / (4 * a) + (-w - Math.sqrt(t2)) / 2;
	// d[i++] = v3 / rt;
	// d[i++] = v4 / rt;
	// }
	// return d;
	// }

	static double[] poly_solve_quartic(final double a, final double b,
			final double c, final double d) {
		/*
		 * This code is based on a simplification of the algorithm from
		 * zsolve_quartic.c for real roots
		 */
		double aa, pp, qq, rr, rc, sc, tc, mt, x1, x2, x3, x4;
		double w1r, w1i, w2r, w2i, w3r;
		double v1, v2, arg;
		double disc, h;
		int k1, k2;
		double[] u, v, zarr;
		u = new double[3];
		v = new double[3];
		zarr = new double[4];
		// ///////////////////////////////////

		// //////////////////////////////

		k1 = k2 = 0;
		aa = a * a;
		pp = b - ((3.0 / 8.0) * aa);
		qq = c - ((1.0 / 2.0) * a * (b - ((1.0 / 4.0) * aa)));
		rr = d
				- ((1.0 / 4.0) * ((a * c) - ((1.0 / 4.0) * aa * (b - ((3.0 / 16.0) * aa)))));
		rc = (1.0 / 2.0) * pp;
		sc = (1.0 / 4.0) * (((1.0 / 4.0) * pp * pp) - rr);
		tc = -((1.0 / 8.0) * qq * (1.0 / 8.0) * qq);

		/*
		 * This code solves the resolvent cubic in a convenient fashion for this
		 * implementation of the quartic. If there are three real roots, then
		 * they are placed directly into u[]. If two are complex, then the real
		 * root is put into u[0] and the real and imaginary part of the complex
		 * roots are placed into u[1] and u[2], respectively. Additionally, this
		 * calculates the discriminant of the cubic and puts it into the
		 * variable disc.
		 */
		{
			final double qcub = ((rc * rc) - (3 * sc));
			final double rcub = (((2 * rc * rc * rc) - (9 * rc * sc)) + (27 * tc));

			final double Q = qcub / 9;
			final double R = rcub / 54;

			final double Q3 = Q * Q * Q;
			final double R2 = R * R;

			final double CR2 = 729 * rcub * rcub;
			final double CQ3 = 2916 * qcub * qcub * qcub;

			disc = (CR2 - CQ3) / 2125764.0;

			if ((0 == R) && (0 == Q)) {
				u[0] = -rc / 3;
				u[1] = -rc / 3;
				u[2] = -rc / 3;
			} else if (CR2 == CQ3) {
				final double sqrtQ = Math.sqrt(Q);
				if (R > 0) {
					u[0] = (-2 * sqrtQ) - (rc / 3);
					u[1] = sqrtQ - (rc / 3);
					u[2] = sqrtQ - (rc / 3);
				} else {
					u[0] = -sqrtQ - (rc / 3);
					u[1] = -sqrtQ - (rc / 3);
					u[2] = (2 * sqrtQ) - (rc / 3);
				}
			} else if (CR2 < CQ3) {
				final double sqrtQ = Math.sqrt(Q);
				final double sqrtQ3 = sqrtQ * sqrtQ * sqrtQ;
				double theta = Math.acos(R / sqrtQ3);
				if ((R / sqrtQ3) >= 1.0)
					theta = 0.0;
				{
					final double norm = -2 * sqrtQ;

					u[0] = (norm * Math.cos(theta / 3)) - (rc / 3);
					u[1] = (norm * Math.cos((theta + (2.0 * Math.PI)) / 3))
							- (rc / 3);
					u[2] = (norm * Math.cos((theta - (2.0 * Math.PI)) / 3))
							- (rc / 3);
				}
			} else {
				final double sgnR = (R >= 0 ? 1 : -1);
				final double modR = Math.abs(R);
				double x = R2 - Q3;
				if (x <= 0)
					x = 0;
				final double sqrt_disc = Math.sqrt(x); // modified here.
				// 2007.1.2
				final double A = -sgnR * Math.pow(modR + sqrt_disc, 1.0 / 3.0);
				final double B = Q / A;
				final double mod_diffAB = Math.abs(A - B);

				u[0] = (A + B) - (rc / 3);
				u[1] = (-0.5 * (A + B)) - (rc / 3);
				u[2] = -(Math.sqrt(3.0) / 2.0) * mod_diffAB;
				// double sgnR = (R >= 0 ? 1 : -1);
				// double modR = Math.abs(R);
				// double sqrt_disc = Math.sqrt(R2 - Q3);
				// double A = -sgnR * Math.pow(modR + sqrt_disc, 1.0 / 3.0);
				// double B = Q / A;
				// double mod_diffAB = Math.abs(A - B);
				//
				// u[0] = A + B - rc / 3;
				// u[1] = -0.5 * (A + B) - rc / 3;
				// u[2] = -(Math.sqrt(3.0) / 2.0) * mod_diffAB;
			}
		}

		/* End of solution to resolvent cubic */

		/*
		 * Combine the square roots of the roots of the cubic resolvent
		 * appropriately. Also, calculate 'mt' which designates the nature of
		 * the roots: mt=1 : 4 real roots (disc == 0) mt=2 : 0 real roots (disc
		 * < 0) mt=3 : 2 real roots (disc > 0)
		 */

		if (0.0 == disc)
			u[2] = u[1];

		if (0 >= disc) {
			mt = 2;

			/*
			 * One would think that we could return 0 here and exit, since mt=2.
			 * However, this assignment is temporary and changes to mt=1 under
			 * certain conditions below.
			 */

			v[0] = Math.abs(u[0]);
			v[1] = Math.abs(u[1]);
			v[2] = Math.abs(u[2]);

			v1 = Math.max(Math.max(v[0], v[1]), v[2]);
			/* Work out which two roots have the largest moduli */
			k1 = 0;
			k2 = 0;
			if (v1 == v[0]) {
				k1 = 0;
				v2 = Math.max(v[1], v[2]);
			} else if (v1 == v[1]) {
				k1 = 1;
				v2 = Math.max(v[0], v[2]);
			} else {
				k1 = 2;
				v2 = Math.max(v[0], v[1]);
			}

			if (v2 == v[0])
				k2 = 0;
			else if (v2 == v[1])
				k2 = 1;
			else
				k2 = 2;

			if (0.0 <= u[k1]) {
				w1r = Math.sqrt(u[k1]);
				w1i = 0.0;
			} else {
				w1r = 0.0;
				w1i = Math.sqrt(-u[k1]);
			}
			if (0.0 <= u[k2]) {
				w2r = Math.sqrt(u[k2]);
				w2i = 0.0;
			} else {
				w2r = 0.0;
				w2i = Math.sqrt(-u[k2]);
			}
		} else {
			mt = 3;

			if ((0.0 == u[1]) && (0.0 == u[2]))
				arg = 0.0;
			else
				arg = Math.sqrt(Math.sqrt((u[1] * u[1]) + (u[2] * u[2])));
			final double theta = Math.atan2(u[2], u[1]);

			w1r = arg * Math.cos(theta / 2.0);
			w1i = arg * Math.sin(theta / 2.0);
			w2r = w1r;
			w2i = -w1i;
		}

		/* Solve the quadratic to obtain the roots to the quartic */
		w3r = ((qq / 8.0) * ((w1i * w2i) - (w1r * w2r)))
				/ ((w1i * w1i) + (w1r * w1r)) / ((w2i * w2i) + (w2r * w2r));
		h = a / 4.0;

		zarr[0] = (w1r + w2r + w3r) - h;
		zarr[1] = ((-w1r - w2r) + w3r) - h;
		zarr[2] = (-w1r + w2r) - w3r - h;
		zarr[3] = w1r - w2r - w3r - h;

		/* Arrange the roots into the variables z0, z1, z2, z3 */
		if (2 == mt) {
			if ((u[k1] >= 0) && (u[k2] >= 0)) {
				mt = 1;
				x1 = zarr[0];
				x2 = zarr[1];
				x3 = zarr[2];
				x4 = zarr[3];
				final double[] x = new double[4];
				x[0] = x1;
				x[1] = x2;
				x[2] = x3;
				x[3] = x4;
				return x;
			} else
				return null;
		} else {
			x1 = zarr[0];
			x2 = zarr[1];
			final double[] x = new double[2];
			x[0] = x1;
			x[1] = x2;
			return x;
		}

	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////

	BigFraction calpoly(TMono m, final BigFraction[] p) {
		if ((m == null) || (p == null))
			return BigFraction.ZERO;

		if (isConstant(m))
			return new BigFraction(m.val);
		BigFraction r = BigFraction.ZERO;

		while (m != null) {
			final BigFraction v = calpoly(m.coef, p);
			if (m.deg == 0)
				r = r.add(v);
			else {
				final int id = m.x - 1;
				if ((id < 0) || (id >= p.length) || (p[m.x - 1] == null))
					return BigFraction.ZERO;
				r = r.add((p[m.x - 1]).pow(m.deg).multiply(v));
			}
			m = m.next;
		}
		return r;
	}

	public int check_ndg(final TMono m, final param[] pm) // 0. TRUE 1. FALSE
	// 2.CAN NOT Verify,
	// should be checked
	// by floating point
	// calculation.
	{
		if (m == null)
			return 1;

		final int x = m.x;
		int n = 0;
		for (n = 0; n < pm.length; n++) {
			final param p = pm[n];
			if ((p == null) || (p.xindex >= x))
				break;
		}
		final BigFraction[] bp = new BigFraction[n + 1];

		int r = ndg_valid(m, pm, bp, 0);
		if (r == -1) {
			long k = 1;
			for (int i = 0; i <= n; i++)
				if (pm[i].m == null) {
					bp[i] = bp[i].add(BigInteger.valueOf((2 * k) + 1));
					r = ndg_valid(m, pm, bp, 0);
					if (r != -1)
						return r;
					k++;
				}
		}

		for (final BigFraction element : bp)
			if (element != null)
				System.out.println(element);
		return -1;
	}

	public int ndg_valid(final TMono m, final param[] pm,
			final BigFraction[] bp, final int n) {
		for (int i = n; (i < bp.length) && (pm[i] != null); i++) {
			final param p = pm[i];
			if (p.m == null) {
				if (bp[i] == null)
					bp[i] = new BigFraction((long) p.value);
			} else {
				final BigFraction[] bb = calcu_pm(p.m, bp, p);
				if (bb == null) // no solution.
					return -1;
				else if (bb.length == 0)
					return 1; // not equal..
				else if (bb.length == 1)
					bp[i] = bb[0];
				else
					for (final BigFraction element : bb) {
						bp[i] = element;
						final int b1 = ndg_valid(m, pm, bp, i + 1);
						if (b1 != 1)
							return b1;
					}
			}
		}
		final boolean r = calpoly(m, bp).compareTo(BigInteger.ZERO) == 0;
		if (r)
			return 0;
		else
			return 1;
	}

	public BigFraction[] calcu_pm(TMono m, final BigFraction[] bp,
			final param pm) { // return null if m.coef == 0
		if (m.deg == 1) {
			final BigFraction a = calpoly(m.coef, bp);
			if (a.isZero())
				return null;

			final BigFraction b = calpoly(m.next, bp);
			final BigFraction[] bb = new BigFraction[1];
			bb[0] = b.divide(a).negate();
			return bb;
		} else if (m.deg == 2) {
			BigFraction a, b, c;
			a = calpoly(m.coef, bp);
			if (a.isZero())
				return null;
			m = m.next;
			if (m.deg == 1) {
				b = calpoly(m.coef, bp);
				m = m.next;
			} else
				b = BigFraction.ZERO;
			c = calpoly(m.coef, bp);
			BigFraction dl = b.multiply(b).subtract(a.multiply(4).multiply(c));
			dl = dl.sqrt();
			if (dl != null) {
				final BigFraction[] bb = new BigFraction[2];
				bb[0] = (b.negate().add(dl).divide(a.multiply(2)));
				bb[1] = (b.negate().subtract(dl).divide(a.multiply(2)));
				return bb;
			} else
				return null;
		} else if (m.deg == 3) {
			BigFraction a;
			a = BigFraction.ZERO;
			while (m != null) {
				final BigFraction f = calpoly(m.coef, bp);
				switch (m.x) {
				case 3:
					a = f;
					break;
				case 2:
					// b = f;
					break;
				case 1:
					// c = f;
					break;
				case 0:
					// d = f;
					break;
				}
				m = m.next;
			}
			if (a.isZero())
				return null;
		}
		return new BigFraction[0];
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static boolean n2dv(TMono m) {
		if (plength(m) != 2)
			return false;
		m = m.next;
		if (m == null)
			return false;
		return isConstant(m.coef);
	}

	public void nn_reduce(TPoly poly) {
		while (poly != null) {
			TMono m = poly.poly;
			if (n2dv(m))
				while ((m != null) && (m.x != 0)) {
					nn_div1(m.x, poly.next);
					m = m.coef;
				}
			poly = poly.next;
		}
	}

	public void nn_div1(final int x, TPoly poly) {

		while (poly != null) {
			final TMono m = poly.poly;
			while (true) {
				final long n = factor_contain(x, 1, m);
				if (n > 0)
					div_factor1(m, x, (int) n);
				else
					break;
			}
			poly = poly.next;
		}
	}

	// public TMono g_prem(TMono p1, TMono p2) {
	// if (p1 == null)
	// return p1;
	// if (p2 == null)
	// return p1;
	// if (p1.x < p2.x)
	// return p1;
	//
	// TMono result = null;
	// if (p1.x == p2.x)
	// result = prem1(p1, p2);
	// else
	// result = prem3(p1, p2);
	//
	// coefgcd(result);
	//
	// return result;
	// return null;
	// }

	public static ArrayList<TMono> bb_reduce(final ArrayList<TMono> vlist, final long t) {
		bb_reduce(vlist, t, false);
		// bb_reduce(vlist, t, false);
		// bb_reduce(vlist, t, true);

		return vlist;
	}

	public static ArrayList<TMono> bb_reduce(final ArrayList<TMono> vlist,
			final long t, final boolean s) {

		while (true) {
			boolean r = true;
			int size = vlist.size();

			for (int i = size - 2; i >= 0; i--) {
				boolean modified = false;
				TMono m2 = vlist.get(i);
				for (int j = i + 1; j < vlist.size(); j++) {
					TMono m1 = vlist.get(j);
					if (s && (plength(m1.coef) != 1))
						continue;

					if (BB_STOP)
						return vlist;
					final TMono m = bb_divnh(m2, m1);
					if (m != null) {
						modified = true;
						m1 = p_copy(m1);
						final BigInteger b1 = getLN(m1);
						m2 = pdif(cp_times(b1, m2), pp_times(m, m1));
						coefgcd(m2);
						r = false;
						if (m2 == null)
							break;
						if (isConstant(m2))
							return vlist;
					}
				}

				if (modified) {
					vlist.remove(i);
					if (m2 != null) {
						coefgcd(m2);
						ppush(m2, vlist);
					}
					size = vlist.size();
					// i = size -2;
				}
			}
			if (r)
				break;
		}
		return vlist;
	}

	public void divm(final TMono m1, TMono m) {
		if ((m1 == null) || (m == null) || (m1.x <= m.x))
			return;

		while ((m != null) && (m.x != 0)) {
			while (true) {
				final long n = factor_contain(m.x, 1, m1);
				if (n > 0)
					div_factor1(m1, m.x, (int) n);
				else
					break;
			}
			m = m.coef;
		}
	}

	public int get_n_paraent(TMono m) {
		if (m == null)
			return 0;
		final int n = 0;
		while (m != null)
			m = m.coef;
		return n;
	}

	public TMono sp_reduce(TMono m1, final TMono m2) { // m1.x == m2.x

		while (true) {

			if (m1 == null)
				return m1;
			if (m2 == null)
				return m1;
			if (m1.x != m2.x)
				break;
			if ((m2.coef == null) || (m2.coef.coef != null))
				break;
			if (m2.deg != 1)
				break;

			// basic.print(m1);

			// BigInteger b1 = getLN(m1);
			// BigInteger b2 = getLN(m2);

			final int n = m1.deg;

			BigInteger bc1 = BigInteger.ONE;
			final BigInteger coefm2 = m2.coef.val;
			TMono e = p_copy(m2.next.coef);
			e = pctimes(e, -1);

			TMono cpm2 = pth(0, 1, 1);
			for (int i = 0; i < n; i++) {
				bc1 = bc1.multiply(coefm2);
				cpm2 = ptimes(cpm2, p_copy(e));
			}
			m1 = padd(ptimes(m1.coef, cpm2), pctimes(m1.next, bc1.intValue()));
			// basic.print(m1);
		}
		coefgcd(m1);
		return m1;
	}

	public static TMono b_reduce(TMono m1, final ArrayList<TMono> vlist) {
		if (m1 != null) {
			boolean bHasPerformedAReduction;
			do {
				bHasPerformedAReduction = false;
				for (final TMono m2 : vlist) {
					if (m1 != m2) { // (m1.coef == null || m1.coef.coef != null)
						// m1 = sp_reduce(m1, m2);

						TMono m = bb_divn(m1, m2);
						while (m != null) {
							final BigInteger b2 = getLN(m2);
							if (BB_STOP)
								return null;

							m1 = pdif(cp_times(b2, m1), pp_times(m, p_copy(m2)));

							if (m1 == null)
								return null;
							
							bHasPerformedAReduction = true;
							m = bb_divn(m1, m2);
						}
					}
				}
			} while (bHasPerformedAReduction);

			coefgcd(m1);
		}
		return m1;
	}

	/*
	 * private void ppmove(TPoly pp) { if (pp == null) return; TMono m =
	 * pp.poly; TPoly tp = pp.next; while (tp != null) { TMono mx = tp.poly; if
	 * (pp_compare(m, mx) < 0) { tp.poly = m; pp.poly = mx; pp = tp; } else
	 * break;
	 * 
	 * tp = pp.next; } }
	 */

	/*
	 * private TPoly shink_poly(TPoly poly) { if (poly == null) return null;
	 * TPoly tp = poly; while (tp.next != null) { if (tp.next.poly == null)
	 * tp.next = tp.next.next; else tp = tp.next; } if (poly.poly != null)
	 * return poly; return poly.next; }
	 */

	/*
	 * private static int compare(TMono m1, TMono m2) { if (m1 == null && m2 ==
	 * null) return 0; if (m1 == null && m2 != null) return -1; if (m1 != null
	 * && m2 == null) return 1;
	 * 
	 * if (m1.x == 0 && m2.x == 0) return 0; if (m1.x == 0 && m2.x != 0) return
	 * -1; if (m1.x != 0 && m2.x == 0) return 1;
	 * 
	 * if (m1.x > m2.x || m1.x == m2.x && m1.deg > m2.deg) return 1; if (m1.x ==
	 * m2.x && m1.deg == m2.deg) return 0;
	 * 
	 * return -1; }
	 */

	/*
	 * private TMono m_head(TMono m) { if (m == null) return null; while
	 * (!Int(m)) m = m.coef; return pth(0, m.val, 0); }
	 */

	// private TMono bb_divnh(TMono m1, TMono m2) {
	// if (m1 == null || m2 == null) return null;
	// int n = compare(m1, m2);
	//
	// if (n < 0) return null;
	//
	// if (Int(m1) && Int(m2))
	// return new TMono(0, m1.val, 0);
	//
	// if (n == 0) {
	// TMono mx = bb_divn(m1.coef, m2.coef);
	// if (mx == null)
	// mx = this.m_head(m1);
	// return mx;
	// }
	//
	// if (n > 0) {
	// if (m1.x == m2.x) {
	// TMono mx = bb_divn(m1.coef, m2.coef);
	// if (mx == null)
	// mx = this.m_head(m1);
	// int dd = m1.deg - m2.deg;
	// if (dd > 0)
	// mx = pp_times(pth(m1.x, 1, dd), mx);
	//
	// return mx;
	// } else {
	// TMono mx = bb_divn(m1.coef, m2);
	// if (mx != null) {
	// int dd = m1.deg;
	// if (dd > 0)
	// mx = pp_times(pth(m1.x, 1, dd), mx);
	// }
	// return mx;
	//
	// }
	//
	// }
	// return null;
	// }

	private static TMono bb_divnh(final TMono m1, final TMono m2) {
		if ((m1 == null) || (m2 == null))
			return null;

		if ((m1.x < m2.x) || ((m1.x == m2.x) && (m1.deg < m2.deg)))
			return null;

		if (isConstant(m1) && isConstant(m2))
			return pth(0, m1.val, 0);

		TMono mx = null;
		if (m1.x == m2.x)
			if (m1.deg == m2.deg)
				return bb_divn(m1.coef, m2.coef);
			else {
				mx = bb_divn(m1.coef, m2.coef);
				final int dd = m1.deg - m2.deg;
				if (dd == 0)
					return mx;
				else if (dd > 0)
					return pp_times(pth(m1.x, 1, dd), mx);
			}

		mx = bb_divn(m1.coef, m2);
		if (mx == null)
			return null;

		return pp_times(pth(m1.x, 1, m1.deg), mx);
	}

	private static TMono bb_divn(TMono m1, final TMono m2) { // get a term of m1 which
		// diviid leading
		// variable of m2.
		if ((m1 == null) || (m2 == null))
			return null;

		if ((m1.x < m2.x) || ((m1.x == m2.x) && (m1.deg < m2.deg)))
			return null;

		if (isConstant(m1) && isConstant(m2))
			return pth(0, m1.val, 0);

		TMono mx = null;

		if (m1.x == m2.x) {
			if (m1.deg == m2.deg)
				return bb_divn(m1.coef, m2.coef);
			else
				while ((m1 != null) && (m1.deg >= m2.deg)) {
					mx = bb_divn(m1.coef, m2.coef);
					final int dd = m1.deg - m2.deg;
					if (mx != null)
						if (dd == 0)
							return mx;
						else if (dd > 0)
							return pp_times(pth(m1.x, 1, dd), mx);

					m1 = m1.next;
				}
			return null;
		} else if (m1.x > m2.x) {
			while (m1 != null) { // m1.x > m2.x

				mx = bb_divn(m1.coef, m2);
				if (mx != null)
					break;
				m1 = m1.next;
			}

			if (m1 == null)
				return null;
			if (mx == null)
				return null;
			if (m1.deg == 0)
				return mx;
			return pp_times(pth(m1.x, 1, m1.deg), mx);
		}

		return null;
	}

	public static void printVpoly(final ArrayList<TMono> v) {
		for (TMono m : v)
			print(m);
		System.out.println("\n");
	}

	public static void g_basis(ArrayList<TMono> v) {
		ArrayList<TMono> tp = null;
		do {
			bb_reduce(v, System.currentTimeMillis());

			if (gb_finished(v))
				break;

			// this.printVpoly(v);
			tp = s_polys(v);
			for (TMono tpe : tp) {
				ppush(tpe, v);
				printpoly(tpe);
			}

			if (tp.size() == 0)
				break;
		} while (!tp.isEmpty());
	}

	public static ArrayList<TMono> s_polys(final ArrayList<TMono> listInput) {

		final ArrayList<TMono> v = new ArrayList<TMono>();
		for (int i = 0; i < listInput.size(); i++) {
			final TMono m1 = listInput.get(i);
			for (int j = i + 1; j < listInput.size(); j++) {
				final TMono m2 = listInput.get(j);

				TMono mx = s_poly1(m1, m2);
				mx = b_reduce(mx, listInput);
				coefgcd(mx);
				if (mx != null)
					ppush(mx, v);
			}
		}
		return v;
	}

	/*
	 * private TMono s_poly(TMono m1, TMono m2) { if (m1.x == m2.x && m1.deg >=
	 * m2.deg) {
	 * 
	 * } else if (m_contain(m2.x, m2.deg, m1.coef)) {
	 * 
	 * } else return null;
	 * 
	 * TMono result; m1 = p_copy(m1); m2 = p_copy(m2); if (m1.x == m2.x) {
	 * result = prem1(m1, m2); } else result = prem3(m1, m2); return result; }
	 */

	private static TMono s_poly1(final TMono m1, final TMono m2) {
		return prem4(m1, m2);
	}

	private static TMono prem4(final TMono m1, final TMono m2) {
		if ((m1 == null) || (m2 == null))
			return null;
		if (m1.x < m2.x)
			return m1;

		// if (m1.x > m2.x)
		{
			final TMono mm = gcd_h(m1, m2);
			if ((mm == null) || (mm.x == 0))
				return null;

			if (mm.x != 0) {
				final TMono t1 = div_gcd(m1, mm);
				final TMono t2 = div_gcd(m2, mm);
				final TMono m = pdif(ptimes(t2, p_copy(m1)),
						ptimes(t1, p_copy(m2)));
				return m;
			}
		}
		// else {
		// return prem1(m1, m2);
		// }
		return null;
	}

	private static TMono gcd_h(TMono m1, TMono m2) { // gcd of m1, m2. (HEAD);
		if ((m1 == null) || (m2 == null))
			return null;
		TMono mx = null;

		while ((m1 != null) && (m2 != null) && (m1.x != 0) && (m2.x != 0))
			if (m1.x == m2.x) {
				TMono m;
				if (m1.x > 0) {
					int dd = m1.deg;
					if (dd > m2.deg)
						dd = m2.deg;
					m = pth(m1.x, 1, dd);
					if (mx == null)
						mx = m;
					else
						mx = ptimes(mx, m);
				}

				m1 = m1.coef;
				m2 = m2.coef;
			} else if (m1.x > m2.x)
				m1 = m1.coef;
			else if (m1.x < m2.x)
				m2 = m2.coef;
		return mx;
	}

	private static TMono div_gcd(TMono m1, TMono m) {
		TMono mx = pth(0, 1, 0);

		while (m1 != null)
			if (m1.x > m.x) {
				mx = ptimes(mx, pth(m1.x, 1, m1.deg));
				m1 = m1.coef;
			} else if (m1.x == m.x) {
				if (m1.x == 0)
					mx = ptimes(mx, pth(0, m1.val, 0));
				else {
					final int dd = m1.deg - m.deg;
					if (dd > 0)
						mx = ptimes(mx, pth(m1.x, 1, dd));
				}
				m1 = m1.coef;
				m = m.coef;
			}
		return mx;
	}

	// private TMono[] mgcd(TMono m1, TMono m2) {
	// if (m1 == null || m2 == null) return null;
	// TMono[] mm = new TMono[2];
	// bb_div2n(m1, m2, mm);
	// return mm;
	// }

	/*
	 * private void bb_div2n(TMono m1, TMono m2, TMono[] mm) { if (m1 == null ||
	 * m2 == null) return;
	 * 
	 * while (m1 != null) { if (m1.x > m2.x) { bb_div2n(m1.coef, m2, mm); if
	 * (mm[0] != null) { mm[1] = ptimes(pth(m1.x, 1, m1.deg), mm[1]); return; }
	 * } else if (m1.x == m2.x && m1.deg >= m2.deg) { mm[0] = pth(0, 1, 0);
	 * mm[1] = pth(0, 1, 0); get_mm(m1, m2, mm); return; } else break;
	 * 
	 * m1 = m1.next; if (m1 != null && m1.deg == 0) m1 = m1.coef; } }
	 */

	private static void get_mm(TMono m1, TMono m2, final TMono[] mm) {
		if ((m1 == null) || (m2 == null))
			return;

		if (m1.x > m2.x) {
			mm[1] = ptimes(mm[1], pth(m1.x, 1, m1.deg));
			m1 = m1.coef;
		} else if (m1.x < m2.x) {
			mm[0] = ptimes(mm[0], pth(m2.x, 1, m2.deg));
			m2 = m2.coef;
		} else if (m1.x == 0) {
			mm[1] = PolyBasic.cp_times(m1.val, mm[1]);
			mm[0] = PolyBasic.cp_times(m2.val, mm[0]);
			return;
		} else {
			if (m1.deg > m2.deg)
				mm[1] = ptimes(mm[1], pth(m1.x, 1, m1.deg - m2.deg));
			else if (m1.deg < m2.deg)
				mm[0] = ptimes(mm[0], pth(m1.x, 1, m2.deg - m1.deg));

			m1 = m1.coef;
			m2 = m2.coef;
		}
		get_mm(m1, m2, mm);
	}

	private static BigInteger getLN(TMono m) {
		if (m == null)
			return null;
		while (!isConstant(m))
			m = m.coef;
		return m.val;
	}

	// public void n_reduce(Vector v1, Vector nlist) {
	// for (int i = 0; i < v1.size(); i++) {
	// TMono m1 = (TMono) v1.get(i);
	// boolean r = false;
	// while (true) {
	// boolean b = true;
	// for (int j = 0; j < nlist.size(); j++) {
	// TMono m2 = (TMono) nlist.get(j);
	// TMono mm = gcd_h(m1, m2);
	// if (mm != null && mm.x != 0) {
	// TMono t1 = div_gcd(m1, mm);
	// TMono t2 = div_gcd(m2, mm);
	// print(m1);
	// print(m2);
	// m1 = pdif(ptimes(t2, p_copy(m1)), ptimes(t1, p_copy(m2)));
	//
	// if (CharSet.debug()) {
	// print(m1);
	// System.out.println("\n");
	// }
	//
	// r = true;
	// b = false;
	// }
	// }
	// if (b) break;
	// }
	// if (r) {
	// v1.remove(i);
	// if (m1 == null)
	// i--;
	// else
	// v1.add(i, m1);
	// }
	// }
	// }

	public static TMono ll_gbasis(final TMono m1, final TMono md, final int x,
			final int para) {
		if (m1 == null)
			return null;

		if (m1.deg == 1) {
			final TMono m11 = getxm1(x, 1, m1); // u1
			final TMono m12 = getxm1(x - 1, 1, m1); // u2
			final TMono c11 = pp_times(pth(para, 1, 1), p_copy(m11)); // zu1,
			// TMono c12 = pp_times(pth(para, 1, 1), p_copy(m12)); // zu2.

			final TMono c2 = pth(x, 1, 1); // x1
			final TMono t11 = ptimes(c11, p_copy(m1)); // zu1*x1
			// TMono t12 = ptimes(c12, p_copy(m12)); // zu2^2

			TMono t2 = pp_times(c2, p_copy(md)); // x1*md.

			t2 = pdif(t2, t11);
			t2 = pp_times(p_copy(m11), t2);
			final TMono t13 = pp_times(p_copy(m12),
					pp_times(pth(para, 1, 1), p_copy(m12)));

			return pdif(t2, pp_times(t13, p_copy(m1)));
		}
		return null;
	}

	public static TMono ll_delta(final int x, final TMono m1, final TMono m2) {
		if (m1 == null)
			return null;
		if (m1.deg == 1) {
			final TMono m11 = getxm1(x, 1, m1);
			final TMono m12 = getxm1(x - 1, 1, m1);

			if (m2 == null)
				return pp_minus(pp_times(p_copy(m11), p_copy(m11)),
						pp_times(p_copy(m12), p_copy(m12)));
			final TMono m21 = getxm1(x, 1, m2);
			final TMono m22 = getxm1(x - 1, 1, m2);

			TMono mx = pp_minus(pp_times(p_copy(m11), p_copy(m22)),
					pp_times(p_copy(m12), p_copy(m21)));

			if (mx == null)
				return mx;
			if (getLN(mx).intValue() < 0)
				mx = cp_times(-1, mx);
			return mx;
		} else if (m1.deg == 2) {
			if (m2 == null)
				return null;
			if (m2.deg == 1) {

			} else if (m2.deg == 2) {
				final TMono m11 = getxm1(x, 1, m1);
				final TMono m12 = getxm1(x - 1, 1, m1);

				final TMono m21 = getxm1(x, 1, m2);
				final TMono m22 = getxm1(x - 1, 1, m2);

				final TMono x1 = pp_minus(p_copy(m11), p_copy(m21));
				final TMono x2 = pp_minus(p_copy(m12), p_copy(m22));
				final TMono mx = padd(pp_times(x1, p_copy(x1)),
						pp_times(x2, p_copy(x2)));
				return mx;
			}
		}
		return null;
	}

	public static TMono getxm1(final int x, TMono m) {
		if (m == null)
			return null;

		if (m.x == x)
			return m.coef;

		while (m.next != null)
			m = m.next;
		if (m.deg != 0)
			return null;
		m = m.coef;
		if ((m.deg == 1) && (m.x == x))
			return m.coef;
		return null;
	}

	public static TMono getxm1(final int x, final int d, TMono m) {
		if (m == null)
			return null;
		while (m != null) {
			if ((m.x < x) || ((m.x == x) && (m.deg < d)))
				return null;

			if ((m.x == x) && (m.deg == d))
				return m.coef;

			if (m.next == null) {
				if (m.deg != 0)
					return null;
				else
					m = m.coef;
			} else
				m = m.next;
		}
		return null;
	}

	public static void upValueTM(final ArrayList<TMono> v, final int dx) {
		if (dx == 0)
			return;

		for (int i = 0; i < v.size(); i++)
			upValueTM(v.get(i), dx);
	}

	public static void upValueDM(final ArrayList<TDono> v, final int dx) {
		for (int i = 0; i < v.size(); i++) {
			final TDono d = v.get(i);

			upValueTM(d.p1, dx);
			upValueTM(d.p2, dx);
		}
	}

	public int getMaxX(final ArrayList<TMono> v) {
		int x = 0;

		for (int i = 0; i < v.size(); i++) {
			final TMono m = v.get(i);
			if (x < m.x)
				x = m.x;
		}
		return x;
	}

	public static void upValueTM(TMono m, final int dx) {
		if (dx == 0)
			return;

		if (m == null)
			return;
		if (m.x == 0)
			return;

		while (m != null) {
			if (m.x != 0)
				m.x += dx;
			// if (m.x == 0) {
			// int n = 0;
			// }

			upValueTM(m.coef, dx);
			m = m.next;
		}
	}

	public static boolean gb_finished(final ArrayList<TMono> v) {
		for (int i = 0; i < v.size(); i++) {
			final TMono m = v.get(i);
			if ((plength(m) == 1) && (m.x == 0) && (m.value() != 0))
				return true;
		}
		return false;
	}

	public static void ndg_reduce(final ArrayList<TMono> v) {
		for (int i = 0; i < v.size(); i++) {
			final TMono m = v.get(i);
			if (m.deg == 0) {
				v.remove(m);
				i--;
			}
		}
	}

	public static ArrayList<TMono> getcnds(final ArrayList<TMono> v, final int dx) {
		final ArrayList<TMono> v1 = new ArrayList<TMono>();
		for (int i = 0; i < v.size(); i++) {
			final TMono m = v.get(i);
			if (ctLessdx1(m, dx)) {
				v1.add(p_copy(m));
				v.remove(m);
				i--;
			}
		}
		return v1;
	}

	public static ArrayList<TMono> specialTreatment(final TMono m1, final TMono m2,
			final int dd) {
		final ArrayList<TMono> v = new ArrayList<TMono>();
		if (m1 == null)
			return v;
		final int x = m1.x;

		if (m1.deg == 1) {
			final TMono m11 = getxm1(x, 1, m1);
			final TMono m12 = getxm1(x - 1, 1, m1);
			if (m2 == null)
				return v;
			final TMono m21 = getxm1(x, 1, m2);
			final TMono m22 = getxm1(x - 1, 1, m2);

			TMono dmm = pdif(ptimes(p_copy(m1), p_copy(m21)),
					ptimes(p_copy(m11), p_copy(m2)));
			dmm = ptimes(pth(dd, 1, 1), dmm);

			TMono dmm1 = pdif(ptimes(p_copy(m1), p_copy(m22)),
					ptimes(p_copy(m12), p_copy(m2)));
			dmm1 = ptimes(pth(dd, 1, 1), dmm1);
			v.add(dmm);
			v.add(dmm1);
		}
		return v;
	}

	public ArrayList<TMono> updateTMM(final ArrayList<TMono> v, final int s,
			final int e, final int dx, final boolean up) {
		final ArrayList<TMono> v3 = new ArrayList<TMono>();

		for (final TMono m : v) {
			final TMono m2 = updateTMM(m, s, e, dx, up);
			ppush(m2, v3);
		}
		return v3;
	}

	private TMono updateTMM(TMono m, final int s, final int e, final int dx,
			final boolean up) {

		TMono mx = null;

		if (up)
			while (m != null) {
				if ((m.x < s) && (m.x != 0)) {
					TMono m1 = updateTMM(m.coef, s, e, dx, up);
					m1 = ptimes(m1, pth(dx + m.x, 1, m.deg));
					mx = padd(mx, m1);
				} else if (m.x == 0) {
					final TMono m1 = pth(0, m.val, m.deg);
					mx = padd(mx, m1);
				} else {
					TMono m1 = updateTMM(m.coef, s, e, dx, up);
					if (m.deg != 0)
						m1 = ptimes(m1, pth(m.x, 1, m.deg));
					mx = padd(mx, m1);
				}
				m = m.next;
			}
		else
			while (m != null) {
				if (m.x > e) {
					TMono m1 = updateTMM(m.coef, s, e, dx, up);
					m1 = ptimes(m1, pth(m.x - dx, 1, m.deg));
					mx = padd(mx, m1);
				} else if (m.x == 0) {
					final TMono m1 = pth(0, m.val, m.deg);
					mx = padd(mx, m1);
				} else {
					TMono m1 = updateTMM(m.coef, s, e, dx, up);
					if (m.deg != 0)
						m1 = ptimes(m1, pth(m.x, 1, m.deg));
					mx = padd(mx, m1);
				}
				m = m.next;
			}
		return mx;
	}

	// ////////////////////////////////////////////////////////////////
	// TDono;

	public ArrayList<TMono> parseCommonDono(final ArrayList<TDono> v,
			final int dx) {
		final ArrayList<TMono> v1 = new ArrayList<TMono>();
		for (int i = 0; i < v.size(); i++) {
			final TDono d = v.get(i);
			for (int j = i + 1; j < v.size(); j++) {
				final TDono d1 = v.get(j);
				final TMono p1 = d.p2;
				final TMono p2 = d1.p2;
				final BigInteger b1 = getLN(p1);
				final BigInteger b2 = getLN(p2);

				if (ck_eq(p1, p2)) {
					final TMono m1 = pp_times(p_copy(d.p1), p_copy(d1.c));
					final TMono m2 = pp_times(p_copy(d.c), p_copy(d1.p1));
					final TMono m = pdif(cp_times(b1, m1), cp_times(b2, m2));
					v1.add(m);
				}
			}
		}
		return v1;
	}

	public void eraseCommonDono(final ArrayList<TDono> v) {
		for (int i = 0; i < v.size(); i++) {
			final TDono d = v.get(i);
			for (int j = i + 1; j < v.size(); j++) {
				final TDono d1 = v.get(j);
				final TMono p1 = d.p2;
				final TMono p2 = d1.p2;
				if (ck_eq(p1, p2)) {
					v.remove(j);
					j--;
				}
			}
		}
	}

	public boolean ck_eq(TMono m1, TMono m2) {
		while ((m1 != null) && (m2 != null)) {
			if ((m1.x != m2.x) || (m1.deg != m2.deg))
				return false;
			if (!ck_eq(m1.coef, m2.coef))
				return false;
			m1 = m1.next;
			m2 = m2.next;
		}

		return m1 == m2; // null
	}

	public ArrayList<TDono> parseDono(final ArrayList<TDono> v, final int dx) {
		final ArrayList<TDono> v1 = new ArrayList<TDono>();
		final ArrayList<TDono> v2 = new ArrayList<TDono>();

		int ldx = 0;
		for (final TDono d : v) {
			final TMono m = d.p2;

			ldx = getLdx(m, dx);
			if (ldx == 0)
				v1.add(d);
			else
				v2.add(d);
		}

		if (!v2.isEmpty()) {
			boolean r = true;
			while (r) {
				r = false;

				for (final TDono d : v2) {
					reduceDono(d, v1, dx);
					if (getLdx(d.p2, dx) == 0) {
						v1.add(d);
						v2.remove(d);
						r = true;
					}
				}
			}
		}

		return v1;
	}

	private int getLdx(TMono m, final int dx) {
		while (m != null) {

			if ((m.x > 0) && (m.x < dx))
				return m.x;

			final int n = getLdx(m.coef, dx);
			if (n > 0)
				return n;
			m = m.next;
		}
		return 0;
	}

	public static boolean ctLessdx1(TMono m, final int dx) {
		while (m != null) {
			if ((m.x > 0) && (m.deg > 0) && (m.x < dx))
				return true;
			m = m.coef;
		}
		return false;
	}

	public boolean ctLessdx(TMono m, final int dx) {
		// int r = 0;

		while (m != null) {
			if ((m.x > 0) && (m.deg > 0) && (m.x < dx))
				return true;

			if (ctLessdx(m.coef, dx))
				return true;

			m = m.next;
		}

		return false;
	}

	private boolean ctLdx(TMono m, final int i) {
		while (m != null) {

			if ((m.x > 0) && (m.x == i))
				return true;

			final boolean n = ctLdx(m.coef, i);
			if (n)
				return n;
			m = m.next;
		}
		return false;
	}

	private int MinLdx(final TMono m, final int dx) {
		final int r = MinLdx(m);
		if (r >= dx)
			return -1;
		return r;
	}

	private int MinLdx(TMono m) {
		int r = Integer.MAX_VALUE;

		while (m != null) {
			if (m.x == 0)
				return Integer.MAX_VALUE;
			if (r > m.x)
				r = m.x;
			final int k = MinLdx(m.coef);
			if (k < r)
				r = k;
			m = m.next;
		}
		return r;

	}

	private int MaxLdx(TMono m, final int dx) {
		int r = 0;

		while (m != null) {
			if (m.x == 0)
				return -1;
			if (m.x < dx)
				if (r < m.x)
					r = m.x;

			final int k = MaxLdx(m.coef, dx);
			if ((k > 0) && (r < k))
				r = k;
			m = m.next;
		}
		return r;
	}

	private int ctMLdx(TMono m, final int i) { // MAX
		int r = 0;

		while (m != null) {
			if (m.x < i)
				break;

			if (m.x == i) {
				if (r < m.deg)
					r = m.deg;
				break;
			} else if (m.x > i) {
				final int k = ctMLdx(m.coef, i);
				if (r < k)
					r = k;
			}

			m = m.next;
		}
		return r;
	}

	public TMono reduceMDono(final TMono mm, final ArrayList<TDono> v,
			final int dx) {
		TMono m = mm;

		while (true) {
			final int max = MinLdx(m, dx);

			if (max <= 0)
				break;

			final TDono d1 = getDo(v, max);
			if (BB_STOP)
				return null;

			if (d1 != null) {

				final int rd = ctMLdx(m, max);

				final TMono m2 = padd(pp_times(p_copy(d1.p1), p_copy(d1.p2)),
						p_copy(d1.c));
				for (int k = 0; k < rd; k++)
					m = pp_times(m, p_copy(d1.p2));

				TMono dp = PolyBasic.p_copy(d1.p1);
				div_factor1(dp, max, 1);
				while ((dp != null) && (dp.x != 0) && (dp.deg == 0))
					dp = dp.coef;

				if ((dp != null) && (dp.x != 0) && (dp.deg != 0))
					for (int k = 0; k < rd; k++)
						m = pp_times(m, p_copy(dp));

				TMono mx = bb_divn(m, m2);

				while ((mx != null) && (mx.x != 0)) {
					final BigInteger b2 = getLN(m2);
					m = pdif(cp_times(b2, m), pp_times(mx, p_copy(m2)));
					mx = bb_divn(m, m2);
					if (BB_STOP)
						return null;
				}
				// this.print(m);
				coefgcd(m);
			}
		}
		return m;
	}

	public void reduceDono(final TDono d, final ArrayList<TDono> v, final int dx) {
		for (int i = 1; i < dx; i++) {
			if (!ctLdx(d.p2, i))
				continue;

			final int n = i;

			final TDono d1 = getDo(v, n);
			if (d1 != null) {
				TMono m = d.p2;
				final TMono m2 = padd(pp_times(p_copy(d1.p1), p_copy(d1.p2)),
						p_copy(d1.c));
				TMono mx = bb_divn(m, m2);
				if (mx == null) {
					m = pp_times(m, p_copy(d1.p2));
					mx = bb_divn(m, m2);
				}

				while ((mx != null) && (mx.x != 0)) {
					final BigInteger b2 = getLN(m2);
					m = pdif(cp_times(b2, m), pp_times(mx, p_copy(m2)));
					mx = bb_divn(m, m2);
				}
				d.p2 = m;
			}
		}
	}

	public static TDono getDo(final ArrayList<TDono> v, final int n) {
		final TDono xd = null;
		int nn = -1;

		for (final TDono d : v) {
			TMono m = d.p1;
			nn = -1;

			while (m != null) {
				if (m.x != 0)
					nn = m.x;
				else
					break;
				m = m.coef;
			}

			if (nn == n)
				return d;
		}

		return xd;
	}

	public void d_reduce(final TDono d1, final ArrayList<TMono> vlist) {
		if (d1 == null)
			return;

		TMono m1 = d1.p2;
		final BigInteger bb = BigInteger.ONE;

		while (true) {
			boolean r = true;
			for (int i = 0; i < vlist.size(); i++) {
				final TMono m2 = vlist.get(i);
				TMono m = bb_divnh(m1, m2);
				while (m != null) {
					final BigInteger b2 = getLN(m2);
					bb.multiply(b2);
					m1 = pdif(cp_times(b2, m1), pp_times(m, p_copy(m2)));
					if (m1 == null)
						break;
					r = false;
					m = bb_divn(m1, m2);
				}
			}
			if (r)
				break;
		}
		// d1.p1 = cp_times(bb, d1.p1);
		d1.c = cp_times(bb, d1.c);
		d1.p2 = m1;
	}

	/*
	 * public void splitDonos(ArrayList<TDono> vnn, ArrayList<TMono> vnds, int
	 * dx) { ArrayList<TMono> vtemp = new ArrayList<TMono>();
	 * 
	 * while (true) { while (!vnds.isEmpty()) { TMono tx = getMaxDMono(vnds,
	 * dx); vnds.remove(tx);
	 * 
	 * int max = MaxLdx(tx, dx); int min = MinLdx(tx, dx);
	 * 
	 * if (max != min) { tx = reduceMDono(tx, vnn, dx); max = MaxLdx(tx, dx); }
	 * 
	 * if (max == min) { TDono d = splitDono(tx, dx); if (d != null) vnn.add(d);
	 * } else { vtemp.add(tx); } } if (vtemp.size() == 0) break; else {
	 * vnds.addAll(vtemp); vtemp.clear(); } } }
	 */

	public TMono getMaxDMono(final ArrayList<TMono> vnds, final int dx) {
		int currentMax = 0;
		TMono tx = null;

		for (final TMono m : vnds) {
			final int x = MaxLdx(m, dx);
			if (x > currentMax) {
				currentMax = x;
				tx = m;
			}
		}
		return tx;
	}

	public TDono splitDono(final TMono m, final int dx) {
		TMono m1 = m;
		TMono c = null;

		while (m1 != null)
			if (m1.x == 0) {
				c = pth(0, m1.val, 0);
				break;
			} else if (m1.deg == 0)
				m1 = m1.coef;
			else
				m1 = m1.next;

		if (c == null)
			return null;

		TMono mx = pp_minus(p_copy(m), p_copy(c));

		// int minX = this.MinLdx(mx, dx);

		TMono mo = pth(0, 1, 0);
		TMono mf = get_factor1(mx);
		while ((mf != null) && (mf.x != 0)) {
			if (mf.x < dx) {
				mo = pp_times(mo, pth(mf.x, 1, mf.deg));
				div_factor1(mx, mf.x, mf.deg);
			}
			mf = mf.coef;
		}

		if ((mx != null) && (getLN(mx).intValue() < 0)) {
			mx = cp_times(-1, mx);
			c = cp_times(-1, c);
		}

		return new TDono(mo, mx, c);
	}

	/*
	 * public void reduceMdo(ArrayList<TMono> vrs, int dx) { for (int i = 0; i <
	 * vrs.size(); i++) { TMono m = vrs.get(i); int max = MaxLdx(m, dx); int min
	 * = MinLdx(m, dx); if (max == min) continue;
	 * 
	 * 
	 * }
	 * 
	 * }
	 */

	public TMono getMono(final TDono d) {
		TMono m = padd(pp_times(p_copy(d.p1), p_copy(d.p2)), p_copy(d.c));
		coefgcd(m);

		if ((m != null) && (getLN(m).intValue() < 0))
			m = cp_times(-1, m);

		return m;
	}
}
