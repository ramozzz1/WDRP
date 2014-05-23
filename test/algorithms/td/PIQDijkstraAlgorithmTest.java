package algorithms.td;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mapdb.Fun.Tuple2;

import algorithm.td.PIQDijkstraAlgorithm;
import algorithm.td.TDDijkstraAlgorithm;

public class PIQDijkstraAlgorithmTest extends TDTestBase {
	public static TDDijkstraAlgorithm tdA;
	public static PIQDijkstraAlgorithm piqA;
	
	@BeforeClass
	public static void setupAlgorithms() {
		tdA = new TDDijkstraAlgorithm(g);
		piqA = new PIQDijkstraAlgorithm(g);
	}
	
	@Test
	public void computeTravelTimesIntervalSourceSourceAllDepartureTimes() {
		Tuple2<Integer, Integer> intervalTD = tdA.computeTravelTimesInterval(0,0);
		Tuple2<Integer, Integer> intervalPIQ = piqA.computeTravelTimesInterval(0,0);
		
		assertEquals(intervalPIQ.toString(), intervalTD.toString());
	}
	
	@Test
	public void computeTravelTimesIntervalSourceNeighbourAllDepartureTimes() {
//		Tuple2<Integer, Integer> intervalTD = tdA.computeTravelTimesInterval(0,1);
//		Tuple2<Integer, Integer> intervalPIQ = piqA.computeTravelTimesInterval(0,1);
		
//		assertEquals(intervalPIQ.toString(), intervalTD.toString());
//		
//		intervalTD = tdA.computeTravelTimesInterval(0,2);
//		intervalPIQ = piqA.computeTravelTimesInterval(0,2);
//		
//		assertEquals(intervalPIQ.toString(), intervalTD.toString());
		
		Tuple2<Integer, Integer> intervalTD = tdA.computeTravelTimesInterval(2,0);
		Tuple2<Integer, Integer> intervalPIQ = piqA.computeTravelTimesInterval(2,0);
		
		assertEquals(intervalPIQ.toString(), intervalTD.toString());
		
//		intervalTD = tdA.computeTravelTimesInterval(4,5);
//		intervalPIQ = piqA.computeTravelTimesInterval(4,5);
//		
//		assertEquals(intervalPIQ.toString(), intervalTD.toString());
	}
	
	@Test
	public void computeTravelTimesIntervalSourceTargetAllDepartureTimes() {
		Tuple2<Integer, Integer> intervalTD = tdA.computeTravelTimesInterval(0,3);
		Tuple2<Integer, Integer> intervalPIQ = piqA.computeTravelTimesInterval(0,3);
		
		assertEquals(intervalPIQ.toString(), intervalTD.toString());
		
		intervalTD = tdA.computeTravelTimesInterval(0,4);
		intervalPIQ = piqA.computeTravelTimesInterval(0,4);
		
		assertEquals(intervalPIQ.toString(), intervalTD.toString());

		intervalTD = tdA.computeTravelTimesInterval(0,5);
		intervalPIQ = piqA.computeTravelTimesInterval(0,5);
		
		assertEquals(intervalPIQ.toString(), intervalTD.toString());
		
		intervalTD = tdA.computeTravelTimesInterval(3,5);
		intervalPIQ = piqA.computeTravelTimesInterval(3,5);
		
		assertEquals(intervalPIQ.toString(), intervalTD.toString());
		
		intervalTD = tdA.computeTravelTimesInterval(1,5);
		intervalPIQ = piqA.computeTravelTimesInterval(1,5);
		
		assertEquals(intervalPIQ.toString(), intervalTD.toString());
	}
}
