package com.exersice.popularmovies.Models.UpdatableModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public abstract class AbstractModelLiveDataObservable<T> extends AbstractViewModel<T> {

    @NonNull
    @Override
    protected ObservableComparableList<T> provideObservable() {
        return new ObservableComparableList<T>() {
            MutableLiveData<SelfComparableList<T>> liveData = new MutableLiveData<>();
            Throwable lastThrowable = null;

            @Override
            public void addObserver(LifecycleOwner owner, UpdateObserver observer) {
                liveData.observe(owner, tSelfComparableList -> {
                    observer.onUpdate(lastThrowable);
                    lastThrowable = null;
                });
            }

            @Override
            public void set(SelfComparableList<T> tSelfComparableList, Throwable throwable) {
                lastThrowable = throwable;
                liveData.setValue(tSelfComparableList);
            }

            @Override
            public SelfComparableList<T> get() {
                return liveData.getValue();
            }
        };
    }
}
