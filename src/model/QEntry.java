package model;

/**
 * Custom PriorityQueue entry for sorting the nodes based on edge difference
 * @author zakaria
 *
 */
public class QEntry implements Comparable<QEntry>
{	
	private long nodeId;
	private int ed;

	public QEntry(long nodeId, int ed) {
		this.nodeId = nodeId;
		this.ed = ed;
	}

	public long getNodeId() {
		return nodeId;
	}
	
	public long getEd() {
		return ed;
	}

	@Override
	public int compareTo(QEntry o) {
		return Integer.compare(this.ed, o.ed);
	}
	
	@Override
	public String toString() {
		return "{"+nodeId+","+ed+"}";
	}
}
