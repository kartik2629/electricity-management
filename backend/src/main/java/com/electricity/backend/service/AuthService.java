package com.electricity.backend.service;

import com.electricity.backend.dto.*;
import com.electricity.backend.entity.*;
import com.electricity.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private ConnectionRequestRepository connectionRequestRepository;

    @Autowired
    private SessionTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse registerCustomer(RegisterRequest request) {
        // 1. Password Match check
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        // 2. Validate Consumer Number exists
        Consumer consumer = consumerRepository.findByConsumerNo(request.getConsumerNo())
                .orElseThrow(() -> new IllegalArgumentException("Please enter a valid Consumer Number."));

        // 3. Validate Consumer is not already registered/claimed
        if (consumer.getCustomer() != null) {
            throw new IllegalArgumentException("Consumer Number is already registered.");
        }

        // 4. Validate unique User ID
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("User ID already exists. Please choose a different User ID.");
        }

        // 5. Validate unique Email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        // 6. Create User
        User user = new User(
            request.getUserId(),
            passwordEncoder.encode(request.getPassword()),
            request.getEmail(),
            request.getMobileNumber(),
            request.getFullName(),
            "CUSTOMER"
        );
        user.setFirstLogin(true);
        user.setStatus("PENDING_APPROVAL");
        user = userRepository.save(user);

        // 7. Create Customer with Random Customer ID
        String randomCustomerId = "CUST-" + String.format("%06d", (int)(Math.random() * 1000000));
        Customer customer = new Customer(randomCustomerId, user);
        customer = customerRepository.save(customer);

        // 8. Update consumer details but DO NOT link to customer yet
        consumer.setAddress(request.getAddress());
        consumer.setContactPhone(request.getMobileNumber());
        consumer.setContactEmail(request.getEmail());
        consumer.setCustomerType(request.getCustomerType().toUpperCase());
        consumer.setElectricalSection(request.getElectricalSection().toUpperCase());
        consumerRepository.save(consumer);

        // 9. Create Connection Request for Admin approval
        String requestId = "REQ-" + java.time.LocalDate.now().getYear() + "-" + String.format("%06d", (int)(Math.random() * 1000000));
        ConnectionRequest connReq = new ConnectionRequest(requestId, user, request.getConsumerNo(), "PENDING");
        connectionRequestRepository.save(connReq);

        return new RegisterResponse(customer.getCustomerId(), user.getFullName(), user.getEmail());
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus()) && !"PENDING_APPROVAL".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalArgumentException("User account is inactive or rejected.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        // Generate session token
        String tokenStr = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(24);
        SessionToken token = new SessionToken(tokenStr, user, expiry);
        tokenRepository.save(token);

        String customerId = null;
        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            Customer customer = customerRepository.findByUserUserId(user.getUserId()).orElse(null);
            if (customer != null) {
                customerId = customer.getCustomerId();
            }
        }

        return new LoginResponse(
            tokenStr,
            user.getUserId(),
            user.getRole(),
            user.getFullName(),
            user.getEmail(),
            customerId,
            user.isFirstLogin(),
            user.getStatus()
        );
    }

    @Transactional
    public void logout(String tokenStr) {
        tokenRepository.findById(tokenStr).ifPresent(token -> tokenRepository.delete(token));
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request, User currentUser) {
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        currentUser.setFirstLogin(false);
        userRepository.save(currentUser);
    }
}
