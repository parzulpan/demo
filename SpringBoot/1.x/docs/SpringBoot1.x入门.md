# SpringBoot1.x 入门

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/helloworld)

## 简介

传统的 JavaEE 开发，十分笨重且配置繁琐，开发效率很低，而且有很复杂的部署流程，对于第三方技术的集成也很困难。

Sring 全家桶时代则解决了上面的问题，而 SpringBoot 则是 JavaEE 一站式解决方案，SpringCloud 是分布式整体解决方案。

SpringBoot 可以简化 Spring 应用开发，遵循约定大于配置的原则，去繁从简。

**SpringBoot 的优点**：

* 快速创建独立运行的 Spring 项目以及与主流框架集成
* 使用嵌入式的 Servlet 容器，应用无需打成WAR包
* starters 自动依赖与版本控制
* 大量的自动配置，简化开发，也可修改默认值
* 无需配置 XML，无代码生成，开箱即用
* 准生产环境的运行时应用监控
* 与云计算的天然集成

## 微服务介绍

传统的单体应用：

* 一个单体应用把所有的功能都放在一个单一进程中
* 通过在多个服务器上复制这些单体进行扩展
* 开发、测试、部署和升级等较为繁琐

微服务应用：

* 微服务是一种架构风格
* 一个微服务架构把每个功能元素放进一个独立的服务中，可以通过 HTTP 等方式进行互通
* 通过跨服务器分发这些服务进行扩展，只在需要时才复制
* 每一个功能元素最终都是一个可独立替换和独立升级的软件单元

## HelloWorld

### 传统方式创建

**需求**：浏览器发送 hello 请求，服务器接受请求并处理，响应 Hello World 字符串。

**实现步骤**：

* 创建一个 Maven 工程
* 引入 SpringBoot 相关依赖

    ```xml
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>1.5.22.RELEASE</version>
            <relativePath/> <!-- lookup parent from repository -->
        </parent>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
        </dependencies>
        
    ```

* 创建主程序

    ```java
    package cn.parzulpan.helloworld;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;

    /**
    * @SpringBootApplication 来标注一个主程序类，说明这是一个Spring Boot应用
    */

    @SpringBootApplication
    public class HelloworldApplication {

        public static void main(String[] args) {
            // Spring应用启动起来
            SpringApplication.run(HelloworldApplication.class, args);
        }

    }
    ```

* 编写相关的 Controller、Service

    ```java
    package cn.parzulpan.helloworld.cotroller;

    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.ResponseBody;
    import org.springframework.web.bind.annotation.RestController;

    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc :
    */

    // 由于方法都是 Restful 风格，还可以直接用在类上
    //@ResponseBody
    //@Controller
    // RestController 更加简单
    @RestController
    public class HelloController {

        // ResponseBody 将这个方法返回的数据直接写给浏览器，如果是对象还可以直接转为 json 数据
    //    @ResponseBody
        @RequestMapping("/hello")
        public String hello() {
            return "hello world!";
        }
    }
    ```

* 运行主程序即可
* 或者以简化部署的方式运行

    ```xml
        <!--这个插件，可以将应用打包成一个可执行的 jar 包，然后直接使用 java -jar 的命令进行执行 -->
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    ```

### Spring Initializer 快速创建

IDE 都支持使用 Spring 的项目创建向导快速创建一个 SpringBoot 项目。可以根据自己的需求选择不同组件，比如这里选择 Web 组件。

**默认生成的 SpringBoot 项目，包括**：

* 主程序已经生成好了，我们只需要我们自己的逻辑
* `resources` 文件夹中目录结构
  * `static` 保存所有的静态资源
  * `templates` 保存所有的模板页面，SpringBoot 默认 jar 包使用嵌入式的 Tomcat，默认不支持 JSP 页面，可以使用模板引擎（freemarker、thymeleaf）
  * `application.properties` SpringBoot 应用的配置文件，可以修改一些默认设置

## HelloWorld 探究

为什么这样简单的步骤和主程序就能运行起来了呢？

为什么能代替 Spring 那么复杂的开发？

### POM 文件

完整的 pom.xml 为：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.22.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>cn.parzulpan</groupId>
    <artifactId>helloworld</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>helloworld</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>

    <!--这个插件，可以将应用打包成一个可执行的 jar 包，然后直接使用 java -jar 的命令进行执行 -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```

---

注意到有一个 **parent 标签**：

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.22.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <!-- 这个 parent 标签 点进去能得到 -->
    <!-- spring-boot-starter-parent-1.5.22.RELEASE.pom -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>1.5.22.RELEASE</version>
        <relativePath>../../spring-boot-dependencies</relativePath>
    </parent>
```

它来真正管理 SpringBoot 应用里面的所有依赖版本（`<artifactId>spring-boot-dependencies</artifactId>` 定义了 jar 包的版本），所以以后我们导入依赖默认是不需要写版本的。

---

然后还注意到了这样的一个**依赖**：

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
```

它是 SpringBoot 的场景启动器，帮助我们导入了 web 模块正常运行所需要依赖的组件。

SpringBoot 将所有的功能场景都抽取出来，做成一个个的 **starters（启动器）**，只需要在项目里面引入这些 starter，相关场景的所有依赖都会导入进来。要用什么功能就导入什么场景的启动器。

### 主程序

它也是主入口类，也是主配置类

```java
@SpringBootApplication
public class HelloworldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloworldApplication.class, args);
    }
}
```

`@SpringBootApplication` Spring Boot应用标注在某个类上说明这个类是 SpringBoot 的主配置类，SpringBoot 就应该运行这个类的 main 方法来启动 SpringBoot 应用。

---

深入查看 `@SpringBootApplication` 源码，得到：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { 
    @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
    @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {}
```

**`@SpringBootConfiguration`** 标注在某个类上，表示这是一个Spring Boot的配置类。它的源码如下，所以配置类也是容器中的一个组件。

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface SpringBootConfiguration {}
```

---

**`@EnableAutoConfiguration`** 开启自动配置功能，以前需要配置的东西，SpringBoot 会帮我们自动配置。它的源码如下：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(EnableAutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {}
```

---

**`@AutoConfigurationPackage`** 自动配置包。它的源码如下：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(AutoConfigurationPackages.Registrar.class)
public @interface AutoConfigurationPackage {}
```

`@Import(AutoConfigurationPackages.Registrar.class)` 这是 Sping 的底层注解，表示给容器导入一个组件。这里可以看到，导入的组件为 **`AutoConfigurationPackages.Registrar.class`**，**它可以将主配置类（@SpringBootApplication 标注的类）的所在包以及所有自曝理由的所有组件都扫描 Spring 容器中**。

---

`@Import(EnableAutoConfigurationImportSelector.class)` 表示导入哪些组件的选择器，将所有需要导入的组件以全类名的方式返回，这些组件就会被添加到容器中。

---

通过上面的分析，这些注解会给给容器中导入非常多的自动配置类（xxxAutoConfiguration），有了这些自动配置类，就免去了我们手动编写配置注入功能组件等的工作。

SpringBoot 在启动的时候从类路径下的 **spring-boot-autoconfigure-1.5.22.RELEASE.jar!/META-INF/spring.factories** 中获取 EnableAutoConfiguration 指定的值，将
这些值作为自动配置类导入到容器中，自动配置类就生效，帮我们进行自动配置工作。以前我们需要自己配置的东西，现在自动配置类都会帮我们自动配置。

## 练习和总结
