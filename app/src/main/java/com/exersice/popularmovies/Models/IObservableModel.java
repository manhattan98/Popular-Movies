package com.exersice.popularmovies.Models;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.lifecycle.LifecycleOwner;

public interface IObservableModel<T> {
    /**
     * set observer callback when content updates. when callback triggered, size and data is up-to-date already
     * @param owner lifecycle owner object, e.g. Activity
     * @param observer callback for handling update data events, for example notifying RecyclerView.Adapter
     */
    @UiThread
    void observe(LifecycleOwner owner, UpdateObserver observer);

    /**
     * callback interface that observes updates in model and can handle exceptions
     */
    interface UpdateObserver {
        @UiThread
        void onUpdate(@Nullable Throwable throwable);
    }
}
