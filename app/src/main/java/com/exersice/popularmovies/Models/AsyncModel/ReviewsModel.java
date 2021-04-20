package com.exersice.popularmovies.Models.AsyncModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.exersice.popularmovies.Models.IUpdatableObservableComparableModel;
import com.exersice.popularmovies.TMDbUtils.TMDbReview;
import com.exersice.popularmovies.TMDbUtils.Utils;

import java.io.IOException;
import java.util.Comparator;
import java.util.Locale;

public class ReviewsModel extends AbstractCacheModel<TMDbReview> implements IUpdatableObservableComparableModel<TMDbReview> {
    private final static int READS_TOTAL_FETCH = Integer.MAX_VALUE;

    private final int movieId;

    private int curPage = 1;
    private int lastPagesTotal = 0;

    private String language = Locale.getDefault().getLanguage();

    public ReviewsModel(CachePolicy<TMDbReview> cachePolicy, ObservablePolicy<?> observablePolicy, AsyncWorker worker, boolean strictCache, int movieId) {
        super(cachePolicy, observablePolicy, worker, strictCache);

        this.movieId = movieId;
    }

    @Override
    protected Boolean addSynchronous(TMDbReview element) throws IOException {
        return false;
    }

    @Override
    protected Boolean removeSynchronous(int index) throws IOException {
        return false;
    }

    @Override
    protected TMDbReview[] fetchSynchronous() throws IOException {
        Utils.TMDbReviewsResponse response = Utils.getReviews(movieId, language, curPage);
        curPage++;
        lastPagesTotal = response.getTotalPages();

        return response.getResults();
    }

    @Override
    protected void releaseAll() throws IOException {
    }

    @Override
    public boolean available() {
        return lastPagesTotal == 0 || curPage <= Math.min(lastPagesTotal, READS_TOTAL_FETCH);
    }

    @Override
    public void setComparator(Comparator<TMDbReview> comparator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setComparator(Comparator<TMDbReview> comparator, long milliseconds, int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<TMDbReview> getCurrentComparator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void observe(LifecycleOwner owner, UpdateObserver observer) {
        observe(owner, (CacheObserver) observer::onUpdate);
    }


    public static class ReviewsModelNewInstanceFactory extends CacheModelNewInstanceFactory {
        private final int movieId;

        public ReviewsModelNewInstanceFactory(CachePolicy cachePolicy, ObservablePolicy observablePolicy, AsyncWorker worker, boolean strictCache, int movieId) {
            super(cachePolicy, observablePolicy, worker, strictCache);

            this.movieId = movieId;
        }

        @Override
        protected Class<?>[] addParameterTypes() {
            return new Class<?>[] { int.class };
        }

        @Override
        protected Object[] addParameterArgs() {
            return new Object[] { movieId };
        }
    }
}
