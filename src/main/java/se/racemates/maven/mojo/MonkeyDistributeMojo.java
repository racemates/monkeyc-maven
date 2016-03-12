package se.racemates.maven.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import se.racemates.maven.distribute.ManifestParser;

import java.io.File;

public class MonkeyDistributeMojo extends AbstractMonkeyMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();

        final File manifest = getManifest();
        final ManifestParser manifestParser = new ManifestParser(manifest);

        manifestParser.getDevices().forEach(device -> {

        });
    }
}
