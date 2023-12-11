package com.porsche.dpp.prod.utils.aws.s3.helpers;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

public class S3ClientFactory {
   private static final S3Client s3Client;
   
   static {
      String region = System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable());
      Region awsRegion = region != null ? Region.of(region) : Region.EU_WEST_1;
      s3Client = (S3Client)((S3ClientBuilder)((S3ClientBuilder)((S3ClientBuilder)S3Client.builder().credentialsProvider(
         EnvironmentVariableCredentialsProvider.create())).region(awsRegion)).httpClientBuilder(
            UrlConnectionHttpClient.builder())).build();
   }
   
   private S3ClientFactory() {}
   
   public static S3Client getS3Client() {
      return s3Client;
   }
}
