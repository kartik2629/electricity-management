package com.electricity.backend.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class PayRequest {

    @NotEmpty(message = "At least one bill must be selected")
    private List<Long> billIds;

    @NotBlank(message = "Payment mode is required")
    private String modeOfPayment;

    public PayRequest() {}

    public List<Long> getBillIds() { return billIds; }
    public void setBillIds(List<Long> billIds) { this.billIds = billIds; }

    public String getModeOfPayment() { return modeOfPayment; }
    public void setModeOfPayment(String modeOfPayment) { this.modeOfPayment = modeOfPayment; }
}
