package com.porsche.dpp.prod.utils.aws.common;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

public abstract class AbstractInstrumentedElt {
   
   protected static void fillSpanSuccess(Span span) {
      span.setAttribute("status", "i.O.");
      span.end();
   }
   
   protected static void fillSpanError(Span span, Throwable e) {
      span.setAttribute(SemanticAttributes.OTEL_STATUS_CODE, SemanticAttributes.OtelStatusCodeValues.ERROR);
      span.setAttribute(SemanticAttributes.OTEL_STATUS_DESCRIPTION, "Ann error occured in S3Helper");
      span.setAttribute(SemanticAttributes.EXCEPTION_TYPE, e.getClass().getCanonicalName());
      span.setAttribute(SemanticAttributes.EXCEPTION_MESSAGE, e.getMessage());
      span.setAttribute(SemanticAttributes.EXCEPTION_STACKTRACE, convertExceptionStackTraceToString(e));
      span.setAttribute("status", "n.i.O.");
      span.end();
   }
   
   protected static String convertExceptionStackTraceToString(Throwable e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      return sw.toString();
   }
}
