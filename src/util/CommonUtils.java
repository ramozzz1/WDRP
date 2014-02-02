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

	public static List<Long> itrToList(Iterable<Long> itr) {
		List<Long> l = new ArrayList<>();
		for (Long i : itr)
		    l.add(i);
		return l;
	}

	public static int convertTimeToSeconds(String a_time)
    {
        int secSinceMidnight = 0;
        int hours, minutes, seconds;
        String[] splitTime = a_time.split(":");

        hours = Integer.parseInt(splitTime[0]);
        minutes = Integer.parseInt(splitTime[1]);
        seconds = Integer.parseInt(splitTime[2]);

        secSinceMidnight = (3600 * hours) + (60 * minutes) + seconds;

        return secSinceMidnight;
    }

	public static int calculateTimeDiff(String time1, String time2) {
		int timeDiff = convertTimeToSeconds(time2) - convertTimeToSeconds(time1);
		return timeDiff;
	}

	public static int generateRandomTime(String minTime, String maxTime) {
		int t2 = convertTimeToSeconds(maxTime);
		int t1 = convertTimeToSeconds(minTime);
		Random r = new Random();
		return r.nextInt((t2-t1)+1) + t1;
	}

	public static String convertSecondsToTime(int totalSecs) {
		int hours = totalSecs / 3600;
		int minutes = (totalSecs % 3600) / 60;
		int seconds = totalSecs % 60;

		return (hours<10?"0":"") + hours + ":" + (minutes<10?"0":"") + minutes + ":" +(seconds<10?"0":"")+ seconds;
	}
}
