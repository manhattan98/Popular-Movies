package com.exersice.popularmovies;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class MiscellaneousTests {

    public static class TestClass {
        public TestClass(int n) {}
        public TestClass() {}
    }

    @Test
    public void testReflection1() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        int k = 5;
        Integer l = 1;

        Object ob = 5;

        Assert.assertNotEquals(int.class, Integer.class);

        Assert.assertEquals(ob.getClass(), Integer.class);



        TestClass t = TestClass.class.getConstructor(int.class).newInstance(25);
    }


}
