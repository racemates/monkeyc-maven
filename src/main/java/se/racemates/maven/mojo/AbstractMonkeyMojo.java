package se.racemates.maven.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public abstract class AbstractMonkeyMojo extends AbstractMojo {

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
        return this.projectBuildDirectory + "/" + this.targetFileName + "-test.prg";
    }

    /*
        Setters for testing
     */

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
