package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Arc implements Serializable, Comparable<Arc> {
	private long headNode;
	private int cost;
	
	public Arc(long headNode, int cost) {
		this.headNode = headNode;
		this.cost = cost;
	}

	public long getHeadNode() {
		return headNode;
	}

	public int getCost() {
		return cost;
	}
	
	@Override
	public int compareTo(Arc a) {
		if(this.headNode > a.headNode)
			return 1;
		else if(this.headNode < a.headNode)
			return -1;
		else if(this.cost > a.cost)
			return 1;
		else if(this.cost < a.cost)
			return -1;
		else
			return 0;
	}
}
