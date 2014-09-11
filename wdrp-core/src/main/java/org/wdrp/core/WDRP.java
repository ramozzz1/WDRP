package org.wdrp.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.mapdb.Fun.Tuple2;
import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.wdrp.core.algorithm.AbstractRoutingAlgorithm;
import org.wdrp.core.algorithm.CHAlgorithm;
import org.wdrp.core.algorithm.DijkstraAlgorithm;
import org.wdrp.core.model.Arc;
import org.wdrp.core.model.Graph;
import org.wdrp.core.model.LatLonPoint;
import org.wdrp.core.model.Path;
import org.wdrp.core.model.Weather;
import org.wdrp.core.reader.OSMDownloader;
import org.wdrp.core.util.GraphUtils;
import org.wdrp.core.util.WeatherUtil;

import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Style;




public class WDRP {
	private static Graph<Arc> graph;
	private static Weather weather;
	private static List<AbstractRoutingAlgorithm<Arc>> algorithmsToRun;
	String regionBoxSaarland = "49.20,49.25,6.95,7.05";
	String regionBoxBW = "47.95,48.05,7.75,7.90";
	private static Connection connection;
	private static List<AbstractRoutingAlgorithm<Arc>> algorithms;
	static Logger logger = Logger.getLogger(WDRP.class);
	
	public static class WDRPHandler implements Container {

