package com.porsche.dpp.prod.utils.aws.s3.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mhp.logging.helper.LoggingHelper;
import com.porsche.dpp.prod.utils.aws.common.AbstractInstrumentedElt;
import com.porsche.dpp.prod.utils.aws.common.S3MetadataGetter;
import com.porsche.dpp.prod.utils.aws.common.S3MetadataSetter;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3Helper extends AbstractInstrumentedElt {
   private static final Logger LOG = LogManager
      .getLogger(S3Helper.class);
   
   public static String getObject(S3Client s3Client, String bucket, String key, Tracer tracer) {
      return getObject(s3Client, bucket, key, StandardCharsets.UTF_8, tracer);
   }
   
   public static String getObject(S3Client s3Client, String bucket, String key, Charset encoding, Tracer tracer) {
      Span span = tracer.spanBuilder("S3Helper.getObject").startSpan();
      span.setAttribute("bucket.id", bucket);
      span.setAttribute("fileKey", key);
      span.setAttribute("encoding", encoding.toString());
      try (Scope scope = span.makeCurrent()) {
         GetObjectRequest getObjectRequest = null;
         getObjectRequest = GetObjectRequest
            .builder()
            .bucket(bucket)
            .key(key)
            .build();
         ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
         Map<String, String> metadata = responseBytes.response().metadata();
         LoggingHelper.info(LOG, S3Helper.class, "getObject.metadata", new Object[] {metadata});

         Context extractedContext = GlobalOpenTelemetry.getPropagators().getTextMapPropagator()
            .extract(Context.current(), metadata, S3MetadataGetter.GETTER);

         Span newSpan = tracer.spanBuilder("S3Helper.getObject")
                           .setParent(extractedContext) // Set the parent context explicitly
                           .startSpan();
         try (Scope extractedScope = newSpan.makeCurrent()) {
            byte[] data = responseBytes.asByteArray();
            String contentStr = new String(data, encoding == null ? StandardCharsets.UTF_8 : encoding);
            
            fillSpanSuccess(span);
            return contentStr;
         } catch (Exception e) {
            fillSpanError(span, e);
            throw new RuntimeException(e);
         }
         
      } catch (Exception e) {
         fillSpanError(span, e);
         throw new RuntimeException(e);
      }
      
   }
   
   public static String putObject(S3Client s3Client,
                                  String bucket, String key,
                                  String contentType, String data, Tracer tracer) {
      ByteArrayOutputStream out = getByteArrayOutputStream(data);
      return putObject(s3Client, bucket, key, contentType, out, tracer);
   }
   
   protected static String putObject(S3Client s3Client,
                                     String bucket, String key,
                                     String contentType, ByteArrayOutputStream out, Tracer tracer) {
      Span span = tracer.spanBuilder("S3Helper.putObject").startSpan();
      span.setAttribute("bucket.id", bucket);
      span.setAttribute("fileKey", key);
      span.setAttribute("contentType", key);
      
      try (Scope scope = span.makeCurrent()) {
         Map<String, String> metadata = new HashMap<>();
         // Propagate the current context into the metadata
         GlobalOpenTelemetry.getPropagators().getTextMapPropagator()
            .inject(Context.current(), metadata, S3MetadataSetter.SETTER);
         
         LoggingHelper.info(LOG, S3Helper.class, "putObject.metadata", new Object[] {metadata});
         
         // Prepare an InputStream from the ByteArrayOutputStream
         InputStream fis = new ByteArrayInputStream(out.toByteArray());
         // https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/examples-s3-objects.html
         // Put file into S3
         s3Client.putObject(
            PutObjectRequest
               .builder()
               .bucket(bucket)
               .metadata(metadata)
               .key(key)
               .build(),
            RequestBody.fromContentProvider(new ContentStreamProvider() {
               @Override
               public InputStream newStream() {
                  return fis;
               }
            }, out.toByteArray().length, contentType));
         
         String filePath = "s3://" + bucket + "/" + key;
         span.setAttribute("filePath", filePath);
         fillSpanSuccess(span);
         return filePath;
      } catch (Exception e) {
         fillSpanError(span, e);
         throw new RuntimeException(e);
      }
   }
   
   private static ByteArrayOutputStream getByteArrayOutputStream(String data) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
      try {
         out.write(data.getBytes());
         byteArrayOutputStream.flush();
         byteArrayOutputStream.close();
      } catch (Exception e) {
         throw new RuntimeException("getByteArrayOutputStream failed", e);
      }
      return byteArrayOutputStream;
   }
   
}
