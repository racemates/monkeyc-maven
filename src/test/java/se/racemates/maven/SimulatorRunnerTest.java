package se.racemates.maven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class SimulatorRunnerTest {

    @org.junit.Test
    public void pickUpOuputFromProgram() throws Exception {
        final InputStream inputStream = SimulatorRunner.run(
                "C:\\garmin\\sdk1_2_2\\bin",
                "C:\\garmin\\workspace\\connectiq-run\\src\\test\\resources\\logsome.prg"
        );

        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            StringBuffer input = new StringBuffer();
            for (int chr = isr.read();
                 chr != -1;
                 chr = isr.read()) {
                System.out.println("partial:" + input);
                input.append((char) chr);
                if (input.toString().contains("-->")) {
                    System.out.println("success:" + input);
                    break;
                }
            }
            assertTrue("Tagged line picked up", input.toString().contains("-->"));
            isr.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }
}