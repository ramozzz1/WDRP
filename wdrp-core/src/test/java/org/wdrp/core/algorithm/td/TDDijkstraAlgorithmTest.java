package org.wdrp.core.algorithm.td;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.wdrp.core.model.TDArc;
import org.wdrp.core.model.TDGraph;

public class TDDijkstraAlgorithmTest extends TDTestBase {
	
	@Test
	public void testTDEdgeCost() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		
		int cost = a.getEdgeCost(g1.getArc(0, 1), 0);
		assertEquals(cost, 4);
		
		cost = a.getEdgeCost(g1.getArc(0, 1), 1);
		assertEquals(cost, 5);
		
		cost = a.getEdgeCost(g1.getArc(0, 1), 2);
		assertEquals(cost, 6);
		
		cost = a.getEdgeCost(g1.getArc(0, 1), 3);
		assertEquals(cost, 7);
		
		cost = a.getEdgeCost(g1.getArc(0, 1), 4);
		assertEquals(cost, 8);
		
		cost = a.getEdgeCost(g1.getArc(0, 1), 5);
		assertEquals(cost, 5+5);
		
		cost = a.getEdgeCost(g1.getArc(0, 1), 6);
		assertEquals(cost, 5+6);
		
		cost = a.getEdgeCost(g1.getArc(0, 1), 10);
		assertEquals(cost, 10+9);
		
		cost = a.getEdgeCost(g1.getArc(0, 1), 19);
		assertEquals(cost, 19+4);
		
		cost = a.getEdgeCost(g1.getArc(0, 1), 20);
		assertEquals(cost, 24);
	}
	
	@Test
	public void testTDEdgeCost1() {
		TDGraph tdg = new TDGraph(60,2);
		tdg.addNode(0);
		tdg.addNode(1);
		
		int[] costs = {10,20};
		tdg.addEdge(0, new TDArc(1, costs));
	
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(tdg);
		
		int cost;
		
		cost = a.getEdgeCost(tdg.getArc(0, 1), 0);
		assertEquals(10, cost);
		
		cost = a.getEdgeCost(tdg.getArc(0, 1), 30);
		assertEquals(40, cost);
		
		cost = a.getEdgeCost(tdg.getArc(0, 1), 59);
		assertEquals(69, cost);
		
		cost = a.getEdgeCost(tdg.getArc(0, 1), 60);
		assertEquals(80, cost);
		
		cost = a.getEdgeCost(tdg.getArc(0, 1), 90);
		assertEquals(110, cost);
		
		cost = a.getEdgeCost(tdg.getArc(0, 1), 119);
		assertEquals(139, cost);
		
		cost = a.getEdgeCost(tdg.getArc(0, 1), 120);
		assertEquals(140, cost);
		
		cost = a.getEdgeCost(tdg.getArc(0, 1), 130);
		assertEquals(150, cost);
		
		cost = a.getEdgeCost(tdg.getArc(0, 1), 180);
		assertEquals(200, cost);
	}
	
	@Test
	public void computeSPSourceSourceValidDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,0,0);
		assertEquals(arrivalTime,0);
	}
	
	@Test
	public void computeSPSourceSourceInvalidDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,0,200);
		assertEquals(arrivalTime, 200);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,1,0);
		
		assertEquals(arrivalTime, 4);
		assertEquals(a.getVisitedNodes().toString(), "{1, 0}");
	}
	
	@Test
	public void computeSPSourceTargetInvalidDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,1,100);
		assertEquals(arrivalTime,104);
	}
	
	@Test
	public void computeSPSourceTargetLateDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,1,19);
		assertEquals(arrivalTime,23);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime2() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,2,0);
		assertEquals(arrivalTime,7);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime3() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,2,1);
		assertEquals(arrivalTime,9);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime4() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,2,4);
		assertEquals(arrivalTime,12);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime5() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,5,0);
		assertEquals(arrivalTime,19);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime6() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,5,1);
		assertEquals(arrivalTime,25);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime7() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,5,2);
		assertEquals(arrivalTime,25);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime8() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,5,3);
		assertEquals(arrivalTime,26);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime9() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int arrivalTime = a.computeEarliestArrivalTime(0,5,5);
		assertEquals(arrivalTime,30);
	}
	
	@Test
	public void computeEATimes() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int[] eaTimes = a.computeEarliestArrivalTimes(0, 5, 0, 3);
		assertEquals(Arrays.toString(eaTimes), "[19, 25, 25]");
	}
	
	@Test
	public void computeBestDepartureTime1() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int bestDepartureTime = a.computeDepartureTime(0, 5, 0, 5);
		assertEquals(bestDepartureTime, 0);
	}
	
	@Test
	public void computeBestDepartureTime2() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int bestDepartureTime = a.computeDepartureTime(0, 5, 1, 3);
		assertEquals(bestDepartureTime, 1);
	}
	
	@Test
	public void computeBestDepartureTime3() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g1);
		int bestDepartureTime = a.computeDepartureTime(0, 5, 2, 3);
		assertEquals(bestDepartureTime, 0);
	}
	
	@Test
	public void computeEATimeOnTwoMinGraph() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(tdGraphTwoMin);
		
		int eaTime;
		
		eaTime = a.computeEarliestArrivalTime(7, 9, 0);
		assertEquals(eaTime, 25);
		
		eaTime = a.computeEarliestArrivalTime(0, 0, 0);
		assertEquals(eaTime, 0);
		
		eaTime = a.computeEarliestArrivalTime(0, 0, 1);
		assertEquals(eaTime, 60);
		
		eaTime = a.computeEarliestArrivalTime(0, 1, 0);
		assertEquals(eaTime, 10);
		
		eaTime = a.computeEarliestArrivalTime(0, 1, 1);
		assertEquals(eaTime, 70);
		
		eaTime = a.computeEarliestArrivalTime(0, 2, 0);
		assertEquals(eaTime, 20);
		
		eaTime = a.computeEarliestArrivalTime(0, 2, 1);
		assertEquals(eaTime, -1);
		
		eaTime = a.computeEarliestArrivalTime(1, 2, 0);
		assertEquals(eaTime, 10);
		
		eaTime = a.computeEarliestArrivalTime(1, 2, 1);
		assertEquals(eaTime, -1);
		
		eaTime = a.computeEarliestArrivalTime(0, 3, 0);
		assertEquals(eaTime, 15);
		
		eaTime = a.computeEarliestArrivalTime(0, 3, 1);
		assertEquals(eaTime, 75);
		
		eaTime = a.computeEarliestArrivalTime(0, 7, 0);
		assertEquals(eaTime, -1);
		
		eaTime = a.computeTravelTime(8, 9, 0);
		assertEquals(eaTime, 20);
		
		eaTime = a.computeTravelTime(8, 9, 1);
		assertEquals(eaTime, 30);
	}
	
	@Test
	public void testComputeTravelTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g2);
		
		int eaTime;
		
		eaTime = a.computeTravelTime(0, 5, 0);
		assertEquals(eaTime, 170);
	}
}
