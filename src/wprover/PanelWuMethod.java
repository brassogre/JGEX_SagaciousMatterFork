package wprover;

import gprover.cons;
import gprover.gterm;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import maths.*;

public class PanelWuMethod extends PanelAlgebraic implements Runnable, MouseListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5438422907431409645L;
	private TMono mremainder;


    public PanelWuMethod(DrawPanel dp, TextPaneWuMethod tpane) {
        super(dp, tpane);
        tpane.addMouseListener(this);
    }

    public void setLanguage(Language lan) {
        this.lan = lan;
    }

    public void stopRunning() {
        if (!running)
            return;
    }

    public void prove(gterm tm) {
        if (running)
            return;

        tpane.setText("");
        gt = tm;
        main = new Thread(this, "wuProver");
        running = true;
        main.start();
    }

    protected void addDiv(int index, TMono m1, int x, long t) {
        index++;
        String s = "R_" + (index - 1) + " = prem(R_" + index + ", h_" + (index - 1) + ") =  ["
                + PolyBasic.printHead(m1) + ", " + PolyBasic.plength(m1) + "]";
        addString(s);
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

        int index = vt.size();

        long time = System.currentTimeMillis();
        int i = 0;
        addString("R_" + index + " = [" + PolyBasic.printHead(m1) + ", " + PolyBasic.plength(m1) + "]");
        while (true) {
            if (i >= vt.size())
                break;

            TMono m = vt.get(i++);
            TMono md = PolyBasic.pcopy(m);
            m1 = PolyBasic.prem(m1, md);
//            if (m1 != null && m1.x == 9) {
//                int k = 0;
//            }
            long t1 = System.currentTimeMillis();
            index--;
            addDiv(index, m1, m.x, t1 - time);
            time = t1;
            if (PolyBasic.pzerop(m1)) {
                addString(getLanguage(1110, "Remainder") + " = R_" + (index) + " = 0");
                return 0;
            }
            if (!running)
                return 1;
        }
        String s = PolyBasic.printMaxstrPoly(m1);
        if (m1 != null)
            s += " != 0";

        addString(getLanguage(1110, "Remainder") + " = " + s);
        mremainder = m1;

        return 1;
    }

    public TMono getTMono(cons c) {
        TMono m = dp.getTMono(c);

        return m;
    }


    public void run() {
        if (gt == null) {
            running = false;
            return;
        }

        String sc = gt.getConcText();
        cons cc = gt.getConclusion();
        TMono mc = getTMono(cc);
        if (mc == null) {
            running = false;
            return;
        }

        addAlgebraicForm();
        addString2(getLanguage(1103, "The Equational Hypotheses:"));

        TPoly pp = null;
        ArrayList<Constraint> vc = new ArrayList<Constraint>();
	dp.getAllConstraints(vc);
        int n = 1;
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
                        addString("  " + PolyBasic.printSPoly(m));
                        pp = PolyBasic.ppush(m, pp);
                    }
                    p1 = p1.next;
                }
            }
        }


        addString2(getLanguage(1104, "The Triangulized Hypotheses") + " (TS):");
        TPoly p1 = dp.getPolyList();
        int i = 0;
        while (p1 != null) {
            addString("h" + i++ + ": " + PolyBasic.printSPoly(p1.poly));
            p1 = p1.next;
        }


        addString2(getLanguage(1105, "The Conclusion") + " (CONC): ");
        addString1(sc + "\n");
        addString(PolyBasic.printSPoly(mc));

        addString2(getLanguage(1106, "Successive Pseudo Remainder of CONC wrpt TS :"));
        int r = 0;

        try {
            r = div(mc, dp.getPolyList());
        } catch (final java.lang.OutOfMemoryError e) {
            running = false;
            JOptionPane.showMessageDialog(PanelWuMethod.this, getLanguage(1130, "System Run Out Of Memory") + "\n" +
                    getLanguage(1131, "The Theorem Is Not Proved!"), getLanguage(1130, "System Run Out of Memory"), JOptionPane.WARNING_MESSAGE);
            addString("\n" + getLanguage(1130, "System Run Out of Memory!"));
            addString("icon3", "icon3");
            addString1(getLanguage(1109, "The conclusion is not proved"));
            return;
        }
        scrollToEnd();

        if (r == 0) {
            addString("icon1", "icon1");
            addString1(getLanguage(1108, "The conclusion is true"));
        } else if (r == 1) {
            addString("icon2", "icon2");
            addString1(getLanguage(1107, "The conclusion is false"));
            if (PolyBasic.plength(mc) > 2) {
                _mremainder = mc;
                addString("\n");
                addButton();
            }
        }

        running = false;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {
            if (mremainder != null) {
                String s = PolyBasic.getExpandedPrint(mremainder);
                JDialog dlg = new JDialog((Frame) null, "Remainder");
                dlg.setSize(300, 200);
                JTextPane p = new JTextPane();
                p.setText(s);
                dlg.getContentPane().add(new JScrollPane(p));
                dlg.setLocation(200, 200);
                dlg.setVisible(true);
            }
        }
    }


    public void mousePressed(MouseEvent e) {
    }


    public void mouseReleased(MouseEvent e) {
    }


    public void mouseEntered(MouseEvent e) {
    }


    public void mouseExited(MouseEvent e) {
    }
}

