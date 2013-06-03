package wprover;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2005-2-10 Time: 19:38:34
 * To change this template use File | Settings | File Templates.
 */
public class PanelProperty extends JPanel implements ActionListener {

	/**
	 * Superclass for all the "Property" dialog boxes that let the user adjust circles, points, angles, text, etc.
	 * to set their color, thickness, dashed lines, proportions, etc.
	 */
	private static final long serialVersionUID = 5910258280771203685L;
	private final DrawPanelOverlay d;
	private final Language lan;

	private final JLabel label = new JLabel("Nothing Selected");
	private final Panel_CS pcs;
	private final Panel_Point ppt;
	private final Panel_Line pln;
	private final Panel_Circle pcir;
	private final Panel_Polygon poly;
	private final Panel_Angle pangle;
	private final Panel_eqmark peqmk;
	private final Panel_trace ptrs;
	private final Panel_text ptex;
	private final Panel_arrow parrow;

	public PanelProperty(DrawPanelOverlay dd, Language lan) {
		d = dd;
		this.lan = lan;

		pcs = new Panel_CS();
		ppt = new Panel_Point();
		pln = new Panel_Line();
		pcir = new Panel_Circle();
		poly = new Panel_Polygon();
		pangle = new Panel_Angle();
		peqmk = new Panel_eqmark();
		ptrs = new Panel_trace();
		ptex = new Panel_text();
		parrow = new Panel_arrow();

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		label.setFont(new Font("Dialog", Font.PLAIN, 18));
		this.add(label);
	}

	private String getLanguage(int n, String s) {
		if (lan == null) {
			return s;
		}

		final String s1 = lan.getString(n);
		if ((s1 != null) && (s1.length() > 0)) {
			return s1;
		}
		return s;
	}

