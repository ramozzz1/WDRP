package org.wdrp.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.mapdb.Fun.Tuple2;
import org.wdrp.core.model.Arc;
import org.wdrp.core.model.Cloud;
import org.wdrp.core.model.Graph;
import org.wdrp.core.model.LatLonPoint;
import org.wdrp.core.model.Weather;

import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Boundary;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.LineStyle;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.Style;

public class WeatherUtil {
	
	public static Weather generateWeatherFromKML(String kmlPath) throws FileNotFoundException {
		Weather w = new Weather();
		
		Kml kml = Kml.unmarshal(new File(kmlPath));
		Feature feature = kml.getFeature();
		if(feature != null) {
	        if(feature instanceof Document) {
	            Document document = (Document) feature;
	            List<Feature> folderList = document.getFeature();
	            for(Feature documentFeature : folderList) {
	            	if(documentFeature instanceof Folder) {
	            		Folder folder = (Folder) documentFeature;
	            		String time = folder.getName();
	    	            List<Feature> placemarkList = folder.getFeature();
	    	            for(Feature pm : placemarkList) {
			                if(pm instanceof Placemark) {
			                    Placemark placemark = (Placemark) pm;
			                    Geometry geometry = placemark.getGeometry();
			                    if(geometry != null) {
			                        if(geometry instanceof Polygon) {
			                            Polygon polygon = (Polygon) geometry;
			                            Boundary outerBoundaryIs = polygon.getOuterBoundaryIs();
			                            if(outerBoundaryIs != null) {
			                                LinearRing linearRing = outerBoundaryIs.getLinearRing();
			                                if(linearRing != null) {
			                                	List<Coordinate> coordinates = linearRing.getCoordinates();
			                                	double cords[][] = new double[coordinates.size()][2];
			                                    if(coordinates != null) {
			                                        for (int i = 0; i < coordinates.size(); i++) {
			                                        	Coordinate coordinate = coordinates.get(i);
			                                        	cords[i][0] = coordinate.getLongitude();
		                                        		cords[i][1] = coordinate.getLatitude();
													}
			                                    }
			                                    
			                                    w.addCloud(time, new Cloud(cords));
			                                }
			                            }
			                        }
			                    }
			                }
	    	            }
	            	}
	            }
	        }
	    }
		
		return w;
	}
	
	public static void generateGraphKML(Graph<Arc> g) throws FileNotFoundException {
		Kml kml = new Kml();
		
		Document doc = kml.createAndSetDocument();
		doc.setName("Graph "+g.getName());
		
		Style style = doc.createAndAddStyle();
		style.setId("lineColor");
		LineStyle ls = style.createAndSetLineStyle();
		ls.setColor("#00000");
		ls.setWidth(1);
		
		for (Tuple2<Long,Arc> arc : g.adjacenyList) {
			LatLonPoint pointFrom = g.getLatLon(arc.a);
			LatLonPoint pointTo = g.getLatLon(arc.b.getHeadNode());
			
			Placemark pm = doc.createAndAddPlacemark();
			LineString ln = pm.createAndSetLineString();
			
			ln.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
			List<Coordinate> coordinates = new ArrayList<Coordinate>();
			coordinates.add(new Coordinate(pointFrom.lon, pointFrom.lat, 0));
			coordinates.add(new Coordinate(pointTo.lon, pointTo.lat, 0));
			ln.setCoordinates(coordinates);

		}
		
		kml.marshal(new File(g.getName()+".kml"));
	}
}
