package algorithms;
import static org.junit.Assert.assertEquals;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import storage.DBHashMap;
import algorithm.ALTAlgorithm;

public class ALTAlgorithmTest extends SPTestBase {
	
	private ALTAlgorithm a;
	
	@Before
	public void setUpALT() {
		a = new ALTAlgorithm(g,2);
		a.precompute();
	}
	
	@Test
	public void testRandomSelectedLandmarks(){
		int numLandmarks = 4;
		List<Long> l = a.selectRandomLandmarks(numLandmarks);
		assertEquals(l.size(), numLandmarks);
	}
	
	@Test
	public void testLandmarkDistances(){
		//set landmarks manual
		List<Long> landMarks = new ArrayList<Long>();
		long n0 = 0;
		long n1 = 2;
		landMarks.add(n0);
		landMarks.add(n1);
		a.setLandMarks(landMarks);
		
		//calculate the landmark distances
		THashMap<Long,DBHashMap<Long,Integer>> ld = a.calculateLandmarkDistances();
		assertEquals(ld.size(),landMarks.size());
		assertEquals(ld.get(n0).toString(),"{5=4, 4=4, 3=3, 2=2, 1=1, 0=0}");
		assertEquals(ld.get(n1).toString(),"{5=2, 4=5, 3=1, 2=0, 1=1, 0=2}");
	}
	
	@Test
	public void testHeuristic(){
		List<Long> landMarks = new ArrayList<Long>();
		long n0 = 0;
		long n1 = 2;
		landMarks.add(n0);
		landMarks.add(n1);
		a.setLandMarks(landMarks);
		a.setLandMarkDistances(a.calculateLandmarkDistances());
		int h = a.getHeuristicValue(0, 0);
		assertEquals(h,0);
		h = a.getHeuristicValue(1, 0);
		assertEquals(h,1);
		h = a.getHeuristicValue(2, 0);
		assertEquals(h,2);
		h = a.getHeuristicValue(3, 0);
		assertEquals(h,3);
		h = a.getHeuristicValue(4, 0);
		assertEquals(h,4);
		h = a.getHeuristicValue(5, 0);
		assertEquals(h,4);
		//h = a.getHeuristicValue(999, 0);
		//assertEquals(h,2);
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
		//int dist = a.computeShortestPath(0, 999);
		//assertEquals(dist,-1);
	}
}