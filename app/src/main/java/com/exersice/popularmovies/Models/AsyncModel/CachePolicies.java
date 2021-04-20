package com.exersice.popularmovies.Models.AsyncModel;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class CachePolicies {
    public static <T> AbstractCacheModel.CachePolicy<T> newListCache() {
        return new AbstractCacheModel.CachePolicy<T>() {
            List<T> cache = Collections.synchronizedList(new LinkedList<>());

            @Override
            public T get(int index) {
                return cache.get(index);
            }

            @Override
            public int size() {
                return cache.size();
            }

            @Override
            public void addAll(Collection<T> c) {
                cache.addAll(c);
            }

            @Override
            public void clear() {
                cache.clear();
            }

            @Override
            public void remove(int index) {
                cache.remove(index);
            }
        };
    }

}
