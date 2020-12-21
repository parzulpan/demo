package cn.parzulpan.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 连接数据库的配置类，代替 配置 QueryRunner 和 配置 数据源，并且导入数据库的配置文件
 *
 *     <!-- 配置 QueryRunner -->
 *     <bean id="runner" class="org.apache.commons.dbutils.QueryRunner" scope="prototype">
 *         <!-- 注入数据源，构造函数形式-->
 *         <constructor-arg name="ds" ref="dataSource"/>
 *     </bean>
 *
 *     <!-- 配置 数据源 -->
 *     <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
 *         <property name="driverClass" value="com.mysql.jdbc.Driver"/>
 *         <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/springT?useSSL=false"/>
 *         <property name="user" value="root"/>
 *         <property name="password" value="root"/>
 *     </bean>
 *
 */

@Configuration
@PropertySource("classpath:jdbc.properties")
public class JdbcConfig {
    @Value("${jdbc.driver}")
    private String driverClass;

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${jdbc.username}")
    private String user;

    @Value("${jdbc.password}")
    private String password;

    /**
     * 创建 QueryRunner，使用 数据源 1
     * @param dataSource
     * @return
     */
    @Bean(name = "runner")
    @Scope("prototype")
    public QueryRunner createQueryRunner(@Qualifier("dataSource1") DataSource dataSource) {
        return new QueryRunner(dataSource);
    }

    /**
     * 创建 数据源 1
     * @return
     */
    @Bean(name = "dataSource1")
    public DataSource createDataSource1() {
        try {
            ComboPooledDataSource ds = new ComboPooledDataSource();
            ds.setDriverClass(driverClass);
            ds.setJdbcUrl(jdbcUrl);
            ds.setUser(user);
            ds.setPassword(password);
            return ds;
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建 数据源 2
     * @return
     */
    @Bean(name = "dataSource2")
    public DataSource createDataSource2() {
        try {
            ComboPooledDataSource ds2 = new ComboPooledDataSource();
            ds2.setDriverClass(driverClass);
            ds2.setJdbcUrl(jdbcUrl);
            ds2.setUser(user);
            ds2.setPassword(password);
            return ds2;
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }
}
