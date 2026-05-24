package com.electricity.backend.config;

import com.electricity.backend.entity.Bill;
import com.electricity.backend.entity.Consumer;
import com.electricity.backend.entity.User;
import com.electricity.backend.repository.BillRepository;
import com.electricity.backend.repository.ConsumerRepository;
import com.electricity.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Seed Admin if not exists
        if (!userRepository.existsByUserId("admin")) {
            User admin = new User(
                "admin",
                passwordEncoder.encode("Admin@123"),
                "admin@electricity.com",
                "9999999999",
                "System Admin",
                "ADMIN"
            );
            admin.setFirstLogin(false);
            userRepository.save(admin);
            System.out.println("Seeded system admin user.");
        }

        // Seed SME if not exists
        if (!userRepository.existsByUserId("sme_expert")) {
            User sme = new User(
                "sme_expert",
                passwordEncoder.encode("SME@123"),
                "sme@electricity.com",
                "8888888888",
                "Subject Matter Expert",
                "SME"
            );
            sme.setFirstLogin(false);
            userRepository.save(sme);
            System.out.println("Seeded SME expert user.");
        }

        // Seed some test consumer numbers for registration test
        seedConsumerIfNotExists("1000000000001", "123 Main St, Office Zone", "7777777777", "consumer1@electricity.com", "RESIDENTIAL", "OFFICE");
        seedConsumerIfNotExists("1000000000002", "456 Market St, Commercial Zone", "7777777778", "consumer2@electricity.com", "COMMERCIAL", "REGION");
        seedConsumerIfNotExists("1000000000003", "789 Park Ave, Office Zone", "7777777779", "consumer3@electricity.com", "RESIDENTIAL", "OFFICE");
        
        // Seed bills for consumer 1000000000001
        seedBillsForConsumer("1000000000001");
    }

    private void seedConsumerIfNotExists(String consumerNo, String address, String phone, String email, String type, String section) {
        if (!consumerRepository.existsByConsumerNo(consumerNo)) {
            Consumer consumer = new Consumer(
                consumerNo,
                address,
                phone,
                email,
                type,
                section,
                "ACTIVE"
            );
            consumerRepository.save(consumer);
            System.out.println("Seeded consumer connection: " + consumerNo);
        }
    }

    private void seedBillsForConsumer(String consumerNo) {
        Consumer consumer = consumerRepository.findByConsumerNo(consumerNo).orElse(null);
        if (consumer == null) return;

        List<Bill> existing = billRepository.findByConsumerConsumerNo(consumerNo);
        if (existing.isEmpty()) {
            // 1. Paid Bill
            Bill paidBill = new Bill();
            paidBill.setBillId("BILL-000001");
            paidBill.setConsumer(consumer);
            paidBill.setBillingPeriod("March 2026");
            paidBill.setBillDate(LocalDate.of(2026, 3, 1));
            paidBill.setDueDate(LocalDate.of(2026, 3, 20));
            paidBill.setDisconnectionDate(LocalDate.of(2026, 4, 5));
            paidBill.setBillAmount(95.00);
            paidBill.setLateFee(0.0);
            paidBill.setStatus("PAID");
            paidBill.setPaymentDate(LocalDate.of(2026, 3, 18));
            paidBill.setModeOfPayment("CARD");
            billRepository.save(paidBill);

            // 2. Unpaid Overdue Bill
            Bill overdueBill = new Bill();
            overdueBill.setBillId("BILL-000002");
            overdueBill.setConsumer(consumer);
            overdueBill.setBillingPeriod("April 2026");
            overdueBill.setBillDate(LocalDate.of(2026, 4, 1));
            overdueBill.setDueDate(LocalDate.of(2026, 4, 20)); // overdue!
            overdueBill.setDisconnectionDate(LocalDate.of(2026, 5, 5));
            overdueBill.setBillAmount(120.50);
            overdueBill.setLateFee(10.00); // flat overdue late fee
            overdueBill.setStatus("UNPAID");
            billRepository.save(overdueBill);

            // 3. Unpaid Normal Bill
            Bill activeBill = new Bill();
            activeBill.setBillId("BILL-000003");
            activeBill.setConsumer(consumer);
            activeBill.setBillingPeriod("May 2026");
            activeBill.setBillDate(LocalDate.of(2026, 5, 1));
            activeBill.setDueDate(LocalDate.of(2026, 6, 20)); // future due date
            activeBill.setDisconnectionDate(LocalDate.of(2026, 7, 5));
            activeBill.setBillAmount(145.00);
            activeBill.setLateFee(0.0);
            activeBill.setStatus("UNPAID");
            billRepository.save(activeBill);

            System.out.println("Seeded bills for consumer: " + consumerNo);
        }
    }
}
