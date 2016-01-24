package se.racemates.maven;

import java.io.*;

class StreamGobbler extends Thread {

    private final InputStream is;
    private final PrintStream out;

    public StreamGobbler(
            final InputStream is,
            final PrintStream out
    ) {
        this.is = is;
        this.out = out;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            for (String line = br.readLine();
                 line != null;
                 line = br.readLine()) {
                out.println(line);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}