package com.exersice.popularmovies.Models.UpdatableModel;

import android.os.Parcelable;

import java.io.IOException;
import java.io.Serializable;

public interface IFetcher<T> {
    /**
     * fetch data using provided loader in parallel thread, then return array in callback
     * @param milliseconds timeout
     */
    void fetch(long milliseconds);
    void fetch(long milliseconds, int count);

    /**
     * free all resources
     */
    void shutdown();

    boolean available();

    /**
     * interface that handles fetch result and optionally some throwable
     * @param <T>
     */
    interface FetchCallback<T> {
        void onFetch(T[] data, Throwable throwable);
    }

    /**
     * interface that represents stream-wise approach to time consumption data loads
     * @param <T>
     */
    interface SynchronousSimpleStream<T> {
        default boolean available() {
            return true;
        }
        T[] read() throws IOException;
        void reset();
    }

}
