package reader;

import gnu.trove.set.hash.THashSet;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.Config;
import model.Arc;
import model.Graph;
import model.Node;
import model.NodeType;

import org.apache.commons.io.FilenameUtils;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import util.CommonUtils;

public class GTFSParser {

	private List<String> daysToConsider;
	private Set<String> serviceIds;
	private Set<String> tripIds;
	private Graph graph;
	private String dirName;	
	public boolean useTripId;
	
	public GTFSParser(String dirName, String day, boolean useTripId) {
		System.out.println(FilenameUtils.getBaseName(dirName+".gtfs"));
		this.graph = new Graph(Config.DBDIR+FilenameUtils.getBaseName(dirName+".gtfs")+"."+Config.EXTENSION);
		System.out.println(this.graph.getNumStations());
		this.daysToConsider = new ArrayList<String>();
		this.serviceIds = new THashSet<String>(); 
		this.tripIds = new THashSet<String>();
		this.dirName = dirName;
		this.useTripId = useTripId;
		if(!day.equals(""))
			this.daysToConsider.add(day);
	}
	
	public GTFSParser(String dirName, String day) {
		this(dirName, day, true);
	}
	
	public GTFSParser(String dirName) {
		this(dirName,"");
	}

	public List<String> getDaysToConsider() {
		return daysToConsider;
	}

	public void setDaysToConsider(List<String> daysToConsider) {
		this.daysToConsider = daysToConsider;
	}

	public Set<String> getServiceIds() {
		return serviceIds;
	}

	public Graph getGraph() {
		return graph;
	}
	
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
	public Set<String> getTripIds() {
		return tripIds;
	}

	public Graph gtfsToGraph() {
		System.out.println("Started parsing gtfs: "+dirName);
		long start = System.currentTimeMillis();
		
		readCalendar();
		readTrips();
		readStops();
		readStopTimes();
		addBoardingAndWaitingArcs();
        
        long end = System.currentTimeMillis() - start;
        System.out.println("Processed "+graph.getNumStations()+" stations.");
        System.out.println("Processed "+graph.getNumNodes()+" nodes and "+ graph.getNumEdges()+" edges.");
        System.out.println("Processing time "+end+"ms");
        System.out.println("Done parsing gtfs");
        
		return graph;
	}
	
	public void readCalendar(){
		String file = dirName+"/"+"calendar.txt";
        try {
        	System.out.println("Getting service ids from calendar for days: "+daysToConsider.toString());
        	
        	ICsvMapReader mapReader = new CsvMapReader(new FileReader(file), CsvPreference.STANDARD_PREFERENCE);
        	final String[] header = mapReader.getHeader(true);
        	Map<String, String> map;
            while( (map = mapReader.read(header)) != null ) {
            	String serviceId = map.get("service_id");
            	for (String day : daysToConsider) {
            		if(Integer.parseInt(map.get(day))==1)
            			serviceIds.add(serviceId);
				}		
            }
        	mapReader.close();
        	
        	System.out.println("Service ids found: "+serviceIds.toString());
        } catch (FileNotFoundException e) {
			System.err.println("File not found: "+file);
		} catch (IOException e) {
			System.err.println("Could not close file: "+file);
		}
	}
	
	public void readTrips(){
		String file = dirName+"/"+"trips.txt";
        try {
        	System.out.println("Getting trip ids from service ids: " + serviceIds.toString());
        	
        	ICsvMapReader mapReader = new CsvMapReader(new FileReader(file), CsvPreference.STANDARD_PREFERENCE);
        	final String[] header = mapReader.getHeader(true);
        	Map<String, String> map;
            while( (map = mapReader.read(header)) != null ) {
            	String tripId = map.get("trip_id");
            	String serviceId = map.get("service_id");
            	if( serviceIds.contains(serviceId))
            		tripIds.add(tripId);
            }
        	mapReader.close();
        	
        	System.out.println("#Trip ids found: "+tripIds.size());
        } catch (FileNotFoundException e) {
			System.err.println("File not found: "+file);
		} catch (IOException e) {
			System.err.println("Could not close file: "+file);
		}
	}
	
	public void readStops(){
		String file = dirName+"/"+"stops.txt";
        try {
        	System.out.println("Getting station ids "+this.graph.getNumStations());
        	
        	ICsvMapReader mapReader = new CsvMapReader(new FileReader(file), CsvPreference.STANDARD_PREFERENCE);
        	final String[] header = mapReader.getHeader(true);
        	Map<String, String> map;
        	int count = 0;
            while( (map = mapReader.read(header)) != null ) {
            	long stopId = Long.parseLong(map.get("stop_id"));
            	double lat = Double.parseDouble(map.get("stop_lat"));
            	double lon = Double.parseDouble(map.get("stop_lon"));
            	
            	//add station
            	this.graph.addStation(stopId, lat, lon);
            	
            	count++;
        		if(count%1000==0) System.out.println("#stops: "+count);
            }
        	mapReader.close();
        	
        	System.out.println("#Stations found: "+ this.graph.getNumStations());
        } catch (FileNotFoundException e) {
			System.err.println("File not found: "+file);
		} catch (IOException e) {
			System.err.println("Could not close file: "+file);
		}
	}

