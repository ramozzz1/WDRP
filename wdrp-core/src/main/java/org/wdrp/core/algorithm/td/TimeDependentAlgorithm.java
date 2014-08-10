package org.wdrp.core.algorithm.td;

public interface TimeDependentAlgorithm {
	
	/**
	 * Calculate the earliest arrival time from one node to another one for a specific departure time
	 * @param source
	 * @param target
	 * @param departureTime
	 * @return
	 */
	public int computeEarliestArrivalTime(long source, long target, int departureTime);
	
	/**
	 * Calculate the minimum travel time from one node to another one for a specific departure time
	 * @param source
	 * @param target
	 * @param departureTime
	 * @return
	 */
	public int computeMinimumTravelTime(long source, long target, int departureTime);
	
	/**
	 * Calculate the travel time from one node to another one for a specific departure time-range
	 * @param source
	 * @param target
	 * @param minDepartureTime
	 * @param maxDepartureTime
	 * @return
	 */
	public int[] computeTravelTimes(long source, long target, int minDepartureTime, int maxDepartureTime);
	
	/**
	 * Calculate the minimum travel time from one node to another one for a specific departure time-range
	 * @param source
	 * @param target
	 * @param minDepartureTime
	 * @param maxDepartureTime
	 * @return
	 */
	public int computeMinimumTravelTime(long source, long target, int minDepartureTime, int maxDepartureTime);
	
	/**
	 * Calculate the departure time, from one node to another one for a specific departure time-range, 
	 * such that the travel time is minimal. 
	 * @param source
	 * @param target
	 * @param minDepartureTime
	 * @param maxDepartureTime
	 * @return
	 */
	public int computeBestDepartureTime(long source, long target, int minDepartureTime, int maxDepartureTime);

	/**
	 * Calculate the earliest arrival time from one node to another one for a specific departure time-range
	 * @param source
	 * @param target
	 * @param minDepartureTime
	 * @param maxDepartureTime
	 * @return
	 */
	public int[] computeEarliestArrivalTimes(long source, long target,
			int minDepartureTime, int maxDepartureTime);
}
