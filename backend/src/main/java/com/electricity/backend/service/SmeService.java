package com.electricity.backend.service;

import com.electricity.backend.entity.Complaint;
import com.electricity.backend.entity.User;
import com.electricity.backend.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SmeService {

    @Autowired
    private ComplaintRepository complaintRepository;

    public List<Complaint> getAssignedComplaints(User currentUser, String status) {
        if ("ALL".equalsIgnoreCase(status) || status == null || status.trim().isEmpty()) {
            return complaintRepository.findByAssignedSmeUserId(currentUser.getUserId());
        }
        return complaintRepository.findByAssignedSmeUserIdAndStatus(currentUser.getUserId(), status);
    }

    @Transactional
    public Complaint updateComplaintStatus(String complaintId, String status, String resolutionNotes, User currentUser) {
        Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint ticket not found."));

        if (complaint.getAssignedSme() == null || !complaint.getAssignedSme().getUserId().equals(currentUser.getUserId())) {
            throw new IllegalArgumentException("Unauthorized: This complaint ticket is not assigned to you.");
        }

        complaint.setStatus(status.toUpperCase());
        complaint.setNotes(resolutionNotes);

        return complaintRepository.save(complaint);
    }
}
