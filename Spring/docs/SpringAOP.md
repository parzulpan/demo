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

归根结底，整个转账操作应该使用**同一个连接**。可以使用 **ThreadLocal** 对象把 Connection 和 当前线程绑定，使一个线程中只有一个能控制事务的对象。

**ThreadLocal**：

* ThreadLocal 可以解决多线程的数据安全问题。
* ThreadLocal 可以给当前线程关联一个数据，这个数据可以是普通变量，可以是对象，也可以是数组和集合等。

**ThreadLocal 特点**：

* ThreadLocal 可以为当前线程关联一个数据，它可以像 Map 一样存取数据，**key 为当前线程**
* 每一个 ThreadLocal 对象，只能为当前线程关联一个数据，如果要为当前线程关联多个数据，就需要使用 多个 ThreadLocal 实例，所以是线程安全的
* 每个 ThreadLocal 对象实例定义的时候，一般都是 Static 类型
* ThreadLocal 中保存数据，在线程销毁后，会由 JVM 自动释放

### 利用事务控制解决 转账问题

[小节源码](https://github.com/parzulpan/demo/tree/main/Spring/src/SpringAOP)

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

在软件行业中，AOP（Aspect Oriented Programming，面向切面编程），是通过预编译方式和运行期动态代理实现程序功能的统一维护技术，是 OOP 的延续，也是函数式编程的一个衍生范型。

利用 AOP 可以对业务逻辑的各个部分进行隔离，从而使业务逻辑各部分之间的耦合度降低，提供程序的可重用性和开发效率。

AOP 实际是 GoF 设计模式 的延续，设计模式孜孜不倦**追求**的是调用者和被调用者之间的解耦，提高代码的灵活性和可扩展性。

主要功能（应用范围）：

* 日志记录
* 性能统计
* 安全控制
* 事务处理
* 异常处理

## AOP 相关术语

AOP 相关术语：

* **`Joinpoint 连接点`** 指哪些被拦截到的点，比如 业务层中**所有**的方法
* **`Pointcut 切入点`** 指对连接点进行拦截的点，比如 业务层中**增强**的方法
* **`Advice 通知`** 指拦截到连接点后要做的事情，通知的类型分为前置通知、后置通知、异常通知、最终通知、环绕通知（**有明确的切入点方法调用**）。比如事务控制
* **`Introduction 引导`** 指一种特殊的通知，在不修改类代码的前提下，它可以在运行期为类动态地添加一些方法或属性
* **`Target 目标对象`** 指代理的目标对象，比如 bankAccountService
* **`Weaving 织入`** 指把增强应用到目标对象来创建新的代理对象的过程，比如 Spring 采用动态代理
* **`Proxy 代理`** 指一个类被 AOP 织入增强后，就产生一个结果代理类
* **`Aspect 切面`** 指切入点和通知的结合

**Spring AOP 的分工**：

* 开发阶段，开发者 做的：
  * 编写核心业务代码
  * 把公用代码抽取出来，即**通知**
  * 在配置文件中，声明切入点和通知间的关系，即**切面**
* 运行阶段，Spring 做的：
  * Spring 监控**切入点**方法的执行
  * 一旦监控到**切入点**方法被执行，使用**代理机制**，动态创建**目标对象**的代理对象，根据**通知**类型，在代理对象的相应位置，将通知对应的功能**织入**，完成代码逻辑

**代理的选择**：

* 在 Spring 中，会根据目标对象是否实现了接口来决定采用哪种动态代理

## XML 的 AOP 配置

[小节源码](https://github.com/parzulpan/demo/tree/main/Spring/src/SpringXmlAOP)

### 步骤一

**步骤一 编写核心业务代码**：AccountServiceImpl.java

```java
package cn.parzulpan.service;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户的业务层接口的实现类
 */

public class AccountServiceImpl implements AccountService {
    public void saveAccount() {
        System.out.println("执行了保存操作...");
    }

    public void updateAccount(int id) {
        System.out.println("执行了更新操作... " + id);
    }

    public int deleteAccount() {
        System.out.println("执行了删除操作...");
        return 0;
    }
}
```

### 步骤二

**步骤二 抽取公共代码，组成通知**：Logger.java

```java
package cn.parzulpan.utils;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用于记录日志的工具类，它提供了公共的方法，即 Advice 通知
 */

public class Logger {

    /**
     * 打印日志
     * 前置通知，在 切入点方法（业务层中增强的方法）之前执行
     */
    public void printLogBefore() {
        System.out.println("Logger 类中的 printLogBefore 方法开始记录日志了...");
    }

    /**
     * 打印日志
     * 最终通知，在 切入点方法（业务层中增强的方法）之后执行
     */
    public void printLogAfter() {
        System.out.println("Logger 类中的 printLogAfter 方法开始记录日志了...");
    }

    /**
     * 环绕通知
     * 问题：当配置了环绕通知之后，切入点方法没有执行，而通知方法执行了
     * 分析：通过对比动态代理中的环绕通知，发现动态代理的环绕通知有明确的切入点方法调用
     * 解决：Spring 提供了一个接口 ProceedingJoinPint，它有一个 proceed()，此方法相当于明确调用切入点方法
     * 该接口可以作为环绕通知的方法的参数，在程序执行时，Spring 会提供该接口的实现类
     */
    public Object printLogAround(ProceedingJoinPoint pjp) {
//        System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...");
        Object rtValue = null;

        try {
            Object[] args = pjp.getArgs();  //  得到方法执行所需的参数
            System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...  前置通知");
            rtValue = pjp.proceed(args);    // 切入点方法
            System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...  后置通知");
        } catch (Throwable throwable) {
            System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...  异常通知");
            throwable.printStackTrace();
        } finally {
            System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...  最终通知");
        }
        return rtValue;
    }
}
```

### 步骤三

***AOP 配置文件编写步骤**：

* 先配置 Spring IOC，将 **业务层** 对象 和 **Advice 通知** 对象配置进来
* 然后配置 Spring AOP：
  * 第一步：使用 `aop:config` 声明 AOP 配置
  * 第二步：使用 `aop:aspect` 配置切面
    * `id 属性` 给切面提供一个唯一标识
    * `ref 属性` 指定配置好的通知类 bean 的 id
  * 第三步：配置通知的**类型**
    * `method 属性` 用于指定通知类中的增强方法名称
    * `pointcut-ref 属性` 用于指定切入点的表达式的引用
    * `pointcut 属性` 用于指定切入点表达式，使用 `aop:pointcut` 配置**切入点表达式**，指定对哪些类的哪些方法进行增强。当它在 `aop:aspect` 标签 内部时，只能用于当前切面。在外部时，就能用于所有切面，但是要求它在 `aop:aspect` 标签 **前面**
      * `expression 属性` 用于定义切入点表达式。
      * `id 属性` 用于给切入点表达式提供一个唯一标识

**切入点表达式**：指定对哪些类的哪些方法进行增强

* **语法**: `execution([修饰符] 返回值类型 包名.类名.方法名(参数))`
* 全匹配方式: `public void cn.parzulpan.service.AccountServiceImpl.saveAccount()`，其中访问修饰符可以省略
* 返回值使用 `* 号`，表示任意返回值: `* cn.parzulpan.service.AccountServiceImpl.saveAccount()`
* 包名使用 `* 号`，表示任意包，但是有几级包，就需要写几个 `* 号` : `*.*.*.AccountServiceImpl.saveAccount()`
* 使用 `.. 号` 来表示当前包，及其子包
* 类名使用 `* 号`，表示任意类，方法名使用 `* 号`，表示任意方法
* 参数列表使用 `* 号`，表示参数可以是任意数据类型，但是必须有参数
* 参数列表使用 `.. 号`，表示有无参数均可，有参数可以是任意类型
* **通常用法**：切到业务层实现类下的所有方法，`* cn.parzulpan.service.*.*(..)`

**通知类型**：

* `aop:before` 用于配置前置通知，指定增强的方法在切入点方法之前执行。**执行时间点**为 切入点方法执行之前执行
* `aop:after-returning` 用于配置后置通知。**执行时间点**为 切入点方法正常执行之后，它和异常通知只能有一个执行
* `aop:after-throwing` 用于配置异常通知。**执行时间点**为 切入点方法执行产生异常后执行，它和后置通知只能有一个执行
* `aop:after` 用于配置最终通知。**执行时间点**为 无论切入点方法执行时是否有异常，它都会在其后面执行
* **`aop:around`** 用于配置环绕通知（**环绕通知指有明确的切入点方法调用**），**它是 Spring 提供的一种可以在代码中手动控制增强代码什么时候执行的方式**

**值得注意的是**，Spring 执行时，后置通知或异常通知总是在最终通知后面。所以，推荐使用环绕通知，自定义执行顺序。

**步骤三 编写 AOP 配置文件**：bean.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        https://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 配置 Spring IOC -->
    <!-- 将 AccountService 对象配置进来 -->
    <bean id="accountService" class="cn.parzulpan.service.AccountServiceImpl"/>
    <!-- 将 Logger 对象配置进来，是一个 Advice 通知 -->
    <bean id="logger" class="cn.parzulpan.utils.Logger"/>

    <!-- 配置 Spring AOP -->
    <!-- 1. 使用 aop:config 声明 AOP 配置 -->
    <aop:config>
        <aop:pointcut id="allMethodPCRGlobal"
                      expression="execution(* cn.parzulpan.service.*.*(..))"/>
        
        <!-- 2. 使用 aop:aspect 配置切面 -->
        <aop:aspect id="logAdvice" ref="logger">
            <!-- 3. 配置通知的类型 -->
            <aop:before method="printLogBefore"
                        pointcut="execution(public void cn.parzulpan.service.AccountServiceImpl.saveAccount())"/>
            <aop:after method="printLogAfter"
                       pointcut-ref="allMethodPCR"/>
            <aop:around method="printLogAround"
                        pointcut-ref="allMethodPCRGlobal"/>

            <aop:pointcut id="allMethodPCR"
                          expression="execution(* cn.parzulpan.service.*.*(..))"/>
        </aop:aspect>
    </aop:config>
</beans>
```

### 步骤四 测试

**步骤四 测试 AOP XML 配置**：XmlAOPTest.java

```java
package cn.parzulpan;

import cn.parzulpan.service.AccountService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 测试 AOP XML 配置
 */

public class XmlAOPTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        AccountService as = ac.getBean("accountService", AccountService.class);
        as.saveAccount();
        System.out.println();
        as.updateAccount(1024);
        System.out.println();
        as.deleteAccount();
    }
}
```

## 注解 的 AOP 配置

[小节源码](https://github.com/parzulpan/demo/tree/main/Spring/src/SpringAnnotataionAOP)

**配置步骤**：

* 第一步 在配置文件中导入 `context` 的名称空间
* 第二步 所有资源使用注解配置
* 第三步 在配置文件中指定 Spring 要扫描的包
* 第四步 在配置文件中指定 Spring AOP 支持
* 第五步 在通知类上使用 `@Aspect` 注解声明为切面
* 第六步 编写切入点表达式注解
* 第七步 在增强的方法上使用**注解配置通知**

bean.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        https://www.springframework.org/schema/aop/spring-aop.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 配置 Spring 创建容器时要扫描的包  -->
    <context:component-scan base-package="cn.parzulpan"/>

    <!-- 配置 Spring AOP 支持-->
    <aop:aspectj-autoproxy/>

</beans>
```

