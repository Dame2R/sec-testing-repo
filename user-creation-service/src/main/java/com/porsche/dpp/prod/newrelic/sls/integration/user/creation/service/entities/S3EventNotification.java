package com.porsche.dpp.prod.newrelic.sls.integration.user.creation.service.entities;

import java.util.List;

public class S3EventNotification {
   private List<Record> Records;

   public List<Record> getRecords() {
      return Records;
   }

   public void setRecords(List<Record> records) {
      this.Records = records;
   }

   public static class Record {
      private String eventVersion;
      private String eventSource;
      private String awsRegion;
      private String eventTime;
      private String eventName;
      private UserIdentity userIdentity;
      private RequestParameters requestParameters;
      private ResponseElements responseElements;
      private S3 s3;

      public String getEventVersion() {
         return eventVersion;
      }

      public void setEventVersion(String eventVersion) {
         this.eventVersion = eventVersion;
      }

      public String getEventSource() {
         return eventSource;
      }

      public void setEventSource(String eventSource) {
         this.eventSource = eventSource;
      }

      public String getAwsRegion() {
         return awsRegion;
      }

      public void setAwsRegion(String awsRegion) {
         this.awsRegion = awsRegion;
      }

      public String getEventTime() {
         return eventTime;
      }

      public void setEventTime(String eventTime) {
         this.eventTime = eventTime;
      }

      public String getEventName() {
         return eventName;
      }

      public void setEventName(String eventName) {
         this.eventName = eventName;
      }

      public UserIdentity getUserIdentity() {
         return userIdentity;
      }

      public void setUserIdentity(UserIdentity userIdentity) {
         this.userIdentity = userIdentity;
      }

      public RequestParameters getRequestParameters() {
         return requestParameters;
      }

      public void setRequestParameters(RequestParameters requestParameters) {
         this.requestParameters = requestParameters;
      }

      public ResponseElements getResponseElements() {
         return responseElements;
      }

      public void setResponseElements(ResponseElements responseElements) {
         this.responseElements = responseElements;
      }

      public S3 getS3() {
         return s3;
      }

      public void setS3(S3 s3) {
         this.s3 = s3;
      }
   }

   public static class UserIdentity {
      private String principalId;

      public String getPrincipalId() {
         return principalId;
      }

      public void setPrincipalId(String principalId) {
         this.principalId = principalId;
      }
   }

   public static class RequestParameters {
      private String sourceIPAddress;

      public String getSourceIPAddress() {
         return sourceIPAddress;
      }

      public void setSourceIPAddress(String sourceIPAddress) {
         this.sourceIPAddress = sourceIPAddress;
      }
   }

   public static class ResponseElements {
      private String xAmzRequestId;
      private String xAmzId2;

      public String getXAmzRequestId() {
         return xAmzRequestId;
      }

      public void setXAmzRequestId(String xAmzRequestId) {
         this.xAmzRequestId = xAmzRequestId;
      }

      public String getXAmzId2() {
         return xAmzId2;
      }

      public void setXAmzId2(String xAmzId2) {
         this.xAmzId2 = xAmzId2;
      }
   }

   public static class S3 {
      private String s3SchemaVersion;
      private String configurationId;
      private Bucket bucket;
      private ObjectData object;

      public String getS3SchemaVersion() {
         return s3SchemaVersion;
      }

      public void setS3SchemaVersion(String s3SchemaVersion) {
         this.s3SchemaVersion = s3SchemaVersion;
      }

      public String getConfigurationId() {
         return configurationId;
      }

      public void setConfigurationId(String configurationId) {
         this.configurationId = configurationId;
      }

      public Bucket getBucket() {
         return bucket;
      }

      public void setBucket(Bucket bucket) {
         this.bucket = bucket;
      }

      public ObjectData getObject() {
         return object;
      }

      public void setObject(ObjectData object) {
         this.object = object;
      }
   }

   public static class Bucket {
      private String name;
      private OwnerIdentity ownerIdentity;
      private String arn;

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public OwnerIdentity getOwnerIdentity() {
         return ownerIdentity;
      }

      public void setOwnerIdentity(OwnerIdentity ownerIdentity) {
         this.ownerIdentity = ownerIdentity;
      }

      public String getArn() {
         return arn;
      }

      public void setArn(String arn) {
         this.arn = arn;
      }
   }

   public static class OwnerIdentity {
      private String principalId;

      public String getPrincipalId() {
         return principalId;
      }

      public void setPrincipalId(String principalId) {
         this.principalId = principalId;
      }
   }

   public static class ObjectData {
      private String key;
      private int size;
      private String eTag;
      private String versionId;
      private String sequencer;

      public String getKey() {
         return key;
      }

      public void setKey(String key) {
         this.key = key;
      }

      public int getSize() {
         return size;
      }

      public void setSize(int size) {
         this.size = size;
      }

      public String getETag() {
         return eTag;
      }

      public void setETag(String eTag) {
         this.eTag = eTag;
      }

      public String getVersionId() {
         return versionId;
      }

      public void setVersionId(String versionId) {
         this.versionId = versionId;
      }

      public String getSequencer() {
         return sequencer;
      }

      public void setSequencer(String sequencer) {
         this.sequencer = sequencer;
      }
   }
}
