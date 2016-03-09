package se.racemates.maven.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public abstract class AbstractMonkeyMojo extends AbstractMojo {

    protected static final String MAIN_BIN_SUFFIX = ".prg";
    protected static final String TEST_BIN_SUFFIX = "-test.prg";
    protected static final String MANIFEST_FILE_NAME = "manifest.xml";

    @Parameter
    protected String sdkPath;

    @Parameter(property = "projectSrcRoot", readonly = true, required = false)
    protected File projectSrcRoot;

    @Parameter(property = "projectTestRoot", readonly = true, required = false)
    protected File projectTestRoot;

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    protected File basedir;

    @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
    protected File projectBuildDirectory;

    @Parameter(defaultValue = "${project.build.finalName}", required = true, readonly = true)
    protected String targetFileName;

    protected String getTestFilePath() {
        return this.projectBuildDirectory + "/" + this.targetFileName + TEST_BIN_SUFFIX;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (this.sdkPath == null) {
            this.sdkPath = System.getenv("GARMIN_HOME");
        }

        if (this.sdkPath == null) {
            throw new MojoExecutionException("You need to set up sdkPath to point to your garmin sdk.");
        }

        getLog().info("sdkPath is: " + this.sdkPath);

        if (this.projectSrcRoot == null) {
            this.projectSrcRoot = new File(basedir, "src/main");
        }

        if (this.projectTestRoot == null) {
            this.projectTestRoot = new File(basedir, "src/test");
        }
    }

    /* Setters for testing */

    public void setProjectSrcRoot(final File projectSrcRoot) {
        this.projectSrcRoot = projectSrcRoot;
    }

    public void setProjectTestRoot(final File projectTestRoot) {
        this.projectTestRoot = projectTestRoot;
    }

    public void setBasedir(final File basedir) {
        this.basedir = basedir;
    }

    public void setProjectBuildDirectory(final File projectBuildDirectory) {
        this.projectBuildDirectory = projectBuildDirectory;
    }

    public void setTargetFileName(final String targetFileName) {
        this.targetFileName = targetFileName;
    }
}
