package model;

import java.util.ArrayList;
import java.util.List;

public class Path {
	private List<PathPart> parts;
	private int cost;
	private Bounds bounds;
	
	public Path() {
		this.parts = new ArrayList<PathPart>();
		this.cost = 0;
		this.bounds = new Bounds();
	}
	
	/**
	 * add node n to the head of the list
	 * @param n
	 */
	public void addNode(Node from, Arc a) {
		addPathPart(new PathPart(from, a));
	}
	
	/**
	 * Add a @PathPart to the head of the list
	 * @param p
	 */
	public void addPathPart(PathPart p) {
		addPathPart(0,p);
	}
	
	/**
	 * Add a @PathPart to position i
	 * @param p
	 */
	public void addPathPart(int i, PathPart p) {
		if(i==-1)
			parts.add(p);
		else
			parts.add(i, p);
		
		if(p.arc!=null) {
			this.cost += p.arc.getCost();
			this.bounds.updateBounds(p.from.getLat(),p.from.getLon());
		}
	}

	public List<Node> getNodes() {
		List<Node> nodes = new ArrayList<Node>();
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
	
	public Bounds getBounds() {
		return bounds;
	}
	
	public boolean isEmpty() {
		return this.parts.isEmpty();
	}
	
	public Path reversePath() {
		Path reversePath = new Path();
		for (PathPart part : parts) {
			Arc a = null;
			if(part.arc != null)
				a = part.arc.reverseEdge(part.from.getId());
			reversePath.addNode(part.from, a);
		}
		
		return reversePath; 
	}
	
	/**
	 * Connect two paths together
	 * @precondition The last node of this path should be equal to the first node of the other path 
	 * @param p
	 */
	public void connect(Path p) {
		if(!p.isEmpty()) {
			for (int i = 1; i < p.parts.size(); i++) {
				addPathPart(-1, p.parts.get(i));
			}
		}
	}
	
	public String toJsonArray() {
		String s = "[";
		for (int i = 0; i < parts.size(); i++) {
			PathPart part = parts.get(i);
			s += "[" + part.from.getLat() + "," + part.from.getLon() + "]";
			if(i < parts.size() - 1)
				s += ",";
		}
		s += "]";
		return s;
	}
	
	@Override
	public String toString() {
		String s = "[";
		if(parts.size() > 0) {
			s += parts.get(0).from.getId();
			for (PathPart part : parts) {
				Arc a = part.arc;
				if(a != null)
					s += "->" + a.getHeadNode();
			}
		}
		s += "]";
		return s;
	}

	public int length() {
		return parts.size();
	}
}
