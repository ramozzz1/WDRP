package reader;

public class OSMNode {
	
	public long id;
	public double lon;
	public double lat;
	
	public OSMNode(long id, double lon, double lat) {
		this.id = id;
		this.lon = lon;
		this.lat = lat;
	}
}
