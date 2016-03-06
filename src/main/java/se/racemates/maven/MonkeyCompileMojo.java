package se.racemates.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE)
public class MonkeyCompileMojo extends AbstractMojo {

    @Parameter(property = "projectSrcRoot", readonly = true)
    private File projectSrcRoot;

    @Parameter(property = "projectTestRoot", readonly = true)
    private File projectTestRoot;

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
    private File projectBuildDirectory;

    @Parameter(defaultValue = "${project.build.finalName}", required = true, readonly = true)
    private String targetFileName;

    @Parameter
    private String sdkPath;

    public void execute() throws MojoExecutionException {

        if (this.sdkPath == null) {
            this.sdkPath = System.getenv("GARMIN_HOME");
        }

        if (this.projectSrcRoot == null) {
            this.projectSrcRoot = new File(basedir, "main/mc");
        }

        if (this.projectTestRoot == null) {
            this.projectTestRoot = new File(basedir, "test/mc");
        }

        final Compiler compiler = new Compiler(sdkPath, basedir, getLog());

        if (!projectSrcRoot.exists()) {
            getLog().info("No sources found to compile");
        } else {
            final File mainManifest = new File(this.projectSrcRoot, "manifest.xml");
            final File mainTarget = new File(this.projectBuildDirectory, this.targetFileName + ".prg");
            compiler.compile(Arrays.asList(projectSrcRoot), mainManifest, mainTarget);
        }

        if (!projectTestRoot.exists()) {
            getLog().info("No test sources found to compile");
        } else {
            final File testManifest = new File(this.projectTestRoot, "manifest.xml");
            final File testTarget = new File(this.projectBuildDirectory, this.targetFileName + "-test.prg");
            compiler.compile(Arrays.asList(projectSrcRoot, projectTestRoot), testManifest, testTarget);
        }
    }
}