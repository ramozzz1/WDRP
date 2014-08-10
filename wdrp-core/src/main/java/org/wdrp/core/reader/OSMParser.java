package org.wdrp.core.reader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.wdrp.core.model.Arc;
import org.wdrp.core.model.Graph;
import org.wdrp.core.model.LatLonPoint;
import org.wdrp.core.util.DistanceUtils;

public class OSMParser {
	
	static final Logger logger = Logger.getLogger(OSMParser.class);
	
	private HashMap<String,Integer> speedTable;
	private int maxNumNodes;
	
	public OSMParser() {
		this(Integer.MAX_VALUE);
	}
	
	public OSMParser(int maxNumNodes) {
		this.speedTable = new HashMap<String,Integer>();
		this.maxNumNodes = maxNumNodes;
		setDefaultSpeeds();
	}
	
	public Graph<Arc> osmToGraph(String path, String graphPath) {
		Graph<Arc> g = null;
		int numNodes=0;
		int numEdges=0;
		long start = System.currentTimeMillis();
        try {
        	XMLInputFactory factory = XMLInputFactory.newInstance();
        	XMLStreamReader streamReader = factory.createXMLStreamReader(
        			new FileReader(path));
        	
        	g = new Graph<Arc>(graphPath);
    		OSMNode node = null;
    		OSMWay way = null;
    		
        	while(streamReader.hasNext()) {
	    		streamReader.next();
	    		if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
	    			String ln = streamReader.getLocalName();
	            	if (ln.equals("node")) {
	            		long id = Long.valueOf(streamReader.getAttributeValue(null, "id"));
			    		double lat = Double.valueOf(streamReader.getAttributeValue(null, "lat"));
			    		double lon = Double.valueOf(streamReader.getAttributeValue(null, "lon"));
			    		node = new OSMNode(id,lat,lon);
	                }
	            	else if(ln.equals("way")) {
			    		way = new OSMWay();
			    	}
			    	else if(way != null  && ln.equals("nd")) {
			    		way.addNodeRef(Long.valueOf(streamReader.getAttributeValue(null, "ref")));
			    	}
	            	else if (way != null && ln.equals("tag")) {
	            		way.addTag(streamReader.getAttributeValue(null, "k"), streamReader.getAttributeValue(null, "v"));
	                }
	            }
	    		else if(streamReader.getEventType() == XMLStreamReader.END_ELEMENT) {
	    			String ln = streamReader.getLocalName();
	                if (ln.equals("node")) {
	                	if(numNodes <= maxNumNodes) {
		                	g.addNode(node.id,node.lat,node.lon);
		                	
		                	if(numNodes > 0 && numNodes%1000000==0) logger.info("#nodes processed: "+numNodes);
		                	numNodes++;
	                	}
	                }
	                else if (ln.equals("way")) {
	                	if(way.isHighway()) { //only highways
	                		//if(way.getNumOfNodeRef() > 1) {
	                			String roadType = way.getRoadType();
	                			if(speedTable.containsKey(roadType)) {
	                				double speed = speedTable.get(roadType)*0.277778;
	                				for (int i = 0; i < way.getNumOfNodeRef()-1; i++) {
	                					long sourceId = way.getNodesRef().get(i);
	                					long targetId = way.getNodesRef().get(i+1);
	                					LatLonPoint sourcePoint = g.getLatLon(sourceId);
		                				LatLonPoint targetPoint = g.getLatLon(targetId);
		                				if(sourcePoint != null && targetPoint != null) {
			                				double distance = DistanceUtils.latlonDistance(sourcePoint.lat, sourcePoint.lon, targetPoint.lat, targetPoint.lon);
			                				
			                				g.addEdge(sourceId,targetId,(int)Math.ceil(distance/speed));
				                			
											if(numEdges > 0 && numEdges%100000==0) logger.info("#edges processed: "+numEdges);
			        	                	numEdges++;
		                				}
		                			}
	                			}
	                		//}
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
        logger.info("Processed "+numNodes+" nodes and "+ numEdges+" edges.");
        logger.info("Processing time "+end+"ms");
        
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
