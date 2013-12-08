import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import model.Arc;
import model.Graph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import algorithm.ContractionHierarchiesAlgorithm;

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
		List<Long> nodesOrdering = a.computeRandomNodeOrdering();
		assertEquals(nodesOrdering.size(), customGraph.nodes.size());
		assertThat(nodesOrdering.toString(), not(is(customGraph.nodes.keySet().toString())));
	}
	
	@Test
	public void testContractNodeAndNoShortcutsAdded() {
		a.contractNode(0);
		
		assertEquals(a.getNumberOfShortcuts(), 0);
	}
	
	@Test
	public void testContractNodeAndShortcutsAdded() {
		a.contractNode(1);
		
		assertEquals(a.getNumberOfShortcuts(), 2);
		Arc a1 = customGraph.getEdge(0, 2);
		assertNotNull(a1);
		assertEquals(a1.getCost(), 2);
		Arc a2 = customGraph.getEdge(2, 3);
		assertNotNull(a2);
		assertEquals(a2.getCost(), 3);
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