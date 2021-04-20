package com.exersice.popularmovies.Models.UpdatableModel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * class that fetches data from simple stream in background thread and then enqueues result handling to main thread.
 * @param <T>
 */
public abstract class AsyncFetcher<T> implements IFetcher<T> {
    /**
     * maximum read operations within fetch invocation
     */
    private final int mMaxReadsOnFetch;

    /**
     * maximum amount of read operations
     */
    private final int mMaxReadsTotalFetch;

    /**
     * current successful reads counter
     */
    private volatile int mReadsPerformed = 0;


    /**
     * executor for fetch operation
     */
    private final ExecutorService mExecService = Executors.newSingleThreadExecutor();

    /**
     * executor for sub-operations during fetch
     */
    private final ExecutorService mSubExecService = Executors.newSingleThreadExecutor();


    protected final FetchCallback<T> mFetchCallback;

    private final SynchronousSimpleStream<T> mStream;


    public AsyncFetcher(SynchronousSimpleStream<T> stream,
                        FetchCallback<T> fetchCallback,
                        int readsOnFetch,
                        int readsTotalFetch) {
        if (stream == null)
            throw new IllegalStateException("parameter SynchronousSimpleStream must be non null value!");

        mStream = stream;
        mFetchCallback = fetchCallback;

        mMaxReadsOnFetch = readsOnFetch;
        mMaxReadsTotalFetch = readsTotalFetch;
    }


    public AsyncFetcher(AsyncFetcher<T> origin,
                        SynchronousSimpleStream<T> newStream) {
        if (newStream == null)
            throw new IllegalStateException("parameter SynchronousSimpleStream must be non null value!");

        mStream = newStream;
        mFetchCallback = origin.mFetchCallback;

        mMaxReadsOnFetch = origin.mMaxReadsOnFetch;
        mMaxReadsTotalFetch = origin.mMaxReadsTotalFetch;
    }

    @Override
    public void fetch(long milliseconds, int count) {
        // maximum inclusive page number
        final int readsThreshold = Math.min(mReadsPerformed + count, mMaxReadsTotalFetch);

        // timeout per task
        //final long millisecondsPerTask = milliseconds / (mPagesLoadedThreshold - mPagesLoaded);
        final long millisecondsPerTask = milliseconds;

        Runnable mWorkTask = () -> {
            // init buffer
            List<T> bufferData = new LinkedList<>();
            Throwable throwable = null;

            try {
                while ((mReadsPerformed < readsThreshold) && mStream.available()) {
                    Future<T[]> readTask = mSubExecService.submit(() -> {
                        Thread.currentThread().setName("Read Task Thread");

                        return mStream.read();
                    });

                    List<T> received = Arrays.asList(readTask.get(millisecondsPerTask, TimeUnit.MILLISECONDS));
                    bufferData.addAll(received);

                    mReadsPerformed++;
                }
            } catch (TimeoutException E) {
                throwable = E;
            } catch (ExecutionException E) {
                throwable = E.getCause();
            } catch (InterruptedException E) {
                // TODO: interrupt policy realization
            } finally {
                postResult((T[])bufferData.toArray(), throwable);
            }
        };

        mExecService.execute(mWorkTask);

    }

    @Override
    public void fetch(long milliseconds) {
        fetch(milliseconds, mMaxReadsOnFetch);
    }

    @Override
    public void shutdown() {
        mExecService.shutdown();
        mSubExecService.shutdown();

        mStream.reset();
        mReadsPerformed = 0;
    }

    @Override
    public boolean available() {
        return mStream.available();
    }


    /**
     * enqueues result callback to thread handler. data[] array can be empty when some error occurs while read
     * @param data data received. can be empty
     * @param throwable optional throwable. can be null
     */
    protected void postResult(T[] data, Throwable throwable) {
        if (mFetchCallback != null)
            post(() -> mFetchCallback.onFetch(data, throwable));
    }

    /**
     * abstract method for
     * @param runnable
     */
    protected abstract void post(Runnable runnable);

}
