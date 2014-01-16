package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.Bind;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Serializer;

import util.DistanceUtils;
import model.LatLonPointSerializer;

public class Graph {
	private DB db;
	private int numNodes;
	private int numEdges;
	
	public BTreeMap<Long,LatLonPoint> nodes;
	public NavigableSet<Fun.Tuple2<Long,Arc>> adjacenyList;
	private NavigableSet<Bounds> bounds;
	
	public Graph() {
		this("temp", true);
	}
	
	public Graph(String fileName) {
		this(fileName, false);
	}
	
	public Graph(String fileName, boolean temp) {
		if(!temp) {
			this.db = DBMaker
					.newFileDB(new File(fileName))
					.transactionDisable()
					.cacheHardRefEnable()
					.syncOnCommitDisable()
					.asyncWriteEnable()
					.mmapFileEnableIfSupported()
					.freeSpaceReclaimQ(0)
					.fullChunkAllocationEnable()
					.closeOnJvmShutdown()
					.make();
		}
		else {
			this.db = DBMaker.newTempFileDB().transactionDisable().make();
		}
		
		Serializer<LatLonPoint> serializer = new LatLonPointSerializer();
		this.nodes = db.createTreeMap("nodes").keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_LONG).valueSerializer(serializer).counterEnable().makeOrGet();
		this.adjacenyList = db.createTreeSet("adjacenyList").serializer(BTreeKeySerializer.TUPLE2).counterEnable().makeOrGet();
		this.bounds = db.createTreeSet("bound").counterEnable().makeOrGet();
		if(this.bounds.size() == 0) {
			this.bounds.add(new Bounds());
		}
		
