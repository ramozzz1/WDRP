package org.wdrp.core.model;

public class NodePair {
	private long source;
	private long target;
	
	public NodePair(long source, long target) {
		this.source = source;
		this.target = target;
	}

	public long getSource() {
		return source;
	}
	
	public long getTarget() {
		return target;
	}
	
	@Override
	public String toString() {
		return "("+source+","+target+")";
	}
}
