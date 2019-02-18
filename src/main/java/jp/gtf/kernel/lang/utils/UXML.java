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

    /**
     * 指定された要素、XPATHで下級要素を取得する
     *
     * @param doc 上位要素
     * @param xPath xPath
     * @return 下級要素リスト
     */
    public static List<Element> xpath(Node doc, String xPath) {
        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr_entitys = xpath.compile(xPath);
            NodeList nl = (NodeList) expr_entitys.evaluate(doc, XPathConstants.NODESET);
            return getChildElements(nl);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(UXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * 指定された要素、XPATHで下級第一番目の要素を取得する<br>
     * 取得できない場合、nullを返却する。
     *
     * @param doc 上位要素
     * @param xPath xPath
     * @return 下位要素
     */
    public static Element firstElement(Node doc, String xPath) {
        try {
            List<Element> elments = xpath(doc, xPath);
            if (null == elments || elments.isEmpty()) {
                return null;
            }
            return elments.get(0);
        } catch (Exception e) {
            Logger.getLogger(UXML.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    /**
     * 指定された要素リスト、下級要素を取得する<br>
     * TEXT_NODE等をFilterする
     *
     * @param parent 上位要素リスト
     * @return 下位要素
     */
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

    /**
     * 指定された要素リスト、下級要素を取得する<br>
     * TEXT_NODE＆Tag名でをFilterする
     *
     * @param parent 上位要素リスト
     * @param tagName タグ名
     * @return 下位要素
     */
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

    /**
     * 指定されたDOCに、上級要素に、elementを作成して追加する。<br>
     * 新規追加した要素をそのまま返却する。
     *
     * @param doc ドキュメント
     * @param parentElement 上位要素
     * @param elementName 要素名
     * @return 新規追加した要素
     */
    public static Element append(Document doc, Element parentElement, String elementName) {
        Element currentElement = doc.createElement(elementName);
        parentElement.appendChild(currentElement);
        return currentElement;
    }

    /**
     * 指定されたDOCに、ROOTに、elementを作成して追加する。<br>
     *
     * @param doc ドキュメント
     * @param elementName 要素名
     * @return 新規追加した要素
     */
    public static Element append(Document doc, String elementName) {
        Element currentElement = doc.createElement(elementName);
        doc.appendChild(currentElement);
        return currentElement;
    }

    /**
     * 新規XMLドキュメントを作成する
     *
     * @return XMLドキュメント
     */
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

    /**
     * XML文字からドキュメントを作成する
     *
     * @param xmlString xml文字列
     * @return ドキュメント
     */
    public static Document loadXmlFromString(String xmlString) {
        try {
            return loadXml(UString.toStream(xmlString));
        } catch (Exception e) {
            Logger.getLogger(UXML.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    /**
     * Streamから、XMLを作成する
     *
     * @param input 入力Stream
     * @return ドキュメント
     */
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

    /**
     * XMLをフォーマットする
     *
     * @param doc XMLドキュメント
     * @return フォーマット済みxml文字列
     */
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

    /**
     * XMLを保存する
     *
     * @param doc ドキュメント
     * @param saveToPath ファイルパス
     */
    public static void save(Document doc, String saveToPath) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(saveToPath));
            transformer.transform(source, result);
        } catch (IllegalArgumentException | TransformerException e) {
            Logger.getLogger(UXML.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * XMLを保存する
     *
     * @param doc ドキュメント
     * @param outputStream 保存先
     */
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

    /**
     * XMLを文字列変換する
     *
     * @param doc ドキュメント
     * @return XML文字列
     */
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
