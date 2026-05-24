package com.electricity.backend.service;

import com.electricity.backend.dto.PayRequest;
import com.electricity.backend.dto.TransactionResponse;
import com.electricity.backend.entity.*;
import com.electricity.backend.repository.BillRepository;
import com.electricity.backend.repository.ConsumerRepository;
import com.electricity.backend.repository.CustomerRepository;
import com.electricity.backend.repository.TransactionRepository;
import com.electricity.backend.repository.ComplaintRepository;
import com.electricity.backend.dto.ComplaintRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private NotificationService notificationService;

    public Customer getProfile(User user) {
        return customerRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Customer profile not found."));
    }

    public List<Consumer> getConsumers(User user) {
        return consumerRepository.findByCustomerUserUserId(user.getUserId());
    }

    public List<Bill> getBills(User user) {
        List<Bill> bills = billRepository.findByConsumerCustomerUserUserId(user.getUserId());
        List<Transaction> transactions = transactionRepository.findByBillsConsumerCustomerUserUserId(user.getUserId());

        for (Bill bill : bills) {
            if ("PAID".equals(bill.getStatus())) {
                transactions.stream()
                        .filter(t -> t.getBills().stream().anyMatch(b -> b.getId().equals(bill.getId())))
                        .findFirst()
                        .ifPresent(t -> bill.setTransactionId(t.getTransactionId()));
            }
        }
        return bills;
    }

    public Optional<Bill> getLatestBill(User user) {
        List<Bill> bills = billRepository.findByConsumerCustomerUserUserId(user.getUserId());
        if (bills.isEmpty()) {
            return Optional.empty();
        }
        // Return latest by bill date
        return bills.stream()
                .max((b1, b2) -> b1.getBillDate().compareTo(b2.getBillDate()));
    }

    @Transactional
    public TransactionResponse payBills(PayRequest request, User user) {
        Customer customer = customerRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Customer profile not found."));

        List<Bill> billsToPay = new ArrayList<>();
        double totalAmount = 0.0;

        for (Long billId : request.getBillIds()) {
            Bill bill = billRepository.findById(billId)
                    .orElseThrow(() -> new IllegalArgumentException("Bill not found: ID " + billId));

            // Verify bill belongs to this customer
            if (!bill.getConsumer().getCustomer().getId().equals(customer.getId())) {
                throw new IllegalArgumentException("Access Denied: Bill " + bill.getBillId() + " does not belong to your account.");
            }

            if ("PAID".equalsIgnoreCase(bill.getStatus())) {
                throw new IllegalArgumentException("Bill " + bill.getBillId() + " is already paid.");
            }

            // Apply late fee if payment is after due date and no late fee is set yet
            if (LocalDate.now().isAfter(bill.getDueDate()) && bill.getLateFee() <= 0.0) {
                bill.setLateFee(10.00); // Flat $10.00 fee
            }

            bill.setStatus("PAID");
            bill.setPaymentDate(LocalDate.now());
            bill.setModeOfPayment(request.getModeOfPayment().toUpperCase());
            billRepository.save(bill);

            billsToPay.add(bill);
            totalAmount += (bill.getBillAmount() + bill.getLateFee());
        }

        // Generate transaction details
        String randomPaymentId = "PAY-" + String.format("%06d", (int)(Math.random() * 1000000));
        String randomTxnId = "TXN-" + String.format("%06d", (int)(Math.random() * 1000000));
        String randomReceipt = "REC-" + String.format("%06d", (int)(Math.random() * 1000000));

        Transaction txn = new Transaction();
        txn.setPaymentId(randomPaymentId);
        txn.setTransactionId(randomTxnId);
        txn.setReceiptNumber(randomReceipt);
        txn.setTransactionDate(LocalDateTime.now());
        txn.setTransactionType("DEBIT");
        txn.setTransactionAmount(totalAmount);
        txn.setTransactionStatus("SUCCESS");
        txn.setBills(billsToPay);
        txn = transactionRepository.save(txn);

        List<TransactionResponse.PaidBillInfo> billInfos = billsToPay.stream()
                .map(b -> new TransactionResponse.PaidBillInfo(b.getBillId(), b.getBillingPeriod(), b.getBillAmount(), b.getLateFee()))
                .collect(Collectors.toList());

        notificationService.sendToRole("ADMIN", "Payment received from " + user.getUserId() + " for amount ₹" + totalAmount, "SUCCESS");

        return new TransactionResponse(
                txn.getPaymentId(),
                txn.getTransactionId(),
                txn.getReceiptNumber(),
                txn.getTransactionDate(),
                txn.getTransactionType(),
                txn.getTransactionAmount(),
                txn.getTransactionStatus(),
                request.getModeOfPayment(),
                billInfos
        );
    }

    public TransactionResponse getTransactionDetails(String transactionId, User user) {
        Customer customer = customerRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Customer profile not found."));

        Transaction txn = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: ID " + transactionId));

        // Verify transaction belongs to this customer
        boolean belongsToCustomer = txn.getBills().stream()
                .anyMatch(b -> b.getConsumer().getCustomer().getId().equals(customer.getId()));

        if (!belongsToCustomer) {
            throw new IllegalArgumentException("Access Denied: You do not have permission to view this transaction.");
        }

        List<TransactionResponse.PaidBillInfo> billInfos = txn.getBills().stream()
                .map(b -> new TransactionResponse.PaidBillInfo(b.getBillId(), b.getBillingPeriod(), b.getBillAmount(), b.getLateFee()))
                .collect(Collectors.toList());

        String mode = txn.getBills().isEmpty() ? "UNKNOWN" : txn.getBills().get(0).getModeOfPayment();

        return new TransactionResponse(
                txn.getPaymentId(),
                txn.getTransactionId(),
                txn.getReceiptNumber(),
                txn.getTransactionDate(),
                txn.getTransactionType(),
                txn.getTransactionAmount(),
                txn.getTransactionStatus(),
                mode,
                billInfos
        );
    }

    @Transactional
    public Complaint registerComplaint(ComplaintRequest request, User user) {
        Consumer consumer = consumerRepository.findByConsumerNo(request.getConsumerNo())
                .orElseThrow(() -> new IllegalArgumentException("Please enter a valid Consumer Number."));

        // Verify consumer belongs to customer
        if (!consumer.getCustomer().getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Access Denied: Consumer connection does not belong to your account.");
        }

        // Generate unique complaint ID COMP-YYYY-XXXXXX
        String complaintId = "COMP-" + java.time.LocalDate.now().getYear() + "-" + String.format("%06d", (int)(Math.random() * 1000000));

        // Estimate resolution time
        String type = request.getComplaintType().trim().toUpperCase();
        String estTime;
        if (type.contains("POWER") || type.contains("OUTAGE")) {
            estTime = "24 Hours";
        } else if (type.contains("METER")) {
            estTime = "3 Business Days";
        } else if (type.contains("BILL")) {
            estTime = "5 Business Days";
        } else {
            estTime = "7 Business Days";
        }

        Complaint complaint = new Complaint();
        complaint.setComplaintId(complaintId);
        complaint.setConsumer(consumer);
        complaint.setComplaintType(request.getComplaintType().trim());
        complaint.setCategory(request.getCategory().trim());
        complaint.setDescription(request.getDescription().trim());
        complaint.setPreferredContactMethod(request.getPreferredContactMethod().trim());
        complaint.setContactDetails(request.getContactDetails().trim());
        complaint.setStatus("PENDING");
        complaint.setDateSubmitted(LocalDateTime.now());
        complaint.setLastUpdatedDate(LocalDateTime.now());
        complaint.setEstimatedResolutionTime(estTime);

        Complaint savedComplaint = complaintRepository.save(complaint);
        
        notificationService.sendToRole("ADMIN", "New complaint lodged by " + user.getUserId() + " (" + savedComplaint.getComplaintId() + ")", "WARNING");
        
        return savedComplaint;
    }

    public List<Complaint> getComplaints(User user) {
        return complaintRepository.findByConsumerCustomerUserUserId(user.getUserId());
    }

    public List<Complaint> searchComplaints(String complaintId, String status, User user) {
        String cId = (complaintId == null || complaintId.trim().isEmpty()) ? null : complaintId.trim();
        String stat = (status == null || status.trim().isEmpty()) ? null : status.trim().toUpperCase();
        return complaintRepository.searchComplaints(user.getUserId(), cId, stat);
    }
}
