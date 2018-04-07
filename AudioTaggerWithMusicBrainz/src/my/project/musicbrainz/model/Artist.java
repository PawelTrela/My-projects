package my.project.musicbrainz.model;

public class Artist {
	private String id;
	private String name;
	private String sortName;
	private String type;
	private String country;
	private String area;
	private String lifeSpanBegin;
	private String lifeSpanEnd;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortName() {
		return sortName;
	}
	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getLifeSpanBegin() {
		return lifeSpanBegin;
	}
	public void setLifeSpanBegin(String lifeSpanBegin) {
		this.lifeSpanBegin = lifeSpanBegin;
	}
	public String getLifeSpanEnd() {
		return lifeSpanEnd;
	}
	public void setLifeSpanEnd(String lifeSpanEnd) {
		this.lifeSpanEnd = lifeSpanEnd;
	}
	
	public Artist() {
		this("", "", "", "", "", "", "", "");
	}
	
	public Artist(String id, String name, String sortName, String type, String country, String area,
			String lifeSpanBegin, String lifeSpanEnd) {
		this.id = id;
		this.name = name;
		this.sortName = sortName;
		this.type = type;
		this.country = country;
		this.area = area;
		this.lifeSpanBegin = lifeSpanBegin;
		this.lifeSpanEnd = lifeSpanEnd;
	}
	
	@Override
	public String toString() {
		return "Artist [id=" + id + ", name=" + name + ", sortName=" + sortName + ", type=" + type + ", country="
				+ country + ", area=" + area + ", lifeSpanBegin=" + lifeSpanBegin + ", lifeSpanEnd=" + lifeSpanEnd
				+ "]";
	}
	
}
