package com.spotentrywebsite.spot_admission_portal.Database;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// 💡 Change target from JeeStudent to SaveAllIndia
public interface SaveJeeApplications extends JpaRepository<SaveAllIndia, String> {
    Optional<SaveAllIndia> findByApplicationId(String applicationId);
}