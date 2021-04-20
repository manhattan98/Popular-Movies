package com.exersice.popularmovies.Models.UpdatableModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public abstract class AbstractModelSingleObserver<T> extends AbstractViewModel<T> {
    /**
     * implements single observer policy
     * @return observer policy
     */
    @NonNull
    @Override
    protected ObservableComparableList<T> provideObservable() {
        return new ObservableComparableList<T>() {
            UpdateObserver observer;
            LifecycleOwner owner;

            @Override
            public void addObserver(LifecycleOwner owner, UpdateObserver observer) {
                this.owner = owner;
                this.observer = observer;
            }

            @Override
            public void set(SelfComparableList<T> tSelfComparableList, Throwable throwable) {
                setSCList(tSelfComparableList);

                if (this.owner != null && this.owner.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED)
                    observer.onUpdate(throwable);
            }

            @Override
            public SelfComparableList<T> get() {
                return getSCList();
            }
        };
    }
}
