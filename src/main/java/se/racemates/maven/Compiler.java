package se.racemates.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Compiler {

    private final String compilerPath;
    private final File executionDir;
    private final Log log;

    public Compiler(final String sdkPath, final File executionDir, final Log log) {
        this.compilerPath = getCompilerPath(sdkPath);
        this.executionDir = executionDir;
        this.log = log;
    }

    public void compile(
            final Collection<File> projectDirs,
            final File manifest,
            final File target) throws MojoExecutionException {

        final List<File> sourceDirectories = getDirectories(projectDirs, "source");
        final List<File> sourceFiles = getFiles(sourceDirectories, "mc");
        final DependencyHelper dependencyHelper = new DependencyHelper(sourceFiles);
        final List<FileInfo> fileInfos = dependencyHelper.sortDependencies();
        final List<String> sourcePaths = fileInfos
                .stream()
                .map(FileInfo::getFile)
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());

        final List<File> resourceDirectories = getDirectories(projectDirs, "resources");
        final List<File> resourceFiles = getFiles(resourceDirectories, "xml");

        final String resourcePathsString = getPathsString(resourceFiles);

        final List<String> commands = new ArrayList<>();
        commands.add(this.compilerPath);
        commands.add("-m");
        commands.add(manifest.getAbsolutePath());
        commands.add("-o");
        commands.add(target.getAbsolutePath());
        commands.addAll(sourcePaths);
        commands.add("-z");
        commands.add(resourcePathsString);

        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commands).directory(this.executionDir);

        try {
            final Process process = processBuilder.start();

            final Logger infoLogger = Logger.info(process.getInputStream(), log);
            infoLogger.start();

            final Logger errorLogger = Logger.error(process.getErrorStream(), log);
            errorLogger.start();

            final int exitValue = process.waitFor();
            infoLogger.join();
            errorLogger.join();

            if (exitValue != 0) {
                throw new MojoExecutionException("Compilation error");
            }
        } catch (final IOException e) {
            throw new MojoExecutionException("Unable to call process monkeyc: " + e.getMessage(), e);
        } catch (final InterruptedException e) {
            throw new MojoExecutionException("Unable to wait for process: " + e.getMessage(), e);
        }
    }

    private String getPathsString(List<File> resourceFiles) {
        return resourceFiles
                .stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(Util.platformResourceSeparator()));
    }

    private List<File> getFiles(final Collection<File> directories, final String fileType) {
        return directories
                .stream()
                .map(file -> new FileScanner(file, fileType).scan())
                .flatMap(files -> files.stream())
                .collect(Collectors.toList());
    }

    private List<File> getDirectories(final Collection<File> directories, final String dirName) {
        return directories
                .stream()
                .map(file -> new File(file, dirName))
                .collect(Collectors.toList());
    }

    private String getCompilerPath(final String sdkPath) {
        return sdkPath + "/bin/" + "monkeyc" + (Util.isWindows() ? ".bat" : "");
    }
}
