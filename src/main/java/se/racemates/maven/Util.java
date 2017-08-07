package se.racemates.maven;

public final class Util {

    private static final String os = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return os.contains("win");
    }


    public static String platformResourceSeparator() {
        return isWindows() ? ";" : ":";
    }

}

