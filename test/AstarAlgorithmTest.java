import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import algorithm.AstarAlgorithm;
import algorithm.AstarAlgorithm.HeuristicTypes;

public class AstarAlgorithmTest extends SPTestBase {
	
	private AstarAlgorithm a;
	
	@Before
	public void setUpALT() {
		a = new AstarAlgorithm(g,HeuristicTypes.EUCLIDEAN_DISTANCE);
	}
	
	@Test
	public void testHeuristic(){
		int h = a.getHeuristicValue(5, 0);
		assertEquals(h,4);
		h = a.getHeuristicValue(4, 0);
		assertEquals(h,3);
		h = a.getHeuristicValue(0, 0);
		assertEquals(h,0);
		h = a.getHeuristicValue(999, 0);
		assertEquals(h,15);
	}
	
	@Test
	public void testShortestPathSourceSource(){
		int dist = a.computeShortestPath(0, 0);
		assertEquals(dist,0);
	}
	
	@Test
	public void testShortestPathSourceNeighbor(){
		int dist = a.computeShortestPath(0, 1);
		assertEquals(dist,1);
	}
	
	@Test
	public void testShortestPathSourceTarget(){
		int dist = a.computeShortestPath(0, 3);
		assertEquals(dist,3);
	}
	
	@Test
	public void testShortestPathSourceTarget2(){
		int dist = a.computeShortestPath(0, 4);
		assertEquals(dist,4);
	}
	
	@Test
	public void testShortestPathSourceTarget3(){
		int dist = a.computeShortestPath(0, 5);
		assertEquals(dist,4);
	}
	
	@Test
	public void testNoPath(){
		int dist = a.computeShortestPath(0, 999);
		assertEquals(dist,-1);
	}
}
