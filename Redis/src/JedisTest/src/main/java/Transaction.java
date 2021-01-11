import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;

/**
 * @author parzulpan
 * @version 1.0
 * @date 2021-01
 * @project JedisTest
 * @package PACKAGE_NAME
 * @desc 事务
 */

public class Transaction {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.56.56", 6379);

        jedis.watch("WK");
        jedis.set("WK", "haha");
        jedis.unwatch();

        redis.clients.jedis.Transaction transaction = jedis.multi();

        Response<String> response = transaction.get("WK");
        transaction.set("WK", "k1");
        transaction.get("WK");
        // 模拟“编译”异常
        transaction.incr("WK");
        // 模拟“运行”异常
        // int a = 1 / 0;

        transaction.lpush("WKList", "21");
        transaction.lpush("WKList", "10");
        transaction.lpush("WKList", "19");

        transaction.exec();
        System.out.println("response: " + response.get());

        System.out.println(jedis.get("WK"));
        System.out.println(jedis.lrange("WKList", 0, -1));
    }
}
