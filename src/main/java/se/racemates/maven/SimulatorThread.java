package se.racemates.maven;

class SimulatorThread extends Thread {

    private final Process simulatorProcess;

    public SimulatorThread(final Process simulatorProcess) {
        this.simulatorProcess = simulatorProcess;
    }

    @Override
    public void run() {
        try {
            final StreamGobbler simulatorErrorStreamGobbler = new StreamGobbler(
                    this.simulatorProcess.getErrorStream(),
                    System.err
            );
            final StreamGobbler simulatorOutputStreamGobbler = new StreamGobbler(
                    this.simulatorProcess.getInputStream(),
                    System.out
            );
            simulatorErrorStreamGobbler.start();
            simulatorOutputStreamGobbler.start();
            this.simulatorProcess.waitFor();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
