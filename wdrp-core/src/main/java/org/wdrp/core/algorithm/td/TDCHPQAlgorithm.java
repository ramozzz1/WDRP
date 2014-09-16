package org.wdrp.core.algorithm.td;

import gnu.trove.set.hash.THashSet;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

import org.wdrp.core.model.NodeEntry;
import org.wdrp.core.model.TDArc;
import org.wdrp.core.model.TDGraph;
import org.wdrp.core.util.ArrayUtils;

public class TDCHPQAlgorithm extends TDCHAlgorithm {

	public static PQDijkstraAlgorithm pqSource;
	public static PQDijkstraAlgorithm pqTarget;
	
	public TDCHPQAlgorithm(TDGraph graph) {
		super(graph);
		
		this.considerArcFlags = true;
		this.considerShortcuts = true;
		
		pqSource = new PQDijkstraAlgorithm(graph, this.considerArcFlags, this.considerShortcuts);
		pqTarget = new PQDijkstraAlgorithm(graph, this.considerArcFlags, this.considerShortcuts);
	}
	
	public int[] computeTravelTimes(long source, long target) {
		if(source != NULL_NODE) {
			int B = Integer.MAX_VALUE; //upperbound
			candidates = new THashSet<Long>();
			
			pqSource.init(source);
			pqTarget.init(target);
			
			Queue<NodeEntry> queueS = pqSource.queue;
			Queue<NodeEntry> queueT = pqTarget.queue;
			
			System.out.println("PQSOURCE QUEUE " + queueS);
			System.out.println("PQTARGET QUEUE " + queueT);
			int iterations = 0;
		
			while((!queueS.isEmpty() || !queueT.isEmpty()) 
					&& (Math.min(!queueS.isEmpty( )? queueS.peek().getDistance():Integer.MAX_VALUE, !queueT.isEmpty( )? queueT.peek().getDistance():Integer.MAX_VALUE) <= B)) {
				System.out.println("ITERATION " +iterations);
				boolean reverseDirection = (iterations%2 == 1);
				if((reverseDirection && queueT.isEmpty())
						|| (!reverseDirection && queueS.isEmpty()))
					reverseDirection = !reverseDirection;
				
				NodeEntry u = reverseDirection ? queueT.poll() : queueS.poll();
				
				System.out.println("MIN " +u);
				int[] ttfUSource = pqSource.f.get(u.getNodeId());
				if(ttfUSource==null) ttfUSource = ArrayUtils.extrapolateArray(new int[]{Integer.MAX_VALUE}, ((TDGraph)pqSource.graph).getMaxTime());
				int[] ttfUTarget = pqTarget.f.get(u.getNodeId());
				if(ttfUTarget==null) ttfUTarget = ArrayUtils.extrapolateArray(new int[]{Integer.MAX_VALUE}, ((TDGraph)pqTarget.graph).getMaxTime());
				
				System.out.println("ttfUSource " +Arrays.toString(ttfUSource)+ " MAX "+  ArrayUtils.getMaxValue(ttfUSource));
				System.out.println("ttfUTarget " +Arrays.toString(ttfUTarget) + " MAX "+  ArrayUtils.getMaxValue(ttfUTarget));
				
				//fix for double entries in the queues
				if(!reverseDirection && ArrayUtils.getMaxValue(ttfUSource) < u.getDistance()) {
					System.out.println("CONTINUE");
					continue;
				}
				if(reverseDirection && ArrayUtils.getMaxValue(ttfUTarget) < u.getDistance()) {
					System.out.println("CONTINUE");
					continue;
				}
				
				B = (int) Math.min(B, (long)ArrayUtils.getMaxValue(ttfUSource) + (long)ArrayUtils.getMaxValue(ttfUTarget));
				
				System.out.println("A "+(long)ArrayUtils.getMaxValue(ttfUSource) + (long)ArrayUtils.getMaxValue(ttfUTarget));
				System.out.println("B "+B);
				
				if(B < Integer.MAX_VALUE 
						&& (((long)ArrayUtils.getMinValue(ttfUSource) + (long)ArrayUtils.getMinValue(ttfUTarget)) <= B)) {
					System.out.println("NEW CANDIDATE "+u.getNodeId());
					candidates.add(u.getNodeId());
				}
				
				if(!reverseDirection) {
					for (TDArc arc : graph.getNeighbors(u.getNodeId())) {
						if(considerArc(arc)) {
							System.out.println("ARC " +arc);
							System.out.println("LINKING " +Arrays.toString(ttfUSource) + " "+Arrays.toString(arc.getCosts()));
							int[] gNew = ttfUSource==null ? arc.getCosts() : ArrayUtils.linkLists(ttfUSource, arc.getCosts(), ((TDGraph)pqSource.graph).getInterval());
							pqSource.relax(u.getNodeId(), gNew, arc);
						}
					}
				}
				else {
					for (TDArc arc : graph.getNeighbors(u.getNodeId())) {
						if(considerArc(arc)) {
							System.out.println("ARC " +arc);
							System.out.println("LINKING " +Arrays.toString(arc.getCosts()) + " "+Arrays.toString(ttfUTarget));
							int[] gNew = ttfUTarget==null ? arc.getCosts() : ArrayUtils.linkLists(graph.getArc(arc.getHeadNode(), u.getNodeId()).getCosts(),ttfUTarget, ((TDGraph)pqTarget.graph).getInterval());
							System.out.println("gNew " +Arrays.toString(gNew));
							pqTarget.relax(u.getNodeId(), gNew, arc);
						}
					}
				}
				
				System.out.println("PQSOURCE QUEUE " + queueS);
				System.out.println("PQTARGET QUEUE " + queueT);
				
				iterations++;
			}
			System.out.println("CANDIDATES " + candidates);
			return merge();
		}
		
		return null;
	}
	
