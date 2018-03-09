package my.project.musicbrainz.model;

public class Work {
	private String id;
	private String title;
	private Artist composer;
	private String beginDate;
	private String endDate;

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
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public Work() {
		
	}
	
	public Work(String id, String title, Artist composer, String beginDate, String endDate) {
		this.id = id;
		this.title = title;
		this.composer = composer;
		this.beginDate = beginDate;
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "\nWork [id=" + id + ", title=" + title + ", composer=" + composer + ", beginDate=" + beginDate + 
				", endDate=" + endDate + "]";
	}
}
