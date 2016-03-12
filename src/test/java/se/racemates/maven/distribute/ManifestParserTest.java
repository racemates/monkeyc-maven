package se.racemates.maven.distribute;

import org.junit.Test;
import se.racemates.maven.Utils;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ManifestParserTest {

    @Test
    public void testGetDevices() {

        final File manifest = Utils.getResource("mc/only-sources/main/manifest.xml");
        final ManifestParser manifestParser = new ManifestParser(manifest);
        assertThat(manifestParser.getDevices().size(), is(2));

    }
}
