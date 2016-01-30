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

    public void execute() throws MojoExecutionException {



        final File manifest = new File(baseDir, "manifest.xml");
        final File bin = new File(baseDir, "bin/test.prg");

        final File source = new File(baseDir, "source");
        final List<File> sources = new FileScanner(source, "mc").scan();
        final List<String> sourcePaths = sources.stream().map(File::getAbsolutePath).collect(Collectors.toList());

        final File resource = new File(baseDir, "resources");
        final List<File> resources = new FileScanner(resource, "xml").scan();
        final String resourcePathsString = resources.stream().map(File::getAbsolutePath).collect(Collectors.joining(":"));

        final List<String> commands = new ArrayList<>();
        commands.add("monkeyc");
        commands.add("-m");
        commands.add(manifest.getAbsolutePath());
        commands.add("-o");
        commands.add(bin.getAbsolutePath());
        commands.addAll(sourcePaths);
        commands.add("-z");
        commands.add(resourcePathsString);

        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commands).directory(baseDir);

        final Process process;
        final String result;
        final String errors;
        try {
            process = processBuilder.start();
            result = readString(process.getInputStream());
            errors = readString(process.getErrorStream());
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to call process monkeyc: " + e.getMessage(), e);
        }

        final int exitValue;
        try {
            exitValue = process.waitFor();
        } catch (InterruptedException e) {
            throw new MojoExecutionException("Unable to wait for process: " + e.getMessage(), e);
        }
        System.out.println(errors);
        System.out.println(result);
        System.out.println("Exit value: " + exitValue);

    }

    //TODO write own code
    private static String readString(InputStream processInputStream) throws IOException {
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(processInputStream));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = inputStream.readLine()) != null) {
            result.append(line).append("\n");
        }
        return result.toString().trim();
    }
}
