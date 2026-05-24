package com.electricity.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complaint_id", unique = true, nullable = false, length = 50)
    private String complaintId; // generated COMP-XXXXXX

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "consumer_id", referencedColumnName = "id", nullable = false)
    private Consumer consumer;

    @Column(name = "complaint_type", nullable = false, length = 50)
    private String complaintType; // e.g. BILLING_ISSUE, POWER_OUTAGE, METER_READING_ISSUE

    @Column(nullable = false, length = 100)
    private String category; // e.g. "High Bill", "Power Outage", etc.

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "preferred_contact_method", nullable = false, length = 20)
    private String preferredContactMethod; // EMAIL, PHONE

    @Column(name = "contact_details", nullable = false, length = 100)
    private String contactDetails;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, IN_PROGRESS, RESOLVED, CLOSED

    @Column(name = "date_submitted", nullable = false)
    private LocalDateTime dateSubmitted = LocalDateTime.now();

    @Column(name = "last_updated_date", nullable = false)
    private LocalDateTime lastUpdatedDate = LocalDateTime.now();

    @Column(name = "estimated_resolution_time", nullable = false, length = 50)
    private String estimatedResolutionTime;

    @Column(length = 1000)
    private String notes; // Remarks added on status change

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_sme_id", referencedColumnName = "id")
    private User assignedSme;

    public Complaint() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public Consumer getConsumer() { return consumer; }
    public void setConsumer(Consumer consumer) { this.consumer = consumer; }

    public String getComplaintType() { return complaintType; }
    public void setComplaintType(String complaintType) { this.complaintType = complaintType; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPreferredContactMethod() { return preferredContactMethod; }
    public void setPreferredContactMethod(String preferredContactMethod) { this.preferredContactMethod = preferredContactMethod; }

    public String getContactDetails() { return contactDetails; }
    public void setContactDetails(String contactDetails) { this.contactDetails = contactDetails; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDateSubmitted() { return dateSubmitted; }
    public void setDateSubmitted(LocalDateTime dateSubmitted) { this.dateSubmitted = dateSubmitted; }

    public LocalDateTime getLastUpdatedDate() { return lastUpdatedDate; }
    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) { this.lastUpdatedDate = lastUpdatedDate; }

    public String getEstimatedResolutionTime() { return estimatedResolutionTime; }
    public void setEstimatedResolutionTime(String estimatedResolutionTime) { this.estimatedResolutionTime = estimatedResolutionTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public User getAssignedSme() { return assignedSme; }
    public void setAssignedSme(User assignedSme) { this.assignedSme = assignedSme; }
}
