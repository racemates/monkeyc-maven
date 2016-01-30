package se.racemates.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mojo(name = "monkeyc", defaultPhase = LifecyclePhase.COMPILE)
public class MonkeyMaven extends AbstractMojo {

    @Parameter(property = "baseDir", required = true)
    private File baseDir;

    @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
    private File projectBuildDirectory;

    @Parameter
    private String sdkPath;

    public void execute() throws MojoExecutionException {

        if (this.sdkPath == null) {
            this.sdkPath = System.getenv("GARMIN_HOME");
        }

        final File manifest = new File(this.baseDir, "manifest.xml");
        final File bin = new File(this.baseDir, "bin/test.prg");

        final File source = new File(this.baseDir, "source");
        final List<File> sources = new FileScanner(source, "mc").scan();
        final List<String> sourcePaths = sources.stream().map(File::getAbsolutePath).collect(Collectors.toList());

        final File resource = new File(this.baseDir, "resources");
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
        processBuilder.command(commands).directory(this.baseDir);

        final Process process;
        final String result;
        final String errors;
        try {
            process = processBuilder.start();
            result = readString(process.getInputStream());
            errors = readString(process.getErrorStream());
        } catch (final IOException e) {
            throw new MojoExecutionException("Unable to call process monkeyc: " + e.getMessage(), e);
        }

        final int exitValue;
        try {
            exitValue = process.waitFor();
        } catch (final InterruptedException e) {
            throw new MojoExecutionException("Unable to wait for process: " + e.getMessage(), e);
        }
        System.out.println(errors);
        System.out.println(result);
        System.out.println("Exit value: " + exitValue);

    }

    //TODO write own code
    private static String readString(final InputStream processInputStream) throws IOException {
        final BufferedReader inputStream = new BufferedReader(new InputStreamReader(processInputStream));
        final StringBuilder result = new StringBuilder();
        String line;
        while((line = inputStream.readLine()) != null) {
            result.append(line).append("\n");
        }
        return result.toString().trim();
    }
}