Logger.java

```java
package cn.parzulpan.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用于记录日志的工具类，它提供了公共的方法，即 Advice 通知，使用注解
 */

@Component
@Aspect
public class Logger {
    // 编写切入点表达式注解
    @Pointcut("execution(* cn.parzulpan.service.*.*(..))")
    private void allMethodPCRGlobal(){}

    /**
     * 打印日志
     * 前置通知，在 切入点方法（业务层中增强的方法）之前执行
     */
    @Before("allMethodPCRGlobal()")
    public void printLogBefore() {
        System.out.println("Logger 类中的 printLogBefore 方法开始记录日志了...");
    }

    /**
     * 打印日志
     * 最终通知，在 切入点方法（业务层中增强的方法）之后执行
     */
    @After("allMethodPCRGlobal()")
    public void printLogAfter() {
        System.out.println("Logger 类中的 printLogAfter 方法开始记录日志了...");
    }

    /**
     * 环绕通知
     * 问题：当配置了环绕通知之后，切入点方法没有执行，而通知方法执行了
     * 分析：通过对比动态代理中的环绕通知，发现动态代理的环绕通知有明确的切入点方法调用
     * 解决：Spring 提供了一个接口 ProceedingJoinPint，它有一个 proceed()，此方法相当于明确调用切入点方法
     * 该接口可以作为环绕通知的方法的参数，在程序执行时，Spring 会提供该接口的实现类
     */
    @Around("allMethodPCRGlobal()")
    public Object printLogAround(ProceedingJoinPoint pjp) {
//        System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...");
        Object rtValue = null;

        try {
            Object[] args = pjp.getArgs();  //  得到方法执行所需的参数
            System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...  前置通知");
            rtValue = pjp.proceed(args);    // 切入点方法
            System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...  后置通知");
        } catch (Throwable throwable) {
            System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...  异常通知");
            throwable.printStackTrace();
        } finally {
            System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...  最终通知");
        }
        return rtValue;
    }
}
```

不使用 XML 的配置方式，直接纯注解，虽然不推荐，但是也可以实现。

SpringConfiguration.java

```java
@Configuration
@ComponentScan(basePackages="cn.parzulpan")
@EnableAspectJAutoProxy
public class SpringConfiguration {
}
```

XmlAOPTest.java

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class AnnotationAOPTest {
}
```

## 总结和练习
