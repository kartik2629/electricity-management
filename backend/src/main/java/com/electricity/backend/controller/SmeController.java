package com.electricity.backend.controller;

import com.electricity.backend.entity.Complaint;
import com.electricity.backend.entity.User;
import com.electricity.backend.service.SmeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sme")
public class SmeController {

    @Autowired
    private SmeService smeService;

    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getAssignedComplaints(
            @RequestAttribute("currentUser") User currentUser,
            @RequestParam(defaultValue = "ALL") String status) {
        List<Complaint> complaints = smeService.getAssignedComplaints(currentUser, status);
        return ResponseEntity.ok(complaints);
    }

    @PostMapping("/complaints/{complaintId}/resolve")
    public ResponseEntity<Complaint> resolveComplaint(
            @RequestAttribute("currentUser") User currentUser,
            @PathVariable String complaintId,
            @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        String notes = payload.get("resolutionNotes");
        
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status field is required.");
        }

        Complaint complaint = smeService.updateComplaintStatus(complaintId, status, notes, currentUser);
        return ResponseEntity.ok(complaint);
    }
}
