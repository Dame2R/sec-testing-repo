import json
import sys
import boto3
import os

# Funktion, um die Ergebnisse zu Security Hub zu senden
def send_to_security_hub_in_batches(findings, batch_size=10):
    client = boto3.client('securityhub', region_name='eu-north-1')
    
    error_occurred = False

    # Teilen der Liste; API-Überladung vermeiden
    for i in range(0, len(findings), batch_size):
        batch = findings[i:i + batch_size]
        # Sende die Findings an Security Hub
        try:
            response = client.batch_import_findings(Findings=batch)
            if response['ResponseMetadata']['HTTPStatusCode'] != 200:
                print("Fehler bei der Aktualisierung der Findings")
                status_code = response['ResponseMetadata']['HTTPStatusCode']
                print(f"HTTP-Statuscode: {status_code}")
                error_occurred = True
        except Exception as e:
            print(f"Fehler beim Senden der Findings: {e}")
            error_occurred = True

    if error_occurred:
        return None
    else:
        return "Findings erfolgreich aktualisiert"
    



# Funktion, um die Ergebnisse zu verarbeiten und das Format für Security Hub anzupassen
def process_findings(data):
    findings = []
    dateoftest = os.getenv('DATE')
    for result in data:
        if 'results' in result and 'failed_checks' in result['results']:
            for failed_check in result['results']['failed_checks']:
                finding = {
                    'SchemaVersion': '2018-10-08', 
                    'Id': failed_check['check_id'],
                    'ProductArn': 'arn:aws:securityhub:eu-north-1:195545939390:product/195545939390/default', 
                    'GeneratorId': 'checkov',
                    'AwsAccountId': '195545939390',
                    'Description': failed_check['check_name'],
                    'CreatedAt': dateoftest,
                    'UpdatedAt': dateoftest,
                    "FindingProviderFields":{
                        'Severity': {
                            'Label': 'Medium',  # Annahme eines Standard-Schweregrads
                            'Original': '7'  # Annahme eines Standard-Schweregrads
                        },
                        'Types': [
                            'Software and Configuration Checks/Vulnerabilities/CVE'
                        ]
                    },
                    'Title': 'IaC-Vulnerability Scans for CloudFormation',
                    'Resources': [
                        {
                            'Id': failed_check['file_path'],
                            'Partition': 'aws',
                            'Region': 'eu-north-1',
                            'Type': failed_check['resource'],
                        }
                    ],
                }
                
                
                findings.append(finding)

    print("Findings exported successfully")
    return findings

def main():
    if len(sys.argv) != 2:
        print("Bitte den Dateinamen des Checkov-Reports als Argument übergeben.")
        sys.exit(1)

    report_file = sys.argv[1]
    findings = []

    try:
        with open(report_file, 'r') as file:
            data = json.load(file)  # Lädt die gesamte JSON-Datei
            for result in data:
                # Überprüfen, ob das Element ein Dictionary und die erwarteten Schlüssel vorhanden sind
                if isinstance(result, dict) and 'results' in result and 'failed_checks' in result['results']:
                    findings.extend(process_findings(result['results']['failed_checks']))
    except FileNotFoundError:
        print(f"Die Datei {report_file} wurde nicht gefunden.")
        sys.exit(1)
    except json.JSONDecodeError:
        print("Fehler beim Parsen der JSON-Datei.")
        sys.exit(1)
        
    try:
        response = send_to_security_hub_in_batches(findings, batch_size=10)
        print(response)
    except Exception as e:
        print(f"Fehler beim Senden der Findings an Security Hub: {e}")

if __name__ == "__main__":
    main()
