package com.spotentrywebsite.spot_admission_portal.Controllers;

import com.spotentrywebsite.spot_admission_portal.DTO.AdminStudentRowDTO;
import com.spotentrywebsite.spot_admission_portal.Database.SaveSpotResgistrations;
import com.spotentrywebsite.spot_admission_portal.Database.SaveJeeApplications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/admin")
public class AdminMatrixController {

    @Autowired
    private SaveSpotResgistrations spotRepo;

    @Autowired
    private SaveJeeApplications jeeRepo;

    @GetMapping("/matrix")
    public ResponseEntity<List<AdminStudentRowDTO>> getLiveDashboardMatrix(
            @RequestParam(value = "activePool", defaultValue = "STATE") String activePool,
            @RequestParam(value = "category", defaultValue = "OPEN_ALL") String category,
            @RequestParam(value = "subQuota", defaultValue = "ALL") String subQuota) {

        // ==========================================
        // ROUTE A: JEE / ALL INDIA LIVE MATRIX POOL
        // ==========================================
        if ("ALL_INDIA".equalsIgnoreCase(activePool)) {
            List<AdminStudentRowDTO> jeeFilteredList = jeeRepo.findAll().stream()
                    // Hierarchical Tie-Breaker Sort Logic for JEE Pool
                    .sorted((a, b) -> {
                        double pOverallA = a.getJeePcmTotalPercentile() != null ? a.getJeePcmTotalPercentile() : 0.0;
                        double pOverallB = b.getJeePcmTotalPercentile() != null ? b.getJeePcmTotalPercentile() : 0.0;
                        int comp = Double.compare(pOverallB, pOverallA); // Descending
                        if (comp != 0) return comp;

                        // Tie 1: Compare JEE Math Percentile
                        double pMathA = a.getJeeMathScore() != null ? a.getJeeMathScore() : 0.0;
                        double pMathB = b.getJeeMathScore() != null ? b.getJeeMathScore() : 0.0;
                        comp = Double.compare(pMathB, pMathA);
                        if (comp != 0) return comp;

                        // Tie 2: Compare JEE Physics Percentile
                        double pPhysA = a.getJeePhysicsScore() != null ? a.getJeePhysicsScore() : 0.0;
                        double pPhysB = b.getJeePhysicsScore() != null ? b.getJeePhysicsScore() : 0.0;
                        comp = Double.compare(pPhysB, pPhysA);
                        if (comp != 0) return comp;

                        // Tie 3: Compare JEE Chemistry Percentile
                        double pChemA = a.getJeeChemistryScore() != null ? a.getJeeChemistryScore() : 0.0;
                        double pChemB = b.getJeeChemistryScore() != null ? b.getJeeChemistryScore() : 0.0;
                        comp = Double.compare(pChemB, pChemA);
                        if (comp != 0) return comp;

                        // Tie 4: Compare HSC Total Board Percentage
                        double hscA = a.getHscPcmPercent() != null ? a.getHscPcmPercent() : 0.0;
                        double hscB = b.getHscPcmPercent() != null ? b.getHscPcmPercent() : 0.0;
                        return Double.compare(hscB, hscA);
                    })
                    .map(jee -> new AdminStudentRowDTO(
                            0,
                            jee.getApplicationId(),
                            jee.getCandidateFullName(),
                            jee.getJeePcmTotalPercentile() != null ? jee.getJeePcmTotalPercentile() : 0.0,
                            jee.getJeeMathScore() != null ? jee.getJeeMathScore() : 0.0,
                            jee.getJeePhysicsScore() != null ? jee.getJeePhysicsScore() : 0.0,
                            jee.getJeeChemistryScore() != null ? jee.getJeeChemistryScore() : 0.0,
                            jee.getHscPcmPercent() != null ? jee.getHscPcmPercent() : 0.0,
                            "ALL INDIA (JEE)"
                    )).collect(Collectors.toList());

            for (int i = 0; i < jeeFilteredList.size(); i++) {
                jeeFilteredList.get(i).setMeritNo(i + 1);
            }

            return ResponseEntity.ok(jeeFilteredList);
        }

        // ==========================================
        // ROUTE B: STATE / MHT-CET LIVE MATRIX POOL
        // ==========================================
        List<AdminStudentRowDTO> stateFilteredList = spotRepo.findAll().stream()
                .filter(student -> {
                    String studentCat = student.getCategory() != null ? student.getCategory().trim().toUpperCase() : "OPEN";
                    String pwd = student.getPwd() != null ? student.getPwd().trim().toUpperCase() : "-";

                    if ("OPEN_PURE".equalsIgnoreCase(category)) {
                        return "OPEN".equals(studentCat) && (pwd.isBlank() || pwd.equals("-") || pwd.equals("NO"));
                    } else if (!"OPEN_ALL".equalsIgnoreCase(category)) {
                        return category.equalsIgnoreCase(studentCat);
                    }
                    return true;
                })
                .filter(student -> {
                    if ("ALL".equalsIgnoreCase(subQuota)) return true;

                    String pwd = student.getPwd() != null ? student.getPwd().toUpperCase() : "";
                    String gender = student.getGender() != null ? student.getGender().toUpperCase() : "";

                    if ("PWD".equalsIgnoreCase(subQuota)) {
                        return !pwd.isBlank() && !pwd.equals("-") && !pwd.equals("NO");
                    } else if ("LADIES".equalsIgnoreCase(subQuota)) {
                        return "FEMALE".equals(gender) || "F".equals(gender);
                    } else if ("ORPHAN".equalsIgnoreCase(subQuota)) {
                        return pwd.contains("ORPHAN");
                    } else if ("DEFENCE".equalsIgnoreCase(subQuota)) {
                        return pwd.contains("DEFENCE");
                    }
                    return true;
                })
                // Hierarchical Tie-Breaker Sort Logic for MHT-CET State Pool
                .sorted((a, b) -> {
                    double pOverallA = a.getPercentileOverall() != null ? a.getPercentileOverall() : 0.0;
                    double pOverallB = b.getPercentileOverall() != null ? b.getPercentileOverall() : 0.0;
                    int comp = Double.compare(pOverallB, pOverallA); // Descending
                    if (comp != 0) return comp;

                    // Tie 1: Compare CET Maths Percentile
                    double pMathA = a.getMathsPercentile() != null ? a.getMathsPercentile() : 0.0;
                    double pMathB = b.getMathsPercentile() != null ? b.getMathsPercentile() : 0.0;
                    comp = Double.compare(pMathB, pMathA);
                    if (comp != 0) return comp;

                    // Tie 2: Compare CET Physics Percentile
                    double pPhysA = a.getPhysicsPercentile() != null ? a.getPhysicsPercentile() : 0.0;
                    double pPhysB = b.getPhysicsPercentile() != null ? b.getPhysicsPercentile() : 0.0;
                    comp = Double.compare(pPhysB, pPhysA);
                    if (comp != 0) return comp;

                    // Tie 3: Compare CET Chemistry Percentile
                    double pChemA = a.getChemistryPercentile() != null ? a.getChemistryPercentile() : 0.0;
                    double pChemB = b.getChemistryPercentile() != null ? b.getChemistryPercentile() : 0.0;
                    comp = Double.compare(pChemB, pChemA);
                    if (comp != 0) return comp;

                    // Tie 4: Compare HSC Board Percentage fallback
                    double hscA = a.getHscPercentage() != null ? a.getHscPercentage() : 0.0;
                    double hscB = b.getHscPercentage() != null ? b.getHscPercentage() : 0.0;
                    return Double.compare(hscB, hscA);
                })
                .map(state -> new AdminStudentRowDTO(
                        0,
                        state.getApplicationId(),
                        state.getFullName(),
                        state.getPercentileOverall() != null ? state.getPercentileOverall() : 0.0,
                        state.getMathsPercentile() != null ? state.getMathsPercentile() : 0.0,
                        state.getPhysicsPercentile() != null ? state.getPhysicsPercentile() : 0.0,
                        state.getChemistryPercentile() != null ? state.getChemistryPercentile() : 0.0,
                        state.getHscPercentage() != null ? state.getHscPercentage() : 0.0,
                        state.getCategory()
                )).collect(Collectors.toList());

        for (int i = 0; i < stateFilteredList.size(); i++) {
            stateFilteredList.get(i).setMeritNo(i + 1);
        }

        return ResponseEntity.ok(stateFilteredList);
    }
}