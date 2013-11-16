package util;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.mapdb.BTreeMap;
import org.mapdb.Fun.Tuple2;

import model.Graph;
import model.LatLonPoint;
import model.NodePair;
import algorithm.DijkstraAlgorithm;

public class GraphUtils {
	
	//convert graph to largest connected component
	public static void convertToLCC(Graph g) {
		Set<Long> visitedNodes = new THashSet<Long>();
    	Set<Long> maxVisitedNodes = new THashSet<Long>();
    	int count = 0;
		for (Long id : g.nodes.keySet()) {
			if(count%100000==0) System.out.println(count);
			if (!visitedNodes.contains(id)) {
				DijkstraAlgorithm d = new DijkstraAlgorithm(g);
				d.computeShortestPath(id, -1);
				if(d.visitedNodesMarks.size() >  maxVisitedNodes.size())
					maxVisitedNodes = d.visitedNodesMarks;
				visitedNodes.addAll(d.visitedNodesMarks);
			}
			count++;
		}
		System.out.println("|V|:"+g.nodes.size()+" |E|:"+g.adjacenyList.size()/2);
		System.out.println("LCC: "+ maxVisitedNodes.size());
		count = 0;
		for (Long id : g.nodes.keySet()) {
			if(count%100000==0) System.out.println(count);
			if (!maxVisitedNodes.contains(id)) {
				g.removeNode(id);
				g.removeEdge(id);
			}
			count++;
		}
		System.out.println("|V|:"+g.nodes.size()+" |E|:"+g.adjacenyList.size()/2);
		g.closeConnection();
	}

	//select n node pairs from map
	public static List<NodePair> getRandomNodePairs(
			BTreeMap<Long, Tuple2<Double, Double>> nodes, int n) {
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

}
