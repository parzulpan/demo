# SpringBoot1.x 数据访问

## 简介

对于数据访问层，无论是 SQL 还是 NOSQL，Spring Boot 默认采用整合 Spring Data 的方式进行统一处理，添加大量自动配置，屏蔽了很多设置。引入各种 xxxTemplate，xxxRepository 来简化我们对数据访问层的操作，对我们来说只需要进行简单的设置即可。

在本文中测试使用 SQL 相关内容，在其他文章中测试使用 NOSQL 相关内容。

## JDBC API

通过包含以下设计决策，SpringData JDBC API 的目标是从概念上简化得多：

* 如果加载实体，则将运行SQL语句。完成此操作后，您将拥有一个完全加载的实体。不会进行延迟加载或缓存。
* 如果保存实体，则将保存它。如果您不这样做，则不会。没有肮脏的跟踪，也没有会话。
* 有一个简单的模型可以将实体映射到表。它可能仅适用于相当简单的情况。如果您不喜欢这样做，则应编写自己的策略。SpringData JDBC API 仅提供非常有限的支持，以通过注释自定义策略。

所以它不提供 JPA 的缓存，延迟加载，回写或其他许多功能。这使 SpringData JDBC API 成为简单，有限，易用的OR​​M。

[本节源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/data-jdbc-api)

新建 SpringBoot 项目，选择 Web 模块，JDBC API 和 MySQL 模块，如下图：

![数据访问 JDBC API](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_201230071016%E6%95%B0%E6%8D%AE%E8%AE%BF%E9%97%AE%20JDBC%20API.png)

或者 新建 SpringBoot 项目后，添加如下的相关依赖：

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>
```

---

然后修改 application.yml 配置文件：

```yml
# 配置数据源
spring:
  datasource:
    username: root
    password: 123456
    url: jdbc:mysql://localhost:3306/dockerT?useSSL=false
    driver-class-name: com.mysql.jdbc.Driver
```

默认是用 org.apache.tomcat.jdbc.pool.DataSource 作为数据源，数据源的相关配置都在 DataSourceProperties 里面。

**数据源自动配置原理**：

* 参考 `org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration`，它根据配置创建数据源，默认使用 Tomcat 连接池，也可以使用 `spring.datasource.type` 指定自定义的数据源类型。
* SpringBoot默认可以支持数据源为：`org.apache.tomcat.jdbc.pool.DataSource、HikariDataSource、BasicDataSource`
* 通过 `org.springframework.boot.autoconfigure.jdbc.DataSourceInitializer` 的 `runSchemaScripts()` 可以运行建表语句，`runDataScripts()` 可以运行插入数据的 sql 语句，默认只需要将文件命名为 `schema‐all.sql data-sqll.sql`，也可以通过配置文件的 `spring.datasource.schema` 和 `spring.datasource.data` 属性指定。
* 因为自动配置了JdbcTemplate，所以可以直接用它操作数据库

### 整合 Druid 数据源

添加依赖：

```xml
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.24</version>
        </dependency>
```

---

然后修改 application.yml 配置文件：

```yml
# 配置数据源
spring:
  datasource:
    # 数据源基本配置
    # ...

    # 使用 DruidDataSource 作为数据源
    type: com.alibaba.druid.pool.DruidDataSource
    # 数据源其他配置
    initialSize: 5
    minIdle: 5
    maxActive: 20
    maxWait: 60000
    timeBetweenEvictionRunsMillis: 60000
    minEvictableIdleTimeMillis: 300000
    validationQuery: SELECT 1 FROM DUAL
    testWhileIdle: true
    testOnBorrow: false
    testOnReturn: false
    poolPreparedStatements: true
    # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
    filters: stat,wall,log4j
    maxPoolPreparedStatementPerConnectionSize: 20
    useGlobalDataSourceStat: true
    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500

```

---

最后编写 Druid 数据源配置类：

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : Druid 数据源配置类
 */

@Configuration
public class DruidConfig {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid() {
        return new DruidDataSource();
    }

    // 配置Druid的监控

    // 先配置一个管理后台的 Servlet
    @Bean
    public ServletRegistrationBean statViewServlet() {
        ServletRegistrationBean srb = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String, String> initParams = new HashMap<>();
        initParams.put("loginUsername", "admin123");
        initParams.put("loginPassword", "admin123");
        initParams.put("allow", "");    //  默认允许所有访问
        initParams.put("deny", ""); // 拒绝的访问
        srb.setInitParameters(initParams);
        return srb;
    }

    // 再配置一个 web 监控的 filter
    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean frb = new FilterRegistrationBean();
        frb.setFilter(new WebStatFilter());
        Map<String,String> initParams = new HashMap<>();
        initParams.put("exclusions","*.js,*.css,/druid/*");
        frb.setInitParameters(initParams);
        frb.setUrlPatterns(Collections.singletonList("/*"));
        return frb;
    }
}
```

配置好后，可以通过访问 `/druid` 通过管理后台。

### 整合 MyBatis

添加依赖：

```xml
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.5</version>
        </dependency>
```

#### 注解方式

纯注解方式，先建表，然后编写实体类。

