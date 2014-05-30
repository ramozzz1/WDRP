package algorithms;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import gnu.trove.map.hash.TLongIntHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import model.Arc;
import model.Graph;
import model.Path;
import model.QEntry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.CommonUtils;
import algorithm.CHAlgorithm;

public class CHAlgorithmTest extends SPTestBase {
	
	private CHAlgorithm d;
	private CHAlgorithm a;
	private Graph<Arc> customGraph;
	
	@Before
	public void setUpCH() {
		createCustomGraph();
		a = new CHAlgorithm(customGraph);
		d = new CHAlgorithm(g);
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
		Arc a1 = customGraph.getArc(0, 2);
		assertNotNull(a1);
		assertEquals(a1.getCost(), 2);
		Arc a2 = customGraph.getArc(2, 3);
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
	
	@Test
	public void testShortestPathSourceSourceCustomGraph() {
		a.precompute();
		int dist = a.computeShortestPath(0, 0);
		assertEquals(dist,0);
	}
	
	@Test
	public void testShortestPathSourceSource() {
		d.precompute();
		int dist = d.computeShortestPath(0, 0);
		assertEquals(dist,0);
	}
	
	@Test
	public void testShortestPathSourceNeighbor() {		
		d.precompute();
		int dist = d.computeShortestPath(0, 1);
		assertEquals(dist,1);
	}
	
	@Test
	public void testShortestPathSourceTarget(){
		d.precompute();
		int dist = d.computeShortestPath(0, 3);
		assertEquals(dist,3);
	}
	
	@Test
	public void testShortestPathSourceTarget2(){
		d.precompute();
		int dist = d.computeShortestPath(0, 4);
		assertEquals(dist,4);
	}
	
	@Test
	public void testShortestPathSourceTarget3(){
		d.precompute();
		int dist = d.computeShortestPath(0, 5);
		assertEquals(dist,4);
	}
	
	@Test
	public void testNoPath(){
		d.precompute();
		int dist = d.computeShortestPath(0, 999);
		assertEquals(dist,-1);
	}
	
	@Test
	public void testExtractPath() {
		d.precompute();
		int c = d.computeShortestPath(0, 5);
		Path p = d.extractPath(5);
		assertEquals(p.getNodes().size(), 5);
		assertEquals(p.getArcs().size(), 4);
		assertEquals(p.getCost(), c);
		assertEquals(p.getCost(), 4);
		assertEquals(p.toString(), "[0->1->2->3->5]");
	}
	
	@Test
	public void testExtractPathNeighbor() {
		d.precompute();
		d.computeShortestPath(0, 1);
		Path p = d.extractPath(1);
		assertEquals(p.getNodes().size(), 2);
		assertEquals(p.getArcs().size(), 1);
		assertEquals(p.getCost(), 1);
		assertEquals(p.toString(), "[0->1]");
	}
	
	@Test
	public void testExtractNoPath() {
		d.precompute();
		d.computeShortestPath(0, 999);
		Path p = d.extractPath(999);
		assertEquals(p.getNodes().size(), 0);
		assertEquals(p.getArcs().size(), 0);
		assertEquals(p.getCost(), 0);
		assertEquals(p.toString(), "[]");
	}
	
	@Test
	public void testExtractSoureSourcePath() {
		d.precompute();
		d.computeShortestPath(0, 0);
		Path p = d.extractPath(0);
		assertEquals(p.getNodes().size(), 1);
		assertEquals(p.getArcs().size(), 0);
		assertEquals(p.getCost(), 0);
		assertEquals(p.toString(), "[0]");
	}
	
	@Test
	public void testConstructPathWithShortcuts() {
		Map<Long,Long> previous = new HashMap<Long,Long>();
		Graph<Arc> g = new Graph<Arc>();
		g.addNode(0);
		g.addNode(1);
		g.addNode(2);
		g.addNode(3);
		g.addNode(4);
		g.addNode(5);
		g.addEdge(0, 1, 0, true, -1);
		g.addEdge(1, 2, 0, true, -1);
		g.addEdge(1, 3, 0, true, 2);
		g.addEdge(1, 4, 0, true, 3);
		g.addEdge(2, 3, 0, true, -1);
		g.addEdge(3, 4, 0, true, -1);
		g.addEdge(4, 5, 0, true, -1);
		
		previous.put(5L,4L);
		previous.put(4L,1L);
		previous.put(1L,0L);
		previous.put(0L,-1L);
		
		CHAlgorithm ch = new CHAlgorithm(g);
		Path p = ch.contructPath(previous, 5);
		assertEquals(p.toString(), "[0->1->2->3->4->5]");
	}
	
	@Test
	public void testConstructPathWithShortcuts1() {
		Map<Long,Long> previous = new HashMap<Long,Long>();
		Graph<Arc> g = new Graph<Arc>();
		g.addNode(0);
		g.addNode(1);
		g.addNode(2);
		g.addNode(3);
		g.addNode(4);
		g.addNode(5);
		g.addEdge(0, 1, 0, true, -1);
		g.addEdge(1, 2, 0, true, -1);
		g.addEdge(1, 3, 0, true, 2);
		g.addEdge(1, 4, 0, true, 3);
		g.addEdge(2, 3, 0, true, -1);
		g.addEdge(3, 4, 0, true, -1);
		g.addEdge(4, 5, 0, true, -1);
		
		previous.put(3L,1L);
		previous.put(1L,-1L);
		
		CHAlgorithm ch = new CHAlgorithm(g);
		Path p = ch.contructPath(previous, 3);
		assertEquals(p.toString(), "[1->2->3]");
	}
	
	@Test
	public void testConstructPathWithShortcuts2() {
		Map<Long,Long> previous = new HashMap<Long,Long>();
		Graph<Arc> g = new Graph<Arc>();
		g.addNode(0);
		g.addNode(1);
		g.addNode(2);
		g.addNode(3);
		g.addNode(4);
		g.addNode(5);
		g.addEdge(0, 1, 0, true, -1);
		g.addEdge(1, 2, 0, true, -1);
		g.addEdge(1, 3, 0, true, 2);
		g.addEdge(1, 4, 0, true, 3);
		g.addEdge(2, 3, 0, true, -1);
		g.addEdge(3, 4, 0, true, -1);
		g.addEdge(4, 5, 0, true, -1);
		
		previous.put(1L,4L);
		previous.put(4L,-1L);
		
		CHAlgorithm ch = new CHAlgorithm(g);
		Path p = ch.contructPath(previous, 1);
		assertEquals(p.toString(), "[4->3->2->1]");
	}
	
	@Test
	public void testConstructPathWithShortcuts3() {
		Map<Long,Long> previous = new HashMap<Long,Long>();
		Graph<Arc> g = new Graph<Arc>();
		g.addNode(0);
		g.addNode(1);
		g.addNode(2);
		g.addNode(3);
		g.addNode(4);
		g.addNode(5);
		g.addEdge(0, 1, 0, true, -1);
		g.addEdge(0, 2, 0, true, 1);
		g.addEdge(0, 4, 0, true, 2);
		g.addEdge(1, 2, 0, true, -1);
		g.addEdge(2, 3, 0, true, -1);
		g.addEdge(2, 4, 0, true, 3);
		g.addEdge(3, 4, 0, true, -1);
		
		previous.put(4L,0L);
		previous.put(0L,-1L);
		
		CHAlgorithm ch = new CHAlgorithm(g);
		Path p = ch.contructPath(previous, 4);
		assertEquals(p.toString(), "[0->1->2->3->4]");
	}

	private void createCustomGraph() {
		customGraph = new Graph<Arc>();
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