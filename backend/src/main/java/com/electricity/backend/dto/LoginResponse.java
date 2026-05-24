package com.electricity.backend.dto;

public class LoginResponse {
    private String token;
    private String userId;
    private String role;
    private String fullName;
    private String email;
    private String customerId;
    private boolean isFirstLogin;

    public LoginResponse() {}

    public LoginResponse(String token, String userId, String role, String fullName, String email, String customerId, boolean isFirstLogin) {
        this.token = token;
        this.userId = userId;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.customerId = customerId;
        this.isFirstLogin = isFirstLogin;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public boolean isFirstLogin() { return isFirstLogin; }
    public void setFirstLogin(boolean firstLogin) { isFirstLogin = isFirstLogin; }
}
