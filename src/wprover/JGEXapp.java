/**
 * 
 */
package wprover;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import wprover.DrawPanelFrame;

/**
 * @author kutach
 *
 */
public class JGEXapp {

	/**
	 * @param args
	 */
	private static DrawPanelFrame mainJFrame;
	
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
    	mainJFrame = new DrawPanelFrame();
    	mainJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mainJFrame.init();

        //Display the window.
    	mainJFrame.pack();
    	mainJFrame.setVisible(true);
    }
    
	public static String retrieveToolTipResource(final String sName) {
		return sName;
	}
	
	public static ImageIcon retrieveIconResource(final String sName) {
		final String imgLocation = "images/" + sName + ".gif";
		final URL imageURL = DrawPanelFrame.class.getResource(imgLocation);
		String altText = "alt";
		return new ImageIcon(imageURL, altText);
	}
	


}
