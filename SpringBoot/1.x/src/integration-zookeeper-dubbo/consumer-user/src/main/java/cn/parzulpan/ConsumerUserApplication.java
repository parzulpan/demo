package cn.parzulpan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 从注册中心取得服务给消费者
 * 1. 引入 dubbo 和 zookeeper 相关依赖
 * 2. 配置 dubbo 的注册中心地址
 * 3. 消费服务
 */

@SpringBootApplication
public class ConsumerUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerUserApplication.class, args);
	}

}
