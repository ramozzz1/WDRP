package algorithms;

import static org.junit.Assert.assertEquals;
import model.TDGraph;

import org.junit.BeforeClass;
import org.junit.Test;

import algorithm.TDDijkstraAlgorithm;

public class TDDijkstraAlgorithmTest {
	public static TDGraph g;
	
	@BeforeClass
	public static void setUpTDGraph() {
		g = new TDGraph();
		
		g.addNode(0);
		g.addNode(1);
		g.addNode(2);
		
		int[] e1 = {4,5,9,4};
		g.addEdge(0, 1, e1);
		
		int[] e2 = {8,10,11,8};
		g.addEdge(0, 2, e2);
		
		int[] e3 = {3,7,5,8};
		g.addEdge(1, 2, e3);
	}
	
	@Test
	public void testTDEdgeCost() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		
		int cost = a.getEdgeCost(g.getEdge(0, 1), 0);
		assertEquals(cost, 4);
		
		cost = a.getEdgeCost(g.getEdge(0, 1), 1);
		assertEquals(cost, 5);
		
		cost = a.getEdgeCost(g.getEdge(0, 1), 2);
		assertEquals(cost, 6);
		
		cost = a.getEdgeCost(g.getEdge(0, 1), 3);
		assertEquals(cost, 7);
		
		cost = a.getEdgeCost(g.getEdge(0, 1), 4);
		assertEquals(cost, 8);
		
		cost = a.getEdgeCost(g.getEdge(0, 1), 5);
		assertEquals(cost, 5+5);
		
		cost = a.getEdgeCost(g.getEdge(0, 1), 6);
		assertEquals(cost, 5+6);
		
		cost = a.getEdgeCost(g.getEdge(0, 1), 10);
		assertEquals(cost, 10+9);
		
		cost = a.getEdgeCost(g.getEdge(0, 1), 19);
		assertEquals(cost, 19+4);
		
		cost = a.getEdgeCost(g.getEdge(0, 1), 20);
		assertEquals(cost, Integer.MAX_VALUE);
	}
	
	@Test
	public void computeSPSourceSourceValidDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int travelTime = a.computeShortestPath(0,0,0);
		assertEquals(travelTime,0);
	}
	
	@Test
	public void computeSPSourceSourceInvalidDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int travelTime = a.computeShortestPath(0,0,200);
		assertEquals(travelTime, 0);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int travelTime = a.computeShortestPath(0,1,0);
		
		assertEquals(travelTime, 4);
		assertEquals(a.getVisitedNodes().toString(), "{1, 0}");
	}
	
	@Test
	public void computeSPSourceTargetInvalidDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int travelTime = a.computeShortestPath(0,1,100);
		assertEquals(travelTime,-2);
	}
	
	@Test
	public void computeSPSourceTargetLateDepartureTime() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int travelTime = a.computeShortestPath(0,1,19);
		assertEquals(travelTime,4);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime2() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int travelTime = a.computeShortestPath(0,2,0);
		assertEquals(travelTime,7);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime3() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int travelTime = a.computeShortestPath(0,2,1);
		assertEquals(travelTime,8);
	}
	
	@Test
	public void computeSPSourceTargetValidDepartureTime4() {
		TDDijkstraAlgorithm a = new TDDijkstraAlgorithm(g);
		int travelTime = a.computeShortestPath(0,2,4);
		assertEquals(travelTime,8);
	}
}
