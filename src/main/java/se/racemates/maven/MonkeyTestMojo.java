package se.racemates.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;

@Mojo(name = "monkeytest", defaultPhase = LifecyclePhase.TEST,
        requiresOnline = false, requiresProject = true,
        threadSafe = false)
public class MonkeyTestMojo
        extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/surefire-reports/TEST-connectiq.xml")
    private File outputFile;

    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}")
    private String programFile;

    @Parameter
    private String sdkPath;

    public void execute()
            throws MojoExecutionException {

        if (!outputFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            outputFile.getParentFile().mkdirs();

        }

        SimulatorRunner simulatorRunner = new SimulatorRunner();
        BufferedWriter fileWriter = null;
        InputStream inputStream = null;
        try {
            fileWriter = new BufferedWriter(new FileWriter(outputFile));

            inputStream = simulatorRunner.run(sdkPath, programFile);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            for (String line = br.readLine();
                 line != null;
                 line = br.readLine()) {
                if (line.startsWith("-->EOF")) {
                    fileWriter.close();
                    break;
                }

                if (line.startsWith("-->")) {
                    fileWriter.write(line.substring(3));
                }

            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + outputFile, e);
        } catch (InterruptedException e) {
            throw new MojoExecutionException("Error", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            simulatorRunner.killProgramProcess();
            simulatorRunner.killSimulatorProcess();
        }
    }
}
