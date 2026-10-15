package com.apexdrive.dto;

import jakarta.validation.constraints.NotBlank;

public class CancelBookingRequest {

    @NotBlank(message = "Customer email is required for identity verification")
    private String customerEmail;

    private String reason;

    public CancelBookingRequest() {
    }

    public CancelBookingRequest(String customerEmail, String reason) {
        this.customerEmail = customerEmail;
        this.reason = reason;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
