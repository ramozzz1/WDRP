package algorithm;

import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.set.hash.THashSet;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import model.Graph;
import model.HeuristicTypes;
import model.LatLonPoint;
import model.Path;
import storage.DBHashMap;
import util.DistanceUtils;

public class TransitNodeRoutingAlgorithm extends AbstractRoutingAlgorithm {
	public ContractionHierarchiesAlgorithm ch;
	private Set<Long> transitNodes;
	public TLongIntHashMap radiusNodes;
	private int numTransitNodes;
	private Long minSourceAccessNode;
	private Long minTargetAccessNode;
	private HeuristicTypes heuristic;
	private Map<Long, THashMap<Long, PPDist>> transitNodesDistances;
	private Map<Long, THashMap<Long, PPDist>> accessNodes;

	public TransitNodeRoutingAlgorithm(Graph graph) {
		this(graph,HeuristicTypes.LATLON_DISTANCE);
	}
	
	public TransitNodeRoutingAlgorithm(Graph graph, HeuristicTypes heuristic) {
		super(graph);
		this.ch = new ContractionHierarchiesAlgorithm(graph);
		this.numTransitNodes = (int) Math.round(Math.sqrt(graph.nodes.size())); 
		this.transitNodes = new THashSet<Long>();
		this.radiusNodes = new TLongIntHashMap();
		this.transitNodesDistances = new DBHashMap<Long, THashMap<Long, PPDist>>();
		this.accessNodes = new DBHashMap<Long, THashMap<Long, PPDist>>();
		this.heuristic = heuristic;
	}
	
	@Override
	public void precompute() {
		//compute the set of transit nodes using CH precomp
		System.out.println("Computing the set of transit nodes using CH precomp");
		this.transitNodes = computeTransitNodes(this.numTransitNodes);
		
		//for each node compute the set of access nodes with distances/paths and store them
		System.out.println("Compute the access nodes");
		int totalNumberAccessNodes = 0;
		int count = 0;
		for (Long n : graph.nodes.keySet()) {
			//compute the access nodes nodes from this node
			Set<Long> an = computeAccessNodes(n, this.transitNodes);
			
			//compute the distance/parent pointers for this node and access nodes
			THashMap<Long, PPDist> accessNodesDistances =  computeOneToAllDistances(n, an);
			
			//store the access nodes for this node
			accessNodes.put(n, accessNodesDistances);
			
			totalNumberAccessNodes += an.size();
			count++;
			if(count%100==0) System.out.println("#nodes processed: "+count);
		}
		
		//compute the distances/paths between the transit nodes and store them
		System.out.println("Computing the distances/paths between the transit nodes ("+this.transitNodes.size()+") and store them");
		this.transitNodesDistances = computeAllToAllDistances(this.transitNodes, this.transitNodes);
		
		System.out.println("Total #transit nodes: "+this.numTransitNodes);
		System.out.println("Total #access nodes: "+totalNumberAccessNodes);
		System.out.println("Avg #access nodes: "+totalNumberAccessNodes/graph.getNumNodes());
	}

	@Override
	public int computeShortestPath(long sourceId, long targetId) {
		
		//if node is far use access nodes
		if(isFar(sourceId, targetId, this.radiusNodes)) {
			//get the access nodes nodes from the source
			THashMap<Long, PPDist> sourceAccessNodesDistances =  accessNodes.get(sourceId);
			
			//get the access nodes nodes from the taget
			THashMap<Long, PPDist> targetAccessNodesDistances = accessNodes.get(targetId);
			
			int minDist = Integer.MAX_VALUE;
			this.minSourceAccessNode = NULL_NODE;
			this.minTargetAccessNode = NULL_NODE;
			for (Entry<Long, PPDist> x : sourceAccessNodesDistances.entrySet()) {
				for (Entry<Long, PPDist> y : targetAccessNodesDistances.entrySet()) {
					int distSX = x.getValue().dist;
					int distXY = transitNodesDistances.get(x.getKey()).get(y.getKey()).dist;
					int distYT = y.getValue().dist;
					
					int dist = distSX + distXY + distYT;
					
					if(dist < minDist) {
						minDist = dist;
						this.minSourceAccessNode = x.getKey();
						this.minTargetAccessNode = y.getKey();
						
						this.visitedNodesMarks = new THashSet<Long>(sourceAccessNodesDistances.keySet());
						this.visitedNodesMarks.addAll(targetAccessNodesDistances.keySet());
					}
				}
			}
			
			if(minDist == Integer.MAX_VALUE) minDist = -1;
			
			return minDist;
		}
		else {
			//node is close, so just use a quick dijkstra
			DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
			int dist = dijkstra.computeShortestPath(sourceId, targetId);
			return dist;
		}
	}

	public THashMap<Long, PPDist> computeOneToAllDistances(long source, Set<Long> set) {
		THashMap<Long, PPDist> oneToAllDistances = new THashMap<Long, PPDist>();
		
		this.ch.computeSPSource(source);
		for (Long node : set) {
			this.ch.computeSPTarget(node);
			int dist = this.ch.computeMinDist();
			TLongLongHashMap pp = this.ch.getParentPointers();
			oneToAllDistances.put(node, new PPDist(pp, dist));
		}
		
		return oneToAllDistances;
	}
	
