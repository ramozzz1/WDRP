package org.wdrp.core.util;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wdrp.core.model.Arc;
import org.wdrp.core.model.Cloud;
import org.wdrp.core.model.Graph;
import org.wdrp.core.model.TDGraph;
import org.wdrp.core.model.Weather;

public class GraphUtilsTest {
	private static Graph<Arc> g;
	private static Weather w;
	private static TDGraph tdGraph;
	
	@Before
	public void setup() {
		g = new Graph<Arc>();
		g.addNode(0,51.910867242637416,4.501476287841797);
		g.addNode(1,51.91303805005687,4.501562118530273);
		g.addNode(2,51.91293215944665,4.508771896362305);
		g.addNode(3,51.91054955470073,4.508600234985352);
		
		g.addEdge(0,new Arc(1, 5));
		g.addEdge(1,new Arc(2, 5));
		g.addEdge(2,new Arc(3, 5));
		g.addEdge(3,new Arc(0, 5));
		
		w = new Weather();
		w.setBeginTime("17:00");
		w.setEndTime("17:15");
		w.setTimeStep(5);
	}
	
	@Test
	public void testGenerateTDGraphWithOneCloudOverlappingSomeEdges() throws ParseException {
		double[][] crd = {{4.505767822265625,51.916479358958874},{4.505767822265625,51.90896108130657},{4.516582489013672,51.90880223087697},{4.515724182128906,51.91579111827799}};
		w.addCloud("17:00", new Cloud(crd));
		
		tdGraph = GraphUtils.convertGraphToTDGraphWithWeather(g, w);
		
		assertEquals("[5, 5, 5, 5]", Arrays.toString(tdGraph.getArc(0, 1).costs));
		assertEquals("[-1, 5, 5, 5]", Arrays.toString(tdGraph.getArc(2, 3).costs));
		assertEquals("[-1, 5, 5, 5]", Arrays.toString(tdGraph.getArc(3, 0).costs));
		assertEquals("[-1, 5, 5, 5]", Arrays.toString(tdGraph.getArc(1, 2).costs));
	}
	
	@Test
	public void testGenerateTDGraphWithOneCloudOverlappingSomeEdgesAtAllTimes() throws ParseException {
		double[][] crd = {{4.505767822265625,51.916479358958874},{4.505767822265625,51.90896108130657},{4.516582489013672,51.90880223087697},{4.515724182128906,51.91579111827799}};
		w.addCloud("17:00", new Cloud(crd));
		w.addCloud("17:05", new Cloud(crd));
		w.addCloud("17:10", new Cloud(crd));
		w.addCloud("17:15", new Cloud(crd));
		
		tdGraph = GraphUtils.convertGraphToTDGraphWithWeather(g, w);
		
		assertEquals("[5, 5, 5, 5]", Arrays.toString(tdGraph.getArc(0, 1).costs));
		assertEquals("[-1, -1, -1, -1]", Arrays.toString(tdGraph.getArc(2, 3).costs));
		assertEquals("[-1, -1, -1, -1]", Arrays.toString(tdGraph.getArc(3, 0).costs));
		assertEquals("[-1, -1, -1, -1]", Arrays.toString(tdGraph.getArc(1, 2).costs));
	}
	
	@Test
	public void testGenerateTDGraphWithOneCloudOverlappingAllEdgesAtAllTimes() throws ParseException {
		double[][] crd = {{4.494953155517578,51.914944038342156},{4.517312049865723,51.914811680658616},{4.5171403884887695,51.90853747891245},{4.495339393615723, 51.90861690466568}};
		w.addCloud("17:00", new Cloud(crd));
		w.addCloud("17:05", new Cloud(crd));
		w.addCloud("17:10", new Cloud(crd));
		w.addCloud("17:15", new Cloud(crd));
		
		tdGraph = GraphUtils.convertGraphToTDGraphWithWeather(g, w);
		
		assertEquals("[-1, -1, -1, -1]", Arrays.toString(tdGraph.getArc(0, 1).costs));
		assertEquals("[-1, -1, -1, -1]", Arrays.toString(tdGraph.getArc(2, 3).costs));
		assertEquals("[-1, -1, -1, -1]", Arrays.toString(tdGraph.getArc(3, 0).costs));
		assertEquals("[-1, -1, -1, -1]", Arrays.toString(tdGraph.getArc(1, 2).costs));
	}
	
