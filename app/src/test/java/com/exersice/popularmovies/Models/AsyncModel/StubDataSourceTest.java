package com.exersice.popularmovies.Models.AsyncModel;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class StubDataSourceTest {
    StubDataSource stubDataSource;

    @Before
    public void setUp() throws Exception {
        stubDataSource = new StubDataSource(1000, 3);
    }


    @Test
    public void addSynchronous() {

    }

    @Test
    public void removeSynchronous() throws IOException {
        try {
            stubDataSource.removeSynchronous(0);
            fail();
        } catch (IOException ignored) { }
    }

    @Test
    public void fetchSynchronous() throws IOException {
        // fetch when empty
        try {
            assertArrayEquals(new Object[0], stubDataSource.fetchSynchronous());
            fail();
        } catch (IOException e) { }

        String[] added = new String[] { "one", "two", "three", "four" };

        for (int i = 0; i < added.length; i++)
            stubDataSource.addSynchronous(added[i]);

        Object[] fetched = stubDataSource.fetchSynchronous();

        assertArrayEquals(fetched, new String[] { "one", "two", "three" });


        fetched = stubDataSource.fetchSynchronous();

        assertArrayEquals(fetched, new String[] { "four" });
    }

    @Test
    public void available() throws IOException {
        assertFalse(stubDataSource.available());

        stubDataSource.addSynchronous(new Object());

        assertTrue(stubDataSource.available());
    }

}