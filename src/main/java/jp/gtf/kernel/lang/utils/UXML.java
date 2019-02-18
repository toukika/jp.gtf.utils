/*
 * Code sample by FXD
 * more infromation please visit https://gtf.jp
 */
package jp.gtf.kernel.lang.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * UXML
 *
 * @author F
 */
public class UXML {

    public static List<Element> xpath(Node doc, String xpathStr) {
        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr_entitys = xpath.compile(xpathStr);
            NodeList nl = (NodeList) expr_entitys.evaluate(doc, XPathConstants.NODESET);
            return getChildElements(nl);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(UXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static List<Element> path(Node doc, String paths) {
        List<Element> els = new ArrayList<>();
        return path(els, doc, paths);
    }

    public static List<Element> path(List<Element> elements, Node doc, String paths) {
        if (paths.contains("/")) {
            String npath = paths.substring(0, paths.indexOf('/'));
            for (Element e : getChildElements(doc.getChildNodes(), npath)) {
                return path(elements, e, paths.substring(paths.indexOf('/') + 1));
            }
        } else {
            return getChildElements(doc.getChildNodes(), paths);
        }
        return elements;
    }

    public static Element firstElement(Node doc, String xpathStr) {
        try {
            List<Element> elments = xpath(doc, xpathStr);
            if (null == elments || elments.isEmpty()) {
                return null;
            }
            return elments.get(0);
        } catch (Exception e) {
            Logger.getLogger(UXML.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public static List<Element> getChildElements(NodeList parent) {
        List<Element> elements = new ArrayList<>();
        for (int i = 0; i < parent.getLength(); ++i) {
            Node current = parent.item(i);
            if (current.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) current);
            }
        }
        return elements;
    }

    public static List<Element> getChildElements(NodeList parent, String tagName) {
        List<Element> elements = new ArrayList<>();
        for (int i = 0; i < parent.getLength(); ++i) {
            Node current = parent.item(i);
            if (current.getNodeType() == Node.ELEMENT_NODE && tagName.equals(current.getNodeName())) {
                elements.add((Element) current);
            }
        }
        return elements;
    }

    public static Element append(Document doc, Element parentElement, String elementName) {
        Element currentElement = doc.createElement(elementName);
        parentElement.appendChild(currentElement);
        return currentElement;
    }

    public static Element append(Document doc, String elementName) {
        Element currentElement = doc.createElement(elementName);
        doc.appendChild(currentElement);
        return currentElement;
    }

    public static Document newXml() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            return docBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            Logger.getLogger(UXML.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public static Document loadXmlFromString(String input) {
        try {
            return loadXml(UString.toStream(input));
        } catch (Exception e) {
            Logger.getLogger(UXML.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public static Document loadXml(InputStream input) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(input);
            input.close();
            return doc;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            Logger.getLogger(UXML.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public static void save(Document doc, String xmlFilePath) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);
        } catch (IllegalArgumentException | TransformerException e) {
            Logger.getLogger(UXML.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static String pretty(String xmlData) {
        return pretty(loadXmlFromString(xmlData));
    }

    public static String pretty(Document doc) {
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            //initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            return String.valueOf(e);
        }
    }

    public static void save(Document doc, OutputStream outputStream) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);
            outputStream.close();
        } catch (IOException | IllegalArgumentException | TransformerException e) {
            Logger.getLogger(UXML.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static String getString(Document doc) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            DOMSource source = new DOMSource(doc);
            String resStr;
            try (StringWriter writer = new StringWriter()) {
                StreamResult result = new StreamResult(writer);
                transformer.transform(source, result);
                resStr = writer.toString();
            }
            return resStr;
        } catch (IOException | IllegalArgumentException | TransformerException e) {
            Logger.getLogger(UXML.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }
}
