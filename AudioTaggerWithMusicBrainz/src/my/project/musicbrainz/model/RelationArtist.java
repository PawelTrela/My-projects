package my.project.musicbrainz.model;

public class RelationArtist {
	private String type;
	private String date;
	private Artist artist;

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
	public Artist getArtist() {
		return artist;
	}
	public void setArtist(Artist artist) {
		this.artist = artist;
	}
	
	public RelationArtist() {
		
	}
	
	public RelationArtist(String type, String date, Artist artist) {
		this.type = type;
		this.date = date;
		this.artist = artist;
	}
	
	@Override
	public String toString() {
		return "\nRelationArtist [type=" + type + ", date=" + date + ", artist=" + artist + "]";
	}
}
