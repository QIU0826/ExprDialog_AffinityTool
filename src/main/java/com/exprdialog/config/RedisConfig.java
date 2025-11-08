package com.exprdialog.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 配置简单的本地缓存管理器（替代Redis）
     */
    @Bean
    public CacheManager cacheManager() {
        // 使用Spring的ConcurrentMapCacheManager作为简单的内存缓存实现
        // 这将在内存中创建一个并发哈希映射来存储缓存数据
        return new ConcurrentMapCacheManager("affinity");
    }
}