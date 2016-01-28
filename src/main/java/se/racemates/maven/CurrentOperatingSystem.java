package se.racemates.maven;

public class CurrentOperatingSystem {

    private static final String os = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return os.contains("win");
    }

    @SuppressWarnings("unused")
    public static boolean isMac() {
        return os.contains("mac");
    }

    @SuppressWarnings("unused")
    public static boolean isUnix() {
        return  os.contains("nix") ||
                os.contains("nux") ||
                os.contains("aix");
    }
}

