package algorithms.td;

import model.TDGraph;

import org.junit.BeforeClass;

import util.ArrayUtils;

public class TDTestBase {
	public static TDGraph g;
	
	@BeforeClass
	public static void setUpTDGraph() {
		g = new TDGraph();
		
		g.addNode(0);
		g.addNode(1);
		g.addNode(2);
		g.addNode(3);
		g.addNode(4);
		g.addNode(5);
		
		int[] e1 = ArrayUtils.extrapolateArrayToArray(new int[]{4,5,9,4},5);
		g.addEdge(0, 1, e1);
		
		int[] e2 = ArrayUtils.extrapolateArrayToArray(new int[]{8,10,11,8},5);
		g.addEdge(0, 2, e2);
		
		int[] e3 = ArrayUtils.extrapolateArrayToArray(new int[]{3,7,5,8},5);
		g.addEdge(1, 2, e3);
		
		int[] e4 = ArrayUtils.extrapolateArrayToArray(new int[]{6,7,6,6},5);
		g.addEdge(2, 3, e4);
		
		int[] e5 = ArrayUtils.extrapolateArrayToArray(new int[]{3,10,3,3},5);
		g.addEdge(3, 4, e5);
		
		int[] e6 = ArrayUtils.extrapolateArrayToArray(new int[]{5,7,5,9},5);
		g.addEdge(3, 5, e6);
		
		int[] e7 = ArrayUtils.extrapolateArrayToArray(new int[]{3,3,3,3},5);
		g.addEdge(4, 5, e7);
	}
}
