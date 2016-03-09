package se.racemates.maven.compile;

import java.io.File;
import java.util.List;

public class FileInfo {

    private String name;
    private List<String> dependencies;
    private File file;

    public FileInfo(final String name, final List<String> dependencies, final File file) {
        this.name = name;
        this.dependencies = dependencies;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public File getFile() {
        return file;
    }
}
