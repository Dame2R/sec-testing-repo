import json
import sys
import boto3
import os

# Funktion, um die Ergebnisse zu Security Hub zu senden
def send_to_security_hub_in_batches(findings, batch_size=10):
    client = boto3.client('securityhub', region_name='eu-west-1')
    
    # Teilen der Liste; API-Überladung vermeiden
    for i in range(0, len(findings), batch_size):
        batch = findings[i:i + batch_size]
        # Sende die Findings an Security Hub
        try:
            response = client.batch_import_findings(Findings=batch)
            if response['ResponseMetadata']['HTTPStatusCode'] == 200:
                print("Findings erfolgreich aktualisiert")
                return response
            else:
                print("Fehler bei der Aktualisierung der Findings")
                status_code = response['ResponseMetadata']['HTTPStatusCode']
                raise Exception(f"HTTP-Statuscode: {status_code}")

        except Exception as e:
            print(f"Fehler beim Senden der Findings: {e}")
    



# Funktion, um die Ergebnisse zu verarbeiten und das Format für Security Hub anzupassen
def process_findings(data):
    findings = []
    total_checks = 0
    passed_checks = 0
    dateoftest = os.getenv('DATE')
    for result in data:

        total_checks += result['summary']['passed'] + result['summary']['failed']
        passed_checks += result['summary']['passed']
        

        for failed_check in result['results']['failed_checks']:
            finding = {
                'SchemaVersion': '2018-10-08', 
                'Id': failed_check['check_id'],
                'ProductArn': 'arn:aws:securityhub:eu-west-1:113224084033:product/113224084033/default', 
                'GeneratorId': 'checkov',
                'AwsAccountId': '113224084033',
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
                        'Region': 'eu-west-1',
                        'Type': failed_check['resource'],
                    }
                ],
            }
            if total_checks > 0:
                passed_percentage = (passed_checks / total_checks) * 100
                failed_percentage = 100 - passed_percentage
            else:
                passed_percentage = 0
                failed_percentage = 0

            
            findings.append(finding)
    print("Passed percentage in this commit: ", passed_percentage)
    print("Findings exported successfully")
    return findings

def main():
    if len(sys.argv) != 2:
        print("Bitte den Dateinamen des Checkov-Reports als Argument übergeben.")
        sys.exit(1)

    report_file = sys.argv[1]

    try:
        with open(report_file, 'r') as file:
            data = json.load(file)
    except FileNotFoundError:
        print(f"Die Datei {report_file} wurde nicht gefunden.")
        sys.exit(1)

    findings = process_findings(data)
    try:
        response = send_to_security_hub_in_batches(findings, batch_size=10)
    except Exception as e:
            print(f"Fehler beim Senden der Findings an Security Hub: {e}")
    print(response)

if __name__ == "__main__":
    main()
