# SSM 整合使用

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringMVC/src/SSM-Integration)

## 搭建整合环境

### 整合说明

SSM 整合可以使用多种方式，但是选择 **XML + 注解** 的方式最为合适。

### 整合思路

* 搭建整合环境
* Spring 环境搭建并测试
* Spring 整合 SpringMVC 并测试
* Spring 整合 MyBatis 并测试

### 创建 Maven 工程

使用到工程的聚合和拆分的概念。

使用 IDEA 新建一个 Maven 工程，选择 `maven-archetype-webapp`

* 在 项目 的 pom.xml 文件中引入坐标依赖

    ```xml
    <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <spring.version>5.1.20.RELEASE</spring.version>
    <mybatis.version>3.5.6</mybatis.version>
    <mysql.version>5.1.48</mysql.version>
    <junit.version>4.12</junit.version>
    <jsckson.version>2.10.5</jsckson.version>
  </properties>

  <dependencies>

    <!-- Spring start-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>${spring.version}</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-tx</artifactId>
      <version>${spring.version}</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-web</artifactId>
      <version>${spring.version}</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-webmvc</artifactId>
      <version>${spring.version}</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-jdbc</artifactId>
      <version>${spring.version}</version>
    </dependency>
    <dependency>
      <groupId>org.aspectj</groupId>
      <artifactId>aspectjweaver</artifactId>
      <version>1.8.14</version>
    </dependency>
    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>servlet-api</artifactId>
      <version>2.5</version>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>javax.servlet.jsp</groupId>
      <artifactId>jsp-api</artifactId>
      <version>2.0</version>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>jstl</groupId>
      <artifactId>jstl</artifactId>
      <version>1.2</version>
    </dependency>
    <!-- Spring end-->

    <!-- MyBatis end-->
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>${mybatis.version}</version>
    </dependency>
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis-spring</artifactId>
      <version>1.3.3</version>
    </dependency>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>${mysql.version}</version>
    </dependency>
    <dependency>
      <groupId>com.mchange</groupId>
      <artifactId>c3p0</artifactId>
      <version>0.9.5.2</version>
    </dependency>
    <dependency>
      <groupId>log4j</groupId>
      <artifactId>log4j</artifactId>
      <version>1.2.12</version>
    </dependency>
    <!-- MyBatis end-->

    <!-- Test end-->
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>${junit.version}</version>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-test</artifactId>
      <version>${spring.version}</version>
    </dependency>
    <!-- Test end-->

    <!-- Common end-->
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>${jsckson.version}</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-core</artifactId>
      <version>${jsckson.version}</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-annotations</artifactId>
      <version>${jsckson.version}</version>
    </dependency>
    <dependency>
      <groupId>commons-fileupload</groupId>
      <artifactId>commons-fileupload</artifactId>
      <version>1.3.1</version>
    </dependency>
    <dependency>
      <groupId>commons-io</groupId>
      <artifactId>commons-io</artifactId>
      <version>2.4</version>
    </dependency>
    <dependency>
      <groupId>com.sun.jersey</groupId>
      <artifactId>jersey-core</artifactId>
      <version>1.18.1</version>
    </dependency>
    <dependency>
      <groupId>com.sun.jersey</groupId>
      <artifactId>jersey-client</artifactId>
      <version>1.18.1</version>
    </dependency>
    <!-- Common end-->

  </dependencies>
    ```

选中项目，点击右键，选择 Add FrameWorks Support，添加上 SpringMVC 支持。会自动创建 `src/main/webapp/WEB-INF/applicationContext.xml` 和 `src/main/webapp/WEB-INF/dispatcher-servlet.xml` 文件，前者是 Spring 配置文件，后者是 SpringMVC 配置文件。

### 创建数据库和表结构

src/main/resources/ssm.sql

```sql
drop database if exists ssm;

create database ssm;
use ssm;

# ---

drop table if exists `user`;

create table `user`(
                       `id` int(11) primary key auto_increment,
                       `username` varchar(30) not null comment '用户名',
                       `password` varchar(30) not null comment '密码'
) engine=InnoDB default charset=utf8;

insert into user(username, password) VALUES
('admin', 'admin11002244'), ('parzulpan', '12345678');

# ----
```

### 编写实体类

src/main/java/cn/parzulpan/domain/User.java

