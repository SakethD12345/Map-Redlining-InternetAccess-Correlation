package edu.brown.cs.student.main.Handlers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

public class CacheProxy {
  Cache<Object, Object> cache;
  public CacheProxy(int maxSize, int maxMinutes) {
    cache = CacheBuilder.newBuilder()
        .maximumSize(maxSize)
        .expireAfterWrite(maxMinutes, TimeUnit.MINUTES)
        .build();
  }

  public boolean isInCache(Object key) {
    return cache.asMap().containsKey(key);
  }

  public Object getValueFromCache(Object key) {
    return cache.asMap().get(key);
  }

  public void addValueToCache(Object key, Object value) {
    cache.put(key, value);
  }
}
