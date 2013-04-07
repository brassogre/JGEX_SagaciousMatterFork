package wprover;

import gprover.jgex_IO;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FontUIResource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.*;

import pdf.PDFJob;
import UI.*;

//import javax.swing.WindowConstants;

public class DrawPanelFrame extends JFrame implements ActionListener, KeyListener,
		DropTargetListener, WindowListener { // APPLET ONLY.

	/**
	 * 
	 */
	private static final long serialVersionUID = 8632391650264913249L;
	private JLabel label;
	private JLabel label2;
	private JPanel tipanel;
	private final ArrayList<OPoolabel> vpoolist = new ArrayList<OPoolabel>();
	private JToggleButton show_button;

	private static Language language;
	public static long HotKeyTimer = 0;

	private final Group drawgroup = new Group();
	private final Group menugroup = new Group();

	private JToggleButton buttonMove, buttonSelect;

	public PanelDraw d;
	public DrawPanelExtended dp;
	public PanelProperty cp;
	public ListTree lp;

	private ToolBarAnimate aframe;
	private FloatableToolBar afpane;

	private DialogProperty propt = null;
	private DialogSelectGraphicEntity sdialog = null;
	private DialogUndoEdit udialog = null;
	private DialogProof pdialog = null;
	private DialogConclusion cdialog = null;
	private DialogRule rdialog = null;
	private DialogNumericalCheck ndialog = null;
	private PopupMenuAbout adialog = null;
	private JDialog prefdialog = null;

	public JScrollPane scroll;
	private ToolBarProof provePanelbar;
	private ToolBarStyle styleDialog;
	// private JPopExView rview;
	private JToggleButton anButton;
	private PanelMProofInput inputm;

	private ProofPanel pprove;
	private JPanel ppanel;
	private JSplitPane contentPane;
	private JFileChooser filechooser;

	private JToggleButton BK1, BK2, BK3, BK4;
	private final ArrayList<ImageIcon> iconPool = new ArrayList<ImageIcon>();
	public String _command;

	public DrawPanelFrame() {
		super("Java Geometry Expert"); // GAPPLET.
	}

	public void init() {
		showWelcome();
		UtilityMiscellaneous.Reset();
		UtilityMiscellaneous.initFont();
		setLocal();
		setLookAndFeel();
		setIconImage(DrawPanelFrame.createImageIcon("images/gexicon.gif").getImage());
		//loadRules();
		loadPreference();
		loadLanguage();
		initAttribute();
		initKeyMap();

		setDefaultLookAndFeelDecorated(true);
		if (UtilityOSValidator.isMac())
			new UtilityAppleUI(this);

		d = new PanelDraw(this);
		dp = d.dp;
		dp.setCurrentDrawPanel(d);
		dp.setLanguage(language);

		createTipLabel();

		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		updateTitle();

		scroll = new JScrollPane(d,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(null);
		scroll.setAutoscrolls(true);
		panel.add(scroll);

		pprove = new ProofPanel(this, d, dp, true, -1);
		inputm = pprove.getmInputPanel();
		ppanel = new JPanel();
		ppanel.setLayout(new BoxLayout(ppanel, BoxLayout.Y_AXIS));
		final JToolBar ptoolbar = pprove.createToolBar();
		ppanel.add(ptoolbar);
		ppanel.add(pprove);
		ppanel.setBorder(null);
		contentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ppanel, panel);
		contentPane.setContinuousLayout(true);
		addSplitListener();

		provePanelbar = new ToolBarProof(this);
		styleDialog = new ToolBarStyle(this, d);
		addMenuToolBar();
		loadCursor();
		new DropTarget(this, this);
		addWindowListener(this);

		getContentPane().add(contentPane, BorderLayout.CENTER);

		// Display the window.
		pack();
		setVisible(true);
	}

	void handleAbout() {
		if (adialog == null)
			adialog = new PopupMenuAbout();
		adialog.initLocation(this);
		adialog.setVisible(true);
	}

	void handlePreferences() {
		if (prefdialog == null)
			prefdialog = new DialogMiscellaneous(this);
		centerDialog(prefdialog);
		prefdialog.setVisible(true);
	}

	public JComponent getContent() {
		return contentPane;
	}

	public void addSplitListener() {
		d.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(final ComponentEvent e) {
				if ((provePanelbar != null) && provePanelbar.isVisible())
					provePanelbar.movetoDxy(0, 0);
			}

			@Override
			public void componentMoved(final ComponentEvent e) {
			}

			@Override
			public void componentShown(final ComponentEvent e) {
			}

			@Override
			public void componentHidden(final ComponentEvent e) {
			}
		});
	}

	public static void loadPreference() {
		if (UtilityMiscellaneous.isApplet())
			return;

		final String u = getUserDir();
		try {
			final InputStreamReader read = new InputStreamReader(
					new FileInputStream(u + "/Property.x"), "UTF-8");// ����UNICODE,UTF-16
			@SuppressWarnings("resource")
			final BufferedReader reader = new BufferedReader(read);
			UtilityMiscellaneous.LoadProperty(reader);
		} catch (final IOException ee) {
			// CMisc.print(ee.getMessage());
		}

	}

	public static void loadRules() {
		//RuleList.loadRules();
		//gib.initRules();
	}

	public void toggleButton(final boolean bEnabled) {
		anButton.setSelected(bEnabled);
	}

	public static void loadLanguage() {
		if (UtilityMiscellaneous.isApplet())
			return;

		language = new Language();
		final String user_directory = getUserDir();
		final File f = new File(user_directory + "/language/" + UtilityMiscellaneous.lan
				+ ".lan");
		language.load(f);
		Language.setLanguage(language);
	}

	public static void initAttribute() {
		if (UtilityMiscellaneous.isApplet())
			return;

		if ((language != null) && !language.isEnglish()) {
			final Font f = language.getFont();

			if (f != null) {
				setUIFont(new FontUIResource(f));
				UtilityMiscellaneous.setFont(f.getName());
			}
		}
	}

	public static void setLocal() {
		Locale.setDefault(Locale.ENGLISH);
		if (language != null)
			language.setLocal();
		// Locale l = new Locale("en", "US");
		// this.setLocale(l);
	}

	/**
	 * Loads and displays a splash screen at initialization of program.
	 * Currently unimplemented.
	 */
	public void showWelcome() {
	}

	public static Font getDefaultFont() {
		if (language == null)
			return null;
		return language.getFont();
	}

	public Frame getFrame() {
		Container c = this;
		while (c != null) {
			if (c instanceof Frame)
				return (Frame) c;
			c = c.getParent();
		}
		return null;
	}

	public void loadCursor() {
	}

	public void createCursor(final Toolkit kit, final String file,
			final String name) {
	}

	// public static Cursor getDefinedCursor(String name) {
	// return null;
	// }

	public boolean isPPanelVisible() {
		return ppanel.isVisible();
	}

	public void showppanel(final boolean t) {
		show_button.setSelected(t);
		ppanel.setVisible(!t);
		contentPane.resetToPreferredSizes();
		// if (t == false) {
		// Dimension dm = contentPane.getMinimumSize();
		// contentPane.setSize(dm);
		// }
	}

	public JSplitPane getSplitContentPane() {
		return contentPane;
	}

	// public void showRulePanel(String s, int x, int y) {
	// if (rview == null) {
	// rview = new JPopExView(this);
	// }
	// if (rview.loadRule(s)) {
	// rview.setLocationRelativeTo(d);
	// rview.setLocation(x, y);
	// rview.setVisible(true);
	// }
	// }

	public ProofPanel getpprove() {
		return pprove;
	}

	public boolean hasAFrame() {
		return aframe != null;
	}

	public ToolBarAnimate getAnimateDialog() {
		if (aframe == null)
			aframe = new ToolBarAnimate(this, d, dp);
		return aframe;
	}

	public boolean isAframeShown() {
		return afpane.isVisible() && (aframe != null) && aframe.isVisible();
	}

	public void showAnimatePane() {

		if (aframe == null)
			getAnimateDialog();

		final Rectangle rc = scroll.getVisibleRect();
		if (afpane == null)
			afpane = new FloatableToolBar();
		aframe.setEnableAll();
		afpane.add(aframe);

		final Dimension dm = afpane.getPreferredSize();
		final int w = (int) dm.getWidth();
		final int h = (int) dm.getHeight();

		afpane.show(d, (int) rc.getWidth() - w, (int) rc.getHeight() - h);
		aframe.repaint();
	}

	public DialogRule getRulesDialog(final int n) {
//		if (rdialog == null) {
			rdialog = new DialogRule(this);
			final int w = rdialog.getWidth();
			int x = getX() - w;
			if (x < 0)
				x = 0;
			rdialog.setLocation(x, getY());
//		}
		rdialog.setSelected(n);
		rdialog.setVisible(true);
		return rdialog;
	}

	public void viewElementsAuto(final GraphicEntity c) {
		if ((c != null) && (propt != null) && (cp != null))
			cp.SetPanelType(c);
	}

	public DialogProperty getDialogProperty() {
		if (propt == null) {
			cp = new PanelProperty(d, language);
			propt = new DialogProperty(this, cp);
			propt.getContentPane().add(cp);
			propt.setVisible(false);
			propt.setTitle(getLanguage("Property"));
			centerDialog(propt);
		}
		return propt;
	}

	public void centerDialog(final JDialog dlg) {
		dlg.setLocation((getX() + (getWidth() / 2)) - (dlg.getWidth() / 2),
				(getY() + (getHeight() / 2)) - (dlg.getHeight() / 2));
	}

	public DialogSelectGraphicEntity getSelectDialog() {
		if (sdialog == null)
			sdialog = new DialogSelectGraphicEntity(this, new ArrayList<GraphicEntity>());
		return sdialog;
	}

	public boolean isConclusionDialogVisible() {
		return ((cdialog != null) && cdialog.isVisible());
	}

	public DialogConclusion getConclusionDialog() {
		if (cdialog == null) {
			cdialog = new DialogConclusion(this, "");
			centerDialog(cdialog);
		}
		return cdialog;
	}

	public void showNumDialog() {
		if (ndialog == null) {
			ndialog = new DialogNumericalCheck(this);
			centerDialog(ndialog);
		}
		ndialog.setVisible(true);
	}

	public void selectAPoint(final GEPoint p) {
		if ((ndialog != null) && ndialog.isVisible())
			ndialog.addSelectPoint(p);
	}

	public DialogUndoEdit getUndoEditDialog() {
		if (udialog == null) {
			udialog = new DialogUndoEdit(this);
			lp = udialog.getTreePanel();
		}
		return udialog;
	}

	public boolean isDialogProveVisible() {
		return (pdialog != null) && pdialog.isVisible();
	}

	public DialogProof getDialogProve() {
		if (pdialog == null)
			pdialog = new DialogProof(this);
		return pdialog;
	}

	public JFileChooser getFileChooser() {
		if (filechooser == null) {
			filechooser = new JFileChooser();
			final String dr = getUserDir();
			filechooser.setCurrentDirectory(new File(dr));
			filechooser.setFileFilter(new UtilityFileFilter("gex.xml"));
		}
		filechooser.setSelectedFile(null);
		filechooser.setSelectedFiles(null);
		return filechooser;
	}

	public static String getUserDir() {
		return System.getProperty("user.dir");
	}

	public static String getFileSeparator() {
		return System.getProperty("file.separator");
	}

	public boolean hasManualInputBar() {
		return inputm != null;
	}

	public int getProveStatus() {
		return pprove.getSelectedIndex();
	}

	public ToolBarStyle getStyleDialog() {
		return styleDialog;
	}

	public PanelMProofInput getMannalInputToolBar() {
		return inputm;
	}

	public void addButtonToDrawGroup(final JToggleButton b) {
		drawgroup.add(b);
	}

	public ToolBarProof getPProveBar() {
		return provePanelbar;
	}

	// public void switchProveBarVisibility(boolean r) {
	// if (r == false) {
	// if (provePanelbar == null) {
	// return;
	// }
	// showProveBar(false);
	// } else {
	// showProveBar(true);
	// }
	// }

	public void showProveBar(final boolean show) {
		if (provePanelbar == null)
			provePanelbar = new ToolBarProof(this);
		if (show) {
			final Dimension dm = provePanelbar.getPreferredSize();
			dm.getWidth();
			final int h = (int) dm.getHeight();
			final Rectangle rc = scroll.getVisibleRect();
			provePanelbar.show(d, 0, (int) rc.getHeight() - h);
			provePanelbar.repaint();

			provePanelbar.setValue(-1);
		} else
			provePanelbar.setVisible(false);
	}

	public void showStyleDialog() {
		if (styleDialog == null)
			styleDialog = new ToolBarStyle(this, d);

		final Dimension dm = styleDialog.getPreferredSize();
		dm.getWidth();
		dm.getHeight();
		scroll.getVisibleRect();
		styleDialog.show(d, 0, 0);
		styleDialog.repaint();
	}

	public void createTipLabel() {

		label = new JLabel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -183878556188915124L;

			@Override
			public Dimension getPreferredSize() {
				final Dimension dm = new Dimension(210, 20);
				return dm;
			}

			@Override
			public Dimension getMaximumSize() {
				final Dimension dm = new Dimension(500, 20);
				return dm;
			}
		};

		label2 = new JLabel("") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 4417263947536195720L;

			@Override
			public Dimension getMaximumSize() {
				final Dimension dm = new Dimension(Integer.MAX_VALUE, 20);
				return dm;
			}
		};
		final Font f = UtilityMiscellaneous.button_label_font;
		label2.setFont(f);
		label.setFont(f);

		final GBevelBorder border = new GBevelBorder(BevelBorder.RAISED, 1);

		label.setBorder(border); // BorderFactory.createBevelBorder(BevelBorder.RAISED));
		label2.setBorder(border); // BorderFactory.createBevelBorder(BevelBorder.RAISED));

		final JPanel panel = new JPanel();
		panel.setBorder(null);
		panel.setBackground(UtilityMiscellaneous.frameColor);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		tipanel = panel;

		show_button = new TStateButton(
				DrawPanelFrame.createImageIcon("images/ticon/show.gif"),
				DrawPanelFrame.createImageIcon("images/ticon/hide.gif"));

		show_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final JToggleButton b = (JToggleButton) e.getSource();
				showppanel(b.isSelected());
			}
		});

		final EntityButtonUI ui = new EntityButtonUI();
		show_button.setUI(ui);
		panel.add(show_button);
		panel.add(label);
		panel.add(label2);
		panel.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
		getContentPane().add("South", panel);

	}

	@Override
	public void setLocation(final int x, final int y) {
		super.setLocation(x, y);
	}

	void addDirectoryIcons(final File f) {
		if (f.isDirectory()) {
			final File contents[] = f.listFiles();
			for (final File content : contents)
				if (!content.isDirectory())
					iconPool.add(DrawPanelFrame.createImageIcon(content.getPath()));
				else
					addDirectoryIcons(content);
		}
	}

	void addAllExamples(final JMenu menu) {
		// final String user_directory = getUserDir();
		// final String sp = GExpert.getFileSeparator();
		// final String dr = user_directory + sp + "examples";
		// final File dir = new File(dr);
		// this.addAllExamplesInDirectoryToMenu(dir, menu, dr);
	}

	// public URL getDocumentBase() {
	// if (CMisc.isApplet()) {
	// Object o = (Object)this;
	// JApplet a = (JApplet)o;
	// return a.getDocumentBase();
	// }
	// return null;
	// }

	void addAllOnLineExamples(final JMenu menu) { // APPLET ONLY

		// if (!CMisc.isApplet()) {
		// return;
		// }
		//
		// try {
		// final Object o = this;
		// final JApplet a = (JApplet) o;
		// final URL base = a.getDocumentBase();
		//
		// final URL ul = new URL(base, "example_list.txt");
		// final URLConnection urlc = ul.openConnection();
		// urlc.connect();
		// final InputStream instream = urlc.getInputStream();
		// @SuppressWarnings("resource")
		// final BufferedReader br = new BufferedReader(new
		// InputStreamReader(instream));
		// br.readLine();
		// this.addDirectory(br, menu, new String(""));
		// } catch (final IOException e) {
		// CMisc.eprint(this, "Error in read example list.");
		// }
	}

	void addAllExamplesInDirectoryToMenu(final File f, final JMenu menu,
			final String path) {

		// final String sp = GExpert.getFileSeparator();
		//
		// if (f.isDirectory()) {
		// final File contents[] = f.listFiles();
		// final int n = contents.length - 1;
		// for (int i = n; i >= 0; --i) {
		// if (!contents[i].isDirectory()) {
		// final String s = contents[i].getName();
		// String t = s;
		//
		// if (s.endsWith(".gex.xml")) {
		// final int size = s.length();
		// t = s.substring(0, size - 8);
		// }
		// final JMenuItem mt = new JMenuItem(t);
		// mt.setToolTipText(s);
		// mt.setName(path);
		// mt.setActionCommand("example");
		// mt.addActionListener(this);
		// addMenuItemInAlphabeticOrder(menu, mt);
		// }
		// }
		//
		// for (final File content : contents) {
		// final String s = content.getName();
		// if (content.isDirectory()) {
		// final JMenu m = new JMenu(s);
		// addAllExamplesInDirectoryToMenu(content, m, path + sp + s);
		// menu.add(m);
		// }
		// }
		// }
	}

	void addDirectory(final BufferedReader bf, final JMenu menu,
			final String path) {
		// throws IOException {

		// void addDirectory(File f, JMenu menu, String path) {
		//
		// String sp = GExpert.getFileSeparator();
		//
		// if (f.isDirectory()) {
		// File contents[] = f.listFiles();
		// int n = contents.length - 1;
		// for (int i = n; i >= 0; i--) {
		// if (contents[i].isDirectory()) {
		// continue;
		// }
		// final String s = contents[i].getName();
		// String t = s;
		// if (s.endsWith(".gex")) {
		// final int size = s.length();
		// t = s.substring(0, size - 4);
		// File gexfile = new File(f, s);
		// if (openAFile(gexfile)) {
		// String sFilename =
		// "/Users/kutach/Documents/Workspace/JGEX_SagaciousMatterFork/xml_examples/"+s+".gex.xml";
		// saveAsXML(sFilename);
		// }
		// gexfile.delete();
		// }
		//
		//
		// JMenuItem mt = new JMenuItem(t);
		// mt.setToolTipText(s);
		// mt.setName(path);
		// mt.setActionCommand("example");
		// mt.addActionListener(this);
		// addMenu(menu, mt);
		// }
		//
		// for (int i = 0; i < contents.length; i++) {
		// String s = contents[i].getName();
		// String t = s;
		// if (contents[i].isDirectory()) {
		// JMenu m = new JMenu(s);
		// this.addDirectory(contents[i], m, path + sp + s);
		// menu.add(m);
		// }
		// }
		//
		// }
		//
		// }

		// String s1 = bf.readLine();
		// s1.trim();
		//
		// while (s1.length() != 0) {
		// if ((s1.length() == 1) && (s1.charAt(0) == '(')) {
		// s1 = bf.readLine();
		// s1.trim();
		// final JMenu m = new JMenu(s1);
		// addMenuItemInAlphabeticOrder(menu, m);
		// if (path.length() == 0) {
		// addDirectory(bf, m, s1);
		// } else {
		// addDirectory(bf, m, path + "/" + s1);
		// }
		// } else if ((s1.length() == 1) && (s1.charAt(0) == ')')) {
		// return;
		// } else {
		// if (s1.endsWith(".gex")) {
		// s1 = s1.substring(0, s1.length() - 4);
		// final JMenuItem mt = new JMenuItem(s1);
		// mt.setName(path);
		// mt.setToolTipText(s1);
		// mt.setActionCommand("example");
		// mt.addActionListener(this);
		// addMenuItemInAlphabeticOrder(menu, mt);
		// }
		//
		// }
		// s1 = bf.readLine();
		//
		// if (s1 != null) {
		// s1.trim();
		// } else {
		// return;
		// }
		// }
	}

	/**
	 * Adds the specified <code>JMenuItem</code> to the specified
	 * <code>JMenu</code> in alphabetical order.
	 * 
	 * @param item
	 *            the <code>JMenuItem</code> to add
	 * @param menu
	 *            the <code>JMenu</code> to receive item
	 */
	static void addMenuItemInAlphabeticOrder(final JMenu menu,
			final JMenuItem item) {
		final String name = item.getText();
		for (int i = 0; i < menu.getItemCount(); i++) {
			final JMenuItem m = menu.getItem(i);
			if ((m != null) && (name.compareTo(m.getText()) < 0)) {
				menu.add(item, i);
				return;
			}
		}
		menu.add(item, -1);
	}

	public void addMenuToolBar() {
		final JToolBar toolBar = new JToolBar("Toolbar");
		toolBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		toolBar.setBackground(UtilityMiscellaneous.frameColor);
		toolBar.setOpaque(false);
		toolBar.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		addButtons(toolBar);

		toolBar.setFloatable(false);
		final JToolBar toolBarRight = new JToolBar("Toolbar",
				SwingConstants.VERTICAL);
		toolBarRight.setBorder(new GBevelBorder(BevelBorder.RAISED, 1));// (EtchedBorder.LOWERED));
		toolBarRight.setBackground(UtilityMiscellaneous.frameColor);
		addRightButtons(toolBarRight);
		toolBarRight.setFloatable(false);

		final JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menu = new JMenu(getLanguage("File"));
		menuBar.add(menu);

		addAMenu(menu, "New", KeyEvent.VK_N, null, "new");

		menu.addSeparator();

		addAMenu(menu, "Open", KeyEvent.VK_O, null, "open");

		menu.addSeparator();

		addAMenu(menu, "Save", KeyEvent.VK_S, null, "save");
		addAMenu(menu, "Save as...", null, null, "saveas");
		// addAMenu(menu, "Save as Text", KeyEvent.VK_T, null, null);

		addAMenu(menu, "Save as PS", null, null, null);
		addAMenu(menu, "Save as PDF", null, null, null);
		// addAMenu(menu, "Save as XML", null, null, null);

		addAMenu(menu, "Save as Image", null, null, "image");
		addAMenu(menu, "Save as Animated Image", null, null, null);
		addAMenu(menu, "Save Proof as Animated Image", null, null, null);

		menu.addSeparator();
		addAMenu(menu, "Print", KeyEvent.VK_P, "Print all visible geometry",
				"print");

		if (!UtilityOSValidator.isMac()) {
			menu.addSeparator();
			addAMenu(menu, "Exit", KeyEvent.VK_X, null, null);
		}

		menu = new JMenu(getLanguage("Examples"));
		menuBar.add(menu);

		// if (CMisc.isApplet()) {
		// addAllOnLineExamples(menu); // APPLET ONLY.
		// } else {
		// addAllExamples(menu);
		// }

		menu = new JMenu("Edit");
		menuBar.add(menu);
		addAMenu(menu, "Redo", KeyEvent.VK_Y, "Redo", null);
		addAMenu(menu, "Undo", KeyEvent.VK_Z, "Undo", null);
		// addAMenu(menu,"Forward to End","Undo a step",this);
		// addAMenu(menu,"Back to Begin","Undo a step",this);
		menu.addSeparator();
		addAMenu(menu, "Translate", null, "Translate View", null);
		addAMenu(menu, "Zoom-in", null, "Zoom in", null);
		addAMenu(menu, "Zoom-out", null, "Zoom out", null);

		menu = new JMenu(getLanguage("Construct"));
		menuBar.add(menu);
		addRadioButtonMenuItem(
				menu,
				"Point by Point and Segment",
				null,
				"Click three points A B C, then click a point D on an object AB = CD",
				null);
		addRadioButtonMenuItem(menu, "Radical of Two Circles", null,
				"Click two circles to construct their radical axis", null);
		menu.addSeparator();

		addRadioButtonMenuItem(menu, "Oriented Segment", null,
				"Click two points A B then  a point C to get AB //= CD", null);
		addRadioButtonMenuItem(
				menu,
				"Oriented T Segment",
				null,
				"Click two points A B then point C to get CD equal and perpendicular to +- AB",
				"o_t_segment");

		final JMenu s1 = new JMenu(getLanguage("Oriented Segment * Ratio"));

		addRadioButtonMenuItem(s1, "1 : 2", null, "Oriented Segment", null);
		addRadioButtonMenuItem(s1, "2 : 1", null, "Oriented Segment", null);
		addRadioButtonMenuItem(s1, "Other...", null, "Input your own ratio",
				"Oriented Segment");
		menu.add(s1);

		final JMenu s2 = new JMenu(getLanguage("Oriented T Segment * Ratio"));
		addRadioButtonMenuItem(s2, "1 : 2", null, null, "o_t_segment");
		addRadioButtonMenuItem(s2, "2 : 1", null, null, "o_t_segment");
		addRadioButtonMenuItem(s2, "Other...", null, "Input your own ratio",
				"o_t_segment");
		menu.add(s2);

		menu.addSeparator();
		JMenu sub = new JMenu(getLanguage("Proportional Segment"));
		// addRadioButtonMenuItem(sub, "1 : -1",
		// "Click two points to get a point with ratio 1:1", this, "propline");
		addRadioButtonMenuItem(sub, "1 : 1", null,
				"Click two points to get a point with ratio 1:1", "propline");
		addRadioButtonMenuItem(sub, "1 : 2", null,
				"Click two points to get a point with ratio 1:2", "propline");
		addRadioButtonMenuItem(sub, "1 : 3", null,
				"Click two points to get a point with ratio 1:3", "propline");
		addRadioButtonMenuItem(sub, "1 : 4", null,
				"Click two points to get a point with ratio 1:4", "propline");
		addRadioButtonMenuItem(sub, "1 : 5", null,
				"Click two points to get a point with ratio 1:5", "propline");
		addRadioButtonMenuItem(sub, "Other...", null, "Input your own ratio",
				"propline");
		menu.add(sub);
		menu.addSeparator();
		sub = new JMenu(getLanguage("Point"));
		addRadioButtonMenuItem(sub, "Point", null, "Add a single point", null);
		addRadioButtonMenuItem(sub, "Midpoint", null,
				"Construct midpoint via 2 points", null);
		sub.addSeparator();
		addRadioButtonMenuItem(sub, "Circumcenter", null,
				"Construct circumcenter via 3 points", null);
		addRadioButtonMenuItem(sub, "Centroid", null,
				"Construct centroid via 3 points", null);
		addRadioButtonMenuItem(sub, "Orthocenter", null,
				"Construct orthocenter via 3 points", null);
		addRadioButtonMenuItem(sub, "Incenter", null,
				"Construct incenter via 3 points", null);
		sub.addSeparator();
		addRadioButtonMenuItem(sub, "Foot", null,
				"Click a point then drag to a line to construct the foot", null);
		menu.add(sub);

		sub = new JMenu(getLanguage("Line"));
		addRadioButtonMenuItem(sub, "Line", null,
				"Draw a lines by connecting two points", null);
		addRadioButtonMenuItem(sub, "Parallel", null,
				"Draw a line which is parallel to another line", null);
		addRadioButtonMenuItem(sub, "Perpendicular", null,
				"Draw a line which is perpendicular to another line", null);
		addRadioButtonMenuItem(sub, "Angle Bisector", null,
				"Construct an angle bisector", null);
		addRadioButtonMenuItem(sub, "Aline", null, "Draw Aline", null);
		addRadioButtonMenuItem(sub, "Perpendicular Bisector", null,
				"Construct a perpendicular bisector of another line", null);
		addRadioButtonMenuItem(sub, "Tangent to Circle", null,
				"Draw line which is tangent to a circle", null);
		menu.add(sub);

		sub = new JMenu(getLanguage("Circle"));
		addRadioButtonMenuItem(sub, "Circle", null,
				"Draw a circle by a center point and a point on circle", null);
		addRadioButtonMenuItem(sub, "Circle by Three Points", null,
				"Draw a circle via 3 points", null);
		addRadioButtonMenuItem(sub, "Circle by Radius", null,
				"Draw a circle with center and radius", null);
		menu.add(sub);

		sub = new JMenu(getLanguage("Action"));
		addRadioButtonMenuItem(sub, "Intersect", null, "Intersect 2 lines",
				null);
		addRadioButtonMenuItem(sub, "Mirror", null,
				"Mirror an element with respect to a line or a point", null);
		menu.add(sub);

		sub = new JMenu(getLanguage("Polygon"));
		addRadioButtonMenuItem(sub, "Triangle", null, "Draw a triangle",
				"triangle");
		addRadioButtonMenuItem(sub, "Isosceles Triangle", null,
				"Draw a isosceles triangle", "isosceles triangle");
		addRadioButtonMenuItem(sub, "Equilateral Triangle", null,
				"Draw a equilateral triangle", "equilateral triangle");
		addRadioButtonMenuItem(sub, "Right-angled Triangle", null,
				"Draw a right triangle", "Tri_perp");
		addRadioButtonMenuItem(sub, "Isosceles Right-angled Triangle", null,
				"Draw a isosceles right triangle", "Tri_sq_iso");
		addRadioButtonMenuItem(sub, "Quadrangle", null, "Draw a  quadrangle",
				"quadrangle");
		addRadioButtonMenuItem(sub, "Parallelogram", null,
				"Draw a parallelogram", "parallelogram");
		addRadioButtonMenuItem(sub, "Trapezoid", null, "Draw a trapezoid",
				"trapezoid");
		addRadioButtonMenuItem(sub, "Right-angled Trapezoid", null,
				"Draw a right angle trapezoid", "ra_trapezoid");
		addRadioButtonMenuItem(sub, "Rectangle", null, "Draw a rectangle",
				"rectangle");
		addRadioButtonMenuItem(sub, "Square", null, "Draw a square", "square");
		addRadioButtonMenuItem(sub, "Pentagon", null, "Draw a pentagon",
				"pentagon");
		addRadioButtonMenuItem(sub, "Polygon", null, "Draw a polygon",
				"polygon");
		menu.add(sub);

		sub = new JMenu(getLanguage("Special Angles"));
		addRadioButtonMenuItem(sub, "15", null, "Draw an angle of 15 degree",
				"sangle");
		addRadioButtonMenuItem(sub, "30", null, "Draw an angle of 30 degree",
				"sangle");
		addRadioButtonMenuItem(sub, "45", null, "Draw an angle of 45 degree",
				"sangle");
		addRadioButtonMenuItem(sub, "60", null, "Draw an angle of 60 degree",
				"sangle");
		addRadioButtonMenuItem(sub, "75", null, "Draw an angle of 75 degree",
				"sangle");
		addRadioButtonMenuItem(sub, "90", null, "Draw an angle of 90 degree",
				"sangle");
		addRadioButtonMenuItem(sub, "115", null, "Draw an angle of 115 degree",
				"sangle");
		addRadioButtonMenuItem(sub, "120", null, "Draw an angle of 120 degree",
				"sangle");
		addRadioButtonMenuItem(sub, "Other...", null, "Draw an angle...",
				"sangle");
		menu.add(sub);
		// menu.addSeparator();

		menu = new JMenu(getLanguage("Constraint"));
		addRadioButtonMenuItem(menu, "Equate angles", null,
				"Set two angles equal", null);
		// addRadioButtonMenuItem(menu, "Nteqangle",
		// "Draw line with two angles equal", this);
		addRadioButtonMenuItem(menu, "Eqangle3p", null,
				"Set the sum of three angles equal to one", null);
		addRadioButtonMenuItem(menu, "Angle Specification", null,
				"Set specific angles of system", null);
		menu.addSeparator();

		addRadioButtonMenuItem(menu, "Equal Ratio", null,
				"Select four segments to set their equal ratio", null);

		addRadioButtonMenuItem(menu, "Equal Distance", null,
				"Set two segments as congruent", "Equal Distance");

		final JMenu sub2 = new JMenu(getLanguage(getLanguage("Ratio Distance")));
		addRadioButtonMenuItem(sub2, "1 : 1", null,
				"Proportion two segments 1 : 1", "ra_side");
		addRadioButtonMenuItem(sub2, "1 : 2", null,
				"Proportion two segments 1 : 2", "ra_side");
		addRadioButtonMenuItem(sub2, "1 : 3", null,
				"Proportion two segments 1 : 3", "ra_side");
		addRadioButtonMenuItem(sub2, "Other..", null,
				"Proportion two segments with specified ratio...", "ra_side");
		menu.add(sub2);
		addRadioButtonMenuItem(menu, "CCtangent", null,
				"Make two circles tangent", null);
		menuBar.add(menu);
		menu = new JMenu(getLanguage("Action"));
		menuBar.add(menu);
		addRadioButtonMenuItem(
				menu,
				"Trace",
				null,
				"Select a point to trace its locus (in combination with move or animation)",
				null);
		addRadioButtonMenuItem(menu, "Locus", null, "The locus of a point",
				null);
		addRadioButtonMenuItem(menu, "Animation", null,
				"Click a point then an object to animate", null);
		menu.addSeparator();

		addRadioButtonMenuItem(menu, "Fill Polygon", null,
				"Select a closed segment path to fill the polygon", null);
		addRadioButtonMenuItem(menu, "Measure Distance", null,
				"Select two angles to set equal", null);
		addRadioButtonMenuItem(menu, "Arrow", null,
				"Select two points to construct an arrow", null);

		final JMenu sub1 = new JMenu(getLanguage("Equal Mark"));
		addRadioButtonMenuItem(sub1, "1", null, "Mark for equal with one line",
				"eqmark");
		addRadioButtonMenuItem(sub1, "2", null, "Mark for equal with two line",
				"eqmark");
		addRadioButtonMenuItem(sub1, "3", null,
				"Mark for equal with three line", "eqmark");
		addRadioButtonMenuItem(sub1, "4", null,
				"Mark for equal with four line", "eqmark");
		menu.add(sub1);
		addRadioButtonMenuItem(menu, "Right-angle Mark", null,
				"Draw a right angle mark", "RAMark");
		addRadioButtonMenuItem(menu, "Calculation", null, "Calculation",
				"Calculation");
		menuBar.add(menu);
		menu.addSeparator();
		addRadioButtonMenuItem(menu, "Hide Object", null, "Hide objects", null);
		addRadioButtonMenuItem(menu, "Show Object", null,
				"Show objects that is hiden", null);
		menu.addSeparator();
		addRadioButtonMenuItem(menu, "Transform", null, "Transform polygon",
				null);
		addRadioButtonMenuItem(menu, "Equivalence", null,
				"Equivalence transform polygon", null);
		addRadioButtonMenuItem(menu, "Free Transform", null,
				"Transform polygon freely", null);

		menu = pprove.getProveMenu();
		// addAMenu(menu, "All Solutions", "All Solutions", this);
		menuBar.add(menu);

		menu = new JMenu(getLanguage("Rules"));
		menuBar.add(menu);
		addAMenu(menu, "Full Angle...", null, "Full Angle Method", null);
		addAMenu(menu, "Geometry Database...", null,
				"Geometry Deductive Database Method", null);

		menu = new JMenu(getLanguage("Option"));
		menuBar.add(menu);
		addAMenu(menu, "Preferences...", null, null, null);
		addAMenu(menu, "Construct History...", null, "Edit construct history",
				null);
		addAMenu(menu, "Show Step Bar", null, "Show Step bar for proof", "step");
		addAMenu(menu, "Style...", null, "Show Draw Style Dialog", null);

		menu = new JMenu(getLanguage("Help"));
		addAMenu(menu, "Help", KeyEvent.VK_F1, "Help", "help");
		addAMenu(menu, "Online Help", null, "Online Help", null);
		addAMenu(menu, "Help on Mode", null, "Help on Mode", null);
		addAMenu(menu, "JGEX Homepage...", null, "JGEX Homepage", null);
		addAMenu(menu, "Contact Us...", null, "Contact Us", null);

		// menu.addSeparator();
		// addAMenu(menu, "Check for Update...", null, "Check for Update",
		// null);
		if (!UtilityOSValidator.isMac())
			addAMenu(menu, "About JGEX...", null, "About Java Geometry Expert",
					"infor");

		menuBar.add(menu);
		toolBarRight.add(Box.createVerticalBox());

		getContentPane().add(toolBar, BorderLayout.PAGE_START);
		getContentPane().add(toolBarRight, BorderLayout.EAST);
	}

	private JRadioButtonMenuItem addRadioButtonMenuItem(final JMenu bar,
			final String sName, final Integer iMnemonic, final String sTooltip,
			final String sImage) {
		assert (bar != null);

		final JRadioButtonMenuItem menuitem = new JRadioButtonMenuItem(sName);

		if ((sName != null) && !sName.isEmpty()) {
			menuitem.setActionCommand(sName);
			menuitem.setText(DrawPanelFrame.getLanguage(sName));
		}

		menuitem.addActionListener(this);

		if ((sTooltip != null) && !sTooltip.isEmpty())
			menuitem.setToolTipText(sTooltip);

		if (iMnemonic != null) {
			menuitem.setMnemonic(iMnemonic);
			final KeyStroke ctrlP = KeyStroke.getKeyStroke(iMnemonic,
					InputEvent.CTRL_MASK);
			menuitem.setAccelerator(ctrlP);
		}

		bar.add(menuitem);
		menugroup.add(menuitem);
		return menuitem;
	}

	// public static void addImageToItem(JMenuItem item) {
	// final ImageIcon m = GExpert.createImageIcon("images/small/blank.gif");
	// item.setIcon(m);
	// }

	// public static void addImageToItem(JMenu item) {
	// final ImageIcon m = GExpert.createImageIcon("images/small/"
	// + "blank.gif");
	// item.setIcon(m);
	// }

	public static void addImageToItem(final JMenuItem item, final String name) {
		ImageIcon m = DrawPanelFrame.createImageIcon("images/small/" + name + ".gif");
		if (m == null)
			m = DrawPanelFrame.createImageIcon("images/small/blank.gif");
		item.setIcon(m);
	}

	public JMenuItem addAMenu(final JMenu bar, final String sName,
			final Integer iMnemonic, final String sTooltip, final String sImage) {
		assert (bar != null);

		final JMenuItem menuitem = new JMenuItem(sName);

		if ((sName != null) && !sName.isEmpty()) {
			menuitem.setActionCommand(sName);
			menuitem.setText(DrawPanelFrame.getLanguage(sName));
		}

		menuitem.addActionListener(this);

		if ((sTooltip != null) && !sTooltip.isEmpty())
			menuitem.setToolTipText(sTooltip);

		if (iMnemonic != null) {
			menuitem.setMnemonic(iMnemonic);
			final KeyStroke ctrlP = KeyStroke.getKeyStroke(iMnemonic,
					InputEvent.CTRL_MASK);
			menuitem.setAccelerator(ctrlP);
		}

		if ((sImage != null) && !sImage.isEmpty()) {
			final ImageIcon m = DrawPanelFrame.createImageIcon("images/small/"
					+ sImage + ".gif");
			menuitem.setIcon(m);
		}
		bar.add(menuitem);
		return menuitem;
	}

	public static Language getLan() {
		return language;
	}

	public static String getLanguage(final String s1) {
		if (language == null)
			return s1;

		final String s2 = language.getString(s1);
		return ((s2 == null) || (s2.isEmpty())) ? s1 : s2;
	}

	public static String getLanguageTip(final String s1) {
		if (language == null)
			return s1;

		final String s2 = language.getString1(s1);
		return s2;
	}

	public static String getLanguage(final int n) {
		if (language == null)
			return "";

		final String s1 = language.getString(n);
		return s1;
	}

	public static String getLanguage(final int n, final String s) {
		if (language == null)
			return s;

		final String s1 = language.getString(n);
		if ((s1 != null) && (s1.length() > 0))
			return s1;
		return s;
	}

	public void setActionMove() {
		sendAction("Move", buttonMove);
		buttonMove.setSelected(true);
	}

	public void setActionSelect() {
		sendAction("Select", buttonSelect);
		buttonSelect.setSelected(true);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();
		final Object src = e.getSource();
		sendAction(command, src);
	}

	public void sendAction(final String command, final Object src) {
		String tip = null;
		String ps = null;
		String pname = null;
		JToggleButton button = null;
		JMenuItem item = null;
		boolean select = true;

		if (src instanceof JMenuItem) {
			item = (JMenuItem) src;
			ps = item.getText();
			tip = item.getToolTipText();
			pname = item.getName();
			select = item.isSelected();
		} else if (src instanceof JToggleButton) {
			button = (JToggleButton) src;
			ps = button.getText();
			tip = button.getToolTipText();
			select = button.isSelected();
		}

		d.setCursor(Cursor.getDefaultCursor());

		if (command.equals("example")) {
			// try {
			if (!UtilityMiscellaneous.isApplet())
				openFromXMLFile(new File(pname + "/" + tip));
			else
				openAOnlineFile(pname, ps);

		} else if (command.equals("Save as PS")) {
			if (!need_save())
				return;

			final DialogPsProperty dlg = new DialogPsProperty(this);
			centerDialog(dlg);
			dlg.setVisible(true);
			final int r = dlg.getSavePsType();
			final boolean ptf = dlg.getPointfilled();
			// boolean pts = dlg.getisProveTextSaved();

			if ((r == 0) || (r == 1) || (r == 2)) {
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(final File f) {
						return f.isDirectory() || f.getName().endsWith("ps");
					}

					@Override
					public String getDescription() {
						return "PostScript (*.ps)";
					}
				});
				final String dr = getUserDir();
				chooser.setCurrentDirectory(new File(dr));

				final int result = chooser.showSaveDialog(this);
				if (result == JFileChooser.CANCEL_OPTION)
					return;
				try {
					final File file = chooser.getSelectedFile();
					String path = file.getPath();
					if (!path.endsWith(".ps"))
						path += ".ps";
					if (file.exists()
							&& getUserOverwriteOption(file.getName()))
						return;
					dp.write_ps(path, r, ptf, true);
				} catch (final Exception ee) {
					UtilityMiscellaneous.print(ee.toString() + "\n" + ee.getStackTrace());
				}
			}

		} else if (command.equalsIgnoreCase("Save as PDF"))
			saveAsPDF();
		// } else if (command.equalsIgnoreCase("Save as XML")) {
		// this.saveAsXML();
		else if (command.equals("Save as Image"))
			saveAsImage();
		else if (command.equals("Save as Animated Image"))
			saveAsGIF();
		else if (command.equalsIgnoreCase("Save Proof as Animated Image"))
			saveProofAsGIF();
		else if (command.equals("Save") || command.equals("Save as...")) {
			if (command.equals("Save"))
				saveAFile(false);
			else
				saveAFile(true);

		} /*
		 * else if (command.equals("Save as Text")) { if (!need_save()) {
		 * return; }
		 * 
		 * final gterm gt = pprove.getConstructionTerm(); if (gt != null) {
		 * final JFileChooser filechooser1 = new JFileChooser(); final String dr
		 * = getUserDir(); filechooser1.setCurrentDirectory(new File(dr));
		 * 
		 * final int result = filechooser1.showDialog(this, "Save"); if (result
		 * == JFileChooser.APPROVE_OPTION) { final File f =
		 * filechooser1.getSelectedFile(); boolean bExists = f.exists(); try {
		 * if (bExists) { f.delete(); } else { f.createNewFile(); }
		 * FileOutputStream fp = new FileOutputStream(f, bExists); if (bExists)
		 * fp.write("\n\n".getBytes());
		 * 
		 * gt.writeAterm(fp); dp.writePointPosition(fp); fp.close(); } catch
		 * (final IOException ee) { JOptionPane.showMessageDialog(this,
		 * ee.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE); }
		 * 
		 * }
		 * 
		 * } }
		 */else if (command.equals("Open"))
			try {
				// final File file = chooser.getSelectedFile();
				openFromXMLFile(null);
			} catch (final Exception ee) {
				ee.printStackTrace();
			}
		else if (command.equals("Exit")) {
			if (saveBeforeExit())
				System.exit(0);
		} else if (command.equals("New"))
			Clear();
		else if (command.equals("Print"))
			dp.PrintContent();
		else if (command.equals("Undo")) {
			dp.Undo_step();
			if (lp != null)
				lp.reload();
			setBKState();
			d.repaint();
		} else if (command.equals("Redo")) {
			dp.redo_step();
			if (lp != null)
				lp.reload();
			setBKState();
			d.repaint();
		} else if (command.equals("Online Help"))
			openURL(("http://woody.cs.wichita.edu/help/index.html"));
		else if (command.equals("JGEX Homepage"))
			openURL(("http://woody.cs.wichita.edu"));
		else if (command.equals("Contact Us"))
			openURL(("mailto:yezheng@gmail.com"));
		else if (command.equals("ff")) {
			dp.redo();
			setBKState();
			d.repaint();
			/*
			 * } else if (command.equalsIgnoreCase("Check for Update")) {
			 * updateJGEX();
			 */
		} else if (command.equals("fr")) {
			dp.Undo();
			setBKState();
			d.repaint();
		} else if (command.equals("autoanimate"))
			dp.autoAnimate();
		else if (command.equals("autoshowstep"))
			dp.autoShowstep();
		else if (command.equals("Preferences..."))
			handlePreferences();
		else if (command.equals("Show Step Bar"))
			showProveBar(true);
		else if (command.equals("Style Dialog"))
			showStyleDialog();
		else if (command.equals("About JGEX"))
			handleAbout();
		else if (command.equals("Help")) {
			final String dr = getUserDir();
			final String sp = getFileSeparator();
			openURL("file:///" + dr + sp + "help" + sp + "index.html");
		} else if (command.equals("grid")) {
			dp.SetGrid(select);
			repaint();
		} else if (command.equals("snap")) {
			dp.SetSnap(!dp.isSnap());
			d.repaint();
		} else if (command.equals("view"))
			dp.SetCurrentAction(DrawPanel.VIEWELEMENT);
		else if (command.equals("lessgrid")) {
			dp.setMeshStep(true);
			button.setSelected(false);
			repaint();
		} else if (command.equals("moregrid")) {
			dp.setMeshStep(false);
			button.setSelected(false);
			repaint();
		} else if (command.equals("Construct History"))
			getUndoEditDialog().showDialog();
		else if (command.equals("Help on Mode")) {
			final String path = HelpMode.getHelpMode(_command);
			if (path != null) {
				final String dr = getUserDir();
				final String sp = getFileSeparator();
				openURL("file:///" + dr + sp + "help" + sp + path);
			}
		} else {
			_command = command;
			final String sx1 = DrawPanelFrame.getLanguage(command);
			String sx2 = DrawPanelFrame.getLanguageTip(command);
			if ((sx2 == null) && (tip != null))
				sx2 = tip;

			setActionTip(sx1, sx2);
			if (button != null) {
				final JRadioButtonMenuItem t = (JRadioButtonMenuItem) menugroup
						.getButton(command);
				if (t != null)
					t.setSelected(true);
				else {
					final ButtonModel m = menugroup.getSelection();
					if (m != null) {
						m.setGroup(null);
						m.setSelected(false);
						m.setGroup(menugroup);
					}
				}
			} else if (item != null) {
				final JToggleButton b = (JToggleButton) drawgroup
						.getButton(command);
				if (b != null)
					b.setSelected(true);
				else {
					final ButtonModel b1 = drawgroup.getSelection();
					if (b1 != null) {
						b1.setGroup(null);
						b1.setSelected(false);
						b1.setGroup(drawgroup);
					}
				}
			}

			if (command.equalsIgnoreCase("select"))
				dp.SetCurrentAction(DrawPanel.SELECT);
			else if (command.equalsIgnoreCase("point"))
				dp.SetCurrentAction(DrawPanelBase.D_POINT);
			else if (command.equalsIgnoreCase("line"))
				dp.SetCurrentAction(DrawPanelBase.D_LINE);
			else if (command.equalsIgnoreCase("circle"))
				dp.SetCurrentAction(DrawPanelBase.D_CIRCLE);
			else if (command.equalsIgnoreCase("oriented segment")) {
				dp.SetCurrentAction(DrawPanelBase.D_PRATIO);
				((JMenuItem) src).getText();
				ps = language.getEnglish(ps);
				if (ps.equalsIgnoreCase("Other..")) {
					final DialogRatioSelect dlg = new DialogRatioSelect(this);
					dlg.setVisible(true);
					dp.setParameter(dlg.getValue1(), dlg.getValue2());
				} else {
					final String s1 = ((JMenuItem) src).getText();
					final int[] t = DrawPanelFrame.parse2Int(s1);
					dp.setParameter(t[0], t[1]);
				}
			} else if (command.equalsIgnoreCase("circler"))
				dp.SetCurrentAction(DrawPanelBase.D_CIRCLEBYRADIUS);
			else if (command.equalsIgnoreCase("parallel"))
				dp.SetCurrentAction(DrawPanelBase.D_PARELINE);
			else if (command.equalsIgnoreCase("perpendicular"))
				dp.SetCurrentAction(DrawPanelBase.D_PERPLINE);
			else if (command.equalsIgnoreCase("aline"))
				dp.SetCurrentAction(DrawPanelBase.D_ALINE);
			else if (command.equalsIgnoreCase("Angle Bisector"))
				dp.SetCurrentAction(DrawPanelBase.D_ABLINE);
			else if (command.equalsIgnoreCase("bline"))
				dp.SetCurrentAction(DrawPanelBase.D_BLINE);
			else if (command.equalsIgnoreCase("tcline"))
				dp.SetCurrentAction(DrawPanelBase.D_TCLINE); // cctangent
			else if (command.equalsIgnoreCase("intersect"))
				dp.SetCurrentAction(DrawPanelBase.MEET);
			else if (command.equalsIgnoreCase("middle"))
				dp.SetCurrentAction(DrawPanelBase.D_MIDPOINT);
			else if (command.equalsIgnoreCase("Circle by Three Points"))
				dp.SetCurrentAction(DrawPanelBase.D_3PCIRCLE);
			else if (command.equalsIgnoreCase("translate")) {
				setDrawCursor(Cursor.HAND_CURSOR);
				dp.SetCurrentAction(DrawPanel.TRANSLATE);
			} else if (command.equalsIgnoreCase("foot"))
				dp.SetCurrentAction(DrawPanelBase.PERPWITHFOOT);
			else if (command.equalsIgnoreCase("angle"))
				dp.SetCurrentAction(DrawPanelBase.D_ANGLE);
			else if (command.equalsIgnoreCase("zoom-in"))
				// setDrawCursor("ZOOM_IN");
				dp.SetCurrentAction(DrawPanel.ZOOM_IN);
			else if (command.equalsIgnoreCase("zoom-out"))
				// setDrawCursor("ZOOM_OUT");
				dp.SetCurrentAction(DrawPanel.ZOOM_OUT);
			else if (command.equalsIgnoreCase("animation"))
				dp.SetCurrentAction(DrawPanel.ANIMATION);
			else if (command.equalsIgnoreCase("equate angles"))
				dp.SetCurrentAction(DrawPanelBase.SETEQANGLE);
			else if (command.equalsIgnoreCase("nteqangle"))
				dp.SetCurrentAction(DrawPanel.NTANGLE);
			else if (command.equalsIgnoreCase("eqangle3p"))
				dp.SetCurrentAction(DrawPanel.SETEQANGLE3P);
			else if (command.equalsIgnoreCase("cctangent"))
				dp.SetCurrentAction(DrawPanel.SETCCTANGENT);
			else if (command.equalsIgnoreCase("angle specification"))
				dp.defineSpecificAngle();
			else if (command.equalsIgnoreCase("ra_side")) {
				dp.SetCurrentAction(DrawPanel.SETEQSIDE);
				dp.setcurrentStatus(0);
				ps = language.getEnglish(ps);
				if (ps.equalsIgnoreCase("Other..")) {
					final DialogRatioSelect dlg = new DialogRatioSelect(this);
					dlg.setVisible(true);
					dp.setParameter(dlg.getValue1(), dlg.getValue2());
				} else {
					final String s1 = ((JMenuItem) src).getText();
					final int[] t = DrawPanelFrame.parse2Int(s1);
					dp.setParameter(t[0], t[1]);
				}

				// int status = Integer.parseInt(ps);
				// dp.setcurrentStatus(status);
			} else if (command.equalsIgnoreCase("equal distance")) {
				dp.SetCurrentAction(DrawPanel.SETEQSIDE);
				dp.setcurrentStatus(1);
				dp.setParameter(1, 1);
			} else if (command.equalsIgnoreCase("fillpolygon"))
				dp.SetCurrentAction(DrawPanel.DEFINEPOLY);
			else if (command.equalsIgnoreCase("polygon"))
				dp.SetCurrentAction(DrawPanelBase.D_POLYGON);
			else if (command.equalsIgnoreCase("square"))
				dp.SetCurrentAction(DrawPanelBase.D_SQUARE);
			else if (command.equalsIgnoreCase("radical of two circles"))
				dp.SetCurrentAction(DrawPanelBase.D_CCLINE);
			else if (command.equalsIgnoreCase("isosceles triangle"))
				dp.SetCurrentAction(DrawPanelBase.D_IOSTRI);
			else if (command.equalsIgnoreCase("fill polygon"))
				dp.SetCurrentAction(DrawPanel.DEFINEPOLY);
			else if (command.equalsIgnoreCase("text"))
				dp.SetCurrentAction(DrawPanelBase.D_TEXT);
			else if (command.equalsIgnoreCase("mirror"))
				dp.SetCurrentAction(DrawPanelBase.MIRROR);
			else if (command.equalsIgnoreCase("circle by diameter"))
				dp.SetCurrentAction(DrawPanelBase.D_PFOOT);
			else if (command.equalsIgnoreCase("Trace"))
				dp.SetCurrentAction(DrawPanel.SETTRACK);
			else if (command.equalsIgnoreCase("Locus"))
				dp.SetCurrentAction(DrawPanel.LOCUS);
			else if (command.equalsIgnoreCase("point by point and segment"))
				dp.SetCurrentAction(DrawPanelBase.D_PTDISTANCE);
			else if (command.equalsIgnoreCase("propline")) {
				final String s = ((JMenuItem) src).getText();
				ps = language.getEnglish(ps);
				if (ps.equalsIgnoreCase("Other..")) {
					dp.SetCurrentAction(DrawPanelBase.LRATIO);
					final DialogRatioSelect dlg = new DialogRatioSelect(this);
					dlg.setVisible(true);
					dp.setParameter(dlg.getValue1(), dlg.getValue2());
					setTipText(dlg.getValue1() + ":" + dlg.getValue2());
				} else {
					dp.SetCurrentAction(DrawPanelBase.LRATIO);
					final int[] t = DrawPanelFrame.parse2Int(s);
					dp.setParameter(t[0], t[1]);
					setTipText(s);
				}
			} else if (command.equalsIgnoreCase("midpoint"))
				dp.SetCurrentAction(DrawPanelBase.D_MIDPOINT);
			else if (command.equalsIgnoreCase("circumcenter"))
				dp.SetCurrentAction(DrawPanelBase.CIRCUMCENTER);
			else if (command.equalsIgnoreCase("centroid"))
				dp.SetCurrentAction(DrawPanelBase.BARYCENTER);
			else if (command.equalsIgnoreCase("orthocenter"))
				dp.SetCurrentAction(DrawPanelBase.ORTHOCENTER);
			else if (command.equalsIgnoreCase("incenter"))
				dp.SetCurrentAction(DrawPanelBase.INCENTER);
			else if (command.equalsIgnoreCase("move"))
				dp.SetCurrentAction(DrawPanel.MOVE);
			else if (command.equalsIgnoreCase("o_t_segment")) {
				dp.SetCurrentAction(DrawPanelBase.D_TRATIO);
				((JMenuItem) src).getText();
				ps = language.getEnglish(ps);
				if (ps.equalsIgnoreCase("Other..")) {
					final DialogRatioSelect dlg = new DialogRatioSelect(this);
					dlg.setVisible(true);
					dp.setParameter(dlg.getValue1(), dlg.getValue2());
				} else {
					final String s1 = ((JMenuItem) src).getText();
					final int[] t = DrawPanelFrame.parse2Int(s1);
					dp.setParameter(t[0], t[1]);
				}
			} else if (command.equalsIgnoreCase("measure distance"))
				dp.SetCurrentAction(DrawPanelBase.DISTANCE);
			else if (command.equalsIgnoreCase("Arrow"))
				dp.SetCurrentAction(DrawPanel.ARROW);
			else if (command.equalsIgnoreCase("horizonal"))
				dp.SetCurrentAction(DrawPanelBase.H_LINE);
			else if (command.equalsIgnoreCase("vertical"))
				dp.SetCurrentAction(DrawPanelBase.V_LINE);
			else if (command.equalsIgnoreCase("eqmark")) {
				dp.SetCurrentAction(DrawPanel.EQMARK);
				final int status = Integer.parseInt(ps);
				dp.setcurrentStatus(status);
			} else if (command.equalsIgnoreCase("triangle")) {
				dp.setcurrentStatus(3);
				dp.SetCurrentAction(DrawPanelBase.D_POLYGON);
				dp.setcurrentStatus(3);
			} else if (command.equalsIgnoreCase("equilateral triangle"))
				dp.SetCurrentAction(DrawPanel.DRAWTRIALL);
			else if (command.equalsIgnoreCase("Tri_perp"))
				dp.SetCurrentAction(DrawPanelBase.D_PFOOT);
			else if (command.equalsIgnoreCase("Tri_sq_iso"))
				dp.SetCurrentAction(DrawPanel.DRAWTRISQISO);
			else if (command.equalsIgnoreCase("quadrangle")) {
				dp.setcurrentStatus(4);
				dp.SetCurrentAction(DrawPanelBase.D_POLYGON);
				dp.setcurrentStatus(4);
			} else if (command.equalsIgnoreCase("parallelogram"))
				dp.SetCurrentAction(DrawPanel.PARALLELOGRAM);
			else if (command.equalsIgnoreCase("ra_trapezoid"))
				dp.SetCurrentAction(DrawPanel.RA_TRAPEZOID);
			else if (command.equalsIgnoreCase("trapezoid"))
				dp.SetCurrentAction(DrawPanel.TRAPEZOID);
			else if (command.equalsIgnoreCase("rectangle"))
				dp.SetCurrentAction(DrawPanel.RECTANGLE);
			else if (command.equalsIgnoreCase("pentagon")) {
				dp.setcurrentStatus(5);
				dp.SetCurrentAction(DrawPanelBase.D_POLYGON);
				dp.setcurrentStatus(5);
			} else if (command.equalsIgnoreCase("polygon")) {
				dp.SetCurrentAction(DrawPanelBase.D_POLYGON);
				dp.setcurrentStatus(9999);
			} else if (command.equalsIgnoreCase("hide object"))
				dp.SetCurrentAction(DrawPanel.HIDEOBJECT);
			else if (command.equalsIgnoreCase("show object"))
				dp.SetCurrentAction(DrawPanel.SHOWOBJECT);
			else if (command.equalsIgnoreCase("Full Angle..."))
				getRulesDialog(1);
			else if (command.equalsIgnoreCase("Geometry Database..."))
				getRulesDialog(0);
			else if (command.equalsIgnoreCase("sangle")) {
				dp.SetCurrentAction(DrawPanel.SANGLE);
				try {
					int n = 0;
					ps = language.getEnglish(ps);
					if (ps.equalsIgnoreCase("Other..")) {
						String s = JOptionPane.showInputDialog(this, DrawPanelFrame
								.getLanguage(1053,
										"Please input the value of the angle"));
						if (s == null)
							s = "0";
						n = Integer.parseInt(s);
					} else
						n = Integer.parseInt(ps);
					dp.setcurrentStatus(n);
				} catch (final NumberFormatException ee) {
					JOptionPane.showMessageDialog(this, ee.getMessage(),
							"Information", JOptionPane.WARNING_MESSAGE);
				}
			} else if (command.equalsIgnoreCase("equal ratio"))
				dp.SetCurrentAction(DrawPanel.RATIO);
			else if (command.equalsIgnoreCase("RAMark"))
				dp.SetCurrentAction(DrawPanel.RAMARK);
			else if (command.equalsIgnoreCase("Transform"))
				dp.SetCurrentAction(DrawPanel.TRANSFORM);
			else if (command.equalsIgnoreCase("Equivalence"))
				dp.SetCurrentAction(DrawPanel.EQUIVALENCE);
			else if (command.equalsIgnoreCase("Free Transform"))
				dp.SetCurrentAction(DrawPanel.FREE_TRANSFORM);
			else if (command.equalsIgnoreCase("Calculation")) {
				final DialogTextValueEditor dlg = new DialogTextValueEditor(this);
				centerDialog(dlg);
				dlg.setVisible(true);
			}

		}
	}

	static int[] parse2Int(final String s) {
		final String[] sl = s.split(":");
		final int[] t = new int[2];
		try {
			t[0] = Integer.parseInt(sl[0].trim());
			t[1] = Integer.parseInt(sl[1].trim());
		} catch (final NumberFormatException ee) {
			t[0] = 1;
			t[1] = 1;
		}
		return t;
	}

	/**
	 * Activates and inactivates the 4 buttons relevant to redo and undo actions
	 * to account for changes in the undo and redo queues.
	 */
	public void setBKState() {
		final int n1 = dp.getUndolistSize();
		final int n2 = dp.getRedolistSize();
		if ((n1 == 0) && (n2 == 0)) {
			BK2.setEnabled(true);
			BK4.setEnabled(true);
			BK1.setEnabled(true);
			BK3.setEnabled(true);
		} else {
			BK2.setEnabled(n1 != 0);
			BK4.setEnabled(n1 != 0);
			BK1.setEnabled(n2 != 0);
			BK3.setEnabled(n2 != 0);
		}
	}

	public boolean saveBeforeExit() {
		if (dp.need_save() && UtilityMiscellaneous.needSave()) {
			final int n = JOptionPane.showConfirmDialog(this,
					"The diagram has been changed, do you want to save?",
					"Save", JOptionPane.YES_NO_CANCEL_OPTION);
			if (n == JOptionPane.OK_OPTION) {
				final boolean r = saveAFile(false);
				return r;
			} else if (n == JOptionPane.NO_OPTION)
				return true;
			else
				return false;
		}
		return true;
	}

	public boolean need_save() {
		if (!dp.need_save()) {
			setTipText(getLanguage(1050, "Nothing to be saved."));
			return false;
		}
		return true;
	}

	public boolean saveAFile(final boolean bPromptUserToSelectFilename) {
		File file = dp.getFile();
		int result = 0;

		if (need_save()) {
			if ((file == null) || bPromptUserToSelectFilename) { // command.equals("Save as...")
				JFileChooser chooser = getFileChooser();

				try {
					if ((file != null) && file.exists())
						chooser.setSelectedFile(file);
					result = chooser.showSaveDialog(this);
				} catch (final Exception ee) {
					filechooser = null;
					chooser = getFileChooser();
					result = chooser.showSaveDialog(this);
				}

				if (result == JFileChooser.APPROVE_OPTION)
					file = chooser.getSelectedFile();
				else
					file = null;
			}
			if (file != null)
				try {
					String path = file.getPath();
					if (!path.endsWith(".gex.xml"))
						path += ".gex.xml";
					final File f = new File(path);
					if (f.exists() && getUserOverwriteOption(file.getName()))
						return false;
					saveAsXML(path);
					updateTitle();
					UtilityMiscellaneous.onFileSavedOrLoaded();
					return true;

				} catch (final Exception ee) {
					ee.printStackTrace();
					UtilityMiscellaneous.print(ee.getMessage() + "\n" + ee.getStackTrace());
				}
		}
		return false;
	}

	public int Clear() {
		int n = 0;
		if (UtilityMiscellaneous.isApplication() && !dp.isitSaved()) {
			n = JOptionPane
					.showConfirmDialog(
							this,
							getLanguage(1000,
									"The diagram has been changed, do you want to save it?"),
							getLanguage("Save"),
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);
			if (n == JOptionPane.YES_OPTION) {
				if (!saveAFile(false))
					return 2;
			} else if (n == JOptionPane.NO_OPTION) {
				// if (!saveAFile(true))
				// return 2;
			} else
				return 2;
		}
		closeAllDialogs();
		provePanelbar.setVisible(false);
		resetAllButtonStatus();
		ComboBoxInteger.resetAll();
		setActionMove();
		dp.clearAll();
		d.clearAll();
		if (pprove != null)
			pprove.finishedDrawing();
		updateTitle();
		scroll.revalidate();

		return 0;
	}

	public void closeAllDialogs() {
		if (propt != null) {
			propt.setVisible(false);
			propt.dispose();
			propt = null;
		}
		if (sdialog != null) {
			sdialog.setVisible(false);
			sdialog.dispose();
			sdialog = null;
		}
		if (udialog != null) {
			udialog.setVisible(false);
			udialog.dispose();
			udialog = null;
		}
		if (pdialog != null) {
			pdialog.setVisible(false);
			pdialog.dispose();
			pdialog = null;
		}
		if (cdialog != null) {
			cdialog.setVisible(false);
			cdialog.dispose();
			cdialog = null;
		}
		if (rdialog != null) {
			rdialog.setVisible(false);
			rdialog.dispose();
			rdialog = null;
		}
		if (ndialog != null) {
			ndialog.setVisible(false);
			ndialog.dispose();
			ndialog = null;
		}
		if (adialog != null) {
			adialog.setVisible(false);
			adialog = null;
			// removeAllDependentDialogs();
		}
	}

	public void setDrawCursor(final int t) {
		d.setCursor(Cursor.getPredefinedCursor(t));
	}

	// public void setDrawCursor(String s) {
	// d.setCursor(GExpert.getDefinedCursor(s));
	// }

	public void reloadLP() {
		if ((lp != null) && (udialog != null) && udialog.isVisible())
			lp.reload();
	}

	public boolean getUserOverwriteOption(final String name) {
		if (JOptionPane.OK_OPTION != JOptionPane
				.showConfirmDialog(
						this,
						name
								+ getLanguage(1002,
										" already exists, do you want to overwrite it?"),
						getLanguage(1001, "File exists"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE))
			return true;
		return false;
	}

	public static int safeParseInt(final String s, final int defInt) {
		int i = defInt;
		try {
			if (!s.isEmpty())
				i = Integer.parseInt(s);
		} catch (final NumberFormatException e) {
		}
		return i;
	}

	public static int safeParseInt(final String s, final int defInt, final int minInt, final int maxInt) {
		int i = defInt;
		if (minInt <= maxInt) {
			try {
				if (!s.isEmpty())
					i = Integer.parseInt(s);
			} catch (final NumberFormatException e) {
			}

			if (i < minInt)
				i = minInt;

			if (i > maxInt)
				i = maxInt;
		}
		return i;
	}

	public static boolean safeParseBoolean(final String s, final boolean defBool) {
		boolean b = defBool;
		try {
			if (!s.isEmpty())
				b = Boolean.parseBoolean(s);
		} catch (final NumberFormatException e) {
		}
		return b;
	}

	public static Color safeParseColor(final String s, final Color defColor) {
		Color c = defColor;
		try {
			if (!s.isEmpty()) {
				final int rgba = Integer.parseInt(s);
				c = new Color(rgba, true);
			}
		} catch (final NumberFormatException e) {
		}
		return c;
	}

	public static double safeParseDouble(final String s, final double defDouble) {
		double d = defDouble;
		try {
			if (!s.isEmpty())
				d = Double.parseDouble(s);
		} catch (final NumberFormatException e) {
		}
		return d;
	}

	public void saveAsXML(final String path) {
		try {
			final DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();
			dbf.setNamespaceAware(true);
			final DocumentBuilder db = dbf.newDocumentBuilder();
			final Document doc = db.newDocument();

			final Element root = doc.createElementNS(null, "Document"); // Create
																		// Root
																		// Element
			doc.appendChild(root); // Add Root to Document
			dp.SaveIntoXMLDocument(root);

			final Element item = doc.createElementNS(null, "PProve"); // Create
																		// another
																		// Element
			root.appendChild(item); // Attach Element to previous element down
									// tree
			pprove.saveIntoXMLDocument(item);

			final DOMImplementationRegistry registry = DOMImplementationRegistry
					.newInstance();
			final DOMImplementationLS domImplLS = (DOMImplementationLS) registry
					.getDOMImplementation("LS");

			final LSSerializer ser = domImplLS.createLSSerializer(); // Create a
																		// serializer
																		// for
																		// the
																		// DOM
			final LSOutput out = domImplLS.createLSOutput();

			final DOMConfiguration config = ser.getDomConfig();
			config.setParameter("format-pretty-print", true);

			final File f = new File(path);
			final boolean bExists = f.exists();
			if (bExists)
				f.delete();
			else
				f.createNewFile();
			final FileOutputStream fp = new FileOutputStream(f, bExists);
			// final DataOutputStream out = new DataOutputStream(fp);
			final OutputStreamWriter oStream = new OutputStreamWriter(fp,
					"UNICODE");
			final BufferedWriter buffOut = new BufferedWriter(oStream);
			//
			out.setCharacterStream(buffOut);
			ser.write(doc, out); // Serialize the DOM

			buffOut.close();
			oStream.close();
			fp.close();

			// Files.newBufferedWriter(file, charset);
			// System.out.println( "STRXML = " + stringOut.toString() ); // Spit
			// out the DOM as a String
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	// public void saveAFile(String path) throws IOException {
	// final DataOutputStream out = drawProcess.openOutputFile(path);
	// dp.Save(out);
	// pprove.SaveProve(out);
	// out.close();
	// }

	public void saveProofAsGIF() {
		if (provePanelbar == null)
			return;
		if (!need_save())
			return;

		final DialogRectChooser1 r1 = new DialogRectChooser1(this);
		centerDialog(r1);
		r1.setVisible(true);
		if (!r1.getResult())
			return;
		final Rectangle rc = r1.getRectangle();

		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new UtilityFileFilter("GIF"));

		final String dr1 = getUserDir();
		chooser.setCurrentDirectory(new File(dr1));

		final int result = chooser.showSaveDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;
		final String dr = getUserDir();
		chooser.setCurrentDirectory(new File(dr));

		File ff = chooser.getSelectedFile();
		String p = ff.getPath();
		if (!p.endsWith("gif") && !p.endsWith("GIF")) {
			p = p + ".gif";
			ff = new File(p);
		}
		try {
			final DataOutputStream out = DrawPanel.openOutputFile(ff
					.getPath());
			final GifEncoder e = new GifEncoder();
			e.setQuality(20);
			e.start(out);
			e.setRepeat(0);
			e.setDelay(200); // 1 frame per sec

			final ImageTimer t = new ImageTimer(this);
			t.setEncorder(e);
			t.setRectangle(rc);

			t.setProveBar(provePanelbar);
			t.setDelay(200);
			t.setVisible(true);
			e.finish();
			out.close();

		} catch (final Exception ee) {
			ee.printStackTrace();
		}

	}

	public void saveAsGIF() {

		if (!need_save())
			return;
		Animation am = dp.getAnimateC();
		if (am == null) {
			JOptionPane
					.showMessageDialog(
							this,
							getLanguage(2301, "No animation has been defined.")
									+ "\n"
									+ getLanguage(2302,
											"Please use the menu \" Action -> Animation \" to define an animation first."),
							"GIF", JOptionPane.WARNING_MESSAGE);
			return;
		}
		am = new Animation(am);

		final DialogGIFExportOptions dlg = new DialogGIFExportOptions(this, getLanguage(2303,
				"GIF Option"));
		centerDialog(dlg);
		dlg.setDefaultValue(20);
		dlg.setVisible(true);
		if (!dlg.getReturnResult())
			return;
		final int q = dlg.getQuality();

		Rectangle rect = null;
		final DialogRectChooser rchoose = new DialogRectChooser(this);
		if (rchoose.getReturnResult())
			rect = rchoose.getSelectedRectangle();
		else
			return;

		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new UtilityFileFilter("GIF"));
		final String dr = getUserDir();
		chooser.setCurrentDirectory(new File(dr));

		final int result = chooser.showSaveDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;

		File ff = chooser.getSelectedFile();
		String p = ff.getPath();
		if (!p.endsWith("gif") && !p.endsWith("GIF")) {
			p = p + ".gif";
			ff = new File(p);
		}

		am.reCalculate();
		final int n = am.getRounds();
		if (n == 0)
			return;

		final int v = 1000 / am.getInitValue();

		final DialogGifProcess dlg1 = new DialogGifProcess(getFrame());
		centerDialog(dlg1);
		dlg1.setTotal(n);

		try {
			@SuppressWarnings("resource")
			final DataOutputStream out = DrawPanel.openOutputFile(ff
					.getPath());
			final GifEncoder e = new GifEncoder();
			e.setQuality(q);
			e.start(out);
			e.setRepeat(0);
			e.setDelay(v); // 1 frame per sec
			dlg1.en = e;
			dlg1.dp = dp;
			dlg1.rect = rect;
			dlg1.am = am;
			dlg1.gxInstance = this;
			dlg1.out = out;
			dlg1.setVisible(true);
			dlg1.setRun();

			// while (n >= 0) {
			// am.onTimer();
			// if (!dp.reCalculate()) {
			// am.resetXY();
			// }
			// e.addFrame(this.getBufferedImage(rect));
			// n--;
			// }
			// e.finish();
			// out.close();

		} catch (final IOException ee) {
			System.out.println(ee.getMessage());
		}

	}

	public void saveAsImage() {

		if (!need_save())
			return;

		Rectangle rect = null;
		final DialogRectChooser rchoose = new DialogRectChooser(this);
		if (rchoose.getReturnResult())
			rect = rchoose.getSelectedRectangle();
		else
			return;

		final JFileChooser chooser = new JFileChooser();
		String[] s = ImageIO.getWriterFormatNames();
		final String[] s1 = new String[s.length + 1];
		for (int i = 0; i < s.length; i++)
			s1[i] = s[i];
		s1[s.length] = "gif";
		s = s1;

		if (s.length > 0) {
			final FileFilter t = chooser.getFileFilter();
			chooser.removeChoosableFileFilter(t);

			UtilityFileFilter selected = null;
			for (final String element : s) {
				final UtilityFileFilter f = new UtilityFileFilter(element);
				chooser.addChoosableFileFilter(f);

				if (element.equalsIgnoreCase("JPG"))
					selected = f;
				if ((selected == null) && element.equalsIgnoreCase("JPEG"))
					selected = f;
			}
			chooser.setFileFilter(selected);
		}
		final String dr = getUserDir();
		chooser.setCurrentDirectory(new File(dr));

		final int result = chooser.showSaveDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;

		File ff = chooser.getSelectedFile();
		final FileFilter f = chooser.getFileFilter();
		final String endfix = f.getDescription();
		if (endfix == null)
			return;

		String p = ff.getPath();
		if (!p.endsWith(endfix)) {
			p = p + "." + endfix;
			ff = new File(p);
		}

		if (endfix.equals("gif"))
			try {
				final DataOutputStream out = DrawPanel.openOutputFile(ff
						.getPath());
				final GifEncoder e = new GifEncoder();
				e.setQuality(1);
				e.start(out);
				e.setRepeat(0);
				e.setDelay(0);
				e.addFrame(getBufferedImage(rect));
				e.finish();
				out.close();
			} catch (final IOException ee) {
				if (UtilityMiscellaneous.isDebug())
					ee.printStackTrace();
				else
					JOptionPane.showMessageDialog(this, ee.getMessage(),
							"Information", JOptionPane.ERROR_MESSAGE);
			}
		else {
			final BufferedImage image = getBufferedImage(rect);
			final Iterator<ImageWriter> iter = ImageIO
					.getImageWritersByFormatName(endfix);
			final ImageWriter writer = iter.next();
			try {
				final ImageOutputStream imageOut = ImageIO
						.createImageOutputStream(ff);
				writer.setOutput(imageOut);

				writer.write(new IIOImage(image, null, null));
				final IIOImage iioImage = new IIOImage(image, null, null);
				if (writer.canInsertImage(0))
					writer.writeInsert(0, iioImage, null);
				imageOut.close();
			} catch (final IOException exception) {
				if (UtilityMiscellaneous.isDebug())
					exception.printStackTrace();
				else
					JOptionPane.showMessageDialog(this, exception.getMessage(),
							"Information", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	public BufferedImage getBufferedImage(final Rectangle rc) {
		final BufferedImage image = new BufferedImage((int) rc.getWidth(),
				(int) rc.getHeight(), BufferedImage.TYPE_INT_RGB);
		final Graphics g = image.getGraphics();
		final Graphics2D g2 = (Graphics2D) g;
		g2.translate(-rc.getX(), -rc.getY());
		d.paintComponent(g2);
		return image;
	}

	public BufferedImage getBufferedImage2(final Rectangle rc) {
		final BufferedImage image = new BufferedImage((int) rc.getWidth(),
				(int) rc.getHeight(), BufferedImage.TYPE_INT_RGB);
		final Graphics g = image.getGraphics();
		final Graphics2D g2 = (Graphics2D) g;
		g2.translate(-rc.getX(), -rc.getY());
		contentPane.paint(g2);
		return image;
	}

	public boolean openAOnlineFile(final String path, final String pname) {

		/*
		 * if (2 == this.Clear()) { return false; // cancel option. }
		 * 
		 * if (CMisc.isApplication()) { return false; }
		 * 
		 * final Object o = this; final JApplet a = (JApplet) o;
		 * 
		 * final URL uc = a.getDocumentBase();
		 * 
		 * URL ul; try { if (pname.length() == 0) { ul = new URL(uc, "examples/"
		 * + path + ".gex"); } else { ul = new URL(uc, "examples/" + path + '/'
		 * + pname + ".gex");// s // + // pname // + //
		 * System.getProperty("file.separator") // + // ps) // ; }
		 * 
		 * final URLConnection urlc = ul.openConnection(); urlc.connect(); final
		 * InputStream instream = urlc.getInputStream(); final DataInputStream
		 * in = (new DataInputStream(instream)); dp.Load(in);
		 * 
		 * if (CMisc.version_load_now < 0.035) { this.showppanel(true); } else
		 * if (CMisc.version_load_now == 0.035) { final mnode n = new mnode();
		 * n.Load(in, dp); pprove.loadMTree(n); this.showppanel(false); } else
		 * if (CMisc.version_load_now >= 0.036) { pprove.LoadProve(in); }
		 * in.close(); dp.stopUndoFlash(); } catch (final IOException ee) { //
		 * CMisc.eprint(this, ee.toString() + "\n" + ee.getStackTrace()); final
		 * StackTraceElement[] tt = ee.getStackTrace();
		 * 
		 * String s = ee.toString(); for (final StackTraceElement element : tt)
		 * { if (element != null) { s += element.toString() + "\n"; } }
		 * JOptionPane.showMessageDialog(this, s); System.out.println(s); }
		 */
		return false;
	}

	public static DataOutputStream openOutputFile(final String s) {

		final String s1 = getUserDir();
		final String s2 = getFileSeparator();

		try {
			final File f = new File(s1 + s2 + s);

			final boolean bExists = f.exists();
			if (bExists)
				f.delete();
			else
				f.createNewFile();
			final FileOutputStream fp = new FileOutputStream(f, bExists);
			final DataOutputStream out = new DataOutputStream(fp);
			return out;
		} catch (final IOException e) {
		}
		return null;
	}

	// Error handler class
	// private class DOMErrorHandlerImpl implements DOMErrorHandler {
	// public boolean handleError(DOMError error) {
	// System.out.println("Error Message:" + error.getMessage());
	// if (error.getSeverity() == DOMError.SEVERITY_WARNING) { return true; }
	// else { return false; }
	// }
	// }

	public boolean openFromXMLFile(final File file) {
		final File f = file;
		// String path = file.getPath();

		final String path = "/Users/kutach/Documents/Workspace/JGEX_SagaciousMatterFork/xml_examples/6_GDD_FULL/81-109/100.gex.xml";
		// if (!path.endsWith(".gex.xml")) {
		// if (path.endsWith(".gex"))
		// path += ".xml";
		// else
		// path += ".gex.xml";
		// f = new File(path);
		// }

		// if (f.exists()) {
		// boolean r = true;
		// if (2 == this.Clear()) {
		// return false;
		// }
		//
		// if (f.getName().endsWith("gex.xml")) {

		dp.stopUndoFlash();
		dp.clearAll();
		dp.setFile(f);

		final Document document = jgex_IO.openXMLfile(path);
		document.normalize();
		final Element docElement = document.getDocumentElement();
		final NodeList nlist = docElement.getChildNodes();
		if (nlist != null)
			for (int i = 0; i < nlist.getLength(); ++i) {
				final Node nn = nlist.item(i);
				if (nn != null && nn instanceof Element) {
					final String sEntry = nn.getNodeName();
					if (sEntry.equalsIgnoreCase("DrawProcess"))
						dp.openFromXMLDocument((Element) nn);
					if (sEntry.equalsIgnoreCase("PProve"))
						 pprove.openFromXMLDocument((Element) nn);
				}
			}

		dp.setName("100.gex");

		UtilityMiscellaneous.version_load_now = 0;
		UtilityMiscellaneous.onFileSavedOrLoaded();
		updateTitle();

		return true;
	}

	protected void addRightButtons(final JToolBar toolBar) {
		JToggleButton button = null;

		button = makeAButton("construct_history", "Construct History",
				"construct history", "construct history", true);
		toolBar.add(button);

		button = makeAButton("translate", "translate", "translate view",
				"Translate");
		toolBar.add(button);
		drawgroup.add(button);
		button = makeAButton("zoom-in", "zoom-in", "Zoom in view", "Zoom-in");
		toolBar.add(button);
		drawgroup.add(button);
		button = makeAButton("zoom-out", "zoom-out", "Zoom out view",
				"Zoom-out");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("snap", "snap", "snap to grid", "snap");
		toolBar.add(button);
		button = makeAButton("grid", "grid", "draw the rectangle grid", "grid");
		toolBar.add(button);
		button = makeAButton("lessGrid", "lessgrid",
				"make the grid less dense", "lessGrid");
		toolBar.add(button);
		button = makeAButton("moreGrid", "moregrid",
				"make the grid more dense", "moreGrid");
		toolBar.add(button);

		BK1 = button = makeAButton("redo", "redo", "redo a step", "redo", true);
		toolBar.add(button);
		// // KeyStroke ctrlP = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
		// InputEvent.CTRL_MASK);
		// button.setAccelerator(ctrlP);

		BK2 = button = makeAButton("undo", "undo", "undo a step", "undo", true);
		toolBar.add(button);
		BK3 = button = makeAButton("ff", "ff", "forward to end", "ff", true);
		toolBar.add(button);
		BK4 = button = makeAButton("fr", "fr", "back to start", "fr", true);
		toolBar.add(button);

		button = makeAButton("autoshowstep", "autoshowstep",
				"auto show draw step by step", "play");
		toolBar.add(button);

		anButton = button = makeAButtonWith2ICon("animate_start",
				"animate_stop", "autoanimate", "start to animate", "play");
		anButton.setToolTipText(DrawPanelFrame.getLanguageTip("animate_start"));
		toolBar.add(button);
		button.setEnabled(false);
	}

	protected void addButtons(final JToolBar toolBar) {
		JToggleButton button = null;
		// ButtonGroup group = new ButtonGroup();

		button = makeAButton("new", "New", "create a new view", "new", true);
		toolBar.add(button);
		// group.add(button);
		toolBar.add(Box.createHorizontalStrut(1));

		button = makeAButton("open", "Open", "Open a file", "open", true);
		// group.add(button);
		toolBar.add(button);

		button = makeAButton("save", "Save", "Save to a file", "save", true);
		// group.add(button);
		toolBar.add(button);

		button = makeAButton("select", "Select", "Select mode", "Select");
		toolBar.add(button);
		buttonSelect = button;
		drawgroup.add(button);

		button = makeAButton("drag", "Move", "Move", "move");
		toolBar.add(button);
		drawgroup.add(button);
		buttonMove = button;

		button = makeAButton("point", "Point", "Add a single point", "point");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("line", "Line",
				"Select two points to construct a line", "line");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("parallel", "Parallel",
				"Draw a line which is parallel to another line", "parallel");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("perp", "Perpendicular",
				"Draw a line which is perpdicular to another line",
				"perpendicular");
		toolBar.add(button);
		drawgroup.add(button);
		// button = makeAButton("abline", "Abline",
		// "Select two lines to construct the bisector of an angle", "abline");
		// toolBar.add(button);
		// group.add(button);

		button = makeAButton("foot", "Foot",
				"Select a point and a line to construct the foot", "foot");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("circle", "Circle",
				"Draw two points and a circle", "circle");
		button.setToolTipText("Click a point then drag to construct a circle");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton(
				"circle3p",
				"Circle by Three Points",
				"Select three points to construct the circle passing through them",
				"circle3p");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("circler", "Circler",
				"Draw a circle with center and radius", "circler");
		button.setToolTipText("Construct a circle by clicking two points as radius and another point as center");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("fillpolygon", "Fill Polygon",
				"define an polygon", "polygon");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("angle", "Angle",
				"Select two lines to define their full-angle with a label",
				"angle");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("text", "Text", "Add text", "text");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("intersect", "Intersect",
				"Take the intersection of two objects (circle or line)",
				"intersect");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton(
				"mirror",
				"Mirror",
				"Mirror a object by clicking and then click a reflection axis or point",
				"reflectr");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton(
				"iso",
				"Isosceles Triangle",
				"Select two points or drag a segment to construct an isosceles triangle",
				"isosceles triangle");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("midpoint", "Midpoint",
				"Click two points to get their midpoint", "midpoint");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("square", "Square",
				"Select two points or drag a segment to construct a square",
				"square");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("triangle", "Triangle", null, "triangle");
		toolBar.add(button);
		drawgroup.add(button);

		button = makeAButton("polygon", "Polygon", null, "polygon");
		toolBar.add(button);
		button.removeActionListener(this);
		drawgroup.add(button);

		JToggleButton b1 = null;
		{
			final String imgLocation = "images/dselect.gif";
			final URL imageURL = DrawPanelFrame.class.getResource(imgLocation);
			Icon co = null;
			if (imageURL != null)
				co = (new ImageIcon(imageURL, ""));
			b1 = new JToggleButton(co) {

				/**
				 * 
				 */
				private static final long serialVersionUID = -6851853702279673987L;

				@Override
				public Dimension getPreferredSize() {
					return new Dimension(10, 28);
				}
			};
			b1.setUI(new EntityButtonUI(1));
			toolBar.add(b1);
		}

		final JPopButtonsPanel p = new JPopButtonsPanel(button, b1);
		button = makeAButton("triangle", "Triangle", null, "triangle");
		p.add(button);
		drawgroup.add(button);

		button = makeAButton("triangle_iso", "Isosceles Triangle", null,
				"isosceles triangle");
		p.add(button);
		drawgroup.add(button);

		button = makeAButton("triangle_all", "Equilateral Triangle", null,
				"equilateral triangle");
		p.add(button);
		drawgroup.add(button);

		button = makeAButton("triangle_perp", "Tri_perp", null, "triangle");
		p.add(button);
		drawgroup.add(button);

		button = makeAButton("quadrangle", "Quadrangle", null, "quadrangle");
		p.add(button);
		drawgroup.add(button);
		button = makeAButton("parallelogram", "Parallelogram", null, "pentagon");
		p.add(button);
		drawgroup.add(button);
		button = makeAButton("trapezoid", "Trapezoid", null, "trapezoid");
		p.add(button);
		drawgroup.add(button);
		button = makeAButton("ra_trapezoid", "RA_trapezoid", null,
				"right angle trapezoid");
		p.add(button);
		drawgroup.add(button);
		button = makeAButton("rectangle", "Rectangle", null, "rectangle");
		p.add(button);
		drawgroup.add(button);
		button = makeAButton("quadrangle_square", "Square", null, "square");
		p.add(button);
		drawgroup.add(button);

		button = makeAButton("pentagon", "Pentagon", null, "pentagon");
		p.add(button);
		drawgroup.add(button);
		button = makeAButton("polygon", "Polygon", null, "polygon");
		p.add(button);
		drawgroup.add(button);
		p.setSelectedButton(button);
	}

	private final ActionListener listener = new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			final JToggleButton button = (JToggleButton) e.getSource();
			button.getModel().setSelected(false);
		}
	};

	protected JToggleButton makeAButton(final String imageName,
			final String actionCommand, final String toolTipText,
			final String altText, final boolean t) {
		final JToggleButton button = makeAButton(imageName, actionCommand,
				toolTipText, altText);
		if (t)
			button.addActionListener(listener);
		return button;
	}

	protected JToggleButton makeAButton(final String imageName,
			final String actionCommand, final String toolTipText,
			final String altText) {
		final String imgLocation = "images/" + imageName + ".gif";
		final URL imageURL = DrawPanelFrame.class.getResource(imgLocation);
		Icon co = null;
		if (imageURL != null)
			co = (new ImageIcon(imageURL, altText));

		final JToggleButton button = new ActionButton(co);
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);

		final String s2 = getLanguageTip(actionCommand);
		if ((s2 != null) && (s2.length() != 0))
			button.setToolTipText(s2);
		else {
			final String s1 = getLanguage(actionCommand);
			if ((toolTipText == null) && (s1 != null) && (s1.length() != 0))
				button.setToolTipText(s1);
		}
		button.addActionListener(this);

		button.setText(null);
		// button.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
		return button;
	}

	protected JToggleButton makeAButtonWith2ICon(final String imageName,
			final String imageNameSelected, final String actionCommand,
			final String toolTipText, final String altText) {

		String imgLocation = "images/" + imageName + ".gif";
		URL imageURL = DrawPanelFrame.class.getResource(imgLocation);

		Icon icon1, icon2;
		icon1 = icon2 = null;

		if (imageURL != null)
			icon1 = (new ImageIcon(imageURL, altText));

		imgLocation = "images/" + imageNameSelected + ".gif";
		imageURL = DrawPanelFrame.class.getResource(imgLocation);

		if (imageURL != null)
			icon2 = (new ImageIcon(imageURL, altText));

		final DActionButton button = new DActionButton(icon1);
		button.set2StatusIcons(icon1, icon2);
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		return button;
	}

	public void resetAllButtonStatus() {

		if (lp != null)
			lp.clearAllTrees();

		if (afpane != null)
			afpane.setVisible(false);

		if (aframe != null)
			aframe.stopA();
		if (anButton != null)
			anButton.setEnabled(false);
		restorScroll();
		pprove.clearAll();
		d.setCursor(Cursor.getDefaultCursor());
		BK1.setEnabled(true);
		BK2.setEnabled(true);
		BK3.setEnabled(true);
		BK4.setEnabled(true);
	}

	// public void setTitle(String title) {
	// if (CMisc.isApplet()) {
	// } else {
	// JFrame f = (JFrame) (Object) this;
	// f.setTitle(title);
	// }
	// }

	public void updateTitle() { // APPLET ONLY.
		if (!UtilityMiscellaneous.isApplication())
			return;

		final String s = dp.getName();
		final JFrame frame = this;

		String v = UtilityVersion.getProject();
		// String d = Version.getData();

		v = DrawPanelFrame.getLanguage(v);

		if ((s != null) && (s.length() != 0))
			frame.setTitle(s + "  -  " + v);
		else
			frame.setTitle(v);

	}

	public void restorScroll() {
		final Rectangle rc = new Rectangle(0, 0, 0, 0);
		scroll.scrollRectToVisible(rc);
		d.setPreferredSize(new Dimension(100, 100));
		d.revalidate();
	}

	public void setActionTip(final String name, final String tip) {
		if (pprove.isProverRunning())
			return;

		label.setText(" " + name);
		if (tip != null)
			label2.setText(" " + tip);
		else
			label2.setText("");
	}

	private Timer timer;
	private int n = 4;
	private static Color fcolor = new Color(128, 0, 0);

	public void setTextLabel2(final String s, final int n) {
		setTextLabel2(s);
		this.n = n;
	}

	public void setLabelText2(final String s) {
		label2.setText(" " + s);
	}

	public void stopTimer() {
		if (timer != null)
			timer.stop();
		n = 0;
		label2.setForeground(fcolor);
	}

	public void setTextLabel2(final String s) {
		label2.setText(" " + s);
		if ((timer == null) && (s != null) && (s.length() != 0)) {
			timer = new Timer(200, new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					if ((n % 2) == 0)
						label2.setForeground(fcolor);
					else
						label2.setForeground(Color.lightGray);
					if (n == 0) {
						timer.stop();
						timer = null;
						label2.setForeground(Color.black);
					}
					n--;

				}
			});
			n = 8;
			timer.start();
		}

	}

	public void setTipText(final String text) {
		this.setTextLabel2(text);
	}

	public void Animate(final int type) {
		d.onAnimate(type);
	}

	@Override
	public void dragEnter(final DropTargetDragEvent dtde) {
		// System.out.println("Drag Enter");
	}

	@Override
	public void dragExit(final DropTargetEvent dte) {
		// System.out.println("Drag Exit");
	}

	@Override
	public void dragOver(final DropTargetDragEvent dtde) {
		// System.out.println("Drag Over");
	}

	@Override
	public void dropActionChanged(final DropTargetDragEvent dtde) {
		// System.out.println("Drop Action Changed");
	}

	@Override
	public void drop(final DropTargetDropEvent dtde) {
		try {
			// Ok, get the dropped object and try to figure out what it is
			final Transferable tr = dtde.getTransferable();
			final DataFlavor[] flavors = tr.getTransferDataFlavors();
			for (final DataFlavor flavor : flavors)
				// Check for file lists specifically
				if (flavor.isFlavorJavaFileListType()) {
					// Great! Accept copy drops...
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

					// And add the list of file names to our text area
					final java.util.List<?> list = (java.util.List<?>) tr
							.getTransferData(flavor);

					if (list.size() == 0)
						continue;
					final String path = list.get(0).toString();
					// If we made it this far, everything worked.
					dtde.dropComplete(true);

					// Open the target file.
					final File file = new File(path);
					if (file.isDirectory())
						continue;
					openFromXMLFile(file);

					// Open the first file for JGEX and return.
					return;
				}
				// Ok, is it another Java object? Currently not implemented for
				// this.
				else if (flavor.isFlavorSerializedObjectType()) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					tr.getTransferData(flavor);
					dtde.dropComplete(true);
					return;
				}
				// How about an input stream? Currently not implemented for
				// this.
				else if (flavor.isRepresentationClassInputStream()) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					dtde.dropComplete(true);
					return;
				}
			// Hmm, the user must not have dropped a file list
			dtde.rejectDrop();
		} catch (final Exception e) {
			e.printStackTrace();
			dtde.rejectDrop();
		}
	}

	@Override
	public void windowOpened(final WindowEvent e) {
	}

	@Override
	public void windowClosing(final WindowEvent e) {
		if (saveBeforeExit())
			System.exit(0);
	}

	@Override
	public void windowClosed(final WindowEvent e) {
	}

	@Override
	public void windowIconified(final WindowEvent e) {
	}

	@Override
	public void windowDeiconified(final WindowEvent e) {
	}

	@Override
	public void windowActivated(final WindowEvent e) {
	}

	@Override
	public void windowDeactivated(final WindowEvent e) {

	}

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////

	class JPopButtonsPanel extends JPopupMenu implements ActionListener,
			MouseListener, ItemListener, PopupMenuListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3577195096799274338L;
		JToggleButton button, b2, bselect;
		ArrayList<JComponent> vlist = new ArrayList<JComponent>();
		boolean entered = false;

		public JPopButtonsPanel(final JToggleButton button,
				final JToggleButton b2) {
			setLayout(new GridLayout(4, 3, 2, 2));
			this.button = button;
			this.b2 = b2;

			button.addActionListener(this);
			b2.addActionListener(this);
			button.addItemListener(JPopButtonsPanel.this);
			button.addMouseListener(JPopButtonsPanel.this);
			b2.addMouseListener(JPopButtonsPanel.this);
			addMouseListener(JPopButtonsPanel.this);
			addPopupMenuListener(this);
		}

		public void setSelectedButton(final JToggleButton b) {
			bselect = b;
		}

		@Override
		public void itemStateChanged(final ItemEvent e) {
			final int n = e.getStateChange();
			if (n == ItemEvent.SELECTED)
				b2.setSelected(true);
			else {
				b2.setSelected(false);
				b2.getModel().setRollover(false);
				button.getModel().setRollover(false);
			}
		}

		public void add(final JToggleButton b) {
			b.addActionListener(this);
			vlist.add(b);
			super.add(b);
		}

		@Override
		public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
			b2.getModel().setRollover(true);
			b2.getModel().setSelected(true);
		}

		@Override
		public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
		}

		@Override
		public void popupMenuCanceled(final PopupMenuEvent e) {
			b2.getModel().setRollover(button.getModel().isRollover());
			b2.getModel().setSelected(button.getModel().isSelected());
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final JToggleButton bt = (JToggleButton) e.getSource();

			final Object o = e.getSource();
			if (o == b2) {
				for (int i = 0; i < vlist.size(); i++) {
					final JToggleButton b = (JToggleButton) vlist.get(i);
					if (b != bselect)
						b.getModel().setRollover(false);
				}
				this.show(button, 0, button.getHeight());
				b2.setSelected(true);
			} else if (o == button) {
				button.setSelected(true);
				if (bselect != null)
					sendAction(bselect.getActionCommand(), bselect);
			} else {
				final JToggleButton b = (JToggleButton) e.getSource();
				setVisible(false);
				button.setIcon(b.getIcon());
				button.setSelected(true);
				bselect = b;
			}
			bt.repaint();
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
		}

		@Override
		public void mousePressed(final MouseEvent e) {
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			final Object o = e.getSource();
			if ((o == b2) || (o == button)) {
				b2.getModel().setRollover(true);
				button.getModel().setRollover(true);
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			final Object o = e.getSource();
			if ((o == b2) || (o == button))
				if (!b2.isSelected() && !button.isSelected()) {
					b2.getModel().setRollover(false);
					button.getModel().setRollover(false);
				}
		}
	}

	protected static ImageIcon createImageIcon(final String path) {
		final java.net.URL imgURL = DrawPanelFrame.class.getResource(path);
		if (imgURL == null)
			return null;
		return new ImageIcon(imgURL);
	}

	public static URL getResourceURL(final String path) {
		return DrawPanelFrame.class.getResource(path);
	}

	/*
	 * private static void createAndShowGUI() { // APPLET ONLY.
	 * 
	 * Locale.setDefault(Locale.ENGLISH);
	 * 
	 * final GExpert exp = new GExpert();
	 * 
	 * if (CMisc.isApplication()) { exp.init(); }
	 * 
	 * if (!CMisc.isApplet()) {
	 * 
	 * final JFrame frame = exp;
	 * frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	 * frame.pack();
	 * 
	 * final Dimension screenSize = Toolkit.getDefaultToolkit()
	 * .getScreenSize(); frame.setSize(1000, 750);
	 * 
	 * frame.setLocation((int) (screenSize.getWidth() - 1000) / 2, (int)
	 * (screenSize.getHeight() - 750) / 2); // center frame.setVisible(true); }
	 * }
	 */

	public static void setLookAndFeel() {

		if (UtilityMiscellaneous.isApplet())
			return;

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// final String f2 = UIManager.getSystemLookAndFeelClassName();
			// final LookAndFeel f3 = UIManager.getLookAndFeel();

			final String f = UtilityMiscellaneous.lookAndFeel;
			if ((f != null) && (f.length() != 0) && !f.equals("Default")) {
				final UIManager.LookAndFeelInfo[] ff = UIManager
						.getInstalledLookAndFeels();
				for (final LookAndFeelInfo element : ff)
					if (element.getName().equals(f)) {
						UIManager.setLookAndFeel(element.getClassName());
						break;
					}
			}
		} catch (final Exception evt) {
			System.err.println("Error with LookAndFeel selection");
		}

	}

	/*
	 * public static void main(String[] args) {
	 * javax.swing.SwingUtilities.invokeLater(new Runnable() {
	 * 
	 * @Override public void run() { createAndShowGUI(); } }); }
	 */

	/*
	 * public static void openURL(String url) { String osName =
	 * System.getProperty("os.name"); try { if (osName.startsWith("Mac OS")) {
	 * Class fileMgr = Class.forName("com.apple.eio.FileManager"); Method
	 * openURL = fileMgr.getDeclaredMethod("openURL", new
	 * Class[]{String.class}); openURL.invoke(null, new Object[]{url}); } else
	 * if (osName.startsWith("Windows")) {
	 * Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
	 * } else { //assume Unix or Linux String[] browsers = { "firefox", "opera",
	 * "konqueror", "epiphany", "mozilla", "netscape"}; String browser = null;
	 * for (int count = 0; count < browsers.length && browser == null; count++)
	 * { if (Runtime.getRuntime().exec(new String[]{"which",
	 * browsers[count]}).waitFor() == 0) { browser = browsers[count]; } } if
	 * (browser == null) { throw new Exception("Could not find web browser"); }
	 * else { Runtime.getRuntime().exec(new String[]{browser, url}); } } } catch
	 * (Exception e) { JOptionPane.showMessageDialog(null, "Can not open Link "
	 * + url + "\n" + e.getMessage()); } }
	 */

	public static void openURL(final String url) {
		// String osName = System.getProperty("os.name");
		try {
			final java.awt.Desktop desk = java.awt.Desktop.getDesktop();
			desk.browse(new URI(url));
		} catch (final Exception e) {
			JOptionPane.showMessageDialog(null, "Can not open Link " + url
					+ "\n" + e.getMessage());
		}
	}

	public void saveAsPDF() {
		// if (!need_save()) {
		// return;
		// }

		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(final File f) {
				if (f.isDirectory())
					return true;

				final String s = f.getName();
				if (s.endsWith("pdf") || s.endsWith("PDF"))
					return true;
				return false;
			}

			@Override
			public String getDescription() {
				return "Adobe PDF File (*.pdf)";
			}
		});
		final String dr = getUserDir();
		chooser.setCurrentDirectory(new File(dr));
		final int n = chooser.showOpenDialog(this);
		if (n != JFileChooser.OPEN_DIALOG)
			return;

		try {
			File file = chooser.getSelectedFile();
			final String path = file.getPath();
			if (path.endsWith("PDF") || path.endsWith("pdf")) {
			} else
				file = new File(path + ".pdf");
			if (file.exists()) {
				final int n2 = JOptionPane.showConfirmDialog(this,
						file.getName()
								+ " already exists, do you want to overwrite?",
						"File Exists", JOptionPane.YES_NO_CANCEL_OPTION);
				if (n2 != JOptionPane.YES_OPTION)
					return;
			}

			final FileOutputStream fileOutputStream = new FileOutputStream(file);

			final PDFJob job = new PDFJob(fileOutputStream);
			final Graphics pdfGraphics = job.getGraphics();
			d.paintAll(pdfGraphics);
			pdfGraphics.dispose();
			job.end();
			fileOutputStream.close();
		} catch (final IOException ee) {
			JOptionPane.showMessageDialog(this, ee.getMessage());
		}

	}

	@Override
	public void keyTyped(final KeyEvent e) {
	}

	@Override
	public void keyPressed(final KeyEvent e) {
	}

	@Override
	public void keyReleased(final KeyEvent e) {
	}

	class Group extends ButtonGroup {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8313874541970739404L;

		public Group() {
			super();
		}

		public AbstractButton getButton(final String s) {
			if ((s != null) && (s.length() != 0))
				for (final AbstractButton b : buttons) {
					final String s1 = b.getActionCommand();
					if (s.equals(s1))
						return b;
				}
			return null;
		}
	}

	// public void addDependentDialog(JDialog dlg) {
	// if (dlg != null && !depdlglist.contains(dlg))
	// depdlglist.add(dlg);
	// }

	// private void removeAllDependentDialogs() {
	// for (int i = 0; i < depdlglist.size(); i++) {
	// JDialog dlg = (JDialog) depdlglist.get(i);
	// dlg.setVisible(false);
	// }
	// depdlglist.clear();
	// }

	public static void setUIFont(final javax.swing.plaf.FontUIResource f) {

		final java.util.Enumeration<Object> keys = UIManager.getDefaults()
				.keys();
		while (keys.hasMoreElements()) {
			final Object key = keys.nextElement();
			final Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}

	public void start() {
	}

	public void stop() {
	}

	public void destroy() {
	}

	public void updateActionPool(final int n) {
		final ArrayList<GraphicEntity> v = new ArrayList<GraphicEntity>();
		dp.getSelectList(v);
		final int nx = vpoolist.size();

		if (!v.isEmpty())
			for (int i = 0; (i < v.size()) && (i < nx); i++) {
				final OPoolabel lb = vpoolist.get(i);
				lb.setObject(v.get(i));
			}
		else
			setActionPool(n);

	}

	public void setActionPool(final int a) {
		final int nn = dp.getPooln(a);
		final int sz = vpoolist.size();

		// Hide all labels.
		if (nn <= 0)
			for (final JLabel label1 : vpoolist)
				label1.setVisible(false);

		for (int i = 0; i < nn; i++)
			if (i < sz) {
				final OPoolabel lb = vpoolist.get(i);
				lb.setType(dp.getPoolA(a, i + 1));
				lb.setVisible(true);
			} else {
				final OPoolabel lb = new OPoolabel();
				vpoolist.add(lb);
				tipanel.add(lb);
				lb.setType(dp.getPoolA(a, i + 1));
			}

		if ((nn > 0) && (nn < sz))
			for (int i = nn; i < sz; i++) {
				final OPoolabel lb = vpoolist.get(i);
				lb.setVisible(false);
			}
	}

	// public void updateJGEX() {
	// final Update up = new Update(this);
	// if (up.updateJGEX()) {
	// }
	// }

	class OPoolabel extends JLabel implements MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5514033917365217081L;
		private GraphicEntity entity;
		private final Color bc = getForeground();

		public OPoolabel() {
			setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			Font f = getFont();
			f = new Font(f.getName(), Font.BOLD, f.getSize());
			setFont(f);
		}

		public void setType(final int t) {
			if (t == 1) {
				setText("P ");
				setToolTipText("Point");
			} else if (t == 2) {
				setText("L ");
				setToolTipText("Line");
			} else if (t == 3) {
				setText("C ");
				setToolTipText("Circle");
			} else if (t == 4) {
				setText("LC");
				setToolTipText("Line or Circle");
			} else {
				setText("?");
				setToolTipText("Anything");
			}
			setForeground(bc);
		}

		public void setObject(final GraphicEntity ge) {
			if (ge == null /* || !(cc instanceof CPoint) */)
				return;
			String na = ge.getname();
			if (na == null)
				na = " ";
			if (na.length() <= 1)
				na += " ";
			setText(na);
			entity = ge;
			setToolTipText(ge.TypeString());
			setForeground(new Color(0, 128, 192));
		}

		public void clear() {
			setText("");
			setToolTipText("");
			entity = null;
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			if (entity != null)
				dp.setObjectListForFlash(entity);
		}

		@Override
		public void mousePressed(final MouseEvent e) {
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
		}

		@Override
		public void mouseExited(final MouseEvent e) {
		}

	}

	public void onKeyCancel() {
		dp.cancelCurrentAction();
		setActionMove();
		closeAllDialogs();
	}

	public void initKeyMap() {
		final KeyboardFocusManager manager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		manager.addKeyEventPostProcessor(new KeyProcessor());
	}

	class KeyProcessor implements KeyEventPostProcessor {
		@Override
		public boolean postProcessKeyEvent(final KeyEvent event) {
			final int key = event.getKeyCode();
			// final long t = System.currentTimeMillis() - HotKeyTimer;
			// HotKeyTimer = System.currentTimeMillis();
			// if (t < 100) {
			// return true;
			// }

			switch (key) {
			case KeyEvent.VK_ESCAPE:
				onKeyCancel();
				break;
			case KeyEvent.VK_S:
				if (event.isShiftDown())
					dp.stateChange();
				break;
			case KeyEvent.VK_Z:
				if (event.isShiftDown() && event.isControlDown())
					dp.redo_step();
				else if (event.isControlDown())
					dp.Undo_step();
				d.repaint();
				break;
			case KeyEvent.VK_PLUS:
			case KeyEvent.VK_EQUALS:
				if (event.isControlDown()) {
					dp.zoom_in(d.getWidth() / 2, d.getHeight() / 2, 1);
					d.repaint();
				}
				break;
			case KeyEvent.VK_MINUS:
				if (event.isControlDown()) {
					dp.zoom_out(d.getWidth() / 2, d.getHeight() / 2, 1);
					d.repaint();
				}
				break;
			case KeyEvent.VK_G:
				if (event.isControlDown()) {
					dp.DRAWGRID = !dp.DRAWGRID;
					d.repaint();
				}
				break;
			case KeyEvent.VK_D: // For ndgs.
				if (event.isAltDown())
					dp.printNDGS();
				break;
			default:
				break;
			}

			return true;
		}
	}

}

