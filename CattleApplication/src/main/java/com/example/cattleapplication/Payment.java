package com.example.cattleapplication;

import java.util.Date;

public class Payment {
    private int paymentId;
    private String userId;
    private int policyId;
    private double amount;
    private Date paymentDate;
    private String status;

    public Payment(int paymentId, String userId, int policyId, double amount, Date paymentDate, String status) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.policyId = policyId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    // Getters
    public int getPaymentId() {
        return paymentId;
    }

    public String getUserId() {
        return userId;
    }

    public int getPolicyId() {
        return policyId;
    }

    public double getAmount() {
        return amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public String getStatus() {
        return status;
    }
}
