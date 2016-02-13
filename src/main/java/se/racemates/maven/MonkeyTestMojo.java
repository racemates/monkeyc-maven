package se.racemates.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;

@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST,
        requiresOnline = false, requiresProject = true,
        threadSafe = false)
public class MonkeyTestMojo
        extends AbstractMojo {

    public static final int RETRIES = 30;
    public static final int WAIT_FOR_OUTPUT_MILLIS = 500;
    @Parameter(defaultValue = "${project.build.directory}/monkey-reports/monkey-report.txt")
    private File outputFile;

    @Parameter(defaultValue = "true")
    private boolean runOnce;

    @Parameter(defaultValue = "${project.build.directory}/${project.name}-${project.version}.prg")
    private String programFile;

    @Parameter
    private String sdkPath;

    public void execute()
            throws MojoExecutionException, MojoFailureException {

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
            readOutput(
                    fileWriter,
                    bufferedReader,
                    simulatorRunner);

        } catch (final IOException e) {
            throw new MojoExecutionException(
                    "Error creating file " + this.outputFile,
                    e
            );
        }
    }

    private void readOutput(
            final BufferedWriter fileWriter,
            final BufferedReader bufferedReader,
            final SimulatorRunner simulatorRunner
    ) throws IOException, MojoFailureException {

        int timeout = 0;

        while (timeout++ < RETRIES) {

            if (simulatorRunner.hasProgramFailed()) {
                throw new MojoFailureException("Program failed.");
            }

            while (bufferedReader.ready()) {

                timeout = 0;

                final String line = bufferedReader.readLine();

                if (line.startsWith("-->EOF")) {
                    fileWriter.close();
                    return;
                }

                if (line.startsWith("-->FAILURE")) {
                    fileWriter.close();
                    throw new MojoFailureException("Failed due to test errors");
                }

                if (line.startsWith("-->")) {
                    final String reportLine = line.substring(3);
                    fileWriter.write(reportLine);
                    getLog().info(reportLine);
                }

                if (line == null) {
                    throw new MojoFailureException("Unexpected exit from program.");
                }
            }

            try {
                Thread.sleep(WAIT_FOR_OUTPUT_MILLIS);
            } catch (final InterruptedException e) {
                //ignore
            }
        }

        throw new MojoFailureException("Waiting for test result timed out");

    }
}
