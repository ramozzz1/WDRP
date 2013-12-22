package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Node implements Serializable {
	
	private long id;
	private double lat;
	private double lon;
	
	public Node(long nodeId, double lat, double lon) {
		this.id = nodeId;
		this.lat = lat;
		this.lon = lon;
	}
	
	public Node(long id) {
		this(id,0,0);
	}

	public Node(long nodeId, LatLonPoint latLonPoint) {
		this(nodeId,latLonPoint.lat,latLonPoint.lon);
	}

	public long getId() {
		return this.id;
	}
	
	public double getLat() {
		return this.lat;
	}
	
	public double getLon() {
		return this.lon;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(this.id).hashCode();
	}
	
	@Override
	public boolean equals (Object o) {
		if(o == null)                return false;
	    if(!(o instanceof Node)) return false;

	    Node other = (Node) o;
	    return this.id == other.id;
	}
}
