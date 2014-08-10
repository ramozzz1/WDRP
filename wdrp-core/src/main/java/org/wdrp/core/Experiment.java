package org.wdrp.core;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.wdrp.core.algorithm.AbstractRoutingAlgorithm;
import org.wdrp.core.algorithm.ArcFlagsAlgorithm;
import org.wdrp.core.algorithm.TimeExpandedAlgorithm;
import org.wdrp.core.model.Arc;
import org.wdrp.core.model.Graph;
import org.wdrp.core.model.NodePair;
import org.wdrp.core.model.TNGraph;
import org.wdrp.core.util.CommonUtils;
import org.wdrp.core.util.GraphUtils;
import org.wdrp.core.util.IOUtils;

public class Experiment {
	private static final String AVG_TRAVEL_TIME = "avg-travel-time(s)";
	private static final String AVG_VISITED_NODES = "avg-visited-nodes";
	private static final String AVG_RUNNING_TIME = "avg-running-time(ms)";
	private static final String PRECOMPUTATION_TIME = "precomputation-time(ms)";
	private static THashMap<String, TreeMap<String, Integer>> results = new THashMap<String, TreeMap<String, Integer>>();
	private static final List<String> METRICS = Arrays.asList(PRECOMPUTATION_TIME,AVG_RUNNING_TIME,AVG_TRAVEL_TIME,AVG_VISITED_NODES);
	
	//run SP on algorithms by selecting random nodes from graph and running them numberOfTimes
	public static void doExperiment(Graph<Arc> g, List<AbstractRoutingAlgorithm<Arc>> algorithms, 
			int numberOfTimes, boolean writePathsToFile, boolean timeDependent, String minTime, String maxTime) {
		System.out.println("SELECTING "+numberOfTimes+" RANDOM NODE PAIRS");
		List<NodePair> randomNodePairs = new ArrayList<NodePair>();
		int randomDepartureTime = 0;
		int numberOfValidPathsFound = 0;
		
		if(!timeDependent) {
			//select random node pairs from graph
			ArcFlagsAlgorithm a = getArcFlagAlgorithm(algorithms);
			if(a!=null) {
				//hack: if we have an arc flag algorithm we have to only get targets from nodes region
				THashSet<Long> regionNodes =  a.computeNodesInRegion(a.latMin, a.latMax, a.lonMin, a.lonMax);
				System.out.println(regionNodes.size());
				randomNodePairs = GraphUtils.getRandomNodePairs(g.nodes, new ArrayList<Long>(regionNodes) ,numberOfTimes);
			}
			else {
				randomNodePairs = GraphUtils.getRandomNodePairs(g.nodes, numberOfTimes);
			}
		}
		else {
			randomNodePairs = GraphUtils.getRandomNodePairs(((TNGraph)g).getStations(), numberOfTimes);
		}
		
		for (AbstractRoutingAlgorithm<Arc> alg : algorithms) {
			TreeMap<String, Integer> metrics = new TreeMap<String, Integer>();
			for (String metric : METRICS)
				metrics.put(metric, 0);
			results.put(alg.getName(), metrics);
		}
		
		System.out.println("----------STARTING PRECOMPUTATIONS-----------");
		
		//do precomputation
		for (AbstractRoutingAlgorithm<Arc> alg : algorithms) {
			String name = alg.getName();
			TreeMap<String, Integer> metrics = results.get(name); 
			System.out.println("Precomputing for "+name);
			
			long start = System.nanoTime();
			alg.precompute();
			long elapsed = (System.nanoTime() - start)/1000000;
			
			System.out.println("Completed precomputing for "+name);
			metrics.put(PRECOMPUTATION_TIME, (int) elapsed);
			results.put(name, metrics);
		}
		
		System.out.println("----------DONE WITH PRECOMPUTATIONS-----------");
		
		System.out.println("----------STARTING RUNNING ALGORITHMS " + numberOfTimes +"X");
		int i = 0;
		//for each node pair run the algorithms and save metrics
		for (NodePair nodePair : randomNodePairs) {
			if(timeDependent) {
				randomDepartureTime = CommonUtils.generateRandomTime(minTime, maxTime);
				System.out.println(i+ " " +nodePair + "@"+CommonUtils.convertSecondsToTime(randomDepartureTime));
			}
			else
				System.out.println(i+ " " +nodePair);
			
			int travelTime = -1;
			for (AbstractRoutingAlgorithm<Arc> alg : algorithms) {
				String name = alg.getName();
				TreeMap<String,Integer> metrics = results.get(name);
				System.out.println("Running SP for " + name);
				long start = System.nanoTime();
				if(timeDependent && (alg instanceof TimeExpandedAlgorithm))
					travelTime = ((TimeExpandedAlgorithm)alg).computeShortestPath(nodePair.getSource(), nodePair.getTarget(), randomDepartureTime);
				else
					travelTime = alg.computeShortestPath(nodePair.getSource(), nodePair.getTarget());
				long elapsed = (System.nanoTime() - start)/1000000;
				
				if(writePathsToFile)
					IOUtils.writePathToFile(alg.getName(), alg.extractPath(nodePair.getTarget()),nodePair.getSource(), nodePair.getTarget());
				
				metrics.put(AVG_RUNNING_TIME, (int) (metrics.get(AVG_RUNNING_TIME)+elapsed));
				metrics.put(AVG_TRAVEL_TIME, metrics.get(AVG_TRAVEL_TIME)+(travelTime == -1?0:travelTime));
				metrics.put(AVG_VISITED_NODES, metrics.get(AVG_VISITED_NODES)+alg.getVisitedNodes().size());
				results.put(name, metrics);
			}
			
			if(travelTime != -1) numberOfValidPathsFound++;
			
			i++;
		}
		System.out.println("----------DONE WITH COMPUTATIONS-----------");
		System.out.println();
	
		System.out.println("#nodes:"+g.nodes.size()+" #edges(including shortcuts):"+g.adjacenyList.size());
		System.out.println("----------RESULTS "+ numberOfTimes + "X-----------");
		System.out.println("Valid paths found:"+numberOfValidPathsFound+"/"+numberOfTimes);
		//print the header for the result table
		Collections.sort(METRICS);
		System.out.printf("%-25s","Algorithm");
		for (String metric : METRICS)
			System.out.printf(" %-20s",metric);
		System.out.println();
			
		//print the metric results
		for (Entry<String, TreeMap<String, Integer>> entry : results.entrySet()) {
			System.out.printf("%-25s",entry.getKey());
			for (Entry<String, Integer> metric : entry.getValue().entrySet()) {
				int value = metric.getValue();
				if(!metric.getKey().equals(PRECOMPUTATION_TIME)) {
					if(!metric.getKey().equals(AVG_RUNNING_TIME))
						value = value/numberOfValidPathsFound;
					else
						value = value/numberOfTimes;						
				}
				System.out.printf(" %-20s",value);
			}
			
			System.out.println();
		}
	}

	private static ArcFlagsAlgorithm getArcFlagAlgorithm(
			List<AbstractRoutingAlgorithm<Arc>> algorithms) {
		for (AbstractRoutingAlgorithm<Arc> abstractRoutingAlgorithm : algorithms) {
			if(abstractRoutingAlgorithm instanceof ArcFlagsAlgorithm)
				return (ArcFlagsAlgorithm)abstractRoutingAlgorithm;
		}
		return null;
	}
}
