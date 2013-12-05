package model;

import java.io.Serializable;

public class Bounds implements Serializable,Comparable<Bounds>{

	private static final long serialVersionUID = 1567018657273465042L;
	private double minLat;
	private double maxLat;
	private double minLon;
	private double maxLon;

	public Bounds(double minLat, double maxLat, double minLon, double maxLon) {
		this.minLat = minLat;
		this.maxLat = maxLat;
		this.minLon = minLon;
		this.maxLon = maxLon;
	}
	
	public Bounds() {
		this(Double.MAX_VALUE,Double.MIN_VALUE,Double.MAX_VALUE,Double.MIN_VALUE);
	}
	
	public void updateBounds(double lat, double lon) {
		if(lat < minLat)
			minLat = lat;
		if(lat > maxLat)
			maxLat = lat;
		if(lon < minLon)
			minLon = lon;
		if(lon > maxLon)
			maxLon = lon;
	}
	
	public double getMinLat() {
		return minLat;
	}

	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}

	public double getMaxLat() {
		return maxLat;
	}

	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}

	public double getMinLon() {
		return minLon;
	}

	public void setMinLon(double minLon) {
		this.minLon = minLon;
	}

	public double getMaxLon() {
		return maxLon;
	}

	public void setMaxLon(double maxLon) {
		this.maxLon = maxLon;
	}
	
	public String toJsonArray() {
		String s = "{";
		
		s += "\"minLat\":" + minLat + ", ";
		s += "\"maxLat\":" + maxLat + ", ";
		s += "\"minLon\":" + minLon + ", ";
		s += "\"maxLon\":" + maxLon;
		
		s += "}";
		return s;
	}
	
	@Override
	public String toString() {
		return toJsonArray();
	}

	@Override
	public int compareTo(Bounds b) {		
		return 0;
	}
}