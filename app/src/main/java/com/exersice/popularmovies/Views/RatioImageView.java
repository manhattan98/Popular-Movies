package com.exersice.popularmovies.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public class RatioImageView extends androidx.appcompat.widget.AppCompatImageView {
    private final String TAG = getClass().getSimpleName();

    private int mRatioX = 1;
    private int mRatioY = 1;

    private boolean mWidthOrigin = true;

    public void setRatioX(int ratio) {
        mRatioX = ratio;
    }
    public void setRatioY(int ratio) {
        mRatioY = ratio;
    }

    public RatioImageView(Context context) {
        super(context);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        //super(context, attrs);
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        
    }

    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // init image measure width and height
        double SCALE_FACTOR;
        int measureWidth = getMeasuredWidth();
        int measureHeight = getMeasuredHeight();

        if (mWidthOrigin) {
            SCALE_FACTOR = (double) mRatioY / (double) mRatioX;
            measureHeight = (int) (measureWidth * SCALE_FACTOR);
        }
        else {
            SCALE_FACTOR = (double) mRatioX / (double) mRatioY;
            measureWidth = (int) (measureHeight * SCALE_FACTOR);
        }

        Log.d(TAG, "measure values: ");

        Log.d(TAG, "spec width = " + widthMeasureSpec);
        Log.d(TAG, "spec height = " + heightMeasureSpec);

        Log.d(TAG, "calculated width = " + measureWidth);
        Log.d(TAG, "calculated height = " + measureHeight);

        setMeasuredDimension(measureWidth, measureHeight);
    }
}
