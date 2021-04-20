package com.exersice.popularmovies.Models.UpdatableModel;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractModelStreamComparable<T> extends AbstractModelSingleObserver<T> {

    @NonNull
    @Override
    protected SelfComparableList<T> provideComparable() {
        return new SelfComparableList<T>() {
            List<T> list;
            {
                list = Collections.synchronizedList(new LinkedList<>());
                Log.v(TAG, "model data structure is initialized");
            }
            Comparator<T> comparator;

            @Override
            public void setComparator(Comparator<T> comparator, long milliseconds, int count) {
                if (!(comparator instanceof IFetcher.SynchronousSimpleStream))
                    throw new IllegalStateException("stream comparable model needs SynchronousSimpleStream implementation!");

                this.comparator = comparator;

                setFetcherStream((IFetcher.SynchronousSimpleStream<T>) comparator);

                list.clear();

                fetch(milliseconds, count);
            }

            @Override
            public Comparator<T> getComparator() {
                return comparator;
            }

            @Override
            public T getSorted(int index) {
                return list.get(index);
            }

            @Override
            public int size() {
                return list.size();
            }

            @Override
            public void addAll(T[] c) {
                list.addAll(Arrays.asList(c));
            }

            @Override
            public void clear() {
                list.clear();
            }
        };
    }

}
