package model;

public class PathPart {

	public long from;
	public Arc arc;
	
	public PathPart(long from, Arc arc) {
		this.from = from;
		this.arc = arc;
	}

}
