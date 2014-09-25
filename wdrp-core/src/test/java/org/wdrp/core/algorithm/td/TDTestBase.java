package org.wdrp.core.algorithm.td;

import java.io.FileNotFoundException;
import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.wdrp.core.model.Arc;
import org.wdrp.core.model.Graph;
import org.wdrp.core.model.TDGraph;
import org.wdrp.core.model.Weather;
import org.wdrp.core.util.ArrayUtils;
import org.wdrp.core.util.GraphUtils;
import org.wdrp.core.util.WeatherUtils;

public class TDTestBase {
	protected static TDGraph g1;
	protected static TDGraph g2;
	protected static TDGraph tdGraphTwoMin;
	
	@After
	public void breakTDGraph() {
		g1.clear();
		tdGraphTwoMin.clear();
	}
	
	@Before
	public void setUpTDGraph() {
		buildStandardTDGraph1();
		buildTwoMinTDGraph();
		buildStandardTDGraph2();
	}

	private void buildTwoMinTDGraph() {
		tdGraphTwoMin = new TDGraph(60,2);
		tdGraphTwoMin.addNode(0);
		tdGraphTwoMin.addNode(1);
		tdGraphTwoMin.addNode(2);
		tdGraphTwoMin.addNode(3);
		tdGraphTwoMin.addNode(4);
		tdGraphTwoMin.addNode(5);
		tdGraphTwoMin.addNode(6);
		tdGraphTwoMin.addNode(7);
		tdGraphTwoMin.addNode(8);
		tdGraphTwoMin.addNode(9);
		tdGraphTwoMin.addNode(10);
		tdGraphTwoMin.addNode(11);
		
		tdGraphTwoMin.addEdge(0, 1,new int[]{10,10});
		tdGraphTwoMin.addEdge(1, 2, new int[]{10,-1});
		tdGraphTwoMin.addEdge(2, 3, new int[]{20, -1});
		tdGraphTwoMin.addEdge(0, 4, new int[]{5, 5});
		tdGraphTwoMin.addEdge(4, 5, new int[]{5, 5});
		tdGraphTwoMin.addEdge(5, 3, new int[]{5, 5});
		tdGraphTwoMin.addEdge(5, 6, new int[]{-1, 5});
		tdGraphTwoMin.addEdge(6, 7, new int[]{45, 5});
		tdGraphTwoMin.addEdge(7, 8, new int[]{5, 5});
		tdGraphTwoMin.addEdge(8, 9, new int[]{30, 30});
		tdGraphTwoMin.addEdge(8, 10, new int[]{10, 10});
		tdGraphTwoMin.addEdge(10, 11, new int[]{5, -1});
		tdGraphTwoMin.addEdge(11, 9, new int[]{5, 5});
	}
	
	private void buildStandardTDGraph1() {
		g1 = new TDGraph(1,20);
		
		g1.addNode(0);
		g1.addNode(1);
		g1.addNode(2);
		g1.addNode(3);
		g1.addNode(4);
		g1.addNode(5);
		g1.addNode(6);
		g1.addNode(7);
		
		int[] e1 = ArrayUtils.extrapolateArray(new int[]{4,5,9,4},5);
		g1.addEdge(0, 1, e1);
		
		int[] e2 = ArrayUtils.extrapolateArray(new int[]{8,10,11,8},5);
		g1.addEdge(0, 2, e2);
		
		int[] e3 = ArrayUtils.extrapolateArray(new int[]{3,7,5,8},5);
		g1.addEdge(1, 2, e3);
		
		int[] e4 = ArrayUtils.extrapolateArray(new int[]{6,7,6,6},5);
		g1.addEdge(2, 3, e4);
		
		int[] e5 = ArrayUtils.extrapolateArray(new int[]{3,10,6,9},5);
		g1.addEdge(3, 4, e5);
		
		int[] e6 = ArrayUtils.extrapolateArray(new int[]{5,7,5,9},5);
		g1.addEdge(3, 5, e6);
		
		int[] e7 = ArrayUtils.extrapolateArray(new int[]{3,3,3,3},5);
		g1.addEdge(4, 5, e7);
		
		int[] e8 = ArrayUtils.extrapolateArray(new int[]{3,7,10,6},5);
		g1.addEdge(5, 6, e8);
		
		int[] e9 = ArrayUtils.extrapolateArray(new int[]{5,5,8,7},5);
		g1.addEdge(6, 7, e9);
	}
	
	private void buildStandardTDGraph2() {
		g2 = new TDGraph(900,2);
		
		g2.addNode(0);
		g2.addNode(1);
		g2.addNode(2);
		g2.addNode(3);
		g2.addNode(4);
		g2.addNode(5);
		g2.addNode(6);
		g2.addNode(7);
		g2.addNode(8);
		g2.addNode(9);
		g2.addNode(10);
		g2.addNode(11);
		
		g2.addEdge(0, 1,new int[]{10,10});
		g2.addEdge(1, 2, new int[]{10, 10});
		g2.addEdge(2, 3, new int[]{-1, 20});
		g2.addEdge(3, 4, new int[]{-1, 20});
		g2.addEdge(4, 5, new int[]{21, 21});
		g2.addEdge(0, 6,new int[]{10,10});
		g2.addEdge(6, 7,new int[]{20,20});
		g2.addEdge(7, 8,new int[]{30,30});
		g2.addEdge(8, 9,new int[]{10,10});
		g2.addEdge(9, 10,new int[]{30,30});
		g2.addEdge(10, 11,new int[]{20,20});
		g2.addEdge(11, 5,new int[]{50,50});
	}
	
	public TDGraph getTestTDGraphGeneratedFromWeahter() throws FileNotFoundException, ParseException {
		GraphUtils.convertOSMToGraph("test.osm", false);
		WeatherUtils.generateWeatherFromKML("test.kml", "test.wea");
		
		Graph<Arc> graph = new Graph<Arc>("test.graph");
		Weather weather =  new Weather("test.wea");
		
		return GraphUtils.convertGraphToTDGraphWithWeather(graph, weather, false);
	}
}		