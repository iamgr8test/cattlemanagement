package com.example.cattleapplication;

public class InsurancePolicy {
    private int policyId;           // Unique ID for the policy
    private String policyName;      // Name of the insurance policy
    private double premiumAmount;    // Amount of premium for the policy
    private String coverageDetails;   // Details about what the policy covers
    private String breedEligibility;  // Eligible breeds for the policy

    // Constructor
    public InsurancePolicy(int policyId, String policyName, double premiumAmount, String coverageDetails, String breedEligibility) {
        this.policyId = policyId;
        this.policyName = policyName;
        this.premiumAmount = premiumAmount;
        this.coverageDetails = coverageDetails;
        this.breedEligibility = breedEligibility; // Initialize breed eligibility
    }

    // Getters
    public int getPolicyId() {
        return policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public double getPremiumAmount() {
        return premiumAmount;
    }

    public String getCoverageDetails() {
        return coverageDetails;
    }

    public String getBreedEligibility() {
        return breedEligibility; // Add this method to access breed eligibility
    }
}
