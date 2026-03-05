package com.otigo.auth_api.auth;

public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private Long userId; // YENİ
    private String role; // YENİ (EXPERT veya PARENT)
    // 👇 YENİ EKLENENLER (Kerem istedi)
    private String firstname;
    private String lastname;


    public LoginResponse() {
    }

    /*public LoginResponse(String accessToken, String refreshToken, Long userId, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.role = role;
    }*/
   // Constructor'ı güncelledik
    public LoginResponse(String accessToken, String refreshToken, Long userId, String role, String firstname, String lastname) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.role = role;
        this.firstname = firstname;
        this.lastname = lastname;
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // YENİ Getter ve Setter'lar
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    // 👇 YENİ GETTER SETTERLAR
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
}