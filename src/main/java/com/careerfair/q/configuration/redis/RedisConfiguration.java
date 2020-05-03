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
    @Qualifier("companyRedisTemplate")
    RedisTemplate<String, Role> companyRedisTemplate() {
        return configureTemplate();
    }

    @Bean
    @Qualifier("employeeRedisTemplate")
    RedisTemplate<String, String> employeeRedisTemplate() {
        return configureTemplate();
    }

    @Bean
    @Qualifier("queueRedisTemplate")
    RedisTemplate<String, Student> queueRedisTemplate() {
        return configureTemplate();
    }

    @Bean
    @Qualifier("studentRedisTemplate")
    RedisTemplate<String, String> studentRedisTemplate() {
        return configureTemplate();
    }

    private <K, V> RedisTemplate<K, V> configureTemplate() {
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

}
