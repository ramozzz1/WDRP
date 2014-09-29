package org.wdrp.core.algorithm.td;

import java.util.Arrays;
import java.util.Map;

import org.mapdb.Fun.Tuple2;
import org.wdrp.core.algorithm.DijkstraAlgorithm;
import org.wdrp.core.model.Path;
import org.wdrp.core.model.TDArc;
import org.wdrp.core.model.TDGraph;
import org.wdrp.core.util.ArrayUtils;

public class TDDijkstraAlgorithm extends DijkstraAlgorithm<TDArc> implements TimeDependentAlgorithm {

	public TDDijkstraAlgorithm() {
		this(null);
	}
	
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
		super.startCost = departureTime*((TDGraph)graph).getInterval();
		
		int earliestArrivalTime = super.computeShortestPath(source, target);
		
		return earliestArrivalTime;
	}
	
	@Override
	public int computeTravelTime(long source, long target,
			int departureTime) {
		//compute the earliest arrival time for the given departure time
		int earliestArrivalTime = this.computeEarliestArrivalTime(source, target, departureTime);
		
		//Check if the arrival time was found
		if(earliestArrivalTime >= 0)
			return earliestArrivalTime - (departureTime*((TDGraph)graph).getInterval());
			
		return earliestArrivalTime;
	}
	
	@Override
	public int computeDepartureTime(long source, long target, int minDepTime, int maxDepTime) {
		int[] travelTimes = computeTravelTimes(source, target, minDepTime, maxDepTime);
		
		int bestDepartureTime = ArrayUtils.getMinIndex(travelTimes);
		
		return bestDepartureTime;
	}
	
	public int computeDepartureTime(long source, long target) {
		return computeDepartureTime(source, target, 0, ((TDGraph)graph).getMaxTime());
	}
	
	@Override
	public int[] computeEarliestArrivalTimes(long source, long target,
			int minDepartureTime, int maxDepartureTime) {
		int[] travelTimes = new int[(maxDepartureTime-minDepartureTime)];
		
		for(int i=0; i < travelTimes.length;i=i+1)
			travelTimes[i] = this.computeEarliestArrivalTime(source, target, i+minDepartureTime);
		
		return travelTimes;
	}
	
	public int[] computeEarliestArrivalTimes(long source, long target) {
		return computeEarliestArrivalTimes(source, target, 0, ((TDGraph)graph).getMaxTime());
	}
	
	public int[] computeTravelTimes(long source, long target) {
		return computeTravelTimes(source, target, 0, ((TDGraph)graph).getMaxTime());
	}
	
	@Override
	public int[] computeTravelTimes(long source, long target,
			int minDepartureTime, int maxDepartureTime) {
		int[] travelTimes = new int[maxDepartureTime-minDepartureTime];
		
		for(int i=0; i < travelTimes.length;i=i+1)
			travelTimes[i] = this.computeTravelTime(source, target, i+minDepartureTime);
		
		return travelTimes;
	}
	
	@Override
	public int getEdgeCost(TDArc a, int arrivalTime) {
		
		int index = (int) Math.floor((float) arrivalTime/((TDGraph)graph).getInterval());
		//System.out.println("index "+index + " "+arrivalTime + " " + Arrays.toString(a.getCosts()));
		
		//check if arrival time is within the possible arrival times of the arc
		if(index >= a.costs.length) {
			index = a.costs.length-1;
		}
		
		if(a.getCostForTime(index)<0) return Integer.MAX_VALUE;
		//time interval is within the bounds, now calculate the arrival time at the head of the edge
		
		return (int) Math.min(Integer.MAX_VALUE,(long)a.getCostForTime(index) + (long)arrivalTime);
	}

	public Tuple2<Integer, Integer> computeTravelTimesInterval(long source, long target) {
		int[] travelTimes = computeTravelTimes(source, target);
		
		return new Tuple2<Integer, Integer>(
				ArrayUtils.getMinValue(travelTimes), 
				ArrayUtils.getMaxValue(travelTimes)
				);
	}
	
	@Override
	public Path contructPath(Map<Long,Long> previous, long target, boolean convertShortcuts) {
		Integer cost = f.get(target);
		if(cost==null || cost == Integer.MAX_VALUE)
			return new Path();
		return super.contructPath(p, target, convertShortcuts);
	}
	
	@Override
	public String getName() {
		return "tddijkstra";
	}
}
