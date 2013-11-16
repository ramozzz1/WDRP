package reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import main.Config;
import model.Graph;
import model.LatLonPoint;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import util.DistanceUtils;

public class OSMParser {
	
	private static Logger logger = Logger.getLogger(OSMParser.class);
	private HashMap<String,Integer> speedTable;
	
	public OSMParser() {
		speedTable = new HashMap<String,Integer>();
		setDefaultSpeeds();
	}
	
	public Graph osmToGraph(String path) {
		Graph g = null;
		int numNodes=0;
		int numEdges=0;
		long start = System.currentTimeMillis();
        try {
        	XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        	XMLEventReader eventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(path));
        	
        	g = new Graph(Config.DBDIR+FilenameUtils.getBaseName(path)+"."+Config.EXTENSION);
    		OSMNode node = null;
    		OSMWay way = null;
    		
        	while(eventReader.hasNext()) {
	    		XMLEvent event = eventReader.nextEvent();
	            if(event.isStartElement()) {
	            	StartElement startElement = event.asStartElement();
	            	if (startElement.getName().getLocalPart().equals("node")) {
	                	node = new OSMNode();
	                	node.setId(Long.parseLong(startElement.getAttributeByName(new QName("id")).getValue()));
	                	node.setLat(Double.parseDouble(startElement.getAttributeByName(new QName("lat")).getValue()));
	                	node.setLon(Double.parseDouble(startElement.getAttributeByName(new QName("lon")).getValue()));
	                }
	            	else if (startElement.getName().getLocalPart().equals("way")) {
	                	way = new OSMWay();
	                }
	            	else if (way != null && startElement.getName().getLocalPart().equals("nd")) {
	            		way.addNodeRef(Long.parseLong(startElement.getAttributeByName(new QName("ref")).getValue()));
	                }
	            	else if (way != null && startElement.getName().getLocalPart().equals("tag")) {
	            		way.addTag(startElement.getAttributeByName(new QName("k")).getValue(), startElement.getAttributeByName(new QName("v")).getValue());
	                }
	            }
	            
	            if (event.isEndElement()) {
	            	EndElement endElement = event.asEndElement();
	                if (endElement.getName().getLocalPart().equals("node")) {
	                	/*g.addNode(node.getId(),node.getLat(),node.getLon());
	                	node = null;*/
	                	
	                	if(numNodes%1000000==0) {
	                		System.out.println(""+numNodes);
	                	}
	                	numNodes++;
	                }
	                else if (endElement.getName().getLocalPart().equals("way")) {
	                	if(way.isHighway()) { //only highways
	                		if(way.getNumOfNodeRef() > 1) {
	                			String roadType = way.getRoadType();
	                			if(speedTable.containsKey(roadType)) {
	                				double speed = speedTable.get(roadType)*0.277778;
	                				for (int i = 0; i < way.getNumOfNodeRef()-1; i++) {
	                					long sourceId = way.getNodesRef().get(i);
	                					long targetId = way.getNodesRef().get(i+1);
	                					LatLonPoint sourcePoint = g.getNode(sourceId);
		                				//LatLonPoint targetPoint = g.getNode(targetId);
		                				//double distance = DistanceUtils.latlonDistance(sourcePoint.getLat(), sourcePoint.getLon(), targetPoint.getLat(), targetPoint.getLon());
		                				
		                				/*g.addEdge(sourceId,targetId,(int)Math.ceil(distance/speed));
			                			*/
										if(numEdges%100000==0) System.out.println(""+numEdges);
		        	                	numEdges++;
		                			}
	                			}
	                		}
	                	}
	                	way = null;
	                }
	            }
        	}
        }
        catch (FileNotFoundException e) {
        	logger.error("File not "+path+" found",e);
        }
        catch (XMLStreamException e) {
        	logger.error("Error while parsing",e);
        }
        
        long end = System.currentTimeMillis() - start;
        System.out.println("Processed "+numNodes+" nodes and "+ numEdges+" edges.");
        System.out.println("Processing time "+end+"ms");
        
        g.closeConnection();
        
        return g;
	}
	
	private void setDefaultSpeeds() {
        speedTable.put("motorway", 130);
        speedTable.put("motorway_link", 60);
        speedTable.put("trunk", 100);
        speedTable.put("trunk_link", 60);
        speedTable.put("primary", 100);
        speedTable.put("primary_link", 60);
        speedTable.put("secondary", 100);
        speedTable.put("secondary_link", 60);
        speedTable.put("tertiary", 50);
        speedTable.put("unclassified", 50);
        speedTable.put("road", 50);
        speedTable.put("residential", 50);
        speedTable.put("living_street", 6);
        speedTable.put("service", 30);
        speedTable.put("unsurfaced", 30);
    }
}
