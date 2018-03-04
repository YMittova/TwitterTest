package com.example.twittertest.provider;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {

    @Headers({"Authorization: Basic QWFHdkRVSFRlb2h2MVZFZnNRNENscFJ6RzpoRHRlZ0RKMXE2QndCU3I4YmdJVktBam5uNnoxaWhrcDIzREVkbFJzNEpzMHFmSkV3RA==",
        "Content-Type: application/x-www-form-urlencoded;charset=UTF-8"})
    @POST("oauth2/token")
    Call<String> getAuthToken(@Body String body);

    @GET("1.1/search/tweets.json?filter:images")
    Call<String> getTweets(final @HeaderMap Map<String, String> headers,
                           final @Query("q") String query,
                           final @Query("count") int count,
                           final @Query("include_entities") boolean includeEntities);
}
