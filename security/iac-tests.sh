#!/bin/bash

#CommitTrigger

#exit code handling with bypass
set -e
final_exit_code=0

services=("user-creation-service" "user-request-service")
cd $CODEBUILD_SRC_DIR
cd /src


for value in "${services[@]}"; do
  echo "***********************************************************************"
  echo "**********************Starting first tests ${value}********************"
  echo "***********************************************************************"
  #cd /newrelic-serverless-integration
  cd /src
  
  echo "cd $value"
  cd $value
  
  #docker update

  echo "Bypass immediate exit and catch later"
  set +e

  echo "checkov -d . --soft-fail-on MEDIUM --external-checks-git https://github.com/Dame2R/checkov-custom-policies.git --bc-api-key 651257ba-5649-4c4f-b635-c40bf9c1493f --repo-id Dame2R/sec-testing-repo --framework cloudformation -o junitxml > checkov-report-$value-$COMMIT_ID.xml"
  checkov -d . --soft-fail-on MEDIUM --external-checks-git https://github.com/Dame2R/checkov-custom-policies.git --bc-api-key 651257ba-5649-4c4f-b635-c40bf9c1493f --repo-id Dame2R/sec-testing-repo -o junitxml > checkov-report-$value-$COMMIT_ID.xml --framework cloudformation
  exit_code=$?

  echo "Exit Code after IaC-Scan: $final_exit_code"

  set -e

  # Aktualisiere den finalen Exit-Code, falls Checkov einen Fehler gefunden hat
  if [ $exit_code -ne 0 ]; then
    final_exit_code=$exit_code
  fi


  #echo "Handling and Reporting to Security Hub - is not implemented"
  #python3 /src/reporting-handler.py "checkov-report-$value-$COMMIT_ID.json"
  
  echo "***********************************************************************"
  echo "**********************Ended first tests ${value}***********************"
  echo "***********************************************************************"
  cd ..
  cd security
done

exit $final_exit_code