package wprover;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2005-1-26 Time: 15:29:29
 * To change this template use File | Settings | File Templates.
 */
public class GEText extends GraphicEntity {

	final public static int NORMAL_TEXT = 0;
	final public static int NAME_TEXT = 1;
	final public static int CNAME_TEXT = 2;
	final public static int VALUE_TEXT = 3;

	private int type = NORMAL_TEXT;
	private int x, y;
	private Font font;
	private String str;
	public MathHelper tvalue;

	GraphicEntity father = null; // if any;

	double w, h;
	double height;
	private String svalue;
	private double posX, posY;


	public GEText() {
		super(GraphicEntity.TEXT);
		str = new String();
	}

	public GEText(final GraphicEntity f, final double dx, final double dy, final int t) {
		super(GraphicEntity.TEXT);
		str = new String();
		m_color = DrawData.getColorIndex(Color.black);
		type = t;
		font = UtilityMiscellaneous.nameFont;
		x += dx;
		y += dy;
		father = f;
	}
	public GEText(final int x, final int y, final String s) {
		super(GraphicEntity.TEXT);
		str = s;
		font = new Font("Dialog", Font.PLAIN, 14);
		this.x = x;
		this.y = y;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof GEText))
			return false;
		GEText other = (GEText) obj;
		if (father == null) {
			if (other.father != null)
				return false;
		} else if (father != other.father)
			return false;
		if (font == null) {
			if (other.font != null)
				return false;
		} else if (!font.equals(other.font))
			return false;
		if (Double.doubleToLongBits(h) != Double.doubleToLongBits(other.h))
			return false;
		if (Double.doubleToLongBits(height) != Double
				.doubleToLongBits(other.height))
			return false;
		if (nameTextShown != other.nameTextShown)
			return false;
		if (Double.doubleToLongBits(posX) != Double
				.doubleToLongBits(other.posX))
			return false;
		if (Double.doubleToLongBits(posY) != Double
				.doubleToLongBits(other.posY))
			return false;
		if (str == null) {
			if (other.str != null)
				return false;
		} else if (!str.equals(other.str))
			return false;
		if (svalue == null) {
			if (other.svalue != null)
				return false;
		} else if (!svalue.equals(other.svalue))
			return false;
		if (tvalue == null) {
			if (other.tvalue != null)
				return false;
		} else if (!tvalue.equals(other.tvalue))
			return false;
		if (type != other.type)
			return false;
		if (Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public void setXY(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public Font getFont() {
		return font;
	}

	public void setTextType(final int t) {
		type = t;
		tvalue = MathHelper.parseString(str);
		if (tvalue == null)
			tvalue = new MathHelper();

		m_dash = 3;
		m_color = 16;
	}

	public boolean changeFontSize(final int n) {
		final int size = font.getSize() + n;
		if (size <= 5)
			return false;

		font = new Font(font.getName(), font.getStyle(), size);
		return true;
	}

	public int getFontSize() {
		return font.getSize();
	}

	public void setBold() {
		if (!font.isBold())
			font = new Font(font.getName(), Font.BOLD, font.getSize());
	}

	public void setPlain() {
		if (!font.isPlain())
			font = new Font(font.getName(), Font.PLAIN, font.getSize());
	}

	public void setFont(final Font f) {
		font = f;
	}

	public void setFontSize(final int n) {
		if (n != font.getSize())
			font = new Font(font.getName(), font.getStyle(), n);
	}

	@Override
	public void move(final double dx, final double dy) {
		super.move(dx, dy);
		if ((type == NORMAL_TEXT) || (type == VALUE_TEXT)) {
			x += dx;
			y += dy;
		}
	}

	public String getText() {
		if (type == NORMAL_TEXT)
			return str;
		else if (type == NAME_TEXT)
			return father.m_name;
		else if (type == CNAME_TEXT)
			return str;
		else if (type == VALUE_TEXT)
			return str;

		return null;

	}

	public void setText1(final String s) {
		str = s;
	}

	public void setText(final String s) {

		if (type == NORMAL_TEXT)
			str = s;
		else if (type == NAME_TEXT)
			father.m_name = s;
		else if (type == CNAME_TEXT) {
			final GEAngle ag = (GEAngle) father;
			ag.setShowType(2);
			father.m_name = s;
			str = s;
		} else if (type == VALUE_TEXT) {
			str = s;
			final MathHelper dt = MathHelper.parseString(str);
			if (dt != null)
				tvalue = dt;
		}
	}

	public String getString() {
		return str;
	}

	public Dimension getTextDimension() {
		return new Dimension((int) w, (int) height);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Point getLocation() {
		return new Point(x, y);
	}

	public int getType() {
		return type;
	}

	public int vlength() {
		if (str == null)
			return 0;
		return str.length();
	}

	public void setSvalue(final String s) {
		svalue = s;
	}

	public boolean nameTextShown = UtilityMiscellaneous.nameTextShown;

	@Override
	public boolean isdraw() {
		if (!super.isdraw())
			return false;
		if ((father != null) && !father.isdraw())
			return false;

		if (UtilityMiscellaneous.isApplication()) {
			if (type == NAME_TEXT)
				return UtilityMiscellaneous.nameTextShown;
		} else if (type == NAME_TEXT)
			return nameTextShown; // APPLET ONLY
		return true;
	}

	public int getSX() {
		double lx = 0;
		if (type == NORMAL_TEXT)
			lx = x;
		else if (type == NAME_TEXT) {
			final GEPoint p = (GEPoint) father;
			lx = p.getx() + x;
		} else if ((type == CNAME_TEXT) && UtilityMiscellaneous.show_angle_text) {
			final GEAngle ag = (GEAngle) father;
			lx = ag.getxForString() + x;
		}
		return (int) lx;
	}

	public int getSY() {
		double ly = 0;
		if (type == NORMAL_TEXT)
			// lx = x;
			ly = y;
		else if (type == NAME_TEXT) {
			final GEPoint p = (GEPoint) father;
			// lx = p.getx() + x;
			ly = p.gety() + y;
		} else if ((type == CNAME_TEXT) && UtilityMiscellaneous.show_angle_text) {
			final GEAngle ag = (GEAngle) father;
			// lx = ag.getxForString() + x;
			ly = ag.getyForString() + y;
		}
		return (int) ly;
	}

	public String getValueText() {
		final double r = tvalue.dvalue; // CTextValue.calvalue(tvalue, null);
		String shead = "";
		switch (m_width) {
		case 0:
			break;
		case 1:
			shead = m_name;
			break;
		case 2:
			shead = str;
			break;
		case 3: {
			if ((m_name == null) || (m_name.length() == 0))
				shead = str;
			else
				shead = m_name + " = " + str;
		}
			break;
		default:
			shead = str;
		}

		return shead + " = " + r;
	}

	public void draw(final Graphics2D g2) {
		if (!isdraw())
			return;

		String tstring = null;

		double lx, ly;
		lx = ly = 0;
		if (type == NORMAL_TEXT) {
			tstring = str;
			lx = x;
			ly = y;
			posX = lx;
			posY = ly;
		} else if (type == NAME_TEXT) {
			tstring = father.m_name;
			final GEPoint p = (GEPoint) father;
			lx = p.getx() + x;
			ly = p.gety() + y;
			posX = lx;
			posY = ly;
		} else if ((type == CNAME_TEXT) && UtilityMiscellaneous.show_angle_text) {
			final GEAngle ag = (GEAngle) father;
			tstring = str;
			final double r[] = GELine.Intersect(ag.lstart, ag.lend);
			if (r != null) {
				double dx = ag.getxForString() - r[0];
				double dy = ag.getyForString() - r[1];
				final double rad = Math.sqrt((w * w) + (height * height)) / 2;
				final double len = Math.sqrt((dx * dx) + (dy * dy));
				dx += (rad * dx) / len;
				dy += (rad * dy) / len;
				lx = (r[0] + x + dx) - (w / 2);
				ly = (r[1] + y + dy) - (height / 2);
				posX = lx;
				posY = ly;
				// lx = ag.getxForString() + x - w / 2;
				// ly = ag.getyForString() + y - height / 2;
			}

		} else if (type == VALUE_TEXT) {

			tstring = getValueText();
			lx = x;
			ly = y;
			posX = lx;
			posY = ly;
		}

		if (tstring == null)
			return;
		if (tstring.length() == 0)
			return;

		final String[] sl = tstring.split("\n");
		g2.setFont(font);
		super.prepareToBeDrawnAsUnselected(g2);
		final FontRenderContext frc = g2.getFontRenderContext();
		final Font f = g2.getFont();
		final LineMetrics lm = f.getLineMetrics(sl[0], frc);
		h = lm.getHeight();
		height = h * sl.length;
		w = 0;

		for (int i = 0; i < sl.length; i++) {
			final Rectangle2D r2 = f.getStringBounds(sl[i], frc);
			if (r2.getWidth() > w)
				w = r2.getWidth();
			g2.drawString(sl[i], (float) lx, (float) (ly + ((i + 1) * h)));
		}

	}

	@Override
	public void draw(final Graphics2D g2, final boolean select) {
		if (select) {
			if (bVisible) {
				int lx, ly;
				lx = ly = 0;

				if ((type == NORMAL_TEXT) || (type == VALUE_TEXT)) {
					lx = x;
					ly = y;
				} else if (type == NAME_TEXT) {
					final GEPoint p = (GEPoint) father;
					lx = (int) p.getx() + x;
					ly = (int) p.gety() + y;
				} else if (type == CNAME_TEXT) {
					final GEAngle ag = (GEAngle) father;
					final double r[] = GELine.Intersect(ag.lstart, ag.lend);
					if (r != null) {
						double dx = ag.getxForString() - r[0];
						double dy = ag.getyForString() - r[1];
						final double rad = Math.sqrt((w * w)
								+ (height * height)) / 2;
						final double len = Math.sqrt((dx * dx) + (dy * dy));
						dx += (rad * dx) / len;
						dy += (rad * dy) / len;
						lx = (int) ((r[0] + x + dx) - (w / 2));
						ly = (int) ((r[1] + y + dy) - (height / 2));
						posX = lx;
						posY = ly;
					}
				}
				final Rectangle rc = new Rectangle(lx - 2, ly + 2, (int) w + 2,
						(int) height + 2);
				g2.setColor(new Color(255, 200, 200));
				g2.fill(rc);
				g2.setColor(Color.black);
				g2.setStroke(new BasicStroke(0.5f));
				g2.draw(rc);
			}
		} else
			draw(g2);
	}

	@Override
	public String TypeString() {
		if (str == null)
			return "";

		int n = str.length();
		if (n >= 5)
			n = 4;
		else
			n--;
		if (n < 0)
			n = 0;

		if (str == null) {
		}

		if (type == NORMAL_TEXT) {
			final String st = Language.getLs(355, "text");
			return st + "(\"" + str.substring(0, n) + "..\")";
		} else if ((type == NAME_TEXT) || (type == CNAME_TEXT)) {
			final String st = Language.getLs(245, "name");
			return st + "(\"" + str.substring(0, n) + "..\")";
		} else if (type == VALUE_TEXT)
			if (m_name == null) {
				final String st = Language.getLs(355, "text");
				return st + "(\"" + str.substring(0, n) + "..\")";
			} else
				return m_name;
		return null;
	}

	@Override
	public String getDescription() {
		return TypeString();
	}

	public boolean inRect(double x0, double y0, double x1, double y1) {
		if (x0 > x1) {
			final double r = x0;
			x0 = x1;
			x1 = r;
		}

		if (y0 > y1) {
			final double r = y0;
			y0 = y1;
			y1 = r;

		}
		return (x0 < x) && (y0 < y) && (x1 > (x + w)) && (y1 > (y + height));
	}

	@Override
	public boolean isLocatedNear(final double x1, final double y1) {
		if (bVisible == false)
			return false;

		if ((type == NORMAL_TEXT) || (type == VALUE_TEXT)) {
			final double dx = x1 - x;
			final double dy = y1 - y;

			if ((dx > 0) && (dx < w) && (dy > 0) && (dy < height))
				return true;
			else
				return false;
		} else if (type == NAME_TEXT) {
			final GEPoint p = (GEPoint) father;
			double dx = p.getx();
			double dy = p.gety();
			dx = x1 - dx - x;
			dy = y1 - dy - y;
			if ((dx > 0) && (dx < w) && (dy > 0) && (dy < height))
				return true;
			else
				return false;
		} else if (type == CNAME_TEXT) {
			final double dx = (posX + (w / 2)) - x1;
			final double dy = (posY + (height / 2)) - y1;
			if ((dx > (-w / 2)) && (dx < (w / 2)) && (dy > (-height / 2))
					&& (dy < (height / 2)))
				return true;
			else
				return false;
		}
		return false;
	}

	public void drag(final double dx, final double dy) {
		x += dx;
		y += dy;
		if (type == NAME_TEXT) {
			if (((x * x) + (y * y)) > (UtilityMiscellaneous.rlength * UtilityMiscellaneous.rlength)) {
				final double r = Math.sqrt((x * x) + (y * y));
				x = (int) ((x * UtilityMiscellaneous.rlength) / r);
				y = (int) ((y * UtilityMiscellaneous.rlength) / r);
			}
		} else if (type == CNAME_TEXT) {
			final GEAngle ag = (GEAngle) father;
			final double x0 = ag.getxForString();
			final double y0 = ag.getyForString();

			double x1 = x - x0;
			double y1 = y - y0;
			if (((x1 * x1) + (y1 * y1)) > (UtilityMiscellaneous.rlength * UtilityMiscellaneous.rlength)) {
				final double r = Math.sqrt((x1 * x1) + (y1 * y1));
				x1 = (int) ((x1 * UtilityMiscellaneous.rlength) / r);
				y1 = (int) ((y1 * UtilityMiscellaneous.rlength) / r);
				x -= dx + x1;
				y -= dy + y1;
			}
		}
	}

	public void drag(final double x0, final double y0, final double dx,
			final double dy) {
		if ((type == NORMAL_TEXT) || (type == VALUE_TEXT))
			drag(dx, dy);
		else if (type == NAME_TEXT) {
			final GEPoint p = (GEPoint) father;
			final double xx = p.getx();
			final double yy = p.gety();

			final double xp = (x0 + dx) - xx;
			final double yp = (y0 + dy) - yy;

			final double len = Math.sqrt((xp * xp) + (yp * yp));

			if (len > UtilityMiscellaneous.rlength) {
				x = (int) ((xp * UtilityMiscellaneous.rlength) / len);
				y = (int) ((yp * UtilityMiscellaneous.rlength) / len);

			} else {
				x += dx;
				y += dy;
			}
		} else if (type == CNAME_TEXT) {
			final GEAngle p = (GEAngle) father;
			final double xx = p.getxForString() - (w / 2);
			final double yy = p.getyForString() - (height / 2);

			final double xp = (x0 + dx) - xx;
			final double yp = (y0 + dy) - yy;

			final double len = Math.sqrt((xp * xp) + (yp * yp));

			if (len > UtilityMiscellaneous.rlength) {
				x = (int) ((xp * UtilityMiscellaneous.rlength) / len);
				y = (int) ((yp * UtilityMiscellaneous.rlength) / len);

			} else {
				x += dx;
				y += dy;
			}
		}
	}

	@Override
	public void SavePS(final FileOutputStream fp, final int stype)
			throws IOException {
		if (!isdraw())
			return;
		if ((father != null) && !father.isdraw())
			return;

		String tstring = null;

		double lx, ly;
		lx = ly = 0;
		if (type == NORMAL_TEXT) {
			tstring = str;
			lx = x;
			ly = y;
		} else if (type == NAME_TEXT) {
			tstring = father.m_name;
			final GEPoint p = (GEPoint) father;
			lx = p.getx() + x;
			ly = p.gety() + y;
		} else if (type == CNAME_TEXT) {
			// CAngle ag = (CAngle) father;
			if ((str == null) || (str.length() == 0))
				tstring = svalue;
			else
				tstring = str;
			lx = posX;
			ly = posY;
		} else if (type == VALUE_TEXT) {
			tstring = getValueText();
			lx = x;
			ly = y;
		}

		if (type == NORMAL_TEXT) {
			String s = "";
			if (font.getStyle() == Font.BOLD)
				s += "/" + font.getName() + "-Bold";
			else
				s += "/" + font.getName() + "-Plain";

			fp.write((s + " findfont " + font.getSize() + " scalefont setfont")
					.getBytes());
			super.saveSuperColor(fp);
			fp.write("\n".getBytes());

			if (!tstring.contains("\n"))
				fp.write(("" + (int) lx + " " + (int) (-ly - 15) + " moveto ("
						+ tstring + ") " + "show\n").getBytes());
			else {
				final String[] str = tstring.split("\n");
				final int h = (int) height / str.length;
				for (int i = 0; i < str.length; i++)
					fp.write(((int) lx + " " + (int) (-ly - (h * i) - 15)
							+ " moveto (" + str[i] + ") " + "show\n")
							.getBytes());
			}
		} else
			fp.write(("mf " + (int) lx + " " + (int) (-ly - 15) + " moveto ("
					+ tstring + ") " + "show\n").getBytes());

	}

	public GEText(DrawPanel dp, final Element thisElement, final Map<Integer, GraphicEntity> mapGE) {
		super(dp, thisElement);

		str = thisElement.getAttribute("string_value");
		type = DrawPanelFrame.safeParseInt(thisElement.getAttribute("text_type"), 0, 0, 1);
		x = DrawPanelFrame.safeParseInt(thisElement.getAttribute("x"), 0);
		y = DrawPanelFrame.safeParseInt(thisElement.getAttribute("y"), 0);

		final int index = DrawPanelFrame.safeParseInt(thisElement.getAttribute("father"), 0);
		father = mapGE.get(index);
		if (type == NAME_TEXT) {
			if (father == null || !(father instanceof GEPoint))
				bIsValidEntity = false;
			else
				((GEPoint)father).setGEText(this);
		}			
	}

	@Override
	public Element saveIntoXMLDocument(final Element rootElement, final String sTypeName) {
		assert (rootElement != null);
		if (rootElement != null) {
			final Element elementThis = super.saveIntoXMLDocument(rootElement, "text");

			elementThis.setAttribute("text_type", String.valueOf(type));
			elementThis.setAttribute("x", String.valueOf(x));
			elementThis.setAttribute("y", String.valueOf(y));
			if (str != null && !str.isEmpty())
				elementThis.setAttribute("string_value", str);
			if (father != null)
				elementThis.setAttribute("father", String.valueOf(father.id()));

			return elementThis;
		}
		return null;
	}

	// public void Save(DataOutputStream out) throws IOException {
	//
	// super.Save(out);
	//
	// out.writeInt(type);
	// out.writeInt(x);
	// out.writeInt(y);
	//
	// byte[] s = font.getName().getBytes();
	// out.writeInt(s.length);
	// out.write(s, 0, s.length);
	// out.writeInt(font.getStyle());
	// out.writeInt(font.getSize());
	//
	// if (type == NORMAL_TEXT) {
	// s = str.getBytes();
	// out.writeInt(s.length);
	// out.write(s, 0, s.length);
	// } else if (type == NAME_TEXT) {
	// out.writeInt(father.m_id);
	// } else if (type == CNAME_TEXT) {
	// if (str == null)
	// str = "";
	//
	// s = str.getBytes();
	// out.writeInt(s.length);
	// out.write(s, 0, s.length);
	// out.writeInt(father.m_id);
	//
	// } else if (type == VALUE_TEXT) {
	// s = str.getBytes();
	// out.writeInt(s.length);
	// out.write(s, 0, s.length);
	// if (father == null)
	// out.writeInt(-1);
	// else out.writeInt(father.m_id);
	// }
	//
	//
	// }

	// public void Load(DataInputStream in, drawProcess dp) throws IOException {
	//
	// if (CMisc.version_load_now < 0.010) {
	// m_id = in.readInt();
	// x = in.readInt();
	// y = in.readInt();
	// int size = in.readInt();
	// byte[] s = new byte[size];
	// in.read(s, 0, size);
	// str = new String(s);
	//
	// if (CMisc.version_load_now >= 0.005) {
	// int n = in.readInt();
	// byte[] str = new byte[n];
	// in.read(str, 0, n);
	// String name = new String(str);
	// int type = in.readInt();
	// int sz = in.readInt();
	// int rgb = in.readInt();
	//
	// font = new Font(name, type, sz);
	//
	// if (CMisc.version_load_now == 0.006) {
	// if (rgb <= 0)
	// m_color = drawData.getColorIndex(Color.black);
	// else
	// m_color = rgb;
	// } else {
	// if (rgb < 0)
	// m_color = drawData.getColorIndex(Color.black);
	// else {
	// if (rgb == 9)
	// m_color = drawData.getColorIndex(Color.black);
	// else
	// m_color = (rgb);
	// }
	// }
	// }
	//
	// {
	// if (m_color == 1)
	// m_color = 3;
	// else if (m_color == 2)
	// m_color = 5;
	// else if (m_color == 3)
	// m_color = 11;
	// else if (m_color == 7)
	// m_color = 8;
	// }
	// } else {
	// super.Load(in, dp);
	//
	//
	// type = in.readInt();
	// x = in.readInt();
	// y = in.readInt();
	//
	// int n = in.readInt();
	// byte[] sstr = new byte[n];
	// in.read(sstr, 0, n);
	// String name = new String(sstr);
	//
	// int font_type = in.readInt();
	// int sz = in.readInt();
	// font = new Font(name, font_type, sz);
	//
	// if (type == NORMAL_TEXT) {
	// int size = in.readInt();
	// byte[] s = new byte[size];
	// in.read(s, 0, size);
	// str = new String(s);
	// } else if (type == NAME_TEXT) {
	// int id = in.readInt();
	// GEPoint p = dp.getPointById(id);
	// p.textNametag = this;
	// father = p;
	// } else if (type == CNAME_TEXT) {
	// int size = in.readInt();
	// byte[] s = new byte[size];
	// in.read(s, 0, size);
	// str = new String(s);
	//
	// int id = in.readInt();
	// CAngle ag = dp.getAngleByid(id);
	// ag.ptext = this;
	// father = ag;
	// } else if (type == VALUE_TEXT) {
	// int size = in.readInt();
	// byte[] s = new byte[size];
	// in.read(s, 0, size);
	// str = new String(s);
	// tvalue = CTextValue.parseString(str);
	// if (tvalue == null)
	// tvalue = new CTextValue();
	// if (CMisc.version_load_now >= 0.052) {
	// int id = in.readInt();
	// father = dp.getObjectById(id);
	// }
	// }
	//
	//
	// }
	//
	// }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((father == null) ? 0 : father.hashCode());
		result = prime * result + ((font == null) ? 0 : font.hashCode());
		long temp;
		temp = Double.doubleToLongBits(h);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(height);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (nameTextShown ? 1231 : 1237);
		temp = Double.doubleToLongBits(posX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(posY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((str == null) ? 0 : str.hashCode());
		result = prime * result + ((svalue == null) ? 0 : svalue.hashCode());
		result = prime * result + ((tvalue == null) ? 0 : tvalue.hashCode());
		result = prime * result + type;
		temp = Double.doubleToLongBits(w);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

}
