package com.spotentrywebsite.spot_admission_portal.Database;

import com.spotentrywebsite.spot_admission_portal.Database.SaveDiploma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiplomaRepository extends JpaRepository<SaveDiploma, String> {

    // Finds single registration profiles by code identifiers
    Optional<SaveDiploma> findByCandidateId(String candidateId);

    // Dynamic Inter-Se Merit Engine: Sorts applicants perfectly for spot round generation
    @Query("SELECT d FROM SaveDiploma d ORDER BY d.diplomaPercentage DESC, d.sscMathsMarks DESC")
    List<SaveDiploma> findAllSortedForSpotMeritRound();
}