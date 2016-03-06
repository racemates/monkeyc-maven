package se.racemates.maven;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MetadataExtractorTest {

    @Test
    public void testClassName() {
        final List<String> strings = new ArrayList<>();
        strings.add("");
        strings.add("class Test {");
        strings.add("hidden var test");
        strings.add("}");

        final MetadataExtractor metadataExtractor = new MetadataExtractor(strings);
        final Optional<String> className = metadataExtractor.className();
        assertThat(className.isPresent(), is(true));
        assertThat(className.get(), is("Test"));
    }

    @Test
    public void testModuleName() {
        final List<String> strings = new ArrayList<>();
        strings.add("");
        strings.add("module Test {");
        strings.add("hidden var test");
        strings.add("}");

        final MetadataExtractor metadataExtractor = new MetadataExtractor(strings);
        final Optional<String> className = metadataExtractor.className();
        assertThat(className.isPresent(), is(true));
        assertThat(className.get(), is("Test"));
    }

    @Test
    public void testOneDependency() {
        final List<String> strings = new ArrayList<>();
        strings.add("");
        strings.add("class Test extends Base1, Base2 {");
        strings.add("hidden var test");
        strings.add("}");

        final MetadataExtractor metadataExtractor = new MetadataExtractor(strings);
        final List<String> dependencies = metadataExtractor.dependencies();
        assertThat(dependencies.size(), is(2));
        assertThat(dependencies.get(0), is("Base1"));
        assertThat(dependencies.get(1), is("Base2"));
    }

    @Test
    public void testTwoDependencies() {
        final List<String> strings = new ArrayList<>();
        strings.add("");
        strings.add("class Test extends Base1 {");
        strings.add("hidden var test");
        strings.add("}");

        final MetadataExtractor metadataExtractor = new MetadataExtractor(strings);
        final List<String> dependencies = metadataExtractor.dependencies();
        assertThat(dependencies.size(), is(1));
        assertThat(dependencies.get(0), is("Base1"));
    }


}
