package com.geogrind.geogrindbackend.config.redis

import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import java.io.Serializable

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration::class)
@EnableCaching
interface RedisConfig {
    @Bean
    fun redisCacheTemplate(redisConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, Serializable>

    @Bean
    fun cacheManager(factory: RedisConnectionFactory): CacheManager
}