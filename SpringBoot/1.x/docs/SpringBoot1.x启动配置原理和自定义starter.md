# SpringBoot1.x 启动配置原理 和 自定义starter

## 启动配置原理

[本节源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/start-starter)

启动过程主要为：

* `new SpringApplication(sources)` 创建 SpringApplication 对象
* `springApplication.run()` 运行Spring应用程序，创建并刷新一个新的应用环境
* 整个过程使用了**事件监听机制**

### 创建 SpringApplication 对象

```java
SpringApplication.run(StartStarterApplication.class, args);

public static ConfigurableApplicationContext run(Object[] sources, String[] args) {
    return new SpringApplication(sources).run(args);
}

public SpringApplication(Object... sources) {
    initialize(sources);
}

private void initialize(Object[] sources) {
    // 保存主配置类信息
    if (sources != null && sources.length > 0) {
        this.sources.addAll(Arrays.asList(sources));
    }

    // 判断当前是否是一个 Web App
    this.webEnvironment = deduceWebEnvironment();

    // 从类路径下找到 META_INF/spring.factories 配置文件的 ApplicationContextInitializer，然后保存起来
    setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));

    // 从类路径下找到 META_INF/spring.factories 配置文件的 ApplicationListener，然后保存起来
    setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));

    // 从多个配置类中找到有 main() 的主配置类
    this.mainApplicationClass = deduceMainApplicationClass();
}
```

![Initializers&listeners](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_201230101131Initializers&listeners.png)

### 运行Spring应用程序，创建并刷新一个新的应用环境

```java
public ConfigurableApplicationContext run(String... args) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    ConfigurableApplicationContext context = null;
    FailureAnalyzers analyzers = null;
    configureHeadlessProperty();

    // 从类路径下的 META_INF/spring.factories 中获取 SpringApplicationRunListeners
    SpringApplicationRunListeners listeners = getRunListeners(args);

    // 回调所有的 SpringApplicationRunListener.starting()
    listeners.starting();
    try {
        // 封装命令行参数
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);

        // 准备环境
        // 准备环境完成后，回调 SpringApplicationRunListener.environmentPrepared() 表示环境准备完成
        ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);

        // 打印 Spring 标志
        Banner printedBanner = printBanner(environment);

        // 创建 ApplicationContext，决定创建 web 的ioc，还是普通的 ioc
        context = createApplicationContext();

        analyzers = new FailureAnalyzers(context);

        // 准备上下文环境，将 environment 保存到 ioc 中，而且调用 applyInitializers()
        // 这个方法将 回调之前保存的所有的 ApplicationContextInitializer 的 initialize()
        // 和 回调之前保存的所有的 SpringApplicationRunListener 的 contextPrepared()
        // 准备上下文环境完成后，回调之前保存的所有的 SpringApplicationRunListener 的 contextLoaded（）
        // 控制台打印：使用 PID 6894 在 192.168.0.103 上启动 主配置类
        prepareContext(context, environment, listeners, applicationArguments, printedBanner);

        // 刷新容器，即 ioc 容器初始化，如果是 web app 还会创建嵌入式的 Tomcat
        refreshContext(context);

        // 从 ioc 容器中获取所有的 ApplicationRunner 和 CommandLineRunner 进行回调
        afterRefresh(context, applicationArguments);

        // 所有的 SpringApplicationRunListener 回调 finished()
        listeners.finished(context, null);

        stopWatch.stop();
        if (this.logStartupInfo) {
            new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
        }

        // 整个 SpringBoot 应用启动完成以后返回启动的 ioc 容器
        return context;
    }
    catch (Throwable ex) {
        handleRunFailure(context, listeners, analyzers, ex);
        throw new IllegalStateException(ex);
    }
}
```

### 事件监听机制

ApplicationContextInitializer、SpringApplicationRunListener 配置在 META-INF/spring.factories 中。

ApplicationRunner、CommandLineRunner 放在 ioc 容器中。

---

HelloApplicationContextInitializer：

```java
public class HelloApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.out.println("ApplicationContextInitializer...initialize..."+applicationContext);
    }
}
```

HelloSpringApplicationRunListener：

```java
public class HelloSpringApplicationRunListener implements SpringApplicationRunListener {
    // 必须有一个构造器
    public HelloSpringApplicationRunListener(SpringApplication application, String[] arg) {
    }

    @Override
    public void starting() {
        System.out.println("SpringApplicationRunListener...starting...");
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        System.out.println("SpringApplicationRunListener...environmentPrepared.." + environment);

        Object o = environment.getSystemProperties().get("os.name");
        System.out.println("SpringApplicationRunListener...environmentPrepared.. os.name "+o);
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("SpringApplicationRunListener...contextPrepared...");
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("SpringApplicationRunListener...contextLoaded...");
    }

    @Override
    public void finished(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println("SpringApplicationRunListener...finished...");
    }
}
```

将它们配置在 META-INF/spring.factories 中：

src/main/resources/META-INF/spring.factories

