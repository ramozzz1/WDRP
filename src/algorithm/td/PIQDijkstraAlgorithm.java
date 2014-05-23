package algorithm.td;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import model.NodeEntry;
import model.TDArc;
import model.TDGraph;

import org.mapdb.Fun.Tuple2;

import util.ArrayUtils;
import algorithm.DijkstraAlgorithm;

public class PIQDijkstraAlgorithm extends DijkstraAlgorithm<TDArc> {

	public Map<Long, Set<Long>> p;
	public THashMap<Long, Tuple2<Integer, Integer>> f;
	
	public PIQDijkstraAlgorithm(TDGraph graph) {
		super(graph);
	}
	
	public Tuple2<Integer, Integer> computeTravelTimesInterval(long source, long target) {
		this.f = new THashMap<Long, Tuple2<Integer, Integer>>();
		this.p = new THashMap<Long, Set<Long>>();
		
		if(source != NULL_NODE) {
			f.put(source, new Tuple2<Integer, Integer>(0,0));
			p.put(source, null);
			
			Queue<NodeEntry> queue = new PriorityQueue<NodeEntry>();
			queue.add(new NodeEntry(source, 0));
			while(!queue.isEmpty()) {
				NodeEntry u = queue.poll();
				long minNodeId = u.getNodeId();
				
				System.out.println("****MIN: "+minNodeId);
				Tuple2<Integer, Integer> targetTTF = f.get(target);
				if(targetTTF != null && u.getDistance() > targetTTF.b)
					return targetTTF;
				
				if(targetTTF != null && u.getDistance() >= Integer.MAX_VALUE)
					return targetTTF;
				
				Tuple2<Integer, Integer> ttfU = f.get(minNodeId);
				if(ttfU.a < u.getDistance())
					continue;
				
				for (TDArc v : graph.getNeighbors(minNodeId)) {
					if(considerEdge(v)) {
						System.out.println("****CONSIDERING EDGE " +minNodeId+"->"+v.getHeadNode());
						
						int qNew = ttfU.a + ArrayUtils.getMinValue(v.getCosts());
						int rNew = ttfU.b + ArrayUtils.getMaxValue(v.getCosts());
						
						System.out.println("****qNew " +qNew);
						System.out.println("****rNew " +rNew);
						
						Tuple2<Integer, Integer> intervalV = f.get(v.getHeadNode());
						if(intervalV==null) intervalV = new Tuple2<Integer, Integer>(Integer.MAX_VALUE, Integer.MAX_VALUE);
						
						if(qNew > intervalV.b) continue;
						if(rNew < intervalV.a) p.put(v.getHeadNode(), null);
						
						Set<Long> predV = p.get(v.getHeadNode());
						if(predV==null) predV = new THashSet<Long>();
						predV.add(minNodeId);
						p.put(v.getHeadNode(), predV);
						
						if(qNew >= intervalV.a && rNew >= intervalV.b)  continue;
						
						Tuple2<Integer, Integer> newInterval = new Tuple2<Integer, Integer>(
								Math.min(qNew, intervalV.a),
								Math.min(rNew, intervalV.b)
								);
						
						f.put(v.getHeadNode(), newInterval);
						
						queue.add(new NodeEntry(v.getHeadNode(), newInterval.a));
					}
				}
			}
		}
		
		return f.get(target);
	}
}