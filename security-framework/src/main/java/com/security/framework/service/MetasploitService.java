package com.security.framework.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetasploitService {
    
    public String scanVulnerabilities(String target) {
        try {
            // Utiliser msfconsole avec un script automatisé
            String[] command = {
                "msfconsole", 
                "-q", 
                "-x", 
                "use auxiliary/scanner/portscan/tcp; set RHOSTS " + target + "; run; exit"
            };
            
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            return "Erreur Metasploit: " + e.getMessage();
        }
    }
    
    public String exploitTarget(String target, String exploit) {
        try {
            String[] command = {
                "msfconsole",
                "-q",
                "-x",
                "use " + exploit + "; set RHOST " + target + "; exploit; exit"
            };
            
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            return "Erreur exploitation: " + e.getMessage();
        }
    }
    
    public List<String> getAvailableExploits() {
        List<String> exploits = new ArrayList<>();
        // Exploits courants pour démonstration
        exploits.add("exploit/multi/samba/usermap_script");
        exploits.add("exploit/unix/ftp/vsftpd_234_backdoor");
        exploits.add("exploit/linux/misc/exim4_string_format");
        exploits.add("auxiliary/scanner/ssh/ssh_login");
        exploits.add("auxiliary/scanner/http/http_version");
        return exploits;
    }
}
