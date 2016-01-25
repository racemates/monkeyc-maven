package se.racemates.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SimulatorRunner {

    private Process simulatorProcess;
    private Process programProcess;

    public InputStream run(
            String sdkPath,
            String programFile
    ) throws IOException, InterruptedException {

        final ProcessBuilder processBuilder = new ProcessBuilder(sdkPath + "\\simulator.exe");
        simulatorProcess = processBuilder.inheritIO().start();
        new Thread() {
            @Override
            public void run() {
                try {
                    simulatorProcess.waitFor();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

        for (int tries = 0; tries < 5; tries++) {
            Process transferFile = null;
            for (int port = 1234; port < 1239; port++) {
                transferFile = pushProgram(port, sdkPath, programFile);
                transferFile.waitFor();
                System.out.println("Exit value:" + transferFile.exitValue() );
                if (transferFile.exitValue() == 0) {
                    programProcess = startProgram(port, sdkPath, programFile);
                    break;
                }
            }
            if (transferFile.exitValue() == 0) {
                break;
            }
            Thread.sleep(500);
        }

        assert programProcess != null;

        final StreamGobbler errorGobbler = new StreamGobbler(programProcess.getErrorStream(), System.err);
 ;      final Process finalProgramProcess = programProcess;

        new Thread() {
            @Override
            public void run() {
                try {
                    finalProgramProcess.waitFor();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
        errorGobbler.start();

        return programProcess.getInputStream();
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


    private static Process pushProgram(int port, String sdkPath, String programFile) throws IOException {
        final String name = new File(programFile).getName();
        System.out.println("pushProgram port:" + port);

        return
                new ProcessBuilder().
                        inheritIO().
                        command(
                                sdkPath + "\\shell.exe",
                                "--transport=tcp",
                                "--transport_args=127.0.0.1:" + port,
                                "push",
                                "\"" + programFile + "\"",
                                "0:/GARMIN/APPS/" + name
                        ).start();

    }

    private static Process startProgram(final int port, final String sdkPath, final String programFile) throws IOException {
        final String name = new File(programFile).getName();
        System.out.println("name:" + name + " port:" + port);
        return
                new ProcessBuilder().
                        command(
                                sdkPath + "\\shell.exe",
                                "--transport=tcp",
                                "--transport_args=127.0.0.1:" + port,
                                "tvm",
                                "0:/GARMIN/APPS/" + name
                        ).start();

    }
}
