package wprover;

import gprover.cndg;
import gprover.cons;
import gprover.gterm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import maths.PolyBasic;
import maths.TDono;
import maths.TMono;
import maths.TPoly;

public class PanelGB extends PanelAlgebraic implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6895306105732549044L;
	private ArrayList<cndg> vndgs;
	private boolean prs = false;
	//private static long TIME = 1000000;
	private JPopupMenu menu;

	public PanelGB(DrawPanel dp, TextPaneWuMethod tpane) {
		super(dp, tpane);
		menu = new JPopupMenu();
		JMenuItem it = new JMenuItem("Save as Maple Format");
		menu.add(it);
		it.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsMaple();
			}
		});
		tpane.addMouseListener(this);
	}


	public void stopRunning() {
		running = false;
		PolyBasic.setbbStop(true);
		this.addString("\n");
		this.addString("icon4", "icon4");
		this.addString("The Process Is Stopped By The User.");

	}


	public void prove(gterm tm, DrawPanel dp) {
		if (running)
			return;

		tpane.setText("");
		_mremainder = null;
		gt = tm;
		main = new Thread(this, "GbProver");
		running = true;
		main.start();
		startTimer();
	}

	public void startTimer() {
		if (gxInstance != null) {
			Timer t = new Timer(1000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (running) {
						rund = PopupMenuRunning.startTimer(gxInstance, "GBasis is Running");
						rund.setPanelGB(PanelGB.this);
					}
					Timer t = (Timer) e.getSource();
					t.stop();
				}
			});

			t.start();
		}

	}


	protected int div(TMono m1, TPoly p1) {
		if (PolyBasic.pzerop(m1))
			return 0;
		ArrayList<TMono> vt = new ArrayList<TMono>();

		while (p1 != null) {
			TMono t = p1.poly;
			vt.add(0, t);
			if (t.x == m1.x)
				break;
			p1 = p1.next;
		}

		//int index = vt.size();

		//long time = System.currentTimeMillis();
		int i = 0;
		while (true) {
			if (i >= vt.size())
				break;

			TMono m = vt.get(i++);
			TMono md = PolyBasic.pcopy(m);
			m1 = PolyBasic.prem(m1, md);
			//            if (m1 != null && m1.x == 9) {
			//                int k = 0;
			//            }
			//long t1 = System.currentTimeMillis();
			//            addDiv(--index, m1, m.x, t1 - time);
			//time = t1;
			if (PolyBasic.pzerop(m1))
				return 0;
			if (!running)
				return 1;
		}
		String s = PolyBasic.printSPoly(m1);
		addString("Remainder:  " + s);

		return 1;
	}

	protected TMono getTMono(cons c) {
		return dp.getTMono(c);
	}

	public static int addMM(TMono m, ArrayList<TMono> v, int param) {
		if (PolyBasic.plength(m) == 1) {
			while (m != null && m.deg > 0 && m.x > 0) {
				TMono m1 = GeoPoly.pth(m.x, 1, 1);
				m1 = GeoPoly.n_ndg(m1, param--);
				v.add(m1);
				m = m.coef;
			}
		} else {
			m = GeoPoly.n_ndg(m, param--);
			v.add(m);
		}

		return param;

	}

	//    private boolean is_ndg_set() {
		//        return gt != null && gt.getNcons().size() > 0;
		//    }

	public void getNDGS(ArrayList<TMono> v3) {
		int t = 3;
		int param = -1;

		if (t == 3) {
			gt.getNcons(vndgs);
		}


		if (t == 0 || t == 3) {     // from FULL
			for (cndg nd : vndgs) {
				TMono m = GeoPoly.mm_poly(nd, dp);
				addString1(nd.toString() + "\n");
				if (m != null) {
					param = addMM(m, v3, param);


				}
			}
		} else if (t == 1) { // from DB
			ArrayList<TMono> v = dp.getNDGS();
			for (TMono m : v) {
				if (m != null) {
					param = addMM(m, v3, param);
				}
			}
		}
	}


	@Override
	public void run() {

		if (gt == null) {
			running = false;
			if (rund != null)
				rund.stopTimer();
			return;
		}

		//        /if (is_ndg_set())
			//            this.gbasis1();
		//        else
		gbasis();

		PolyBasic.setbbStop(false);
		running = false;
		if (rund != null)
			rund.stopTimer();
	}

	public void gbasis1() {

		String sc = gt.getConcText();
		cons cc = gt.getConclusion();

		TMono mc = getTMono(cc);
		if (mc == null) {
			running = false;
			return;
		}


		addAlgebraicForm();
		addString2("The equational hypotheses:");

		ArrayList<Constraint> vc = new ArrayList<Constraint>();
		dp.getAllConstraints(vc);

		int n = 1;
		ArrayList<TMono> pp = new ArrayList<TMono>();


		for (Constraint c : vc) {
			if (c.bPolyGenerate) {
				c.PolyGenerate();
				TPoly p1 = Constraint.getPolyListAndSetNull();
				if (p1 != null)
					addString1(n++ + ": " + c.toString() + "\n");
				while (p1 != null) {
					TMono m = p1.getPoly();
					if (m != null) {
						PolyBasic.ppush(m, pp);
						addString("  " + PolyBasic.printSPoly(m));
					}
					p1 = p1.next;
				}
			}
		}


		addString2("Nondegenerate Conditions");
		ArrayList<TMono> v3 = new ArrayList<TMono>();
		getNDGS(v3);
		ArrayList<TMono> p = v3;
		printTP(p);


		for (int i = 0; i < p.size(); i++) {
			TMono m = p.get(i);
			PolyBasic.ppush(m, pp);
		}

		if (prs) {
			addString2("Poly set before gbasis");
			printTP(pp);
		}

		int dx = p.size() + 2;
		PolyBasic.upValueTM(pp, dx);

		int index = 1;
		long t = System.currentTimeMillis();

		//        boolean r1 = false;
		//        if (true) {
		//            sbasis();
		//            return;
		//        }

		while (true) {
			if (!prs) {
				addString2(index + ": Poly set before bb-reduce");
				printTP(pp);
			}
			//            r1 = true;


			pp = PolyBasic.bb_reduce(pp, t);
			//           if (System.currentTimeMillis() - t > 10000)
			//             break;

			if (!prs) {
				addString2(index++ + ": Poly set after bb-reduce");
				printTP(pp);
			}
			//            }
			if (gb_finished(pp))
				break;
			//       if (pp.size() > 30)
			//            break;

			ArrayList<TMono> tp = PolyBasic.s_polys(pp);
			if (tp.size() != 0) {
				tp = PolyBasic.bb_reduce(tp, t);
				if (!prs) {
					addString2("S - Polynomials");
					printTP(tp);
				}
				//                }
				for (int i = 0; i < tp.size(); i++)
					PolyBasic.ppush(tp.get(i), pp);
			} else {
				break;
			}
			if (!running)
				return;
		}


		String s1 = PolyBasic.printSPoly(mc);
		PolyBasic.upValueTM(mc, dx);
		mc = PolyBasic.b_reduce(mc, pp);
		PolyBasic.upValueTM(mc, -dx);
		PolyBasic.upValueTM(pp, -dx);
		String s2 = PolyBasic.printSPoly(mc);

		if (prs) {
			addString2("Poly set after gbasis");
			printTP(pp);
		}


		addString2("The conclusion: ");
		addString1(sc + "\n");

		addString(s1);
		addString2("The conclusion after reduce:");
		addString(s2);

		if (mc == null) {
			addString("icon1", "icon1");
			addString1("The conclusion is true");
		} else {
			addString("icon2", "icon2");
			addString1("The conclusion is false");
		}
		running = false;
	}

	public void printTP(ArrayList<TMono> v) {
		for (TMono m : v) {
			if (m != null)
				addString(PolyBasic.printSPoly(m));
		}
	}

	public boolean gb_finished(ArrayList<TMono> v) {
		for (TMono m : v) {
			if (PolyBasic.plength(m) == 1 && m.x == 0)
				return true;
		}
		return false;
	}

	public void test(ArrayList<TMono> pp, int dx) {
		int size = pp.size();


		if (size < 2) return;

		int index = size - 3;
		ArrayList<TMono> vp = new ArrayList<TMono>();
		for (int i = size - 2; i < size; i++)
			vp.add(pp.get(i));


		for (int i = index; i >= 0; i--) {
			addString2(i + "GBASIS");
			printTP(vp);

			vp.add(0, pp.get(i));
			gbasis(vp);
		}

		addString2(-1 + "GBASIS");
		PolyBasic.upValueTM(vp, -dx);
		printTP(vp);


	}


	public void gbasis(ArrayList<TMono> pp) {
		//        long t = System.currentTimeMillis();

		while (true) {
			pp = PolyBasic.bb_reduce(pp, 10000);
			if (!isRunning())
				break;

			if (gb_finished(pp))
				break;

			ArrayList<TMono> tp = PolyBasic.s_polys(pp);

			if (!tp.isEmpty()) {
				for (TMono m : tp)
					PolyBasic.ppush(m, pp);
			} else {
				break;
			}
		}
	}

	public void dbasis(ArrayList<TMono> pp) {

		ArrayList<TMono> v = new ArrayList<TMono>();
		int size = pp.size();
		for (int i = 0; i < size - 1; i++) {
			TMono m1 = pp.get(i);
			for (int j = i + 1; j < size; j++) {
				TMono m2 = pp.get(j);
				v.clear();
				v.add(PolyBasic.p_copy(m1));
				v.add(PolyBasic.p_copy(m2));

				PolyBasic.printVpoly(v);
				while (true) {
					v = PolyBasic.bb_reduce(v, -1);

					if (gb_finished(v))
						break;


					ArrayList<TMono> tp = PolyBasic.s_polys(v);

					if (v.size() >= 1) {
						PolyBasic.printVpoly(v);
						PolyBasic.printVpoly(tp);
					}
					tp = PolyBasic.bb_reduce(tp, -1);


					if (!tp.isEmpty()) {
						for (int k = 0; k < tp.size(); k++)
							PolyBasic.ppush(tp.get(k), v);
					} else {
						break;
					}
				}
				PolyBasic.printVpoly(v);
			}
		}
	}


	public TMono sbasis(int x, ArrayList<TMono> v, TMono mc) {

		ArrayList<TMono> vg = new ArrayList<TMono>();
		if (v.isEmpty()) return mc;
		GeoPoly basic = GeoPoly.getPoly();

		int nn = x;
		int dx = nn / 2 + 2;
		TMono m1, m2;
		int param = 0;
		ArrayList<TMono> vrs = new ArrayList<TMono>();


		for (int n = 1; n < nn / 2 + 1; n++) {
			m1 = m2 = null;

			for (int i = 0; i < v.size(); i++) {
				TMono m = v.get(i);
				if (m.x == 2 * n || m.x == 2 * n - 1) {
					if (m1 == null)
						m1 = m;
					else m2 = m;
				}
			}

			if (m1 != null)
				v.remove(m1);
			if (m2 != null)
				v.remove(m2);

			if (m1 != null || m2 != null) {
				TMono t = PolyBasic.ll_delta(2 * n, m1, m2);
				if (PolyBasic.plength(t) == 1 && t.x == 0 && t.val.intValue() != 0)
					t = null;
				--param;
				int dd = -param + 3;
				t = GeoPoly.n_ndg(t, param);

				vg.clear();

				PolyBasic.ppush(m2, vg);
				PolyBasic.ppush(m1, vg);
				PolyBasic.ppush(t, vg);
				//                System.out.println(basic.getAllPrinted(m1));
				//                System.out.println(basic.getAllPrinted(m2));

				PolyBasic.upValueTM(vg, dd);
				//
				//                if(false)
					//                {
					//                    Vector vtp = basic.specialTreatment(m1, m2, param + dd);
					//                    if (vtp.size() != 0) {
				//                        vg.clear();
				//                        for (int i = 0; i < vtp.size(); i++)
				//                            basic.ppush((TMono) vtp.get(i), vg);
				//                        basic.ppush(t, vg);
				//                    }
				//                }

				this.gbasis(vg);
				PolyBasic.upValueTM(vg, -dd);
				for (int i = 0; i < vg.size(); i++) {
					TMono tt = vg.get(i);
					PolyBasic.ppush(tt, vrs);
				}
			}
		}

		PolyBasic.upValueTM(vrs, dx);

		ArrayList<TMono> vnds = PolyBasic.getcnds(vrs, dx);
		PolyBasic.bb_reduce(vrs, 10000, true);
		if (!running)
			return null;

		ArrayList<TDono> vnn = new ArrayList<TDono>();
		for (TMono m : vnds) {
			m = PolyBasic.b_reduce(PolyBasic.p_copy(m), vrs);
			if (!running)
				return null;
			TDono d = basic.splitDono(m, dx);

			if (d != null)
				vnn.add(d);
		}

		ArrayList<TMono> vco = basic.parseCommonDono(vnn, dx);
		for (TMono d : vnds) {
			PolyBasic.ppush(d, vrs);
		}
		for (TMono m : vco) {
			PolyBasic.ppush(m, vrs);
		}

		PolyBasic.bb_reduce(vrs, 10000, true);
		mc = PolyBasic.b_reduce(mc, vrs);

		while (basic.ctLessdx(mc, dx)) {
			mc = basic.reduceMDono(mc, vnn, dx); // reduced all u parameters.
			mc = PolyBasic.b_reduce(mc, vrs);
			if (!running)
				return null;
		}

		TMono mcr = PolyBasic.p_copy(mc);
		basic.eraseCommonDono(vnn);


		ArrayList<TMono> vnn1 = new ArrayList<TMono>();
		for (int i = 0; i < vnn.size(); i++) {
			TDono d = vnn.get(i);
			TMono m = PolyBasic.p_copy(d.p2);
			m = basic.reduceMDono(m, vnn, dx);
			if (!running)
				return null;
			m = PolyBasic.b_reduce(m, vrs);
			if (!running)
				return null;
			vnn1.add(m);
		}

		PolyBasic.upValueTM(vrs, -dx);
		PolyBasic.upValueDM(vnn, -dx);
		PolyBasic.upValueTM(mc, -dx);
		PolyBasic.upValueTM(mcr, -dx);
		PolyBasic.upValueTM(vnn1, -dx);

		printTP(vrs);
		//        this.printVectorExpanded(vrs, 0);

		addSVdd(vnn1);
		v.clear();
		v.addAll(vrs);
		return mc;
	}

	public void printVectorExpanded(ArrayList<TMono> vrs, int dx) {
		PolyBasic.upValueTM(vrs, -dx);
		for (int i = 0; i < vrs.size(); i++) {
			TMono ma = vrs.get(i);
			String st = PolyBasic.getExpandedPrint(ma);
			if (st.endsWith("*"))
				st = st.substring(0, st.length() - 1);
			else if (st.endsWith("-") || st.endsWith("+"))
				st += "1";
			System.out.println(st);
		}
		PolyBasic.upValueTM(vrs, dx);
	}

	public void addSVdd(ArrayList<TMono> v) {
		addString2(getLanguage(1116, "The Nondegenerate Conditions"));
		for (int i = 0; i < v.size(); i++) {
			//            TDono d = (TDono) v.get(i);
			TMono m = v.get(i);
			PolyBasic.coefgcd(m);
			TMono mf = PolyBasic.get_factor1(m);
			if (mf == null) {
				PolyBasic.factor1(m);
				String s = PolyBasic.printNPoly(m);
				this.addString(s);
			} else {
				TMono ff = mf;
				while (mf != null) {
					PolyBasic.div_factor1(m, mf.x, mf.deg);
					mf = mf.coef;
				}
				String s = PolyBasic.printNPoly(ff, m);
				this.addString(s);

			}
		}

	}

	public void printVDD(final ArrayList<TDono> v) {
		for (final TDono d : v) {
			PolyBasic.sprint(d.p1);
			System.out.print("*( ");
			PolyBasic.sprint(d.p2);
			System.out.print(" )");
			if (d.c.value() > 0)
				System.out.print(" + ");
			PolyBasic.sprint(d.c);
			System.out.print("\n");
		}
	}

	public void gbasis() {
		GeoPoly basic = GeoPoly.getPoly();
		String sc = gt.getConcText();
		cons cc = gt.getConclusion();
		TMono mc = getTMono(cc);
		if (mc == null) {
			running = false;
			return;
		}

		addAlgebraicForm();
		addString2(getLanguage(1103, "The Equational Hypotheses:"));

		ArrayList<Constraint> vc = new ArrayList<Constraint>();
		dp.getAllConstraints(vc);
		int n = 1;
		ArrayList<TMono> pp = new ArrayList<TMono>();


		for (int i = 0; i < vc.size(); i++) {
			Constraint c = vc.get(i);
			if (c.bPolyGenerate) {
				c.PolyGenerate();
				TPoly p1 = Constraint.getPolyListAndSetNull();
				if (p1 != null)
					addString1(n++ + ": " + c.toString() + "\n");
				while (p1 != null) {
					TMono m = p1.getPoly();
					if (m != null) {
						PolyBasic.ppush(m, pp);
						addString("  " + PolyBasic.printSPoly(m));
					}
					p1 = p1.next;
				}
			}
		}

		addString2(getLanguage(1114, "The Initial Polynomial Set"));
		printTP(pp);

		String s1 = PolyBasic.printSPoly(mc);

		addString2(getLanguage(1115, "The Groebner Basis: ") + "GB = ");
		//        addString2("GB = ");
		ArrayList<TMono> v = dp.getPBMono();

		int x = basic.getMaxX(v);
		int dx = x / 2 + 2;
		PolyBasic.upValueTM(mc, dx);

		mc = sbasis(x, v, mc);
		if (!running)
			return;
		pp = v;

		String s2 = PolyBasic.printSPoly(mc);
		addString2(getLanguage(1105, "The Conclusion: "));
		addString1(sc + "\n");
		addString(s1);
		addString2(getLanguage(1117, "The Conclusion After Reduce:"));
		addString(s2);

		if (mc == null) {
			addString("icon1", "icon1");
			addString1(getLanguage(1108, "The conclusion is true"));
		} else {
			addString("icon2", "icon2");
			addString1(getLanguage(1109, "The conclusion is false"));
			if (PolyBasic.plength(mc) > 2) {
				_mremainder = mc;
				addString("\n");
				addButton();
			}
		}

		running = false;
	}


	////////////////////////////////////////////////////////////
	public void mouseClicked(MouseEvent e) {
		menu.show((JComponent) e.getSource(), e.getX(), e.getY());
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void saveAsMaple() {
		JFileChooser filechooser1 = new JFileChooser();
		String dr = DrawPanelFrame.getUserDir();
		filechooser1.setCurrentDirectory(new File(dr));
		int result = filechooser1.showDialog(this, "Save");
		if (result == JFileChooser.APPROVE_OPTION) {
			File f = filechooser1.getSelectedFile();
			boolean bExists = f.exists();
			try {
				if (bExists) {
					f.delete();
				} else {
					f.createNewFile();
				}
				FileOutputStream fos = new FileOutputStream(f, bExists);
				writeMaple(fos);
				fos.close();
			} catch (IOException ee) {
				JOptionPane.showMessageDialog(this, ee.getMessage(),
						"Save Failed", JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	public void writeMaple(FileOutputStream out) throws IOException {
		GeoPoly basic = GeoPoly.getPoly();
		cons cc = gt.getConclusion();
		TMono mc = getTMono(cc);

		// Part 1: Order for the variables.
		boolean bFirstTimeFlag = true;
		out.write("vars := [".getBytes());

		ArrayList<GEPoint> vp = new ArrayList<GEPoint>();
		dp.getPointList(vp);
		for (int i = vp.size() - 1; i >= 0; i--) {
			GEPoint pt = vp.get(i);
			String s1 = pt.x1.getString();
			String s2 = pt.y1.getString();
			if (!GeoPoly.checkZ(pt.x1.xindex) && !GeoPoly.checkZ(pt.y1.xindex)) {
				if (!bFirstTimeFlag)
					out.write(", ".getBytes());
				bFirstTimeFlag = false;
				out.write((s2 + ", " + s1).getBytes());
			}
		}


		ArrayList<TMono> v = dp.getPBMono();
		int x = basic.getMaxX(v);

		ArrayList<TMono> vg = new ArrayList<TMono>();
		int nn = x;
		TMono m1, m2;
		int param = 0;

		for (int n = 1; n < nn / 2 + 1; n++) {
			m1 = m2 = null;

			for (int i = 0; i < v.size(); i++) {
				TMono m = v.get(i);
				if (m.x == 2 * n || m.x == 2 * n - 1) {
					if (m1 == null)
						m1 = m;
					else m2 = m;
				}
			}

			if (m1 != null)
				v.remove(m1);
			if (m2 != null)
				v.remove(m2);

			if (m1 != null || m2 != null) {
				TMono t = PolyBasic.ll_delta(2 * n, m1, m2);
				if (PolyBasic.plength(t) == 1 && t.x == 0 && t.val.intValue() != 0)
					t = null;
				--param;
				t = GeoPoly.n_ndg(t, param);
				if (t != null)
					out.write((", u" + (-param)).getBytes());

				PolyBasic.ppush(m2, vg);
				PolyBasic.ppush(m1, vg);
				PolyBasic.ppush(t, vg);
			}
		}
		out.write("];".getBytes());

		// Part 2: All polynomials.
		//        out.write(("\n" + vg.size()).getBytes());
		for (int i = 0; i < vg.size(); i++) {
			TMono m = vg.get(i);
			out.write("\n".getBytes());
			out.write(("P" + i + " := " + PolyBasic.getExpandedPrint(m) +" ;").getBytes());
		}
		String st = PolyBasic.getExpandedPrint(mc);
		out.write(("\n C := " + st +" ;").getBytes());

	}
}