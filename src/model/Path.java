package model;

import java.util.ArrayList;
import java.util.List;

public class Path {
	private List<PathPart> parts;
	private int cost;
	
	public Path() {
		this.parts = new ArrayList<PathPart>();
		this.cost = 0;
	}
	
	/**
	 * add node n to the head of the list
	 * @param n
	 */
	public void addNode(long from, Arc a) {
		parts.add(0, new PathPart(from, a));
		if(a!=null)
			this.cost += a.getCost();
	}

	public List<Long> getNodes() {
		List<Long> nodes = new ArrayList<Long>();
		for (PathPart part : parts)
			nodes.add(part.from);
		return nodes;
	}
	
	public List<Arc> getArcs() {
		List<Arc> arcs = new ArrayList<Arc>();
		for (PathPart part : parts) {
			if(part.arc!= null)
				arcs.add(part.arc);
		}
		return arcs;
	}
	
	public int getCost() {
		return cost;
	}
	
	@Override
	public String toString() {
		String s = "[";
		if(parts.size() > 0) {
			s += parts.get(0).from;
			for (PathPart part : parts) {
				Arc a = part.arc;
				if(a != null)
					s += "->" + a.getHeadNode();
			}
		}
		s += "]";
		return s;
	}
}
