package com.otigo.auth_api.auth;

public class ResetPasswordRequest {
    private String token;
    private String newPassword;

    // Getter ve Setter'lar
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}