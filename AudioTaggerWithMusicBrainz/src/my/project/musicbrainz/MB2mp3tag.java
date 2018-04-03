package my.project.musicbrainz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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

public class MB2mp3tag {
	static Map<String, Artist> artists = new HashMap<>();
	static Map<String, Recording> recordings = new HashMap<>();
	static Map<String, Work> works = new HashMap<>();
	static XMLProvider xmlProvider;
	static DocumentBuilderFactory dbf;
	static DocumentBuilder db;
	static List<String> output4mp3tag;
	
	public static final Logger logger = LogManager.getLogger(MB2mp3tag.class);

	public static void main(String[] args) {
		ParametersParserAndValidator parametersParser = new ParametersParserAndValidator(MB2mp3tag.class.getSimpleName(), args);
		if (!parametersParser.areParametersValid()) {
			return;
		}
//		if (parametersParser.areParametersValid()) {
//			System.out.println(parametersParser.getMp3Tags());
//			System.out.println(parametersParser.getReleaseId());
//			System.out.println(parametersParser.getXmlCacheFolder());
//			return;
//		}
		
		xmlProvider = new XMLProvider("MusicBrainzCache");
		output4mp3tag = new ArrayList<>();
		
		try {
			File xmlRelease = xmlProvider.getRelease(parametersParser.getReleaseId()).toFile();
			dbf = DocumentBuilderFactory.newInstance();
			try {
				db = dbf.newDocumentBuilder();
				try {
					Release release = new Release();
					Document documentRelease = db.parse(xmlRelease);
					Element elementRelease = (Element) documentRelease.getDocumentElement().getFirstChild();
					long startTime = System.currentTimeMillis();
					Integer noOfTracksInRelease = getNumberOfTracksInRelease(elementRelease);
					Integer noOfProcessedTrack = 0;
					release.setId(elementRelease.getAttribute("id"));
					release.setTitle(getTextContent(elementRelease, "title"));
					release.setLabel(getTextContent(elementRelease, "label-info-list.label-info.label.name"));
					Element elementMediumList = (Element) elementRelease.getElementsByTagName("medium-list").item(0);
					release.setMediumListCount(elementMediumList.getAttribute("count"));
					List<Medium> mediumList = new ArrayList<>();
					NodeList nodeListMedium = elementRelease.getElementsByTagName("medium");
					int noOfMediums = nodeListMedium.getLength();
					for (int currentMedium = 0; currentMedium < noOfMediums; currentMedium++) {
						Element elementMedium = (Element) nodeListMedium.item(currentMedium);
						Medium medium = new Medium();
						medium.setParent(release);
						medium.setPosition(getTextContent(elementMedium, "position"));
						medium.setTitle(getTextContent(elementMedium, "title"));
						medium.setFormat(getTextContent(elementMedium, "format"));
						List<Track> trackList = new ArrayList<>();
						Element elementTrackList = (Element) elementMedium.getElementsByTagName("track-list").item(0);
						medium.setTrackListCount(elementTrackList.getAttribute("count"));
						NodeList nodeListTrack = elementTrackList.getElementsByTagName("track");
						int noOfTracksInMedium = nodeListTrack.getLength();
						for (int currentTrack = 0; currentTrack < noOfTracksInMedium; currentTrack++) {
							Element elementTrack = (Element) nodeListTrack.item(currentTrack);
							Track track = new Track();
							track.setParent(medium);
							track.setId(elementTrack.getAttribute("id"));
							track.setTitle(getTextContent(elementTrack, "title"));
							track.setPosition(Integer.parseInt(getTextContent(elementTrack, "position")));
							track.setLength(Integer.parseInt(getTextContent(elementTrack, "length")));
							// recording
							String recordingId = ((Element) elementTrack.getElementsByTagName("recording").item(0))
									.getAttribute("id");
							Recording recording = null;
							if (recordings.containsKey(recordingId)) {
								recording = recordings.get(recordingId);
							} else {
								File xmlRecording = xmlProvider.getRecording(recordingId).toFile();
								Document recordingDoc = db.parse(xmlRecording);
								recording = new Recording();
								recording.setId(recordingId);
								Element elementRecording = (Element) recordingDoc.getDocumentElement().getFirstChild();
								recording.setTitle(getTextContent(elementRecording, "title"));
								NodeList recordingRelationList = elementRecording.getElementsByTagName("relation-list");
								for (int k = 0; k < recordingRelationList.getLength(); k++) {
									Element elementRelationList = (Element) recordingRelationList.item(k);
									if (elementRelationList.getAttribute("target-type").equals("artist")) {
										recording.setRelationArtist(createRelationArtistList(elementRelationList));
									} else if (elementRelationList.getAttribute("target-type").equals("work")) {
										recording.setRelationWork(createRecordingRelationWork(elementRelationList));
									}
								}
							}
							recordings.put(recordingId, recording);
							track.setRecording(recording);
							trackList.add(track);
							addToMp3tagOutputList(track, parametersParser.getMp3Tags());
							noOfProcessedTrack += 1;
							printProgress(startTime, noOfTracksInRelease, noOfProcessedTrack, prepareProgressBarInfo(
									currentMedium + 1, noOfMediums, currentTrack + 1, noOfTracksInMedium));
						}
						medium.setTrackList(trackList);
						mediumList.add(medium);
					}
					release.setMediumList(mediumList);
					System.out.println("");
					// logger.debug(release.toString());
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
		for (int i = 0; i < relation.getLength(); i++) {
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
		} else {
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
			for (int k = 0; k < workRelationList.getLength(); k++) {
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
		for (int i = 0; i < relation.getLength(); i++) {
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
		} else {
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
		} else {
			elementName = name;
		}
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element childElement = (Element) children.item(i);
				if (childElement.getTagName().equals(elementName)) {
					if (remainPath.isEmpty()) {
						return childElement.getTextContent();
					} else {
						return getTextContent(childElement, remainPath);
					}
				}
			}
		}
		return "";
	}

	private static Integer getNumberOfTracksInRelease(Element element) {
		Integer noOfTracks = 0;
		NodeList trackList = element.getElementsByTagName("track-list");
		for (int i = 0; i < trackList.getLength(); i++) {
			Element trackListElement = (Element) trackList.item(i);
			noOfTracks += Integer.parseInt(trackListElement.getAttribute("count"));
		}
		return noOfTracks;
	}

	private static void printProgress(long startTime, long total, long current, String info) {
		long eta = current == 0 ? 0 : (total - current) * (System.currentTimeMillis() - startTime) / current;

		String etaHms = current == 0 ? "N/A"
				: String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
						TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
						TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

		StringBuilder string = new StringBuilder(115);
		int percent = (int) (current * 100 / total);
		string.append('\r')
				.append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
				.append(String.format(" %d%% [", percent))
				.append(String.join("", Collections.nCopies(percent * 2 / 3, "="))).append('>')
				.append(String.join("", Collections.nCopies(66 - (percent * 2 / 3), " "))).append(']').append(info)
				.append(String.format(", ETA: %s", etaHms));

		System.out.print(string);
	}

	private static String prepareProgressBarInfo(int currentAlbum, int allAlbums, int currentTrack, int allTracks) {
		String albums = "" + currentAlbum + "/" + allAlbums;
		String tracks = "" + currentTrack + "/" + allTracks;
		StringBuilder output = new StringBuilder(18);
		output.append(" ALB ")
				.append(String.join("",
						Collections.nCopies(String.valueOf(allAlbums).length() * 2 + 1 - albums.length(), " ")))
				.append(albums).append(", TRK ").append(String.join("", Collections.nCopies(5 - tracks.length(), " ")))
				.append(tracks);

		return output.toString();
	}
	
	private static void addToMp3tagOutputList(Track track, String tags) {
		List<String> tagList = Arrays.asList(tags.split(Pattern.quote("|")));
		for (int i = 0; i < tagList.size(); i++) {
			String tagValue;
			switch (tagList.get(i)) {
			case "discnumber":
				tagValue = Mp3tagsValuesProvider.getDiscNumber(track);
				break;
			case "disctotal":
				tagValue = Mp3tagsValuesProvider.getDiscTotal(track);
				break;
			case "album":
				tagValue = Mp3tagsValuesProvider.getAlbumName(track);
				break;
			case "tracknumber":
				tagValue = Mp3tagsValuesProvider.getTrackNumber(track);
				break;
			case "tracktotal":
				tagValue = Mp3tagsValuesProvider.getTrackTotal(track);
				break;
			case "title":
				tagValue = Mp3tagsValuesProvider.getTrackTitle(track);
				break;
			case "composer":
				tagValue = Mp3tagsValuesProvider.getComposer(track, false);
				break;
			case "artist":
				tagValue = Mp3tagsValuesProvider.getArtist(track);
				break;
			case "year":
				tagValue = Mp3tagsValuesProvider.getComposingDate(track);
				break;
			case "organization":
				tagValue = Mp3tagsValuesProvider.getOrganization(track);
				break;
			case "comment":
				tagValue = Mp3tagsValuesProvider.getComment(track);
				break;
			case "url":
				tagValue = Mp3tagsValuesProvider.getUrl(track);
				break;
			case "length":
				tagValue = Mp3tagsValuesProvider.getTrackLength(track);
				break;
			default:
				tagValue = "";
				break;		
			}
			tagList.set(i, tagValue);
		}
		output4mp3tag.add(String.join("|", tagList));
	}
}
