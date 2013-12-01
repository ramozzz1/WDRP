package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import model.Graph;
import model.Path;
import algorithm.AbstractRoutingAlgorithm;
import algorithm.DijkstraAlgorithm;



public class WDRP {
	String regionBoxSaarland = "49.20,49.25,6.95,7.05";
	String regionBoxBW = "47.95,48.05,7.75,7.90";
	
    public static void main(String[] args) throws IOException{
    	int port = 8888;
    	ServerSocket server = new ServerSocket(port);
    	BufferedReader in = null;
    	PrintWriter out = null;
    	
    	Graph graph = new Graph("resources/db/saarland.graph");
    	AbstractRoutingAlgorithm algorithm = new DijkstraAlgorithm(graph);
    	algorithm.precompute();
    	
    	int i = 0;
    	while(true) {
    		System.out.println("[" + (i++) + "]"
    				+ "Waiting for query on port " + port + "...");
    		Socket client = server.accept();
    		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    		
    		//Get the request line
    		String request = in.readLine();
    		System.out.println("Request string is \""
    				+ (request.length() < 99 ? request : request.substring(0,97) + "...")
    				+ "\"");
    		
    		int pos =  request.indexOf("&");
    		if(pos != -1) {
    			request = request.substring(6, pos);
    		}
    		
    		System.out.println("Raw request: " + request);
    		
    		String[] parts = request.split(",");
    		float sourceLat = Float.parseFloat(parts[0]);
    		float sourceLon = Float.parseFloat(parts[1]);
    		float targetLat = Float.parseFloat(parts[2]);
    		float targetLon = Float.parseFloat(parts[3]);
    		
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
    		System.out.println("#Visited nodes: " + algorithm.visitedNodesMarks.size());
    		System.out.println("Travel time: " +travelTime);
    		System.out.println("Path length: " + p.length());
    		
    		String jsonp = "redrawLineServerCallback({\n"
    				+ " \"travel_time\": " + travelTime + "," 
    				+ " \"path\":" + p.toJsonArray() +"\n"
    				+ "})\n";
    		
    		String answer = "HTTP/1.0 200 OK" + "\r\n"
    				+ "Content-Length: " + jsonp.length() + "\r\n"
    				+ "Content-Type: plain/text" + "\r\n"
    				+ "Connection: close" + "\r\n"
    				+ "\r\n" + jsonp;
    		
    		out = new PrintWriter(client.getOutputStream(),true);
    		out.write(answer);
    		out.flush();
    		client.close();
    	}
    	//GraphUtils.convertOSMToGraph("saarland");
    	
    	/*Graph g = new Graph("resources/db/saarland.graph");	
    	List<AbstractRoutingAlgorithm> algorithms = new ArrayList<AbstractRoutingAlgorithm>();
    	algorithms.add(new DijkstraAlgorithm(g));
    	algorithms.add(new AstarAlgorithm(g));
    	//algorithms.add(new ALTAlgorithm(g,42));    	
    	algorithms.add(new ArcFlagsAlgorithm(g,49.20,49.25,6.95,7.05));
    	Experiment.doExperiment(g, algorithms, 1, true);*/
    }   
}