package wprover;

import com.apple.eawt.*;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.Application;

public class AppleUI {
	private static GExpert gexpert = null;
	private Application app = null;
	private AppleUIHandlers handlers = null;
	public AppleUI(GExpert ge) {
		gexpert = ge;
		app = Application.getApplication();
		handlers = new AppleUIHandlers();
		app.setAboutHandler(handlers);
		app.setQuitHandler(handlers);
		app.setPreferencesHandler(handlers);
		//app.setDockIconImage(icon.getImage());
	}

	public class AppleUIHandlers implements PreferencesHandler, AboutHandler, QuitHandler {
		@Override
		public void handlePreferences(PreferencesEvent e) {
			assert(gexpert != null);
			gexpert.handlePreferences();
		}
		   
		@Override
		public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
			response.performQuit();	
		}

		@Override
		public void handleAbout(AboutEvent e) {
			assert(gexpert != null);
			gexpert.handleAbout();
		}		
	}
}
