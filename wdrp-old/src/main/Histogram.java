package main;

public class Histogram {
	private final int[] posFreq;    
	private final int[] negFreq;
	private int N;
	
    // Create a new histogram. 
    public Histogram(int N) {   
        posFreq = new int[N+1];
        negFreq = new int[N+1];
        this.N = N+1;
    }

    // Add one occurrence of the value i. 
    public void addDataPoint(int i) {
    	int index = Math.abs(i);
    	if(index >= N)
    		index = N-1;
    	
    	if(i>=0)
    		posFreq[index]++;
    	else
    		negFreq[index]++;
    }
    
    public void printHistogram() {
    	for (int i = negFreq.length-1; i > 0; i--)
    		System.out.print(-i+"\t");
    	for (int i = 0; i < posFreq.length; i++)
    		System.out.print(i+"\t");
    	
    	System.out.println();
    	
    	for (int i = negFreq.length-1; i > 0; i--)
			System.out.print(negFreq[i]+"\t");
    	for (int i = 0; i < posFreq.length; i++)
			System.out.print(posFreq[i]+"\t");
    	
    	System.out.println();
    }
}
