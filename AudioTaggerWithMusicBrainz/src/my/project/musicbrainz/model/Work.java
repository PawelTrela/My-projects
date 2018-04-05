package my.project.musicbrainz.model;

import java.util.List;

public class Work {
	private String id;
	private String title;
	private List<RelationArtist> relationArtist;

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
	public Work() {
		this("", "", null);
	}
	
	public Work(String id, String title, List<RelationArtist> relationArtist) {
		this.id = id;
		this.title = title;
		this.relationArtist = relationArtist;
	}

	@Override
	public String toString() {
		return "\nWork [id=" + id + ", title=" + title + ", relationArtist=" + relationArtist + "]";
	}
}
