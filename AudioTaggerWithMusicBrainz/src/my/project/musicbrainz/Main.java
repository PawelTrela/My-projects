package my.project.musicbrainz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import my.project.musicbrainz.model.Medium;
import my.project.musicbrainz.model.Release;
import my.project.musicbrainz.model.Track;

public class Main {

	public static void main(String[] args) {
		XMLProvider xmlProvider = new XMLProvider(".");
		try {
			File xml = xmlProvider.getRelease("e5db824a-6b2c-4200-9f17-ca4c6adf6ace").toFile();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				try {
					Release release = new Release();
					Document dom = db.parse(xml);
					Element elementRelease = (Element) dom.getDocumentElement().getFirstChild();
					release.setId(elementRelease.getAttribute("id"));
					Element elementTitle = (Element) elementRelease.getElementsByTagName("title").item(0);
					release.setTitle(elementTitle.getTextContent());
					System.out.println("Title: " + release.getTitle());
					Element elementMediumList = (Element) elementRelease.getElementsByTagName("medium-list").item(0);
					release.setMediumListCount(elementMediumList.getAttribute("count"));
					System.out.println(release.getMediumListCount());
					List<Medium> mediumList = new ArrayList<>();
					NodeList nodeListMedium = elementRelease.getElementsByTagName("medium");
					for(int i=0; i<nodeListMedium.getLength(); i++) {
						Element elementMedium = (Element) nodeListMedium.item(i);
						Medium medium = new Medium();
						medium.setPosition(elementMedium.getElementsByTagName("position").item(0).getTextContent());
						medium.setFormat(elementMedium.getElementsByTagName("format").item(0).getTextContent());
						List<Track> trackList = new ArrayList<>();
						Element elementTrackList = (Element) elementMedium.getElementsByTagName("track-list").item(0);
						medium.setTrackListCount(elementTrackList.getAttribute("count"));
						NodeList nodeListTrack = elementTrackList.getElementsByTagName("track");
						for(int j=0; j<nodeListTrack.getLength(); j++) {
							Element elementTrack = (Element) nodeListTrack.item(j);
							Track track = new Track();
							track.setId(elementTrack.getAttribute("id"));
							track.setTitle(elementTrack.getElementsByTagName("title").item(0).getTextContent());
							track.setPosition(Integer.getInteger(elementTrack.getElementsByTagName("position").item(0).getTextContent(), j+1));
							//%TODO length
							track.setLength(0);
							trackList.add(track);
						}
						medium.setTrackList(trackList);
						mediumList.add(medium);
					}
					release.setMediumList(mediumList);
					System.out.println(release.toString());
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

