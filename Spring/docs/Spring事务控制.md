# Spring 事务控制

## Spring 事务控制介绍

JavaEE 体系进行分层开发，事务控制位于**业务层**，Spring 提供了分层设计**业务层**的事务处理解决方案。

Spring 的事务控制都是基于 AOP 的，它既可以使用编程的方式实现，也可以使用配置的方式实现。**但是推荐以配置的方式实现**。

PlatformTransactionManager 接口是提供事务操作的方法，它包含的获取事务状态信息、提交事务、回滚事务等方法。

**DataSourceTransactionManager** 实现类用于 Spring JdbcTemplate 或 MyBatis 持久化数据。

**事务的传播行为**：

* **REQUIRED** 如果当前没有事务，就新建一个事务，如果已经存在一个事务中，加入到这个事务中。**一般用于增删改操作**
* **SUPPORTS** 支持当前事务，如果当前没有事务，就以非事务方式执行。**一般用于查操作**
* MANDATORY 使用当前的事务，如果当前没有事务，就抛出异常
* REQUERS_NEW 新建事务，如果当前在事务中，把当前事务挂起
* NOT_SUPPORTED 以非事务方式执行操作，如果当前存在事务，就把当前事务挂起
* NEVER 以非事务方式运行，如果当前存在事务，抛出异常
* NESTED 如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则执行 REQUIRED 类似的操作

## XML 的 事务控制

