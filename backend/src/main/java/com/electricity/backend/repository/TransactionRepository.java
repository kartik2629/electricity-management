package com.electricity.backend.repository;

import com.electricity.backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);
    Optional<Transaction> findByPaymentId(String paymentId);
    Optional<Transaction> findByReceiptNumber(String receiptNumber);
    List<Transaction> findByBillsConsumerCustomerUserUserId(String userId);
}
