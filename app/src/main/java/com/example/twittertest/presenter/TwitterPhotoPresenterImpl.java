package com.example.twittertest.presenter;

import android.support.annotation.NonNull;

import com.example.twittertest.R;
import com.example.twittertest.provider.TwitterPhotoDataProvider;

public class TwitterPhotoPresenterImpl implements TwitterPhotoPresenter, TwitterPhotoDataProvider.OnDataUpdatedListener, OnPictureClickListener {

    private TwitterPhotoDataProvider mProvider;
    private View mView;

    public TwitterPhotoPresenterImpl(final View view) {
        mProvider = new TwitterPhotoDataProvider();
        mView = view;
    }

    public void onStart() {
        mProvider.addDataUpdateListener(this);
        if (mProvider.getStatus() == TwitterPhotoDataProvider.Status.UNLOADED) {
            mProvider.requestAuthorization();
        }
    }

    public void onStop() {
        mProvider.removeDataUpdateListener();
    }

    @Override
    public void searchTweets(String query) {
        mProvider.requestTweets(query);
    }

    @Override
    public void clearSearch() {
        mProvider.clear();
        mView.clearSearch();
    }

    @Override
    public void onFullScreenPictureClosed() {
        mView.closeFullScreenPicture();
    }

    @Override
    public void onDataUpdated() {
        switch (mProvider.getStatus()) {
            case AUTH_LOADING: {
                mView.updateLoadingView(R.string.authorizing_is_in_progress, true);
                break;
            }
            case AUTH_LOADED: {
                mView.updateLoadingView(0, false);
                mView.showSuccessfulAuth();
                break;
            }
            case SEARCH_LOADING: {
                mView.updateLoadingView(R.string.searching_for_tweets, true);
                break;
            }
            case SEARCH_LOADED: {
                mView.updateLoadingView(0, false);
                mView.showPictures(mProvider.getImageUrls());
                break;
            }
            case ERROR: {
                mView.showAuthError(mProvider.getError());
                break;
            }
        }
    }

    @Override
    public void onPictureClicked(final @NonNull String url) {
        mView.showPictureFullScreen(url);
    }
}
