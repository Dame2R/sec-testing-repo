package com.porsche.dpp.prod.newrelic.sls.integration.user.request.service.helpers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class APIGatewayProxyResponseEventBuilder {
   
   private int                 statusCode = 200;
   private Map<String, String> headers    = new HashMap<>();
   private String              rawBody;
   private boolean             base64Encoded;
   
   private APIGatewayProxyResponseEventBuilder() {
      
   }
   
   public static APIGatewayProxyResponseEventBuilder apiGatewayProxyResponseEvent() {
      return new APIGatewayProxyResponseEventBuilder();
   }
   
   public APIGatewayProxyResponseEventBuilder statusCode(int statusCode) {
      this.statusCode = statusCode;
      return this;
   }
   
   public APIGatewayProxyResponseEventBuilder header(String key, String value) {
      this.headers.put(key, value);
      return this;
   }
   
   public APIGatewayProxyResponseEventBuilder headers(Map<String, String> headers) {
      this.headers = headers;
      return this;
   }
   
   /**
    * Builds the {@link ApiGatewayResponse} using the passed raw body string.
    */
   public APIGatewayProxyResponseEventBuilder rawBody(String rawBody) {
      this.rawBody = rawBody;
      return this;
   }
   
   /**
    * Builds the {@link ApiGatewayResponse} using the passed object body converted to JSON.
    */
   public APIGatewayProxyResponseEventBuilder objectBody(Object objectBody) {
      this.rawBody = JsonHelper.writeValueAsString(objectBody);
      return this;
   }
   
   /**
    * Builds the {@link ApiGatewayResponse} using the passed binary body encoded as base64.
    * {@link #setBase64Encoded(boolean) setBase64Encoded(true)} will be in invoked automatically.
    */
   public APIGatewayProxyResponseEventBuilder binaryBody(byte[] binaryBody) {
      this.rawBody = new String(Base64.getEncoder().encode(binaryBody), StandardCharsets.UTF_8);
      base64Encoded(true);
      return this;
   }
   
   /**
    * A binary or rather a base64encoded responses requires
    * <ol>
    * <li>"Binary Media Types" to be configured in API Gateway
    * <li>a request with an "Accept" header set to one of the "Binary Media Types"
    * </ol>
    */
   public APIGatewayProxyResponseEventBuilder base64Encoded(boolean base64Encoded) {
      this.base64Encoded = base64Encoded;
      return this;
   }
   
   public APIGatewayProxyResponseEvent build() {
      APIGatewayProxyResponseEvent event = new APIGatewayProxyResponseEvent();
      event.setStatusCode(this.statusCode);
      event.setBody(this.rawBody);
      event.setHeaders(this.headers);
      event.setIsBase64Encoded(this.base64Encoded);
      return event;
   }
   
}
