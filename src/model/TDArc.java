package model;


@SuppressWarnings("serial")
public class TDArc extends Arc{
	
	public int[] costs;
	
	public TDArc(int maxTime) {
		super();
		
		this.costs = new int[maxTime];
	}
	
	public TDArc(Arc arc) {
		super(arc);
	}
	
	public TDArc(int targetId, int[] costs) {
		super(targetId, 0);
		
		this.costs = costs;
	}

	public TDArc(long targetId, int[] costs, boolean arcFlag, long shortcutNode) {
		super(targetId,0,arcFlag,shortcutNode);
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
}
