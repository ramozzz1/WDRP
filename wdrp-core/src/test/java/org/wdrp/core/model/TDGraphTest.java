package org.wdrp.core.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class TDGraphTest {
	
	@Test
	public void testTDGraph() {
		TDGraph g = new TDGraph();
		
		g.addNode(0);
		g.addNode(1);
		g.addNode(2);
		
		int[] e1 = {1,1,1,1};
		g.addEdge(0, 1, e1);
		
		int[] e2 = {1,2,1,2};
		g.addEdge(0, 2, e2);
		
		int[] e3 = {2,1,2,1};
		g.addEdge(1, 2, e3);
		
		TDArc a = (TDArc)g.getArc(0, 1);
		assertEquals(Arrays.toString(a.getCosts()),"[1, 1, 1, 1]");
		TDArc b = (TDArc)g.getArc(0, 2);
		assertEquals(Arrays.toString(b.getCosts()),"[1, 2, 1, 2]");
		TDArc c = (TDArc)g.getArc(1, 2);
		assertEquals(Arrays.toString(c.getCosts()),"[2, 1, 2, 1]");
	}
	
	@Test
	public void testGetCostForTimeGraph() {
		TDGraph g = new TDGraph();
		
		g.addNode(0);
		g.addNode(1);
		
		int[] e1 = {1,5,10,6};
		g.addEdge(0, 1, e1);
		
		TDArc a = (TDArc)g.getArc(0, 1);
		assertEquals(a.getCostForTime(0), 1);
		assertEquals(a.getCostForTime(1), 5);
		assertEquals(a.getCostForTime(2), 10);
		assertEquals(a.getCostForTime(3), 6);
	}
}