		this.numNodes = this.nodes.size();
		this.numEdges = this.adjacenyList.size();
	}
	
	public void closeConnection() {
		db.close();
	}
	
	public Node getNode(long nodeId) {
		return new Node(nodeId,nodes.get(nodeId));
	}
	
	public LatLonPoint getLatLon(long nodeId) {
		return nodes.get(nodeId);
	}
	
	/**
	 * Gets edge with sourc=souceId and target=targetId
	 * @param sourceId
	 * @param targetId
	 * @return the edge (sourceId,targetId) if present else returns null 
	 */
	public Arc getEdge(long sourceId, long targetId) {
		for (Arc a : getNeighbors(sourceId)) {
			if(a.getHeadNode() == targetId) {
				return a;
			}
		}
		return null;
	}
	
	public void addNode(long nodeId) {
		addNode(nodeId,0.0,0.0);
	}
	
	public void addNode(long nodeId, double lat, double lon) {
		nodes.put(nodeId, new LatLonPoint(lat, lon));
		extendBounds(lat,lon);
		this.numNodes++;
	}
	
	public void extendBounds(double lat, double lon) {
		Bounds b = this.bounds.pollFirst();
		b.updateBounds(lat, lon);
		this.bounds.add(b);
	}

	public void addEdge(long sourceId, long targetId, int cost) {
		addEdge(sourceId,targetId,cost,false,-1);
	}
	
	public void addEdge(long sourceId, long targetId, int cost, boolean arcFlag, long shortcutNode) {
		addEdge(sourceId,new Arc(targetId,cost,arcFlag,shortcutNode));
		addEdge(targetId,new Arc(sourceId,cost,arcFlag,shortcutNode));
	}
	
	public void addEdge(long sourceId, Arc a) {
		this.adjacenyList.add(Fun.t2(sourceId,a));
		this.numEdges++;
	}
	
	/**
	 * Set the arc flags for all edges
	 * @param arcFlag
	 */
	public void setArcFlagsForAllEdges(boolean arcFlag) {
		Iterator<Tuple2<Long,Arc>> itr = this.adjacenyList.iterator();
		while(itr.hasNext()) {
			Tuple2<Long,Arc> arc = itr.next();
			setArcFlagForEdge(arc.a, arc.b, arcFlag);
		}
	}
	/**
	 * Sets the arc flag for an edge
	 * @param currNode
	 * @param oldArc
	 * @param arcFlag
	 */
	public void setArcFlagForEdge(long currNode, Arc oldArc, boolean arcFlag) {
		//make copy of arc
		Arc newArc = new Arc(oldArc);
		newArc.setArcFlag(arcFlag);
		
		//remove the edge
		removeEdge(currNode,oldArc);
		
		//add the new edge
		addEdge(currNode, newArc);
	}
	
	public int getNumNodes() {
		return this.numNodes;
	}
	
	public int getNumEdges() {
		return this.numEdges;
	}

	public Iterable<Arc> getNeighbors(long nodeId) {
		return Fun.filter(this.adjacenyList, nodeId);
	}
	
	@SuppressWarnings("unused")
	public int getNumNeighbors(long nodeId) {
		int count = 0;
		for(Arc a : getNeighbors(nodeId)) { count++;}
		return count;
	}
	
	public Bounds getBounds() {
		return this.bounds.first();
	}
	
	/**
	 * Gets the closest node to the given lat lon points
	 * @param lat
	 * @param lon
	 * @return
	 */
	public long getClosestNode(double lat, double lon) {
		long minNode = -1;
		double minDist = Integer.MAX_VALUE;
		for (Map.Entry<Long, LatLonPoint> e : this.nodes.entrySet()) {
			LatLonPoint p = e.getValue();
			double dist = DistanceUtils.latlonDistance(p.lat, p.lon, lat, lon);
			if(dist < minDist) {
				minNode = e.getKey();
				minDist = dist;
			}
		}
		return minNode;
	}
	
	/**
	 * disable node, i.e. set the arc flags of the 
	 *    incoming edges of this node to false 
	 * @param n
	 */
	public void disableNode(long n) {
		for (Arc a : getNeighbors(n)) {
			setArcFlagForEdge(a.getHeadNode(),a.reverseEdge(n), false);
		}
	}
	
	/**
	 * enable node, i.e. set the arc flags of the 
	 *    incoming edges of this node to true 
	 * @param n the node id
	 */
	public void enableNode(long n) {
		for (Arc a : getNeighbors(n)) {
			setArcFlagForEdge(a.getHeadNode(),a.reverseEdge(n), true);
		}
	}
	
	/**
	 * Get arcs that are outgoing to this node but not disabled
	 * @param n the node id
	 * @return
	 */
	public List<Arc> getNeighborsNotDisabled(long n) {
		List<Arc> nonDisabledArcs = new ArrayList<Arc>(); 
		for (Arc a : getNeighbors(n)) {
			if(a.isArcFlag())
				nonDisabledArcs.add(a);
		}
		return nonDisabledArcs;
	}
	
	/**
	 * Get number of neighbors not disabled
	 * @param n the node id
	 * @return
	 */
	public int getNumNeighborsNotDisabled(long n) {
		int count = 0;
		for (Arc a : getNeighbors(n)) {
			if(a.isArcFlag())
				count++;
		}
		return count;
	}
	
	/**
	 * Get the arcs that are not disabled, i.e. the arcs that have an arc flag which is true
	 * @return
	 */
	public List<Arc> getArcsNotDisabled() {
		List<Arc> arcs = new ArrayList<Arc>();
		for (Tuple2<Long,Arc> arc : this.adjacenyList) {
			if(arc.b.isArcFlag())
				arcs.add(arc.b);
		}
		return arcs;
	}

	public void removeNode(long nodeId) {
		this.nodes.remove(nodeId);
		this.numNodes--;
	}
	
	/**
	 * Removes all edges of a node
	 * @param nodeId
	 */
	public void removeAllEdgesOfNode(long nodeId) {
		for (Arc a : getNeighbors(nodeId)) {
			this.adjacenyList.remove(Fun.t2(nodeId, a));
			this.numEdges--;
		}
	}
	
	/**
	 * Removes the an Arc of a node
	 * @param sourceId
	 * @param arc
	 */
	public void removeEdge(long sourceId, Arc arc) {
		this.adjacenyList.remove(Fun.t2(sourceId, arc));
	}
	
	/**
	 * Removes the Arc with source=sourceId and target=targetId
	 * @param sourceId
	 * @param targetId
	 */
	public void removeEdge(long sourceId, long targetId) {
		removeEdge(sourceId, getEdge(sourceId, targetId));
	}
	
	public void clear() {
		this.nodes.clear();
		this.adjacenyList.clear();
		this.numEdges=0;
		this.numNodes=0;
		closeConnection();
	}
	
	@Override
	public String toString() {
		String s = "{"+getNumNodes()+", "+getNumEdges()+", ";
		s += "[";
		
		String arrayS = "";
		Iterator<Tuple2<Long,Arc>> itr = this.adjacenyList.descendingIterator();
		while(itr.hasNext()) {
			Tuple2<Long,Arc> element = itr.next();
			arrayS += "("+element.a+","+element.b.getHeadNode()+","+element.b.getCost()+"), ";
	      }
		
		if(arrayS.contains(","))
			arrayS = new StringBuilder(arrayS).replace(arrayS.lastIndexOf(","), arrayS.lastIndexOf(",")+2, "").toString();
		
		s += arrayS+"]";
		s += "}";
		return s;
	}
}
