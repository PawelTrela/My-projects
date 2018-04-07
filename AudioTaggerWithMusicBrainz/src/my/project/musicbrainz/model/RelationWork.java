package my.project.musicbrainz.model;

public class RelationWork {
	private String type;
	private String date;
	private Work work;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Work getWork() {
		return work;
	}
	public void setWork(Work work) {
		this.work = work;
	}
	
	public RelationWork() {
		this("", "", null);
	}
	
	public RelationWork(String type, String date, Work work) {
		this.type = type;
		this.date = date;
		this.work = work;
	}
	
	@Override
	public String toString() {
		return "RelationWork [type=" + type + ", date=" + date + ", work=" + work + "]";
	}
}
