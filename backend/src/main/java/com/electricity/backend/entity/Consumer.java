package com.electricity.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "consumers")
public class Consumer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consumer_no", unique = true, nullable = false, length = 13)
    private String consumerNo; // 13-digit unique number

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @Column(name = "contact_email", nullable = false, length = 100)
    private String contactEmail;

    @Column(name = "customer_type", nullable = false, length = 20)
    private String customerType; // RESIDENTIAL, COMMERCIAL

    @Column(name = "electrical_section", nullable = false, length = 50)
    private String electricalSection; // OFFICE, REGION

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer; // Can be null if the consumer number is not yet registered/linked to a customer user

    public Consumer() {}

    public Consumer(String consumerNo, String address, String contactPhone, String contactEmail, String customerType, String electricalSection, String status) {
        this.consumerNo = consumerNo;
        this.address = address;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.customerType = customerType;
        this.electricalSection = electricalSection;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConsumerNo() { return consumerNo; }
    public void setConsumerNo(String consumerNo) { this.consumerNo = consumerNo; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }

    public String getElectricalSection() { return electricalSection; }
    public void setElectricalSection(String electricalSection) { this.electricalSection = electricalSection; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
