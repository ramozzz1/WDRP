package org.wdrp.core.model;

public class TDGraph extends Graph<TDArc> {
	
	public TDGraph(String fileName) {
		super(fileName);
	}
	
	public TDGraph(int interval, int maxTime) {
		super();
		setInterval(interval);
		setMaxTime(maxTime);
	}

	public void addEdge(int sourceId, int targetId, int[] costs) {
		this.addEdge(sourceId, new TDArc(targetId,costs));
		this.addEdge(targetId, new TDArc(sourceId,costs));
	}
	
	public int getInterval() {
		return _db.getAtomicInteger("interval").get();
	}
	
	public void setInterval(int interval) {
		if(_db.getAtomicInteger("interval") == null) {
			_db.createAtomicInteger("interval", interval);
		} else {
			_db.getAtomicInteger("interval").set(interval);
		}
	}
	
	public int getMaxTime() {
		return _db.getAtomicInteger("maxTime").get();
	}
	
	public void setMaxTime(int maxTime) {
		if(_db.getAtomicInteger("maxTime") == null) {
			_db.createAtomicInteger("maxTime", maxTime);
		} else {
			_db.getAtomicInteger("maxTime").set(maxTime);
		}
	}
}
