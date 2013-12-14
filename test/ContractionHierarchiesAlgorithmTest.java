import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import gnu.trove.map.hash.TLongIntHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import model.Arc;
import model.Graph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import algorithm.ContractionHierarchiesAlgorithm;
import algorithm.ContractionHierarchiesAlgorithm.QEntry;

public class ContractionHierarchiesAlgorithmTest extends SPTestBase {
	
	private ContractionHierarchiesAlgorithm a;
	private Graph customGraph;
	
	@Before
	public void setUpAF() {
		createCustomGraph();
		a = new ContractionHierarchiesAlgorithm(customGraph);
	}
	
	@Test
	public void testRandomNodesOrdering() {
		Queue<QEntry> nodesOrdering = a.computeRandomNodeOrdering();
		List<Long> nodes = new ArrayList<Long>();
		while (nodesOrdering.size() > 0)
			nodes.add(nodesOrdering.poll().getNodeId());
		
		assertEquals(nodes.size(), customGraph.nodes.size());
		assertThat(nodes.toString(), not(is(customGraph.nodes.keySet().toString())));
	}
	
	@Test
	public void testPositiveEdgeDifference() {
		int ed = a.computeEdgeDifference(1);
		
		assertEquals(ed, 3);
	}
	
	@Test
	public void testNegativeEdgeDifference1() {
		int ed = a.computeEdgeDifference(3);
		
		assertEquals(ed, -3);
	}
	
	@Test
	public void testNegativeEdgeDifference2() {
		int ed = a.computeEdgeDifference(0);
		
		assertEquals(ed, -2);
	}
	
	@Test
	public void testEDNodesOrdering() {
		Queue<QEntry> nodesEDOrdering = a.computeEDNodeOrdering();
		List<QEntry> nodes = new ArrayList<QEntry>();
		while (nodesEDOrdering.size() > 0)
			nodes.add(nodesEDOrdering.poll());
		assertEquals(nodes.toString(), "[{3,-3}, {0,-2}, {5,-2}, {2,-1}, {4,-1}, {1,3}]");
	}
	
	@Test
	public void testNodesHieracy() {
		Queue<QEntry> nodesEDOrdering = a.computeEDNodeOrdering();
		TLongIntHashMap nodesHierachy =  a.contractNodes(nodesEDOrdering.size(), nodesEDOrdering);
		assertEquals(nodesHierachy.toString(), "{5=1, 4=4, 3=0, 2=2, 1=5, 0=3}");
	}
	
	@Test
	public void testContractNodeAndNoShortcutsAdded() {
		int sh = a.contractSingleNode(0);
		
		assertEquals(sh, 0);
	}
	
	@Test
	public void testContractNodeAndShortcutsAdded1() {
		int sh = a.contractSingleNode(4);
		
		assertEquals(sh, 2);
	}
		
	@Test
	public void testContractNodeAndShortcutsAdded2() {
		int sh = a.contractSingleNode(1);
		
		assertEquals(sh, 4);
		Arc a1 = customGraph.getEdge(0, 2);
		assertNotNull(a1);
		assertEquals(a1.getCost(), 2);
		Arc a2 = customGraph.getEdge(2, 3);
		assertNotNull(a2);
		assertEquals(a2.getCost(), 3);
	}
	
	@Test
	public void testContractNodeAndShortcutsAdded3() {
		
		int ed = a.computeEdgeDifference(3);
		assertEquals(ed, -3);
		
		ed = a.computeEdgeDifference(0);
		assertEquals(ed, -2);
		
		int sh = a.contractSingleNode(3);
		assertEquals(sh, 0);
		
		ed = a.computeEdgeDifference(0);
		assertEquals(ed, 0);
	}

	private void createCustomGraph() {
		customGraph = new Graph();
		customGraph.addNode(0);
		customGraph.addNode(1);
		customGraph.addNode(2);
		customGraph.addNode(3);
		customGraph.addNode(4);
		customGraph.addNode(5);
		customGraph.addEdge(0, 1, 1);
		customGraph.addEdge(0, 3, 3);
		customGraph.addEdge(1, 3, 2);
		customGraph.addEdge(1, 4, 3);
		customGraph.addEdge(1, 5, 4);
		customGraph.addEdge(2, 1, 1);
		customGraph.addEdge(3, 4, 1);
		customGraph.addEdge(4, 5, 1);
		
		customGraph.setArcFlagsForAllEdges(true);
	}
	
	@After
	public void clearGraph() {
		g.clear();
	}
}