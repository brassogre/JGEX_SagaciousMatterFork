/**
 * 
 */
package wprover;

import javax.swing.JFrame;

import wprover.GExpert;

/**
 * @author kutach
 *
 */
public class JGEXapp {

	/**
	 * @param args
	 */
	private static GExpert mainJFrame;
	
	public static void main(String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
    	System.setProperty("com.apple.mrj.application.apple.menu.about.name", "JGEX");
    	
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
                createAndShowGUI();
            }
        });
    }
 
    private static void createAndShowGUI() {
        //Create and set up the window.
    	mainJFrame = new GExpert();
    	mainJFrame.init();
    	mainJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
    	mainJFrame.pack();
    	mainJFrame.setVisible(true);
    }
    
}