// //////////////////////////////////////////////
// //// End of GExpert.java.
// /////////////////////////////////////////////

class DActionButton extends ActionButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1979936974405335312L;
	/**
	 * 
	 */
	private Icon icon1, icon2;

	public DActionButton(final Icon co) {
		super(co);
	}

	public void set2StatusIcons(final Icon ico1, final Icon ico2) {
		icon1 = ico1;
		icon2 = ico2;
	}

	@Override
	public void setSelected(final boolean b) {
		setIcon(b ? icon2 : icon1);
		super.setSelected(b);
	}

	@Override
	public void setEnabled(final boolean b) {
		super.setEnabled(b);
		setIcon(icon1);
		setSelected(false);
	}
}

class ActionButton extends JToggleButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5108212184451232297L;
	private static EntityButtonUI uib = new EntityButtonUI();
	Dimension dm;

	public ActionButton(final Icon co) {
		super(co);
		setRolloverEnabled(true);
		setOpaque(false);
		this.setUI(uib);
		dm = new Dimension(32, 28);
	}

	public void setButtonSize(final int x, final int y) {
		dm.setSize(x, y);
	}

	@Override
	public Dimension getPreferredSize() {
		return dm;
	}

	@Override
	public Dimension getMaximumSize() {
		return dm;
	}

	// ////////////////////////////////////////////////////////////////////

}

class TStateButton extends JToggleButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4935406781723686825L;
	ImageIcon icon1, icon2;

	public TStateButton(final ImageIcon m1, final ImageIcon m2) {
		super(m1, false);
		icon1 = m1;
		icon2 = m2;
	}

	@Override
	public void setSelected(final boolean b) {
		setIcon(b ? icon2 : icon1);
		super.setSelected(b);
	}
}
