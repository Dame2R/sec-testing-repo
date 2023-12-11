package com.porsche.dpp.prod.utils.aws.lambda;

import com.mhp.logging.helper.LoggingHelper;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.porsche.dpp.prod.utils.aws.common.AbstractInstrumentedElt;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import java.util.UUID;

public abstract class AbstractLambdaHandler<ACTION, RESULT> extends AbstractInstrumentedElt
                  implements RequestHandler<ACTION, RESULT> {
   static {
      System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
   }
   private final Tracer tracer = GlobalOpenTelemetry.getTracer(this.getClass().getCanonicalName(), "0.0.1");
   
   public AbstractLambdaHandler() {}
   
   protected static Throwable findRootCause(Throwable throwable, Span span) {
      Throwable rootCause;
      for (rootCause = throwable; rootCause.getCause() != null
                                  && rootCause.getCause() != rootCause; rootCause = rootCause.getCause()) {}
      
      fillSpanError(span, rootCause);
      return rootCause;
   }
   
   public static final boolean isNullOrEmpty(String string) {
      return string == null || string.length() == 0;
   }
   
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
   
   public RESULT handleRequest(ACTION input, Context context) {
      Span span = tracer.spanBuilder("handleRequest").startSpan();
      Object var7;
      try (Scope scope = span.makeCurrent()) {
         this.preExecute(input, context, tracer, span);
         if (this.isWarmUpEvent(input, context, span)) {
            var7 = this.handleWarmUpEvent();
            return (RESULT)var7;
         }
         
         RESULT result = this.execute(input, context, tracer);
         
         var7 = result;
         
         fillSpanSuccess(span);
      } catch (Throwable var10) {
         throw this.handleRootCause(input, context, var10, span);
      }
      finally {
         this.postExecute(input, context, tracer);
      }
      
      return (RESULT)var7;
   }
   
   protected void postExecute(ACTION input, Context context, Tracer tracer) {}
   
   protected void preExecute(ACTION input, Context context, Tracer tracer, Span span) {
      this.addCorrelationIdToContext(input, context, span);
      this.addAwsAccountIdToContext(input, context, span);
   }
   
   protected RuntimeException handleRootCause(ACTION input, Context context, Throwable e,
                                              Span span) {
      Throwable rootCause = findRootCause(e, span);
      LoggingHelper.error(getLogger(),this,"handleRootCause", new Object[]{},rootCause, UUID.randomUUID().toString());
      String msg = !isNullOrEmpty(rootCause.getMessage()) ? rootCause.getMessage() : e.getMessage();
      return this.wrapToRuntimeException(msg, e);
   }
   
   protected RuntimeException wrapToRuntimeException(String msg, Throwable e) {
      return new RuntimeException(msg, e);
   }
   
   protected void addAwsAccountIdToContext(ACTION input, Context context, Span span) {
      if (context != null) {
         String awsAccountId = getXthElement(context.getInvokedFunctionArn(), 5, ":");
         ThreadContext.put("awsAccountId", awsAccountId);
         span.setAttribute("awsAccountId", awsAccountId);
      }
      
   }
   
   protected void addCorrelationIdToContext(ACTION input, Context context, Span span) {
      if (context != null) {
         String correlationId = context.getAwsRequestId();
         ThreadContext.put("correlationId", correlationId);
         span.setAttribute("correlationId", correlationId);
      }
      
   }
   
   protected RESULT handleWarmUpEvent() {
      return null;
   }
   
   protected boolean isWarmUpEvent(ACTION input, Context context, Span span) {
      boolean isWarmUpEvent = false;
      if (context != null && context.getClientContext() != null && context.getClientContext().getCustom() != null
          && "serverless-plugin-warmup".equals(context.getClientContext().getCustom().get("source"))) {
         isWarmUpEvent = true;
      }
      
      span.setAttribute("isWarmUpEvent", isWarmUpEvent);
      
      return isWarmUpEvent;
   }
   
   protected abstract RESULT execute(ACTION var1, Context var2, Tracer tracer);
   protected abstract Logger getLogger();
}
