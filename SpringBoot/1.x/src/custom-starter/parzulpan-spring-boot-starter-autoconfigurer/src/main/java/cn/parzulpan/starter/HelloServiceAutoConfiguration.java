package cn.parzulpan.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : HelloService 自动配置类
 */

@Configuration
@ConditionalOnWebApplication    // web app 才有效
@EnableConfigurationProperties(HelloServiceProperties.class)    // 让 HelloServiceProperties 生效并加入到容器中
public class HelloServiceAutoConfiguration {

    @Autowired
    HelloServiceProperties helloServiceProperties;

    @Bean
    public HelloService helloService() {
        HelloService helloService = new HelloService();
        helloService.setHelloServiceProperties(helloServiceProperties);
        return helloService;
    }
}
