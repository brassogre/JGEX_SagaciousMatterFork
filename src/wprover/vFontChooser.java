package wprover;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import static java.awt.font.TextAttribute.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-6-28
 * Time: 22:01:58
 * To change this template use File | Settings | File Templates.
 */


public class vFontChooser extends JBaseDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8551572733547553840L;

	protected int Closed_Option = JOptionPane.CLOSED_OPTION;

    protected InputList fontNameInputList = new InputList(fontNames, "Name:");

    protected InputList fontSizeInputList = new InputList(fontSizes, "Size:");

    protected MutableAttributeSet attributes;

    protected JCheckBox boldCheckBox;

    protected JCheckBox italicCheckBox;

    protected JCheckBox underlineCheckBox;

    protected JCheckBox strikethroughCheckBox;

    protected JCheckBox subscriptCheckBox;

    protected JCheckBox superscriptCheckBox;

    protected ColorComboBox colorComboBox;

    protected FontLabel previewLabel;

    public static String[] fontNames;

    public static String[] fontSizes;

    private String PREVIEW_TEXT;
    private static vFontChooser chooser;
    private GExpert gxInstance;


    private vFontChooser(GExpert owner) {
        super(owner.getFrame(), "Font Chooser", true);
        gxInstance = owner;
        this.setTitle(getLanguage(601, "Font Chooser"));

        boldCheckBox = new JCheckBox(getLanguage(604, "Bold"));
        italicCheckBox = new JCheckBox(getLanguage(605, "Italic"));
        underlineCheckBox = new JCheckBox(getLanguage(606, "Underline"));
        strikethroughCheckBox = new JCheckBox(getLanguage(607, "Strikethrough"));
        subscriptCheckBox = new JCheckBox(getLanguage(608, "Subscript"));
        superscriptCheckBox = new JCheckBox(getLanguage(609, "Superscript"));
        PREVIEW_TEXT = getLanguage(610, "Preview Font");


        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel p = new JPanel(new GridLayout(1, 2, 10, 2));
        p.setBorder(new TitledBorder(new EtchedBorder(), getLanguage(602, "Font")));
        p.add(fontNameInputList);
        fontNameInputList.setDisplayedMnemonic('n');
        fontNameInputList.setToolTipText(getLanguage(611, "Font name"));

        p.add(fontSizeInputList);
        fontSizeInputList.setDisplayedMnemonic('s');
        fontSizeInputList.setToolTipText(getLanguage(612, "Font size"));
        getContentPane().add(p);

        p = new JPanel(new GridLayout(2, 3, 10, 5));
        p.setBorder(new TitledBorder(new EtchedBorder(), getLanguage(603, "Effects")));
        boldCheckBox.setMnemonic('b');
        boldCheckBox.setToolTipText("Bold font");
        p.add(boldCheckBox);

        italicCheckBox.setMnemonic('i');
        italicCheckBox.setToolTipText("Italic font");
        p.add(italicCheckBox);

        underlineCheckBox.setMnemonic('u');
        underlineCheckBox.setToolTipText("Underline font");
        p.add(underlineCheckBox);

        strikethroughCheckBox.setMnemonic('r');
        strikethroughCheckBox.setToolTipText("Strikethrough font");
        p.add(strikethroughCheckBox);

        subscriptCheckBox.setMnemonic('t');
        subscriptCheckBox.setToolTipText("Subscript font");
        p.add(subscriptCheckBox);

        superscriptCheckBox.setMnemonic('p');
        superscriptCheckBox.setToolTipText("Superscript font");
        p.add(superscriptCheckBox);
        getContentPane().add(p);

        getContentPane().add(Box.createVerticalStrut(5));
        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalStrut(10));
        JLabel lbl = new JLabel(getLanguage("Color"));
        lbl.setDisplayedMnemonic('c');
        p.add(lbl);
        p.add(Box.createHorizontalStrut(20));
        colorComboBox = new ColorComboBox();
        lbl.setLabelFor(colorComboBox);
        colorComboBox.setToolTipText(getLanguage(613, "Font color"));
        ToolTipManager.sharedInstance().registerComponent(colorComboBox);
        p.add(colorComboBox);
        p.add(Box.createHorizontalStrut(10));
        getContentPane().add(p);

        p = new JPanel(new BorderLayout());
        p.setBorder(new TitledBorder(new EtchedBorder(), getLanguage(614, "Preview")));
        previewLabel = new FontLabel(PREVIEW_TEXT);

        p.add(previewLabel, BorderLayout.CENTER);
        getContentPane().add(p);

        p = new JPanel(new FlowLayout());
        JPanel p1 = new JPanel(new GridLayout(1, 2, 10, 2));
        JButton btOK = new JButton(getLanguage(3204, "OK"));
        btOK.setToolTipText(getLanguage(615, "Save and exit"));
        getRootPane().setDefaultButton(btOK);
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Closed_Option = JOptionPane.OK_OPTION;
                dispose();
            }
        };
        btOK.addActionListener(actionListener);
        p1.add(btOK);

        JButton btCancel = new JButton(getLanguage("Cancel"));
        btCancel.setToolTipText(getLanguage(616, "Exit without save"));
        actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Closed_Option = JOptionPane.CANCEL_OPTION;
                dispose();
            }
        };
        btCancel.addActionListener(actionListener);
        p1.add(btCancel);
        p.add(p1);
        getContentPane().add(p);

