package com.exersice.popularmovies.Models.AsyncModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class IModelRWTest {
    final static int DEFAULT_COUNT = 3;
    final static long DEFAULT_MILLISECONDS_TIMEOUT = 1000;
    final static long DEFAULT_MILLISECONDS_SLEEP = 800;
    final static int PAGE_SIZE = 5;

    StubDataSource stubDataSource;

    IModelRW<Object> model;
    ExecutorService executorService;
    AtomicBoolean isCalledBack;

    AbstractCacheModel.ObservablePolicy<?> stubObservable = new AbstractCacheModel.ObservablePolicy<Object>() {
        List<IModelRW.CacheObserver> observers = new LinkedList<>();
        Object value = null;

        @Override
        public void observe(LifecycleOwner owner, IModelRW.CacheObserver observer) {
            observers.add(observer);
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public void setValue(Object element, Throwable throwable) {
            value = element;
            for (IModelRW.CacheObserver observer : observers)
                observer.onCacheUpdated(throwable);
        }
    };

    AsyncWorker stubWorker = new AsyncWorker() {
        @Override
        protected void post(Runnable runnable) {
            assertFalse(isCalledBack.get());

            executorService.submit(runnable);

            assertTrue("Succeed!", isCalledBack.get());
        }
    };


    @Before
    public void setUp() throws Exception {
        executorService = Executors.newSingleThreadExecutor();
        isCalledBack = new AtomicBoolean(false);

        stubDataSource = new StubDataSource(DEFAULT_MILLISECONDS_SLEEP, PAGE_SIZE);

        model = new AbstractCacheModel<Object>(CachePolicies.newListCache(), stubObservable, stubWorker, true) {
            @Override
            protected Boolean addSynchronous(Object element) throws IOException {
                return stubDataSource.addSynchronous(element);
            }

            @Override
            protected Boolean removeSynchronous(int index) throws IOException {
                return stubDataSource.removeSynchronous(index);
            }

            @Override
            protected Object[] fetchSynchronous() throws IOException {
                return stubDataSource.fetchSynchronous();
            }

            @Override
            protected void releaseAll() throws IOException {
                stubDataSource.releaseAll();
            }

            @Override
            public boolean available() {
                return stubDataSource.available();
            }
        };
        model.setDefaultMillisAndCount(DEFAULT_MILLISECONDS_TIMEOUT, DEFAULT_COUNT);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void get() {
    }

    @Test
    public void size() {
    }

    @Test
    public void fetch() {
    }

    @Test
    public void reFetch() {
    }

    @Test
    public void addAsync() {
    }

    @Test
    public void removeAsync() {
    }

    /*@Test
    public void available() {
    }*/

    @Test
    public void observe() throws IOException {
        final String mainThreadName = Thread.currentThread().getName();

        model.observe(null, throwable -> {
            assertNotEquals(Thread.currentThread().getName(), mainThreadName);
            isCalledBack.set(true);
            assertNotEquals(throwable, null);
        });

        stubDataSource.addSynchronous(new Object());
        model.fetch();

    }
}