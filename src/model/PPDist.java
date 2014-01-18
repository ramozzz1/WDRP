package model;

import java.io.Serializable;
import java.util.Map;

/**
 * Helper class for storing distance and parent pointers pairs
 *
 */
@SuppressWarnings("serial")
public class PPDist implements Serializable{
	public Map<Long,Long> previous;
	public int dist;
	
	public PPDist(Map<Long,Long> previous, int dist) {
		this.previous = previous;
		this.dist = dist;
	}
	
	@Override
	public String toString() {
		return "{"+this.dist+", "+this.previous.toString()+"}";
	}

}