//        pack();
        setResizable(false);

        ListSelectionListener listSelectListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updatePreview();
            }
        };
        fontNameInputList.addListSelectionListener(listSelectListener);
        fontSizeInputList.addListSelectionListener(listSelectListener);

        actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePreview();
            }
        };
        boldCheckBox.addActionListener(actionListener);
        italicCheckBox.addActionListener(actionListener);
        colorComboBox.addActionListener(actionListener);
        underlineCheckBox.addActionListener(actionListener);
        strikethroughCheckBox.addActionListener(actionListener);
        subscriptCheckBox.addActionListener(actionListener);
        superscriptCheckBox.addActionListener(actionListener);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fontNames = ge.getAvailableFontFamilyNames();
        fontSizes = new String[]{"8", "9", "10", "11", "12", "14", "15", "16",
                "17", "18", "19", "20", "21", "22", "24", "26", "28", "36", "48", "72"};

//        SimpleAttributeSet a = new SimpleAttributeSet();
//        StyleConstants.setFontFamily(a, "Monospaced");
//        StyleConstants.setFontSize(a, 12);
//        this.setAttributes(a);
    }


    String getLanguage(String s) {
        if (gxInstance != null)
            return GExpert.getLanguage(s);
        return s;
    }

    public String getLanguage(int n, String s) {
        String s1 = "";
        if (gxInstance != null)
            s1 = GExpert.getLanguage(n);
        if (s1 != null && s1.length() > 0)
            return s1;
        return s;
    }


    private void setAttributes(AttributeSet a) {
        attributes = new SimpleAttributeSet(a);
        String name = StyleConstants.getFontFamily(a);
        fontNameInputList.setSelected(name);
        int size = StyleConstants.getFontSize(a);
        fontSizeInputList.setSelectedInt(size);
        boldCheckBox.setSelected(StyleConstants.isBold(a));
        italicCheckBox.setSelected(StyleConstants.isItalic(a));
        underlineCheckBox.setSelected(StyleConstants.isUnderline(a));
        strikethroughCheckBox.setSelected(StyleConstants.isStrikeThrough(a));
        subscriptCheckBox.setSelected(StyleConstants.isSubscript(a));
        superscriptCheckBox.setSelected(StyleConstants.isSuperscript(a));
        colorComboBox.setSelectedItem(StyleConstants.getForeground(a));
        updatePreview();
    }

    private AttributeSet getAttributes() {
        if (attributes == null)
            return null;
        StyleConstants.setFontFamily(attributes, fontNameInputList
                .getSelected());
        StyleConstants.setFontSize(attributes, fontSizeInputList
                .getSelectedInt());
        StyleConstants.setBold(attributes, boldCheckBox.isSelected());
        StyleConstants.setItalic(attributes, italicCheckBox.isSelected());
        StyleConstants.setUnderline(attributes, underlineCheckBox.isSelected());
        StyleConstants.setStrikeThrough(attributes, strikethroughCheckBox
                .isSelected());
        StyleConstants.setSubscript(attributes, subscriptCheckBox.isSelected());
        StyleConstants.setSuperscript(attributes, superscriptCheckBox
                .isSelected());
        StyleConstants.setForeground(attributes, (Color) colorComboBox.getSelectedItem());
        return attributes;
    }

    private int getOption() {
        return Closed_Option;
    }

    protected void updatePreview() {
        StringBuilder previewText = new StringBuilder(PREVIEW_TEXT);
        String name = fontNameInputList.getSelected();
        int size = fontSizeInputList.getSelectedInt();
        if (size <= 0)
            return;

        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();

        attributes.put(FAMILY, name);
        attributes.put(SIZE, (float) size);

        // Using HTML to force JLabel manage natively unsupported attributes
        if (underlineCheckBox.isSelected() || strikethroughCheckBox.isSelected()) {
            previewText.insert(0, "<html>");
            previewText.append("</html>");
        }

        if (underlineCheckBox.isSelected()) {
            attributes.put(UNDERLINE, UNDERLINE_LOW_ONE_PIXEL);
            previewText.insert(6, "<u>");
            previewText.insert(previewText.length() - 7, "</u>");
        }
        if (strikethroughCheckBox.isSelected()) {
            attributes.put(STRIKETHROUGH, STRIKETHROUGH_ON);
            previewText.insert(6, "<strike>");
            previewText.insert(previewText.length() - 7, "</strike>");
        }


        if (boldCheckBox.isSelected())
            attributes.put(WEIGHT, WEIGHT_BOLD);
        if (italicCheckBox.isSelected())
            attributes.put(POSTURE, POSTURE_OBLIQUE);

        if (subscriptCheckBox.isSelected()) {
            attributes.put(SUPERSCRIPT, SUPERSCRIPT_SUB);
        }
        if (superscriptCheckBox.isSelected())
            attributes.put(SUPERSCRIPT, SUPERSCRIPT_SUPER);

        superscriptCheckBox.setEnabled(!subscriptCheckBox.isSelected());
        subscriptCheckBox.setEnabled(!superscriptCheckBox.isSelected());


        Font fn = new Font(attributes);

        previewLabel.setText(previewText.toString());
        previewLabel.setFont(fn);

        Color c = (Color) colorComboBox.getSelectedItem();
        previewLabel.setForeground(c);
        previewLabel.repaint();
    }


    public static int showDialog(GExpert frame, Font f, String title, Color c) {

        if (chooser == null) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            fontNames = ge.getAvailableFontFamilyNames();
            fontSizes = new String[]{"8", "9", "10", "11", "12", "13", "14", "15", "16",
                    "18", "20", "22", "24", "26", "28", "36", "48", "72"};
            chooser = new vFontChooser(frame);
        }
        SimpleAttributeSet a = new SimpleAttributeSet();
        StyleConstants.setFontSize(a, f.getSize());
        StyleConstants.setBold(a, f.isBold());
        StyleConstants.setItalic(a, f.isItalic());
        StyleConstants.setFontFamily(a, f.getFamily());
        chooser.setAttributes(a);
