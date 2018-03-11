package my.project.musicbrainz.model;

public class RelationArtist {
	private String type;
	private String beginDate;
	private String endDate;
	private Artist artist;

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public Artist getArtist() {
		return artist;
	}
	public void setArtist(Artist artist) {
		this.artist = artist;
	}
	public RelationArtist() {
		
	}
	
	public RelationArtist(String type, String beginDate, String endDate, Artist artist) {
		this.type = type;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.artist = artist;
	}
	
	@Override
	public String toString() {
		return "\nRelationArtist [type=" + type + ", beginDate=" + beginDate + 
				", endDate=" + endDate + ", artist=" + artist + "]";
	}
}
