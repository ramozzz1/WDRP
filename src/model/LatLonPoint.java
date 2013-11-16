package model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.mapdb.Serializer;

public class LatLonPoint {
	
	public double lat;
	public double lon;
	
	public LatLonPoint(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
}

class LatLonPointSerializer implements Serializer<LatLonPoint>, Serializable{

    @Override
    public void serialize(DataOutput out, LatLonPoint value) throws IOException {
        out.writeDouble(value.lat);
        out.writeDouble(value.lon);
    }

    @Override
    public LatLonPoint deserialize(DataInput in, int available) throws IOException {
        return new LatLonPoint(in.readDouble(), in.readDouble());
    }
}