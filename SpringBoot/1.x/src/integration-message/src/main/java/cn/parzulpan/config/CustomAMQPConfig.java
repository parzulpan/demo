package cn.parzulpan.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc : 自定义 AMQP 配置类
 */

@Configuration
public class CustomAMQPConfig {

    /**
     * 自定义消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
