package cn.parzulpan.config;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : MyBatis 配置类，自定义 MyBatis 的配置规则
 */

@Configuration
public class MyBatisConfig {

    public ConfigurationCustomizer configurationCustomizer() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
                configuration.setMapUnderscoreToCamelCase(true);
            }
        };
    }
}
