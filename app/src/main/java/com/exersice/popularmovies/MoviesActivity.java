package com.exersice.popularmovies;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.exersice.popularmovies.Models.AsyncModel.AbstractCacheModel;
import com.exersice.popularmovies.Models.AsyncModel.AndroidAsyncWorker;
import com.exersice.popularmovies.Models.AsyncModel.CachePolicies;
import com.exersice.popularmovies.Models.AsyncModel.MoviesModel;
import com.exersice.popularmovies.Models.AsyncModel.ObservablePolicies;
import com.exersice.popularmovies.Models.UpdatableModel.MoviesViewModel;
import com.exersice.popularmovies.TMDbUtils.TMDbMovie;
import com.exersice.popularmovies.TMDbUtils.Utils;
import com.exersice.popularmovies.Views.RatioImageView;
import com.squareup.picasso.Picasso;

import java.util.Comparator;

public class MoviesActivity extends AbstractScrollingActivity {
    private static final boolean IS_DEBUG = false;


    //protected MoviesViewModel mMoviesViewModel;
    protected MoviesModel mMoviesViewModel;

    protected Comparator<TMDbMovie> POPULARITY_COMPARATOR;
    protected Comparator<TMDbMovie> RATING_COMPARATOR;


    public final static Utils.POSTER_SIZE POSTER_SIZE = Utils.POSTER_SIZE.w185;
    public final static int COLUMNS_NUMBER = 3;
    public final static int WIDTH_MULTIPLIER = 2;
    public final static int HEIGHT_MULTIPLIER = 3;

    public final static int PADDING_LEFT = 16;
    public final static int PADDING_RIGHT = 16;


    public final static long UPDATE_TIMEOUT_MILLISECONDS = 10000;
    public final static int UPDATE_COUNT = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMoviesViewModel.setDefaultMillisAndCount(UPDATE_TIMEOUT_MILLISECONDS, UPDATE_COUNT);

        getMainRecycler().setAdapter(new MoviesAdapter());
        getMainRecycler().setLayoutManager(new GridLayoutManager(this, COLUMNS_NUMBER));

        getMainRecycler().setPadding((int) toDIP(PADDING_LEFT), 0, (int) toDIP(PADDING_RIGHT), 0);
        getMainRecycler().setClipToPadding(false);

        setTitle(getResources().getString(R.string.app_name));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        switch (itemId) {
            case R.id.action_sort_popularity:
                setComparator(POPULARITY_COMPARATOR);
                return true;
            case R.id.action_sort_rating:
                setComparator(RATING_COMPARATOR);
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }


    @Override
    protected void initViewModel(Bundle savedInstanceState) {
        //mMoviesViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MoviesViewModel.class);
        mMoviesViewModel = new ViewModelProvider(this, new AbstractCacheModel.CacheModelNewInstanceFactory(CachePolicies.newListCache(), ObservablePolicies.newLiveDataObserver(), new AndroidAsyncWorker(), true)).get(MoviesModel.class);
        setViewModel(mMoviesViewModel);

        POPULARITY_COMPARATOR = mMoviesViewModel.POPULARITY_COMPARATOR;
        RATING_COMPARATOR = mMoviesViewModel.RATING_COMPARATOR;
    }

    @Override
    protected void prepareEmptyText(TextView emptyText) {
        emptyText.setText(getResources().getText(R.string.movies_empty));
    }


    // -------------------------------------------------------- adapter --------------------------------------------------------


    protected class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {
        public MoviesAdapter() {
            super();

            initialModelUpdate();
        }

        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(MoviesActivity.this).inflate(R.layout.item_movie,parent, false);

            return new MovieHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
            holder.bind(mMoviesViewModel.get(position));

            updateModelIfLast(position);
        }

        @Override
        public int getItemCount() {
            return mMoviesViewModel.size();
        }

        protected class MovieHolder extends RecyclerView.ViewHolder {
            RatioImageView movieImage;
            TextView movieText;
            TextView movieAdditionalText;

            public MovieHolder(@NonNull View itemView) {
                super(itemView);

                movieImage = itemView.findViewById(R.id.movie_poster_image);
                movieText = itemView.findViewById(R.id.movie_title_text);
                movieAdditionalText = itemView.findViewById(R.id.additional_text);

                movieImage.setRatioX(WIDTH_MULTIPLIER);
                movieImage.setRatioY(HEIGHT_MULTIPLIER);
            }

            public void bind(TMDbMovie movie) {
                // set onClick listener to launch detail activity
                this.itemView.setOnClickListener(v ->
                        DetailActivity.startDetailActivity(MoviesActivity.this, movie));

                Uri imgUri = Utils.getPosterUri(movie.getPosterPath(), POSTER_SIZE);

                Log.v(TAG, imgUri.toString());

                Picasso.get()
                        .load(imgUri)
                        .placeholder(R.color.design_default_color_background)
                        //.centerCrop()
                        //.noPlaceholder()
                        .into(movieImage);
                movieText.setText(movie.getTitle());
                movieAdditionalText.setText(String.valueOf(movie.getYear()));

                if (IS_DEBUG) {
                    if (mMoviesViewModel.getCurrentComparator() == POPULARITY_COMPARATOR)
                        movieAdditionalText.setText(String.valueOf(movie.getPopularity()));
                    else if (mMoviesViewModel.getCurrentComparator() == RATING_COMPARATOR)
                        movieAdditionalText.setText(String.valueOf(movie.getVoteAverage()));
                }

            }

        }
    }
}
