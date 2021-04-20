package com.exersice.popularmovies;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exersice.popularmovies.Models.AsyncModel.AndroidAsyncWorker;
import com.exersice.popularmovies.Models.AsyncModel.CachePolicies;
import com.exersice.popularmovies.Models.AsyncModel.ObservablePolicies;
import com.exersice.popularmovies.Models.AsyncModel.ReviewsModel;
import com.exersice.popularmovies.Models.UpdatableModel.ReviewsViewModel;
import com.exersice.popularmovies.TMDbUtils.TMDbMovie;
import com.exersice.popularmovies.TMDbUtils.TMDbReview;

import java.util.LinkedList;
import java.util.List;

public class ReviewsActivity extends AbstractScrollingActivity {

    private static final String EXTRA_MOVIE = "EXTRA_MOVIE";


    public final static long UPDATE_TIMEOUT_MILLISECONDS = 10000;
    public final static int UPDATE_COUNT = 1;


    private TMDbMovie mCurrentMovie;

    //private ReviewsViewModel mReviewsViewModel;
    private ReviewsModel mReviewsViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReviewsViewModel.setDefaultMillisAndCount(UPDATE_TIMEOUT_MILLISECONDS, UPDATE_COUNT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.reviews));


        if (mCurrentMovie == null ) {
            displayErrorText();
        }
        else {
            getMainRecycler().setAdapter(new ReviewsAdapter());
            getMainRecycler().setLayoutManager(new LinearLayoutManager(this));
        }

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_MOVIE, mCurrentMovie);
    }

    @Override
    protected void onRestoreInstanceState (@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mCurrentMovie = savedInstanceState.getParcelable(EXTRA_MOVIE);
    }


    @Override
    protected void initViewModel(Bundle savedInstanceState) {
        mCurrentMovie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        if (savedInstanceState != null)
            mCurrentMovie = savedInstanceState.getParcelable(EXTRA_MOVIE);

        if (mCurrentMovie != null ) {
            //mReviewsViewModel = new ViewModelProvider(this, new ReviewsViewModel.NewInstanceReviewsFactory(mCurrentMovie.getId())).get(ReviewsViewModel.class);
            mReviewsViewModel = new ViewModelProvider(this, new ReviewsModel.ReviewsModelNewInstanceFactory(CachePolicies.newListCache(), ObservablePolicies.newLiveDataObserver(), new AndroidAsyncWorker(), true, mCurrentMovie.getId())).get(ReviewsModel.class);
            setViewModel(mReviewsViewModel);
        }

    }

    @Override
    protected void prepareEmptyText(TextView emptyText) {
        emptyText.setText(getResources().getText(R.string.reviews_empty));
    }


    // ----------------------------------------------------- adapter -----------------------------------------------------


    class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {
        List<Boolean> isExpanded = new LinkedList<>();

        @NonNull
        @Override
        public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(ReviewsActivity.this).inflate(R.layout.item_review, parent, false);
            return new ReviewHolder(itemView);
        }

        public ReviewsAdapter() {
            super();

            initialModelUpdate();
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
            // add new elements if needed
            for (int i = isExpanded.size(); i <= position; i++)
                isExpanded.add(false);

            holder.bind(position);

            updateModelIfLast(position);
        }

        @Override
        public int getItemCount() {
            return mReviewsViewModel.size();
        }

        class ReviewHolder extends RecyclerView.ViewHolder {
            TextView author;
            TextView content;
            //boolean isExpanded = false;
            int maxLines = 4;

            public ReviewHolder(@NonNull View itemView) {
                super(itemView);

                author = itemView.findViewById(R.id.author_text);
                content = itemView.findViewById(R.id.review_text);

                //maxLines = content.getMaxLines();
            }

            public void bind(int position) {
                TMDbReview review = mReviewsViewModel.get(position);

                this.itemView.setOnClickListener(v -> {
                    if (!isExpanded.get(position)) {
                        //maxLines = content.getMaxLines();
                        content.setMaxLines(Integer.MAX_VALUE);
                        isExpanded.set(position, true);
                    }
                    else {
                        content.setMaxLines(maxLines);
                        isExpanded.set(position, false);
                    }
                });
                if (isExpanded.get(position))
                    content.setMaxLines(Integer.MAX_VALUE);
                else
                    content.setMaxLines(maxLines);

                author.setText(review.getAuthor());
                content.setText(review.getContent());
            }
        }

    }

    public static void startReviewsActivity(Activity parentActivity, TMDbMovie movie) {
        Intent intent = new Intent(parentActivity, ReviewsActivity.class);
        intent.putExtra(EXTRA_MOVIE, (Parcelable) movie);

        parentActivity.startActivity(intent);
    }
}