	public void SetPanelType(GraphicEntity obj) {
		if (obj == null) {
			return;
		}

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		this.removeAll();
		label.setText(obj.getDescription());
		this.add(label);
		final int t = obj.get_type();
		pcs.setColorOnly(true);

		switch (t) {
		case GraphicEntity.POINT: {
			final GEPoint p = (GEPoint) obj;
			pcs.setVariable(obj);
			this.add(pcs);
			ppt.pt = p;
			p.initializePropertyPanel(ppt.table);
			ppt.border.setTitle(p.TypeString());
			this.add(ppt);
			break;
		}
		case GraphicEntity.LINE: {
			final GELine line = (GELine) obj;
			pcs.setVariable(obj);
			this.add(pcs);
			pln.line = line;
			line.initializePropertyPanel(pln.table, pln.slider, pln.button1, pln.button2, pln.button3);
			pln.border.setTitle(line.TypeString());
			this.add(pln);
			break;
		}
		case GraphicEntity.CIRCLE: {
			final GECircle c = (GECircle) obj;
			pcs.setVariable(obj);
			this.add(pcs);
			pcir.setVariable(c);
			this.add(pcir);
			break;
		}
		case GraphicEntity.POLYGON: {
			final GEPolygon cp = (GEPolygon) obj;
			pcs.setVariable(obj);
			this.add(pcs);
			poly.setVariable(cp);
			this.add(poly);
			break;
		}
		case GraphicEntity.ANGLE: {
			pcs.setVariable(obj);
			this.add(pcs);
			final GEAngle ca = (GEAngle) obj;
			pangle.setVariable(ca);
			this.add(pangle);
			break;
		}
		case GraphicEntity.EQMARK: {
			final GEEqualDistanceMark m = (GEEqualDistanceMark) obj;
			pcs.setVariable(obj);
			this.add(pcs);
			peqmk.setVariable(m);
			this.add(peqmk);
			break;
		}
		case GraphicEntity.TRACE: {
			final GETrace m = (GETrace) obj;
			pcs.setVariable(obj);
			this.add(pcs);
			ptrs.setVariable(m);
			this.add(ptrs);
			break;
		}
		case GraphicEntity.TEXT: {
			final GEText tx = (GEText) obj;
			pcs.setVariable(obj);
			pcs.setColorOnly(false);
			this.add(pcs);
			if (tx.getType() == GEText.VALUE_TEXT) {
				ptex.setVariable(tx);
				this.add(ptex);
			}
			break;
		}
		case GraphicEntity.ARROW: {
			final GEArrow ar = (GEArrow) obj;
			pcs.setVariable(ar);
			this.add(pcs);
			parrow.setVariable(ar);
			this.add(parrow);
			break;
		}
		default:
			pcs.setVariable(obj);
			this.add(pcs);
			break;
		}
		this.revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	public void setPropertyChanged() {
	}

	public static JButton CreateIconButton(String imageName) {
		final String imgLocation = "images/" + imageName;
		final URL imageURL = DrawPanelFrame.class.getResource(imgLocation);

		final JButton button = new JButton();

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL));
		} else { // no image found
			button.setText(imageName);
		}
		button.setMaximumSize(new Dimension(20, 18));
		button.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		return button;
	}

	public static JTable createTable(Object obj1, Object obj2) {
		final Object data[][] = { { obj1, obj2 } };
		final String[] sname = { "", "" };
		final JTable tb = new JTable(data, sname);

		tb.setRowHeight(20);
		tb.setPreferredSize(new Dimension(70, 20));
		final TableColumn t1 = tb.getColumnModel().getColumn(0);
		t1.setPreferredWidth(20);

		final TableColumn cn = tb.getColumnModel().getColumn(1);
		cn.setPreferredWidth(50);
		tb.setSelectionBackground(Color.lightGray);
		tb.setSelectionForeground(Color.black);
		return tb;
	}

	/**
	 * 
	 */
	class Panel_CS extends JPanel implements ActionListener {
		private static final long serialVersionUID = 6216307466040755371L;
		//DPanel d;
		JComboBox<Integer> color;
		JComboBox<Integer> line_type;
		JComboBox<Integer> line_width;
		PanelColorComboRender color_render;
		PanelColorComboRender line_type_render;
		PanelColorComboRender line_width_render;
		GraphicEntity current_data = null;

		JTable tb1, tc1, td1;

		public void setColorOnly(boolean r) {
			tc1.setVisible(r);
			td1.setVisible(r);
		}

		public Panel_CS() {
			//d = dd;

			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			this.setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createCompoundBorder(
							new TitledBorder(getLanguage(3207, "Basic")),
							BorderFactory.createEmptyBorder(2, 2, 2, 2)), this.getBorder()));

			color = ComboBoxInteger.CreateAInstance();
			color.addActionListener(this);

			tb1 = PanelProperty.createTable(getLanguage(350, "Color"), "");
			final TableColumn cn = tb1.getColumnModel().getColumn(1);
			color_render = new PanelColorComboRender(0, 100, 20);
			cn.setCellRenderer(color_render);
			cn.setCellEditor(new DefaultCellEditor(color));
			add(tb1);

			final Integer[] array1 = new Integer[DrawData.getDashCounter()];
			for (int i = 0; i < DrawData.getDashCounter(); i++) {
				array1[i] = new Integer(i);
			}
			line_type = new JComboBox<Integer>(array1);
			line_type.setMaximumRowCount(20);
			final PanelColorComboRender render1 = new PanelColorComboRender(2, 100, 20);
			render1.setPreferredSize(new Dimension(50, 20));
			line_type.setRenderer(render1);
			line_type.addActionListener(this);

			tc1 = PanelProperty.createTable(getLanguage(730, "Type"), "");
			final TableColumn cn1 = tc1.getColumnModel().getColumn(1);
			line_type_render = new PanelColorComboRender(2, 100, 20);
			cn1.setCellRenderer(line_type_render);
			cn1.setCellEditor(new DefaultCellEditor(line_type));
			add(tc1);

			final Integer[] array2 = new Integer[20];
			for (int i = 0; i < 20; i++) {
				array2[i] = new Integer(i);
			}

			line_width = new JComboBox<Integer>(array2);
			line_width.setMaximumRowCount(20);
			final PanelColorComboRender render2 = new PanelColorComboRender(1, 100, 20);
			render2.setPreferredSize(new Dimension(50, 20));
			line_width.setRenderer(render2);
			line_width.addActionListener(this);

			td1 = PanelProperty.createTable(getLanguage(731, "Width"), "");
			final TableColumn cn2 = td1.getColumnModel().getColumn(1);
			line_width_render = new PanelColorComboRender(1, 100, 20);
			cn2.setCellRenderer(line_width_render);
			cn2.setCellEditor(new DefaultCellEditor(line_width));
			add(td1);
		}

		public void setVariable(GraphicEntity dp) {
			current_data = null;
			if (dp == null) {
				color.setSelectedIndex(0);
				line_type.setSelectedIndex(0);
				line_width.setSelectedIndex(0);
				return;
			}

			((ComboBoxInteger) color).setSelectedIndex(dp.m_color);
			line_type.setSelectedIndex(dp.m_dash);
			line_width.setSelectedIndex(dp.m_width);
			current_data = dp;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final Object obj = e.getSource();

			if (obj == color) {

				int index = color.getSelectedIndex();
				if (index >= 0) {
					if (index == (color.getItemCount() - 1)) {
						final Color newColor = JColorChooser.showDialog(this,
								"Choose Color", Color.white);
						if (newColor != null) {
							final int id = DrawData.addColor(newColor);
							ComboBoxInteger.reGenerateAll();
							color.setSelectedIndex(id - 1);
							index = id - 1;

							if (current_data != null) {
								current_data.setColor(index);
							}
							color_render.index = index;
						}
					} else {
						if (current_data != null) {
							current_data.setColor(index);
						}
						color_render.index = index;
					}

				}
			} else if (obj == line_type) {
				final int index = line_type.getSelectedIndex();
				if (index >= 0) {
					line_type_render.index = index;
					if (current_data != null) {
						current_data.m_dash = (index);
					}
				}
			} else if (obj == line_width) {
				final int index = line_width.getSelectedIndex();
				if (index >= 0) {
					line_width_render.index = index;
					if (current_data != null) {
						current_data.m_width = (index);
					}
				}
			}
			d.repaint();

		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
		}
	}

	class Panel_Line extends JPanel implements TableModelListener,
			ActionListener, ChangeListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5263282944133571145L;
		GELine line;
		JTable table;
		TitledBorder border;
		JButton button1, button2, button3;
		JPopupMenu popup;
		JSlider slider;

		public Panel_Line() {

			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			border = new TitledBorder(getLanguage(40, "Line"));

			this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(border,
							BorderFactory.createEmptyBorder(2, 2, 2, 2)),
					this.getBorder()));
			table = new JTable(new LineTableModel());
			table.getModel().addTableModelListener(this);
			this.add(table);

			button1 = PanelProperty.CreateIconButton("line_two_end.gif");
			button2 = PanelProperty.CreateIconButton("line_more_end.gif");
			button3 = PanelProperty.CreateIconButton("line_no_end.gif");
			button1.addActionListener(this);
			button2.addActionListener(this);
			button3.addActionListener(this);

			final JPanel panel = new JPanel();
			panel.setMaximumSize(new Dimension(200, 50));
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			final JLabel label = new JLabel(getLanguage(730, "Type") + ":   ");
			panel.add(label);
			panel.add(Box.createHorizontalStrut(10));
			panel.add(button1);
			panel.add(Box.createHorizontalStrut(10));
			panel.add(button2);
			panel.add(Box.createHorizontalStrut(10));
			panel.add(button3);
			button1.setBackground(Color.white);
			button2.setBackground(Color.white);
			button3.setBackground(Color.white);
			popup = new JPopupMenu();
			popup.add(slider = new JSlider(0, 200));
			slider.setMajorTickSpacing(50);
			slider.setPaintLabels(true);
			slider.addChangeListener(this);
			this.add(panel);

		}

		public void ClearButtonBackGround() {
			button1.setBackground(Color.white);
			button2.setBackground(Color.white);
			button3.setBackground(Color.white);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			final int n = slider.getValue();
			if (line != null) {
				line.setExtent(n);
			}
			d.repaint();
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			final int row = e.getFirstRow();
			final int column = e.getColumn();
			final TableModel model = (TableModel) e.getSource();
			model.getValueAt(row, column);
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			final Object src = e.getSource();
			ClearButtonBackGround();

			if (src instanceof JButton) {
				if (line == null) {
					return;
				}
				if (src == button1) {
					line.ext_type = 0;
					button1.setBackground(Color.lightGray);
				} else if (src == button2) {
					line.ext_type = 1;
					button2.setBackground(Color.lightGray);
					popup.show(button2, 0, button2.getHeight() + 2);
				} else if (src == button3) {
					line.ext_type = 2;
					button3.setBackground(Color.lightGray);
				}
			}
			d.repaint();
		}

