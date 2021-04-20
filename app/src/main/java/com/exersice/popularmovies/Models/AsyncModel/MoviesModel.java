package com.exersice.popularmovies.Models.AsyncModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.exersice.popularmovies.Models.IUpdatableObservableComparableModel;
import com.exersice.popularmovies.Models.UpdatableModel.MoviesViewModel;
import com.exersice.popularmovies.TMDbUtils.TMDbMovie;
import com.exersice.popularmovies.TMDbUtils.Utils;

import java.io.IOException;
import java.util.Locale;

public class MoviesModel extends ComparableSourceCacheModel<TMDbMovie> implements IUpdatableObservableComparableModel<TMDbMovie> {
    private final static int READS_TOTAL_FETCH = Integer.MAX_VALUE;

    @Override
    public void observe(LifecycleOwner owner, UpdateObserver observer) {
        observe(owner, (CacheObserver) observer::onUpdate);
    }

    protected static abstract class AbstractSource implements Source<TMDbMovie> {
        int curPage = 1;
        int lastPagesTotal = 0;

        String language = Locale.getDefault().getLanguage();
        String region = Locale.getDefault().getCountry();

        @Override
        public boolean available() {
            return lastPagesTotal == 0 || curPage <= Math.min(lastPagesTotal, READS_TOTAL_FETCH);
        }

        @Override
        public void reset() {
            curPage = 1;
        }
    }

    public final Source<TMDbMovie> POPULARITY_COMPARATOR = new AbstractSource() {
        @Override
        public TMDbMovie[] next() throws IOException {
            Utils.TMDbPopularResponse response = Utils.getPopular(language, curPage, region);
            curPage++;
            lastPagesTotal = response.getTotalPages();

            return response.getResults();
        }
    };

    public final Source<TMDbMovie> RATING_COMPARATOR = new AbstractSource() {
        @Override
        public TMDbMovie[] next() throws IOException {
            Utils.TMDbTopRatedResponse response = Utils.getTopRated(language, curPage, region);
            curPage++;
            lastPagesTotal = response.getTotalPages();

            return response.getResults();
        }
    };

    public MoviesModel(CachePolicy<TMDbMovie> cachePolicy, ObservablePolicy observablePolicy, AsyncWorker worker, boolean strictCache) {
        super(cachePolicy, observablePolicy, worker, strictCache);
    }

    @Override
    protected Source<TMDbMovie> initDefaultSource() {
        return POPULARITY_COMPARATOR;
    }

    @Override
    protected void releaseAll() throws IOException {
        POPULARITY_COMPARATOR.close();
        RATING_COMPARATOR.close();
    }
}
