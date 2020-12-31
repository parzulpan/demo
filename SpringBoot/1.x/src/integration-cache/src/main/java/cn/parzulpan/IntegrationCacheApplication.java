package cn.parzulpan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching  // 开启基于注解的缓存
@MapperScan(basePackages = {"cn.parzulpan.mapper"})
@SpringBootApplication
public class IntegrationCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationCacheApplication.class, args);
    }

}
