package se.racemates.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE)
public class MonkeyCompileMojo extends AbstractMojo {

    @Parameter(property = "projectRoot", readonly = true)
    private File projectRoot;

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
    private File projectBuildDirectory;

    @Parameter(defaultValue = "${project.build.finalName}", required = true, readonly = true)
    private String targetFileName;

    @Parameter
    private String sdkPath;

    public void execute() throws MojoExecutionException {

        if (this.sdkPath == null) {
            this.sdkPath = System.getenv("GARMIN_HOME");
        }

        if (this.projectRoot == null) {
            this.projectRoot = basedir;
        }

        final File manifest = new File(this.projectRoot, "manifest.xml");
        final File bin = new File(this.projectBuildDirectory, this.targetFileName + ".prg");

        final File source = new File(this.projectRoot, "source");
        final List<File> sources = new FileScanner(source, "mc").scan();
        final DependencyHelper dependencyHelper = new DependencyHelper(sources);
        final List<FileInfo> fileInfos = dependencyHelper.sortDependencies();
        final List<String> sourcePaths = fileInfos
                .stream()
                .map(FileInfo::getFile)
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());

        final File resource = new File(this.projectRoot, "resources");
        final List<File> resources = new FileScanner(resource, "xml").scan();
        final String resourcePathsString = resources
                .stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(Util.platformResourceSeparator()));

        final List<String> commands = new ArrayList<>();
        commands.add(
                this.sdkPath + "/bin/" +
                        "monkeyc" + (Util.isWindows() ? ".bat" : "")
        );
        commands.add("-m");
        commands.add(manifest.getAbsolutePath());
        commands.add("-o");
        commands.add(bin.getAbsolutePath());
        commands.addAll(sourcePaths);
        commands.add("-z");
        commands.add(resourcePathsString);

        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commands).directory(this.projectRoot);

        try {
            final Process process = processBuilder.start();

            final Logger infoLogger = Logger.info(process.getInputStream(), getLog());
            infoLogger.start();

            final Logger errorLogger = Logger.error(process.getErrorStream(), getLog());
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
}
