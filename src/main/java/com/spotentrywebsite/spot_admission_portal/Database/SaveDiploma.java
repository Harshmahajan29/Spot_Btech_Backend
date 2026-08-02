package com.spotentrywebsite.spot_admission_portal.Database;

import jakarta.persistence.*;

@Entity
@Table(
        name = "all_india_diploma",
        indexes = { @Index(name = "idx_diploma_lookup", columnList = "candidate_id") }
)
public class SaveDiploma {

    @Id
    @Column(name = "candidate_id", nullable = false, unique = true, length = 50)
    private String candidateId;

    @Column(name = "qualification_type", nullable = false, length = 30)
    private String qualificationType = "DIPLOMA";

    @Column(name = "diploma_branch", nullable = false, length = 255)
    private String diplomaBranch;

    @Column(name = "mobile_no", nullable = false, length = 15)
    private String mobileNo;

    @Column(name = "registered_category", nullable = false, length = 20)
    private String registeredCategory;

    @Column(name = "final_eligible_category", nullable = false, length = 20)
    private String finalEligibleCategory;

    @Column(name = "system_flag", length = 5)
    private String systemFlag;

    @Column(name = "diploma_percentage", nullable = false)
    private Double diplomaPercentage;

    @Column(name = "ssc_maths_marks")
    private Double sscMathsMarks;

    @Column(name = "preferences", nullable = false, length = 1000)
    private String preferences;

    @Embedded
    private DiplomaDocuments documents;

    // --- CONSTRUCTORS ---
    public SaveDiploma() {}

    // --- GETTERS AND SETTERS ---
    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public String getQualificationType() { return qualificationType; }
    public void setQualificationType(String qualificationType) { this.qualificationType = qualificationType; }

    public String getDiplomaBranch() { return diplomaBranch; }
    public void setDiplomaBranch(String diplomaBranch) { this.diplomaBranch = diplomaBranch; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getRegisteredCategory() { return registeredCategory; }
    public void setRegisteredCategory(String registeredCategory) { this.registeredCategory = registeredCategory; }

    public String getFinalEligibleCategory() { return finalEligibleCategory; }
    public void setFinalEligibleCategory(String finalEligibleCategory) { this.finalEligibleCategory = finalEligibleCategory; }

    public String getSystemFlag() { return systemFlag; }
    public void setSystemFlag(String systemFlag) { this.systemFlag = systemFlag; }

    public Double getDiplomaPercentage() { return diplomaPercentage; }
    public void setDiplomaPercentage(Double diplomaPercentage) { this.diplomaPercentage = diplomaPercentage; }

    public Double getDoubleSscMathsMarks() { return sscMathsMarks; }
    public void setSscMathsMarks(Double sscMathsMarks) { this.sscMathsMarks = sscMathsMarks; }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }

    public DiplomaDocuments getDocuments() { return documents; }
    public void setDocuments(DiplomaDocuments documents) { this.documents = documents; }
}