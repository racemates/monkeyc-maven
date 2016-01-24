package se.racemates.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SimulatorRunner {


    public static InputStream run(
            String sdkPath,
            String programFile
    ) throws IOException, InterruptedException {

        final ProcessBuilder processBuilder = new ProcessBuilder(sdkPath + "\\simulator.exe");
        final Process simulator = processBuilder.inheritIO().start();
        new Thread() {
            @Override
            public void run() {
                try {
                    simulator.waitFor();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

        Process programProcess = null;
        for (int tries = 0; tries < 5; tries++) {
            Process transferFile = null;
            for (int port = 1234; port < 1239; port++) {
                transferFile = pushProgram(port, sdkPath, programFile);
                transferFile.waitFor();
                if (transferFile.exitValue() == 1) {
                    programProcess = startProgram(port, sdkPath, programFile);
                    break;
                }
            }
            if (transferFile.exitValue() == 1) {
                break;
            }
            Thread.sleep(500);
        }

        assert programProcess != null;

        final StreamGobbler errorGobbler = new StreamGobbler(programProcess.getErrorStream(), System.err);
        errorGobbler.start();

        final Process finalProgramProcess = programProcess;

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


        final InputStream inputStream = programProcess.getInputStream();

        return inputStream;
    }

    private static Process pushProgram(int port, String sdkPath, String programFile) throws IOException {
        return
                new ProcessBuilder().
                        inheritIO().
                        command(
                                sdkPath + "\\shell.exe",
                                "--transport=tcp",
                                "--transport_args=127.0.0.1:" + port,
                                "push",
                                "\"" + programFile + "\"",
                                "0:/GARMIN/APPS/$FILE"
                        ).start();

    }

    private static Process startProgram(int port, String sdkPath, String programFile) throws IOException {
        return
                new ProcessBuilder().
                        command(
                                sdkPath + "\\shell.exe",
                                "--transport=tcp",
                                "--transport_args=127.0.0.1:" + port,
                                "tvm",
                                "\"" + programFile + "\"",
                                "0:/GARMIN/APPS/" + new File(programFile).getName(),
                                "1"
                        ).start();

    }


    //"$MB_HOME"/shell --transport=tcp --transport_args=127.0.0.1:$i push "$PRG_PATH" "0:/GARMIN/APPS/$FILE"
    //        if [ $? -eq 1 ]; then
    //continue
    //fi
    //"$MB_HOME"/shell --transport=tcp --transport_args=127.0.0.1:$i tvm "0:/GARMIN/APPS/$FILE" $DEVICE_ID

}
