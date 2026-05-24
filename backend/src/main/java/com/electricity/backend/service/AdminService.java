package com.electricity.backend.service;

import com.electricity.backend.entity.*;
import com.electricity.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Map<String, Object> addCustomer(User userDetails) {
        // 1. Uniqueness check
        if (userRepository.existsByUserId(userDetails.getUserId())) {
            throw new IllegalArgumentException("User ID already exists.");
        }
        if (userRepository.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        // 2. Generate default password (first 4 characters of full name + @123)
        String cleanedName = userDetails.getFullName().replaceAll("\\s+", "");
        String prefix = cleanedName.length() >= 4 ? cleanedName.substring(0, 4) : cleanedName;
        String defaultPassword = prefix + "@123";

        // 3. Encrypt and set status
        User user = new User(
                userDetails.getUserId(),
                passwordEncoder.encode(defaultPassword),
                userDetails.getEmail(),
                userDetails.getMobileNumber(),
                userDetails.getFullName(),
                "CUSTOMER"
        );
        user.setFirstLogin(true);
        user.setStatus("ACTIVE");
        user = userRepository.save(user);

        // 4. Generate random Customer ID CUST-YYYY-XXXXXX
        String customerId = "CUST-" + java.time.LocalDate.now().getYear() + "-" + String.format("%06d", (int)(Math.random() * 1000000));
        Customer customer = new Customer(customerId, user);
        customer = customerRepository.save(customer);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("customerId", customer.getCustomerId());
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("defaultPassword", defaultPassword);
        return response;
    }

    public List<Customer> searchCustomers(String query) {
        String searchStr = (query == null || query.trim().isEmpty()) ? null : query.trim();
        return customerRepository.searchCustomers(searchStr);
    }

    @Transactional
    public Consumer addConsumer(Consumer consumerDetails, String customerId) {
        // 1. Validate consumerNo uniqueness and length
        String consumerNo = consumerDetails.getConsumerNo();
        if (consumerNo == null || consumerNo.length() != 13 || !consumerNo.matches("\\d+")) {
            throw new IllegalArgumentException("Consumer Number must be exactly 13 digits.");
        }
        if (consumerRepository.existsByConsumerNo(consumerNo)) {
            throw new IllegalArgumentException("Consumer Number already exists.");
        }

        // 2. Fetch customer
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer Account not found."));

        consumerDetails.setCustomer(customer);
        if (consumerDetails.getContactEmail() == null || consumerDetails.getContactEmail().trim().isEmpty()) {
            consumerDetails.setContactEmail(customer.getUser().getEmail());
        }
        if (consumerDetails.getContactPhone() == null || consumerDetails.getContactPhone().trim().isEmpty()) {
            consumerDetails.setContactPhone(customer.getUser().getMobileNumber());
        }

        if (consumerDetails.getStatus() == null) {
            consumerDetails.setStatus("ACTIVE");
        }
        return consumerRepository.save(consumerDetails);
    }

    public Page<Consumer> getConsumers(String status, String customerType, String search, Pageable pageable) {
        String stat = (status == null || status.trim().isEmpty()) ? "ALL" : status.trim();
        String type = (customerType == null || customerType.trim().isEmpty()) ? "ALL" : customerType.trim();
        String query = (search == null || search.trim().isEmpty()) ? null : search.trim();
        return consumerRepository.searchConsumersAdmin(stat, type, query, pageable);
    }

    @Transactional
    public Consumer updateConsumer(String consumerNo, Consumer updatedDetails) {
        Consumer consumer = consumerRepository.findByConsumerNo(consumerNo)
                .orElseThrow(() -> new IllegalArgumentException("Consumer account not found."));

        consumer.setAddress(updatedDetails.getAddress());
        consumer.setElectricalSection(updatedDetails.getElectricalSection().toUpperCase());
        consumer.setCustomerType(updatedDetails.getCustomerType().toUpperCase());
        consumer.setStatus(updatedDetails.getStatus().toUpperCase());

        return consumerRepository.save(consumer);
    }

    @Transactional
    public Consumer toggleConsumerStatus(String consumerNo) {
        Consumer consumer = consumerRepository.findByConsumerNo(consumerNo)
                .orElseThrow(() -> new IllegalArgumentException("Consumer account not found."));

        if ("ACTIVE".equalsIgnoreCase(consumer.getStatus())) {
            consumer.setStatus("INACTIVE");
        } else {
            consumer.setStatus("ACTIVE");
        }

        return consumerRepository.save(consumer);
    }

    @Transactional
    public Bill addBill(Bill bill, String consumerNo) {
        Consumer consumer = consumerRepository.findByConsumerNo(consumerNo)
                .orElseThrow(() -> new IllegalArgumentException("Consumer connection not found."));

        if (bill.getBillAmount() <= 0) {
            throw new IllegalArgumentException("Bill amount must be greater than zero.");
        }

        // Duplicate check
        if (billRepository.findByConsumerConsumerNoAndBillingPeriod(consumerNo, bill.getBillingPeriod()).isPresent()) {
            throw new IllegalArgumentException("Bill already exists for this connection for the billing period " + bill.getBillingPeriod());
        }

        bill.setConsumer(consumer);
        String uniqueBillId = "BILL-" + java.time.LocalDate.now().getYear() + "-" + String.format("%06d", (int)(Math.random() * 1000000));
        bill.setBillId(uniqueBillId);
        bill.setStatus("UNPAID");

        return billRepository.save(bill);
    }

    public Page<Bill> getBills(String consumerNo, String status, Pageable pageable) {
        String stat = (status == null || status.trim().isEmpty()) ? "ALL" : status.trim();
        String cNo = (consumerNo == null || consumerNo.trim().isEmpty()) ? null : consumerNo.trim();
        return billRepository.findAllAdmin(cNo, stat, pageable);
    }

    public List<Complaint> getComplaints(String status, String complaintId, String smeId) {
        String stat = (status == null || status.trim().isEmpty()) ? "ALL" : status.trim();
        String compId = (complaintId == null || complaintId.trim().isEmpty()) ? null : complaintId.trim();
        String sme = (smeId == null || smeId.trim().isEmpty()) ? null : smeId.trim();
        return complaintRepository.searchComplaintsAdmin(stat, compId, sme);
    }

    public List<User> getSmeUsers() {
        return userRepository.findByRole("SME");
    }

    @Transactional
    public Complaint assignComplaintToSme(String complaintId, String smeUserId) {
        Complaint complaint = complaintRepository.findByComplaintId(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found."));

        User sme = userRepository.findByUserId(smeUserId)
                .orElseThrow(() -> new IllegalArgumentException("SME user not found."));

        if (!"SME".equalsIgnoreCase(sme.getRole())) {
            throw new IllegalArgumentException("Assigned user must have SME role.");
        }

        complaint.setAssignedSme(sme);
        if ("PENDING".equalsIgnoreCase(complaint.getStatus()) || "LODGED".equalsIgnoreCase(complaint.getStatus())) {
            complaint.setStatus("IN_PROGRESS");
        }

        return complaintRepository.save(complaint);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalCustomers = customerRepository.count();
        long totalConsumers = consumerRepository.count();
        long totalComplaints = complaintRepository.count();
        long totalUsers = userRepository.count();
        
        List<Bill> allBills = billRepository.findAll();
        double totalPaidAmount = 0;
        double totalUnpaidAmount = 0;
        long totalPaidBills = 0;
        long totalUnpaidBills = 0;
        long overdueBills = 0;
        double highestBill = 0;
        double totalBillSum = 0;
        
        java.time.LocalDate today = java.time.LocalDate.now();
        
        for (Bill b : allBills) {
            double amt = b.getBillAmount();
            double fee = b.getLateFee();
            double total = amt + fee;
            totalBillSum += amt;
            if (amt > highestBill) highestBill = amt;
            
            if ("PAID".equalsIgnoreCase(b.getStatus())) {
                totalPaidAmount += total;
                totalPaidBills++;
            } else {
                totalUnpaidAmount += total;
                totalUnpaidBills++;
                if (b.getDueDate() != null && b.getDueDate().isBefore(today)) {
                    overdueBills++;
                }
            }
        }
        
        double avgBillAmount = allBills.isEmpty() ? 0 : totalBillSum / allBills.size();
        double collectionRate = (totalPaidAmount + totalUnpaidAmount) > 0 
            ? (totalPaidAmount / (totalPaidAmount + totalUnpaidAmount)) * 100.0 
            : 0;
        
        long lodgedComplaints = 0;
        long inProgressComplaints = 0;
        long resolvedComplaints = 0;
        
        List<Complaint> allComplaints = complaintRepository.findAll();
        for (Complaint c : allComplaints) {
            if ("LODGED".equalsIgnoreCase(c.getStatus()) || "PENDING".equalsIgnoreCase(c.getStatus())) {
                lodgedComplaints++;
            } else if ("IN_PROGRESS".equalsIgnoreCase(c.getStatus())) {
                inProgressComplaints++;
            } else if ("RESOLVED".equalsIgnoreCase(c.getStatus()) || "CLOSED".equalsIgnoreCase(c.getStatus())) {
                resolvedComplaints++;
            }
        }
        
        double resolutionRate = totalComplaints > 0 
            ? ((double) resolvedComplaints / totalComplaints) * 100.0 
            : 0;
        
        long residentialConsumers = 0;
        long commercialConsumers = 0;
        long activeConsumers = 0;
        long inactiveConsumers = 0;
        List<Consumer> allConsumers = consumerRepository.findAll();
        for (Consumer c : allConsumers) {
            if ("RESIDENTIAL".equalsIgnoreCase(c.getCustomerType())) {
                residentialConsumers++;
            } else if ("COMMERCIAL".equalsIgnoreCase(c.getCustomerType())) {
                commercialConsumers++;
            }
            if ("ACTIVE".equalsIgnoreCase(c.getStatus())) {
                activeConsumers++;
            } else {
                inactiveConsumers++;
            }
        }
        
        long totalSmes = userRepository.findAll().stream()
            .filter(u -> "SME".equalsIgnoreCase(u.getRole()))
            .count();
        
        stats.put("totalCustomers", totalCustomers);
        stats.put("totalConsumers", totalConsumers);
        stats.put("totalComplaints", totalComplaints);
        stats.put("totalUsers", totalUsers);
        stats.put("totalSmes", totalSmes);
        stats.put("totalPaidAmount", totalPaidAmount);
        stats.put("totalUnpaidAmount", totalUnpaidAmount);
        stats.put("totalPaidBills", totalPaidBills);
        stats.put("totalUnpaidBills", totalUnpaidBills);
        stats.put("totalBills", (long) allBills.size());
        stats.put("overdueBills", overdueBills);
        stats.put("avgBillAmount", Math.round(avgBillAmount * 100.0) / 100.0);
        stats.put("highestBill", highestBill);
        stats.put("collectionRate", Math.round(collectionRate * 10.0) / 10.0);
        stats.put("lodgedComplaints", lodgedComplaints);
        stats.put("inProgressComplaints", inProgressComplaints);
        stats.put("resolvedComplaints", resolvedComplaints);
        stats.put("resolutionRate", Math.round(resolutionRate * 10.0) / 10.0);
        stats.put("residentialConsumers", residentialConsumers);
        stats.put("commercialConsumers", commercialConsumers);
        stats.put("activeConsumers", activeConsumers);
        stats.put("inactiveConsumers", inactiveConsumers);
        
        return stats;
    }
}
