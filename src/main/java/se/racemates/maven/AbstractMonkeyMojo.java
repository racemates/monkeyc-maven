package se.racemates.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public abstract class AbstractMonkeyMojo extends AbstractMojo {

    @Parameter(property = "projectSrcRoot", readonly = true, required = false)
    protected File projectSrcRoot;

    @Parameter(property = "projectTestRoot", readonly = true, required = false)
    protected File projectTestRoot;
}
