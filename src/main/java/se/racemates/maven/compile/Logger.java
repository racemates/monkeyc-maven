package se.racemates.maven.compile;

import org.apache.maven.plugin.logging.Log;

import java.io.*;

public class Logger extends Thread {

    enum Level {
        INFO,
        ERROR
    }

    private final InputStream inputStream;
    private final Log log;
    private final Level level;

    private Logger(
            final InputStream inputStream,
            final Log log,
            final Level level) {
        this.inputStream = inputStream;
        this.log = log;
        this.level = level;
    }

    public static Logger info(
            final InputStream inputStream,
            final Log log) {
        return new Logger(inputStream, log, Level.INFO);
    }

    public static Logger error(
            final InputStream inputStream,
            final Log log) {
        return new Logger(inputStream, log, Level.ERROR);
    }

    public void run() {
        try (
                final BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(this.inputStream))
        ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                this.log(line);
            }
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void log(final String message) {
        switch (level){
            case INFO:
                this.log.info(message);
                break;
            case ERROR:
                this.log.error(message);
                break;
        }
    }
}