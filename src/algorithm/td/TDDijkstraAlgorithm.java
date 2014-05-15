package algorithm.td;

import model.Arc;
import model.TDArc;
import model.TDGraph;
import util.ArrayUtils;
import algorithm.DijkstraAlgorithm;

public class TDDijkstraAlgorithm extends DijkstraAlgorithm implements TimeDependentAlgorithm {

	public TDDijkstraAlgorithm(TDGraph graph) {
		super(graph);
	}

	@Override
	public int computeEarliestArrivalTime(long source, long target,
			int departureTime) {
		
		//set start cost of source to departure time
		super.startCost = departureTime;
		
		int earliestArrivalTime = super.computeShortestPath(source, target);
		
		return earliestArrivalTime;
	}
	
	@Override
	public int computeMinimumTravelTime(long source, long target,
			int departureTime) {
		//compute the earliest arrival time for the given departure time
		int earliestArrivalTime = this.computeEarliestArrivalTime(source, target, departureTime);
		
		//Check if the arrival time was found
		if(earliestArrivalTime >= 0)
			return earliestArrivalTime - departureTime;
			
		return earliestArrivalTime;
	}
	
	@Override
	public int[] computeEarliestArrivalTimes(long source, long target,
			int minDepartureTime, int maxDepartureTime) {
		int[] travelTimes = new int[(maxDepartureTime-minDepartureTime)+1];
		
		for(int i=0; i < travelTimes.length;i=i+1)
			travelTimes[i] = this.computeEarliestArrivalTime(source, target, i+minDepartureTime);
		
		return travelTimes;
	}
	
	@Override
	public int[] computeTravelTimes(long source, long target,
			int minDepartureTime, int maxDepartureTime) {
		int[] travelTimes = new int[(maxDepartureTime-minDepartureTime)+1];
		
		for(int i=0; i < travelTimes.length;i=i+1)
			travelTimes[i] = this.computeMinimumTravelTime(source, target, i+minDepartureTime);
		
		return travelTimes;
	}

	@Override
	public int computeMinimumTravelTime(long source, long target,
			int minDepartureTime, int maxDepartureTime) {
		
		int[] travelTimes = computeTravelTimes(source, target, minDepartureTime, maxDepartureTime);
		
		int minimumTravelTime = ArrayUtils.getMinValue(travelTimes);
		
		return minimumTravelTime;
	}
	
	@Override
	public int computeBestDepartureTime(long source, long target,
			int minDepartureTime, int maxDepartureTime) {
		
		int[] travelTimes = computeTravelTimes(source, target, minDepartureTime, maxDepartureTime);
				
		int bestDepartureTime = ArrayUtils.getMinIndex(travelTimes)+minDepartureTime;
		
		return bestDepartureTime;
	}
	
	@Override
	public int getEdgeCost(Arc a, int departureTime) {
		TDArc tdArc = (TDArc)a;
		
		//convert the departure time from Arc a to corresponding time
		int timeInterval = (int)(Math.ceil((departureTime+1)/5.0d)-1);
		
		//check if time interval is within the bounds of this edge
		if(timeInterval >= tdArc.costs.length) {
			//the time interval is not within the bounds of this edge (so edge is not reachable)
			return Integer.MAX_VALUE;
		}
		else {
			//time interval is within the bounds, now calculate the calculate the arrival time at the head of the edge
			return tdArc.getCostForTime(timeInterval) + departureTime;
		}
	}
}
