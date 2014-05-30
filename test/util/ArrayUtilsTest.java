package util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ArrayUtilsTest {

	@Test
	public void testConvertIntegerListToIntArray() {
		List<Integer> intList = new ArrayList<Integer>();
		intList.add(4);
		intList.add(9);
		intList.add(2);
		intList.add(7);
		intList.add(0);
		
		int[] intArray = ArrayUtils.toIntArray(intList);
		
		assertEquals("[4, 9, 2, 7, 0]",Arrays.toString(intArray));
	}
	
	@Test
	public void testMinimumList() {
		List<Integer> listA = new ArrayList<Integer>();
		listA.add(4);
		listA.add(9);
		listA.add(2);
		listA.add(7);
		listA.add(0);
		
		List<Integer> listB = new ArrayList<Integer>();
		listB.add(2);
		listB.add(8);
		listB.add(4);
		listB.add(5);
		listB.add(6);
		
		List<Integer> minList = ArrayUtils.minList(listA, listB);
		
		assertEquals("[2, 8, 2, 5, 0]",Arrays.toString(minList.toArray()));
	}
	
	@Test
	public void testMinimumList1() {
		List<Integer> listA = new ArrayList<Integer>();
		listA.add(4);
		listA.add(9);
		listA.add(2);
		listA.add(7);
		listA.add(0);
		
		List<Integer> listB = new ArrayList<Integer>();
		listB.add(8);
		listB.add(4);
		listB.add(5);
		
		List<Integer> minList = ArrayUtils.minList(listA, listB);
		
		assertEquals("[4, 4, 2, 7, 0]",Arrays.toString(minList.toArray()));
	}
	
	
	@Test
	public void testLargerEqualSmallerList() {
		List<Integer> listA = new ArrayList<Integer>();
		listA.add(4);
		listA.add(9);
		listA.add(2);
		
		List<Integer> listB = new ArrayList<Integer>();
		listB.add(8);
		listB.add(4);
		listB.add(5);
		
		List<Integer> listC = new ArrayList<Integer>();
		listC.add(7);
		listC.add(3);
		listC.add(4);
		
		assertEquals(false,ArrayUtils.listLargerOrEqual(listA, listB));
		assertEquals(true,ArrayUtils.listLargerOrEqual(listB, listB));
		assertEquals(true,ArrayUtils.listLargerOrEqual(listB, listB));
		assertEquals(true,ArrayUtils.listLargerOrEqual(listB, listC));
		assertEquals(false,ArrayUtils.listLargerOrEqual(listA, listC));
		
		assertEquals(false,ArrayUtils.listSmaller(listB, listB));
		assertEquals(true,ArrayUtils.listSmaller(listC, listB));
		assertEquals(false,ArrayUtils.listSmaller(listC, listA));
	}
	
	@Test
	public void testExtrapolateArray() {
		int[] array1 = new int[] {2, 3};
		int[] array2 = new int[] {2, 3, 9, 5};
		int[] array3 = new int[] {2};
		
		List<Integer> list = ArrayUtils.extrapolateArray(array1, 1);
		assertEquals("[2, 3]",Arrays.toString(list.toArray()));
		
		list = ArrayUtils.extrapolateArray(array1, 2);
		assertEquals("[2, 2, 3, 3]",Arrays.toString(list.toArray()));
		
		list = ArrayUtils.extrapolateArray(array1, 5);
		assertEquals("[2, 2, 2, 2, 2, 3, 3, 3, 3, 3]",Arrays.toString(list.toArray()));
		
		list = ArrayUtils.extrapolateArray(array2, 1);
		assertEquals("[2, 3, 9, 5]",Arrays.toString(list.toArray()));
		
		list = ArrayUtils.extrapolateArray(array2, 2);
		assertEquals("[2, 2, 3, 3, 9, 9, 5, 5]",Arrays.toString(list.toArray()));
		
		list = ArrayUtils.extrapolateArray(array3, 5);
		assertEquals("[2, 2, 2, 2, 2]",Arrays.toString(list.toArray()));
	}
	
	@Test
	public void testLinkLists() {
		List<Integer> listA = new ArrayList<Integer>();
		listA.add(1);
		listA.add(1);
		listA.add(4);
		
		List<Integer> listB = new ArrayList<Integer>();
		listB.add(3);
		listB.add(6);
		listB.add(5);
		
		List<Integer> listC = new ArrayList<Integer>();
		listC.add(7);
		listC.add(13);
		listC.add(7);

		List<Integer> linkList = ArrayUtils.linkLists(listA, listB);
		
		assertEquals("[7, 6, -1]", Arrays.toString(linkList.toArray()));
	}
	
	@Test
	public void testLargerArray() {
		List<Integer> listA = new ArrayList<Integer>(Arrays.asList(2));
		List<Integer> listB = new ArrayList<Integer>(Arrays.asList(3));
		
		assertTrue(ArrayUtils.listLarger(listB, listA));
		
		listA = new ArrayList<Integer>(Arrays.asList(2));
		listB = new ArrayList<Integer>(Arrays.asList(2));
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new ArrayList<Integer>(Arrays.asList(2,2));
		listB = new ArrayList<Integer>(Arrays.asList(2,2));
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new ArrayList<Integer>(Arrays.asList(2,4));
		listB = new ArrayList<Integer>(Arrays.asList(2,1));
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new ArrayList<Integer>(Arrays.asList(2,4,5));
		listB = new ArrayList<Integer>(Arrays.asList(2,5,4));
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new ArrayList<Integer>(Arrays.asList(2,4,5));
		listB = new ArrayList<Integer>(Arrays.asList(2,4,5));
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new ArrayList<Integer>(Arrays.asList(2,4,5));
		listB = new ArrayList<Integer>(Arrays.asList(2,4,6));
		
		assertTrue(ArrayUtils.listLarger(listB, listA));
		
		listA = new ArrayList<Integer>(Arrays.asList(2,4,5,9));
		listB = new ArrayList<Integer>(Arrays.asList(2,4,6,5));
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new ArrayList<Integer>(Arrays.asList(4,4,4,-1));
		listB = new ArrayList<Integer>(Arrays.asList(4,4,-1,-1));
		
		assertTrue(ArrayUtils.listLarger(listB, listA));
	}
	
}