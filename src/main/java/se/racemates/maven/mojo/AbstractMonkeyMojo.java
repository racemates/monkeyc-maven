package se.racemates.maven.mojo;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Collection;

public abstract class AbstractMonkeyMojo extends AbstractMojo {

    static final String MAIN_BIN_SUFFIX = ".prg";
    static final String TEST_BIN_SUFFIX = "-test.prg";
    static final String MANIFEST_FILE_NAME = "manifest.xml";

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

    @Parameter(property = "mainManifestPath", readonly = true, required = false)
    protected File mainManifestPath;

    @Parameter(property = "keyPath", readonly = true, required = false)
    protected File keyPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (this.sdkPath == null) {
            this.sdkPath = System.getenv("GARMIN_HOME");
        }

        if (this.sdkPath == null) {
            throw new MojoExecutionException("You need to set up environment variable GARMIN_HOME to point to your garmin sdk.");
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

    public void setKeyPath(File keyPath) {
        this.keyPath = keyPath;
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

    protected String getTestFilePath() {
        return this.projectBuildDirectory + "/" + this.targetFileName + TEST_BIN_SUFFIX;
    }

    protected File getManifest() {
        if (this.mainManifestPath != null) {
            return this.mainManifestPath;
        } else {
            return new File(this.projectSrcRoot, MANIFEST_FILE_NAME);
        }
    }

    protected File getKey() throws MojoExecutionException {
        if (this.keyPath != null) {
            return this.keyPath;
        } else if (this.basedir != null) {
            final Collection<File> files = FileUtils.listFiles(basedir, new String[]{"der"}, false);
            if (!files.isEmpty()) {
                return files.iterator().next();
            }
        }
        throw new MojoExecutionException("You have to set keyPath");
    }
}
