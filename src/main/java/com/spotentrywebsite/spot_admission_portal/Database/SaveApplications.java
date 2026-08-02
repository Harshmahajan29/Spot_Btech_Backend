package com.spotentrywebsite.spot_admission_portal.Database;

import jakarta.persistence.*;

@Entity
@Table(name = "spot_registrations")
public class SaveApplications {

    @Id
    @Column(name = "application_id", nullable = false, unique = true)
    private String applicationId;

    @Column(name = "merit_no")
    private Integer meritNo;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "category")
    private String category;

    @Column(name = "gender")
    private String gender;

    @Column(name = "pwd")
    private String pwd;

    @Column(name = "orphan")
    private String isOrphan;

    @Column(name = "def")
    private String isDefence;

    // Percentiles (Entrance exam scores)
    @Column(name = "percentile_overall")
    private Double percentileOverall;

    @Column(name = "physics_percentile")
    private Double physicsPercentile;

    @Column(name = "chemistry_percentile")
    private Double chemistryPercentile;

    @Column(name = "maths_percentile")
    private Double mathsPercentile;

    // HSC (Class 12) board marks
    @Column(name = "hsc_percentage")
    private Double hscPercentage;

    @Column(name = "exam_type")
    private String examType;

    @Column(name = "physics_board_percent")
    private Double physicsBoardPercent;

    @Column(name = "chemistry_board_percent")
    private Double chemBoardPercent;

    @Column(name = "maths_board_percent")
    private Double mathsBoardPercent;

    // NEW FIELD: Stores user preferences as a comma-separated string (e.g., "CSE Aided,IT Aided")


    // --- Getters and Setters ---

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public Integer getMeritNo() { return meritNo; }
    public void setMeritNo(Integer meritNo) { this.meritNo = meritNo; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPwd() { return pwd; }
    public void setPwd(String pwd) { this.pwd = pwd; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

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

    public Double getChemBoardPercent() { return chemBoardPercent; }
    public void setChemBoardPercent(Double chemBoardPercent) { this.chemBoardPercent = chemBoardPercent; }

    public Double getMathsBoardPercent() { return mathsBoardPercent; }
    public void setMathsBoardPercent(Double mathsBoardPercent) { this.mathsBoardPercent = mathsBoardPercent; }

    public String getIsDefence() {
        return isDefence;
    }

    public void setIsDefence(String isDefence) {
        this.isDefence = isDefence;
    }

    public void setIsOrphan(String isOrphan) {
        this.isOrphan = isOrphan;
    }

    public String getIsOrphan() {
        return isOrphan;
    }
}
