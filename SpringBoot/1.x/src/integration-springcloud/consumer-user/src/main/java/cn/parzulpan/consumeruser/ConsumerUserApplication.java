package cn.parzulpan.consumeruser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 从 eureka 注册中心取得服务给消费者
 * 1. 配置 eureka 信息
 * 2. 使用 @EnableDiscoveryClient 启用发现服务功能
 */

@EnableDiscoveryClient	// 启用发现服务功能
@SpringBootApplication
public class ConsumerUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerUserApplication.class, args);
	}

	@LoadBalanced	// 启用负载均衡功能
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
