package main;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import model.Graph;
import model.NodePair;
import util.GraphUtils;
import algorithm.AbstractRoutingAlgorithm;
import algorithm.ArcFlagsAlgorithm;

public class Experiment {
	private static final String AVG_TRAVEL_TIME = "avg-travel-time(s)";
	private static final String AVG_VISITED_NODES = "avg-visited-nodes";
	private static final String AVG_RUNNING_TIME = "avg-running-time(ms)";
	private static final String PRECOMPUTATION_TIME = "precomputation-time(ms)";
	private static THashMap<String, TreeMap<String, Integer>> results = new THashMap<String, TreeMap<String, Integer>>();
	private static final List<String> METRICS = Arrays.asList(PRECOMPUTATION_TIME,AVG_RUNNING_TIME,AVG_TRAVEL_TIME,AVG_VISITED_NODES);
	
	//run SP on algorithms by selecting random nodes from graph and running them numberOfTimes
	public static void doExperiment(Graph g, List<AbstractRoutingAlgorithm> algorithms, int numberOfTimes) {
		System.out.println("SELECTING "+numberOfTimes+" RANDOM NODE PAIRS");
		
		//select random node pairs from graph
		List<NodePair> randomNodePairs = new ArrayList<NodePair>();
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
		
		for (AbstractRoutingAlgorithm alg : algorithms) {
			TreeMap<String, Integer> metrics = new TreeMap<String, Integer>();
			for (String metric : METRICS)
				metrics.put(metric, 0);
			results.put(alg.getName(), metrics);
		}
		
		System.out.println("----------STARTING PRECOMPUTATIONS-----------");
		
		//do precomputation
		for (AbstractRoutingAlgorithm alg : algorithms) {
			String name = alg.getName();
			TreeMap<String, Integer> metrics = results.get(name); 
			System.out.println("Precomputing for "+name);
			long start = System.nanoTime();
			alg.precompute();
			long elapsed = (System.nanoTime() - start)/1000000;
			metrics.put(PRECOMPUTATION_TIME, (int) elapsed);
			results.put(name, metrics);
		}
		
		System.out.println("----------DONE WITH PRECOMPUTATIONS-----------");
		
		System.out.println("----------STARTING RUNNING ALGORITHMS " + numberOfTimes +"X");
		int i = 0;
		//for each node pair run the algorithms and save metrics
		for (NodePair nodePair : randomNodePairs) {
			System.out.println(i+ " " +nodePair);
			for (AbstractRoutingAlgorithm alg : algorithms) {
				String name = alg.getName();
				TreeMap<String,Integer> metrics = results.get(name);
				System.out.println("Running SP for " + name);
				long start = System.nanoTime();
				int travelTime = alg.computeShortestPath(nodePair.getSource(), nodePair.getTarget());
				long elapsed = (System.nanoTime() - start)/1000000;
				metrics.put(AVG_RUNNING_TIME, (int) (metrics.get(AVG_RUNNING_TIME)+elapsed));
				metrics.put(AVG_TRAVEL_TIME, metrics.get(AVG_TRAVEL_TIME)+travelTime);
				metrics.put(AVG_VISITED_NODES, metrics.get(AVG_VISITED_NODES)+alg.visitedNodesMarks.size());
				results.put(name, metrics);
			}
			i++;
		}
		System.out.println("----------DONE WITH COMPUTATIONS-----------");
		System.out.println();
		
		System.out.println("----------RESULTS "+ numberOfTimes + "X-----------");
		//print the header for the result table
		Collections.sort(METRICS);
		System.out.printf("%-20s","Algorithm");
		for (String metric : METRICS)
			System.out.printf(" %-20s",metric);
		System.out.println();
			
		//print the metric results
		for (Entry<String, TreeMap<String, Integer>> entry : results.entrySet()) {
			System.out.printf("%-20s",entry.getKey());
			for (Entry<String, Integer> metric : entry.getValue().entrySet()) {
				int value = metric.getValue();
				if(!metric.getKey().equals(PRECOMPUTATION_TIME))
					value = value/numberOfTimes;
				System.out.printf(" %-20s",value);
			}
			
			System.out.println();
		}
	}

	private static ArcFlagsAlgorithm getArcFlagAlgorithm(
			List<AbstractRoutingAlgorithm> algorithms) {
		for (AbstractRoutingAlgorithm abstractRoutingAlgorithm : algorithms) {
			if(abstractRoutingAlgorithm instanceof ArcFlagsAlgorithm)
				return (ArcFlagsAlgorithm)abstractRoutingAlgorithm;
		}
		return null;
	}
}
