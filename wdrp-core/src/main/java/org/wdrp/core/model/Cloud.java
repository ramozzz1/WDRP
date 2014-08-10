package org.wdrp.core.model;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

public class Cloud {
	private Polygon polygon;
	
	public Cloud(List<Coordinate> coordinates) {
		polygon = new GeometryFactory().createPolygon((Coordinate[]) coordinates.toArray());
	}
	
	public Polygon getPolygon() {
		return polygon;
	}

	public boolean intersectsLine(double x1, double x2, double y1, double y2) {
		Coordinate[] coordinates = {new Coordinate(x1, y1, 0) ,new Coordinate(x2, y2, 0)};
		LineString ls = new GeometryFactory().createLineString(coordinates);
		return polygon.intersects(ls);
	}
}
