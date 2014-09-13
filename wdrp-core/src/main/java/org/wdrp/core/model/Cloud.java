package org.wdrp.core.model;

import java.io.Serializable;

import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;


@SuppressWarnings("serial")
public class Cloud implements Serializable, Comparable<Cloud> {
	private double[][] _coordinates;
	
	public Cloud(double[][] coordinates) {
		_coordinates = coordinates;
	}

	public Polygon getPolygon() {
		Polygon polygon = new Polygon();
		polygon.startPath(new Point(_coordinates[0][0], _coordinates[0][1]));
	    
	    for (int i = 1; i < _coordinates.length; i++)
	    	polygon.lineTo(new Point(_coordinates[i][0], _coordinates[i][1]));
	    
		return polygon;
	}

	public boolean intersectsLine(double x1, double x2, double y1, double y2) {
		Polyline polyline = new Polyline();
		polyline.startPath(new Point(x1, y1));
		polyline.lineTo(new Point(x2, y2));
		
		return !GeometryEngine.intersect(polyline, getPolygon(), SpatialReference.create(4326)).isEmpty();
	}

	@Override
	public int compareTo(Cloud c) {
		double thisArea = getPolygon().calculateArea2D();
		double otherArea = c.getPolygon().calculateArea2D();
		if(thisArea > otherArea) {
			return 1;
		}
		else if(thisArea < otherArea) {
			return -1;
		}
		else {
			return 0;
		}
	}

	public double[][] getCoordinates() {
		return _coordinates;
	}
}
