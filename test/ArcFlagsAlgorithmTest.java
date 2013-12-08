import static org.junit.Assert.assertEquals;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;

import model.Arc;
import model.Path;

import org.junit.Before;
import org.junit.Test;

import algorithm.ArcFlagsAlgorithm;
import algorithm.DijkstraAlgorithm;

public class ArcFlagsAlgorithmTest extends SPTestBase {
	
	private ArcFlagsAlgorithm a;
	
	@Before
	public void setUpAF() {
		a = new ArcFlagsAlgorithm(g,-1.0,1.0,-1.0,1.0);
	}
	
	@Test
	public void testNodesInRegion(){
		THashSet<Long> nodesInRegion = a.computeNodesInRegion(-1.0,1.0,-1.0,1.0);
		assertEquals(nodesInRegion.size(), 3);
		assertEquals(nodesInRegion.toString(), "{2, 1, 0}");
	}
	
	@Test
	public void testNodesInRegion2(){
		THashSet<Long> nodesInRegion = a.computeNodesInRegion(1.0,3.0,1.0,3.0);
		assertEquals(nodesInRegion.size(), 4);
		assertEquals(nodesInRegion.toString(), "{5, 4, 3, 2}");
	}
	
	@Test
	public void testAllNodesInRegion(){
		THashSet<Long> nodesInRegion = a.computeNodesInRegion(-1000.0,1000.0,-1000.0,1000.0);
		assertEquals(nodesInRegion.size(), g.nodes.size());
		assertEquals(nodesInRegion.toString(), "{999, 5, 4, 3, 2, 1, 0}");
	}
	
	@Test
	public void testNoNodesInRegion(){
		THashSet<Long> nodesInRegion = a.computeNodesInRegion(100,105,100,105);
		assertEquals(nodesInRegion.size(), 0);
	}
	
	@Test
	public void testAllNodesInRegionBoundaryNodes(){
		THashSet<Long> nodesInRegion = a.computeNodesInRegion(0.0,0.0,0.0,0.0);
		THashSet<Long> boundaryNodes = a.computeBoundaryNodes(nodesInRegion);
		assertEquals(boundaryNodes.toString(), nodesInRegion.toString());
		assertEquals(boundaryNodes.size(), nodesInRegion.size());
	}
	
	@Test
	public void testNoBoundaryNodes(){
		THashSet<Long> nodesInRegion = a.computeNodesInRegion(-1000.0,1000.0,-1000.0,1000.0);
		THashSet<Long> boundaryNodes = a.computeBoundaryNodes(nodesInRegion);
		assertEquals(boundaryNodes.size(), 0);
	}
	
	@Test
	public void testSomeBoundaryNodes(){
		THashSet<Long> nodesInRegion = a.computeNodesInRegion(-1.0,1.0,-1.0,1.0);
		THashSet<Long> boundaryNodes = a.computeBoundaryNodes(nodesInRegion);
		assertEquals(boundaryNodes.size(), 2);
		assertEquals(boundaryNodes.toString(), "{2, 0}");
	}
	
	@Test
	public void testArcFlags(){
		/*THashSet<Long> nodesInRegion = a.computeNodesInRegion(0.0,0.0,0.0,0.0);
		THashSet<Long> boundaryNodes = a.computeBoundaryNodes(nodesInRegion);
		a.computeArcFlags(boundaryNodes, nodesInRegion);
		List<Arc> visitedArcs = new ArrayList<Arc>();
		for (Long node : nodesInRegion) {
			DijkstraAlgorithm d = new DijkstraAlgorithm(a.graph);
			d.computeShortestPath(node, -1);
			List<Path> paths = d.extractPathAllPath();
			for (Path path : paths) {
				for (Arc arc : path.getArcs()) {
					if(!visitedArcs.contains(arc))
						visitedArcs.add(arc);
				}
			}
		}
		List<Arc> arcs = a.getArcFlags();
		assertEquals(arcs.size(), visitedArcs.size());*/
	}
	
	@Test
	public void testShortestPathSourceSource(){
		a.setRegion(-1.0,1.0,-1.0,1.0);
		a.precompute();
		int dist = a.computeShortestPath(0, 0);
		assertEquals(dist,0);
	}
	
	@Test
	public void testShortestPathSourceNeighbor(){
		a.setRegion(-1.0,1.0,-1.0,1.0);
		a.precompute();
		int dist = a.computeShortestPath(0, 1);
		assertEquals(dist,1);
	}
	
	@Test
	public void testShortestPathSourceTarget(){
		a.setRegion(1.0,3.0,1.0,3.0);
		a.precompute();
		System.out.println(a.nodesInRegion);
		List<Arc> arcs = a.getArcFlags();
		int dist = a.computeShortestPath(0, 3);
		assertEquals(dist,3);
	}
	
	@Test
	public void testShortestPathSourceTarget2(){
		a.setRegion(1.0,3.0,1.0,3.0);
		a.precompute();
		int dist = a.computeShortestPath(0, 4);
		assertEquals(dist,4);
	}
	
	@Test
	public void testShortestPathSourceTarget3(){
		a.setRegion(1.0,3.0,1.0,3.0);
		a.precompute();
		int dist = a.computeShortestPath(0, 5);
		assertEquals(dist,4);
	}
	
	@Test
	public void testNoPath(){
		a.setRegion(-100.0,100.0,-100.0,100.0);
		a.precompute();
		int dist = a.computeShortestPath(0, 999);
		assertEquals(dist,-1);
	}
	
	@Test
	public void testSourceInsideTargetOutsideRegion(){
		a.setRegion(1.0,3.0,1.0,3.0);
		a.precompute();
		int dist = a.computeShortestPath(5, 1);
		assertEquals(dist,-1);
	}	
	
	@Test
	public void testSourceInsideAndTargetInsideRegion(){
		a.setRegion(-1.0,1.0,-1.0,1.0);
		a.precompute();
		int dist = a.computeShortestPath(0, 1);
		assertEquals(dist,1);
	}
	
	@Test
	public void testSourceOutsideAndTargetInsideRegion(){
		a.setRegion(1.0,3.0,1.0,3.0);
		a.precompute();
		int dist = a.computeShortestPath(0, 5);
		assertEquals(dist,4);
	}
}