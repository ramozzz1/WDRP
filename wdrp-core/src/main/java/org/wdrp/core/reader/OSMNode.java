package org.wdrp.core.reader;

public class OSMNode {
	
	public long id;
	public double lon;
	public double lat;
	
	public OSMNode(long id, double lat, double lon) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
	}
}
