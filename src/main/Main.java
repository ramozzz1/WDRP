package main;

import reader.OSMParser;

public class Main {

    public static void main(String[] args){
    	/*System.out.println("TESTING PUT SPEED");
		int numberOfPuts = 4000000;
		long elapsedTime = 0;
		long tet = 0;
		double avg_put_time = 0;
		double avg_get_time = 0;
		Random r = new Random();
		Long[] longArray = new Long[numberOfPuts];
		Integer[] intArray = new Integer[numberOfPuts];
		
		System.out.println("GENERATING RANDOM INPUT ARRAYS");
		for (int i = 0; i < numberOfPuts; i++) {
			longArray[i] = (long)r.nextInt();
			intArray[i] = r.nextInt(numberOfPuts*5);
		}
		
		
		//DBHashMap<Long, Integer> m = new DBHashMap<Long,Integer>();
		//HashMap<Long, Integer> m = new HashMap<Long,Integer>();
		DB db = DBMaker
				.newTempFileDB()
				.transactionDisable()
				.asyncFlushDelay(100)
				.cacheHardRefEnable()
				.closeOnJvmShutdown()
                .make();
		
		Map<Long, Integer> m = db.createTreeMap("m").keepCounter(true).makeOrGet();
		System.out.println("DBHASHMAP TEST");
		long start = System.currentTimeMillis();
		for (int i = 0; i < numberOfPuts; i++) {
			m.put(longArray[i], intArray[i]);
			if(i%1000000==0) System.out.println(i);
		}
		long el = System.currentTimeMillis() - start;
		System.out.println("TIME: " + el +"ms");
    	
		start = System.currentTimeMillis();
		for (int i = 0; i < numberOfPuts; i++) {
			m.get(longArray[i]);
			if(i%1000000==0) System.out.println(i);
		}
		el = System.currentTimeMillis() - start;
		System.out.println("TIME: " + el +"ms");*/
		
    	OSMParser parser = new OSMParser();
    	parser.osmToGraph("resources/osm/baden-wuerttemberg.osm");
    	/*Graph g = new Graph("resources/osm/baden-wuerttemberg.osm");
    	GraphUtils.convertToLCC(g);*/
    	
    	/*Graph g = new Graph("resources/db/baden-wuerttemberg.graph");
    	Long[] longArray = new Long[g.getNumNodes()];
    	int i = 0;
    	for (Long id : g.nodes.keySet()) {
    		longArray[i] = id;
    		if(i%1000000==0) System.out.println(i);
    		i++;
		}
    	
    	long start = System.currentTimeMillis();
    	for (i = 0; i < g.getNumNodes(); i++) {
    		g.getNode(longArray[i]);
    		if(i%1000000==0) System.out.println(i);
		}
    	long el = System.currentTimeMillis() - start;
		System.out.println("TIME: " + el +"ms");*/
    	/*Graph g = new Graph("resources/db/baden-wuerttemberg.graph");
    	List<AbstractRoutingAlgorithm> algorithms = new ArrayList<AbstractRoutingAlgorithm>();
    	algorithms.add(new DijkstraAlgorithm(g));
    	algorithms.add(new AstarAlgorithm(g));
    	algorithms.add(new ALTAlgorithm(g,42));
    	Experiment.doExperiment(g, algorithms, 100);*/
    	
    }
}