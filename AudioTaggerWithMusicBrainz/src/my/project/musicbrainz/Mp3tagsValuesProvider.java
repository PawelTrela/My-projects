package my.project.musicbrainz;

import java.util.List;
import java.util.concurrent.TimeUnit;
import my.project.musicbrainz.model.Medium;
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
		String valueToReturn = releaseTitle;
		
		if (albumCount > 1) {
			valueToReturn = valueToReturn + ", " + medium.getFormat() + medium.getPosition();
			if (!albumTitle.isEmpty()) {
				valueToReturn = valueToReturn + ". " + albumTitle;
			}
		}
		else {
			if (!albumTitle.isEmpty()) {
				valueToReturn = valueToReturn + ", " + albumTitle;
			}
		}
		return valueToReturn;
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
	
	public static String getComposer(Track track) {
		
	}
	
	public static String getArtist(Track track) {
		
	}
	
	public static String getRecordingDate(Track track) {
		/*
		 * TODO Loop through all relationArtist elements and get end date from it
		 */
		List<RelationArtist> relationArtist = track.getRecording().getRelationArtist();
		
	}
	
	public static String getOrganization(Track track) {
		return track.getParent().getParent().getLabel();
	}
	
	public static String getComment(Track track) {
		
	}
	
	public static String getUrl(Track track) {
		return "https://musicbrainz.org/track/" + track.getId();
	}
}
