package wprover;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class DialogMiscellaneous extends DialogBase implements FocusListener, ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7299289004557515780L;
	private DrawPanelFrame gxInstance;
    //private String lan;
    private JTabbedPane tpane;

    private DisplayPanel pane1;
    private modePanel pane2;
    //private colorPanel panelc;
    private FontPanel pane3;
    private AnglePanel pane4;
    private boolean onSetting = false;


    public DialogMiscellaneous(DrawPanelFrame gx) {
        super(gx.getFrame(), ("Preference"), false);
        gxInstance = gx;
        //lan = CMisc.lan;


        String s = DrawPanelFrame.getLanguage("Preference");
        this.setTitle(s);

        gxInstance = gx;
//        if (gxInstance != null)
//            gxInstance.addDependentDialog(this);

        JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
        pane.addTab(DrawPanelFrame.getLanguage("Display"), pane1 = createPanelDisply());

        pane.addTab(DrawPanelFrame.getLanguage("Mode"), pane2 = new modePanel());

        //pane.addTab(GExpert.getLanguage("Color"), panelc = new colorPanel());
        pane.addTab(DrawPanelFrame.getLanguage("Font"), pane3 = new FontPanel());
        pane.addTab(DrawPanelFrame.getLanguage("Other"), pane4 = new AnglePanel());
        tpane = pane;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(pane);

        JPanel p2 = new JPanel(new FlowLayout());
        JButton b1 = new JButton("Save Preference");
        b1.setText(DrawPanelFrame.getLanguage(501, "Save Preference"));

        JButton b3 = new JButton("Default");
        b3.setText(DrawPanelFrame.getLanguage(505, "Default"));
        b3.setActionCommand("Default");
        b3.addActionListener(this);

        b1.setActionCommand("Save Preference");

        JButton b2 = new JButton("OK");
        b2.setText(DrawPanelFrame.getLanguage(3204, "OK"));
        b2.setActionCommand("OK");
        b1.addActionListener(this);
        b2.addActionListener(this);
        p2.add(Box.createHorizontalGlue());
        p2.add(b1);
        p2.add(b3);
        p2.add(Box.createHorizontalGlue());
        p2.add(b2);
        panel.add(p2);


        this.addFocusListener(this);
        this.getContentPane().add(panel);
        this.setSize(550, 530);

    }

    public void setSelectedTabbedPane(int n) {
        tpane.setSelectedIndex(n);
    }

    public void init() {
        pane1.init();
        pane2.init();
        pane3.init();
        pane4.init();
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("OK")) {
            this.setVisible(false);

        } else if (command.equals("Default")) {
            onSetting = true;
            UtilityMiscellaneous.Reset();
            init();
            onSetting = false;
        } else if (command.equals("Save Preference")) {

            String s1 = DrawPanelFrame.getUserDir();
            String s2 = DrawPanelFrame.getFileSeparator();

            try {
                @SuppressWarnings("resource")
				OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(new File(s1 + s2 + "Property.x")), "UTF-8");

                UtilityMiscellaneous.SaveProperty(writer);
            } catch (IOException ee) {
                JOptionPane.showMessageDialog(gxInstance, DrawPanelFrame.getLanguage(502, "Can not Save Preference"), "Fail", JOptionPane.WARNING_MESSAGE);
            }
            JOptionPane.showMessageDialog(gxInstance, DrawPanelFrame.getLanguage(503,
                    "Save Preference Successfully") + "\n" +
                    DrawPanelFrame.getLanguage(506, "Please restart the program."),
                    DrawPanelFrame.getLanguage("Saved"), JOptionPane.WARNING_MESSAGE);

            try {

                ProcessBuilder pb = new ProcessBuilder("java", "-jar",
                        DrawPanelFrame.getUserDir() + DrawPanelFrame.getFileSeparator() + "jgex.jar");
                pb.directory(new File(DrawPanelFrame.getUserDir()));
                //Map<String, String> map = pb.environment();
                //Process p = 
                pb.start();
                System.exit(0);
            } catch (IOException e0) {
            }
        }

    }


    private DisplayPanel createPanelDisply() {
        return new DisplayPanel();
    }


    class DisplayPanel extends JPanel {

        /**
		 * 
		 */
		private static final long serialVersionUID = -8026587391007958876L;
		private JLabel text;
        private JRadioButton b1, b2, b3;
        private JSlider slider, slider1;
        private JCheckBox bts, bft;
        private JSpinner spinner;

        public DisplayPanel() {
            this.setLayout(new GridLayout(5, 1));
            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p1.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage(358, "Polygon Alpha")));
            float f = UtilityMiscellaneous.getFillCompositeAlpha();
            int n = 100 - (int) (f * 100);

            slider = new JSlider(0, 100);
            slider.setValue(n);
            slider.setPaintTicks(true);
            slider.setMinorTickSpacing(1);
            slider.setMajorTickSpacing(20);
            slider.setPaintTrack(true);
            slider.setPaintLabels(true);

            p1.add(slider);
            slider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (onSetting) return;
                    JSlider slider = (JSlider) e.getSource();
                    int n = slider.getValue();
                    float f = (100 - n) / 100.0f;
                    UtilityMiscellaneous.setFillCompositeAlpha(f);
                    text.setText(new Integer(n).toString());
                    gxInstance.d.repaint();
                }
            });

            p1.add((text = new JLabel()));
            //   p1.add(Box.createHorizontalGlue());
            text.setText(new Integer(n).toString());

            p1.add(Box.createHorizontalGlue());
            this.add(p1);

            JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            slider1 = new JSlider(0, 20);
            slider1.setValue(UtilityMiscellaneous.getPointRadius());
            slider1.setPaintTicks(true);
            slider1.setMinorTickSpacing(1);
            slider1.setMajorTickSpacing(5);
            slider1.setPaintTrack(true);
            slider1.setPaintLabels(true);

            p2.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage(359, "Radius of Point")));
            p2.add(slider1);
            slider1.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (onSetting) return;
                    JSlider slider = (JSlider) e.getSource();

                    int n = slider.getValue();
                    UtilityMiscellaneous.setPointRadius(n);
                    gxInstance.d.repaint();
                }
            });
            p2.add(Box.createHorizontalGlue());
            this.add(p2);

            JPanel p3 = new JPanel();
            p3.setLayout(new FlowLayout(FlowLayout.LEADING));
            p3.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage(360, "Point's Text")));
            JButton button = new JButton(DrawPanelFrame.getLanguage(362, "Default Font"));
            button.setText(UtilityMiscellaneous.nameFont.getName() + " " + UtilityMiscellaneous.nameFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            DialogVFontChooser.showDialog(gxInstance, UtilityMiscellaneous.nameFont,
                                    DrawPanelFrame.getLanguage(1024, "Choose default foot for point's text"), Color.black)) {
                        UtilityMiscellaneous.nameFont = DialogVFontChooser.getReturnFont();
                        JButton b = (JButton) e.getSource();
                        b.setText(UtilityMiscellaneous.nameFont.getName() + " " + UtilityMiscellaneous.nameFont.getSize());
                    }
                }

            });

            JCheckBox b = bts = new JCheckBox(DrawPanelFrame.getLanguage(361, "Show Text"));
            b.setSelected(UtilityMiscellaneous.nameTextShown);
            b.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (onSetting) return;
                    UtilityMiscellaneous.nameTextShown = ((JCheckBox) e.getSource()).isSelected();
                    gxInstance.d.repaint();
                }


            });

            p3.add(b);
            p3.add(Box.createHorizontalStrut(10));
            p3.add(button);
            p3.add(Box.createHorizontalStrut(10));
            this.add(p3);


            JPanel p5 = new JPanel();
            p5.setLayout(new FlowLayout(FlowLayout.LEADING));
            p5.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage(363, "Angle Text")));
            ButtonGroup bg = new ButtonGroup();
            ItemListener listener = new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (onSetting) return;
                    Object obj = e.getSource();
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if (obj == b1) {
                            UtilityMiscellaneous.show_angle_type = 0;
                        } else if (obj == b2) {
                            UtilityMiscellaneous.show_angle_type = 1;
                        } else if (obj == b3) {
                            UtilityMiscellaneous.show_angle_type = 2;
                        }
                    }
                }
            };
            b1 = new JRadioButton(DrawPanelFrame.getLanguage("None"));
            b1.addItemListener(listener);
            bg.add(b1);
            p5.add(b1);
            p5.add((b2 = new JRadioButton(DrawPanelFrame.getLanguage("Label"))));
            bg.add(b2);
            b2.addItemListener(listener);
            p5.add((b3 = new JRadioButton(DrawPanelFrame.getLanguage("Degrees"))));
            bg.add(b3);
            b3.addItemListener(listener);
            switch (UtilityMiscellaneous.show_angle_type) {
                case 0:
                    b1.setSelected(true);
                    break;
                case 1:
                    b2.setSelected(true);
                    break;
                case 2:
                    b3.setSelected(true);
                    break;
            }
            button = new JButton(DrawPanelFrame.getLanguage("Font"));
            button.setText(UtilityMiscellaneous.angleNameFont.getName() + " " + UtilityMiscellaneous.angleNameFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    if (JOptionPane.OK_OPTION ==
                            DialogVFontChooser.showDialog(gxInstance, UtilityMiscellaneous.angleNameFont, Color.black)) {
                        UtilityMiscellaneous.angleNameFont = DialogVFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(UtilityMiscellaneous.angleNameFont.getName() + " " + UtilityMiscellaneous.angleNameFont.getSize());
                    }
                }

            });
            p5.add(Box.createHorizontalStrut(10));
            p5.add(button);
            this.add(p5);

            JPanel p6 = new JPanel();
            p6.setLayout(new FlowLayout(FlowLayout.LEADING));
            p6.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage(367, "Foot Mark")));
            JCheckBox bx = bft = new JCheckBox(DrawPanelFrame.getLanguage(368, "Show foot mark"));
            bx.setSelected(UtilityMiscellaneous.footMarkShown);

            bx.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    UtilityMiscellaneous.footMarkShown = ((JCheckBox) e.getSource()).isSelected();
                    gxInstance.d.repaint();
                }
            });
            p6.add(bx);
            p6.add(Box.createHorizontalStrut(10));
            p6.add(new JLabel(DrawPanelFrame.getLanguage("Length") + ":  "));
            JSpinner spin = spinner = new JSpinner();
            spin.setValue(UtilityMiscellaneous.FOOT_MARK_LENGTH);
            spin.addChangeListener(new ChangeListener() {


                public void stateChanged(ChangeEvent e) {
                    if (onSetting) return;
                    Object obj = ((JSpinner) e.getSource()).getValue();
                    int n = Integer.parseInt(obj.toString());
                    UtilityMiscellaneous.FOOT_MARK_LENGTH = n;
                    gxInstance.d.repaint();
                }
            });

            p6.add(spin);

            this.add(p6);
        }

        public void init() {
            float f = UtilityMiscellaneous.getFillCompositeAlpha();
            int n = 100 - (int) (f * 100);


            bts.setSelected(UtilityMiscellaneous.nameTextShown);

            switch (UtilityMiscellaneous.show_angle_type) {
                case 0:
                    b1.setSelected(true);
                    break;
                case 1:
                    b2.setSelected(true);
                    break;
                case 2:
                    b3.setSelected(true);
                    break;
            }
            bft.setSelected(UtilityMiscellaneous.footMarkShown);
            spinner.setValue(UtilityMiscellaneous.FOOT_MARK_LENGTH);


            slider.setValue(n);
            slider1.setValue(UtilityMiscellaneous.getPointRadius());
        }

    }

    class AnglePanel extends JPanel implements ItemListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = -1164591080069536039L;
		JRadioButton ba, bwa, bma, bfill;
        JSlider slider;


        public AnglePanel() {
            this.setLayout(new GridLayout(5, 1));
            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p1.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage("Angle")));

            ButtonGroup bg = new ButtonGroup();
            ba = new JRadioButton(DrawPanelFrame.getLanguage(383, "Without Arrow"));
            bg.add(ba);
            bwa = new JRadioButton(DrawPanelFrame.getLanguage(384, "With Arrow"));
            bg.add(bwa);
            bma = new JRadioButton(DrawPanelFrame.getLanguage(385, "Multiple Arc"));
            bg.add(bma);
            bfill = new JRadioButton(DrawPanelFrame.getLanguage(386, "Fill"));


            ba.addItemListener(this);
            bwa.addItemListener(this);
            bma.addItemListener(this);
            bfill.addItemListener(this);
            bg.add(bfill);
            p1.add(ba);
            p1.add(bwa);
            p1.add(bma);
            p1.add(bfill);
            this.add(p1);

            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage(381, "Polygon Moving Interval")));
            p1.setLayout(new FlowLayout(FlowLayout.LEFT));

            int d = UtilityMiscellaneous.getMoveStep();
            slider = new JSlider(0, 20);
            slider.setValue(d);
            slider.setPaintTicks(true);
            slider.setMinorTickSpacing(1);
            slider.setMajorTickSpacing(4);
            slider.setMinimum(2);
            slider.setPaintTrack(true);
            slider.setPaintLabels(true);

            p1.add(slider);
            p1.add(Box.createHorizontalGlue());
            slider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (onSetting) return;

                    JSlider slider = (JSlider) e.getSource();
                    int n = slider.getValue();
                    UtilityMiscellaneous.setMoveStep(n);
                    gxInstance.dp.recal_allFlash();
                }
            });
            this.add(p1);
            init();
        }

        public void itemStateChanged(ItemEvent e) {
            if (onSetting) return;
            Object obj = e.getSource();
            if (obj == ba || obj == bwa || obj == bma || obj == bfill) {
                if (ba.isSelected())
                    UtilityMiscellaneous.ANGLE_TYPE = 0;
                else if (bwa.isSelected())
                    UtilityMiscellaneous.ANGLE_TYPE = 1;
                else if (bma.isSelected())
                    UtilityMiscellaneous.ANGLE_TYPE = 2;
                else if (bfill.isSelected())
                    UtilityMiscellaneous.ANGLE_TYPE = 3;
            }
        }

        public void init() {
            ba.setSelected(UtilityMiscellaneous.ANGLE_TYPE == 0);
            bwa.setSelected(UtilityMiscellaneous.ANGLE_TYPE == 1);
            bma.setSelected(UtilityMiscellaneous.ANGLE_TYPE == 2);
            bfill.setSelected(UtilityMiscellaneous.ANGLE_TYPE == 3);
            int d = UtilityMiscellaneous.getMoveStep();
            slider.setValue(d);
        }
    }


    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
        this.setVisible(false);
    }

    class FontPanel extends JPanel {


        /**
		 * 
		 */
		private static final long serialVersionUID = -4310873442390912768L;


		public FontPanel() {
            this.setLayout(new GridLayout(3, 2));

            JPanel p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage(360, "Point's Text")));
            JButton button = new JButton("PTEXT");
            button.setText(UtilityMiscellaneous.nameFont.getName() + " " + UtilityMiscellaneous.nameFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            DialogVFontChooser.showDialog(gxInstance, UtilityMiscellaneous.nameFont, Color.black)) {
                        UtilityMiscellaneous.nameFont = DialogVFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(UtilityMiscellaneous.nameFont.getName() + " " + UtilityMiscellaneous.nameFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);


            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder("THM - " + DrawPanelFrame.getLanguage(3100, "Theorem")));
            button = new JButton("THM");
            button.setText(UtilityMiscellaneous.thmFont.getName() + " " + UtilityMiscellaneous.thmFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            DialogVFontChooser.showDialog(gxInstance, UtilityMiscellaneous.thmFont, Color.black)) {
                        UtilityMiscellaneous.thmFont = DialogVFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(UtilityMiscellaneous.thmFont.getName() + " " + UtilityMiscellaneous.thmFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);

            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder("F - D"));// + GExpert.getLanguage(3002, "Full Angle Method")
