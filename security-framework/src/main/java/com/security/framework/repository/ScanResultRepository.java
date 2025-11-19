package com.security.framework.repository;

import com.security.framework.entity.ScanResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {
    
    List<ScanResult> findByTargetContainingIgnoreCase(String target);
    List<ScanResult> findByScanType(String scanType);
    List<ScanResult> findTop10ByOrderByScanDateDesc();
    List<ScanResult> findTop20ByOrderByScanDateDesc();
    
    @Query("SELECT COUNT(s) FROM ScanResult s")
    Long countTotalScans();
    
    @Query("SELECT COUNT(DISTINCT s.target) FROM ScanResult s")
    Long countUniqueTargets();
}
