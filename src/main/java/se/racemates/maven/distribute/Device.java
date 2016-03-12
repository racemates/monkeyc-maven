package se.racemates.maven.distribute;

public class Device {

    private final String name;

    public Device(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
