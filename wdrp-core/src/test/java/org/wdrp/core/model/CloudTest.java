package org.wdrp.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CloudTest {
	
	private double[][] coordinatesGCS = {{4.493751525878906,51.90911993117428},{4.513664245605469,51.90911993117428},{4.5119476318359375,51.91737935008507},{4.491691589355469,51.91610876913393}};
	
	@Test
	public void testWholeLineWithinCloud() {
		Cloud c = new Cloud(coordinatesGCS);
		
		double x1 = 4.499931335449219;
		double x2 = 4.505424499511719;
		double y1 = 51.912296810541456;
		double y2 = 51.913567499362514;

		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testPartOfLineWithinCloud() {
		Cloud c = new Cloud(coordinatesGCS);
		
		double x1 = 4.497356414794922;
		double x2 = 4.515895843505859;
		double y1 = 51.90647236002553;
		double y2 = 51.91653230011276;

		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testPartOfLineWithinCloud1() {
		double[][] crd = {
							{4.505767822265625,51.916479358958874},
							{4.505767822265625,51.90896108130657},
							{4.516582489013672,51.90880223087697},
							{4.515724182128906,51.91579111827799}
						};
		
		Cloud c = new Cloud(crd);
		
		double x1 = 4.501562118530273;
		double x2 = 4.508771896362305;
		double y1 = 51.91303805005687;
		double y2 = 51.91293215944665;

		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testPartOfLineWithinCloud2() {
		double[][] crd = { 
							{ 4.504609107971191, 51.91348808236464 },
							{ 4.504952430725098, 51.91286597768845 },
							{ 4.506196975708008, 51.91315717669492 },
							{ 4.506111145019531, 51.913554263205945 } 
						};
		
		Cloud c = new Cloud(crd);

		double x1 = 4.508771896362305;
		double x2 = 4.501562118530273;
		double y1 = 51.91293215944665;
		double y2 = 51.91293215944665;

		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testWholeLineOutOfCloud1() {
		double[][] crd = {
							{4.505767822265625,51.916479358958874},
							{4.505767822265625,51.90896108130657},
							{4.516582489013672,51.90880223087697},
							{4.515724182128906,51.91579111827799}
						};
		
		Cloud c = new Cloud(crd);
		
		double x1 = 4.501562118530273;
		double x2 = 4.501476287841797;
		double y1 = 51.91303805005687;
		double y2 =  51.910867242637416;

		assertFalse(c.intersectsLine(x1,x2,y1,y2));
	}
    	
	@Test
	public void testWholeLineWithinCloudWithLinePointsOnBorder() {
		Cloud c = new Cloud(coordinatesGCS);
		
		double x1 = 4.493751525878906;
		double x2 = 4.5119476318359375;
		double y1 = 51.90911993117428;
		double y2 = 51.91737935008507;
		
		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testWholeLineWithinCloudWithEndPointJustOverBorder() {
		Cloud c = new Cloud(coordinatesGCS);
		
		double x1 = 4.491691589355469;
		double x2 = 4.518556594848633;
		double y1 = 51.91610876913393;
		double y2 = 51.90769026213801;
		
		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testLineCrossesCloud() {
		Cloud c = new Cloud(coordinatesGCS);
		
		double x1 = 4.464054107666016;
		double x2 = 4.5284271240234375;
		double y1 = 51.90350689504887;
		double y2 = 51.9300831820897;
		
		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testLineOutsideCloud() {
		Cloud c = new Cloud(coordinatesGCS);
		
		double x1 = 4.489717483520508;
		double x2 = 4.519500732421875;
		double y1 = 51.906948934350396;
		double y2 = 51.90689598189732;
		
		assertFalse(c.intersectsLine(x1,x2,y1,y2));
	}
}