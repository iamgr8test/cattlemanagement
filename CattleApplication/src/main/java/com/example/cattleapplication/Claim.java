package com.example.cattleapplication;

public class Claim {
    private String claimId;
    private String cattleId;
    private String claimStatus;
    private String userId; // Field for user ID (Submitted By)
    private String claimDate; // New field for submission date

    public Claim(String claimId, String cattleId, String claimStatus, String userId, String claimDate) {
        this.claimId = claimId;
        this.cattleId = cattleId;
        this.claimStatus = claimStatus;
        this.userId = userId; // Initialize the user ID field
        this.claimDate = claimDate; // Initialize the submission date field
    }

    public String getClaimId() {
        return claimId;
    }

    public String getCattleId() {
        return cattleId;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public String getUserId() {
        return userId; // Getter for user ID
    }

    public String getClaimDate() {
        return claimDate; // Getter for submission date
    }
}
