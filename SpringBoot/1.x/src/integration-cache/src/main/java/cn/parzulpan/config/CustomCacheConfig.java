package cn.parzulpan.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc : 自定义缓存配置类
 */

@Configuration
public class CustomCacheConfig {

    // 自定义生成策略
    @Bean("customKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                return method.getName() + "[" + Arrays.asList(params).toString() + "]";
            }
        };
    }
}
