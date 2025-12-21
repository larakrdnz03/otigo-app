package com.otigo.auth_api.auth;

public class RefreshTokenRequest {

    private String refreshToken;

    // Getter ve Setter
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}