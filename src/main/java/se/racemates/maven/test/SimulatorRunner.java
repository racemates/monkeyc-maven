package se.racemates.maven.test;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import se.racemates.maven.Util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class SimulatorRunner implements Closeable {

    private static final String SIMULATION_DEVICE_ID = "vivoactive_hr";
    private Process simulatorProcess;
    private Process programProcess;
    private final Log log;
    private final boolean runOnce;
    private boolean programFailed;

    public SimulatorRunner(
            final Log log,
            final boolean runOnce
    ) {
        this.log = log;
        this.runOnce = runOnce;
    }

    public InputStream run(
            final String sdkPath,
            final String programFile
    ) throws MojoExecutionException {

        startSimulatorThread(sdkPath);

        this.programProcess = transferAndStartProgram(
                sdkPath,
                programFile
        );

        return this.programProcess.getInputStream();
    }

    private Process transferAndStartProgram(
            final String sdkPath,
            final String programFile
    ) throws MojoExecutionException {
        try {
            return startProgramProcess(
                    sdkPath,
                    programFile
            );
        } catch (final IOException e) {
            throw new MojoExecutionException("Failed to start program.", e);
        }
    }

    private Process startProgramProcess(
            final String sdkPath,
            final String programFile
    ) throws IOException {

        final Process startProgram = startProgram(
                sdkPath,
                programFile
        );

        final StreamGobbler errorGobbler = new StreamGobbler(
                startProgram.getErrorStream(),
                System.err
        );
        errorGobbler.start();

        new Thread(() -> {
            try {
                startProgram.waitFor();
                if (startProgram.exitValue() != 0) {
                    SimulatorRunner.this.programFailed = true;
                }
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        return startProgram;
    }

    private void startSimulatorThread(final String sdkPath) throws MojoExecutionException {

        final String simulatorPath = sdkPath + "/bin";
        final String simulatorExecutable = simulatorPath + "/" + (Util.isWindows() ? "simulator.exe" : "connectiq");
        this.log.debug("Starting simulator process:" + simulatorExecutable);

        final ProcessBuilder processBuilder = new ProcessBuilder(
                simulatorExecutable
        );

        try {
            this.simulatorProcess = processBuilder.start();
        } catch (final IOException e) {
            throw new MojoExecutionException(
                    "Could not start simulator",
                    e
            );
        }

        new SimulatorThread(this.simulatorProcess).start();
    }

    private void killProgramProcess() {
        killProcessIfActive(this.programProcess);
    }

    private void killSimulatorProcess() {
        killProcessIfActive(this.simulatorProcess);
    }

    private void killProcessIfActive(final Process process) {
        if (process != null && process.isAlive()) {
            this.log.debug("Destroying forcible");
            process.destroyForcibly();
        }
    }

    private Process startProgram(
            final String sdkPath,
            final String programFile
    ) throws IOException {



        return new ProcessBuilder()
                .command(
                        "java",
                        "-classpath",
                        sdkPath + "/bin/monkeybrains.jar",
                        "com.garmin.monkeybrains.monkeydodeux.MonkeyDoDeux",
                        "-f",
                        "\"" + programFile + "\"",
                        "-d",
                        SIMULATION_DEVICE_ID,
                        "-s",
                        sdkPath + "/bin/shell"
                )
                .start();
    }

    @Override
    public void close() throws IOException {
        killProgramProcess();
        if (this.runOnce) {
            this.log.debug("Killing simulator process");
            killSimulatorProcess();
        }
    }

    public boolean hasProgramFailed() {
        return this.programFailed;
    }
}
