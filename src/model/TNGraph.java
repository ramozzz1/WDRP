package model;

import java.util.NavigableSet;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.Fun;

public class TNGraph extends Graph {
	
	public NavigableSet<Fun.Tuple2<Long,Long>> nodesPerStation;
	public BTreeMap<Long,LatLonPoint> stations;
	private int numStations;
	
	public TNGraph(String fileName, boolean temp) {
		super(fileName,temp);
		
		this.stations = db.createTreeMap("stations").keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_LONG).counterEnable().makeOrGet();
		this.nodesPerStation = db.createTreeSet("nodesPerStation").serializer(BTreeKeySerializer.TUPLE2).counterEnable().makeOrGet();
		this.numStations = this.stations.size();
	}
	
	public TNGraph(String fileName) {
		this(fileName,false);
	}	
	
	public TNGraph() {
		this("", true);
	}

	public void addStation(long stopId, double lat, double lon) {
		this.stations.put(stopId, new LatLonPoint(lat, lon));
		extendBounds(lat,lon);
		this.numStations++;
	}

	public void addNodeToStation(long stationId, long nodeId) {
		this.nodesPerStation.add(Fun.t2(stationId,nodeId));
	}
	
	public int getNumStations() {
		return this.numStations;
	}
	
	public Iterable<Long> getNodesOfStation(Long stationId) {
		return Fun.filter(this.nodesPerStation, stationId);
	}
	
	public void clear() {
		this.stations.clear();
		this.nodesPerStation.clear();
		this.numStations = 0;
		super.clear();
	}


	public BTreeMap<Long, LatLonPoint> getStations() { 
		return this.stations;
	}
}
