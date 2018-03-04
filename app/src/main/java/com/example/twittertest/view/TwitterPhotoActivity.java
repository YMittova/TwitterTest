package com.example.twittertest.view;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.twittertest.R;
import com.example.twittertest.presenter.TwitterPhotoPresenter;
import com.example.twittertest.presenter.TwitterPhotoPresenterImpl;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TwitterPhotoActivity extends AppCompatActivity implements TwitterPhotoPresenter.View {

    private static final int SEARCH_INPUT_TEXT_DELAY = 500;
    private static final int MIN_SEARCH_TEXT_SIZE = 2;

    @DimenRes
    private static final int sSpacing = R.dimen.element_spacing;
    @DimenRes
    private static final int sItemWidth = R.dimen.element_size;

    private View mLoadingContainer;
    private TextView mLoadingText;
    private View mContentContainer;
    private View mClearSearchButton;
    private RecyclerView mPhotos;
    private View mSearchResultsEmpty;
    private EditText mSearchEdit;
    private Dialog mFullScreenDialog;

    private TwitterPhotoPresenterImpl mPresenter;
    private PhotoAdapter mAdapter;
    private String mSearchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_photo);

        mLoadingContainer = findViewById(R.id.twitterPhoto_loadingContainer);
        mLoadingText = (TextView) findViewById(R.id.twitterPhoto_loadingText);
        mContentContainer = findViewById(R.id.twitterPhoto_contentContainer);
        mClearSearchButton = findViewById(R.id.twitterPhoto_clearSearch);
        mPhotos = (RecyclerView) findViewById(R.id.twitterPhoto_photosList);
        mSearchResultsEmpty = findViewById(R.id.twitterPhoto_searchResultsEmpty);
        mSearchEdit = (EditText) findViewById(R.id.twitterPhoto_searchQuery);
        mSearchEdit.addTextChangedListener(mTextWatcher);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        mPresenter = new TwitterPhotoPresenterImpl(this);

        final int spacing = getResources().getDimensionPixelSize(sSpacing);
        final int itemWidth = getResources().getDimensionPixelSize(sItemWidth);
        final int colNum = (screenWidth - spacing) / (itemWidth + spacing);
        final int space = (screenWidth - colNum * itemWidth) / (colNum + 1);

        mPhotos.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, colNum);
        mPhotos.setLayoutManager(layoutManager);
        mPhotos.addItemDecoration(new PhotoItemDecoration(colNum, space));
        mAdapter = new PhotoAdapter(mPresenter);
        mPhotos.setAdapter(mAdapter);

        mClearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clearSearch();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void showAuthError(@NonNull String error) {
        Toast.makeText(this, "Authorization failed with error: " + error, Toast.LENGTH_LONG).show();
        Log.e("TAG", "Authorization failed with error: " + error);
    }

    @Override
    public void showSuccessfulAuth() {
        mContentContainer.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.successfully_authorized, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateLoadingView(final @StringRes int stringRes, final boolean visible) {
        if (visible) {
            mLoadingText.setText(stringRes);
        }
        mLoadingContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showPictures(@NonNull List<String> imageUrls) {
        if (imageUrls.isEmpty()) {
            mSearchResultsEmpty.setVisibility(View.VISIBLE);
            mPhotos.setVisibility(View.GONE);
        }
        else {
            mSearchResultsEmpty.setVisibility(View.GONE);
            mAdapter.setPhotoUrls(imageUrls);
            mAdapter.notifyDataSetChanged();
            mPhotos.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showPictureFullScreen(String url) {
        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Material_NoActionBar_Fullscreen);
        mFullScreenDialog.setContentView(R.layout.dialog_image_fullscreen);

        ImageView imageView = (ImageView) mFullScreenDialog.findViewById(R.id.imageFullscreen_image);
        Picasso.with(getApplicationContext())
            .load(url)
            .into(imageView);
        mFullScreenDialog.show();

        View closeButton = mFullScreenDialog.findViewById(R.id.imageFullscreen_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onFullScreenPictureClosed();
            }
        });
    }

    @Override
    public void closeFullScreenPicture() {
        if (mFullScreenDialog != null) {
            mFullScreenDialog.dismiss();
        }
    }

    @Override
    public void clearSearch() {
        mAdapter.clear();
        mSearchEdit.setText("");
        mClearSearchButton.setVisibility(View.INVISIBLE);
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        private Handler mHandler = new Handler(Looper.getMainLooper());
        private Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mSearchString == null) {
                    return;
                }
                mPresenter.searchTweets(mSearchString);
            }
        };

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable text) {
            String searchText = text.toString();
            if (TextUtils.isEmpty(mSearchString) && searchText.length() == 1) {
                // just started entering a text, show "clear text" button
                mClearSearchButton.setVisibility(View.VISIBLE);
            }
            else if (TextUtils.isEmpty(searchText) && mSearchString != null && mSearchString.length() == 1) {
                mClearSearchButton.setVisibility(View.INVISIBLE);
            }
            mSearchString = searchText;
            if (TextUtils.isEmpty(searchText) || searchText.length() <= MIN_SEARCH_TEXT_SIZE) {
                mSearchResultsEmpty.setVisibility(View.GONE); // hide "no results" view
                mPhotos.setVisibility(View.GONE);
                mHandler.removeCallbacks(mRunnable);
                return;
            }
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, SEARCH_INPUT_TEXT_DELAY);
        }
    };
}
