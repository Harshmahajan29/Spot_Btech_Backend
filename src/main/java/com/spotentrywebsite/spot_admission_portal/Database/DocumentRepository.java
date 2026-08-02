package com.spotentrywebsite.spot_admission_portal.Database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<SpotRegistrationDocuments, Long> {
    // Allows you to fetch documents easily using a candidate's App ID
    Optional<SpotRegistrationDocuments> findByCandidateId(String candidateId);
}