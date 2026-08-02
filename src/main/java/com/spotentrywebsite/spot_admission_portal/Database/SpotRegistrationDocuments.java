package com.spotentrywebsite.spot_admission_portal.Database;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "spot_registration_documents")
public class SpotRegistrationDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_id", nullable = false, unique = true)
    private String candidateId;

    @Column(name = "marksheet_path")
    private String marksheetPath;

    @Column(name = "caste_certificate_path")
    private String casteCertificatePath;

    @Column(name = "validity_certificate_path")
    private String validityCertificatePath;

    @Column(name = "ncl_certificate_path")
    private String nclCertificatePath;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }

    public SpotRegistrationDocuments() {}

    public SpotRegistrationDocuments(String candidateId) {
        this.candidateId = candidateId;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public String getMarksheetPath() { return marksheetPath; }
    public void setMarksheetPath(String marksheetPath) { this.marksheetPath = marksheetPath; }

    public String getCasteCertificatePath() { return casteCertificatePath; }
    public void setCasteCertificatePath(String casteCertificatePath) { this.casteCertificatePath = casteCertificatePath; }

    public String getValidityCertificatePath() { return validityCertificatePath; }
    public void setValidityCertificatePath(String validityCertificatePath) { this.validityCertificatePath = validityCertificatePath; }

    public String getNclCertificatePath() { return nclCertificatePath; }
    public void setNclCertificatePath(String nclCertificatePath) { this.nclCertificatePath = nclCertificatePath; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}