//                    + "-" + GExpert.getLanguage(3001, "Deductive Datab?ase Method")));
            // p1.setLayout(new FlowLayout(FlowLayout.LEFT));
            button = new JButton("Full");
            button.setText(UtilityMiscellaneous.fullFont.getName() + " " + UtilityMiscellaneous.fullFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            DialogVFontChooser.showDialog(gxInstance, UtilityMiscellaneous.fullFont, Color.black)) {
                        UtilityMiscellaneous.fullFont = DialogVFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(UtilityMiscellaneous.fullFont.getName() + " " + UtilityMiscellaneous.fullFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);

            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder("Area - " + DrawPanelFrame.getLanguage(3004, "Area Method")));
            // p1.setLayout(new FlowLayout(FlowLayout.LEFT));
            button = new JButton("Area");
            button.setText(UtilityMiscellaneous.areaFont.getName() + " " + UtilityMiscellaneous.areaFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            DialogVFontChooser.showDialog(gxInstance, UtilityMiscellaneous.areaFont, Color.black)) {
                        UtilityMiscellaneous.areaFont = DialogVFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(UtilityMiscellaneous.areaFont.getName() + " " + UtilityMiscellaneous.areaFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);

            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder("Manual - " + DrawPanelFrame.getLanguage(3007, "Manual Method")));
            // p1.setLayout(new FlowLayout(FlowLayout.LEFT));
            button = new JButton("Manual");
            button.setText(UtilityMiscellaneous.manualFont.getName() + " " + UtilityMiscellaneous.manualFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            DialogVFontChooser.showDialog(gxInstance, UtilityMiscellaneous.manualFont, Color.black)) {
                        UtilityMiscellaneous.manualFont = DialogVFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(UtilityMiscellaneous.manualFont.getName() + " " + UtilityMiscellaneous.manualFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);

            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder("Fix - " + DrawPanelFrame.getLanguage(307, "Fixpoint")));
            // p1.setLayout(new FlowLayout(FlowLayout.LEFT));
            button = new JButton("Fixpoint");
            button.setText(UtilityMiscellaneous.fixFont.getName() + " " + UtilityMiscellaneous.fixFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            DialogVFontChooser.showDialog(gxInstance, UtilityMiscellaneous.fixFont, Color.black)) {
                        UtilityMiscellaneous.fixFont = DialogVFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(UtilityMiscellaneous.fixFont.getName() + " " + UtilityMiscellaneous.fixFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);

            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder("Algebra - " + DrawPanelFrame.getLanguage(1111, "Algebra")));
            //
            //  p1.setLayout(new FlowLayout(FlowLayout.LEFT));
            button = new JButton("Algebra");
            button.setText(UtilityMiscellaneous.algebraFont.getName() + " " + UtilityMiscellaneous.algebraFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            DialogVFontChooser.showDialog(gxInstance, UtilityMiscellaneous.algebraFont, Color.black)) {
                        UtilityMiscellaneous.algebraFont = DialogVFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(UtilityMiscellaneous.algebraFont.getName() + " " + UtilityMiscellaneous.algebraFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);


        }


        public void init() {

        }

    }

    class colorPanel extends JPanel implements ItemListener, MouseListener {

        /**
		 * 
		 */
		private static final long serialVersionUID = 2313722906344236935L;
		private JRadioButton b1, b2, b3;
        private ColorPane pbk, pgrid;

        public colorPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p1.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage(388, "Color Mode")));

            ButtonGroup bg = new ButtonGroup();
            b1 = new JRadioButton(DrawPanelFrame.getLanguage("Colorful"));
            b2 = new JRadioButton(DrawPanelFrame.getLanguage("Gray"));
            b3 = new JRadioButton(DrawPanelFrame.getLanguage(352, "Black and White"));
            {
                int n = UtilityMiscellaneous.ColorMode;
                if (n == 0)
                    b1.setSelected(true);
                else if (n == 1)
                    b2.setSelected(true);
                else b3.setSelected(true);
            }


            bg.add(b1);
            bg.add(b2);
            bg.add(b3);
            p1.add(b1);
            p1.add(b2);
            p1.add(b3);
            b1.addItemListener(this);
            b2.addItemListener(this);
            b3.addItemListener(this);
            p1.add(Box.createHorizontalGlue());


            JPanel p3 = new JPanel(new GridLayout(1, 2));
            p3.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage("Color")));

            pbk = new ColorPane(100, 30);
            pbk.addMouseListener(this);
            JPanel p31 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p31.add(new JLabel(DrawPanelFrame.getLanguage(377, "BackGroud Color")));
            p31.add(pbk);

            pgrid = new ColorPane(100, 30);
            pgrid.addMouseListener(this);
            JPanel p32 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p32.add(new JLabel(DrawPanelFrame.getLanguage("Grid")));
            p32.add(pgrid);

            p3.add(p31);
            p3.add(p32);

            this.add(p1);
            this.add(p3);
            init();

        }


        public void init() {

            int n = UtilityMiscellaneous.ColorMode;
            if (n == 0)
                b1.setSelected(true);
            else if (n == 1)
                b2.setSelected(true);
            else b3.setSelected(true);

            pbk.setBackground(UtilityMiscellaneous.getBackGroundColor());
            pgrid.setBackground(UtilityMiscellaneous.getGridColor());
//            gxInstance.d.setBackground(CMisc.getBackGroundColor());
            this.repaint();
        }

        public void itemStateChanged(ItemEvent e) {
            Object o = e.getSource();
            if (o == b1 || o == b2 || o == b3) {
                if (b1.isSelected()) {
                    UtilityMiscellaneous.ColorMode = 0;
                } else if (b2.isSelected()) {
                    UtilityMiscellaneous.ColorMode = 1;
                } else if (b3.isSelected()) {
                    UtilityMiscellaneous.ColorMode = 2;
                }
            }
            gxInstance.d.repaint();


        }

        public void mouseClicked(MouseEvent e) {
            Object o = e.getSource();
            if (o == pbk) {
                Color newColor = JColorChooser.showDialog(gxInstance,
                        DrawPanelFrame.getLanguage(379, "Choose Color"), UtilityMiscellaneous.getBackGroundColor());
                if (newColor != null) {
                    Color c = newColor;
                    UtilityMiscellaneous.setBackGroundColor(c);
                    gxInstance.d.setBackground(c);
                    pbk.setBackground(c);
                    pbk.repaint();
                }

            } else if (o == pgrid) {
                Color newColor = JColorChooser.showDialog(gxInstance,
                        DrawPanelFrame.getLanguage(379, "Choose Color"), UtilityMiscellaneous.getBackGroundColor());
                if (newColor != null) {
                    Color c = newColor;
                    UtilityMiscellaneous.setGridColor(c);
                    gxInstance.d.repaint();
                    pgrid.setBackground(c);
                    pgrid.repaint();
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

    class ColorPane extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 2786810685421339320L;
		int w, h;

        public ColorPane(int w, int h) {
            this.w = w;
            this.h = h;
            this.setBorder(new LineBorder(Color.lightGray, 1));
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public Dimension getPreferredSize() {
            return new Dimension(w, h);
        }
    }

    class modePanel extends JPanel implements ItemListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = 454715029776352664L;
		private JRadioButton r1, r2;
        private JComboBox<String> blanguage, blook;

        modePanel() {

            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p2.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage(371, "AntiAlias")));
            ButtonGroup bg1 = new ButtonGroup();
            r1 = new JRadioButton(DrawPanelFrame.getLanguage("ON"));
            r2 = new JRadioButton(DrawPanelFrame.getLanguage("OFF"));
            if (UtilityMiscellaneous.AntiAlias)
                r1.setSelected(true);
            else r2.setSelected(true);
            r1.addItemListener(this);
            r2.addItemListener(this);
            bg1.add(r1);
            bg1.add(r2);
            p2.add(r1);
            p2.add(r2);


            JPanel p4 = new JPanel(new FlowLayout(FlowLayout.LEFT));

            p4.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage("Language")));
            String[] lan = {
                    "English",
                    "Chinese",
                    "Japanese",
                    "French",
                    "German",
                    "Italian",
                    "Spaish",
                    "Persian"
            };
            blanguage = new JComboBox<String>(lan);
            blanguage.setSelectedItem(UtilityMiscellaneous.lan);
            p4.add(blanguage);
            blanguage.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (blanguage.getSelectedIndex() != -1)
                        UtilityMiscellaneous.lan = blanguage.getSelectedItem().toString();
                }
            });

            JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p5.setBorder(BorderFactory.createTitledBorder(DrawPanelFrame.getLanguage("LookAndFeel")));
            UIManager.LookAndFeelInfo[] ff = UIManager.getInstalledLookAndFeels();
            String ss[] = new String[ff.length + 1];
            ss[0] = "Default";


            for (int i = 1; i < ff.length + 1; i++)
                ss[i] = ff[i - 1].getName();

            blook = new JComboBox<String>(ss);
            blook.setSelectedItem(UtilityMiscellaneous.lookAndFeel);
            p5.add(blook);
            blook.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (onSetting) return;
                    if (blook.getSelectedIndex() != -1)
                        UtilityMiscellaneous.lookAndFeel = blook.getSelectedItem().toString();
                }
            });


            this.add(p2);
            this.add(p4);
            this.add(p5);
            this.revalidate();
        }

        public void itemStateChanged(ItemEvent e) {
            Object o = e.getSource();
            if (o == r1 || o == r2) {
                if (r1.isSelected())
                    UtilityMiscellaneous.AntiAlias = true;
                else UtilityMiscellaneous.AntiAlias = false;
            }
            gxInstance.d.repaint();
        }


        public void init() {


            blanguage.setSelectedItem(UtilityMiscellaneous.lan);
            blook.setSelectedItem(UtilityMiscellaneous.lookAndFeel);
            gxInstance.d.setBackground(UtilityMiscellaneous.getBackGroundColor());
        }
    }
}
