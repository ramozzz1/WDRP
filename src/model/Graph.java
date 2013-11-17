package model;

import java.io.File;
import java.util.Iterator;
import java.util.NavigableSet;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.Bind;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

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
	
	public void addNode(long nodeId) {
		addNode(nodeId,0.0,0.0);
	}
	
	public void addNode(long nodeId, double lat, double lon) {
		nodes.put(nodeId, new LatLonPoint(lat, lon));
		this.numNodes++;
	}
	
	public void addEdge(long sourceId, long targetId, int cost) {
		this.adjacenyList.add(Fun.t2(sourceId,new Arc(targetId,cost)));
		this.adjacenyList.add(Fun.t2(targetId,new Arc(sourceId,cost)));
		
		this.numEdges++;
		this.numEdges++;
	}
	
	public int getNumNodes() {
		return this.numNodes;
	}
	
	public int getNumEdges() {
		return this.numEdges;
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

	public Iterable<Arc> getNeighbors(long nodeId) {
		return Bind.findVals2(this.adjacenyList, nodeId);
	}

	public void clear() {
		this.nodes.clear();
		this.adjacenyList.clear();
		this.numEdges=0;
		this.numNodes=0;
		closeConnection();
	}

	public void removeNode(long nodeId) {
		this.nodes.remove(nodeId);
		this.numNodes--;
	}
	
	public void removeEdge(long nodeId) {
		for (Arc a : getNeighbors(nodeId))
			this.adjacenyList.remove(Fun.t2(nodeId, a));
		
		this.numEdges--;
	}
}
