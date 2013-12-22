import static org.junit.Assert.assertEquals;
import model.Arc;
import model.Graph;
import model.Node;
import model.Path;

import org.junit.Before;
import org.junit.Test;

import algorithm.ContractionHierarchiesAlgorithm;


public class PathTest {
	
	private Path sp;
	private Path tp; 
	
	@Before
	public void setUp() {
		sp= new Path();
		sp.addNode(new Node(5), new Arc(5, 2));
		sp.addNode(new Node(4), new Arc(4, 5));
		sp.addNode(new Node(3), new Arc(3, 6));
		sp.addNode(new Node(2), new Arc(2, 2));
		sp.addNode(new Node(1), new Arc(1, 1));
		sp.addNode(new Node(0), null);
		
		tp = new Path();
		tp.addNode(new Node(10), new Arc(10, 6));
		tp.addNode(new Node(9), new Arc(9, 23));
		tp.addNode(new Node(8), new Arc(8, 12));
		tp.addNode(new Node(7), new Arc(7, 3));
		tp.addNode(new Node(6), new Arc(6, 10));
		tp.addNode(new Node(5), null);
	}
	
	@Test
	public void testPath() {
		assertEquals(sp.getNodes().size(), 6);
		assertEquals(sp.getArcs().size(), 5);
		assertEquals(sp.getCost(), 16);
		assertEquals(sp.toString(), "[0->1->2->3->4->5]");
	}
	
	@Test
	public void testReversePath() {
		Path reverseSp = sp.reversePath();
		assertEquals(reverseSp.getNodes().size(), 6);
		assertEquals(reverseSp.getArcs().size(), 5);
		assertEquals(reverseSp.toString(), "[5->4->3->2->1->0]");
	}
	
	@Test
	public void testConnectPath() {
		Path reverseTp = tp.reversePath();
		sp.connect(reverseTp);
		
		//assertEquals(sp.getNodes().size(), 10);
		//assertEquals(sp.getArcs().size(), 9);
		//assertEquals(sp.getCost(), 0);
		//assertEquals(sp.toString(), "[0->1->2->3->5->6->7->8->9->10->11]");
	}
}
