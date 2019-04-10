package com.kemo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClusterApplication.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ValueOperations<String, String> valueOperations;

    @Test
    public void test() {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set("spring_test", "02");

        ValueOperations<String, Object> valueOperations1 = redisTemplate.opsForValue();
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value3");
        valueOperations1.set("spring_test_1", map);

        System.out.println(valueOperations.get("spring_test"));
        valueOperations.increment("spring_incr", 1);
        System.out.println(valueOperations.get("spring_incr"));

        final StringBuilder script = new StringBuilder();
        script.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        script.append("then ");
        script.append("    return redis.call(\"del\",KEYS[1]) ");
        script.append("else ");
        script.append("    return 0 ");
        script.append("end ");

        final List<String> keys = new ArrayList<String>();
        keys.add("spring_lock");
        final List<String> argvs = new ArrayList<String>();
        argvs.add("0001");
        Long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(script.toString(), keys, argvs);
                }

                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(script.toString(), keys, argvs);
                }
                return 0L;
            }
        });
        System.out.println(result);
    }

    @Test
    public void getTest() {
        String key = "\"mine.platform.last.price.etc_usdt\"";
        String valueStr = stringRedisTemplate.opsForValue().get(key);
        System.out.println(" stringRedisTemplate >>> "+ valueStr);

        Object value = redisTemplate.opsForValue().get(key);
        System.out.println(" redisTemplate<String, Object> >>> "+ value);

        String valueResult = valueOperations.get(key);
        System.out.println(" valueOperations<String, String> >>> "+ valueResult);

    }

    @Test
    public void getHash() {
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries("all_market_hotdata");
        System.out.println(map.get("eth_btc"));
    }

    @Test
    public void eval() {

//        redisTemplate.opsForValue().set("spring_lock_class", "0001");

        final StringBuilder script = new StringBuilder();
//        script.append("local key, maxPermits, expire = KEYS[1], ARGV[1], ARGV[2] ");
//        script.append(" ");
//        script.append("local times = redis.call('incr', key) ");
//        script.append("if tonumber(times) == 1 then ");
//        script.append("    redis.call('expire', key, tonumber(expire)) ");
//        script.append("end ");
//        script.append(" ");
//        script.append("if tonumber(times) > tonumber(maxPermits) then ");
//        script.append("    return 0 ");
//        script.append("else ");
//        script.append("    return 1 ");
//        script.append("end ");

        script.append("local times = redis.call('incr', KEYS[1]) ");
        script.append("if tonumber(times) == 1 then ");
        script.append("    redis.call('expire', KEYS[1], tonumber(ARGV[2])) ");
        script.append("end ");
        script.append(" ");
        script.append("if tonumber(times) > tonumber(ARGV[1]) then ");
        script.append("    return 0 ");
        script.append("else ");
        script.append("    return 1 ");
        script.append("end ");

//        script.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
//        script.append("then ");
//        script.append("    return redis.call(\"del\",KEYS[1]) ");
//        script.append("else ");
//        script.append("    return true ");
//        script.append("end ");

        final List<String> keys = new ArrayList<String>();
        keys.add("spring_lock_class");
        keys.add("spring_lock_class");
        final List<String> argvs = new ArrayList<String>();
        argvs.add("1");
        argvs.add("10");
//        argvs.add("\"0001\"");
//        argvs.add("1");

        Object result = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return ((JedisCluster) nativeConnection).eval(script.toString(), keys, argvs);
                }

                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    return ((Jedis) nativeConnection).eval(script.toString(), keys, argvs);
                }
                return null;
            }
        });
        System.out.println(result);
    }
}
