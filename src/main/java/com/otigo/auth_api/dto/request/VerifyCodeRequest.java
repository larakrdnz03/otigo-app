package com.otigo.auth_api.dto.request;

import com.otigo.auth_api.entity.enums.UserRole;

public class VerifyCodeRequest {
    private String email;
    private String code;
    private UserRole role;
    private String firstName;
    private String lastName;
    private String parentPattern;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getParentPattern() { return parentPattern; }
    public void setParentPattern(String parentPattern) { this.parentPattern = parentPattern; }
}