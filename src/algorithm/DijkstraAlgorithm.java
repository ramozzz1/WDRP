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

public class DijkstraAlgorithm<K extends Arc> extends AbstractRoutingAlgorithm<K> {
	
	public Map<Long,Long> previous;
	public THashMap<Long, Integer> f;
	public Queue<NodeEntry> queue;
	public boolean considerArcFlags;
	public boolean considerShortcuts;
	public int costUpperbound;
	public int maxNumSettledNodes;
	public int startCost;
	
	public DijkstraAlgorithm(Graph<K> graph) {
		super(graph);
		setDefaultSettings();
	}
	
	public DijkstraAlgorithm(Graph<K> graph, boolean considerArcFlags, boolean considerShortcuts) {
		this(graph);
		this.considerArcFlags=considerArcFlags;
		this.considerShortcuts = considerShortcuts;
	}
	
	protected void setDefaultSettings() {
		this.considerArcFlags=false;
		this.considerShortcuts = false;
		this.costUpperbound=Integer.MAX_VALUE;
		this.maxNumSettledNodes=Integer.MAX_VALUE;
		this.startCost = 0;
	}
	
	public void init(long source) {
		this.f = new THashMap<Long, Integer>();
		this.previous = new THashMap<Long,Long>();
		this.visitedNodesMarks = new THashSet<Long>();
		this.queue = new PriorityQueue<NodeEntry>();
		
		if(source != NULL_NODE) {
			f.put(source, this.startCost);
			previous.put(source, NULL_NODE);
			queue.add(new NodeEntry(source, this.startCost));
		}
	}
	
	@Override
	public int computeShortestPath(long source, long target) {
		if(source != NULL_NODE) {
			
			init(source);
			
			while(!queue.isEmpty()) {
				NodeEntry u = queue.poll();
				long minNodeId = u.getNodeId();
				System.out.println("MIN NODE:"+minNodeId +", "+ u.getDistance());
				visitedNodesMarks.add(minNodeId);
				
				if(u.getDistance() >= Integer.MAX_VALUE)
					return -1;
				
				if(minNodeId == target || addionalStopCondition(minNodeId,target))
					return f.get(minNodeId);
					
				if(u.getDistance() > costUpperbound)
					return -1;
				
				if(visitedNodesMarks.size() > maxNumSettledNodes)
					return -1;
				
				int h = getHeuristicValue(minNodeId,target);
				int distU = f.get(minNodeId);
				if(distU+h < u.getDistance())
					continue;
				
				for (K arc : graph.getNeighbors(minNodeId)) {
					System.out.println("NEIGHBOR:"+minNodeId +", "+ arc.getHeadNode());
					if(considerArc(arc))
						relax(target, minNodeId, distU, arc);
				}
			}
		}
		
		return -1;
	}

	public void relax(long target, long u, int distU, K arc) {
		Object distN = f.get(arc.getHeadNode());
		int dist = getEdgeCost(arc, distU);
		if(distN==null || dist < (int)distN) {
			System.out.println("UPDATED "+arc.getHeadNode() + " to "+dist);
			f.put(arc.getHeadNode(), dist);
			previous.put(arc.getHeadNode(), u);
			int h = getHeuristicValue(arc.getHeadNode(),target);
			queue.add(new NodeEntry(arc.getHeadNode(), dist+h));
		}
	}
	
	protected int getEdgeCost(K e, int dist) {
		return e.getCost() + dist;
	}

	protected boolean addionalStopCondition(long sourceId, long targetId) {
		return false;
	}

	public boolean considerArc(K e) {
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
