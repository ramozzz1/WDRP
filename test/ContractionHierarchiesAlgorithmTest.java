import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import model.Arc;
import model.Graph;

import org.junit.Before;
import org.junit.Test;

import algorithm.ContractionHierarchiesAlgorithm;

public class ContractionHierarchiesAlgorithmTest extends SPTestBase {
	
	private ContractionHierarchiesAlgorithm a;
	
	@Before
	public void setUpAF() {
		a = new ContractionHierarchiesAlgorithm(g);
	}
	
	@Test
	public void testRandomNodesOrdering() {
		List<Long> nodesOrdering = a.computeRandomNodeOrdering();
		assertEquals(nodesOrdering.size(), g.nodes.size());
		assertThat(nodesOrdering.toString(), not(is(g.nodes.keySet().toString())));
	}
	
	@Test
	public void testContractNodeAndNoShortcutsAdded() {
		Graph graph = createCustomGraph();
		a = new ContractionHierarchiesAlgorithm(graph);
		a.contractNode(0);
		
		assertEquals(a.getNumberOfShortcuts(), 0);
	}
	
	@Test
	public void testContractNodeAndShortcutsAdded() {
		Graph graph = createCustomGraph();
		a = new ContractionHierarchiesAlgorithm(graph);
		a.contractNode(1);
		
		assertEquals(a.getNumberOfShortcuts(), 2);
		Arc a1 = graph.getEdge(0, 2);
		assertNotNull(a1);
		assertEquals(a1.getCost(), 2);
		Arc a2 = graph.getEdge(2, 3);
		assertNotNull(a2);
		assertEquals(a2.getCost(), 3);
	}

	/**
	 * 
	 */
	private Graph createCustomGraph() {
		Graph graph = new Graph();
		graph.addNode(0);
		graph.addNode(1);
		graph.addNode(2);
		graph.addNode(3);
		graph.addNode(4);
		graph.addNode(5);
		graph.addEdge(0, 1, 1);
		graph.addEdge(0, 3, 3);
		graph.addEdge(1, 3, 2);
		graph.addEdge(1, 4, 3);
		graph.addEdge(1, 5, 4);
		graph.addEdge(2, 1, 1);
		graph.addEdge(3, 4, 1);
		graph.addEdge(4, 5, 1);
		
		graph.setArcFlagsForAllEdges(true);
		return graph;
	}
}