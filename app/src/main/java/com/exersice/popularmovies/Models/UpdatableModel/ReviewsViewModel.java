package com.exersice.popularmovies.Models.UpdatableModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.exersice.popularmovies.TMDbUtils.TMDbReview;
import com.exersice.popularmovies.TMDbUtils.Utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

public class ReviewsViewModel extends AbstractModelSelfComparable<TMDbReview> {
    private final static int READS_ON_FETCH = 3;
    private final static int READS_TOTAL_FETCH = Integer.MAX_VALUE;

    private final int movieId;


    public ReviewsViewModel(int movieId) {
        super();
        this.movieId = movieId;
    }


    @Override
    protected void initDataFetcher() {
        setFetcherParams(new IFetcher.SynchronousSimpleStream<TMDbReview>() {
            int curPage = 1;
            int lastPagesTotal = 0;

            String language = Locale.getDefault().getLanguage();

            @Override
            public boolean available() { return curPage == 1 || curPage <= lastPagesTotal; }

            @Override
            public void reset() { curPage = 1; }

            @Override
            public TMDbReview[] read() throws IOException {
                Utils.TMDbReviewsResponse response = Utils.getReviews(movieId, language, curPage);
                lastPagesTotal = response.getTotalPages();
                curPage++;
                return response.getResults();
            }
        }, READS_ON_FETCH, READS_TOTAL_FETCH);
    }


    public static class NewInstanceReviewsFactory implements ViewModelProvider.Factory {
        private final int movieId;

        public NewInstanceReviewsFactory(int movieId) {
            this.movieId = movieId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            T modelInstance = null;
            try {
                modelInstance = modelClass.getConstructor(int.class).newInstance(movieId);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (modelInstance instanceof ReviewsViewModel) {
                return modelInstance;
            }
            else {
                throw new RuntimeException("Class<" + modelClass.getSimpleName() + "> cannot be instantiated by " + getClass().getSimpleName());
            }
        }
    }
}