		@Override
		public void handle(Request request, Response response) {
			try {
				Query query = request.getQuery();
				String action = query.get("action");
				System.out.println(action);
				if(action != null) {
					String content = "Invalid request";
					if(action.equals("graph_bounds")) {
						content = "{\n";
						content += "\"bounds\":"+graph.getBounds().toJsonArray();
						content += "\n}";
					}
					else if(action.equals("select_graph")) {
						String graphName = query.get("graph_name");
						if(graphName!=null) {
							String graphPath = graphName;
							File f = new File(graphPath);
							if(f.exists() && !f.isDirectory()) {
								graph = new Graph<Arc>(graphPath);
								
								content = "{\n";
								content += "\"success\":"+true;
								content += "\n}";
							}
							else {
								content="No graph found for: "+graphPath;
							}
						}
						else {
							content="No graph_name specified";
						}
					}
					else if(action.equals("select_weather")) {
						String weatherName = query.get("weather_name");
						if(weatherName!=null) {
							String weatherPath = weatherName;
							File f = new File(weatherPath);
							if(f.exists() && !f.isDirectory()) {
								weather = new Weather(weatherPath);
								//if no tdgraph exist for graph and cloud combination convert the current graph to a tdgraph
								
								//if it does exist load it
								
								content = "{\n";
								content += "\"success\":"+true;
								content += "\n}";
							}
							else {
								content="No cloud found for: "+weatherPath;
							}
						}
						else {
							content="No cloud_name specified";
						}
					}
					else if(action.equals("select_algorithm")) {
						algorithmsToRun = new ArrayList<AbstractRoutingAlgorithm<Arc>>();
						List<String> selectedAlgorithms = Arrays.asList(query.get("algorithms").split(","));
						if(selectedAlgorithms != null && selectedAlgorithms.size() > 0) {
							for (int i = 0; i < algorithms.size(); i++) {
								AbstractRoutingAlgorithm<Arc> alg = algorithms.get(i);
								if(selectedAlgorithms.contains(alg.getName())) {
									alg.setGraph(graph);
									algorithmsToRun.add(alg);
									
									content = "{\n";
									content += "\"success\":"+true;
									content += "\n}";
								}
							}
						}
						else {
							content="No algorithms specified";
						}
					}
					else if(action.equals("get_maps")) {
						content = "{\n";
						content += "\"maps\": [";
						
						File folder = new File(Paths.get("").toAbsolutePath().toString());
						File[] listOfFiles = folder.listFiles();
						
						String prefix = "";
						for (int i = 0; i < listOfFiles.length; i++) {
							if (listOfFiles[i].isFile() && FilenameUtils.isExtension(listOfFiles[i].getName(), "graph")) {
								content += prefix 
										+ "{" + "\"fileName\":" + "\""+listOfFiles[i].getName()+"\""
										+"}";
								prefix = ",";
							}
						 }
						    
						content += "]\n}";
					}
					else if(action.equals("get_weathers")) {
						content = "{\n";
						content += "\"weathers\": [";
						
						File folder = new File(Paths.get("").toAbsolutePath().toString());
						File[] listOfFiles = folder.listFiles();
						
						String prefix = "";
						for (int i = 0; i < listOfFiles.length; i++) {
							if (listOfFiles[i].isFile() && FilenameUtils.isExtension(listOfFiles[i].getName(), "wea")) {
								content += prefix 
										+ "{" 
										+ "\"fileName\":" + "\""+listOfFiles[i].getName()+"\""
										+"}";
								prefix = ",";
							}
						 }
						    
						content += "]\n}";
					}
					else if(action.equals("get_algorithms")) {
						content = "{\n";
						content += "\"algorithms\": [";
						
						String prefix = "";
						for (int i = 0; i < algorithms.size(); i++) {
								content += prefix 
										+ "{" + "\"algorithmName\":" + "\""+algorithms.get(i).getName()+"\""
										+"}";
								prefix = ",";
						 }
						content += "]\n}";
					}
					else if(action.equals("edges")) {
						content = "{\n";
						
						content += "\"edges\": [";
						
						String prefix = "";
						int i =0;
						System.out.println("Getting edges...");
						for (Tuple2<Long,Arc> arc : graph.adjacenyList) {
							LatLonPoint nodeFrom = graph.getLatLon(arc.a);
							LatLonPoint nodeTo = graph.getLatLon(arc.b.getHeadNode());
							content += prefix 
									+ "{" + "\"nodeFrom\":" + nodeFrom.toJsonObject()
									+ "," + "\"nodeTo\":"   + nodeTo.toJsonObject()
									+ "," + "\"shortcut\":"   + arc.b.isShortcut()
									+"}";
							prefix = ",";
							i++;
							
							if(i==10000) break;
						}
						
						content += "]\n}";
					}
					else if(action.equals("route")) {
						String s = query.get("source");
						String t = query.get("target");
						System.out.println(s + " " + t);
						if(s != null && t != null) {
							String[] source = s.split(",");
							String[] target = t.split(",");
							if(source.length == 2 && target.length == 2) {
					    		float sourceLat = Float.parseFloat(source[0]);
					    		float sourceLon = Float.parseFloat(source[1]);
					    		float targetLat = Float.parseFloat(target[0]);
					    		float targetLon = Float.parseFloat(target[1]);
					    		
					    		System.out.println("source:" + sourceLat + ", " + sourceLon);
					    		System.out.println("target:" + targetLat + ", " + targetLon);
					    		
								long sourceId = graph.getClosestNode(sourceLat,sourceLon);
					    		long targetId = graph.getClosestNode(targetLat,targetLon); 
					    		
					    		System.out.println("Closest source:" + sourceId);
					    		System.out.println("Closest target:" + targetId);
					    		
					    		String prefix = "";
					    		content = "{\n";
								content += "\"paths\": [";
								
					    		for (int i = 0; i < algorithmsToRun.size(); i++) {
					    			AbstractRoutingAlgorithm<Arc> alg = algorithmsToRun.get(i);
					    			
					    			long start = System.currentTimeMillis();
									int travelTime = alg.computeShortestPath(sourceId, targetId);
						    		long end = System.currentTimeMillis() - start;
						    		Path p = alg.extractPath(targetId);
						    		System.out.println("Running algorithm: " + alg.getName());
						    		System.out.println("Path found within: " + end + "ms");
						    		System.out.println("#Visited nodes: " + alg.getVisitedNodes().size());
						    		System.out.println("Travel time: " +travelTime);
						    		System.out.println("Path length: " + p.length());
						    		
						    		content += prefix
						    				+ "{\n"
						    				+ " \"algorithm\": " + "\""+ alg.getName() + "\""+ ",\n"
						    				+ " \"travel_time\": " + travelTime + ",\n"
						    				+ " \"bounds\": " + p.getBounds().toJsonArray() + ",\n"
						    				+ " \"points\":" + p.toJsonArray()
						    				+ "\n}";
						    		prefix = ",";
								}
					    		
					    		content += "]\n}";
							}
							else {
								System.err.println("Wrong input source,target");
							}
						}
						else {
							System.err.println("Wrong input source,target");
						}
					}
					else {
						System.err.println("No action handler for: " +action);
					}
					
					PrintStream body = response.getPrintStream();
					long time = System.currentTimeMillis();
					response.setValue("Content-Type", "application/json");
					response.setValue("Server", "WDRPHandler/1.0 (Simple 4.0)");
					response.setValue("Access-Control-Allow-Origin", "*");
					response.setDate("Date", time);
					response.setDate("Last-Modified", time);
					body.println(content);
					body.close();
					
					System.out.println(content);
				}
			} catch(Exception e) {
				e.printStackTrace();
		    }
		}
		
	}
	
	public static void main(String[] args) throws IOException, URISyntaxException{
		
//		OSMDownloader.downloadOsmFromGeofabrik("andorra.osm","europe/andorra");
//		GraphUtils.convertOSMToGraph("andorra.osm", true);
//		Long startNode = 0l;
//		Long endNode = 0l;
//		String graphPath = "andorra.graph";
//		int numberOfTimes = 0;
//		String algs = "dijkstra,ch";
//		
//		computeShortestPath(graphPath,algs,numberOfTimes,startNode ,endNode);
//		
//		Graph<Arc> g = new Graph<Arc>("andorra.graph");
//		KMLUtil.generateGraphKML(g);
		
		//WeatherUtil.generateWeatherFromKML("test.kml", "test.wea");
		
		int port = 8888;
		setupAlgorithms();
		
		Container container = new WDRPHandler();
		Server server = new ContainerServer(container);
		connection = new SocketConnection(server);
		SocketAddress address = new InetSocketAddress(port);
		
		connection.connect(address);
    }

	private static void computeShortestPath(String graphPath, String algs,
			int numberOfTimes, Long startNode, Long endNode) {
		
	}

	private static void setupAlgorithms() {
		algorithms = Arrays.asList(
				new DijkstraAlgorithm<Arc>(),
				new CHAlgorithm()
				);
	}   
}