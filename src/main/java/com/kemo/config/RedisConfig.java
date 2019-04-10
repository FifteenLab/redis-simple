package com.kemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

//    @Autowired
//    private RedisProperties properties;

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        JedisConnectionFactory factory = createConnectionFactory();
//        factory.setPassword(this.properties.getPassword());
//        return factory;
//    }
//
//    private JedisConnectionFactory createConnectionFactory() {
//        RedisProperties.Cluster cluster = properties.getCluster();
//        JedisPoolConfig jedisPoolConfig = jedisPoolConfig();
//        if (cluster == null || cluster.getNodes() == null || cluster.getNodes().isEmpty()) {
//            return createRedisConnectionFactory(jedisPoolConfig, this.properties.getHost(), this.properties.getPort());
//        }
//
//        List<String> nodes = cluster.getNodes();
//        if (nodes.size() == 1) {
//            String[] hostAndPort = nodes.get(0).split(":");
//            Assert.state(hostAndPort.length == 2, "Must be defined as 'host:port'");
//            return createRedisConnectionFactory(jedisPoolConfig, hostAndPort[0], Integer.valueOf(hostAndPort[1]));
//        }
//
//        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(this.properties.getCluster().getNodes());
//        return new JedisConnectionFactory(clusterConfiguration, jedisPoolConfig);
//    }
//
//    private JedisConnectionFactory createRedisConnectionFactory(JedisPoolConfig jedisPoolConfig, String host, Integer port) {
//        JedisConnectionFactory factory;
//        factory = new JedisConnectionFactory(jedisPoolConfig);
//        factory.setHostName(host);
//        factory.setPort(port);
//        return factory;
//    }
//
//    private JedisPoolConfig jedisPoolConfig() {
//        JedisPoolConfig config = new JedisPoolConfig();
//        RedisProperties.Pool props = this.properties.getPool();
//        config.setMaxTotal(props.getMaxActive());
//        config.setMaxIdle(props.getMaxIdle());
//        config.setMinIdle(props.getMinIdle());
//        config.setMaxWaitMillis(props.getMaxWait());
//        return config;
//    }

    /**
     * 创建RedisTemplate
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(factory);

        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());

        StringRedisSerializer stringRedisSerializer =  new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        return redisTemplate;
    }

    @Bean
    public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate) {
        // 实际传入的参数是 StringRedisTemplate
        return redisTemplate.opsForValue();
    }

}
