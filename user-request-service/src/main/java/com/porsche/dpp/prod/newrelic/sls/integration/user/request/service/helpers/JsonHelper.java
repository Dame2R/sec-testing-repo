package com.porsche.dpp.prod.newrelic.sls.integration.user.request.service.helpers;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonHelper {
   
   private final static ObjectMapper OBJECTMAPPER = new ObjectMapper();
   
   static {
      OBJECTMAPPER.disable(SerializationFeature.INDENT_OUTPUT);
      OBJECTMAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      OBJECTMAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
      OBJECTMAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      // OBJECTMAPPER.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
      // OBJECTMAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
      OBJECTMAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
      
   }
   
   private JsonHelper() {}
   
   public static ObjectMapper getObjectMapper() {
      return OBJECTMAPPER;
   }
   
   public static String writeValueAsString(Object obj) {
      return writeValueAsString(obj, false);
   }
   
   public static String writeValueAsString(Object obj, boolean usePrettyPrinter) {
      try {
         String json;
         if (usePrettyPrinter) {
            json = OBJECTMAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
         } else {
            json = OBJECTMAPPER.writeValueAsString(obj);
         }
         return json;
      } catch (IOException e) {
         throw new RuntimeException("writeValueAsString failed", e);
      }
   }
   
   public static <T> T readValueAsObject(String json, Class<T> clazz) {
      return convertStrToObject(json, clazz);
   }
   
   public static <T> T convertStrToObject(String objStr, Class<T> clazz) {
      try {
         T o = OBJECTMAPPER.readValue(objStr, clazz);
         return o;
      } catch (Exception e) {
         throw new RuntimeException("convertStrToObject failed", e);
      }
   }
}
