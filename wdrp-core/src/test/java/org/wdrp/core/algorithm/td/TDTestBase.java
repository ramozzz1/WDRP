package org.wdrp.core.algorithm.td;

import org.junit.After;
import org.junit.Before;
import org.wdrp.core.model.TDGraph;
import org.wdrp.core.util.ArrayUtils;

public class TDTestBase {
	public static TDGraph g;
	
	@After
	public void breakTDGraph() {
		g.clear();
	}
	
	@Before
	public void setUpTDGraph() {
		g = new TDGraph(1,20);
		
		g.addNode(0);
		g.addNode(1);
		g.addNode(2);
		g.addNode(3);
		g.addNode(4);
		g.addNode(5);
		g.addNode(6);
		g.addNode(7);
		
		int[] e1 = ArrayUtils.extrapolateArray(new int[]{4,5,9,4},5);
		g.addEdge(0, 1, e1);
		
		int[] e2 = ArrayUtils.extrapolateArray(new int[]{8,10,11,8},5);
		g.addEdge(0, 2, e2);
		
		int[] e3 = ArrayUtils.extrapolateArray(new int[]{3,7,5,8},5);
		g.addEdge(1, 2, e3);
		
		int[] e4 = ArrayUtils.extrapolateArray(new int[]{6,7,6,6},5);
		g.addEdge(2, 3, e4);
		
		int[] e5 = ArrayUtils.extrapolateArray(new int[]{3,10,6,9},5);
		g.addEdge(3, 4, e5);
		
		int[] e6 = ArrayUtils.extrapolateArray(new int[]{5,7,5,9},5);
		g.addEdge(3, 5, e6);
		
		int[] e7 = ArrayUtils.extrapolateArray(new int[]{3,3,3,3},5);
		g.addEdge(4, 5, e7);
		
		int[] e8 = ArrayUtils.extrapolateArray(new int[]{3,7,10,6},5);
		g.addEdge(5, 6, e8);
		
		int[] e9 = ArrayUtils.extrapolateArray(new int[]{5,5,8,7},5);
		g.addEdge(6, 7, e9);
	}
}		