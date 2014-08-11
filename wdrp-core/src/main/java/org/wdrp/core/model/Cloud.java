package org.wdrp.core.model;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class Cloud {
	private Polygon polygon;
	
	public Cloud(ArrayList<Coordinate> coordinates) {
		if(!coordinates.get(0).equals(coordinates.get(coordinates.size()-1))) {
			coordinates.add(new Coordinate(coordinates.get(0)));
		}
		
		GeometryFactory fact = new GeometryFactory();
		LinearRing shell = fact.createLinearRing(convertToArray(coordinates));
		
		polygon = fact.createPolygon(shell);
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public boolean intersectsLine(double x1, double x2, double y1, double y2) {
		Coordinate[] coordinates = {new Coordinate(x1, y1, 0) ,new Coordinate(x2, y2, 0)};
		LineString ls = new GeometryFactory().createLineString(coordinates);
		return polygon.intersects(ls);
	}
	
	private Coordinate[] convertToArray(ArrayList<Coordinate> coordinates) {
		Coordinate[] cords = new Coordinate[coordinates.size()];
		int count = 0;
		for (Coordinate coordinate : coordinates) {
			cords[count] = coordinate;
			count++;
		}
		return cords;
	}
}
