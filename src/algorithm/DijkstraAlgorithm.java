package algorithm;

import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.set.hash.THashSet;

import java.util.PriorityQueue;
import java.util.Queue;

import model.Arc;
import model.Graph;
import model.NodeEntry;

public class DijkstraAlgorithm extends AbstractRoutingAlgorithm {
	
	private TLongLongHashMap previous;
	public THashMap<Long, Integer> distance;
	
	public DijkstraAlgorithm(Graph graph) {
		super(graph);
	}
	
	@Override
	public int computeShortestPath(long sourceId, long targetId) {
		this.distance = new THashMap<Long, Integer>();
		this.previous = new TLongLongHashMap();
		this.visitedNodesMarks = new THashSet<Long>();
		
		distance.put(sourceId, 0);
		previous.put(sourceId, -1L);
		
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
			
			for (Arc e : graph.getNeighbors(u.getNodeId()) ) {
				//System.out.println(u.getNodeId()+" NEIGHBOR:"+e.getHeadNode() +" COST "+e.getCost());
				int dist = distU + e.getCost();
				if(!distance.containsKey(e.getHeadNode()) || dist < distance.get(e.getHeadNode())) {
					distance.put(e.getHeadNode(), dist);
					//previous.put(e.getHeadNode(), u.getNodeId());
					h = getHeuristicValue(e.getHeadNode(),targetId);
					queue.add(new NodeEntry(e.getHeadNode(), dist+h));
					//System.out.println(u.getNodeId()+" UPDATE "+e.getHeadNode()+" WITH "+(dist+h));
				}
			}
		}
		
		return -1;
	}

	public int getHeuristicValue(long nodeId, long targetId) {
		return 0;
	}

	@Override
	public void precompute() {}
	
	@Override
	public String getName() {
		return "Dijkstra";
	}
}
