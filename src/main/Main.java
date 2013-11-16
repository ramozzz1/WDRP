package main;

import reader.OSMParser;

public class Main {

    public static void main(String[] args){
    	OSMParser parser = new OSMParser();
    	parser.osmToGraph("resources/osm/baden-wuerttemberg.osm");
    	/*Graph g = new Graph("resources/db/baden-wuerttemberg.graph");
    	List<AbstractRoutingAlgorithm> algorithms = new ArrayList<AbstractRoutingAlgorithm>();
    	algorithms.add(new DijkstraAlgorithm(g));
    	algorithms.add(new AstarAlgorithm(g));
    	algorithms.add(new ALTAlgorithm(g,42));
    	Experiment.doExperiment(g, algorithms, 100);*/
    	
    }
}