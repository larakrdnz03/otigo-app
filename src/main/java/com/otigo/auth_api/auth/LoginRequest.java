package com.otigo.auth_api.auth;

// Bu sınıf jakarta.validation.constraints.Email ve NotEmpty gibi importları da içerebilir

public class LoginRequest {
    
    private String email;
    private String password;

    // Getter ve Setter'lar
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}