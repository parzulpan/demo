# SpringBoot1.x 配置

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/config)

## 配置文件

SpringBoot 使用一个全局的配置文件，配置文件名是固定的。

application.properties、application.yml都是配置文件。

**配置文件的作用**：修改 SpringBoot 自动配置的默认值，SpringBoot 在底层都给我们自动配置好。

## YAML 语法

YAML（YAML Ain't Markup Language），它是一个标记语言，又不是一个标记语言。yaml 文件以数据为中心，比 json、xml 等更适合做配置文件。

### 基本语法

`k: v` 表示一对键值对，**空格必须有**。

以空格的缩进来控制层级关系，只要是左对齐的一列数据，都是同一个层级的。属性和值对大小写敏感。

```yaml
server:
  port: 8081
  path: /hello
```

### 值的写法

**字面量，比如数字，字符串，布尔等普通的值**：

字符串默认不用加上单引号或者双引号。

`""` 双引号；不会转义字符串里面的特殊字符；特殊字符会作为本身想表示的意思

```yaml
name: "zhangsan \n lisi"
```

`''` 单引号；会转义特殊字符，特殊字符最终只是一个普通的字符串数据

```yaml
name: 'zhangsan \n lisi'
```

**对象，即键值对**：

对象还是 `k: v` 的方式

```yaml
friends:
  lastName: zhangsan
  age: 20
```

等效的行内写法

```yaml
friends: {lastName: zhangsan, age: 20}
```

**数组，即 List、Set**：

用 `- 值` 表示数组中的一个元素

```yaml
pets:
  ‐ cat
  ‐ dog
  ‐ pig
```

等效的行内写法

```yaml
pets: [cat, dog, pig]
```

## 配置文件 值注入

**先写实体类**：

```java
/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 人实体类
 *
 * 将配置文件中配置的每一个属性的值，映射到这个组件中
 * @ConfigurationProperties： 告诉 SpringBoot 将本类中的所有属性和配置文件中相关的配置进行绑定
 * prefix = "person" 配置文件中哪个下面的所有属性进行一一映射
 *
 */

@Component
@ConfigurationProperties(prefix = "person") // 默认从全局配置文件中获取值
public class Person {
    private String name;
    private Integer age;
    private Boolean boss;
    private Date date;

    private Map<String, Object> maps;
    private List<Object> lists;
    private Dog dog;

    // getter setter toString
}
```

在写配置文件之前，需要先导入配置文件处理器，这样配置文件进行绑定就会有提示。

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
```

**再写配置文件**：

```yaml
server:
  port: 8081

person:
  name: hello
  age: 18
  boss: true
  date: 2020/01/01
  maps: {k1: v1, k2: 12}
  lists:
    - ll
    - aa
  dog:
    name: xx
    age: 1
```

**测试**：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigApplicationTests {

    @Autowired
    Person person;

    @Test
    public void contextLoads() {
        System.out.println(person);
    }
}
```

### @Value & @ConfigurationProperties

这两个注解的**区别**：

| | | |
| :-- | :-- | :-- |
| | @ConfigurationProperties | @Value |
功能 | 批量注入配置文件中的属性 | 一个个指定
松散绑定（松散语法） | 支持 | 不支持
SpEL | 不支持 | 支持
JSR303数据校验 `@Validated` | 支持 | 不支持
复杂类型封装 | 支持 | 不支持

配置文件 yml 还是 properties 他们都能获取到值。

如果说，我们只是在某个业务逻辑中需要获取一下配置文件中的某项值，使用 `@Value`。

如果说，我们专门编写了一个 JavaBean 来和配置文件进行映射，我们就直接使用 `@ConfigurationProperties`。

### @PropertySource & @ImportResource & @Bean

`@PropertySource` 加载指定的配置文件

```java
@PropertySource(value = {"classpath:person.properties"})
@Component
@ConfigurationProperties(prefix = "person")
public class Person{}
```

`@ImportResource` 导入 Spring 的配置文件，让配置文件里面的内容生效。SpringBoot 里面没有 Spring 的配置文件，我们自己编写的配置文件，也不能自动识别。想让Spring的配置文件生效，加载到容器中，可以用 @ImportResource 标注在一个配置类上。