	@Test
	public void testGenerateTDGraphWithOneCloudOverlappingNoEdgesAtAllTimes() throws ParseException {
		double[][] crd = {{4.501862525939941,51.920211557377826},{4.508686065673828,51.920714454011666},{4.508728981018066,51.91925868514854},{4.499931335449219,51.91894105657773}};
		w.addCloud("17:00", new Cloud(crd));
		w.addCloud("17:05", new Cloud(crd));
		w.addCloud("17:10", new Cloud(crd));
		w.addCloud("17:15", new Cloud(crd));
		
		tdGraph = GraphUtils.convertGraphToTDGraphWithWeather(g, w);
		
		assertEquals("[5, 5, 5, 5]", Arrays.toString(tdGraph.getArc(0, 1).costs));
		assertEquals("[5, 5, 5, 5]", Arrays.toString(tdGraph.getArc(2, 3).costs));
		assertEquals("[5, 5, 5, 5]", Arrays.toString(tdGraph.getArc(3, 0).costs));
		assertEquals("[5, 5, 5, 5]", Arrays.toString(tdGraph.getArc(1, 2).costs));
	}
	
	@Test
	public void testGenerateTDGraphWithCloudsOverlappingSomeEdgesAtSomeTimes() throws ParseException {
		double[][] crd1 = {{4.502023458480835,51.9114562830734},{4.502162933349609,51.91166145264695},{4.502474069595337,51.9114761382344},{4.502323865890503,51.911403335934516}};
		w.addCloud("17:00", new Cloud(crd1));
		double[][] crd2 =  { { 4.50261354446411, 51.91178720127707 }, { 4.503171443939209, 51.911840147963375 }, { 4.5031821727752686, 51.91209826216509 }, { 4.50264573097229, 51.91208502557545 } };
		w.addCloud("17:05", new Cloud(crd2));
		double[][] crd3 = { { 4.503579139709472, 51.9126674318286 }, { 4.503686428070068, 51.91234975662692 }, { 4.504351615905762, 51.91254830389134 }, { 4.504265785217285, 51.912905686755074 } };
		w.addCloud("17:10", new Cloud(crd3));
		double[][] crd4 = { { 4.504609107971191, 51.91348808236464 }, { 4.504952430725098, 51.91286597768845 }, { 4.506196975708008, 51.91315717669492 }, { 4.506111145019531, 51.913554263205945 } };
		w.addCloud("17:15", new Cloud(crd4));
		
		double[][] crd1_1 =  { { 4.5067548751831055, 51.91113198086792 }, { 4.506733417510986, 51.910403946978676 }, { 4.505832195281982, 51.910443658222434 }, { 4.505617618560791, 51.91106579645663 } };
		w.addCloud("17:00", new Cloud(crd1_1));
		double[][] crd2_1 =  { { 4.506690502166748, 51.91180705629177 }, { 4.506711959838867, 51.91139671753777 }, { 4.5075058937072745, 51.91144966468446 }, { 4.507720470428467, 51.91192618619546 }, { 4.507205486297607, 51.912323283591995 }, { 4.50664758682251, 51.91227033747535 } };
		w.addCloud("17:05", new Cloud(crd2_1));
		double[][] crd3_1 = { { 4.507870674133301, 51.91249535804 }, { 4.5090508460998535, 51.91193942283191 }, { 4.509930610656737, 51.91257477679359 }, { 4.508857727050781, 51.912826268586706 } };
		w.addCloud("17:10", new Cloud(crd3_1));
		double[][] crd4_1 = { { 4.510424137115478, 51.91335572038936 }, { 4.510939121246338, 51.91279979583272 }, { 4.512183666229248, 51.913302775490024 }, { 4.5116472244262695, 51.913686624595975 }};
		w.addCloud("17:15", new Cloud(crd4_1));
		
		tdGraph = GraphUtils.convertGraphToTDGraphWithWeather(g, w);
		
		assertEquals("[5, 5, 5, 5]", Arrays.toString(tdGraph.getArc(0, 1).costs));
		assertEquals("[5, 5, -1, 5]", Arrays.toString(tdGraph.getArc(2, 3).costs));
		assertEquals("[-1, 5, 5, 5]", Arrays.toString(tdGraph.getArc(3, 0).costs));
		assertEquals("[5, 5, 5, -1]", Arrays.toString(tdGraph.getArc(1, 2).costs));
	}
	
	@After
	public void breakup() {
		assertEquals(g.getNumNodes(), tdGraph.getNumNodes());
		assertEquals(g.getNumEdges(), tdGraph.getNumEdges());
	}
}
