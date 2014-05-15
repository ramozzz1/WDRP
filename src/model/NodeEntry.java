package model;

public class NodeEntry implements Comparable<NodeEntry>
{	
	private long nodeId;
	private int distance;

	public NodeEntry(long nodeId, int distance) {
		this.nodeId = nodeId;
		this.distance = distance;
	}
	
	public long getNodeId() {
		return nodeId;
	}

	public int getDistance() {
		return distance;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(this.nodeId).hashCode();
	}
	
	@Override
	public int compareTo(NodeEntry o) {
		return Integer.compare(this.distance, o.getDistance());
	}
}
