package com.electricity.backend.dto;

import jakarta.validation.constraints.*;

public class RegisterRequest {

    @NotBlank(message = "Consumer Number is required")
    @Pattern(regexp = "^\\d{13}$", message = "Please enter a valid Consumer Number.")
    private String consumerNo;

    @NotBlank(message = "Full Name is required")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Full Name should accept only characters")
    @Size(max = 50, message = "Full Name cannot exceed 50 characters")
    private String fullName;

    @NotBlank(message = "Address is required")
    @Size(min = 10, message = "Address length must be at least 10 characters")
    private String address;

    @NotBlank(message = "Email is required")
    @Email(message = "Incorrect email format.")
    private String email;

    @NotBlank(message = "Mobile Number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number is invalid.")
    private String mobileNumber;

    @NotBlank(message = "Customer Type is required")
    private String customerType; // RESIDENTIAL, COMMERCIAL

    @NotBlank(message = "Electrical Section is required")
    private String electricalSection; // OFFICE, REGION

    @NotBlank(message = "User ID is required")
    @Size(min = 5, max = 20, message = "User ID must be between 5 and 20 characters")
    private String userId;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    public RegisterRequest() {}

    // Getters and Setters
    public String getConsumerNo() { return consumerNo; }
    public void setConsumerNo(String consumerNo) { this.consumerNo = consumerNo; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }

    public String getElectricalSection() { return electricalSection; }
    public void setElectricalSection(String electricalSection) { this.electricalSection = electricalSection; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