	private int[] merge() {
		Queue<TTFEntry> pq = new PriorityQueue<TTFEntry>();
		for (Long x : candidates) {
			int[] ttfXSource = pqSource.f.get(x);
			int[] ttfXTarget = pqTarget.f.get(x);
			
			System.out.println("ttfXSource: "+Arrays.toString(ttfXSource));
			System.out.println("ttfXTarget: "+Arrays.toString(ttfXTarget));
			
			int[] ttf = ArrayUtils.linkLists(ttfXSource, ttfXTarget, ((TDGraph)graph).getInterval());
			
			System.out.println("LINKED: "+Arrays.toString(ttf));
			pq.add(new TTFEntry(ttf, ttf.length));
		}
		
		while(pq.size() > 0) {
			int[] ttfMin1 = pq.poll().getTTF();
			if(pq.size() > 0) {
				int[] ttfMin2 = pq.poll().getTTF();
				
				int[] minTTF = ArrayUtils.minList(ttfMin1, ttfMin2);
				
				pq.add(new TTFEntry(minTTF, minTTF.length));
			}
			else {
				return ttfMin1;
			}
		}
		
		return null;
	}

	@Override
	public int computeEarliestArrivalTime(long source, long target, int departureTime) {
		int eaTimes[] = computeEarliestArrivalTimes(source, target);
		
		return eaTimes[departureTime];
	}
}

class TTFEntry implements Comparable<TTFEntry>
{	
	private int[] ttf;
	private int distance;

	public TTFEntry(int[] ttf, int distance) {
		this.ttf = ttf;
		this.distance = distance;
	}
	
	public int[] getTTF() {
		return ttf;
	}

	public int getDistance() {
		return distance;
	}

	@Override
	public int hashCode() {
		return this.ttf.hashCode();
	}
	
	@Override
	public int compareTo(TTFEntry o) {
		return Integer.compare(this.distance, o.getDistance());
	}
	
	@Override
	public String toString() {
		return "nodeId: "+ttf+", cost: "+distance;
	}
}
