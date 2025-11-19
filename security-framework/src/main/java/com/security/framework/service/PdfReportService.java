package com.security.framework.service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.security.framework.entity.ScanResult;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfReportService {
    
    public byte[] generateSecurityReport(ScanResult scan) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // En-tête du rapport
            addHeader(document, scan);
            
            // Résumé exécutif
            addExecutiveSummary(document, scan);
            
            // Détails techniques
            addTechnicalDetails(document, scan);
            
            // Résultats du scan
            addScanResults(document, scan);
            
            // Recommandations
            addRecommendations(document, scan);
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF: " + e.getMessage(), e);
        }
    }
    
    public byte[] generateAuditReport(List<ScanResult> scans) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // En-tête rapport d'audit
            Paragraph title = new Paragraph("RAPPORT D'AUDIT DE SÉCURITÉ COMPLET")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE);
            document.add(title);
            
            document.add(new Paragraph(" "));
            
            // Tableau récapitulatif
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 2}));
            summaryTable.setWidth(UnitValue.createPercentValue(100));
            
            // En-tête du tableau
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("Cible").setBold()));
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("Date").setBold()));
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("Type").setBold()));
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("Hôtes").setBold()));
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("Ports").setBold()));
            
            // Données
            for (ScanResult scan : scans) {
                summaryTable.addCell(new Cell().add(new Paragraph(scan.getTarget())));
                summaryTable.addCell(new Cell().add(new Paragraph(
                    scan.getScanDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                )));
                summaryTable.addCell(new Cell().add(new Paragraph(scan.getScanType())));
                summaryTable.addCell(new Cell().add(new Paragraph(
                    scan.getHostsFound() != null ? scan.getHostsFound().toString() : "N/A"
                )));
                summaryTable.addCell(new Cell().add(new Paragraph(
                    scan.getPortsOpen() != null ? scan.getPortsOpen().toString() : "N/A"
                )));
            }
            
            document.add(summaryTable);
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération rapport audit: " + e.getMessage(), e);
        }
    }
    
    private void addHeader(Document document, ScanResult scan) {
        // Titre principal
        Paragraph title = new Paragraph("RAPPORT DE SCAN DE SÉCURITÉ")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.DARK_GRAY);
        document.add(title);
        
        document.add(new Paragraph(" "));
        
        // Informations de base
        Paragraph info = new Paragraph()
                .add("Cible: ").add(new Text(scan.getTarget()).setBold())
                .add("     Date: ").add(new Text(
                    scan.getScanDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                ).setBold())
                .add("     Type: ").add(new Text(scan.getScanType()).setBold());
        document.add(info);
        
        document.add(new Paragraph(" "));
    }
    
    private void addExecutiveSummary(Document document, ScanResult scan) {
        Paragraph sectionTitle = new Paragraph("RÉSUMÉ EXÉCUTIF")
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.BLUE);
        document.add(sectionTitle);
        
        // Calcul du niveau de risque
        String riskLevel = calculateRiskLevel(scan);
        String riskColor = getRiskColor(riskLevel);
        
        Paragraph risk = new Paragraph("Niveau de risque: ")
                .add(new Text(riskLevel).setBold().setFontColor(getPdfColor(riskColor)));
        document.add(risk);
        
        // Statistiques
        Paragraph stats = new Paragraph()
                .add("Hôtes trouvés: ").add(new Text(
                    scan.getHostsFound() != null ? scan.getHostsFound().toString() : "0"
                ).setBold())
                .add("     Ports ouverts: ").add(new Text(
                    scan.getPortsOpen() != null ? scan.getPortsOpen().toString() : "0"
                ).setBold())
                .add("     Vulnérabilités: ").add(new Text(
                    scan.getVulnerabilitiesFound() != null ? scan.getVulnerabilitiesFound().toString() : "0"
                ).setBold());
        document.add(stats);
        
        document.add(new Paragraph(" "));
    }
    
    private void addTechnicalDetails(Document document, ScanResult scan) {
        Paragraph sectionTitle = new Paragraph("DÉTAILS TECHNIQUES")
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.BLUE);
        document.add(sectionTitle);
        
        Paragraph command = new Paragraph("Commande exécutée: ")
                .add(new Text(scan.getCommand()).setFontColor(ColorConstants.DARK_GRAY));
        document.add(command);
        
        document.add(new Paragraph(" "));
    }
    
    private void addScanResults(Document document, ScanResult scan) {
        Paragraph sectionTitle = new Paragraph("RÉSULTATS DU SCAN")
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.BLUE);
        document.add(sectionTitle);
        
        // Résultats formatés
        if (scan.getResults() != null && scan.getResults().length() > 500) {
            Paragraph results = new Paragraph(scan.getResults().substring(0, 500) + "...")
                    .setFontSize(8)
                    .setFontColor(ColorConstants.DARK_GRAY);
            document.add(results);
        }
        
        document.add(new Paragraph(" "));
    }
    
    private void addRecommendations(Document document, ScanResult scan) {
        Paragraph sectionTitle = new Paragraph("RECOMMANDATIONS DE SÉCURITÉ")
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.BLUE);
        document.add(sectionTitle);
        
        // Recommandations basées sur les résultats
        List<String> recommendations = generateRecommendations(scan);
        for (String recommendation : recommendations) {
            Paragraph rec = new Paragraph("• " + recommendation)
                    .setFontSize(10)
                    .setMarginLeft(20);
            document.add(rec);
        }
        
        document.add(new Paragraph(" "));
    }
    
    private String calculateRiskLevel(ScanResult scan) {
        if (scan.getVulnerabilitiesFound() != null && scan.getVulnerabilitiesFound() > 0) {
            return "ÉLEVÉ";
        } else if (scan.getPortsOpen() != null && scan.getPortsOpen() > 5) {
            return "MOYEN";
        } else {
            return "FAIBLE";
        }
    }
    
    private String getRiskColor(String riskLevel) {
        switch (riskLevel) {
            case "ÉLEVÉ": return "RED";
            case "MOYEN": return "ORANGE";
            default: return "GREEN";
        }
    }
    
    private com.itextpdf.kernel.colors.Color getPdfColor(String color) {
        switch (color) {
            case "RED": return ColorConstants.RED;
            case "ORANGE": return ColorConstants.ORANGE;
            case "GREEN": return ColorConstants.GREEN;
            default: return ColorConstants.BLACK;
        }
    }
    
    private List<String> generateRecommendations(ScanResult scan) {
        List<String> recommendations = new java.util.ArrayList<>();
        
        if (scan.getPortsOpen() != null && scan.getPortsOpen() > 0) {
            recommendations.add("Fermer les ports non essentiels exposés");
        }
        
        if (scan.getVulnerabilitiesFound() != null && scan.getVulnerabilitiesFound() > 0) {
            recommendations.add("Appliquer les correctifs de sécurité disponibles");
            recommendations.add("Mettre à jour les services vulnérables");
        }
        
        recommendations.add("Mettre en place un pare-feu pour filtrer le trafic entrant");
        recommendations.add("Surveiller régulièrement les journaux de sécurité");
        
        return recommendations;
    }
}
