package algorithm;

import model.Arc;
import model.TDGraph;
import model.TDArc;

public class TDDijkstraAlgorithm extends DijkstraAlgorithm implements TimeDependentAlgorithm {

	public TDDijkstraAlgorithm(TDGraph graph) {
		super(graph);
	}

	@Override
	public int computeShortestPath(long source, long target,
			int departureTime) {
		
		//set start cost of source to departure time
		super.startCost = departureTime;
		
		int arrivalTime = super.computeShortestPath(source, target);
		if(arrivalTime > 0 ) {
			//return the travel time
			return arrivalTime - departureTime;
		}
		return arrivalTime;
	}
	
	@Override
	public int getEdgeCost(Arc a, int departureTime) {
		TDArc tdArc = (TDArc)a;
		int timeInterval = (int)(Math.ceil((departureTime+1)/5.0d)-1);
		
		if(timeInterval >= tdArc.costs.length) {
			//the time interval is not within the bounds of this edge (so edge is not reachable)
			return Integer.MAX_VALUE;
		}
		else {
			//time interval is within the bounds of this edge
			return tdArc.getCostForTime(timeInterval) + departureTime;
		}
	}
}
