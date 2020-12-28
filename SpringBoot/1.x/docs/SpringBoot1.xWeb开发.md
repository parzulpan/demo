# SpringBoot1.x Web 开发

## 简介

SpringBoot 非常适合 Web 应用程序开发。可以使用嵌入式 Tomcat，Jetty 或 Undertow 轻松创建独立的 HTTP 服务器。

大多数Web应用程序将使用 `spring-boot-starter-web` 模块来快速启动和运行。

使用 SpringBoot 开发 Web 应用的流程：

* 创建 SpringBoot 应用，选择需要集成的模块
* SpringBoot 默认将这些模块场景配置好，只需要在配置文件指定相关属性即可
* 编写相关的业务代码

`xxxxAutoConfigurartion` 自动配置类，给容器中添加组件。`xxxxProperties` 封装配置文件中相关属性。

## SpringBoot 对静态资源的映射规则

**对于静态资源文件夹映射**：

springframework/boot/autoconfigure/web/**WebMvcAutoConfiguration.java**

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!this.resourceProperties.isAddMappings()) {
        logger.debug("Default resource handling disabled");
        return;
    }
    Integer cachePeriod = this.resourceProperties.getCachePeriod();
    if (!registry.hasMappingForPattern("/webjars/**")) {
        customizeResourceHandlerRegistration(registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/").setCachePeriod(cachePeriod));
    }
    String staticPathPattern = this.mvcProperties.getStaticPathPattern();
    // 静态资源文件夹映射
    if (!registry.hasMappingForPattern(staticPathPattern)) {
        customizeResourceHandlerRegistration(registry.addResourceHandler(staticPathPattern)
                .addResourceLocations(this.resourceProperties.getStaticLocations())
                .setCachePeriod(cachePeriod));
    }
}
```

可以看到：

* 所有以 `/webjars/**` 的方式访问项目的任何资源，都会去以下 `classpath:/META-INF/resources/webjars/` 找资源。webjars 以 jar包 的方式引入静态资源，比如访问 `localhost:8080/webjars/jquery/3.3.1/jquery.js`。
* 所有以 `/**` 的方式访问当前项目的任何资源，都去静态资源的文件夹找资源。SpringBoot 默认的静态资源的文件夹有：
  * `"classpath:/META-INF/resources/"`
  * `"classpath:/resources/"`
  * `"classpath:/static/"`
  * `"classpath:/public/"`
  * `根目录`

---

**对于欢迎页映射**：

```java
@Bean
public WelcomePageHandlerMapping welcomePageHandlerMapping(ResourceProperties resourceProperties) {
    return new WelcomePageHandlerMapping(resourceProperties.getWelcomePage(),
            this.mvcProperties.getStaticPathPattern());
}
```

可以看到：

 静态资源文件夹下的所有 index.html 页面，被 `"/**"` 映射，比如访问 `localhost:8080/` 就会找 index 页面。

---

**对于图标映射**：

```java
@Configuration
@ConditionalOnProperty(value = "spring.mvc.favicon.enabled", matchIfMissing = true)
public static class FaviconConfiguration {

    private final ResourceProperties resourceProperties;

    public FaviconConfiguration(ResourceProperties resourceProperties) {
        this.resourceProperties = resourceProperties;
    }

    @Bean
    public SimpleUrlHandlerMapping faviconHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        // 所有的 **/favicon.ico
        mapping.setUrlMap(Collections.singletonMap("**/favicon.ico", faviconRequestHandler()));
        return mapping;
    }

    @Bean
    public ResourceHttpRequestHandler faviconRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        requestHandler.setLocations(this.resourceProperties.getFaviconLocations());
        return requestHandler;
    }

}
```

可以看到：

所有的 `**/favicon.ico` 都是在静态资源文件下找。

---

这些都是 SpringBoot 默认配置的属性，`ResourceProperties` 可用于配置资源处理的属性。

```java
@ConfigurationProperties(prefix = "spring.resources", ignoreUnknownFields = false)
public class ResourceProperties implements ResourceLoaderAware, InitializingBean {

    private static final String[] SERVLET_RESOURCE_LOCATIONS = { "/" };

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
            "classpath:/resources/", "classpath:/static/", "classpath:/public/" };

    private static final String[] RESOURCE_LOCATIONS;

    // ...

    // 静态资源的位置
    private String[] staticLocations = RESOURCE_LOCATIONS;

    // 资源处理程序所服务的资源的缓存周期（以秒为单位）
    private Integer cachePeriod;

    // ...
}
```

## 模版引擎

**Template**：

```html
...

Hello ${user}
...
```

**Data**：

```data
...

model.adddAttibute("user", "parzulpan")
...
```

通过 TemplateEngine 能得到：

```html
...

Hello parzulpan
...
```

SpringBoot 推荐的模版引擎 有 Thymeleaf，它语法更简单，功能更强大。

### 引入 Thymeleaf

添加如下属性和依赖：

```xml
    <properties>
        <thymeleaf.version>3.0.9.RELEASE</thymeleaf.version>
        <!-- 布局功能的支持程序  thymeleaf3主程序  layout2以上版本 -->
        <!-- thymeleaf2主程序   layout12以上版本 -->
        <thymeleaf-layout-dialect.version>2.2.2</thymeleaf-layout-dialect.version>
    </properties>

    <!-- thymeleaf 模版引擎 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
```

### Thymeleaf 使用

可以看到 ThymeleafProperties 为如下，可以根据它来配置使用：

```java
@ConfigurationProperties(prefix = "spring.thymeleaf")
public class ThymeleafProperties {

    private static final Charset DEFAULT_ENCODING = Charset.forName("UTF-8");

