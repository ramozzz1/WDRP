package algorithms;

import static org.junit.Assert.assertEquals;
import model.TNGraph;

import org.junit.Test;

import algorithm.TimeExpandedDijkstraAlgorithm;

public class TEDijkstraAlgorithmTest {

	@Test
	public void testClostestNode() {
		TNGraph graph = new TNGraph();
		graph.addNodeToStation(0, 10000051111110L);
		graph.addNodeToStation(0, 20050051111110L);
		graph.addNodeToStation(0, 30000051111110L);
		graph.addNodeToStation(0, 30100051111110L);
		graph.addNodeToStation(0, 30100451111110L);
		graph.addNodeToStation(0, 32400451111110L);
		graph.addNodeToStation(0, 31300451111110L);
		graph.addNodeToStation(0, 31200451111110L);
		graph.addNodeToStation(0, 31200471111110L);
		
		TimeExpandedDijkstraAlgorithm a = new TimeExpandedDijkstraAlgorithm(graph);
		
		assertEquals(31200451111110L, a.selectClosestSourceFromStation(0, "12:00:44"));
		assertEquals(31200471111110L, a.selectClosestSourceFromStation(0, "12:00:46"));
		assertEquals(31200451111110L, a.selectClosestSourceFromStation(0, "12:00:45"));
		assertEquals(32400451111110L, a.selectClosestSourceFromStation(0, "13:01:45"));
	}
}
