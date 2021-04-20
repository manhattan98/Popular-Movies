package com.exersice.popularmovies.Models.UpdatableModel;

import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

public abstract class AbstractModelSelfComparable<T> extends AbstractModelSingleObserver<T> {
    @NonNull
    @Override
    protected SelfComparableList<T> provideComparable() {
        return new SelfComparableList<T>() {
            SynchronizedBucketList<T> bucketList = new SynchronizedBucketList<>();

            @Override
            public void setComparator(Comparator<T> comparator, long milliseconds, int count) {
                bucketList.setComparator(comparator);

                notifyObservers();
            }

            @Override
            public Comparator<T> getComparator() {
                return bucketList.currentComparator();
            }

            @Override
            public T getSorted(int index) {
                return bucketList.getSorted(index);
            }

            @Override
            public int size() {
                return bucketList.size();
            }

            @Override
            public void addAll(T[] c) {
                bucketList.addData(c);
            }

            @Override
            public void clear() {
                bucketList.clear();
            }
        };
    }
}
