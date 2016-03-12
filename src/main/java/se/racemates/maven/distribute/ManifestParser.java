package se.racemates.maven.distribute;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManifestParser {

    private static final String NAMESPACE_URI = "http://www.garmin.com/xml/connectiq";
    private static final String NAMESPACE_PREFIX = "iq";
    private final Document document;
    private final XPath xPath;

    public ManifestParser(final File manifest) {
        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            this.document = documentBuilder.parse(manifest);
            final XPathFactory factory = XPathFactory.newInstance();
            this.xPath = factory.newXPath();
            this.xPath.setNamespaceContext(new SimpleNamespaceContext(NAMESPACE_PREFIX, NAMESPACE_URI));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException("Unable to parse file: " + manifest.getAbsolutePath(), e);
        }
    }

    public List<Device> getDevices() {
        final ArrayList<Device> devices = new ArrayList<>();
        try {
            final XPathExpression expression = this.xPath.compile("/iq:manifest/iq:application/iq:products/iq:product/@id");
            final NodeList products = (NodeList) expression.evaluate(this.document, XPathConstants.NODESET);
            for (int i = 0; i < products.getLength(); i++) {
                final Node item = products.item(i);
                devices.add(new Device(item.getNodeValue()));
            }
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException("Unable to parse document", e);
        }
        return devices;
    }

}
