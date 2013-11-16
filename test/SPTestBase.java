import model.Graph;

import org.junit.After;
import org.junit.Before;


public class SPTestBase {
	protected Graph g;
	
	@Before
	public void setUpGraph() {
		g = new Graph();
		g.addNode(0,0,0);
		g.addNode(1,0,1);
		g.addNode(2,1,1);
		g.addNode(3,1,2);
		g.addNode(4,2,1);
		g.addNode(5,1,3);		
		g.addNode(999,-10,10);
		
		g.addEdge(0,1,1);
		g.addEdge(0,2,3);
		g.addEdge(0,3,10);
		g.addEdge(0,4,4);
		g.addEdge(1,2,1);
		g.addEdge(2,3,1);
		g.addEdge(4,2,5);
		g.addEdge(4,5,3);
		g.addEdge(5,3,1);
	}
	
	@After
	public void clearGraph() {
		g.clear();
	}
}
