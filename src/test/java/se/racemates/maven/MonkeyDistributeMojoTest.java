package se.racemates.maven;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.racemates.maven.mojo.MonkeyDistributeMojo;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MonkeyDistributeMojoTest {

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testExecution_shouldBuildTowArtifacts() throws Exception {
        final String targetFileName = "only-sources";
        final File baseDirectory = temporaryFolder.newFolder();
        final File targetFolder = temporaryFolder.newFolder();

        final MonkeyDistributeMojo distribute = (MonkeyDistributeMojo) mojoRule.lookupMojo("distribute", "src/test/resources/mc/only-sources/pom.xml");
        distribute.setBasedir(baseDirectory);
        distribute.setProjectBuildDirectory(targetFolder);
        distribute.setTargetFileName(targetFileName);

        assertThat(distribute, is(notNullValue()));
        distribute.execute();

        final File[] files = targetFolder.listFiles((dir, name) -> name.endsWith(".prg"));
        assertThat(files.length, is(2));
    }
}
