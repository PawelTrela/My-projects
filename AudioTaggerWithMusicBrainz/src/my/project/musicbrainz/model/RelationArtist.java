package my.project.musicbrainz.model;

public class RelationArtist {
	private String type;
	private String attribute;
	private String beginDate;
	private String endDate;
	private Artist artist;

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
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
	
	public RelationArtist(String type, String attribute, String beginDate, String endDate, Artist artist) {
		this.type = type;
		this.attribute = attribute;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.artist = artist;
	}
	
	@Override
	public String toString() {
		return "\nRelationArtist [type=" + type + ", attribute=" + attribute + ", beginDate=" + beginDate + 
				", endDate=" + endDate + ", artist=" + artist + "]";
	}
}
