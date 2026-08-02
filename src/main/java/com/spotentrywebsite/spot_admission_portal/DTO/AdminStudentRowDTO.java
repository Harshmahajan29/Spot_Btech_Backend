package com.spotentrywebsite.spot_admission_portal.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminStudentRowDTO {
    @JsonProperty("meritNo")
    private int meritNo;

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("pOverall")
    private double pOverall;

    @JsonProperty("pMath")
    private double pMath;

    @JsonProperty("pPhys")
    private double pPhys;

    @JsonProperty("pChem")
    private double pChem;

    @JsonProperty("hsc")
    private double hsc;

    @JsonProperty("category")
    private String category;

    public AdminStudentRowDTO(int meritNo, String appId, String name, double pOverall, double pMath, double pPhys, double pChem, double hsc, String category) {
        this.meritNo = meritNo;
        this.appId = appId;
        this.name = name;
        this.pOverall = pOverall;
        this.pMath = pMath;
        this.pPhys = pPhys;
        this.pChem = pChem;
        this.hsc = hsc;
        this.category = category;
    }

    // Standard public getters
    public int getMeritNo() { return meritNo; }
    public String getAppId() { return appId; }
    public String getName() { return name; }
    public double getpOverall() { return pOverall; }
    public double getpMath() { return pMath; }
    public double getpPhys() { return pPhys; }
    public double getpChem() { return pChem; }
    public double getHsc() { return hsc; }
    public String getCategory() { return category; }

    public void setMeritNo(int i) { this.meritNo = i;}
}