package algorithm.td;

import model.TDArc;
import model.TDGraph;

import org.mapdb.Fun.Tuple2;

import util.ArrayUtils;
import algorithm.DijkstraAlgorithm;

public class TDDijkstraAlgorithm extends DijkstraAlgorithm<TDArc> implements TimeDependentAlgorithm {

	public TDDijkstraAlgorithm(TDGraph graph) {
		super(graph);
	}

	public TDDijkstraAlgorithm(TDGraph graph, boolean considerArcFlags, boolean considerShortcuts) {
		super(graph, considerArcFlags, considerShortcuts);
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
	
	public int[] computeEarliestArrivalTimes(long source, long target) {
		return computeEarliestArrivalTimes(source, target, 0, 20);
	}
	
	public int[] computeTravelTimes(long source, long target) {
		return computeTravelTimes(source, target, 0, 20);
	}
	
	@Override
	public int[] computeTravelTimes(long source, long target,
			int minDepartureTime, int maxDepartureTime) {
		int[] travelTimes = new int[maxDepartureTime-minDepartureTime];
		
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
	
	public int computeBestDepartureTime(long source, long target) {
		return computeBestDepartureTime(source, target, 0, 20);
	}
	
	@Override
	public int computeBestDepartureTime(long source, long target,
			int minDepartureTime, int maxDepartureTime) {
		
		int[] travelTimes = computeTravelTimes(source, target, minDepartureTime, maxDepartureTime);
				
		int bestDepartureTime = ArrayUtils.getMinIndex(travelTimes)+minDepartureTime;
		
		return bestDepartureTime;
	}
	
	@Override
	public int getEdgeCost(TDArc a, int arrivalTime) {
		
		//check if arrival time is within the possible arrival times of the arc
		if(arrivalTime >= a.costs.length) {
			//the time interval is not within the bounds of this arc (so arc is not reachable)
			return Integer.MAX_VALUE;
		}
		else {
			if(a.getCostForTime(arrivalTime)<0) return Integer.MAX_VALUE;
			//time interval is within the bounds, now calculate the calculate the arrival time at the head of the edge
			return a.getCostForTime(arrivalTime) + arrivalTime;
		}
	}

	public Tuple2<Integer, Integer> computeTravelTimesInterval(long source, long target) {
		int[] travelTimes = computeTravelTimes(source, target);
		
		return new Tuple2<Integer, Integer>(
				ArrayUtils.getMinValue(travelTimes), 
				ArrayUtils.getMaxValue(travelTimes)
				);
	}
}
