package se.racemates.maven;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class DependencyHelper {

    private final Map<String, FileInfo> map;
    private final List<String> index;

    public DependencyHelper(final List<File> files) {
        this.map = transform(files);
        this.index = new ArrayList<>();
    }

    public List<FileInfo> sortDependencies() {
        final List<FileInfo> result = new ArrayList<>();
        map.keySet().forEach(this::checkDependencies);
        index.forEach(key -> {
            result.add(map.get(key));
        });

        return result;
    }

    private void checkDependencies(final String key) {
        if (index.contains(key)) {
            return;
        }

        final FileInfo fileInfo = map.get(key);
        if (fileInfo == null) {
            return;
        }

        final List<String> dependencies = fileInfo.getDependencies();
        for (final String dependency : dependencies) {
            checkDependencies(dependency);
        }

        index.add(key);
    }

    private Map<String, FileInfo> transform(final List<File> files) {

        final Map<String, FileInfo> index = new HashMap<>();
        for (final File file : files) {
            final List<String> strings = getStrings(file);
            final MetadataExtractor metadataExtractor = new MetadataExtractor(strings);
            final Optional<String> className = metadataExtractor.className();
            if (!className.isPresent()) {
                continue;
            }
            final FileInfo fileInfo = new FileInfo(className.get(), metadataExtractor.dependencies(), file);
            index.put(fileInfo.getName(), fileInfo);
        }

        return index;
    }

    private List<String> getStrings(final File file1) {
        try {
            return Files.readAllLines(file1.toPath());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read file", e);
        }
    }
}
