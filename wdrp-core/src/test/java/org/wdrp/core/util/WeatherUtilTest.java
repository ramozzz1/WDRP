package org.wdrp.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.wdrp.core.model.Cloud;
import org.wdrp.core.model.Weather;

public class WeatherUtilTest {
	
	@Test
	public void testGeneratingWeatherFromKml() throws FileNotFoundException, URISyntaxException {
		URL resource = getClass().getResource("/kml/test.kml");
		assertNotNull("Test file missing",resource);
		
		String fileName = "test.wea";
		IOUtils.deleteFile(fileName);
		
		WeatherUtil.generateWeatherFromKML(resource.getPath(),fileName);
		
		Weather w = new Weather(fileName);
		
		List<Cloud> clouds1700 = w.getCloudsAsList("17:00");
		assertEquals(2,clouds1700.size());
		List<Cloud> clouds1705 = w.getCloudsAsList("17:05");
		assertEquals(2,clouds1705.size());
		assertTrue(w.getCloudsAsList("17:15").isEmpty());
		
		assertEquals(Arrays.toString(clouds1700.get(1).getPolygon().getCoordinates2D())
				, "[(4.492034912109375 , 51.904248279647476), (4.498729705810547 , 51.904036456725066), (4.498300552368164 , 51.90054123428928), (4.4913482666015625 , 51.90075307369361)]");
		assertEquals(Arrays.toString(clouds1700.get(0).getPolygon().getCoordinates2D())
				, "[(4.5023345947265625 , 51.90287141279659), (4.502162933349609 , 51.900964912098985), (4.504995346069336 , 51.90117674950547), (4.503793716430664 , 51.90340098196447)]");
		
		IOUtils.deleteFile(fileName);
	}
}