```java
package cn.parzulpan.domain;

import java.io.Serializable;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户实体类
 */

public class User implements Serializable {
    private Integer id;
    private String username;
    private String password;

    // getter setter toString
}
```

### 编写 dao 接口和实现类

src/main/java/cn/parzulpan/dao/UserDAO.java

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户持久层接口
 */

public interface UserDAO {
    /**
     * 查询所有用户信息
     * @return
     */
    public List<User> findAll();

    /**
     * 根据 Id 查询用户信息
     * @param id
     * @return
     */
    public User findById(Integer id);

    /**
     * 根据 用户名 查询用户信息
     * @param username
     * @return
     */
    public User findByName(String username);

    /**
     * 保存用户
     * @param user
     */
    public void save(User user);
}
```

src/main/java/cn/parzulpan/dao/impl/UserDAOImpl.java

```java
// 后面整合 MyBatis 后直接使用注解开发，这个文件将弃用删除

@Repository("userDAO")
public class UserDAOImpl implements UserDAO {
    @Override
    public List<User> findAll() {
        System.out.println("用户持久层：查询所有用户信息");
        return null;
    }

    @Override
    public User findById(Integer id) {
        System.out.println("用户持久层：根据 Id 查询用户信息");
        return null;
    }

    @Override
    public User findByName(String username) {
        System.out.println("用户持久层：根据 用户名 查询用户信息");
        return null;
    }

    @Override
    public void save(User user) {
        System.out.println("用户持久层：保存用户");

    }
}
```

### 编写 service 接口和实现类

src/main/java/cn/parzulpan/service/UserService.java

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户业务层接口
 */

public interface UserService {

    /**
     * 查询所有用户信息
     * @return
     */
    public List<User> findAllUser();

    /**
     * 根据 Id 查询用户信息
     * @param id
     * @return
     */
    public User findUserById(Integer id);

    /**
     * 根据 用户名 查询用户信息
     * @param username
     * @return
     */
    public User findUserByName(String username);

    /**
     * 保存用户
     * @param user
     */
    public void saveUser(User user);
}
```

src/main/java/cn/parzulpan/service/impl/UserServiceImpl.java

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户业务层接口的实现类
 */

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public List<User> findAllUser() {
        System.out.println("用户业务层：查询所有用户信息");
        return userDAO.findAll();
    }

    @Override
    public User findUserById(Integer id) {
        System.out.println("用户业务层：根据 Id 查询用户信息");
        return userDAO.findById(id);
    }

    @Override
    public User findUserByName(String username) {
        System.out.println("用户业务层：根据 用户名 查询用户信息");
        return userDAO.findByName(username);
    }

    @Override
    public void saveUser(User user) {
        System.out.println("用户业务层：保存用户");
        userDAO.save(user);
    }
}
```

## Spring 代码的编写

### 编写 Spring 配置文件

src/main/webapp/WEB-INF/applicationContext.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/aop
       http://www.springframework.org/schema/aop/spring-aop.xsd
       http://www.springframework.org/schema/tx
       http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- Spring 配置 Start -->
    <!-- 开启注解扫描，只扫描 service 和 dao 层，忽略 web 层 -->
    <context:component-scan base-package="cn.parzulpan">
        <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>

    <!-- Spring 配置 End -->

</beans>
```

### 测试 Spring 环境

src/test/java/cn/parzulpan/service/impl/UserServiceImplTest.java

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 对 用户业务层接口的实现类 进行单元测试
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/applicationContext.xml"})
public class UserServiceImplTest {

    @Autowired
    private UserService us;

    @Test
    public void findAllUser() {
        List<User> users = us.findAllUser();
    }

    @Test
    public void findUserById() {
        User user = us.findUserById(1);
    }

    @Test
    public void findUserByName() {
        User user = us.findUserByName("admin");
    }

