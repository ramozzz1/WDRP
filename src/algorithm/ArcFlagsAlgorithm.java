package algorithm;

import gnu.trove.procedure.TLongLongProcedure;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Arc;
import model.Graph;
import model.LatLonPoint;

import org.mapdb.Fun.Tuple2;

public class ArcFlagsAlgorithm extends DijkstraAlgorithm {

	public double latMin;
	public double latMax;
	public double lonMin;
	public double lonMax;
	public THashSet<Long> nodesInRegion;
	private int numberOfArcFlags;
	
	public ArcFlagsAlgorithm(Graph graph, double latMin, double latMax, double lonMin, double lonMax) {
		super(graph);
		setRegion(latMin, latMax, lonMin, lonMax);
		numberOfArcFlags=0;
	}
	
	public void setRegion(double latMin, double latMax, double lonMin, double lonMax) {
		this.latMin = latMin;
		this.latMax = latMax;
		this.lonMin = lonMin;
		this.lonMax = lonMax;
	}
	
	@Override
	public void precompute() {
		//get all the nodes of the region
		nodesInRegion = computeNodesInRegion(latMin, latMax, lonMin, lonMax);
		
		System.out.println("#nodesInRegion: " +nodesInRegion.size());
		
		//get the boundaryNodes of a given region 
		THashSet<Long> boundaryNodes = computeBoundaryNodes(nodesInRegion); 
		
		System.out.println("#boundaryNodes: " +boundaryNodes.size());
		
		//compute the correct arc flags for the boundary nodes and region nodes
		computeArcFlags(boundaryNodes, nodesInRegion);
		
		System.out.println("#Arc flags: " + numberOfArcFlags);
	}
	
	public void computeArcFlags(THashSet<Long> boundaryNodes, THashSet<Long> regionNodes) {
		computeArcFlagsForRegionNodes(regionNodes);
		computeArcFlagsForBoundaryNodes(boundaryNodes);
	}
	
	public List<Arc> getArcFlags() {
		List<Arc> arcFlags = new ArrayList<Arc>();
		for (Tuple2<Long, Arc> e : graph.adjacenyList) {
			Arc a = e.b;
			if(a.isArcFlag()) {
				arcFlags.add(a);
			}
		}
		return arcFlags;
	}

	private void computeArcFlagsForBoundaryNodes(THashSet<Long> boundaryNodes) {
		int count=0;
		for (Long node : boundaryNodes) {
			count++;
			System.out.println(count+" computing All-SP for node: " + node);
			
			//first compute the shortest path from this node to all other nodes
			super.computeShortestPath(node, -1);
			
			System.out.println("Updating visisted arc flags");
			
			//check which arcs were visited and set their flags to true
			super.previous.forEachEntry(new TLongLongProcedure() {
				@Override
				public boolean execute(long currNode, long prevNode) {
					//get the reverse edge
					Arc oldArc = graph.getEdge(currNode, prevNode);
					if(oldArc!=null)
						updateArcFlag(currNode, oldArc);
					return true;
				}
			});
		}
	}

	private void computeArcFlagsForRegionNodes(THashSet<Long> regionNodes) {
		for (Long node : regionNodes) {
			for (Arc a : graph.getNeighbors(node)) {
				if(regionNodes.contains(a.getHeadNode()))
					updateArcFlag(node, a);
			}
		}
	}

	public THashSet<Long> computeBoundaryNodes(THashSet<Long> nodesInRegion) {
		THashSet<Long> boundaryNodes = new THashSet<Long>();
		for (Long node : nodesInRegion) {
			for (Arc a : graph.getNeighbors(node)) {
				Long headNode = a.getHeadNode();
				if(!nodesInRegion.contains(headNode)) {
					//headNode not in same region so node is a boundary node
					boundaryNodes.add(node);
				}
			}
		}
		return boundaryNodes;
	}
	
	/**
	 * @param currNode
	 * @param oldArc
	 */
	private void updateArcFlag(long currNode, Arc oldArc) {
		//get the edge
		if(oldArc!= null) {
			//check if arc flag already set to true
			if(!oldArc.isArcFlag()) {
				//create new arc with arcFlag set to true
				Arc newArc = new Arc(oldArc);
				newArc.setArcFlag(true);
				
				//remove the edge
				graph.removeEdge(currNode,oldArc);
				
				//add the new edge
				graph.addEdge(currNode, newArc);
				
				numberOfArcFlags++;
			}
		}
	}

	/**
	 * Get all the nodes of a region 
	 * @param latMin
	 * @param latMax
	 * @param lonMin
	 * @param lonMax
	 * @return
	 */
	public THashSet<Long> computeNodesInRegion(double latMin, double latMax, double lonMin, double lonMax) {
		THashSet<Long> nodes = new THashSet<Long>();
		for (Map.Entry<Long, LatLonPoint> n : graph.nodes.entrySet()) {
			LatLonPoint p = n.getValue();
			if(p.lat <= latMax && p.lat>=latMin 
					&& p.lon <= lonMax && p.lon >= lonMin) {
				//node is within the region
				nodes.add(n.getKey());
			}
		}
		return nodes;
	}

	@Override
	public int computeShortestPath(long sourceId, long targetId) {
		//compute the shorest path using dijkstra where arc flags are considered
		super.considerArcFlags = true;
		return super.computeShortestPath(sourceId, targetId);
	}
	
	@Override
	public String getName() {
		return "Arc Flags";
	}
}
