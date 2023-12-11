package com.porsche.dpp.prod.newrelic.sls.integration.user.request.service.helpers;

public class StringHelper {
   
   public final static String getXthElement(String string, int number, String separator) {
      if (isNullOrEmpty(string)) {
         return string;
      }
      String[] splitted = string.split(separator);
      if (splitted.length <= number - 1) {
         return "";
      }
      return splitted[number - 1];
   }
   
   public static final boolean isNullOrEmpty(String string) {
      return string == null || string.length() == 0;
   }
}
