# SpringBoot1.x Web 开发

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/web-restful-crud)

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

## 错误处理机制

## 配置嵌入式 Servlet 容器

SpringBoot 默认使用 Tomcat 作为嵌入式的 Servlet 容器。

### 定制和修改 Servlet 容器的相关配置

**方式一**：可以通过修改跟 server 有关的配置来定制和修改，能修改的配置可以参考 `ServerProperties` 类，这个类本质也是 `EmbeddedServletContainerCustomizer`

```properties
server.port=8081
server.context‐path=/crud
server.tomcat.uri‐encoding=UTF‐8

// 通用的 Servlet 容器设置
server.xxx
// Tomcat 的设置
server.tomcat.xxx
```

**方式二**：编写一个 `EmbeddedServletContainerCustomizer`，即嵌入式的 Servlet 容器的定制器，用它来修改 Servlet 容器的配置

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义服务配置类
 */

@Configuration
public class CustomServerConfig {

    // 定制器 添加到容器中
    @Bean
    public EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer(){
        return new EmbeddedServletContainerCustomizer() {
            // 定制嵌入式的Servlet容器相关的规则
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                container.setPort(8083);
            }
        };
    }
}
```

### 注册 Servlet 三大组件

三大组件即 Servlet、Filter、Listener。

由于 SpringBoot 默认是以 jar包 的方式启动嵌入式的 Servlet 容器来启动 SpringBoot 的 Web 应用，即没有 web.xml 文件。所以注册三个组件可以使用以下方式：

* ServletRegistrationBean
* FilterRegistrationBean
* ServletListenerRegistrationBean

    ```java
    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 自定义 Servlet
    */

    public class CustomServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doPost(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write("Hello CustomServlet...");
        }
    }
    ```

    ```java
    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 自定义 Filter
    */

    public class CustomFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {

        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            System.out.println("CustomFilter process...");
            filterChain.doFilter(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {

        }
    }
    ```

    ```java
    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 自定义 Listener
    */

    public class CustomListener implements ServletContextListener {
        @Override
        public void contextInitialized(ServletContextEvent servletContextEvent) {
            System.out.println("CustomListener contextInitialized... ");
        }

        @Override
        public void contextDestroyed(ServletContextEvent servletContextEvent) {
            System.out.println("CustomListener contextDestroyed... ");
        }
    }
    ```

---

* 自定义服务配置类

    ```java
    /**
    * @Author : parzulpan
    * @Time : 2020-12
    * @Desc : 自定义服务配置类
    */

    @Configuration
    public class CustomServerConfig {

        // 自定义 Servlet 添加到容器中
        @Bean
        public ServletRegistrationBean customServlet() {
            ServletRegistrationBean srb = new ServletRegistrationBean(new CustomServlet(), "/customServlet");
            srb.setLoadOnStartup(1);    // 可以设置各种属性
            return srb;
        }

        // 自定义 Filter 添加到容器中
        @Bean
        public FilterRegistrationBean customFilter() {
            FilterRegistrationBean frb = new FilterRegistrationBean();
            frb.setFilter(new CustomFilter());
            frb.setUrlPatterns(Arrays.asList("/hello", "/customServlet"));
            return frb;
        }

        // 自定义 Listener 添加到容器中
        @Bean
        public ServletListenerRegistrationBean customListener() {
            ServletListenerRegistrationBean<CustomListener> lrb = new ServletListenerRegistrationBean<>(new CustomListener());
            return lrb;
        }
    ```

比较典型的例子有，SpringBoot 帮我们自动 SpringMVC 的时候，自动的注册 SpringMVC 的前端控制器，即 DispatcherServlet。

### 更换嵌入式 Servlet 容器

SpringBoot 默认使用 Tomcat 作为嵌入式的 Servlet 容器，它也支持 Jetty 和 Undertow 容器。

Tomcat 是 apache 下的一款重量级的服务器，不用多说历史悠久，而且经得起实践的考验。而 Jetty 和 Undertow 都是基于 NIO 实现的高并发轻量级的服务器，支持 Servlet3.1 和 WebSocket。

`ConfigurableEmbeddedServletContainer` 继承关系为：

![ConfigurableEmbeddedServletContainer继承关系](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_201229080006ConfigurableEmbeddedServletContainer%E7%BB%A7%E6%89%BF%E5%85%B3%E7%B3%BB.png)

**使用 Jetty**：

```xml
    <dependencies>
        <!-- web 模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <!-- 排除默认的 Tomcat 容器 -->
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- 引入新的 Jetty 容器 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jetty</artifactId>
        </dependency>
    <dependencies>
```

**使用 Undertow**：

```xml
    <dependencies>
        <!-- web 模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <!-- 排除默认的 Tomcat 容器 -->
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- 引入新的 Undertow 容器 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-undertow</artifactId>
        </dependency>
    <dependencies>
```

### 嵌入式 Servlet 容器 自动配置原理

嵌入式 Servlet 容器 怎么配置上去的，怎么工作的？

是因为存在 `EmbeddedServletContainerAutoConfiguration` 嵌入式的 Servlet 容器自动配置类。

```java
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@ConditionalOnWebApplication
//导入 BeanPostProcessorsRegistrar 给容器中导入一些组件
//导入了EmbeddedServletContainerCustomizerBeanPostProcessor，即后置处理器，在 bean 初始化前后（创建完对象，还没赋值赋值）执行初始化工作
@Import(BeanPostProcessorsRegistrar.class)
public class EmbeddedServletContainerAutoConfiguration {

    // 如果正在使用 Tomcat，则为嵌套配置。
    @Configuration
    @ConditionalOnClass({ Servlet.class, Tomcat.class })
    // 判断当前容器没有用户自己定义 EmbeddedServletContainerFactory 嵌入式的 Servlet 容器工厂，它的作用是创建嵌入式的Servlet容器
    @ConditionalOnMissingBean(value = EmbeddedServletContainerFactory.class, search = SearchStrategy.CURRENT)
    public static class EmbeddedTomcat {

        @Bean
        // 返回一个 EmbeddedServletContainer，并且启动 Tomcat服务器，其他容器同理
        public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
            return new TomcatEmbeddedServletContainerFactory();
        }

    }

    // 如果正在使用 Jetty，则为嵌套配置。
    @Configuration
    @ConditionalOnClass({ Servlet.class, Server.class, Loader.class, WebAppContext.class })
    @ConditionalOnMissingBean(value = EmbeddedServletContainerFactory.class, search = SearchStrategy.CURRENT)
    public static class EmbeddedJetty {

        @Bean
        public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory() {
            return new JettyEmbeddedServletContainerFactory();
        }

    }

    // 如果正在使用 Undertow，则为嵌套配置。
    @Configuration
    @ConditionalOnClass({ Servlet.class, Undertow.class, SslClientAuthMode.class })
    @ConditionalOnMissingBean(value = EmbeddedServletContainerFactory.class, search = SearchStrategy.CURRENT)
    public static class EmbeddedUndertow {

        @Bean
        public UndertowEmbeddedServletContainerFactory undertowEmbeddedServletContainerFactory() {
            return new UndertowEmbeddedServletContainerFactory();
        }
    }
}
```

---

那么对嵌入式容器的配置修改是怎么生效？

我们知道，对于配置修改有两种方式，一种是修改配置文件，本质是使用 ServerProperties 类，而一种是使用 **EmbeddedServletContainerCustomizer** 类。值得注意的是，ServerProperties 也是 Customizer，即也是一种定制器。

**具体步骤为**：

* SpringBoot 根据导入的依赖情况，给容器中添加相应的 **EmbeddedServletContainerFactory**，比如 TomcatEmbeddedServletContainerFactory 容器工厂
* 容器中某个组件要创建对象就会使用后置处理器 EmbeddedServletContainerCustomizerBeanPostProcessor，只要是嵌入式的 Servlet 容器工厂，后置处理器就工作。
* 后置处理器会从容器中获取所有的 **EmbeddedServletContainerCustomizer** 类，进而调用定制器的定制方法。

### 嵌入式 Servlet 容器 启动原理

什么时候创建嵌入式的 Servlet 容器工厂？什么时候获取嵌入式的 Servlet 容器并启动 Tomcat？

**创建嵌入式的 Servlet 容器工厂过程**：

![创建嵌入式的 Servlet 容器工厂过程 调试断点](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_201229090244%E5%88%9B%E5%BB%BA%E5%B5%8C%E5%85%A5%E5%BC%8F%E7%9A%84%20Servlet%20%E5%AE%B9%E5%99%A8%E5%B7%A5%E5%8E%82%E8%BF%87%E7%A8%8B%20%E8%B0%83%E8%AF%95%E6%96%AD%E7%82%B9.png)

![创建嵌入式的 Servlet 容器工厂过程](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_201229090252%E5%88%9B%E5%BB%BA%E5%B5%8C%E5%85%A5%E5%BC%8F%E7%9A%84%20Servlet%20%E5%AE%B9%E5%99%A8%E5%B7%A5%E5%8E%82%E8%BF%87%E7%A8%8B.png)

* **第一点**：SpringBoot 应用启动运行 run()
* **第二点**：SpringBoot 刷新 IOC 容器，此时会创建 IOC 容器对象，并初始化容器，创建容器中的每一个组件。如果是 web app 则创建AnnotationConfigEmbeddedWebApplicationContext，否则创建 AnnotationConfigApplicationContext
* **第三点**：刷新刚才创建好的 IOC 容器
* **第四点**：web 的 IOC 容器 重写了 onRefresh()
* **第五点**：web 的 IOC 容器 创建嵌入式 Servlet 容器

---

**获取嵌入式的 Servlet 容器并启动 Tomcat 过程**：

* **第一点**：获取嵌入式的 Servlet 容器工厂
* **第二点**：使用容器工厂获取嵌入式的 Servlet 容器
* **第三点**：嵌入式的 Servlet 容器创建对象并启动 Servlet 容器
* **第四点**：先启动嵌入式的 Servlet 容器，再将 IOC 容器中剩下没有创建出的对象获取出来

总结的话，就是 IOC容器启动时会创建嵌入式的 Servlet 容器。

## 使用外置 Servlet 容器

嵌入式 Servlet 容器，可将应用打成可执行的 jar包。

它的优点是：简单、便携；

它的缺点：默认不支持 JSP、优化定制比较复杂；

而外置的 Servlet 容器，就是在外面安装 Tomcat，应用 war 包的方式打包。

**使用步骤**：

* 必须创建一个 war 项目，然后利用 idea 创建好目录结构
* 必须将嵌入式的 Tomcat 指定为 provided
* 必须编写一个 SpringBootServletInitializer 的子类，并调用 configure 方法，服务器依靠它启动 SpringBoot 应用
* 配置启动 Tomcat 服务器就可以使用

**jar包**：执行 SpringBoot 主类的 main()，启动 IOC 容器并创建嵌入式的 Servlet 容器，启动 SpringBoot 应用。

**war包**：启动外置服务器，服务器通过 SpringBootServletInitializer 启动 SpringBoot 应用，启动 IOC 容器并创建Servlet 容器。

[本节源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/web-jsp)

## 练习和总结
