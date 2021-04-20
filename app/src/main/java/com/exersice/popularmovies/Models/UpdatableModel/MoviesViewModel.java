package com.exersice.popularmovies.Models.UpdatableModel;

import android.icu.util.ULocale;
import android.os.Parcelable;
import android.util.Log;

import com.exersice.popularmovies.TMDbUtils.TMDbMovie;
import com.exersice.popularmovies.TMDbUtils.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

public class MoviesViewModel extends AbstractModelStreamComparable<TMDbMovie> {
    private static final String TAG = MoviesViewModel.class.getSimpleName();

    protected interface StreamComparator<DT> extends IFetcher.SynchronousSimpleStream<DT>, Comparator<DT> {
        @Override
        default int compare(DT o1, DT o2) { return 0; }
    }


    private final static int READS_ON_FETCH = 3;
    private final static int READS_TOTAL_FETCH = Integer.MAX_VALUE;


    protected abstract static class MovieStreamComparator implements StreamComparator<TMDbMovie> {
        int curPage = 1;
        int lastPagesTotal = 0;

        String language = Locale.getDefault().getLanguage();
        String region = Locale.getDefault().getCountry();


        public MovieStreamComparator() {
            Log.v(TAG, "movies stream is initialized");
        }

        @Override
        public boolean available() { return curPage == 1 || curPage <= lastPagesTotal; }

        @Override
        public void reset() { curPage = 1; }
    }

    public final StreamComparator<TMDbMovie> POPULARITY_COMPARATOR = new MovieStreamComparator() {
        @Override
        public int compare(TMDbMovie o1, TMDbMovie o2) {
            return o2.getPopularity() - o1.getPopularity();
        }

        @Override
        public TMDbMovie[] read() throws IOException {
            Log.v(TAG, "begin read at page: " + curPage);

            Utils.TMDbPopularResponse response = Utils.getPopular(language, curPage, region);
            lastPagesTotal = response.getTotalPages();
            curPage++;
            return response.getResults();
        }


    };

    public final StreamComparator<TMDbMovie> RATING_COMPARATOR = new MovieStreamComparator() {
        @Override
        public int compare(TMDbMovie o1, TMDbMovie o2) {
            return o2.getVoteAverage() - o1.getVoteAverage();
        }

        @Override
        public TMDbMovie[] read() throws IOException {
            Log.v(TAG, "begin read at page: " + curPage);

            Utils.TMDbTopRatedResponse response = Utils.getTopRated(language, curPage, region);
            lastPagesTotal = response.getTotalPages();
            curPage++;
            return response.getResults();
        }
    };


    @Override
    protected void initDataFetcher() {
        setFetcherParams(POPULARITY_COMPARATOR, READS_ON_FETCH, READS_TOTAL_FETCH);
    }

}
