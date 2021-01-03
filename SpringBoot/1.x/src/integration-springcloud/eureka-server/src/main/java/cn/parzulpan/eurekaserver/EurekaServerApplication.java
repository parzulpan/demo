package cn.parzulpan.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 注册中心
 * 1. 配置 eureka 信息
 * 2. 使用 @EnableEurekaServer 启用注册中心服务
 * 3. 启动注册中心，访问 http://localhost:8761/ 就能看到注册中心的界面
 */

@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}
