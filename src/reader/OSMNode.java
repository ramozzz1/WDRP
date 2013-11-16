package reader;

public class OSMNode {
	
	private long id;
	private double lon;
	private double lat;
	
	public OSMNode() {
		
	}
	
	public OSMNode(long id, double lon, double lat) {
		this.id = id;
		this.lon = lon;
		this.lat = lat;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}
	
	@Override
	public boolean equals (Object o) {
		if(o == null)                return false;
	    if(!(o instanceof OSMNode)) return false;

	    OSMNode other = (OSMNode) o;
	    return this.id == other.id;
	}
	
	@Override
	public int hashCode(){
		return (int) this.id;
	}
}
