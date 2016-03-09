package se.racemates.maven.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import se.racemates.maven.compile.MonkeyCompiler;

import java.io.File;
import java.util.Arrays;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE)
public class MonkeyCompileMojo extends AbstractMonkeyMojo {

    @Parameter(property = "mainManifestPath", readonly = true, required = false)
    protected File mainManifestPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();

        final MonkeyCompiler compiler = new MonkeyCompiler(this.sdkPath, this.basedir, getLog());

        if (!this.projectSrcRoot.exists()) {
            getLog().info("No sources found to compile");
        } else {
            final File mainManifest = getManifest();
            final File mainTarget = new File(this.projectBuildDirectory, this.targetFileName + MAIN_BIN_SUFFIX);
            compiler.compile(Arrays.asList(this.projectSrcRoot), mainManifest, mainTarget);
        }
    }

    private File getManifest() {
        if (this.mainManifestPath != null) {
            return this.mainManifestPath;
        } else {
            return new File(this.projectSrcRoot, MANIFEST_FILE_NAME);
        }
    }
}