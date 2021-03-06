package org.wdrp.core.algorithm.td;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Arrays;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.wdrp.core.algorithm.DijkstraAlgorithm;
import org.wdrp.core.model.Arc;
import org.wdrp.core.model.Graph;
import org.wdrp.core.model.NodeEntry;
import org.wdrp.core.model.Path;
import org.wdrp.core.model.TDArc;
import org.wdrp.core.model.TDGraph;
import org.wdrp.core.util.ArrayUtils;

public class PQDijkstraAlgorithm extends DijkstraAlgorithm<TDArc>  {

	public Map<Long, Set<Long>> p;
	public THashMap<Long, int[]> f;
	
	public PQDijkstraAlgorithm(Graph<TDArc> graph) {
		super(graph);
	}
	
	public PQDijkstraAlgorithm(TDGraph graph, boolean considerArcFlags,
			boolean considerShortcuts) {
		super(graph, considerArcFlags, considerShortcuts);
	}
	
	public PQDijkstraAlgorithm() {
		this(null);
	}

	@Override
	public void init(long source) {
		this.f = new THashMap<Long, int[]>();
		this.p = new THashMap<Long, Set<Long>>();
		this.queue = new PriorityQueue<NodeEntry>();
		
		f.put(source, new int[((TDGraph)graph).getMaxTime()]);
		p.put(source, null);
		queue.add(new NodeEntry(source, 0));
	}
	
	@Override
	public int computeTravelTime(long source, long target, int departureTime) {
		return this.computeTravelTimes(source, target)[departureTime];
	}

	@Override
	public int computeDepartureTime(long source, long target, int minDepTime, int maxDepTime) {
		int[] travelTimes = computeTravelTimes(source, target, minDepTime, maxDepTime);
		
		int bestDepartureTime = ArrayUtils.getMinIndex(travelTimes);
		
		return bestDepartureTime;
	}
	
	public int computeDepartureTime(long source, long target) {
		return computeDepartureTime(source, target, 0, ((TDGraph)graph).getMaxTime());
	}
	
	public int[] computeTravelTimes(long source, long target, int minDepartureTime, int maxDepartureTime) {
		int travelTimes[] = computeTravelTimes(source, target);
		
		return Arrays.copyOfRange(travelTimes, minDepartureTime, maxDepartureTime);
	}
	
	public int[] computeTravelTimes(long source, long target) {
		if(source != NULL_NODE) {
			
			init(source);
			
			while(!queue.isEmpty()) {
				NodeEntry u = queue.poll();
				long minNodeId = u.getNodeId();
				//System.out.println("MINNODE:"+minNodeId + " -> " + Arrays.toString(f.get(minNodeId)));
				
				int[] targetTTF = f.get(target);
				if(targetTTF != null && u.getDistance() > ArrayUtils.getMaxValue(targetTTF))
					return targetTTF;
				
				if(targetTTF != null && u.getDistance() >= Integer.MAX_VALUE)
					return targetTTF;
				
				int[] ttfU = f.get(minNodeId);
				if(ttfU != null && ArrayUtils.getMaxValue(ttfU) < u.getDistance())
					continue;
				
				for (TDArc v : graph.getNeighbors(minNodeId)) {
					if(considerArc(v)) {
						int[] gNew = ttfU==null ? v.getCosts() : ArrayUtils.linkLists(ttfU, v.getCosts(), ((TDGraph)graph).getInterval());
						//System.out.println(v.getHeadNode()+" "+Arrays.toString(ttfU) + " " + Arrays.toString(v.getCosts())+ " " + Arrays.toString(gNew));
						relax(minNodeId, gNew, v);
					}
				}
			}
			
			int[] t = f.get(target);
			return t != null ? t: ArrayUtils.nullArray(((TDGraph)graph).getMaxTime());
		}
		
		return null;
	}

	public void relax(long u, int[] gNew, TDArc v) {
		int[] ttfV = f.get(v.getHeadNode());
		//System.out.println(v.getHeadNode()+" ttV: " + Arrays.toString(ttfV));
		if(ttfV==null || !(ArrayUtils.listLargerOrEqual(gNew,ttfV))) {
			
			if(ttfV==null || ArrayUtils.listSmaller(gNew,ttfV)) p.put(v.getHeadNode(), null);
				
			int[] minTTF = null;
			if(ttfV==null) 
				minTTF = gNew;
			else 
				minTTF = ArrayUtils.minList(gNew, ttfV);
			
			f.put(v.getHeadNode(), minTTF);
			
			Set<Long> predV = p.get(v.getHeadNode());
			if(predV==null) predV = new THashSet<Long>();
			predV.add(u);
			
			p.put(v.getHeadNode(), predV);
			
			int minValue = ArrayUtils.getMinValue(minTTF);
			//System.out.println(v.getHeadNode()+" "+Arrays.toString(minTTF) + " " + Arrays.toString(ttfV)+ " " + Arrays.toString(gNew));
			queue.add(new NodeEntry(v.getHeadNode(), minValue));
		}
	}
	
	public Path contructPath(Map<Long,Long> previous, long target, int depTime, boolean convertShortcuts) {
		Path path = new Path();
		
		long currNode = target;
		if(p.containsKey(currNode)) {
			do {		
				Set<Long> predecessors = p.get(currNode);
				System.out.println("CURR: "+currNode + " pred: "+predecessors);
				int[] currCosts = f.get(currNode);
				int cost = currCosts[depTime];
				if(cost==-1)
					return new Path();
				System.out.println("cost: " + cost);
				long prevNode = NULL_NODE;
				if(predecessors != null) { 
					for (long predecessor : predecessors) {
						System.out.println(predecessor + "->" + currNode);
						TDArc a = graph.getArc(predecessor, currNode);
						int[] prevCosts = f.get(predecessor);
						System.out.println("prevCosts: " + Arrays.toString(prevCosts));
						System.out.println("Arc costs: " + Arrays.toString(a.getCosts()));
						int index = (int) Math.floor((float) (prevCosts[depTime]+(depTime*((TDGraph)graph).getInterval()))/((TDGraph)graph).getInterval());
						System.out.println(index);
						if(a.getCostForTime(index) == (cost - prevCosts[depTime])) {
							if(convertShortcuts && (a != null && a.isShortcut()))
								convertShortcutArcToPath(a, predecessor, currNode, path);
							else
								path.addNode(graph.getNode(currNode), a);
							System.out.println("path: "+path);
							prevNode = predecessor;
							break;
						}
					}
				}
				else {
					path.addNode(graph.getNode(currNode), null);
				}
				currNode = prevNode;
				
			} while(currNode != NULL_NODE);
		}
		
		return path;
	}
	
	@Override
	public String getName() {
		return "pqdijkstra";
	}
}