//		public void setVariable(GELine line) {
//			table.setValueAt(line.m_name, 0, 1);
//			border.setTitle(line.TypeString());
//			final GEPoint[] pl = line.getMaxMinPoint();
//			table.setValueAt(line.getAllPointName(), 1, 1);
//			if (pl != null) {
//				table.setValueAt(new Double(round(pl[0].getx())), 2, 1);
//				table.setValueAt(new Double(round(pl[0].gety())), 3, 1);
//				table.setValueAt(new Double(round(pl[1].getx())), 4, 1);
//				table.setValueAt(new Double(round(pl[1].gety())), 5, 1);
//			}
//			ClearButtonBackGround();
//			if (line.ext_type == 0) {
//				button1.setBackground(Color.lightGray);
//			} else if (line.ext_type == 1) {
//				button2.setBackground(Color.lightGray);
//			} else {
//				button3.setBackground(Color.lightGray);
//			}
//			this.line = line;
//			slider.setValue(line.getExtent());
//		}

	}

	class Panel_Circle extends JPanel implements TableModelListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5573968749702113091L;

		JTable table;
		GECircle circle = null;
		TitledBorder border;

		public Panel_Circle() {

			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			border = new TitledBorder(getLanguage(3106, "Circle"));
			this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(border,
							BorderFactory.createEmptyBorder(2, 2, 2, 2)),
					this.getBorder()));

			table = new JTable(new CircleTableModel());
			final TableColumn t1 = table.getColumnModel().getColumn(0);
			t1.setPreferredWidth(20);
			final TableColumn cn = table.getColumnModel().getColumn(1);
			cn.setPreferredWidth(50);

			table.getModel().addTableModelListener(this);
			this.add(table);
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (circle == null) {
				return;
			}

			final int row = e.getFirstRow();
			final int column = e.getColumn();
			final TableModel model = (TableModel) e.getSource();
			final Object data = model.getValueAt(row, column);
			if (row == 0) {
				circle.m_name = data.toString();
			} else if (row == 1) {
			} else if (row == 2) {
			}
			d.repaint();
		}

		public void setVariable(GECircle c) {
			circle = c;
			border.setTitle(c.TypeString());
			table.setValueAt(circle.m_name, 0, 1);
			table.setValueAt(circle.getAllPointName(), 1, 1);
			table.setValueAt(circle.o.m_name, 2, 1);

			table.setValueAt(new Double(round(circle.o.getx())), 3, 1);
			table.setValueAt(new Double(round(circle.o.gety())), 4, 1);
			table.setValueAt(new Double(round(circle.getRadius())), 5, 1);
		}

	}

	class Panel_Point extends JPanel implements TableModelListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2415027796818524804L;

		//DPanel d;

		JTable table;

		GEPoint pt = null;
		TitledBorder border;

		public Panel_Point() {
			//d = dd;

			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			border = new TitledBorder(getLanguage(33, "Point"));

			this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(border,
							BorderFactory.createEmptyBorder(2, 2, 2, 2)),
					this.getBorder()));

			// JComboBox comboBox = new JComboBox();
			// for (int i = 0; i < type.length; i++)
			// comboBox.addItem(type[i]);
			//
			// cn1.setCellEditor(new DefaultCellEditor(comboBox));

			table = new JTable(new PointTableModel());
			final TableColumn t1 = table.getColumnModel().getColumn(0);
			t1.setPreferredWidth(20);
			final TableColumn cn = table.getColumnModel().getColumn(1);
			cn.setPreferredWidth(50);

			table.getModel().addTableModelListener(this);
			this.add(table);
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (pt == null) {
				return;
			}

			final int row = e.getFirstRow();
			final int column = e.getColumn();
			final TableModel model = (TableModel) e.getSource();
			final Object data = model.getValueAt(row, column);
			if (data == null) {
				return;
			}

			if (row == 0) {
				pt.m_name = data.toString();
			} else if (row == 1) {
				final Integer r = new Integer(data.toString());
				pt.setRadius(r.intValue());
			} else if (row == 2) {
				final Double d = new Double(data.toString());
				pt.setXY(d.doubleValue(), pt.gety());
			} else if (row == 3) {
				final Double d = new Double(data.toString());
				pt.setXY(pt.getx(), d.doubleValue());
			} else if (row == 4) {
				pt.setFrozen(Boolean.parseBoolean(data.toString()));
			}
			d.repaint();

		}

