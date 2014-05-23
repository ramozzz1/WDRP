package algorithms.td;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gnu.trove.map.hash.TLongIntHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import model.QEntry;
import model.TDArc;
import model.TDGraph;

import org.junit.Before;
import org.junit.Test;

import util.ArrayUtils;
import util.CommonUtils;
import algorithm.td.TDContractionHierarchiesAlgorithm;

public class TDContractionHierarchiesAlgorithmTest extends TDTestBase {
	
	private TDContractionHierarchiesAlgorithm d;
	private TDContractionHierarchiesAlgorithm a;
	private TDGraph customGraph;
	
	@Before
	public void setUpCH() {
		createCustomGraph();
		a = new TDContractionHierarchiesAlgorithm(customGraph);
		d = new TDContractionHierarchiesAlgorithm(g);
	}
	
	@Test
	public void testComputeShortcuts() {
		int n = 2;
		customGraph.disableNode(n);
		int sh = a.computeShortcuts(n, false);
		customGraph.enableNode(n);
		assertEquals(sh, 0);
		
		n = 0;
		customGraph.disableNode(n);
		sh = a.computeShortcuts(n, false);
		customGraph.enableNode(n);
		assertEquals(sh, 0);
		
		n = 3;
		customGraph.disableNode(n);
		sh = a.computeShortcuts(n, false);
		customGraph.enableNode(n);
		assertEquals(sh, 0);
		
		n = 5;
		customGraph.disableNode(n);
		sh = a.computeShortcuts(n, false);
		customGraph.enableNode(n);
		assertEquals(sh, 0);
		
		n = 4;
		customGraph.disableNode(n);
		sh = a.computeShortcuts(n, false);
		customGraph.enableNode(n);
		assertEquals(sh, 2);
		
		n = 1;
		customGraph.disableNode(n);
		sh = a.computeShortcuts(n, false);
		customGraph.enableNode(n);
		assertEquals(sh, 8);
	}
	
	@Test
	public void testLazyHeuristic() {
		Queue<QEntry> queue = a.computeEDNodeOrdering();
		
		a.lazyUpdate(queue);
		List<QEntry> list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{3,-3}, {0,-2}, {5,-2}, {2,-1}, {4,-1}, {1,3}]");
		
		a.contractSingleNode(queue.poll().getNodeId());
		
		a.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{5,-2}, {0,-1}, {2,-1}, {4,-1}, {1,3}]");
		
		a.contractSingleNode(queue.poll().getNodeId());
		
		a.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{0,-1}, {2,-1}, {4,-1}, {1,3}]");
		
		a.contractSingleNode(queue.poll().getNodeId());
		
		a.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{2,-1}, {4,-1}, {1,3}]");
		
		a.contractSingleNode(queue.poll().getNodeId());
		
		a.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{4,-1}, {1,3}]");
		
		a.contractSingleNode(queue.poll().getNodeId());
		
		a.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{1,3}]");
		
		a.contractSingleNode(queue.poll().getNodeId());
		
		a.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[]");
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
		assertEquals(nodesHierachy.toString(), "{5=1, 4=4, 3=0, 2=3, 1=5, 0=2}");
	}
	
	@Test
	public void testUpwardGraph() {
		Queue<QEntry> nodesEDOrdering = a.computeEDNodeOrdering();
		TLongIntHashMap nodesHierachy =  a.contractNodes(nodesEDOrdering.size(), nodesEDOrdering);		
		a.constructUpwardsGraph(nodesHierachy);
		
		assertEquals(a.getNumberOfShortcuts(), 0);
		assertEquals(customGraph.getArcsNotDisabled().size(), 8);
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
		TDArc a1 = customGraph.getEdge(0, 2);
		assertNotNull(a1);
		assertEquals(a1.getCost(), 2);
		TDArc a2 = customGraph.getEdge(2, 3);
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
		assertEquals(ed, -1);
		
		sh = a.contractSingleNode(0);
		assertEquals(sh, 0);
		
		ed = a.computeEdgeDifference(2);
		assertEquals(ed, -1);
		
		sh = a.contractSingleNode(2);
		assertEquals(sh, 0);
		
		ed = a.computeEdgeDifference(5);
		assertEquals(ed, -2);
		
		sh = a.contractSingleNode(5);
		assertEquals(sh, 0);
		
		ed = a.computeEdgeDifference(4);
		assertEquals(ed, -1);
		
		sh = a.contractSingleNode(4);
		assertEquals(sh, 0);
		
		ed = a.computeEdgeDifference(1);
		assertEquals(ed, 0);
		
		sh = a.contractSingleNode(1);
		assertEquals(sh, 0);
	}

	private void createCustomGraph() {
		customGraph = new TDGraph();
		customGraph.addNode(0);
		customGraph.addNode(1);
		customGraph.addNode(2);
		customGraph.addNode(3);
		customGraph.addNode(4);
		customGraph.addNode(5);
		customGraph.addEdge(0, 1, ArrayUtils.extrapolateArrayToArray(new int[]{1,1,1,1}, 5));
		customGraph.addEdge(0, 3, ArrayUtils.extrapolateArrayToArray(new int[]{3,3,3,3}, 5));
		customGraph.addEdge(1, 3, ArrayUtils.extrapolateArrayToArray(new int[]{2,2,2,2}, 5));
		customGraph.addEdge(1, 4, ArrayUtils.extrapolateArrayToArray(new int[]{3,3,3,3}, 5));
		customGraph.addEdge(1, 5, ArrayUtils.extrapolateArrayToArray(new int[]{4,4,4,4}, 5));
		customGraph.addEdge(2, 1, ArrayUtils.extrapolateArrayToArray(new int[]{1,1,1,1}, 5));
		customGraph.addEdge(3, 4, ArrayUtils.extrapolateArrayToArray(new int[]{1,1,1,1}, 5));
		customGraph.addEdge(4, 5, ArrayUtils.extrapolateArrayToArray(new int[]{1,1,1,1}, 5));
		
		customGraph.setArcFlagsForAllEdges(true);
	}
}