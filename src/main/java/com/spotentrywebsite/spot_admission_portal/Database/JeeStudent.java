package com.spotentrywebsite.spot_admission_portal.Database;

import jakarta.persistence.*;

@Entity
@Table(
        name = "all_india_merit_list_2026_full",
        indexes = { @Index(name = "idx_jee_app_id", columnList = "applicationId") }
)
public class JeeStudent {
    @Id
    @Column(name = "merit_no", nullable = false)
    private Integer meritNo;

    @Column(name = "application_id", nullable = false, unique = true, length = 50)
    private String applicationId;

    @Column(name = "candidate_name", nullable = false, length = 255)
    private String candidateFullName; // Maps to candidate_name in DB

    @Column(name = "merit_exam_percentile" , nullable = false)
    private Double jeePcmTotalPercentile; // Missing in your previous Java code, added to match DB

    // --- JEE Mains Columns ---

    @Column(name = "jee_math_percentile")
    private Double jeeMathScore;

    @Column(name = "jee_physics_percentile")
    private Double jeePhysicsScore;

    @Column(name = "jee_chemistry_percentile")
    private Double jeeChemistryScore;

    // --- MHT-CET Columns ---
    @Column(name = "mht_cet_pcm_total_percentile")
    private Double mhtCetPcmTotalPercentile;

    @Column(name = "mht_cet_math_percentile")
    private Double mhtCetMathPercentile;

    @Column(name = "mht_cet_physics_percentile")
    private Double mhtCetPhysicsPercentile;

    @Column(name = "mht_cet_chemistry_percentile")
    private Double mhtCetChemistryPercentile;

    // --- HSC (Class 12) Metrics ---
    @Column(name = "hsc_pcm_pct")
    private Double hscPcmPercent;

    @Column(name = "hsc_math_pct")
    private Double hscMathPercent;

    @Column(name = "hsc_physics_pct")
    private Double hscPhysicsPercent;

    @Column(name = "hsc_diploma_dvoc_total_pct") // Fixed 'dvoc' to 'voc' to match your schema
    private Double hscDiplomaDVocTotalPercent;

    // --- SSC (Class 10) Metrics ---
    @Column(name = "ssc_total_pct")
    private Double sscTotalPercent;

    @Column(name = "ssc_math_pct")
    private Double sscMathPercent;

    @Column(name = "ssc_science_pct")
    private Double sscSciencePercent;

    @Column(name = "ssc_english_pct")
    private Double sscEnglishPercent;
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

    public Double getMhtCetPcmTotalPercentile() { return mhtCetPcmTotalPercentile; }
    public void setMhtCetPcmTotalPercentile(Double mhtCetPcmTotalPercentile) { this.mhtCetPcmTotalPercentile = mhtCetPcmTotalPercentile; }

    public Double getMhtCetMathPercentile() { return mhtCetMathPercentile; }
    public void setMhtCetMathPercentile(Double mhtCetMathPercentile) { this.mhtCetMathPercentile = mhtCetMathPercentile; }

    public Double getMhtCetPhysicsPercentile() { return mhtCetPhysicsPercentile; }
    public void setMhtCetPhysicsPercentile(Double mhtCetPhysicsPercentile) { this.mhtCetPhysicsPercentile = mhtCetPhysicsPercentile; }

    public Double getMhtCetChemistryPercentile() { return mhtCetChemistryPercentile; }
    public void setMhtCetChemistryPercentile(Double mhtCetChemistryPercentile) { this.mhtCetChemistryPercentile = mhtCetChemistryPercentile; }

    public Double getHscPcmPercent() { return hscPcmPercent; }
    public void setHscPcmPercent(Double hscPcmPercent) { this.hscPcmPercent = hscPcmPercent; }

    public Double getHscMathPercent() { return hscMathPercent; }
    public void setHscMathPercent(Double hscMathPercent) { this.hscMathPercent = hscMathPercent; }

    public Double getHscPhysicsPercent() { return hscPhysicsPercent; }
    public void setHscPhysicsPercent(Double hscPhysicsPercent) { this.hscPhysicsPercent = hscPhysicsPercent; }

    public Double getHscDiplomaDVocTotalPercent() { return hscDiplomaDVocTotalPercent; }
    public void setHscDiplomaDVocTotalPercent(Double hscDiplomaDVocTotalPercent) { this.hscDiplomaDVocTotalPercent = hscDiplomaDVocTotalPercent; }

    public Double getSscTotalPercent() { return sscTotalPercent; }
    public void setSscTotalPercent(Double sscTotalPercent) { this.sscTotalPercent = sscTotalPercent; }

    public Double getSscMathPercent() { return sscMathPercent; }
    public void setSscMathPercent(Double sscMathPercent) { this.sscMathPercent = sscMathPercent; }

    public Double getSscSciencePercent() { return sscSciencePercent; }
    public void setSscSciencePercent(Double sscSciencePercent) { this.sscSciencePercent = sscSciencePercent; }

    public Double getSscEnglishPercent() { return sscEnglishPercent; }
    public void setSscEnglishPercent(Double sscEnglishPercent) { this.sscEnglishPercent = sscEnglishPercent; }
}