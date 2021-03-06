package org.wdrp.core.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Arc implements Serializable, Comparable<Arc> {
	protected long headNode;
	protected int cost;
	protected boolean arcFlag;
	protected boolean[] arcFlags;
	protected long shortcutNode;
	
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

	public Arc() {
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
	
	public void setArcFlag(boolean arcFlag, int r) {
		this.arcFlags[r] = arcFlag;	
	}
	
	public boolean isArcFlag() {
		return this.arcFlag;	
	}
	
	public boolean isArcFlag(int r) {
		return this.arcFlags[r];	
	}
	
	public boolean isShortcut() { 
		return this.shortcutNode != -1;
	}
	
	public long getShortcutNode() { 
		return this.shortcutNode;
	}
	
	public Arc copy() {
		return new Arc(this.headNode, this.cost, this.arcFlag, this.shortcutNode);
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
