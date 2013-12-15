package algorithm;

import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongIntHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

import model.Arc;
import model.Graph;
import model.Path;

import org.mapdb.Fun.Tuple2;

import util.CommonUtils;

public class ContractionHierarchiesAlgorithm extends AbstractRoutingAlgorithm {
	private Queue<QEntry> contractionOrder;
	private TLongIntHashMap nodesHierachy;
	private HeuristicTypes heuristicType;
	private int maxNumberContractions;
	private int numberOfShortcuts;
	
	public ContractionHierarchiesAlgorithm(Graph graph) {
		this(graph,Integer.MAX_VALUE, HeuristicTypes.LAZY);
	}
	
	public ContractionHierarchiesAlgorithm(Graph graph, HeuristicTypes heuristicType) {
		this(graph,Integer.MAX_VALUE, heuristicType);
	}
	
	public ContractionHierarchiesAlgorithm(Graph graph, int maxNumberContractions, HeuristicTypes heuristicType) {
		super(graph);
		this.contractionOrder = new PriorityQueue<QEntry>();
		this.nodesHierachy = new TLongIntHashMap();
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
		System.out.println("Computing inital node ordering of contraction, based on edge difference");
		this.contractionOrder = computeEDNodeOrdering();
		
		//determine number of contractions
		int numOfContractions = Math.min(this.maxNumberContractions, this.contractionOrder.size());
		
		//contract the nodes in the nodes ordering
		System.out.println("Starting contracting "+numOfContractions+ " nodes");
		this.nodesHierachy =  contractNodes(numOfContractions, this.contractionOrder);
		
		//for all arcs u,v where order of v > u we set arcFlag to true else we set it to false (unreachable)
		System.out.println("Constructing upwards graph");
		constructUpwardsGraph(this.nodesHierachy);
		
		System.out.println("#shortcust added: "+this.numberOfShortcuts);
	}

	/**
	 * Construct upwards graph based on the nodes hierarchy
	 * @param hierachy the hierarchy of the nodes
	 */
	public void constructUpwardsGraph(TLongIntHashMap hierarchy) {
		int count = 0;
		for (Tuple2<Long, Arc> arc : graph.adjacenyList) {
			long u = arc.a;
			long v = arc.b.getHeadNode();
			
			if(hierarchy.get(v) > hierarchy.get(u))
				graph.setArcFlagForEdge(u, arc.b, true);			
			else
				graph.setArcFlagForEdge(u, arc.b, false);
			
			count++;
			if(count%100000==0) System.out.println("#arcs processed: " + count);
		}
	}

	/**
	 * @param numOfContractions the number of contractions to do
	 * @param order the initial order in which to contract (can be updated during contracting)
	 * @return the hierarchy in which the nodes where contracted
	 */
	public TLongIntHashMap contractNodes(int numOfContractions, Queue<QEntry> order) {
		TLongIntHashMap hierarchy = new TLongIntHashMap();
		for (int i = 0; i < numOfContractions; i++) {
			if(i%1000==0)System.out.println("#nodes contracted: "+i);
			
			//get node with smallest ed
			long node = order.poll().getNodeId();
			
			//store the level of this node
			hierarchy.put(node, i);
			
			//contract node
			int shortcuts = contractSingleNode(node);
			
			//update the node ordering
			updateNodeContractionOrdering(order);
			
			//increment total #shortcuts
			this.numberOfShortcuts += shortcuts;
		}
		
		return hierarchy;
	}
	
	public void updateNodeContractionOrdering(Queue<QEntry> queue) {
		if(heuristicType == HeuristicTypes.LAZY) {
			lazyUpdate(queue);
		}
	}

