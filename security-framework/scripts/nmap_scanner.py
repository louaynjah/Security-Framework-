#!/usr/bin/env python3
import subprocess
import xml.etree.ElementTree as ET
import json
import sys
import os

class NmapScanner:
    def __init__(self):
        self.results = []
    
    def scan_target(self, target, scan_type="-sS"):
        """
        Exécute un scan Nmap et retourne les résultats en JSON
        """
        try:
            # Fichier temporaire pour les résultats XML
            xml_file = f"/tmp/nmap_scan_{target.replace('/', '_')}.xml"
            
            # Commande Nmap
            cmd = ["nmap", scan_type, "-oX", xml_file, target]
            
            print(f"🔍 Exécution du scan: {' '.join(cmd)}")
            result = subprocess.run(cmd, capture_output=True, text=True)
            
            if result.returncode == 0:
                # Parse le résultat XML
                scan_data = self.parse_nmap_xml(xml_file)
                
                # Nettoyage
                os.remove(xml_file)
                
                return {
                    "status": "SUCCESS",
                    "target": target,
                    "scan_type": scan_type,
                    "data": scan_data
                }
            else:
                return {
                    "status": "ERROR",
                    "target": target,
                    "error": result.stderr
                }
                
        except Exception as e:
            return {
                "status": "ERROR",
                "target": target,
                "error": str(e)
            }
    
    def parse_nmap_xml(self, xml_file):
        """
        Parse le fichier XML de Nmap et extrait les informations importantes
        """
        try:
            tree = ET.parse(xml_file)
            root = tree.getroot()
            
            scan_data = {
                "hosts": [],
                "summary": {
                    "hosts_up": 0,
                    "hosts_down": 0,
                    "ports_open": 0
                }
            }
            
            for host in root.findall("host"):
                host_info = {
                    "address": host.find("address").get("addr") if host.find("address") is not None else "Unknown",
                    "status": host.find("status").get("state") if host.find("status") is not None else "unknown",
                    "ports": []
                }
                
                # Compter les hosts up/down
                if host_info["status"] == "up":
                    scan_data["summary"]["hosts_up"] += 1
                else:
                    scan_data["summary"]["hosts_down"] += 1
                
                # Ports ouverts
                ports_element = host.find("ports")
                if ports_element is not None:
                    for port in ports_element.findall("port"):
                        port_info = {
                            "port": port.get("portid"),
                            "protocol": port.get("protocol"),
                            "state": port.find("state").get("state") if port.find("state") is not None else "unknown",
                            "service": port.find("service").get("name") if port.find("service") is not None else "unknown"
                        }
                        
                        if port_info["state"] == "open":
                            scan_data["summary"]["ports_open"] += 1
                            host_info["ports"].append(port_info)
                
                scan_data["hosts"].append(host_info)
            
            return scan_data
            
        except Exception as e:
            return {"error": f"Erreur parsing XML: {str(e)}"}

if __name__ == "__main__":
    # Exemple d'utilisation
    if len(sys.argv) > 1:
        target = sys.argv[1]
        scanner = NmapScanner()
        result = scanner.scan_target(target)
        print(json.dumps(result, indent=2))
    else:
        print("Usage: python3 nmap_scanner.py <target>")
