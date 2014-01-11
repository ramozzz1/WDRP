package algorithms;
import static org.junit.Assert.assertEquals;
import model.Path;

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
	public void testShortestPathSourceTarget4(){
		int dist = d.computeShortestPath(4, 5);
		assertEquals(dist,3);
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
	
	@Test
	public void testExtractPath() {
		d.computeShortestPath(0, 5);
		Path p = d.extractPath(5);
		assertEquals(p.getNodes().size(), 5);
		assertEquals(p.getArcs().size(), 4);
		assertEquals(p.getCost(), 4);
		assertEquals(p.toString(), "[0->1->2->3->5]");
	}
	
	@Test
	public void testExtractPathNeighbor() {
		d.computeShortestPath(0, 1);
		Path p = d.extractPath(1);
		assertEquals(p.getNodes().size(), 2);
		assertEquals(p.getArcs().size(), 1);
		assertEquals(p.getCost(), 1);
		assertEquals(p.toString(), "[0->1]");
	}
	
	@Test
	public void testExtractNoPath() {
		d.computeShortestPath(0, 999);
		Path p = d.extractPath(999);
		assertEquals(p.getNodes().size(), 0);
		assertEquals(p.getArcs().size(), 0);
		assertEquals(p.getCost(), 0);
		assertEquals(p.toString(), "[]");
	}
	
	@Test
	public void testExtractSoureSourcePath() {
		d.computeShortestPath(0, 0);
		Path p = d.extractPath(0);
		assertEquals(p.getNodes().size(), 1);
		assertEquals(p.getArcs().size(), 0);
		assertEquals(p.getCost(), 0);
		assertEquals(p.toString(), "[0]");
	}
}
