# newrelic-serverless-integration

This is a brief description of the newrelic-serverless-integration. It aims to showcase the integration of new relic by using OpenTelemetry Java Auto-Instrumentation in a serverless microservices application.

## Table of Contents

1. [Architecture](#Architecture)
2. [Getting Started](#getting-started)
3. [Prerequisites](#prerequisites)
4. [Installation](#installation)
5. [Deployment](#deployment)
6. [Testing](#testing)


## Architecture
![newrelic-serverless-integration](/docs/img/newrelic-serverless-integration.drawio.png)

## Getting Started

These instructions will guide you through the process of setting up the newrelic-serverless-integration on your local machine for development and testing purposes. See the Deployment section for notes on how to deploy the project on an aws account.

### Prerequisites

Before you can begin using this project, ensure you have the following software installed on your local machine:

- Git
- Node.js (LTS version)
- npm
- jdk 8
- maven
- Serverless Framework v3.x ([Setting Up Serverless Framework With AWS][https://www.serverless.com/framework/docs/getting-started])

### Installation

To set up the newrelic-serverless-integration on your local machine, follow these steps:

1. Clone the repository:

`git clone git@github.com:dev-yy/newrelic-serverless-integration.git`

2. Change into the project directory:

`cd newrelic-serverless-integration`

3. Build the Multi-Module Project:

`mvn clean install`

4. Change into the user-request-service directory:

`cd user-request-service`

5. Install the required dependencies:

`npm install`

6. Change into the user-creation-service directory:

`cd ../user-creation-service`

7. Install the required dependencies:

`npm install`

### Deployment

1. Change into the user-request-service directory:

`cd ../user-request-servic`

2. Deploy the user-request-service:

`sls deploy --stage %stage% --param="owner=%owner%" --aws-profile  %profile% --verbose`

- stage: stage to deploy (dev, int, prod)
- owner: abbreviation of you own showcase (e.g. ugur)
- profile: aws credentials profile to be used for deployment

4. Change into the user-creation-service directory:

`cd ../user-creation-service`

5. Deploy the user-creation-service:

`sls deploy --stage %stage% --param="owner=%owner%" --aws-profile  %profile% --verbose`

- stage: stage to deploy (dev, int, prod)
- owner: abbreviation of you own showcase (e.g. ugur)
- profile: aws credentials profile to be used for deployment

### Testing

`
curl -X POST -H "Content-Type: application/json" -d '{
  "given_name" : "Max",
  "family_name" : "Mustermann",
  "preferred_username" : "mmustermann",
  "email" : "max.mustermann@gmail.com"
}' https://qrov8vmrij.execute-api.eu-west-1.amazonaws.com/dev/api/v1/user
`