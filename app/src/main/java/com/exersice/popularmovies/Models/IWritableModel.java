package com.exersice.popularmovies.Models;

import androidx.annotation.Nullable;

public interface IWritableModel<T> {
    void setDefaultMillisAndCount(long milliseconds, int count);

    /**
     * add new entry right to repository
     * cache may be invalidated !
     * @param milliseconds timeout
     */
    void addAsync(long milliseconds, T element, AddCallback callback);
    void addAsync(T element, AddCallback callback);

    /**
     * remove entry right from repository. only cached entry can be identified by its index
     * @param milliseconds timeout
     * @param index index in cache
     */
    void removeAsync(long milliseconds, int index, RemoveCallback callback);
    void removeAsync(int index, RemoveCallback callback);


    @FunctionalInterface
    interface AddCallback {
        void onAdd(@Nullable Throwable throwable);
    }

    @FunctionalInterface
    interface RemoveCallback {
        void onRemove(@Nullable Throwable throwable);
    }
}
