package com.exersice.popularmovies.Models.AsyncModel;

import androidx.lifecycle.LifecycleOwner;

import com.exersice.popularmovies.Models.IComparableModel;
import com.exersice.popularmovies.Models.IUpdatableObservableComparableModel;

import java.io.Closeable;
import java.io.IOException;
import java.util.Comparator;

public abstract class ComparableSourceCacheModel<T> extends AbstractCacheModel<T> implements IComparableModel<T> {
    protected interface Source<T> extends Comparator<T>, Closeable {
        boolean available();
        void reset();
        T[] next() throws IOException;
        default boolean remove(T element) throws IOException { return false; }
        default boolean add(T element) throws IOException { return false; }
        default void close() throws IOException {}

        @Override
        default int compare(T o1, T o2) { return 0; }
    }


    private Source<T> mSource;

    protected abstract Source<T> initDefaultSource();


    protected final Source<T> getSource() {
        if (mSource == null)
            mSource = initDefaultSource();
        return mSource;
    }

    protected final void setSource(Source<T> source) {
        if (source == null)
            throw new NullPointerException();
        mSource = source;
    }


    public ComparableSourceCacheModel(CachePolicy<T> cachePolicy, ObservablePolicy observablePolicy, AsyncWorker worker, boolean strictCache) {
        super(cachePolicy, observablePolicy, worker, strictCache);
    }


    @Override
    protected Boolean addSynchronous(T element) throws IOException {
        return getSource().add(element);
    }

    @Override
    protected Boolean removeSynchronous(int index) throws IOException {
        return getSource().remove(getObservableCache().getValue().get(index));
    }

    @Override
    protected T[] fetchSynchronous() throws IOException {
        return getSource().next();
    }

    @Override
    public boolean available() {
        return getSource().available();
    }

    @Override
    public void setComparator(Comparator<T> comparator) {
        setComparator(comparator, getDefaultMilliseconds(), getDefaultCount());
    }

    @Override
    public void setComparator(Comparator<T> comparator, long milliseconds, int count) {
        if (!(comparator instanceof ComparableSourceCacheModel.Source))
            throw new IllegalStateException();

        setSource((Source<T>) comparator);
        getSource().reset();
        reFetch(milliseconds, count);
    }

    @Override
    public Comparator<T> getCurrentComparator() {
        return getSource();
    }
}
