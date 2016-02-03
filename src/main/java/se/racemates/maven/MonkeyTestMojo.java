package se.racemates.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;

@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST,
        requiresOnline = false, requiresProject = true,
        threadSafe = false)
public class MonkeyTestMojo
        extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/monkey-reports/monkey-report.txt")
    private File outputFile;

    @Parameter(defaultValue = "true")
    private boolean runOnce;

    @Parameter(defaultValue = "${project.build.directory}/${project.name}-${project.version}.prg")
    private String programFile;

    @Parameter
    private String sdkPath;

    public void execute()
            throws MojoExecutionException {

        if (this.sdkPath == null) {
            this.sdkPath = System.getenv("GARMIN_HOME");
        }

        if (this.sdkPath == null) {
            throw new MojoExecutionException("You need to set up sdkPath to point to your garmin sdk.");
        }


        getLog().info("sdkPath is: " + this.sdkPath);

        if (!this.outputFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            this.outputFile
                    .getParentFile()
                    .mkdirs();
        }

        try (
                final SimulatorRunner simulatorRunner = new SimulatorRunner(
                        getLog(),
                        this.runOnce
                );
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(this.outputFile));

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(simulatorRunner.run(
                        this.sdkPath,
                        this.programFile
                )))
        ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                if (line.startsWith("-->EOF")) {
                    fileWriter.close();
                    return;
                }

                if (line.startsWith("-->FAILURE")) {
                    fileWriter.close();
                    throw new MojoExecutionException("Failed due to test errors");
                }

                if (line.startsWith("-->")) {
                    final String reportLine = line.substring(3);
                    fileWriter.write(reportLine);
                    getLog().info(reportLine);
                }
            }

        } catch (final IOException e) {
            throw new MojoExecutionException(
                    "Error creating file " + this.outputFile,
                    e
            );
        }
    }
}
