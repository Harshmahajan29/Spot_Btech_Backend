package com.spotentrywebsite.spot_admission_portal.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotentrywebsite.spot_admission_portal.Database.DiplomaStudent;
import com.spotentrywebsite.spot_admission_portal.Database.DiplomaStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/diploma")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DiplomaController {

    @Autowired
    private DiplomaStudentRepository diplomaRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // =========================================================================
    // 1. DIPLOMA REGISTRATION ENDPOINT (MULTIPART) — item 2
    // =========================================================================
    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerDiplomaStudent(
            @RequestParam(value = "studentDataJson", required = false) String studentDataJson,
            @RequestParam(value = "marksheetFile", required = false) MultipartFile marksheetFile,
            @RequestParam(value = "casteFile", required = false) MultipartFile casteFile,
            @RequestParam(value = "nclFile", required = false) MultipartFile nclFile) {

        if (studentDataJson == null || studentDataJson.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "No diploma profile data detected in incoming request payload."));
        }

        Map<String, Object> data;
        try {
            data = objectMapper.readValue(studentDataJson, Map.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Payload decoding failure: " + e.getMessage()));
        }

        // Item 4: email + mobile mandatory for every candidate
        String email = data.get("email") != null ? data.get("email").toString().trim() : "";
        String phoneNo = data.get("phoneNo") != null ? data.get("phoneNo").toString().trim() :
                (data.get("mobile_no") != null ? data.get("mobile_no").toString().trim() : "");

        if (email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Validation Failed: Email address is mandatory."));
        }
        if (phoneNo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Validation Failed: Mobile number is mandatory."));
        }

        String appId = data.get("applicationId") != null ? data.get("applicationId").toString() :
                (data.get("candidate_id") != null ? data.get("candidate_id").toString() : "");
        appId = appId.trim().toUpperCase();
        if (appId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Application Identification String missing."));
        }

        if (diplomaRepo.existsById(appId)) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "This Application ID is already registered in the Diploma pool."));
        }

        DiplomaStudent d = new DiplomaStudent();
        d.setApplicationId(appId);
        d.setFullName(firstNonEmpty(data, "fullName", "candidateName", "diploma_branch"));
        d.setDiplomaBranch(firstNonEmpty(data, "diploma_branch", "diplomaBranch"));
        d.setCategory(firstNonEmpty(data, "registered_category", "category"));
        d.setFinalEligibleCategory(firstNonEmpty(data, "final_eligible_category", "category"));
        d.setSystemFlag(firstNonEmpty(data, "system_flag", ""));
        d.setGender(firstNonEmpty(data, "gender", "M"));
        d.setPwd(firstNonEmpty(data, "pwd", "No"));

        d.setDiplomaPercentage(parseDouble(data, "diploma_percentage", "diplomaPercentage"));
        d.setSscTotal(parseDouble(data, "ssc_total", "sscOverallPercent", "sscTotal"));
        d.setSscMath(parseDouble(data, "ssc_maths_marks", "sscMathPercent", "sscMath"));
        d.setSscScience(parseDouble(data, "ssc_science", "sscSciencePercent", "sscScience"));
        d.setSscEnglish(parseDouble(data, "ssc_english", "sscEnglishPercent", "sscEnglish"));

        d.setEmail(email);
        d.setPhoneNo(phoneNo);

        diplomaRepo.save(d);

        // NOTE: marksheetFile / casteFile / nclFile are accepted here; wire up actual
        // file storage (disk/S3/DB blob) per your existing document-storage convention.

        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Diploma candidate successfully registered."));
    }

    // =========================================================================
    // 2. DIPLOMA MERIT LIST ENDPOINT — item 2 (sorted diploma% desc, then SSC tie-breaks)
    // =========================================================================
    @GetMapping("/merit-list")
    public ResponseEntity<List<Map<String, Object>>> getDiplomaMeritList() {
        List<DiplomaStudent> students = diplomaRepo.findAll();

        Comparator<DiplomaStudent> comparator = Comparator
                .comparingDouble((DiplomaStudent s) -> nz(s.getDiplomaPercentage())).reversed()
                .thenComparing(Comparator.comparingDouble((DiplomaStudent s) -> nz(s.getSscTotal())).reversed())
                .thenComparing(Comparator.comparingDouble((DiplomaStudent s) -> nz(s.getSscMath())).reversed())
                .thenComparing(Comparator.comparingDouble((DiplomaStudent s) -> nz(s.getSscScience())).reversed())
                .thenComparing(Comparator.comparingDouble((DiplomaStudent s) -> nz(s.getSscEnglish())).reversed());

        List<DiplomaStudent> sorted = students.stream().sorted(comparator).collect(Collectors.toList());

        List<Map<String, Object>> result = new java.util.ArrayList<>();
        int rank = 1;
        for (DiplomaStudent s : sorted) {
            Map<String, Object> row = new java.util.LinkedHashMap<>();
            row.put("meritNo", rank++);
            row.put("appId", s.getApplicationId());
            row.put("name", s.getFullName());
            row.put("branch", s.getDiplomaBranch());
            row.put("diplomaPercent", nz(s.getDiplomaPercentage()));
            row.put("sscTotal", nz(s.getSscTotal()));
            row.put("sscMath", nz(s.getSscMath()));
            row.put("sscScience", nz(s.getSscScience()));
            row.put("sscEnglish", nz(s.getSscEnglish()));
            row.put("category", s.getFinalEligibleCategory());
            row.put("email", s.getEmail());
            row.put("phoneNo", s.getPhoneNo());
            result.add(row);
        }

        return ResponseEntity.ok(result);
    }

    private double nz(Double v) { return v != null ? v : 0.0; }

    private String firstNonEmpty(Map<String, Object> data, String... keys) {
        for (String k : keys) {
            Object v = data.get(k);
            if (v != null && !v.toString().trim().isEmpty()) return v.toString().trim();
        }
        return "";
    }

    private Double parseDouble(Map<String, Object> data, String... keys) {
        for (String k : keys) {
            Object v = data.get(k);
            if (v != null && !v.toString().trim().isEmpty()) {
                try { return Double.parseDouble(v.toString()); } catch (NumberFormatException ignored) {}
            }
        }
        return 0.0;
    }
}