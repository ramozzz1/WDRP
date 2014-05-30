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
import algorithm.td.TDCHAlgorithm;
import algorithm.td.TDDijkstraAlgorithm;

public class TDCHAlgorithmTest extends TDTestBase {
	
	private TDDijkstraAlgorithm dynamicTDDijkstra;
	private TDDijkstraAlgorithm staticTDDijkstra;
	private TDCHAlgorithm dynamicTDCH;
	private TDCHAlgorithm staticTDCH;
	private TDGraph staticGraph;
	
	@Before
	public void setUpCH() {
		createCustomGraph();
		staticTDCH = new TDCHAlgorithm(staticGraph);
		dynamicTDCH = new TDCHAlgorithm(g);
		staticTDDijkstra = new TDDijkstraAlgorithm(staticGraph);
		dynamicTDDijkstra = new TDDijkstraAlgorithm(g);
	}
	
	@Test
	public void testComputeShortcuts() {
		int n,sh;
		
		n = 2;
		staticGraph.disableNode(n);
		sh = staticTDCH.computeShortcuts(n, false);
		staticGraph.enableNode(n);
		assertEquals(sh, 0);
		
		n = 0;
		staticGraph.disableNode(n);
		sh = staticTDCH.computeShortcuts(n, false);
		staticGraph.enableNode(n);
		assertEquals(sh, 0);
		
		n = 3;
		staticGraph.disableNode(n);
		sh = staticTDCH.computeShortcuts(n, false);
		staticGraph.enableNode(n);
		assertEquals(sh, 1);
		
		n = 5;
		staticGraph.disableNode(n);
		sh = staticTDCH.computeShortcuts(n, false);
		staticGraph.enableNode(n);
		assertEquals(sh, 0);
		
		n = 4;
		staticGraph.disableNode(n);
		sh = staticTDCH.computeShortcuts(n, false);
		staticGraph.enableNode(n);
		assertEquals(sh, 2);
		
		n = 1;
		staticGraph.disableNode(n);
		sh = staticTDCH.computeShortcuts(n, false);
		staticGraph.enableNode(n);
		assertEquals(sh, 10);
	}
	
	@Test
	public void testLazyHeuristic() {
		Queue<QEntry> queue = staticTDCH.computeEDNodeOrdering();
		
		staticTDCH.lazyUpdate(queue);
		List<QEntry> list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{0,-2}, {3,-2}, {5,-2}, {2,-1}, {4,-1}, {1,5}]");
		
		staticTDCH.contractSingleNode(queue.poll().getNodeId());
		
