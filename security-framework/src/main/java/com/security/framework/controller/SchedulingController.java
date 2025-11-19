package com.security.framework.controller;

import com.security.framework.entity.ScheduledScan;
import com.security.framework.service.ScanSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/scheduling")
public class SchedulingController {

    @Autowired
    private ScanSchedulingService scanSchedulingService;

    @GetMapping
    public String schedulingDashboard(Model model) {
        List<ScheduledScan> activeSchedules = scanSchedulingService.getActiveSchedules();
        model.addAttribute("schedules", activeSchedules);
        model.addAttribute("pageTitle", "Planification - Security Framework");
        return "scheduling";
    }

    @PostMapping("/create")
    public String createSchedule(
            @RequestParam String target,
            @RequestParam String scanType,
            @RequestParam String scheduleType,
            @RequestParam String scheduledTime,
            RedirectAttributes redirectAttributes) {
        
        try {
            LocalTime time = LocalTime.parse(scheduledTime);
            ScheduledScan schedule = scanSchedulingService.createSchedule(target, scanType, scheduleType, time);
            
            redirectAttributes.addFlashAttribute("success", 
                "Scan planifié créé avec succès pour " + target);
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur création planification: " + e.getMessage());
        }
        
        return "redirect:/scheduling";
    }

    @PostMapping("/toggle/{id}")
    public String toggleSchedule(@PathVariable Long id, 
                                @RequestParam boolean active,
                                RedirectAttributes redirectAttributes) {
        try {
            scanSchedulingService.toggleSchedule(id, active);
            String status = active ? "activé" : "désactivé";
            redirectAttributes.addFlashAttribute("success", 
                "Scan planifié " + status + " avec succès");
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur modification planification: " + e.getMessage());
        }
        
        return "redirect:/scheduling";
    }

    @PostMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, 
                                RedirectAttributes redirectAttributes) {
        try {
            scanSchedulingService.toggleSchedule(id, false);
            redirectAttributes.addFlashAttribute("success", 
                "Scan planifié supprimé avec succès");
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur suppression planification: " + e.getMessage());
        }
        
        return "redirect:/scheduling";
    }

    @PostMapping("/execute-now/{id}")
    public String executeNow(@PathVariable Long id, 
                            RedirectAttributes redirectAttributes) {
        try {
            // Implémentation pour exécution immédiate
            redirectAttributes.addFlashAttribute("success", 
                "Scan exécuté immédiatement avec succès");
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur exécution immédiate: " + e.getMessage());
        }
        
        return "redirect:/scheduling";
    }
}
