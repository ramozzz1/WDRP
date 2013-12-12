package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import model.Arc;
import model.Graph;
import util.CommonUtils;

public class ContractionHierarchiesAlgorithm extends DijkstraAlgorithm {
	public enum HeuristicTypes {
		LAZY_PERIODIC, LAZY, PERIODIC;
	}
	
	private Queue<QEntry> nodesOrdering;
	private int maxNumberContractions;
	private int numberOfShortcuts;
	private HeuristicTypes heuristicType;
	
	public ContractionHierarchiesAlgorithm(Graph graph) {
		this(graph,Integer.MAX_VALUE, HeuristicTypes.LAZY);
	}
	
	public ContractionHierarchiesAlgorithm(Graph graph, HeuristicTypes heuristicType) {
		this(graph,Integer.MAX_VALUE, heuristicType);
	}
	
	public ContractionHierarchiesAlgorithm(Graph graph, int maxNumberContractions, HeuristicTypes heuristicType) {
		super(graph);
		this.nodesOrdering = new PriorityQueue<QEntry>();
		this.maxNumberContractions = maxNumberContractions;
		this.heuristicType = heuristicType;
		this.numberOfShortcuts = 0;
	}

	@Override
	public void precompute() {
		//set the arc flag for all edges to true
		System.out.println("Setting arc flags for all edges to true");
		graph.setArcFlagsForAllEdges(true); 
		
		//compute a random node ordering
		System.out.println("Computing node ordering");
		this.nodesOrdering = computeRandomNodeOrdering();
		
		//determine number of contractions
		int numOfContractions = Math.min(this.maxNumberContractions, this.nodesOrdering.size());
		
		//contract the nodes in the nodes ordering
		System.out.println("Starting contracting "+numOfContractions+ " nodes");
		for (int i = 0; i < numOfContractions; i++) {
			if(numOfContractions%100000==0)System.out.println("#nodes contracted: "+i);
			
			//get node with smallest ed
			long node = this.nodesOrdering.poll().getNodeId();
			
			//contract node
			int shortcuts = contractNode(node);
			
			//update the node ordering
			updateNodeOrdering(this.nodesOrdering);
			
			this.numberOfShortcuts += shortcuts;			
		}
	}
	
	public void updateNodeOrdering(Queue<QEntry> queue) {
		if(heuristicType == HeuristicTypes.LAZY) {
			lazyUpdate(queue);
		}
	}

	public void lazyUpdate(Queue<QEntry> queue) {
		//get the current node with the smallest ED
		QEntry entry = queue.peek();
		long currentMinNode = entry.getNodeId();
		
		//compute the ed again
		int newEd = computeEdgeDifference(currentMinNode);
		
		if(newEd > entry.getEd()) {
			//new ed is larger so this node is not the minimum anymore, add it again with the new ed
			queue.poll();
			queue.add(new QEntry(currentMinNode, newEd));
			
			//repeat again with
			lazyUpdate(queue);
		}
		else {
			//stop: the node with the smallest ed is still the smallest
			return;
		}
	}

	public Queue<QEntry> computeNodeOrdering() {
		Queue<QEntry> queue = new PriorityQueue<QEntry>();
		for (Long node : graph.nodes.keySet()) {
			int ed = computeEdgeDifference(node);
			queue.add(new QEntry(node, ed));
		}
		return queue;
	}
	
	/**
	 * Compute random node ordering
	 */
	public Queue<QEntry> computeRandomNodeOrdering() {
		Queue<QEntry> queue = new PriorityQueue<QEntry>();
		List<Long> randOrderList = CommonUtils.generateRandomOrder(new ArrayList<Long>(graph.nodes.keySet()));
		for (Long node : randOrderList) {
			queue.add(new QEntry(node, 0));
		}
		return queue;
	}
	
	/**
	 * Compute the edge difference: #shortcuts - #edges 
	 * @param v
	 * @return
	 */
	public int computeEdgeDifference(long v) {
		//calculate the #shortcuts without adding them
		int shortcuts = contractNode(v, false);
		
		//set the node v to reachable again
		Iterable<Arc> neighbors = graph.getNeighbors(v);
		for (Arc arc : neighbors)
			graph.setArcFlagForEdge(v, arc, true);
		
		//compute ed
		int edgeDifference = shortcuts - graph.getNumNeighbors(v);
		return edgeDifference;
	}
	
	public int contractNode(long v) {
		return contractNode(v,true);
	}
	
	/**
	 * contract the node and add shortcuts if necessary
	 * @param v the node to contract
	 * @param addShortcut if true the shortcuts are added to the graph else not 
	 * Return the #shortcuts added
	 */
	public int contractNode(long v, boolean addShortcut) {
		int shortcuts = 0;
		
		//first set all the neighbors to false,i.e. to indicate unreachablity from node v
		Iterable<Arc> neighbors = graph.getNeighbors(v);
		for (Arc arc : neighbors)
			graph.setArcFlagForEdge(v, arc, false);
		
		for (Arc inArc : neighbors) {
			for (Arc outArc : neighbors) {
				long u = inArc.getHeadNode();
				long w = outArc.getHeadNode();
				if(u != w) {
					//store the cost of visiting the node through node v
					int directCost = inArc.getCost() + outArc.getCost();
					//calculate the sp from node u to w while ignoring v
					super.considerArcFlags = true;
					super.maxNumSettledNodes = 20;
					super.costUpperbound = directCost;
					int spCost = super.computeShortestPath(u, w);
					if(spCost == -1 || spCost > directCost) { 
						/*no sp could be found or sp found which is longer than the direct one (i.e. the real sp)
						  so we have to add a shortcut*/
						if(addShortcut)
							graph.addEdge(u, w, directCost, true, v);
						shortcuts++;
					}
				}
			}
		}
		
		return shortcuts;
	}
	
	@Override
	public int computeShortestPath(long sourceId, long targetId) {
		//reset the default settings of dijkstra
		super.setDefaultSettings();
		return super.computeShortestPath(sourceId, targetId);
	}
	
	
	public int getNumberOfShortcuts() {
		return this.numberOfShortcuts;
	}
	
	@Override
	public String getName() {
		return "Contraction Hierachies";
	}
}

class QEntry implements Comparable<QEntry>
{	
	private long nodeId;
	private int ed;

	public QEntry(long nodeId, int ed) {
		this.nodeId = nodeId;
		this.ed = ed;
	}

	public long getNodeId() {
		return nodeId;
	}
	
	public long getEd() {
		return ed;
	}

	@Override
	public int compareTo(QEntry o) {
		return Integer.compare(this.ed, o.ed);
	}
}