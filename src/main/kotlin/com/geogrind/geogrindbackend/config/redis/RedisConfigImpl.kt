package com.geogrind.geogrindbackend.config.redis

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.io.Serializable
import java.time.Duration

// configure redis caching layer for faster data retrieval
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration::class)
@EnableCaching
class RedisConfigImpl : RedisConfig {

    // Load environment variables from the .env file
    private val dotenv = Dotenv.configure().directory(".").load()

    private val redisHost = dotenv["REDIS_HOST"]

    private val redisPort = dotenv["REDIS_PORT"].toInt()

    private val redisPassword = dotenv["REDIS_PASSWORD"]

    @Bean
    override fun redisConnectionFactory(): LettuceConnectionFactory {
        val configuration = RedisStandaloneConfiguration(redisHost, redisPort)
        configuration.setPassword(redisPassword)
        return LettuceConnectionFactory(configuration)
    }

    @Bean
    override fun redisCacheTemplate(redisConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, Serializable> {
        val template = RedisTemplate<String, Serializable>()
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        template.connectionFactory = redisConnectionFactory
        return template
    }

    @Bean
    override fun cacheManager(factory: RedisConnectionFactory): CacheManager {
        val config = RedisCacheConfiguration.defaultCacheConfig()
        val redisCacheConfiguration = config
            .entryTtl(Duration.ofMinutes(60)) // Set the cache expiration time to clear the Redis cache
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())
            )
        val redisCacheManager = RedisCacheManager.builder(factory).cacheDefaults(redisCacheConfiguration).build()
        return redisCacheManager
    }
}