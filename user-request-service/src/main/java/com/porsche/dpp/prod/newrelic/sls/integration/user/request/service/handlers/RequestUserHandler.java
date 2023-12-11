package com.porsche.dpp.prod.newrelic.sls.integration.user.request.service.handlers;

import java.util.HashMap;
import java.util.Map;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.porsche.dpp.prod.newrelic.sls.integration.user.request.service.dtos.UserRequestDto;
import com.porsche.dpp.prod.newrelic.sls.integration.user.request.service.helpers.APIGatewayProxyResponseEventBuilder;
import com.porsche.dpp.prod.newrelic.sls.integration.user.request.service.helpers.JsonHelper;
import com.porsche.dpp.prod.newrelic.sls.integration.user.request.service.helpers.StringHelper;
import com.porsche.dpp.prod.utils.aws.lambda.AbstractLambdaHandler;
import com.porsche.dpp.prod.utils.aws.s3.helpers.S3ClientFactory;
import com.porsche.dpp.prod.utils.aws.s3.helpers.S3Helper;

import io.opentelemetry.api.trace.Tracer;
import software.amazon.awssdk.services.s3.S3Client;

public class RequestUserHandler extends
                  AbstractLambdaHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
   
   private static final Logger   LOG      = LogManager
      .getLogger(RequestUserHandler.class);
   private final static S3Client s3Client = S3ClientFactory.getS3Client();
   
   @Override
   public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent input, Context context, Tracer tracer) {
      
       Span span = tracer.spanBuilder("handleRequest").startSpan();
       try(Scope scope = span.makeCurrent()) {

         String correlationId = context.getAwsRequestId();
         ThreadContext.put("correlationId", correlationId);
         String awsAccountId = StringHelper.getXthElement(context.getInvokedFunctionArn(), 5, ":");
         ThreadContext.put("awsAccountId", awsAccountId);
         
         Map<String, Object> msg = new HashMap<>();
         msg.put("action", "handleRequest");
         msg.put("initiator", this);
         msg.put("input", input);
         msg.put("context", context);
         LOG.info(JsonHelper.writeValueAsString(msg));
         
         UserRequestDto userRequest = JsonHelper.readValueAsObject(input.getBody(), UserRequestDto.class);
         
         String bucket = System.getenv("USER_REQUESTS_BUCKET");
         String key = "requests/" + userRequest.getPreferred_username() + ".json";
         String json = JsonHelper.writeValueAsString(userRequest);
         
         msg.clear();
         msg.put("action", "putObject");
         msg.put("bucket", bucket);
         msg.put("key", key);
         msg.put("content", userRequest);
         
         LOG.info(JsonHelper.writeValueAsString(msg));
         
         String path = S3Helper.putObject(s3Client, bucket, key, "application/json", json,tracer);
         Map<String, Object> body = new HashMap<>();
         body.put("s3Path", path);
         
         msg.clear();
         msg.put("action", "handleRequest");
         msg.put("return_value", path);
         LOG.info(JsonHelper.writeValueAsString(msg));
         
         APIGatewayProxyResponseEvent response = APIGatewayProxyResponseEventBuilder
            .apiGatewayProxyResponseEvent()
            .statusCode(201)
            .header("X-Powered-By", "AWS Lambda & Serverless")
            .header("Content-Type", "application/json")
            .objectBody(JsonHelper.writeValueAsString(body))
            .build();

         fillSpanSuccess(span);
         
         return response;
      } catch (Exception e) {
         Map<String, Object> msg = new HashMap<>();
         msg.put("exception", e);
         fillSpanError(span,e);
         LOG.error(JsonHelper.writeValueAsString(e));
         throw new RuntimeException(e);
      }
   }

   @Override protected Logger getLogger() {
      return LOG;
   }

}
