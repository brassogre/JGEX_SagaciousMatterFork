package wprover;


public class UtilityVersion {

    private static final float version = 0.82f;
    private static final String sversion = "0.82";
    private static final String data = " 04/04/2013 ";
    private static final String project = "Java Geometry Expert";


    public static final float getVersionf() {
        return version;
    }

    public static final String getVersion1() {
        return sversion;
    }

    public static final String getNameAndVersion() {
        return project + " " + sversion;
    }

    public static final String getVersion() {
        return " " + project + " ";
    }

    public static final String getProject() {
        return project;
    }

    public static final String getData() {
        return data;
    }
}
