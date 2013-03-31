package wprover;

import gprover.gib;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import UI.EntityButtonUI;

public class GApplet1 extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 487248913584781081L;
	private boolean hasProof = false;
	private boolean hasProofPane = false;
	private int PWIDTH = 300;

	public DPanel d;
	public drawTextProcess dp;
	public ProofPanel pproof;

	@Override
	public void init() {

		try {
			setAppletGridColor();
			CMisc.initFont();
			msymbol.createAllIcons();
			//RuleList.loadRules();
			gib.initRules();
			CMisc.homedir = getDocumentBase();

			d = new DPanel(null);
			dp = new drawTextProcess();
			d.dp = dp;

			this.setSize(getAppletWidth(), getAppletHeight());
			d.setBackground(getAppletBackGround());

			getPPWidth();
			addProof();

			if (hasProof) {
				pproof = new ProofPanel(null, d, dp, false, 1) {
					/**
					 * 
					 */
					private static final long serialVersionUID = 3658312315702968250L;

					@Override
					public Dimension getMaximumSize() {
						final Dimension dm = super.getMaximumSize();
						// double w = dm.getWidth() < PWIDTH ? dm.getWidth() :
						// PWIDTH;
						dm.setSize(PWIDTH, dm.getHeight());
						return dm;
					}

				};
				pproof.setMember(d, dp);
				pproof.setApplet1(this);
				dp.setCurrentDrawPanel(d);
				addProveStepBar();
			}

			// this.loadfile();
			if (pproof != null)
				dp.gt = pproof.getConstructionTerm();
			addAnimation();
			addProofStatus();
			setAppletGrid();

			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					createGUI();
				}
			});
		} catch (final Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}

		addResetButton();
		dp.SetCurrentAction(drawProcess.MOVE);
		// d.repaint();

	}

	private void createGUI() {
		if (!hasProofPane)
			getContentPane().add(d, BorderLayout.CENTER);
		else {
			final JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.add(pproof);
			panel.add(d);
			// JSplitPane panel = new
			// JSplitPane(JSplitPane.HORIZONTAL_SPLIT,pproof,d);
			// panel.resetToPreferredSizes();
			getContentPane().add(panel, BorderLayout.CENTER);

		}
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void destroy() {
		dp.clearAll();
		d.removeAll();
		super.destroy();
	}

	@Override
	public boolean isActive() {
		return super.isActive();
	}

	// ///////////////////////////////////////

	private void addResetButton() {
		final String s = getParameter("reset_button");
		if (s == null)
			return;
		if (!Boolean.parseBoolean(s))
			return;
		final BReset br = new BReset();
		final Dimension dm = br.getPreferredSize();
		d.add(br);
		br.setBounds(0, 0, (int) dm.getWidth(), (int) dm.getHeight());
	}

	private Color getAppletBackGround() {
		final String s = getParameter("background");
		if (s == null)
			return null;
		return new Color(Integer.parseInt(s));

	}

	private int getAppletWidth() {
		final String s = getParameter("Width");
		if (s == null)
			return 1000;
		return Integer.parseInt(s);
	}

	private int getAppletHeight() {
		final String s = getParameter("Height");
		if (s == null)
			return 750;
		return Integer.parseInt(s);
	}

	private void getPPWidth() {
		final String s = getParameter("pwidth");
		if (s != null)
			PWIDTH = Integer.parseInt(s);
	}

	// private String getAppletFilename() {
	// String s = this.getParameter("filename");
	// return s;
	// }

	// private boolean getAppletAnimated() {
	// String s = this.getParameter("animated");
	// return Boolean.parseBoolean(s);
	// }

	private boolean getAnimationTool() {
		final String s = getParameter("animationbar");
		return Boolean.parseBoolean(s);
	}

	private void addAnimation() {
		if (getAnimationTool()) {
			final AnimateC c = dp.getAnimateC();
			if (c != null) {
				final AnimatePanel bar = new AnimatePanel(null, d, dp); // null,
																		// false);
				bar.setAttribute(c);
				final Dimension dm = bar.getPreferredSize();
				bar.setEnableAll();
				d.add(bar);
				bar.setBounds(0, getHeight() - (int) dm.getHeight(),
						(int) dm.getWidth(), (int) dm.getHeight());
			}
		}

	}

	private void addProveStepBar() {
		final String s = getParameter("provestepbar");
		if (Boolean.parseBoolean(s)) {
			final ProveBar bar = new ProveBar(null, d, dp, pproof);
			final Dimension dm = bar.getPreferredSize();
			d.add(bar);
			bar.setBounds(0, getHeight() - (int) dm.getHeight(),
					(int) dm.getWidth(), (int) dm.getHeight());
		}
	}

	private void addProof() {
		final String s1 = getParameter("has_proof");
		final String s2 = getParameter("ppane");
		hasProof = Boolean.parseBoolean(s1);
		if (hasProof)
			hasProofPane = Boolean.parseBoolean(s2);
	}

	private void addProofStatus() {
		final String s = getParameter("pindex");
		if (hasProof && hasProofPane && (s != null)) {
			final int n = Integer.parseInt(s);
			pproof.setProofStatus(n);
		}
	}

	// private boolean loadfile() {
	// String filename = this.getAppletFilename();
	//
	// try {
	// URL ul = new URL(this.getDocumentBase(), filename);
	// URLConnection urlc = ul.openConnection();
	// urlc.connect();
	// InputStream instream = urlc.getInputStream();
	// DataInputStream in = new DataInputStream(instream);
	// dp.Load(in);
	// if (CMisc.version_load_now >= 0.036 && pproof != null) {
	// pproof.LoadProve(in);
	// }
	// // in.close();
	// instream.close();
	// dp.SetCurrentAction(drawProcess.MOVE);
	// dp.reCalculate();
	// } catch (Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	// return true;
	//
	// }

	public void setAppletGrid() {
		final String sx = getParameter("showgrid");
		if ((sx != null) && Boolean.parseBoolean(sx)) {
			final String s = getParameter("grid");
			if (s != null) {
				final int n = Integer.parseInt(s);
				dp.SetGrid(true);
				dp.setGridXY(n);
			}
		}
	}

	public void setAppletGridColor() {
		final String s = getParameter("gridcolor");
		if (s != null) {
			final int n = Integer.parseInt(s);
			CMisc.setGridColor(new Color(n));
		}
	}

	// public void setPointFrozen() {
	/*
	 * String s = this.getParameter("freezeAll"); if (s != null) { if
	 * (Boolean.parseBoolean(s)) { String s1 = this.getParameter("freept");
	 * 
	 * } }
	 */// }

	protected JToggleButton makeAButton(final String imageName,
			final String actionCommand, final String toolTipText,
			final String altText) {
		final String imgLocation = "images/" + imageName + ".gif";
		final URL imageURL = GExpert.class.getResource(imgLocation);
		Icon co = null;
		if (imageURL != null)
			co = (new ImageIcon(imageURL, altText));
		final JToggleButton button = new ActionButton(co);
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.setText(null);
		return button;
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

	class BReset extends JToolBar implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7166828464499071599L;

		public BReset() {
			setFloatable(false);
			setBorder(null);
			final JButton b = new JButton(
					GExpert.createImageIcon("images/other/reset.gif"));
			b.setToolTipText("Reset");
			b.setUI(new EntityButtonUI());
			b.addActionListener(this);
			this.add(b);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			dp.clearAll();
			// loadfile();
			d.repaint();
		}
	}
}