数据表：

```sql
use dockerT;

SET FOREIGN_KEY_CHECKS=0;

# Table structure for department
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
    `id` int(11) primary key NOT NULL AUTO_INCREMENT,
    `departmentName` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

# Table structure for employee
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
    `id` int(11) primary key NOT NULL AUTO_INCREMENT,
    `lastName` varchar(255) DEFAULT NULL,
    `email` varchar(255) DEFAULT NULL,
    `gender` int(2) DEFAULT NULL,
    `birth` date DEFAULT NULL,
    `d_id` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```

实体类：

省略...

---

编写操作 Department 表的 Mapper：

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 操作 Department 表的 Mapper
 */

@Repository
@Mapper
public interface DepartmentMapper {

    @Select("select * from department where id=#{id}")
    public Department getDeptById(Integer id);

    @Delete("delete from department where id=#{id}")
    public int deleteDeptById(Integer id);

    // 自增
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into department(departmentName)values(#{departmentName})")
    public int insertDept(Department department);

    @Update("update department set departmentName=#{departmentName} where id=#{id}")
    public int updateDept(Department department);
}
```

---

编写控制类：

```java
@RestController
public class DeptController {
    @Autowired
    DepartmentMapper departmentMapper;

    // http://localhost:8080/dept?departmentName=Admin
    @GetMapping("/dept")
    public Department addDepartment(Department department) {
        departmentMapper.insertDept(department);
        return department;
    }

    // http://localhost:8080/dept/1001
    @GetMapping("/dept/{id}")
    public Department getDepartment(@PathVariable("id") Integer id) {
        return departmentMapper.getDeptById(id);
    }
}
```

---

也可以自定义 MyBatis 的配置规则，如果解决数据表名和属性名不一致的情况，给容器中添加一个 ConfigurationCustomizer 即可。

```java
@Configuration
public class MyBatisConfig {
    
    public ConfigurationCustomizer configurationCustomizer() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
                configuration.setMapUnderscoreToCamelCase(true);
            }
        };
    }
}
```

---

如果 Mapper 接口太多，可以需要写很多个 @Mapper 注解，可以使用 @MapperScan 批量扫描所有的 Mapper 接口。

```java
@MapperScan("cn.parzulpan.mapper")
@SpringBootApplication()
public class DataJdbcApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJdbcApiApplication.class, args);
    }
}
```

#### 配置方式

编写全局配置文件：

```xml

```

编写 SQL 映射文件：

```xml

```

然后修改 application.yml 配置文件，指定文件位置：

```yml
mybatis:
  config‐location: classpath:mybatis/mybatis‐config.xml
  mapper‐locations: classpath:mybatis/mapper/*.xml
```

## SpringData JPA

为了执行简单查询以及执行分页和审核，必须编写太多样板代码。SpringData JPA 旨在通过将工作量减少到实际需要的数量来显着改善数据访问层的实现。它相比与 SpringData JDBC API 功能更加强大，使用也更复杂。

[本节源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/data-jpa)

**使用步骤**：

* 编写一个实体类和数据表进行映射，并且配置好映射关系

    ```java
    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 使用 JPA 注解配置映射关系
    */

    @Entity // 告诉 JPA 这是一个实体类，即和数据库映射的表
    @Table  // 指定和哪个数据表对应，如果没有这个表在配置中可以指定自动创建，如果省略默认表名就是 user，即类名首字母小写
    public class User {

        @Id // 这是一个主键
        @GeneratedValue(strategy = GenerationType.IDENTITY) // 策略是自增
        private Integer id;

        @Column(name = "last_name", length = 50)    // 指定和数据表对应的一个列，如果省略默认列名就是属性名
        private String lastName;

        @Column
        private String email;

        // getter setter
    }
    ```

* 编写一个接口来操作实体类对应的数据表（Repository）

    ```java
    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 操作实体类对应的数据表的接口
    * JpaRepository<T, ID extends Serializable>
    * T 是 实体类，ID 是实体类的主键
    */

    public interface UserRepository extends JpaRepository<User, Integer> {
    }
    ```

* 配置 JPA

    ```yml
    spring:
    datasource:
        username: root
        password: 123456
        url: jdbc:mysql://localhost:3306/dockerT?useSSL=false
        driver-class-name: com.mysql.jdbc.Driver
    jpa:
        hibernate:
        ddl-auto: update  # 更新或者创建数据表结构
        show-sql: true  # 控制显示相应 SQL
    ```

* 测试

    ```java
    package cn.parzulpan.controller;

    import cn.parzulpan.entity.User;
    import cn.parzulpan.repository.UserRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    public class UserController {

        @Autowired
        UserRepository userRepository;

        // http://localhost:8080/user/1
        @GetMapping("/user/{id}")
        public User getUser(@PathVariable("id") Integer id){
            User user = userRepository.findOne(id);
            return user;
        }

        // http://localhost:8080/user/?lastName=parzul&email=aaf@gmail.com
        @GetMapping("/user")
        public User insertUser(User user){
            User save = userRepository.save(user);
            return save;
        }

    }
    ```

## 练习和总结
