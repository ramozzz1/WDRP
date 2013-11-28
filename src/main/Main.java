package main;

import java.util.ArrayList;
import java.util.List;

import model.Graph;
import util.GraphUtils;
import algorithm.ALTAlgorithm;
import algorithm.AbstractRoutingAlgorithm;
import algorithm.ArcFlagsAlgorithm;
import algorithm.AstarAlgorithm;
import algorithm.DijkstraAlgorithm;


public class Main {
	String regionBoxSaarland = "49.20,49.25,6.95,7.05";
	String regionBoxBW = "47.95,48.05,7.75,7.90";
	
    public static void main(String[] args){
    	GraphUtils.convertOSMToGraph("baden-wuerttemberg");
    	
    	Graph g = new Graph("resources/db/baden-wuerttemberg.graph");	
    	List<AbstractRoutingAlgorithm> algorithms = new ArrayList<AbstractRoutingAlgorithm>();
    	algorithms.add(new DijkstraAlgorithm(g));
    	algorithms.add(new AstarAlgorithm(g));
    	algorithms.add(new ALTAlgorithm(g,42));    	
    	algorithms.add(new ArcFlagsAlgorithm(g,47.95,48.05,7.75,7.90));
    	Experiment.doExperiment(g, algorithms, 100);
    }
    
    
}