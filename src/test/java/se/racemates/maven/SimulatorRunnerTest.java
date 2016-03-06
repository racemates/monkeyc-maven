package se.racemates.maven;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.InputStreamReader;

import static junit.framework.TestCase.assertTrue;

public class SimulatorRunnerTest extends AbstractMojoTestCase {

    public void testPickUpOutputFromProgram() throws Exception {

        try (
                final SimulatorRunner simulatorRunner = new SimulatorRunner(
                        new SystemStreamLog(),
                        true
                );

                final InputStreamReader inputStreamReader = new InputStreamReader(simulatorRunner.run(
                        System.getenv("GARMIN_HOME"),
                        getTestPath("logsome-test.prg")
                ))
        ) {
            final StringBuilder input = new StringBuilder();

            for (int chr = inputStreamReader.read();
                 chr != -1;
                 chr = inputStreamReader.read()) {
                input.append((char) chr);
                if (input.toString().contains("-->")) {
                    break;
                }
            }
            assertTrue(
                    "Tagged line picked up",
                    input.toString().contains("-->")
            );
        }
    }
}