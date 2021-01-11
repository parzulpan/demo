import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author parzulpan
 * @version 1.0
 * @date 2021-01
 * @project JedisTest
 * @package PACKAGE_NAME
 * @desc API 使用
 */

public class API {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.56.56", 6379);

        // key
        Set<String> keys = jedis.keys("*");
        keys.forEach(System.out::println);
        System.out.println(jedis.exists("test"));
        System.out.println(jedis.exists("k1"));
        System.out.println(jedis.ttl("test"));
        System.out.println("---------------");

        // String
        jedis.set("StringK1", "StringV1");
        System.out.println(jedis.get("StringK1"));
        jedis.mset("StringK2", "StringV2", "StringK3", "StringV3");
        System.out.println(jedis.mget("StringK2", "StringK3"));
        System.out.println("---------------");

        // List
        jedis.lpush("ListK1", "V1", "V2", "V5", "V3");
        List<String> listK1 = jedis.lrange("ListK1", 0, -1);
        listK1.forEach(System.out::println);
        System.out.println("---------------");

        // Set
        jedis.sadd("SetK1", "K1");
        jedis.sadd("SetK1", "K2");
        jedis.sadd("SetK1", "K3");
        Set<String> setK1 = jedis.smembers("SetK1");
        setK1.forEach(System.out::println);
        jedis.srem("SetK1", "K2");
        System.out.println(jedis.smembers("SetK1").size());
        System.out.println("---------------");

        // Hash
        jedis.hset("HashK1", "username", "parzulpan");
        System.out.println(jedis.hget("HashK1", "username"));
        HashMap<String, String> map = new HashMap<>();
        map.put("age", "18");
        map.put("address", "los");
        jedis.hmset("HashK2", map);
        List<String> HashK2 = jedis.hmget("HashK2", "age", "address");
        HashK2.forEach(System.out::println);
        System.out.println("---------------");

        // ZSet
        jedis.zadd("ZSetK1", 60d, "v1");
        jedis.zadd("ZSetK1", 10d, "v2");
        jedis.zadd("ZSetK1", 90d, "v3");
        jedis.zadd("ZSetK1", 100d, "v4");
        Set<String> zSetK1 = jedis.zrange("ZSetK1", 0, -1);
        zSetK1.forEach(System.out::println);
        System.out.println("---------------");

    }
}
