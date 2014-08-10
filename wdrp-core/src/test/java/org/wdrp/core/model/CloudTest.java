package org.wdrp.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

public class CloudTest {
	
	private List<Coordinate> coordinates = Arrays.asList(
			new Coordinate(0, 0, 0)
			,new Coordinate(4, 0, 0)
			,new Coordinate(4, 4, 0)
			,new Coordinate(0, 4, 0)
			,new Coordinate(0, 0, 0)
		);
	
	@Test
	public void testSimpleCould(){
		Cloud c = new Cloud(coordinates);
		
		assertEquals(
				Arrays.toString(c.getPolygon().getCoordinates())
				,"[(0.0, 0.0, 0.0), (4.0, 0.0, 0.0), (4.0, 4.0, 0.0), (0.0, 4.0, 0.0), (0.0, 0.0, 0.0)]"
			);
	}
	
	@Test
	public void testWholeLineWithinCloud() {
		Cloud c = new Cloud(coordinates);
		
		double x1 = 1;
		double x2 = 2;
		double y1 = 1;
		double y2 = 2;
		
		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testWholeLineWithinCloudWithLinePointsOnBorder() {
		Cloud c = new Cloud(coordinates);
		
		double x1 = 0;
		double x2 = 4;
		double y1 = 0;
		double y2 = 4;
		
		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testWholeLineWithinCloudWithLinePointsOnJustOverBorder() {
		Cloud c = new Cloud(coordinates);
		
		double x1 = 0;
		double x2 = 4;
		double y1 = 0;
		double y2 = 5;
		
		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testPartOfLineWithinCloud() {
		Cloud c = new Cloud(coordinates);
		
		double x1 = 1;
		double x2 = 8;
		double y1 = 1;
		double y2 = 8;
		
		assertTrue(c.intersectsLine(x1,x2,y1,y2));
	}
	
	@Test
	public void testLineOutsideCloud() {
		Cloud c = new Cloud(coordinates);
		
		double x1 = 5;
		double x2 = 6;
		double y1 = 5;
		double y2 = 6;
		
		assertFalse(c.intersectsLine(x1,x2,y1,y2));
	}
}