```java
@ImportResource(locations = {"classpath:beans.xml"})
@SpringBootApplication
public class ConfigApplication {}
```

但是这种写法太繁琐，SpringBoot 推荐给容器中添加组件的方式是使用全注解的方式。步骤为：

* 为 Spring 配置文件写一个配置类 `@Configuration`
* 使用 `@Bean` 给容器中添加组件

    ```java
    @Configuration
    public class MyAppConfig {
        //将方法的返回值添加到容器中；容器中这个组件默认的 id 就是方法名
        @Bean
        public HelloService helloService(){
            System.out.println("配置类@Bean给容器中添加组件了...");
            return new HelloService();
        }
    }
    ```

* 可以测试容器是否含有组件

    ```java
        @Autowired
        ApplicationContext ioc;
        
        public void testHasHelloService() {
            boolean helloService = ioc.containsBean("helloService");
            System.out.println(helloService);
        }
    ```

## 配置文件 占位符

**随机数**：

```yaml
${random.value}、${random.int}、${random.long}
${random.int(10)}、${random.int[1024,65536]}
```

占位符获取之前配置的值，如果没有可以是用 `:` 指定默认值

```yaml
person.last‐name=张三${random.uuid}
person.age=${random.int}
person.birth=2017/12/15
person.boss=false
person.maps.k1=v1
person.maps.k2=14
person.lists=a,b,c
person.dog.name=${person.hello:hello}_dog
person.dog.age=15
```

## Profile 文件

Profile 是 Spring 对不同环境提供不同配置功能的支持，可以通过激活、指定参数等方式快速切换环境。

### 多 Profile 文件方式

我们在主配置文件编写的时候，文件名可以是 `application-{profile}.properties/yml`

默认使用 application.properties 的配置。

application.properties

```properties
server.port=8080
spring.profiles.active=dev  # 指定使用那个环境
```

---

application-dev.properties

```properties
server.port=8081
```

---

application-prod.properties

```properties
server.port=8082
```

运行主配置类，会显示 `Tomcat started on port(s): 8081 (http)`

### 多文档块方式

yml 支持多文档块方式。

```yaml
server:
  port: 8091
spring:
  profiles:
    active: dev

---

server:
  port: 8092
spring:
  profiles: dev
person:
  name: 哈哈
  age: 18
  boss: true
  date: 2020/01/01
  maps: {k1: v1, k2: 12}
  lists:
    - ll
    - aa
  dog:
    name: xx
    age: 1

---

server:
  port: 8093
spring:
  profiles: prod

```

运行主配置类，会显示 `tomcat started on port(s): 8092 (http)`

### 其他方式

可以直接在测试的时候，配置传入命令行参数

`java -jar spring-boot-02-config-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev`

## 配置文件 加载位置

SpringBoot 启动会扫描以下位置的 application.properties 或者 application.yml 文件作为 SpringBoot 的默认配置文件：

* `file:./config/`
* `file:./`
* `classpath:/config/`
* `classpath:/`

以上优先级由高到底，高优先级的配置会覆盖低优先级的配置。SpringBoot 会从这四个位置全部加载主配置文件，以**互补配置（高优先级没有的，就用低优先级的）**的方式。

还可以通过 `spring.config.location` 来改变默认的配置文件位置。

项目打包好以后，我们可以使用命令行参数的形式，启动项目的时候来指定配置文件的新位置。指定配置文件和默认加载的这些配置文件共同起作用形成互补配置。

`java -jar config-0.0.1-SNAPSHOT.jar --spring.config.location=/parzulpan/config/application.properties`

## 外部配置 加载顺序

SpringBoot 也可以从以下位置加载配置，它们优先级从高到低。高优先级的配置覆盖低优先级的配置，所有的配置会形成互补配置。

一 **命令行参数**

所有的配置都可以在命令行上进行指定

`java -jar config-0.0.1-SNAPSHOT.jar --server.port=8087 --server.context-path=/abc`

多个配置用空格分开，格式为 `--配置项=值`

二 来自 java:comp/env 的 JNDI 属性

三 Java 系统属性（`System.getProperties()`）

四 操作系统环境变量

五 RandomValuePropertySource 配置的 random.* 属性值

---

由 jar包 外向 jar包 内进行寻找；优先加载带 profile

