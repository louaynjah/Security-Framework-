package com.security.framework.service;

import com.security.framework.entity.ScheduledScan;
import com.security.framework.entity.ScanResult;
import com.security.framework.repository.ScheduledScanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ScanSchedulingService {
    
    @Autowired
    private ScheduledScanRepository scheduledScanRepository;
    
    @Autowired
    private NmapService nmapService;
    
    // Vérifie toutes les minutes les scans planifiés
    @Scheduled(fixedRate = 60000) // 60 secondes
    public void executeScheduledScans() {
        List<ScheduledScan> dueScans = scheduledScanRepository.findByIsActiveTrueAndNextExecutionBefore(LocalDateTime.now());
        
        for (ScheduledScan scheduledScan : dueScans) {
            if (shouldExecute(scheduledScan)) {
                executeScan(scheduledScan);
                updateNextExecution(scheduledScan);
            }
        }
    }
    
    private boolean shouldExecute(ScheduledScan scheduledScan) {
        return scheduledScan.getIsActive() && 
               scheduledScan.getNextExecution() != null &&
               scheduledScan.getNextExecution().isBefore(LocalDateTime.now());
    }
    
    private void executeScan(ScheduledScan scheduledScan) {
        try {
            // Exécuter le scan
            ScanResult result = nmapService.completeSecurityAudit(
                scheduledScan.getTarget(), 
                scheduledScan.getScanType()
            );
            
            // Mettre à jour la dernière exécution
            scheduledScan.setLastExecution(LocalDateTime.now());
            scheduledScanRepository.save(scheduledScan);
            
            // Logger l'exécution
            System.out.println("✅ Scan planifié exécuté: " + scheduledScan.getTarget() + 
                             " - " + scheduledScan.getScanType());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur exécution scan planifié: " + e.getMessage());
        }
    }
    
    private void updateNextExecution(ScheduledScan scheduledScan) {
        LocalDateTime nextExecution = calculateNextExecution(scheduledScan);
        scheduledScan.setNextExecution(nextExecution);
        scheduledScanRepository.save(scheduledScan);
    }
    
    private LocalDateTime calculateNextExecution(ScheduledScan scheduledScan) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (scheduledScan.getScheduleType().toUpperCase()) {
            case "DAILY":
                return now.plusDays(1).with(scheduledScan.getScheduledTime());
                
            case "WEEKLY":
                return now.plusWeeks(1).with(scheduledScan.getScheduledTime());
                
            case "MONTHLY":
                return now.plusMonths(1).with(scheduledScan.getScheduledTime());
                
            case "HOURLY":
                return now.plusHours(1);
                
            default:
                return now.plusDays(1).with(scheduledScan.getScheduledTime());
        }
    }
    
    public ScheduledScan createSchedule(String target, String scanType, String scheduleType, LocalTime scheduledTime) {
        ScheduledScan schedule = new ScheduledScan(target, scanType, scheduleType, scheduledTime);
        schedule.setNextExecution(calculateNextExecution(schedule));
        return scheduledScanRepository.save(schedule);
    }
    
    public void toggleSchedule(Long scheduleId, boolean active) {
        ScheduledScan schedule = scheduledScanRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Planification non trouvée"));
        
        schedule.setIsActive(active);
        if (active) {
            schedule.setNextExecution(calculateNextExecution(schedule));
        }
        scheduledScanRepository.save(schedule);
    }
    
    public List<ScheduledScan> getActiveSchedules() {
        return scheduledScanRepository.findByIsActiveTrue();
    }
}
