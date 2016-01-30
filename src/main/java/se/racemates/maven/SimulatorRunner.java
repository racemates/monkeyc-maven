package se.racemates.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SimulatorRunner implements Closeable {

    public static final int PORT_SCAN_START = 1234;
    public static final int PORT_SCAN_END = 1239;
    public static final int SLEEP_TIME_BETWEEN_TRIES_TO_FIND_SIMULATOR = 500;
    private Process simulatorProcess;
    private Process programProcess;
    private final Log log;
    private final boolean runOnce;

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

        for (@SuppressWarnings("LocalCanBeFinal") int tries = 0; tries < 5; tries++) {
            Process transferFile;
            for (int port = PORT_SCAN_START; port < PORT_SCAN_END; port++) {
                try {
                    transferFile = pushProgram(
                            port,
                            sdkPath,
                            programFile
                    );
                    transferFile.waitFor();
                    this.log.debug("Exit value from shell transfer file:" + transferFile.exitValue());
                    if (transferFile.exitValue() == 0) {
                        return startProgramProcess(
                                sdkPath,
                                programFile,
                                port
                        );
                    }
                    Thread.sleep(SLEEP_TIME_BETWEEN_TRIES_TO_FIND_SIMULATOR);
                } catch (IOException | InterruptedException e) {
                    throw new MojoExecutionException(
                            "Failed to transfer file to simulator.",
                            e
                    );
                }
            }
        }

        throw new MojoExecutionException("Failed to transfer file to simulator.");
    }

    private Process startProgramProcess(
            final String sdkPath,
            final String programFile,
            final int port
    ) throws IOException {

        final Process startProgram = startProgram(
                port,
                sdkPath,
                programFile
        );

        final StreamGobbler errorGobbler = new StreamGobbler(
                startProgram.getErrorStream(),
                System.err
        );
        errorGobbler.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    startProgram.waitFor();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

        return startProgram;
    }

    private void startSimulatorThread(final String sdkPath) throws MojoExecutionException {

        final String simulatorPath = Util.platformCommand(
                sdkPath + "/bin",
                "simulator"
        );
        this.log.debug("Starting simulator process:" + simulatorPath);

        final ProcessBuilder processBuilder = new ProcessBuilder(
                simulatorPath
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

    public void killProgramProcess() {
        kippProcessIfActive(this.programProcess);
    }

    public void killSimulatorProcess() {
        kippProcessIfActive(this.simulatorProcess);
    }

    private void kippProcessIfActive(final Process process) {
        if (process != null && process.isAlive()) {
            process.destroy();
        }
    }

    private Process pushProgram(
            final int port,
            final String sdkPath,
            final String programFile
    ) throws IOException {

        final String name = new File(programFile).getName();
        this.log.debug("pushProgram port:" + port);

        return new ProcessBuilder()
                .command(
                        Util.platformCommand(sdkPath + "/bin", "shell"),
                        "--transport=tcp",
                        "--transport_args=127.0.0.1:" + port,
                        "push",
                        "\"" + programFile + "\"",
                        "0:/GARMIN/APPS/" + name
                ).start();
    }

    private Process startProgram(
            final int port,
            final String sdkPath,
            final String programFile
    ) throws IOException {

        final String name = new File(programFile).getName();
        this.log.debug("name:" + name + " port:" + port);

        return new ProcessBuilder()
                .command(
                        Util.platformCommand(sdkPath + "/bin", "shell"),
                        "--transport=tcp",
                        "--transport_args=127.0.0.1:" + port,
                        "tvm",
                        "0:/GARMIN/APPS/" + name
                )
                .start();
    }

    @Override
    public void close() throws IOException {
        killProgramProcess();
        if (this.runOnce) {
            killSimulatorProcess();
        }
    }
}