    @Test
    public void saveUser() {
        us.saveUser(new User(null, "test", "test1234"));
    }
}
```

## Spring 整合 Spring MVC

* 在 web.xml 中配置 DispatcherServlet 前端控制器
* 在 web.xml 中配置 DispatcherServlet 过滤器 解决中文乱码
* 在 web.xml 中配置 ContextLoaderListener 监听器 为了在controller 中能成功的调用 service 对象中的方法

    ```xml
    <!DOCTYPE web-app PUBLIC
    "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
    "http://java.sun.com/dtd/web-app_2_3.dtd" >

    <web-app>
    <display-name>Archetype Created Web Application</display-name>

        <!-- 配置Spring的监听器 Start -->
        <listener>
            <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
        </listener>
        <!-- 通过监听器，读取 Spring 的配置文件 -->
        <context-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/applicationContext.xml</param-value>
        </context-param>
        <!-- 配置Spring的监听器 End -->

        <!-- 配置 app 分派器（前端控制器）Start -->
        <servlet>
            <servlet-name>dispatcher</servlet-name>
            <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
            <!-- 配置初始化参数，读取 SpringMVC 的配置文件 -->
            <init-param>
                <param-name>contextConfigLocation</param-name>
                <param-value>/WEB-INF/dispatcher-servlet.xml</param-value>
            </init-param>
            <!-- 配置 Servlet 对象的创建时间点为应用加载时创建，取值只能为非零整数，表示启动顺序 -->
            <load-on-startup>1</load-on-startup>
        </servlet>
        <!-- 配置映射，同 Servlet 一样 -->
        <servlet-mapping>
            <servlet-name>dispatcher</servlet-name>
            <url-pattern>/</url-pattern>
        </servlet-mapping>
        <!-- 配置 app 分派器（前端控制器）End -->

        <!-- 配置解决中文乱码的过滤器 Start -->
        <filter>
            <filter-name>characterEncodingFilter</filter-name>
            <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
            <!-- 设置过滤器中的属性值 -->
            <init-param>
                <param-name>encoding</param-name>
                <param-value>UTF-8</param-value>
            </init-param>
            <init-param>
                <param-name>forceRequestEncoding</param-name>
                <param-value>true</param-value>
            </init-param>
            <init-param>
                <param-name>forceResponseEncoding</param-name>
                <param-value>true</param-value>
            </init-param>
        </filter>
        <!-- 过滤所有请求 -->
        <filter-mapping>
            <filter-name>characterEncodingFilter</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>
        <!-- 配置解决中文乱码的过滤器 End -->

    </web-app>
    ```

---

* 在 dispatcher-servlet.xml 中 配置视图解析器、静态资源以及开启注解支持

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:mvc="http://www.springframework.org/schema/mvc"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
                http://www.springframework.org/schema/beans
                http://www.springframework.org/schema/beans/spring-beans.xsd
                http://www.springframework.org/schema/mvc
                http://www.springframework.org/schema/mvc/spring-mvc.xsd
                http://www.springframework.org/schema/context
                http://www.springframework.org/schema/context/spring-context.xsd">
                
        <!-- SpringMVC 配置 Start -->

        <!-- 开启注解扫描，只扫描 web 层 -->
        <context:component-scan base-package="cn.parzulpan">
            <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
        </context:component-scan>

        <!-- 配置 视图解析器 -->
        <bean id="defaultViewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
            <property name="prefix" value="/WEB-INF/views/"/>
            <property name="suffix" value=".jsp"/>
        </bean>

        <!-- 配置 文件解析器，要求 id 名称必须是 multipartResolver -->
        <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
            <property name="maxUploadSize" value="10485760"/>
        </bean>

        <!-- 配置 处理器拦截器 -->

        <!-- 设置静态资源不过滤 -->
        <mvc:resources mapping="/js/**" location="/js/"/>
        <mvc:resources mapping="/css/**" location="/css/"/>
        <mvc:resources mapping="/images/**" location="/images/"/>

        <!-- 开启对 SpringMVC 注解的支持 -->
        <mvc:annotation-driven/>

        <!-- SpringMVC 配置 End -->

    </beans>
    ```

## Spring 整合 MyBatis

