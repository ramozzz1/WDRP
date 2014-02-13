package algorithm;

import util.CommonUtils;
import model.Graph;
import model.Node;
import model.NodeType;
import model.TNGraph;

public class TimeExpandedDijkstraAlgorithm extends DijkstraAlgorithm implements TimeExpandedAlgorithm {
	
	public TimeExpandedDijkstraAlgorithm(TNGraph graph) {
		super(graph);
	}

	@Override
	public int computeShortestPath(long sourceStationId, long targetStationId,
			String departureTime) {
		return computeShortestPath(sourceStationId, targetStationId, CommonUtils.convertTimeToSeconds(departureTime));
	}
	
	@Override
	public int computeShortestPath(long sourceStationId, long targetStationId,
			int departureTime) {
		long sourceId = selectClosestSourceFromStation(sourceStationId, departureTime);
		
		int cost = super.computeShortestPath(sourceId, targetStationId);
		if(cost != -1) {
			int initWaitingTime = CommonUtils.calculateTimeDiff(CommonUtils.convertSecondsToTime(departureTime), Node.getTime(sourceId));
			return cost+initWaitingTime;
		}
		return cost;
	}

	/**
	 * Calculate the closest departure node from this station for a particular departure time
	 * @param stationId
	 * @param departureTime
	 * @return
	 */
	public long selectClosestSourceFromStation(long stationId,
			int departureTime) {
		
		long minCost = Integer.MAX_VALUE;
		long closestNode = NULL_NODE;
		for (long node : ((TNGraph)this.graph).getNodesOfStation(stationId)) {
			if(Node.getNodeType(node) == NodeType.DEPARTURE) {
				int cost = CommonUtils.convertTimeToSeconds(Node.getTime(node)) - departureTime;
				if(cost >= 0 && cost < minCost) {
					closestNode = node;
					minCost = cost;
				}
			}
		}
		
		return closestNode;
	}
	
	public long selectClosestSourceFromStation(long stationId,
			String departureTime) {
		return selectClosestSourceFromStation(stationId, CommonUtils.convertTimeToSeconds(departureTime));
	}

	@Override
	protected boolean addionalStopCondition(long sourceId, long targetStationId) {
		return Node.getStationId(sourceId) == targetStationId;
	}
	
	@Override
	public String getName() {
		return "TE-Dijkstra";
	}
}
