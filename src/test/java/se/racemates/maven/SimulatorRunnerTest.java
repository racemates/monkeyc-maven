package se.racemates.maven;

import org.apache.maven.plugin.logging.SystemStreamLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static junit.framework.TestCase.assertTrue;

public class SimulatorRunnerTest {

    @org.junit.Test
    public void pickUpOuputFromProgram() throws Exception {
        final SimulatorRunner simulatorRunner = new SimulatorRunner(new SystemStreamLog());
        final InputStream inputStream = simulatorRunner.run(
                "C:/garmin/sdk1_2_2",
                "C:/garmin/workspace/connectiq-run/src/test/resources/logsome.prg"
        );

        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            StringBuilder input = new StringBuilder();
            for (int chr = isr.read();
                 chr != -1;
                 chr = isr.read()) {
                input.append((char) chr);
                if (input.toString().contains("-->")) {
                    break;
                }
            }
            assertTrue("Tagged line picked up", input.toString().contains("-->"));
            isr.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            simulatorRunner.killProgramProcess();
            simulatorRunner.killSimulatorProcess();
        }
    }
}