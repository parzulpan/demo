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

* 创建 ssm_parent 父工程（打包方式选择pom，必须的）
* 创建 ssm_web 子模块（打包方式是war包）
* 创建 ssm_service 子模块（打包方式是jar包）
* 创建 ssm_dao 子模块（打包方式是jar包）
* 创建 ssm_domain 子模块（打包方式是jar包）
* web 依赖于 service，service 依赖于 dao，dao 依赖于 domain
* 在 ssm_parent 的 pom.xml 文件中引入坐标依赖

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
        <!-- Spring end-->

        <!-- MyBatis end-->
        <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>${mybatis.version}</version>
        </dependency>
        <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>${mysql.version}</version>
        </dependency>
        <dependency>
        <groupId>c3p0</groupId>
        <artifactId>c3p0</artifactId>
        <version>0.9.1.2</version>
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

### 配置 SpringMVC 环境

### 测试 SpringMVC 环境

## Spring 整合 MyBatis

## 练习和总结
