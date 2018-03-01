package my.project.musicbrainz.model;

import java.util.List;

public class Recording {
	private String id;
	private String title;
	private List<RelationArtist> relationArtist;
	private Work work;
	
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
	public List<RelationArtist> getRelationArtist() {
		return relationArtist;
	}
	public void setRelationArtist(List<RelationArtist> relationArtist) {
		this.relationArtist = relationArtist;
	}
	public Work getWork() {
		return work;
	}
	public void setWork(Work work) {
		this.work = work;
	}
	
	public Recording() {
		
	}
	
	public Recording(String id, String title, List<RelationArtist> relationArtist, Work work) {
		this.id = id;
		this.title = title;
		this.relationArtist = relationArtist;
		this.work = work;
	}
	
	@Override
	public String toString() {
		return "\nRecording [id=" + id + ", title=" + title + ", relationArtist=" + relationArtist + ", work=" + work
				+ "]";
	}
}
