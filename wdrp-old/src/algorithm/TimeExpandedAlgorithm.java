package algorithm;

public interface TimeExpandedAlgorithm {
	
	/**
	 * Calculate shortest path from one station to another one for a specific departure time
	 * @param sourceStationId
	 * @param targetStationId
	 * @param departureTime in seconds
	 * @return
	 */
	public int computeShortestPath(long sourceStationId, long targetStationId, int departureTime);

	public int computeShortestPath(long sourceStationId, long targetStationId,
			String departureTime);
}
