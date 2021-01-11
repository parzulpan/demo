import redis.clients.jedis.Jedis;

/**
 * @author parzulpan
 * @version 1.0
 * @date 2021-01
 * @project JedisTest
 * @package PACKAGE_NAME
 * @desc 测试连通
 */

public class Ping {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.56.56", 6379);
        System.out.println(jedis.ping());
    }
}