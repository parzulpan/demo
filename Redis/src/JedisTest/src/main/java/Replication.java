import redis.clients.jedis.Jedis;

/**
 * @author parzulpan
 * @version 1.0
 * @date 2021-01
 * @project JedisTest
 * @package PACKAGE_NAME
 * @desc 主从复制
 */

public class Replication {
    public static void main(String[] args) throws InterruptedException {
        Jedis jedisM = new Jedis("192.168.56.56", 6379);
        Jedis jedisS = new Jedis("192.168.56.56", 6380);

        jedisS.slaveof("192.168.56.56", 6379);

        jedisM.set("class", "12345");

        Thread.sleep(3000);
        String aClass = jedisS.get("class");
        System.out.println(aClass);
    }
}
