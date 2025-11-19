package com.security.framework.controller;

import com.security.framework.service.MetasploitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MetasploitController {
    
    @Autowired
    private MetasploitService metasploitService;
    
    @GetMapping("/metasploit")
    public String metasploitDashboard(Model model) {
        List<String> exploits = metasploitService.getAvailableExploits();
        model.addAttribute("exploits", exploits);
        model.addAttribute("pageTitle", "Metasploit - Security Framework");
        return "metasploit";
    }
    
    @PostMapping("/metasploit/scan")
    public String scanWithMetasploit(@RequestParam String target, Model model) {
        String result = metasploitService.scanVulnerabilities(target);
        model.addAttribute("scanResult", result);
        model.addAttribute("target", target);
        model.addAttribute("pageTitle", "Résultats Metasploit");
        return "metasploit";
    }
    
    @PostMapping("/metasploit/exploit")
    public String exploitTarget(@RequestParam String target, 
                               @RequestParam String exploit,
                               Model model) {
        String result = metasploitService.exploitTarget(target, exploit);
        model.addAttribute("exploitResult", result);
        model.addAttribute("target", target);
        model.addAttribute("exploit", exploit);
        model.addAttribute("pageTitle", "Résultats d'Exploitation");
        return "metasploit";
    }
}
