package wprover;

import gprover.Prover;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class GeometryProver implements Runnable {

    DrawPanelFrame gxInstance;
    ProofPanel pprove;
    Thread main;
    private int Status = 0;
    private boolean isRunning = false;

    Timer timer = null;
    int number = 0;
    long ftime = 0;

    public GeometryProver(ProofPanel p, DrawPanelFrame fr) {
        pprove = p;
        gxInstance = fr;
    }

    public void setFix() {
        Status = 0;
    }

    public void setProve() {
        Status = 1;
    }

    public void startTimer() {
        number = 0;
        ftime = System.currentTimeMillis();

        if (timer != null) // Make sure that any previous timers are stopped (and then later automatically deleted).
        	timer.stop();
        
        timer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isRunning)
                    timer.stop(); // Stop the timer when it is still running but calculation of the proof has stopped in the meantime.
                else {
                	// Provide an update to the user showing that the proof is still being calculated.
                    double t = System.currentTimeMillis() - ftime;
                    int ft = (int) (t / 1000);
                    gxInstance.setLabelText2("Building fixpoint (" + ft + " seconds"
                            + ";  " + Prover.getNumberofProperties() + " facts)");
                }
            }
        });
        timer.setCoalesce(true);
        timer.start();
    }

    public void run() {
        isRunning = true;
        try {
            if (Status == 0) {
                long n1 = System.currentTimeMillis();
                Prover.run();
                n1 = System.currentTimeMillis() - n1;
                pprove.displayDatabase(n1);
            } else {
                boolean t = Prover.prove();
                pprove.displayGDDProve(t);
            }
        } catch (OutOfMemoryError ee) {
            if (UtilityMiscellaneous.DEBUG)
                ee.printStackTrace();
            UtilityMiscellaneous.print(ee.getMessage());
            if (gxInstance != null)
                gxInstance.setTextLabel2("The system has run out of memory!", -1);
            Prover.reset();
            isRunning = false;
            JOptionPane.showMessageDialog(gxInstance,
                    "The system has run out of memory!\n The theorem is not proven.",
                    "Not Proven", JOptionPane.WARNING_MESSAGE);

        } catch (Error ee) {
            JOptionPane.showMessageDialog(gxInstance,
                    "The theorem is not proven.\n" + ee.getMessage(),
                    "Not Proven", JOptionPane.WARNING_MESSAGE);
            Prover.reset();

        } catch (Exception ee) {
            JOptionPane.showMessageDialog(gxInstance,
                    "The theorem is not proven.\n" + ee.getMessage(),
                    "Not Proven", JOptionPane.WARNING_MESSAGE);
            Prover.reset();
        }
        if (gxInstance != null)
            gxInstance.stopTimer();

        isRunning = false;
    }

    public void start() {
        if (!isRunning) {
	        main = new Thread(this, "Prover");
	        main.start();
	        startTimer();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}

