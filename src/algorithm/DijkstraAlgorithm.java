package algorithm;

import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import model.Arc;
import model.Graph;
import model.LatLonPoint;
import model.Node;
import model.NodeEntry;
import model.Path;

public class DijkstraAlgorithm extends AbstractRoutingAlgorithm {
	
	private static final long NULL_NODE = -1L;
	public TLongLongHashMap previous;
	public THashMap<Long, Integer> distance;
	public boolean considerArcFlags;
	
	public DijkstraAlgorithm(Graph graph) {
		super(graph);
		this.considerArcFlags=false;
	}
	
	@Override
	public int computeShortestPath(long sourceId, long targetId) {
		this.distance = new THashMap<Long, Integer>();
		this.previous = new TLongLongHashMap();
		this.visitedNodesMarks = new THashSet<Long>();
		
		distance.put(sourceId, 0);
		previous.put(sourceId, NULL_NODE);
		
		Queue<NodeEntry> queue = new PriorityQueue<NodeEntry>();
		queue.add(new NodeEntry(sourceId, 0));
		while(!queue.isEmpty()) {
			NodeEntry u = queue.poll();
			
			visitedNodesMarks.add(u.getNodeId());
			
			if(u.getNodeId() == targetId)
				return distance.get(targetId);
			
			int h = getHeuristicValue(u.getNodeId(),targetId);
			int distU = distance.get(u.getNodeId());
			if(distU+h < u.getDistance())
				continue;
			
			//System.out.println("MIN:"+u.getNodeId());
			for (Arc e : graph.getNeighbors(u.getNodeId())) {
				if(!this.considerArcFlags || (this.considerArcFlags && e.isArcFlag())) { 
					//System.out.println(u.getNodeId()+" NEIGHBOR:"+e.getHeadNode() +" COST "+e.getCost());
					int dist = distU + e.getCost();
					if(!distance.containsKey(e.getHeadNode()) || dist < distance.get(e.getHeadNode())) {
						distance.put(e.getHeadNode(), dist);
						previous.put(e.getHeadNode(), u.getNodeId());
						h = getHeuristicValue(e.getHeadNode(),targetId);
						queue.add(new NodeEntry(e.getHeadNode(), dist+h));
						//System.out.println(u.getNodeId()+" UPDATE "+e.getHeadNode()+" WITH "+(dist+h));
					}
				}
			}
		}
		
		return -1;
	}

	public int getHeuristicValue(long nodeId, long targetId) {
		return 0;
	}
	
	public List<Path> extractPathAllPath() {
		List<Path> paths = new ArrayList<Path>();
		for (Long node : graph.nodes.keySet()) {
			paths.add(extractPath(node));
		}
		return paths;
	}
	
	@Override
	public Path extractPath(long nodeId) {
		Path path = new Path();
		
		if(previous.containsKey(nodeId)) {
			long prevNode = previous.get(nodeId);
			do {
				LatLonPoint p = graph.getNode(nodeId);
				path.addNode(new Node(nodeId,p.lat,p.lon), graph.getEdge(prevNode, nodeId));
				nodeId = prevNode;
				prevNode = previous.get(nodeId);
			} while(nodeId != NULL_NODE);
		}
			
		return path;
	}

	@Override
	public void precompute() {}
	
	@Override
	public String getName() {
		return "Dijkstra";
	}
}