	public THashMap<Long, THashMap<Long, PPDist>> computeAllToAllDistances(Set<Long> setA, Set<Long> setB) {
		THashMap<Long, THashMap<Long, PPDist>> allToAllDistances = new THashMap<Long, THashMap<Long, PPDist>>();
		int count = 0;
		for (Long nodeA : setA) {
			THashMap<Long, PPDist> distanceMap = computeOneToAllDistances(nodeA, setB);
			allToAllDistances.put(nodeA, distanceMap);
			
			count++;
			System.out.println("#transit nodes processed: "+count);
		}
		
		return allToAllDistances;
	}

	public boolean isFar(long sourceId, long targetId, TLongIntHashMap radiusNodes) {
		LatLonPoint sp = this.graph.getLatLon(sourceId);
		LatLonPoint tp = this.graph.getLatLon(targetId);
		int dist = computeDistance(sp,tp);
		
		if(dist > radiusNodes.get(sourceId))
			return true;
		else
			return false;
	}

	/**
	 * Compute the set of transit nodes of this graph using CH precomputation step, and then selecting the last nTransitNodes 
	 * nodes as the transit nodes
	 * @param nTransitNodes
	 * @return
	 */
	public Set<Long> computeTransitNodes(int nTransitNodes) {
		THashSet<Long> tn = new THashSet<Long>();
		
		//do the precomputation of CH algorithm
		System.out.println("Starting precomputation of CH algorithm");
		this.ch.precompute();
		
		//after the precomp of CH get the last numTransitNodes contracted; these are the transit nodes
		System.out.println("Select the transit nodes based on CH precomp");
		List<Long> order = this.ch.getContractionOrder();
		for (int i = order.size()-1; i >= order.size()-nTransitNodes; i--)
			tn.add(order.get(i));
		
		return tn;
	}
	
	/**
	 * Compute the set of access nodes of node with id nodeId, using a one-to-all search in 
	 * the upward graph (precomputed by CH)  
	 * @param nodeId
	 * @param tn
	 * @return
	 */
	public Set<Long> computeAccessNodes(long nodeId, Set<Long> tn) {
		//get the latlon point needed for computing the maximum radius
		LatLonPoint nodePoint = this.graph.getLatLon(nodeId);
		
		//set of access nodes
		Set<Long> accessNodes = new THashSet<Long>();
		
		//maximal radius
		int maxRadius = 0;
		
		//Compute one-to-all path from this node in the upward graph
		this.ch.computeSPSource(nodeId);
		
		//Get the parent pointers of all the settled nodes from the source
		TLongLongHashMap pp = this.ch.getParentPointersSource();
		
		//keep a checkmark set to keep up with nodes already visited
		Set<Long> nodesCheckmark = new THashSet<Long>();
		
		//for each settled node v, compute the first node x, which is in transit nodes set, on SP(u, v) 
		for (Long v : this.ch.getVisitedNodesSource()) { 
			if(!nodesCheckmark.contains(v)) {
				long firstAccessNodeInPath = NULL_NODE;
				
				//add the first node which is in transit nodes set to the access nodes of this node
				long currNode = v;
				while(currNode != NULL_NODE) {
					//add to checkmark set
					nodesCheckmark.add(currNode);
					
					//check if node is transit node
					if(tn.contains(currNode))
						firstAccessNodeInPath = currNode;
					
					currNode = pp.get(currNode);
				}
				
				//no access node added, this means no transit node used for reaching v 
				if(firstAccessNodeInPath==NULL_NODE) {
					LatLonPoint vPoint = this.graph.getLatLon(v);
					int vDistance = computeDistance(nodePoint, vPoint);
					maxRadius = Math.max(maxRadius, vDistance);
				}
				else {
					//access node was found, add the first found in the SP
					accessNodes.add(firstAccessNodeInPath);
				}
			}
		}
		
		//save the maximum radius found for this node
		this.radiusNodes.put(nodeId, maxRadius);
		
		return accessNodes;
	}

	/**
	 * @param nodePoint
	 * @param vPoint
	 * @return
	 */
	private int computeDistance(LatLonPoint nodePoint, LatLonPoint vPoint) {
		int vDistance = 0;
		if(heuristic == HeuristicTypes.EUCLIDEAN_DISTANCE)
			vDistance = (int)Math.ceil(DistanceUtils.euclideanDistance(nodePoint.lat, nodePoint.lon, vPoint.lat, vPoint.lon));
		else
			vDistance = (int)Math.ceil(DistanceUtils.latlonDistance(nodePoint.lat, nodePoint.lon, vPoint.lat, vPoint.lon));
		return vDistance;
	}
	
	@Override
	public String getName() {
		return "Transit Node Routing";
	}

	@Override
	public Path extractPath(long targetId) {
//		//contruct path from source to the minimum common node [s->...->c]
//		Path sourcePath = contructPath(this.previousSource, this.minCommonNode);
//		
//		//contruct path from target to the minimum common node [c->...->t]
//		Path targetPath = contructPath(this.previousTarget, this.minCommonNode).reversePath();
//		
//		//connect the source path and target path
//		sourcePath.connect(targetPath);
//		
//		//return the connected path
//		return sourcePath;
		return null;
	}

	@Override
	public Set<Long> getVisitedNodes() {
		return this.visitedNodesMarks;
	}
	
	/**
	 * Helper class for storing distance and parent pointers pairs
	 *
	 */
	public class PPDist {
		public TLongLongHashMap previous;
		public int dist;
		
		public PPDist(TLongLongHashMap previous, int dist) {
			this.previous = previous;
			this.dist = dist;
		}
		
		@Override
		public String toString() {
			return "{"+this.dist+", "+this.previous.toString()+"}";
		}

	}
}
