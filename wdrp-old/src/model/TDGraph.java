package model;

public class TDGraph extends Graph<TDArc> {
	
	public void addEdge(int sourceId, int targetId, int[] costs) {
		this.addEdge(sourceId, new TDArc(targetId,costs));
		this.addEdge(targetId, new TDArc(sourceId,costs));
	}
	
}
