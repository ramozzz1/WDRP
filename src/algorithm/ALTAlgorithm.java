package algorithm;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;

import model.Arc;
import model.Graph;
import storage.DBHashMap;
import storage.DBLongIntHashMap;
import util.CommonUtils;

public class ALTAlgorithm extends DijkstraAlgorithm<Arc> {

	private List<Long> landMarks;
	private THashMap<Long,DBHashMap<Long,Integer>> landMarkDistances;
	private int numLandmarks;
	
	public ALTAlgorithm(Graph<Arc> graph) {
		super(graph);
		this.landMarks = new ArrayList<Long>();
		this.numLandmarks = 0;
	}

	public ALTAlgorithm(Graph<Arc> graph, int numLandmarks) {
		super(graph);
		assert numLandmarks <= graph.getNumNodes() : "#landmark nodes has to be <= to #nodes in the graph";
		this.landMarks = new ArrayList<Long>();
		this.numLandmarks = numLandmarks;
	}

	@Override
	public void precompute() {
		this.landMarks = selectRandomLandmarks(this.numLandmarks);
		this.landMarkDistances = calculateLandmarkDistances();
	}
	
	@Override
	public int computeShortestPath(long sourceId, long targetId) {
		return super.computeShortestPath(sourceId, targetId);
	}
	
	//select random landmarks from graph
	public List<Long> selectRandomLandmarks(int nl) {
		return CommonUtils.getRandomKeys(graph.nodes, nl);
	}

	//calculate distances from all landmarks to all nodes   
	public THashMap<Long,DBHashMap<Long,Integer>> calculateLandmarkDistances() {
		THashMap<Long,DBHashMap<Long,Integer>> ld = new THashMap<Long,DBHashMap<Long,Integer>>();
		int count = 0;
		for (Long landmarkId : this.landMarks) {
			count++;
			System.out.println("Landmark #"+count+": "+landmarkId);
			super.computeShortestPath(landmarkId, -1);
			ld.put(landmarkId, new DBLongIntHashMap(super.f));
		}
		return ld;
	}
	
	//based on the landmark distances calculate heuristic function node
	@Override
	public int getHeuristicValue(long nodeId, long targetId) {
		if(targetId != -1) {
			int maxDist = 0;
			for (DBHashMap<Long,Integer> ld : landMarkDistances.values()) {
				if(ld.containsKey(nodeId)){
					int lmDist = Math.abs(ld.get(nodeId) - ld.get(targetId));
					maxDist = Math.max(maxDist, lmDist);
				}
			}
			return maxDist;
		}
		return 0;
	}

	public List<Long> getLandMarks() {
		return this.landMarks;
	}

	public void setLandMarks(List<Long> landMarks) {
		this.landMarks = landMarks;
	}

	public THashMap<Long,DBHashMap<Long,Integer>> getLandMarkDistances() {
		return landMarkDistances;
	}
	
	public void setLandMarkDistances(
			THashMap<Long, DBHashMap<Long,Integer>> landMarkDistances) {
		this.landMarkDistances = landMarkDistances;
	}
	
	@Override
	public String getName() {
		return "ALT";
	}
}
