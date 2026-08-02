package com.spotentrywebsite.spot_admission_portal.Database;

import jakarta.persistence.*;

@Entity
@Table(name = "diploma_students")
public class DiplomaStudent {

    @Id
    @Column(name = "application_id", nullable = false, unique = true)
    private String applicationId;

    @Column(name = "merit_no")
    private Integer meritNo;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "diploma_branch")
    private String diplomaBranch;

    @Column(name = "category")
    private String category;

    @Column(name = "final_eligible_category")
    private String finalEligibleCategory;

    @Column(name = "system_flag")
    private String systemFlag;

    @Column(name = "gender")
    private String gender;

    @Column(name = "pwd")
    private String pwd;

    // Item 2: diploma%, ssc_total, ssc_science, ssc_math, ssc_english
    @Column(name = "diploma_percentage")
    private Double diplomaPercentage;

    @Column(name = "ssc_total")
    private Double sscTotal;

    @Column(name = "ssc_science")
    private Double sscScience;

    @Column(name = "ssc_math")
    private Double sscMath;

    @Column(name = "ssc_english")
    private Double sscEnglish;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_no")
    private String phoneNo;

    // --- Getters and Setters ---
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public Integer getMeritNo() { return meritNo; }
    public void setMeritNo(Integer meritNo) { this.meritNo = meritNo; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDiplomaBranch() { return diplomaBranch; }
    public void setDiplomaBranch(String diplomaBranch) { this.diplomaBranch = diplomaBranch; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFinalEligibleCategory() { return finalEligibleCategory; }
    public void setFinalEligibleCategory(String finalEligibleCategory) { this.finalEligibleCategory = finalEligibleCategory; }

    public String getSystemFlag() { return systemFlag; }
    public void setSystemFlag(String systemFlag) { this.systemFlag = systemFlag; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPwd() { return pwd; }
    public void setPwd(String pwd) { this.pwd = pwd; }

    public Double getDiplomaPercentage() { return diplomaPercentage; }
    public void setDiplomaPercentage(Double diplomaPercentage) { this.diplomaPercentage = diplomaPercentage; }

    public Double getSscTotal() { return sscTotal; }
    public void setSscTotal(Double sscTotal) { this.sscTotal = sscTotal; }

    public Double getSscScience() { return sscScience; }
    public void setSscScience(Double sscScience) { this.sscScience = sscScience; }

    public Double getSscMath() { return sscMath; }
    public void setSscMath(Double sscMath) { this.sscMath = sscMath; }

    public Double getSscEnglish() { return sscEnglish; }
    public void setSscEnglish(Double sscEnglish) { this.sscEnglish = sscEnglish; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }
}