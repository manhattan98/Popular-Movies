package com.exersice.popularmovies.Models.UpdatableModel;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.Assert.*;

public class SynchronizedBucketListTest {
    SynchronizedBucketList<Integer> bucketList = new SynchronizedBucketList<>();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testClone() {
    }

    @Test
    public void testEquals() {
    }

    @Test
    public void addData() {
        Integer[] addedIntegers = new Integer[] {1, 2, 3};

        bucketList.addData(addedIntegers);

        Integer[] array = new Integer[bucketList.size()];

        for (int i = 0; i < bucketList.size(); i++)
            array[i] = bucketList.get(i);

        assertArrayEquals(addedIntegers, array);
    }

    @Test
    public void size() {
        assertEquals(0, bucketList.size());


    }

    @Test
    public void get() {
    }

    @Test
    public void getSorted() {
        Comparator<Integer> intComparator = (o1, o2) -> o2 - o1;

        Integer[] addedArray = new Integer[] {2, 5, 9, 0, 44, 91, 11};

        Integer[] sortedArray = new Integer[addedArray.length];
        System.arraycopy(addedArray, 0, sortedArray, 0, addedArray.length);
        Arrays.sort(sortedArray, intComparator);

        bucketList.addData(addedArray);
        bucketList.setComparator(intComparator);
        Integer[] resultArray = new Integer[bucketList.size()];
        for (int i = 0; i < bucketList.size(); i++)
            resultArray[i] = bucketList.getSorted(i);

        assertArrayEquals(sortedArray, resultArray);
    }

    @Test
    public void clear() {
        bucketList.addData(new Integer[1]);

        assertNotEquals(0, bucketList.size());

        bucketList.clear();

        assertEquals(0, bucketList.size());
    }

    @Test
    public void remove() {
        bucketList.addData(new Integer[]{1, 5});
        bucketList.addData(new Integer[]{6});

        bucketList.remove(2);
        bucketList.remove(0);

        Integer[] resultArray = new Integer[bucketList.size()];
        for (int i = 0; i < bucketList.size(); i++)
            resultArray[i] = bucketList.get(i);

        assertArrayEquals(new Integer[] {5}, resultArray);
    }

    @Test
    public void setComparator() {
    }

    @Test
    public void currentComparator() {
    }
}