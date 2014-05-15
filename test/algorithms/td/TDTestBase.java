package algorithms.td;

import model.TDGraph;

import org.junit.BeforeClass;

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
		
		int[] e1 = {4,5,9,4};
		g.addEdge(0, 1, e1);
		
		int[] e2 = {8,10,11,8};
		g.addEdge(0, 2, e2);
		
		int[] e3 = {3,7,5,8};
		g.addEdge(1, 2, e3);
		
		int[] e4 = {6,7,6,6};
		g.addEdge(2, 3, e4);
		
		int[] e5 = {3,10,3,3};
		g.addEdge(3, 4, e5);
		
		int[] e6 = {5,7,5,9};
		g.addEdge(3, 5, e6);
		
		int[] e7 = {3,3,3,3};
		g.addEdge(4, 5, e7);
	}
}
