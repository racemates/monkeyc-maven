package se.racemates.maven;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import se.racemates.maven.mojo.MonkeyCompileMojo;
import se.racemates.maven.mojo.MonkeyTestMojo;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MonkeyTestMojoTest {

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    @Test
    public void testExecution_shouldPickUpOutputFromProgram() throws Exception {

        final String targetFileName = "logsome";
        final File baseDirectory = temporaryFolder.newFolder();
        final File targetFolder = new File("/tmp");

        final MonkeyTestMojo test = (MonkeyTestMojo) mojoRule.lookupMojo("test", "src/test/resources/mc/logsome/pom.xml");

        final TestLog log = new TestLog();
        test.setLog(log);

        final File testReportFile = new File(targetFolder, "monkey-reports/monkey-report.txt");

        test.setKeyPath(Utils.getResource("mc/developer_key.der"));
        test.setBasedir(baseDirectory);
        test.setTestReportFile(testReportFile);
        test.setProjectBuildDirectory(targetFolder);
        test.setTargetFileName(targetFileName);
        test.setRunOnce(true);


        test.execute();

        assertThat(log.getInfo().contains("This line should be picked up"), is(true));
    }

    private final class TestLog extends SystemStreamLog {

        private final StringBuilder builder;

        private TestLog() {
            builder = new StringBuilder();
        }

        @Override
        public void info(CharSequence content) {
            super.info(content);
            builder.append(content);
        }

        public String getInfo() {
            return builder.toString();
        }
    }
}
