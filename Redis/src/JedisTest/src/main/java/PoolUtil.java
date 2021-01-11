import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author parzulpan
 * @version 1.0
 * @date 2021-01
 * @project JedisTest
 * @package PACKAGE_NAME
 * @desc JedisPoolUtil
 * 1. 获取 Jedis 实例需要从 JedisPool 中获取
 * 2. 用完 Jedis 实例需要返还给 JedisPool
 * 3. 如果 Jedis 在使用过程中出错，则也需要还给 JedisPool
 */
public class PoolUtil {
    private static volatile JedisPool jedisPool = null;

    private PoolUtil() {

    }

    public static JedisPool getJedisPoolInstance() {
        if (null == jedisPool) {
            synchronized (PoolUtil.class) {
                if (null == jedisPool) {
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    jedisPoolConfig.setMaxIdle(32);
                    jedisPoolConfig.setMaxWaitMillis(100 * 1000);
                    jedisPoolConfig.setTestOnBorrow(true);

                    jedisPool = new JedisPool(jedisPoolConfig, "192.168.56.56", 6379);
                }
            }
        }
        return jedisPool;
    }
}
