package com.security.framework.controller;

import com.security.framework.entity.ScanResult;
import com.security.framework.service.NmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DashboardController {
    
    @Autowired
    private NmapService nmapService;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Statistiques pour le dashboard
        model.addAttribute("totalScans", nmapService.getTotalScans());
        model.addAttribute("uniqueTargets", nmapService.getUniqueTargets());
        model.addAttribute("recentScans", nmapService.getScanHistory());
        model.addAttribute("pageTitle", "Security Framework - Dashboard");
        return "dashboard";
    }
    
    @GetMapping("/scan")
    public String scanPage(Model model) {
        model.addAttribute("pageTitle", "Nouveau Scan");
        return "scan";
    }
    
    @PostMapping("/scan")
    public String launchScan(@RequestParam String target, 
                            @RequestParam(defaultValue = "quick") String scanType, 
                            Model model) {
        ScanResult scanResult = nmapService.scanTarget(target, scanType);
        model.addAttribute("scanResult", scanResult.getResults());
        model.addAttribute("target", target);
        model.addAttribute("pageTitle", "Résultats du Scan");
        return "scan";
    }
    
    @GetMapping("/history")
    public String scanHistory(Model model) {
        List<ScanResult> history = nmapService.getScanHistory();
        model.addAttribute("scanHistory", history);
        model.addAttribute("pageTitle", "Historique des Scans");
        return "history";
    }
}
