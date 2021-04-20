package com.exersice.popularmovies.Models.AsyncModel;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AsyncWorker {
    /**
     * executor for submit operation
     */
    protected final ExecutorService mExecService = Executors.newSingleThreadExecutor();

    /**
     * executor for sub-operations during submit
     */
    protected final ExecutorService mSubExecService = Executors.newSingleThreadExecutor();


    public <T> void submit(long milliseconds, int count, MultipleAction<T> action) {
        // timeout per task
        //final long millisecondsPerTask = milliseconds / (mPagesLoadedThreshold - mPagesLoaded);
        final long millisecondsPerTask = milliseconds;

        Runnable mainTask = () -> {
            Thread.currentThread().setName("Main Task Thread");

            // init buffer
            List<T> buffer = Collections.synchronizedList(new LinkedList<>());
            Throwable throwable = null;

            try {
                for (int i = 0; (i < count) && (action.availableInvoke()); i++) {
                    final int subTaskNum = i;
                    Future<T> subTask = mSubExecService.submit(() -> {
                        Thread.currentThread().setName("Sub Task Thread - " + subTaskNum);

                        return action.nextInvoke();
                    });

                    //List<T> received = Arrays.asList(subTask.get(millisecondsPerTask, TimeUnit.MILLISECONDS));
                    T received = subTask.get(millisecondsPerTask, TimeUnit.MILLISECONDS);
                    buffer.add(received);
                }
            } catch (TimeoutException E) {
                throwable = E;
            } catch (ExecutionException E) {
                throwable = E.getCause();
            } catch (InterruptedException E) {
                // TODO: interrupt policy realization
            } finally {
                final Throwable finalThrowable = throwable;
                post(() ->
                    action.returnAll(finalThrowable, buffer));
            }
        };

        mExecService.execute(mainTask);
    }


    public <T> void submit(long milliseconds, Action<T> action) {
        submit(milliseconds, 1, new MultipleAction<T>() {
            @Override
            public boolean availableInvoke() {
                return true;
            }

            @Override
            public T nextInvoke() throws IOException {
                return action.doInvoke();
            }

            @Override
            public void returnAll(Throwable throwable, List<T> results) {
                action.onReturn(throwable, results.get(0));
            }
        });
    }


    public void shutdown() {
        mExecService.shutdown();
        mSubExecService.shutdown();
    }


    /**
     * enqueues runnable callback to caller's thread
     * @param runnable
     */
    protected abstract void post(Runnable runnable);


    /**
     * interface that specify some async action, that can be invoked multiple times by calling nextInvoke()
     * after all invocations it handles all accumulated results by calling returnAll()
     * @param <T>
     */
    public interface MultipleAction<T> {
        /**
         * availability status
         * @return true if has more work, otherwise false
         */
        boolean availableInvoke();

        /**
         * method for calling time consumption synchronous operations. can be invoked multiple times
         * @return some data
         */
        T nextInvoke() throws IOException;

        /**
         * callback that handles result from, possibly multiply, nextInvoke() calls
         * @param throwable optional throwable if occurred
         * @param results data array from multiply calls nextInvoke()
         */
        void returnAll(Throwable throwable, List<T> results);
    }

    /**
     * interface that specify some async action, that can be invoked once
     * after invocation it handles result by calling onReturn()
     * @param <T>
     */
    public interface Action<T> {
        /**
         * method for calling time consumption synchronous operations
         * @return some data
         */
        T doInvoke() throws IOException;

        /**
         * callback that handles result from onInvoke() call
         * @param throwable optional throwable if occurred
         * @param result data from call doInvoke()
         */
        void onReturn(Throwable throwable, T result);
    }

}
