package com.porsche.dpp.prod.utils.aws.common;

import java.util.Map;

import io.opentelemetry.context.propagation.TextMapSetter;

public class S3MetadataSetter implements TextMapSetter<Map<String, String>> {
   public static final S3MetadataSetter SETTER = new S3MetadataSetter();
   
   private S3MetadataSetter() {}
   
   @Override
   public void set(Map<String, String> carrier, String key, String value) {
      assert carrier != null;
      carrier.put(key, value);
   }
}
