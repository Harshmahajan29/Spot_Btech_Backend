package com.spotentrywebsite.spot_admission_portal.Database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JeeStudentRepository extends JpaRepository<JeeStudent, String> {
    Optional<JeeStudent> findByApplicationId(String applicationId);
}