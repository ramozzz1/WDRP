package algorithm;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import model.Arc;
import model.Graph;
import model.NodeEntry;
import model.Path;

public class DijkstraAlgorithm extends AbstractRoutingAlgorithm {
	
	public Map<Long,Long> previous;
	public THashMap<Long, Integer> distance;
	public boolean considerArcFlags;
	public boolean considerShortcuts;
	public int costUpperbound;
	public int maxNumSettledNodes;
	protected int startCost;
	
	public DijkstraAlgorithm(Graph graph) {
		super(graph);
		setDefaultSettings();
	}
	
	protected void setDefaultSettings() {
		this.considerArcFlags=false;
		this.considerShortcuts = false;
		this.costUpperbound=Integer.MAX_VALUE;
		this.maxNumSettledNodes=Integer.MAX_VALUE;
		this.startCost = 0;
	}
	
	@Override
	public int computeShortestPath(long sourceId, long targetId) {
		this.distance = new THashMap<Long, Integer>();
		this.previous = new THashMap<Long,Long>();
		this.visitedNodesMarks = new THashSet<Long>();
		
		if(sourceId != NULL_NODE) {
			distance.put(sourceId, this.startCost);
			previous.put(sourceId, NULL_NODE);
			
			Queue<NodeEntry> queue = new PriorityQueue<NodeEntry>();
			queue.add(new NodeEntry(sourceId, this.startCost));
			while(!queue.isEmpty()) {
				NodeEntry u = queue.poll();
				long minNodeId = u.getNodeId();
				//System.out.println("MIN NODE:"+minNodeId +", "+ u.getDistance());
				visitedNodesMarks.add(minNodeId);
				
				if(u.getDistance() >= Integer.MAX_VALUE)
					return -1;
				
				if(minNodeId == targetId || addionalStopCondition(minNodeId,targetId))
					return distance.get(minNodeId);
					
				if(u.getDistance() > costUpperbound)
					return -1;
				
				if(visitedNodesMarks.size() > maxNumSettledNodes)
					return -1;
				
				int h = getHeuristicValue(minNodeId,targetId);
				int distU = distance.get(minNodeId);
				if(distU+h < u.getDistance())
					continue;
				
				for (Arc e : graph.getNeighbors(minNodeId)) {
					if(considerEdge(e)) {
						Object distN = distance.get(e.getHeadNode());
						int dist = getEdgeCost(e, distU);
						//System.out.println("N: "+e.getHeadNode()+", "+dist);
						if(distN==null || dist < (int)distN) {
							distance.put(e.getHeadNode(), dist);
							previous.put(e.getHeadNode(), minNodeId);
							h = getHeuristicValue(e.getHeadNode(),targetId);
							queue.add(new NodeEntry(e.getHeadNode(), dist+h));
						}
					}
				}
			}
		}
		
		return -1;
	}
	
	protected int getEdgeCost(Arc e, int dist) {
		return e.getCost() + dist;
	}

	protected boolean addionalStopCondition(long sourceId, long targetId) {
		return false;
	}

	protected boolean considerEdge(Arc e) {
		if(!this.considerArcFlags || (this.considerArcFlags && e.isArcFlag())) {
			if(this.considerShortcuts || !e.isShortcut())
				return true;
		}
		return false;
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
		return contructPath(this.previous, nodeId);
	}

	@Override
	public void precompute() {}
	
	@Override
	public String getName() {
		return "Dijkstra";
	}

	@Override
	public Set<Long> getVisitedNodes() {
		return this.visitedNodesMarks;
	}
}
