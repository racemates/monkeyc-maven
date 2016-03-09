package se.racemates.maven.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import se.racemates.maven.compile.MonkeyCompiler;

import java.io.File;
import java.util.Arrays;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE)
public class MonkeyCompileMojo extends AbstractMonkeyMojo {

    @Parameter
    private String sdkPath;

    public void execute() throws MojoExecutionException {

        if (this.sdkPath == null) {
            this.sdkPath = System.getenv("GARMIN_HOME");
        }

        if (this.projectSrcRoot == null) {
            this.projectSrcRoot = new File(basedir, "src/main");
        }

        if (this.projectTestRoot == null) {
            this.projectTestRoot = new File(basedir, "src/test");
        }

        final MonkeyCompiler compiler = new MonkeyCompiler(sdkPath, basedir, getLog());

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