package com.spotentrywebsite.spot_admission_portal.Database;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(
        name = "merit_list_2026",
        // 🚨 FIXED: Changed 'applicationId' to the actual database column name 'application_id'
        schema = "public" ,
        indexes = { @Index(name = "idx_cet_app_id", columnList = "application_id") }
)
public class CetStudent {

    @Id
    @Column(name = "application_id", nullable = false, unique = true)
    private String applicationId;

    @Column(name = "merit_no")
    private Integer meritNo;

    @Column(name = "candidate_name")
    private String fullName;
    @Column(name = "category")
    private String category;
    @Column(name = "gender")
    private String gender;
    @Column(name = "pwd_def")
    private String pwd;
//    @Column(name = "")
//    private String orphan;   // "y" or "n"
//    private String ews;      // "y" or "n"
//    private String tfws;     // "y" or "n"

    // Percentiles (Entrance exam scores)
    @Column(name = "cet_pcm_percentile")
    private Double percentileOverall;

    @Column(name = "cet_physics_percentile")
    private Double physicsPercentile;

    @Column(name = "cet_chemistry_percentile")
    private Double chemistryPercentile;

    @Column(name = "cet_math_percentile")
    private Double mathsPercentile;

    // HSC (Class 12) board marks
    @Column(name = "hsc_pcm_pct")
    private Double hscPercentage;

    @Column(name = "merit_exam")
    private String examType;

    @Column(name = "hsc_physics_pct")
    private Double physicsBoardPercent;

//    @Column(name = "chem_board_percent")
//    private Double chemBoardPercent;

    @Column(name = "hsc_math_pct")
    private Double mathsBoardPercent;

    // SSC (Class 10) marks
    @Column(name = "ssc_total_pct")
    private Double sscOverallPercent;

    @Column(name = "ssc_math_pct")
    private Double sscMath;

    @Column(name = "ssc_english_pct")
    private Double sscEnglish;

    @Column(name = "ssc_science_pct")
    private Double sscScience;

    // --- GETTERS AND SETTERS ---

    public Integer getMeritNo() { return meritNo; }
    public void setMeritNo(Integer meritNo) { this.meritNo = meritNo; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPwd() { return pwd; }
    public void setPwd(String pwd) { this.pwd = pwd; }

//    public String getOrphan() { return orphan; }
//    public void setOrphan(String orphan) { this.orphan = orphan; }
//
//    public String getEws() { return ews; }
//    public void setEws(String ews) { this.ews = ews; }

//    public String getTfws() { return tfws; }
//    public void setTfws(String tfws) { this.tfws = tfws; }

    public void setExamType(String examType){this.examType = examType;}
public String getExamType(){return examType;}
    public Double getPercentileOverall() { return percentileOverall; }
    public void setPercentileOverall(Double percentileOverall) { this.percentileOverall = percentileOverall; }

    public Double getPhysicsPercentile() { return physicsPercentile; }
    public void setPhysicsPercentile(Double physicsPercentile) { this.physicsPercentile = physicsPercentile; }

    public Double getChemistryPercentile() { return chemistryPercentile; }
    public void setChemistryPercentile(Double chemistryPercentile) { this.chemistryPercentile = chemistryPercentile; }

    public Double getMathsPercentile() { return mathsPercentile; }
    public void setMathsPercentile(Double mathsPercentile) { this.mathsPercentile = mathsPercentile; }

    public Double getHscPercentage() { return hscPercentage; }
    public void setHscPercentage(Double hscPercentage) { this.hscPercentage = hscPercentage; }

    public Double getPhysicsBoardPercent() { return physicsBoardPercent; }
    public void setPhysicsBoardPercent(Double physicsBoardPercent) { this.physicsBoardPercent = physicsBoardPercent; }
//
//    public Double getChemBoardPercent() { return chemBoardPercent; }
//    public void setChemBoardPercent(Double chemBoardPercent) { this.chemBoardPercent = chemBoardPercent; }

    public Double getMathsBoardPercent() { return mathsBoardPercent; }
    public void setMathsBoardPercent(Double mathsBoardPercent) { this.mathsBoardPercent = mathsBoardPercent; }

    public Double getSscOverallPercent() { return sscOverallPercent; }
    public void setSscOverallPercent(Double sscOverallPercent) { this.sscOverallPercent = sscOverallPercent; }

    public Double getSscMath() { return sscMath; }
    public void setSscMath(Double sscMath) { this.sscMath = sscMath; }

    public Double getSscEnglish() { return sscEnglish; }
    public void setSscEnglish(Double sscEnglish) { this.sscEnglish = sscEnglish; }

    public Double getSscScience() { return sscScience; }
    public void setSscScience(Double sscScience) { this.sscScience = sscScience; }
}