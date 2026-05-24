package com.electricity.backend.repository;

import com.electricity.backend.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByBillId(String billId);
    List<Bill> findByConsumerConsumerNo(String consumerNo);
    List<Bill> findByConsumerConsumerNoAndStatus(String consumerNo, String status);
    Optional<Bill> findByConsumerConsumerNoAndBillingPeriod(String consumerNo, String billingPeriod);
    
    List<Bill> findByConsumerCustomerUserUserId(String userId);
    List<Bill> findByConsumerCustomerUserUserIdAndStatus(String userId, String status);
    
    // For date range and history filters
    List<Bill> findByConsumerCustomerUserUserIdAndBillDateBetween(String userId, LocalDate start, LocalDate end);
    List<Bill> findByConsumerCustomerUserUserIdAndStatusAndBillDateBetween(String userId, String status, LocalDate start, LocalDate end);

    @Query("SELECT b FROM Bill b WHERE " +
           "(:consumerNo IS NULL OR :consumerNo = '' OR b.consumer.consumerNo = :consumerNo) AND " +
           "(:status = 'ALL' OR b.status = :status)")
    Page<Bill> findAllAdmin(
            @Param("consumerNo") String consumerNo,
            @Param("status") String status,
            Pageable pageable);
}
