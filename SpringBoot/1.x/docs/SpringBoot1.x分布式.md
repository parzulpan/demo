# SpringBoot1.x 分布式

## 分布式应用

## Zookeeper&Dubbo

ZooKeeper 是用于分布式应用程序的高性能协调服务。它在一个简单的界面中公开了常见的服务，例如命名，配置管理，同步和组服务，因此您不必从头开始编写它们。您可以现成使用它来实现共识，组管理，领导者选举和状态协议。您可以根据自己的特定需求在此基础上构建。

Apache Dubbo 是一款高性能、轻量级的开源 Java 服务框架。它提供了六大核心能力：面向接口代理的高性能RPC调用，智能容错和负载均衡，服务自动注册和发现，高度可扩展能力，运行期流量调度，可视化的服务治理与运维。它最大的特点是按照分层的方式来架构，使用这种方式可以使各个层之间解耦合（或者最大限度地松耦合）。从服务模型的角度来看，Dubbo采用的是一种非常简单的模型，要么是提供方提供服务，要么是消费方消费服务，所以基于这一点可以抽象出服务提供方（Provider）和服务消费方（Consumer）两个角色。

### 安装 Zookeeper

* 下载：`docker pull zookeeper:3.4.14`
* 运行：`docker run --name zookeeper -p 2181:2181 -p 2888:2888 -p 3888:3888 -p 8088:8080 --restart always -d zookeeper:3.4.14`

### 整合使用

[本节源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/integration-zookeeper-dubbo)

* 安装 zookeeper 作为注册中心
* 编写**服务提供者**，分为三步：
  * 引入 dubbo 和 zookeeper 相关依赖

    ```xml
            <!-- https://mvnrepository.com/artifact/com.alibaba.boot/dubbo-spring-boot-starter -->
            <dependency>
                <groupId>com.alibaba.boot</groupId>
                <artifactId>dubbo-spring-boot-starter</artifactId>
                <version>0.1.2.RELEASE</version>
            </dependency>
    ```

  * 配置 dubbo 的注册中心地址和扫描包

    ```properties
    dubbo.application.name=provider-ticket
    dubbo.registry.address=zookeeper://localhost:2181
    dubbo.scan.base-packages=cn.parzulpan.ticket.service
    ```

  * 使用 @com.alibaba.dubbo.config.annotation.Service，将服务发布出去，src/main/java/cn/parzulpan/ticket/service/TicketServiceImpl.java

    ```java
    /**
    * @Author : parzulpan
    * @Time : 2021-01
    * @Desc :
    */

    @Service
    @com.alibaba.dubbo.config.annotation.Service    //将服务发布出去
    public class TicketServiceImpl implements TicketService {
        @Override
        public String getTicket() {
            return "《大闹天宫》";
        }
    }
    ```

* 编写**服务消费者**，分为三步：
  * 引入 dubbo 和 zookeeper 相关依赖

    ```xml
            <!-- https://mvnrepository.com/artifact/com.alibaba.boot/dubbo-spring-boot-starter -->
            <dependency>
                <groupId>com.alibaba.boot</groupId>
                <artifactId>dubbo-spring-boot-starter</artifactId>
                <version>0.1.2.RELEASE</version>
            </dependency>
    ```

  * 配置 dubbo 的注册中心地址

    ```properties
    dubbo.application.name=consumer-user
    dubbo.registry.address=zookeeper://localhost:2181
    ```

  * 消费服务，src/main/java/cn/parzulpan/user/service/UserService.java

    ```java
    @Service
    public class UserService {

        @Reference  // 远程引用，按照注册中心的全类名匹配的
        TicketService ticketService;

        public void get() {
            String ticket = ticketService.getTicket();
            System.out.println("买到票了：" + ticket);
        }
    }
    ```

* 测试，先启动服务提供者，然后运行测试程序

    ```java
    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class ConsumerUserApplicationTests {

        @Autowired
        UserService userService;

        @Test
        public void contextLoads() {
            userService.get();
        }
    }
    ```

## SpringBoot&SpringCloud

SpringCloud 为开发人员提供了工具，以快速构建分布式系统中的一些常见模式（例如配置管理，服务发现，断路器，智能路由，微代理，控制总线，一次性令牌，全局锁，领导选举，分布式会话，群集状态）。分布式系统的协调导致样板式样，并且使用 SpringCloud 开发人员可以快速站起来实现这些样板的服务和应用程序。它们可以在任何分布式环境中正常工作，包括开发人员自己的笔记本电脑，裸机数据中心以及 Cloud 等托管平台。

Spring Cloud 分布式开发，常用解决方案：

* Spring Cloud Alibaba
* Spring Cloud Netflix

### Spring Cloud Alibaba

[Spring Cloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba)

Spring Cloud Alibaba 致力于提供微服务开发的一站式解决方案。此项目包含开发分布式应用微服务的必需组件，方便开发者通过 Spring Cloud 编程模型轻松使用这些组件来开发分布式应用服务。依托 Spring Cloud Alibaba，您只需要添加一些注解和少量配置，就可以将 Spring Cloud 应用接入阿里微服务解决方案，通过阿里中间件来迅速搭建分布式应用系统。

**主要功能**：

