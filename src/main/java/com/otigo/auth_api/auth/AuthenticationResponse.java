package com.otigo.auth_api.auth;

public class AuthenticationResponse {

    private String accessToken;

    // --- Boş Constructor ---
    public AuthenticationResponse() {
    }

    // --- Dolu Constructor ---
    public AuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    // --- Getter ve Setter ---
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}