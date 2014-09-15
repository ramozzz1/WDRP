package org.wdrp.core.model;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Arrays;

import org.junit.Test;
import org.wdrp.core.util.IOUtils;

public class WeatherTest {
	
	@Test
	public void testEmptyWeather() {
		Weather w = new Weather();
		assertTrue(w.getCloudsAsList("17:00").isEmpty());
		assertTrue(w.getCloudsAsList("17:00").isEmpty());
		assertNull(w.getBeginTime());
		assertNull(w.getEndTime());
		assertEquals(0, w.getTimeStep());
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
		IOUtils.deleteFile(fileName);
		
		Weather w = new Weather(fileName);
		w.setBeginTime("17:00");
		w.setEndTime("17:05");
		w.setTimeStep(5);
		
		double[][] points = {{0,0},{0,2}, {2,2}};
		w.addCloud("17:00", new Cloud(points));
		double[][] points2 = {{0,0},{0,2}, {-2,2}};
		w.addCloud("17:05", new Cloud(points2));
		
		assertEquals("17:00", w.getBeginTime());
		assertEquals("17:05", w.getEndTime());
		assertEquals(5, w.getTimeStep());
		assertEquals(1, w.getCloudsAsList("17:00").size());
		assertEquals(1, w.getCloudsAsList("17:05").size());
		
		w.close();
		
		Weather w1 = new Weather(fileName);
		
		assertEquals("17:00", w1.getBeginTime());
		assertEquals("17:05", w1.getEndTime());
		assertEquals(5, w1.getTimeStep());
		assertEquals(1, w1.getCloudsAsList("17:00").size());
		assertEquals(1, w1.getCloudsAsList("17:05").size());
		
		assertEquals("[(0.0 , 0.0), (0.0 , 2.0), (2.0 , 2.0)]", Arrays.toString(w1.getCloudsAsList("17:00").get(0).getPolygon().getCoordinates2D()));
		assertEquals("[(0.0 , 0.0), (0.0 , 2.0), (-2.0 , 2.0)]", Arrays.toString(w1.getCloudsAsList("17:05").get(0).getPolygon().getCoordinates2D()));
		
		IOUtils.deleteFile(fileName);
	}
	
	@Test
	public void testNumberOfTimeSteps() throws ParseException {
		Weather w = new Weather();
		w.setBeginTime("17:00");
		w.setEndTime("19:00");
		w.setTimeStep(10);
		
		assertEquals(12, w.getNumberOfTimeSteps());
		
		w.setTimeStep(1);
		
		assertEquals(120, w.getNumberOfTimeSteps());
		
		w.setTimeStep(5);
		
		assertEquals(24, w.getNumberOfTimeSteps());
	}
	
	@Test
	public void testAddTimeStepAndGetTime() throws ParseException {
		Weather w = new Weather();
		w.setBeginTime("17:00");
		w.setEndTime("19:00");
		w.setTimeStep(5);
		
		assertEquals("17:00", w.addTimeStepAndGetTime(0));
		assertEquals("18:00", w.addTimeStepAndGetTime(12));
		assertEquals("18:30", w.addTimeStepAndGetTime(18));
		assertEquals("19:00", w.addTimeStepAndGetTime(24));
	}
}
