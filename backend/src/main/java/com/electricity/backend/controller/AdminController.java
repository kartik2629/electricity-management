package com.electricity.backend.controller;

import com.electricity.backend.entity.*;
import com.electricity.backend.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/connection-requests")
    public ResponseEntity<List<ConnectionRequest>> getPendingRequests() {
        return ResponseEntity.ok(adminService.getPendingRequests());
    }

    @PostMapping("/connection-requests/{id}/approve")
    public ResponseEntity<Map<String, String>> approveConnection(@PathVariable String id) {
        adminService.approveConnection(id);
        return ResponseEntity.ok(Map.of("message", "Connection request approved successfully."));
    }

    @PostMapping("/connection-requests/{id}/reject")
    public ResponseEntity<Map<String, String>> rejectConnection(@PathVariable String id) {
        adminService.rejectConnection(id);
        return ResponseEntity.ok(Map.of("message", "Connection request rejected."));
    }

    @PostMapping("/customers")
    public ResponseEntity<Map<String, Object>> addCustomer(@Valid @RequestBody User userDetails) {
        Map<String, Object> response = adminService.addCustomer(userDetails);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customers/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam(required = false) String query) {
        List<Customer> customers = adminService.searchCustomers(query);
        return ResponseEntity.ok(customers);
    }

    @PostMapping("/consumers")
    public ResponseEntity<Consumer> addConsumer(
            @RequestParam String customerId,
            @Valid @RequestBody Consumer consumerDetails) {
        Consumer consumer = adminService.addConsumer(consumerDetails, customerId);
        return ResponseEntity.ok(consumer);
    }

    @GetMapping("/consumers")
    public ResponseEntity<Page<Consumer>> getConsumers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "ALL") String customerType,
            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Consumer> consumers = adminService.getConsumers(status, customerType, search, pageable);
        return ResponseEntity.ok(consumers);
    }

    @PutMapping("/consumers/{consumerNo}")
    public ResponseEntity<Consumer> updateConsumer(
            @PathVariable String consumerNo,
            @Valid @RequestBody Consumer updatedDetails) {
        Consumer consumer = adminService.updateConsumer(consumerNo, updatedDetails);
        return ResponseEntity.ok(consumer);
    }

    @PostMapping("/consumers/{consumerNo}/toggle-status")
    public ResponseEntity<Consumer> toggleConsumerStatus(@PathVariable String consumerNo) {
        Consumer consumer = adminService.toggleConsumerStatus(consumerNo);
        return ResponseEntity.ok(consumer);
    }

    @PostMapping("/bills")
    public ResponseEntity<Bill> addBill(
            @RequestParam String consumerNo,
            @Valid @RequestBody Bill billDetails) {
        Bill bill = adminService.addBill(billDetails, consumerNo);
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/bills")
    public ResponseEntity<Page<Bill>> getBills(
            @RequestParam(required = false) String consumerNo,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Bill> bills = adminService.getBills(consumerNo, status, pageable);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getComplaints(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String complaintId,
            @RequestParam(required = false) String smeId) {
        List<Complaint> complaints = adminService.getComplaints(status, complaintId, smeId);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/smes")
    public ResponseEntity<List<User>> getSmes() {
        List<User> smes = adminService.getSmeUsers();
        return ResponseEntity.ok(smes);
    }

    @PostMapping("/complaints/{complaintId}/assign")
    public ResponseEntity<Complaint> assignComplaintToSme(
            @PathVariable String complaintId,
            @RequestParam String smeId) {
        Complaint complaint = adminService.assignComplaintToSme(complaintId, smeId);
        return ResponseEntity.ok(complaint);
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
}
