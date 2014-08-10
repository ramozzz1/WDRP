package org.wdrp.core.algorithm.td;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class TDDijkstraAlgorithmTest extends TDTestBase {
	
	@Test
	public void testTDEdgeCost() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		
		int cost = a.getEdgeCost(g.getArc(0, 1), 0);
		assertEquals(cost, 4);
		
		cost = a.getEdgeCost(g.getArc(0, 1), 1);
		assertEquals(cost, 5);
		
		cost = a.getEdgeCost(g.getArc(0, 1), 2);
		assertEquals(cost, 6);
		
		cost = a.getEdgeCost(g.getArc(0, 1), 3);
		assertEquals(cost, 7);
		
		cost = a.getEdgeCost(g.getArc(0, 1), 4);
		assertEquals(cost, 8);
		
		cost = a.getEdgeCost(g.getArc(0, 1), 5);
		assertEquals(cost, 5+5);
		
		cost = a.getEdgeCost(g.getArc(0, 1), 6);
		assertEquals(cost, 5+6);
		
		cost = a.getEdgeCost(g.getArc(0, 1), 10);
		assertEquals(cost, 10+9);
		
		cost = a.getEdgeCost(g.getArc(0, 1), 19);
		assertEquals(cost, 19+4);
		
		cost = a.getEdgeCost(g.getArc(0, 1), 20);
		assertEquals(cost, Integer.MAX_VALUE);
	}
	
	@Test
	public void computeSPSourceSourceValidDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,0,0);
		assertEquals(arrivalTime,0);
	}
	
	@Test
	public void computeSPSourceSourceInvalidDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,0,200);
		assertEquals(arrivalTime, 200);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,1,0);
		
		assertEquals(arrivalTime, 4);
		assertEquals(a.getVisitedNodes().toString(), "{1, 0}");
	}
	
	@Test
	public void computeSPSourceTargetInvalidDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,1,100);
		assertEquals(arrivalTime,-1);
	}
	
	@Test
	public void computeSPSourceTargetLateDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,1,19);
		assertEquals(arrivalTime,23);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime2() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,2,0);
		assertEquals(arrivalTime,7);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime3() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,2,1);
		assertEquals(arrivalTime,9);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime4() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,2,4);
		assertEquals(arrivalTime,12);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime5() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,5,0);
		assertEquals(arrivalTime,19);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime6() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,5,1);
		assertEquals(arrivalTime,25);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime7() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,5,2);
		assertEquals(arrivalTime,25);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime8() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,5,3);
		assertEquals(arrivalTime,26);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime9() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int arrivalTime = a.computeEarliestArrivalTime(0,5,5);
		assertEquals(arrivalTime,-1);
	}
	
	@Test
	public void computeEATimes() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int[] eaTimes = a.computeEarliestArrivalTimes(0, 5, 0, 3);
		assertEquals(Arrays.toString(eaTimes), "[19, 25, 25, 26]");
	}
	
	@Test
	public void computeBestDepartureTime1() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int bestDepartureTime = a.computeBestDepartureTime(0, 5, 0, 5);
		assertEquals(bestDepartureTime, 0);
	}
	
	@Test
	public void computeBestDepartureTime2() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int bestDepartureTime = a.computeBestDepartureTime(0, 5, 1, 3);
		assertEquals(bestDepartureTime, 2);
	}
	
	@Test
	public void computeBestDepartureTime3() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int bestDepartureTime = a.computeBestDepartureTime(0, 5, 2, 3);
		assertEquals(bestDepartureTime, 2);
	}
}