* **服务限流降级**：默认支持 WebServlet、WebFlux, OpenFeign、RestTemplate、Spring Cloud Gateway, Zuul, Dubbo 和 RocketMQ 限流降级功能的接入，可以在运行时通过控制台实时修改限流降级规则，还支持查看限流降级 Metrics 监控。
* **服务注册与发现**：适配 Spring Cloud 服务注册与发现标准，默认集成了 Ribbon 的支持。
* **分布式配置管理**：支持分布式系统中的外部化配置，配置更改时自动刷新。
* **消息驱动能力**：基于 Spring Cloud Stream 为微服务应用构建消息驱动能力。
* **分布式事务**：使用 @GlobalTransactional 注解，高效并且对业务零侵入地解决分布式事务问题。
* **阿里云对象存储**：阿里云提供的海量、安全、低成本、高可靠的云存储服务。支持在任何应用、任何时间、任何地点存储和访问任意类型的数据。
* **分布式任务调度**：提供秒级、精准、高可靠、高可用的定时（基于 Cron 表达式）任务调度服务。同时提供分布式的任务执行模型，如网格任务。网格任务支持海量子任务均匀分配到所有 Worker（schedulerx-client）上执行。
* **阿里云短信服务**：覆盖全球的短信服务，友好、高效、智能的互联化通讯能力，帮助企业迅速搭建客户触达通道。

**主要组件**：

* **[Sentinel](https://github.com/alibaba/Sentinel)**：把流量作为切入点，从流量控制、熔断降级、系统负载保护等多个维度保护服务的稳定性。
* **[Nacos](https://github.com/alibaba/Nacos)**：一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。
* **[RocketMQ](https://rocketmq.apache.org/)**：一款开源的分布式消息系统，基于高可用分布式集群技术，提供低延时的、高可靠的消息发布与订阅服务。
* **[Dubbo](https://github.com/apache/dubbo)**：Apache Dubbo™ 是一款高性能 Java RPC 框架。
* **[Seata](https://github.com/seata/seata)**：阿里巴巴开源产品，一个易于使用的高性能微服务分布式事务解决方案。
* **[Alibaba Cloud OSS](https://www.aliyun.com/product/oss)**: 阿里云对象存储服务（Object Storage Service，简称 OSS），是阿里云提供的海量、安全、低成本、高可靠的云存储服务。您可以在任何应用、任何时间、任何地点存储和访问任意类型的数据。
* **[Alibaba Cloud SchedulerX](https://help.aliyun.com/document_detail/43136.html)**: 阿里中间件团队开发的一款分布式任务调度产品，提供秒级、精准、高可靠、高可用的定时（基于 Cron 表达式）任务调度服务。
* **[Alibaba Cloud SMS](https://www.aliyun.com/product/sms)**: 覆盖全球的短信服务，友好、高效、智能的互联化通讯能力，帮助企业迅速搭建客户触达通道。

**使用步骤**：

* 引入依赖，如果需要使用已发布的版本，在 dependencyManagement 中添加如下配置

    ```xml
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>2.2.3.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    ```

* 版本管理
  * 1.5.x 版本适用于 Spring Boot 1.5.x
  * 2.0.x 版本适用于 Spring Boot 2.0.x
  * 2.1.x 版本适用于 Spring Boot 2.1.x
  * 2.2.x 版本适用于 Spring Boot 2.2.x

### Spring Cloud Netflix

[Spring Cloud Netflix](https://github.com/spring-cloud/spring-cloud-netflix)

该项目通过自动配置并绑定到 Spring Environment 和其他 Spring 编程模型习惯用法，为 Spring Boot 应用程序提供了 Netflix OSS 集成。通过一些简单的注释，您可以快速启用和配置应用程序内部的通用模式，并使用经过实战检验的 Netflix 组件构建大型分布式系统。提供的模式包括**服务发现**（Eureka），**断路器**（Hystrix），**智能路由/服务网关**（Zuul），**客户端负载平衡**（Ribbon）和 **分布式配置**（Spring Cloud
 Config）。

**主要功能**：

* **服务发现**：可以注册 Eureka 实例，并且客户端可以使用 Spring 托管的 Bean 发现实例
* **服务发现**：可以使用声明性 Java 配置创建嵌入式 Eureka 服务器
* **断路器**：Hystrix 客户端可以使用简单的注释驱动的方法装饰器构建
* **断路器**：具有声明性 Java 配置的嵌入式 Hystrix 仪表板
* **客户端负载均衡器**：功能区
* **外部配置**：从 Spring Environment 到 Archaius 的桥梁（使用 Spring Boot 约定启用 Netflix 组件的本机配置）
* **路由器和过滤器**：Zuul 过滤器的自动注册，以及用于反向代理创建的简单配置约定

**主要组件**：

* spring-cloud-netflix-archaius
* spring-cloud-netflix-concurrency-limits
* spring-cloud-netflix-hystrix-contract
* spring-cloud-netflix-hystrix-dashboard
* spring-cloud-netflix-hystrix-stream
* spring-cloud-netflix-hystrix
* spring-cloud-netflix-ribbon
* spring-cloud-netflix-turbine-stream
* spring-cloud-netflix-turbine
* spring-cloud-netflix-zuul

### 整合使用

[本节源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/integration-springcloud)

* 编写 **[eureka 注册中心](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/integration-springcloud/eureka-server)**
  * 导入相关依赖
  * 编写注册中心
    * 配置 eureka 信息
    * 使用 @EnableEurekaServer 启用注册中心服务
    * 启动注册中心，访问 `http://localhost:8761/` 就能看到注册中心的界面
* 编写 **[服务提供者](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/integration-springcloud/provider-ticket)**
  * 导入相关依赖
  * 将服务提供者注册到 eureka 注册中心
    * 配置 eureka 信息
    * 可以观察到注册中心的界面，运行的示例。同一个应用也可以注册多次
      * 分别使用 8801 和 8002 端口，然后 打包启动这两个应用
* 编写 **[服务消费者](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/integration-springcloud/consumer-user)**
  * 导入相关依赖
  * 从 eureka 注册中心取得服务给消费者
    * 配置 eureka 信息
    * 使用 @EnableDiscoveryClient 启用发现服务功能
* 测试，先启动 eureka 注册中心，再启动服务提供者，最后启动服务消费者

## 总结和练习
