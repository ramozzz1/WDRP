package algorithms;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import model.Graph;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import util.GraphUtils;
import util.IOUtils;
import algorithm.TimeExpandedDijkstraAlgorithm;

public class TimeExpandedDijkstraAlgorithmTest {
	
	private static String dir = "resources/gtfs/temp";
	private static String calendar = dir+"/"+"calendar.txt";
	private static String trips = dir+"/"+"trips.txt";
	private static String stops = dir+"/"+"stops.txt";
	private static String stopTimes = dir+"/"+"stop_times.txt";
	private static TimeExpandedDijkstraAlgorithm a;
	private static Graph g;
	
	@BeforeClass
	public static void setUp() {
		new File(dir).mkdir();
		
		String calendarContent = "service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date\n"
				+ "20120701CC,1,1,1,1,0,0,0,20120701,20120901\n"
				+ "20120701CA,0,0,0,0,0,0,1,20120701,20120901\n"
				+ "20120701AD,0,0,0,0,0,1,0,20120701,20120901\n"
				+ "20120701DC,0,0,0,0,0,1,1,20120701,20120901\n";
		IOUtils.writeContentToFile(calendar, calendarContent);
		
		String tripsContent = "route_id,service_id,trip_id,trip_headsign,direction_id,block_id,shape_id\n"
				+ "M1,20120701CC,20120701CC_029000_M01_0105_M01_1,E VILLAGE 8 ST via 5 AV,1,,M010105\n"
				+ "M1,20120701CA,20120701CA_029000_M01_0105_M01_1,E VILLAGE 8 ST via 5 AV,1,,M010105\n"
				+ "M1,20120701CC,20120701CC_031500_M01_0105_M01_2,E VILLAGE 8 ST via 5 AV,1,,M010105\n"
				+ "M1,20120701CA,20120701CA_031500_M01_0105_M01_2,E VILLAGE 8 ST via 5 AV,1,,M010105\n"
				+ "M1,20120701CA,20120701CA_035000_M01_0105_M01_4,E VILLAGE 8 ST via 5 AV,1,,M010105\n"
				+ "M1,20120701CC,20120701CC_036200_M01_0105_M01_5,E VILLAGE 8 ST via 5 AV,1,,M010105\n"
				+ "M1,20120701CA,20120701CA_047000_M01_0105_M01_13,E VILLAGE 8 ST via 5 AV,1,,M010105\n"
				+ "M1,20120701CC,20120701CC_046800_M01_0106_M01_12,LIMITED EAST VILLAGE ST via 5 AV,1,,M010106\n"
				+ "M1,20120701CA,20120701CA_046800_M01_0106_M01_12,LIMITED EAST VILLAGE ST via 5 AV,1,,M010106\n";
		IOUtils.writeContentToFile(trips, tripsContent);
		
		String stopsContent = "stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,zone_id,stop_url,location_type,parent_station\n"
				+ "404827,,MALCOLM X BL - W 146 ST,,40.821053,-73.935913,,,0,\n"
				+ "400088,,MALCOLM X BL - W 145 ST,,40.820152,-73.936607,,,0,\n"
				+ "400089,,MALCOLM X BL - W 142 ST,,40.818249,-73.938004,,,0,\n"
				+ "400091,,W 139 ST - MALCOLM X BL,,40.81638,-73.938744,,,0,\n"
				+ "400092,,5 AV - W 138 ST,,40.815201,-73.935982,,,0,\n"
				+ "400093,,5 AV - W 137 ST,,40.813793,-73.937004,,,0,\n"
				+ "400094,,5 AV - W 135 ST,,40.812366,-73.938042,,,0,\n"
				+ "400095,,5 AV - W 132 ST,,40.810619,-73.939285,,,0,\n"
				+ "400096,,5 AV - W 130 ST,,40.809345,-73.940208,,,0,\n";
		IOUtils.writeContentToFile(stops, stopsContent);
		
		String stopTimesContent = "trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_type,shape_dist_traveled\n"
				+ "20120701CC_029000_M01_0105_M01_1,25:55:00,25:55:00,404827,1,,0,0,\n"
				+ "20120701CC_029000_M01_0105_M01_1,25:56:05,25:56:06,400095,2,,0,0,\n"
				+ "20120701CC_029000_M01_0105_M01_1,25:57:13,25:57:13,400089,3,,0,0,\n"
				+ "20120701CC_029000_M01_0105_M01_1,25:59:13,25:59:13,400088,4,,0,0,\n"
				+ "20120701CC_029000_M01_0105_M01_1,26:01:44,26:01:44,400096,5,,0,0,\n"
				+ "20120701CC_029000_M01_0105_M01_1,26:02:13,26:02:13,400092,6,,0,0,\n"
				+ "20120701CC_029000_M01_0105_M01_1,26:02:45,26:02:45,400091,7,,0,0,\n";
		IOUtils.writeContentToFile(stopTimes, stopTimesContent);
		
		g = GraphUtils.convertGTFSToGraph("temp");
		a = new TimeExpandedDijkstraAlgorithm(g);
	}
	
	@AfterClass
	public static void cleanUp() throws IOException {
		g.clear();
		FileUtils.deleteDirectory(new File(dir));
	}
	
	@Test
	public void testShortestPathSourceSource() {
		int dist = a.computeShortestPath(404827, 404827, "25:55:00");
		assertEquals(dist,0);
		
		dist = a.computeShortestPath(404827, 404827, "25:45:00");
		assertEquals(dist,600);
		
		dist = a.computeShortestPath(404827, 404827, "25:55:01");
		assertEquals(dist,-1);
		
		dist = a.computeShortestPath(404827, 404827, "28:55:01");
		assertEquals(dist,-1);
	}
	
	@Test
	public void testShortestPathSourceNeighbor() {
		int dist = a.computeShortestPath(404827, 400095, "25:55:00");
		assertEquals(dist,65);
		
		dist = a.computeShortestPath(404827, 400095, "25:45:00");
		assertEquals(dist,665);
		
		dist = a.computeShortestPath(404827, 400095, "25:55:01");
		assertEquals(dist,-1);
		
		dist = a.computeShortestPath(404827, 400095, "28:55:01");
		assertEquals(dist,-1);
	}
	
	@Test
	public void testShortestPathSourceFarTarget() {
		int dist = a.computeShortestPath(404827, 400091, "25:55:00");
		assertEquals(dist,465);
		
		dist = a.computeShortestPath(404827, 400091, "05:55:00");
		assertEquals(dist,72465);
		
		dist = a.computeShortestPath(404827, 400091, "25:55:01");
		assertEquals(dist,-1);
	}
}
