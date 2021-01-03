package cn.parzulpan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling   // 开启定时任务注解支持
@EnableAsync    // 开启异步注解支持
@SpringBootApplication
public class IntegrationTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationTaskApplication.class, args);
    }

}
