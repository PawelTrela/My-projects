package my.project.musicbrainz.model;

public class Work {
	private String id;
	private String title;
	private Artist composer;

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
	public Artist getComposer() {
		return composer;
	}
	public void setComposer(Artist composer) {
		this.composer = composer;
	}
	
	public Work() {
		
	}
	
	public Work(String id, String title, Artist composer) {
		this.id = id;
		this.title = title;
		this.composer = composer;
	}

	@Override
	public String toString() {
		return "\nWork [id=" + id + ", title=" + title + ", composer=" + composer + "]";
	}
}
