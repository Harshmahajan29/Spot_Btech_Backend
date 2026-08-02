package com.spotentrywebsite.spot_admission_portal.Database;

import jakarta.persistence.*;

@Entity

@Table(
name = "all_india",
indexes = { @Index(name = "idx_jee_app_id", columnList = "applicationId") })



public class SaveAllIndia {

    @Column(name = "merit_no", nullable = false)
    private Integer meritNo;
    @Id
    @Column(name = "application_id", nullable = false, unique = true, length = 50)
    private String applicationId;

    @Column(name = "candidate_name", nullable = false, length = 255)
    private String candidateFullName; // Maps to candidate_name in DB

    @Column(name = "merit_exam_percentile_mark" , nullable = false)
    private Double jeePcmTotalPercentile; // Missing in your previous Java code, added to match DB

    // --- JEE Mains Columns ---

    @Column(name = "jee_math_percentile")
    private Double jeeMathScore;

    @Column(name = "jee_physics_percentile")
    private Double jeePhysicsScore;

    @Column(name = "jee_chemistry_percentile")
    private Double jeeChemistryScore;

    // --- MHT-CET Columns ---


    // --- HSC (Class 12) Metrics ---
    @Column(name = "hsc_pcm_percent")
    private Double hscPcmPercent;

    @Column(name = "hsc_math_percent")
    private Double hscMathPercent;

    @Column(name = "hsc_physics_percent")
    private Double hscPhysicsPercent;


    // --- GETTERS AND SETTERS ---

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public Integer getMeritNo() { return meritNo; }
    public void setMeritNo(Integer meritNo) { this.meritNo = meritNo; }

    public String getCandidateFullName() { return candidateFullName; }
    public void setCandidateFullName(String candidateFullName) { this.candidateFullName = candidateFullName; }

//    public String getMeritExam() { return meri; }
//    public void setMeritExam(String meritExam) { this.meritExam = meritExam; }
//
//    public String getGender() { return gender; }
//    public void setGender(String gender) { this.gender = gender; }

    public Double getJeePcmTotalPercentile() { return jeePcmTotalPercentile; }
    public void setJeePcmTotalPercentile(Double jeePcmTotalPercentile) { this.jeePcmTotalPercentile = jeePcmTotalPercentile; }

    public Double getJeeMathScore() { return jeeMathScore; }
    public void setJeeMathScore(Double jeeMathScore) { this.jeeMathScore = jeeMathScore; }

    public Double getJeePhysicsScore() { return jeePhysicsScore; }
    public void setJeePhysicsScore(Double jeePhysicsScore) { this.jeePhysicsScore = jeePhysicsScore; }

    public Double getJeeChemistryScore() { return jeeChemistryScore; }
    public void setJeeChemistryScore(Double jeeChemistryScore) { this.jeeChemistryScore = jeeChemistryScore; }



    public Double getHscPcmPercent() { return hscPcmPercent; }
    public void setHscPcmPercent(Double hscPcmPercent) { this.hscPcmPercent = hscPcmPercent; }

    public Double getHscMathPercent() { return hscMathPercent; }
    public void setHscMathPercent(Double hscMathPercent) { this.hscMathPercent = hscMathPercent; }

    public Double getHscPhysicsPercent() { return hscPhysicsPercent; }
    public void setHscPhysicsPercent(Double hscPhysicsPercent) { this.hscPhysicsPercent = hscPhysicsPercent; }






}
