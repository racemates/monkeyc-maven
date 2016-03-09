package se.racemates.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import se.racemates.maven.mojo.MonkeyCompileMojo;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MonkeyCompileMojoTest {

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    @Test
    public void testExecution_shouldSucceed_whenBothSourceAndTest() throws Exception {

        final String targetFileName = "drawable";
        final File baseDirectory = temporaryFolder.newFolder();
        final File targetFolder = temporaryFolder.newFolder();

        final MonkeyCompileMojo compile = (MonkeyCompileMojo) mojoRule.lookupMojo("compile", "src/test/resources/mc/Drawable/pom.xml");
        compile.setBasedir(baseDirectory);
        compile.setProjectBuildDirectory(targetFolder);
        compile.setTargetFileName(targetFileName);

        assertThat(compile, is(notNullValue()));
        compile.execute();
    }

    @Test
    public void testExecution_shouldSucceed_whenOnlySource() throws Exception {
        final String targetFileName = "only-sources";
        final File baseDirectory = temporaryFolder.newFolder();
        final File targetFolder = temporaryFolder.newFolder();

        final MonkeyCompileMojo compile = (MonkeyCompileMojo) mojoRule.lookupMojo("compile", "src/test/resources/mc/only-sources/pom.xml");
        compile.setBasedir(baseDirectory);
        compile.setProjectBuildDirectory(targetFolder);
        compile.setTargetFileName(targetFileName);
        
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
        final MonkeyCompileMojo compile = (MonkeyCompileMojo) mojoRule.lookupMojo("compile", "src/test/resources/mc/Failable/pom.xml");
        assertThat(compile, is(notNullValue()));
        expectedException.expect(MojoExecutionException.class);
        compile.execute();
    }
}
