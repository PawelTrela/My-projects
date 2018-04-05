package my.project.musicbrainz.model;

import java.util.List;

public class Recording {
	private String id;
	private String title;
	private List<RelationArtist> relationArtist;
	private RelationWork relationWork;
	
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
	public RelationWork getRelationWork() {
		return relationWork;
	}
	public void setRelationWork(RelationWork relationWork) {
		this.relationWork = relationWork;
	}
	
	public Recording() {
		this("", "", null, null);
	}
	
	public Recording(String id, String title, List<RelationArtist> relationArtist, RelationWork relationWork) {
		this.id = id;
		this.title = title;
		this.relationArtist = relationArtist;
		this.relationWork = relationWork;
	}
	
	@Override
	public String toString() {
		return "\nRecording [id=" + id + ", title=" + title + ",\n\nrelationArtist=" + relationArtist + ", relationWork=" + relationWork
				+ "]";
	}
}
