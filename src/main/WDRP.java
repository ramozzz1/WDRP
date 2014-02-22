package main;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import model.Graph;
import model.Path;

import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import util.GraphUtils;
import algorithm.AbstractRoutingAlgorithm;
import algorithm.ContractionHierarchiesAlgorithm;
import algorithm.DijkstraAlgorithm;



public class WDRP {
	private static Graph graph;
	private static AbstractRoutingAlgorithm algorithm;
	String regionBoxSaarland = "49.20,49.25,6.95,7.05";
	String regionBoxBW = "47.95,48.05,7.75,7.90";
	
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
					    		
					    		long start = System.currentTimeMillis();
					    		int travelTime = algorithm.computeShortestPath(sourceId, targetId);
					    		long end = System.currentTimeMillis() - start;
					    		Path p = algorithm.extractPath(targetId);
					    		System.out.println("Path found within: " + end + "ms");
					    		System.out.println("#Visited nodes: " + algorithm.getVisitedNodes().size());
					    		System.out.println("Travel time: " +travelTime);
					    		System.out.println("Path length: " + p.length());
					    		
					    		content = "{\n"
					    				+ " \"travel_time\": " + travelTime + ",\n"
					    				+ " \"bounds\": " + p.getBounds().toJsonArray() + ",\n"
					    				+ " \"path\":" + p.toJsonArray()
					    				+ "\n}";
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
				}
			} catch(Exception e) {
				e.printStackTrace();
		    }
		}
		
	}
	
	public static void main(String[] args) throws IOException{
		/*int port = 8888;
		
		graph = new Graph("resources/db/saarland.graph");
    	algorithm = new TransitNodeRoutingAlgorithm(graph);
    	
		Container container = new WDRPHandler();
		Server server = new ContainerServer(container);
		Connection connection = new SocketConnection(server);
		SocketAddress address = new InetSocketAddress(port);
		
  		connection.connect(address);*/
		
		Graph g = GraphUtils.convertOSMToGraph("saarland");
		//Graph g = GraphUtils.convertGTFSToGraph("manhattan");
		//Graph g = new Graph("resources/db/manhattan.graph");
  		List<AbstractRoutingAlgorithm> algorithms = new ArrayList<AbstractRoutingAlgorithm>();
    	//algorithms.add(new DijkstraAlgorithm(g));
    	//algorithms.add(new TimeExpandedDijkstraAlgorithm(g));
    	algorithms.add(new ContractionHierarchiesAlgorithm(g));
  		//algorithms.add(new AstarAlgorithm(g));
    	//algorithms.add(new ALTAlgorithm(g,16));    	
    	//algorithms.add(new ArcFlagsAlgorithm(g,47.95,48.05,7.75,7.90));
    	Experiment.doExperiment(g, algorithms, 100, false, false, "06:00:00", "18:00:00");
    }   
}