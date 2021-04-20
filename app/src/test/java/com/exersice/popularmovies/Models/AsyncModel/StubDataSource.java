package com.exersice.popularmovies.Models.AsyncModel;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class StubDataSource {
    private final long maxMillisecondsSleep;
    private final int pageSize;

    private final List<Object> sourceList;


    private int curPage = 0;


    public StubDataSource(long maxMilliseconds, int pageSize) {
        this.maxMillisecondsSleep = maxMilliseconds;
        this.pageSize = pageSize;
        this.sourceList = Collections.synchronizedList(new LinkedList<>());
    }

    /**
     * typical delay for imitating long time running blocking operations
     */
    protected void typicalWait() {
        Random rand = new Random();
        try {
            Thread.sleep(Math.min(Math.abs(rand.nextLong()), maxMillisecondsSleep));
        } catch (InterruptedException ignored) { }
    }

    public Boolean addSynchronous(Object element) throws IOException {
        typicalWait();

        return sourceList.add(element);
    }

    public Boolean removeSynchronous(int index) throws IOException {
        if (sourceList.size() == 0)
            throw new IOException();

        typicalWait();

        sourceList.remove(index);
        return true;
    }

    public Object[] fetchSynchronous() throws IOException {
        if (!available())
            throw new IOException("Source is not available!");

        Object[] result = new Object[Math.min(sourceList.size() - pageSize * curPage, pageSize)];
        /*if (result.length == 0)
            throw new IOException();*/

        for (int i = 0; i < result.length; i++)
            result[i] = sourceList.get(i + curPage * pageSize);

        curPage++;

        typicalWait();

        return result;
    }

    public void releaseAll() throws IOException { }

    public boolean available() {
        int pagesMax = sourceList.size() / pageSize + (sourceList.size() - (sourceList.size() / pageSize) * pageSize == 0 ? 0 : 1);
        return curPage < pagesMax;
    }
}
