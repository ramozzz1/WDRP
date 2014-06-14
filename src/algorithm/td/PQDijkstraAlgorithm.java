package algorithm.td;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Arrays;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import model.Graph;
import model.NodeEntry;
import model.TDArc;
import model.TDGraph;
import util.ArrayUtils;
import algorithm.DijkstraAlgorithm;

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
	
	@Override
	public void init(long source) {
		this.f = new THashMap<Long, int[]>();
		this.p = new THashMap<Long, Set<Long>>();
		this.queue = new PriorityQueue<NodeEntry>();
		
		f.put(source, new int[graph.timeInterval]);
		p.put(source, null);
		queue.add(new NodeEntry(source, 0));
	}

	public int computeBestDepartureTime(long source, long target) {
		int[] travelTimes = computeTravelTimes(source, target);
		
		int bestDepartureTime = ArrayUtils.getMinIndex(travelTimes);
		
		return bestDepartureTime;
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
						int[] gNew = ttfU==null ? v.getCosts() : ArrayUtils.linkLists(ttfU, v.getCosts());
						relax(minNodeId, gNew, v);
					}
				}
			}
			
			return f.get(target);
		}
		
		return null;
	}

	public void relax(long u, int[] gNew, TDArc v) {
		int[] ttfV = f.get(v.getHeadNode());
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
			queue.add(new NodeEntry(v.getHeadNode(), minValue));
		}
	}
}
