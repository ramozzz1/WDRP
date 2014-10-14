package org.wdrp.core.algorithm;

import gnu.trove.map.hash.TLongLongHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wdrp.core.model.Arc;
import org.wdrp.core.model.Graph;
import org.wdrp.core.model.Path;
import org.wdrp.core.model.TDArc;
import org.wdrp.core.model.TDGraph;

public abstract class AbstractRoutingAlgorithm<K extends Arc> {
	public Graph<K> graph;
	public Set<Long> visitedNodesMarks;
	protected static final long NULL_NODE = -1L;
	
	public AbstractRoutingAlgorithm(Graph<K> graph) {
		this.graph = graph;
	}
	
	public abstract int computeShortestPath(long source, long target);
	
	public int computeTravelTime(long source, long target, int depTime) {
		return -1;
	}
	
	public int computeDepartureTime(long source, long target, int minDepTime, int maxDepTime) {
		return -1;
	}

	public abstract void precompute();
	
	public abstract String getName();

	public abstract Path extractPath(long target);

	public abstract Set<Long> getVisitedNodes();
	
	public void setGraph(Graph<K> graph) {
		this.graph = graph;
	}
	
	public List<Long> getNodesOnPath(TLongLongHashMap previous, long target) {
		List<Long> nodes = new ArrayList<Long>();
		
		long currNode = target;
		if(previous.containsKey(currNode)) {
			do {				
				long prevNode = previous.get(currNode);
				nodes.add(currNode);
				currNode = prevNode;
			} while(currNode != NULL_NODE);
		}
		
		Collections.reverse(nodes);
		
		return nodes;
	}
	
	/**
	 * Construct path from nodeId to the first element of previous (i.e) the one that was inserted the first (source)
	 * Note: shortcut edges are transformed into "real" edges if convertShortcut is set to true
	 * @param p
	 * @param target
	 * @param convertShortcuts true if we need to convert shortcuts to real edges
	 * @return
	 */
	public Path contructPath(Map<Long,Long> p, Map<Long,Integer> f, long target, boolean convertShortcuts) {
		Path path = new Path();
		
		long currNode = target;
		if(p.containsKey(currNode)) {
			do {				
				Integer costToCurr = f.get(currNode);
				long prevNode = p.get(currNode);
				System.out.println("curr:"+prevNode);
				System.out.println("prev:"+prevNode);
				List<K> arcs = graph.getArcs(prevNode, currNode);
				K a = null;
				if(!arcs.isEmpty()) {
					for (K k : arcs) {
						Integer costToPrev = f.get(prevNode);
						if(costToPrev!=null) {
							System.out.println(f);
							int arcCost = k.getCost();
							if(k instanceof TDArc) {
								int index = (int) Math.floor((float) costToPrev/((TDGraph)graph).getInterval());
								if(index>=((TDArc)k).getCosts().length)
									index = ((TDArc)k).getCosts().length-1;
								arcCost = ((TDArc) k).getCostForTime(index);
							}
							System.out.println("arcCost: " + arcCost + " costToPrev: "+costToPrev + " costToCurr: "+costToCurr);
							if(arcCost+costToPrev == costToCurr) {
								a = k;
								break;
							}
						}
					}
				}
				
				System.out.println("arc: " + a);
				if(convertShortcuts && (a != null && a.isShortcut()))
					convertShortcutArcToPath(a, prevNode, currNode, path);
				else
					path.addNode(graph.getNode(currNode), a);
				System.out.println("path: " + path);
				currNode = prevNode;
			} while(currNode != NULL_NODE);
		}
		
		return path;
	}
	
	protected void convertShortcutArcToPath(K a,long fromNode, long toNode, Path p) {
		if(a != null && a.isShortcut()) {
			long shortcutNode = a.getShortcutNode();
			
			//replace the shortcut by adding edge from the shortcut node to the current node
			K arcSHFrom = graph.getArc(shortcutNode, toNode);
			if(!arcSHFrom.isShortcut())
				p.addNode(graph.getNode(shortcutNode), arcSHFrom);
			else {
				convertShortcutArcToPath(arcSHFrom,shortcutNode,toNode,p);
			}
			
			//replace the shortcut by adding edge from the previous node to the shortcut node
			K arcSHTo = graph.getArc(fromNode, shortcutNode);
			if(!arcSHTo.isShortcut())
				p.addNode(graph.getNode(fromNode), arcSHTo);
			else {
				convertShortcutArcToPath(arcSHTo,fromNode,shortcutNode,p);
			}
		}
	}
}
