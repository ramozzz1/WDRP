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
	public int compareTo(Arc arc) {
		if((headNode<arc.headNode && cost==arc.cost)||(headNode==arc.headNode && cost<arc.cost)) return -1;
		if((headNode==arc.headNode && cost>arc.cost)||(headNode>arc.headNode && cost==arc.cost)) return 1;
		return 0;
	}
}
