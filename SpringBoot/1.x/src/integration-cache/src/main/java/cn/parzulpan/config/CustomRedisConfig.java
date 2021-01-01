package cn.parzulpan.config;

import cn.parzulpan.bean.Department;
import cn.parzulpan.bean.Employee;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc : 自定义 Redis 配置类
 */

@Configuration
public class CustomRedisConfig {

    // 使用 Jackson 序列化器，不使用默认的 JDK 的
    @Bean
    public RedisTemplate<Object, Employee> employeeRedisTemplate(RedisConnectionFactory rcf){
        RedisTemplate<Object, Employee> template = new RedisTemplate<>();
        template.setConnectionFactory(rcf);
        Jackson2JsonRedisSerializer<Employee> jrs = new Jackson2JsonRedisSerializer<>(Employee.class);
        template.setDefaultSerializer(jrs);
        return template;
    }

    @Bean
    public RedisTemplate<Object, Department> departmentRedisTemplate(RedisConnectionFactory rcf){
        RedisTemplate<Object, Department> template = new RedisTemplate<>();
        template.setConnectionFactory(rcf);
        Jackson2JsonRedisSerializer<Department> jrs = new Jackson2JsonRedisSerializer<>(Department.class);
        template.setDefaultSerializer(jrs);
        return template;
    }

    // 自定义缓存管理器
    @Primary    // 将其作为默认的
    @Bean
    public RedisCacheManager employeeCacheManager(RedisTemplate<Object, Employee> employeeRedisTemplate) {
        RedisCacheManager redisCacheManager = new RedisCacheManager(employeeRedisTemplate);

        // 使用前缀，默认将 CacheName 作为 key 的前缀
        redisCacheManager.setUsePrefix(true);

        return redisCacheManager;
    }

    @Bean
    public RedisCacheManager departmentCacheManager(RedisTemplate<Object, Department> departmentRedisTemplate) {
        RedisCacheManager redisCacheManager = new RedisCacheManager(departmentRedisTemplate);

        // 使用前缀，默认将 CacheName 作为 key 的前缀
        redisCacheManager.setUsePrefix(true);

        return redisCacheManager;
    }
}
