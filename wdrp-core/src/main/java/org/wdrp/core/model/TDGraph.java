package org.wdrp.core.model;

public class TDGraph extends Graph<TDArc> {
	
	public TDGraph(String fileName) {
		super(fileName);
	}
	
	public TDGraph() {
		super();
	}

	public void addEdge(int sourceId, int targetId, int[] costs) {
		this.addEdge(sourceId, new TDArc(targetId,costs));
		this.addEdge(targetId, new TDArc(sourceId,costs));
	}
	
}
