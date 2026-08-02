//package com.spotentrywebsite.spot_admission_portal.Controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Exposes the payment_records table to the admin dashboard (admin.html / admin-logic.js).
// * Kept as its own controller so it can be dropped into the existing project without
// * touching the admin controller(s) that already serve /api/admin/matrix, /oms-merit-list, etc.
// */
//@RestController
//@RequestMapping("/api/admin")
//@CrossOrigin(origins = "*", allowedHeaders = "*")
//public class PaymentAdminController {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @GetMapping("/payment-records")
//    public ResponseEntity<List<Map<String, Object>>> getAllPaymentRecords(
//            @RequestParam(value = "search", required = false) String search) {
//
//        List<Map<String, Object>> records = new ArrayList<>();
//        String sql = "SELECT id, candidate_name, application_id, mobile_no, email, payment_transaction_id, " +
//                "razorpay_order_id, amount, currency, payment_status, created_at FROM payment_records " +
//                (search != null && !search.isBlank()
//                        ? "WHERE application_id ILIKE ? OR candidate_name ILIKE ? OR payment_transaction_id ILIKE ? "
//                        : "") +
//                "ORDER BY created_at DESC";
//
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            if (search != null && !search.isBlank()) {
//                String like = "%" + search.trim() + "%";
//                ps.setString(1, like);
//                ps.setString(2, like);
//                ps.setString(3, like);
//            }
//
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    Map<String, Object> row = new LinkedHashMap<>();
//                    row.put("id", rs.getLong("id"));
//                    row.put("candidateName", rs.getString("candidate_name"));
//                    row.put("applicationId", rs.getString("application_id"));
//                    row.put("mobileNo", rs.getString("mobile_no"));
//                    row.put("email", rs.getString("email"));
//                    row.put("paymentTransactionId", rs.getString("payment_transaction_id"));
//                    row.put("razorpayOrderId", rs.getString("razorpay_order_id"));
//                    row.put("amount", rs.getBigDecimal("amount"));
//                    row.put("currency", rs.getString("currency"));
//                    row.put("paymentStatus", rs.getString("payment_status"));
//                    row.put("createdAt", rs.getTimestamp("created_at"));
//                    records.add(row);
//                }
//            }
//            return ResponseEntity.ok(records);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(List.of());
//        }
//    }
//
//    @GetMapping("/payment-records/summary")
//    public ResponseEntity<Map<String, Object>> getPaymentSummary() {
//        Map<String, Object> summary = new LinkedHashMap<>();
//        String sql = "SELECT COUNT(*) AS total_count, COALESCE(SUM(amount), 0) AS total_amount " +
//                "FROM payment_records WHERE payment_status = 'SUCCESS'";
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//            if (rs.next()) {
//                summary.put("totalSuccessfulPayments", rs.getInt("total_count"));
//                summary.put("totalAmountCollected", rs.getBigDecimal("total_amount"));
//            }
//            return ResponseEntity.ok(summary);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//        }
//    }
//}