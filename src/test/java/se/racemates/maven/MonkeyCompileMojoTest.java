package se.racemates.maven;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MonkeyCompileMojoTest extends AbstractMojoTestCase {

    public void testExecution() throws Exception {
        File pom = getTestFile("src/test/resources/mb/Drawable/pom.xml");
        assertThat(pom, is(notNullValue()));
        assertThat(pom.exists(), is(true));
        MonkeyCompileMojo monkeyCompileMojo = (MonkeyCompileMojo) lookupMojo("compile", pom);
        assertThat(monkeyCompileMojo, is(notNullValue()));
        monkeyCompileMojo.execute();
    }
}
