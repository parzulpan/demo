# MyBatis 连接池和事务控制

[文章源码](https://github.com/parzulpan/demo/tree/main/MyBatis/src/MyBatisJNDI)

## MyBaits 连接池

实际开发中都会使用连接池，因为它可以减少获取连接所消耗的时间。[具体可查看](https://www.cnblogs.com/parzulpan/p/14129981.html)

MyBatis 数据源配置在 SqlMapConfig.xml 中的 dataSource 标签，type 属性就是表示采用何种方式，它提供了三种方式的配置：

* **POOLED** 采用传统的 `javax.sql.DataSource` 接口中的连接池，MyBatis中有针对此接口的实现
* **UNPOOLED** 采用传统的获取连接的方式，虽然也实现 `javax.sql.DataSource` 接口，但是并没有使用池的思想
* **JNDI** 采用服务器提供的 JNDI 技术实现，来获取 DataSource 对象，不同的服务器所能拿到 DataSource 是不一样的。**值得注意的是**，如果不是 web 或者 maven 的 war 工程，是不能使用的。例如 Tomcat 服务器采用连接池就是DBCP。

### POOLED 分析

连接池就是用于存储连接的一个容器，即一个集合对象，该集合必须是线程安全的，不能两个线程拿到同一个连接。而且，该集合还必须实现队列先进先出的特性。

运行测试程序，得到：

![POOLED](https://images.cnblogs.com/cnblogs_com/parzulpan/1900685/o_201216103848POOLED.png)

可以看到，先得到了一个连接，然后使用完成后，又将连接放回了连接池中。

**源码分析**：

![POOLED分析](https://images.cnblogs.com/cnblogs_com/parzulpan/1900685/o_201216110309POOLED%E5%88%86%E6%9E%90.png)

由上图可以看到，一共有两个连接池，即 idleConnections 空闲池 和 activeConnections 活动池。

* 第一步：如果空闲池还有连接的话，直接从中取出一个拿出来使用，否则
* 第二步：查看活动池是否达到了最大数量，如果没有，则创建一个连接放入活动池，否则
* 第三步：从活动池取出一个最老的连接出来

### UNPOOLED 分析

运行测试程序，得到：

![UNPOOLED](https://images.cnblogs.com/cnblogs_com/parzulpan/1900685/o_201216103858UNPOOLED.png)

可以看到，并没有连接池的概念。

### JNDI 分析

JNDI（Java Nameing and Directory），即 Java 命名和目录接口，它提供了一个接口让用户在不知道资源所在位置的情形下，取得该资源服务。

使用步骤：

* 建立 Maven 工程，选择 archetype-webapp
* 手动添加 java resources test 等文件夹
* 在 webapp 目录下添加 META-INF.context.xml
  
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <Context>
    <!-- 
    <Resource 
    name="jdbc/mybatisT"						    数据源的名称
    type="javax.sql.DataSource"						数据源类型
    auth="Container"								数据源提供者
    maxActive="20"									最大活动数
    maxWait="10000"									最大等待时间
    maxIdle="5"										最大空闲数
    username="root"									用户名
    password="root"									密码
    driverClassName="com.mysql.jdbc.Driver"			驱动类
    url="jdbc:mysql://localhost:3306/mybatisT?useSSL=false"	连接url字符串
    />
    -->
    <Resource 
    name="jdbc/mybatisT"
    type="javax.sql.DataSource"
    auth="Container"
    maxActive="20"
    maxWait="10000"
    maxIdle="5"
    username="root"
    password="root"
    driverClassName="com.mysql.jdbc.Driver"
    url="jdbc:mysql://localhost:3306/mybatisT?useSSL=false"
    />
    </Context>
    ```

* 配置 SqlMapConfig.xml

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE configuration
            PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-config.dtd">

    <configuration>
        <typeAliases>
            <package name="cn.parzulpan.domain"/>
        </typeAliases>
        <environments default="mysql">
            <environment id="mysql">
                <!-- 配置事务控制的方式 -->
                <transactionManager type="JDBC"/>
                <!-- 配置连接数据库的必备信息  type属性表示是否使用数据源（连接池）-->
                <dataSource type="JNDI">
                    <property name="data_source" value="java:comp/env/jdbc/mybatisT"/>
                </dataSource>
            </environment>
        </environments>

        <!-- 指定mapper配置文件的位置 -->
        <mappers>
            <package name="cn.parzulpan.dao"/>
        </mappers>
    </configuration>
    ```

* 编辑 index.jsp，部署 Tomcat

    ```jsp
    <%@ page import="java.io.InputStream" %>
    <%@ page import="org.apache.ibatis.io.Resources" %>
    <%@ page import="org.apache.ibatis.session.SqlSessionFactoryBuilder" %>
    <%@ page import="org.apache.ibatis.session.SqlSessionFactory" %>
    <%@ page import="org.apache.ibatis.session.SqlSession" %>
    <%@ page import="cn.parzulpan.dao.UserDAO" %>
    <%@ page import="cn.parzulpan.domain.User" %>
    <%@ page import="java.util.List" %>
    <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <html>
    <body>
    <h2>Hello World!</h2>
    <%
        // 1. 读取配置文件
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
        // 2. 创建 SqlSessionFactory 的构建者对象
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        // 3. 使用构建者创建工厂对象 SqlSessionFactory
        SqlSessionFactory factory = builder.build(is);
        // 4. 使用 SqlSessionFactory 生产 SqlSession 对象
        SqlSession sqlSession = factory.openSession();
        // 5. 使用 SqlSession 对象 创建 DAO 接口的的代理对象
        UserDAO userDAO = sqlSession.getMapper(UserDAO.class);
        // 6. 使用代理对象执行方法
        List<User> users = userDAO.findAll();
        for (User user : users) {
            System.out.println(user);
        }
        // 7. 释放资源
        sqlSession.close();
        is.close();
    %>
    </body>
    </html>
    ```

## MyBatis 事务控制

在 JDBC 中我们可以通过手动方式将事务的提交改为手动方式，通过 setAutoCommit() 方法就可以调整。

MyBatis 框架因为是对 JDBC 的封装，所以 Mybatis 框架的事务控制方式，本身也是用 JDBC 的 setAutoCommit() 方法来设置事务提交方式的。

```java
public class Test {
    private InputStream is;
    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    private SqlSessionFactory factory;
    private SqlSession session;
    private UserDAO userDAO;

    @Before
    public void init() throws IOException {
        is = Resources.getResourceAsStream("SqlMapConfig.xml");
        factory = builder.build(is);
        session = factory.openSession();
        userDAO = session.getMapper(UserDAO.class);
    }

    @After
    public void destroy() throws IOException {
        session.commit();   // 事务提交
        session.close();
        is.close();
    }
}
```

其实也可以采取非手动方式，openSession() 有一个可以设置是否自动提交的方法 `SqlSession openSession(boolean autoCommit);` 。

```java
public class MyBatisCRUDTest {
    private InputStream is;
    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    private SqlSessionFactory factory;
    private SqlSession session;
    private UserDAO userDAO;

    @Before
    public void init() throws IOException {
        is = Resources.getResourceAsStream("SqlMapConfig.xml");
        factory = builder.build(is);
        session = factory.openSession(true);
        userDAO = session.getMapper(UserDAO.class);
    }

    @After
    public void destroy() throws IOException {
//        session.commit();   // 事务提交
        session.close();
        is.close();
    }
}
```

但就编程而言，设置为自动提交方式为 false 再根据情况决定是否进行提交，这种方式更常用。因为可以根据业务情况来决定提交是否进行提交。

## 练习和总结

---

**什么是事务？**

**事务的四大特性？**

**不考虑隔离性会产生的问题？**

**四种隔离级别？**

[参考数据库事务](https://www.cnblogs.com/parzulpan/p/14129976.html)

---
