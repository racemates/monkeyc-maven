package se.racemates.maven;

import java.io.File;

public final class Utils {

    private Utils() {
    }

    public static File getResource(final String path) {
        return new File(Utils.class.getClassLoader().getResource(path).getFile());
    }
}
