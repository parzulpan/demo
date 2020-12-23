# SpringMVC 入门

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringMVC/src/SpringMVCStart)

## SpringMVC 基本概念

在 JavaEE 开发中，几乎全都是基于 B/S 架构的开发。在 B/S 架构中，系统标准的三层架构包括：表现层、业务层、持久层。

* **表现层**：负责接收客户端请求，向客户端响应结果等，一般使用 MVC 模型
  * **Model 模型**：指数据模型，用于封装数据
  * **View 视图**：指 jsp 或 html，用于展示数据
  * **Controller 控制器**：指和用户交互的部分，用于处理程序逻辑
* **业务层**：负责处理业务逻辑，调用持久层进行数据持久化等
* **持久层**：负责数据持久化等

![B/S 三层架构](https://images.cnblogs.com/cnblogs_com/parzulpan/1905354/o_201223070156BS%E4%B8%89%E5%B1%82%E6%9E%B6%E6%9E%84.png)

### SpringMVC

SpringMVC 是一种基于 Java 的实现 MVC 设计模型的请求驱动类型的轻量级 Web 框架，属于 Spring FrameWork 的后续产品，已经融合在 Spring Web Flow 里面。

Spring 框架提供了构建 Web 应用程序的全功能 MVC 模块，使用 Spring 可插入的 MVC 架构，从而在使用 Spring 进行 WEB 开发时，可以选择使用 Spring 的 Spring MVC 框架或集成其他 MVC 开发框架。

**SpringMVC 的优势**：

* 清晰的角色划分
  * 前端控制器（DispatcherServlet）
  * 处理器映射器（HandlerMapping）
  * 处理器适配器（HandlerAdapter）
  * 视图解析器（ViewResolver）
  * 页面控制器（Controller）
  * 验证器（Validator）
  * 命令对象（CommandObject 请求参数绑定到的对象）
  * 表单对象（FormObject 提供给表单展示和提交的对象）
* 扩展灵活
* 可适配性，通过 HandlerAdapter 可以支持任意的类作为处理器
* 可定制性，通过 HandlerMapping、ViewResolver 可以非常简单的定制
* 强大的 JSP 标签库，使 JSP 编写更容易
* RESTful 风格的支持等

**SpringMVC 和 Struts2 的对比**：

* **相同点**：
  * 都是表现层框架，都是基于 MVC 模型
  * 底层实现都离不开原始的 Servlet API
  * 处理请求的机制都是一个核心控制器
* **不同点**：
  * SpringMVC 的入口是 Servlet，而 Struts2 是 Filter
  * SpringMVC 是基于方法的，而 Struts2 是基于类的，所以 SpringMVC 会比 Struts2 稍快
  * SpringMVC 使用更加简洁，处理 AJAX 请求更方便

## SpringMVC 入门案例

需求：点击 index.jsp 页面的超链接，跳转到另一个页面并输出内容到控制台。

### 第一步：开发环境搭建

* 使用 IDEA 新建一个 Maven 工程，选择 `maven-archetype-webapp`
* 编辑 pom.xml，导入依赖

    <details>
    <summary>查看 pom.xml</summary>

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>cn.parzulpan</groupId>
    <artifactId>SpringMVCStart</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <name>SpringMVCStart Maven Webapp</name>
    <!-- FIXME change it to the project's website -->
    <url>http://www.example.com</url>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <spring.version>5.1.20.RELEASE</spring.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
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
    </dependencies>

    <build>
        <finalName>SpringMVCStart</finalName>
        <pluginManagement><!-- lock down plugins versions to avoid using Maven defaults (may be moved to parent pom) -->
        <plugins>
            <plugin>
            <artifactId>maven-clean-plugin</artifactId>
            <version>3.1.0</version>
            </plugin>
            <!-- see http://maven.apache.org/ref/current/maven-core/default-bindings.html#Plugin_bindings_for_war_packaging -->
            <plugin>
            <artifactId>maven-resources-plugin</artifactId>
            <version>3.0.2</version>
            </plugin>
            <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.0</version>
            </plugin>
            <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.22.1</version>
            </plugin>
            <plugin>
            <artifactId>maven-war-plugin</artifactId>
            <version>3.2.2</version>
            </plugin>
            <plugin>
            <artifactId>maven-install-plugin</artifactId>
            <version>2.5.2</version>
            </plugin>
            <plugin>
            <artifactId>maven-deploy-plugin</artifactId>
            <version>2.8.2</version>
            </plugin>
        </plugins>
        </pluginManagement>
    </build>
    </project>

    ```

    </details>

* 选中项目，点击右键，选择 `Add FrameWorks Support`，添加上 SpringMVC 支持
* 配置 Tomcat，推荐版本 8 以上

### 第二步：编写程序

* 编写 WEB-INF 目录下的 web.xml，配置一个 Servlet

    <details>
    <summary>查看 web.xml</summary>

    ```xml
    <!DOCTYPE web-app PUBLIC
    "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
    "http://java.sun.com/dtd/web-app_2_3.dtd" >

    <web-app>
    <display-name>Archetype Created Web Application</display-name>
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/applicationContext.xml</param-value>
    </context-param>

    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <!-- 配置 app 分派器（前端控制器） -->
    <servlet>
        <servlet-name>app</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!-- 配置初始化参数，读取 SpringMVC 的配置文件 -->
        <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/app-servlet.xml</param-value>
        </init-param>
        <!-- 配置 Servlet 对象的创建时间点为应用加载时创建，取值只能为非零整数，表示启动顺序 -->
        <load-on-startup>1</load-on-startup>
    </servlet>

    <!-- 配置映射，同 Servlet 一样 -->
    <servlet-mapping>
        <servlet-name>app</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    </web-app>
    ```

    <details>

* 由于已经添加了 SpringMVC 支持，编写 WEB-INF 目录下 的 app-servlet.xml，即 SpringMVC 的配置文件

    <details>
    <summary>查看 app-servlet.xml</summary>

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:mvc="http://www.springframework.org/schema/mvc"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
                http://www.springframework.org/schema/beans
                http://www.springframework.org/schema/beans/spring-beans.xsd
                http://www.springframework.org/schema/mvc
                http://www.springframework.org/schema/mvc/spring-mvc.xsd
                http://www.springframework.org/schema/context
                http://www.springframework.org/schema/context/spring-context.xsd">

        <!-- 此文件负责整个 Spring MVC 的配置 -->

        <!-- 配置 Spring 容器要扫描的包 -->
        <context:component-scan base-package="cn.parzulpan"/>

        <!-- 配置 视图解析器 -->
        <bean id="defaultViewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
            <property name="prefix" value="/WEB-INF/views/"/>
            <property name="suffix" value=".jsp"/>
        </bean>

        <!-- 开启 Spring MVC 注解支持 -->
        <mvc:annotation-driven/>

    </beans>
    ```

    </details>

* 编写控制器并使用注解配置，StartController.java

    <details>
    <summary>查看 StartController.java</summary>

    ```java
    package cn.parzulpan.web.controller;

    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.RequestMapping;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 控制器，由于返回了结果，视图解析器会进行解析匹配，所以需要有对应的响应页面
    */

    @Controller
    @RequestMapping(path = "/say")
    public class StartController {

        @RequestMapping(path = "/hello")
        public String sayHello() {
            System.out.println("SpringMVC Hello");
            return "hello";
        }

        @RequestMapping(path = "/world")
        public String sayWorld() {
            System.out.println("SpringMVC World");
            return "world";
        }
    }
    ```

    </details>

* 编写对应响应页面

    <details>
    <summary>查看 响应页面</summary>

    ```jsp
    <!-- hello.jsp -->

    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>
    <head>
        <title>Title</title>
    </head>
    <body>
        <h3> SpringMVC Hello !!! </h3>
    </body>
    </html>
    ```

    ```jsp
    <!-- world.jsp -->

    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>
    <head>
        <title>Title</title>
    </head>
    <body>
        <h3> SpringMVC World !!! </h3>
    </body>
    </html>
    ```

    </details>

* 编写测试文件，index.jsp

    <details>
    <summary>查看 index.jsp</summary>

    ```jsp
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>
    <head>
        <title>Title</title>
    </head>
    <body>

        <h3> SpringMVC Start</h3>
        <a href="say/hello">Say Hello</a>
        <a href="say/world">Say World</a>

    </body>
    </html>

    ```

    </details>

**`@RequestMapping` 注解说明**：

* 源码

    ```java
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Mapping
    public @interface RequestMapping {
    ```

* 它的作用是于建立请求 URL 和处理请求方法之间的对应关系。
* 出现在类上，表示请求 URL 的第一级访问目录。此处不写的话，就相当于应用的根目录。写的话需要以 / 开头。出现在方法上，表示请求 URL 的第二级访问目录。
* `value / path 属性` 用于指定请求的 URL
* `method 属性` 用于指定请求的方式
* `params 属性` 用于指定限制请求参数的条件。它支持简单的表达式。要求请求参数的 key 和 value 必须和配置的一模一样。
* `headers 属性` 用于指定限制请求消息头的条件。

## 入门案例原理分析

**组件说明**：

* **前端控制器（DispatcherServlet）** 用户请求到达前端控制器，它就相当于 MVC 模型 中的 C，dispatcherServlet 是整个流程控制的中心，由它调用其它组件处理用户的请求，dispatcherServlet 的存在降低了组件之间的耦合性。
* **处理器映射器（HandlerMapping）** 它负责根据用户请求找到 Handler 处理器，SpringMVC 提供了不同的映射器实现不同的映射方式，例如：配置文件方式，实现接口方式，注解方式等。
* **页面控制器（Controller）** 开发中要编写的具体业务控制器。
* **处理器适配器（HandlerAdapter）** 对处理器进行执行，这是适配器模式的应用，通过扩展适配器可以对更多类型的处理器进行执行。
* **视图解析器（ViewResolver）** 负责将处理结果生成 View 视图，View Resolver 首先根据逻辑视图名解析成物理视图名即具体的页面地址，再生成 View 视图对象，最后对 View 进行渲染将处理结果通过页面展示给用户。
* **视图（View）** SpringMVC 框架提供了很多的 View 视图类型的支持，最常用的视图就是 jsp。一般情况下需要通过页面标签或页面模版技术将模型数据通过页面展示给用户。

其中，处理器映射器、处理器适配器、视图解析器称为 SpringMVC 的三大组件。

在 SpringMVC 配置文件中，使用 `<mvc:annotation-driven>` 可以自动加载 处理映射器 和 处理器适配器。

![SpringMVCServlet流程](https://images.cnblogs.com/cnblogs_com/parzulpan/1905354/o_201223131544SpringMVCServlet%E6%B5%81%E7%A8%8B.png)

## 请求参数的绑定

### 绑定说明

**绑定机制：**

* 表单提交的数据都是 `k=v` 格式的，比如 `username=haha&password=123`
* SpringMVC 的参数绑定过程是把表单提交的**请求参数**，作为**控制器中方法的参数**进行绑定的
* 要求提交表单的 name 和参数的名称是相同的

**支持的数据类型：**

* 基本数据类型和字符串类型
* 实体类型（JavaBean）
* 集合数据类型（List、Map等）

### 使用要求

**基本数据类型和字符串类型：**

* 提交表单的 **name** 和参数的**名称**是相同的
* **严格区分大小写**

**实体类型（JavaBean）：**

* 提交表单的 **name** 和 JavaBean 中的**属性名称**需要一致
* 如果一个 JavaBean 类中包含其他的**引用类型**，那么表单的 **name** 属性需要编写成 `对象.属性`，例如 `address.name`

**集合数据类型（List、Map等）：**

* JSP页面编写方式：`list[0].属性`，`map['one'].属性`

### 使用示例

账户实体类 Account.java

<details>
<summary>查看 Account.java</summary>

```java
public class Account implements Serializable {
    private Integer id;
    private String name;
    private Float money;
    private Address address;
    // getter setter toString
}
```

</details>

账户地址实体类 Address.java

<details>
<summary>查看 Address.java</summary>

```java
public class Address implements Serializable {
    private String provinceName;
    private String cityName;
    // getter setter toString
}
```

</details>

用户实体类 User.java

<details>
<summary>查看 User.java</summary>

```java
public class User implements Serializable {
    private String username;
    private String password;
    private Integer age;
    private List<Account> accounts;
    private Map<String, Account> accountMap;
    // getter setter toString
}
```

</details>

请求参数绑定的控制器 ParamsController.java

<details>
<summary>查看 ParamsController.java</summary>

```java
@Controller
@RequestMapping(path = "/params")
public class ParamsController {

    /**
     * 基本数据类型和字符串类型
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/stringAndIntegerParams")
    public String stringAndIntegerParams(String username, Integer password) {
        System.out.println(username);
        System.out.println(password);
        return "hello";
    }

    /**
     * 实体类型（JavaBean）
     * @param account
     * @return
     */
    @RequestMapping("/javaBeanParams")
    public String javaBeanParams(Account account) {
        System.out.println(account);
        return "hello";
    }

    /**
     * 集合数据类型（List、Map等）
     * @param user
     * @return
     */
    @RequestMapping("/collectionParams")
    public String collectionParams(User user) {
        System.out.println(user);
        return "hello";
    }
}
```

</details>

请求参数的绑定测试：params.jsp

<details>
<summary>查看 app-servlet.xml</summary>

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>请求参数的绑定</title>
</head>
<body>
    <a href="params/stringAndIntegerParams?username=parzulpan啊哈哈&password=1024">基本类型和 String 类型作为参数</a>
    <br>
    <form action="params/javaBeanParams" method="post">
        账户名称：<input type="text" name="name" ><br/>
        账户金额：<input type="text" name="money" ><br/>
        账户省份：<input type="text" name="address.provinceName" ><br/>
        账户城市：<input type="text" name="address.cityName" ><br/>
        <input type="submit" value="保存">
    </form>
    <br>
    <form action="params/collectionParams" method="post">
        用户名称：<input type="text" name="username" ><br/>
        用户密码：<input type="password" name="password" ><br/>
        用户年龄：<input type="text" name="age" ><br/>
        账户 1 名称：<input type="text" name="accounts[0].name" ><br/>
        账户 1 金额：<input type="text" name="accounts[0].money" ><br/>
        账户 2 名称：<input type="text" name="accounts[1].name" ><br/>
        账户 2 金额：<input type="text" name="accounts[1].money" ><br/>
        账户 3 名称：<input type="text" name="accountMap['one'].name" ><br/>
        账户 3 金额：<input type="text" name="accountMap['one'].money" ><br/>
        账户 4 名称：<input type="text" name="accountMap['two'].name" ><br/>
        账户 4 金额：<input type="text" name="accountMap['two'].money" ><br/>
        <input type="submit" value="保存">
    </form>

</body>
</html>
```

</details>

### 请求参数乱码问题

**对于 Post 请求（`form 标签 method=post`）**：解决的方法是在 `web.xml` 中配置一个编码过滤器

```xml
<web-app>

  <!-- 配置解决中文乱码的过滤器 -->
  <filter>
    <filter-name>characterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <!-- 设置过滤器中的属性值 -->
    <init-param>
      <param-name>encoding</param-name>
      <param-value>UTF-8</param-value>
    </init-param>
  </filter>
  <!-- 过滤所有请求 -->
  <filter-mapping>
    <filter-name>characterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>

</web-app>
```

**对于 Get 请求**：解决的方法是修改 Tomcat 的 `server.xml` 配置文件，添加 `<Connector useBodyEncodingForURI="true"/>`

### 特殊情况

## SpringMVC 常用注解

### RequestParam

### RequestBody

### PathVaribale

#### RESTful URL

### RequestHeader

### CookieValue

### ModelAttribute

### SessionAttribute

## 练习和总结

<details>
<summary>查看 app-servlet.xml</summary>

```

```

</details>
