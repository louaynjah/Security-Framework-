package com.security.framework.service;

import com.security.framework.entity.ScanResult;
import com.security.framework.repository.ScanResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NmapService {
    
    @Autowired
    private ScanResultRepository scanResultRepository;
    
    @Autowired
    private MetasploitService metasploitService;
    
    public ScanResult completeSecurityAudit(String target, String scanType) {
        ScanResult scanResult = scanTarget(target, scanType);
        
        // Si le scan Nmap trouve des ports ouverts, lancer Metasploit
        if (scanResult.getPortsOpen() != null && scanResult.getPortsOpen() > 0) {
            try {
                String metasploitResult = metasploitService.scanVulnerabilities(target);
                scanResult.setMetasploitScan(metasploitResult);
                scanResult.setVulnerabilitiesFound(extractVulnerabilitiesCount(metasploitResult));
            } catch (Exception e) {
                scanResult.setMetasploitScan("Metasploit scan failed: " + e.getMessage());
            }
        }
        
        return scanResultRepository.save(scanResult);
    }
    
    private Integer extractVulnerabilitiesCount(String metasploitOutput) {
        // Logique simple pour compter les vulnérabilités
        int count = 0;
        if (metasploitOutput.contains("[+]") || metasploitOutput.contains("VULNERABLE")) {
            count++;
        }
        return count;
    }
    
    
    public ScanResult scanTarget(String target, String scanType) {
        try {
            String[] command;
            String commandString;
            
            switch(scanType) {
                case "quick":
                    command = new String[]{"nmap", "-T4", "-F", target};
                    commandString = "nmap -T4 -F " + target;
                    break;
                case "detailed":
                    command = new String[]{"nmap", "-sS", "-sV", "-O", target};
                    commandString = "nmap -sS -sV -O " + target;
                    break;
                case "vuln":
                    command = new String[]{"nmap", "--script", "vuln", target};
                    commandString = "nmap --script vuln " + target;
                    break;
                default:
                    command = new String[]{"nmap", "-sS", target};
                    commandString = "nmap -sS " + target;
            }
            
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            
            String scanOutput = result.toString();
            ScanResult scanResult = new ScanResult(target, scanType, commandString, scanOutput);
            
            scanResult.setHostsFound(extractHostsFound(scanOutput));
            scanResult.setPortsOpen(extractPortsOpen(scanOutput));
            
            return scanResultRepository.save(scanResult);
            
        } catch (Exception e) {
            ScanResult errorResult = new ScanResult(target, scanType, "ERROR", "Erreur: " + e.getMessage());
            errorResult.setStatus("FAILED");
            return scanResultRepository.save(errorResult);
        }
    }
    
    private Integer extractHostsFound(String scanOutput) {
        Pattern pattern = Pattern.compile("(\\d+) host[s]? up");
        Matcher matcher = pattern.matcher(scanOutput);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
    
    private Integer extractPortsOpen(String scanOutput) {
        Pattern pattern = Pattern.compile("(\\d+)/tcp\\s+open");
        Matcher matcher = pattern.matcher(scanOutput);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
    
    public List<ScanResult> getScanHistory() {
        return scanResultRepository.findTop10ByOrderByScanDateDesc();
    }
    
    public Long getTotalScans() {
        return scanResultRepository.countTotalScans();
    }
    
    public Long getUniqueTargets() {
        return scanResultRepository.countUniqueTargets();
    }
}
