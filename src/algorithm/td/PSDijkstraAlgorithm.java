package algorithm.td;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import model.Arc;
import model.NodeEntry;
import model.TDArc;
import model.TDGraph;
import util.ArrayUtils;
import algorithm.DijkstraAlgorithm;

public class PSDijkstraAlgorithm extends DijkstraAlgorithm  {

	public Map<Long, Set<Long>> p;
	public THashMap<Long, List<Integer>> f;
	
	public PSDijkstraAlgorithm(TDGraph graph) {
		super(graph);
	}
	
	public int[] computeTravelTimes(long source, long target) {
		return computeTravelTimes(source, target, 0, 20);
	}
	
	public int[] computeTravelTimes(long source, long target, int minDepartureTime, int maxDepartureTime) {
		this.f = new THashMap<Long, List<Integer>>();
		this.p = new THashMap<Long, Set<Long>>();
		
		if(source != NULL_NODE) {
			f.put(source, ArrayUtils.extrapolateArray(new int[]{0}, maxDepartureTime-minDepartureTime));
			p.put(source, null);
			
			Queue<NodeEntry> queue = new PriorityQueue<NodeEntry>();
			queue.add(new NodeEntry(source, 0));
			while(!queue.isEmpty()) {
				NodeEntry u = queue.poll();
				long minNodeId = u.getNodeId();
				
				System.out.println("MIN: "+minNodeId);
				List<Integer> targetTTF = f.get(target);
				if(targetTTF != null && u.getDistance() > Collections.max(targetTTF))
					return ArrayUtils.toIntArray(targetTTF);
				
				if(targetTTF != null && u.getDistance() >= Integer.MAX_VALUE)
					return ArrayUtils.toIntArray(targetTTF);
				
				List<Integer> ttfU = f.get(minNodeId);
				if(ttfU != null && Collections.max(ttfU) < u.getDistance())
					continue;
				
				for (Arc e : graph.getNeighbors(minNodeId)) {
					TDArc v = (TDArc)e;
					
					List<Integer> ttfUV = ArrayUtils.extrapolateArray(v.getCosts(), 5).subList(minDepartureTime, maxDepartureTime);
					List<Integer> gNew = ttfU==null ? ttfUV : ArrayUtils.linkLists(ttfU, ttfUV);
					System.out.println("NEIGHBOR ("+minNodeId+","+v.getHeadNode()+") :"+ttfUV.toString());
					System.out.println("GNEW ("+minNodeId+","+v.getHeadNode()+") :"+gNew.toString());
					List<Integer> ttfV = f.get(v.getHeadNode());
					if(ttfV != null) System.out.println(v.getHeadNode()+": "+ttfV.toString());
					if(ttfV==null || !(ArrayUtils.listLargerOrEqual(gNew,ttfV))) {
						
						if(ttfV==null || ArrayUtils.listSmaller(gNew,ttfV)) p.put(v.getHeadNode(), null);
							
						List<Integer> minTTF = null;
						if(ttfV==null) 
							minTTF = gNew;
						else 
							minTTF = ArrayUtils.minList(gNew, ttfV);
						
						System.out.println("MINTTF: "+v.getHeadNode()+" :"+minTTF.toString());
						f.put(v.getHeadNode(), minTTF);
						
						Set<Long> predV = p.get(v.getHeadNode());
						if(predV==null) predV = new THashSet<Long>();
						predV.add(minNodeId);
						
						p.put(v.getHeadNode(), predV);
						
						int minValue = ArrayUtils.getMinValue(minTTF);
						System.out.println(v.getHeadNode() +" minVal: "+ minValue);
						queue.add(new NodeEntry(v.getHeadNode(), minValue));
					}
				}
			}
		}
		
		return ArrayUtils.toIntArray(f.get(target));
	}
}
