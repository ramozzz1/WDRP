package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Arc implements Serializable, Comparable<Arc> {
	private long headNode;
	private int cost;
	private boolean arcFlag;
	
	public Arc(Arc arc) {
		this.headNode = arc.headNode;
		this.cost = arc.cost;
		this.arcFlag = arc.arcFlag;
	}
	
	public Arc(long headNode, int cost, boolean arcFlag) {
		this.headNode = headNode;
		this.cost = cost;
		this.arcFlag = arcFlag;
	}
	
	public Arc(long headNode, int cost) {
		this.headNode = headNode;
		this.cost = cost;
		this.arcFlag = false;
	}

	public long getHeadNode() {
		return headNode;
	}

	public int getCost() {
		return cost;
	}
	
	public void setArcFlag(boolean arcFlag) {
		this.arcFlag = arcFlag;	
	}
	
	public boolean isArcFlag() {
		return this.arcFlag;	
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
		/*else if(this.arcFlag==false && a.arcFlag==true)
			return -1;
		else if(this.arcFlag==true && a.arcFlag==false)
			return 1;*/
		else
			return 0;
	}
	
	@Override
	public String toString() {
		return "("+headNode+", "+cost+", "+arcFlag+")";
	}
}
