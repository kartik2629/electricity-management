package com.electricity.backend.dto;

public class RegisterResponse {
    private String customerId;
    private String customerName;
    private String email;

    public RegisterResponse() {}

    public RegisterResponse(String customerId, String customerName, String email) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
