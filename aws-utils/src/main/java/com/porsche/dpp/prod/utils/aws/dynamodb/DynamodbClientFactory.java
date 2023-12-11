package com.porsche.dpp.prod.utils.aws.dynamodb;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

public class DynamodbClientFactory {
   protected static DynamoDbClient         dynamoDbClient;
   protected static    DynamoDbEnhancedClient dynamoDbEnhancedClient;

   public DynamodbClientFactory() {
   }

   public static DynamoDbClient getDynamoDbClient() {
      return dynamoDbClient;
   }

   public static DynamoDbEnhancedClient getDynamoDbEnhancedClient() {
      return dynamoDbEnhancedClient;
   }

   static {
      String region = System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable());
      Region awsRegion = region != null ? Region.of(region) : Region.EU_WEST_1;
      dynamoDbClient = (DynamoDbClient)((DynamoDbClientBuilder)((DynamoDbClientBuilder)((DynamoDbClientBuilder)DynamoDbClient.builder().credentialsProvider(
                        EnvironmentVariableCredentialsProvider.create())).region(awsRegion)).httpClientBuilder(
                        UrlConnectionHttpClient.builder())).build();
      dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
   }
}
