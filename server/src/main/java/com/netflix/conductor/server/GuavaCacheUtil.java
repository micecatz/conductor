package com.netflix.conductor.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaCacheUtil {

    private static final ConcurrentHashMap<String, Boolean> tokenCache = new ConcurrentHashMap<>();

    private static LoadingCache<String, Boolean> LoadTokenCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).weakKeys().build(new CacheLoader<String, Boolean>(){
        @Override
        public Boolean load(String token) throws Exception {
            return tokenCache.get(token);
        }
    });

    public boolean getTokenCache(String token) throws ExecutionException {
        Boolean status = LoadTokenCache.getIfPresent(token);
        if(status == null){
            return false;
        }
        return status;
    }

    public void insertTokenCache(String token, Boolean status) throws ExecutionException {
        LoadTokenCache.put(token, status);
    }
}
