package com.example.twittertest.provider;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TwitterPhotoDataProvider {

    private static final String BASE_AUTH_URL = "https://api.twitter.com/";
    private static final String BASE_SEARCH_URL = "https://api.twitter.com/1.1/";
    private static final String GRANT_TYPE_TEXT = "grant_type=client_credentials";
    private static final String ACCESS_TOKEN_FIELD = "access_token";
    private static final String AUTH_TOKEN_KEY = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String MEDIA_URL_PATTERN = "\"media_url\":\"";
    private static final int BATCH_SIZE = 50;

    private OnDataUpdatedListener mListener;
    private Status mStatus = Status.UNLOADED;
    private String mError;
    private String mAccessToken;
    private List<String> mImageUrls = new ArrayList<>();
    private final Retrofit mRetrofit;

    public TwitterPhotoDataProvider() {
        mRetrofit = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_AUTH_URL)
            .build();
    }

    public void addDataUpdateListener(final @NonNull OnDataUpdatedListener listener) {
        this.mListener = listener;
    }

    public void removeDataUpdateListener() {
        mListener = null;
    }

    public void requestAuthorization() {
        AuthService service = mRetrofit.create(AuthService.class);
        Call<String> call = service.getAuthToken(GRANT_TYPE_TEXT);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                final String responseString = response.body();
                if (!responseString.contains(ACCESS_TOKEN_FIELD)) {
                    mStatus = Status.ERROR;
                    mError = "Access token not found in response";
                    notifyDataUpdated();
                    return;
                }
                final AuthResponse authResponse = new Gson().fromJson(responseString, AuthResponse.class);
                mAccessToken = authResponse.getAccessToken();
                mStatus = Status.AUTH_LOADED;
                notifyDataUpdated();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mStatus = Status.ERROR;
                mError = t.getLocalizedMessage();
                notifyDataUpdated();
            }
        });
        mStatus = Status.AUTH_LOADING;
        notifyDataUpdated();
    }

    public void clear() {
        mImageUrls.clear();
    }

    public void requestTweets(final String query) {
        AuthService service = mRetrofit.create(AuthService.class);
        final Map<String, String> headers = new HashMap<>();
        headers.put(AUTH_TOKEN_KEY, TOKEN_PREFIX + mAccessToken);
        Call<String> call = service.getTweets(headers, query, BATCH_SIZE, true);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (TextUtils.isEmpty(response.body())) {
                    mStatus = Status.ERROR;
                    mError = "Search response is empty";
                    notifyDataUpdated();
                }

                mImageUrls = extractUrlsFromResponse(response.body());
                mStatus = Status.SEARCH_LOADED;
                notifyDataUpdated();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mStatus = Status.ERROR;
                mError = t.getLocalizedMessage();
                notifyDataUpdated();
            }
        });
        mStatus = Status.SEARCH_LOADING;
        notifyDataUpdated();
    }

    private List<String> extractUrlsFromResponse(final @NonNull String responseString) {
        final List<String> urls = new ArrayList<>();
        Pattern p = Pattern.compile(MEDIA_URL_PATTERN);
        Matcher match = p.matcher(responseString);

        while (match.find()) {
            StringBuilder sb = new StringBuilder();
            int index = match.end();
            while (index < responseString.length()) {
                char character = responseString.charAt(index);
                if (character == '\\') {
                    index++;
                    continue;
                }
                if (character == '"') {
                    break;
                }
                sb.append(character);
                index++;
            }
            urls.add(sb.toString());
        }
        return urls;
    }

    private void notifyDataUpdated() {
        if (mListener != null) {
            mListener.onDataUpdated();
        }
    }

    public Status getStatus() {
        return mStatus;
    }

    public String getError() {
        return mError;
    }

    public List<String> getImageUrls() {
        return mImageUrls;
    }

    public interface OnDataUpdatedListener {
        void onDataUpdated();
    }

    public enum Status {
        UNLOADED,
        AUTH_LOADING, AUTH_LOADED,
        SEARCH_LOADING, SEARCH_LOADED,
        ERROR;
    }
}