* 删除 SqlConfigMap.xml，在 applicationContext.xml 中 配置 MyBatis
* 在 applicationContext.xml 中 配置 Spring 声明式事务管理

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xmlns:tx="http://www.springframework.org/schema/tx"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd">

        <!-- Spring 配置 Start -->
        <!-- 开启注解扫描，只扫描 service 和 dao 层，忽略 web 层 -->
        <context:component-scan base-package="cn.parzulpan">
            <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
        </context:component-scan>

        <!-- 配置 Spring 声明式事务管理 -->
        <!-- 1. 配置事务管理器 -->
        <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
            <property name="dataSource" ref="dataSource"/>
        </bean>
        <!-- 2. 配置事务的通知 -->
        <tx:advice id="txAdvice" transaction-manager="transactionManager">
            <tx:attributes>
                <tx:method name="*" read-only="false" propagation="REQUIRED"/>
                <tx:method name="find*" read-only="true" propagation="SUPPORTS"/>
            </tx:attributes>
        </tx:advice>
        <!-- 3. 配置 AOP 增强 -->
        <aop:config>
            <aop:pointcut id="allServiceImplPT" expression="execution(* cn.parzulpan.service.impl.*.*(..))"/>
            <aop:advisor advice-ref="txAdvice" pointcut-ref="allServiceImplPT"/>
        </aop:config>

        <!-- Spring 配置 End -->

        <!-- MyBatis 配置 Start -->
        <!-- 配置 C3P0 连接池对象 -->
        <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
            <property name="driverClass" value="com.mysql.jdbc.Driver"/>
            <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/ssm?useSSL=false"/>
            <property name="user" value="root"/>
            <property name="password" value="root"/>
        </bean>
        <!-- 配置SqlSession的工厂 -->
        <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
            <property name="dataSource" ref="dataSource" />
        </bean>
        <!-- 配置映射信息 -->
        <bean id="mapperScanner" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
            <property name="basePackage" value="cn.parzulpan.dao"/>
        </bean>
        <!-- MyBatis 配置 Start -->

    </beans>
    ```

* 以注解的方式编写 DAO，注意 `@Param` 的使用，当 SQL 中需要多个参数的时候，MayBatis 会将参数列表中的参数封装成一个 Map 进行传递，这个过程是通过 @Param 来实现的，@Param 注解括号中的值会作为 key，value 就是参数实际的值。解析参数的时候会按照 @Param 中定义的 key 获取对应的值

    ```java
    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 用户持久层接口
    */

    @Repository
    public interface UserDAO {
        /**
        * 查询所有用户信息
        * @return
        */
        @Select("select * from user")
        public List<User> findAll();

        /**
        * 根据 Id 查询用户信息
        * @param id
        * @return
        */
        @Select("select * from user where id = #{id}")
        public User findById(@Param("id") Integer id);

        /**
        * 根据 用户名 查询用户信息
        * @param username
        * @return
        */
        @Select("select * from user where username = #{username}")
        public User findByName(@Param("username") String username);

        /**
        * 根据用户名和密码查询用户信息
        * @param username
        * @param password
        * @return
        */
        @Select("select * from user where username = #{username} and password = #{password}")
        public User findByNameAndPwd(@Param("username") String username, @Param("password") String password);

        /**
        * 保存用户
        * @param user
        * @return
        */
        @Insert("insert into user(username, password) values (#{username}, #{password})")
        @SelectKey(keyColumn = "id", keyProperty = "id", resultType = Integer.class, before = false,
                statement = {"select last_insert_id()"})
        public int save(User user);
    }
    ```

## 整合测试

src/main/java/cn/parzulpan/web/UserController.java

```java
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param user
     * @return
     */
    @RequestMapping("/login")
    public String login(User user){
        System.out.println("表现层：登录用户");
        System.out.println(user);

        User userByNameAndPwd = userService.findUserByNameAndPwd(user.getUsername(), user.getPassword());
        if (userByNameAndPwd != null) {
            System.out.println("登录成功");
            return "redirect:findAll";
        }

        return "error";
    }

    /**
     * 注册
     * @param user
     * @return
     */
    @RequestMapping("/registration")
    public String registration(User user){
        System.out.println("表现层：注册用户");
        System.out.println(user);

        int i = userService.saveUser(user);
        if (i > 0) {
            System.out.println("注册成功");
            return "redirect:findAll";
        }

        return "error";
    }

    /**
     * 查询所有账户
     * @param model
     * @return
     */
    @RequestMapping("/findAll")
    public String findAll(Model model) {
        System.out.println("表现层：查询所有账户");

        List<User> users = userService.findAllUser();
        model.addAttribute("users", users);

        return "success";
    }


    /**
     * 返回首页
     * @return
     */
    @RequestMapping("/returnIndex")
    public String returnIndex() {
        System.out.println("表现层：返回首页");

        return "redirect:/index.jsp";
    }
}
```

## 练习和总结
