package wprover;


public class Version {

    private static final float version = 0.81f;
    private static final String sversion = "0.81";
    private static final String data = " 02/02/2013 ";
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
