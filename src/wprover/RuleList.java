package wprover;

import gprover.gib;
import gprover.jgex_IO;

import java.util.ArrayList;
import java.util.TreeMap;

import org.w3c.dom.*;

/**
 * This class contains the textual representations for presentation of the rules used 
 * for the Geometry Deductive Database (GDD) and Full Angle techniques for automated theorem proving.
 */
public class RuleList {

	private RuleList() {
	}

	final private static TreeMap<Integer, Rule> mapGDD = new TreeMap<Integer, Rule>();
	final private static TreeMap<Integer, Rule> mapFull = new TreeMap<Integer, Rule>();

	final public static void getAllGDDRules(ArrayList<Rule> v) {
		v.clear();
		if (mapGDD.isEmpty())
			loadRulesFromXML(false);
		for (int index = 1; index <= 50; ++index) {
			Rule g = mapGDD.get(index);
			if (g != null)
				v.add(g);
		}
	}

	final public static void getAllFullRules(ArrayList<Rule> v) {
		v.clear();
		if (mapFull.isEmpty())
			loadRulesFromXML(true);
		for (int index = 1; index <= 50; ++index) {
			Rule g = mapFull.get(index);
			if (g != null)
				v.add(g);
		}
	}

	final public static Rule getGrule(int n) {
		if (mapGDD.isEmpty())
			loadRulesFromXML(false);
		return mapGDD.get(n);
	}

	final public static Rule getFrule(int n) {
		if (mapFull.isEmpty())
			loadRulesFromXML(true);
		return mapFull.get(n);
	}

	private static void loadRulesFromXML(boolean bFull) {
		String path = jgex_IO.getWorkingDirectory() + (bFull ? "full_angle_rules.xml" : "gdd_rules.xml");
		final Document document = jgex_IO.openXMLfile(path);
		document.normalize();
		final Element docElement = document.getDocumentElement();
		final NodeList listDictionary = docElement.getElementsByTagName("dict");
		if (listDictionary != null) {
			for (int j = 0; j < listDictionary.getLength(); ++j) {
				final Node nDictionary = listDictionary.item(j);
				if (nDictionary != null) {
					NodeList nlist = nDictionary.getChildNodes();
					Integer keyInt = null;
					String name = "";
					String description = "";
					String example = "";
					for (int i = 0; i < nlist.getLength(); ++i) {
						final Node nn = nlist.item(i);					
						if ((nn != null) && (nn instanceof Element)) {
							Element ee = (Element)nn;
							final String sEntry = nn.getNodeName();
							if (sEntry.equals("key")) {
								String keyStr = ee.getTextContent();
								try {
									keyInt = Integer.parseInt(keyStr);
								} catch (NumberFormatException e) {
								}
							}
							String typeStr = nn.getNodeName();

							if (typeStr.equals("name")) {
								name = ee.getTextContent();
							}
							if (typeStr.equals("string")) {
								description = ee.getTextContent();
							}
							if (typeStr.equals("example")) {
								example = ee.getTextContent();
							}
							if (typeStr.equals("array")) { // TODO: Cut out all this code by getting rid of <array> in the xml dtd
								final NodeList nnlist = ee.getChildNodes();
								for (int ii = 0; ii < nnlist.getLength(); ++ii) {
									Node nnnn = nnlist.item(ii);
									if ((nnnn != null) && (nnnn instanceof Element)) {
										Element eeee = (Element)nnnn;
										typeStr = nnnn.getNodeName();
										if (typeStr.equals("name")) {
											name = eeee.getTextContent();
										}
										if (typeStr.equals("string")) {
											description = eeee.getTextContent();
										}
										if (typeStr.equals("example")) {
											example = eeee.getTextContent();
										}
									}
								}
							}								

							if (keyInt != null && keyInt > 0 && (!name.isEmpty() || !description.isEmpty() || !example.isEmpty())) {
								Rule g = new Rule(keyInt, name, description, example, bFull);
								if (bFull)
									mapFull.put(keyInt, g);
								else
									mapGDD.put(keyInt, g);
								keyInt = null;
								name = "";
								description = "";
								example = "";
							}
						}
					}
				}
			}
		}
	}

	public static boolean getValue(int n) {
		return gib.RValue[n - 1];
	}

	public static void setValue(int n, boolean v) {
		gib.RValue[n - 1] = v;
	}

}