[本节源码](https://github.com/parzulpan/demo/tree/main/Spring/src/SpringTransactionXml)

**使用步骤**：

* 创建 Spring 的配置文件并导入约束
* 准备数据库表和实体类
* 编写持久层接口和实现类
* 编写业务层接口和实现类
* 编写配置文件
  * 在配置文件中配置持久层和业务
  * 配置数据源
  * 配置事务相关：
    * 配置事务管理器
    * 配置事务的通知
      * 配置事务的属性
    * 配置 AOP
      * 配置 AOP 切入点表达式
      * 配置切入点表达式和事务通知的对应关系

---

账户的持久层接口的实现类：AccountDAOImpl.java

```java
package cn.parzulpan.dao;

import cn.parzulpan.domain.Account;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户的持久层接口的实现类
 */

public class AccountDAOImpl extends JdbcDaoSupport implements AccountDAO {

    public Account findById(Integer accountId) {
        List<Account> accounts = getJdbcTemplate().query("select * from bankAccount where id = ?",
                new BeanPropertyRowMapper<Account>(Account.class),
                accountId);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    public Account findByName(String name) {
        List<Account> accounts = getJdbcTemplate().query("select * from bankAccount where name = ?",
                new BeanPropertyRowMapper<Account>(Account.class),
                name);
        if (accounts.isEmpty()) {
            return null;
        }
        if (accounts.size() > 1) {
            throw new RuntimeException("结果集不唯一");
        }
        return accounts.get(0);
    }

    public void update(Account account) {
        getJdbcTemplate().update("update bankAccount set name = ?, money = ? where id = ?",
                account.getName(), account.getMoney(), account.getId());
    }
}
```

---

账户的持久层接口的实现类：AccountServiceImpl.java

```java
package cn.parzulpan.service;

import cn.parzulpan.dao.AccountDAO;
import cn.parzulpan.domain.Account;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户的业务层接口的实现类，事务控制应该在业务层
 */

public class AccountServiceImpl implements AccountService {
    private AccountDAO accountDAO;

    public void setAccountDAO(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account findById(Integer accountId) {
        return accountDAO.findById(accountId);
    }

    public void transfer(String sourceName, String targetName, Double money) {
        System.out.println("开始进行转账...");

        Account source = accountDAO.findByName(sourceName);
        Account target = accountDAO.findByName(targetName);
        source.setMoney(source.getMoney() - money);
        target.setMoney(target.getMoney() + money);
        accountDAO.update(source);
        int i = 1 / 0;  // 模拟转账故障
        accountDAO.update(target);

        System.out.println("转账完成...");
    }
}
```

---

配置文件：bean.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        https://www.springframework.org/schema/aop/spring-aop.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/tx
        https://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- 配置账户业务层 -->
    <bean id="accountService" class="cn.parzulpan.service.AccountServiceImpl">
        <property name="accountDAO" ref="accountDAO"/>
    </bean>

    <!-- 配置账户持久层 -->
    <bean id="accountDAO" class="cn.parzulpan.dao.AccountDAOImpl">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 配置数据源 -->
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/springT?useSSL=false"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </bean>

    <!-- 1. 配置事务管理器 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 2. 配置事务的通知 -->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <!-- 3. 配置事务的属性
                指定是业务核心方法
                read-only：是否是只读事务。默认 false，不只读。
                isolation：指定事务的隔离级别。默认值是使用数据库的默认隔离级别。
                propagation：指定事务的传播行为。
                timeout：指定超时时间。默认值为：-1。永不超时。
                rollback-for：用于指定一个异常，当执行产生该异常时，事务回滚。产生其他异常，事务不回滚。
                没有默认值，任何异常都回滚。
                no-rollback-for：用于指定一个异常，当产生该异常时，事务不回滚，产生其他异常时，事务回滚。
                没有默认值，任何异常都回滚。
        -->
        <tx:attributes>
            <tx:method name="*" read-only="false" propagation="REQUIRED"/>
            <!-- 查询方法 -->
            <tx:method name="find*" read-only="true" propagation="SUPPORTS"/>
        </tx:attributes>
    </tx:advice>

    <!-- 4. 配置 AOP -->
    <aop:config>
        <!-- 5. 配置 AOP 切入点表达式 -->
        <aop:pointcut id="allServiceImplPCR" expression="execution(* cn.parzulpan.service.*.*(..))"/>
        <!-- 6. 配置切入点表达式和事务通知的对应关系 -->
        <aop:advisor advice-ref="txAdvice" pointcut-ref="allServiceImplPCR"/>
    </aop:config>

</beans>
```

---

对 账户的业务层 进行单元测试：AccountServiceImplTest.java

```java
package cn.parzulpan.service;

import cn.parzulpan.domain.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 对 账户的业务层 进行单元测试
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:bean.xml")
public class AccountServiceImplTest {

    @Autowired
    private  AccountService as;

    @Test
    public void findById() {
        Account account = as.findById(1);
        System.out.println(account);
    }

    @Test
    public void transfer() {
        as.transfer("aaa", "bbb", 100.0);
    }
}
```

## 注解 的 事务控制

[本节源码](https://github.com/parzulpan/demo/tree/main/Spring/src/SpringTransactionAnnotation)

**使用步骤**：

* 其他同 XML 的 事务控制
* 编写配置文件
  * 配置事务管理器
  * 开启对注解事务的支持
  * 在需要事务支持的地方使用 @Transactional

---

账户的持久层接口的实现类：AccountDAOImpl.java

```java
package cn.parzulpan.dao;

import cn.parzulpan.domain.Account;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户的持久层接口的实现类
 */

@Repository("accountDAO")
public class AccountDAOImpl implements AccountDAO {
    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public Account findById(Integer accountId) {
        List<Account> accounts = jdbcTemplate.query("select * from bankAccount where id = ?",
                new BeanPropertyRowMapper<Account>(Account.class),
                accountId);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    public Account findByName(String name) {
        List<Account> accounts = jdbcTemplate.query("select * from bankAccount where name = ?",
                new BeanPropertyRowMapper<Account>(Account.class),
                name);
        if (accounts.isEmpty()) {
            return null;
        }
        if (accounts.size() > 1) {
            throw new RuntimeException("结果集不唯一");
        }
        return accounts.get(0);
    }

    public void update(Account account) {
        jdbcTemplate.update("update bankAccount set name = ?, money = ? where id = ?",
                account.getName(), account.getMoney(), account.getId());
    }
}
```

---

账户的持久层接口的实现类：AccountServiceImpl.java

```java
package cn.parzulpan.service;

import cn.parzulpan.dao.AccountDAO;
import cn.parzulpan.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户的业务层接口的实现类，事务控制应该在业务层
 */

@Service("accountService")
public class AccountServiceImpl implements AccountService {
    @Resource(name = "accountDAO")
    private AccountDAO accountDAO;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Account findById(Integer accountId) {
        return accountDAO.findById(accountId);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void transfer(String sourceName, String targetName, Double money) {
        System.out.println("开始进行转账...");

        Account source = accountDAO.findByName(sourceName);
        Account target = accountDAO.findByName(targetName);
        source.setMoney(source.getMoney() - money);
        target.setMoney(target.getMoney() + money);
        accountDAO.update(source);
        int i = 1 / 0;  // 模拟转账故障
        accountDAO.update(target);

        System.out.println("转账完成...");
    }
}
```

---

配置文件：bean.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        https://www.springframework.org/schema/aop/spring-aop.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/tx
        https://www.springframework.org/schema/tx/spring-tx.xsd">

    <context:component-scan base-package="cn.parzulpan"/>

    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 配置数据源 -->
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/springT?useSSL=false"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </bean>

    <!-- 1. 配置事务管理器 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 2. 开启对注解事务的支持 -->
    <tx:annotation-driven transaction-manager="transactionManager"/>

    <!-- 3. 在需要事务支持的地方使用 @Transactional -->
</beans>
```

---

## 总结和练习