//		public void initPanel(GEPoint p) {
//			pt = p;
//			table.setValueAt(p.m_name, 0, 1);
//			table.setValueAt(new Integer(p.getRadiusValue()), 1, 1);
//			table.setValueAt(new Double(round(p.getx())), 2, 1);
//			table.setValueAt(new Double(round(p.gety())), 3, 1);
//			table.setValueAt(pt.isFrozen(), 4, 1);
//			border.setTitle(p.TypeString());
//		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
		}
	}

	class Panel_Angle extends JPanel implements ActionListener,
			TableModelListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5660200840437911749L;
		JComboBox<Integer> bcolor;
		TitledBorder border;
		JTable tb1, tb2, tb3, tbt;
		GEAngle angle;

		String[] type = { "Without Arrow", "With Arrow", "Multiple Arc", "Fill" };
		String[] text_type = { "Default", "No Text", "Value", "Name",
				"Name With Value" };

		PanelColorComboRender color_render;

		public Panel_Angle() {
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			border = new TitledBorder(getLanguage(40, "Line"));

			this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(border,
							BorderFactory.createEmptyBorder(2, 2, 2, 2)),
					this.getBorder()));
			bcolor = ComboBoxInteger.CreateAInstance();
			bcolor.addActionListener(this);

			tb1 = PanelProperty.createTable(getLanguage(730, "Type"), "");
			final TableColumn cn1 = tb1.getColumnModel().getColumn(1);
			JComboBox<String> comboBox = new JComboBox<String>();

			for (int i = 0; i < type.length; i++) {
				comboBox.addItem(getLanguage(383 + i, type[i]));
			}

			cn1.setCellEditor(new DefaultCellEditor(comboBox));
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			// renderer.setToolTipText("Click to select the angle type.");
			cn1.setCellRenderer(renderer);

			tbt = PanelProperty.createTable(getLanguage(363, "Angle Text"), "");
			final TableColumn cnt = tbt.getColumnModel().getColumn(1);
			comboBox = new JComboBox<String>();
			for (final String element : text_type) {
				comboBox.addItem(element);
			}

			cnt.setCellEditor(new DefaultCellEditor(comboBox));
			renderer = new DefaultTableCellRenderer();
			// renderer.setToolTipText("Click to select the angle text type.");
			cnt.setCellRenderer(renderer);

			tb2 = PanelProperty.createTable(getLanguage(350, "Color"), "");
			final TableColumn cn = tb2.getColumnModel().getColumn(1);
			final PanelColorComboRender cr = new PanelColorComboRender(0, 100, 20);
			cn.setCellRenderer(cr);
			cn.setCellEditor(new DefaultCellEditor(bcolor));
			color_render = new PanelColorComboRender(0, 100, 20);
			cn.setCellRenderer(color_render);

			tb3 = PanelProperty.createTable(getLanguage(390, "Arc Num"), "");

			tb1.getModel().addTableModelListener(this);
			tb2.getModel().addTableModelListener(this);
			tb3.getModel().addTableModelListener(this);
			tbt.getModel().addTableModelListener(this);
			this.add(tb1);
			this.add(tb2);
			this.add(tb3);
			this.add(tbt);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final Object obj = e.getSource();
			if (obj == bcolor) {
				final int n = bcolor.getSelectedIndex();
				if (angle != null) {
					angle.setValue1(n);
				}
				color_render.index = n;
			}
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			final Object obj = e.getSource();
			final int row = e.getFirstRow();
			final int column = e.getColumn();
			final TableModel model = (TableModel) e.getSource();
			final Object data = model.getValueAt(row, column);

			if (obj == tb1.getModel()) {
				int n = 0;
				for (int i = 0; i < type.length; i++) {
					if (data.equals(type[i])) {
						n = i;
						break;
					}
				}
				tb2.setValueAt(angle.getValue1(), 0, 1);
				tb3.setValueAt(angle.getValue1(), 0, 1);
				color_render.index = angle.getValue1();
				setAgTabel(n);
				angle.setAngleType(n);
			} else if (obj == tb3.getModel()) {
				try {
					final int n = Integer.parseInt(data.toString());
					angle.setValue1(n);
				} catch (final NumberFormatException ee) {
					ee.printStackTrace();
				}

			} else if (obj == tbt.getModel()) {
				int n = 0;
				for (int i = 0; i < text_type.length; i++) {
					if (data.equals(text_type[i])) {
						n = i;
						break;
					}
				}
				angle.setTextType(n - 1);
			}

			d.repaint();
		}

		public void setVariable(GEAngle ag) {
			angle = ag;
			bcolor.setSelectedIndex(ag.getValue1());
			final int n = ag.getAngleType();
			final int tn = ag.getTextType() + 1;
			tbt.setValueAt(text_type[tn], 0, 1);
			tb1.setValueAt(type[n], 0, 1);
			tb2.setValueAt(angle.getValue1(), 0, 1);
			tb3.setValueAt(angle.getValue1(), 0, 1);
			color_render.index = angle.getValue1();
			setAgTabel(n);
			border.setTitle(ag.TypeString());
		}

		public void setTableAg(int n) {

		}

		public void setAgTabel(int n) {

			if (n != 3) {
				tb2.setVisible(false);
			} else {
				tb2.setVisible(true);
			}
			if (n != 2) {
				tb3.setVisible(false);
			} else {
				tb3.setVisible(true);
			}
		}
	}

	class Panel_Base extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6084501850015133581L;
		//protected DPanel d;
		protected TitledBorder border;

		public Panel_Base() {
			//this.d = d;
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			border = new TitledBorder("");
			this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(border,
							BorderFactory.createEmptyBorder(2, 2, 2, 2)),
					this.getBorder()));
		}

		public void setBorder(GraphicEntity c) {
			border.setTitle(c.getDescription());
		}
	}

	class Panel_text extends Panel_Base implements TableModelListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4227260708055886315L;
		GEText tx;
		JTable table, table1, table2;
		TableModel model1, model, model2;

		public Panel_text() {
			//super(dd);

			final Object[][] obj = { { getLanguage(365, "Label"),
					new String("") } };

			model = new propertyTableModel(obj);

			table = new JTable(model);
			final Object[][] obj1 = {
					{ getLanguage(348, "Show Label"), Boolean.TRUE },
					{ getLanguage(349, "Show Text"), Boolean.TRUE },

			};

			model1 = new propertyTableModel(obj1);
			table1 = new JTable(model1);

			final Object[][] obj2 = { { getLanguage(347, "Significant Digit"),
					new Integer(0) }

			};

			model2 = new propertyTableModel(obj2);
			table2 = new JTable(model2);

			model2.addTableModelListener(this);
			model.addTableModelListener(this);
			model1.addTableModelListener(this);
			this.add(table);
			this.add(table1);
			this.add(table2);
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (tx == null) {
				return;
			}
			final Object src = e.getSource();

			final int row = e.getFirstRow();
			final int column = e.getColumn();
			final TableModel m = (TableModel) e.getSource();
			final Object data = m.getValueAt(row, column);
			if (data == null) {
				return;
			}

			if ((row == 0) && (src == model)) {
				tx.m_name = data.toString();
			} else if (src == model1) {
				final String s = data.toString();
				Boolean.parseBoolean(s);
				tx.setWidth(0); // 0: default (text) 1.label only . 2.label +
								// text.
				final Object data1 = model1.getValueAt(0, 1);
				final Object data2 = model1.getValueAt(1, 1);
				final boolean b1 = Boolean.parseBoolean(data1.toString());
				final boolean b2 = Boolean.parseBoolean(data2.toString());
				int w = 0;

				if (b1) {
					if (b2) {
						w = 3;
					} else {
						w = 1;
					}
				} else {
					if (b2) {
						w = 2;
					} else {
						w = 0;
					}
				}
				tx.m_width = w;
			} else {
				final String s1 = model2.getValueAt(0, 1).toString();
				final int n = Integer.parseInt(s1);
				tx.m_dash = n;
			}
			d.repaintAndCalculate();
		}

		public void setVariable(GEText t) {
			tx = t;
			String s = tx.m_name;
			if (s == null) {
				s = "";
			}
			table.setValueAt(s, 0, 1);
			final int w = tx.m_width;
			table2.setValueAt(tx.m_dash, 0, 1);
			if (w == 0) {
				table1.setValueAt(false, 0, 1);
				table1.setValueAt(false, 1, 1);
			} else if (w == 1) {
				table1.setValueAt(true, 0, 1);
				table1.setValueAt(false, 1, 1);
			} else if (w == 2) {
				table1.setValueAt(false, 0, 1);
				table1.setValueAt(true, 1, 1);
			} else {
				table1.setValueAt(true, 0, 1);
				table1.setValueAt(true, 1, 1);
			}
			d.repaint();
		}

	}

	class Panel_trace extends Panel_Base implements TableModelListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7435904058226068648L;
		GETrace ts;
		JTable table, table1;
		TableModel model1, model;

		public Panel_trace() {
			//super(dd);

			final Object[][] obj = { { getLanguage(450, "Point number"),
					new Integer("1") }, };

			model = new propertyTableModel(obj);

			table = new JTable(model);
			final Object[][] obj1 = { { getLanguage(451, "Draw Line"),
					Boolean.TRUE }, };

			model1 = new propertyTableModel(obj1);
			table1 = new JTable(model1);
			model.addTableModelListener(this);
			model1.addTableModelListener(this);
			this.add(table);
			this.add(table1);
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (ts == null) {
				return;
			}
			final Object src = e.getSource();

			final int row = e.getFirstRow();
			final int column = e.getColumn();
			final TableModel m = (TableModel) e.getSource();
			final Object data = m.getValueAt(row, column);

			if ((row == 0) && (src == model)) {
				final int n = Integer.parseInt(data.toString());
				ts.setMaxNumberOfPoints(n);
			} else if ((row == 0) && (src == model1)) {
				ts.setDrawLines(Boolean.parseBoolean(data.toString()));
			}
			d.repaintAndCalculate();
		}

		public void setVariable(GETrace tc) {
			ts = tc;
			table.setValueAt((tc.getPointSize()), 0, 1);
			table1.setValueAt(tc.doesDrawLines(), 0, 1);
			d.repaint();
		}
	}

	class Panel_eqmark extends Panel_Base implements TableModelListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1349162731196175159L;
		GEEqualDistanceMark mk;
		JTable table;

		public Panel_eqmark() {
			//super(dd);

			final Object[][] obj = {
					{ getLanguage(452, "Num"), new Integer("1") },
					{ getLanguage(247, "Length"), new Integer("1") } };

			table = new JTable(new propertyTableModel(obj));
			table.getModel().addTableModelListener(this);
			this.add(table);
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (mk == null) {
				return;
			}

			final int row = e.getFirstRow();
			final int column = e.getColumn();
			final TableModel model = (TableModel) e.getSource();
			final Object data = model.getValueAt(row, column);
			if (row == 0) {
				mk.setNum(Integer.parseInt(data.toString()));
			} else if (row == 1) {
				mk.setLength(Integer.parseInt(data.toString()));
			}
			d.repaint();
		}

		public void setVariable(GEEqualDistanceMark mc) {
			mk = mc;
			table.setValueAt((mk.getNum()), 0, 1);
			table.setValueAt((mk.getLength()), 1, 1);
		}
	}

	class Panel_Polygon extends JPanel implements TableModelListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3339094394085135012L;
		JTable table, table1;
		GEPolygon polygon = null;
		String[] type = { "Fill", "Grid", "Vertical Line", "Horizontal Line" };
		TitledBorder border;
		JComboBox<String> comboBox;

		public Panel_Polygon() {

			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			border = new TitledBorder(getLanguage(70, "Polygon"));

			this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createCompoundBorder(border,
							BorderFactory.createEmptyBorder(2, 2, 2, 2)),
					this.getBorder()));

			final JTable tb1 = PanelProperty.createTable(getLanguage(730, "Type"),
					"");
			final TableColumn cn1 = tb1.getColumnModel().getColumn(1);
			comboBox = new JComboBox<String>();
			for (int i = 0; i < type.length; i++) {
				comboBox.addItem(getLanguage(4000 + i, type[i]));
			}

			cn1.setCellEditor(new DefaultCellEditor(comboBox));
			final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			// renderer.setToolTipText("Click to select the fill type.");
			cn1.setCellRenderer(renderer);
			this.add(tb1);
			table1 = tb1;
			table1.getModel().addTableModelListener(this);

			table = new JTable(new PolygonTableModel());
			final TableColumn t1 = table.getColumnModel().getColumn(0);
			t1.setPreferredWidth(20);
			final TableColumn cn = table.getColumnModel().getColumn(1);
			cn.setPreferredWidth(50);

			table.getModel().addTableModelListener(this);
			this.add(table);
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (polygon == null) {
				return;
			}

			final int row = e.getFirstRow();
			final int column = e.getColumn();
			final TableModel model = (TableModel) e.getSource();
			final Object data = model.getValueAt(row, column);
			if ((row == 0) && (model == table1.getModel())) {
				int t = -1;
				// String s = data.toString();
				// if (s.compareTo("Fill") == 0)
				// t = 0;
				// else if (s.compareTo("Grid") == 0)
				// t = 1;
				// else if (s.compareTo("Line") == 0)
				// t = 2;
				// else if (s.compareTo("V Line") == 0)
				// t = 3;
				t = comboBox.getSelectedIndex();
				if (t >= 0) {
					polygon.setType(t);
				}
			} else if ((row == 0) && (model == table.getModel())) {
				polygon.setGrid(((Integer) data).intValue());
			} else if ((row == 1) && (model == table.getModel())) {
				final Integer d = new Integer(data.toString());
				polygon.setSlope(d.intValue());
			}
			d.repaint();

		}

		public void setVariable(GEPolygon c) {
			polygon = c;

			table1.setValueAt(type[polygon.getType()], 0, 1);

			table.setValueAt(new Integer(polygon.grid), 0, 1);
			table.setValueAt(new Integer(polygon.slope), 1, 1);
		}

	}

	class Panel_arrow extends Panel_Base implements TableModelListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8243104291735380981L;
		GEArrow arrow;
		JTable table;

		public Panel_arrow() {
			//super(dd);

			final Object[][] obj = {
					{ getLanguage(452, "Angle"), new Integer("30") },
					{ getLanguage(247, "Length"), new Integer("1") } };

			table = new JTable(new propertyTableModel(obj));
			table.getModel().addTableModelListener(this);
			this.add(table);
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (arrow == null) {
				return;
			}

			final int row = e.getFirstRow();
			final int column = e.getColumn();
			final TableModel model = (TableModel) e.getSource();
			final Object data = model.getValueAt(row, column);
			if (row == 0) {
				arrow.angle = (Integer.parseInt(data.toString()));
			} else if (row == 1) {
				arrow.length = (Integer.parseInt(data.toString()));
			}
			d.repaint();
		}

		public void setVariable(GEArrow mc) {
			arrow = mc;
			table.setValueAt(arrow.angle, 0, 1);
			table.setValueAt(arrow.length, 1, 1);
		}
	}

	class DefaultTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5234444732875385367L;
		private final Object[][] data;

		public DefaultTableModel(Object[][] o) {
			data = o;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public String getColumnName(int col) {
			return "";
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		@Override
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			if (col < 1) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
	}

	class propertyTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7139639343126966121L;
		private final String[] names = { "", "" };
		private Object[][] data = null;

		public propertyTableModel(Object[][] d) {
			data = d;
		}

		@Override
		public int getColumnCount() {
			return names.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public String getColumnName(int col) {
			return names[col];
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		@Override
		public Class<?> getColumnClass(int c) {
			final Object o = getValueAt(0, c);
			if (o != null) {
				return o.getClass();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			if (col < 1) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}

	}

	class PointTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 559345946356614058L;
		private final String[] names = { "", "" };
		private final Object[][] data = {
				{ getLanguage(245, "Name"), new String() },
				{ getLanguage(4004, "Radius"), new Integer(-1) },
				{ getLanguage(4005, "X Coordinate"), new Double(0) },
				{ getLanguage(4006, "Y Coordinate"), new Double(0) },
				{ getLanguage(4007, "Freezed"), new Boolean(false) } };

		@Override
		public int getColumnCount() {
			return names.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public String getColumnName(int col) {
			return names[col];
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		@Override
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			if (col < 1) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
	}

	class LineTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3888436199135601146L;
		private final String[] names = { "", "" };
		private final Object[][] data = {
				{ getLanguage(245, "Name"), new String() },
				{ getLanguage(4008, "Point on Line"), new String() },
				{ "X1 ", new Double(0) }, { "Y1 ", new Double(0) },
				{ "X2 ", new Double(0) }, { "Y2", new Double(0) } };

		@Override
		public int getColumnCount() {
			return names.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public String getColumnName(int col) {
			return names[col];
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		@Override
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			if (col < 1) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
	}

	class CircleTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5364628393865353382L;
		private final String[] names = { "", "" };
		private final Object[][] data = {
				{ getLanguage(245, "Name"), new String() },
				{ getLanguage(4009, "Point on Circle"), new String() },
				{ getLanguage(4012, "Center"), new String() },
				{ getLanguage(4010, "Center X"), new Double(0) },
				{ getLanguage(4011, "Center Y"), new Double(0) },
				{ getLanguage(4004, "Radius"), new Double(0) } };

		@Override
		public int getColumnCount() {
			return names.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public String getColumnName(int col) {
			return names[col];
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		@Override
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			if ((col < 1) || (row >= 1)) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
	}

	class PolygonTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2462727551714557126L;
		private final String[] names = { "", "" };
		private final Object[][] data = {
				{ getLanguage(4013, "Grid Step"), new Integer(0) },
				{ getLanguage(4014, "Slope Angle"), new Integer(0) }, };

		@Override
		public int getColumnCount() {
			return names.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public String getColumnName(int col) {
			return names[col];
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		@Override
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			if (col < 1) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
	}

	public static double round(double r) {
		final int t = (int) (100 * r);
		return t / 100.0;
	}

	class CPropertyTableCellRender implements TableCellRenderer {
		TableCellRenderer deditor;

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return deditor.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
		}

	}

	class CPropertyTableCellEditor implements TableCellEditor {
		DefaultCellEditor editor;
		DefaultCellEditor booleanEditor;
		DefaultCellEditor selected;

		public CPropertyTableCellEditor() {
			selected = editor = new DefaultCellEditor(new JTextField());
			booleanEditor = new DefaultCellEditor(new JCheckBox());
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			if (value instanceof Boolean) {
				selected = booleanEditor;
				return booleanEditor.getTableCellEditorComponent(table, value,
						isSelected, row, column);
			}
			selected = editor;
			return editor.getTableCellEditorComponent(table, value, isSelected,
					row, column);
		}

		public void setEditorAt(int row, TableCellEditor editor) {

		}

		@Override
		public Object getCellEditorValue() {
			return selected.getCellEditorValue();
		}

		@Override
		public boolean stopCellEditing() {
			return selected.stopCellEditing();
		}

		@Override
		public void cancelCellEditing() {
			selected.cancelCellEditing();
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			return selected.isCellEditable(anEvent);
		}

		@Override
		public void addCellEditorListener(CellEditorListener l) {
			selected.addCellEditorListener(l);
		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
			selected.removeCellEditorListener(l);
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			return selected.shouldSelectCell(anEvent);
		}
	}
}