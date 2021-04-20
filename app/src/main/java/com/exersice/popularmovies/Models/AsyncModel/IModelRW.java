package com.exersice.popularmovies.Models.AsyncModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

public interface IModelRW<T> {
    /**
     * set default values for async
     * @param milliseconds
     * @param count
     */
    void setDefaultMillisAndCount(long milliseconds, int count);

    /**
     * get cached entry
     * @param index index in cache
     * @return cache entry
     */
    T get(int index);

    /**
     * current cache size. may be increased after fetch
     * @return current size
     */
    int size();

    /**
     * fetch next data from repository
     * @param milliseconds timeout
     * @param count number of fetch operations after call
     */
    void fetch(long milliseconds, int count);
    void fetch();

    /**
     * clear cache and reset fetch cursor. then re-fetch data from repository
     * @param milliseconds timeout
     * @param count number of fetch operations after call
     */
    void reFetch(long milliseconds, int count);
    void reFetch();

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

    /**
     * data source fetch availability status
     * @return true if source can provide more data, otherwise false
     */
    boolean available();

    /**
     * add lifecycle-aware observer that handles cache updates
     * @param owner lifecycle owner
     * @param observer cache observer that pertain owner
     */
    void observe(LifecycleOwner owner, CacheObserver observer);


    /**
     * interface that allows handling cache updates
     */
    @FunctionalInterface
    interface CacheObserver {
        void onCacheUpdated(@Nullable Throwable throwable);
    }

    @FunctionalInterface
    interface AddCallback {
        void onAdd(@Nullable Throwable throwable);
    }

    @FunctionalInterface
    interface RemoveCallback {
        void onRemove(@Nullable Throwable throwable);
    }
}
