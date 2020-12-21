package cn.parzulpan.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : Spring 的配置类，作用和 bean.xml 相同，并且导入连接数据库的配置类
 *
 *     <!-- 告知 Spring 在创建容器时要扫描的包 -->
 *     <context:component-scan base-package="cn.parzulpan"/>
 *
 */

@Configuration
@ComponentScan("cn.parzulpan")
@Import(JdbcConfig.class)
public class SpringConfiguration {
}
