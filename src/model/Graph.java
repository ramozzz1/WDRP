package model;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.Bind;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

import util.DistanceUtils;

public class Graph {
	private DB db;
	private int numNodes;
	private int numEdges;
	
	public BTreeMap<Long,LatLonPoint> nodes;
	public NavigableSet<Fun.Tuple2<Long,Arc>> adjacenyList;
	
	public Graph() {
		this("temp");
	}
	
	public Graph(String fileName) {
		this.db = DBMaker
				.newFileDB(new File(fileName))
				.transactionDisable()
				.cacheHardRefEnable()
				.asyncFlushDelay(100)
				.randomAccessFileEnableKeepIndexMapped()
				.make();
		
		this.nodes = db.createTreeMap("nodes").keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_LONG).keepCounter(true).makeOrGet();
		this.adjacenyList = db.createTreeSet("adjacenyList").keepCounter(true).makeOrGet();
		this.numNodes = this.nodes.size();
		this.numEdges = this.adjacenyList.size();
	}
	
	public void closeConnection() {
		db.close();
	}
	
	public LatLonPoint getNode(long nodeId) {
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
		this.numNodes++;
	}
	
	public void addEdge(long sourceId, long targetId, int cost) {
		addEdge(sourceId,targetId,cost,false);
	}
	
	public void addEdge(long sourceId, long targetId, int cost, boolean arcFlag) {
		addEdge(sourceId,new Arc(targetId,cost,arcFlag));
		addEdge(targetId,new Arc(sourceId,cost,arcFlag));
	}
	
	public void addEdge(long sourceId, Arc a) {
		this.adjacenyList.add(Fun.t2(sourceId,a));
		this.numEdges++;
	}
	
	public int getNumNodes() {
		return this.numNodes;
	}
	
	public int getNumEdges() {
		return this.numEdges;
	}

	public Iterable<Arc> getNeighbors(long nodeId) {
		return Bind.findVals2(this.adjacenyList, nodeId);
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
