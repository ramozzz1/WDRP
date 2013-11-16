import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import algorithm.DijkstraAlgorithm;


public class DijkstraAlgorithmTest extends SPTestBase {
	
	private DijkstraAlgorithm d;
	
	@Before
	public void setUpALT() {
		d = new DijkstraAlgorithm(g);
	}
	
	@Test
	public void testShortestPathSourceSource() {
		int dist = d.computeShortestPath(0, 0);
		assertEquals(dist,0);
	}
	
	@Test
	public void testShortestPathSourceNeighbor() {		
		int dist = d.computeShortestPath(0, 1);
		assertEquals(dist,1);
	}
	
	@Test
	public void testShortestPathSourceTarget(){
		int dist = d.computeShortestPath(0, 3);
		assertEquals(dist,3);
	}
	
	@Test
	public void testShortestPathSourceTarget2(){
		int dist = d.computeShortestPath(0, 4);
		assertEquals(dist,4);
	}
	
	@Test
	public void testShortestPathSourceTarget3(){
		int dist = d.computeShortestPath(0, 5);
		assertEquals(dist,4);
	}
	
	@Test
	public void testAllShortestPath1(){
		int dist = d.computeShortestPath(0, -1);
		assertEquals(dist,-1);
		assertEquals(d.distance.toString(),"{5=4, 4=4, 3=3, 2=2, 1=1, 0=0}");
	}
	
	@Test
	public void testAllShortestPath2(){
		int dist = d.computeShortestPath(2, -1);
		assertEquals(dist,-1);
		assertEquals(d.distance.toString(),"{5=2, 4=5, 3=1, 2=0, 1=1, 0=2}");
	}
	
	@Test
	public void testNoPath(){
		int dist = d.computeShortestPath(0, 999);
		assertEquals(dist,-1);
	}
}
