package com.porsche.dpp.prod.newrelic.sls.integration.user.creation.service.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.porsche.dpp.prod.newrelic.sls.integration.user.creation.service.entities.S3EventNotification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.mhp.aws.commons.lang.base.JsonHelper;
import com.mhp.logging.helper.LoggingHelper;
import com.porsche.dpp.prod.newrelic.sls.integration.user.creation.service.daos.impl.UserDaoImpl;
import com.porsche.dpp.prod.newrelic.sls.integration.user.creation.service.entities.User;
import com.porsche.dpp.prod.utils.aws.lambda.AbstractLambdaHandler;
import com.porsche.dpp.prod.utils.aws.s3.helpers.S3ClientFactory;
import com.porsche.dpp.prod.utils.aws.s3.helpers.S3Helper;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import software.amazon.awssdk.services.s3.S3Client;

public class CreateUserHandler extends AbstractLambdaHandler<Map<String, Object>, Void> {
   
   private static final Logger      LOG      = LogManager
      .getLogger(CreateUserHandler.class);
   private final static S3Client    s3Client = S3ClientFactory.getS3Client();
   private final static UserDaoImpl userDao  = new UserDaoImpl();
   
   @Override
   public Void execute(Map<String, Object> input, Context context, Tracer tracer) {
      // public Void execute(S3Event input, Context context, Tracer tracer) {
      Map<String, Object> msg = new HashMap<>();
      Span span = tracer.spanBuilder("execute").startSpan();
      try (Scope scope = span.makeCurrent()) {
         msg.put("action", "execute");
         msg.put("initiator", this);
         msg.put("input", input);
         msg.put("context", context);
         LoggingHelper.info(LOG, this, "execute", new Object[] {input});
         
         String inputStr = JsonHelper.writeValueAsString(input);
         S3EventNotification event = JsonHelper.readValueAsObject(inputStr, S3EventNotification.class);
         
         // Map<String,Object> records = JsonHelper.readValue(input, "Records",Object.class);
         List<S3EventNotification.Record> records = event.getRecords();
         
         if (records == null || records.isEmpty()) {
            msg.put("additional_info", "records empty");
            span.setAttribute("additional_info", "records empty");
            return null;
         }
         S3EventNotification.Record record = records.get(0);
         String bucket = record.getS3().getBucket().getName();
         String key = record.getS3().getObject().getKey();
         
         msg.put("bucket", bucket);
         msg.put("key", key);
         msg.put("size", record.getS3().getObject().getSize());
         
         LoggingHelper.info(getLogger(), this, "execute.msg", new Object[] {msg});
         
         String json = S3Helper.getObject(s3Client, bucket, key, tracer);
         User user = JsonHelper.readValueAsObject(json, User.class);
         user.updatePkAndSk();
         user.updateChangedDate();
         
         msg.clear();
         msg.put("action", "saveUser");
         msg.put("user", user);
         LoggingHelper.info(LOG, this, "execute", new Object[] {msg});
         userDao.save(user, tracer);
         
         msg.clear();
         msg.put("action", "handleRequest");
         msg.put("return_value", user);
         LOG.info(JsonHelper.writeValueAsString(msg));
         
         fillSpanSuccess(span);
         return null;
      } catch (Exception e) {
         msg.put("exception", e);
         fillSpanError(span, e);
         LOG.error(JsonHelper.writeValueAsString(e));
         throw new RuntimeException(e);
      }
   }
   
   @Override
   protected Logger getLogger() {
      return LOG;
   }
   
}
