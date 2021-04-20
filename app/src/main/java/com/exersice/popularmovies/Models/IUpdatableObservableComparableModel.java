package com.exersice.popularmovies.Models;

import com.exersice.popularmovies.Models.IComparableModel;
import com.exersice.popularmovies.Models.IObservableModel;
import com.exersice.popularmovies.Models.IUpdatableModel;

public interface IUpdatableObservableComparableModel<T> extends IUpdatableModel<T>, IObservableModel<T>, IComparableModel<T> { }
