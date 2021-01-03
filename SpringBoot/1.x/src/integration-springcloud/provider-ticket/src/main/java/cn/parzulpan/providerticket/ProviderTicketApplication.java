package cn.parzulpan.providerticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 将服务提供者注册到 eureka 注册中心
 * 1. 配置 eureka 信息
 * 2. 可以观察到注册中心的界面，运行的示例。同一个应用也可以注册多次。
 *   2.1 分别使用 8801 和 8002 端口，然后 打包启动这两个应用
 */


@SpringBootApplication
public class ProviderTicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProviderTicketApplication.class, args);
	}

}
