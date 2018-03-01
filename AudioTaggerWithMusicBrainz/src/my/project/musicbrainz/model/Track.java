package my.project.musicbrainz.model;

public class Track {
	private String id;
	private Integer position;
	private String title;
	private Integer length;
	private Recording recording;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Recording getRecording() {
		return recording;
	}
	public void setRecording(Recording recording) {
		this.recording = recording;
	}
	
	public Track() {
		
	}
	
	public Track(String id, int position, String title, int length, Recording recording) {
		this.id = id;
		this.position = position;
		this.title = title;
		this.length = length;
		this.recording = recording;
	}
	
	@Override
	public String toString() {
		return "\n		Track [id=" + id + ", position=" + position + ", title=" + title + ", length=" + length + ", recording="
				+ recording + "]";
	}
}
