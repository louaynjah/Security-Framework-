package com.security.framework.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scan_results")
public class ScanResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String target;
    
    @Column(name = "scan_type")
    private String scanType;
    
    @Column(columnDefinition = "TEXT")
    private String command;
    
    @Column(columnDefinition = "LONGTEXT")
    private String results;
    
    @Column(name = "scan_date")
    private LocalDateTime scanDate;
    
    private String status; // COMPLETED, RUNNING, FAILED
    
    @Column(name = "hosts_found")
    private Integer hostsFound;
    
    @Column(name = "ports_open")
    private Integer portsOpen;
     // Ajouter ces champs à ScanResult.java
    @Column(name = "vulnerabilities_found")
    private Integer vulnerabilitiesFound;

    @Column(name = "exploitation_result")
    private String exploitationResult;

    @Column(name = "metasploit_scan")
    private String metasploitScan;
    
    // Constructeurs
    public ScanResult() {
        this.scanDate = LocalDateTime.now();
        this.status = "COMPLETED";
    }
    
    
    public ScanResult(String target, String scanType, String command, String results) {
        this();
        this.target = target;
        this.scanType = scanType;
        this.command = command;
        this.results = results;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    
    public String getScanType() { return scanType; }
    public void setScanType(String scanType) { this.scanType = scanType; }
    
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    
    public String getResults() { return results; }
    public void setResults(String results) { this.results = results; }
    
    public LocalDateTime getScanDate() { return scanDate; }
    public void setScanDate(LocalDateTime scanDate) { this.scanDate = scanDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getHostsFound() { return hostsFound; }
    public void setHostsFound(Integer hostsFound) { this.hostsFound = hostsFound; }
    
    public Integer getPortsOpen() { return portsOpen; }
    public void setPortsOpen(Integer portsOpen) { this.portsOpen = portsOpen; }
   

    public Integer getVulnerabilitiesFound() { return vulnerabilitiesFound; }
    public void setVulnerabilitiesFound(Integer vulnerabilitiesFound) { this.vulnerabilitiesFound = vulnerabilitiesFound; }

    public String getExploitationResult() { return exploitationResult; }
    public void setExploitationResult(String exploitationResult) { this.exploitationResult = exploitationResult; }

    public String getMetasploitScan() { return metasploitScan; }
    public void setMetasploitScan(String metasploitScan) { this.metasploitScan = metasploitScan; }
}
