package org.wdrp.core.algorithm.td;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class PQDijkstraAlgorithmTest extends TDTestBase {
	
	public static TDDijkstraAlgorithm tdA;
	public static PQDijkstraAlgorithm pqA;
	
	@Before
	public void setupAlgorithms() {
		tdA = new TDDijkstraAlgorithm(g);
		pqA = new PQDijkstraAlgorithm(g);
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
		int departureTimeTDA = tdA.computeBestDepartureTime(0,0);
		int departureTimePSA = pqA.computeBestDepartureTime(0,0);
		
		assertEquals(departureTimeTDA, departureTimePSA);
	}
	
	@Test
	public void computeBestDepartureTimeSourceNeighbourAllDepartureTimes() {
		int departureTimeTDA = tdA.computeBestDepartureTime(0,1);
		int departureTimePSA = pqA.computeBestDepartureTime(0,1);
		
		assertEquals(departureTimeTDA, departureTimePSA);
	}
	
	@Test
	public void computeBestDepartureTimeSourceTargetAllDepartureTimes() {
		int departureTimeTDA = tdA.computeBestDepartureTime(0,5);
		int departureTimePSA = pqA.computeBestDepartureTime(0,5);
		
		assertEquals(departureTimeTDA, departureTimePSA);
	}
	
	@Test
	public void testEATimeSourceTargetDynamicAllDPTimes() {		
		int[] eaTimes;
		
		Set<Long> nodes = pqA.graph.nodes.keySet();
		for (Long u : nodes) {
			for (Long v : nodes) {
				System.out.println("<> "+u+" "+v);
				eaTimes = pqA.computeTravelTimes(u, v);
				assertEquals(Arrays.toString(eaTimes), Arrays.toString(tdA.computeTravelTimes(u, v)));
			}
		}
	}
}
