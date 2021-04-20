package com.exersice.popularmovies.Models.AsyncModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCacheModel<T> extends ViewModel implements IModelRW<T> {
    protected final String TAG = getClass().getSimpleName();

    // final
    private ObservablePolicy<CachePolicy<T>> mObservableCache;

    private final ObservablePolicy<?> mObservablePolicy;
    private final CachePolicy<T> mCachePolicy;

    private AsyncWorker mWorker;

    protected final boolean StrictCacheMode;


    private Long mDefaultMilliseconds = null;
    private Integer mDefaultCount = null;


    public interface CachePolicy<T> {
        T get(int index);
        int size();
        void addAll(Collection<T> c);
        default void add(T element) { addAll(Collections.singletonList(element)); }
        void clear();
        void remove(int index);
        default CachePolicy<T> copy() { return this; }
    }

    public interface ObservablePolicy<T> {
        void observe(LifecycleOwner owner, CacheObserver observer);
        T getValue();
        void setValue(T element, Throwable throwable);
        default void setValue(T element) { setValue(element, null); }
    }

    /**
     * cache policy parametrization
     */
    public AbstractCacheModel(CachePolicy<T> cachePolicy,
                              ObservablePolicy<?> observablePolicy,
                              AsyncWorker worker,
                              boolean strictCache) {
        if (cachePolicy == null || observablePolicy == null || worker == null)
            throw new NullPointerException();

        mCachePolicy = cachePolicy;
        mObservablePolicy = observablePolicy;

        mWorker = worker;

        StrictCacheMode = strictCache;
    }


    /**
     * method that allows complement observable policy interface to add more functionality
     * @param observable
     * @return complemented observable
     */
    protected ObservablePolicy<?> prepareObservable(ObservablePolicy<?> observable) {
        return observable;
    }

    /**
     * method that allows complement cache policy interface to add more functionality
     * @param cache
     * @return complemented cache
     */
    protected CachePolicy<T> prepareCache(CachePolicy<T> cache) {
        return cache;
    }


    protected final ObservablePolicy<CachePolicy<T>> getObservableCache() {
        if (mObservableCache == null)
            mObservableCache = new ObservablePolicy<CachePolicy<T>>() {
                ObservablePolicy<CachePolicy<T>> observable = (ObservablePolicy<CachePolicy<T>>) prepareObservable(mObservablePolicy);
                CachePolicy<T> cache = prepareCache(mCachePolicy);
                boolean valueNotSet = true;

                @Override
                public void observe(LifecycleOwner owner, CacheObserver observer) {
                    observable.observe(owner, observer);
                }

                @Override
                public CachePolicy<T> getValue() {
                    if (valueNotSet)
                        return cache;
                    return observable.getValue();
                }

                @Override
                public void setValue(CachePolicy<T> element, Throwable throwable) {
                    observable.setValue(element, throwable);
                    valueNotSet = false;
                }
            };
        return mObservableCache;
    }

    protected long getDefaultMilliseconds() {
        if (mDefaultMilliseconds == null)
            throw new IllegalStateException();
        return mDefaultMilliseconds;
    }

    protected int getDefaultCount() {
        if (mDefaultCount == null)
            throw  new IllegalStateException();
        return mDefaultCount;
    }


    protected abstract Boolean addSynchronous(T element) throws IOException;

    protected abstract Boolean removeSynchronous(int index) throws IOException;

    protected abstract T[] fetchSynchronous() throws IOException;

    protected abstract void releaseAll() throws IOException;

    @Override
    public abstract boolean available();


    protected abstract class FetchAction implements AsyncWorker.MultipleAction<T[]> {
        @Override
        public boolean availableInvoke() {
            return available();
        }

        @Override
        public T[] nextInvoke() throws IOException {
            return fetchSynchronous();
        }

        @Override
        public void returnAll(Throwable throwable, List<T[]> results) {
            List<T> res = new LinkedList<>();

            for (T[] array : results)
                res.addAll(Arrays.asList(array));

            saveToCache(throwable, res, getObservableCache().getValue().copy());
        }

        public abstract void saveToCache(Throwable throwable, List<T> results, CachePolicy<T> intermediateCache);
    }


    // ---------------------------------------------- interface implementation ----------------------------------------------

    @Override
    public void setDefaultMillisAndCount(long milliseconds, int count) {
        mDefaultMilliseconds = milliseconds;
        mDefaultCount = count;
    }

    /**
     * get cached entry
     *
     * @param index index in cache
     * @return cache entry
     */
    @Override
    public T get(int index) {
        return getObservableCache().getValue().get(index);
    }

    /**
     * current cache size. may be increased after fetch
     *
     * @return current size
     */
    @Override
    public int size() {
        return getObservableCache().getValue().size();
    }

    /**
     * fetch next data from repository
     *
     * @param milliseconds timeout
     * @param count        number of fetch operations after call
     */
    @Override
    public void fetch(long milliseconds, int count) {
        mWorker.submit(milliseconds, count, new FetchAction() {
            @Override
            public void saveToCache(Throwable throwable, List<T> results, CachePolicy<T> intermediateCache) {
                intermediateCache.addAll(results);
                getObservableCache().setValue(intermediateCache, throwable);
            }
        });
    }

    @Override
    public void fetch() {
        fetch(getDefaultMilliseconds(), getDefaultCount());
    }

    /**
     * clear cache and reset fetch cursor. then re-fetch data from repository
     *
     * @param milliseconds timeout
     * @param count        number of fetch operations after call
     */
    @Override
    public void reFetch(long milliseconds, int count) {
        mWorker.submit(milliseconds, count, new FetchAction() {
            @Override
            public void saveToCache(Throwable throwable, List<T> results, CachePolicy<T> intermediateCache) {
                intermediateCache.clear();
                intermediateCache.addAll(results);
                getObservableCache().setValue(intermediateCache, throwable);
            }
        });
    }

    @Override
    public void reFetch() {
        reFetch(getDefaultMilliseconds(), getDefaultCount());
    }

    /**
     * add new entry right to repository
     * cache may be invalidated !
     *
     * @param milliseconds timeout
     * @param element
     * @param callback
     */
    @Override
    public void addAsync(long milliseconds, T element, AddCallback callback) {
        mWorker.submit(milliseconds, new AsyncWorker.Action<Boolean>() {
            @Override
            public Boolean doInvoke() throws IOException {
                return addSynchronous(element);
            }

            @Override
            public void onReturn(Throwable throwable, Boolean result) {
                if (!StrictCacheMode)
                    if (result) {
                        CachePolicy<T> imCache = getObservableCache().getValue().copy();
                        imCache.add(element);
                        getObservableCache().setValue(imCache, throwable);
                    }

                callback.onAdd(throwable);
            }
        });
    }

    @Override
    public void addAsync(T element, AddCallback callback) {
        addAsync(getDefaultMilliseconds(), element, callback);
    }

    /**
     * remove entry right from repository. only cached entry can be identified by its index
     *
     * @param milliseconds timeout
     * @param index        index in cache
     * @param callback
     */
    @Override
    public void removeAsync(long milliseconds, int index, RemoveCallback callback) {
        mWorker.submit(milliseconds, new AsyncWorker.Action<Boolean>() {
            @Override
            public Boolean doInvoke() throws IOException {
                return removeSynchronous(index);
            }

            @Override
            public void onReturn(Throwable throwable, Boolean result) {
                if (!StrictCacheMode)
                    if (result) {
                        CachePolicy<T> imCache = getObservableCache().getValue().copy();
                        imCache.remove(index);
                        getObservableCache().setValue(imCache, throwable);
                    }

                callback.onRemove(throwable);
            }
        });
    }

    @Override
    public void removeAsync(int index, RemoveCallback callback) {
        removeAsync(getDefaultMilliseconds(), index, callback);
    }

    /**
     * add lifecycle-aware observer that handles cache updates
     *
     * @param owner    lifecycle owner
     * @param observer cache observer that pertain owner
     */
    @Override
    public void observe(LifecycleOwner owner, CacheObserver observer) {
        getObservableCache().observe(owner, observer);
    }


    @Override
    protected void onCleared() {
        try {
            releaseAll();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mWorker.shutdown();

        super.onCleared();
    }


    /**
     * custom factory for proper model initialization
     */
    public static class CacheModelNewInstanceFactory implements ViewModelProvider.Factory {
        protected final CachePolicy<?> cachePolicy;
        protected final ObservablePolicy<?> observablePolicy;
        protected final AsyncWorker asyncWorker;
        protected final boolean strictCache;

        /**
         * default constructor reflects model constructor.
         * subclasses can complement arguments list by overriding template methods addParameterTypes() and addParameterArgs().
         * this methods allows append new constructor parameters to the end of default parameters list
         * @param cachePolicy
         * @param observablePolicy
         * @param worker
         * @param strictCache
         */
        public CacheModelNewInstanceFactory(CachePolicy<?> cachePolicy,
                                            ObservablePolicy<?> observablePolicy,
                                            AsyncWorker worker,
                                            boolean strictCache) {
            this.cachePolicy = cachePolicy;
            this.observablePolicy = observablePolicy;
            this.asyncWorker = worker;
            this.strictCache = strictCache;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            T modelInstance = null;

            final Class<?>[] baseTypes = new Class<?>[] { CachePolicy.class, ObservablePolicy.class, AsyncWorker.class, boolean.class };
            final Class<?>[] addedTypes = addParameterTypes();
            final Class<?>[] complementedTypes = new Class<?>[baseTypes.length + addedTypes.length];

            final Object[] baseArgs = new Object[] { cachePolicy, observablePolicy, asyncWorker, strictCache };
            final Object[] addedArgs = addParameterArgs();
            final Object[] complementedArgs = new Object[baseArgs.length + addedArgs.length];

            for (int i = 0; i < complementedTypes.length; i++) {
                if (i < baseTypes.length) {
                    complementedTypes[i] = baseTypes[i];
                    complementedArgs[i] = baseArgs[i];
                }
                else {
                    complementedTypes[i] = addedTypes[i - baseTypes.length];
                    complementedArgs[i] = addedArgs[i - baseArgs.length];
                }
            }

            Throwable cause = null;

            try {
                modelInstance = modelClass.getConstructor(complementedTypes).newInstance(complementedArgs);
            } catch (IllegalAccessException|InstantiationException|NoSuchMethodException|InvocationTargetException e) {
                cause = e;
            }
            if (modelInstance instanceof AbstractCacheModel) {
                return modelInstance;
            }
            else {
                throw new RuntimeException("Class<" + modelClass.getSimpleName() + "> cannot be instantiated by " + getClass().getSimpleName(), cause);
            }
        }

        /**
         * template method that appends new constructor parameter types to the end of default parameters list
         */
        protected Class<?>[] addParameterTypes() {
            return new Class<?>[0];
        }

        /**
         * template method that appends new constructor parameter values to the end of default parameters list
         */
        protected Object[] addParameterArgs() {
            return new Object[0];
        }
    }
}
