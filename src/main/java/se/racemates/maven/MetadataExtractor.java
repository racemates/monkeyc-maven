package se.racemates.maven;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetadataExtractor {

    private final List<String> strings;
    private final Pattern classPattern;
    private final Pattern dependenciesPattern;

    public MetadataExtractor(final List<String> strings) {
        this.strings = strings;
        this.classPattern = Pattern.compile("\\s*(class|module) (\\w+)");
        this.dependenciesPattern = Pattern.compile("\\s*extends (\\w+)(?:,\\s*(\\w+))*");
    }

    public Optional<String> className() {
        for (final String string : this.strings) {
            final Matcher matcher = this.classPattern.matcher(string);
            if (matcher.find()) {
                return Optional.ofNullable(matcher.group(2));
            }
        }
        return Optional.empty();
    }

    public List<String> dependencies() {
        final List<String> result = new ArrayList<>();
        for (final String string : this.strings) {
            final Matcher matcher = this.dependenciesPattern.matcher(string);
            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    final String group = matcher.group(i);
                    if (group != null) {
                        result.add(group);
                    }
                }
                return result;
            }
        }
        return result;
    }
}
