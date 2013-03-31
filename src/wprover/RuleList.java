package wprover;

import gprover.gib;
import gprover.jgex_IO;

import java.util.ArrayList;
import java.util.TreeMap;

import org.w3c.dom.*;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: 2007-5-9
 * Time: 15:28:50
 * To change this template use File | Settings | File Templates.
 */
public class RuleList {
	//  final private static boolean SAVEAGAIN = true;

	private RuleList() {
	}

	final private static TreeMap<Integer, grule> mapGDD = new TreeMap<Integer, grule>();
	final private static TreeMap<Integer, grule> mapFull = new TreeMap<Integer, grule>();

	//    final private static ArrayList<grule> GDDLIST = new ArrayList<grule>();
	//    final private static ArrayList<grule> FULLLIST = new ArrayList<grule>();

	final public static void getAllGDDRules(ArrayList<grule> v) {
		v.clear();
		if (mapGDD.isEmpty())
			loadRulesFromXML(false);
		for (int index = 1; index <= 50; ++index) {
			grule g = mapGDD.get(index);
			if (g != null)
				v.add(g);
		}
	}

	final public static void getAllFullRules(ArrayList<grule> v) {
		v.clear();
		if (mapFull.isEmpty())
			loadRulesFromXML(true);
		for (int index = 1; index <= 50; ++index) {
			grule g = mapFull.get(index);
			if (g != null)
				v.add(g);
		}
	}

	//    final public static void getAllGDDRules(ArrayList<grule> v) {
	//    	v.clear();
	//        v.addAll(GDDLIST);
	//    }
	//
	//    final public static void getAllFullRules(ArrayList<grule> v) {
	//    	v.clear();
	//        v.addAll(FULLLIST);
	//    }

	final public static grule getGrule(int n) {
		if (mapGDD.isEmpty())
			loadRulesFromXML(false);
		return mapGDD.get(n);
	}

	final public static grule getFrule(int n) {
		if (mapFull.isEmpty())
			loadRulesFromXML(true);
		return mapFull.get(n);
	}

	//    final public static grule getGrule(int n) {
	//        n--;
	//        if (n < 0 || n > GDDLIST.size())
	//            return null;
	//        return GDDLIST.get(n);
	//    }
	//
	//    final public static grule getFrule(int n) {
	//        n--;
	//        if (n < 0 || n > FULLLIST.size())
	//            return null;
	//        return FULLLIST.get(n);
	//    }

	//    final private static void loadRulesURL(URL base) {
	//        try {
	//            DataInputStream in = getStream(base, "fullrule");
	//
	//
	//
	//        } catch (IOException e) {
	//        }
	//    }

	/*private static DataInputStream getStream(URL base, String file) {
        try {
            URL ul = new URL(base, file);
            URLConnection urlc = ul.openConnection();
            urlc.connect();

            InputStream instream = urlc.getInputStream();
            DataInputStream in = new DataInputStream(instream);
            return in;
        } catch (IOException e) {
        }
        return null;
    }*/

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
								grule g = new grule(keyInt, name, description, example, bFull);
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


	/*private static void loadRules(String[] src, ArrayList<grule> vs, int type) {
        String s, s1, s2;
        s = s1 = s2 = null;

        int i = 0;
        int len = src.length;

        String t = src[i]; //reader.readLine().trim();

        int id = 1;

        while (t != null) {
            t = t.trim();
            if (t.length() != 0) {
                if (s != null && t.startsWith("*")) {
                    grule r = new grule(id++, s, s1, s2, type);
                    vs.add(r);
                    s = t;
                    s1 = s2 = null;
                } else {
                    if (s == null)
                        s = t;
                    else if (s1 == null)
                        s1 = t;
                    else s2 = t;
                }

            }
            if (i >= len - 1)
                break;

            t = src[++i];
        }
    }

    public static void loadRules() {
        loadRules(Rules.GDD, GDDLIST, 0);
        loadRules(Rules.FULL, FULLLIST, 1);
    }

    public static void writeRules(File file, File file2) {
        try {
            file2.createNewFile();

            BufferedReader reader = new BufferedReader(new FileReader(file));

            BufferedWriter writer = new BufferedWriter(new FileWriter(file2));

            String t = reader.readLine();
            writer.write("package gprover\n");
            writer.write("public class fullrule{\n");

            while (t != null) {
                if (t.length() != 0)
                    writer.write('"' + t + '"' + ",\n");

                t = reader.readLine();
            }
            writer.write("}");
            writer.flush();
            writer.close();
            reader.close();

        } catch (IOException ee) {
        }

    }*/


	public static boolean getValue(int n) {
		return gib.RValue[n - 1];
	}

	public static void setValue(int n, boolean v) {
		gib.RValue[n - 1] = v;
	}

}
