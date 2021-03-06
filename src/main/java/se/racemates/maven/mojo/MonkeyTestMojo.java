package se.racemates.maven.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import se.racemates.maven.compile.MonkeyCompiler;
import se.racemates.maven.test.SimulatorRunner;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST,
        requiresOnline = false, requiresProject = true,
        threadSafe = false)
public class MonkeyTestMojo extends AbstractMonkeyMojo {

    public static final int RETRIES = 30;
    public static final int WAIT_FOR_OUTPUT_MILLIS = 500;

    @Parameter(defaultValue = "${project.build.directory}/monkey-reports/monkey-report.txt")
    private File testReportFile;

    @Parameter(defaultValue = "true")
    private boolean runOnce;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();

        if (!this.testReportFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            this.testReportFile
                    .getParentFile()
                    .mkdirs();
        }

        final MonkeyCompiler compiler = new MonkeyCompiler(sdkPath, basedir, getLog());
        if (!this.projectTestRoot.exists()) {
            getLog().info("No test sources found to compile");
        } else {
            final File testManifest = getManifestWithReplacedId();

            final File testTarget = new File(this.projectBuildDirectory, this.targetFileName + TEST_BIN_SUFFIX);
            compiler.compile(Arrays.asList(this.projectSrcRoot, this.projectTestRoot), testManifest, getKey(), testTarget);
            runSimulator();
        }
    }

    private File getManifestWithReplacedId() throws MojoExecutionException {
        try {
            final File testManifest = new File(this.projectTestRoot, MANIFEST_FILE_NAME);
            Charset charset = StandardCharsets.UTF_8;

            String content = new String(Files.readAllBytes(testManifest.toPath()), charset);
            content = content.replace("00000000-0000-0000-0000-000000000000",
                                      UUID
                                              .randomUUID().toString());
            final File modifiedManifest = new File(
                    this.projectBuildDirectory,
                    testManifest.getName()
            );
            Path modifiedFile = Files.write(modifiedManifest.toPath(), content.getBytes(charset));

            return modifiedFile.toFile();

        } catch (IOException e) {
            throw new MojoExecutionException("Error processing manifest", e);
        }
    }

    private void runSimulator() throws MojoFailureException, MojoExecutionException {

        try (
                final SimulatorRunner simulatorRunner = new SimulatorRunner(
                        getLog(),
                        this.runOnce
                );

                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(this.testReportFile));

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(simulatorRunner.run(
                        this.sdkPath,
                        getTestFilePath()
                )))
        ) {
            handleSimulatorOutput(
                    fileWriter,
                    bufferedReader,
                    simulatorRunner);

        } catch (final IOException e) {
            throw new MojoExecutionException(
                    "Error creating file " + this.testReportFile,
                    e
            );
        }
    }

    private void handleSimulatorOutput(
            final BufferedWriter fileWriter,
            final BufferedReader bufferedReader,
            final SimulatorRunner simulatorRunner
    ) throws IOException, MojoFailureException {

        int timeout = 0;

        final StringBuilder output = new StringBuilder();

        while (timeout++ < RETRIES) {

            if (simulatorRunner.hasProgramFailed()) {
                throw new MojoFailureException("Program failed.");
            }

            while (bufferedReader.ready()) {

                timeout = 0;

                final String line = bufferedReader.readLine();
                output.append(line);
                output.append("\n");

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
            }

            try {
                Thread.sleep(WAIT_FOR_OUTPUT_MILLIS);
            } catch (final InterruptedException e) {
                //ignore
            }
        }

        throw new MojoFailureException("Waiting for test result timed out \n" + output.toString());

    }

    public void setTestReportFile(final File testReportFile) {
        this.testReportFile = testReportFile;
    }

    public void setRunOnce(boolean runOnce) {
        this.runOnce = runOnce;
    }
}
