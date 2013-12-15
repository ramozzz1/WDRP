package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class CommonUtils {
	
	public static <K> List<K> generateRandomOrder(List<K> arr) {
		Random r = new Random();
		for (int i = arr.size() - 1; i > 0; i--) {
			int j = r.nextInt(i);
			Collections.swap(arr, i, j);
		}
		return arr;
	}
	
	//get n random keys from map m
	public static <K,E> List<K> getRandomKeys(Map<K,E> m, int n) {
		List<Integer> randomIndices = getRandomIndices(n,m.keySet().size());
		List<K> l = new ArrayList<K>();
		int i = 0;
		for (K key : m.keySet()) {
			if(randomIndices.contains(i)) {
				randomIndices.remove(new Integer(i));
				l.add(key);
				if(l.size() == n)
					break;
			}
			i++;
		}
		return l;
	}
	
	//get one random key from map m
	public static <K,E> K getRandomKey(Map<K,E> m) {
		return getRandomKeys(m,1).get(0);
	}
	
	//get number of n random indices from [0,max-1]
	public static List<Integer> getRandomIndices(int n, int max) { 
		Random r = new Random();
		List<Integer> randomIndices = new ArrayList<Integer>();
		while(randomIndices.size() != n) {
			int randomIndex = r.nextInt(max);
			if (!randomIndices.contains(randomIndex))
				randomIndices.add(randomIndex);
		}
		return randomIndices;
	}
	
	public static <K> List<K> convertQueueToArray(Queue<K> q) {
		Queue<K> temp = new PriorityQueue<K>( q );
		List<K> list = new ArrayList<K>();
		while(temp.size()>0) list.add(temp.poll());
		return list;
	}
}
