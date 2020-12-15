# MyBatis 入门

[文章源码](https://github.com/parzulpan/demo/MyBatis/src/mybatisBase)

## 软件框架

软件框架伴随着软件工程的发展而出现，所谓的软件框架，**是提取了特定领域的软件的共性部分所形成的软件体系**，它并不是一个成熟的软件，更像是一个半成品。开发者在框架之上，可以进行既可靠又快速的**二次开发**。

## 三层架构

简单来说，包含：

* **表现层**：是用于展示数据的
* **业务层**：是处理业务需求
* **数据访问层**：是和数据库交互的

**分层开发的常见框架**：

* 表现层：Spring MVC
* 业务层：根据需求而定的业务代码
* 数据访问层：MyBatis、Spring Data

![MVC逻辑图](https://images.cnblogs.com/cnblogs_com/parzulpan/1900685/o_201215033835MVC%E9%80%BB%E8%BE%91%E5%9B%BE.png)

**持久层技术解决方案**：

* JDBC 技术：
  * Connection
  * PreparedStatement
  * ResultSet
* Spring JdbcTemplate：
  * Spring 中对 JDBC 的简单封装
* Apache DBUtils：
  * 它和 JdbcTemplate 很像，也是对 JDBC 的简单封装

注意：以上这些都不是框架，JDBC 是规范，Spring JdbcTemplate和Apache DBUtils 都只是工具类。

## MyBatis 概述

MyBatis 是一个优秀的基于 Java 的**持久层框架**，它内部封装了 JDBC，使开发者只需要关注 SQL 语句本身，而不需要花费精力去处理加载驱动、创建连接、创建 Statement 等繁杂的过程。

MyBatis 通过 **xml 文件配置** 或 **注解的方式** 将要执行的各种 Statement 配置起来，并通过 Java 对象和 Statement 中
SQL 的动态参数进行映射生成最终执行的 SQL 语句，最后由 MyBatis 框架执行 SQL 并将结果映射为 Java 对象并返回。

**ORM**（Object Relational Mappging），即对象关系映射。简单的说，就是把数据库表和实体类及实体类的属性对应起来，操作实体类就实现操作数据库表。

## MyBatis 环境搭建

### 第一步：创建 Maven 工程

创建 mybatisBase Maven 工程，工程信息为：

pom.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>cn.parzulpan</groupId>
    <artifactId>mybatisBase</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
</project>
```

导入相关坐标：

pom.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>cn.parzulpan</groupId>
    <artifactId>mybatisBase</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.6</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.48</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.12</version>
        </dependency>
    </dependencies>

</project>
```

### 第二步：创建实体类和持久层接口

```java
package cn.parzulpan.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author : parzulpan
 * @Time : 2020-12-15
 * @Desc :
 */

public class User implements Serializable {
    private static final long serialVersionUID = 15683903250463743L;
    private Integer id;
    private String username;
    private Date birthday;
    private String sex;
    private String address;

    public User() {
    }

    public User(Integer id, String username, Date birthday, String sex, String address) {
        this.id = id;
        this.username = username;
        this.birthday = birthday;
        this.sex = sex;
        this.address = address;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", birthday=" + birthday +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
```

```java
package cn.parzulpan.dao;

import cn.parzulpan.domain.User;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-15
 * @Desc :
 */

public interface UserDAO {

    /**
     * 查询所有信息
     * @return
     */
    List<User> findAll();
}

```

### 第三步：编写持久层接口的映射文件

UserDAO.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!--持久层接口的映射文件-->
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <select id="findAll" resultType="cn.parzulpan.domain.User">
        select * from user;
    </select>
</mapper>
```

### 第四步：编写 Mybatis 的主配置文件

SqlMapConfig.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<!--Mybatis 的主配置文件-->
<configuration>
    <!--配置 MyBatis 环境-->
    <environments default="mysql">
        <!--配置 MySQL 环境-->
        <environment id="mysql">
            <!--配置事务的类型-->
            <transactionManager type="JDBC"/>
            <!--配置连接数据库的信息，用的是数据源（连接池）-->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatisT?useSSL=false"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
    </environments>

    <!--告知 MyBatis 映射配置的位置-->
    <mappers>
        <mapper resource="cn/parzulpan/dao/UserDAO.xml"/>
    </mappers>
</configuration>
```

### 环境搭建的注意事项

1. 创建 UserDAO.xml 和 UserDAO.java时名称是为了和我们之前的知识保持一致。在 MyBatis 中它把持久层的操作接口名称和映射文件也叫做：Mapper，所以 UserDAO 和 UserMapper是一样的。
2. 在 IDEA 中创建目录的时候，它和包是不一样的。包在创建时：cn.parzulpan.dao它是三级结构，目录在创建时：cn.parzulpan.dao是一级目录。
3. MyBatis 的映射配置文件位置必须和 DAO 接口的包结构相同。
4. 映射配置文件的 mapper 标签 namespace 属性的取值必须是 DAO 接口的全限定类名。
5. 映射配置文件的操作配置（select），id 属性的取值必须是 DAO 接口的方法名

当遵从了第三，四，五点之后，在开发中就无须再写 DAO 的实现类。

### 编写测试类

先添加 log4j 日志文件。

log4j.properties：

```properties
# Set root category priority to INFO and its only appender to CONSOLE.
#log4j.rootCategory=INFO, CONSOLE   debug   info   warn   error   fatal
log4j.rootCategory=debug, CONSOLE, LOGFILE

# Set the enterprise logger category to FATAL and its only appender to CONSOLE.
log4j.logger.org.apache.axis.enterprise=FATAL, CONSOLE

# CONSOLE is set to be a ConsoleAppender using a PatternLayout.
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern=%d{ISO8601} %-6r [%15.15t] %-5p %30.30c %x - %m

# LOGFILE is set to be a File appender using a PatternLayout.
log4j.appender.LOGFILE=org.apache.log4j.FileAppender
log4j.appender.LOGFILE.File=/Users/parzulpan/Personal/Log/mybatis-log4j.log
log4j.appender.LOGFILE.Append=true
log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
log4j.appender.LOGFILE.layout.ConversionPattern=%d{ISO8601} %-6r [%15.15t] %-5p %30.30c %x - %m
```

再测试 MyBatis 环境。

**操作步骤**：

* 读取配置文件
* 创建 SqlSessionFactory 的构建者对象
* 使用构建者创建工厂对象 SqlSessionFactory
* 使用 SqlSessionFactory 生产 SqlSession 对象
* 使用 SqlSession 对象 创建 DAO 接口的的代理对象
* 使用代理对象执行方法
* 释放资源

```java
package cn.parzulpan.test;

import cn.parzulpan.dao.UserDAO;
import cn.parzulpan.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-15
 * @Desc :
 */

public class MyBatisTest {
    public static void main(String[] args) throws IOException {
        // 1. 读取配置文件
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        // 2. 创建 SqlSessionFactory 的构建者对象
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        // 3. 使用构建者创建工厂对象 SqlSessionFactory
        SqlSessionFactory factory = builder.build(is);
        // 4. 使用 SqlSessionFactory 生产 SqlSession 对象
        SqlSession session = factory.openSession();
        // 5. 使用 SqlSession 对象 创建 DAO 接口的的代理对象
        UserDAO userDAO = session.getMapper(UserDAO.class);
        // 6. 使用代理对象执行方法
        List<User> users = userDAO.findAll();
        users.forEach(System.out::println);

        // 7. 释放资源
        session.close();
        is.close();
    }
}
```

## 基于注解的 MyBatis 使用

### 在持久层接口中添加注解

```java
package cn.parzulpan.dao;

import cn.parzulpan.domain.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-15
 * @Desc : 基于注解的
 */

public interface UserDAOA {
    /**
     * 查询所有信息
     * @return
     */
    @Select("select * from user")
    List<User> findAll();
}
```

### 修改 Mybatis 的主配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<!--Mybatis 的主配置文件-->
<configuration>
    <!--配置 MyBatis 环境-->
    <environments default="mysql">
        <!--配置 MySQL 环境-->
        <environment id="mysql">
            <!--配置事务的类型-->
            <transactionManager type="JDBC"/>
            <!--配置连接数据库的信息，用的是数据源（连接池）-->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatisT?useSSL=false"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
    </environments>

    <!--告知 MyBatis 映射配置的位置-->
    <mappers>
        <!--使用 xml-->
        <mapper resource="cn/parzulpan/dao/UserDAO.xml"/>
        <!--使用 注解-->
        <!--指定被注解的 DAO 全限定类名-->
        <mapper class="cn.parzulpan.dao.UserDAOA"/>
    </mappers>
</configuration>
```

### 编写基于注解的测试类

```java
package cn.parzulpan.test;

import cn.parzulpan.dao.UserDAO;
import cn.parzulpan.dao.UserDAOA;
import cn.parzulpan.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-15
 * @Desc : 基于注解的
 */

public class MyBatisATest {
    public static void main(String[] args) throws IOException {
        // 1. 读取配置文件
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        // 2. 创建 SqlSessionFactory 的构建者对象
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        // 3. 使用构建者创建工厂对象 SqlSessionFactory
        SqlSessionFactory factory = builder.build(is);
        // 4. 使用 SqlSessionFactory 生产 SqlSession 对象
        SqlSession session = factory.openSession();
        // 5. 使用 SqlSession 对象 创建 DAO 接口的的代理对象
        UserDAOA userDAOA = session.getMapper(UserDAOA.class);
        // 6. 使用代理对象执行方法
        List<User> users = userDAOA.findAll();
        users.forEach(System.out::println);

        // 7. 释放资源
        session.close();
        is.close();
    }
}
```

**注意**：

* 在使用基于注解的 MyBatis 配置时，不需要编写持久层接口的映射文件；
* 在实际开发中，都是越简便越好，不管使用 XML 还是注解配置，都是采用不写 DAO 实现类的方式。但是 MyBatis 它是支持写 DAO 实现类的。

## 总结和练习
