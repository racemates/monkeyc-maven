package se.racemates.maven.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import se.racemates.maven.compile.MonkeyCompiler;

import java.io.File;
import java.util.Arrays;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE)
public class MonkeyCompileMojo extends AbstractMonkeyMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();

        final MonkeyCompiler compiler = new MonkeyCompiler(this.sdkPath, this.basedir, getLog());

        if (!this.projectSrcRoot.exists()) {
            getLog().info("No sources found to compile");
        } else {
            final File mainManifest = new File(this.projectSrcRoot, MANIFEST_FILE_NAME);
            final File mainTarget = new File(this.projectBuildDirectory, this.targetFileName + MAIN_BIN_SUFFIX);
            compiler.compile(Arrays.asList(this.projectSrcRoot), mainManifest, mainTarget);
        }
    }
}