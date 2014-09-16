package org.wdrp.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class ArrayUtilsTest {
	
	@Test
	public void testMinimumList() {
		int[] listA = new int[5];
		listA[0] = 4;
		listA[1] = 9;
		listA[2] = 2;
		listA[3] = 7;
		listA[4] = 0;
		
		int[] listB = new int[5];
		listB[0] = 2;
		listB[1] = 8;
		listB[2] = 4;
		listB[3] = 5;
		listB[4] = 6;
		
		int[] minList = ArrayUtils.minList(listA, listB);
		
		assertEquals("[2, 8, 2, 5, 0]",Arrays.toString(minList));
	}
	
	@Test
	public void testLargerEqualSmallerList() {
		int[] listA = new int[3];
		listA[0] =(4);
		listA[1] =(9);
		listA[2] =(2);
		
		int[] listB = new int[3];
		listB[0] =(8);
		listB[1] =(4);
		listB[2] =(5);
		
		int[] listC = new int[3];
		listC[0] =(7);
		listC[1] =(3);
		listC[2] =(4);
		
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
	public void testLinkLists() {
		int[] listA = new int[3];
		listA[0] =(1);
		listA[1] =(1);
		listA[2] =(4);
		
		int[] listB = new int[3];
		listB[0] =(3);
		listB[1] =(6);
		listB[2] =(5);
		
		int[] listC = new int[3];
		listC[0] =(7);
		listC[1] =(13);
		listC[2] =(7);

		int[] linkList = ArrayUtils.linkLists(listA, listB,1);
		
		assertEquals("[7, 6, -1]", Arrays.toString(linkList));
	}
	
	@Test
	public void testLargerArray() {
		int[] listA = new int[]{2};
		int[] listB = new int[]{3};
		
		assertTrue(ArrayUtils.listLarger(listB, listA));
		
		listA = new int[]{2};
		listB = new int[]{2};
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new int[]{2,2};
		listB = new int[]{2,2};
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new int[]{2,4};
		listB = new int[]{2,1};
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new int[]{2,4,5};
		listB = new int[]{2,5,4};
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new int[]{2,4,5};
		listB = new int[]{2,4,5};
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new int[]{2,4,5};
		listB = new int[]{2,4,6};
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new int[]{2,4,5,9};
		listB = new int[]{2,4,6,5};
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
		
		listA = new int[]{4,4,4,-1};
		listB = new int[]{4,4,-1,-1};
		
		assertFalse(ArrayUtils.listLarger(listB, listA));
	}
}