package algorithm;

import java.util.Set;

import model.Graph;

public abstract class AbstractRoutingAlgorithm {
	public Graph graph;
	public Set<Long> visitedNodesMarks;
	
	public AbstractRoutingAlgorithm(Graph graph) {
		this.graph = graph;
	}
	
	public abstract int computeShortestPath(long sourceId, long targetId);

	public abstract void precompute();
	
	public abstract String getName();
}
