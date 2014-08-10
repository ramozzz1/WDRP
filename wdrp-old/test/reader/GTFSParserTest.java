package reader;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Arc;
import model.Node;
import model.NodeType;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.Fun.Tuple2;

import util.CommonUtils;
import util.IOUtils;

public class GTFSParserTest {
	private static String dir = "resources/gtfs/temp";
	private static String calendar = dir+"/"+"calendar.txt";
	private static String trips = dir+"/"+"trips.txt";
	private static String stops = dir+"/"+"stops.txt";
	private static String stopTimes = dir+"/"+"stop_times.txt";
	private static GTFSParser parser;
	
	@Before
	public void setUp() {
		parser = new GTFSParser(dir);
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
	}
	
	@After
	public void cleanUp() throws IOException {
		parser.getGraph().clear();
		FileUtils.deleteDirectory(new File(dir));
	}
	
	@Test
	public void testSortNodes() {
		parser.getGraph().addNodeToStation(0, 100200050);
		parser.getGraph().addNodeToStation(0, 100250050);
		parser.getGraph().addNodeToStation(0, 300250050);
		parser.getGraph().addNodeToStation(0, 200250050);
		parser.getGraph().addNodeToStation(0, 300240050);
		parser.getGraph().addNodeToStation(0, 200190050);
		
		List<Long> l = parser.sortNodes(CommonUtils.itrToList(parser.getGraph().getNodesOfStation(0L)));
		assertEquals(l.toString(), "[200190050, 100200050, 300240050, 100250050, 200250050, 300250050]");
	}
	
	@Test
	public void testGetAttributesFromId() {
		String stationId = "400089";
		String time = "00:16:05";
		NodeType type = NodeType.ARRIVAL;
		long id = Node.generateId(type,stationId, time, "daksjhd",  true);
		
		assertEquals(time, Node.getTime(id));
		assertEquals(stationId, Node.getStationId(id)+"");
		assertEquals(type, Node.getNodeType(id));
	}
	
	@Test
	public void testNodesPerStation() {
		parser.getGraph().addNodeToStation(0, 5);
		parser.getGraph().addNodeToStation(0, 1);
		parser.getGraph().addNodeToStation(0, 1);
		parser.getGraph().addNodeToStation(0, 4);
		parser.getGraph().addNodeToStation(0, 2);
		
		List<Long> l = new ArrayList<Long>();
		for(Long id : parser.getGraph().getNodesOfStation(0L))
			l.add(id);
		assertEquals(l.toString(), "[1, 2, 4, 5]");
	}
	
	@Test
	public void testCalcTimeDiff() {
		assertEquals(0, CommonUtils.calculateTimeDiff("23:49:15", "23:49:15"));
		assertEquals(31, CommonUtils.calculateTimeDiff("25:45:00", "25:45:31"));
		assertEquals(20*3600, CommonUtils.calculateTimeDiff("05:45:31", "25:45:31"));
	}
	
	@Test
	public void testAddTime() {
		assertEquals("25:50:31", parser.calculateTransitTime("25:45:31", 5));
		assertEquals("26:00:31", parser.calculateTransitTime("25:55:31", 5));
		assertEquals("06:00:31", parser.calculateTransitTime("05:55:31", 5));
		assertEquals("00:10:31", parser.calculateTransitTime("00:05:31", 5));
		assertEquals("10:10:31", parser.calculateTransitTime("10:05:31", 5));
		assertEquals("24:00:31", parser.calculateTransitTime("23:55:31", 5));
	}
	
	@Test
	public void testReadCalendarNoDay() {
		parser.readCalendar();
		assertEquals(parser.getServiceIds().toString(),"{}");
	}
	
	@Test
	public void testReadCalendarNoServiceAvailableOnDay() {
		List<String> daysToConsider = Arrays.asList("friday");
		parser.setDaysToConsider(daysToConsider);
		parser.readCalendar();
		assertEquals(parser.getServiceIds().toString(),"{}");
	}
	
