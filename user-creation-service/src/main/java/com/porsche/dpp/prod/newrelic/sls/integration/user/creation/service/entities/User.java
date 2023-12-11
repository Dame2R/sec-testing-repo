package com.porsche.dpp.prod.newrelic.sls.integration.user.creation.service.entities;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class User {
   
   protected String pk;
   protected String sk;
   protected String entityType = this.getClass().getSimpleName();
   
   private String   given_name;
   
   private String   family_name;
   
   private String   preferred_username;
   
   private String   email;
   
   protected String changedDate;
   
   public User() {}
   
   public User(String given_name, String family_name, String preferred_username, String email) {
      this.given_name = given_name;
      this.family_name = family_name;
      this.preferred_username = preferred_username;
      this.email = email;
      
      updatePkAndSk();
   }
   
   @DynamoDbPartitionKey
   public String getPk() {
      return pk;
   }
   
   public void setPk(String pk) {
      this.pk = pk;
   }
   
   @DynamoDbSortKey
   public String getSk() {
      return sk;
   }
   
   public void setSk(String sk) {
      this.sk = sk;
   }
   
   public String getEntityType() {
      return entityType;
   }
   
   public void setEntityType(String entityType) {
      this.entityType = entityType;
   }
   
   public String getGiven_name() {
      return given_name;
   }
   
   public void setGiven_name(String given_name) {
      this.given_name = given_name;
   }
   
   public String getFamily_name() {
      return family_name;
   }
   
   public void setFamily_name(String family_name) {
      this.family_name = family_name;
   }
   
   public String getPreferred_username() {
      return preferred_username;
   }
   
   public void setPreferred_username(String preferred_username) {
      this.preferred_username = preferred_username;
   }
   
   public String getEmail() {
      return email;
   }
   
   public void setEmail(String email) {
      this.email = email;
   }
   
   public String getChangedDate() {
      return changedDate;
   }
   
   public void setChangedDate(String changedDate) {
      this.changedDate = changedDate;
   }
   
   public User updatePkAndSk() {
      this.setPk(
         this.getEntityType() + "#" + this.getPreferred_username());
      this.setSk(this.getEntityType() + "#" + this.getPreferred_username());
      return this;
   }
   
   public void updateChangedDate() {
      Timestamp now = new Timestamp(System.currentTimeMillis());
      this.setChangedDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S").format(now));
   }
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((preferred_username == null) ? 0 : preferred_username.hashCode());
      return result;
   }
   
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      User other = (User)obj;
      if (preferred_username == null) {
         if (other.preferred_username != null)
            return false;
      } else if (!preferred_username.equals(other.preferred_username))
         return false;
      return true;
   }
   
}
