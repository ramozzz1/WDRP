package model;

import java.util.Arrays;


@SuppressWarnings("serial")
public class TDArc extends Arc{
	
	public int[] costs;
	
	public TDArc(int maxTime) {
		super();
		
		this.costs = new int[maxTime];
	}
	
	public TDArc(Arc arc, int maxTime) {
		super(arc);
		
		this.costs = new int[maxTime];
		for (int i = 0; i < maxTime; i++)
			this.costs[i] = arc.getCost();
	}
	
	public TDArc(int targetId, int[] costs) {
		super(targetId, costs[0]);
		
		this.costs = costs;
	}

	public TDArc(long targetId, int[] costs, boolean arcFlag, long shortcutNode) {
		super(targetId,costs[0],arcFlag,shortcutNode);
		this.costs = costs;
	}

	@Override
	public TDArc copy() {
		return new TDArc(this.headNode, this.costs, this.arcFlag, this.shortcutNode);
	}
	
	@Override
	public Arc reverseEdge(long v) {
		return new TDArc(v, costs, arcFlag, shortcutNode);
	}
	
	/**
	 * Get cost at specific time point 
	 * @param time
	 * @return
	 */
	public int getCostForTime(int time) { 
		return costs[time];
	}
	
	/**
	 * Sets the cost for a specific time
	 * @param cost
	 * @param time
	 */
	public void setCostForTime(int cost, int time) { 
		costs[time] = cost;
	}
	
	public int[] getCosts() { 
		return costs;
	}
	
	public void setCosts(int[] costs) { 
		this.costs = costs;
	}
	
	@Override
	public String toString() {
		return "("+headNode+", "+cost+", "+arcFlag+", "+shortcutNode+","+Arrays.toString(costs)+")";
	}
}
