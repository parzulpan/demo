# 自定义 MyBatis

[文章源码](https://github.com/parzulpan/demo/tree/main/MyBatis/src/customMyBatis)

## 执行查询信息的分析

我们知道，MyBatis 在使用代理 DAO 的方式实现增删改查时只做两件事：

* 创建代理对象
* 在代理对象中调用 `selectList()`

**配置信息 1**：连接数据库的信息，有了它们就能创建 Connection 对象

    ```xml
    <dataSource type="POOLED">
        <property name="driver" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/mybatisT?useSSL=false"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </dataSource>
    ```

**配置信息 2**：有了它们就有了映射配置信息

    ```xml
    <mappers>
        <!--使用 xml-->
        <mapper resource="cn/parzulpan/dao/UserDAO.xml"/>
        <!--使用 注解-->
        <!--指定被注解的 DAO 全限定类名-->
        <mapper class="cn.parzulpan.dao.UserDAOA"/>
    </mappers>
    ```

**配置信息 3**：有了它们就有了要执行 SQL 语句，即能获取 PreparedStatement 对象，并且还指定了封装的实体类全限定类名

    ```xml
    <!--持久层接口的映射文件-->
    <mapper namespace="cn.parzulpan.dao.UserDAO">
        <select id="findAll" resultType="cn.parzulpan.domain.User">
            select * from user;
        </select>
    </mapper>
    ```

对于上面三个配置信息，需要读取配置文件，用到的就是解析 XML 的技术，这里选用 `dom4j`。

有了上面的准备，现在可以准备 **第二件事** **`selectList()`**：

* 根据配置文件信息创建 Connection 对象
  * 注册驱动，获取连接等
* 获取预处理对象 PrepareStatement
  * 执行 `connection.prepareStatement(sql)`，此时需要 SQL 语句，可以由**配置信息 3** 得到
* 执行查询
  * 执行 `ResultSet rs = prepareStatement.executeQuery()`
* 遍历结果用于封装

    ```java
    ArrayList<T> list = new ArrayList<>();
    while (resultSet.next()) {
        T t = (T)Class.forName(配置信息 3 的实体类全限定类名).newInstance();
        // 使用反射封装 
        list.add(t);
    }
    ```

* 返回 list
  * `return list;`

要想让 **`selectList()`** 执行，需要给方法提供两个信息

* 连接信息
* 映射信息，包括 执行的 SQL 语句 和 封装结果的实体类全限定类名，可以把这两个信息组合起来定义成一个 **Mapper 对象**

这个对象可以用用一个 Map 存储起来：

* **key** 是一个 String，值为 `cn.parzulpan.dao.UserDAO.findAll`
* **value** 即这个 Mapper 对象，属性有 `String sql` 和 `String domainClassPath`

现在需要准备 **第一件事** **创建代理对象**：

```java
// 5. 使用 SqlSession 对象 创建 DAO 接口的的代理对象
UserDAO userDAO = session.getMapper(UserDAO.class);
```

```java
// 根据 DAO 接口的字节码创建 DAO 的代理对象
public <T> getMapper(Class<T> DAOInterfaceClass) {

    /**
    loader，类加载器，它使用和被代理类相同的类加载，即  DAOInterfaceClass.getClass().getClassLoader()

    interfaces，代理对象要实现的接口字节码数组，它使用和被代理类相同的接口，即 DAOInterfaceClass.getClass().getInterfaces()

    handler，如何代理，它需要自己实现，写一个实现接口 InvocationHandler 的类，类中调用第二件事 selectList()
    */

    Proxy.newProxyInstance(ClassLoader loader, Class[] interfaces, InvocationHandler handler);
}
```

## 自定义实现

万变不离其宗，看别人是如何实现的？

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
        // 使用类加载器，它只能读取类路径的配置文件
        // 使用 ServletContext 对象的 getRealPath()
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");

        // 2. 创建 SqlSessionFactory 的构建者对象
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        // 3. 使用构建者创建工厂对象 SqlSessionFactory
        // 创建工厂对象 使用了建造者模式
        // 优势：把对象的创建细节隐藏，使用者直接调用方法即可拿到对象
        SqlSessionFactory factory = builder.build(is);

        // 4. 使用 SqlSessionFactory 生产 SqlSession 对象
        // 生产 SqlSession 对象 使用了工厂模式
        // 优势：解藕，降低了类之间的依赖关系
        SqlSession session = factory.openSession();

        // 5. 使用 SqlSession 对象 创建 DAO 接口的的代理对象
        // 创建 DAO 接口的代理对象 使用了代理模式
        // 优势：在不修改源码的基础上对已有方法增强
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

通过 上面的示例代码 和 [MyBatis 入门](https://www.cnblogs.com/parzulpan/p/14138757.html) 我们知道，需要实现以下类和接口：

* class Resources
* class SqlSessionFactoryBuilder
* interface SqlSessionFactory
* interface SqlSession

### 引入工具类

* [XMLConfigBuilder.java](https://github.com/parzulpan/demo/tree/main/MyBatis/src/customMyBatis/src/main/java/cn/parzulpan/mybatis/utils/XMLConfigBuilder.java) 用于解析配置文件
* [Executor.java](https://github.com/parzulpan/demo/tree/main/MyBatis/src/customMyBatis/src/main/java/cn/parzulpan/mybatis/utils/Executor.java) 负责执行SQL语句，并且封装结果集
* [DataSourceUtil.java](https://github.com/parzulpan/demo/tree/main/MyBatis/src/customMyBatis/src/main/java/cn/parzulpan/mybatis/utils/DataSourceUtil.java) 用于创建数据源的

### 编写 主配置文件

SqlMapConfig.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>

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

**注意**：由于没有使用 MyBatis 的 jar 包，所以要把配置文件的约束删掉，否则会报错。

### 编写 读取配置文件类

```java
package cn.parzulpan.mybatis.io;

import java.io.InputStream;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 使用类加载器读取配置文件
 */

public class Resources {

    /**
     * 用于加载 xml 文件，并且得到一个流对象
     * @param xmlPath xml 文件路径
     * @return 流对象
     */
    public static InputStream getResourceAsStream(String xmlPath) {
        return Resources.class.getClassLoader().getResourceAsStream(xmlPath);
    }
}

```

### 编写 Mapper 类

```java
package cn.parzulpan.mybatis.cfg;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用于封装查询时的必要信息，包括 执行的 SQL 语句 和 封装结果的实体类全限定类名
 */

public class Mapper {
    private String queryString; // sql 语句
    private String resultType;  // 结果的实体类全限定类名

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
```

### 编写 Configuration 配置类

```java
package cn.parzulpan.mybatis.cfg;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 核心配置类，包含数据库信息、sql 的 map 集合
 */

public class Configuration {
    private String username;    //用户名
    private String password;    //密码
    private String url; //地址
    private String driver;  //驱动

    /**
     要想让 **`selectList()`** 执行，需要给方法提供两个信息

     * 连接信息
     * 映射信息，包括 执行的 SQL 语句 和 封装结果的实体类全限定类名，可以把这两个信息组合起来定义成一个 **Mapper 对象**

     这个对象可以用用一个 Map 存储起来：

     * **key** 是一个 String，值为 `cn.parzulpan.dao.UserDAO.findAll`
     * **value** 即这个 Mapper 对象，属性有 `String sql` 和 `String domainClassPath`

     */

    private Map<String, Mapper> mappers = new HashMap<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public Map<String, Mapper> getMappers() {
        return mappers;
    }

    public void setMappers(Map<String, Mapper> mappers) {
        this.mappers.putAll(mappers);   // 注意这里是追加的方式，而不是覆盖
    }
}
```

### 编写 持久层接口的映射文件

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!--持久层接口的映射文件-->
<mapper namespace="cn.parzulpan.dao.UserDAO">
    <select id="findAll" resultType="cn.parzulpan.domain.User">
        select * from user;
    </select>
</mapper>
```

**注意**：由于没有使用 MyBatis 的 jar 包，所以要把映射文件的约束删掉，否则会报错。

### 编写 SqlSessionFactoryBuilder 建造者类

```java
package cn.parzulpan.mybatis.session;

import cn.parzulpan.mybatis.session.impl.SqlSessionFactoryImpl;

import java.io.InputStream;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用于 SqlSessionFactory 的创建
 */

public class SqlSessionFactoryBuilder {

    /**
     * 根据传入的流，实现对 SqlSessionFactory 的创建
     * @param is
     * @return
     */
    public SqlSessionFactory build(InputStream is) {
        SqlSessionFactoryImpl factory = new SqlSessionFactoryImpl();
        factory.setIs(is);  // //给 factory 中 is 赋值
        return factory;
    }
}

```

### 编写 SqlSessionFactory 接口和实现类

```java
package cn.parzulpan.mybatis.session;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : SqlSessionFactory 接口
 */

public interface SqlSessionFactory {

    /**
     * 创建一个新的 SqlSession 对象
     * @return
     */
    SqlSession openSession();
}

```

```java
package cn.parzulpan.mybatis.session.impl;

import cn.parzulpan.mybatis.cfg.Configuration;
import cn.parzulpan.mybatis.session.SqlSession;
import cn.parzulpan.mybatis.session.SqlSessionFactory;
import cn.parzulpan.mybatis.utils.XMLConfigBuilder;

import java.io.InputStream;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : SqlSessionFactory 接口的实现类
 */

public class SqlSessionFactoryImpl implements SqlSessionFactory {
    private InputStream is = null;

    public InputStream getIs() {
        return is;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }

    /**
     * 创建一个新的 SqlSession 对象
     *
     * @return
     */
    @Override
    public SqlSession openSession() {
        SqlSessionImpl session = new SqlSessionImpl();
        Configuration cfg = XMLConfigBuilder.loadConfiguration(session, is);    // 调用工具类解析 xml 文件
        session.setCfg(cfg);
        return session;
    }
}

```

### 编写 SqlSession 接口和实现类

```java
package cn.parzulpan.mybatis.session;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : SqlSession 接口，操作数据库的核心对象
 */

public interface SqlSession {

    /**
     * 创建 DAO 接口的的代理对象
     * @param DAOInterfaceClass
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<T> DAOInterfaceClass);

    /**
     * 释放资源
     */
    void close();
}

```

```java
package cn.parzulpan.mybatis.session.impl;

import cn.parzulpan.mybatis.cfg.Configuration;
import cn.parzulpan.mybatis.session.SqlSession;
import cn.parzulpan.mybatis.session.handler.MapperInvocationHandler;
import cn.parzulpan.mybatis.utils.DataSourceUtil;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : SqlSession 接口的实现类
 */

public class SqlSessionImpl implements SqlSession {
    private Configuration cfg;  // 核心配置对象
    private Connection connection;  // 连接对象

    public Configuration getCfg() {
        return cfg;
    }

    public void setCfg(Configuration cfg) {
        this.cfg = cfg;
        this.connection = DataSourceUtil.getConnection(this.cfg);
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * 创建 DAO 接口的的代理对象
     *
     * @param DAOInterfaceClass DAO 接口的字节码
     * @return
     */
    @Override
    public <T> T getMapper(Class<T> DAOInterfaceClass) {

//        Proxy.newProxyInstance(DAOInterfaceClass.getClassLoader(),
//                DAOInterfaceClass.getInterfaces(),
//                new MapperInvocationHandler(cfg.getMappers(), connection));

        T DAOProxy = (T)Proxy.newProxyInstance(DAOInterfaceClass.getClassLoader(),
                new Class[]{DAOInterfaceClass},
                new MapperInvocationHandler(cfg.getMappers(), connection));

        return DAOProxy;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
```

### 编写 创建 DAO 的代理对象的类

```java
package cn.parzulpan.mybatis.session.handler;

import cn.parzulpan.mybatis.cfg.Mapper;
import cn.parzulpan.mybatis.utils.Executor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用于创建代理对象是增强方法
 */

public class MapperInvocationHandler implements InvocationHandler {
    private Map<String, Mapper> mappers;    //  key 包含实体类全限定类名和方法名
    private Connection connection;

    public MapperInvocationHandler(Map<String, Mapper> mappers, Connection connection) {
        this.mappers = mappers;
        this.connection = connection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1. 获取方法名
        String methodName = method.getName();
        // 2. 获取方法所在类名
        String className = method.getDeclaringClass().getName();
        // 3. 组合 key
        String key = className + "." + methodName;
        // 4. 获取 mappers 中的 Mapper 对象
        Mapper mapper = mappers.get(key);
        // 5. 判断是否有 mapper
        if (mapper == null) {
            throw new IllegalArgumentException("传入的参数有误，无法获取执行的必要条件。");
        }
        // 6. 创建 Executor 对象，负责执行 SQL 语句，并且封装结果集
        return new Executor().selectList(mapper, connection);
    }
}
```

### 自定义注解

```java
package cn.parzulpan.mybatis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义 Select 注解
 */

// 生命周期为 RUNTIME，出现位置为 METHOD
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {
    String value(); // 配置 SQL 语句
}
```

## 总结和练习

---

**自定义 MyBatis 步骤总结**：

* 第一步：SqlSessionBuilder 接收 SqlMapConfig.xml 文件流，构建出 SqlSessionFactory 对象；
* 第二步：SqlSessionFactory 加载解析 SqlMapConfig.xml 文件流，得到连接信息和映射信息，用来生产出真正操作数据库的 SqlSession 对象；
* 第三步：SqlSession 对象有两大作用，分别是生成接口代理对象和定义通用增删改查方法。
* 第四步：
  * 第一步：在 SqlSessionImpl 对象的 `getMapper()` 分两步实现：1. 先用核心配置对象和连接对象；2. 通过代理模式创建出代理类对象；
  * 第二步：在 Executor 工具类 `selectList()` 等方法分两步实现：1. 得到连接对象；2. 得到 SQL 语句，进行 JDBC 操作。
* 第五步：封装结果集，变成 Java 对象返回给调用者。

---
