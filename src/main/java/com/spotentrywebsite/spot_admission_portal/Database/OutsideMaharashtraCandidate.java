package com.spotentrywebsite.spot_admission_portal.Database;

import jakarta.persistence.*;

@Entity
@Table(name = "outside_maharashtra_candidates")
public class OutsideMaharashtraCandidate {

    @Id
    @Column(name = "application_id", nullable = false, unique = true)
    private String applicationId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "exam_type")
    private String examType;

    @Column(name = "pwd")
    private String pwd;

    // NOTE: gender and category are intentionally NOT stored here (business rule)

    @Column(name = "percentile_overall")
    private Double percentileOverall;

    @Column(name = "maths_percentile")
    private Double mathsPercentile;

    @Column(name = "physics_percentile")
    private Double physicsPercentile;

    @Column(name = "chemistry_percentile")
    private Double chemistryPercentile;

    @Column(name = "hsc_percentage")
    private Double hscPercentage;

    @Column(name = "jee_pcm_total_percentile")
    private Double jeePcmTotalPercentile;

    @Column(name = "jee_math_score")
    private Double jeeMathScore;

    @Column(name = "jee_physics_score")
    private Double jeePhysicsScore;

    @Column(name = "jee_chemistry_score")
    private Double jeeChemistryScore;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_no")
    private String phoneNo;

    // --- Getters and Setters ---
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public String getPwd() { return pwd; }
    public void setPwd(String pwd) { this.pwd = pwd; }

    public Double getPercentileOverall() { return percentileOverall; }
    public void setPercentileOverall(Double percentileOverall) { this.percentileOverall = percentileOverall; }

    public Double getMathsPercentile() { return mathsPercentile; }
    public void setMathsPercentile(Double mathsPercentile) { this.mathsPercentile = mathsPercentile; }

    public Double getPhysicsPercentile() { return physicsPercentile; }
    public void setPhysicsPercentile(Double physicsPercentile) { this.physicsPercentile = physicsPercentile; }

    public Double getChemistryPercentile() { return chemistryPercentile; }
    public void setChemistryPercentile(Double chemistryPercentile) { this.chemistryPercentile = chemistryPercentile; }

    public Double getHscPercentage() { return hscPercentage; }
    public void setHscPercentage(Double hscPercentage) { this.hscPercentage = hscPercentage; }

    public Double getJeePcmTotalPercentile() { return jeePcmTotalPercentile; }
    public void setJeePcmTotalPercentile(Double jeePcmTotalPercentile) { this.jeePcmTotalPercentile = jeePcmTotalPercentile; }

    public Double getJeeMathScore() { return jeeMathScore; }
    public void setJeeMathScore(Double jeeMathScore) { this.jeeMathScore = jeeMathScore; }

    public Double getJeePhysicsScore() { return jeePhysicsScore; }
    public void setJeePhysicsScore(Double jeePhysicsScore) { this.jeePhysicsScore = jeePhysicsScore; }

    public Double getJeeChemistryScore() { return jeeChemistryScore; }
    public void setJeeChemistryScore(Double jeeChemistryScore) { this.jeeChemistryScore = jeeChemistryScore; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }
}