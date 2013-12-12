package algorithm;

import java.util.ArrayList;
import java.util.List;

import main.Histogram;
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
		System.out.println("Setting arc flags for all edges to true");
		graph.setArcFlagsForAllEdges(true); 
		
		//compute a random node ordering
		System.out.println("Computing random node ordering");
		this.nodesOrdering = computeRandomNodeOrdering();
		
		//determine number of contractions
		int numOfContractions = Math.min(this.maxNumberContractions, this.nodesOrdering.size());
		
		long totalTime = 0;
		Histogram shHistogram = new Histogram(4);
		Histogram edHistogram = new Histogram(3);
		//contract the nodes in the nodes ordering
		System.out.println("Starting contracting "+numOfContractions+ " nodes");
		for (int i = 0; i < numOfContractions; i++) {
			long node = this.nodesOrdering.get(i);
			System.out.println(i + " contracting node " + node);
			long start = System.nanoTime();
			int shortcuts = contractNode(node);
			totalTime += (System.nanoTime()-start)/1000;
			int ed = shortcuts - graph.getNumNeighbors(node);
			shHistogram.addDataPoint(shortcuts);
			edHistogram.addDataPoint(ed);
			this.numberOfShortcuts += shortcuts;
		}
		System.out.println("#contractions: " +numOfContractions);
		System.out.println("time (average): " +(totalTime/numOfContractions)+ "us");
		System.out.println("total shortcuts: " +this.numberOfShortcuts);
		System.out.println("shortcut histogram:");
		shHistogram.printHistogram();
		System.out.println("ED histogram:");
		edHistogram.printHistogram();
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
	 * Return the #shortcuts added
	 */
	public int contractNode(long v) {
		int shortcuts = 0;
		Iterable<Arc> neighbors = graph.getNeighbors(v);
		for (Arc arc : neighbors) {
			//first set all the neighbors to false,i.e. to indicate unreachablity from node v
			graph.setArcFlagForEdge(v, arc, false);
		}
		
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
