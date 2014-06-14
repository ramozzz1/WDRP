package algorithms.td;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gnu.trove.map.hash.TLongIntHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
		assertEquals(sh, 4);
		
		n = 5;
		staticGraph.disableNode(n);
		sh = staticTDCH.computeShortcuts(n, false);
		staticGraph.enableNode(n);
		assertEquals(sh, 0);
		
		n = 4;
		staticGraph.disableNode(n);
		sh = staticTDCH.computeShortcuts(n, false);
		staticGraph.enableNode(n);
		assertEquals(sh, 4);
		
		n = 1;
		staticGraph.disableNode(n);
		sh = staticTDCH.computeShortcuts(n, false);
		staticGraph.enableNode(n);
		assertEquals(sh, 14);
	}
	
	@Test
	public void testLazyHeuristic() {
		Queue<QEntry> queue = staticTDCH.computeEDNodeOrdering();
		
		staticTDCH.lazyUpdate(queue);
		List<QEntry> list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{0,-2}, {5,-2}, {2,-1}, {3,1}, {4,1}, {1,9}]");
		
		staticTDCH.contractSingleNode(queue.poll().getNodeId());
		
		staticTDCH.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{5,-2}, {2,-1}, {3,1}, {4,1}, {1,9}]");
		
		staticTDCH.contractSingleNode(queue.poll().getNodeId());
		
		staticTDCH.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{2,-1}, {3,1}, {4,1}, {1,9}]");
		
		staticTDCH.contractSingleNode(queue.poll().getNodeId());
		
		staticTDCH.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{3,1}, {4,1}, {1,9}]");
		
		staticTDCH.contractSingleNode(queue.poll().getNodeId());
		
		staticTDCH.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{4,1}, {1,9}]");
		
		staticTDCH.contractSingleNode(queue.poll().getNodeId());
		
		staticTDCH.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[{1,9}]");
		
		staticTDCH.contractSingleNode(queue.poll().getNodeId());
		
		staticTDCH.lazyUpdate(queue);
		list = CommonUtils.convertQueueToArray(queue);
		assertEquals(list.toString(), "[]");
	}
	
	@Test
	public void testPositiveEdgeDifference() {
		int ed = staticTDCH.computeEdgeDifference(1);
		
		assertEquals(ed, 9);
	}
	
	@Test
	public void testNegativeEdgeDifference1() {
		int ed = staticTDCH.computeEdgeDifference(3);
		
		assertEquals(ed, 1);
	}
	
	@Test
	public void testNegativeEdgeDifference3() {
		int ed;
		
		g.setArcFlagsForAllEdges(true);
		ed = dynamicTDCH.computeEdgeDifference(0);
		assertEquals(ed, -2);
		
		g.setArcFlagsForAllEdges(true);
		ed = dynamicTDCH.computeEdgeDifference(1);
		assertEquals(ed, 0);
		
		g.setArcFlagsForAllEdges(true);
		ed = dynamicTDCH.computeEdgeDifference(2);
		assertEquals(ed, 1);
		
		g.setArcFlagsForAllEdges(true);
		ed = dynamicTDCH.computeEdgeDifference(3);
		assertEquals(ed, 1);
		
		g.setArcFlagsForAllEdges(true);
		ed = dynamicTDCH.computeEdgeDifference(4);
		assertEquals(ed, -2);
		
		g.setArcFlagsForAllEdges(true);
		ed = dynamicTDCH.computeEdgeDifference(5);
		assertEquals(ed, 3);
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
		assertEquals(nodes.toString(), "[{0,-2}, {5,-2}, {2,-1}, {3,1}, {4,1}, {1,9}]");
	}
	
	@Test
	public void testNodesHieracy() {
		Queue<QEntry> nodesEDOrdering = staticTDCH.computeEDNodeOrdering();
		TLongIntHashMap nodesHierachy =  staticTDCH.contractNodes(nodesEDOrdering.size(), nodesEDOrdering);
		assertEquals(nodesHierachy.toString(), "{5=1, 4=4, 3=3, 2=2, 1=5, 0=0}");
	}
	
	@Test
	public void testUpwardGraph() {
		Queue<QEntry> nodesEDOrdering = staticTDCH.computeEDNodeOrdering();
		TLongIntHashMap nodesHierachy =  staticTDCH.contractNodes(nodesEDOrdering.size(), nodesEDOrdering);		
		staticTDCH.constructUpwardsGraph(nodesHierachy);
		
		assertEquals(staticTDCH.getNumberOfShortcuts(), 2);
		assertEquals(staticGraph.getArcsNotDisabled().size(), 9);
	}
	
	@Test
	public void testContractNodeAndNoShortcutsAdded() {
		int sh = staticTDCH.contractSingleNode(0);
		
		assertEquals(sh, 0);
	}
	
	@Test
	public void testContractNodeAndShortcutsAdded1() {
		int sh = staticTDCH.contractSingleNode(4);
		
		assertEquals(sh, 4);
	}
		
	@Test
	public void testContractNodeAndShortcutsAdded2() {
		int sh = staticTDCH.contractSingleNode(1);
		
		assertEquals(sh, 14);
		TDArc a1 = staticGraph.getArc(0, 2);
		assertNotNull(a1);
		assertEquals(a1.getCost(), 2);
		TDArc a2 = staticGraph.getArc(2, 3);
		assertNotNull(a2);
		assertEquals(a2.getCost(), 3);
	}
	
	@Test
	public void testContractNodeAndShortcutsAdded3() {
		int ed;
		
		ed = staticTDCH.computeEdgeDifference(3);
		assertEquals(ed, 1);
		
		ed = staticTDCH.computeEdgeDifference(0);
		assertEquals(ed, -2);
		
		int sh = staticTDCH.contractSingleNode(3);
		assertEquals(sh, 4);
		
		ed = staticTDCH.computeEdgeDifference(0);
		assertEquals(ed, -2);
		
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
		assertEquals(ed, -2);
		
		sh = staticTDCH.contractSingleNode(4);
		assertEquals(sh, 0);
		
		ed = staticTDCH.computeEdgeDifference(1);
		assertEquals(ed, 0);
		
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
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(0, 2, 3);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(0, 2, 3));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(3, 5, 9);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(3, 5, 9));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(2, 4, 9);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(2, 4, 9));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(3, 5, 17);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(3, 5, 17));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(0, 4, 3);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(0, 4, 3));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(4, 2, 5);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(4, 2, 5));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(5, 3, 8);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(5, 3, 8));
		
		eaTime = dynamicTDCH.computeEarliestArrivalTime(4, 3, 5);
		assertEquals(eaTime, dynamicTDDijkstra.computeEarliestArrivalTime(4, 3, 5));
	}
	
	@Test
	public void testEATimeSourceTargetDynamicAllDPTimes() {		
		int[] eaTimes;
		dynamicTDCH.precompute();
		
		Set<Long> nodes = dynamicTDCH.graph.nodes.keySet();
		for (Long u : nodes) {
			for (Long v : nodes) {
				eaTimes = dynamicTDCH.computeEarliestArrivalTimes(u, v);
				assertEquals(Arrays.toString(eaTimes), Arrays.toString(dynamicTDDijkstra.computeEarliestArrivalTimes(u, v)));
			}
		}
	}

	@Test
	public void testSH() {
		TDGraph graph = createSmallCustomGraph();
		TDCHAlgorithm tdch = new TDCHAlgorithm(graph);
		
		int n,sh;
		
		n = 0;
		graph.disableNode(n);
		sh = tdch.computeShortcuts(n, false);
		graph.enableNode(n);
		assertEquals(sh, 0);
		
		n = 1;
		graph.disableNode(n);
		sh = tdch.computeShortcuts(n, false);
		graph.enableNode(n);
		assertEquals(sh, 2);
		
		n = 2;
		graph.disableNode(n);
		sh = tdch.computeShortcuts(n, false);
		graph.enableNode(n);
		assertEquals(sh, 0);
	}
	
	@Test
	public void testEA() {
		TDGraph graph = createSmallCustomGraph();
		TDCHAlgorithm tdch = new TDCHAlgorithm(graph);
		TDDijkstraAlgorithm tdDijkstra = new TDDijkstraAlgorithm(graph);
		
		tdch.precompute();
		
		int ea;
		
		ea = tdch.computeEarliestArrivalTime(0, 1, 0);
		assertEquals(ea, tdDijkstra.computeEarliestArrivalTime(0, 1, 0));
		
		ea = tdch.computeEarliestArrivalTime(1, 0, 0);
		assertEquals(ea, tdDijkstra.computeEarliestArrivalTime(1, 0, 0));
		
		ea = tdch.computeEarliestArrivalTime(0, 1, 1);
		assertEquals(ea, tdDijkstra.computeEarliestArrivalTime(0, 1, 1));
		
		ea = tdch.computeEarliestArrivalTime(1, 0, 1);
		assertEquals(ea, tdDijkstra.computeEarliestArrivalTime(1, 0, 1));
		
		ea = tdch.computeEarliestArrivalTime(0, 1, 2);
		assertEquals(ea, tdDijkstra.computeEarliestArrivalTime(0, 1, 2));
		
		ea = tdch.computeEarliestArrivalTime(1, 0, 2);
		assertEquals(ea, tdDijkstra.computeEarliestArrivalTime(1, 0, 2));
		
		ea = tdch.computeEarliestArrivalTime(1, 2, 0);
		assertEquals(ea, tdDijkstra.computeEarliestArrivalTime(1, 2, 0));
		
		ea = tdch.computeEarliestArrivalTime(2, 1, 0);
		assertEquals(ea, tdDijkstra.computeEarliestArrivalTime(2, 1, 0));
		
		ea = tdch.computeEarliestArrivalTime(2, 1, 1);
		assertEquals(ea, tdDijkstra.computeEarliestArrivalTime(2, 1, 1));
		
		ea = tdch.computeEarliestArrivalTime(2, 1, 1);
		assertEquals(ea, tdDijkstra.computeEarliestArrivalTime(2, 1, 1));
		
		ea = tdch.computeEarliestArrivalTime(2, 1, 2);
		assertEquals(ea, tdDijkstra.computeEarliestArrivalTime(2, 1, 2));
		
		ea = tdch.computeEarliestArrivalTime(2, 1, 2);
		assertEquals(ea, tdDijkstra.computeEarliestArrivalTime(2, 1, 2));
	}

	public TDGraph createSmallCustomGraph() {
		TDGraph graph = new TDGraph();
		graph.timeInterval = 3;
		
		graph.addNode(0);
		graph.addNode(1);
		graph.addNode(2);
		
		graph.addEdge(0, 1, new int[]{1,1,1});
		graph.addEdge(0, 2, new int[]{3,2,2});
		graph.addEdge(1, 2, new int[]{1,1,1});
		
		graph.setArcFlagsForAllEdges(true);
		
		return graph;
	}
	
	private void createCustomGraph() {
		staticGraph = new TDGraph();
		staticGraph.addNode(0);
		staticGraph.addNode(1);
		staticGraph.addNode(2);
		staticGraph.addNode(3);
		staticGraph.addNode(4);
		staticGraph.addNode(5);
		staticGraph.addEdge(0, 1, ArrayUtils.extrapolateArray(new int[]{1,1,1,1}, 5));
		staticGraph.addEdge(0, 3, ArrayUtils.extrapolateArray(new int[]{3,3,3,3}, 5));
		staticGraph.addEdge(1, 3, ArrayUtils.extrapolateArray(new int[]{2,2,2,2}, 5));
		staticGraph.addEdge(1, 4, ArrayUtils.extrapolateArray(new int[]{3,3,3,3}, 5));
		staticGraph.addEdge(1, 5, ArrayUtils.extrapolateArray(new int[]{4,4,4,4}, 5));
		staticGraph.addEdge(2, 1, ArrayUtils.extrapolateArray(new int[]{1,1,1,1}, 5));
		staticGraph.addEdge(3, 4, ArrayUtils.extrapolateArray(new int[]{1,1,1,1}, 5));
		staticGraph.addEdge(4, 5, ArrayUtils.extrapolateArray(new int[]{1,1,1,1}, 5));
		
		staticGraph.setArcFlagsForAllEdges(true);
	}
}