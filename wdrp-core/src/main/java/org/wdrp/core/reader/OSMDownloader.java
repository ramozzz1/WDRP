package org.wdrp.core.reader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.utils.URIBuilder;
import org.wdrp.core.util.DownloadUtil;

public class OSMDownloader {
	
	private static final String OSM_MAP_API = "http://api.openstreetmap.org/api/0.6/map";
	private static final String GEOFABRIK_API = "http://download.geofabrik.de";
	
	public static void downloadOsmUsingBbox(String fileName, double minLat,
			double minLng, double maxLat, double maxLng) throws URISyntaxException, IOException {
			URIBuilder builder = new URIBuilder(OSM_MAP_API)
									.addParameter("bbox", minLat+","+minLng+","+maxLat+","+maxLng);
			URL url = new URL(builder.toString());
			DownloadUtil.downloadAndSaveFromUrl(url, ensureOSMExtension(fileName), false);
	}

	public static void downloadOsmFromGeofabrik(String fileName,
			String geofrabrikPath) throws IOException, URISyntaxException {
		URIBuilder builder = new URIBuilder(GEOFABRIK_API)
							.setPath("/"+geofrabrikPath+"-latest"+".osm.bz2");
		URL url = new URL(builder.toString());
		DownloadUtil.downloadAndSaveFromUrl(url, ensureOSMExtension(fileName), true);
	}
	
	private static String ensureOSMExtension(String fileName) {
		if(!FilenameUtils.isExtension(fileName, "osm"))
			return fileName += ".osm";
		else
			return fileName;
	}
}