    private static final MimeType DEFAULT_CONTENT_TYPE = MimeType.valueOf("text/html");

    public static final String DEFAULT_PREFIX = "classpath:/templates/";

    public static final String DEFAULT_SUFFIX = ".html";
}
```

所以只要把 HTML 页面放在 `classpath:/templates/`，thymeleaf 就能自动渲染。

**使用步骤**：

* 导入 thymeleaf 的名称空间

    ```html
    <html lang="en" xmlns:th="http://www.thymeleaf.org">
    ```

* 使用 thymeleaf 语法

    ```html
    <!DOCTYPE html>
    <html lang="en" xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="UTF‐8">
        <title>Title</title>
    </head>
    <body>
        <h1>成功！</h1>
        <!-- th:text 将div里面的文本内容设置为 -->
        <div th:text="${hello}">这是显示欢迎信息</div>
    </body>
    </html>
    ```

### 语法规则

`th:text`，改变当前元素里面的文本内容。

`th:xx` ，xx 可以是任意 html 属性，用来替换原生属性的值。

## SpringMVC 自动配置

SpringBoot 为 SpringMVC 提供了自动配置，可与大多数应用程序完美配合。自动配置在 Spring 的默认值之上添加了以下功能：

* 包含 ContentNegotiatingViewResolver 和 BeanNameViewResolver
* 支持提供静态资源，包括对 WebJars 的支持
* 自动注册 `Converter，GenericConverter，Formatter` beans
* 支持 HttpMessageConverters
* 自动注册 MessageCodesResolver
* 静态 index.html 支持。
* 定制 Favicon 支持
* 自动使用 `ConfigurableWebBindingInitializer` bean

如果您想保留 SpringBoot MVC功能，并且只想添加其他 MVC 配置（拦截器，格式化程序，视图控制器等），则可以添加自己 `@Configuration` 的配置类，继承自 WebMvcConfigurerAdapter，但不能添加 `@EnableWebMvc`。这样的话，既保留了所有的自动配置，也能用我们扩展的配置。

```java
package cn.parzulpan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自动以配置类，扩展 SpringMVC
 */

@Configuration
public class CustomMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        // 浏览器发送 /parzulpan 请求来到自定义 404 页面
        registry.addViewController("/parzulpan").setViewName("404");
    }
}
```

---

也可以全面接管 SpringMVC，使所有的 SpringMVC 的自动配置都失效，只使用自己的配置。只需要在配置类中添加 `@EnableWebMvc` 即可。

为什么会失效呢？

因为 @EnableWebMvc 会将 WebMvcConfigurationSupport 组件导入进来，而该组件导入进来后，WebMvcAutoConfiguration 配置类就会失效。

```java
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class,
WebMvcConfigurerAdapter.class })

//容器中没有这个组件的时候，这个自动配置类才生效
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)

@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@AutoConfigureAfter({ DispatcherServletAutoConfiguration.class,
ValidationAutoConfiguration.class })
public class WebMvcAutoConfiguration {}
```

## 修改 SpringBoot 默认配置

SpringBoot 在自动配置很多组件的时候，先看容器中有没有用户自己配置的（@Bean、@Component）如果有就用用户配置的。如果没有，才自动配置。如果有些组件可以有多个（ViewResolver），可以将用户配置的和自己默
认的组合起来。

## RestfulCRUD

### 添加资源

将所有的静态资源都添加到 `src/main/resources/static` 文件夹下，所有的模版资源都添加到 `src/main/resources/templates` 文件夹下。

创建数据库表，并编写对应实体类。

```sql
use web_restful_crud;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `id` int(11) primary key NOT NULL AUTO_INCREMENT,
  `departmentName` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

use web_restful_crud;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` int(11) primary key NOT NULL AUTO_INCREMENT,
  `lastName` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `gender` int(2) DEFAULT NULL,
  `birth` date DEFAULT NULL,
  `d_id` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```

```java
public class Department {
    private Integer id;
    private String departmentName;
    // setter getter toString
}

public class Employee {
    private Integer id;
    private String lastName;
    private String email;
    private Integer gender; // 1 male, 0 female
    private Date birth;
    private Department department;
    // setter getter toString
}
```

### 默认访问首页

可以使用 WebMvcConfigurerAdapter 可以来扩展 SpringMVC 的功能，可以不用自己实现一个 ViewController。

src/main/java/cn/parzulpan/config/CustomMvcConfig.java

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自动以配置类，扩展 SpringMVC
 */

@Configuration
public class CustomMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 浏览器发送 /parzulpan 请求来到自定义 404 页面
        registry.addViewController("/parzulpan").setViewName("404");
    }

    // 将组件注册在容器，所有的 WebMvcConfigurerAdapter 组件都会一起起作用
    @Bean
    public WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
        WebMvcConfigurerAdapter adapter = new WebMvcConfigurerAdapter() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("login");
                registry.addViewController("/index").setViewName("login");
                registry.addViewController("/index.html").setViewName("login");
            }
        };
        return adapter;
    }
}

```

### 国际化

国际化之前添加 Thymeleaf 支持。

```html
<html lang="en">

<link href="asserts/css/bootstrap.min.css" rel="stylesheet">
```

改为：

```html
<html lang="en" xmlns:th="http://www.thymeleaf.org">

<link href="asserts/css/bootstrap.min.css" th:href="@{/asserts/css/bootstrap.min.css}" rel="stylesheet">
```

这样做的好处是，当通过 `server.context-path=/crud` 更改项目路径时，静态文件可以自动匹配。

### 登陆

### 拦截器进行登陆检查

### CRUD-员工列表

## 练习和总结
