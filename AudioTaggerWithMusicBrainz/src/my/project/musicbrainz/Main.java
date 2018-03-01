package my.project.musicbrainz;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {

	public static void main(String[] args) {
		XMLProvider xmlProvider = new XMLProvider(".");
		try {
			File xml = xmlProvider.getRelease("e5db824a-6b2c-4200-9f17-ca4c6adf6ace").toFile();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				try {
					Document dom = db.parse(xml);
					NodeList nl = dom.getElementsByTagName("release");
					Node node = nl.item(0);
					Element el = (Element) node;
					System.out.println(el.getAttribute("id"));
					NamedNodeMap attr = node.getAttributes();
					System.out.println(attr.getNamedItem("id").getNodeValue());
					System.out.println(attr.toString());
					System.out.println(node.getNodeName() + ", " +node.getNodeType() + ", " + node.getNodeValue());
				} catch (SAXException e) {
					e.printStackTrace();
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
//    private static void downloadUsingNIO(String urlStr, String file) throws IOException {
//        URL url = new URL(urlStr);
//        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
//        FileOutputStream fos = new FileOutputStream(file);
//        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
//        fos.close();
//        rbc.close();
//    }
}

