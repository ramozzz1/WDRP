package org.wdrp.core.model;

import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;


public class Cloud {
	private Polygon polygon;
	
	public Cloud(double[][] pts) {
		polygon = new Polygon();

		polygon.startPath(new Point(pts[0][0], pts[0][1]));
	    
	    for (int i = 1; i < pts.length; i++)
	    	polygon.lineTo(new Point(pts[i][0], pts[i][1]));
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public boolean intersectsLine(double x1, double x2, double y1, double y2) {
		Polyline polyline = new Polyline();
		polyline.startPath(new Point(x1, y1));
		polyline.lineTo(new Point(x2, y2));
		
		return !GeometryEngine.intersect(polyline, polygon, SpatialReference.create(4326)).isEmpty();
	}
}