```factories
org.springframework.context.ApplicationContextInitializer=\
cn.parzulpan.listener.HelloApplicationContextInitializer

org.springframework.boot.SpringApplicationRunListener=\
cn.parzulpan.listener.HelloSpringApplicationRunListener
```

---

HelloApplicationRunner：

```java
@Component
public class HelloApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("ApplicationRunner...run...." + args);
    }
}
```

HelloCommandLineRunner：

```java
@Component
public class HelloCommandLineRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("CommandLineRunner...run..."+ Arrays.asList(args));
    }
}
```

将它们放置在 ioc 容器中。

---

运行主配置类，观察打印输出，可以得到上面的结论。

## 自定义 starter

SpringBoot 最大的特点就是引入非常多的场景启动器，想使用那个场景就可以直接整合。

它也支持自定义场景启动器，比如 `mybatis-spring-boot-starter`。

编写自动配置需要的**必有项**：

```java
@Configuration // 指定这个类是一个配置类
@ConditionalOnXXX // 在指定条件成立的情况下自动配置类生效
@AutoConfigureAfter // 指定自动配置类的顺序

@Bean // 给容器中添加组件
@ConfigurationPropertie // 结合相关 xxxProperties 类来绑定相关的配置
@EnableConfigurationProperties // 让 xxxProperties 生效并加入到容器中
```

自动配置类要能加载将需要启动就加载的自动配置类，配置在 `META‐INF/spring.factories` 中

```factories
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
```

---

**模式总结**：

* 启动器只用来做依赖导入 `xx-spring-boot-starter`
* 编写一个自动配置模块 `xx-spring-boot-starter-autoconfigurer`
* 启动器依赖自动配置模块，别人使用只需要引入启动器
* 官方命名空间: `spring-boot-starter-模块名`， 自定义命名空间：`模块名-spring-boot-starter`

### 自定义步骤

[本节源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/custom-starter)

前期准备：创建一个空项目 custom-starter ，向其加入一个 Maven 工厂
parzulpan-spring-boot-starter 模块，在加入一个 springboot 类型的 parzulpan-spring-boot-starte 模块。

**启动器模块**：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>cn.parzulpan</groupId>
    <artifactId>parzulpan-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>

    <!-- 启动器 -->
    <dependencies>

        <!-- 依赖自动配置模块 -->
        <dependency>
            <groupId>cn.parzulpan</groupId>
            <artifactId>parzulpan-spring-boot-starter-configurer</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>

</project>
```

---

**自动配置模块**：

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
    <artifactId>parzulpan-spring-boot-starter-configurer</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>parzulpan-spring-boot-starter-configurer</name>
    <description>parzulpan starter configurer</description>

    <properties>
        <project.build.sourceEncoding>UTF‐8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF‐8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <!-- 引入spring‐boot‐starter，它是所有 starter 的基本配置 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
    </dependencies>

</project>
```

编写业务类 HelloService：

```java
package cn.parzulpan;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : HelloService 
 */

public class HelloService {

    HelloServiceProperties helloServiceProperties;

    public HelloServiceProperties getHelloServiceProperties() {
        return helloServiceProperties;
    }

    public void setHelloServiceProperties(HelloServiceProperties helloServiceProperties) {
        this.helloServiceProperties = helloServiceProperties;
    }

    public String sayHelloName(String name) {
        return helloServiceProperties.getPrefix() + " - " + name + " - " + helloServiceProperties.getSuffix();
    }
}
```

编写属性类 HelloServiceProperties：

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : HelloService 属性类
 */

@ConfigurationProperties(prefix = "parzulpan.hello")
public class HelloServiceProperties {
    private String prefix;  // 前置语
    private String suffix;  // 后置语

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
```

编写配置文件 src/main/resources/META-INF/spring.factories：

```factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
cn.parzulpan.HelloServiceAutoConfiguration
```

编写自动配置类 HelloServiceAutoConfiguration：

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : HelloService 自动配置类
 */

@Configuration
@ConditionalOnWebApplication    // web app 才有效
@EnableConfigurationProperties(HelloServiceProperties.class)    // 让 HelloServiceProperties 生效并加入到容器中
public class HelloServiceAutoConfiguration {

    @Autowired
    HelloServiceProperties helloServiceProperties;

    @Bean
    public HelloService helloService() {
        HelloService helloService = new HelloService();
        helloService.setHelloServiceProperties(helloServiceProperties);
        return helloService;
    }
}
```

---

将这两个模块分别 install 到本地，然后测试使用，创建一个 SpringBoot Web 项目 custom-starter-test ，引入自定义 starter。[测试源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/custom-starter-test)

```xml
        <!--  引入自定义 starter -->
        <dependency>
            <groupId>cn.parzulpan</groupId>
            <artifactId>parzulpan-spring-boot-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```

编写配置文件 application.properties：

```properties
parzulpan.hello.prefix=PARZULPAN
parzulpan.hello.suffix=HELLO WORLD
```

编写控制类：

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@RestController
public class HelloController {

    @Autowired
    HelloService helloService;

    // http://localhost:8080/hello
    @GetMapping("/hello")
    public String hello() {
        return helloService.sayHelloName("curry");
    }
}
```


## 练习和总结
