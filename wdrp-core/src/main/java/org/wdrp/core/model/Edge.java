package org.wdrp.core.model;

import java.io.Serializable;

public class Edge implements Serializable, Comparable<Edge> {
	
	private static final long serialVersionUID = 1L;
	private long source;
	private long target;
	private int cost;
	
	public Edge(long source, long target, int cost) {
		this.source = source;
		this.target = target;
		this.cost = cost;
	}
	
	public long getSource() {
		return this.source;
	}
	
	public long getTarget() {
		return this.target;
	}
	
	public Edge reverseEdge() {
		return new Edge(this.target,this.source,this.cost);
	}
	
	public int getCost() {
		return this.cost;
	}
	
	@Override
	public boolean equals (Object o) {
		if(o instanceof Edge) {
			Edge otherEdge = (Edge) o;
			if(this.source==otherEdge.getSource() && this.source==otherEdge.getTarget()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return (int) (this.source * this.target);
	}
	
	@Override
    public String toString() {
		return "("+this.source+","+this.target+","+this.cost+")";
    }

	@Override
	public int compareTo(Edge o) {
		return 0;
	}
}
