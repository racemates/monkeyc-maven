package se.racemates.maven;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MonkeyCompileMojoTest {

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testExecution_shouldSucced() throws Exception {
        final Mojo compile = mojoRule.lookupMojo("compile", "src/test/resources/mc/Drawable/pom.xml");
        assertThat(compile, is(notNullValue()));
        compile.execute();
    }

    @Test
    public void testExecution_shouldFail() throws Exception {
        final Mojo compile = mojoRule.lookupMojo("compile", "src/test/resources/mc/Failable/pom.xml");
        assertThat(compile, is(notNullValue()));
        expectedException.expect(MojoExecutionException.class);
        compile.execute();
    }
}
