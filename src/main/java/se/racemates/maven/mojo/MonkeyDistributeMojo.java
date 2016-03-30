package se.racemates.maven.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import se.racemates.maven.compile.MonkeyCompiler;
import se.racemates.maven.distribute.Device;
import se.racemates.maven.distribute.ManifestParser;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Mojo(name = "distribute")
public class MonkeyDistributeMojo extends AbstractMonkeyMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();

        final File manifest = getManifest();
        final ManifestParser manifestParser = new ManifestParser(manifest);

        final MonkeyCompiler compiler = new MonkeyCompiler(this.sdkPath, this.projectBuildDirectory, getLog());

        if (!this.projectSrcRoot.exists()) {
            getLog().info("No sources found to compile");
        } else {
            final List<File> sources = Collections.singletonList(this.projectSrcRoot);
            manifestParser.getDevices().forEach(device -> {
                final File target = getTarget(device);
                try {
                    getLog().info("Building executable for device: " + device);
                    compiler.compile(sources, manifest, target, device);
                } catch (MojoExecutionException e) {
                    throw new RuntimeException("Unable to build artifact for device: " + device, e);
                }
            });
        }
    }

    private File getTarget(final Device device) {
        return new File(this.projectBuildDirectory, this.targetFileName + "-" + device.getName() + MAIN_BIN_SUFFIX);
    }
}
