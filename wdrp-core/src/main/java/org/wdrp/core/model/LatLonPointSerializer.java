package org.wdrp.core.model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.mapdb.Serializer;


	/**
	 * custom serializer for storing lat,lon pairs
	 * @author zakaria
	 *
	 */
	@SuppressWarnings("serial")
	public class LatLonPointSerializer implements Serializer<LatLonPoint>, Serializable{

	    @Override
	    public void serialize(DataOutput out, LatLonPoint value) throws IOException {
	        out.writeDouble(value.lat);
	        out.writeDouble(value.lon);
	    }

	    @Override
	    public LatLonPoint deserialize(DataInput in, int available) throws IOException {
	        return new LatLonPoint(in.readDouble(), in.readDouble());
	    }

		@Override
		public int fixedSize() {
			return -1;
		}
	}