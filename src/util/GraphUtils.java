package util;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import model.Graph;
import model.LatLonPoint;
import model.NodePair;
import model.TNGraph;

import org.mapdb.BTreeMap;

import reader.GTFSParser;
import reader.OSMParser;
import algorithm.DijkstraAlgorithm;

public class GraphUtils {
	
	public static TNGraph convertGTFSToGraph (String dirName) {		
		IOUtils.deleteFile("resources/db/"+dirName+".graph");
		
		GTFSParser parser = new GTFSParser("resources/gtfs/"+dirName, "wednesday");
		TNGraph g = parser.gtfsToGraph();
		
		return g;
	}
	
	public static Graph convertOSMToGraph (String fileName) {		
		IOUtils.deleteFile("resources/db/"+fileName+".graph");
		
		OSMParser parser = new OSMParser();
		Graph g = parser.osmToGraph("resources/osm/"+fileName+".osm");
		GraphUtils.convertToLCC(g);
		
		return g;
	}
	
	//convert graph to largest connected component
	public static void convertToLCC(Graph g) {
		System.out.println("Converting Graph to LCC Graph");
		Set<Long> visitedNodes = new THashSet<Long>();
    	Set<Long> maxVisitedNodes = new THashSet<Long>();
    	int count = 0;
		for (Long id : g.nodes.keySet()) {
			if(count%100000==0) System.out.println("#nodes processed: "+count);
			if (!visitedNodes.contains(id)) {
				DijkstraAlgorithm d = new DijkstraAlgorithm(g);
				d.computeShortestPath(id, -1);
				if(d.getVisitedNodes().size() >  maxVisitedNodes.size())
					maxVisitedNodes = d.getVisitedNodes();
				visitedNodes.addAll(d.getVisitedNodes());
			}
			count++;
		}
		
		System.out.println("|V|:"+g.nodes.size()+" |E|:"+g.adjacenyList.size()/2);
		System.out.println("LCC: "+ maxVisitedNodes.size());
		
		System.out.println("Removing nodes/edges not part of LCC");
		count = 0;
		int removedNodes = 0;
		for (Long id : g.nodes.keySet()) {
			if(count%100000==0) System.out.println("#nodes processed: "+count + " #nodes removed: " +removedNodes);
			if (!maxVisitedNodes.contains(id)) {
				g.removeNode(id);
				g.removeAllEdgesOfNode(id);
				removedNodes++;
			}
			count++;
		}
		
		System.out.println("Graph converted to LCC Graph");
		System.out.println("|V|:"+g.nodes.size()+" |E|:"+g.adjacenyList.size()/2);
		System.out.println("Total #nodes removed: " + removedNodes);
	}

	public static Long getRandomNode(
			List<Long> nodes) {
		Random r = new Random();
		return nodes.get(r .nextInt(nodes.size()));
	}
		
	//select n node pairs from map
	public static List<NodePair> getRandomNodePairs(
			BTreeMap<Long, LatLonPoint> nodes, int n) {
		List<NodePair> np = new ArrayList<NodePair>();
		List<Long> keys = new ArrayList<Long>(nodes.keySet());
		Random r = new Random();
		for (int i = 0; i < n; i++)
			np.add(new NodePair(keys.get(r .nextInt(keys.size())),keys.get(r.nextInt(keys.size()))));
		/*List<Long> nodeIds = CommonUtils.getRandomKeys(nodes, n*2);
		for (int i = 0; i < nodeIds.size(); i=i+2)
			np.add(new NodePair(nodeIds.get(i),nodeIds.get(i+1)));*/
		return np;
	}

	public static List<NodePair> getRandomNodePairs(
			BTreeMap<Long, LatLonPoint> nodes, List<Long> regionNodes,
			int n) {
		List<NodePair> np = new ArrayList<NodePair>();
		List<Long> keys = new ArrayList<Long>(nodes.keySet());
		Random r = new Random();
		for (int i = 0; i < n; i++)
			np.add(new NodePair(keys.get(r .nextInt(keys.size())),regionNodes.get(r.nextInt(regionNodes.size()))));
		return np;
	}

}
