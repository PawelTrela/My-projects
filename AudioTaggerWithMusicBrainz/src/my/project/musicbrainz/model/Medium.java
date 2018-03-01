package my.project.musicbrainz.model;

import java.util.List;

public class Medium {
	private String position;
	private String format;
	private String trackListCount;
	private List<Track> trackList;
	
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getTrackListCount() {
		return trackListCount;
	}
	public void setTrackListCount(String trackListCount) {
		this.trackListCount = trackListCount;
	}
	public List<Track> getTrackList() {
		return trackList;
	}
	public void setTrackList(List<Track> trackList) {
		this.trackList = trackList;
	}
	
	public Medium() {
		
	}
	
	public Medium(String position, String format, String trackListCount, List<Track> trackList) {
		this.position = position;
		this.format = format;
		this.trackListCount = trackListCount;
		this.trackList = trackList;
	}
	
	@Override
	public String toString() {
		return "\n	Medium [position=" + position + ", format=" + format + ", trackListCount=" + trackListCount
				+ ", trackList=" + trackList + "]";
	}
}
