package se.racemates.maven.distribute;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

public class SimpleNamespaceContext implements NamespaceContext {

    private final String prefix;
    private final String uri;

    public  SimpleNamespaceContext(final String prefix, final String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return uri;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return prefix;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        throw new UnsupportedOperationException("Multiple namespaces not supported in " + getClass().getSimpleName());
    }
}
