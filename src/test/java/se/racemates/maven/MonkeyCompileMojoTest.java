package se.racemates.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import se.racemates.maven.mojo.MonkeyCompileMojo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MonkeyCompileMojoTest {

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testExecution_shouldSucced_whenBothSourceAndTest() throws Exception {
        final MonkeyCompileMojo compile = (MonkeyCompileMojo) mojoRule.lookupMojo("compile", "src/test/resources/mc/drawable/pom.xml");
        assertThat(compile, is(notNullValue()));
        compile.execute();
    }

    @Test
    public void testExecution_shouldSucced_whenOnlySource() throws Exception {
        final MonkeyCompileMojo compile = (MonkeyCompileMojo) mojoRule.lookupMojo("compile", "src/test/resources/mc/only-sources/pom.xml");
        assertThat(compile, is(notNullValue()));
        compile.execute();
    }

    @Test
    public void testExecution_shouldFail_whenEmptyProject() throws Exception {
        final MonkeyCompileMojo compile = (MonkeyCompileMojo) mojoRule.lookupMojo("compile", "src/test/resources/mc/empty/pom.xml");
        assertThat(compile, is(notNullValue()));
        expectedException.expect(MojoExecutionException.class);
        compile.execute();
    }

    @Test
    public void testExecution_shouldFail() throws Exception {
        final MonkeyCompileMojo compile = (MonkeyCompileMojo) mojoRule.lookupMojo("compile", "src/test/resources/mc/failable/pom.xml");
        assertThat(compile, is(notNullValue()));
        expectedException.expect(MojoExecutionException.class);
        compile.execute();
    }
}
