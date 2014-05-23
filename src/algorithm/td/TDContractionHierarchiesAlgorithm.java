package algorithm.td;

import gnu.trove.map.hash.TLongIntHashMap;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import model.QEntry;
import model.TDArc;
import model.TDGraph;

import org.mapdb.Fun.Tuple2;

import util.ArrayUtils;
import algorithm.DijkstraAlgorithm;

public class TDContractionHierarchiesAlgorithm extends DijkstraAlgorithm<TDArc>  {

	private int numberOfShortcuts;
	private PQDijkstraAlgorithm psDijkstra;
	private Queue<QEntry> contractionOrder;
	private TLongIntHashMap nodesHierachy;
	
	public TDContractionHierarchiesAlgorithm(TDGraph graph) {
		super(graph);
		this.contractionOrder = new PriorityQueue<QEntry>();
		this.nodesHierachy = new TLongIntHashMap();
		this.numberOfShortcuts = 0;
		this.psDijkstra = new PQDijkstraAlgorithm(graph);
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
		int numOfContractions = this.contractionOrder.size();
		
		//contract the nodes in the nodes ordering
		System.out.println("Starting contracting "+numOfContractions+ " nodes");
		this.nodesHierachy =  contractNodes(numOfContractions, this.contractionOrder);
		
		//for all arcs u,v where order of v > u we set arcFlag to true else we set it to false (unreachable)
		System.out.println("Constructing upwards graph");
		constructUpwardsGraph(this.nodesHierachy);
		
		System.out.println("total #shortcust added: "+this.numberOfShortcuts);
		graph.setCH(true);
	}
	
	public int getNumberOfShortcuts() {
		return this.numberOfShortcuts;
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
			lazyUpdate(order);
			
			//increment total #shortcuts
			this.numberOfShortcuts += shortcuts;
		}
		
		return hierarchy;
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
	 * Compute the edge difference: #shortcuts - #edges 
	 * @param v
	 * @return
	 */
	public int computeEdgeDifference(long v) {
		//first set all the neighbors to false,i.e. to indicate unreachability from node v
		graph.disableNode(v);
				
		//calculate the #shortcuts without adding them
		int shortcuts = computeShortcuts(v, false);
		
		//set the node v to reachable again
		graph.enableNode(v);
		
		//compute ed		
		int edgeDifference = shortcuts - graph.getNumNeighborsNotDisabled(v);
		
		return edgeDifference;
	}
	
	/**
	 * compute the shortcuts that need to be added when removing this node
	 * @param v
	 * @param addShortcut
	 * @return
	 */
	public int computeShortcuts(long v, boolean addShortcut) {
		int shortcuts = 0;
		
		psDijkstra.considerArcFlags = true;
		psDijkstra.considerShortcuts = true;
		
		List<TDArc> neighbors = graph.getNeighborsNotDisabled(v);
		for (TDArc tdInArc : neighbors) {
			for (TDArc tdOutArc : neighbors) {
				long u = tdInArc.getHeadNode();
				long w = tdOutArc.getHeadNode();
				
				if(u != w) {
					
					System.out.println("****CHECKING IF SHORTCUT NEEDED BETWEEN "+u+" and "+w);
					
					//store the cost of visiting the node through node v for every departure time
					List<Integer> directCosts = ArrayUtils.linkLists(
							tdInArc.getCosts(), 
							tdOutArc.getCosts()
							);
					
					//compute the costs from u to w without v in the graph
					List<Integer> disabledCosts = ArrayUtils.toList(psDijkstra.computeTravelTimes(u, w));
					
					System.out.println("****DIRECT COSTS "+directCosts);
					System.out.println("****DISABLED COSTS "+disabledCosts);
					
					if(disabledCosts == null || ArrayUtils.listLarger(disabledCosts, directCosts)) { 
						System.out.println("****ADDED SHORTCUT BETWEEN "+u+" and "+w);
						/*no path could be found for any departure time or there was at least one departure time for which the was larger*/
						if(addShortcut)
							graph.addEdge(u, new TDArc(w, ArrayUtils.toIntArray(directCosts), true, v));						
						shortcuts++;
					}
				}
			}
		}
		return shortcuts;
	}
	
	/**
	 * Construct upwards graph based on the nodes hierarchy
	 * @param hierachy the hierarchy of the nodes
	 */
	public void constructUpwardsGraph(TLongIntHashMap hierarchy) {
		int count = 0;
		for (Tuple2<Long, TDArc> arc : graph.adjacenyList) {
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
	
	public int[] computeTravelTimes(long source, long target, int departureTime) {
		return null;
	}
}
