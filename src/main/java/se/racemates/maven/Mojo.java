package se.racemates.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase process-sources
 */
public class Mojo
        extends AbstractMojo {


    /**
     * @parameter expression="${project.build.directory}/surefire-reports/TEST-connectiq.xml"
     */
    private File outputFile;


    /**
     * @parameter expression="${project.build.directory}/${project.build.finalName}"
     */
    private String programFile;

    /*
     * @parameter
     * @required
     */
    private String sdkPath;

    /*
     * @parameter
     * @required
     */
    public void execute()
            throws MojoExecutionException {

        if (!outputFile.exists()) {
            outputFile.mkdirs();
        }

        BufferedWriter fileWriter = null;

        try {
            fileWriter = new BufferedWriter(new FileWriter(outputFile));

            final InputStream inputStream = SimulatorRunner.run(sdkPath, programFile);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            for (String line = br.readLine();
                 line != null;
                 line = br.readLine()) {

                System.out.println(line);
                if (line.startsWith("-->")) {
                    fileWriter.write(line);
                    fileWriter.newLine();
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + outputFile, e);
        } catch (InterruptedException e) {
            throw new MojoExecutionException("Error", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
