package my.project.musicbrainz;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
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
	static Map<String, Work> works = new HashMap<>();
	static XMLProvider xmlProvider;
	static DocumentBuilderFactory dbf;
	static DocumentBuilder db;
	static List<String> output4mp3tag;

	// private static final Logger logger = LogManager.getLogger();

	public static void main(String[] args) {
		StatusLogger.getLogger().setLevel(Level.OFF);
		Logger logger = LogManager.getLogger();
		ParametersParserAndValidator parametersParser = new ParametersParserAndValidator(MB2mp3tag.class.getSimpleName(), args);
		if (!parametersParser.areParametersValid()) {
			logger.error("Parameters are not valid.");
			return;
		}
		xmlProvider = new XMLProvider(parametersParser);

		output4mp3tag = new ArrayList<>();

		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			InputStream xmlRelease = xmlProvider.getRelease(parametersParser.getReleaseId());
			Document documentRelease = db.parse(xmlRelease);
			xmlRelease.close();
			Element elementRelease = (Element) documentRelease.getDocumentElement().getFirstChild();
			long startTime = System.currentTimeMillis();
			Integer noOfTracksInRelease = getNumberOfTracksInRelease(elementRelease);
			Integer noOfProcessedTrack = 0;

			Release release = new Release();
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
				Element elementTrackList = (Element) elementMedium.getElementsByTagName("track-list").item(0);
				medium.setTrackListCount(elementTrackList.getAttribute("count"));

				List<Track> trackList = new ArrayList<>();
				NodeList nodeListTrack = elementTrackList.getElementsByTagName("track");
				int noOfTracksInMedium = nodeListTrack.getLength();
				for (int currentTrack = 0; currentTrack < noOfTracksInMedium; currentTrack++) {
					noOfProcessedTrack += 1;
					printProgress(startTime, noOfTracksInRelease, noOfProcessedTrack, prepareProgressBarInfo(
							currentMedium + 1, noOfMediums, currentTrack + 1, noOfTracksInMedium));

					Element elementTrack = (Element) nodeListTrack.item(currentTrack);

					Track track = new Track();
					track.setParent(medium);
					track.setId(elementTrack.getAttribute("id"));
					track.setTitle(getTextContent(elementTrack, "title"));
					track.setPosition(Integer.parseInt(getTextContent(elementTrack, "position")));
					track.setLength(Integer.parseInt(getTextContent(elementTrack, "length")));

					String recordingId = ((Element) elementTrack.getElementsByTagName("recording").item(0))
							.getAttribute("id");

					InputStream xmlRecording = xmlProvider.getRecording(recordingId);
					Document recordingDoc = db.parse(xmlRecording);
					xmlRecording.close();
					Recording recording = new Recording();
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
					track.setRecording(recording);
					trackList.add(track);
					addToMp3tagOutputList(track, parametersParser.getMp3Tags());
				}
				medium.setTrackList(trackList);
				mediumList.add(medium);
			}
			release.setMediumList(mediumList);
			
			logger.debug("Output:");
			output4mp3tag.forEach(item -> logger.debug(item));
			if (parametersParser.isConsoleOutput()) {
				System.out.println("\n\n");
				output4mp3tag.forEach(System.out::println);
			}
			else {
				File outputFile = parametersParser.getOutputFile(release.getTitle());
				if (outputFile != null) {
					Files.write(outputFile.toPath(), output4mp3tag, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
					System.out.println("\n\nOutput was saved to file \"" + outputFile.getPath() + "\" successfully.");
				}
			}
			
			
		} catch (IOException | ParserConfigurationException | SAXException | InterruptedException e) {
			logger.error(e.toString());
			Arrays.asList(e.getStackTrace()).forEach(item -> logger.error("        at " + item));
			System.out.println("Got error: " + e.getClass().getName() + " (" + e.getMessage() + ")");
		}
	}

	private static RelationWork createRecordingRelationWork(Element elementRelationList) 
			throws SAXException, IOException, InterruptedException {
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

	private static Work getWork(String workId) throws SAXException, IOException, InterruptedException {
		Work work = null;

		if (works.containsKey(workId)) {
			work = works.get(workId);
		} else {
			InputStream xmlWork = xmlProvider.getWork(workId);
			Document workDocument = db.parse(xmlWork);
			xmlWork.close();
			work = new Work();
			work.setId(workId);
			Element elementWork = (Element) workDocument.getDocumentElement().getFirstChild();
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

	private static List<RelationArtist> createRelationArtistList(Element elementRelationList) 
			throws SAXException, IOException, InterruptedException {
		List<RelationArtist> relationArtist = new ArrayList<>();
		NodeList relation = elementRelationList.getElementsByTagName("relation");
		for (int i = 0; i < relation.getLength(); i++) {
			Element elementRelation = (Element) relation.item(i);
			RelationArtist relationArtistMember = new RelationArtist();
			relationArtistMember.setType(elementRelation.getAttribute("type"));
			relationArtistMember.setAttribute(getTextContent(elementRelation, "attribute-list.attribute"));
			relationArtistMember.setBeginDate(getTextContent(elementRelation, "begin"));
			relationArtistMember.setEndDate(getTextContent(elementRelation, "end"));
			relationArtistMember.setArtist(getArtist(getTextContent(elementRelation, "target")));
			relationArtist.add(relationArtistMember);
		}
		return relationArtist;
	}

	private static Artist getArtist(String artistId) throws SAXException, IOException, InterruptedException {
		Artist artist = null;

		if (artists.containsKey(artistId)) {
			artist = artists.get(artistId);
		} else {
			InputStream xmlArtist = xmlProvider.getArtist(artistId);
			Document artistDocument = db.parse(xmlArtist);
			xmlArtist.close();
			artist = new Artist();
			artist.setId(artistId);
			Element elementArtist = (Element) artistDocument.getDocumentElement().getFirstChild();
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
		String remainingPath = "";
		int dotPosition = name.indexOf(".");

		if (dotPosition > 0) {
			elementName = name.substring(0, dotPosition);
			remainingPath = name.substring(dotPosition + 1, name.length());
		} else {
			elementName = name;
		}
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element childElement = (Element) children.item(i);
				if (childElement.getTagName().equals(elementName)) {
					if (remainingPath.isEmpty()) {
						return childElement.getTextContent();
					} else {
						return getTextContent(childElement, remainingPath);
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

		StringBuilder string = new StringBuilder(80);
		int percent = (int) (current * 100 / total);
		string.append('\r')
		.append(percent < 10 ? "  " : (percent < 100 ? " " : ""))
		.append(String.format(" %d%% [", percent))
		.append(String.join("", Collections.nCopies((int) (percent * 0.35), "="))).append('>')
		.append(String.join("", Collections.nCopies(35 - (int) (percent * 0.35), " "))).append(']').append(info)
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
			case "%discnumber%":
				tagValue = Mp3tagsValuesProvider.getDiscNumber(track);
				break;
			case "%disctotal%":
				tagValue = Mp3tagsValuesProvider.getDiscTotal(track);
				break;
			case "%album%":
				tagValue = Mp3tagsValuesProvider.getAlbumName(track);
				break;
			case "%tracknumber%":
				tagValue = Mp3tagsValuesProvider.getTrackNumber(track);
				break;
			case "%tracktotal%":
				tagValue = Mp3tagsValuesProvider.getTrackTotal(track);
				break;
			case "%title%":
				tagValue = Mp3tagsValuesProvider.getTrackTitle(track);
				break;
			case "%composer%":
				tagValue = Mp3tagsValuesProvider.getComposer(track, false);
				break;
			case "%artist%":
				tagValue = Mp3tagsValuesProvider.getArtist(track);
				break;
			case "%year%":
				tagValue = Mp3tagsValuesProvider.getComposingDate(track);
				break;
			case "%organization%":
				tagValue = Mp3tagsValuesProvider.getOrganization(track);
				break;
			case "%comment%":
				tagValue = Mp3tagsValuesProvider.getComment(track);
				break;
			case "%url%":
				tagValue = Mp3tagsValuesProvider.getUrl(track);
				break;
			case "%length%":
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
