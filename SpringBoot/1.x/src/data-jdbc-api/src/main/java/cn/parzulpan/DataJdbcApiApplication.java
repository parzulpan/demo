package cn.parzulpan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@MapperScan("cn.parzulpan.mapper")
@SpringBootApplication()
public class DataJdbcApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJdbcApiApplication.class, args);
    }

}
