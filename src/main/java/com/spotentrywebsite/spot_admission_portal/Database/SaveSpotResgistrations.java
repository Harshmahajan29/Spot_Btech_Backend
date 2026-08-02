package com.spotentrywebsite.spot_admission_portal.Database;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SaveSpotResgistrations extends JpaRepository<SaveApplications , String> {
    Optional<SaveApplications> findByApplicationId(String applicationId);
}
