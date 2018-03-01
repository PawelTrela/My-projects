package my.project.musicbrainz.model;

import java.util.List;

public class Release {
	private String id;
	private String title;
	private String label;
	private String mediumListCount;
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

	public String getMediumListCount() {
		return mediumListCount;
	}

	public void setMediumListCount(String mediumListCount) {
		this.mediumListCount = mediumListCount;
	}

	public List<Medium> getMediumList() {
		return mediumList;
	}

	public void setMediumList(List<Medium> mediumList) {
		this.mediumList = mediumList;
	}

	public Release() {
		
	}
	
	public Release(String id, String title, String label, String mediumListCount, List<Medium> mediumList) {
		this.id = id;
		this.title = title;
		this.label = label;
		this.mediumListCount = mediumListCount;
		this.mediumList = mediumList;
	}

	@Override
	public String toString() {
		return "Release [id=" + id + ", title=" + title + ", label=" + label + ", mediumListCount=" + mediumListCount
				+ ", mediumList=" + mediumList + "]";
	}
}
