package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Arc implements Serializable, Comparable<Arc> {
	private long headNode;
	private int cost;
	private boolean arcFlag;
	private long shortcutNode;
	
	public Arc(Arc arc) {
		this.headNode = arc.headNode;
		this.cost = arc.cost;
		this.arcFlag = arc.arcFlag;
		this.shortcutNode = arc.shortcutNode;
	}
	
	public Arc(long headNode, int cost, boolean arcFlag, long shortcutNode) {
		this.headNode = headNode;
		this.cost = cost;
		this.arcFlag = arcFlag;		
		this.shortcutNode = shortcutNode;
	}
	
	public Arc(long headNode, int cost) {
		this.headNode = headNode;
		this.cost = cost;
		this.arcFlag = false;
		this.shortcutNode = -1;
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
	
	public boolean isShortcut() { 
		return this.shortcutNode != -1;
	}
	
	public long getShortcutNode() { 
		return this.shortcutNode;
	}
	
	public Arc reverseEdge(long v) {
		return new Arc(v, cost, arcFlag, shortcutNode);
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
		else if(this.shortcutNode > a.shortcutNode)
			return 1;
		else if(this.shortcutNode < a.shortcutNode)
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
		return "("+headNode+", "+cost+", "+arcFlag+", "+shortcutNode+")";
	}
}
