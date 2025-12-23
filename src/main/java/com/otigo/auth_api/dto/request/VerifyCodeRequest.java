package com.otigo.auth_api.dto.request;
import com.otigo.auth_api.entity.enums.UserRole;

public class VerifyCodeRequest {
    private String email;
    private String code;
    private UserRole role;

    // Getter ve Setter'lar
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}