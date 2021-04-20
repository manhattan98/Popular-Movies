package com.exersice.popularmovies.Models;

import java.util.Comparator;

public interface IComparableModel<T> {
    void setDefaultMillisAndCount(long milliseconds, int count);

    void setComparator(Comparator<T> comparator);
    void setComparator(Comparator<T> comparator, long milliseconds, int count);

    Comparator<T> getCurrentComparator();
}
