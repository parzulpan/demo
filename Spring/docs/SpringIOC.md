# Spring IOC

## IOC 的常用注解

[小节源码](https://github.com/parzulpan/demo/tree/main/Spring/src/SpringIOC)

之前的 XML 配置：

```xml
    <bean  id="accountService" class="cn.parzulpan.service.AccountServiceImpl"
           scope="" init-method="" destroy-method=""
           <perperty name="" value="" | ref=""/>
    />
```

常用注解的**分类**：

* 用于创建对象的，它们的作用就和 XML 配置 中编写一个 `<bean id="" class="">` 标签实现的功能相同。
* 用于注入数据的，它们的作用就和 XML 配置 中编写一个 `<bean>` 标签中写一个 `<perperty>` 标签实现的功能相同。
* 用于改变作用范围的，它们的作用就和 XML 配置 中编写一个 `<bean>` 标签中 `scope` 属性实现的功能相同。
* 用于体现生命周期的，它们的作用就和 XML 配置 中编写一个 `<bean>` 标签中 `init-method` 和 `destroy-method` 属性实现的功能相同。

### 用于创建对象的

* 用于创建对象的，它们的作用就和 XML 配置 中编写一个 `<bean id="" class="">` 标签实现的功能相同。
  * `@Component` 用于把当前类对象存入 Spring 容器中，`value 属性` 指定 bean 的 id，如果不指定 value 属性，默认 bean 的 id 是当前类的类名，**首字母小写**。
  * `@Controller` 一般用于表现层的注解。
  * `@Service` 一般用于业务层的注解。
  * `@Repository` 一般用于持久层的注解。
  * 注意：上面三个注解都是针对 Componet 的衍生注解，他们的作用及属性都是一模一样的，只不过是提供了更加明确的语义化。

**注意**：如果注解中有且只有一个属性要赋值时，**且名称是 value**，value 在赋值是可以不写。

```java
package cn.parzulpan.service;


import org.springframework.stereotype.Component;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@Component()
public class AccountServiceImpl implements AccountService {
}
```

```java
package cn.parzulpan.ui;

import cn.parzulpan.service.AccountService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public class Client {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        AccountService as = ac.getBean("accountServiceImpl", AccountService.class);
        System.out.println(as);

    }
}

```

**注意**：由于用到了注解，所以需要配置 bean.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 告知 Spring 在创建容器时要扫描的包 -->
    <context:component-scan base-package="cn.parzulpan"></context:component-scan>

</beans>
```

### 用于注入数据的

* 用于注入数据的，它们的作用就和 XML 配置 中编写一个 `<bean>` 标签中写一个 `<perperty>` 标签实现的功能相同。
  * `@Autowired` 自动按照类型注入。当使用注解注入属性时，set 方法可以省略。它**只能注入其他 bean 类型**，当有多个类型匹配时，使用要注入的对象**变量名称**作为 bean 的 id，在 spring 容器查找，找到了也可以注入成功，找不到就报错。
  * `@Qualifier` 在自动按照类型注入的基础之上，再按照 Bean 的 id 注入。它在给**成员注入**时不能独立使用，必须和 `@Autowire` 一起使用；但是给**方法参数注入**时，可以独立使用。它**也只能注入其他 bean 类型**。`value 属性` 指定 bean 的 id。
  * `@Resource` 直接按照 Bean 的 id 注入，能独立使用。它**也只能注入其他 bean 类型**。`name 属性` 指定 bean 的 id。
  * `@Value` 注入基本数据类型和 String 类型数据。`value属性` 用于指定值，它也可以使用 Spring 的 SpEL，即 Spring 的 EL 表达式（`${表达式}`）。

```java
package cn.parzulpan.service;


import cn.parzulpan.dao.AccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@Service
public class AccountServiceImpl implements AccountService {

//    @Autowired
//    private AccountDAO accountDAO;

//    @Autowired
//    @Qualifier("accountDAOImpl2")
//    private AccountDAO accountDAO;

    @Resource(name = "accountDAOImpl2")
    private AccountDAO accountDAO;

    @Override
    public void saveAccount() {
        System.out.println(accountDAO);
        accountDAO.saveAccount();
    }
}

```

**注意**：集合类型的注入只能通过 XML 来实现。

### 用于改变作用范围的

* 用于改变作用范围的，它们的作用就和 XML 配置 中编写一个 `<bean>` 标签中 `scope` 属性实现的功能相同。
  * @Scope 指定 bean 的作用范围。`value 属性` 指定范围的值。取值为 `singleton` `prototype` `request` `session` `globalsession`

```java
@Scope("prototype")
public class AccountServiceImpl implements AccountService {}
```

### 用于体现生命周期的

* 用于体现生命周期的，它们的作用就和 XML 配置 中编写一个 `<bean>` 标签中 `init-method` 和 `destroy-method` 属性实现的功能相同。
  * `@PostConstruct` 用于指定初始化方法。
  * `@PreDestroy` 用于指定销毁方法。

```java
@Service
@Scope("singleton")
public class AccountServiceImpl implements AccountService {
    @PostConstruct
    public void init() {
        System.out.println("AccountService 对象初始化...");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("AccountService 对象销毁...");
    }
}
```

## xml 单表 CRUD

**需求**：实现银行账户的 CRUD 操作。采用 **xml** 配置。使用 **Spring IOC** 实现对象的管理，使用 **DBUtils** 和 **C3P0** 作为持久层解决方案。[小节源码](https://github.com/parzulpan/demo/tree/main/Spring/src/SpringXmlCRUD)

* 银行账户表结构

    ```sql
    use springT;

    # ---
    # 银行账户表结构

    drop table if exists `bankAccount`;

    create table `bankAccount`(
        `id` int(11) primary key auto_increment,
        `name` varchar(32) not null comment '账户名称',
        `money` float not null comment '账户余额'
    ) engine=InnoDB default charset=utf8;

    insert into bankAccount(name, money) values
    ('aaa', 1000), ('bbb', 2000), ('ccc', 3000);

    # ---
    ```

* 银行账户的实体类

    ```java
    package cn.parzulpan.domain;

    import java.io.Serializable;

    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : 银行账户的实体类
     */

    public class BankAccount implements Serializable {
        private Integer id;
        private String name;
        private Double money;

        public BankAccount() {
        }

        public BankAccount(Integer id, String name, Double money) {
            this.id = id;
            this.name = name;
            this.money = money;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getMoney() {
            return money;
        }

        public void setMoney(Double money) {
            this.money = money;
        }

        @Override
        public String toString() {
            return "BankAccount{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", money=" + money +
                    '}';
        }
    }
    ```

* 银行账户的持久层接口的实现类

    ```java
    package cn.parzulpan.dao;

    import cn.parzulpan.domain.BankAccount;
    import org.apache.commons.dbutils.QueryRunner;
    import org.apache.commons.dbutils.handlers.BeanHandler;
    import org.apache.commons.dbutils.handlers.BeanListHandler;

    import java.sql.SQLException;
    import java.util.List;

    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : 银行账户的持久层接口的实现类
     */

    public class BankAccountDAOImpl implements BankAccountDAO {
        private QueryRunner runner;

        public void setRunner(QueryRunner runner) {
            this.runner = runner;
        }

        public List<BankAccount> findAll() {
            try {
                return runner.query("select * from bankAccount",
                        new BeanListHandler<BankAccount>(BankAccount.class));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public BankAccount findById(Integer id) {
            try {
                return runner.query("select * from bankAccount where id = ?",
                        new BeanHandler<BankAccount>(BankAccount.class), id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public void save(BankAccount bankAccount) {
            try {
                runner.update("insert into bankAccount(name, money) values (?, ?)",
                        bankAccount.getName(), bankAccount.getMoney());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public void update(BankAccount bankAccount) {
            try {
                runner.update("update bankAccount set name = ?, money = ? where id = ?",
                        bankAccount.getName(), bankAccount.getMoney(), bankAccount.getId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public void deleteById(Integer id) {
            try {
                runner.update("delete from bankAccount where id = ?",
                        id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    ```

* 银行账户的业务层接口的实现类

    ```java
    package cn.parzulpan.service;

    import cn.parzulpan.dao.BankAccountDAO;
    import cn.parzulpan.domain.BankAccount;

    import java.util.List;

    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : 银行账户的业务层接口的实现类
     */

    public class BankAccountServiceImpl implements BankAccountService {
        private BankAccountDAO bankAccountDAO;

        public void setBankAccountDAO(BankAccountDAO bankAccountDAO) {
            this.bankAccountDAO = bankAccountDAO;
        }

        public List<BankAccount> findAll() {
            return bankAccountDAO.findAll();
        }

        public BankAccount findById(Integer id) {
            return bankAccountDAO.findById(id);
        }

        public void save(BankAccount bankAccount) {
            bankAccountDAO.save(bankAccount);
        }

        public void update(BankAccount bankAccount) {
            bankAccountDAO.update(bankAccount);
        }

        public void deleteById(Integer id) {
            bankAccountDAO.deleteById(id);
        }
    }
    ```

* xml 配置

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd">

        <!-- 配置 Service -->
        <bean id="bankAccountService" class="cn.parzulpan.service.BankAccountServiceImpl">
            <!-- 注入DAO，set() 形式-->
            <property name="bankAccountDAO" ref="bankAccountDAO"/>
        </bean>

        <!-- 配置 DAO -->
        <bean id="bankAccountDAO" class="cn.parzulpan.dao.BankAccountDAOImpl">
            <!-- 注入QueryRunner，set() 形式-->
            <property name="runner" ref="runner"/>
        </bean>

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

* 对银行账户的业务层接口的实现类进行单元测试

    ```java
    package cn.parzulpan.service;

    import cn.parzulpan.domain.BankAccount;
    import org.junit.Test;
    import org.springframework.context.support.ClassPathXmlApplicationContext;

    import java.util.List;


    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : 对银行账户的业务层接口的实现类进行单元测试
     * 可以看到，每个测试方法都重新获取了一次 Spring 的核心容器，造成了不必要的重复代码，这个问题可以整合 Junit 解决
     */

    public class BankAccountServiceImplTest {

        @Test
        public void findAll() {
            ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
            BankAccountService as = ac.getBean("bankAccountService", BankAccountService.class);
            List<BankAccount> accounts = as.findAll();
            for (BankAccount account : accounts) {
                System.out.println(account);
            }
        }

        @Test
        public void findById() {
            ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
            BankAccountService as = ac.getBean("bankAccountService", BankAccountService.class);
            BankAccount account = as.findById(1);
            System.out.println(account);
        }

        @Test
        public void save() {
            ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
            BankAccountService as = ac.getBean("bankAccountService", BankAccountService.class);
            as.save(new BankAccount(null, "ta", 4325.12314));
        }

        @Test
        public void update() {
            ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
            BankAccountService as = ac.getBean("bankAccountService", BankAccountService.class);
            BankAccount account = as.findById(1);
            account.setMoney(5153.325);
            as.update(account);
        }

        @Test
        public void deleteById() {
            ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
            BankAccountService as = ac.getBean("bankAccountService", BankAccountService.class);
            as.deleteById(4);
        }
    }
    ```

## annotaion 单表 CRUD

### 新注解说明

由于 xml 文件中，依然需要告知 Spring 在创建容器时**要扫描的包**，以及**配置数据源**等。

现在是如何用注解替代这些配置。

**新注解**：

* **`@Configuration`** 指定当前类是一个 Spring 配置类。`value 属性` 用于指定配置类的字节码。
* **`@ComponentScan`** 指定 Spring 在初始化容器时要扫描的包。`basePackages/value 属性` 用于指定要扫描的包。
* **`@Bean`** 该注解**只能写在方法上**，表明使用此方法创建一个对象，并且放入 Spring 容器。`name 属性` 给当前 @Bean 注解方法创建的对象指定一个名称（即 bean 的 id）。
* **`@PropertySource`** 用于加载 .properties 文件中的配置。`value[] 属性` 用于指定 properties 文件位置。如果是在类路径下，需要写上 `classpath`。
* **`@Import`** 用于导入其他配置类，在引入其他配置类时，可以不用再写 @Configuration 注解，但是建议写上。`value[] 属性` 用于指定其他配置类的字节码。

### 整合 Junit

```java
public class AccountServiceTest {
    @Autowired
    private AccountService as;

    @Test
    public void findAllTest() {
        as.findAllAccount();    //  nullPointerExcepetion
    }
}
```

在 Junit 中：

* 应用程序的入口是 `main()`，但是在 Junit 中，没有 main() 也能运行，因为 Junit 集成了一个 main()，该方法会判断当前测试类中哪些方法有 @Test 注解，然后让其执行
* Junit 不会知道是否使用了 SPring 框架，所以也不会在读取配置文件/配置类时创建 Spring 核心容器
* 所以，当测试方法执行时，没有 IOC 容器，就算写了 `@Autowired` 注解，也无法实现注入

但是 Junit 给暴露了一个注解，可以替换掉它的运行器（ 替换 `main()` ）。这时，需要依靠 Spring 框架，因为它提供了一个运行器，可以读取配置文件（或注解）来创建容器。只需要告诉它配置文件在哪就行了。

* `@RunWith` 替换 main 方法
* `@ContextConfiguration` 告知 Spring 的运行其，Spring IOC 创建是基于 xml 还是 注解的，并且说明文字。`locations 属性` 用于指定配置文件的位置。如果是类路径下，需要用 `classpath:` 表明。`classes 属性` 用于指定注解的类。当不使用 xml 配置时，需要用此属性指定注解类的位置。

**需求**：实现银行账户的 CRUD 操作。采用 **annotation** 配置。使用 **Spring IOC** 实现对象的管理，使用 **DBUtils** 和 **C3P0** 作为持久层解决方案。[小节源码](https://github.com/parzulpan/demo/tree/main/Spring/src/SpringAnnotationCRUD)

* 银行账户的持久层接口的实现类，添加注入数据

    ```java
    @Repository("bankAccountDAO")
    public class BankAccountDAOImpl implements BankAccountDAO {
        @Autowired
        private QueryRunner runner;
    }
    ```

* 银行账户的业务层接口的实现类，添加注入数据

    ```java
    @Service("bankAccountService")
    public class BankAccountServiceImpl implements BankAccountService {
        @Autowired
        private BankAccountDAO bankAccountDAO;
    }
    ```

* 连接数据库的配置类

    ```java
    package cn.parzulpan.config;

    import com.mchange.v2.c3p0.ComboPooledDataSource;
    import org.apache.commons.dbutils.QueryRunner;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.PropertySource;
    import org.springframework.context.annotation.Scope;

    import javax.sql.DataSource;
    import java.beans.PropertyVetoException;

    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : 连接数据库的配置类，代替 配置 QueryRunner 和 配置 数据源，并且导入数据库的配置文件
     *
     *     <!-- 配置 QueryRunner -->
     *     <bean id="runner" class="org.apache.commons.dbutils.QueryRunner" scope="prototype">
     *         <!-- 注入数据源，构造函数形式-->
     *         <constructor-arg name="ds" ref="dataSource"/>
     *     </bean>
     *
     *     <!-- 配置 数据源 -->
     *     <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
     *         <property name="driverClass" value="com.mysql.jdbc.Driver"/>
     *         <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/springT?useSSL=false"/>
     *         <property name="user" value="root"/>
     *         <property name="password" value="root"/>
     *     </bean>
     *
     */

    @Configuration
    @PropertySource("classpath:jdbc.properties")
    public class JdbcConfig {
        @Value("${jdbc.driver}")
        private String driverClass;

        @Value("${jdbc.url}")
        private String jdbcUrl;

        @Value("${jdbc.username}")
        private String user;

        @Value("${jdbc.password}")
        private String password;

        /**
         * 创建 QueryRunner，使用 数据源 1
         * @param dataSource
         * @return
         */
        @Bean(name = "runner")
        @Scope("prototype")
        public QueryRunner createQueryRunner(@Qualifier("dataSource1") DataSource dataSource) {
            return new QueryRunner(dataSource);
        }

        /**
         * 创建 数据源 1
         * @return
         */
        @Bean(name = "dataSource1")
        public DataSource createDataSource1() {
            try {
                ComboPooledDataSource ds = new ComboPooledDataSource();
                ds.setDriverClass(driverClass);
                ds.setJdbcUrl(jdbcUrl);
                ds.setUser(user);
                ds.setPassword(password);
                return ds;
            } catch (PropertyVetoException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 创建 数据源 2
         * @return
         */
        @Bean(name = "dataSource2")
        public DataSource createDataSource2() {
            try {
                ComboPooledDataSource ds2 = new ComboPooledDataSource();
                ds2.setDriverClass(driverClass);
                ds2.setJdbcUrl(jdbcUrl);
                ds2.setUser(user);
                ds2.setPassword(password);
                return ds2;
            } catch (PropertyVetoException e) {
                throw new RuntimeException(e);
            }
        }
    }
    ```

* Spring 的配置类

    ```java
    package cn.parzulpan.config;

    import org.springframework.context.annotation.ComponentScan;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Import;
    import org.springframework.context.annotation.PropertySource;

    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : Spring 的配置类，作用和 bean.xml 相同，并且导入连接数据库的配置类
     *
     *     <!-- 告知 Spring 在创建容器时要扫描的包 -->
     *     <context:component-scan base-package="cn.parzulpan"/>
     *
     */

    @Configuration
    @ComponentScan("cn.parzulpan")
    @Import(JdbcConfig.class)
    public class SpringConfiguration {
    }
    ```

* 对银行账户的业务层接口的实现类进行单元测试，整合 Junit 解决重复代码问题，使用注解

    ```java
    package cn.parzulpan.service;

    import cn.parzulpan.config.SpringConfiguration;
    import cn.parzulpan.domain.BankAccount;
    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.AnnotationConfigApplicationContext;
    import org.springframework.test.context.ContextConfiguration;
    import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

    import java.util.List;


    /**
     * @Author : parzulpan
     * @Time : 2020-12
     * @Desc : 对银行账户的业务层接口的实现类进行单元测试，整合 Junit 解决重复代码问题，使用注解
     */

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(classes = SpringConfiguration.class)
    public class BankAccountServiceImplAndJunitTest {
        @Autowired
        private BankAccountService as;

        @Test
        public void findAll() {
            // 通过注解获取容器
            AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfiguration.class);
            BankAccountService as = ac.getBean("bankAccountService", BankAccountService.class);
            List<BankAccount> accounts = as.findAll();
            for (BankAccount account : accounts) {
                System.out.println(account);
            }
        }

        @Test
        public void findById() {
            // 整合 Junit
            BankAccount account = as.findById(1);
            System.out.println(account);
        }

        @Test
        public void save() {
            // 整合 Junit
            as.save(new BankAccount(null, "ta", 4325.12314));
        }

        @Test
        public void update() {
            // 整合 Junit
            BankAccount account = as.findById(1);
            account.setMoney(5153.325);
            as.update(account);
        }

        @Test
        public void deleteById() {
            // 整合 Junit
            as.deleteById(4);
        }
    }
    ```

可以看到，纯注解开发实际上也比较繁琐，所以推荐混合开发。XML 配置 适用于 Bean 来自第三方实现，而 注解配置 适用于 Bean 来自自己实现。

## 练习和总结
