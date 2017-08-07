package com.netflix.conductor.server;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

import java.util.concurrent.TimeUnit;


public class EhCacheConfig {

    private CacheManager cacheManager;
    private Cache<String, Boolean> tokenCache;

    public EhCacheConfig() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();

        tokenCache = cacheManager.createCache("tokenCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Boolean.class, ResourcePoolsBuilder.heap(10)).withExpiry(
            Expirations.timeToLiveExpiration(Duration.of(1, TimeUnit.DAYS))));
    }

    public Cache<String, Boolean> getTokenCache() {
        return tokenCache;
    }



}
