package se.racemates.maven;

import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DependencyHelperTest {

    @Test
    public void testSortDependencies() {
        final String file = getClass().getClassLoader().getResource("mc/dependencies").getFile();
        final FileScanner scanner = new FileScanner(new File(file), "mc");
        final List<File> files = scanner.scan();
        final DependencyHelper helper = new DependencyHelper(files);
        final List<FileInfo> fileInfos = helper.sortDependencies();

        assertThat(files.size(), is(4));
        assertThat(fileInfos.size(), is(4));
    }
}
