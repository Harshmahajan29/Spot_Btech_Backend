package com.spotentrywebsite.spot_admission_portal.Controllers;

import com.spotentrywebsite.spot_admission_portal.Database.DiplomaStudent;
import com.spotentrywebsite.spot_admission_portal.Database.DiplomaStudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// =========================================================================
// EXCEL EXPORT CONTROLLER — item 3
// Uses SXSSFWorkbook (streaming) so 800-1000 row exports stay memory-light.
// Column names are written EXACTLY as they exist in each merit list dataset.
// =========================================================================
@RestController
@RequestMapping("/api/admin/export")
@CrossOrigin(origins = "*")
public class ExcelExportController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DiplomaStudentRepository diplomaRepo;

    // -------------------------------------------------------------------
    // 1. STATE (MHT-CET) MERIT LIST EXPORT
    // -------------------------------------------------------------------
    @GetMapping("/state")
    public ResponseEntity<byte[]> exportStateMeritList() throws Exception {
        String[] columns = {
                "meritNo", "appId", "name", "category", "gender", "pwdStatus",
                "pOverall", "pMath", "pPhys", "pChem", "hsc"
        };

        // FIXED: Added explicit ::numeric casts to the percentile columns to prevent text > integer exception
        String query =
                "SELECT full_name, application_id, category, gender, pwd, " +
                        "percentile_overall, maths_percentile, physics_percentile, chemistry_percentile, hsc_percentage " +
                        "FROM spot_registrations WHERE percentile_overall IS NOT NULL AND percentile_overall::numeric > 0 " +
                        "ORDER BY percentile_overall::numeric DESC, maths_percentile::numeric DESC, physics_percentile::numeric DESC, chemistry_percentile::numeric DESC";

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(-1)) { // -1 disables auto-flush window, we flush manually below
            SXSSFSheet sheet = workbook.createSheet("State Merit List");
            writeHeaderRow(sheet, columns);

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                int rowNum = 1;
                int meritNo = 1;
                while (rs.next()) {
                    Row row = sheet.createRow(rowNum++);
                    int c = 0;
                    row.createCell(c++).setCellValue(meritNo++);
                    row.createCell(c++).setCellValue(rs.getString("application_id"));
                    row.createCell(c++).setCellValue(rs.getString("full_name"));
                    row.createCell(c++).setCellValue(rs.getString("category"));
                    row.createCell(c++).setCellValue(rs.getString("gender"));
                    row.createCell(c++).setCellValue(rs.getString("pwd"));
                    row.createCell(c++).setCellValue(rs.getDouble("percentile_overall"));
                    row.createCell(c++).setCellValue(rs.getDouble("maths_percentile"));
                    row.createCell(c++).setCellValue(rs.getDouble("physics_percentile"));
                    row.createCell(c++).setCellValue(rs.getDouble("chemistry_percentile"));
                    row.createCell(c).setCellValue(rs.getDouble("hsc_percentage"));
                }
            }

            return toResponse(workbook, "State_MHT_CET_Merit_List.xlsx");
        }
    }

    // -------------------------------------------------------------------
    // 2. ALL INDIA (JEE) MERIT LIST EXPORT
    // -------------------------------------------------------------------
    @GetMapping("/all-india")
    public ResponseEntity<byte[]> exportAllIndiaMeritList() throws Exception {
        String[] columns = {
                "meritNo", "appId", "name", "pOverall", "pMath", "pPhys", "pChem", "hsc"
        };

        // FIXED: Added explicit ::numeric casts to avoid the comparison type exception
        String query = "SELECT application_id, candidate_name, merit_exam_percentile_mark, " +
                "jee_math_percentile, jee_physics_percentile, jee_chemistry_percentile, hsc_pcm_percent " +
                "FROM all_india WHERE merit_exam_percentile_mark IS NOT NULL AND merit_exam_percentile_mark::numeric > 0 " +
                "ORDER BY merit_exam_percentile_mark::numeric DESC, jee_math_percentile::numeric DESC, jee_physics_percentile::numeric DESC";

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(-1)) {
            SXSSFSheet sheet = workbook.createSheet("All India JEE Merit List");
            writeHeaderRow(sheet, columns);

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                int rowNum = 1;
                int meritNo = 1;
                while (rs.next()) {
                    Row row = sheet.createRow(rowNum++);
                    int c = 0;
                    row.createCell(c++).setCellValue(meritNo++);
                    row.createCell(c++).setCellValue(rs.getString("application_id"));
                    row.createCell(c++).setCellValue(rs.getString("candidate_name"));
                    row.createCell(c++).setCellValue(rs.getDouble("merit_exam_percentile_mark"));
                    row.createCell(c++).setCellValue(rs.getDouble("jee_math_percentile"));
                    row.createCell(c++).setCellValue(rs.getDouble("jee_physics_percentile"));
                    row.createCell(c++).setCellValue(rs.getDouble("jee_chemistry_percentile"));
                    row.createCell(c).setCellValue(rs.getDouble("hsc_pcm_percent"));
                }
            }

            return toResponse(workbook, "All_India_JEE_Merit_List.xlsx");
        }
    }

    // -------------------------------------------------------------------
    // 3. DIPLOMA MERIT LIST EXPORT — item 2 / item 3
    // -------------------------------------------------------------------
    @GetMapping("/diploma")
    public ResponseEntity<byte[]> exportDiplomaMeritList() throws Exception {
        String[] columns = {
                "meritNo", "appId", "name", "branch", "diplomaPercent",
                "sscTotal", "sscMath", "sscScience", "sscEnglish", "category", "email", "phoneNo"
        };

        List<DiplomaStudent> students = diplomaRepo.findAll();
        Comparator<DiplomaStudent> comparator = Comparator
                .comparingDouble((DiplomaStudent s) -> nz(s.getDiplomaPercentage())).reversed()
                .thenComparing(Comparator.comparingDouble((DiplomaStudent s) -> nz(s.getSscTotal())).reversed())
                .thenComparing(Comparator.comparingDouble((DiplomaStudent s) -> nz(s.getSscMath())).reversed())
                .thenComparing(Comparator.comparingDouble((DiplomaStudent s) -> nz(s.getSscScience())).reversed())
                .thenComparing(Comparator.comparingDouble((DiplomaStudent s) -> nz(s.getSscEnglish())).reversed());
        List<DiplomaStudent> sorted = students.stream().sorted(comparator).collect(Collectors.toList());

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(-1)) {
            SXSSFSheet sheet = workbook.createSheet("Diploma Merit List");
            writeHeaderRow(sheet, columns);

            int rowNum = 1;
            int meritNo = 1;
            for (DiplomaStudent s : sorted) {
                Row row = sheet.createRow(rowNum++);
                int c = 0;
                row.createCell(c++).setCellValue(meritNo++);
                row.createCell(c++).setCellValue(s.getApplicationId());
                row.createCell(c++).setCellValue(s.getFullName());
                row.createCell(c++).setCellValue(s.getDiplomaBranch());
                row.createCell(c++).setCellValue(nz(s.getDiplomaPercentage()));
                row.createCell(c++).setCellValue(nz(s.getSscTotal()));
                row.createCell(c++).setCellValue(nz(s.getSscMath()));
                row.createCell(c++).setCellValue(nz(s.getSscScience()));
                row.createCell(c++).setCellValue(nz(s.getSscEnglish()));
                row.createCell(c++).setCellValue(s.getFinalEligibleCategory());
                row.createCell(c++).setCellValue(s.getEmail());
                row.createCell(c).setCellValue(s.getPhoneNo());
            }

            return toResponse(workbook, "Diploma_Merit_List.xlsx");
        }
    }

    // -------------------------------------------------------------------
    // Shared helpers
    // -------------------------------------------------------------------
    private void writeHeaderRow(Sheet sheet, String[] columns) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }
    }

    private ResponseEntity<byte[]> toResponse(SXSSFWorkbook workbook, String filename) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.dispose(); // cleans up temp files backing the streaming workbook

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok().headers(headers).body(bos.toByteArray());
    }

    private double nz(Double v) { return v != null ? v : 0.0; }
}