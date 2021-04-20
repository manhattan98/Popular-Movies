package com.exersice.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.exersice.popularmovies.TMDbUtils.Utils;
import com.exersice.popularmovies.Views.RatioImageView;
import com.squareup.picasso.Picasso;

import com.exersice.popularmovies.TMDbUtils.*;

import java.io.IOException;
import java.io.Serializable;

public class DetailActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private static final String EXTRA_MOVIE = "EXTRA_MOVIE";
    //private static final String EXTRA_THUMBNAIL = "EXTRA_THUMBNAIL";


    private static final YouTubeUtils.THUMBNAIL_SIZE THUMBNAIL_SIZE = YouTubeUtils.THUMBNAIL_SIZE.hqdefault;

    private static final int TRAILER_IMAGE_RATIO_X = 16;
    private static final int TRAILER_IMAGE_RATIO_Y = 9;

    public static final Utils.POSTER_SIZE POSTER_SIZE = Utils.POSTER_SIZE.w500;

    private TextView mErrorTextView;
    private ViewGroup mMainContentLayout;
    private Toolbar mToolbar;
    private Button mFavoriteButton;
    private ImageView mPosterImageView;
    private TextView mTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mVoteTextView;
    private TextView mVoteBigTextView;
    private TextView mVoteCountTextView;
    private TextView mOverviewTextView;
    private ImageButton mGoToReviewsButton;
    private ViewGroup mRelatedVideosViewGroup;

    private TMDbMovie mCurrentMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Log.v(TAG, "onCreate() is invoked");

        initViews();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // init movie
        mCurrentMovie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        if (savedInstanceState != null) {
            mCurrentMovie = savedInstanceState.getParcelable(EXTRA_MOVIE);
        }

        if (mCurrentMovie != null) {
            // bind views with given movie
            bindViews(mCurrentMovie);
            showContent();
        } else {
            // default behaviour when no extra movie object...
            showError();
        }

        // init reviews button transition
        mGoToReviewsButton.setOnClickListener(v ->
                ReviewsActivity.startReviewsActivity(DetailActivity.this, mCurrentMovie));

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(EXTRA_MOVIE, mCurrentMovie);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState (@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mCurrentMovie = savedInstanceState.getParcelable(EXTRA_MOVIE);
    }

    private void bindViews(TMDbMovie movie) {
        // load trailers
        new QueryVideosTask().execute(movie.getId());

        Picasso.get()
                .load(Utils.getPosterUri(movie.getPosterPath(), POSTER_SIZE))
                .placeholder(R.color.design_default_color_background)
                .into(mPosterImageView);

        mTitleTextView.setText(movie.getTitle());
        mReleaseDateTextView.setText(Utils.formatDateToString(movie));
        mVoteTextView.setText(Utils.formatVoteString(movie.getVoteAverage()));
        mVoteBigTextView.setText(Utils.formatVoteString(movie.getVoteAverage()));
        mVoteCountTextView.setText(String.valueOf(movie.getVoteCount()));
        mOverviewTextView.setText(movie.getOverview());
    }

    private void initViews() {
        mErrorTextView = findViewById(R.id.error_text);
        mMainContentLayout = findViewById(R.id.main_content);
        mToolbar = findViewById(R.id.main_toolbar);
        mFavoriteButton = findViewById(R.id.mark_favorite_button);
        mPosterImageView = findViewById(R.id.poster_image);
        mTitleTextView = findViewById(R.id.title);
        mReleaseDateTextView = findViewById(R.id.year_text);
        mVoteTextView = findViewById(R.id.vote_text);
        mVoteBigTextView = findViewById(R.id.vote_big_text);
        mVoteCountTextView = findViewById(R.id.reviews_count_text);
        mOverviewTextView = findViewById(R.id.overview_text);
        mGoToReviewsButton = findViewById(R.id.go_reviews_button);
        mRelatedVideosViewGroup = findViewById(R.id.related_videos_layout);
    }

    private void showError() {
        mMainContentLayout.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    private void showContent() {
        mErrorTextView.setVisibility(View.INVISIBLE);
        mMainContentLayout.setVisibility(View.VISIBLE);
    }

    public static void startDetailActivity(Activity parentActivity, TMDbMovie movie) {
        Intent intent = new Intent(parentActivity, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, (Parcelable) movie);

        parentActivity.startActivity(intent);
    }


    // -------------------------- AsyncTask for requesting videos associated with TMDbMovie --------------------------

    protected class QueryVideosTask extends AsyncTask<Integer, Void, Utils.TMDbVideosResponse> {
        @Override
        protected Utils.TMDbVideosResponse doInBackground(Integer... integers) {
            try {
                return Utils.getVideos(integers[0]);
            } catch (IOException E) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Utils.TMDbVideosResponse tmDbVideosResponse) {
            if (tmDbVideosResponse != null) {
                for (TMDbVideo video : tmDbVideosResponse.getResults()) {
                    if (video.getSite().equals("YouTube")) {
                        View relatedVideoView = LayoutInflater.from(DetailActivity.this).inflate(R.layout.item_video, mRelatedVideosViewGroup, false);

                        RatioImageView relatedVideoImage = relatedVideoView.findViewById(R.id.video_thumbnail_image);
                        TextView relatedVideoText = relatedVideoView.findViewById(R.id.video_name_text);

                        // init image scale ratio for RatioImageView
                        relatedVideoImage.setRatioX(TRAILER_IMAGE_RATIO_X);
                        relatedVideoImage.setRatioY(TRAILER_IMAGE_RATIO_Y);

                        // bind views with related videos data
                        final Uri thumbnailUri = YouTubeUtils.getImageUri(video.getKey(), THUMBNAIL_SIZE);
                        Log.v(TAG, thumbnailUri.toString());

                        // load image thumbnail into corresponding ImageView
                        Picasso.get()
                                .load(thumbnailUri)
                                .placeholder(R.color.design_default_color_surface)
                                .into(relatedVideoImage);
                        relatedVideoText.setText(video.getName());

                        relatedVideoView.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, YouTubeUtils.getWatchUri(video.getKey()));
                            if (intent.resolveActivity(getPackageManager()) != null)
                                startActivity(intent);
                        });

                        mRelatedVideosViewGroup.addView(relatedVideoView);
                    }
                }
            }
        }
    }

}