package org.wdrp.core.util;

import gnu.trove.set.hash.THashSet;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.mapdb.BTreeMap;
import org.mapdb.Fun.Tuple2;
import org.wdrp.core.algorithm.CHAlgorithm;
import org.wdrp.core.algorithm.DijkstraAlgorithm;
import org.wdrp.core.algorithm.td.TDCHAlgorithm;
import org.wdrp.core.model.Arc;
import org.wdrp.core.model.Cloud;
import org.wdrp.core.model.Graph;
import org.wdrp.core.model.LatLonPoint;
import org.wdrp.core.model.NodePair;
import org.wdrp.core.model.TDArc;
import org.wdrp.core.model.TDGraph;
import org.wdrp.core.model.Weather;
import org.wdrp.core.reader.OSMParser;

public class GraphUtils {
	static final Logger logger = Logger.getLogger(GraphUtils.class);
	
	public static Graph<Arc> convertOSMToGraph (String path, boolean addShortcuts) {
		String graphPath = FilenameUtils.getBaseName(path)+".graph";
		
		IOUtils.deleteFile(graphPath);
		
		OSMParser parser = new OSMParser();
		logger.info("Convert osm "+path+" to graph "+graphPath);
		Graph<Arc> g = parser.osmToGraph(path, graphPath);
		logger.info("Done parsing osm");
		
		logger.info("Convert graph to LCC");
		GraphUtils.convertToLCC(g);
		
		if(addShortcuts) {
			logger.info("Add shortcuts to graph");
			addShortcuts(g);
			logger.info("Shortcuts successfully added");
		}
		
		g.closeConnection();
		return g;
	}

	public static Graph<Arc> addShortcuts(Graph<Arc> g) {
		CHAlgorithm ch = new CHAlgorithm(g);
		ch.precompute();
		return ch.graph;
	}
	
	public static TDGraph addTDShortcuts(TDGraph tg) {
		TDCHAlgorithm tdch = new TDCHAlgorithm(tg);
		tdch.precompute();
		return (TDGraph)tdch.graph;
	}
	
	public static TDGraph convertGraphToTDGraphWithWeather(Graph<Arc> g, Weather w, boolean addTDShortcuts) throws ParseException {
		
		TDGraph tdGraph;
		if(g.getName() != null) {
			String tdGraphFileName = g.getName()+".tdgraph";
			IOUtils.deleteFile(tdGraphFileName);
			tdGraph = new TDGraph(tdGraphFileName);
		}
		else tdGraph = new TDGraph(0,0);
		
		int numberOfTimeSteps = w.getNumberOfTimeSteps()+1;
		
		tdGraph.setInterval(w.getTimeStep()*60);
		tdGraph.setMaxTime(numberOfTimeSteps);
		
		int count = 0;
		for (Entry<Long, LatLonPoint> n : g.nodes.entrySet()) {
			count++;
			tdGraph.addNode(n.getKey(),n.getValue().lat,n.getValue().lon);
			if(count%100000==0) logger.info("#nodes processed: "+count);
		}
		
		for (Tuple2<Long, Arc> tp : g.adjacenyList) {
			Long id = tp.a;
			Arc arc = tp.b;
			
			if(!arc.isShortcut()) {
				LatLonPoint pntA = g.getLatLon(id);
				LatLonPoint pntB = g.getLatLon(arc.getHeadNode());
				
				int[] costs = new int[numberOfTimeSteps];
				
				int i = 0;
				while(i < costs.length) {
					costs[i] = arc.getCost();
					for (Cloud c : w.getClouds(w.addTimeStepAndGetTime(i))) {
						if(c.intersectsLine(pntA.lon, pntB.lon, pntA.lat, pntB.lat)) {
							costs[i] = -1;
							break;
						}
					}
					i++;
				}
				
				tdGraph.addEdge(id, new TDArc(arc.getHeadNode(), costs));
			}
		}
		
		if(addTDShortcuts) {
			logger.info("Add td-shortcuts to tdgraph");
			addTDShortcuts(tdGraph);
			logger.info("TDShortcuts successfully added");
		}
		
		return tdGraph;
	}

	//convert graph to largest connected component
	public static void convertToLCC(Graph<Arc> g) {
    	Set<Long> maxVisitedNodes = new THashSet<Long>();
    	int count = 0;
    	int removedNodes = 0;
		for (Long id : g.nodes.keySet()) {
			if(count > 0 && count%100000==0) logger.info("#nodes processed: "+count + " #nodes removed: " +removedNodes);
			if (!maxVisitedNodes.contains(id)) {
				DijkstraAlgorithm<Arc> d = new DijkstraAlgorithm<Arc>(g);
				d.computeShortestPath(id, -1);
				if(d.getVisitedNodes().size() >  maxVisitedNodes.size())
					maxVisitedNodes = d.getVisitedNodes();
				else {
					for (Long nodeToRemove : d.getVisitedNodes()) {
						g.removeNode(nodeToRemove);
						g.removeAllEdgesOfNode(nodeToRemove);
						removedNodes++;
					}
				}
			}
			count++;
		}
		
		logger.info("Graph converted to LCC Graph");
		logger.info("|V|:"+g.nodes.size()+" |E|:"+g.adjacenyList.size()/2);
		logger.info("Total #nodes removed: " + removedNodes);
	}

	public static Long getRandomNode(
			List<Long> nodes) {
		Random r = new Random();
		return nodes.get(r .nextInt(nodes.size()));
	}
		
	//select n node pairs from map
	public static List<NodePair> getRandomNodePairs(
			BTreeMap<Long, LatLonPoint> nodes, int n) {
		List<NodePair> np = new ArrayList<NodePair>();
		List<Long> keys = new ArrayList<Long>(nodes.keySet());
		Random r = new Random();
		for (int i = 0; i < n; i++)
			np.add(new NodePair(keys.get(r .nextInt(keys.size())),keys.get(r.nextInt(keys.size()))));
		/*List<Long> nodeIds = CommonUtils.getRandomKeys(nodes, n*2);
		for (int i = 0; i < nodeIds.size(); i=i+2)
			np.add(new NodePair(nodeIds.get(i),nodeIds.get(i+1)));*/
		return np;
	}

	public static List<NodePair> getRandomNodePairs(
			BTreeMap<Long, LatLonPoint> nodes, List<Long> regionNodes,
			int n) {
		List<NodePair> np = new ArrayList<NodePair>();
		List<Long> keys = new ArrayList<Long>(nodes.keySet());
		Random r = new Random();
		for (int i = 0; i < n; i++)
			np.add(new NodePair(keys.get(r .nextInt(keys.size())),regionNodes.get(r.nextInt(regionNodes.size()))));
		return np;
	}

}
