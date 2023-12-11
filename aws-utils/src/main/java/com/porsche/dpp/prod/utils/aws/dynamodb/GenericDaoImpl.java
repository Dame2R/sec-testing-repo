package com.porsche.dpp.prod.utils.aws.dynamodb;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.logging.log4j.Logger;

import com.porsche.dpp.prod.utils.aws.common.AbstractInstrumentedElt;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public abstract class GenericDaoImpl<T> extends AbstractInstrumentedElt implements GenericDao<T> {
   
   // https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_BatchWriteItem.html
   private static final int                MAX_BATCH_SIZE = 25;
   protected static DynamoDbClient         nativeClient   = DynamodbClientFactory
      .getDynamoDbClient();
   protected static DynamoDbEnhancedClient enhancedClient = DynamodbClientFactory
      .getDynamoDbEnhancedClient();
   protected String                        tableName;
   private Class<T>                        type;
   
   @SuppressWarnings ({"unchecked", "rawtypes"})
   public GenericDaoImpl() {
      this.tableName = System.getenv(getTableNameKey());
      Type t = getClass().getGenericSuperclass();
      ParameterizedType pt = (ParameterizedType)t;
      type = (Class)pt.getActualTypeArguments()[0];
   }
   
   @SuppressWarnings ({"unchecked", "rawtypes"})
   public GenericDaoImpl(String tableName) {
      this.tableName = tableName;
      Type t = getClass().getGenericSuperclass();
      ParameterizedType pt = (ParameterizedType)t;
      type = (Class)pt.getActualTypeArguments()[0];
   }
   
   protected DynamoDbTable<T> getTable() {
      
      BeanTableSchema<T> fromBean = TableSchema.fromBean(type);
      DynamoDbTable<T> table = enhancedClient.table(tableName, fromBean);
      return table;
   }
   
   public T save(T item, Tracer tracer) {
      Span span = tracer.spanBuilder("GenericDaoImpl.save").startSpan();
      try (Scope scope = span.makeCurrent()) {
         DynamoDbTable<T> table = getTable();
         table.putItem(item);
         fillSpanSuccess(span);
         return item;
      } catch (Exception e) {
         fillSpanError(span, e);
         throw new RuntimeException(e);
      }
      
   }
   
   public T update(T item, Tracer tracer) {
      Span span = tracer.spanBuilder("GenericDaoImpl.update").startSpan();
      try (Scope scope = span.makeCurrent()) {
         DynamoDbTable<T> table = getTable();
         table.updateItem(item);
         fillSpanSuccess(span);
         return item;
      } catch (Exception e) {
         fillSpanError(span, e);
         throw new RuntimeException(e);
      }
   }
   
   public void delete(T item, Tracer tracer) {
      Span span = tracer.spanBuilder("GenericDaoImpl.delete").startSpan();
      try (Scope scope = span.makeCurrent()) {
         DynamoDbTable<T> table = getTable();
         table.deleteItem(item);
         fillSpanSuccess(span);
      } catch (Exception e) {
         fillSpanError(span, e);
         throw new RuntimeException(e);
      }
   }
   
   protected abstract String getTableNameKey();
   
   protected abstract Logger getLogger();
}