	@Test
	public void testReadCalendarOneService() {
		List<String> daysToConsider = Arrays.asList("wednesday");
		parser.setDaysToConsider(daysToConsider);
		parser.readCalendar();
		assertEquals(parser.getServiceIds().toString(),"{20120701CC}");
	}
	
	@Test
	public void testReadCalendarMultipleServiceOneDay() {
		List<String> daysToConsider = Arrays.asList("saturday");
		parser.setDaysToConsider(daysToConsider);
		parser.readCalendar();
		assertEquals(parser.getServiceIds().toString(),"{20120701AD, 20120701DC}");
	}
	
	@Test
	public void testReadCalendarMultipleDaysMultipleService() {
		List<String> daysToConsider = Arrays.asList("monday","saturday");
		parser.setDaysToConsider(daysToConsider);
		parser.readCalendar();
		assertEquals(parser.getServiceIds().toString(),"{20120701AD, 20120701DC, 20120701CC}");
	}
	
	@Test
	public void testReadCalendarMultipleDaysOneService() {
		List<String> daysToConsider = Arrays.asList("monday","tuesday","wednesday");
		parser.setDaysToConsider(daysToConsider);
		parser.readCalendar();
		assertEquals(parser.getServiceIds().toString(),"{20120701CC}");
	}
	
	@Test
	public void testReadTrips() {
		List<String> daysToConsider = Arrays.asList("wednesday");
		parser.setDaysToConsider(daysToConsider);
		parser.readCalendar();
		parser.readTrips();
		assertEquals(parser.getTripIds().toString(),"{20120701CC_036200_M01_0105_M01_5, 20120701CC_029000_M01_0105_M01_1, 20120701CC_046800_M01_0106_M01_12, 20120701CC_031500_M01_0105_M01_2}");
	}
	
	@Test
	public void testReadTripsEmpty() {
		List<String> daysToConsider = Arrays.asList("friday");
		parser.setDaysToConsider(daysToConsider);
		parser.readCalendar();
		parser.readTrips();
		assertEquals(parser.getTripIds().toString(),"{}");
	}
	
	@Test
	public void testReadStops() {
		parser.readStops();
		assertEquals(parser.getGraph().getNumStations(), 9);
	}
	
	@Test
	public void testReadStopTimes() {
		List<String> daysToConsider = Arrays.asList("wednesday");
		parser.setDaysToConsider(daysToConsider);
		parser.readCalendar();
		parser.readTrips();
		parser.readStops();
		parser.readStopTimes();
		assertEquals(parser.getGraph().getNumNodes(), 21);
		assertEquals(parser.getGraph().getNumEdges(), 20);
		
		for (Tuple2<Long, Arc> arc : parser.getGraph().adjacenyList) {
			System.out.println(arc.a+"->"+arc.b);
		}
	}
	
	@Test
	public void testAddBoardingAndWaitingArcs() {
		List<String> daysToConsider = Arrays.asList("wednesday");
		parser.setDaysToConsider(daysToConsider);
		parser.readCalendar();
		parser.readTrips();
		parser.readStops();
		parser.readStopTimes();
		parser.addBoardingAndWaitingArcs();
		assertEquals(parser.getGraph().getNumEdges(), 20);
	}
	
	@Test
	public void testAddBoardingAndWaitingArcs2() {
		parser.getGraph().addStation(0, 0, 0);
		parser.getGraph().addNodeToStation(0, 30800000);
		parser.getGraph().addNodeToStation(0, 30800010);
		parser.getGraph().addNodeToStation(0, 20802000);
		parser.getGraph().addNodeToStation(0, 20807000);
		parser.getGraph().addNodeToStation(0, 30807000);
		parser.getGraph().addNodeToStation(0, 30812000);
		parser.getGraph().addNodeToStation(0, 20817000);
		parser.getGraph().addNodeToStation(0, 10755000);
		parser.getGraph().addNodeToStation(0, 10755010);
		parser.getGraph().addNodeToStation(0, 20800000);
		parser.getGraph().addNodeToStation(0, 20800010);
		
		parser.addBoardingAndWaitingArcs();
		assertEquals(parser.getGraph().getNumEdges(), 8);
	}
}
