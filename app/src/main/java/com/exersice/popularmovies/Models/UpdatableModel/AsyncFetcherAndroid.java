package com.exersice.popularmovies.Models.UpdatableModel;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;

import androidx.core.os.HandlerCompat;

public class AsyncFetcherAndroid<T> extends AsyncFetcher<T> {
    /**
     * android main thread handler
     */
    protected final Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

    public AsyncFetcherAndroid(SynchronousSimpleStream<T> stream, FetchCallback<T> fetchCallback, int readsOnFetch, int readsTotalFetch) {
        super(stream, fetchCallback, readsOnFetch, readsTotalFetch);
    }

    public AsyncFetcherAndroid(AsyncFetcher<T> origin, SynchronousSimpleStream<T> newStream) {
        super(origin, newStream);
    }

    @Override
    protected void post(Runnable runnable) {
        mainThreadHandler.post(runnable);
    }
}
