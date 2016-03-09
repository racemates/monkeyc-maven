package se.racemates.maven;

import org.junit.Test;
import se.racemates.maven.compile.FileScanner;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileScannerTest {

    @Test
    public void testFileScanner_shouldFindFiles() {
        final String file = getClass().getClassLoader().getResource("mc/drawable/main/resources").getFile();
        final FileScanner fileScanner = new FileScanner(new File(file), "xml");
        final List<File> files = fileScanner.scan();
        assertThat(files.size(), is(4));
    }
}
