package se.racemates.maven;

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SimulatorRunner {

    public static final int PORT_SCAN_START = 1234;
    public static final int PORT_SCAN_END = 1239;
    public static final int SLEEP_TIME_BETWEEN_TRIES_TO_FIND_SIMULATOR = 500;
    private Process simulatorProcess;
    private Process programProcess;
    private final Log log;

    public SimulatorRunner(final Log log) {
        this.log = log;
    }

    public InputStream run(
            final String sdkPath,
            final String programFile
    ) throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder(sdkPath + "/bin/simulator.exe");
        this.simulatorProcess = processBuilder.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    final StreamGobbler simulatorErrorStreamGobbler = new StreamGobbler(
                            simulatorProcess.getErrorStream(),
                            System.err
                    );
                    final StreamGobbler simulatorOutputStreamGobbler = new StreamGobbler(
                            simulatorProcess.getInputStream(),
                            System.out
                    );
                    simulatorErrorStreamGobbler.start();
                    simulatorOutputStreamGobbler.start();
                    SimulatorRunner.this.simulatorProcess.waitFor();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

        for (int tries = 0; tries < 5; tries++) {
            Process transferFile = null;
            for (int port = PORT_SCAN_START; port < PORT_SCAN_END; port++) {
                transferFile = pushProgram(
                        port,
                        sdkPath,
                        programFile
                );
                transferFile.waitFor();
                this.log.debug("Exit value from shell transfer file:" + transferFile.exitValue());
                if (transferFile.exitValue() == 0) {
                    this.programProcess = startProgram(
                            port,
                            sdkPath,
                            programFile
                    );
                    break;
                }
            }
            if (transferFile.exitValue() == 0) {
                break;
            }
            Thread.sleep(SLEEP_TIME_BETWEEN_TRIES_TO_FIND_SIMULATOR);
        }

        final StreamGobbler errorGobbler = new StreamGobbler(
                this.programProcess.getErrorStream(),
                System.err
        );

        final Process finalProgramProcess = this.programProcess;

        new Thread() {
            @Override
            public void run() {
                try {
                    finalProgramProcess.waitFor();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
        errorGobbler.start();

        return this.programProcess.getInputStream();
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


    private static Process pushProgram(
            final int port,
            final String sdkPath,
            final String programFile
    ) throws IOException {
        final String name = new File(programFile).getName();
        System.out.println("pushProgram port:" + port);

        return
                new ProcessBuilder()
                        .
                                inheritIO()
                        .
                                command(
                                        sdkPath + "/bin/shell.exe",
                                        "--transport=tcp",
                                        "--transport_args=127.0.0.1:" + port,
                                        "push",
                                        "\"" + programFile + "\"",
                                        "0:/GARMIN/APPS/" + name
                                )
                        .start();

    }

    private static Process startProgram(
            final int port,
            final String sdkPath,
            final String programFile
    ) throws IOException {
        final String name = new File(programFile).getName();
        System.out.println("name:" + name + " port:" + port);
        return
                new ProcessBuilder()
                        .
                                command(
                                        sdkPath + "/bin/shell.exe",
                                        "--transport=tcp",
                                        "--transport_args=127.0.0.1:" + port,
                                        "tvm",
                                        "0:/GARMIN/APPS/" + name
                                )
                        .start();

    }
}
