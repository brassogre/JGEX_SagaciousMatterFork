package wprover;

import gprover.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

import org.eclipse.jdt.annotation.NonNull;

import maths.param;

public class DrawPanelExtended extends DrawPanel {

    private Timer cons_timer;
    double PX, PY;

    public DrawPanelExtended(final DrawPanelFrame dpf) {
    	super(dpf);
    }
    
    public boolean inConstruction() {
        if (gt == null)
            return false;

        int n = gt.getCons_no();
        return nd > 1 && nd < n;
    }

    public boolean isConstructionFinished() {
        if (gt == null)
            return true;

        int n = gt.getCons_no();
        return nd > n;
    }

    public void setConstructLines(gterm g) {
        initialize();
        gt = g;
        nd = 1;
    }


    public boolean animateDiagramFromString(String s) {
        Animation an = new Animation();
        if (an.loadAnimationString(s, this)) {
            if (gxInstance != null)
                gxInstance.toggleButton(true);
            animate = an;
            autoAnimate();
            return true;
        } else {
            gxInstance.toggleButton(false);
            return false;
        }

    }

    public void autoConstruct(gterm g) {
        setConstructLines(g);

        SetCurrentAction(CONSTRUCT_FROM_TEXT);

        if (cons_timer != null)
            cons_timer.stop();
        int n = 0;
        if (gt.isPositionSet())
            n = 0;
        else {
            Object[] options = {"Text with a diagram",
                    "Auto Construction Step By Step",
                    "Text only"};
            n = JOptionPane.showOptionDialog(gxInstance,
                    "Please choose construct type you want\n",
                    "Constrct type",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
        }

        if (n == 0) {
            while (true) {
                mouseDown(0, 0, true);
                reCalculate();
                if (isConstructionFinished())
                    break;
            }
        } else if (n == 1) {

            cons_timer = new Timer(2000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!isConstructionFinished()) {
                        int index = nd;
                        Pro_point pt = gterm().getProPoint(index);
                        if (pt != null) {
                            mouseDown(pt.getX(), pt.getY());
                        }
                    } else {
                        ((Timer) e.getSource()).stop();
                        	mouseDown(0, 0); // for conclusion.
                    }
                    panel.repaint();
                }
            });
            cons_timer.start();
        }
    }

    public void addAuxPoint(auxpt ax) {
        if (isConstructionFinished()) {
            int act = gxInstance.dp.CurrentAction;

            DrawData.setAuxStatus();
            gxInstance.dp.SetCurrentAction(DrawPanel.CONSTRUCT_FROM_TEXT);

            nd = gterm().getCons_no() + 1;

            int no = ax.getPtsNo();
            for (int i = 0; i < no; i++) {
                Pro_point pt = ax.getPtsbyNo(i);
                gterm gt = gterm();
                gt.addauxedPoint(pt);
                cons cs = new cons(pt.type);
                for (int k = 0; k < pt.ps.length && pt.ps[k] != 0; k++)
                    cs.add_pt(pt.ps[k]);
                gt.addauxedCons(cs);

                int n = pointlist.size();
                int nn = nd;
                while (nn == nd)
                    pointAdded(cs, cs.type, cs.ps, true, nd, pt.getX(), pt.getY(), null, cs.pss);
                if (pointlist.size() > n) {
                    GEPoint c = getLastConstructedPoint();
                    decoratePointAsRecentlyConstructed(c);
                }
            }
            panel.repaint();
            gxInstance.dp.SetCurrentAction(act);
        }

    }

    public static void decoratePointAsRecentlyConstructed(GEPoint pt) {
        pt.setRadius(6);
        pt.setColor(8);
    }

    public boolean addFreePt(cons c) {
        int[] pp = c.ps;
        for (int i = 0; i < pp.length && pp[i] != 0; i++) {
            if (isFreePoint(pp[i]) && addPt(pp[i]) != null)
                return true;
        }
        return false;
    }

    public boolean addGTPt(cons c) {
        int[] pp = c.ps;
        int i = 0;

        for (; i < pp.length && pp[i] != 0; i++) {
            if (null != addPt2(pp[i]))
                break;
        }
        i = 0;
        for (; i < pp.length && pp[i] != 0; i++) {
            if (findPoint(gt.getPtName(pp[i])) == null)
                return true;
        }
        return false;
    }

    public GEPoint addPt2(int n) {
        int x = gt.getPointsNum();
        for (int i = 1; i <= x && i < n; i++) {
            Pro_point pt = gt.getProPoint(i);
            if (findPoint(pt.getName()) == null)
                return addPt(i);
        }
        return addPt(n);
    }

    public void addPt2Line(int p1, int p2, int p3) {
        GELine ln = findLineGivenTwoPointIndices(p2, p3);
        if (ln == null)
            ln = addLn(p2, p3);
        if (ln == null)
            return;
        GEPoint pt = getPt(p1);
        ln.add(pt);
        Constraint cs = new Constraint(Constraint.PONLINE, pt, ln, false);
        addConstraintToList(cs);
    }

    public void mouseDown(double x, double y) {
        mouseDown(x, y, false);
    }

    public void mouseDown(double x, double y, boolean cc) {
        if (CurrentAction == CONSTRUCT_FROM_TEXT) {
            PX = x;
            PY = y;
            GEPoint cp = null;
            if (nd <= gt.getCons_no()) {
                int index = nd;
                cons pt = gterm().getPcons(index);
                if (pt != null) {
                    int[] pp = pt.ps;
                    int type = pt.type;
                    if (!pt.is_conc()) {
                        pointAdded(pt, type, pp, cc, index, x, y, cp, pt.pss);

                        if (index != nd)
                            UndoAdded(pt.toDString(), false);
                    } else {
                        addCondAux(gterm().getConclusion(), false);
                        cond cc1 = gterm().getConc();
                        addConcLineOrCircle(cc1);
                        if (gterm().isTermAnimated()) {
                            String s = gterm().getAnimateString();
                            animateDiagramFromString(s);
                        }
                        UndoAdded(pt.toString(), false);
                        finishConstruction();
                        nd++;
                    }
                    gxInstance.getProofPanel().setListSelection(pt);
                } else finishConstruction();
            } else if (addPt2(gt.getPointsNum()) == null)
                finishConstruction();
        } else {
            super.DWButtonDown(x, y);
        }
    }

    public void pointAdded(cons pt, int type, int[] pp, boolean cc, int index, double x, double y, GEPoint cp, Object[] pss) {
        switch (pt.type) {
            case gib.C_POINT: {

                if (!addFreePt(pt)) {
                    nd++;
                    optimizePolynomial();
                }
            }
            break;
            case gib.C_FOOT: {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.PFOOT, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                    addConstraintToList(cs);
                    addLn(pp[0], pp[1]);
                    addLn(pp[2], pp[3]);
                    addPt2Line(pp[0], pp[2], pp[3]);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.CO_MIDP:
            case gib.C_MIDPOINT: {
                if (!addGTPt(pt)) {
                    addLn(pp[1], pp[2]);
                    Constraint cs = new Constraint(Constraint.MIDPOINT, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]));
                    addConstraintToList(cs);
                    addPt2Line(pp[0], pp[1], pp[2]);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;

            case gib.C_O_C: {
                if (!addGTPt(pt)) {
                    GECircle c = null;
                    if ((c = fd_circle(pp[1], pp[2])) == null) { // add circle
                        addCr(pp[1], pp[2]);
                    } else {
                        cp = getPt(pp[0]);
                        Constraint cs = new Constraint(Constraint.PONCIRCLE, cp, c);
                        c.add(cp);
                        c.pointStickToCircle(cp);
                        characteristicSetMethodAndAddPoly(cc);
                        addConstraintToList(cs);
                        nd++;
                    }
                }
            }
            break;
            case gib.C_O_P: {
                if (!addGTPt(pt)) {
                    GELine lp = null;
                    if ((lp = fd_p_line(pp[1], pp[2], pp[3])) == null) {
                        lp = addPLn(pp[1], pp[2], pp[3]);
                    }
                    {
                        addPointToLine(getPt(pp[0]), lp, false);
                        characteristicSetMethodAndAddPoly(cc);
                        nd++;
                    }
                }
            }
            break;
            case gib.C_O_B: {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.PERPBISECT, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]));
                    addConstraintToList(cs);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.C_O_L: {
                if (!addGTPt(pt)) {
                    GELine ln = addLn(pp[1], pp[2]);
                    cp = getPt(pp[0]);
                    Constraint cs = new Constraint(Constraint.PONLINE, cp, ln);
                    ln.pointonline(cp);
                    characteristicSetMethodAndAddPoly(cc);
                    ln.pointonline(cp);
                    ln.add(cp);
                    addConstraintToList(cs);
                    nd++;
                }
            }
            break;
            case gib.C_O_D: {
                if (!addGTPt(pt)) {
                    addLn(pp[1], pp[2]);
                    cp = getPt(pp[0]);
                    Constraint cs = new Constraint(Constraint.ONDCIRCLE, cp, getPt(pp[1]), getPt(pp[2]));
                    addLn(pp[0], pp[1]);
                    addLn(pp[0], pp[2]);
                    characteristicSetMethodAndAddPoly(cc);
                    addConstraintToList(cs);
                    nd++;
                }
            }
            break;
            case gib.C_I_BR: {
                if (!addGTPt(pt)) {
                    cp = getPt(pp[0]);
                    Constraint cs = new Constraint(Constraint.EQDISTANCE, cp, getPt(pp[1]), cp, getPt(pp[2]));
                    Constraint cs1 = new Constraint(Constraint.EQDISTANCE, cp, getPt(pp[3]), getPt(pp[4]), getPt(pp[5]));
                    characteristicSetMethodAndAddPoly(cc);
                    addConstraintToList(cs);
                    addConstraintToList(cs1);
                    nd++;
                }
            }
            break;

            case gib.C_I_LL: {
                if (!addGTPt(pt)) {
                    cp = getPt(pp[0]);
                    GELine ln = addLn(pp[1], pp[2]);
                    GELine ln1 = addLn(pp[3], pp[4]);
                    Constraint cs = new Constraint(Constraint.PONLINE, cp, ln, false);
                    ln.add(cp);
                    Constraint cs1 = new Constraint(Constraint.PONLINE, cp, ln1, false);
                    ln1.add(cp);
                    Constraint csx = new Constraint(Constraint.INTER_LL, cp, ln, ln1);
                    characteristicSetMethodAndAddPoly(cc);
                    addConstraintToList(csx);
                    addConstraintToList(cs);
                    addConstraintToList(cs1);
                    nd++;
                }
            }
            break;
            case gib.C_I_LP: {
                GELine ln = addLn(pp[1], pp[2]);
                GELine lp;
                if ((lp = fd_p_line(pp[3], pp[4], pp[5])) == null) {
                    addPLn(pp[3], pp[4], pp[5]);
                } else {
                    if (!addGTPt(pt)) {
                        cp = getPt(pp[0]);
                        Constraint cs = new Constraint(Constraint.PONLINE, cp, ln);
                        lp.add(cp);
                        addPointToLine(cp, lp, false);
                        characteristicSetMethodAndAddPoly(cc);
                        addConstraintToList(cs);
                        nd++;
                    }
                }
            }
            break;

            case gib.C_I_PP: {
                GELine lp1, lp2;
                if ((lp1 = fd_p_line(pp[1], pp[2], pp[3])) == null) {
                    addPLn(pp[1], pp[2], pp[3]);
                } else if ((lp2 = fd_p_line(pp[4], pp[5], pp[6])) == null) {
                    addPLn(pp[4], pp[5], pp[6]);
                } else {
                    if (!addGTPt(pt)) {
                        cp = getPt(pp[0]);
                        addPointToLine(cp, lp1, false);
                        addPointToLine(cp, lp2, false);
                        characteristicSetMethodAndAddPoly(cc);
                        nd++;
                    }
                }
            }
            break;
            case gib.C_CIRCUM: {
                if (!addGTPt(pt)) {
                    GEPoint p1 = getPt(pp[1]);
                    GEPoint p2 = getPt(pp[2]);
                    GEPoint p3 = getPt(pp[3]);
                    cp = getPt(pp[0]);
                    Constraint cs = new Constraint(Constraint.CIRCUMCENTER, cp, p1, p2, p3);
                    addConstraintToList(cs);
                    GECircle c = addCr(pp[0], pp[1]);
                    c.add(p2);
                    c.add(p3);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.C_I_CC: {
                if (!addGTPt(pt)) {
                    cp = getPt(pp[0]);
                    GECircle c1 = ad_circle(pp[1], pp[2]);
                    GECircle c2 = ad_circle(pp[3], pp[4]);
                    HashSet<GEPoint> vset = GECircle.CommonPoints(c1, c2);
                    if (vset.size() == 1) {
                        GEPoint t = vset.iterator().next();
                        Constraint cs = new Constraint(Constraint.INTER_CC1, cp, t, c1, c2);
                        c1.add(cp);
                        c2.add(cp);
                        characteristicSetMethodAndAddPoly(cc);
                        addConstraintToList(cs);
                    } else if (vset.isEmpty()) {
                        Constraint cs1 = new Constraint(Constraint.PONCIRCLE, cp, c1, false);
                        Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c2, false);
                        Constraint cs = new Constraint(Constraint.INTER_CC, cp, c1, c2);
                        addConstraintToList(cs1);
                        addConstraintToList(cs2);
                        addConstraintToList(cs);
                        c1.add(cp);
                        c2.add(cp);
                        characteristicSetMethodAndAddPoly(true);
                    }
                    nd++;
                }
            }
            break;
            case gib.C_I_LC: {
                if (!addGTPt(pt)) {
                    GELine ln = addLn(pp[1], pp[2]);
                    GECircle c = ad_circle(pp[3], pp[4]);
                    GEPoint p, p1;
                    p = p1 = null;
                    cp = getPt(pp[0]);
                    for (int i = 0; i < ln.getPtsSize(); i++) {
                        Object obj = ln.points.get(i);
                        if (c.points.contains(obj)) {
                            if (p == null) {
                                p = (GEPoint) obj;
                            } else {
                                p1 = (GEPoint) obj;
                            }
                        }
                    }
                    if (p1 != null && p != null) {
                        return;
                    } else if (p != null && p1 == null) {
                        Constraint css = new Constraint(Constraint.LC_MEET, cp, p, ln, c);
                        characteristicSetMethodAndAddPoly(cc);
                        addConstraintToList(css);
                        ln.add(cp);
                        c.add(cp);
                    } else {
                        Constraint cs1 = new Constraint(Constraint.PONCIRCLE, cp, c, false);
                        Constraint cs2 = new Constraint(Constraint.PONLINE, cp, ln, false);
                        Constraint cs = new Constraint(Constraint.INTER_LC, cp, ln, c);
                        addConstraintToList(cs1);
                        addConstraintToList(cs2);
                        addConstraintToList(cs);
                        ln.add(cp);
                        c.add(cp);
                        characteristicSetMethodAndAddPoly(true);
                    }
                    nd++;
                }
            }
            break;
            case gib.C_I_LT: {
                if (!addGTPt(pt)) {
                    GELine ln = addLn(pp[1], pp[2]);
                    //CLine ln1 = addLn(pp[4], pp[5]);
                    //CLine lnt = addLn(pp[0], pp[3]);
                    Constraint cs = new Constraint(Constraint.PERPENDICULAR, fd_point(pp[0]), fd_point(pp[3]), fd_point(pp[4]), fd_point(pp[5]));
                    GEPoint p = fd_point(pp[0]);
                    addPointToLine(p, ln, false);
                    addConstraintToList(cs);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;

            case gib.C_O_T: {
                if (!addGTPt(pt)) {
                    GELine ln = addLn(pp[0], pp[1]);
                    GELine ln1 = addLn(pp[2], pp[3]);
                    Constraint cs = new Constraint(Constraint.PERPENDICULAR, ln, ln1);
                    addConstraintToList(cs);
                    addLineToList(ln);
                    addLineToList(ln1);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.C_O_A: {
                if (!addGTPt(pt)) {
                    if ((findLineGivenTwoPointIndices(pp[1], pp[2])) == null)
                        addLn(pp[1], pp[2]);
                    if ((findLineGivenTwoPointIndices(pp[3], pp[4])) == null)
                        addLn(pp[3], pp[4]);
                    if ((findLineGivenTwoPointIndices(pp[4], pp[5])) == null)
                        addLn(pp[4], pp[5]);
                    {
                        cp = getPt(pp[0]);
                        addLn(pp[1], pp[0]);
                        Constraint cs = new Constraint(Constraint.ONALINE, fd_point(pp[0]), fd_point(pp[1]),
                                fd_point(pp[2]), fd_point(pp[3]), fd_point(pp[4]), fd_point(pp[5]));
                        addConstraintToList(cs);
                        characteristicSetMethodAndAddPoly(cc);
                        nd++;
                    }
                }
            }
            break;
            case gib.C_O_R: {
                if (!addGTPt(pt)) {
                    {
                        GECircle c = null;
                        while (addGTPt(pt)) ;

                        cp = getPt(pp[0]);
                        c = addCr(pp[1], pp[0]);
                        Constraint cs = new Constraint(Constraint.ONRCIRCLE, cp, getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                        c.add(cp);
                        c.pointStickToCircle(cp);
                        characteristicSetMethodAndAddPoly(cc);
                        addConstraintToList(cs);
                        nd++;
                    }
                }
            }
            break;
            case gib.C_O_S: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.ONSCIRCLE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                addConstraintToList(cs);
                characteristicSetMethodAndAddPoly(cc);
                nd++;
                break;
            }
            case gib.C_O_AB: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.ONSCIRCLE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                addConstraintToList(cs);
                addLn(pp[1], pp[2]);
                addLn(pp[0], pp[2]);
                addLn(pp[3], pp[2]);
                characteristicSetMethodAndAddPoly(cc);
                nd++;
                break;
            }
            case gib.C_SQUARE: {
                while (addGTPt(pt)) ;
                Constraint cs1 = new Constraint(Constraint.PSQUARE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]));
                Constraint cs2 = new Constraint(Constraint.NSQUARE, getPt(pp[1]), getPt(pp[0]), getPt(pp[3]));
                Constraint cs = new Constraint(Constraint.SQUARE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                addConstraintToList(cs);
                addConstraintToList(cs1);
                addConstraintToList(cs2);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[3]);
                addLn(pp[3], pp[0]);
                characteristicSetMethodAndAddPoly(cc);
                nd++;
                break;
            }
            case gib.C_ISO_TRI: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.ISO_TRIANGLE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]));
                addConstraintToList(cs);
                characteristicSetMethodAndAddPoly(cc);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[0]);
                nd++;
                break;
            }
            case gib.C_EQ_TRI: {
                while (addGTPt(pt)) ;
                GEPoint p0 = getPt(pp[0]);
                GEPoint p1 = getPt(pp[1]);
                GEPoint p2 = getPt(pp[2]);
                Constraint cs = new Constraint(Constraint.PETRIANGLE, p0, p1, p2);
//                            constraint cs1 = new constraint(constraint.EQDISTANCE, p0, p1, p0, p2, false);
//                            constraint cs2 = new constraint(constraint.EQDISTANCE, p1, p0, p1, p2, false);
                addConstraintToList(cs);
//                            addConstraintToList(cs1);
//                            addConstraintToList(cs2);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[0]);
                characteristicSetMethodAndAddPoly(cc);
                nd++;
                break;
            }
            case gib.C_R_TRI: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.RIGHT_ANGLED_TRIANGLE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]));
                addConstraintToList(cs);
                characteristicSetMethodAndAddPoly(cc);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[0]);
                nd++;
                break;
            }
            case gib.C_R_TRAPEZOID: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.RIGHT_ANGLE_TRAPEZOID, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                addConstraintToList(cs);
                characteristicSetMethodAndAddPoly(cc);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[3]);
                addLn(pp[3], pp[0]);
                nd++;
                break;
            }
            case gib.C_TRAPEZOID: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.TRAPEZOID, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                addConstraintToList(cs);
                characteristicSetMethodAndAddPoly(cc);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[3]);
                addLn(pp[3], pp[0]);
                nd++;
                break;
            }
            case gib.C_PARALLELOGRAM: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.PARALLELOGRAM, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                addConstraintToList(cs);
                characteristicSetMethodAndAddPoly(cc);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[3]);
                addLn(pp[3], pp[0]);
                nd++;
                break;
            }
            case gib.C_RECTANGLE: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.RECTANGLE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                addConstraintToList(cs);
                characteristicSetMethodAndAddPoly(cc);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[3]);
                addLn(pp[3], pp[0]);
                nd++;
                break;
            }
            case gib.C_TRIANGLE: {
                if (!addGTPt(pt)) {
                    addAllLn(pp);
                    Constraint cs = new Constraint(Constraint.TRIANGLE, SelectList);
                    addConstraintToList(cs);
                    nd++;
                    SelectList.clear();
                }
                break;
            }
            case gib.C_QUADRANGLE: {
                if (!addGTPt(pt)) {
                    addAllLn(pp);
                    Constraint cs = new Constraint(Constraint.QUADRANGLE, SelectList);
                    addConstraintToList(cs);
                    nd++;
                    SelectList.clear();
                }
                break;
            }
            case gib.C_PENTAGON: {
                if (!addGTPt(pt)) {
                    addAllLn(pp);
                    Constraint cs = new Constraint(Constraint.PENTAGON, SelectList);
                    addConstraintToList(cs);
                    nd++;
                    SelectList.clear();
                }
                break;
            }
            case gib.C_POLYGON: {
                if (!addGTPt(pt)) {
                    addAllLn(pp);
                    Constraint cs = new Constraint(Constraint.POLYGON, SelectList);
                    addConstraintToList(cs);
                    nd++;
                    SelectList.clear();
                }
                break;
            }
            case gib.C_INVERSION:
                break;
            case gib.C_REF: {
                cp = addPt(index, x, y);
                GELine ln = findLineGivenTwoPoints(getPt(pp[3]), getPt(pp[4]));
                Constraint cs = new Constraint(Constraint.MIRROR, cp, getPt(pp[1]), ln);
                addConstraintToList(cs);
                characteristicSetMethodAndAddPoly(cc);
                nd++;
            }
            break;
            case gib.C_SYM: {
                cp = addPt(index);
                Constraint cs = new Constraint(Constraint.SYMPOINT, cp,
                        getPt(pp[0]), getPt(pp[1]));
                addConstraintToList(cs);
                characteristicSetMethodAndAddPoly(cc);
                nd++;

            }
            break;
            case gib.C_I_RR: {
                while (addGTPt(pt)) ;
                cp = fd_point(pp[0]);
                GEPoint o = fd_point(pp[1]);
                GEPoint a = fd_point(pp[2]);
                GEPoint b = fd_point(pp[3]);
                Constraint cs = new Constraint(Constraint.EQDISTANCE, cp, o, a, b);
                GEPoint o1 = fd_point(pp[4]);
                GEPoint a1 = fd_point(pp[5]);
                GEPoint b1 = fd_point(pp[6]);
                Constraint cs1 = new Constraint(Constraint.EQDISTANCE, cp, o1, a1, b1);
                addConstraintToList(cs);
                addConstraintToList(cs1);
                characteristicSetMethodAndAddPoly(cc);
                nd++;
            }
            break;
            case gib.C_I_CR:
                break;
            case gib.C_I_LR: {
                while (addGTPt(pt)) ;
                {
                    cp = fd_point(pp[0]);
                    GEPoint o = fd_point(pp[3]);
                    GEPoint a = fd_point(pp[4]);
                    GEPoint b = fd_point(pp[5]);
                    Constraint cs = new Constraint(Constraint.EQDISTANCE, cp, o, a, b);
                    addConstraintToList(cs);
                    addPointToLine(cp, addLn(pp[1], (pp[2])), false);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
                break;
            }
            case gib.C_I_TC: {
                while (addGTPt(pt)) ;
                {
                    GELine lp1 = addTLn(pp[1], pp[2], pp[3]);
                    GECircle c1 = ad_circle(pp[4], pp[5]);
                    cp = fd_point(pp[0]);
//                                MeetLCToDefineAPoint(lp1,c1,false,0,0);
                    addPointToLine(cp, lp1, false);
                    Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c1);
                    addConstraintToList(cs2);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.C_I_TR: {
                {
                    while (addGTPt(pt)) ;
                    addLn(pp[0], pp[1]);
                    addLn(pp[2], pp[3]);
                    GECircle c = add_rcircle(pp[4], pp[5], pp[6]);
                    cp = fd_point(pp[0]);
                    Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c);
                    //constraint cs1 = new constraint(constraint.PERPENDICULAR, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]), fd_point(pp[3]));
                    addConstraintToList(cs2);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.C_I_PC: {

                {
                    while (addGTPt(pt)) ;
                    GELine lp1 = null;
                    GECircle c1 = null;
                    lp1 = addPLn(pp[1], pp[2], pp[3]);
                    c1 = addCr(pp[4], pp[5]);
                    cp = fd_point(pp[0]);
                    addPointToLine(cp, lp1, false);
                    Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c1);
                    addConstraintToList(cs2);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.C_I_TT: {
                GELine lp1, lp2;
                lp1 = lp2 = null;
                {
                    while (addGTPt(pt)) ;
                    lp1 = addTLn(pp[1], pp[2], pp[3]);
                    lp2 = addTLn(pp[4], pp[5], pp[6]);
                    cp = fd_point(pp[0]);
                    addPointToLine(cp, lp1, false);
                    addPointToLine(cp, lp2, false);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }

            }
            break;
            case gib.C_I_PT: {
                GELine lp1, lp2;
                lp1 = lp2 = null;
                while (addGTPt(pt)) ;
                {
                    lp1 = addPLn(pp[1], pp[2], pp[3]);
                    lp2 = addTLn(pp[4], pp[5], pp[6]);
                    cp = fd_point(pp[0]);
                    addPointToLine(cp, lp1, false);
                    addPointToLine(cp, lp2, false);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.C_I_LS: {
                GELine ln = null;
                {
                    ln = addLn(pp[1], pp[2]);
                    cp = fd_point(pp[0]);
                    GECircle c2 = fd_circle(pp[3], pp[4], pp[5]);
                    GEPoint pi = lcmeet(c2, ln);
                    if (pi == null) {
                        Constraint cs = new Constraint(Constraint.PONLINE, cp, ln);
                        Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c2);
                        addConstraintToList(cs);
                        addConstraintToList(cs2);
                        characteristicSetMethodAndAddPoly(cc);
                    } else {
                        if (lcmeet(c2, ln, pi) != null) {
                            return;
                        }
                        Constraint cs = new Constraint(Constraint.LC_MEET, cp, pi, ln, c2);
                        characteristicSetMethodAndAddPoly(cc);
                        addConstraintToList(cs);
                    }
                    nd++;
                }

            }
            break;
            case gib.C_I_SS: {
                GECircle c1 = fd_circle(pp[1], pp[2], pp[3]);
                GECircle c2 = fd_circle(pp[4], pp[5], pp[6]);
                HashSet<GEPoint> s = GECircle.CommonPoints(c1, c2);
                if (s.isEmpty()) {
                    cp = addPt(index, x, y);
                    Constraint cs = new Constraint(Constraint.PONCIRCLE, cp, c1);
                    Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c2);
                    characteristicSetMethodAndAddPoly(cc);
                    addConstraintToList(cs);
                    addConstraintToList(cs2);
                    nd++;
                } else if (s.size() == 1) {
                    cp = addPt(index);
                    GEPoint pi = s.iterator().next();
                    Constraint cs = new Constraint(Constraint.INTER_CC1, cp, pi, c1, c2);
                    characteristicSetMethodAndAddPoly(cc);
                    addConstraintToList(cs);
                    nd++;
                } else {
                    cp = addPt(index);
                    nd++;
                }
                c1.add(cp);
                c2.add(cp);
            }
            break;
            case gib.C_I_LB: {
            	GELine lp1;
                lp1 = null;
                while (addGTPt(pt)) ;

                if ((lp1 = findLineGivenTwoPointIndices(pp[1], pp[2])) == null) {
                    addLn(pp[1], pp[2]);
                } else if ((findLineGivenTwoPointIndices(pp[4], pp[5])) == null) {
                    addLn(pp[4], pp[5]);
                } else {
                    cp = fd_point(pp[0]);
                    addPointToLine(cp, lp1, false);
                    Constraint cs = new Constraint(Constraint.PERPBISECT, cp, fd_point(pp[3]), fd_point(pp[4]), fd_point(pp[5]));
                    addConstraintToList(cs);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.C_I_TB: {
                GELine lp1, lp2;
                lp1 = lp2 = null;
                while (addGTPt(pt)) ;
                if ((lp1 = fd_t_line(pp[1], pp[2], pp[3])) == null) {
                    addLn(pp[1], pp[2]);
                } else if ((lp2 = findLineGivenTwoPointIndices(pp[4], pp[5])) == null) {
                    addLn(pp[4], pp[5]);
                } else {
                    cp = fd_point(pp[0]);
                    Constraint cs1 = new Constraint(Constraint.PERPBISECT, lp1, lp2);
                    Constraint cs2 = new Constraint(Constraint.PERPBISECT, cp, fd_point(pp[3]), fd_point(pp[4]));
                    addConstraintToList(cs1);
                    addConstraintToList(cs2);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.C_I_PB: {
                GELine lp1, lp2;
                lp1 = lp2 = null;
                while (addGTPt(pt)) ;
                if ((lp1 = fd_p_line(pp[1], pp[2], pp[3])) == null) {
                    addPLn(pp[1], pp[2], pp[3]);
                } else if ((lp2 = findLineGivenTwoPointIndices(pp[4], pp[5])) == null) {
                    addLn(pp[4], pp[5]);
                } else {
                    cp = fd_point(pp[0]);
                    Constraint cs1 = new Constraint(Constraint.PARALLEL, lp1, lp2);
                    Constraint cs2 = new Constraint(Constraint.PERPBISECT, cp, fd_point(pp[3]), fd_point(pp[4]));
                    addConstraintToList(cs1);
                    addConstraintToList(cs2);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.C_I_BB: {
//                CLine lp1, lp2;
//                lp1 = lp2 = null;
                {
                    addLn(pp[1], pp[2]);
                    addLn(pp[3], pp[4]);
                    if (!addGTPt(pt)) {
                        Constraint cs1 = new Constraint(Constraint.PERPBISECT, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]));
                        Constraint cs2 = new Constraint(Constraint.PERPBISECT, fd_point(pp[0]), fd_point(pp[3]), fd_point(pp[4]));
                        addConstraintToList(cs1);
                        addConstraintToList(cs2);
                        characteristicSetMethodAndAddPoly(cc);
                        nd++;
                    }
                }
            }
            break;
            case gib.C_I_BC: {
                //CLine lp1 = null;
                //Circle c1 = null;
                while (addGTPt(pt)) ;
                if ((findLineGivenTwoPointIndices(pp[1], pp[2])) == null) {
                    addLn(pp[1], pp[2]);
                } else if ((fd_circle(pp[3], pp[4])) == null) {
                    addCr(pp[3], pp[4]);
                } else {
                    cp = fd_point(pp[0]);
                    Constraint cs1 = new Constraint(Constraint.PERPBISECT, cp, fd_point(pp[1]), fd_point(pp[2]));
                    GECircle c = fd_circle(pp[1], pp[2]);
                    Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c);
                    addConstraintToList(cs1);
                    addConstraintToList(cs2);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            case gib.C_NETRIANGLE: {

            }
            break;
            case gib.C_PETRIANGLE: {
                paraCounter++;
                cp = addPt(index);
                Constraint cs = new Constraint(Constraint.PETRIANGLE, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]));
                addLn(index, pp[1]);
                addLn(index, pp[2]);
                addConstraintToList(cs);
                characteristicSetMethodAndAddPoly(cc);
                nd++;
            }
            break;
            case gib.C_ICENT1:
                break;
            case gib.C_ICENT: {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.INCENTER,
                            fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]), fd_point(pp[3]));
                    addConstraintToList(cs);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.C_ORTH: {
                if (!addGTPt(pt)) {
                    Constraint cs1 = new Constraint(Constraint.ORTHOCENTER,
                            fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]), fd_point(pp[3]));
                    addConstraintToList(cs1);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case gib.C_CENT:
                break;
            case gib.C_TRATIO: {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.TRATIO, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]), fd_point(pp[3]),
                            new Integer(pp[4]), new Integer(pp[5]));
                    addConstraintToList(cs);
                    characteristicSetMethodAndAddPoly(cc);
                    addLn(pp[0], pp[1]);
                    nd++;
                }
                break;
            }
            case gib.C_PRATIO: {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.PRATIO, fd_point(pp[0]), fd_point(pp[1]),
                            fd_point(pp[2]), fd_point(pp[3]), new Integer(pp[4]), new Integer(pp[5]));
                    addConstraintToList(cs);
                    characteristicSetMethodAndAddPoly(cc);
                    addLn(pp[0], pp[1]);
                    nd++;
                }
                break;
            }
            case gib.C_LRATIO: {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.LRATIO, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[3]), new Integer(pp[4]), new Integer(pp[5]));
                    addConstraintToList(cs);
                    characteristicSetMethodAndAddPoly(cc);
                    addLn(pp[1], pp[3]);
                    nd++;
                }
            }
            break;
            case gib.C_CONSTANT: {
                param p = getANewParam();
                Constraint cs = new Constraint(Constraint.CONSTANT, pss[0], pss[1], p);
                addConstraintToList(cs);
                characteristicSetMethodAndAddPoly(false);
                nd++;
            }
            break;
            case gib.C_LINE:
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.LINE, getPt(pp[0]), getPt(pp[1]));
                    addLn(pp[0], pp[1]);
                    addConstraintToList(cs);
                    nd++;
                }
                break;
            case gib.C_CIRCLE:
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.CIRCLE, getPt(pp[0]), getPt(pp[1]));
                    addCr(pp[0], pp[1]);
                    addConstraintToList(cs);
                    nd++;
                    break;
                }
            case gib.C_EQDISTANCE: // not constructable.
            {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.EQDISTANCE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                    addConstraintToList(cs);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
                break;
            }
            case gib.C_EQANGLE: {
                if (!addGTPt(pt)) {
                    GEPoint p1 = getPt(pp[0]);
                    //CPoint p2 = getPt(pp[1]);
                    GEPoint p3 = getPt(pp[2]);
                    GEPoint p4 = getPt(pp[3]);
                    //CPoint p5 = getPt(pp[4]);
                    GEPoint p6 = getPt(pp[5]);
                    GELine ln1 = addLn(pp[0], pp[1]);
                    GELine ln2 = addLn(pp[1], pp[2]);
                    GELine ln3 = addLn(pp[3], pp[4]);
                    GELine ln4 = addLn(pp[4], pp[5]);
                    GEAngle ag1 = new GEAngle(ln1, ln2, p1, p3);
                    GEAngle ag2 = new GEAngle(ln3, ln4, p4, p6);
                    addAngleToList(ag1);
                    addAngleToList(ag2);
                    Constraint cs = new Constraint(Constraint.EQANGLE, ag1, ag2);
                    addConstraintToList(cs);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
                break;
            }
            case gib.C_CCTANGENT: {
                if (!addGTPt(pt)) {
                    GECircle c1 = addCr(pp[1], pp[2]);
                    GECircle c2 = addCr(pp[4], pp[5]);
                    Constraint cs = new Constraint(Constraint.CCTANGENT, c1, c2);
                    addConstraintToList(cs);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
                break;
            }
            case gib.C_EQANGLE3P: {
                if (!addGTPt(pt)) {
                    GELine l1 = addLn(pp[0], pp[1]);
                    GELine l2 = addLn(pp[1], pp[2]);
                    GELine l3 = addLn(pp[3], pp[4]);
                    GELine l4 = addLn(pp[4], pp[5]);
                    GELine l5 = addLn(pp[6], pp[7]);
                    GELine l6 = addLn(pp[7], pp[8]);
                    GEAngle ag1 = new GEAngle(l1, l2, fd_point(pp[0]), fd_point(pp[2]));
                    GEAngle ag2 = new GEAngle(l3, l4, fd_point(pp[3]), fd_point(pp[5]));
                    GEAngle ag3 = new GEAngle(l5, l6, fd_point(pp[6]), fd_point(pp[8]));
                    param pm = findConstantParam(pss[9].toString());
                    Constraint cs = new Constraint(Constraint.EQANGLE3P, ag1, ag2, ag3, pm);
                    addConstraintToList(cs);
                    characteristicSetMethodAndAddPoly(cc);
                    nd++;
                }
                break;
            }
            default: {
                UtilityMiscellaneous.print("draw not yet supported : ");
                nd++;
            }
        }
    }

    public param findConstantParam(String name) {
        for (Constraint cs : constraintlist) {
            if (cs.GetConstraintType() == Constraint.CONSTANT) {
                if (name.equals(cs.getelement(0).toString())) {
                    param p = (param) cs.getelement(2);
                    return p;
                }
            }
        }
        return null;
    }

    public void addObjectFlash(GraphicEntity c1, GraphicEntity c2, GraphicEntity c3) {
        clearFlash();
        int n = UtilityMiscellaneous.getFlashInterval();

        if (c1 != null) {
            Flash f = getObjectFlash(c1);
            f.setDealy(n / 2);
            addFlash1(f);
        }

        if (c2 != null) {

            Flash f = getObjectFlash(c2);
            f.setDealy(n / 2);
            addFlash1(f);
        }

        if (c2 != null) {
            Flash f = getObjectFlash(c3);
            f.setDealy(n / 2);
            addFlash1(f);
        }
        panel.repaint();
    }

    public void flashcons(cons c) {
        if (c == null)
            return;
        switch (c.type) {
            case gib.C_POINT:
//                setObjectListForFlash(fd_line());
                break;
            case gib.C_O_L:
                addObjectFlash(fd_point(c.ps[0]), findLineGivenTwoPointIndices(c.ps[1], c.ps[2]), null);
                break;
            case gib.C_O_P:
                addObjectFlash(fd_point(c.ps[0]), fd_point(c.ps[1]), findLineGivenTwoPointIndices(c.ps[2], c.ps[3]));
                break;
            case gib.C_O_T:
                addObjectFlash(fd_point(c.ps[0]), fd_point(c.ps[1]), findLineGivenTwoPointIndices(c.ps[2], c.ps[3]));
                break;
            case gib.C_O_A:
                break;
            case gib.C_O_C:
                addObjectFlash(fd_point(c.ps[0]), fd_circle(c.ps[1], c.ps[2]), null);
                break;
            case gib.C_O_R:
                addObjectFlash(fd_point(c.ps[0]), fd_circle(c.ps[1], c.ps[2], c.ps[3]), null);
                break;
            case gib.C_I_LL:
                addObjectFlash(fd_point(c.ps[0]), findLineGivenTwoPointIndices(c.ps[1], c.ps[2]), findLineGivenTwoPointIndices(c.ps[3], c.ps[4]));
                break;
            case gib.C_I_LP:
                addObjectFlash(fd_point(c.ps[0]), findLineGivenTwoPointIndices(c.ps[1], c.ps[2]), findLineGivenTwoPointIndices(c.ps[4], c.ps[5]));
                break;
            case gib.C_I_LT:
                addObjectFlash(fd_point(c.ps[0]), findLineGivenTwoPointIndices(c.ps[1], c.ps[2]), findLineGivenTwoPointIndices(c.ps[4], c.ps[5]));
                break;
            case gib.C_I_LC:
                addObjectFlash(fd_point(c.ps[0]), findLineGivenTwoPointIndices(c.ps[1], c.ps[2]), fd_circle(c.ps[3], c.ps[4]));
                break;
            case gib.C_I_PP:
            case gib.C_I_PT:
                addObjectFlash(fd_point(c.ps[0]), findLineGivenTwoPointIndices(c.ps[0], c.ps[1]), findLineGivenTwoPointIndices(c.ps[0], c.ps[4]));
                break;
            case gib.C_I_LR:
                break;
            case gib.C_I_CC:
                addObjectFlash(fd_point(c.ps[0]), fd_circle(c.ps[1], c.ps[2]), fd_circle(c.ps[3], c.ps[4]));
                break;
            case gib.C_FOOT:
                addObjectFlash(fd_point(c.ps[0]), findLineGivenTwoPointIndices(c.ps[0], c.ps[1]), findLineGivenTwoPointIndices(c.ps[2], c.ps[3]));
                break;
            case gib.C_CIRCUM:
            case gib.C_CENT:
            case gib.C_ORTH:
                addObjectFlash(fd_point(c.ps[0]), fd_circle(c.ps[1], c.ps[2], c.ps[3]), null);
                break;
            default:
                addObjectFlash(fd_point(c.ps[0]), null, null);
                break;
        }
    }

    public void finishConstruction() {
        gxInstance.setActionMove();
        SetCurrentAction(MOVE);
        gxInstance.getProofPanel().setListSelectionLast();
        gxInstance.getProofPanel().finishedDrawing();
        flashCond(gterm().getConc(), true);
    }

    public void addConcLineOrCircle(cond cc) {
        if (cc != null) {
	        switch (cc.pred) {
	            case gib.CO_ACONG:
	                drawLineAndAdd(fd_point(cc.p[0]), fd_point(cc.p[1]));
	                drawLineAndAdd(fd_point(cc.p[2]), fd_point(cc.p[3]));
	                drawLineAndAdd(fd_point(cc.p[4]), fd_point(cc.p[5]));
	                drawLineAndAdd(fd_point(cc.p[6]), fd_point(cc.p[7]));
	                break;
	            case gib.CO_CONG:
	                drawLineAndAdd(fd_point(cc.p[0]), fd_point(cc.p[1]));
	                drawLineAndAdd(fd_point(cc.p[2]), fd_point(cc.p[3]));
	        }
        }
    }

    public void addAllLn(int[] pp) {
        SelectList.clear();
        int p1, p2;
        p1 = p2 = 0;
        for (int i = 0; i < pp.length && pp[i] != 0; i++) {
            int p = pp[i];
            if (p1 == 0)
                p1 = p;
            else if (p2 == 0)
                p2 = p;
            else {
                p1 = p2;
                p2 = p;
            }
            if (p1 != 0 && p2 != 0)
                addToSelectList(addLn(p1, p2));
        }
        addToSelectList(addLn(p2, pp[0]));
    }

    GEPoint getPt(int i) {
        String s = gterm().getPtName(i);
        if (s != null && !s.isEmpty()) {
	        for (GEPoint p : pointlist) {
	        	assert(p != null);
	        	String ss = p.getname();
	            if (s.equalsIgnoreCase(ss))
	            	return p;
	        }
        }
        return null;
    }

    public final boolean isFreePoint(int n) {
        return gterm().isFreePoint(n);
    }


    GEPoint addPt(int index) {
        Pro_point pt = gterm().getProPoint(index);
        GEPoint cp = null;
        if (findPoint(pt.getName()) == null) {
            cp = CreateANewPoint(pt.getX(), pt.getY());
            GEText t = cp.textNametag;
            t.setText(pt.name);
            t.setXY(pt.getX1(), pt.getY1());
            addPointToList(cp);
            if (PX != 0 && PY != 0 && cp.getx() == 0 && cp.gety() == 0)
                cp.setXY(PX, PY);
        }
        return cp;
    }

    GEPoint addPt(int index, double x, double y) {
        GEPoint p = addPt(index);
        p.setXY(x, y);
        return p;
    }

    GELine findLineGivenTwoPointIndices(int a, int b) {
        return findLineGivenTwoPoints(getPt(a), getPt(b));
    }

    GELine fd_p_line(int a, int b, int c) {
        GEPoint A = getPt(a);
        GELine ln = findLineGivenTwoPointIndices(b, c);
        if (ln != null) {
            for (Constraint cs : constraintlist) {
                if (cs.GetConstraintType() == Constraint.PARALLEL) {
                    GELine ln1 = (GELine) cs.getelement(0);
                    GELine ln2 = (GELine) cs.getelement(1);
                    if (ln1.points.contains(A) && ln2 == ln) {
                        return ln1;
                    } else if (ln2.points.contains(A) && ln1 == ln) {
                        return ln2;
                    }
                }
            }
        }
        return null;
    }

    GELine fd_t_line(int a, int b, int c) {
        GEPoint A = getPt(a);
        GELine ln = findLineGivenTwoPointIndices(b, c);
        if (ln != null) {
            for (Constraint cs : constraintlist) {
                if (cs.GetConstraintType() == Constraint.PERPENDICULAR) {
                    GELine ln1 = (GELine) cs.getelement(0);
                    GELine ln2 = (GELine) cs.getelement(1);
                    if (ln1.points.contains(A) && ln2 == ln) {
                        return ln1;
                    } else if (ln2.points.contains(A) && ln1 == ln) {
                        return ln2;
                    }
                }
            }
        }
        return null;
    }

    GELine fd_b_line(int a, int b, int c) {
        GEPoint A = getPt(a);
        GEPoint B = getPt(b);
        GEPoint C = getPt(c);
        for (Constraint cs : constraintlist) {
            int t = cs.GetConstraintType();
            if (t == Constraint.PERPBISECT) {
                GEPoint p1 = (GEPoint) cs.getelement(0);
                GEPoint p2 = (GEPoint) cs.getelement(1);
                GEPoint p3 = (GEPoint) cs.getelement(1);

                if (A == p1 && ((B == p2 && C == p3) || (B == p3 && C == p2))) {
                    return findLineGivenTwoPointIndices(b, c);
                }
            }
        }
        return null;
    }

    GECircle ad_circle(int o, int a) {
        GECircle c = fd_circle(o, a);
        if (c != null) {
            return c;
        }
        c = new GECircle(getPt(o), getPt(a));
        addCircleToList(c);
        return c;
    }


    GECircle fd_circle3(int a, int b, int c) {
        GEPoint A = getPt(a);
        GEPoint B = getPt(b);
        GEPoint C = getPt(c);
        for (GECircle cc : circlelist) {
            if (cc.points.contains(A) && cc.points.contains(B) && cc.points.contains(C)) {
                return cc;
            }
        }
        return null;
    }

    GELine addTLn(int a, int b, int c) {
        GEPoint p1 = getPt(a);
        GELine lp = findLineGivenTwoPointIndices(b, c);
        if (lp == null) {
            lp = addLn(b, c);
        }
        GELine ln = new GELine(GELine.TLine, p1);
        Constraint cs = new Constraint(Constraint.PERPENDICULAR, ln, lp);
        ln.addConstraint(cs);
        addLineToList(ln);
        addConstraintToList(cs);
        return ln;
    }

    GELine addPLn(int a, int b, int c) {
        GEPoint p1 = getPt(a);
        GELine lp = findLineGivenTwoPointIndices(b, c);
        if (lp == null) {
            lp = addLn(b, c);
        }
        GELine ln = new GELine(GELine.PLine, p1);
        Constraint cs = new Constraint(Constraint.PARALLEL, ln, lp);
        ln.addConstraint(cs);
        addLineToList(ln);
        addConstraintToList(cs);
        return ln;
    }

    GELine addLn(int a, int b) {
        if (a == 0 || b == 0 || a == b)
            return null;

        GEPoint p1 = getPt(a);
        GEPoint p2 = getPt(b);
        GELine ln = findLineGivenTwoPoints(p1, p2);
        if (p1 != null && p2 != null && ln == null) {
            GELine line = new GELine(p1, p2);
            addLineToList(line);
            return line;

        } else {
            return ln;
        }
    }


    public static GEPoint llmeet(@NonNull GELine ln, @NonNull GELine ln1) {
    	Collection<GEPoint> collectionPoints = new HashSet<GEPoint>();
    	return ln.getCommonPoints(ln1, collectionPoints);
    }

    public static GEPoint lcmeet(GECircle c, GELine ln) {
    	Collection<GEPoint> collectionPoints = new HashSet<GEPoint>();
    	return ln.getCommonPoints(c, collectionPoints);
    }

    public static GEPoint lcmeet(GECircle c, GELine ln, GEPoint p1) {
        for (int i = 0; i < c.points.size(); i++) {
            GEPoint t = c.points.get(i);
            if (t != p1 && ln.isCoincidentWith(t)) {
                return t;
            }
        }
        return null;
    }

    public void flashCond(cond co, boolean fb) {
        if (pointlist.size() == 0) {
            return;
        }

        if (co.p[0] == 0 && co.p[1] == 0) {
            flashattr(co.get_attr(), panel);
        } else {
            if (co.pred == gib.CO_ACONG || co.pred == gib.CO_ATNG) {
                int[] vp = co.p;
                if (vp[0] != 0) {
                    Flash f = getAngleFlash(panel, vp[0], vp[1], vp[2], vp[3]);
                    addFlash(f);
                    f = getAngleFlash(panel, vp[4], vp[5], vp[6], vp[7]);
                    addFlash1(f);
                }
            } else {
                addFlash(getCond(co, fb));
            }
        }
    }


    public Flash getCond(cond co, boolean fb) {
        if (pointlist.size() == 0) {
            return null;
        }
        return getFlashCond(panel, co, fb);
    }

    public void addCongFlash(cond co, boolean cl) {
        if (co == null) {
            return;
        }
        if (co.pred == gib.CO_ACONG) {
            addAcongFlash(co, cl);
        } else {
            addFlash1(getFlashCond(panel, co, cl));
        }
    }

    public void addAcongFlash(cond co, boolean cl) {
        if (co.pred == gib.CO_ACONG) {
            if (cl) {
                clearFlash();
            }
            int[] vp = co.p;
            if (vp[0] == 0) {
                return;
            }
            Flash f = getAngleFlash(panel, vp[0], vp[1], vp[2], vp[3]);
            addFlash1(f);
            f = getAngleFlash(panel, vp[4], vp[5], vp[6], vp[7]);
            addFlash1(f);
        }
    }

    public ArrayList<Flash> getAcongFlash(JPanel panel, cond co) {
        ArrayList<Flash> v = new ArrayList<Flash>();
        if (co.pred == gib.CO_ACONG) {
            int[] vp = co.p;
            if (vp[0] == 0) {
                return null;
            }
            Flash f = getAngleFlash(panel, vp[0], vp[1], vp[2], vp[3]);

            v.add(f);
            f = getAngleFlash(panel, vp[4], vp[5], vp[6], vp[7]);
            v.add(f);
        }
        return v;
    }

    public GELine add_Line(GEPoint a, GEPoint b) {
        GELine ln = findLineGivenTwoPoints(a, b);
        if (ln == null) {
	        ln = new GELine(a, b);
	        addLineToList(ln);
        }
        return ln;
    }

    public GELine addLnWC(GEPoint a, GEPoint b, int color, int d) {
        GELine ln = findLineGivenTwoPoints(a, b);
        if (ln == null) {
            ln = add_Line(a, b);
            if (ln != null) {
                ln.setColor(DrawData.RED);
                ln.setDash(d);
            }
        }
        return ln;
    }

    public GEPoint getPtN(cons c, int n) {
        if (c == null) return null;
        Object o = c.getPTN(n);
        if (o == null) return null;
        String s = o.toString();
        return findPoint(s);

    }

    public void addCondAux(cons co, boolean aux) {
        if (co == null) {
            return;
        }
        int d = 0;
        if (aux) {
            d = DrawData.DASH8;
        }

        switch (co.type) {
            case gib.CO_COLL: {
                GELine ln = addLnWC(getPtN(co, 0), getPtN(co, 1), DrawData.RED, d);
                ln.add(getPtN(co, 2));
                //vl.add(ln);
            }
            break;
            case gib.CO_PARA:
            case gib.CO_PERP:
            case gib.CO_CONG:

            {
                //CLine ln1 = addLnWC(getPtN(co, 0), getPtN(co, 1), drawData.RED, d);
                //CLine ln2 = addLnWC(getPtN(co, 2), getPtN(co, 3), drawData.RED, d);
                //vl.add(ln1);
                //vl.add(ln2);
            }
            break;
            case gib.CO_ACONG:
                break;
            case gib.CO_MIDP: {
                add_Line(getPtN(co, 0), getPtN(co, 1));
                add_Line(getPtN(co, 2), getPtN(co, 3));
                int n = getEMarkNum() / 2 + 1;
                GEEqualDistanceMark m1 = addedMark(getPtN(co, 0), getPtN(co, 1));
                GEEqualDistanceMark m2 = addedMark(getPtN(co, 2), getPtN(co, 3));
                if (m1 != null) {
                    m1.setdnum(n);
                }
                if (m2 != null) {
                    m2.setdnum(n);
                }
            }
            break;
            case gib.CO_CTRI:
                break;
            case gib.CO_CYCLIC:
                break;
        }
        //return vl;
    }

    public Flash getFlashCond(JPanel panel, cond co, boolean fb) {
        Flash f = getFlashCond(panel, co);
        return f;
    }

    public Flash getFlashCond(JPanel panel, cond co) {

        if (pointlist.size() == 0) {
            return null;
        }

        switch (co.pred) {
            case gib.CO_COLL: {
                FlashLine f = new FlashLine(panel);
                int d = f.addALine();
                for (int i = 0; i < 3; i++) {
                    f.addAPoint(d, fd_point(co.p[i]));
                }
                return (f);
            }
            case gib.CO_PARA: {
                int[] p = co.p;
                FlashLine f = new FlashLine(panel);
                int id = f.addALine();
                f.addAPoint(id, fd_point(p[0]));
                f.addAPoint(id, fd_point(p[1]));
                f.setInfinitLine(id);
                id = f.addALine();
                f.addAPoint(id, fd_point(p[2]));
                f.addAPoint(id, fd_point(p[3]));
                f.setInfinitLine(id);
                return (f);
            }
            case gib.CO_PERP: {
                int[] p = co.p;
                FlashTLine f = new FlashTLine(panel, fd_point(p[0]), fd_point(p[1]),
                        fd_point(p[2]), fd_point(p[3]));
                addFlash1(f);
                return (f);
            }
            case gib.CO_CONG: {
                int[] p = co.p;
                if ((p[0] == p[2] && p[1] == p[3]) || (p[0] == p[3] && p[1] == p[2])) {
                    FlashLine f = new FlashLine(panel);
                    int id = f.addALine();
                    f.addAPoint(id, fd_point(p[0]));
                    f.addAPoint(id, fd_point(p[1]));
                    return (f);
                } else {
                    FlashCG f = new FlashCG(panel);
                    f.addACg(fd_point(p[0]), fd_point(p[1]));
                    f.addACg(fd_point(p[2]), fd_point(p[3]));
                    return (f);
                }
            }
            case gib.CO_MIDP: {
                int[] p = co.p;
                FlashCG f = new FlashCG(panel);
                f.addACg(fd_point(p[0]), fd_point(p[1]));
                f.addACg(fd_point(p[0]), fd_point(p[2]));
                return (f);
            }
            case gib.CO_ACONG: {
                int[] vp = co.p;
                if (vp[0] == 0) {
                    break;
                }
                Flash f = getAngleFlash(panel, vp[0], vp[1], vp[2], vp[3]);
                return (f);
            }
            case gib.CO_TANG: {
                Flash f = getAngleFlash(panel, co.p[0], co.p[1], co.p[1], co.p[2]);
                return (f);
            }
            case gib.CO_STRI:
            case gib.CO_CTRI: {
                int[] p = co.p;
                FlashTriangle f = new FlashTriangle(panel, fd_point(p[0]), fd_point(p[1]), fd_point(p[2]), fd_point(p[3]), fd_point(p[4]),
                        fd_point(p[5]), true, DrawData.LIGHTCOLOR);
                return (f);
            }
            case gib.CO_CYCLIC: {
                FlashCircle f = new FlashCircle(panel);
                int[] p = co.p;
                if (co.p[0] != 0) {
                    f.setCenter(fd_point(p[0]));
                }
                for (int i = 1; i < p.length; i++) {
                    if (p[i] != 0) {
                        f.addAPoint(fd_point(p[i]));
                    }
                }
                return (f);
            }

        }
        return null;

    }

    public Flash getAngleFlash(JPanel panel, int p1, int p2, int p3, int p4) {
        return getMAngleFlash(panel, fd_point(p1), fd_point(p2), fd_point(p3), fd_point(p4), 1); // full angle.
    }

    public Flash getMAngleFlash(JPanel panel, GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4, int t) {
        GEAngle ag = fd_angle_m(p1, p2, p3, p4);
        if (ag != null) {
            Flash f = getObjectFlash(ag);
            return f;
        }
        FlashAngle f = new FlashAngle(panel, p1, p2, p3, p4);
        f.setFtype(t);

        GELine ln1, ln2;
        if ((ln1 = findLineGivenTwoPoints(p1, p2)) != null) {
            f.setDrawLine1(false);
        }
        if ((ln2 = findLineGivenTwoPoints(p3, p4)) != null) {
            f.setDrawLine2(false);
        }
        if (ln1 != null && ln2 != null && GELine.commonPoint(ln1, ln2) != null) {
            f.setAngleTwoLineIntersected(true);
        }

        return f;
    }

 /*   public static void flashMpnode(mobject obj) {
        if (obj instanceof massertion) {

        } else if (obj instanceof mdrobj) {

        }
    }*/

    public int getAreaFlashNumber() {
        int n = 0;
        for (int i = 0; i < flashlist.size(); i++) {
            Flash f = flashlist.get(i);
            if (f instanceof FlashArea) {
                n++;
            }
        }
        return n;
    }

    public Flash getAreaFlash(mdrobj d) {
        int n = getAreaFlashNumber();
        ArrayList<GEPoint> v = new ArrayList<GEPoint>();
        for (int i = 0; i < d.getObjectNum(); i++)
            v.add(d.getObject(i));

        GEPolygon p = findPolygon1(v);
        if (p == null) {
            FlashArea f = new FlashArea(panel, n);
            for (int i = 0; i < v.size(); i++)
                f.addAPoint(v.get(i));
            return f;
        }
        FlashObject f = new FlashObject(panel);
        f.addFlashObject(p);
        return f;
    }

    public Flash getDrobjFlash(mdrobj d) {
        int t1 = d.getType1();
        if (t1 == mdrobj.LINE) {
            GEPoint p1 = d.getObject(0);
            GEPoint p2 = d.getObject(1);
            if (p1 != null && p2 != null) {
                FlashLine f = new FlashLine(panel);
                int id = f.addALine();
                f.addAPoint(id, p1);
                f.addAPoint(id, p2);
                return f;
            }
        } else if (t1 == mdrobj.CIRCLE) {
        } else if (t1 == mdrobj.AREA || t1 == mdrobj.TRIANGLE ||
                t1 == mdrobj.SQUARE || t1 == mdrobj.PARALLELOGRAM || t1 == mdrobj.RECTANGLE
                || t1 == mdrobj.QUADRANGLE || t1 == mdrobj.TRAPEZOID) {
            return (getAreaFlash(d));
        } else if (t1 == mdrobj.ANGLE) {
            if (d.getObjectNum() == 3) {
                Flash f = getMAngleFlash(panel, d.getObject(0), d.getObject(1),
                        d.getObject(1), d.getObject(2), 3);
                return f;
            }
        }
        return null;
    }

    public void flashmnode(mnode n) {

        for (int i = 0; i < n.objSize(); i++) {
            mobject obj = n.getObject(i);
            flashmobj(obj, false);
        }
    }

    public void flashmobj(mobject obj) {
        flashmobj(obj, true);
    }

    public void flashmobj(mobject obj, boolean clear) {
        if (obj == null) {
            return;
        }
        if (clear) {
            clearFlash();
        }
        int t = obj.getType();
        switch (t) {
            case mobject.DOBJECT: {
                mdrobj d = (mdrobj) obj;
                Flash f = getDrobjFlash(d);
                if (f != null) {
                    addFlash1(f);
                }
            }
            break;
            case mobject.DRAW: {
                mdraw d = (mdraw) obj;
                ArrayList<UndoStruct> v = new ArrayList<UndoStruct>();
		d.getAllUndoStruct(v);
                setUndoListForFlash1(v);
            }
            break;
            case mobject.ASSERT:
                flashassert((massertion) obj);
                break;
            case mobject.EQUATION: {
                mequation eq = (mequation) obj;
                int cd = 0;
                for (int i = 0; i < eq.getTermCount(); i++) {
                    meqterm tm = eq.getTerm(i);
                    mdrobj dj = tm.getObject();
                    if (dj != null) {
                        Flash fs = getDrobjFlash(dj);
                        if (tm.isEqFamily() && !dj.isPolygon())
                            cd++;

                        if (fs != null) {
                            fs.setColor(DrawData.getColorSinceRed(cd));
                            addFlash1(fs);
                        }
                    }
                }
            }
            break;
        }
    }

    public void addlineFlash(GEPoint p1, GEPoint p2) {
        FlashLine f = new FlashLine(panel);
        int id = f.addALine();
        f.addAPoint(id, p1);
        f.addAPoint(id, p2);
        addFlash1(f);
    }

    public void addInfinitelineFlash(GEPoint p1, GEPoint p2) {
        FlashLine f = new FlashLine(panel);
        int id = f.addALine();
        f.addAPoint(id, p1);
        f.addAPoint(id, p2);
        f.setInfinitLine(id);
        addFlash1(f);
    }

    public void flashassert(massertion ass) {
        if (ass == null)
            return;

        switch (ass.getAssertionType()) {
            case massertion.COLL: {
                FlashLine f = new FlashLine(panel);
                int id = f.addALine();
                for (int i = 0; i < ass.getobjNum(); i++) {
                    f.addAPoint(id, (GEPoint) ass.getObject(i));
                }
                addFlash1(f);
            }
            break;
            case massertion.PARA: {
                FlashLine f = new FlashLine(panel);
                int id = 0;
                for (int i = 0; i < ass.getobjNum(); i++) {
                    if (i % 2 == 0) {
                        id = f.addALine();
                        f.setInfinitLine(id);
                    }
                    f.addAPoint(id, (GEPoint) ass.getObject(i));
                }
                addFlash1(f);
            }
            break;
            case massertion.EQDIS: {
                GEPoint p1 = (GEPoint) ass.getObject(0);
                GEPoint p2 = (GEPoint) ass.getObject(1);
                GEPoint p3 = (GEPoint) ass.getObject(2);
                GEPoint p4 = (GEPoint) ass.getObject(3);
                if (p1 != null && p2 != null && p3 != null && p4 != null) {
                    addCGFlash(p1, p2, p3, p4);
                }
            }
            break;
            case massertion.PERP: {
                GEPoint p1 = (GEPoint) ass.getObject(0);
                GEPoint p2 = (GEPoint) ass.getObject(1);
                GEPoint p3 = (GEPoint) ass.getObject(2);
                GEPoint p4 = (GEPoint) ass.getObject(3);
                FlashTLine f = new FlashTLine(panel, p1, p2, p3, p4);
                addFlash1(f);
            }
            break;

            case massertion.DISLESS:
            case massertion.PERPBISECT:
            case massertion.CONCURRENT: {
                FlashLine f = new FlashLine(panel);
                int id = 0;
                for (int i = 0; i < ass.getobjNum(); i++) {
                    if (i % 2 == 0) {
                        id = f.addALine();
                    }
                    f.addAPoint(id, (GEPoint) ass.getObject(i));
                }
                addFlash1(f);
            }

            break;
            case massertion.CYCLIC: {
                FlashCircle f = new FlashCircle(panel);
                for (int i = 0; i < ass.getobjNum(); i++) {
                    f.addAPoint((GEPoint) ass.getObject(i));
                }
                addFlash1(f);
            }
            break;
            case massertion.EQANGLE:
            case massertion.ANGLESS: {
                if (ass.getobjNum() == 6) {
                    Flash f = getMAngleFlash(panel, (GEPoint) ass.getObject(0), (GEPoint) ass.getObject(1),
                            (GEPoint) ass.getObject(1), (GEPoint) ass.getObject(2), 3);
                    addFlash1(f);
                    f = getMAngleFlash(panel, (GEPoint) ass.getObject(3), (GEPoint) ass.getObject(4),
                            (GEPoint) ass.getObject(4), (GEPoint) ass.getObject(5), 3);
                    addFlash1(f);
                }
            }
            break;
            case massertion.MID:
                if (ass.getobjNum() == 3) {
                    FlashCG f = new FlashCG(panel);
                    GEPoint p1 = (GEPoint) ass.getObject(0);
                    GEPoint p2 = (GEPoint) ass.getObject(1);
                    GEPoint p3 = (GEPoint) ass.getObject(2);
                    f.addACg(p1, p2);
                    f.addACg(p1, p3);
                    if (fd_edmark(p1, p2) != null)
                        f.setDrawdTT(false);

                    addFlash1(f);
                }
                break;
            case massertion.CONG:
            case massertion.SIM: {
                if (6 == ass.getobjNum()) {
                    FlashTriangle f = new FlashTriangle(panel,
                            (GEPoint) ass.getObject(0),
                            (GEPoint) ass.getObject(1),
                            (GEPoint) ass.getObject(2),
                            (GEPoint) ass.getObject(3),
                            (GEPoint) ass.getObject(4),
                            (GEPoint) ass.getObject(5), false,
                            DrawData.LIGHTCOLOR);
                    addFlash1(f);
                }
            }
            break;

            case massertion.R_TRIANGLE: {
                addAreaFlash(ass);
                GEPoint p1 = (GEPoint) ass.getObject(0);
                GEPoint p2 = (GEPoint) ass.getObject(1);
                GEPoint p3 = (GEPoint) ass.getObject(2);
//                CPoint p4 = (CPoint) ass.getObject(3);
                FlashTLine f = new FlashTLine(panel, p1, p2, p1, p3);
                addFlash1(f);
                break;
            }
            case massertion.R_ISO_TRIANGLE: {
                addAreaFlash(ass);

                GEPoint p1 = (GEPoint) ass.getObject(0);
                GEPoint p2 = (GEPoint) ass.getObject(1);
                GEPoint p3 = (GEPoint) ass.getObject(2);
                addCGFlash(p1, p2, p1, p3);

                FlashTLine f = new FlashTLine(panel, p1, p2, p1, p3);
                addFlash1(f);
                break;
            }

            case massertion.CONVEX:
            case massertion.TRAPEZOID:
            case massertion.SQUARE:
            case massertion.RECTANGLE: {
                addAreaFlash(ass);
                break;
            }
            case massertion.EQ_TRIANGLE: {
                addAreaFlash(ass);
                GEPoint p1 = (GEPoint) ass.getObject(0);
                GEPoint p2 = (GEPoint) ass.getObject(1);
                GEPoint p3 = (GEPoint) ass.getObject(2);

                FlashCG f1 = new FlashCG(panel);
                f1.addACg(p1, p2);
                if (null != fd_edmark(p1, p2))
                    f1.setDrawdTT(false);
                FlashCG f2 = new FlashCG(panel);
                f2.addACg(p1, p3);
                if (null != fd_edmark(p1, p3))
                    f2.setDrawdTT(false);

                FlashCG f3 = new FlashCG(panel);
                f3.addACg(p2, p3);
                if (null != fd_edmark(p1, p3))
                    f3.setDrawdTT(false);
                addFlash1(f1);
                addFlash1(f2);
                addFlash1(f3);
                break;
            }
            case massertion.ISO_TRIANGLE: {
                addAreaFlash(ass);
                GEPoint p1 = (GEPoint) ass.getObject(0);
                GEPoint p2 = (GEPoint) ass.getObject(1);
                GEPoint p3 = (GEPoint) ass.getObject(2);
                addCGFlash(p1, p2, p1, p3);
                break;

            }
            case massertion.PARALLELOGRAM: {
                addAreaFlash(ass);
                break;
            }

            case massertion.BETWEEN: {
                GEPoint p1 = (GEPoint) ass.getObject(0);
                GEPoint p2 = (GEPoint) ass.getObject(1);
                GEPoint p3 = (GEPoint) ass.getObject(2);
                addPtEnlargeFlash(p1);
                addlineFlash(p2, p3);
                break;
            }

            case massertion.ANGLE_INSIDE:
            case massertion.ANGLE_OUTSIDE: {
                GEPoint p1 = (GEPoint) ass.getObject(0);
                GEPoint p2 = (GEPoint) ass.getObject(1);
                GEPoint p3 = (GEPoint) ass.getObject(2);
                GEPoint p4 = (GEPoint) ass.getObject(3);
                addPtEnlargeFlash(p1);
                Flash f = getMAngleFlash(panel, p2, p3, p3, p4, 3);
                addFlash1(f);
                break;
            }
            case massertion.TRIANGLE_INSIDE: {
                addAreaFlash1(ass);
                GEPoint p1 = (GEPoint) ass.getObject(0);
                addPtEnlargeFlash(p1);
                break;
            }
            case massertion.OPPOSITE_SIDE: {

                GEPoint p1 = (GEPoint) ass.getObject(0);
                GEPoint p2 = (GEPoint) ass.getObject(1);
                GEPoint p3 = (GEPoint) ass.getObject(2);
                GEPoint p4 = (GEPoint) ass.getObject(3);
                addInfinitelineFlash(p3, p4);
                FlashArrow f = new FlashArrow(panel, p1, p2, 0);
                addFlash1(f);
                break;
            }
            case massertion.SAME_SIDE: {
                GEPoint p1 = (GEPoint) ass.getObject(0);
                GEPoint p2 = (GEPoint) ass.getObject(1);
                GEPoint p3 = (GEPoint) ass.getObject(2);
                GEPoint p4 = (GEPoint) ass.getObject(3);
                addInfinitelineFlash(p3, p4);
                FlashArrow f = new FlashArrow(panel, p1, p2, 1);
                addFlash1(f);
                break;
            }
            case massertion.PARA_INSIDE: {
                addAreaFlash1(ass);
                addPtEnlargeFlash((GEPoint) ass.getObject(0));
                break;
            }
        }

    }

    public void addPtEnlargeFlash(GEPoint pt) {
        FlashPoint f = new FlashPoint(panel, pt);
        addFlash1(f);
    }

    public void addCGFlash(GEPoint p1, GEPoint p2, GEPoint p3, GEPoint p4) {
        FlashCG f1 = new FlashCG(panel);
        f1.addACg(p1, p2);
        if (null != fd_edmark(p1, p2))
            f1.setDrawdTT(false);
        FlashCG f2 = new FlashCG(panel);
        f2.addACg(p3, p4);
        if (null != fd_edmark(p3, p4))
            f2.setDrawdTT(false);
        FlashSegmentMoving fn = new FlashSegmentMoving(panel, p1, p2, p3, p4, 3, 3);
        addCgFlash(f1, f2, fn);
        startFlash();
    }

    public void addAreaFlash1(massertion ass) {
        int n = getAreaFlashNumber();
        FlashArea f = new FlashArea(panel, n);
        for (int i = 1; i < ass.getobjNum(); i++) {
            f.addAPoint((GEPoint) ass.getObject(i));
        }
        addFlash1(f);
    }

    public void addAreaFlash(massertion ass) {
        int n = getAreaFlashNumber();
        FlashArea f = new FlashArea(panel, n);
        for (int i = 0; i < ass.getobjNum(); i++) {
            f.addAPoint((GEPoint) ass.getObject(i));
        }
        addFlash1(f);
    }

    public Flash getAngleFlashLL(JPanel panel, int p, l_line l1, l_line l2) {
        int a, b;

        if (p == l1.pt[0])
            a = l1.pt[1];
        else
            a = l1.pt[0];
        if (p == l2.pt[0])
            b = l2.pt[1];
        else
            b = l2.pt[0];
        Flash f = getAngleFlash(panel, a, p, b, p);
        return f;
    }

    public void flashattr(cclass cc, JPanel panel) {
        if (cc == null)
            return;

        if (pointlist.size() == 0)
            return;

        if (cc instanceof angles) {
            angles ag = (angles) cc;
            l_line ln1 = ag.l1;
            l_line ln2 = ag.l2;
            l_line ln3 = ag.l3;
            l_line ln4 = ag.l4;
            Flash f = getAngleFlash(panel, ln1.pt[0], ln1.pt[1], ln2.pt[0], ln2.pt[1]);
            Flash f1 = getAngleFlash(panel, ln3.pt[0], ln3.pt[1], ln4.pt[0], ln4.pt[1]);
            addFlash1(f);
            addFlash1(f1);
        } else if (cc instanceof l_line) {
            l_line ln = (l_line) cc;
            FlashLine f = new FlashLine(panel);
            int id = f.addALine();
            for (int i = 0; i <= ln.no; i++) {
                f.addAPoint(id, fd_point(ln.pt[i]));
            }
            addFlash(f);
        } else if (cc instanceof p_line) {
            p_line pn = (p_line) cc;
            FlashLine f = new FlashLine(panel);
            for (int i = 0; i <= pn.no; i++) {
                l_line ln = pn.ln[i];
                int nd = f.addALine();
                f.setInfinitLine(nd);
                for (int j = 0; j <= ln.no; j++) {
                    f.addAPoint(nd, fd_point(ln.pt[j]));
                }
            }
            addFlash(f);

        } else if (cc instanceof t_line) {
            t_line tn = (t_line) cc;
            l_line ln = tn.l1;
            l_line ln1 = tn.l2;
            FlashTLine f = new FlashTLine(panel);
            for (int i = 0; i <= ln.no; i++)
                f.ln1.addAPoint(fd_point(ln.pt[i]));
            for (int i = 0; i <= ln1.no; i++)
                f.ln2.addAPoint(fd_point(ln1.pt[i]));
            addFlash(f);
        } else if (cc instanceof a_cir) {
            a_cir ac = (a_cir) cc;
            FlashCircle f = new FlashCircle(panel);
            f.setCenter(fd_point(ac.o));
            for (int i = 0; i <= ac.no; i++) {
                f.addAPoint(fd_point(ac.pt[i]));
            }
            addFlash(f);

        } else if (cc instanceof sim_tri) {
            sim_tri sm = (sim_tri) cc;
            clearFlash();
            int cn = DrawData.LIGHTCOLOR;
            FlashTriangle f = new FlashTriangle(panel, fd_point(sm.p1[0]), fd_point(sm.p1[1]), fd_point(sm.p1[2]),
                    fd_point(sm.p2[0]), fd_point(sm.p2[1]), fd_point(sm.p2[2]),
                    sm.dr == 1, cn);
            addFlash1(f);

        } else if (cc instanceof cong_seg) {
            cong_seg cg = (cong_seg) cc;
            FlashCG f = new FlashCG(panel);
            f.addACg(fd_point(cg.p1), fd_point(cg.p2));
            f.addACg(fd_point(cg.p3), fd_point(cg.p4));
            addFlash(f);
        } else if (cc instanceof midpt) {
            midpt md = (midpt) cc;
            FlashCG f = new FlashCG(panel);
            f.addACg(fd_point(md.a), fd_point(md.m));
            f.addACg(fd_point(md.b), fd_point(md.m));
            addFlash(f);
        } else if (cc instanceof ratio_seg) {
            ratio_seg ra = (ratio_seg) cc;
            FlashLine f = new FlashLine(panel);
            for (int i = 0; i < 4; i++) {
                int id = f.addALine();
                f.addAPoint(id, fd_point(ra.r[i * 2 + 1]));
                f.addAPoint(id, fd_point(ra.r[i * 2 + 2]));
            }
            f.setAlternate(true);
            addFlash(f);
        } else if (cc instanceof angst) {
            angst ag = (angst) cc;
            for (int i = 0; i < ag.no; i++) {
                Flash f = getAngleFlash(panel, ag.ln1[i].pt[0], ag.ln1[i].pt[1],
                        ag.ln2[i].pt[0], ag.ln2[i].pt[1]);
                addFlash1(f);
            }
        } else if (cc instanceof anglet) {
            anglet at = (anglet) cc;
            int a, b, p;
            p = at.p;
            if (p == at.l1.pt[0])
                a = at.l1.pt[1];
            else
                a = at.l1.pt[0];
            if (p == at.l2.pt[0])
                b = at.l2.pt[1];
            else
                b = at.l2.pt[0];
            Flash f = getAngleFlash(panel, a, p, b, p);
            addFlash1(f);
        } else if (cc instanceof angtn) {
            angtn atn = (angtn) cc;
            clearFlash();
            Flash f1 = getAngleFlashLL(panel, atn.t1, atn.ln1, atn.ln2);
            Flash f2 = getAngleFlashLL(panel, atn.t2, atn.ln3, atn.ln4);
            addFlash1(f1);
            addFlash1(f2);

        } else if (cc instanceof s_tris) {
            s_tris sm = (s_tris) cc;
            int n = sm.no;
            clearFlash();

            for (int i = 0; i < n; i++) {
                int cn = DrawData.LIGHTCOLOR;
                FlashTriangle f = new FlashTriangle(panel, fd_point(sm.p1[i]), fd_point(sm.p2[i]), fd_point(sm.p3[i]),
                        fd_point(sm.p1[i + 1]), fd_point(sm.p2[i + 1]), fd_point(sm.p3[i + 1]),
                        sm.dr[i] == sm.dr[i + 1], cn + i);
                addFlash1(f);
            }
        } else if (cc instanceof c_segs) {
            c_segs cg = (c_segs) cc;
            FlashCG f = new FlashCG(panel);
            for (int i = 0; i <= cg.no; i++) {
                f.addACg(fd_point(cg.p1[i]), fd_point(cg.p2[i]));
            }
            addFlash(f);
        } else if (cc instanceof angtr) {

        } else if (cc instanceof l_list) {
            clearFlash();
            l_list ls = (l_list) cc;
            for (int i = 0; i < ls.nd; i++) {
                angtr t = ls.md[i].tr;
                if (t != null) {
                    Flash f = getAngleFlash(panel, t.get_lpt1(), t.v, t.v, t.get_lpt2());
                    addFlash1(f);
                }
            }
            mnde m = ls.mf[0];
            if (m != null) {
                angtr t = m.tr;
                if (t != null) {
                    Flash f = getAngleFlash(panel, t.get_lpt1(), t.v, t.v, t.get_lpt2());
                    f.setColor(Color.pink);
                    addFlash1(f);
                }
            }
        } else if (cc instanceof rule) {
            clearFlash();
            rule r = (rule) cc;
            for (int i = 0; i < r.mr1.length; i++) {
                mnde m = r.mr1[i];
                if (m == null)
                    continue;

                angtr t = r.mr1[i].tr;
                if (t != null) {
                    Flash f = getAngleFlash(panel, t.get_lpt1(), t.v, t.v, t.get_lpt2());
                    addFlash1(f);
                }
            }
            mnde m = r.mr;
            if (m != null) {
                angtr t = m.tr;
                if (t != null) {
                    Flash f = getAngleFlash(panel, t.get_lpt1(), t.v, t.v, t.get_lpt2());
                    f.setColor(Color.pink);
                    addFlash1(f);
                }
            }
        }

    }

    public static void falshPropoint(Pro_point pt) {
        switch (pt.type) {
            case gib.C_CIRCUM:
        }
    }

    public FlashAngle find_angFlash(GEPoint a, GEPoint b, GEPoint c, GEPoint d) {
        for (int i = 0; i < flashlist.size(); i++) {
            Flash flash = flashlist.get(i);
            if (flash instanceof FlashAngle) {
                FlashAngle f = (FlashAngle) flash;
                if (f.p1 == a && f.p2 == b && f.p3 == c && f.p4 == d) {
                    return f;
                }
            }
        }
        return null;
    }

    public Flash addFlashXtermAngle(xterm x) {
        if (x == null) {
            return null;
        }
        var v = x.var;
        if (v == null) {
            return null;
        }
        int a, b, c, d;
        if (x.getPV() > 0) {
            a = v.pt[0];
            b = v.pt[1];
            c = v.pt[2];
            d = v.pt[3];
        } else {
            a = v.pt[2];
            b = v.pt[3];
            c = v.pt[0];
            d = v.pt[1];
        }
        GEPoint p1 = fd_point(a);
        GEPoint p2 = fd_point(b);
        GEPoint p3 = fd_point(c);
        GEPoint p4 = fd_point(d);
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return null;
        }

        Flash f;
        if ((f = find_angFlash(p1, p2, p3, p4)) != null) {
            f.start();
        } else {
            f = getAngleFlash(panel, a, b, c, d);
            addFlash1(f);
            if (x.getPV() < 0) {
                f.setColor(Color.magenta);
            }
            panel.repaint();
        }
        return f;
    }


    public Flash addFlashAngle(int a, int b, int c, int d) {
        GEPoint p1 = fd_point(a);
        GEPoint p2 = fd_point(b);
        GEPoint p3 = fd_point(c);
        GEPoint p4 = fd_point(d);
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return null;
        }

        Flash f;
        if ((f = find_angFlash(p1, p2, p3, p4)) != null) {
            f.start();
        } else {
            f = getAngleFlash(panel, a, b, c, d);
            addFlash1(f);
            panel.repaint();
        }
        return f;
    }

    public void addauxPoint(int m1, int m2) {
        GELine ln = findLineGivenTwoPointIndices(m1, m2);
        if (ln != null) {
            return;
        }
        ln = addLn(m1, m2);
        ln.m_dash = 3;
        ln.m_color = DrawData.getColorIndex(Color.red);
        ln.m_width = 1;
    }

    public void addaux(ProofText cpt) {
        cond co = cpt.getcond();
        if (co == null) {
            return;
        }
        switch (co.pred) {
            case gib.CO_COLL: {
                int[] p = new int[3];
                for (int i = 0; i < 3; i++) {
                    p[i] = co.p[i];
                }
                //  proveFlash(1, p, null);
                break;
            }
            case gib.CO_PARA:
            case gib.CO_PERP:
            case gib.CO_CONG: {
                int[] p1 = new int[2];
                for (int i = 0; i < 2; i++) {
                    p1[i] = co.p[i];
                }
                int[] p2 = new int[2];
                for (int i = 2; i < 4; i++) {
                    p2[i - 2] = co.p[i];
                }
                if (co.pred == gib.CO_PARA) {
                } else if (co.pred == gib.CO_PERP) { // proveFlash(2, p1, p2);
                } else if (co.pred == gib.CO_CONG) { //proveFlash(3, p1, p2);
                    if (p1[0] == p2[0] && p1[1] == p2[1] ||
                            p1[0] == p2[1] && p1[1] == p2[0]) {
                    } else {
                        GEEqualDistanceMark ce1 = addedMark(p1[0], p1[1]);
                        GEEqualDistanceMark ce2 = addedMark(p2[0], p2[1]);
                        aux_mark++;
                        ce1.setdnum(aux_mark);
                        ce2.setdnum(aux_mark);
                        ce1.setColor(aux_mark + 2);
                        ce2.setColor(aux_mark + 2);
                        ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
                        v.add(ce1);
                        v.add(ce2);
                        flashStep(v);
                    }
                }
                addLn(p1[0], p1[1]);
                addLn(p2[0], p2[1]);
            }
            break;
            case gib.CO_ACONG: {
                //int[] vp = co.p;
                /*if (vp[0] != 0 && false) {
                    CLine ln1 = addLn(co.p[0], co.p[1]);
                    CLine ln2 = addLn(co.p[2], co.p[3]);
                    CLine ln3 = addLn(co.p[4], co.p[5]);
                    CLine ln4 = addLn(co.p[6], co.p[7]);
                    int[] p = get4PtsForAngle(co.p);

                    CPoint p1 = fd_point(p[0]);
                    CPoint p2 = fd_point(p[1]);
                    CAngle ang = new CAngle(ln1, ln2, p1, p2);
                    p1 = fd_point(p[2]);
                    p2 = fd_point(p[3]);
                    CAngle ang1 = new CAngle(ln3, ln4, p1, p2);

                    CAngle ta = fd_angle(ang);
                    CAngle ta1 = fd_angle(ang1);

                    if (ta == null || ta1 == null) {
                        aux_angle++;
                    }
                    ArrayList<CClass> v = new ArrayList<CClass>();

                    String ss1, ss2;
                    if (ta == null) {
                        ss1 = getAngleSimpleName();
                        ang.setColor(aux_angle);
                        ang.radius = ang.radius + 5 * aux_angle;
                        ang.ptext.setText(ss1);
                        ang.ptext.setColor(ang.getColorIndex());
                        addAngleToList(ang);
                        v.add(ang);
                    } else {
                        v.add(ta);
                        ss1 = ta.ptext.getText();
                    }
                    if (ta1 == null) {
                        ss2 = getAngleSimpleName();
                        ang1.setColor(aux_angle);
                        ang1.radius = ang1.radius + 5 * aux_angle;
                        ang1.ptext.setText(ss2);
                        ang1.ptext.setColor(ang1.getColorIndex());
                        addAngleToList(ang1);
                        v.add(ang1);
                    } else {
                        v.add(ta1);
                        ss2 = ta1.ptext.getText();
                    }
                    cpt.setMessage(ss1 + " = " + ss2);
                    flashStep(v);
                } else {
                    cond c = co.getPCO();
                }*/

            }
            break;
            case gib.CO_CTRI: {
                GEPolygon poly1 = new GEPolygon();
                aux_polygon++;
                poly1.setColor(aux_polygon + 2);
                for (int i = 0; i < 3; i++) {
                    poly1.addAPoint(fd_point(co.p[i]));
                }
                poly1.addAPoint(fd_point(co.p[0]));

                GEPolygon poly2 = new GEPolygon();
                aux_polygon++;
                poly2.setColor(aux_polygon + 2);
                for (int i = 3; i < 6; i++) {
                    poly2.addAPoint(fd_point(co.p[i]));
                }
                poly2.addAPoint(fd_point(co.p[3]));
                addGraphicEntity(polygonlist, poly1);
                addGraphicEntity(polygonlist, poly2);
                ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
                v.add(poly1);
                v.add(poly2);
                flashStep(v);
            }
            break;
            case gib.CO_CYCLIC: {
                int[] p = new int[4];
                int k = 0;
                for (int i = 0; i < 10; i++) {
                    if (co.p[i] != 0) {
                        p[k++] = co.p[i];
                    }
                }
                GEPoint p1 = fd_point(p[0]);
                GEPoint p2 = fd_point(p[1]);
                GEPoint p3 = fd_point(p[2]);
                GEPoint p4 = fd_point(p[3]);

                GECircle c = fd_circle(p[0], p[1], p[2]);
                if (c == null) {
                    GEPoint pt = CreateANewPoint(0, 0);
                    //constraint cs = new constraint(constraint.CIRCUMCENTER, pt, p1, p2, p3);
                    characteristicSetMethodAndAddPoly(false);
                    addPointToList(pt);
                    GECircle cc = new GECircle(pt, p1, p2, p3);
                    cc.add(p4);
                    addCircleToList(cc);
                }
                //proveFlash(5, p, null);
                break;
            }

        }
        UndoAdded("step");
    }

    int[] get4PtsForAngle(int[] pt) {
        int[] p = new int[4];
        boolean rt1, rt2;
        rt1 = rt2 = true;
        //int c1, c2;
        //c1 = c2 = 0;

        if (pt[0] == pt[2]) {
            p[0] = pt[1];
            p[1] = pt[3];
//            c1 = pt[0];
        } else if (pt[0] == pt[3]) {
            p[0] = pt[1];
            p[1] = pt[2];
//            c1 = pt[0];
        } else if (pt[1] == pt[2]) {
            p[0] = pt[0];
            p[1] = pt[3];
//            c1 = pt[1];

        } else if (pt[1] == pt[3]) {
            p[0] = pt[0];
            p[1] = pt[3];
//            c1 = pt[1];

        } else {
            p[0] = pt[0];
            p[1] = pt[3];
            rt1 = false;
        }

        if (pt[4] == pt[6]) {
            p[2] = pt[5];
            p[3] = pt[7];
 //           c1 = pt[4];
        } else if (pt[4] == pt[7]) {
            p[2] = pt[5];
            p[3] = pt[6];
 //           c1 = pt[4];
        } else if (pt[5] == pt[6]) {
            p[2] = pt[4];
            p[3] = pt[7];
 //           c1 = pt[5];
        } else if (pt[5] == pt[7]) {
            p[2] = pt[4];
            p[3] = pt[5];

        } else {
            p[2] = pt[4];
            p[3] = pt[7];
            rt2 = false;
        }

        if (rt1 && rt2) {
            return p;
        }

        GELine ln1 = findLineGivenTwoPoints(fd_point(pt[0]), fd_point(pt[1]));
        GELine ln2 = findLineGivenTwoPoints(fd_point(pt[2]), fd_point(pt[3]));
        GEPoint pcm = GELine.commonPoint(ln1, ln2);
        if (pcm == null) {
            return p;
        }
        int nc1 = pointlist.indexOf(pcm) + 1;

        ln1 = findLineGivenTwoPoints(fd_point(pt[4]), fd_point(pt[5]));
        ln2 = findLineGivenTwoPoints(fd_point(pt[6]), fd_point(pt[7]));
        pcm = GELine.commonPoint(ln1, ln2);
        if (pcm == null) {
            return p;
        }
        int nc2 = pointlist.indexOf(pcm) + 1;

        for (int k = 0; k <= 1; k++) {
            for (int m = 2; m <= 3; m++) {
                double ct1 = cos3Pt(pt[k], pt[m], nc1);
                for (int i = 4; i <= 5; i++) {
                    for (int j = 6; j <= 7; j++) {
                        double ct2 = cos3Pt(pt[i], pt[j], nc2);
                        if (Math.abs(ct2 - ct1) < UtilityMiscellaneous.ZERO) {
                            p[0] = pt[k];
                            p[1] = pt[m];
                            p[2] = pt[i];
                            p[3] = pt[j];
                            return p;
                        }
                    }
                }
            }
        }

        return p;
    }

    double cos3Pt(int a, int b, int c) {
        GEPoint p1 = fd_point(a);
        GEPoint p2 = fd_point(b);
        GEPoint cp = fd_point(c);
        double dc2 = Math.pow(p1.getx() - p2.getx(), 2) + Math.pow(p1.gety() - p2.gety(), 2);
        double da = Math.sqrt(Math.pow(p1.getx() - cp.getx(), 2) + Math.pow(p1.gety() - cp.gety(), 2));
        double db = Math.sqrt(Math.pow(p2.getx() - cp.getx(), 2) + Math.pow(p2.gety() - cp.gety(), 2));
        double cs = (da * da + db * db - dc2) / (2 * da * db);
        return cs;
    }

    public void addAngleToList2(GEAngle ag) {
        int num = 0;
        double[] p = GELine.Intersect(ag.lstart, ag.lend);
        if (p != null) {
            for (GEAngle ag1 : anglelist) {
                double[] pp = GELine.Intersect(ag1.lstart, ag1.lend);
                if (pp != null && Math.abs(p[0] - pp[0]) < UtilityMiscellaneous.ZERO && Math.abs(p[1] - pp[1]) < UtilityMiscellaneous.ZERO) {
                    ++num;
                }
            }
            if (num > 0) {
                ag.setRadius(15 * num + ag.getRadius());
            }
        }
        addAngleToList(ag);
    }


    Constraint findConstraint(int t, Object obj1, Object obj2) {
    	for (Constraint c : constraintlist) {
    		if (c.GetConstraintType() == t) {
    			Set<Object> s = new HashSet<Object>();
    			c.getAllElements(s);
    			if (s.contains(obj1) && s.contains(obj2))
    				return c;
    		}
    	}
    	return null;
    }

}
