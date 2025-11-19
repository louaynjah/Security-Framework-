package com.security.framework.controller;

import com.security.framework.entity.ScanResult;
import com.security.framework.service.PdfReportService;
import com.security.framework.repository.ScanResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ReportController {

    @Autowired
    private PdfReportService pdfReportService;

    @Autowired
    private ScanResultRepository scanResultRepository;

    @GetMapping("/reports")
    public String reportsDashboard(Model model) {
        List<ScanResult> recentScans = scanResultRepository.findTop10ByOrderByScanDateDesc();
        model.addAttribute("recentScans", recentScans);
        model.addAttribute("pageTitle", "Rapports - Security Framework");
        return "reports";
    }

    @GetMapping("/reports/scan/{id}")
    public ResponseEntity<byte[]> generateScanReport(@PathVariable Long id) {
        try {
            ScanResult scan = scanResultRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Scan non trouvé: " + id));

            byte[] pdfBytes = pdfReportService.generateSecurityReport(scan);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", 
                "security-scan-report-" + scan.getTarget() + "-" + 
                scan.getScanDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")) + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reports/audit")
    public ResponseEntity<byte[]> generateAuditReport(
            @RequestParam(required = false) String target,
            @RequestParam(required = false) String scanType) {
        try {
            List<ScanResult> scans;
            
            if (target != null && !target.isEmpty()) {
                scans = scanResultRepository.findByTargetContainingIgnoreCase(target);
            } else if (scanType != null && !scanType.isEmpty()) {
                scans = scanResultRepository.findByScanType(scanType);
            } else {
                scans = scanResultRepository.findTop20ByOrderByScanDateDesc();
            }

            byte[] pdfBytes = pdfReportService.generateAuditReport(scans);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", 
                "security-audit-report-" + java.time.LocalDate.now() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
