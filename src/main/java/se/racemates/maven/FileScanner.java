package se.racemates.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {

    private final File baseFolder;
    private final String fileType;
    private final List<File> foundFiles;

    public FileScanner(final File baseFolder, final String fileType) {
        this.baseFolder = baseFolder;
        this.fileType = fileType;
        this.foundFiles = new ArrayList<>();
    }

    public List<File> scan() {
        scanDirectory(baseFolder);
        return foundFiles;
    }

    private void scanDirectory(final File baseFolder) {
        final File[] files = baseFolder.listFiles();
        for (final File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file);
            } else {
                final String fileName = file.getName();
                final int index = fileName.lastIndexOf(".");
                final String extension = fileName.substring(index + 1);
                if (extension.equals(fileType)) {
                    foundFiles.add(file);
                }
            }
        }
    }
}