六 **jar包 外部的 `application-{profile}.properties` 或 `application.yml(带spring.profile)` 配置文件**

七 **jar包 内部的 `application-{profile}.properties` 或 `application.yml(带spring.profile)` 配置文件**

---

由 jar包 外向 jar包 内进行寻找；再来加载不带 profile

八 **jar包 外部的 `application.properties` 或 `application.yml(不带spring.profile)` 配置文件**

九 **jar包 内部的 `application.properties` 或 `application.yml(不带spring.profile)` 配置文件**

---

十 @Configuration 注解类上的 @PropertySource

十一 通过 SpringApplication.setDefaultProperties 指定的默认属性

[可以参考官方文档](https://docs.spring.io/spring-boot/docs/1.5.22.RELEASE/reference/htmlsingle/#boot-features-external-config)

## **自动配置原理**

**这个非常重要。**

[配置文件能配置的属性参照](https://docs.spring.io/spring-boot/docs/1.5.22.RELEASE/reference/htmlsingle/#common-application-properties)

* SpingBoot 启动的时候加载主配置类，开启了自定配置功能 **`@EnableAutoConfiguration`**
* **`@EnableAutoConfiguration`** 利用 EnableAutoConfigurationImportSelector 给容器导入一些组件，它继承自 AutoConfigurationImportSelector，它包含一个很重要的方法：
  
    ```java
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        if (!isEnabled(annotationMetadata)) {
            return NO_IMPORTS;
        }
        try {
            AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader
                    .loadMetadata(this.beanClassLoader);
            AnnotationAttributes attributes = getAttributes(annotationMetadata);
            List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);   // 获取候选的配置
            // ...
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    ```

    ```java
    protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
                getBeanClassLoader());
        Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
                + "are using a custom packaging, make sure that file is correct.");
        return configurations;
    }
    ```

* loadFactoryNames 方法，会扫描所有 jar包 类路径下的 `META‐INF/spring.factories`，把扫描到的这些文件的包装成 properties 对象，从中获取到 EnableAutoConfiguration.class 类对应的值，然后把它们添加到容器中。可以看到 spring.factories 如下，每一个这样的 xxxAutoConfiguration 类都是容器中的一个组件，都加入到容器中，用它们来做自动配置。

    ```factories
    # Auto Configure
    org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
    # 省略...
    org.springframework.boot.autoconfigure.web.HttpEncodingAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.WebClientAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration,\
    org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration,\
    org.springframework.boot.autoconfigure.websocket.WebSocketMessagingAutoConfiguration,\
    org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration
    ```

### HttpEncodingAutoConfiguration 为例

它为 Http 编码自动配置。

其**源码**为：

```java
// 表示这是一个配置类，即可以向容器中添加组件。
@Configuration

// 启动指定类的 ConfigurationProperties 功能，即将配置文件中对应的值和 HttpEncodingProperties 绑定起来，
// 并把 HttpEncodingProperties 添加到 IOC 容器中。
@EnableConfigurationProperties(HttpEncodingProperties.class)

// @Conditional 的扩展注解，可以设置不同条件，如果满足指定的条件，整个配置类就会生效。这里的条件是判断当前应用是否是 Web 应用。
@ConditionalOnWebApplication

// @Conditional 的扩展注解，这里的条件是判断当前应用是否有这个类，即 SpringMVC 配置中解决乱码的过滤器。
@ConditionalOnClass(CharacterEncodingFilter.class)

// @Conditional 的扩展注解，这里的条件是判断配置文件中是否存在某个配置 spring.http.encoding.enabled，如果不存在，判断也是成立的。即使我们配置文件中不配置 pring.http.encoding.enabled=true，也是默认生效的。
@ConditionalOnProperty(prefix = "spring.http.encoding", value = "enabled", matchIfMissing = true)
public class HttpEncodingAutoConfiguration {

    // 已经和 SpringBoot 的配置文件映射了
    private final HttpEncodingProperties properties;

    // 只有一个有参构造器的情况下，参数的值就会从容器中拿
    public HttpEncodingAutoConfiguration(HttpEncodingProperties properties) {
        this.properties = properties;
    }

    // 给容器中添加一个组件，这个组件的某些值需要从 properties 中获取
    @Bean
    // @Conditional 的扩展注解，这里的条件是判断容器有没有这个组件
    @ConditionalOnMissingBean(CharacterEncodingFilter.class)
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
        filter.setEncoding(this.properties.getCharset().name());
        filter.setForceRequestEncoding(this.properties.shouldForce(Type.REQUEST));
        filter.setForceResponseEncoding(this.properties.shouldForce(Type.RESPONSE));
        return filter;
    }
}
```

一但这个配置类生效，这个配置类就会给容器中添加各种组件，这些组件的属性是从对应的 xxxxProperties 类中获取的，这些类里面的每一个属性又是和配置文件绑定的。

所有在配置文件中能配置的属性都是在 xxxxProperties 类中封装着，配置文件能配置什么就可以参照某个功能对应的这个属性类。

```java
@ConfigurationProperties(prefix = "spring.http.encoding")
public class HttpEncodingProperties {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private Charset charset = DEFAULT_CHARSET;
    private Boolean force;
    private Boolean forceRequest;
    private Boolean forceResponse;
    // ...
}
```

### 自动配置原理总结

* SpingBoot 启动会加载大量的自动配置类
* 开发时，可以看需要的功能有没有 SpringBoot 默认写好的自动配置类
* 如果有默认的自动配置类，可以再看这个自动配置类中到底配置了哪些组件
* 给容器中自动配置类添加组件的时候，会从相应的 xxxxProperties 类中获取某些属性。我们就可以在配置文件中指定这些属性的值。
* `xxxxAutoConfigurartion` 自动配置类，给容器中添加组件。`xxxxProperties` 封装配置文件中相关属性。

### @Conditional 扩展注解

它的作用是 必须在 @Conditional 指定的条件成立，才给容器中添加组件，此时配置配里面的所有内容才生效。

常用拓展注解：

| @Conditional扩展注解 | 作用（判断是否满足当前指定条件） |
| :--- | :--- |
@ConditionalOnJava | 系统的 Java 版本是否符合要求
@ConditionalOnBean | 容器中存在指定 Bean
@ConditionalOnMissingBean | 容器中不存在指定 Bean
@ConditionalOnExpression | 满足 SpEL 表达式指定
@ConditionalOnClass | 系统中有指定的类
@ConditionalOnMissingClass | 系统中没有指定的类
@ConditionalOnSingleCandidate | 容器中只有一个指定的 Bean，或者这个 Bean 是首选 Bean
@ConditionalOnProperty | 系统中指定的属性是否有指定的值
@ConditionalOnResource | 类路径下是否存在指定资源文件
@ConditionalOnWebApplication | 当前是 Web 环境
@ConditionalOnNotWebApplication | 当前不是 Web 环境
@ConditionalOnJndi | JNDI 存在指定项

我们知道，自动配置类必须在一定的条件下才能生效，那么怎么知道哪些自动配置类生效呢？

可以通过启用 `debug=true` 属性，来让控制台打印自动配置报告，这样我们就可以很方便的知道哪些自动配置类生效。

```log
# 自动配置类启用的
Positive matches:
-----------------

   DispatcherServletAutoConfiguration matched:
      - @ConditionalOnClass found required class 'org.springframework.web.servlet.DispatcherServlet'; @ConditionalOnMissingClass did not find unwanted class (OnClassCondition)
      - @ConditionalOnWebApplication (required) found 'session' scope (OnWebApplicationCondition)

   DispatcherServletAutoConfiguration.DispatcherServletConfiguration matched:
      - @ConditionalOnClass found required class 'javax.servlet.ServletRegistration'; @ConditionalOnMissingClass did not find unwanted class (OnClassCondition)
      - Default DispatcherServlet did not find dispatcher servlet beans (DispatcherServletAutoConfiguration.DefaultDispatcherServletCondition)

# 没有启动，没有匹配成功的自动配置类
Negative matches:
-----------------

   ActiveMQAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required classes 'javax.jms.ConnectionFactory', 'org.apache.activemq.ActiveMQConnectionFactory' (OnClassCondition)

   AopAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required classes 'org.aspectj.lang.annotation.Aspect', 'org.aspectj.lang.reflect.Advice' (OnClassCondition)
```

## 练习和总结
