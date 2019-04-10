package com.kemo;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cluster {

    private static JedisCluster jedis;

    static {
        Set<HostAndPort> hostAndPorts = new HashSet<HostAndPort>();
        hostAndPorts.add(new HostAndPort("127.0.0.1", 7001));
        hostAndPorts.add(new HostAndPort("127.0.0.1", 7002));
        hostAndPorts.add(new HostAndPort("127.0.0.1", 7003));
        hostAndPorts.add(new HostAndPort("127.0.0.1", 7004));
        hostAndPorts.add(new HostAndPort("127.0.0.1", 7005));
        hostAndPorts.add(new HostAndPort("127.0.0.1", 7006));

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(100);
        config.setMaxTotal(100);
        config.setMinIdle(10);
        config.setMaxWaitMillis(2000);
        // 对获取的connection 进行校验
        config.setTestOnBorrow(true);

        jedis = new JedisCluster(hostAndPorts, 10000, 1000, 1000, "foobared", config);
    }

    public static void main(String[] args) {
        System.out.println("判断Key是否存在" + jedis.exists("test"));
        jedis.set("test", "1");
        System.out.println("判断Key是否存在" + jedis.exists("test"));
        jedis.set("test01", "01");
        jedis.incr("incr_test");
        System.out.println(jedis.get("incr_test"));

        jedis.set("lock", "0002");
        // 集群模式下，不能同时操作两个KEY。风险：Lua script attempted to access a non local key in a cluster node
        StringBuilder script = new StringBuilder();
        script.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        script.append("then ");
        script.append("    return redis.call(\"del\",KEYS[1]) ");
        script.append("else ");
        script.append("    return false ");
        script.append("end ");

        List<String> keys = new ArrayList<String>();
        keys.add("lock");
        List<String> argvs = new ArrayList<String>();
        argvs.add("0001");
        System.out.println(jedis.eval(script.toString(), keys, argvs).getClass());
    }

}
