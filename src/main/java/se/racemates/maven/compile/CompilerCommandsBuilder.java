package se.racemates.maven.compile;

import se.racemates.maven.Util;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CompilerCommandsBuilder {

    private final Map<String, String> index;
    private final String compilerPath;
    private final Collection<File> sources;
    private final Collection<File> resources;

    public CompilerCommandsBuilder(final String sdkPath) {
        this.index = new HashMap<>();
        this.compilerPath = getCompilerPath(sdkPath);
        this.sources = new ArrayList<>();
        this.resources = new ArrayList<>();
    }

    public CompilerCommandsBuilder manifest(final File manifest) {
        this.index.put("-m", manifest.getAbsolutePath());
        return this;
    }

    public CompilerCommandsBuilder target(final File target) {
        this.index.put("-o", target.getAbsolutePath());
        return this;
    }

    public CompilerCommandsBuilder device(final String device) {
        this.index.put("-d", device);
        return this;
    }

    public CompilerCommandsBuilder sources(final Collection<File> sources) {
        this.sources.addAll(sources);
        return this;
    }

    public CompilerCommandsBuilder resources(final Collection<File> resources) {
        this.resources.addAll(resources);
        return this;
    }

    public List<String> build() {
        final List<String> commands = new ArrayList<>();
        commands.add(compilerPath);
        this.index.entrySet().forEach(entry -> {
            commands.add(entry.getKey());
            commands.add(entry.getValue());
        });
        commands.add("-z");
        commands.add(getPathsString(this.resources));
        this.sources.forEach(source -> {
            commands.add(source.getAbsolutePath());
        });
        return commands;
    }

    private String getCompilerPath(final String sdkPath) {
        return sdkPath + "/bin/" + "monkeyc" + (Util.isWindows() ? ".bat" : "");
    }

    private String getPathsString(Collection<File> resourceFiles) {
        return resourceFiles
                .stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(Util.platformResourceSeparator()));
    }
}
