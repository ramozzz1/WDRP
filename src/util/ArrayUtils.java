package util;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {
	public static int getMinValue(List<Integer> list){  
		int currentValue = Integer.MAX_VALUE;
		for (int j=0; j < list.size(); j++) {
			int val = list.get(j);
			if (val < currentValue && val >= 0) {
				currentValue = val;
			}
		}
		return currentValue;
	}
	
	public static int getMinIndex(int[] array){  
		int currentValue = Integer.MAX_VALUE;
		int smallestIndex = -1;
		for (int j=0; j < array.length; j++) {
			if (array[j] < currentValue && array[j] >= 0) {
				currentValue = array[j];
				smallestIndex = j;
			}
		}
		return smallestIndex;  
	}

	public static int getMinValue(int[] array) {
		int currentValue = Integer.MAX_VALUE;
		for (int j=0; j < array.length; j++) {
			if (array[j] < currentValue && array[j] >= 0) {
				currentValue = array[j];
			}
		}
		return currentValue;
	}
	
	public static int getMaxValue(int[] array) {
		int currentValue = Integer.MIN_VALUE;
		for (int j=0; j < array.length; j++) {
			if (array[j] > currentValue)
				currentValue = array[j];
		}
		return currentValue;
	}
	
	public static int[] toIntArray(List<Integer> integerList) {  
		if(integerList == null) return null;
		
        int[] intArray = new int[integerList.size()];  
        for (int i = 0; i < integerList.size(); i++) {  
            intArray[i] = integerList.get(i);  
        }  
        return intArray;  
    } 
	
	public static List<Integer> toList(int[] ints) {  
		if(ints == null) return null;
		
		List<Integer> intList = new ArrayList<Integer>();
	    for (int index = 0; index < ints.length; index++)
	    {
	        intList.add(ints[index]);
	    }
	    
	    return intList;
    }

	public static List<Integer> minList(List<Integer> listA, List<Integer> listB) {		
		int maxSize = Math.max(listA.size(),listB.size());
		List<Integer> minList = new ArrayList<Integer>(maxSize);
		for (int i = 0; i < maxSize; i++) {
			int valueA = i < listA.size() ? listA.get(i) : Integer.MAX_VALUE;
			int valueB = i < listB.size() ? listB.get(i) : Integer.MAX_VALUE;
			
			valueA = valueA >= 0 ? valueA : Integer.MAX_VALUE;
			valueB = valueB >= 0 ? valueB : Integer.MAX_VALUE;
			
			int minValue = Math.min(valueA, valueB);
			
			if(minValue==Integer.MAX_VALUE) minValue = -1;
			
			minList.add(minValue);
        }  
		
		return minList;
	}

	public static boolean listLarger(List<Integer> listA,
			List<Integer> listB) {
		assert listA.size() == listB.size() : "lists should be equal size";
		
		boolean larger = true;
		boolean equal = true;
		int maxSize = Math.max(listA.size(),listB.size());
		for (int i = 0; i < maxSize; i++) {
			int valueA = i < listA.size() ? listA.get(i) : Integer.MAX_VALUE;
			int valueB = i < listB.size() ? listB.get(i) : Integer.MAX_VALUE;
			
			valueA = valueA >= 0 ? valueA : Integer.MAX_VALUE;
			valueB = valueB >= 0 ? valueB : Integer.MAX_VALUE;
			
			if(valueB > valueA) return false;
			equal = equal && valueB == valueA;
			//if(!(valueA > valueB) && valueA!=Integer.MAX_VALUE && valueB!=Integer.MAX_VALUE) return false;
        }  
		
		return larger && !equal;
	}
	
	public static boolean listEqual(List<Integer> listA,
			List<Integer> listB) {
		assert listA.size() == listB.size() : "lists should be equal size";
		
		boolean larger = true;
		int maxSize = Math.max(listA.size(),listB.size());
		for (int i = 0; i < maxSize; i++) {
			int valueA = i < listA.size() ? listA.get(i) : Integer.MAX_VALUE;
			int valueB = i < listB.size() ? listB.get(i) : Integer.MAX_VALUE;
			
			valueA = valueA >= 0 ? valueA : Integer.MAX_VALUE;
			valueB = valueB >= 0 ? valueB : Integer.MAX_VALUE;
			
			if(valueB != valueA) return false;
			
			//if(!(valueA > valueB) && valueA!=Integer.MAX_VALUE && valueB!=Integer.MAX_VALUE) return false;
        }  
		
		return larger;
	}
	
	public static boolean listLargerOrEqual(List<Integer> listA,
			List<Integer> listB) {
		assert listA.size() == listB.size() : "lists should be equal size";
		
		int maxSize = Math.max(listA.size(),listB.size());
		for (int i = 0; i < maxSize; i++) {
			int valueA = i < listA.size() ? listA.get(i) : Integer.MAX_VALUE;
			int valueB = i < listB.size() ? listB.get(i) : Integer.MAX_VALUE;
			
			valueA = valueA >= 0 ? valueA : Integer.MAX_VALUE;
			valueB = valueB >= 0 ? valueB : Integer.MAX_VALUE;
			
			if(!(valueA >= valueB)) return false;
        }  
		
		return true;
	}

	public static boolean listSmaller(List<Integer> listA,
			List<Integer> listB) {
		assert listA.size() == listB.size() : "lists should be equal size";
		
		int maxSize = Math.max(listA.size(),listB.size());
		for (int i = 0; i < maxSize; i++) {
			int valueA = i < listA.size() ? listA.get(i) : Integer.MAX_VALUE;
			int valueB = i < listB.size() ? listB.get(i) : Integer.MAX_VALUE;
			
			valueA = valueA >= 0 ? valueA : Integer.MAX_VALUE;
			valueB = valueB >= 0 ? valueB : Integer.MAX_VALUE;
			
			if(!(valueA < valueB)) return false;
        }  
		
		return true;
	}
	
	public static List<Integer> extrapolateArray(int[] array, int interval) {
		int newSize = array.length * interval;
		List<Integer> list = new ArrayList<Integer>(newSize);
		for (int i = 0; i < newSize; i++) {
			int index = i / interval;
			list.add(array[index]);
        }  
		
		return list;
	}
	
	public static int[] extrapolateArrayToArray(int[] array, int interval) {
		int newSize = array.length * interval;
		int[] newArray = new int[newSize];
		for (int i = 0; i < newSize; i++) {
			int index = i / interval;
			newArray[i] = array[index];
        }  
		
		return newArray;
	}

	public static List<Integer> linkLists(List<Integer> f, List<Integer> g) {
		int maxSize = Math.max(g.size(),f.size());	
		List<Integer> linkList = new ArrayList<Integer>(maxSize);
		for (int i = 0; i < maxSize; i++) {
			int travelTimeUW = -1;
			
			int travelTimeUV = f.get(i);
			if(travelTimeUV >= 0) {
				int arrivalTimeV = i+travelTimeUV;
				
				if(arrivalTimeV < g.size()) {
					int travelTimeForArrivalAtG = g.get(arrivalTimeV);
					if(travelTimeForArrivalAtG >=0)
						travelTimeUW = travelTimeUV + travelTimeForArrivalAtG;
				}
			}
			
			linkList.add(travelTimeUW);
        }
		
		return linkList;
	}

	public static List<Integer> linkLists(int[] a, int[] b) {
		return linkLists(toList(a), toList(b));
	}

	public static List<Integer> extrapolateArray(List<Integer> array,
			int interval) {
		return extrapolateArray(toIntArray(array),interval);
	}

	public static int[] interpolateArray(List<Integer> array, int interval) {
		int newSize = array.size() / interval;
		int[] list = new int[newSize];
		for (int i = 0; i < newSize; i++) {
			int index = i * interval;
			list[i] = array.get(index);
        }  
		
		return list;
	}
}
