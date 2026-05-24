package com.electricity.backend.repository;

import com.electricity.backend.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByComplaintId(String complaintId);
    List<Complaint> findByConsumerCustomerUserUserId(String userId);
    
    @Query("SELECT c FROM Complaint c WHERE c.consumer.customer.user.userId = :userId " +
           "AND (:complaintId IS NULL OR c.complaintId = :complaintId) " +
           "AND (:status IS NULL OR c.status = :status)")
    List<Complaint> searchComplaints(@Param("userId") String userId, 
                                     @Param("complaintId") String complaintId, 
                                     @Param("status") String status);
    
    // Admin & SME filtering
    List<Complaint> findByStatus(String status);
    List<Complaint> findByAssignedSmeUserId(String userId);
    List<Complaint> findByAssignedSmeUserIdAndStatus(String userId, String status);
    
    List<Complaint> findByConsumerCustomerCustomerIdOrConsumerConsumerNo(String customerId, String consumerNo);
    
    List<Complaint> findByDateSubmittedBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT c FROM Complaint c WHERE " +
           "(:status = 'ALL' OR c.status = :status) AND " +
           "(:complaintId = '' OR LOWER(c.complaintId) = LOWER(:complaintId)) AND " +
           "(:smeId = '' OR c.assignedSme.userId = :smeId)")
    List<Complaint> searchComplaintsAdmin(
            @Param("status") String status,
            @Param("complaintId") String complaintId,
            @Param("smeId") String smeId);
}
