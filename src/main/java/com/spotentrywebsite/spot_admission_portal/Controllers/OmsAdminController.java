package com.spotentrywebsite.spot_admission_portal.Controllers;

import com.spotentrywebsite.spot_admission_portal.Database.OutsideMaharashtraCandidate;
import com.spotentrywebsite.spot_admission_portal.Database.OutsideMaharashtraCandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OmsAdminController {

    @Autowired
    private OutsideMaharashtraCandidateRepository omsRepo;

    @GetMapping("/oms-merit-list")
    public ResponseEntity<?> getOmsMeritList() {
        try {
            // Sort by Percentile Overall Descending, then by tie-breakers if needed
            Sort sortingRules = Sort.by(Sort.Direction.DESC, "percentileOverall")
                    .and(Sort.by(Sort.Direction.DESC, "mathsPercentile"))
                    .and(Sort.by(Sort.Direction.DESC, "physicsPercentile"));

            List<OutsideMaharashtraCandidate> rawList = omsRepo.findAll(sortingRules);
            List<Map<String, Object>> formattedMatrixList = new ArrayList<>();

            int meritRank = 1;
            for (OutsideMaharashtraCandidate oms : rawList) {
                Map<String, Object> studentNode = new HashMap<>();

                // Map database entity properties onto standard frontend JSON matrix keys
                studentNode.put("meritNo", meritRank++);
                studentNode.put("appId", oms.getApplicationId());
                studentNode.put("name", oms.getFullName());
                studentNode.put("pOverall", oms.getPercentileOverall() != null ? oms.getPercentileOverall() : 0.0);
                studentNode.put("pMath", oms.getMathsPercentile() != null ? oms.getMathsPercentile() : 0.0);
                studentNode.put("pPhys", oms.getPhysicsPercentile() != null ? oms.getPhysicsPercentile() : 0.0);
                studentNode.put("pChem", oms.getChemistryPercentile() != null ? oms.getChemistryPercentile() : 0.0);
                studentNode.put("hsc", oms.getHscPercentage() != null ? oms.getHscPercentage() : 0.0);
                studentNode.put("category", "OMS"); // Explicitly show outside state label
                studentNode.put("pwdStatus", oms.getPwd() != null ? oms.getPwd() : "NO");

                formattedMatrixList.add(studentNode);
            }

            return ResponseEntity.ok(formattedMatrixList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error building OMS matrix array list: " + e.getMessage());
        }
    }
}