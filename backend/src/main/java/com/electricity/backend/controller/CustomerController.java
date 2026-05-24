package com.electricity.backend.controller;

import com.electricity.backend.dto.PayRequest;
import com.electricity.backend.dto.TransactionResponse;
import com.electricity.backend.dto.ComplaintRequest;
import com.electricity.backend.entity.*;
import com.electricity.backend.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        Customer customer = customerService.getProfile(user);
        List<Consumer> consumers = customerService.getConsumers(user);

        Map<String, Object> profile = new HashMap<>();
        profile.put("customerId", customer.getCustomerId());
        profile.put("fullName", user.getFullName());
        profile.put("email", user.getEmail());
        profile.put("mobileNumber", user.getMobileNumber());
        
        // Use primary consumer's address as billing address, or default to user mobile number
        String billingAddress = consumers.isEmpty() ? "No address registered" : consumers.get(0).getAddress();
        profile.put("billingAddress", billingAddress);
        profile.put("consumers", consumers);

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/bills")
    public ResponseEntity<List<Bill>> getBills(HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        List<Bill> bills = customerService.getBills(user);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/bills/latest")
    public ResponseEntity<Map<String, Object>> getLatestBill(HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        Bill bill = customerService.getLatestBill(user).orElse(null);

        Map<String, Object> response = new HashMap<>();
        if (bill != null) {
            response.put("billId", bill.getBillId());
            response.put("consumerNo", bill.getConsumer().getConsumerNo());
            response.put("billingPeriod", bill.getBillingPeriod());
            response.put("billDate", bill.getBillDate());
            response.put("dueDate", bill.getDueDate());
            response.put("disconnectionDate", bill.getDisconnectionDate());
            response.put("billAmount", bill.getBillAmount());
            response.put("lateFee", bill.getLateFee());
            response.put("status", bill.getStatus());
            response.put("payableAmount", bill.getBillAmount() + bill.getLateFee());
        } else {
            response.put("message", "No billing history found.");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pay")
    public ResponseEntity<TransactionResponse> payBills(
            @Valid @RequestBody PayRequest request,
            HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        TransactionResponse response = customerService.payBills(request, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionDetails(
            @PathVariable String transactionId,
            HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        TransactionResponse response = customerService.getTransactionDetails(transactionId, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complaints")
    public ResponseEntity<Complaint> registerComplaint(
            @Valid @RequestBody ComplaintRequest request,
            HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        Complaint complaint = customerService.registerComplaint(request, user);
        return ResponseEntity.ok(complaint);
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getComplaints(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        List<Complaint> complaints = customerService.getComplaints(user);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/complaints/search")
    public ResponseEntity<List<Complaint>> searchComplaints(
            @RequestParam(required = false) String complaintId,
            @RequestParam(required = false) String status,
            HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("currentUser");
        List<Complaint> complaints = customerService.searchComplaints(complaintId, status, user);
        return ResponseEntity.ok(complaints);
    }
}
