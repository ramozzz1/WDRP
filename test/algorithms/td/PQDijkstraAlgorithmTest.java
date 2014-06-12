package algorithms.td;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import algorithm.td.PQDijkstraAlgorithm;
import algorithm.td.TDDijkstraAlgorithm;

public class PQDijkstraAlgorithmTest extends TDTestBase {
	
	public static TDDijkstraAlgorithm tdA;
	public static PQDijkstraAlgorithm psA;
	
	@Before
	public void setupAlgorithms() {
		tdA = new TDDijkstraAlgorithm(g);
		psA = new PQDijkstraAlgorithm(g);
	}
	
	@Test
	public void computeTravelTimesSourceSourceAllDepartureTimes() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,0);
		int[] travelTimesPSA = psA.computeTravelTimes(0,0);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeTravelTimesSourceNeighbourAllDepartureTimes() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,1);
		int[] travelTimesPSA = psA.computeTravelTimes(0,1);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,2);
		travelTimesPSA = psA.computeTravelTimes(0,2);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(2,0);
		travelTimesPSA = psA.computeTravelTimes(2,0);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(4,5);
		travelTimesPSA = psA.computeTravelTimes(4,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(1,2);
		travelTimesPSA = psA.computeTravelTimes(1,2);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeTravelTimesSourceTargetAllDepartureTimes() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,3);
		int[] travelTimesPSA = psA.computeTravelTimes(0,3);
		
		System.out.println(Arrays.toString(travelTimesTDA));
		System.out.println(Arrays.toString(travelTimesPSA));
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,4);
		travelTimesPSA = psA.computeTravelTimes(0,4);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));

		travelTimesTDA = tdA.computeTravelTimes(0,5);
		travelTimesPSA = psA.computeTravelTimes(0,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(3,5);
		travelTimesPSA = psA.computeTravelTimes(3,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(1,5);
		travelTimesPSA = psA.computeTravelTimes(1,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeTravelTimesSourceSourceDepartureTimesInterval() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,0,0,4);
		int[] travelTimesPSA = psA.computeTravelTimes(0,0,0,4);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,0,16,20);
		travelTimesPSA = psA.computeTravelTimes(0,0,16,20);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,0,4,9);
		travelTimesPSA = psA.computeTravelTimes(0,0,4,9);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeTravelTimesSourceNeighbourDepartureTimesInterval() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,1,0,5);
		int[] travelTimesPSA = psA.computeTravelTimes(0,1,0,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,2,5,10);
		travelTimesPSA = psA.computeTravelTimes(0,2,5,10);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(2,0,5,10);
		travelTimesPSA = psA.computeTravelTimes(2,0,5,10);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(4,5,7,20);
		travelTimesPSA = psA.computeTravelTimes(4,5,7,20);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeTravelTimesSourceTargetDepartureTimesInterval() {
		int[] travelTimesTDA = tdA.computeTravelTimes(0,3,9,20);
		int[] travelTimesPSA = psA.computeTravelTimes(0,3,9,20);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(0,4,3,5);
		travelTimesPSA = psA.computeTravelTimes(0,4,3,5);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));

		travelTimesTDA = tdA.computeTravelTimes(0,5,1,15);
		travelTimesPSA = psA.computeTravelTimes(0,5,1,15);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(3,5,10,15);
		travelTimesPSA = psA.computeTravelTimes(3,5,10,15);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
		
		travelTimesTDA = tdA.computeTravelTimes(1,5,15,20);
		travelTimesPSA = psA.computeTravelTimes(1,5,15,20);
		
		assertEquals(Arrays.toString(travelTimesTDA), Arrays.toString(travelTimesPSA));
	}
	
	@Test
	public void computeBestDepartureTimeSourceSourceAllDepartureTimes() {
		int departureTimeTDA = tdA.computeBestDepartureTime(0,0);
		int departureTimePSA = psA.computeBestDepartureTime(0,0);
		
		assertEquals(departureTimeTDA, departureTimePSA);
	}
	
	@Test
	public void computeBestDepartureTimeSourceNeighbourAllDepartureTimes() {
		int departureTimeTDA = tdA.computeBestDepartureTime(0,1);
		int departureTimePSA = psA.computeBestDepartureTime(0,1);
		
		assertEquals(departureTimeTDA, departureTimePSA);
	}
	
	@Test
	public void computeBestDepartureTimeSourceTargetAllDepartureTimes() {
		int departureTimeTDA = tdA.computeBestDepartureTime(0,5);
		int departureTimePSA = psA.computeBestDepartureTime(0,5);
		
		assertEquals(departureTimeTDA, departureTimePSA);
	}
}
