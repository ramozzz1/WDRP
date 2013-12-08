package algorithm;

import java.util.ArrayList;
import java.util.List;

import model.Arc;
import model.Graph;
import util.CommonUtils;

public class ContractionHierarchiesAlgorithm extends DijkstraAlgorithm {

	private List<Long> nodesOrdering;
	private int maxNumberContractions;
	private int numberOfShortcuts;
	
	public ContractionHierarchiesAlgorithm(Graph graph) {
		this(graph,Integer.MAX_VALUE);
	}
	
	public ContractionHierarchiesAlgorithm(Graph graph, int maxNumberContractions) {
		super(graph);
		this.nodesOrdering = new ArrayList<Long>();
		this.maxNumberContractions = maxNumberContractions;
		this.numberOfShortcuts = 0;
	}

	@Override
	public void precompute() {
		//set the arc flag for all edges to true
		graph.setArcFlagsForAllEdges(true); 
		
		//compute a random node ordering
		this.nodesOrdering = computeRandomNodeOrdering();
		
		//determine number of contractions
		int numOfContractions = Math.min(this.maxNumberContractions, this.nodesOrdering.size());
		//contract the nodes in the nodes ordering
		for (int i = 0; i < numOfContractions; i++) {
			long  node = this.nodesOrdering.get(i);
			contractNode(node);
		}
	}
	
	/**
	 * Compute random node ordering
	 */
	public List<Long> computeRandomNodeOrdering() {
		return CommonUtils.generateRandomOrder(new ArrayList<Long>(graph.nodes.keySet()));
	}
	
	/**
	 * contract the node and add shortcuts if necessary
	 * @param v
	 */
	public void contractNode(long v) {
		System.out.println("Contracting node " + v);
		Iterable<Arc> neighbors = graph.getNeighbors(v);
		for (Arc arc : neighbors) {
			//first set all the neighbors to false,i.e. to indicate unreachablity from node v
			graph.setArcFlagForEdge(v, arc, false);
			System.out.println(v + " " + arc);
		}
		
		for (Arc inArc : neighbors) {
			for (Arc outArc : neighbors) {
				long u = inArc.getHeadNode();
				long w = outArc.getHeadNode();
				if(u != w) {
					//store the cost of visiting the node through node v
					int directCost = inArc.getCost() + outArc.getCost();
					System.out.println(u+ "->" +v + "->" + w + " direct cost: "+directCost);
					//calculate the sp from node u to w while ignoring v
					super.considerArcFlags = true;
					super.maxNumSettledNodes = 20;
					super.costUpperbound = directCost;
					int spCost = super.computeShortestPath(u, w);
					System.out.println(u+ "->" + w + " sp cost (without v): "+spCost);
					if(spCost == -1 || spCost > directCost) { 
						/*no sp could be found or sp found which is longer than the direct one (i.e. the real sp)
						  so we have to add a shortcut*/
						System.out.println("Adding shortcut "+ u + "->" + w + " with cost: " + directCost);
						graph.addEdge(u, w, directCost, true);
						this.numberOfShortcuts++;
					}
					else {
						System.out.println("No shortcut added "+ u + "->" + w + " sp without v: " + spCost);
					}
					//System.out.println(u + " " + w + " dc: " + directCost + " sp: " + spCost);
				}
			}
		}
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
