package org.wdrp.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mapdb.Fun.Tuple2;
import org.wdrp.core.model.Arc;
import org.wdrp.core.model.Graph;
import org.wdrp.core.model.LatLonPoint;

import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.LineStyle;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Style;

public class KMLUtil {
	
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
