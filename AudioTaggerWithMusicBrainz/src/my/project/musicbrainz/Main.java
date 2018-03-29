package my.project.musicbrainz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import my.project.musicbrainz.model.Medium;
import my.project.musicbrainz.model.Release;
import my.project.musicbrainz.model.Track;
import my.project.musicbrainz.model.Work;
import my.project.musicbrainz.model.Artist;
import my.project.musicbrainz.model.Recording;
import my.project.musicbrainz.model.RelationArtist;
import my.project.musicbrainz.model.RelationWork;

public class Main {
	static Map<String, Artist> artists = new HashMap<>();
	static Map<String, Recording> recordings = new HashMap<>();
	static Map<String, Work> works = new HashMap<>();
	static XMLProvider xmlProvider;
	static DocumentBuilderFactory dbf;
	static DocumentBuilder db;
	
	public static final Logger logger = LogManager.getLogger(Main.class);
	
	public static void main(String[] args) {
		xmlProvider = new XMLProvider("MusicBrainzCache");
		try {
			//e5db824a-6b2c-4200-9f17-ca4c6adf6ace
			// 9c5c043e-bc69-4edb-81a4-1aaf9c81e6dc - Glenn Gould Remastered
			// 07da4b32-1a0d-4a9f-ae62-b997321fb946
			// b0837172-673c-4416-80d6-8a5801e6f102 - Andras Schiff - Mozart Piano Concertos
			File xmlRelease = xmlProvider.getRelease("b0837172-673c-4416-80d6-8a5801e6f102").toFile();
			dbf = DocumentBuilderFactory.newInstance();
			try {
				db = dbf.newDocumentBuilder();
				try {
					Release release = new Release();
					Document documentRelease = db.parse(xmlRelease);
					Element elementRelease = (Element) documentRelease.getDocumentElement().getFirstChild();
					release.setId(elementRelease.getAttribute("id"));
					release.setTitle(getTextContent(elementRelease, "title"));
					release.setLabel(getTextContent(elementRelease, "label-info-list.label-info.label.name"));
					Element elementMediumList = (Element) elementRelease.getElementsByTagName("medium-list").item(0);
					release.setMediumListCount(elementMediumList.getAttribute("count"));
					List<Medium> mediumList = new ArrayList<>();
					NodeList nodeListMedium = elementRelease.getElementsByTagName("medium");
					for(int i=0; i<nodeListMedium.getLength(); i++) {
						Element elementMedium = (Element) nodeListMedium.item(i);
						Medium medium = new Medium();
						medium.setParent(release);
						medium.setPosition(getTextContent(elementMedium, "position"));
						medium.setTitle(getTextContent(elementMedium, "title"));
						medium.setFormat(getTextContent(elementMedium, "format"));
						List<Track> trackList = new ArrayList<>();
						Element elementTrackList = (Element) elementMedium.getElementsByTagName("track-list").item(0);
						medium.setTrackListCount(elementTrackList.getAttribute("count"));
						NodeList nodeListTrack = elementTrackList.getElementsByTagName("track");
						for(int j=0; j<nodeListTrack.getLength(); j++) {
							Element elementTrack = (Element) nodeListTrack.item(j);
							Track track = new Track();
							track.setParent(medium);
							track.setId(elementTrack.getAttribute("id"));
							track.setTitle(getTextContent(elementTrack, "title"));
							track.setPosition(Integer.parseInt(getTextContent(elementTrack, "position")));
							track.setLength(Integer.parseInt(getTextContent(elementTrack, "length")));
							// recording
							String recordingId = ((Element) elementTrack.getElementsByTagName("recording").item(0)).getAttribute("id");
							Recording recording = null;
							if (recordings.containsKey(recordingId)) {
								recording = recordings.get(recordingId);
							}
							else {
								File xmlRecording = xmlProvider.getRecording(recordingId).toFile();
								Document recordingDoc = db.parse(xmlRecording);
								recording = new Recording();
								recording.setId(recordingId);
								Element elementRecording = (Element) recordingDoc.getDocumentElement().getFirstChild();
								recording.setTitle(getTextContent(elementRecording, "title"));
								NodeList recordingRelationList = elementRecording.getElementsByTagName("relation-list");
								for(int k=0; k<recordingRelationList.getLength(); k++) {
									Element elementRelationList = (Element) recordingRelationList.item(k);
									if (elementRelationList.getAttribute("target-type").equals("artist")) {
										recording.setRelationArtist(createRelationArtistList(elementRelationList));
									}
									else if (elementRelationList.getAttribute("target-type").equals("work")) {
										recording.setRelationWork(createRecordingRelationWork(elementRelationList));
									}
								}
							}
							recordings.put(recordingId, recording);
							track.setRecording(recording);
							trackList.add(track);
							logger.debug("Disc " + Mp3tagsValuesProvider.getDiscNumber(track) + "/" + Mp3tagsValuesProvider.getDiscNumber(track) + "|" +
							"Track " + Mp3tagsValuesProvider.getTrackNumber(track) + "/" + Mp3tagsValuesProvider.getTrackTotal(track) + "|" +
									Mp3tagsValuesProvider.getAlbumName(track) + "|" + Mp3tagsValuesProvider.getTrackTitle(track) + ": " + Mp3tagsValuesProvider.getTrackLength(track) + "|" +
							Mp3tagsValuesProvider.getUrl(track) + "|" + Mp3tagsValuesProvider.getArtist(track) + "|" +
									Mp3tagsValuesProvider.getOrganization(track) + "|" + Mp3tagsValuesProvider.getComposer(track, false) + "|" +
							Mp3tagsValuesProvider.getComment(track));
						}
						medium.setTrackList(trackList);
						mediumList.add(medium);
					}
					release.setMediumList(mediumList);
					System.out.println("");
//					logger.debug(release.toString());
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
    
	private static RelationWork createRecordingRelationWork(Element elementRelationList) {
		NodeList relation = elementRelationList.getElementsByTagName("relation");
		for(int i=0; i<relation.getLength(); i++) {
			Element elementRelation = (Element) relation.item(i);
			if (elementRelation.getAttribute("type").equals("performance")) {
				RelationWork relationWork = new RelationWork();
				String workId = getTextContent(elementRelation, "target");
				if (workId.length() > 0) {
					Work work = getWork(getTextContent(elementRelation, "target"));
					relationWork.setDate(getTextContent(elementRelation, "begin"));
					relationWork.setType("performance");
					relationWork.setWork(work);
				}
				return relationWork;
			}
		}
		return null;
	}

	private static Work getWork(String workId) {
		Work work = null;
		
		if (works.containsKey(workId)) {
			work = works.get(workId);
		}
		else {
			File xmlWork = null;
			try {
				xmlWork = xmlProvider.getWork(workId).toFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Document workDoc = null;
			try {
				workDoc = db.parse(xmlWork);
			} catch (SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			work = new Work();
			work.setId(workId);
			Element elementWork = (Element) workDoc.getDocumentElement().getFirstChild();
			work.setTitle(getTextContent(elementWork, "title"));
			NodeList workRelationList = elementWork.getElementsByTagName("relation-list");
			for(int k=0; k<workRelationList.getLength(); k++) {
				Element elementRelationList = (Element) workRelationList.item(k);
				if (elementRelationList.getAttribute("target-type").equals("artist")) {
					work.setRelationArtist(createRelationArtistList(elementRelationList));
				}
			}
			works.put(workId, work);
		}
		return work;
	}

	private static List<RelationArtist> createRelationArtistList(Element elementRelationList) {
		List<RelationArtist> relationArtist = new ArrayList<>();
		NodeList relation = elementRelationList.getElementsByTagName("relation");
		for(int i=0; i<relation.getLength(); i++) {
			Element elementRelation = (Element) relation.item(i);
			RelationArtist relationArtistMember = new RelationArtist();
			relationArtistMember.setType(elementRelation.getAttribute("type"));
			relationArtistMember.setAttribute(getTextContent(elementRelation, "attribute-list.attribute"));
			// begin, end, target
			String artistId = getTextContent(elementRelation, "target");
			relationArtistMember.setBeginDate(getTextContent(elementRelation, "begin"));
			relationArtistMember.setEndDate(getTextContent(elementRelation, "end"));
			relationArtistMember.setArtist(getArtist(artistId));
			relationArtist.add(relationArtistMember);
		}
		return relationArtist;
	}
	
	private static Artist getArtist(String artistId) {
		Artist artist = null;
		
		if (artists.containsKey(artistId)) {
			artist = artists.get(artistId);
		}
		else {
			File xmlArtist = null;
			try {
				xmlArtist = xmlProvider.getArtist(artistId).toFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Document artistDoc = null;
			try {
				artistDoc = db.parse(xmlArtist);
			} catch (SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			artist = new Artist();
			artist.setId(artistId);
			
			Element elementArtist = (Element) artistDoc.getDocumentElement().getFirstChild();
			artist.setType(elementArtist.getAttribute("type"));
			artist.setName(getTextContent(elementArtist, "name"));
			artist.setSortName(getTextContent(elementArtist, "sort-name"));
			artist.setCountry(getTextContent(elementArtist, "country"));
			artist.setArea(getTextContent(elementArtist, "area.name"));
			artist.setLifeSpanBegin(getTextContent(elementArtist, "life-span.begin"));
			artist.setLifeSpanEnd(getTextContent(elementArtist, "life-span.end"));
			artists.put(artistId, artist);
		}
		
		return artist;
	}
	
	private static String getTextContent(Element element, String name) {
		String elementName = "";
		String remainPath = "";
		int dotPosition = name.indexOf(".");
		
		if (dotPosition > 0) {
			elementName = name.substring(0, dotPosition);
			remainPath = name.substring(dotPosition + 1, name.length());
		}
		else {
			elementName = name;
		}
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element childElement = (Element) children.item(i);
				if (childElement.getTagName().equals(elementName)) {
					if (remainPath.isEmpty()) {
						return childElement.getTextContent();
					}
					else {
						return getTextContent(childElement, remainPath);
					}
				}
			}
		}
		return "";
	}
}

