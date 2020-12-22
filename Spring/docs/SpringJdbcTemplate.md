# Spring JdbcTemplate

[文章源码](https://github.com/parzulpan/demo/tree/main/Spring/src/SpringJdbcTemplate)

## JdbcTemplate 概述

它是 Spring 框架中提供的一个对象，是对原始 Jdbc API 对象的简单封装。Spring 框架提供了很多的操作模板类。

操作关系型数据的：JdbcTemplate、HibernateTemplate

操作 NoSQL 数据库的：RedisTemplate

操作消息队列的：JmsTemplate

要使用 JdbcTemplate，需要 spring-jdbc 包。

## JdbcTemplate CRUD

bean.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="cn.parzulpan"/>

    <!-- 添加继承 JdbcDaoSupport 的实现类到容器，并注入 jdbcTemplate-->
    <bean id="accountDAO2" class="cn.parzulpan.dao.AccountDAOImpl2">
        <property name="jdbcTemplate" ref="jdbcTemplate"/>
    </bean>

    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/springT?useSSL=false"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </bean>

</beans>
```

---

JdbcTemplate 对象的创建和基本使用：

```java
package cn.parzulpan;


import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : JdbcTemplate 对象的创建和基本使用
 */

public class JdbcTemplateTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        JdbcTemplate jt = ac.getBean("jdbcTemplate", JdbcTemplate.class);
        jt.execute("insert into bankAccount(name, money) values ('caf', 25415.6)");
    }
}
```

---

AccountDAOImpl.java

```java
package cn.parzulpan.dao;

import domain.Account;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户持久层实现类，这种方式即可以实现注解配置，也可以使用 xml 配置
 */

@Repository("accountDAO")
public class AccountDAOImpl implements AccountDAO {
    @Resource(name = "jdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Account> findAll() {
        return jdbcTemplate.query("select * from bankAccount",
                new BeanPropertyRowMapper<>(Account.class));
    }

    @Override
    public Account findById(Integer id) {
        List<Account> accounts = jdbcTemplate.query("select * from bankAccount where id = ?",
                new BeanPropertyRowMapper<>(Account.class),
                id);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    @Override
    public Account findByName(String name) {
        List<Account> accounts = jdbcTemplate.query("select * from bankAccount where name = ?",
                new BeanPropertyRowMapper<>(Account.class),
                name);
        if (accounts.isEmpty()) {
            return null;
        }

        if (accounts.size() > 1) {
            throw new RuntimeException("结果集不唯一！");
        }

        return accounts.get(0);
    }

    @Override
    public void update(Account account) {
        jdbcTemplate.update("update bankAccount set name = ? , money = ? where id = ?",
                account.getName(), account.getMoney(), account.getId());
    }

    @Override
    public void insert(Account account) {
        jdbcTemplate.update("insert into bankAccount(name, money) values (?, ?)",
                account.getName(), account.getMoney());
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("delete from bankAccount where id = ?",
                id);
    }

    @Override
    public Long getCount() {
        return jdbcTemplate.queryForObject("select count(*) from bankAccount;",
                Long.class);
    }
}
```

---

AccountDAOImpl2.java

```java
package cn.parzulpan.dao;

import domain.Account;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户持久层实现类，继承 JdbcDaoSupport
 *
 * JdbcDaoSupport 是 Spring 框架提供的一个类，该类中定义了一个 JdbcTemplate 对象，
 * 可以直接获取使用，但是要想创建该对象，需要为其提供一个数据源。
 *
 * 这种的好处是当有很多个 DAO 时，不需要注入很多 jdbcTemplate
 *
 * 注意：这种方式只能使用 xml 配置，因为 JdbcDaoSupport 中已经定义了 jdbcTemplate，且提供其 setter
 */

public class AccountDAOImpl2 extends JdbcDaoSupport implements AccountDAO {
    @Override
    public List<Account> findAll() {
        return super.getJdbcTemplate().query("select * from bankAccount",
                new BeanPropertyRowMapper<>(Account.class));
    }

    @Override
    public Account findById(Integer id) {
        List<Account> accounts = getJdbcTemplate().query("select * from bankAccount where id = ?",
                new BeanPropertyRowMapper<>(Account.class),
                id);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    @Override
    public Account findByName(String name) {
        List<Account> accounts = getJdbcTemplate().query("select * from bankAccount where name = ?",
                new BeanPropertyRowMapper<>(Account.class),
                name);
        if (accounts.isEmpty()) {
            return null;
        }

        if (accounts.size() > 1) {
            throw new RuntimeException("结果集不唯一！");
        }

        return accounts.get(0);
    }

    @Override
    public void update(Account account) {
        getJdbcTemplate().update("update bankAccount set name = ? , money = ? where id = ?",
                account.getName(), account.getMoney(), account.getId());
    }

    @Override
    public void insert(Account account) {
        getJdbcTemplate().update("insert into bankAccount(name, money) values (?, ?)",
                account.getName(), account.getMoney());
    }

    @Override
    public void delete(Integer id) {
        getJdbcTemplate().update("delete from bankAccount where id = ?",
                id);
    }

    @Override
    public Long getCount() {
        return getJdbcTemplate().queryForObject("select count(*) from bankAccount;",
                Long.class);
    }
}
```

---

AccountDAOImplTest.java

```java
package cn.parzulpan.dao;

import domain.Account;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户持久层实现类的测试
 */

public class AccountDAOImplTest {
    ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
//    AccountDAO ad = ac.getBean("accountDAO1", AccountDAO.class);
    AccountDAO ad = ac.getBean("accountDAO2", AccountDAO.class);

    @Test
    public void findAll() {
        List<Account> accounts = ad.findAll();
        for (Account account : accounts) {
            System.out.println(account);
        }
    }

    @Test
    public void findById() {
        Account account = ad.findById(1);
        System.out.println(account);
    }

    @Test
    public void findByName() {
        Account account = ad.findByName("aaa");
        System.out.println(account);
    }

    @Test
    public void update() {
        ad.update(new Account(6, "update", 214.0));
    }

    @Test
    public void insert() {
        ad.insert(new Account(99, "insert", 3125616.425));
    }

    @Test
    public void delete() {
        ad.delete(7);
    }

    @Test
    public void getCount() {
        Long count = ad.getCount();
        System.out.println(count);
    }
}
```

## 总结和练习
