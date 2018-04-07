package my.project.musicbrainz.model;

import java.util.List;

public class Medium {
	private String position;
	private String format;
	private String title;
	private String trackListCount;
	private List<Track> trackList;
	private Release parent;
	
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	
	public Release getParent() {
		return parent;
	}
	public void setParent(Release parent) {
		this.parent = parent;
	}
	public Medium() {
		this("", "", "", null);
	}
	
	public Medium(String position, String format, String trackListCount, List<Track> trackList) {
		this.position = position;
		this.format = format;
		this.trackListCount = trackListCount;
		this.trackList = trackList;
	}
	
	@Override
	public String toString() {
		return "Medium [position=" + position + ", format=" + format + ", trackListCount=" + trackListCount
				+ ", trackList=" + trackList + "]";
	}
}
