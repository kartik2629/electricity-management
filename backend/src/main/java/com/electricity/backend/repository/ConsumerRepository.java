package com.electricity.backend.repository;

import com.electricity.backend.entity.Consumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    Optional<Consumer> findByConsumerNo(String consumerNo);
    boolean existsByConsumerNo(String consumerNo);
    
    List<Consumer> findByCustomerUserUserId(String userId);
    List<Consumer> findByCustomerId(Long customerId);
    List<Consumer> findByCustomerCustomerId(String customerId);

    Page<Consumer> findByElectricalSectionAndCustomerType(String electricalSection, String customerType, Pageable pageable);
    Page<Consumer> findByElectricalSection(String electricalSection, Pageable pageable);
    Page<Consumer> findByCustomerType(String customerType, Pageable pageable);
    
    List<Consumer> findByCustomerUserFullNameContainingIgnoreCaseOrConsumerNoContaining(String name, String consumerNo);

    @Query("SELECT c FROM Consumer c WHERE " +
           "(:status = 'ALL' OR c.status = :status) AND " +
           "(:customerType = 'ALL' OR c.customerType = :customerType) AND " +
           "(:search = '' OR LOWER(c.consumerNo) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.customer.customerId) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.customer.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Consumer> searchConsumersAdmin(
            @Param("status") String status, 
            @Param("customerType") String customerType, 
            @Param("search") String search, 
            Pageable pageable);
}
