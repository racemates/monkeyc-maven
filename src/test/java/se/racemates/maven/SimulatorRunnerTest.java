package se.racemates.maven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static junit.framework.TestCase.assertEquals;

public class SimulatorRunnerTest {

    @org.junit.Test
    public void pickUpOuputFromProgram() throws Exception {
        final InputStream inputStream = SimulatorRunner.run(
                "C:\\garmin\\sdk1_2_2\\bin",
                "C:\\garmin\\workspace\\connectiq-run\\src\\test\\resources\\logsome.prg"
        );

        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line;
            for (line = br.readLine();
                 line != null;
                 line = br.readLine()) {
                System.out.println(line);
                if (line.startsWith("-->")) {
                    break;
                }
            }
            assertEquals("Tagged line picked up", "-->This line should be picked up", line);

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }
}