package com.exersice.popularmovies.Models.AsyncModel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ReviewsModelTest {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Test
    public void testCreation() {
        ReviewsModel model = new ReviewsModel.ReviewsModelNewInstanceFactory(
                CachePolicies.newListCache(),
                ObservablePolicies.newLiveDataObserver(),
                new AndroidAsyncWorker(),
                true,
                0).create(ReviewsModel.class);
    }
}