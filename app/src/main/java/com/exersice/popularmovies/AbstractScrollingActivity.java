package com.exersice.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.exersice.popularmovies.Models.IUpdatableObservableComparableModel;

import java.util.Comparator;


public abstract class AbstractScrollingActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();

    private static final String EXTRA_RECYCLER_STATE = "EXTRA_RECYCLER_STATE";
    private static final String EXTRA_ACTION_STATE = "EXTRA_ACTION_STATE";


    protected static final String ERROR_NULL_ADAPTER = "adapter is not present!";
    protected static final String ERROR_NULL_LAYOUT_MANAGER = "layout manager is not present!";


    private TextView mErrorText;

    private ViewGroup mErrorLayout;
    private Button mRetryButton;

    private ProgressBar mProgressBar;

    private ViewGroup mMainLayout;
    private Toolbar mMainToolbar;
    private RecyclerView mMainRecycler;
    private TextView mEmptyText;


    //private long mLastMilliseconds;
    //private int mLastCount;


    //private AbstractViewModel mViewModel;
    private IUpdatableObservableComparableModel<?> mViewModel;

    private ActionState mActionState = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abstract_scrolling);

        Log.v(TAG, "onCreate() is invoked");

        initViews();

        if (savedInstanceState != null)
            restoreActionState(savedInstanceState);

        setSupportActionBar(mMainToolbar);

        initViewModel(savedInstanceState);

        prepareEmptyText(mEmptyText);


        // post-update model behavior
        mViewModel.observe(this, throwable -> {
            Log.v(TAG, "model is updated", throwable);

            // fail case
            if (throwable != null) {
                displayErrorAction();
                Log.e(TAG, "update error occurred!", throwable);
            }
            // success case
            else {
                if (mViewModel.size() > 0)
                    displayMainContent();
                else
                    displayEmptyText();

                // data set changed notification...
                if (mMainRecycler.getAdapter() != null)
                    mMainRecycler.getAdapter().notifyDataSetChanged();
                else
                    Log.e(TAG, ERROR_NULL_ADAPTER);
            }
        });


        // retry button behavior
        mRetryButton.setOnClickListener(v -> {
            displayProgressBar();
            mViewModel.fetch();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mMainRecycler.getLayoutManager() != null)
            outState.putParcelable(EXTRA_RECYCLER_STATE, mMainRecycler.getLayoutManager().onSaveInstanceState());
        else
            Log.e(TAG, ERROR_NULL_LAYOUT_MANAGER);

        outState.putString(EXTRA_ACTION_STATE, getActionState().toString());
    }

    @Override
    protected void onRestoreInstanceState (@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (mMainRecycler.getLayoutManager() != null)
            mMainRecycler.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(EXTRA_RECYCLER_STATE));
        else
            Log.e(TAG, ERROR_NULL_LAYOUT_MANAGER);

        restoreActionState(savedInstanceState);
    }


    protected void restoreActionState(@NonNull Bundle bundle) {
        String rState = bundle.getString(EXTRA_ACTION_STATE);

        // ERROR_TEXT, ERROR_ACTION, MAIN_CONTENT, PROGRESS_BAR, EMPTY_TEXT
        switch (rState) {
            case "ERROR_TEXT":
                displayErrorText();
                break;
            case "ERROR_ACTION":
                displayErrorAction();
                break;
            case "MAIN_CONTENT":
                displayMainContent();
                break;
            case "PROGRESS_BAR":
                displayProgressBar();
                break;
            case "EMPTY_TEXT":
                displayEmptyText();
                break;
        }
    }


    protected void initViews() {
        mErrorText = findViewById(R.id.error_text);
        mErrorLayout = findViewById(R.id.error_layout);
        mRetryButton = findViewById(R.id.retry_button);
        mProgressBar = findViewById(R.id.main_progress_bar);
        mMainLayout = findViewById(R.id.main_layout);
        mMainToolbar = findViewById(R.id.main_toolbar);
        mMainRecycler = findViewById(R.id.main_recycler);
        mEmptyText = findViewById(R.id.empty_text);
    }

    protected void displayErrorText() {
        mActionState = ActionState.ERROR_TEXT;

        mProgressBar.setVisibility(View.INVISIBLE);
        mMainLayout.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.VISIBLE);
    }

    protected void displayErrorAction() {
        mActionState = ActionState.ERROR_ACTION;

        mProgressBar.setVisibility(View.INVISIBLE);
        mMainLayout.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.VISIBLE);
    }

    protected void displayMainContent() {
        mActionState = ActionState.MAIN_CONTENT;

        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorLayout.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
        mMainLayout.setVisibility(View.VISIBLE);
        mEmptyText.setVisibility(View.INVISIBLE);
        mMainRecycler.setVisibility(View.VISIBLE);
    }

    protected void displayProgressBar() {
        mActionState = ActionState.PROGRESS_BAR;

        mErrorLayout.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
        mMainLayout.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void displayEmptyText() {
        mActionState = ActionState.EMPTY_TEXT;

        mErrorLayout.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mMainLayout.setVisibility(View.VISIBLE);
        mMainRecycler.setVisibility(View.INVISIBLE);
        mEmptyText.setVisibility(View.VISIBLE);
    }


    protected float toDIP(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    protected float toSP(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }


    protected ActionState getActionState() {
        return mActionState;
    }

    protected RecyclerView getMainRecycler() {
        return mMainRecycler;
    }

    protected void setViewModel(IUpdatableObservableComparableModel<?> viewModel) {
        mViewModel = viewModel;
    }

    protected void updateModelIfLast(int position) {
        //mLastMilliseconds = milliseconds;
        //mLastCount = count;

        if (position == mViewModel.size() - 1)
            if (mViewModel.available() && getActionState() == ActionState.MAIN_CONTENT)
                mViewModel.fetch();
    }

    protected void initialModelUpdate() {
        //mLastMilliseconds = milliseconds;
        //mLastCount = count;

        if (mViewModel.size() == 0)
            if (mViewModel.available()) {
                displayProgressBar();
                mViewModel.fetch();
            }
    }

    protected void setComparator(Comparator comparator) {
        displayProgressBar();
        mMainRecycler.getLayoutManager().scrollToPosition(0);
        mViewModel.setComparator(comparator);
    }


    // -------------------------------------------------------------------------------------------------------------------


    protected enum ActionState {
        ERROR_TEXT, ERROR_ACTION, MAIN_CONTENT, PROGRESS_BAR, EMPTY_TEXT
    }


    // -------------------------------------------------------------------------------------------------------------------


    /**
     * template method that by contract must call setViewModel() to initialize view model
     */
    protected abstract void initViewModel(Bundle savedInstanceState);

    protected abstract void prepareEmptyText(TextView emptyText);

}
