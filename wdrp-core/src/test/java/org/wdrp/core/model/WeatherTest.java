package org.wdrp.core.model;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import org.wdrp.core.util.IOUtils;

public class WeatherTest {
	
	@Test
	public void testEmptyWeather() {
		Weather w = new Weather();
		assertTrue(w.getCloudsAsList("17:00").isEmpty());
		assertTrue(w.getCloudsAsList("17:00").isEmpty());
	}
	
	@Test
	public void testAddCloud() {
		Weather w = new Weather();
		
		double[][] points = {{0,0},{0,2}, {2,2}};
		w.addCloud("17:00", new Cloud(points));
		
		double[][] points2 = {{0,0},{0,2}, {-2,2}};
		w.addCloud("17:00", new Cloud(points2));
		
		assertEquals(2, w.getCloudsAsList("17:00").size());
		assertEquals("[(0.0 , 0.0), (0.0 , 2.0), (-2.0 , 2.0)]", Arrays.toString(w.getCloudsAsList("17:00").get(0).getPolygon().getCoordinates2D()));
		assertEquals("[(0.0 , 0.0), (0.0 , 2.0), (2.0 , 2.0)]", Arrays.toString(w.getCloudsAsList("17:00").get(1).getPolygon().getCoordinates2D()));
	}
	
	@Test
	public void testPersistentWeather() {
		String fileName = "test.wea";
		Weather w = new Weather(fileName);
		
		double[][] points = {{0,0},{0,2}, {2,2}};
		w.addCloud("17:00", new Cloud(points));
		double[][] points2 = {{0,0},{0,2}, {-2,2}};
		w.addCloud("17:05", new Cloud(points2));
		
		assertEquals(1, w.getCloudsAsList("17:00").size());
		assertEquals(1, w.getCloudsAsList("17:05").size());
		
		Weather w1 = new Weather(fileName);
		assertEquals(1, w1.getCloudsAsList("17:00").size());
		assertEquals(1, w1.getCloudsAsList("17:05").size());
		
		assertEquals("[(0.0 , 0.0), (0.0 , 2.0), (2.0 , 2.0)]", Arrays.toString(w.getCloudsAsList("17:00").get(0).getPolygon().getCoordinates2D()));
		assertEquals("[(0.0 , 0.0), (0.0 , 2.0), (-2.0 , 2.0)]", Arrays.toString(w.getCloudsAsList("17:05").get(0).getPolygon().getCoordinates2D()));
		
		IOUtils.deleteFile(fileName);
	}
}
