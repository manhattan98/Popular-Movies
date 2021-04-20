package com.exersice.popularmovies.Models.AsyncModel;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public abstract class ObservablePolicies {
    public static <T> AbstractCacheModel.ObservablePolicy<T> newSingleObserver() {
        return new AbstractCacheModel.ObservablePolicy<T>() {
            LifecycleOwner owner = null;
            IModelRW.CacheObserver observer = null;
            T value;

            @Override
            public void observe(LifecycleOwner owner, IModelRW.CacheObserver observer) {
                this.owner = owner;
                this.observer = observer;
            }

            @Override
            public T getValue() {
                return value;
            }

            @Override
            public void setValue(T element, Throwable throwable) {
                value = element;

                if (owner.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED)
                    observer.onCacheUpdated(throwable);
            }
        };
    }

    public static <T> AbstractCacheModel.ObservablePolicy<T> newLiveDataObserver() {
        return new AbstractCacheModel.ObservablePolicy<T>() {
            MutableLiveData<T> liveData = new MutableLiveData<>();
            Throwable lastThrowable = null;

            @Override
            public void observe(LifecycleOwner owner, IModelRW.CacheObserver observer) {
                liveData.observe(owner, t ->
                        observer.onCacheUpdated(lastThrowable));
            }

            @Override
            public T getValue() {
                return liveData.getValue();
            }

            @Override
            public void setValue(T element, Throwable throwable) {
                lastThrowable = throwable;
                liveData.setValue(element);
            }
        };
    }
}
