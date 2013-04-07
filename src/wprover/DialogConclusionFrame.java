package wprover;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import maths.*;

public class DialogConclusionFrame extends DialogBase implements  ActionListener, WindowListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3833808194284220827L;
    private static CharacteristicSetMethod charset = CharacteristicSetMethod.getinstance();
    PanelDraw d;

    DrawPanel dp;

    JPanel pinf;

    JLabel label;
    JButton pbutton = new JButton("Prove");

    public DialogConclusionFrame(DrawPanel dp) {

        this.dp = dp;
        this.setTitle("Conclusion");

        Dimension VD = new Dimension(15, 40);
        Dimension HD = new Dimension(40, 15);


        JPanel panelAll = new JPanel();
        panelAll.setLayout(new BoxLayout(panelAll, BoxLayout.X_AXIS));

        panelAll.add(Box.createRigidArea(VD));

        JPanel innerPanel = new JPanel() {
	    /**
			 * 
			 */
			private static final long serialVersionUID = 1715400632932737080L;

		@Override
            public Dimension getMaximumSize() {
                return new Dimension(getPreferredSize().width, super.getMaximumSize().height);
            }
        };
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        panelAll.add(innerPanel);
        panelAll.add(Box.createRigidArea(VD));

        label = new JLabel();
        innerPanel.add(Box.createRigidArea(new Dimension(40, 30)));
        innerPanel.add(new JLabel("Conclusion: "));
        innerPanel.add(label);
        innerPanel.add(Box.createRigidArea(HD));
        innerPanel.add(pbutton);
        innerPanel.add(Box.createRigidArea(HD));


        this.setSize(800, 600);
        pinf = new JPanel();
        pinf.setSize(200, 400);
        pbutton.addActionListener(this);

        getContentPane().add(panelAll, BorderLayout.LINE_START);
        panelAll.add(pinf);
        this.setLocation(300, 200);
        this.addWindowListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
    }


    @Override
    public void windowClosed(WindowEvent e) {
    }

    ;

    @Override
    public void windowIconified(WindowEvent e) {
    }

    ;

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    ;

    @Override
    public void windowActivated(WindowEvent e) {

    }

    ;

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    ;

    @Override
    public void windowClosing(WindowEvent e) {


    }

    ;

    @Override
    public void windowOpened(WindowEvent e) {
    }

    ;

    private void prove(int type) {
        ArrayList<GraphicEntity> vv = new ArrayList<GraphicEntity>();
        dp.getSelectList(vv);
        TPoly plist = null;

        Object[] obj = new Object[4];

        for (int i = 0; i < 4; i++)
            obj[i] = null;

        for (int i = 0; i < vv.size(); i++)
            obj[i] = vv.get(i);

        TPoly pclist = dp.getCopyPolyBacklist();


        pclist = PolyBasic.OptimizePoly(pclist);
        plist = PolyBasic.OptimizePoly(plist);
        pclist = charset.charset(pclist);
        pclist = CharacteristicSetMethod.reverse(pclist);


        TMono tp = plist.getPoly();
        int i = 0;
        TPoly tl = pclist;
        while (tl != null) {
            i++;
            tl = tl.getNext();
        }
        int n = i;
        StyledDocument doc = null;//field.getStyledDocument();
        int result = -1;

        try {

            doc.insertString(doc.getLength(), "Step 1 convert the geometric conditions into polynomial form: \n" + dp.getPolyString(0), doc.getStyle("small"));

            doc.insertString(doc.getLength(), "The conclusion polynomial is: \nc = " + PolyBasic.printSPoly(tp) + "\n", doc.getStyle("small"));

            doc.insertString(doc.getLength(), "Step 2 Transform the hypothesis polynomial set into triangular form:\n"

                    + dp.getPolyString(1), doc.getStyle("small"));

            doc.insertString(doc.getLength(), "\nStep 3 Successive Pseudo Division:\n"
                    + "Let R_" + n + " = c (the conclusion polynomial)\n"
                    , doc.getStyle("small"));
        } catch (BadLocationException ble) {

            System.err.println("Could not insert initial text into text pane.");
        }

        try {
            long time = System.currentTimeMillis();

            while (pclist != null) {
                // CMisc.print("step");
                // poly.print(tp);
                // poly.print(pclist.getPoly());
                tp = PolyBasic.prem(tp, pclist.getPoly());
                long t = System.currentTimeMillis() - time;
                doc.insertString(doc.getLength(), "Length of R_" + (i - 1) + " = prem(R_" + i + ", f_" + (i - 1) + ") =  " + PolyBasic.plength(tp)
                        + "\t\ttime = " + (float) t / 1000 + '\n', doc.getStyle("small"));
                i--;
                pclist = pclist.getNext();

            }

            time = System.currentTimeMillis() - time;

            result = PolyBasic.plength(tp);
            doc.insertString(doc.getLength(), "\nStep 4 check the final remainder:", doc.getStyle("small"));

            if (result == 0)
                doc.insertString(doc.getLength(),
                        "Since the final remainder R0 is zero, and we have"
                                + "\n   the following remainder formula for "
                                + "successive pseudo division:\n"
                                + "\n     "
                                + "I0^s0 I1^s1 ... I" + n + "^s"
                                + n + " c = Q0 f0 + Q1 f1 + ... + Q"
                                + n + " f" + n + " + R0\n"
                                + "\nthe conclusion polynomial c must be 0 "
                                + "if all the I_i are not zero."
                                + "\nThe " + " theorem is proved under non-degenerate conditions "
                                + "\n\n     I_0 != 0, ..., I_" + n + " != 0\n\n"
                                + "where I_i is the leading coefficient of  f_i."
                                + "used time =  " + (double) time / 1000 + " seconds \n"
                                + "\n\nFor an elementary exposition of the underlying"
                                + " theory see Chou's 1984 paper included in this CD "
                                + "distribution.\n Also see Chou and Gao's paper "
                                + "'A Class of Geometry Statements of Constructive "
                                + "Type and Geometry Theorem Proving', "
                                + "TR-89-37, CS department, UT, Austin, 1989. QED.\n"
                                + "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n",

                        doc.getStyle("small"));
            else {

                doc.insertString(doc.getLength(), "since the final remainder R_" + i + " is not zero,\n"
                        + "the statement is not confirmed\n", doc.getStyle("small"));
                doc.insertString(doc.getLength(), "The remaining polynomial is: \nc = " + PolyBasic.printSPoly(tp) + "\n", doc.getStyle("small"));

            }
        } catch (BadLocationException ble) {

            System.err.println("Could not insert initial text into text pane.");
        }

    }

    public void runc(final int type) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	    @Override
            public void run() {
                {
                    prove(type);
                }
            }
        });
    }
}

