package cn.parzulpan.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc : 自定义健康指示器
 */

@Component
public class MyAppHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // 自定义检查方法
        //  Health.up().build() 代表健康
        // Health.down().build() 代表不健康，还可以带上信息
        return Health.down().withDetail("msg", "服务异常").build();
    }
}
