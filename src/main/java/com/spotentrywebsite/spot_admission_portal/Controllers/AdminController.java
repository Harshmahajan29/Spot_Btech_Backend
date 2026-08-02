//package com.spotentrywebsite.spot_admission_portal.Controllers;
//
//import com.spotentrywebsite.spot_admission_portal.Database.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/admin")
//@CrossOrigin(origins = "*", allowedHeaders = "*")
//public class AdminController {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired
//    private JeeStudentRepository jeeMasterRepo;
//
//    @Autowired
//    private CetStudentRepository cetMasterRepo;
//
//    // =========================================================================
//    // 1. VERIFICATION ENDPOINT FOR CAP STUDENTS (CROSS-DATABASE LOOKUP)
//    // =========================================================================
//    @GetMapping("/fetch-and-verify/{appId}")
//    public ResponseEntity<?> verifyStudentPools(@PathVariable String appId) {
//        String targetId = appId.trim().toUpperCase();
//
//        boolean foundJee = false;
//        boolean foundCet = false;
//
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("applicationId", targetId);
//
//        // Default base fallbacks
//        payload.put("category", "OPEN");
//        payload.put("eligiblePool", "STATE_ONLY");
//
//        // Step A: Look into the Master JEE Table (jee_mht_cet_merit_2025)
//        Optional<JeeStudent> jeeMatch = jeeMasterRepo.findByApplicationId(targetId);
//        if (jeeMatch.isPresent()) {
//            JeeStudent jeeData = jeeMatch.get();
//            foundJee = true;
//
//            payload.put("name", jeeData.getCandidateFullName());
//            payload.put("eligiblePool", "BOTH");
//
//            // Map JEE specific score matrix metrics
//            payload.put("jeeOverall", jeeData.getJeePcmTotalPercentile());
//            payload.put("jeeMath", jeeData.getJeeMathScore());
//            payload.put("jeePhysics", jeeData.getJeePhysicsScore());
//            payload.put("jeeChemistry", jeeData.getJeeChemistryScore());
//
//            // Temporary map fallback fields from JEE record if CET table query misses later
//            payload.put("cetOverall", jeeData.getMhtCetPcmTotalPercentile());
//            payload.put("cetMath", jeeData.getMhtCetMathPercentile());
//            payload.put("cetPhysics", jeeData.getMhtCetPhysicsPercentile());
//            payload.put("cetChemistry", jeeData.getMhtCetChemistryPercentile());
//            payload.put("hsc", jeeData.getHscPcmPercent());
//            payload.put("ssc", jeeData.getSscTotalPercent());
//            payload.put("hscPcmPercent" , jeeData.getHscPcmPercent());
//            payload.put(("hscPhysics") , jeeData.getHscPhysicsPercent());
//            payload.put(("hscMaths"), jeeData.getHscMathPercent());
//
//            if (jeeData.getMhtCetPcmTotalPercentile() != null && jeeData.getMhtCetPcmTotalPercentile() > 0) {
//                foundCet = true;
//            }
//        }
//
//        // Step B: Look into the Master CET Table (STUDENTS)
//        Optional<CetStudent> cetMatch = cetMasterRepo.findByApplicationId(targetId);
//        if (cetMatch.isPresent()) {
//            CetStudent cetData = cetMatch.get();
//            foundCet = true;
//
//            // Overwrite name & category values using the comprehensive CET profile layout
//            payload.put("name", cetData.getFullName());
//            payload.put("category", cetData.getCategory()); // Overwrites 'OPEN' fallback with real category
//
//            // Map precise metrics directly out of CET record
//            payload.put("cetOverall", cetData.getPercentileOverall());
//            payload.put("cetMath", cetData.getMathsPercentile());
//            payload.put("cetPhysics", cetData.getPhysicsPercentile());
//            payload.put("cetChemistry", cetData.getChemistryPercentile());
//            payload.put("hsc", cetData.getHscPercentage());
//            payload.put("ssc", cetData.getSscOverallPercent());
//
//            if (foundJee) {
//                payload.put("eligiblePool", "BOTH");
//            } else {
//                payload.put("eligiblePool", "STATE_ONLY");
//            }
//        }
//
//        // Assign final synchronization indicator flags down to orchestrator state
//        payload.put("foundJee", foundJee);
//        payload.put("foundCet", foundCet);
//
//        // If it wasn't found in either system, reject request safely
//        if (!foundJee && !foundCet) {
//            return ResponseEntity.badRequest().body("Application ID not found in either system records.");
//        }
//
//        return ResponseEntity.ok(payload);
//    }
//
//    // =========================================================================
//    // 2. STATE MERIT LIST ENDPOINT (MHT-CET + Non-CAP / Diploma Entries)
//    // =========================================================================
//    @GetMapping("/state-registrations")
//    public ResponseEntity<?> getStateRegistrations() {
//        List<Map<String, Object>> students = new ArrayList<>();
//
//        String query =
//                "SELECT full_name, application_id, category, gender, pwd, " +
//                        "percentile_overall, maths_percentile, physics_percentile, chemistry_percentile, hsc_percentage " +
//                        "FROM spot_registrations WHERE percentile_overall IS NOT NULL AND percentile_overall > 0 " +
//                        "UNION " +
//                        "SELECT candidate_name AS full_name, application_id, 'OPEN' AS category, 'M' AS gender, 'No' AS pwd, " +
//                        "mht_cet_pcm_total_percentile AS percentile_overall, mht_cet_math_percentile AS maths_percentile, " +
//                        "mht_cet_physics_percentile AS physics_percentile, mht_cet_chemistry_percentile AS chemistry_percentile, hsc_pcm_percent AS hsc_percentage " +
//                        "FROM all_india WHERE mht_cet_pcm_total_percentile IS NOT NULL AND mht_cet_pcm_total_percentile > 0 " +
//                        "ORDER BY percentile_overall DESC, maths_percentile DESC, physics_percentile DESC, chemistry_percentile DESC";
//
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement ps = conn.prepareStatement(query);
//             ResultSet rs = ps.executeQuery()) {
//
//            int dynamicStateRank = 1;
//            while (rs.next()) {
//                Map<String, Object> student = new HashMap<>();
//                student.put("meritNo", dynamicStateRank++);
//                student.put("appId", rs.getString("application_id"));
//                student.put("name", rs.getString("full_name"));
//                student.put("category", cleanCategory(rs.getString("category")));
//                student.put("gender", rs.getString("gender"));
//                student.put("pwdStatus", rs.getString("pwd"));
//
//                student.put("pOverall", rs.getDouble("percentile_overall"));
//                student.put("pMath", rs.getDouble("maths_percentile"));
//                student.put("pPhys", rs.getDouble("physics_percentile"));
//                student.put("pChem", rs.getDouble("chemistry_percentile"));
//                student.put("hsc", rs.getDouble("hsc_percentage"));
//
//                students.add(student);
//            }
//            return ResponseEntity.ok(students);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("State database calculation failure: " + e.getMessage());
//        }
//    }
//
//    // =========================================================================
//    // 3. ALL INDIA MERIT LIST ENDPOINT (JEE Quota Sorting Engine)
//    // =========================================================================
//    @GetMapping("/all-india-registrations")
//    public ResponseEntity<?> getAllIndiaRegistrations() {
//        List<Map<String, Object>> students = new ArrayList<>();
//
//        String query = "SELECT application_id, candidate_name, merit_exam_percentile_mark, " +
//                "jee_math_percentile, jee_physics_percentile, jee_chemistry_percentile, hsc_pcm_percent " +
//                "FROM all_india WHERE merit_exam_percentile_mark IS NOT NULL AND merit_exam_percentile_mark > 0 " +
//                "ORDER BY merit_exam_percentile_mark DESC, jee_math_percentile DESC, jee_physics_percentile DESC";
//
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement ps = conn.prepareStatement(query);
//             ResultSet rs = ps.executeQuery()) {
//
//            int dynamicAllIndiaRank = 1;
//            while (rs.next()) {
//                Map<String, Object> student = new HashMap<>();
//                student.put("meritNo", dynamicAllIndiaRank++);
//                student.put("appId", rs.getString("application_id"));
//                student.put("name", rs.getString("candidate_name"));
//
//                student.put("pOverall", rs.getDouble("merit_exam_percentile_mark"));
//                student.put("pMath", rs.getDouble("jee_math_percentile"));
//                student.put("pPhys", rs.getDouble("jee_physics_percentile"));
//                student.put("pChem", rs.getDouble("jee_chemistry_percentile"));
//                student.put("hsc", rs.getDouble("hsc_pcm_percent"));
//
//                students.add(student);
//            }
//            return ResponseEntity.ok(students);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("All India tracking logic failure: " + e.getMessage());
//        }
//    }
//
//    // =========================================================================
//    // 4. SAVE COMPLETED PAYMENT RECORD ENDPOINT
//    // =========================================================================
//    @PostMapping("/save-payment")
//    public ResponseEntity<?> savePaymentRecord(@RequestBody Map<String, Object> payload) {
//        String sql = "INSERT INTO payment_records " +
//                "(candidate_name, application_id, mobile_no, email, payment_transaction_id, razorpay_order_id, amount, payment_status) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            String candidateName = (String) payload.get("candidateName");
//            String applicationId = (String) payload.get("applicationId");
//            String mobileNo = (String) payload.get("mobileNo");
//            String email = (String) payload.get("email");
//            String txnId = (String) payload.get("paymentTransactionId"); // razorpay_payment_id
//            String orderId = (String) payload.get("razorpayOrderId");
//
//            double amount = 1000.00;
//            if (payload.get("amount") != null) {
//                amount = Double.parseDouble(payload.get("amount").toString());
//            }
//
//            String status = payload.get("paymentStatus") != null ?
//                    payload.get("paymentStatus").toString() : "SUCCESS";
//
//            ps.setString(1, candidateName);
//            ps.setString(2, applicationId);
//            ps.setString(3, mobileNo);
//            ps.setString(4, email);
//            ps.setString(5, txnId);
//            ps.setString(6, orderId);
//            ps.setDouble(7, amount);
//            ps.setString(8, status);
//
//            ps.executeUpdate();
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "SUCCESS");
//            response.put("message", "Payment record stored successfully.");
//            response.put("transactionId", txnId);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError()
//                    .body("Failed to persist payment details: " + e.getMessage());
//        }
//    }
//
//    private String cleanCategory(String raw) {
//        if (raw == null) return "OPEN";
//        String upper = raw.toUpperCase();
//        if (upper.contains("OPEN")) return "OPEN";
//        if (upper.contains("SC")) return "SC";
//        if (upper.contains("ST")) return "ST";
//        if (upper.contains("OBC")) return "OBC";
//        if (upper.contains("SBC")) return "SBC";
//        if (upper.contains("SEBC")) return "SEBC";
//        if (upper.contains("NT-A") || upper.contains("VJ")) return "VJ/NT-A";
//        if (upper.contains("NT-B")) return "NT-B";
//        if (upper.contains("NT-C")) return "NT-C";
//        if (upper.contains("NT-D")) return "NT-D";
//        return "OPEN";
//    }
//}