package com.example.twittertest.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.util.List;

public interface TwitterPhotoPresenter {

    void onStart();

    void onStop();

    void searchTweets(final String query);

    void clearSearch();

    void onFullScreenPictureClosed();

    interface View {

        void showAuthError(final @NonNull String error);

        void showSuccessfulAuth();

        void updateLoadingView(final @StringRes int stringRes, final boolean visible);

        void showPictures(final @NonNull List<String> imageUrls);

        void showPictureFullScreen(final String url);

        void closeFullScreenPicture();

        void clearSearch();
    }
}
