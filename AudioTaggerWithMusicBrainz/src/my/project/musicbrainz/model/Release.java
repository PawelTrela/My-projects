package my.project.musicbrainz.model;

import java.util.List;

public class Release {
	private String id;
	private String title;
	private String label;
	private String date;
	private String mediumListCount;
	private List<Artist> artistList;
	private List<Medium> mediumList;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMediumListCount() {
		return mediumListCount;
	}

	public void setMediumListCount(String mediumListCount) {
		this.mediumListCount = mediumListCount;
	}

	public List<Artist> getArtistList() {
		return artistList;
	}

	public void setArtistList(List<Artist> artistList) {
		this.artistList = artistList;
	}

	public List<Medium> getMediumList() {
		return mediumList;
	}

	public void setMediumList(List<Medium> mediumList) {
		this.mediumList = mediumList;
	}

	public Release() {
		this("", "", "", "", "", null, null);
	}
	
	public Release(String id, String title, String label, String date, String mediumListCount, 
			List<Artist> artistList, List<Medium> mediumList) {
		this.id = id;
		this.title = title;
		this.label = label;
		this.date = date;
		this.mediumListCount = mediumListCount;
		this.artistList = artistList;
		this.mediumList = mediumList;
	}

	@Override
	public String toString() {
		return "Release [id=" + id + ", title=" + title + ", label=" + label + ", date=" + date + ", mediumListCount="
				+ mediumListCount + ", artistList=" + artistList + ", mediumList=" + mediumList + "]";
	}
}
