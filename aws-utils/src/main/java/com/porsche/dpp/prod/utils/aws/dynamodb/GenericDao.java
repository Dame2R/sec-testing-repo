package com.porsche.dpp.prod.utils.aws.dynamodb;

import io.opentelemetry.api.trace.Tracer;

public interface GenericDao<T> {
   public T save(T item, Tracer tracer);
   
   public T update(T item, Tracer tracer);
   
   public void delete(T item, Tracer tracer);
   
}
