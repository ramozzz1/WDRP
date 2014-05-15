package models;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import main.Config;
import model.Graph;
import model.Node;

import org.junit.Test;

import reader.OSMParser;

public class GraphTest {
	private static final String SMALL_OSM_FILE = Config.OSMDIR+"small-osm-file.osm";
	
	@Test
	public void testEmptyGraph(){
		Graph g = new Graph();
		assertEquals("{0, 0, []}", g.toString());
	}
	
	@Test
	public void testAddVertex(){
		Graph g = new Graph();
		g.addNode(0);
		g.addNode(1);
		g.addNode(2);
		assertEquals("{3, 0, []}", g.toString());
	}
	
	@Test
	public void testAddEdge(){
		Graph g = new Graph();
		int n0 = 0;
		int n1 = 1;
		int n2 = 2;
		
		g.addNode(n0);
		g.addNode(n1);
		g.addNode(n2);
		g.addEdge(n0,n2,3);
		g.addEdge(n1,n2,5);
		assertEquals("{3, 4, [(2,1,5), (2,0,3), (1,2,5), (0,2,3)]}", g.toString());
	}
	
	@Test
	public void testEqualNode(){
		Node n0 = new Node(0);
		Node n1 = new Node(0);

		assertTrue(n0.equals(n1));
	}
	
	@Test
	public void testRemoveNode(){
		Graph g = new Graph();
		int n0 = 0;
		int n1 = 1;

		g.addNode(n0);
		g.addNode(n1);
		assertEquals("{2, 0, []}", g.toString());
		
		g.removeNode(n0);
		assertEquals("{1, 0, []}", g.toString());
		
		g.removeNode(n1);
		assertEquals("{0, 0, []}", g.toString());
	}
	
	@Test
	public void testRemoveEdge(){
		Graph g = new Graph();
		int n0 = 0;
		int n1 = 1;

		g.addNode(n0);
		g.addNode(n1);
		g.addEdge(n0, n1, 1);
		assertEquals("{2, 2, [(1,0,1), (0,1,1)]}", g.toString());
		
		g.removeAllEdgesOfNode(n0);
		assertEquals("{2, 1, [(1,0,1)]}", g.toString());
		
		g.removeAllEdgesOfNode(n1);
		assertEquals("{2, 0, []}", g.toString());
	}
	
	@Test
	public void testAddDoubleNode(){
		Graph g = new Graph();
		int n0 = 0;
		int n1 = 0;

		g.addNode(n0);
		g.addNode(n1);
		assertEquals("{1, 0, []}", g.toString());
	}
	
	@Test
	public void testBounds(){
		Graph g = new Graph();
		g.addNode(0,0,0);
		g.addNode(1,0,1);
		g.addNode(2,1,1);
		g.addNode(3,1,2);
		g.addNode(4,2,1);
		g.addNode(5,1,3);		
		g.addNode(999,-10,10);
		assertEquals("{\"minLat\":-10.0, \"maxLat\":2.0, \"minLon\":0.0, \"maxLon\":10.0}", g.getBounds().toString());
	}
	
	@Test
	public void testEnableDisableNode(){
		Graph g = new Graph();
		int n0 = 0;
		int n1 = 1;
		int n2 = 2;
		
		g.addNode(n0);
		g.addNode(n1);
		g.addNode(n2);
		g.addEdge(n0, n1, 1);
		g.addEdge(n1, n2, 1);
		g.addEdge(n2, n0, 1);
		
		g.setArcFlagsForAllEdges(true);
		
		assertEquals(g.getNumNeighborsNotDisabled(n1),2);
		assertEquals(g.getNeighborsNotDisabled(n1).toString(),"[(0, 1, true, -1), (2, 1, true, -1)]");
		
		g.disableNode(n0);
		assertEquals(g.getNumNeighborsNotDisabled(n1),1);
		assertEquals(g.getNeighborsNotDisabled(n1).toString(),"[(2, 1, true, -1)]");
		
		g.enableNode(n0);
		assertEquals(g.getNumNeighborsNotDisabled(n1),2);
		assertEquals(g.getNeighborsNotDisabled(n1).toString(),"[(0, 1, true, -1), (2, 1, true, -1)]");
		
		g.disableNode(n0);
		g.disableNode(n2);
		assertEquals(g.getNumNeighborsNotDisabled(n1),0);
		assertEquals(g.getNeighborsNotDisabled(n1).toString(),"[]");
	}
	
	/*@Test
	public void testAddDoubleEdge(){
		Graph g = new Graph();
		Node n0 = new Node("0");
		Node n1 = new Node("1");
		Edge e0 = new Edge(n0,n1,3);
		Edge e1 = new Edge(n1,n0,3);

		g.addNode(n0);
		g.addNode(n1);
		g.addEdge(e0);
		g.addEdge(e1);
		assertEquals("{2, 2, [(0,1,3.0), (1,0,3.0)]}", g.toString());
	}*/
	
	@Test
	public void testReadSmallOSMFileS(){
		OSMParser parser = new OSMParser();
		Graph g = parser.osmToGraph(SMALL_OSM_FILE);
		assertEquals("{4, 4, [(2464420207,2464420260,2), (2464420260,2464420207,2), (2464420281,2464420271,3), (2464420271,2464420281,3)]}", g.toString());
	}
}
