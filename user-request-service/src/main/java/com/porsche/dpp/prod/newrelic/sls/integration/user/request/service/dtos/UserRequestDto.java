package com.porsche.dpp.prod.newrelic.sls.integration.user.request.service.dtos;

import com.porsche.dpp.prod.newrelic.sls.integration.user.request.service.helpers.JsonHelper;

public class UserRequestDto {
   
   private String given_name;
   
   private String family_name;
   
   private String preferred_username;
   
   private String email;
   
   public UserRequestDto() {}
   
   public UserRequestDto(String given_name, String family_name, String preferred_username, String email) {
      this.given_name = given_name;
      this.family_name = family_name;
      this.preferred_username = preferred_username;
      this.email = email;
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
      UserRequestDto other = (UserRequestDto)obj;
      if (preferred_username == null) {
         if (other.preferred_username != null)
            return false;
      } else if (!preferred_username.equals(other.preferred_username))
         return false;
      return true;
   }
   
   public static void main(String[] args) {
      System.out.println(
         JsonHelper.writeValueAsString(new UserRequestDto("Yang", "Yang", "yyang", "yang.yang@mhp.com"), true));
   }
}