//        if (title != null)
//            chooser.setTitle(title);
        int x = frame.getX() + frame.getWidth() / 2 - 350 / 2;
        int y = frame.getY() + frame.getHeight() / 2 - 450 / 2;
        chooser.setLocation(x, y);
        chooser.setSize(350, 450);
        chooser.setVisible(true);
        return chooser.getOption();
    }

    public static int showDialog(GExpert frame, Font f, Color c) {
        return showDialog(frame, f, "Font Chooser", c);
    }

    public static Font getReturnFont() {
        SimpleAttributeSet a = (SimpleAttributeSet) chooser.getAttributes();

        String s1 = StyleConstants.getFontFamily(a);
        int size = StyleConstants.getFontSize(a);

        boolean b = StyleConstants.isBold(a);
        boolean it = StyleConstants.isItalic(a);
        int style;
        if (b)
            style = Font.BOLD;
        else if (it)
            style = Font.ITALIC;
        else
            style = Font.PLAIN;


        Font f = new Font(s1, style, size);
        //a.getAttribute()
        return f;

    }

    class InputList extends JPanel implements ListSelectionListener, ActionListener {
        /**
		 * 
		 */
		private static final long serialVersionUID = -1978540133527028488L;

		protected JLabel label = new JLabel();

        protected JTextField textfield;

        protected JList<String> list;

        protected JScrollPane scroll;

        public InputList(String[] data, String title) {
            setLayout(null);

            add(label);
            textfield = new OpelListText();
            textfield.addActionListener(this);
            label.setLabelFor(textfield);
            add(textfield);
            list = new OpelListList(data);
            list.setVisibleRowCount(4);
            list.addListSelectionListener(this);
            scroll = new JScrollPane(list);
            add(scroll);
        }

        public InputList(String title, int numCols) {
            setLayout(null);
            label = new OpelListLabel(title, JLabel.LEFT);
            add(label);
            textfield = new OpelListText(numCols);
            textfield.addActionListener(this);
            label.setLabelFor(textfield);
            add(textfield);
            list = new OpelListList();
            list.setVisibleRowCount(4);
            list.addListSelectionListener(this);
            scroll = new JScrollPane(list);
            add(scroll);
        }

        public void setToolTipText(String text) {
            super.setToolTipText(text);
            label.setToolTipText(text);
            textfield.setToolTipText(text);
            list.setToolTipText(text);
        }

        public void setDisplayedMnemonic(char ch) {
            label.setDisplayedMnemonic(ch);
        }

        public void setSelected(String sel) {
            list.setSelectedValue(sel, true);
            textfield.setText(sel);
        }

        public String getSelected() {
            return textfield.getText();
        }

        public void setSelectedInt(int value) {
            setSelected(Integer.toString(value));
        }

        public int getSelectedInt() {
            try {
                return Integer.parseInt(getSelected());
            } catch (NumberFormatException ex) {
                return -1;
            }
        }

        public void valueChanged(ListSelectionEvent e) {
            Object obj = list.getSelectedValue();
            if (obj != null)
                textfield.setText(obj.toString());
        }

        public void actionPerformed(ActionEvent e) {
            ListModel<String> model = list.getModel();
            String key = textfield.getText().toLowerCase();
            for (int k = 0; k < model.getSize(); k++) {
                String data = model.getElementAt(k);
                if (data.toLowerCase().startsWith(key)) {
                    list.setSelectedValue(data, true);
                    break;
                }
            }
        }

        public void addListSelectionListener(ListSelectionListener lst) {
            list.addListSelectionListener(lst);
        }

        public Dimension getPreferredSize() {
            Insets ins = getInsets();
            Dimension labelSize = label.getPreferredSize();
            Dimension textfieldSize = textfield.getPreferredSize();
            Dimension scrollPaneSize = scroll.getPreferredSize();
            int w = Math.max(Math.max(labelSize.width, textfieldSize.width),
                    scrollPaneSize.width);
            int h = labelSize.height + textfieldSize.height + scrollPaneSize.height;
            return new Dimension(w + ins.left + ins.right, h + ins.top + ins.bottom);
        }

        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        public void doLayout() {
            Insets ins = getInsets();
            Dimension size = getSize();
            int x = ins.left;
            int y = ins.top;
            int w = size.width - ins.left - ins.right;
            int h = size.height - ins.top - ins.bottom;

            Dimension labelSize = label.getPreferredSize();
            label.setBounds(x, y, w, labelSize.height);
            y += labelSize.height;
            Dimension textfieldSize = textfield.getPreferredSize();
            textfield.setBounds(x, y, w, textfieldSize.height);
            y += textfieldSize.height;
            scroll.setBounds(x, y, w, h - y);
        }

        public void appendResultSet(ResultSet results, int index,
                                    boolean toTitleCase) {
            textfield.setText("");
            DefaultListModel<String> model = new DefaultListModel<String>();
            try {
                while (results.next()) {
                    String str = results.getString(index);
                    if (toTitleCase) {
                        str = Character.toUpperCase(str.charAt(0))
                                + str.substring(1);
                    }

                    model.addElement(str);
                }
            } catch (SQLException ex) {
                System.err.println("appendResultSet: " + ex.toString());
            }
            list.setModel(model);
            if (model.getSize() > 0)
                list.setSelectedIndex(0);
        }

        class OpelListLabel extends JLabel {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1486262254598831735L;

			public OpelListLabel(String text, int alignment) {
                super(text, alignment);
            }

            public AccessibleContext getAccessibleContext() {
                return InputList.this.getAccessibleContext();
            }
        }

        class OpelListText extends JTextField {
            /**
			 * 
			 */
			private static final long serialVersionUID = -2193808231632428531L;

			public OpelListText() {
            }

            public OpelListText(int numCols) {
                super(numCols);
            }

            public AccessibleContext getAccessibleContext() {
                return InputList.this.getAccessibleContext();
            }
        }

        class OpelListList extends JList<String> {
            /**
			 * 
			 */
			private static final long serialVersionUID = 5673889866521283606L;

			public OpelListList() {
            }

            public OpelListList(String[] data) {
                super(data);
            }

            public AccessibleContext getAccessibleContext() {
                return InputList.this.getAccessibleContext();
            }
        }

        // Accessibility Support

        public AccessibleContext getAccessibleContext() {
            if (accessibleContext == null)
                accessibleContext = new AccessibleOpenList();
            return accessibleContext;
        }

        protected class AccessibleOpenList extends AccessibleJComponent {

            /**
			 * 
			 */
			private static final long serialVersionUID = 2557310681961932224L;

			public String getAccessibleName() {
                CMisc.print("getAccessibleName: " + accessibleName);
                if (accessibleName != null)
                    return accessibleName;
                return label.getText();
            }

            public AccessibleRole getAccessibleRole() {
                return AccessibleRole.LIST;
            }
        }
    }

    class FontLabel extends JLabel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 7017992713463880893L;

		public FontLabel(String text) {
            super(text, JLabel.CENTER);
            setBackground(Color.white);
            setForeground(Color.black);
            setOpaque(true);
            setBorder(new LineBorder(Color.black));
            setPreferredSize(new Dimension(120, 40));
        }
    }

    class ColorComboBox extends JComboBox<Color> {

        /**
		 * 
		 */
		private static final long serialVersionUID = -2916771532942863098L;

		public ColorComboBox() {
            int[] values = new int[]{0, 128, 192, 255};
            for (int r = 0; r < values.length; r++)
                for (int g = 0; g < values.length; g++)
                    for (int b = 0; b < values.length; b++) {
                        Color c = new Color(values[r], values[g], values[b]);
                        addItem(c);
                    }
            setRenderer(new ColorComboRenderer1());

        }

        class ColorComboRenderer1 extends JPanel implements ListCellRenderer<Color> {
            /**
			 * 
			 */
			private static final long serialVersionUID = 313511257983365453L;
			protected Color m_c = Color.black;

            public ColorComboRenderer1() {
                super();
                setBorder(new CompoundBorder(new MatteBorder(2, 10, 2, 10, Color.white), new LineBorder(Color.black)));
            }

            public Component getListCellRendererComponent(JList<? extends Color> list, Color obj, int row, boolean sel, boolean hasFocus) {
                   m_c = obj;
                return this;
            }

            public void paint(Graphics g) {
                setBackground(m_c);
                super.paint(g);
            }

        }
    }

}