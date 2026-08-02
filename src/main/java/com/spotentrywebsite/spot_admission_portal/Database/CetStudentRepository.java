package com.spotentrywebsite.spot_admission_portal.Database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CetStudentRepository extends JpaRepository<CetStudent , String> {
    Optional<CetStudent> findByApplicationId(String applicationId);
}
