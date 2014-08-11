package org.wdrp.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

public class CloudTest {
	
	private ArrayList<Coordinate> coordinatesRel;
	private ArrayList<Coordinate> coordinatesLatLng;
	
	@Before
	public void setup() {
		coordinatesRel = new ArrayList<Coordinate>();
		coordinatesRel.add(new Coordinate(0, 0, 0));
		coordinatesRel.add(new Coordinate(4, 0, 0));
		coordinatesRel.add(new Coordinate(4, 4, 0));
		coordinatesRel.add(new Coordinate(0, 4, 0));
		
		coordinatesLatLng = new ArrayList<Coordinate>();
		coordinatesLatLng.add(new Coordinate(73.46008301171878,21.329035778926478,0));
		coordinatesLatLng.add(new Coordinate(78.30505371484378,21.40065516914794,0));
		coordinatesLatLng.add(new Coordinate(77.37121582421878,20.106233605369603,0));
		coordinatesLatLng.add(new Coordinate(72.65808105859378,20.14749530904506,0));
	}
	
	@Test
	public void testSimpleCould(){
		Cloud c = new Cloud(coordinatesRel);
		
		assertEquals(
				Arrays.toString(c.getPolygon().getCoordinates())
				,"[(0.0, 0.0, 0.0), (4.0, 0.0, 0.0), (4.0, 4.0, 0.0), (0.0, 4.0, 0.0), (0.0, 0.0, 0.0)]"
			);
	}
	
	@Test
	public void testWholeLineWithinCloud() {
		Cloud c = new Cloud(coordinatesRel);
		
		double x1 = 1;
		double x2 = 2;
		double y1 = 1;
		double y2 = 2;
		
		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testWholeLineWithinCloudLatLng() {
		Cloud c = new Cloud(coordinatesRel);
		
		double x1 = 74.11376953125;
		double x2 = 75.16845703125;
		double y1 = 20.673905264672843;
		double y2 = 20.838277806058933;
		
		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testWholeLineWithinCloudWithLinePointsOnBorder() {
		Cloud c = new Cloud(coordinatesRel);
		
		double x1 = 0;
		double x2 = 4;
		double y1 = 0;
		double y2 = 4;
		
		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testWholeLineWithinCloudWithLinePointsOnJustOverBorder() {
		Cloud c = new Cloud(coordinatesRel);
		
		double x1 = 0;
		double x2 = 4;
		double y1 = 0;
		double y2 = 5;
		
		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testPartOfLineWithinCloud() {
		Cloud c = new Cloud(coordinatesRel);
		
		double x1 = 1;
		double x2 = 8;
		double y1 = 1;
		double y2 = 8;
		
		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testLineOutsideCloud() {
		Cloud c = new Cloud(coordinatesRel);
		
		double x1 = 5;
		double x2 = 6;
		double y1 = 5;
		double y2 = 6;
		
		assertFalse(c.intersectsLine(x1,x2,y1,y2));
	}
}
