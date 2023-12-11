package com.porsche.dpp.prod.utils.aws.common;

import io.opentelemetry.context.propagation.TextMapGetter;

import java.util.Map;

public class S3MetadataGetter implements TextMapGetter<Map<String, String>> {
   public static final S3MetadataGetter GETTER = new S3MetadataGetter();

   private S3MetadataGetter() {}

   @Override
   public Iterable<String> keys(Map<String, String> carrier) {
      return carrier.keySet();
   }

   @Override
   public String get(Map<String, String> carrier, String key) {
      assert carrier != null;
      return carrier.get(key);
   }
}
