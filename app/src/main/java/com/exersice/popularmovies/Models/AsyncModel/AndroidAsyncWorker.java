package com.exersice.popularmovies.Models.AsyncModel;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

public class AndroidAsyncWorker extends AsyncWorker {
    /**
     * android main thread handler
     */
    protected final Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

    @Override
    protected void post(Runnable runnable) {
        mainThreadHandler.post(runnable);
    }
}
