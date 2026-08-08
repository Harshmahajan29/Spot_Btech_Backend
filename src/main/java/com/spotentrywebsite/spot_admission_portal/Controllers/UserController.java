package com.spotentrywebsite.spot_admission_portal.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotentrywebsite.spot_admission_portal.Database.CetStudent;
import com.spotentrywebsite.spot_admission_portal.Database.CetStudentRepository;
import com.spotentrywebsite.spot_admission_portal.Database.JeeStudent;
import com.spotentrywebsite.spot_admission_portal.Database.JeeStudentRepository;
import com.spotentrywebsite.spot_admission_portal.Database.OutsideMaharashtraCandidate;
import com.spotentrywebsite.spot_admission_portal.Database.OutsideMaharashtraCandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private OutsideMaharashtraCandidateRepository omsRepo;

    @Autowired
    private CetStudentRepository cetStudentRepository;

    @Autowired
    private JeeStudentRepository jeeStudentRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // =========================================================================
    // HELPER: SANITIZE NAME AND CATEGORY (PRESERVES NT-A/B/C/D & DEFENCE)
    // =========================================================================
    private Map<String, String> sanitizeNameAndCategory(String rawName, String rawCategory) {
        String name = rawName != null ? rawName.trim() : "Unknown Candidate";
        String category = rawCategory != null ? rawCategory.trim() : "";

        // Extract category embedded in name string if category column is blank or generic
        if (category.isEmpty() || category.equalsIgnoreCase("OPEN") || category.equalsIgnoreCase("NT")) {
            Pattern pattern = Pattern.compile("\\b(NT-[ABCD]|NT-A|NT-B|NT-C|NT-D|VJ/DT|DT/VJ|OBC|SC|ST|SBC)\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(name);

            if (matcher.find()) {
                category = matcher.group(1).toUpperCase();
            } else {
                Pattern parenPattern = Pattern.compile("\\((NT-[ABCD])\\)", Pattern.CASE_INSENSITIVE);
                Matcher parenMatcher = parenPattern.matcher(name);
                if (parenMatcher.find()) {
                    category = parenMatcher.group(1).toUpperCase();
                }
            }
        }

        // Clean up embedded tags from candidate name
        name = name.replaceAll("(?i)\\s*NT\\s*\\(NT-[ABCD]\\)", "")
                .replaceAll("(?i)\\s*\\(NT-[ABCD]\\)", "")
                .replaceAll("(?i)\\s*\\bNT-[ABCD]\\b", "")
                .trim();

        if (category.isEmpty()) {
            category = "OPEN";
        }

        return Map.of("name", name, "category", category);
    }

    // =========================================================================
    // 1. PRE-REGISTRATION CHECK
    // =========================================================================
    @GetMapping("/user/check-registration/{appId}")
    public ResponseEntity<Boolean> checkRegistrationExists(@PathVariable String appId) {
        String targetId = appId.trim().toUpperCase();
        String query = "SELECT COUNT(*) FROM spot_registrations WHERE application_id = ? " +
                "UNION ALL SELECT COUNT(*) FROM all_india WHERE application_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, targetId);
            ps.setString(2, targetId);
            try (ResultSet rs = ps.executeQuery()) {
                int total = 0;
                while (rs.next()) total += rs.getInt(1);
                return ResponseEntity.ok(total > 0);
            }
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    // =========================================================================
    // 1b. RICH REGISTRATION-STATUS CHECK (item 6 — used by the frontend both
    //    right after profile fetch and again right before final submit)
    // =========================================================================
    @GetMapping("/user/registration-status/{appId}")
    public ResponseEntity<Map<String, Object>> getRegistrationStatus(@PathVariable String appId) {
        String targetId = appId.trim().toUpperCase();
        Map<String, Object> result = new LinkedHashMap<>();

        String candidateQuery =
                "SELECT full_name, category, email, phone_no, " +
                        "hsc_percentage, physics_board_percent, chemistry_board_percent, maths_board_percent, defence_priority " +
                        "FROM spot_registrations WHERE application_id = ? " +
                        "UNION ALL " +
                        "SELECT candidate_name, 'JEE' as category, email, phone_no, " +
                        "hsc_pcm_percent as hsc_percentage, NULL as physics_board_percent, NULL as chemistry_board_percent, NULL as maths_board_percent, NULL as defence_priority " +
                        "FROM all_india WHERE application_id = ? " +
                        "LIMIT 1";

        try (Connection conn = dataSource.getConnection()) {
            Map<String, Object> candidate = null;
            try (PreparedStatement ps = conn.prepareStatement(candidateQuery)) {
                ps.setString(1, targetId);
                ps.setString(2, targetId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        candidate = new LinkedHashMap<>();
                        candidate.put("applicationId", targetId);
                        candidate.put("fullName", rs.getString(1));
                        candidate.put("category", rs.getString(2));
                        candidate.put("email", rs.getString(3));
                        candidate.put("phoneNo", rs.getString(4));

                        Map<String, Object> hscDetails = new LinkedHashMap<>();
                        hscDetails.put("hscPercentage", rs.getObject(5));
                        hscDetails.put("hscPhysicsPercent", rs.getObject(6));
                        hscDetails.put("hscChemistryPercent", rs.getObject(7));
                        hscDetails.put("hscMathPercent", rs.getObject(8));
                        candidate.put("hscDetails", hscDetails);

                        candidate.put("hscPercentage", rs.getObject(5));
                        candidate.put("hscPhysicsPercent", rs.getObject(6));
                        candidate.put("hscChemistryPercent", rs.getObject(7));
                        candidate.put("hscMathPercent", rs.getObject(8));
                        candidate.put("defencePriority", rs.getObject(9));
                    }
                }
            }

            result.put("registered", candidate != null);
            result.put("candidate", candidate);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("registered", false);
            result.put("error", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    // =========================================================================
    // 1c. CAP AUTO-VERIFY LOOKUP (FETCHES FROM merit_list_2026 DIRECTLY)
    // =========================================================================
    @GetMapping("/api/admin/fetch-and-verify/{appId}")
    public ResponseEntity<Map<String, Object>> fetchAndVerify(@PathVariable String appId) {
        String targetId = appId.trim().toUpperCase();

        Optional<CetStudent> cetOpt = cetStudentRepository.findById(targetId);
        Optional<JeeStudent> jeeOpt = jeeStudentRepository.findByApplicationId(targetId);

        if (cetOpt.isEmpty() && jeeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        boolean foundCet = cetOpt.isPresent();
        boolean foundJee = jeeOpt.isPresent();

        result.put("applicationId", targetId);
        result.put("foundCet", foundCet);
        result.put("foundJee", foundJee);
        result.put("eligiblePool", foundCet && foundJee ? "BOTH" : (foundJee ? "JEE" : "CET"));

        String fetchedName = foundCet ? cetOpt.get().getFullName() : jeeOpt.get().getCandidateFullName();
        String fetchedCategory = foundCet ? cetOpt.get().getCategory() : "OPEN";
        String fetchedPwd = foundCet ? cetOpt.get().getPwd() : "NO";

        // Query merit_list_2026 directly to extract accurate Defence status and Category
        String meritQuery = "SELECT candidate_name, category, pwd_def , gender FROM merit_list_2026 WHERE TRIM(application_id) = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(meritQuery)) {

            ps.setString(1, targetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String mName = rs.getString("candidate_name");
                    String mCat = rs.getString("category");
                    String pwdDef = rs.getString("pwd_def");

                    Map<String, String> sanitized = sanitizeNameAndCategory(mName, mCat);
                    fetchedName = sanitized.get("name");
                    fetchedCategory = sanitized.get("category");

                    // If pwd_def contains DEF, flag it as DEF while preserving main category
                    if (pwdDef != null && (pwdDef.toUpperCase().contains("DEF") || pwdDef.toUpperCase().contains("DEFENCE"))) {
                        fetchedPwd = "DEF";
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Direct merit_list_2026 lookup notice: " + e.getMessage());
        }

        result.put("name", fetchedName);
        result.put("category", fetchedCategory);
        result.put("gender", foundCet ? cetOpt.get().getGender() : "M");
        result.put("pwd", fetchedPwd);

        if (foundCet) {
            CetStudent c = cetOpt.get();
            result.put("cetOverall", c.getPercentileOverall());
            result.put("cetMath", c.getMathsPercentile());
            result.put("cetPhysics", c.getPhysicsPercentile());
            result.put("cetChemistry", c.getChemistryPercentile());

            // ---------------------------------------------------------------
            // item 5 FIX: "hsc" and "hscPcmPercent" were both being set from
            // c.getHscPercentage() (column hsc_pcm_pct — the PCM-group percentage),
            // so the frontend's "HSC Overall %" and "HSC PCM %" fields always
            // showed the identical number. c.getHscPercentageOverall() (column
            // diploma_dvoc_pct) is the actual Class XII overall board percentage
            // and belongs in "hsc" instead.
            // ---------------------------------------------------------------
            result.put("hsc", c.getHscPercentageOverall());
            result.put("hscPcmPercent", c.getHscPercentage());
            result.put("hscPhysics", c.getPhysicsBoardPercent());
            result.put("hscMaths", c.getMathsBoardPercent());

            result.put("ssc", c.getSscOverallPercent());
            result.put("sscMaths", c.getSscMath());
            result.put("sscScience", c.getSscScience());
            result.put("sscEnglish", c.getSscEnglish());
        }

        if (foundJee) {
            JeeStudent j = jeeOpt.get();
            result.put("jeeOverall", j.getJeePcmTotalPercentile());
            result.put("jeeMath", j.getJeeMathScore());
            result.put("jeePhysics", j.getJeePhysicsScore());
            result.put("jeeChemistry", j.getJeeChemistryScore());

            if (!foundCet) {
                // Same fix as above, mirrored for the JEE-only path: "hsc" now
                // comes from the overall board-percentage column instead of
                // duplicating the PCM-only percentile.
                result.put("hsc", j.getHscDiplomaDVocTotalPercent());
                result.put("hscPcmPercent", j.getHscPcmPercent());
                result.put("hscPhysics", j.getHscPhysicsPercent());
                result.put("hscMaths", j.getHscMathPercent());

                result.put("ssc", j.getSscTotalPercent());
                result.put("sscMaths", j.getSscMathPercent());
                result.put("sscScience", j.getSscSciencePercent());
                result.put("sscEnglish", j.getSscEnglishPercent());
            }
        }

        return ResponseEntity.ok(result);
    }

    // =========================================================================
    // 2. REGISTRATION ROUTE — MULTIPART, JSON-ONLY PAYLOAD (payment gateway
    //    removed — this endpoint persists the candidate directly; nothing here
    //    writes to payment_records). Document files are no longer sent to this
    //    endpoint at all: the frontend uploads them directly to Supabase Storage
    //    and sends only the resulting public URLs inside studentDataJson
    //    (marksheetDocUrl / casteValidityDocUrl / nclDocUrl / defenceCertDocUrl).
    //    The request stays multipart/form-data purely because studentDataJson
    //    and appId are still sent as form fields.
    //
    //    REQUIRED DB MIGRATION before deploying this change:
    //      ALTER TABLE spot_registrations ADD COLUMN caste_validity_doc_url TEXT;
    //      ALTER TABLE spot_registrations ADD COLUMN ncl_doc_url TEXT;
    //      ALTER TABLE spot_registrations ADD COLUMN defence_cert_doc_url TEXT;
    //      ALTER TABLE spot_registrations ADD COLUMN marksheet_doc_url TEXT;
    // =========================================================================
    @PostMapping(value = "/user/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerStudent(
            @RequestParam(value = "appId", required = false) String formAppId,
            @RequestParam(value = "studentDataJson", required = false) String studentDataJson) {

        String rawAppId = "";
        Map<String, Object> data = null;

        try {
            if (studentDataJson != null && !studentDataJson.isEmpty()) {
                data = objectMapper.readValue(studentDataJson, Map.class);
                rawAppId = formAppId;
            }

            if (data == null) {
                return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "No parsing matrix profile data detected in incoming request payload."));
            }

            if ((rawAppId == null || rawAppId.isEmpty()) && data.containsKey("applicationId")) {
                rawAppId = data.get("applicationId").toString();
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Payload decoding failure: " + e.getMessage()));
        }

        String email = data.get("email") != null ? data.get("email").toString().trim() : "";
        String phoneNo = data.get("phoneNo") != null ? data.get("phoneNo").toString().trim() :
                (data.get("phone_no") != null ? data.get("phone_no").toString().trim() : "");

        if (email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Validation Failed: Email address is mandatory."));
        }
        if (phoneNo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Validation Failed: Phone number is mandatory."));
        }

        String rawFullName = data.get("fullName") != null ? (String) data.get("fullName") :
                (data.get("name") != null ? (String) data.get("name") :
                        (data.get("candidate_name") != null ? (String) data.get("candidate_name") : "Unknown Candidate"));

        String rawCategory = data.get("category") != null ? data.get("category").toString() : "";
        String gender = data.get("gender") != null ? data.get("gender").toString() : "MALE";
        String pwd = data.get("pwd") != null ? data.get("pwd").toString() : "NO";

        String sanitizedAppId = rawAppId == null ? "" : rawAppId.trim().toUpperCase();
        if (sanitizedAppId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Application Identification String missing."));
        }

        // item 6: belt-and-braces server-side duplicate guard, in addition to the
        // frontend's pre-flight checks against /user/check-registration and
        // /user/registration-status (which run right after profile fetch and
        // again right before this call).
        String dupCheckSql = "SELECT COUNT(*) FROM spot_registrations WHERE application_id = ? " +
                "UNION ALL SELECT COUNT(*) FROM all_india WHERE application_id = ?";
        try (Connection dupConn = dataSource.getConnection();
             PreparedStatement dupPs = dupConn.prepareStatement(dupCheckSql)) {
            dupPs.setString(1, sanitizedAppId);
            dupPs.setString(2, sanitizedAppId);
            try (ResultSet rs = dupPs.executeQuery()) {
                int total = 0;
                while (rs.next()) total += rs.getInt(1);
                if (total > 0) {
                    return ResponseEntity.status(409).body(Map.of(
                            "status", "ALREADY_REGISTERED",
                            "message", "A candidate with Application ID " + sanitizedAppId + " is already registered."));
                }
            }
        } catch (Exception dupEx) {
            System.err.println("Duplicate-check lookup notice: " + dupEx.getMessage());
        }

        // Fallback verification against merit_list_2026 for Defence (pwd_def) & Category
        String meritQuery = "SELECT candidate_name, category, pwd_def FROM merit_list_2026 WHERE TRIM(application_id) = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(meritQuery)) {

            ps.setString(1, sanitizedAppId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String mName = rs.getString("candidate_name");
                    String mCat = rs.getString("category");
                    String pwdDef = rs.getString("pwd_def");

                    if (rawCategory.isEmpty() || rawCategory.equalsIgnoreCase("OPEN") || rawCategory.equalsIgnoreCase("DEFENCE")) {
                        rawCategory = mCat;
                    }
                    if (rawFullName.equals("Unknown Candidate")) {
                        rawFullName = mName;
                    }

                    // Check if candidate is marked DEFENCE in pwd_def column
                    if (pwdDef != null && (pwdDef.toUpperCase().contains("DEF") || pwdDef.toUpperCase().contains("DEFENCE"))) {
                        pwd = "DEF";
                    }
                }
            }
        } catch (Exception ignored) { }

        // Sanitize name and extract precise Category (e.g. NT-D, OBC, SC)
        Map<String, String> sanitized = sanitizeNameAndCategory(rawFullName, rawCategory);
        String fullName = sanitized.get("name");
        String category = sanitized.get("category");

        // Set pwd to DEF if Defence payload parameter received
        if (pwd != null && (pwd.trim().equalsIgnoreCase("DEF") || pwd.trim().equalsIgnoreCase("DEFENCE"))) {
            pwd = "DEF";
        }

        // Defence Priority Quota: rank 1 (Priority I) through 9 (Priority IX), sent by
        // the frontend only when pwd === "DEF". Required so the admin dashboard can sort
        // defence-quota candidates by priority ahead of / within their category, per the
        // CET Cell circular (Priority I = battle-casualty widows/wards, down to Priority IX).
        Integer defencePriority = parseInteger(data, "defencePriority", "priority", "defencePriorityRank");
        if (!"DEF".equals(pwd)) {
            // Only meaningful for Defence-quota candidates — don't persist a stray value
            // if pwd somehow isn't DEF despite a priority being present in the payload.
            defencePriority = null;
        }

        String examType = data.get("examType") != null ? data.get("examType").toString() :
                (data.get("exam_type") != null ? data.get("exam_type").toString() : "CET");

        boolean foundCet = data.get("foundCet") != null && Boolean.parseBoolean(data.get("foundCet").toString());
        boolean foundJee = data.get("foundJee") != null && Boolean.parseBoolean(data.get("foundJee").toString());

        if (!foundCet && !foundJee) {
            foundCet = true;
        }

        boolean isOms = category != null && category.contains("Applicable");

        Double pOverall = parseDouble(data, "percentileOverall", "pOverall", "cetOverall");
        Double pMaths = parseDouble(data, "mathsPercentile", "pMaths", "cetMath");
        Double pPhys = parseDouble(data, "physicsPercentile", "pPhys", "cetPhysics");
        Double pChem = parseDouble(data, "chemistryPercentile", "pChem", "cetChemistry");
        Double hscPct = parseDouble(data, "hscPercentage", "hscPct", "hsc_percentage", "hscDiplomaVocTotalPercent", "hsc");

        Double hscPhysics = parseDouble(data, "hscPhysicsPercent", "hscPhysics", "physics_board_percent");
        // NOTE: HSC Chemistry marks were removed from the frontend form. The frontend no
        // longer sends "hscChemistryPercent" for manual/Non-CAP candidates, so this will
        // resolve to 0.0 for those records going forward. The CAP automated-lookup flow
        // (fetchAndVerify) never populated a board-level chemistry figure either, so this
        // column has effectively been unused there too. The chemistry_board_percent
        // column itself is left in place for historical rows already in the table —
        // flagging in case you want to drop it in a future migration.
        Double hscChemistry = parseDouble(data, "hscChemistryPercent", "hscChemistry", "chemistry_board_percent");
        Double hscMath = parseDouble(data, "hscMathPercent", "hscMaths", "maths_board_percent");

        Double jeeOverall = parseDouble(data, "jeePcmTotalPercentile", "jeeOverall", "meritExamPercentileMark");
        Double jeeMath = parseDouble(data, "jeeMathPercentile", "jeeMathScore", "jeeMath");
        Double jeePhys = parseDouble(data, "jeePhysicsPercentile", "jeePhysicsScore", "jeePhysics");
        Double jeeChem = parseDouble(data, "jeeChemistryPercentile", "jeeChemistryScore", "jeeChemistry");

        // item 2: document URLs — uploaded client-side directly to Supabase Storage.
        // Any of these may be null (document not required / not uploaded).
        String marksheetDocUrl = stringOrNull(data, "marksheetDocUrl");
        String casteValidityDocUrl = stringOrNull(data, "casteValidityDocUrl");
        String nclDocUrl = stringOrNull(data, "nclDocUrl");
        String defenceCertDocUrl = stringOrNull(data, "defenceCertDocUrl");

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try {
                if (foundCet) {
                    // Spot registration insert: Stores main category in category, defence flag in pwd,
                    // the Defence priority rank (1-9) when pwd == "DEF", and now the Supabase document
                    // URLs (see migration note above the method for the required ALTER TABLE statements).
                    String insertStateSql = "INSERT INTO spot_registrations (" +
                            "application_id, full_name, category, gender, pwd, exam_type, " +
                            "percentile_overall, maths_percentile, physics_percentile, chemistry_percentile, " +
                            "hsc_percentage, physics_board_percent, chemistry_board_percent, maths_board_percent, " +
                            "defence_priority, " +
                            "caste_validity_doc_url, ncl_doc_url, defence_cert_doc_url, marksheet_doc_url, " +
                            "email, phone_no) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement psState = conn.prepareStatement(insertStateSql)) {
                        psState.setString(1, sanitizedAppId);
                        psState.setString(2, fullName);
                        psState.setString(3, category);
                        psState.setString(4, gender);
                        psState.setString(5, pwd);
                        psState.setString(6, examType);
                        psState.setDouble(7, pOverall);
                        psState.setDouble(8, pMaths);
                        psState.setDouble(9, pPhys);
                        psState.setDouble(10, pChem);
                        psState.setDouble(11, hscPct);
                        psState.setDouble(12, hscPhysics);
                        psState.setDouble(13, hscChemistry);
                        psState.setDouble(14, hscMath);
                        if (defencePriority != null) {
                            psState.setInt(15, defencePriority);
                        } else {
                            psState.setNull(15, Types.INTEGER);
                        }
                        psState.setString(16, casteValidityDocUrl);
                        psState.setString(17, nclDocUrl);
                        psState.setString(18, defenceCertDocUrl);
                        psState.setString(19, marksheetDocUrl);
                        psState.setString(20, email);
                        psState.setString(21, phoneNo);
                        psState.executeUpdate();
                    }
                }

                if (foundJee) {
                    String insertJeeSql = "INSERT INTO all_india (application_id, candidate_name, " +
                            "merit_exam_percentile_mark, jee_math_percentile, jee_physics_percentile, jee_chemistry_percentile, hsc_pcm_percent, email, phone_no) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement psJee = conn.prepareStatement(insertJeeSql)) {
                        psJee.setString(1, sanitizedAppId);
                        psJee.setString(2, fullName);
                        psJee.setDouble(3, jeeOverall);
                        psJee.setDouble(4, jeeMath);
                        psJee.setDouble(5, jeePhys);
                        psJee.setDouble(6, jeeChem);
                        psJee.setDouble(7, hscPct);
                        psJee.setString(8, email);
                        psJee.setString(9, phoneNo);
                        psJee.executeUpdate();
                    }
                }

                conn.commit();

                if (isOms && foundCet) {
                    try {
                        OutsideMaharashtraCandidate oms = new OutsideMaharashtraCandidate();
                        oms.setApplicationId(sanitizedAppId);
                        oms.setFullName(fullName);
                        oms.setExamType(examType);
                        oms.setPwd(pwd);
                        oms.setPercentileOverall(pOverall);
                        oms.setMathsPercentile(pMaths);
                        oms.setPhysicsPercentile(pPhys);
                        oms.setChemistryPercentile(pChem);
                        oms.setHscPercentage(hscPct);
                        oms.setJeePcmTotalPercentile(jeeOverall);
                        oms.setJeeMathScore(jeeMath);
                        oms.setJeePhysicsScore(jeePhys);
                        oms.setJeeChemistryScore(jeeChem);
                        oms.setEmail(email);
                        oms.setPhoneNo(phoneNo);
                        // TODO: OutsideMaharashtraCandidate entity needs a defencePriority
                        // field + setter added before this line can be uncommented:
                        // oms.setDefencePriority(defencePriority);

                        if (!omsRepo.existsById(sanitizedAppId)) {
                            omsRepo.save(oms);
                        }
                    } catch (Exception omsEx) {
                        System.err.println("OMS mirror insert failed for " + sanitizedAppId + ": " + omsEx.getMessage());
                    }
                }

                return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Candidate successfully processed into transaction pools."));

            } catch (Exception innerEx) {
                conn.rollback();
                throw innerEx;
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "ERROR", "message", "Database operations tracking failure: " + e.getMessage()));
        }
    }

    private Double parseDouble(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null) {
                try {
                    return Double.parseDouble(map.get(key).toString());
                } catch (NumberFormatException ignored) {}
            }
        }
        return 0.0;
    }

    // New: mirrors parseDouble but returns null (not 0) when absent/unparseable, since
    // 0 is not a safe default for defencePriority — it would collide with "no priority
    // set" and rank as if it were higher than Priority I in a naive comparator.
    private Integer parseInteger(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null) {
                try {
                    return Integer.parseInt(map.get(key).toString());
                } catch (NumberFormatException ignored) {}
            }
        }
        return null;
    }

    // item 2: pulls a Supabase public document URL out of the payload, or null
    // if that document wasn't uploaded (all document fields are optional here).
    private String stringOrNull(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }
}