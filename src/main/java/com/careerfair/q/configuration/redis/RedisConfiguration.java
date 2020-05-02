package com.careerfair.q.configuration.redis;

import com.careerfair.q.model.redis.Student;
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

import java.util.UUID;

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
    @Qualifier("redisCompanyTemplate")
    RedisTemplate<String, Role> redisCompanyTemplate() {
        return configureTemplate();
    }

    @Bean
    @Qualifier("redisEmployeeTemplate")
    RedisTemplate<String, String> redisEmployeeTemplate() {
        return configureTemplate();
    }

    @Bean
    @Qualifier("redisQueueTemplate")
    RedisTemplate<String, Student> redisQueueTemplate() {
        return configureTemplate();
    }

    private <K, V> RedisTemplate<K, V> configureTemplate() {
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

}
