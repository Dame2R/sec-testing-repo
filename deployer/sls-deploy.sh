#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

owner=$(echo $owner | sed 's/\r//')
if [ ! -z "$TARGET_OWNER" ]; then
  owner="$TARGET_OWNER"
elif [ -z "$owner" ]; then
  owner=mhp
fi

echo "***********************************************************************"
echo "**********************Base Properties**********************************"
echo "***********************************************************************"
echo 'owner='$owner
echo 'TARGET_STAGE='$TARGET_STAGE

# Declare a string array
services=("user-creation-service" "user-request-service")

echo "***********************************************************************"
echo "**********************Deploy Artifacts*********************************"
echo "***********************************************************************"
# Iterate the loop to read and print each array element
for value in "${services[@]}"; do
  echo $value
done

for value in "${services[@]}"; do
  echo "***********************************************************************"
  echo "**********************Deploying ${value}*********************************"
  echo "***********************************************************************"
  cd ..

  echo "$value"
  cd $value

  echo "npm install --silent --no-progress -g serverless"
  npm install --silent --no-progress

  echo "sls deploy --stage $TARGET_STAGE --param="owner=$owner" --conceal --verbose"
  sls deploy --stage $TARGET_STAGE --param="owner=$owner" --conceal --verbose

  cd ..
  cd deployer
done
