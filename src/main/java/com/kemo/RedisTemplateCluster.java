package com.kemo;

import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * 使用RedisTemplate 实现 Redis操作
 *
 * @author Jack
 * @since 2019-03-25
 */
public class RedisTemplateCluster {

    private static StringRedisTemplate redisTemplate;

    static {
        Set<String> clusterNodes = new HashSet<String>();
        clusterNodes.add("127.0.0.1:7001");
        clusterNodes.add("127.0.0.1:7002");
        clusterNodes.add("127.0.0.1:7003");
        clusterNodes.add("127.0.0.1:7004");
        clusterNodes.add("127.0.0.1:7005");
        clusterNodes.add("127.0.0.1:7006");

        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(clusterNodes);

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(100);
        config.setMaxTotal(100);
        config.setMinIdle(10);
        config.setMaxWaitMillis(2000);
        // 对获取的connection 进行校验
        config.setTestOnBorrow(true);
        // 构建ConnectionFactory
        JedisConnectionFactory factory = new JedisConnectionFactory(clusterConfiguration, config);
        factory.setPassword("foobared");
        factory.afterPropertiesSet();

        redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.afterPropertiesSet();
    }

    public static void main(String[] args) {
        System.out.println(redisTemplate.opsForValue().get("test01"));
        redisTemplate.opsForValue().set("test02", "01");
    }
}
