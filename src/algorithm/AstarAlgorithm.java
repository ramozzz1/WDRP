package algorithm;

import model.Arc;
import model.Graph;
import model.HeuristicTypes;
import model.LatLonPoint;
import util.DistanceUtils;

public class AstarAlgorithm extends DijkstraAlgorithm<Arc> {
	private HeuristicTypes heuristicType;
	private LatLonPoint targetPoint;
	
	public AstarAlgorithm(Graph<Arc> graph) {
		super(graph);
		this.heuristicType = HeuristicTypes.LATLON_DISTANCE;
	}
	
	public AstarAlgorithm(Graph<Arc> graph, HeuristicTypes heuristicType) {
		super(graph);
		this.heuristicType = heuristicType;
	}
	
	@Override
	public int computeShortestPath(long sourceId, long targetId) {
		targetPoint=null;
		return super.computeShortestPath(sourceId, targetId);
	}
	
	@Override
	public String getName() {
		return "Astar";
	}
	
	@Override
	public int getHeuristicValue(long nodeId, long targetId) {
		if(targetId != -1) {
			int h = 0;
			LatLonPoint nodePoint = graph.getLatLon(nodeId);
			if(targetPoint == null) targetPoint =  graph.getLatLon(targetId);
			if(targetPoint!=null && nodePoint!=null) {
				if(this.heuristicType == HeuristicTypes.LATLON_DISTANCE)
					h = (int)Math.ceil(DistanceUtils.latlonDistance(nodePoint.lat, nodePoint.lon, targetPoint.lat, targetPoint.lon)/(130*0.277778));
				else if(this.heuristicType == HeuristicTypes.EUCLIDEAN_DISTANCE)
					h = (int)Math.ceil(DistanceUtils.euclideanDistance(nodePoint.lat, nodePoint.lon, targetPoint.lat, targetPoint.lon));
			}
			return h;
		}
		return 0;
	}
}
