package algorithm;

public interface TimeDependentAlgorithm {
	
	/**
	 * Calculate shortest path from one node to another one for a specific departure time
	 * @param source
	 * @param target
	 * @param departureTime
	 * @return
	 */
	public int computeShortestPath(long source, long target, int departureTime);
}