		staticTDCH.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{3,-2}, {5,-2}, {2,-1}, {4,-1}, {1,5}]");
		
		staticTDCH.contractSingleNode(queue.poll().getNodeId());
		
		staticTDCH.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{5,-2}, {2,-1}, {4,-1}, {1,5}]");
		
		staticTDCH.contractSingleNode(queue.poll().getNodeId());
		
		staticTDCH.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{2,-1}, {4,-1}, {1,5}]");
		
		staticTDCH.contractSingleNode(queue.poll().getNodeId());
		
		staticTDCH.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{4,-1}, {1,5}]");
		
		staticTDCH.contractSingleNode(queue.poll().getNodeId());
		
		staticTDCH.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{1,5}]");
		
		staticTDCH.contractSingleNode(queue.poll().getNodeId());
		
		staticTDCH.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[]");
	}
	
	@Test
	public void testPositiveEdgeDifference() {
		int ed = staticTDCH.computeEdgeDifference(1);
		
		assertEquals(ed, 5);
	}
	
	@Test
	public void testNegativeEdgeDifference1() {
		int ed = staticTDCH.computeEdgeDifference(3);
		
		assertEquals(ed, -2);
	}
	
	@Test
	public void testNegativeEdgeDifference2() {
		int ed = staticTDCH.computeEdgeDifference(0);
		
		assertEquals(ed, -2);
	}
	
	@Test
	public void testEDNodesOrdering() {
		Queue<QEntry> nodesEDOrdering = staticTDCH.computeEDNodeOrdering();
		List<QEntry> nodes = new ArrayList<QEntry>();
		while (nodesEDOrdering.size() > 0)
			nodes.add(nodesEDOrdering.poll());
		assertEquals(nodes.toString(), "[{0,-2}, {3,-2}, {5,-2}, {2,-1}, {4,-1}, {1,5}]");
	}
	
	@Test
	public void testNodesHieracy() {
		Queue<QEntry> nodesEDOrdering = staticTDCH.computeEDNodeOrdering();
		TLongIntHashMap nodesHierachy =  staticTDCH.contractNodes(nodesEDOrdering.size(), nodesEDOrdering);
		assertEquals(nodesHierachy.toString(), "{5=2, 4=4, 3=1, 2=3, 1=5, 0=0}");
	}
	
	@Test
	public void testUpwardGraph() {
		Queue<QEntry> nodesEDOrdering = staticTDCH.computeEDNodeOrdering();
		TLongIntHashMap nodesHierachy =  staticTDCH.contractNodes(nodesEDOrdering.size(), nodesEDOrdering);		
		staticTDCH.constructUpwardsGraph(nodesHierachy);
		
		assertEquals(staticTDCH.getNumberOfShortcuts(), 0);
		assertEquals(staticGraph.getArcsNotDisabled().size(), 8);
	}
	
	@Test
	public void testContractNodeAndNoShortcutsAdded() {
		int sh = staticTDCH.contractSingleNode(0);
		
		assertEquals(sh, 0);
	}
	
	@Test
	public void testContractNodeAndShortcutsAdded1() {
		int sh = staticTDCH.contractSingleNode(4);
		
		assertEquals(sh, 2);
	}
		
	@Test
	public void testContractNodeAndShortcutsAdded2() {
		int sh = staticTDCH.contractSingleNode(1);
		
		assertEquals(sh, 8);
		TDArc a1 = staticGraph.getArc(0, 2);
		assertNotNull(a1);
		assertEquals(a1.getCost(), 2);
		TDArc a2 = staticGraph.getArc(2, 3);
		assertNotNull(a2);
		assertEquals(a2.getCost(), 3);
	}
	
	@Test
	public void testContractNodeAndShortcutsAdded3() {
		
		int ed = staticTDCH.computeEdgeDifference(3);
		assertEquals(ed, -2);
		
		ed = staticTDCH.computeEdgeDifference(0);
		assertEquals(ed, -2);
		
		int sh = staticTDCH.contractSingleNode(3);
		assertEquals(sh, 1);
		
		ed = staticTDCH.computeEdgeDifference(0);
		assertEquals(ed, -1);
		
		sh = staticTDCH.contractSingleNode(0);
		assertEquals(sh, 0);
		
		ed = staticTDCH.computeEdgeDifference(2);
		assertEquals(ed, -1);
		
		sh = staticTDCH.contractSingleNode(2);
		assertEquals(sh, 0);
		
		ed = staticTDCH.computeEdgeDifference(5);
		assertEquals(ed, -2);
		
		sh = staticTDCH.contractSingleNode(5);
		assertEquals(sh, 0);
		
		ed = staticTDCH.computeEdgeDifference(4);
		assertEquals(ed, -1);
		
		sh = staticTDCH.contractSingleNode(4);
		assertEquals(sh, 1);
		
		ed = staticTDCH.computeEdgeDifference(1);
		assertEquals(ed, -1);
		
		sh = staticTDCH.contractSingleNode(1);
		assertEquals(sh, 0);
	}
	
	@Test
	public void testEATimeSourceSourceStatic() {
		staticTDCH.precompute();
		int eaTime = staticTDCH.computeEarliestArrivalTime(0, 0, 0);
		
		
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(0, 0, 0));
	}
	
	@Test
	public void testEATimeSourceSourceDynamic() {
		dynamicTDCH.precompute();
		int eaTime = dynamicTDCH.computeShortestPath(0, 0);
		
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(0, 0, 0));
	}
	
	@Test
	public void testEATimeSourceNeighborStatic() {		
		int eaTime;
		staticTDCH.precompute();
		
		eaTime = staticTDCH.computeEarliestArrivalTime(0, 1, 0);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(0, 1, 0));
		
		eaTime = staticTDCH.computeEarliestArrivalTime(0, 2, 0);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(0, 2, 0));
		
		eaTime = staticTDCH.computeEarliestArrivalTime(2, 0, 0);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(2, 0, 0));
		
		eaTime = staticTDCH.computeEarliestArrivalTime(4, 5, 0);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(4, 5, 0));
		
		eaTime = staticTDCH.computeEarliestArrivalTime(5, 4, 0);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(5, 4, 0));
		
		eaTime = staticTDCH.computeEarliestArrivalTime(5, 4, 10);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(5, 4, 10));
		
		eaTime = staticTDCH.computeEarliestArrivalTime(5, 4, 21);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(5, 4, 21));
	}
	
	@Test
	public void testEATimeSourceNeighborDynamic() {		
		int eaTime;
		dynamicTDCH.precompute();
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(0, 1, 0);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(0, 1, 0));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(0, 2, 0);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(0, 2, 0));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(2, 0, 0);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(2, 0, 0));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(4, 5, 0);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(4, 5, 0));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(5, 4, 0);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(5, 4, 0));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(5, 4, 10);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(5, 4, 10));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(5, 4, 21);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(5, 4, 21));
	}
	
	@Test
	public void testEATimeSourceTargetStatic() {		
		int eaTime;
		staticTDCH.precompute();
		
		eaTime = staticTDCH.computeEarliestArrivalTime(0, 3, 0);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(0, 3, 0));
		
		eaTime = staticTDCH.computeEarliestArrivalTime(0, 4, 0);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(0, 4, 0));
		
		eaTime = staticTDCH.computeEarliestArrivalTime(4, 0, 0);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(4, 0, 0));
		
		eaTime = staticTDCH.computeEarliestArrivalTime(0, 5, 0);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(0, 5, 0));
		
		eaTime = staticTDCH.computeEarliestArrivalTime(3, 5, 0);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(3, 5, 0));
		
		eaTime = staticTDCH.computeEarliestArrivalTime(2, 5, 10);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(2, 5, 10));
		
		eaTime = staticTDCH.computeEarliestArrivalTime(1, 4, 21);
		assertEquals(eaTime, staticTDDijkstra.computeEarliestArrivalTime(1, 4, 21));
	}
	
	@Test
	public void testEATimeSourceTargetDynamic() {		
		int eaTime;
		dynamicTDCH.precompute();
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(0, 3, 0);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(0, 3, 0));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(0, 4, 0);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(0, 4, 0));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(4, 0, 0);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(4, 0, 0));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(0, 5, 0);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(0, 5, 0));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(3, 5, 0);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(3, 5, 0));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(2, 5, 10);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(2, 5, 10));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(1, 4, 21);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(1, 4, 21));
	}

	private void createCustomGraph() {
		staticGraph = new TDGraph();
		staticGraph.addNode(0);
		staticGraph.addNode(1);
		staticGraph.addNode(2);
		staticGraph.addNode(3);
		staticGraph.addNode(4);
		staticGraph.addNode(5);
		staticGraph.addEdge(0, 1, ArrayUtils.extrapolateArrayToArray(new int[]{1,1,1,1}, 5));
		staticGraph.addEdge(0, 3, ArrayUtils.extrapolateArrayToArray(new int[]{3,3,3,3}, 5));
		staticGraph.addEdge(1, 3, ArrayUtils.extrapolateArrayToArray(new int[]{2,2,2,2}, 5));
		staticGraph.addEdge(1, 4, ArrayUtils.extrapolateArrayToArray(new int[]{3,3,3,3}, 5));
		staticGraph.addEdge(1, 5, ArrayUtils.extrapolateArrayToArray(new int[]{4,4,4,4}, 5));
		staticGraph.addEdge(2, 1, ArrayUtils.extrapolateArrayToArray(new int[]{1,1,1,1}, 5));
		staticGraph.addEdge(3, 4, ArrayUtils.extrapolateArrayToArray(new int[]{1,1,1,1}, 5));
		staticGraph.addEdge(4, 5, ArrayUtils.extrapolateArrayToArray(new int[]{1,1,1,1}, 5));
		
		staticGraph.setArcFlagsForAllEdges(true);
	}
}