package my.project.musicbrainz;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import my.project.musicbrainz.model.Artist;
import my.project.musicbrainz.model.Medium;
import my.project.musicbrainz.model.Recording;
import my.project.musicbrainz.model.RelationArtist;
import my.project.musicbrainz.model.Release;
import my.project.musicbrainz.model.Track;

public class Mp3tagsValuesProvider {
	public static String getDiscNumber(Track track) {
		return track.getParent().getPosition();
	}

	public static String getDiscTotal(Track track) {
		return track.getParent().getParent().getMediumListCount();
	}

	public static String getAlbumName(Track track) {
		Medium medium = track.getParent();
		Release release = medium.getParent();
		String albumTitle = medium.getTitle();
		String releaseTitle = release.getTitle();
		Integer albumCount = Integer.getInteger(release.getMediumListCount(), 1);
		StringBuilder valueToReturn = new StringBuilder();
		valueToReturn.append(releaseTitle);

		if (albumCount > 1) {
			valueToReturn.append(", " + medium.getFormat() + medium.getPosition());
			if (!albumTitle.isEmpty()) {
				valueToReturn.append(". " + albumTitle);
			}
		}
		else {
			if (!albumTitle.isEmpty()) {
				valueToReturn.append(", " + albumTitle);
			}
		}
		return normalize(valueToReturn.toString());
	}

	public static String getTrackNumber(Track track) {
		return track.getPosition().toString();
	}

	public static String getTrackTotal(Track track) {
		return track.getParent().getTrackListCount();
	}

	public static String getTrackTitle(Track track) {
		String title = track.getRecording().getTitle();
		if (title.isEmpty()) {
			track.getTitle();
		}
		return title;
	}

