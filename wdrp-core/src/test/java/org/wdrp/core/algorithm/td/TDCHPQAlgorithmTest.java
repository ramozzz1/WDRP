package org.wdrp.core.algorithm.td;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.wdrp.core.model.TDGraph;

public class TDCHPQAlgorithmTest extends TDTestBase {
	
	public static TDDijkstraAlgorithm tdA;
	public static TDCHPQAlgorithm tdCHPQ;
	
	@Before
	public void setupAlgorithms() {
		tdA = new TDDijkstraAlgorithm(g1);
		tdCHPQ = new TDCHPQAlgorithm(g1);
		
		tdCHPQ.precompute();
	}
	
	@Test
	public void computeTravelTimesSourceSourceAllDepartureTimes() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,0);
		int[] travelTimesPSA = tdCHPQ.computeTravelTimes(0,0);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeTravelTimesSourceNeighbourAllDepartureTimes() {
		int[] travelTimesTDA,travelTimesPSA;
		
		travelTimesTDA = tdA.computeTravelTimes(5,0);
		travelTimesPSA = tdCHPQ.computeTravelTimes(5,0);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(6,0);
		travelTimesPSA = tdCHPQ.computeTravelTimes(6,0);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,1);
		travelTimesPSA = tdCHPQ.computeTravelTimes(0,1);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(1,0);
		travelTimesPSA = tdCHPQ.computeTravelTimes(1,0);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,2);
		travelTimesPSA = tdCHPQ.computeTravelTimes(0,2);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(2,0);
		travelTimesPSA = tdCHPQ.computeTravelTimes(2,0);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(4,5);
		travelTimesPSA = tdCHPQ.computeTravelTimes(4,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(1,2);
		travelTimesPSA = tdCHPQ.computeTravelTimes(1,2);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,5);
		travelTimesPSA = tdCHPQ.computeTravelTimes(0,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,6);
		travelTimesPSA = tdCHPQ.computeTravelTimes(0,6);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void testAllTravelTimesAllNodesAllDPTimes() {		
		int[] travelTimes;
		tdCHPQ.precompute();
		
		Set<Long> nodes = tdCHPQ.graph.nodes.keySet();
		for (Long u : nodes) {
			for (Long v : nodes) {
				travelTimes = tdCHPQ.computeTravelTimes(u, v);
				assertEquals(Arrays.toString(travelTimes), Arrays.toString(tdA.computeTravelTimes(u, v)));
			}
		}
	}
	
	@Test
	public void testEATimeSourceTargetDynamicAllDPTimesOnTwoMinGraph() {		
		int[] travelTimes;
		
		tdCHPQ = new TDCHPQAlgorithm(tdGraphTwoMin);
		tdA = new TDDijkstraAlgorithm(tdGraphTwoMin);
				
		tdCHPQ.precompute();
		
		Set<Long> nodes = tdCHPQ.graph.nodes.keySet();
		for (Long u : nodes) {
			for (Long v : nodes) {
				travelTimes = tdCHPQ.computeTravelTimes(u, v);
				assertEquals(Arrays.toString(travelTimes), Arrays.toString(tdA.computeTravelTimes(u, v)));
			}
		}
	}
	
	@Test
	public void testTravelTimesSourceTargetDynamicAllDPTimesOnTDGraphGeneratedFromWeather() throws FileNotFoundException, ParseException {		
		int[] travelTimes;
		
		TDGraph tdGraph = getTestTDGraphGeneratedFromWeahter();
		assertEquals(2, tdGraph.getMaxTime());
		assertEquals(300, tdGraph.getInterval());
		
		tdA = new TDDijkstraAlgorithm(tdGraph);
		tdCHPQ = new TDCHPQAlgorithm(tdGraph);
		
		tdCHPQ.precompute();
		
		Set<Long> nodes = tdCHPQ.graph.nodes.keySet();
		for (Long u : nodes) {
			for (Long v : nodes) {
				System.out.println(u+"<>"+v);
				travelTimes = tdCHPQ.computeTravelTimes(u, v);
				assertEquals(Arrays.toString(travelTimes), Arrays.toString(tdA.computeTravelTimes(u, v)));
			}
		}
	}
}
