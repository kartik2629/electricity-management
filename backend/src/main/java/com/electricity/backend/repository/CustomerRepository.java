package com.electricity.backend.repository;

import com.electricity.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerId(String customerId);
    Optional<Customer> findByUserUserId(String userId);
    List<Customer> findByUserFullNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM Customer c WHERE :query IS NULL " +
           "OR LOWER(c.customerId) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.user.userId) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.user.fullName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.user.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Customer> searchCustomers(@Param("query") String query);
}
