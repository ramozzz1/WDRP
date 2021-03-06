package org.wdrp.core.algorithm.td;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mapdb.Fun.Tuple2;

public class PIQDijkstraAlgorithmTest extends TDTestBase {
	public static TDDijkstraAlgorithm tdA;
	public static PIQDijkstraAlgorithm piqA;
	
	@Before
	public void setupAlgorithms() {
		tdA = new TDDijkstraAlgorithm(g1);
		piqA = new PIQDijkstraAlgorithm(g1);
	}
	
	private boolean intevalLargerOrEqual(Tuple2<Integer, Integer> intA, Tuple2<Integer, Integer> intB) {
		return intA.a <= intB.a && intA.b >= intB.b;
	}
	
	@Test
	public void computeTravelTimesIntervalSourceSource() {
		Tuple2<Integer, Integer> intervalTD = tdA.computeTravelTimesInterval(0,0);
		Tuple2<Integer, Integer> intervalPIQ = piqA.computeTravelTimesInterval(0,0);
		
		assertTrue(intevalLargerOrEqual(intervalPIQ,intervalTD));
	}
	
	@Test
	public void computeTravelTimesIntervalSourceNeighbour() {
		Tuple2<Integer, Integer> intervalTD = tdA.computeTravelTimesInterval(0,1);
		Tuple2<Integer, Integer> intervalPIQ = piqA.computeTravelTimesInterval(0,1);
		
		assertTrue(intevalLargerOrEqual(intervalPIQ,intervalTD));
		
		intervalTD = tdA.computeTravelTimesInterval(0,2);
		intervalPIQ = piqA.computeTravelTimesInterval(0,2);
		
		assertTrue(intevalLargerOrEqual(intervalPIQ,intervalTD));
		
		intervalTD = tdA.computeTravelTimesInterval(2,0);
		intervalPIQ = piqA.computeTravelTimesInterval(2,0);
		
		assertTrue(intevalLargerOrEqual(intervalPIQ,intervalTD));
		
		intervalTD = tdA.computeTravelTimesInterval(4,5);
		intervalPIQ = piqA.computeTravelTimesInterval(4,5);
		
		assertTrue(intevalLargerOrEqual(intervalPIQ,intervalTD));
	}
	
	@Test
	public void computeTravelTimesIntervalSourceTarget() {
		Tuple2<Integer, Integer> intervalTD = tdA.computeTravelTimesInterval(0,3);
		Tuple2<Integer, Integer> intervalPIQ = piqA.computeTravelTimesInterval(0,3);
		
		assertTrue(intevalLargerOrEqual(intervalPIQ,intervalTD));
		
		intervalTD = tdA.computeTravelTimesInterval(0,4);
		intervalPIQ = piqA.computeTravelTimesInterval(0,4);
		
		assertTrue(intevalLargerOrEqual(intervalPIQ,intervalTD));

		intervalTD = tdA.computeTravelTimesInterval(0,5);
		intervalPIQ = piqA.computeTravelTimesInterval(0,5);
		
		assertTrue(intevalLargerOrEqual(intervalPIQ,intervalTD));
		
		intervalTD = tdA.computeTravelTimesInterval(3,5);
		intervalPIQ = piqA.computeTravelTimesInterval(3,5);
		
		assertTrue(intevalLargerOrEqual(intervalPIQ,intervalTD));
		
		intervalTD = tdA.computeTravelTimesInterval(1,5);
		intervalPIQ = piqA.computeTravelTimesInterval(1,5);
		
		assertTrue(intevalLargerOrEqual(intervalPIQ,intervalTD));
		
		intervalTD = tdA.computeTravelTimesInterval(2,0);
		intervalPIQ = piqA.computeTravelTimesInterval(2,0);
		
		assertTrue(intevalLargerOrEqual(intervalPIQ,intervalTD));
	}
	
	@Test
	public void testTTIntervalSourceTargetDynamicAllDPTimesOnTwoMinGraph() {		
		Tuple2<Integer, Integer> intervalPIQ;
		
		tdA = new TDDijkstraAlgorithm(tdGraphTwoMin);
		piqA = new PIQDijkstraAlgorithm(tdGraphTwoMin);
		
		Set<Long> nodes = piqA.graph.nodes.keySet();
		for (Long u : nodes) {
			for (Long v : nodes) {
				intervalPIQ = piqA.computeTravelTimesInterval(u, v);
				assertTrue(intevalLargerOrEqual(intervalPIQ,tdA.computeTravelTimesInterval(u, v)));
			}
		}
	}
}
