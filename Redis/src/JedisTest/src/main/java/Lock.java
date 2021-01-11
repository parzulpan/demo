import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * @author parzulpan
 * @version 1.0
 * @date 2021-01
 * @project JedisTest
 * @package PACKAGE_NAME
 * @desc 模拟锁
 */

public class Lock {
    Jedis jedis = new Jedis("192.168.56.56", 6379);

    Lock() {
        this.jedis.set("balance", "10000");
        this.jedis.set("debt", "0");
    }

    public static void main(String[] args) {

        Lock lock = new Lock();
        try {
            boolean consume = lock.consume();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean consume() throws InterruptedException {
        int balance;
        int debt;
        int amount = 100;

        jedis.watch("balance");

        // 模拟其他程序消费
        // jedis.set("balance", "1000");

        Thread.sleep(5000);
        balance = Integer.parseInt(jedis.get("balance"));

        if (balance < amount) {
            jedis.unwatch();
            System.out.println("余额不足，消费失败！");
            return false;
        } else {
            Transaction transaction = jedis.multi();
            transaction.decrBy("balance", amount);
            transaction.incrBy("debt", amount);
            transaction.exec();
            System.out.println("消费成功！账户情况：");
            System.out.println("余额：" + Integer.parseInt(jedis.get("balance")));
            System.out.println("待还：" + Integer.parseInt(jedis.get("debt")));
            return true;

        }
    }
}
