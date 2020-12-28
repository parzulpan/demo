# SpringBoot1.x 日志

## 日志框架

市面上有很多日志框架，一个日志框架一般包括抽象层和实现。

SpringBoot，它的底层是 Spring，而 Spring 框架默认是用 JCL（java.util.logging），但是 SpringBoot 选用 **slf4j（抽象）** 和 **logback（实现）**。

## slf4j 使用

开发时，日志记录方法的调用，不应该来直接调用日志的实现类，而是调用日志抽象层里面的方法。

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello World");
  }
}
```

SLF4J 不依赖任何特殊类的装载机。实际上，每个 SLF4J 绑定在编译时都进行了硬连线，以使用一个且仅一个特定的日志记录框架。图形化说明：

![SLF4J图形化说明](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_201227142515SLF4J%E5%9B%BE%E5%BD%A2%E5%8C%96%E8%AF%B4%E6%98%8E.png)

每一个日志的实现框架都有自己的配置文件。使用 slf4j 以后，配置文件还是使用日志实现框架自己本身的配置文件。

为了统一日志记录，即使是别的框架也统一使用 slf4j 进行输出。为了实现这个目的，必须桥接其他 API，SLF4J 附带了几个桥接模块，这些模块会将对 log4j，JCL 和 java.util.logging API 的调用重定向为行为，就好像是对 SLF4J API 进行的操作一样。如下图：

![SLF4J桥接旧版API](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_201227143341SLF4J%E6%A1%A5%E6%8E%A5%E6%97%A7%E7%89%88API.png)

如何让系统中所有的日志都统一到 slf4j，使用如下步骤：

* 将系统中其他日志框架先排除出去；
* 用中间包来替换原有的日志框架；
* 导入 slf4j 其他的实现

SpingBoot 日志依赖关系：

![SpingBoot日志依赖关系](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_201227145153SpingBoot%E6%97%A5%E5%BF%97%E4%BE%9D%E8%B5%96%E5%85%B3%E7%B3%BB.png)

SpringBoot 能自动适配所有的日志，而且底层使用 slf4j+logback 的方式记录日志，引入其他框架的时候，只需要把这个框架依赖的日志框架移除掉即可。

比如说，Spring 用的是 commons-logging，但是 SpringBoot 也进行了如下处理：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring‐core</artifactId>
    <exclusions>
        <exclusion>
            <groupId>commons‐logging</groupId>
            <artifactId>commons‐logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 日志使用

### 默认配置

SpringBoot 默认帮我们配置好了日志，可以直接使用。

```java
package cn.parzulpan;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoggingApplicationTests {

    // 记录器
    Logger logger =  LoggerFactory.getLogger(getClass());

    @Test
    public void contextLoads() {
//        System.out.println();

        // SpringBoot 默认给我们使用的是 info 级别的，没有指定级别的就用 SpringBoot 默认规定的级别
        // 日志的级别为 trace < debug < info < warn < error，可以调整输出的日志级别
        // 日志就只会在这个级别以后的高级别生效，所以不会输出 trace 和 debug
        logger.trace("这是trace日志...");
        logger.debug("这是debug日志...");
        logger.info("这是info日志...");
        logger.warn("这是warn日志...");
        logger.error("这是error日志...");
    }

}
```

可以看到，控制台输出：

```log
2020-12-27 23:48:25.385  INFO 8146 --- [           main] cn.parzulpan.LoggingApplicationTests     : 这是info日志...
2020-12-27 23:48:25.385  WARN 8146 --- [           main] cn.parzulpan.LoggingApplicationTests     : 这是warn日志...
2020-12-27 23:48:25.385 ERROR 8146 --- [           main] cn.parzulpan.LoggingApplicationTests     : 这是error日志...
```

**日志输出格式：**

* %d 表示日期时间
* %thread 表示线程名，
* %‐5level 级别从左显示5个字符宽度
* %logger{50} 表示 logger 名字最长 50 个字符，否则按照句点分割
* %msg 日志消息
* %n 换行符
* 例子：`%d{yyyy‐MM‐dd HH:mm:ss.SSS} [%thread] %‐5level %logger{50} ‐ %msg%n`

为了可以输出 trace 和 debug 级别日志，可以修改 SpringBoot 默认配置

```properties
# 修改日志级别
logging.level.cn.parzulpan = trace

# 可以指定日志完整的路径，不指定路径在当前项目下生成 springboot.log 日志
logging.file=logging/springboot.log

#  在当前磁盘的根路径下创建 spring 文件夹和里面的 log 文件夹，使用 spring.log 作为默认文件
#logging.path=/spring/log

# 在控制台输出的日志的格式
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} ----> [%thread] ---> %-5level %logger{50} - %msg%n

# 指定文件中日志输出的格式
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} ==== [%thread] ==== %-5level %logger{50} - %msg%n
```

### 指定配置

[具体可查看官方文档](https://docs.spring.io/spring-boot/docs/1.5.22.RELEASE/reference/html/boot-features-logging.html)

## 练习和总结
