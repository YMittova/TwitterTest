package com.example.twittertest.provider;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {

    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("access_token")
    private String accessToken;

    public AuthResponse(String tokenType, String accessToken) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
