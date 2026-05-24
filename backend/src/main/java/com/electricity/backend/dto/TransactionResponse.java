package com.electricity.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionResponse {
    private String paymentId;
    private String transactionId;
    private String receiptNumber;
    private LocalDateTime transactionDate;
    private String transactionType;
    private double transactionAmount;
    private String transactionStatus;
    private String modeOfPayment;
    private List<PaidBillInfo> paidBills;

    public TransactionResponse() {}

    public TransactionResponse(String paymentId, String transactionId, String receiptNumber, LocalDateTime transactionDate, String transactionType, double transactionAmount, String transactionStatus, String modeOfPayment, List<PaidBillInfo> paidBills) {
        this.paymentId = paymentId;
        this.transactionId = transactionId;
        this.receiptNumber = receiptNumber;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.transactionStatus = transactionStatus;
        this.modeOfPayment = modeOfPayment;
        this.paidBills = paidBills;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getModeOfPayment() { return modeOfPayment; }
    public void setModeOfPayment(String modeOfPayment) { this.modeOfPayment = modeOfPayment; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public double getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(double transactionAmount) { this.transactionAmount = transactionAmount; }

    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }

    public List<PaidBillInfo> getPaidBills() { return paidBills; }
    public void setPaidBills(List<PaidBillInfo> paidBills) { this.paidBills = paidBills; }

    public static class PaidBillInfo {
        private String billId;
        private String billingPeriod;
        private double billAmount;
        private double lateFee;

        public PaidBillInfo() {}

        public PaidBillInfo(String billId, String billingPeriod, double billAmount, double lateFee) {
            this.billId = billId;
            this.billingPeriod = billingPeriod;
            this.billAmount = billAmount;
            this.lateFee = lateFee;
        }

        public String getBillId() { return billId; }
        public void setBillId(String billId) { this.billId = billId; }

        public String getBillingPeriod() { return billingPeriod; }
        public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }

        public double getBillAmount() { return billAmount; }
        public void setBillAmount(double billAmount) { this.billAmount = billAmount; }

        public double getLateFee() { return lateFee; }
        public void setLateFee(double lateFee) { this.lateFee = lateFee; }
    }
}
