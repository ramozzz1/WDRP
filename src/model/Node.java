package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Node implements Serializable {
	
	private long id;
	private double lat;
	private double lon;
	
	public Node(long id) {
		this.id = id;
		this.lat = 0;
		this.lon = 0;
	}
	
	public Node(long nodeId, double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
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
