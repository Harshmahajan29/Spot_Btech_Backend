package com.spotentrywebsite.spot_admission_portal.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;

public class PaymentOrderRequest {

    // JsonAlias ensures that whether frontend sends candidateId or candidate_id, it maps perfectly
    @JsonAlias({"candidateId", "candidate_id", "applicationId"})
    private String candidateId;

    @JsonAlias({"finalEligibleCategory", "final_eligible_category", "category"})
    private String finalEligibleCategory;

    // Standard Getters and Setters
    public String getCandidateId() {
        return candidateId;
    }
    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getFinalEligibleCategory() {
        return finalEligibleCategory;
    }
    public void setFinalEligibleCategory(String finalEligibleCategory) {
        this.finalEligibleCategory = finalEligibleCategory;
    }
}