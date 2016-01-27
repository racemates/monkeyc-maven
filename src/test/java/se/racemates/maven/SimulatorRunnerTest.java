package se.racemates.maven;

import org.apache.maven.plugin.logging.SystemStreamLog;

import java.io.InputStreamReader;

import static junit.framework.TestCase.assertTrue;

public class SimulatorRunnerTest {

    @org.junit.Test
    public void pickUpOuputFromProgram() throws Exception {

        try (
                final SimulatorRunner simulatorRunner = new SimulatorRunner(
                        new SystemStreamLog(),
                        true
                );

                final InputStreamReader inputStreamReader = new InputStreamReader(simulatorRunner.run(
                        "C:/garmin/sdk1_2_2",
                        "C:/garmin/workspace/connectiq-run/src/test/resources/logsome.prg"
                ))
        ) {
            final StringBuilder input = new StringBuilder();

            for (int chr = inputStreamReader.read();
                 chr != -1;
                 chr = inputStreamReader.read()) {
                input.append((char) chr);
                if (input
                        .toString()
                        .contains("-->")) {
                    break;
                }
            }
            assertTrue(
                    "Tagged line picked up",
                    input
                            .toString()
                            .contains("-->")
            );
        }
    }
}