package cn.parzulpan;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class IntegrationMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationMessageApplication.class, args);
    }

}