	public void readStopTimes() {
		String file = dirName+"/"+"stop_times.txt";
        try {
        	System.out.println("Getting station ids");
        	
        	ICsvMapReader mapReader = new CsvMapReader(new FileReader(file), CsvPreference.STANDARD_PREFERENCE);
        	final String[] header = mapReader.getHeader(true);
        	Map<String, String> map;
        	String prevTripId = "";
        	String prevDepTime = "";
        	long prevDepNode = -1;
        	int count = 0;
            while( (map = mapReader.read(header)) != null ) {
            	String currTripId = map.get("trip_id");
            	String currDepTime = map.get("departure_time");
            	String currArrTime = map.get("arrival_time");
            	String currStopId = map.get("stop_id");
            	if(tripIds.contains(currTripId)) {
            		count++;
        			if(count%10000==0) System.out.println("#stop times processed: " +count);
        			
            		//make departure node of this node
            		long depNode = Node.generateId(NodeType.DEPARTURE, currStopId, currDepTime, currTripId, useTripId);
            		this.graph.addNode(depNode);
            		this.graph.addNodeToStation(Long.parseLong(currStopId),depNode);
            		
            		//make arrival node of this node
            		long arrNode = Node.generateId(NodeType.ARRIVAL, currStopId, currArrTime, currTripId, useTripId);
            		this.graph.addNode(arrNode);
            		this.graph.addNodeToStation(Long.parseLong(currStopId),arrNode);
            		
            		//make transit node of arrival node
            		String traTime = calculateTransitTime(currArrTime, 5);
            		long traNode = Node.generateId(NodeType.TRANSIT, currStopId, traTime, currTripId, useTripId);
            		this.graph.addNode(traNode);
            		this.graph.addNodeToStation(Long.parseLong(currStopId), traNode);
            		
            		//place edge between arrival node and transit node
            		int traCost = CommonUtils.calculateTimeDiff(currArrTime, traTime);
            		this.graph.addEdge(arrNode, new Arc(traNode, traCost));
            		
            		//place edge between current arrival and departure node
            		int depArrCost = CommonUtils.calculateTimeDiff(currArrTime, currDepTime);
        			this.graph.addEdge(arrNode, new Arc(depNode, depArrCost));
            		
            		//should place edge between previous stop id (dep node) and current stop id (arr node)
            		if(currTripId.equals(prevTripId)) {
            			int cost = CommonUtils.calculateTimeDiff(prevDepTime, currArrTime);
            			this.graph.addEdge(prevDepNode, new Arc(arrNode, cost));
            		}
            		
            		prevDepNode = depNode;
            		prevDepTime = currDepTime;
            		prevTripId = currTripId;
            	} 
            	
            }
        	mapReader.close();
        } catch (FileNotFoundException e) {
			System.err.println("File not found: "+file);
		} catch (IOException e) {
			System.err.println("Could not close file: "+file);
		}
	}
	
	public void addBoardingAndWaitingArcs() {
		System.out.println("Adding boarding and waiting arcs");
		
		int count = 0;
		for(Long stationId : this.graph.stations.keySet()) {
			count++;
			if(count%1000==0) System.out.println("#stations processed: " +count);
			
			List<Long> nodes = sortNodes(CommonUtils.itrToList(this.graph.getNodesOfStation(stationId)));
			long lastTransitNode = -1;
			for(Long node: nodes) {
				NodeType type = Node.getNodeType(node);
				if(lastTransitNode != -1) {
					//if node is transit node or departure -> add edge between last transit node
					if(type == NodeType.TRANSIT || type == NodeType.DEPARTURE) {
						int cost = CommonUtils.calculateTimeDiff(Node.getTime(lastTransitNode), Node.getTime(node));
						this.graph.addEdge(lastTransitNode, new Arc(node, cost));
					}
				}
				
				//update last transit node
				if(type == NodeType.TRANSIT)
					lastTransitNode = node;
			}
		}
		
		System.out.println("Added all boarding and waiting arcs");
	}

	@SuppressWarnings("unchecked")
	public List<Long> sortNodes(List<Long> l) {
		@SuppressWarnings("rawtypes")
		Comparator customComp = new Comparator<Long>() {
			   public int compare(Long a, Long b) {
				   int cost = CommonUtils.calculateTimeDiff(Node.getTime(a), Node.getTime(b));
				   if(cost > 0)
					   return -1;
				   else if(cost < 0)
					   return 1;
				   else if(cost == 0)
					   if(Node.getNodeType(a).Value() < Node.getNodeType(b).Value())
						   return -1;
					   else if(Node.getNodeType(a).Value() > Node.getNodeType(b).Value())
						   return 1;
					   else
						   return 0;
				   return 0;
			   }
			};
		Collections.sort(l,customComp);
		return l;
	}

	public String calculateTransitTime(String time, int minutesToAdd) {
        int hours, minutes, seconds;
        String hoursStr, minutesStr, secondsStr;
        String[] splitTime = time.split(":");

        hours = Integer.parseInt(splitTime[0]);
        minutes = Integer.parseInt(splitTime[1]);
        seconds = Integer.parseInt(splitTime[2]);
        
        minutes += minutesToAdd;
        if(minutes >= 60) {
        	minutes = minutes % 60;
        	hours += 1;
        }
        
        if(hours < 10)
        	hoursStr = "0" + hours;
        else
        	hoursStr = hours+"";
        
        if(minutes < 10)
        	minutesStr = "0" + minutes;
        else
        	minutesStr = minutes+"";
        
        if(seconds < 10)
        	secondsStr = "0" + seconds;
        else
        	secondsStr = seconds+"";
        	
		return hoursStr+":"+minutesStr+":"+secondsStr+"";
	}
}