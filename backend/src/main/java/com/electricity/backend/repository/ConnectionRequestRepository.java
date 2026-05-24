package com.electricity.backend.repository;

import com.electricity.backend.entity.ConnectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, String> {
    List<ConnectionRequest> findByStatusOrderByCreatedAtDesc(String status);
}
