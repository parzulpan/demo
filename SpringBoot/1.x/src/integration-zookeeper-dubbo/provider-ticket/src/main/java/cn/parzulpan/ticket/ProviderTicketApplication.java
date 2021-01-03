package cn.parzulpan.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 将服务提供者注册到注册中心
 * 1. 引入 dubbo 和 zookeeper 相关依赖
 * 2. 配置 dubbo 的注册中心地址和扫描包
 * 3. 使用 @com.alibaba.dubbo.config.annotation.Service，将服务发布出去
 */

@SpringBootApplication
public class ProviderTicketApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderTicketApplication.class, args);
    }

}
