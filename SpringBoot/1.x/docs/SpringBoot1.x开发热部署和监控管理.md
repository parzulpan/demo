# SpringBoot1.x 开发热部署和监控管理

## 热部署

在开发中我们修改一个 Java 文件后想看到效果不得不重启应用，这导致大量时间花费，我们希望不重启应用的情况下，程序可以自动部署（热部署）。

* **模板引擎**
  * 在 SpringBoot 中开发情况下禁用模板引擎的 cache
  * 页面模板改变，按 CTRL+F9 可以重新编译当前页面并生效
* **Spring Loaded**
  * Spring 官方提供的热部署程序，实现修改类文件的热部署
  * 下载 [Spring Loaded](https://github.com/spring-projects/spring-loaded)
  * 添加运行时参数，javaagent:/parzulpan/dev/springloaded-xx.jar -noverify
* **JRebel**
  * 收费的一个热部署软件
  * 安装插件使用即可
* **Spring Boot Devtools**
  * **推荐使用**
  * 引入依赖

    ```xml
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
                <scope>runtime</scope>
                <optional>true</optional>
            </dependency>
    ```

  * 按 CTRL+F9 生效

## 监控管理

通过引入 spring-boot-starter-actuator，可以使用 Spring Boot 为我们提供的准生产环境下的应用监控和管理功能。我们可以通过HTTP，JMX，SSH协议来进行操作，自动得到审计、健康及指标信息等。

**使用步骤**：

* 引入依赖

    ```xml
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
    ```

* 通过 http 方式访问监控端点
* 可进行 shutdown（POST 提交，此端点默认关闭）

**监控和管理端点说明**：

* `autoconfig` 所有自动配置信息
* `auditevents` 审计事件
* `beans` 所有 Bean 的信息
* `configprops` 所有配置属性
* `dump` 线程状态信息
* `env` 当前环境信息
* `health` 应用健康状况
* `info` 当前应用信息
* `metrics` 应用的各项指标
* `mappings` 应用 @RequestMapping 映射路径
* `shutdown` 关闭当前应用（默认关闭）
* `trace` 追踪信息（最新的 http 请求）

## 定制端点信息

* 定制端点一般通过 `endpoints+端点名+属性名` 来设置，比如
  * 修改端点 id `endpoints.beans.id=mybeans`
  * 开启远程应用关闭功能 `endpoints.shutdown.enabled=true`
  * 关闭端点 `endpoints.beans.enabled=false`
  * 开启所需端点 `endpoints.enabled=false` `endpoints.beans.enabled=true`
* 定制端点访问根路径 `management.context-path=/manage`
* 关闭 http 端点 `management.port=-1`

## 自定义 HealthIndicator

自定义步骤：

* 编写一个指示器实现 HealthIndicator 接口
* 指示器的名字为 xxHealthIndicator
* 将其加入容器中

```java
/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc : 自定义健康指示器
 */

@Component
public class MyAppHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // 自定义检查方法
        //  Health.up().build() 代表健康
        // Health.down().build() 代表不健康，还可以带上信息
        return Health.down().withDetail("msg", "服务异常").build();
    }
}
```

## 练习和总结
