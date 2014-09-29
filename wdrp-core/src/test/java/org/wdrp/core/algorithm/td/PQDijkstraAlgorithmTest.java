package org.wdrp.core.algorithm.td;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.wdrp.core.model.TDGraph;

public class PQDijkstraAlgorithmTest extends TDTestBase {
	
	public static TDDijkstraAlgorithm tdA;
	public static PQDijkstraAlgorithm pqA;
	
	@Before
	public void setupAlgorithms() {
		tdA = new TDDijkstraAlgorithm(g1);
		pqA = new PQDijkstraAlgorithm(g1);
	}
	
	@Test
	public void computeTravelTimesSourceSourceAllDepartureTimes() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,0);
		int[] travelTimesPSA = pqA.computeTravelTimes(0,0);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeTravelTimesSourceNeighbourAllDepartureTimes() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,1);
		int[] travelTimesPSA = pqA.computeTravelTimes(0,1);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,2);
		travelTimesPSA = pqA.computeTravelTimes(0,2);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(2,0);
		travelTimesPSA = pqA.computeTravelTimes(2,0);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(4,5);
		travelTimesPSA = pqA.computeTravelTimes(4,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(1,2);
		travelTimesPSA = pqA.computeTravelTimes(1,2);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeTravelTimesSourceTargetAllDepartureTimes() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,3);
		int[] travelTimesPSA = pqA.computeTravelTimes(0,3);
		
		System.out.println(Arrays.toString(travelTimesTDA));
		System.out.println(Arrays.toString(travelTimesPSA));
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,4);
		travelTimesPSA = pqA.computeTravelTimes(0,4);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));

		travelTimesTDA = tdA.computeTravelTimes(0,5);
		travelTimesPSA = pqA.computeTravelTimes(0,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(3,5);
		travelTimesPSA = pqA.computeTravelTimes(3,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(1,5);
		travelTimesPSA = pqA.computeTravelTimes(1,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeTravelTimesSourceSourceDepartureTimesInterval() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,0,0,4);
		int[] travelTimesPSA = pqA.computeTravelTimes(0,0,0,4);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,0,16,20);
		travelTimesPSA = pqA.computeTravelTimes(0,0,16,20);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,0,4,9);
		travelTimesPSA = pqA.computeTravelTimes(0,0,4,9);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeTravelTimesSourceNeighbourDepartureTimesInterval() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,1,0,5);
		int[] travelTimesPSA = pqA.computeTravelTimes(0,1,0,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,2,5,10);
		travelTimesPSA = pqA.computeTravelTimes(0,2,5,10);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(2,0,5,10);
		travelTimesPSA = pqA.computeTravelTimes(2,0,5,10);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(4,5,7,20);
		travelTimesPSA = pqA.computeTravelTimes(4,5,7,20);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeTravelTimesSourceTargetDepartureTimesInterval() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,3,9,20);
		int[] travelTimesPSA = pqA.computeTravelTimes(0,3,9,20);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,4,3,5);
		travelTimesPSA = pqA.computeTravelTimes(0,4,3,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));

		travelTimesTDA = tdA.computeTravelTimes(0,5,1,15);
		travelTimesPSA = pqA.computeTravelTimes(0,5,1,15);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(3,5,10,15);
		travelTimesPSA = pqA.computeTravelTimes(3,5,10,15);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(1,5,15,20);
		travelTimesPSA = pqA.computeTravelTimes(1,5,15,20);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeBestDepartureTimeSourceSourceAllDepartureTimes() {
		int departureTimeTDA = tdA.computeDepartureTime(0,0);
		int departureTimePSA = pqA.computeDepartureTime(0,0);
		
		assertEquals(departureTimeTDA, departureTimePSA);
	}
	
	@Test
	public void computeBestDepartureTimeSourceNeighbourAllDepartureTimes() {
		int departureTimeTDA = tdA.computeDepartureTime(0,1);
		int departureTimePSA = pqA.computeDepartureTime(0,1);
		
		assertEquals(departureTimeTDA, departureTimePSA);
	}
	
	@Test
	public void computeBestDepartureTimeSourceTargetAllDepartureTimes() {
		int departureTimeTDA = tdA.computeDepartureTime(0,5);
		int departureTimePSA = pqA.computeDepartureTime(0,5);
		
		assertEquals(departureTimeTDA, departureTimePSA);
	}
	
	@Test
	public void testEATimeSourceTargetDynamicAllDPTimes() {		
		int[] eaTimes;
		
		Set<Long> nodes = pqA.graph.nodes.keySet();
		for (Long u : nodes) {
			for (Long v : nodes) {
				eaTimes = pqA.computeTravelTimes(u, v);
				assertEquals(Arrays.toString(eaTimes), Arrays.toString(tdA.computeTravelTimes(u, v)));
			}
		}
	}
	
	@Test
	public void testTravelTimesSourceTargetDynamicAllDPTimesOnTwoMinGraph() {		
		int[] travelTimes;
		
		tdA = new TDDijkstraAlgorithm(tdGraphTwoMin);
		pqA = new PQDijkstraAlgorithm(tdGraphTwoMin);
		
		Set<Long> nodes = pqA.graph.nodes.keySet();
		for (Long u : nodes) {
			for (Long v : nodes) {
				travelTimes = pqA.computeTravelTimes(u, v);
				int max = ((TDGraph)pqA.graph).getMaxTime();
				for (int i = 0; i < max; i++) {
					System.out.println(u+"<>"+v + " @"+i);
					assertEquals(travelTimes[i],tdA.computeTravelTime(u, v, i));
					//System.out.println(travelTimes[i]);
					//assertEquals(pqA.contructPath(null, v, i, true).toString(), tdA.contructPath(tdA.p, v).toString());
				}
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
		pqA = new PQDijkstraAlgorithm(tdGraph);
		
		Set<Long> nodes = pqA.graph.nodes.keySet();
		for (Long u : nodes) {
			for (Long v : nodes) {
				System.out.println(u+"<>"+v);
				travelTimes = pqA.computeTravelTimes(u, v);
				assertEquals(Arrays.toString(travelTimes), Arrays.toString(tdA.computeTravelTimes(u, v)));
			}
		}
	}
	
	@Test
	public void testContructPath() {
		tdA = new TDDijkstraAlgorithm(tdGraphTwoMin);
		pqA = new PQDijkstraAlgorithm(tdGraphTwoMin);
		
		int travelTimeTDA = tdA.computeTravelTime(0,5,0);
		int[] travelTimesPSA = pqA.computeTravelTimes(0,5);
		System.out.println(travelTimeTDA);
		System.out.println(tdA.contructPath(tdA.p, 5));
		System.out.println(pqA.contructPath(null, 5, 0, true));
		//assertEquals(tdA.contructPath(tdA.p, 1).toString(), Arrays.toString(travelTimesPSA));
	}
}