	public void lazyUpdate(Queue<QEntry> queue) {
		if(queue.size() > 0) {
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
	}

	/**
	 * Compute a node ordering based on the smallest ED first, largest last
	 * @return queue with the smallest ED order
	 */
	public Queue<QEntry> computeEDNodeOrdering() {
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
		//first set all the neighbors to false,i.e. to indicate unreachablity from node v
		graph.disableNode(v);
				
		//calculate the #shortcuts without adding them
		int shortcuts = computeShortcuts(v, false);
		
		//set the node v to reachable again
		graph.enableNode(v);
		
		//compute ed		
		int edgeDifference = shortcuts - graph.getNumNeighborsNotDisabled(v);
		return edgeDifference;
	}
	
	public int contractSingleNode(long v) {
		return contractSingleNode(v,true);
	}
	
	/**
	 * contract the node and add shortcuts if necessary
	 * @param v the node to contract
	 * @param addShortcut if true the shortcuts are added to the graph else not 
	 * Return the #shortcuts added
	 */
	public int contractSingleNode(long v, boolean addShortcut) { 		
		//first set all the neighbors to false,i.e. to indicate unreachablity from node v
		graph.disableNode(v);
		
		//now compute the number of shortcuts that need to be added
		int shortcuts = computeShortcuts(v, true);
		
		return shortcuts;
	}

	/**
	 * compute the shortcuts that need to be added when removing this node
	 * @param v
	 * @param addShortcut
	 * @return
	 */
	public int computeShortcuts(long v, boolean addShortcut) {
		int shortcuts = 0;
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(this.graph);
		dijkstra.considerArcFlags = true;
		dijkstra.considerShortcuts = true;
		
		List<Arc> neighbors = graph.getNeighborsNotDisabled(v);
		for (Arc inArc : neighbors) {
			for (Arc outArc : neighbors) {
				long u = inArc.getHeadNode();
				long w = outArc.getHeadNode();
				
				if(u != w) {
					//store the cost of visiting the node through node v
					int directCost = inArc.getCost() + outArc.getCost();
					//calculate the sp from node u to w while ignoring v
					dijkstra.costUpperbound = directCost;
					int spCost = dijkstra.computeShortestPath(u, w);
					//System.out.println("("+u+","+w+") " + spCost + " " +directCost);
					if(spCost == -1 || spCost > directCost) { 
						/*no sp could be found or sp found which is longer than the direct one (i.e. the real sp)
						  so we have to add a shortcut*/
						if(addShortcut)
							graph.addEdge(u, new Arc(w, directCost, true, v));						
						shortcuts++;
						//System.out.println("ADDED: ("+u+","+w+")");
					}
				}
			}
		}
		return shortcuts;
	}
	
	@Override
	public int computeShortestPath(long sourceId, long targetId) {
		//compute dijkstra one-to-all from source
		DijkstraAlgorithm dijkstraSource = new DijkstraAlgorithm(this.graph);
		dijkstraSource.considerArcFlags = true;
		dijkstraSource.considerShortcuts = true;
		dijkstraSource.computeShortestPath(sourceId, -1);
		THashMap<Long, Integer> distSource = dijkstraSource.distance;
		
		//compute dijkstra one-to-all from target
		DijkstraAlgorithm dijkstraTarget = new DijkstraAlgorithm(this.graph);
		dijkstraTarget.considerArcFlags = true;
		dijkstraTarget.considerShortcuts = true;
		dijkstraTarget.computeShortestPath(targetId, -1);
		THashMap<Long, Integer> distTarget = dijkstraTarget.distance;
		
		int min = Integer.MAX_VALUE;
		for (Entry<Long, Integer> n : distSource.entrySet()) {
			if(distTarget.contains(n.getKey()))
				min = Math.min(min, (n.getValue() + distTarget.get(n.getKey())));
		}
		
		if(min == Integer.MAX_VALUE) min = -1;
		
		return min;
	}
	
	@Override
	public Path extractPath(long targetId) {
		Path path = new Path();
		
		/*if(previous.containsKey(nodeId)) {
			long prevNode = previous.get(nodeId);
			do {
				LatLonPoint p = graph.getNode(nodeId);
				path.addNode(new Node(nodeId,p.lat,p.lon), graph.getEdge(prevNode, nodeId));
				nodeId = prevNode;
				prevNode = previous.get(nodeId);
			} while(nodeId != NULL_NODE);
		}*/
			
		return path;
	}
	
	public int getNumberOfShortcuts() {
		return this.numberOfShortcuts;
	}
	
	@Override
	public String getName() {
		return "Contraction Hierachies";
	}
	
	/**
	 * Enums for update heuristics
	 * @author zakaria
	 *
	 */
	public enum HeuristicTypes {
		LAZY_PERIODIC, LAZY, PERIODIC;
	}
	
	/**
	 * Custom PriorityQueue entry for sorting the nodes based on edge difference
	 * @author zakaria
	 *
	 */
	public class QEntry implements Comparable<QEntry>
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
		
		@Override
		public String toString() {
			return "{"+nodeId+","+ed+"}";
		}
	}
	
}