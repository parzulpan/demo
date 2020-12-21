# Spring AOP

## SpringCRUD 存在的问题

* bean.xml

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/context
            http://www.springframework.org/schema/context/spring-context.xsd">

        <context:component-scan base-package="cn.parzulpan"/>

        <!-- 配置 QueryRunner -->
        <bean id="runner" class="org.apache.commons.dbutils.QueryRunner" scope="prototype">
            <!-- 注入数据源，构造函数形式-->
            <constructor-arg name="ds" ref="dataSource"/>
        </bean>

        <!-- 配置 数据源 -->
        <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
            <property name="driverClass" value="com.mysql.jdbc.Driver"/>
            <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/springT?useSSL=false"/>
            <property name="user" value="root"/>
            <property name="password" value="root"/>
        </bean>

    </beans>
    ```

* BankAccountDAOImpl.java

    ```java
    package cn.parzulpan.dao;

    import cn.parzulpan.domain.BankAccount;
    import cn.parzulpan.utils.ConnectionUtil;
    import org.apache.commons.dbutils.QueryRunner;
    import org.apache.commons.dbutils.handlers.BeanHandler;
    import org.apache.commons.dbutils.handlers.BeanListHandler;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Repository;

    import java.sql.SQLException;
    import java.util.List;

    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : 银行账户的持久层接口的实现类
     */

    @Repository("bankAccountDAO")
    public class BankAccountDAOImpl implements BankAccountDAO {
        @Autowired
        private QueryRunner runner;

        public List<BankAccount> findAll() {
            try {
                return runner.query("select * from bankAccount",
                        new BeanListHandler<BankAccount>(BankAccount.class));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public BankAccount findByName(String accountName) {
            try {
                List<BankAccount> accounts = runner.query("select * from bankAccount where name = ?",
                        new BeanListHandler<BankAccount>(BankAccount.class), accountName);
                if (accounts == null || accounts.size() == 0) {
                    return null;
                }
                if (accounts.size() > 1) {
                    throw new RuntimeException("结果集不一致，请检查账户名称！");
                }
                return accounts.get(0);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    ```

* BankAccountServiceImpl.java

    ```java
    package cn.parzulpan.service;

    import cn.parzulpan.dao.BankAccountDAO;
    import cn.parzulpan.domain.BankAccount;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;

    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : 银行账户的业务层接口的实现类
     */

    @Service("bankAccountService")
    public class BankAccountServiceImpl implements BankAccountService {
        @Autowired
        private BankAccountDAO bankAccountDAO;

        public List<BankAccount> findAll() {
            return accounts =  bankAccountDAO.findAll();
    }

        public void transfer(String sourceName, String targetName, Double money) {
            BankAccount source = bankAccountDAO.findByName(sourceName);
            BankAccount target = bankAccountDAO.findByName(targetName);
            source.setMoney(source.getMoney() - money);
            target.setMoney(target.getMoney() + money);
            bankAccountDAO.update(source);
            int i = 1 / 0;  //  模拟转账异常
            bankAccountDAO.update(target);
        }
    }
    ```

当执行 **转账操作** 时，由于执行有异常，转账失败。但是因为每次执行持久层方法都是独立事务，导致无法实现事务控制，不符合事务的一致性。

归根结底，整个转账操作应该使用**同一个连接**。即使用 **ThreadLocal** 对象把 Connection 和 当前线程绑定，使一个线程中只有一个能控制事务的对象。

**ThreadLocal**：

* ThreadLocal 可以解决多线程的数据安全问题。
* ThreadLocal 可以给当前线程关联一个数据，这个数据可以是普通变量，可以是对象，也可以是数组和集合等。

**ThreadLocal 特点**：

* ThreadLocal 可以为当前线程关联一个数据，它可以像 Map 一样存取数据，**key 为当前线程**
* 每一个 ThreadLocal 对象，只能为当前线程关联一个数据，如果要为当前线程关联多个数据，就需要使用 多个 ThreadLocal 实例，所以是线程安全的
* 每个 ThreadLocal 对象实例定义的时候，一般都是 Static 类型
* ThreadLocal 中保存数据，在线程销毁后，会由 JVM 自动释放

### 利用事务控制解决 转账问题

* ConnectionUtil.java

```java
package cn.parzulpan.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 连接对象的工具类，它用于从数据中获取一个连接，并且实现和线程的绑定。
 */

@Component
public class ConnectionUtil {
    private ThreadLocal<Connection> conns = new ThreadLocal<Connection>();

    @Autowired
    private DataSource dataSource;

    /**
     * 获取一个连接
     * @return connection
     */
    public Connection getThreadConnection() {
        // 1. 从 ThreadLocal 中获取
        Connection connection = conns.get();
        // 2. 判断当前线程上是否有连接
        if (connection == null) {
            try {
                // 3. 从数据源中获取一个连接，并且存入 ThreadLocal
                connection = dataSource.getConnection();
                conns.set(connection);
                connection.setAutoCommit(false);    // 设置这个连接为手动管理事务
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // 4. 返回当前线程上的连接
        return connection;
    }

    /**
     * 提交事务并关闭连接
     */
    public void commitAndClose() {
        Connection connection = conns.get();
        if (connection != null) {   // 如果不等于 null，说明之前使用过这个连接，操作过数据库
            try {
                connection.commit();    // 提交事务
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close(); // 关闭连接，资源资源
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        conns.remove(); // 对于用了线程池技术的，需要将连接与线程解绑
    }

    /**
     * 回滚事务并关闭连接
     */
    public void rollbackAndClose() {
        Connection connection = conns.get();
        if (connection != null) {   // 如果不等于 null，说明之前使用过这个连接，操作过数据库
            try {
                connection.rollback();    // 回滚事务
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close(); // 关闭连接，资源资源
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        conns.remove(); // 对于用了线程池技术的，需要将连接与线程解绑
    }
}
```

* bean.xml

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/context
            http://www.springframework.org/schema/context/spring-context.xsd">

        <context:component-scan base-package="cn.parzulpan"/>

        <!-- 配置 QueryRunner -->
        <bean id="runner" class="org.apache.commons.dbutils.QueryRunner" scope="prototype">
            <!-- 注入数据源，构造函数形式-->
    <!--        <constructor-arg name="ds" ref="dataSource"/>-->
            <!-- 注释掉 注入数据源，不需要自己获取连接，在 ConnectionUtil 中注入，并由 ConnectionUtil 进行事务控制 -->
        </bean>

        <!-- 配置 数据源 -->
        <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
            <property name="driverClass" value="com.mysql.jdbc.Driver"/>
            <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/springT?useSSL=false"/>
            <property name="user" value="root"/>
            <property name="password" value="root"/>
        </bean>
        
    </beans>
    ```

* BankAccountDAOImpl.java

    ```java
    package cn.parzulpan.dao;

    import cn.parzulpan.domain.BankAccount;
    import cn.parzulpan.utils.ConnectionUtil;
    import org.apache.commons.dbutils.QueryRunner;
    import org.apache.commons.dbutils.handlers.BeanHandler;
    import org.apache.commons.dbutils.handlers.BeanListHandler;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Repository;

    import java.sql.SQLException;
    import java.util.List;

    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : 银行账户的持久层接口的实现类，使用 ConnectionUtil 事务控制
     */

    @Repository("bankAccountDAO")
    public class BankAccountDAOImpl implements BankAccountDAO {
        @Autowired
        private QueryRunner runner;
        @Autowired
        private ConnectionUtil connectionUtil;

        public List<BankAccount> findAll() {
            try {
                return runner.query(connectionUtil.getThreadConnection(), "select * from bankAccount",
                        new BeanListHandler<BankAccount>(BankAccount.class));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public BankAccount findByName(String accountName) {
            try {
                List<BankAccount> accounts = runner.query(connectionUtil.getThreadConnection(), "select * from bankAccount where name = ?",
                        new BeanListHandler<BankAccount>(BankAccount.class), accountName);
                if (accounts == null || accounts.size() == 0) {
                    return null;
                }
                if (accounts.size() > 1) {
                    throw new RuntimeException("结果集不一致，请检查账户名称！");
                }
                return accounts.get(0);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    ```

* BankAccountServiceImpl.java

```java
package cn.parzulpan.service;

import cn.parzulpan.dao.BankAccountDAO;
import cn.parzulpan.domain.BankAccount;
import cn.parzulpan.utils.ConnectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 银行账户的业务层接口的实现类，使用 ConnectionUtil 事务控制
 */

@Service("bankAccountService")
public class BankAccountServiceImpl implements BankAccountService {
    @Autowired
    private BankAccountDAO bankAccountDAO;
    @Autowired
    private ConnectionUtil connectionUtil;

    public List<BankAccount> findAll() {
        List<BankAccount> accounts = null;
        try {
            accounts =  bankAccountDAO.findAll();
            connectionUtil.commitAndClose();
        } catch (Exception e) {
            connectionUtil.rollbackAndClose();
            throw new RuntimeException(e);
        }
        return accounts;
    }

    public void transfer(String sourceName, String targetName, Double money) {
        try {
            BankAccount source = bankAccountDAO.findByName(sourceName);
            BankAccount target = bankAccountDAO.findByName(targetName);
            source.setMoney(source.getMoney() - money);
            target.setMoney(target.getMoney() + money);
            bankAccountDAO.update(source);
            int i = 1 / 0;  //  模拟转账异常
            bankAccountDAO.update(target);
            connectionUtil.commitAndClose();
        } catch (Exception e) {
            connectionUtil.rollbackAndClose();
            throw new RuntimeException(e);
        }
    }
}

```

虽然通过事务控制对业务层进行了改造，但是也产生了**新的问题**：业务层方法变得臃肿了，里面充斥着很多**重复代码**。并且存在很多**依赖注入**。这个问题可以通过 **Spring 事务管理** 来解决！

**更加严重的是**，业务层方法和事务控制方法严重耦合了，试想一下，比如 提交事务并关闭连接 `commitAndClose()` 等方法名更改，那么所有业务层的代码都需要更改。这个问题可以通过 **动态代理** 来解决！

## 动态代理

[推荐查看 反射的应用：动态代理](https://www.cnblogs.com/parzulpan/p/14131737.html#%E5%8F%8D%E5%B0%84%E7%9A%84%E5%BA%94%E7%94%A8%EF%BC%9A%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86)

动态代理是指客户通过代理类来调用其它对象的方法，并且是在程序运行时根据需要 **动态创建目标类（字节码在用时才创建和加载）** 的代理对象，它可以在不修改源码的基础上**对方法进行增强**。而静态代理在编译期间字节码就确定下来了。

动态代理实现的**两种方式**：

* ***基于接口的动态代理**
  * 如何创建代理对象：`JDK java.lang.reflect.Proxy`，即使用 `Proxy.newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)`
    * `loader` **类加载器**，它是用于加载代理对象字节码的，和被代理对象使用相同的类加载器，即 **`被代理对象.getClass().getClassLoader()`**
    * `interfaces` **字节码数组**，它是用于让代理对象和被代理对象有相同的方法，即 **`被代理对象.getClass().getInterfaces()`**
    * `h` **提供增强的代码**，它是用于如何代理，通常是一个 `InvocationHandler` 接口的实现类，可以是匿名内部类
  * 创建代理对象要求：被代理类 实现 `InvocationHandler` 接口，要实现这个接口，必须重写 `Object invoke(Object proxy, Method method, Object[] args)`
    * `proxy` 代理对象的引用
    * `method` 当前执行的方法
    * `args` 当前执行的方法所需的参数
    * `@return` `method.invoke(被代理对象, args)`
    * 可以在 `invoke()` 前后增加一些通用方法。**注意**，被代理对象必须是基于接口的
* 基于子类的动态代理
  * 如何创建代理对象：`cglib 2.2.2 net.sf.cglib.proxy.Enhancer` 或者 `Spring org.springframework.cglib.proxy.Enhancer`，即使用 `Enhancer.create(Class type, Callback callback)`
    * `type` 它是用于指定被代理对象的字节码，即 **`被代理对象.getClass()`**
    * `callback` **提供增强的代码**，它是用于如何代理，通常是一个 `MethodInterceptor` 接口的实现类，可以是匿名内部类
  * 创建代理对象要求：被代理类不能是最终类（不能用 final 修饰的类）。必须重写 `Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)`
    * `o` 代理对象的引用
    * `method` 当前执行的方法
    * `args` 当前执行的方法所需的参数
    * `methodProxy` 当前执行方法的代理对象

## 解决 SpringCRUD 存在的问题

* BeanFactory.java

    ```java
    package cn.parzulpan.factory;

    import cn.parzulpan.service.BankAccountService;
    import cn.parzulpan.utils.ConnectionUtil;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;

    import java.lang.reflect.InvocationHandler;
    import java.lang.reflect.Method;
    import java.lang.reflect.Proxy;

    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : 用于创建 业务层实现类 的 代理对象工厂
     */

    @Component
    public class BeanFactory {
        @Autowired
        private BankAccountService bankAccountService;  // 被代理类
        @Autowired
        private ConnectionUtil connectionUtil;

        /**
         * 获取 业务层实现类 的 代理对象
         * @return
         */
        public BankAccountService getBankAccountService() {
            System.out.println("获取 业务层实现类 的 代理对象");
            return (BankAccountService) Proxy.newProxyInstance(bankAccountService.getClass().getClassLoader(),
                    bankAccountService.getClass().getInterfaces(),
                    new InvocationHandler() {
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            // 添加事务控制
                            Object rtValue = null;
                            try {
    //                            accounts =  bankAccountDAO.findAll();
                                rtValue = method.invoke(bankAccountService, args);
                                connectionUtil.commitAndClose();
                            } catch (Exception e) {
                                connectionUtil.rollbackAndClose();
                                throw new RuntimeException(e);
                            }
                            return rtValue;
                        }
                    });
        }
    }
    ```

* BankAccountServiceImpl.java

    ```java
    package cn.parzulpan.service;

    import cn.parzulpan.dao.BankAccountDAO;
    import cn.parzulpan.domain.BankAccount;
    import cn.parzulpan.utils.ConnectionUtil;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;

    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : 银行账户的业务层接口的实现类，使用 ConnectionUtil 事务控制，使用动态代理
     */

    @Service("bankAccountService")
    public class BankAccountServiceImpl implements BankAccountService {
        @Autowired
        private BankAccountDAO bankAccountDAO;
    //    @Autowired
    //    private ConnectionUtil connectionUtil;

        public List<BankAccount> findAll() {
    //        使用事务管理
    //        List<BankAccount> accounts = null;
    //        try {
    //            accounts =  bankAccountDAO.findAll();
    //            connectionUtil.commitAndClose();
    //        } catch (Exception e) {
    //            connectionUtil.rollbackAndClose();
    //            throw new RuntimeException(e);
    //        }
    //        return accounts;

            // 使用动态代理
            return bankAccountDAO.findAll();
        }

        public void transfer(String sourceName, String targetName, Double money) {
    //        try {
    //            BankAccount source = bankAccountDAO.findByName(sourceName);
    //            BankAccount target = bankAccountDAO.findByName(targetName);
    //            source.setMoney(source.getMoney() - money);
    //            target.setMoney(target.getMoney() + money);
    //            bankAccountDAO.update(source);
    //            int i = 1 / 0;  //  模拟转账异常
    //            bankAccountDAO.update(target);
    //            connectionUtil.commitAndClose();
    //        } catch (Exception e) {
    //            connectionUtil.rollbackAndClose();
    //            throw new RuntimeException(e);
    //        }

            BankAccount source = bankAccountDAO.findByName(sourceName);
            BankAccount target = bankAccountDAO.findByName(targetName);
            source.setMoney(source.getMoney() - money);
            target.setMoney(target.getMoney() + money);
            bankAccountDAO.update(source);
            int i = 1 / 0;  //  模拟转账异常
            bankAccountDAO.update(target);
        }
    }
    ```

* bean.xml

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/context
            http://www.springframework.org/schema/context/spring-context.xsd">

        <context:component-scan base-package="cn.parzulpan"/>

        <!-- 配置代理的 Service -->
        <bean id="proxyBankAccountService" factory-bean="beanFactory" factory-method="getBankAccountService"/>

        <!-- 配置 QueryRunner -->
        <bean id="runner" class="org.apache.commons.dbutils.QueryRunner" scope="prototype">
            <!-- 注入数据源，构造函数形式-->
    <!--        <constructor-arg name="ds" ref="dataSource"/>-->
            <!-- 注释掉 注入数据源，不需要自己获取连接，在 ConnectionUtil 中注入，并由 ConnectionUtil 进行事务控制 -->
        </bean>

        <!-- 配置 数据源 -->
        <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
            <property name="driverClass" value="com.mysql.jdbc.Driver"/>
            <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/springT?useSSL=false"/>
            <property name="user" value="root"/>
            <property name="password" value="root"/>
        </bean>

    </beans>
    ```

* BankAccountServiceImplTest.java

    ```java
    package cn.parzulpan.service;

    import cn.parzulpan.domain.BankAccount;
    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.test.context.ContextConfiguration;
    import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

    import javax.annotation.Resource;
    import java.util.List;

    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : 测试 银行账户的业务层接口的实现类
     */

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(locations = "classpath:bean.xml")
    public class BankAccountServiceImplTest {

    //    @Autowired
    //    private BankAccountService as;

        // 指定 BankAccountService 的代理对象
        @Resource(name = "proxyBankAccountService")
        private BankAccountService as;

        @Test
        public void findAllTest() {
            List<BankAccount> accounts = as.findAll();
            for (BankAccount account : accounts) {
                System.out.println(account);
            }
        }

        @Test
        public void transfer() {
            as.transfer("aaa", "bbb", 100.0);
        }
    }
    ```

## AOP 概念

## AOP 相关术语

## XML 的 AOP 配置

## 注解 的 AOP 配置

## 总结和练习
