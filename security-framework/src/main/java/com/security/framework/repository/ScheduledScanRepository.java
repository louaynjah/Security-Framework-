package com.security.framework.repository;

import com.security.framework.entity.ScheduledScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledScanRepository extends JpaRepository<ScheduledScan, Long> {
    
    List<ScheduledScan> findByIsActiveTrue();
    
    List<ScheduledScan> findByIsActiveTrueAndNextExecutionBefore(LocalDateTime dateTime);
    
    @Query("SELECT COUNT(s) FROM ScheduledScan s WHERE s.isActive = true")
    Long countActiveSchedules();
}
