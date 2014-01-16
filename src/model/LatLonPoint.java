package model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.mapdb.Serializer;

@SuppressWarnings("serial")
public class LatLonPoint implements Serializable {
	
	public double lat;
	public double lon;
	
	public LatLonPoint(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}
	
	@Override
	public int hashCode() {
		return Double.valueOf(this.lat).hashCode() * Double.valueOf(this.lon).hashCode();
	}
	
	@Override
	public boolean equals (Object o) {
		if(o == null)                return false;
	    if(!(o instanceof LatLonPoint)) return false;

	    LatLonPoint other = (LatLonPoint) o;
	    return this.getLat() == other.getLat() && this.getLon() == other.getLon();
	}
}