	public static String getTrackLength(Track track) {
		Integer millis = track.getLength();
		Long hours = TimeUnit.MILLISECONDS.toHours(millis);
		Long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1);
		Long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1);

		String secondsAsString = seconds.toString();
		if (seconds < 10) {
			secondsAsString = "0" + secondsAsString;
		}
		String minutesAsString = minutes.toString();
		if (minutes < 10 && hours > 0) {
			minutesAsString = "0" + minutesAsString;
		}
		String valueToReturn = minutesAsString + ":" + secondsAsString;
		if (hours > 0) {
			valueToReturn = hours.toString() + ":" + valueToReturn;
		}

		return valueToReturn;
	}

	public static String getComposer(Track track, boolean includeDetails) {
		String valueToReturn = "";
		if (hasRelationWork(track.getRecording())) {
			List<RelationArtist> relations = track.getRecording().getRelationWork().getWork().getRelationArtist();
			for (int i = 0; i < relations.size(); i++) {
				RelationArtist relation = relations.get(i);
				if (relation.getType().equals("composer")) {
					Artist artist = relation.getArtist();
					valueToReturn += textSeparator(valueToReturn);
					valueToReturn += artist.getName();
					if (includeDetails) {
						String details = "";
						if (!artist.getLifeSpanBegin().isEmpty()) {
							details = artist.getLifeSpanBegin() + " - " + artist.getLifeSpanEnd();
						}
						if (!artist.getCountry().isEmpty()) {
							details += textSeparator(details);
							details += artist.getArea();
						}
						if (!details.isEmpty()) {
							details = " (" + details + ")";
						}
						valueToReturn += details;
					}
				}
			}
		}
		return normalize(valueToReturn);
	}

	public static String getArtist(Track track) {
		String valueToReturn = "";

		if (hasRelationArtist(track.getRecording())) {
			Map<String,String> artists = new LinkedHashMap<>();
			artists.put("instrument", null);
			artists.put("harpsichord", null);
			artists.put("strings", null);
			artists.put("performer", null);
			artists.put("vocal", null);
			artists.put("performing orchestra", null);
			artists.put("conductor", null);

			List<RelationArtist> relations = track.getRecording().getRelationArtist();
			for (int i = 0; i<relations.size(); i++) {
				RelationArtist relation = relations.get(i);
				String relationType = relation.getType();
				if (artists.containsKey(relationType)) {
					String artistName = relation.getArtist().getName();
					if (relationType.equals("instrument")) {
						String instrument = relation.getAttribute();
						if (!instrument.isEmpty()) {
							artistName += " (" + instrument + ")";
						}
					}
					else if (!relationType.equals("performing orchestra")) {
						artistName += " (" + relationType + ")";	
					}
					artists.put(relationType, artistName);
				}
			}
			
			for (Map.Entry<String,String> entry : artists.entrySet()) {
				if (entry.getValue()!=null) {
					valueToReturn += textSeparator(valueToReturn);
					valueToReturn += entry.getValue();
				}
			}
			valueToReturn = normalize(valueToReturn);
		}
		else {
			List<Artist> artists = track.getArtistList();
			if (artists == null) {
				artists = track.getParent().getParent().getArtistList();
			}
			if (artists != null) {
				for (Artist artist : artists) {
					valueToReturn += textSeparator(valueToReturn);
					valueToReturn += artist.getName();
				}
			}
		}
		return valueToReturn;
	}

	public static String getRecordingDate(Track track) {
		String valueToReturn = "";
		if (hasRelationArtist(track.getRecording())) {
			List<RelationArtist> relations = track.getRecording().getRelationArtist();
			for (int i = 0; i < relations.size(); i++) {
				RelationArtist relation = relations.get(i);
				valueToReturn = relation.getBeginDate();
				if (valueToReturn.isEmpty()) {
					valueToReturn = relation.getBeginDate();
				}
				if (!valueToReturn.isEmpty()) {
					break;
				}
			}
		}
		if (valueToReturn.isEmpty()) {
			valueToReturn = track.getParent().getParent().getDate();
		}
		return valueToReturn;
	}
	
	public static String getComposingDate(Track track) {
		String valueToReturn = "";
		if (hasRelationWork(track.getRecording())) {
			List<RelationArtist> relations = track.getRecording().getRelationWork().getWork().getRelationArtist();
			for (int i = 0; i < relations.size(); i++) {
				RelationArtist relation = relations.get(i);
				if (relation.getType().equals("composer")) {
					valueToReturn = relation.getBeginDate();
				}
			}
		}
		return valueToReturn;
	}

	public static String getOrganization(Track track) {
		return normalize(track.getParent().getParent().getLabel());
	}

	public static String getComment(Track track) {
		String artists = getArtist(track);
		String recordingDate = getRecordingDate(track);
		String composer = getComposer(track, true);
		String composingDate = getComposingDate(track);
		StringBuilder valueToReturn = new StringBuilder();
		if (!artists.isEmpty()) {
			valueToReturn.append("recorded by " + artists);
			if (!recordingDate.isEmpty()) {
				valueToReturn.append(" in " + recordingDate);
			}
		}
		if (!composer.isEmpty()) {
			valueToReturn.append(textSeparator(valueToReturn.toString()));
			valueToReturn.append("composed by " + composer);
			if (!composingDate.isEmpty()) {
				valueToReturn.append(" in " + composingDate);
			}
		}
		return normalize(valueToReturn.toString());
	}

	public static String getUrl(Track track) {
		return "https://musicbrainz.org/track/" + track.getId();
	}

	private static boolean hasRelationWork(Recording recording) {
		if (recording.getRelationWork() != null) {
			return true;
		}
		return false;
	}
	
	private static boolean hasRelationArtist(Recording recording) {
		if (recording.getRelationArtist() != null) {
			return true;
		}
		return false;
	}
	
	private static String textSeparator(String pastText) {
		if (!pastText.isEmpty()) {
			return ", ";
		}
		return "";
	}
	
	private static String normalize(String textToNormalize) {
		String outputString = textToNormalize.replaceAll(Pattern.quote("|"), "_");
		return outputString;
	}
}
