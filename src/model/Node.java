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
	
	public static long generateId(NodeType nodeType, String stationId, String time, String tripId, boolean useTripId) {
		return Long.parseLong(nodeType.Value()+time.replace(":", "")+(useTripId?(tripId.hashCode()+"").replace("-","").substring(0, 6):"")+stationId+"");
	}

	public static NodeType getNodeType(long nodeId) {
		int rawType = Integer.parseInt(String.valueOf(nodeId).substring(0, 1));
		return NodeType.fromInteger(rawType);
	}

	public static String getTime(long nodeId) {
		String time = String.valueOf(nodeId);
		return time.substring(1, 3)+":"+time.substring(3, 5)+":"+time.substring(5, 7);
	}
	
	public static long getStationId(long nodeId) {
		String rawId = String.valueOf(nodeId);
		return Long.parseLong(rawId.substring(13, rawId.length()));
	}
}
