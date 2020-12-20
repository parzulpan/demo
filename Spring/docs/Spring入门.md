# Spring 入门

[文章源码](https://github.com/parzulpan/demo/tree/main/Spring/src/SpringBase)

## Spring 概述

### Spring

Spring 是分层的 Java SE/EE 应用全栈式轻量级开源框架，以 **IOC**（Inverse Of Control，反转控制）和 **AOP**（Aspect Oriented Programming，面向切面编程）为**内核**，提供了 表现层 Spring MVC 和 持久层 Spring JDBC 以及 业务层事务管理等众多技术。而且可以方便的整合其他开源框架和类库。

### Spring 优势

* **方便解耦，简化开发**：通过 IOC 容器可以将对象间的依赖关系交由 Spring 进行控制，可以避免过度的程序耦合。而且也不需要再为单例模式、属性解析等底层需求编写代码。
* **面向切面**：通过 AOP，可以方便进行面向切面的编程，弥补面向对象的一切缺陷。
* **声明式事务**：通过 声明式方式灵活的进行事务管理，可以提高开发效率和质量。
* **源码学习典范**：Spring 的源代码设计精妙、结构清晰、匠心独用，处处体现着对 Java 设计模式灵活运用以及对 Java 技术的高深造诣。它的源代码是 Java 技术的最佳实践的范例。

### Spring 体系结构

![Spring 体系结构](https://images.cnblogs.com/cnblogs_com/parzulpan/1903586/o_201220070050spring-overview.png)

## 程序的耦合和解耦

耦合性是对模块间关联程度的度量。耦合的强弱取决于模块间接口的复杂性、调用模块的方式以及通过界面传送数据的多少。

**耦合的分类**：

* **内容耦合**：当一个模块直接修改或操作另一个模块的数据时，或一个模块不通过正常入口而转入另一个模块时。内容耦合是**最高程度**的耦合，应该避免使用。
* **公共耦合**：两个或两个以上的模块共同引用一个全局数据项，这种耦合被称为公共耦合。在具有大量公共耦合的结构中，确定究竟是哪个模块给全局变量赋了一个特定的值是十分困难的。
* **外部耦合**：一组模块都访问同一全局简单变量而不是同一全局数据结构，而且不是通过参数表传递该全局变量的信息，则称之为外部耦合。
* **控制耦合**：一个模块通过接口向另一个模块传递一个控制信号，接受信号的模块根据信号值而进行适当的动作，这种耦合被称为控制耦合。
* **标记耦合**：若一个模块 A 通过接口向两个模块 B 和 C 传递一个公共参数，那么称模块 B 和 C 之间存在一个标记耦合。
* **数据耦合**：模块之间通过参数来传递数据，那么被称为数据耦合。数据耦合是**最低**的一种耦合形式，系统中一般都存在这种类型的耦合，因为为了完成一些有意义的功能，往往需要将某些模块的输出数据作为另一些模块的输入数据。
* **非直接耦合**：两个模块之间没有直接关系，它们之间的联系完全是通过主模块的控制和调用来实现的。

总结起来，就是如果模块间必须存在耦合，就尽量使用数据耦合，少用控制耦合，限制公共耦合的范围，尽量避免使用内容耦合。

### 程序耦合举例

* AccountDAOImpl.java

    ```java
    package cn.parzulpan.dao;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 账户持久层接口的实现类
    */

    public class AccountDAOImpl implements AccountDAO{
        /**
        * 模拟保存账户
        */
        public void saveAccount() {
            System.out.println("保存了账户...");
        }
    }

    ```

* AccountServiceImpl.java

    ```java
    package cn.parzulpan.service;

    import cn.parzulpan.dao.AccountDAO;
    import cn.parzulpan.dao.AccountDAOImpl;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 账户业务层接口的实现类
    */

    public class AccountServiceImpl implements AccountService{
        private AccountDAO accountDAO = new AccountDAOImpl();   // 这里发生了耦合

        /**
        * 模拟保存账户
        */
        public void saveAccount() {
            accountDAO.saveAccount();
        }
    }

    ```

* Client.java

    ```java
    package cn.parzulpan.ui;

    import cn.parzulpan.service.AccountServiceImpl;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 模拟一个表现层，用于调用业务层，实际开发中应该是一个 Servlet 等
    */

    public class Client {
        public static void main(String[] args) {
            AccountServiceImpl accountService = new AccountServiceImpl();   // 这里发生了耦合
            accountService.saveAccount();
        }
    }

    ```

### Factory 解耦

在实际开发中可以把**三层的对象**都使用配置文件配置起来，当启动服务器应用加载的时候，让一个类中的方法通过**读取配置文件**，把这些对象创建出来并且**存起来（用容器存储）**。在接下来的使用的时候，直接拿过来用就行。

那么，这个读取配置文件，创建和获取三层对象的类就是**工厂类**。即两个步骤：

* 通过读取配置文件来获取创建对象的全限定类名。
* 使用反射来创建对象，避免使用 new 关键字。

工厂就是负责给从容器中获取指定对象的类，这时候获取对象的方式发生了改变。之前，在获取对象时，**采用 new 的方式**，**是主动的**。现在，在获取对象时，**采用跟工厂要的方式**，工厂会查找或者创建对象，**是被动的**。

**之前**：

![new](https://images.cnblogs.com/cnblogs_com/parzulpan/1903586/o_201220093621new.png)

**现在**：

![factory](https://images.cnblogs.com/cnblogs_com/parzulpan/1903586/o_201220093629factory.png)

* bean.properties

    ```properties
    accountService=cn.parzulpan.service.AccountServiceImpl
    accountDAO = cn.parzulpan.dao.AccountDAOImpl
    ```

* BeanFactory.java

    ```java
    package cn.parzulpan.factory;

    import java.io.InputStream;
    import java.util.*;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 工厂类，负责给从容器中获取指定对象的类
    */

    public class BeanFactory {
        private static Properties properties;

        private static Map<String, Object> beans;   // Factory 解耦的优化，存放创建的对象，称为容器

        static {
            try {
                // 实例化对象
                properties = new Properties();
                // 获取文件流对象，使用类加载器
                InputStream is = BeanFactory.class.getClassLoader().getResourceAsStream("bean.properties");
                properties.load(is);

                beans = new HashMap<>();
                Enumeration<Object> keys = properties.keys();
                while (keys.hasMoreElements()) {
                    String key = keys.nextElement().toString();
                    String beanPath = properties.getProperty(key);
                    Object instance = Class.forName(beanPath).newInstance();
                    beans.put(key, instance);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new ExceptionInInitializerError("初始化 Properties 失败！");
            }
        }

        /**
        * 获取指定对象的类
        * @param beanName
        * @return
        */
        public static Object getBean(String beanName){
            try {
    //            return Class.forName(properties.getProperty(beanName)).newInstance(); // 两个步骤
                System.out.println(beanName + " " + beans.get(beanName));
                return beans.get(beanName); // 两个步骤
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    ```

* AccountServiceImpl.java

    ```java
    package cn.parzulpan.service;

    import cn.parzulpan.dao.AccountDAO;
    import cn.parzulpan.dao.AccountDAOImpl;
    import cn.parzulpan.factory.BeanFactory;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 账户业务层接口的实现类
    */

    public class AccountServiceImpl implements AccountService{
    //    private AccountDAO accountDAO = new AccountDAOImpl();   // 这里发生了耦合

        /**
        * 模拟保存账户
        */
        public void saveAccount() {
            AccountDAO accountDAO = (AccountDAO) BeanFactory.getBean("accountDAO"); // 通过 Factory 解耦
            if (accountDAO != null) {
                accountDAO.saveAccount();
            }
        }
    }

    ```

* Client.java

    ```java
    package cn.parzulpan.ui;

    import cn.parzulpan.factory.BeanFactory;
    import cn.parzulpan.service.AccountService;
    import cn.parzulpan.service.AccountServiceImpl;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 模拟一个表现层，用于调用业务层，实际开发中应该是一个 Servlet 等
    */

    public class Client {
        public static void main(String[] args) {
    //        AccountServiceImpl accountService = new AccountServiceImpl();   // 这里发生了耦合

            AccountService accountService = (AccountService) BeanFactory.getBean("accountService"); // 通过 Factory 解耦

            if (accountService != null) {
                accountService.saveAccount();
            }

            // 通过 Factory 解耦存在的问题
            for (int i = 0; i < 5; ++i) {
                System.out.println(BeanFactory.getBean("accountService"));  // 对象被创建多次
            }
        }
    }

    ```

### IOC 解耦

* bean.xml

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd">

        <!-- 把对象的创建交给 Spring 来管理-->
        <bean id="accountService" class="cn.parzulpan.service.AccountServiceImpl"/>
        <bean id="accountDAO" class="cn.parzulpan.dao.AccountDAOImpl"/>

    </beans>
    ```

* ClientIOC.java

    ```java
    package cn.parzulpan.ui;

    import cn.parzulpan.dao.AccountDAO;
    import cn.parzulpan.service.AccountService;
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.support.ClassPathXmlApplicationContext;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 使用 IOC
    */

    public class ClientIOC {
        public static void main(String[] args) {
            // 使用 ApplicationContext 接口，获取 Spring 核心容器
            ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
            // 根据 id 获取 Bean 对象
            AccountService as = ac.getBean("accountService", AccountService.class);
            System.out.println(as);
            AccountDAO ad = ac.getBean("accountDAO", AccountDAO.class);
            System.out.println(ad);
        }

    }
    ```

## IOC

**IOC**（Inverse Of Control，反转控制），把创建对象的权利交给 Spring 框架，它包括 **DI**（Dependency Injection，依赖注入）和 **DL**（Dependency Lookup，依赖查找）。

简单的说，IOC 是一种以被动接收的方式获取对象的思想，它主要是为了**降低程序的耦合**。

### bean 标签

* **作用**：用于配置对象让 Spring 来创建。**默认情况下**它调用的是类中的无参构造函数，如果没有无参构造函数则不能创建成功。
* **属性**：
  * `id`：给对象在容器中提供一个唯一标识，用于**获取对象**。
  * `class`：指定类的全限定类名，用于**反射创建对象**，默认情况下调用无参构造函数。
  * `scope`：指定对象的作用范围
    * `singleton` 默认值，单例的
    * `prototype` 多例的
    * `request` WEB 项目中，Spring 创建一个 Bean 的对象，将对象存入到 request 域中
    * `session` WEB 项目中，Spring 创建一个 Bean 的对象，将对象存入到 session 域中
    * `global session` WEB 项目中，应用在集群环境，如果没有集群环境那么 globalSession 相当于 session
  * `init-method`：指定类中的初始化方法名称。
  * `destroy-method`：指定类中销毁方法名称。

### bean 的三种创建方式

**第一种方式：使用默认无参构造函数**。它会根据默认无参构造函数来创建类对象。如果 bean 中没有默认无参构造函数，将会创建失败。

```xml
    <bean id="accountServiceIOC" class="cn.parzulpan.service.AccountServiceImplIOC"/>
    <bean id="accountDAOIOC" class="cn.parzulpan.dao.AccountDAOImplIOC"/>
```

**第二种方式：使用实例工厂的方法创建对象**。先把工厂的创建交给 Spring 来管理，然后在使用工厂的 bean 来调用里面的方法。

* factory-bean 属性：用于指定实例工厂 bean 的 id
* factory-method 属性：用于指定实例工厂中创建对象的方法

```java
package cn.parzulpan.factory;

import cn.parzulpan.service.AccountService;
import cn.parzulpan.service.AccountServiceImplIOC;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : Spring 管理实例工厂。模拟一个工厂类，该类可能存在于 jar 包中，无法通过修改源码来提供默认构造函数
 */

public class InstanceFactory {
    public AccountService getAccountService() {
        return new AccountServiceImplIOC();
    }
}
```

```xml
    <bean id="instanceFactory" class="cn.parzulpan.factory.InstanceFactory"/>
    <bean id="accountServiceIOC" factory-bean="instanceFactory" factory-method="getAccountService"/>
    <bean id="accountDAOIOC" class="cn.parzulpan.dao.AccountDAOImplIOC"/>
```

**第三种方式：使用静态工厂的方法创建对象**。使用某个类中的静态方法创建对象，并存入 Spring 核心容器。

* id 属性：指定 bean 的 id，用于从容器中获取
* class 属性：指定静态工厂的全限定类名
* factory-method 属性：指定生产对象的静态方法

```java
package cn.parzulpan.factory;

import cn.parzulpan.service.AccountService;
import cn.parzulpan.service.AccountServiceImplIOC;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : Spring 管理静态工厂。模拟一个工厂类，该类可能存在于 jar 包中，无法通过修改源码来提供默认构造函数
 */

public class StaticFactory {
    public static AccountService getAccountService() {
        return new AccountServiceImplIOC();
    }
}

```

```xml
    <bean id="accountServiceIOC" class="cn.parzulpan.factory.StaticFactory" factory-method="getAccountService"/>
    <bean id="accountDAOIOC" class="cn.parzulpan.dao.AccountDAOImplIOC"/>
```

测试 ClientIOC.java：

```java
public class ClientIOC {
    public static void main(String[] args) {
        // 使用 ApplicationContext 接口，获取 Spring 核心容器
        ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");

        System.out.println("------");

        //
        AccountService asi = ac.getBean("accountServiceIOC", AccountService.class);
        System.out.println(asi);
        AccountDAO adi = ac.getBean("accountDAOIOC", AccountDAO.class);
        System.out.println(adi);
        adi.saveAccount();

    }

}
```

### bean 的作用范围和生命周期

**对于单例对象**：`scope="singleton"`

一个应用只有一个对象的实例，它的作用范围就是整个引用。

**生命周期**：

* 对象出生：当应用加载，创建容器时，对象就被创建了。
* 对象活着：只要容器在，对象一直活着。
* 对象死亡：当应用卸载，销毁容器时，对象就被销毁了。

```xml
    <!-- bean 的作用范围和生命周期 -->
    <bean id="accountServiceIOC" class="cn.parzulpan.service.AccountServiceImplIOC" scope="singleton"
          init-method="init" destroy-method="destroy"/>
    <bean id="accountDAOIOC" class="cn.parzulpan.dao.AccountDAOImplIOC" scope="singleton"
          init-method="init" destroy-method="destroy"/>
```

**对于多例对象**：`scope="prototype"`

每次访问对象时，都会重新创建对象实例。

**生命周期**：

* 对象出生：当使用对象时，创建新的对象实例。
* 对象活着：只要对象在使用中，就一直活着。
* 对象死亡：当对象长时间不用时，被 java 的垃圾回收器回收了。

```java
    <bean id="accountServiceIOC" class="cn.parzulpan.service.AccountServiceImplIOC" scope="prototype"
          init-method="init" destroy-method="destroy"/>
    <bean id="accountDAOIOC" class="cn.parzulpan.dao.AccountDAOImplIOC" scope="prototype"
          init-method="init" destroy-method="destroy"/>
```

**测试**：

```java
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        AccountService asi = ac.getBean("accountServiceIOC", AccountService.class);
        System.out.println(asi);
        AccountDAO adi = ac.getBean("accountDAOIOC", AccountDAO.class);
        System.out.println(adi);
        adi.saveAccount();

        ac.close(); // 手动关闭容器
```

## DI

**DI**（Dependency Injection，依赖注入），它是 Spring IOC 的具体实现。因为 IOC 作用是降低耦合，那么依赖关系的维护都交给了 Spring，依赖关系的维护就称之为依赖注入。

能依赖注入的数据，有三类：

* 基本数据类型和 String
* 其他 Bean 类型，在配置文件中或者其他注解配置过的 Bean
* 集合类型

依赖注入的方法，有三种：

* 使用构造函数注入
* 使用 set 方法注入
* 使用注解注入

### 使用构造函数注入

类中需要提供一个对应参数列表的构造函数。

属性：

* index 指定参数在构造函数参数列表的索引位置
* type 指定参数在构造函数中的数据类型
* name 指定参数在构造函数中的名称
* value 它能赋的值是基本数据类型和 String 类型
* ref 它能赋的值是其他 bean 类型，也就是说，必须得是在配置文件中配置过的 bean
* 前三个都是找给谁赋值，后两个指的是赋什么值的

```xml
    <!-- 构造函数注入
         类中需要提供一个对应参数列表的构造函数
         属性：
            index 指定参数在构造函数参数列表的索引位置
            type 指定参数在构造函数中的数据类型
            name 指定参数在构造函数中的名称
            value 它能赋的值是基本数据类型和 String 类型
            ref 它能赋的值是其他 bean 类型，也就是说，必须得是在配置文件中配置过的 bean
            前三个都是找给谁赋值，后两个指的是赋什么值的
    -->
    <bean id="accountServiceDI" class="cn.parzulpan.service.AccountServiceImplDI">
        <constructor-arg name="name" value="parzulpan"/>
        <constructor-arg name="age" value="100"/>
        <constructor-arg name="birthday" ref="now"/>
    </bean>
    <bean id="now" class="java.util.Date"/>
```

ClientDI.java

```java
package cn.parzulpan.ui;

import cn.parzulpan.service.AccountService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public class ClientDI {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        AccountService asi = ac.getBean("accountServiceDI", AccountService.class);
        System.out.println(asi);
        asi.saveAccount();  // call saveAccount() parzulpan 100 Sun Dec 20 19:27:49 CST 2020
    }
}

```

这种注入方式的**优点**：在获取 bean 对象时，注入数据是必须的操作，否则无法创建成功。

**缺点**：改变了 bean 对象的实例化方式，在创建对象时，如果用不到这些属性，也必须提供。

### 使用 set 方法注入

类中需要提供属性的 set 方法。

属性：

* name：找的是类中 set 方法后面的部分
* ref：给属性赋值是其他 bean 类型的
* value：给属性赋值是基本数据类型和 string 类型的

```xml
    <!-- set 方法 注入
         类中需要提供属性的 set 方法
         属性：
            name：找的是类中 set 方法后面的部分
            ref：给属性赋值是其他 bean 类型的
            value：给属性赋值是基本数据类型和 string 类型的
    -->
    <bean id="accountServiceDI2" class="cn.parzulpan.service.AccountServiceImplDI2">
        <property name="name" value="库里"/>
        <property name="age" value="30"/>
        <property name="birthday" ref="nowSet"/>
    </bean>
    <bean id="nowSet" class="java.util.Date"/>
```

ClientDI.java

```java
        AccountService asi2 = ac.getBean("accountServiceDI2", AccountService.class);
        System.out.println(asi2);
        asi2.saveAccount();  // call saveAccount() 库里 30 Sun Dec 20 20:11:01 CST 2020
```

这种注入方式的**优点**：创建对象时没有明确的限制，可以直接使用默认构造函数。

**缺点**：如果某个成员必须有值，则 set 方法无法保证一定执行。

但是，set 方式是更常用的方式。

### 注入集合属性

注入集合属性，在注入集合数据时，只要结构相同，标签可以互换。

List 结构的：array, list, set

Map 结构的：map, entry, props, prop

```xml
    <!-- 注入集合属性
         在注入集合数据时，只要结构相同，标签可以互换
         List 结构的：array, list, set
         Map 结构的：map, entry, props, prop
    -->
    <bean id="accountServiceDI3" class="cn.parzulpan.service.AccountServiceImplDI3">
        <property name="myStr">
            <set>
                <value>AAA</value>
                <value>BBB</value>
                <value>CCC</value>
            </set>
        </property>
        <property name="myList">
            <list>
                <value>AAA</value>
                <value>BBB</value>
                <value>CCC</value>
            </list>
        </property>
        <property name="mySet">
            <set>
                <value>AAA</value>
                <value>BBB</value>
                <value>CCC</value>
            </set>
        </property>
        <property name="myMap">
            <map>
                <entry key="testA" value="aaa"/>
                <entry key="testB" value="bbb"/>
            </map>
        </property>
        <property name="myProps">
            <props>
                <prop key="testA">aaa</prop>
                <prop key="testB">bbb</prop>
            </props>
        </property>
    </bean>
```

## 练习和总结

---

**ApplicationContext 接口的三个实现类？**

* `ClassPathXmlApplicationContext` 它是从类的根路径下加载配置文件，推荐使用这种
* `FileSystemXmlApplicationContext` 它是从磁盘路径上加载配置文件，配置文件可以在磁盘的任意位置，不推荐使用这种
* `AnnotationConfigApplicationContext` 使用注解配置容器对象时，需要使用此类来创建 Spring 核心容器，它用来读取注解

---

**BeanFactory 和 ApplicationContext 的区别？**

* `BeanFactory` 是 Spring 核心容器中的顶层接口
* `ApplicationContext` 是 BeanFactory 的子接口
* **它们两者创建对象的时间点不一样**：
  * `ApplicationContext` 立即加载，只要读取了配置文件，默认情况下就会创建对象，**适用于单例对象**，推荐使用这种
  * `BeanFactory` 延迟加载，什么时候使用什么时候创建对象，**适用于多例对象**

---
