package algorithms.td;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import algorithm.td.TDCHPQAlgorithm;
import algorithm.td.TDDijkstraAlgorithm;

public class TDCHPQAlgorithmTest extends TDTestBase {
	
	public static TDDijkstraAlgorithm tdA;
	public static TDCHPQAlgorithm tdCHPQ;
	
	@Before
	public void setupAlgorithms() {
		tdA = new TDDijkstraAlgorithm(g);
		tdCHPQ = new TDCHPQAlgorithm(g);
		
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
}
