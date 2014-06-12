package algorithm.td;

import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import model.NodeEntry;
import model.QEntry;
import model.TDArc;
import model.TDGraph;

import org.mapdb.Fun.Tuple2;

import util.ArrayUtils;
import algorithm.DijkstraAlgorithm;

public class TDCHAlgorithm extends DijkstraAlgorithm<TDArc>  {

	private int numberOfShortcuts;
	private PQDijkstraAlgorithm psDijkstra;
	private TDDijkstraAlgorithm tdDijkstra;
	private TDDijkstraAlgorithm downwardTDDijkstra;
	private PIQDijkstraAlgorithm piqDijkstra;
	private Queue<QEntry> contractionOrder;
	private TLongIntHashMap nodesHierachy;
	private Set<Long> candidates;
	
	public TDCHAlgorithm(TDGraph graph) {
		super(graph);
		
		this.considerArcFlags = true;
		this.considerShortcuts = true;
		
		this.contractionOrder = new PriorityQueue<QEntry>();
		this.nodesHierachy = new TLongIntHashMap();
		this.numberOfShortcuts = 0;
		
		this.psDijkstra = new PQDijkstraAlgorithm(graph, this.considerArcFlags, this.considerShortcuts);
		this.tdDijkstra = new TDDijkstraAlgorithm(graph, this.considerArcFlags, this.considerShortcuts);
		this.downwardTDDijkstra = new TDDijkstraAlgorithm(graph, this.considerArcFlags, this.considerShortcuts);
		this.piqDijkstra = new PIQDijkstraAlgorithm(graph, this.considerArcFlags, this.considerShortcuts);
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
			System.out.println("CONTRACTING NODE: "+node);
			int shortcuts = contractSingleNode(node);
			System.out.println("------&&&&&&------ NEIGHBORS NOT DISABLED: "+graph.getNeighborsNotDisabled(3));
			//update the node ordering
			System.out.println("LAZY UPDATING "+node);
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
		
		List<TDArc> neighbors = graph.getNeighborsNotDisabled(v);
		System.out.println(v+ " NEIGHBORS NOT DISABLED: "+neighbors);
		for (TDArc tdInArc : neighbors) {
			for (TDArc tdOutArc : neighbors) {
				long u = tdInArc.getHeadNode();
				long w = tdOutArc.getHeadNode();
				
				if(u != w) {
					
					System.out.println("****CHECKING IF SHORTCUT NEEDED BETWEEN "+u+" and "+w);
					
					//store the cost of visiting the node through node v for every departure time
					List<Integer> costsThroughContractedNode = ArrayUtils.linkLists(
							tdInArc.getCosts(), 
							tdOutArc.getCosts()
							);
					
					//compute the costs from u to w without v in the graph
					List<Integer> witnessSearchCosts = ArrayUtils.toList(psDijkstra.computeTravelTimes(u, w));
					
					System.out.println("****Cost through contracted node "+costsThroughContractedNode);
					System.out.println("****Cost of witness search "+witnessSearchCosts);
					
					if(witnessSearchCosts != null && ArrayUtils.listLarger(costsThroughContractedNode, witnessSearchCosts))
						continue;
					else { 
						System.out.println("****ATEMPT TO ADD SHORTCUT BETWEEN "+u+" and "+w);
						/*no path could be found for any departure time 
						 *   or there was at least one departure time for which the  was larger*/
						if(addShortcut) {
							graph.addEdge(u, new TDArc(w, ArrayUtils.toIntArray(costsThroughContractedNode), true, v));
							System.out.println("****SHORTCUT WAS ADDED BETWEEN "+u+" and "+w);
							System.out.println(u+ " ARCS: "+ graph.getArcs(u, w));
						}
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
		//System.out.println("HIERARCHY: "+hierarchy);
		System.out.println("(3,5) ARCS: "+ graph.getArcs(3, 5));
		int count = 0;
		for (Tuple2<Long, TDArc> arc : graph.adjacenyList) {
			long u = arc.a;
			long v = arc.b.getHeadNode();
			
			
			if(hierarchy.get(v) > hierarchy.get(u))
				graph.setArcFlagForEdge(u, arc.b, true);			
			else
				graph.setArcFlagForEdge(u, arc.b, false);
			
			System.out.println("ARC: "+u+"("+hierarchy.get(u)+")"+" , "+v+"("+hierarchy.get(v)+") "+arc.b.isArcFlag());
			System.out.println("ARC: "+u+"("+hierarchy.get(u)+")"+" , "+v+"("+hierarchy.get(v)+") "+Arrays.toString(arc.b.getCosts()));
			
			count++;
			if(count%100000==0) System.out.println("#arcs processed: " + count);
		}
		System.out.println("(3,5) ARCS: "+ graph.getArcs(3, 5));
	}
	
	public int[] computeEarliestArrivalTimes(long source, long target,
			int minDepartureTime, int maxDepartureTime) {
		int[] travelTimes = new int[(maxDepartureTime-minDepartureTime)+1];
		
		for(int i=0; i < travelTimes.length;i=i+1)
			travelTimes[i] = this.computeEarliestArrivalTime(source, target, i+minDepartureTime);
		
		return travelTimes;
	}
	
	public int[] computeEarliestArrivalTimes(long source, long target) {
		return computeEarliestArrivalTimes(source, target, 0, 20);
	}
	
	public int computeEarliestArrivalTime(long source, long target, int departureTime) {
		if(source != NULL_NODE) {
			int B = Integer.MAX_VALUE; //upperbound
			candidates = new THashSet<Long>();
			
			tdDijkstra.startCost = departureTime;
			
			tdDijkstra.init(source);
			piqDijkstra.init(target);
			
			
			Queue<NodeEntry> queueS = tdDijkstra.queue;
			Queue<NodeEntry> queueT = piqDijkstra.queue;
			
			System.out.println("TDDIJKSTRA QUEUE " + queueS);
			System.out.println("PIQDIJKSTRA QUEUE " + queueT);
			int iterations = 0;
		
			while((!queueS.isEmpty() || !queueT.isEmpty()) 
					&& (Math.min(!queueS.isEmpty( )? queueS.peek().getDistance():Integer.MAX_VALUE, !queueT.isEmpty( )? queueT.peek().getDistance():Integer.MAX_VALUE) <= B)) {
				
				System.out.println("ITERATION " + iterations);
				boolean reverseDirection = (iterations%2 == 1);
				if((reverseDirection && queueT.isEmpty())
						|| (!reverseDirection && queueS.isEmpty()))
					reverseDirection = !reverseDirection;
				
				NodeEntry u = reverseDirection ? queueT.poll() : queueS.poll();
				
				Object retrievedCostU = tdDijkstra.f.get(u.getNodeId());
				int costU = Integer.MAX_VALUE;
				if(retrievedCostU != null) costU = (int) retrievedCostU;
				
				Tuple2<Integer, Integer> costIntervalU = piqDijkstra.f.get(u.getNodeId());
				if(costIntervalU == null) costIntervalU = new Tuple2<Integer, Integer>(Integer.MAX_VALUE, Integer.MAX_VALUE);
				
				System.out.println("MIN " + u.getNodeId() + " -> costs: "+costU + "," +costIntervalU.a + " B: " + B);
				//fix for double entries in the queues
				if(reverseDirection && costIntervalU.a < u.getDistance())
					continue;
				if(!reverseDirection && costU < u.getDistance())
					continue;
				
				B = (int) Math.min(B, (long)costU + (long)costIntervalU.b);
				
				if(B < Integer.MAX_VALUE 
						&& ((long)costU + (long)costIntervalU.a <= B))
					candidates.add(u.getNodeId());
				
				
				
				System.out.println("MIN B: "  + B);
				
				if(!reverseDirection) {
					for (TDArc arc : graph.getNeighbors(u.getNodeId())) {
						System.out.println("ARC: "+u.getNodeId() +"," + arc.getHeadNode()+" isArcFlag: "+arc.isArcFlag());
						if(considerArc(arc)) {
							System.out.println("arc: "+Arrays.toString(arc.getCosts()));
							tdDijkstra.relax(target, u.getNodeId(), costU, arc);
						}
					}
				}
				else {
					for (TDArc arc : graph.getNeighbors(u.getNodeId())) {
						System.out.println("ARC: "+u.getNodeId() +"," + arc.getHeadNode()+" isArcFlag: "+arc.isArcFlag());
//						TDArc rArc = graph.getArc(arc.getHeadNode(), u.getNodeId());
//						System.out.println("ARC: "+arc.getHeadNode() +"," + rArc.getHeadNode()+" isArcFlag: "+rArc.isArcFlag());
						if(considerArc(arc)) {
							System.out.println("CONSIDERING ARC ");
							//TDArc rArc = graph.getArc(arc.getHeadNode(), u.getNodeId());
							//arc.setCosts(rArc.getCosts());
							//System.out.println("arc: "+Arrays.toString(arc.getCosts()));
							//System.out.println("rArc: "+Arrays.toString(rArc.getCosts()));
//							TDArc rArc = graph.getArc(arc.getHeadNode(), u.getNodeId());
//							arc.setCosts(rArc.getCosts());
							piqDijkstra.relax(u.getNodeId(), costIntervalU, arc);
						}
					}
				}
				
//				for (TDArc arc : graph.getNeighbors(u.getNodeId())) {
//					System.out.println("ARC: "+u.getNodeId() +"," + arc.getHeadNode()+" isArcFlag: "+arc.isArcFlag());
//					if(considerArc(arc)) {
//						System.out.println("ARC "+u.getNodeId() +"," + arc.getHeadNode() + " " +ArrayUtils.toList(arc.getCosts()));
//						if (reverseDirection)
//							piqDijkstra.relax(u.getNodeId(), costIntervalU, arc);
//						else
//							tdDijkstra.relax(target, u.getNodeId(), costU, arc);
//					}
//				}
					
				System.out.println("FW QUEUE " + queueS);
				System.out.println("BW QUEUE " + queueT);
				
				iterations++;
			}
			
			System.out.println("FW SEARCH " + tdDijkstra.f);
			System.out.println("BW SEARCH " + piqDijkstra.f);
			System.out.println("CANDIDATES " + candidates);
			
			return downwardSearch(B, target);
		}
		
		return -1;
	}

	public int downwardSearch(int B, long target) {
		int eaTime = -1;
		
		downwardTDDijkstra.init(NULL_NODE);
		downwardTDDijkstra.previous = tdDijkstra.previous;
		
		for (Long u : candidates) {
			int costU = tdDijkstra.f.get(u);
			Tuple2<Integer, Integer> costIntervalU = piqDijkstra.f.get(u);
			if(costIntervalU == null) costIntervalU = new Tuple2<Integer, Integer>(Integer.MAX_VALUE, Integer.MAX_VALUE);
			
			if(B < Integer.MAX_VALUE && (costU+costIntervalU.a)<=B) {
				downwardTDDijkstra.f.put(u, costU);
				downwardTDDijkstra.queue.add(new NodeEntry(u, costU));
			}
		}
		
		System.out.println("DOWNWARD DIJKSTRA: "+downwardTDDijkstra.queue);
		
		while(!downwardTDDijkstra.queue.isEmpty()) {
			NodeEntry u = downwardTDDijkstra.queue.poll();
			System.out.println("DOWNWARD DIJKSTRA MIN "+u.getNodeId() + ", "+ u.getDistance());
			
			if(u.getDistance() >= Integer.MAX_VALUE)
				return -1;
			
			if(u.getNodeId() == target)
				return downwardTDDijkstra.f.get(target);
			
			int costU = downwardTDDijkstra.f.get(u.getNodeId());
			if(costU < u.getDistance())
				continue;
			
			Set<Long> p = piqDijkstra.p.get(u.getNodeId());
			System.out.println("P: "+p);
			if(p != null) {
				for (Long v : p) {
					List<TDArc> arcs = graph.getArcs(u.getNodeId(), v);
					for (TDArc arc : arcs) {
						if(!considerArc(arc)) {
//							TDArc rArc = graph.getArc(arc.getHeadNode(), u.getNodeId());
//							arc.setCosts(rArc.getCosts());
							System.out.println("RELAXING ARC "+Arrays.toString(arc.getCosts()));
//							System.out.println("RELAXING RARC "+Arrays.toString(rArc.getCosts()));
							downwardTDDijkstra.relax(target, u.getNodeId(), downwardTDDijkstra.f.get(u.getNodeId()), arc);
						}
					}
				}
			}
			System.out.println("DOWNWARD DIJKSTRA QUEUE: "+downwardTDDijkstra.queue);
		}
		
		return eaTime;
	}
}
