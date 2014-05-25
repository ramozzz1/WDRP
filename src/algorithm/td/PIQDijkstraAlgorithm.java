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
	
	public PIQDijkstraAlgorithm(TDGraph graph, boolean considerArcFlags, boolean considerShortcuts) {
		super(graph, considerArcFlags, considerShortcuts);
	}

	@Override
	public void init(long source) {
		this.f = new THashMap<Long, Tuple2<Integer, Integer>>();
		this.p = new THashMap<Long, Set<Long>>();
		this.queue = new PriorityQueue<NodeEntry>();
		
		f.put(source, new Tuple2<Integer, Integer>(0,0));
		p.put(source, null);
		queue.add(new NodeEntry(source, 0));
	}
	
	public Tuple2<Integer, Integer> computeTravelTimesInterval(long source, long target) {
		if(source != NULL_NODE) {
			
			init(source);
			
			while(!queue.isEmpty()) {
				NodeEntry u = queue.poll();
				long minNodeId = u.getNodeId();
				
				System.out.println("****MIN: "+minNodeId);
				Tuple2<Integer, Integer> targetInterval = f.get(target);
				if(targetInterval!= null && u.getDistance() > targetInterval.b)
					return targetInterval;
				
				if(u.getDistance() >= Integer.MAX_VALUE)
					return targetInterval;
				
				Tuple2<Integer, Integer> intervalU = f.get(minNodeId);
				if( intervalU.a < u.getDistance())
					continue;
				
				for (TDArc arc : graph.getNeighbors(minNodeId)) {
					relax(minNodeId, intervalU, arc);
				}
			}
		}
		
		return f.get(target);
	}

	public void relax(long u,
			Tuple2<Integer, Integer> intervalU, TDArc arc) {
		if(considerArc(arc)) {
			System.out.println("----CONSIDERING EDGE " +u+"->"+arc.getHeadNode());
			
			int qNew = intervalU.a + ArrayUtils.getMinValue(arc.getCosts());
			int rNew = intervalU.b + ArrayUtils.getMaxValue(arc.getCosts());
			
			System.out.println("qNew " +qNew);
			System.out.println("rNew " +rNew);
			
			Tuple2<Integer, Integer> intervalV = f.get(arc.getHeadNode());
			if(intervalV==null) intervalV = new Tuple2<Integer, Integer>(Integer.MAX_VALUE, Integer.MAX_VALUE);
			
			if(qNew > intervalV.b)
				return;
			if(rNew < intervalV.a) p.put(arc.getHeadNode(), null);
			
			Set<Long> predV = p.get(arc.getHeadNode());
			if(predV==null) predV = new THashSet<Long>();
			predV.add(u);
			p.put(arc.getHeadNode(), predV);
			
			if(qNew >= intervalV.a && rNew >= intervalV.b)
				return;
			
			Tuple2<Integer, Integer> newInterval = new Tuple2<Integer, Integer>(
					Math.min(qNew, intervalV.a),
					Math.min(rNew, intervalV.b)
					);
			System.out.println("UPDATED NODE "+arc.getHeadNode()+" TO "+newInterval);
			
			f.put(arc.getHeadNode(), newInterval);
			
			queue.add(new NodeEntry(arc.getHeadNode(), newInterval.a));
		}
	}
}