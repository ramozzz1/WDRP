package algorithm;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import model.Arc;
import model.Graph;
import model.HeuristicTypes;
import model.LatLonPoint;
import model.PPDist;
import model.Path;
import storage.DBHashMap;
import util.DistanceUtils;

public class TransitNodeRoutingAlgorithm extends AbstractRoutingAlgorithm<Arc> {
	public ContractionHierarchiesAlgorithm ch;
	private Set<Long> transitNodes;
	public DBHashMap<Long,Integer> radiusNodes;
	private int numTransitNodes;
	private Long minSourceAccessNode;
	private Long minTargetAccessNode;
	private HeuristicTypes heuristic;
	private Map<Long, HashMap<Long, PPDist>> transitNodesDistances;
	private Map<Long, HashMap<Long, PPDist>> accessNodes;
	private Map<Long,Long> ppSource;
	private Map<Long,Long> ppAccess;
	private Map<Long,Long> ppTarget;
	private DijkstraAlgorithm<Arc> dijkstra;
	private boolean usedDijkstra;

	public TransitNodeRoutingAlgorithm(Graph<Arc> graph) {
		this(graph,HeuristicTypes.LATLON_DISTANCE);
	}
	
	public TransitNodeRoutingAlgorithm(Graph<Arc> graph, HeuristicTypes heuristic) {
		super(graph);
		this.ch = new ContractionHierarchiesAlgorithm(graph);
		this.numTransitNodes = (int) Math.round(Math.sqrt(graph.nodes.size())); 
		this.transitNodes = new THashSet<Long>();
		this.radiusNodes = new DBHashMap<Long,Integer>(graph.isTemp()?"":getName()+"/"+graph.getName()+"-"+"radiusNodes");
		this.transitNodesDistances = new DBHashMap<Long, HashMap<Long, PPDist>>(graph.isTemp()?"":getName()+"/"+graph.getName()+"-"+"transitNodes");
		this.accessNodes = new DBHashMap<Long, HashMap<Long, PPDist>>(graph.isTemp()?"":getName()+"/"+graph.getName()+"-"+"accessNodes");
		this.heuristic = heuristic;
	}
	
	@Override
	public void precompute() {
		//compute the set of transit nodes using CH precomp
		if(!alreadyPrecomputed()) {
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
				HashMap<Long, PPDist> accessNodesDistances =  computeOneToAllDistances(n, an);
				
				//store the access nodes for this node
				accessNodes.put(n, accessNodesDistances);
				
				totalNumberAccessNodes += an.size();
				count++;
				if(count%100==0) System.out.println("#nodes processed: "+count);
			}
			
			//compute the distances/paths between the transit nodes and store them
			System.out.println("Computing the distances/paths between the transit nodes ("+this.transitNodes.size()+") and store them");
			this.transitNodesDistances.putAll(computeAllToAllDistances(this.transitNodes, this.transitNodes));
			
			System.out.println("Total #transit nodes: "+this.numTransitNodes);
			System.out.println("Total #access nodes: "+totalNumberAccessNodes);
			System.out.println("Avg #access nodes: "+totalNumberAccessNodes/graph.getNumNodes());
		}
	}

	private boolean alreadyPrecomputed() {
		return this.accessNodes.size() > 0 && this.transitNodesDistances.size() > 0 && graph.isCH();
	}

	@Override
	public int computeShortestPath(long sourceId, long targetId) {
		usedDijkstra = false;
		//if node is far use access nodes
		if(isFar(sourceId, targetId, this.radiusNodes)) {
			//get the access nodes nodes from the source
			HashMap<Long, PPDist> sourceAccessNodesDistances =  accessNodes.get(sourceId);
			
			//get the access nodes nodes from the taget
			HashMap<Long, PPDist> targetAccessNodesDistances = accessNodes.get(targetId);
			
			int minDist = Integer.MAX_VALUE;
			this.minSourceAccessNode = NULL_NODE;
			this.minTargetAccessNode = NULL_NODE;
			this.ppSource = new HashMap<Long,Long>();
			this.ppAccess = new HashMap<Long,Long>();
			this.ppTarget = new HashMap<Long,Long>();
			
			for (Entry<Long, PPDist> x : sourceAccessNodesDistances.entrySet()) {
				for (Entry<Long, PPDist> y : targetAccessNodesDistances.entrySet()) {
					PPDist accessDist = transitNodesDistances.get(x.getKey()).get(y.getKey());
					int distSX = x.getValue().dist;
					int distXY = accessDist.dist;
					int distYT = y.getValue().dist;
					
					int dist = distSX + distXY + distYT;
					
					if(dist < minDist) {
						minDist = dist;
						this.ppSource = x.getValue().previous;
						this.ppAccess = accessDist.previous;
						this.ppTarget = y.getValue().previous;
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
			usedDijkstra = true;
			//node is close, so just use a quick dijkstra
			this.dijkstra = new DijkstraAlgorithm<Arc>(graph);
			int dist = dijkstra.computeShortestPath(sourceId, targetId);
			return dist;
		}
	}

	public HashMap<Long, PPDist> computeOneToAllDistances(long source, Set<Long> set) {
		HashMap<Long, PPDist> oneToAllDistances = new HashMap<Long, PPDist>();
		
		this.ch.computeSPSource(source);
		for (Long node : set) {
			this.ch.computeSPTarget(node);
			int dist = this.ch.computeMinDist();
			Map<Long,Long> pp = this.ch.getParentPointers();
			oneToAllDistances.put(node, new PPDist(pp, dist));
		}
		
		return oneToAllDistances;
	}
	
	public Map<Long, HashMap<Long, PPDist>> computeAllToAllDistances(Set<Long> setA, Set<Long> setB) {
		THashMap<Long, HashMap<Long, PPDist>> allToAllDistances = new THashMap<Long, HashMap<Long, PPDist>>();
		int count = 0;
		for (Long nodeA : setA) {
			HashMap<Long, PPDist> distanceMap = computeOneToAllDistances(nodeA, setB);
			allToAllDistances.put(nodeA, distanceMap);
			
			count++;
			System.out.println("#transit nodes processed: "+count);
		}
		
		return allToAllDistances;
	}

	public boolean isFar(long sourceId, long targetId, Map<Long,Integer> radiusNodes) {
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
		Map<Long,Long> pp = this.ch.getParentPointersSource();
		
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
		if(!usedDijkstra) {
			//contruct path from the source to the minimum source access node
			Path sourcePath = contructPath(this.ppSource, this.minSourceAccessNode);
			
			//contruct path from the minimum source access node to the minimum target access node
			Path accessPath = contructPath(this.ppAccess, this.minTargetAccessNode);
			
			//contruct path from the minimum target access node to the target
			Path targetPath = contructPath(this.ppTarget, targetId);
			
			sourcePath.connect(accessPath);
			sourcePath.connect(targetPath);
			
			return sourcePath;
		}
		else {
			return dijkstra.extractPath(targetId);
		}
	}

	@Override
	public Set<Long> getVisitedNodes() {
		return this.visitedNodesMarks;
	}
}
