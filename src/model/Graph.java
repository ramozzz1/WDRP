package model;

import java.io.File;
import java.util.Iterator;
import java.util.NavigableSet;

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
	
	public BTreeMap<Long,Fun.Tuple2<Double,Double>> nodes;
	public NavigableSet<Fun.Tuple2<Long,Arc>> adjacenyList;
	
	public Graph() {
		this("temp");
	}
	
	public Graph(String fileName) {
/*		this.db = DBMaker
                .newFileDB(new File(fileName))
                .transactionDisable()
                .randomAccessFileEnableKeepIndexMapped()
                .closeOnJvmShutdown()
                .make();*/
		
		this.db = DBMaker
				.newFileDB(new File(fileName))
				.transactionDisable()
				.asyncFlushDelay(100)
				.cacheHardRefEnable()
				.randomAccessFileEnableKeepIndexMapped()
				.closeOnJvmShutdown()
                .make();
		
		this.nodes = db.createTreeMap("nodes").keepCounter(true).makeOrGet();
		this.adjacenyList = db.createTreeSet("adjacenyList").keepCounter(true).makeOrGet();
		this.numNodes = this.nodes.size();
		this.numEdges = this.adjacenyList.size();
	}
	
	public void commit() {
		db.commit();
	}
	
	public void closeConnection() {
		db.close();
	}
	
	public LatLonPoint getNode(long nodeId) {
		Tuple2<Double, Double> t = nodes.get(nodeId);
		return new LatLonPoint(t.a, t.b);

	}
	
	public void addNode(long nodeId) {
		addNode(nodeId,0.0,0.0);
	}
	
	public void addNode(long nodeId, double lat, double lon) {
		nodes.put(nodeId, Fun.t2(lat, lon));
		
		this.numNodes++;
		//if(this.numNodes%1000000==0) db.commit();
	}
	
	public void addEdge(long sourceId, long targetId, int cost) {
		this.adjacenyList.add(Fun.t2(sourceId,new Arc(targetId,cost)));
		this.adjacenyList.add(Fun.t2(targetId,new Arc(sourceId,cost)));
		
		this.numEdges++;
		this.numEdges++;
		//if(this.numEdges%1000000==0) db.commit();
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
