package org.wdrp.core.util;

import java.util.ArrayList;
import java.util.List;

import org.wdrp.core.model.TDGraph;

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
			if(array[j]==-1)
				return Integer.MAX_VALUE;
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

	public static int[] minList(int[] listA, int[] listB) {		
		int maxSize = Math.max(listA.length,listB.length);
		int[] minList = new int[maxSize];
		for (int i = 0; i < maxSize; i++) {
			int valueA = i < listA.length ? listA[i] : Integer.MAX_VALUE;
			int valueB = i < listB.length ? listB[i] : Integer.MAX_VALUE;
			
			valueA = valueA >= 0 ? valueA : Integer.MAX_VALUE;
			valueB = valueB >= 0 ? valueB : Integer.MAX_VALUE;
			
			int minValue = Math.min(valueA, valueB);
			
			if(minValue==Integer.MAX_VALUE) minValue = -1;
			
			minList[i] = minValue;
        }  
		
		return minList;
	}

	public static boolean listLarger(int[] listA,
			int[] listB) {
		assert listA.length == listB.length : "lists should be equal size";
		
		int maxSize = Math.max(listA.length,listB.length);
		for (int i = 0; i < maxSize; i++) {
			int valueA = i < listA.length ? listA[i] : Integer.MAX_VALUE;
			int valueB = i < listB.length ? listB[i] : Integer.MAX_VALUE;
			
			valueA = valueA >= 0 ? valueA : Integer.MAX_VALUE;
			valueB = valueB >= 0 ? valueB : Integer.MAX_VALUE;
			
			if((valueA==valueB) && valueA==Integer.MAX_VALUE) continue;
			if(!(valueA > valueB)) return false;
			//equal = equal && valueB == valueA;
			//if(!(valueA > valueB) && valueA!=Integer.MAX_VALUE && valueB!=Integer.MAX_VALUE) return false;
        }  
		
		return true;
	}
	
	public static boolean listEqual(int[] listA,
			int[] listB) {
		assert listA.length== listB.length : "lists should be equal size";
		
		boolean larger = true;
		int maxSize = Math.max(listA.length,listB.length);
		for (int i = 0; i < maxSize; i++) {
			int valueA = i < listA.length ? listA[i] : Integer.MAX_VALUE;
			int valueB = i < listB.length ? listB[i] : Integer.MAX_VALUE;
			
			valueA = valueA >= 0 ? valueA : Integer.MAX_VALUE;
			valueB = valueB >= 0 ? valueB : Integer.MAX_VALUE;
			
			if(valueB != valueA) return false;
			
			//if(!(valueA > valueB) && valueA!=Integer.MAX_VALUE && valueB!=Integer.MAX_VALUE) return false;
        }  
		
		return larger;
	}
	
	public static boolean listLargerOrEqual(int[] listA,
			int[] listB) {
		assert listA.length == listB.length : "lists should be equal size";
		
		int maxSize = Math.max(listA.length,listB.length);
		for (int i = 0; i < maxSize; i++) {
			int valueA = i < listA.length ? listA[i] : Integer.MAX_VALUE;
			int valueB = i < listB.length ? listB[i] : Integer.MAX_VALUE;
			
			valueA = valueA >= 0 ? valueA : Integer.MAX_VALUE;
			valueB = valueB >= 0 ? valueB : Integer.MAX_VALUE;
			
			if(!(valueA >= valueB)) return false;
        }  
		
		return true;
	}

	public static boolean listSmaller(int[] listA, int[] listB) {
		assert listA.length == listB.length : "lists should be equal size";
		
		int maxSize = Math.max(listA.length,listB.length);
		for (int i = 0; i < maxSize; i++) {
			int valueA = i < listA.length ? listA[i] : Integer.MAX_VALUE;
			int valueB = i < listB.length ? listB[i] : Integer.MAX_VALUE;
			
			valueA = valueA >= 0 ? valueA : Integer.MAX_VALUE;
			valueB = valueB >= 0 ? valueB : Integer.MAX_VALUE;
			
			if(!(valueA < valueB)) return false;
        }  
		
		return true;
	}

	public static int[] linkLists(int[] f, int[] g, int interval) {
		if(isZeroArray(g)) {
			int[] tmp = f;
			f = g;
			g = tmp;
		}
		int maxSize = Math.max(g.length,f.length);	
		int[] linkList = new int[maxSize];
		for (int i = 0; i < maxSize; i++) {
			int travelTimeUW = -1;
			
			int travelTimeUV = f[i];
			if(travelTimeUV >= 0) {
				int arrivalTimeV = i*interval+travelTimeUV;
				
				int index = (int) Math.floor((float) arrivalTimeV/interval);
				
				if(index < g.length) {
					int travelTimeForArrivalAtG = g[index];
					if(travelTimeForArrivalAtG >=0)
						travelTimeUW = travelTimeUV + travelTimeForArrivalAtG;
				}
			}
			
			linkList[i] = travelTimeUW;
        }
		
		return linkList;
	}

	private static boolean isZeroArray(int[] g) {
		return listEqual(new int[g.length], g);
	}

	public static int[] interpolateArray(int[] array, int interval) {
		int newSize = array.length / interval;
		int[] list = new int[newSize];
		for (int i = 0; i < newSize; i++) {
			int index = i * interval;
			list[i] = array[index];
        }  
		
		return list;
	}
	
	public static int[] extrapolateArray(int[] array, int interval) {
		int newSize = array.length * interval;
		int[] list = new int[newSize];
		for (int i = 0; i < newSize; i++) {
			int index = i / interval;
			list[i] = array[index];
        }  
		
		return list;
	}

	public static int[] nullArray(int size) {
		int[] nullArray = new int[size];
		
		for (int i = 0; i < nullArray.length; i++) {
			nullArray[i] = -1;
		}
		
		return nullArray;
	}
}