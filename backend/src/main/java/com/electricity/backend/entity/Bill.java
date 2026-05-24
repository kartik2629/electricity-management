package com.electricity.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_id", unique = true, nullable = false, length = 50)
    private String billId; // generated BILL-XXXXXX

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "consumer_id", referencedColumnName = "id", nullable = false)
    private Consumer consumer;

    @Column(name = "billing_period", nullable = false, length = 50)
    private String billingPeriod; // e.g., "June 2024"

    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "disconnection_date", nullable = false)
    private LocalDate disconnectionDate;

    @Column(name = "bill_amount", nullable = false)
    private double billAmount;

    @Column(name = "late_fee", nullable = false)
    private double lateFee = 0.0;

    @Column(nullable = false, length = 20)
    private String status = "UNPAID"; // PAID, UNPAID

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "mode_of_payment", length = 50)
    private String modeOfPayment;

    @Transient
    private String transactionId;

    public Bill() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public Consumer getConsumer() { return consumer; }
    public void setConsumer(Consumer consumer) { this.consumer = consumer; }

    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getDisconnectionDate() { return disconnectionDate; }
    public void setDisconnectionDate(LocalDate disconnectionDate) { this.disconnectionDate = disconnectionDate; }

    public double getBillAmount() { return billAmount; }
    public void setBillAmount(double billAmount) { this.billAmount = billAmount; }

    public double getLateFee() { return lateFee; }
    public void setLateFee(double lateFee) { this.lateFee = lateFee; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getModeOfPayment() { return modeOfPayment; }
    public void setModeOfPayment(String modeOfPayment) { this.modeOfPayment = modeOfPayment; }
}
