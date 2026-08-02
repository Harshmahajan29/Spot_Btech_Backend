package com.spotentrywebsite.spot_admission_portal.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

@RestController
@RequestMapping("/api/admin/actions")
// Allowing the specific frontend origin, methods, and preflight headers to handle CORS safely
@CrossOrigin(
        origins = "*"
)
public class AdminActionsController {

    @Autowired
    private DataSource dataSource;

    // =========================================================================
    // 1. CLEAR TO OPEN ENDPOINT (Dropping Category, Symbols & Quota Overrides)
    // =========================================================================
    @PostMapping("/convert-to-open/{appId}")
    public ResponseEntity<?> convertToOpen(@PathVariable String appId) {
        String targetId = appId.trim().toUpperCase();

        // This query strips out categories, removes symbol flags ($ / # / _ORPHAN),
        // and resets the combined PwD/Defence data tracking field back to 'No'.
        String updateQuery = "UPDATE spot_registrations SET category = 'OPEN', pwd = 'No' WHERE application_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {

            ps.setString(1, targetId);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                return ResponseEntity.ok("Student profile " + targetId + " successfully converted to OPEN. All specialized reservation criteria purged.");
            } else {
                return ResponseEntity.badRequest().body("No active spot registration found matching Application ID: " + targetId);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Database modification sequence aborted: " + e.getMessage());
        }
    }

    // =========================================================================
    // 2. PURGE CANDIDATE ENDPOINT (Dropping Student safely out of Merit List Matrix)
    // =========================================================================
    // Inside your AdminActionsController.java file
    @DeleteMapping("/delete-student/{appId}")
    public ResponseEntity<?> deleteStudent(
            @PathVariable String appId,
            @RequestParam(value = "pool", defaultValue = "STATE") String pool) { // 👈 Read pool parameter

        String targetId = appId.trim().toUpperCase();

        // 1. Determine target table based on frontend active pool state
        String tableName = "spot_registrations";
        if ("ALL_INDIA".equalsIgnoreCase(pool)) {
            tableName = "all_india";
        }

        // 2. Build safe SQL query dynamically targeting the identified table
        String deleteQuery = "DELETE FROM " + tableName + " WHERE UPPER(TRIM(application_id)) = ?";

        System.out.println("Executing delete from table [" + tableName + "] for ID: " + targetId);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteQuery)) {

            ps.setString(1, targetId);
            int rowsDeleted = ps.executeUpdate();

            if (rowsDeleted > 0) {
                return ResponseEntity.ok("Candidate profile " + targetId + " completely dropped from " + tableName + ".");
            } else {
                return ResponseEntity.badRequest().body("Purge operation dropped: target registration matching " + targetId + " does not exist in " + tableName + ".");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Database drop statement execution failure: " + e.getMessage());
        }
    }
}
