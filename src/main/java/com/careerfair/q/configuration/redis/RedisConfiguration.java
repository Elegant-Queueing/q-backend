package com.careerfair.q.configuration.redis;

import com.careerfair.q.util.enums.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@PropertySource(value = "application.properties")
public class RedisConfiguration {

    @Value("${redis.hostname}")
    private String hostname;

    @Value("${redis.port}")
    private int port;

    @Bean
    JedisConnectionFactory redisConnectionFactory() {
        return new JedisConnectionFactory(new RedisStandaloneConfiguration(hostname, port));
    }

    @Bean
    @Qualifier("redisVirtualQueueTemplate")
    RedisTemplate<String, Role> redisVirtualQueueTemplate() {
        RedisTemplate<String, Role> redisTemplate = new RedisTemplate<>();
        return configureTemplate(redisTemplate);
    }

    @Bean
    @Qualifier("redisEmployeeTemplate")
    RedisTemplate<String, String> redisEmployeeTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        return configureTemplate(redisTemplate);
    }

    private <K, V> RedisTemplate<K, V> configureTemplate(RedisTemplate<K, V> redisTemplate) {
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

}
