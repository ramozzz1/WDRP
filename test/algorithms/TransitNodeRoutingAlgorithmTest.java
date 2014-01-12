package algorithms;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gnu.trove.map.hash.THashMap;

import java.util.Set;

import model.HeuristicTypes;
import model.Path;

import org.junit.Before;
import org.junit.Test;

import algorithm.TransitNodeRoutingAlgorithm;
import algorithm.TransitNodeRoutingAlgorithm.PPDist;

public class TransitNodeRoutingAlgorithmTest extends SPTestBase {
	
	private TransitNodeRoutingAlgorithm a;
	
	@Before
	public void setUpTNR() {
		a = new TransitNodeRoutingAlgorithm(g, HeuristicTypes.EUCLIDEAN_DISTANCE);
	}
	
	@Test
	public void computeSomeTransitNodes() {
		int num = 2;
		Set<Long> tn = a.computeTransitNodes(num);
		assertEquals(tn.size(), num);
		assertEquals(tn.toString(), "{5, 2}");
	}
	
	@Test
	public void computeNoTransitNodes() {
		int num = 0;
		Set<Long> tn = a.computeTransitNodes(num);
		assertEquals(tn.size(), num);
		assertEquals(tn.toString(), "{}");
	}
	
	@Test
	public void computeAccessNodesForNodeWhichIsNotAccessNode1() {
		int num = 2;
		Set<Long> tn = a.computeTransitNodes(num);
		Set<Long> an = a.computeAccessNodes(0, tn);
		assertEquals(an.size(), 1);
		assertEquals(an.toString(), "{2}");
	}
	
	@Test
	public void computeAccessNodesForNodeWhichIsNotAccessNode2() {
		int num = 2;
		Set<Long> tn = a.computeTransitNodes(num);
		Set<Long> an = a.computeAccessNodes(4, tn);
		assertEquals(an.size(), 2);
		assertEquals(an.toString(), "{5, 2}");
	}
	
	@Test
	public void computeAccessNodesForNodeWhichIsAccessNode() {
		int num = 2;
		Set<Long> tn = a.computeTransitNodes(num);
		Set<Long> an = a.computeAccessNodes(5, tn);
		assertEquals(an.size(), 1);
		assertEquals(an.toString(), "{5}");
	}
	
	@Test
	public void computeNoMaxRadius() {
		int num = 2;
		Set<Long> tn = a.computeTransitNodes(num);
		a.computeAccessNodes(5, tn);
		int minRadius = a.radiusNodes.get(5);
		assertEquals(minRadius, 0);
	}
	
	@Test
	public void computeMaxRadius1() {
		int num = 2;
		Set<Long> tn = a.computeTransitNodes(num);
		a.computeAccessNodes(0, tn);
		int minRadius = a.radiusNodes.get(0);
		assertEquals(minRadius, 3);
	}
	
	@Test
	public void computeMaxRadius2() {
		int num = 2;
		Set<Long> tn = a.computeTransitNodes(num);
		a.computeAccessNodes(4, tn);
		int minRadius = a.radiusNodes.get(4);
		assertEquals(minRadius, 2);
	}
	
	@Test
	public void computeMaxRadiusTransitNode() {
		int num = 2;
		Set<Long> tn = a.computeTransitNodes(num);
		a.computeAccessNodes(5, tn);
		int minRadius = a.radiusNodes.get(5);
		assertEquals(minRadius, 0);
	}
	
	@Test
	public void farAwayNode() {
		int num = 2;
		Set<Long> tn = a.computeTransitNodes(num);
		a.computeAccessNodes(4, tn);
		assertTrue(a.isFar(4, 999, a.radiusNodes));
	}
	
	@Test
	public void farAwayTransitNode() {
		int num = 2;
		Set<Long> tn = a.computeTransitNodes(num);
		a.computeAccessNodes(5, tn);
		assertTrue(a.isFar(5, 0, a.radiusNodes));
		assertTrue(a.isFar(5, 1, a.radiusNodes));
		assertTrue(a.isFar(5, 2, a.radiusNodes));
		assertTrue(a.isFar(5, 3, a.radiusNodes));
		assertTrue(a.isFar(5, 4, a.radiusNodes));
		assertTrue(a.isFar(5, 999, a.radiusNodes));
		assertFalse(a.isFar(5, 5, a.radiusNodes));
	}
	
	@Test
	public void computeOne2AllSP() {
		int num = 2;
		Set<Long> tn = a.computeTransitNodes(num);
		Set<Long> an = a.computeAccessNodes(0, tn);
		THashMap<Long, PPDist> dist = a.computeOneToAllDistances(0, an);
		assertEquals(dist.get(2L).dist, 2);
		assertEquals(dist.get(2L).previous.toString(), "{2=1, 1=0, 0=-1}");
	}
	
	@Test
	public void testShortestPathSourceSource() {
		a.precompute();
		int dist = a.computeShortestPath(0, 0);
		assertEquals(dist,0);
	}
	
	@Test
	public void testShortestPathSourceNeighbor() {		
		a.precompute();
		int dist = a.computeShortestPath(0, 1);
		assertEquals(dist,1);
	}
	
	@Test
	public void testShortestPathSourceTarget(){
		a.precompute();
		int dist = a.computeShortestPath(0, 3);
		assertEquals(dist,3);
	}
	
	@Test
	public void testShortestPathSourceTarget2(){
		a.precompute();
		int dist = a.computeShortestPath(0, 4);
		assertEquals(dist,4);
	}
	
	@Test
	public void testShortestPathSourceTarget3(){
		a.precompute();
		int dist = a.computeShortestPath(0, 5);
		assertEquals(dist,4);
	}
	
	@Test
	public void testShortestPathSourceTarget5(){
		a.precompute();
		int dist = a.computeShortestPath(5, 4);
		assertEquals(dist,3);
	}
	
	@Test
	public void testNoPath(){
		a.precompute();
		int dist = a.computeShortestPath(0, 999);
		assertEquals(dist,-1);
	}
	
	@Test
	public void testExtractPath() {
		a.precompute();
		a.computeShortestPath(0, 5);
		Path p = a.extractPath(5);
		assertEquals(p.getNodes().size(), 5);
		assertEquals(p.getArcs().size(), 4);
		assertEquals(p.getCost(), 4);
		assertEquals(p.toString(), "[0->1->2->3->5]");
	}
	
	@Test
	public void testExtractPathNeighbor() {
		a.precompute();
		a.computeShortestPath(0, 1);
		Path p = a.extractPath(1);
		assertEquals(p.getNodes().size(), 2);
		assertEquals(p.getArcs().size(), 1);
		assertEquals(p.getCost(), 1);
		assertEquals(p.toString(), "[0->1]");
	}
	
	@Test
	public void testExtractNoPath() {
		a.precompute();
		a.computeShortestPath(0, 999);
		Path p = a.extractPath(999);
		assertEquals(p.getNodes().size(), 0);
		assertEquals(p.getArcs().size(), 0);
		assertEquals(p.getCost(), 0);
		assertEquals(p.toString(), "[]");
	}
	
	@Test
	public void testExtractSoureSourcePath() {
		a.precompute();
		a.computeShortestPath(0, 0);
		Path p = a.extractPath(0);
		assertEquals(p.getNodes().size(), 1);
		assertEquals(p.getArcs().size(), 0);
		assertEquals(p.getCost(), 0);
		assertEquals(p.toString(), "[0]");
	}
}