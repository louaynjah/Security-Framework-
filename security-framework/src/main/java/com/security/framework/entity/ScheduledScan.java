package com.security.framework.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "scheduled_scans")
public class ScheduledScan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String target;
    
    @Column(name = "scan_type")
    private String scanType;
    
    @Column(name = "schedule_type") // DAILY, WEEKLY, MONTHLY, CUSTOM
    private String scheduleType;
    
    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;
    
    @Column(name = "days_of_week") // Pour les scans hebdomadaires
    private String daysOfWeek;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "last_execution")
    private LocalDateTime lastExecution;
    
    @Column(name = "next_execution")
    private LocalDateTime nextExecution;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    // Constructeurs
    public ScheduledScan() {
        this.createdDate = LocalDateTime.now();
        this.isActive = true;
    }
    
    public ScheduledScan(String target, String scanType, String scheduleType, LocalTime scheduledTime) {
        this();
        this.target = target;
        this.scanType = scanType;
        this.scheduleType = scheduleType;
        this.scheduledTime = scheduledTime;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    
    public String getScanType() { return scanType; }
    public void setScanType(String scanType) { this.scanType = scanType; }
    
    public String getScheduleType() { return scheduleType; }
    public void setScheduleType(String scheduleType) { this.scheduleType = scheduleType; }
    
    public LocalTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalTime scheduledTime) { this.scheduledTime = scheduledTime; }
    
    public String getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(String daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getLastExecution() { return lastExecution; }
    public void setLastExecution(LocalDateTime lastExecution) { this.lastExecution = lastExecution; }
    
    public LocalDateTime getNextExecution() { return nextExecution; }
    public void setNextExecution(LocalDateTime nextExecution) { this.nextExecution = nextExecution; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}
