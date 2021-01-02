# SpringBoot1.x 消息

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/integration-message)

## 概述

大多应用中，可通过消息服务**中间件**来提升系统**异步通信**、扩展**解耦能力**。

消息服务有两个重要概念，即**消息代理**（message broker）和**目的地**（destnation），当消息发送者发送消息以后，将由消息代理接管，消息代理保证消息传递到指定目的地。

而目的地也有两种形式：

* **队列（queue）** 点对点消息通信
  * 消息发送者发送消息，消息代理将其放入一个队列中，消息接收者从队列中获取消息内容，消息读取后会被移除队列。
  * 消息只有唯一哥的发送者和接受者，但是不能说只能有一个接收者。
* **主题（topic）** 发布/订阅消息
  * 发送者/发布者发送消息到主题，多个接受者/订阅者监听订阅这个主题，那么就会在消息到达时同时收到消息。

消息队列的两个前导概念：

* **JMS**（Java Message Service）：
  * 基于 JVM 消息代理的规范
  * ActiveMQ、HornetMQ 是 JMS 实现
* **AMQP**（Advanced Message Queuing Protocol）：
  * 高级消息队列协议，也是一个消息代理的规范，兼容 JMS
  * RabbitMQ 是 AMQP 的实现
* 两者的异同：

| | JMS | AMQP |
| :--- | :--- | :--- |
| 定义 | Java Api | 网络线级协议 |
| 跨语言 | 否 | 是 |
| 跨平台 | 否 | 是 |
| Model | Peer-2-Peer、Pub/Sub | direct exchange、fanout exchange、topic change、headers exchange、system exchange |
| 支持消息类型 | TextMessage、MapMessage、BytesMessage、StreamMessage、ObjectMessage、Message （只有消息头和属性） | `byte[]`，当实际应用时，有复杂的消息，可以将消息**序列化后**发送 |
| 综合评价 | JMS 定义了 JAVAAPI 层面的标准，其对跨平台的支持较差 | AMQP 定义了网络层的协议标准，具有跨平台、跨语言特性 |

## 应用场景

### 异步处理

**场景说明**：用户注册后，需要发注册邮件和注册短信。传统的做法有两种 1.串行的方式；2.并行方式

**串行方式**：将注册信息写入数据库成功后，发送注册邮件，再发送注册短信。以上三个任务全部完成后，返回给客户端。

![异步处理串行方式](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102072809%E5%BC%82%E6%AD%A5%E5%A4%84%E7%90%86%E4%B8%B2%E8%A1%8C%E6%96%B9%E5%BC%8F.png)

**并行方式**：将注册信息写入数据库成功后，发送注册邮件的同时，发送注册短信。以上三个任务完成后，返回给客户端。与串行的差别是，并行的方式可以提高处理的时间。

![异步处理并行方式](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102072829%E5%BC%82%E6%AD%A5%E5%A4%84%E7%90%86%E5%B9%B6%E8%A1%8C%E6%96%B9%E5%BC%8F.png)

**引入消息队列后**，将不是必须的业务逻辑，异步处理。改造后的架构如下：

![异步处理引入消息队列](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102072837%E5%BC%82%E6%AD%A5%E5%A4%84%E7%90%86%E5%BC%95%E5%85%A5%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97.png)

### 应用解耦

**场景说明**：用户下单后，订单系统需要通知库存系统。传统的做法是，订单系统调用库存系统的接口。如下图：

![应用解耦传统做法](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102072847%E5%BA%94%E7%94%A8%E8%A7%A3%E8%80%A6%E4%BC%A0%E7%BB%9F%E5%81%9A%E6%B3%95.png)

传统模式的**缺点**：假如库存系统无法访问，则订单减库存将失败，从而导致订单失败，订单系统与库存系统耦合。

**引入消息队列后**，

![应用解耦引入消息队列](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102072856%E5%BA%94%E7%94%A8%E8%A7%A3%E8%80%A6%E5%BC%95%E5%85%A5%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97.png)

订单系统：用户下单后，订单系统完成持久化处理，将消息写入消息队列，返回用户订单下单成功。

库存系统：订阅下单的消息，采用拉/推的方式，获取下单信息，库存系统根据下单信息，进行库存操作。

### 流量削峰

**场景说明**：秒杀活动，一般会因为流量过大，导致流量暴增，应用挂掉。为解决这个问题，一般需要在应用前端加入消息队列。

**引入消息队列后**，

![流量削峰引入消息队列](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102072909%E6%B5%81%E9%87%8F%E5%89%8A%E5%B3%B0%E5%BC%95%E5%85%A5%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97.png)

可以控制活动的人数，也可以缓解短时间内高流量压垮应用。

### 日志处理

**场景说明**：日志处理是指将消息队列用在日志处理中，比如Kafka的应用，解决大量日志传输的问题。

**引入消息队列后**，

![日志处理引入消息队列](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102072920%E6%97%A5%E5%BF%97%E5%A4%84%E7%90%86%E5%BC%95%E5%85%A5%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97.png)

日志采集客户端：负责日志数据采集，定时写受写入 Kafka 队列。

Kafka 消息队列：负责日志数据的接收，存储和转发。

日志处理应用：订阅并消费 kafka 队列中的日志数据。

### 消息通讯

**场景说明**：息队列一般都内置了高效的通信机制，因此也可以用在纯的消息通讯。比如实现点对点消息队列，或者聊天室等。

**引入消息队列后**，

点对点通讯：客户端A和客户端B使用同一队列，进行消息通讯。

![点对点通讯引入消息队列](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102072928%E7%82%B9%E5%AF%B9%E7%82%B9%E9%80%9A%E8%AE%AF%E5%BC%95%E5%85%A5%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97.png)

聊天室通讯：客户端A，客户端B，客户端N订阅同一主题，进行消息发布和接收，实现类似聊天室效果。

![聊天室通讯引入消息队列](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102072934%E8%81%8A%E5%A4%A9%E5%AE%A4%E9%80%9A%E8%AE%AF%E5%BC%95%E5%85%A5%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97.png)

## RabbitMQ

**Spring 支持**：

* spring-jms 提供了对 JMS 的支持
* spring-rabbit 提供了对 AMQP 的支持
* 需要 ConnectionFactory 的实现来连接消息代理
* 提供 JmsTemplate、RabbitTemplate 来发送消息
* @JmsListener（JMS）、@RabbitListener（AMQP）注解在方法上监听消息代理发布的消息
* @EnableJms、@EnableRabbit 开启支持

**Spring Boot 自动配置**：

* JmsAutoConfiguration
* RabbitAutoConfiguration

RabbitMQ 是部署最广泛的开源消息代理，它是由 Erlang 开发的 AMQP 的开源实现。

### 基本概念

* **Message** 消息。消息是不具名的，它由消息头和消息体组成。消息体是不透明的，而消息头则由一系列的可选属性组成，这些属性包括：
  * routing-key（路由键）
  * priority（相对于其他消息的优先权）
  * delivery-mode（指出该消息可能需要持久性存储）
* **Exchange** 交换器。用来接收生产者发送的消息并将这些消息路由给服务器中的队列。
* **Queue** 消息队列。用来保存消息直到发送给消费者。它是消息的容器，也是消息的终点。一个消息可投入一个或多个队列。消息一直在队列里面，等待消费者连接到这个队列将其取走。
* **Binding** 绑定。用于消息队列和交换器之间的关联。一个绑定就是基于路由键将交换器和消息队列连接起来的路由规则，所以可以**将交换器理解成一个由绑定构成的路由表**。Exchange 和 Queue 的绑定可以是多对多的关系。
* **Connection** 网络连接。比如一个 TCP 连接。
* **Channel** 信道。多路复用连接中的一条独立的双向数据流通道。信道是建立在真实的TCP连接内的虚拟连接，AMQP 命令都是通过信道发出去的，不管是发布消息、订阅队列还是接收消息，这些动作都是通过信道完成。因为对于操作系统来说建立和销毁 TCP 都是非常昂贵的开销，所以引入了信道的概念，以复用一条 TCP 连接。
* **Publisher** 消息的生产者。是一个向交换器发布消息的客户端应用程序。
* **Consumer** 消息的消费者。是一个从消息队列中取得消息的客户端应用程序。
* **Virtual Host** 虚拟主机。表示一批交换器、消息队列和相关对象。虚拟主机是共享相同的身份认证和加密环境的独立服务器域。每个 vhost 本质上就是一个 mini 版的 RabbitMQ 服务器，拥有自己的队列、交换器、绑定和权限机制。vhost 是 AMQP 概念的基础，必须在连接时指定。RabbitMQ 默认的 vhost 是 `/` 。
* **Broker** 消息队列服务器实体。

这个概念之间的**关系**为：

![消息队列概念图](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102074110%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97%E6%A6%82%E5%BF%B5%E5%9B%BE.png)

### 运行机制

**AMQP 中的消息路由**：AMQP 中消息的路由过程和 Java 开发者熟悉的 JMS 存在一些差别，AMQP 中增加了 Exchange 和 Binding 的角色。生产者把消息发布到 Exchange 上，消息最终到达队列并被消费者接收，而 Binding 决定交换器的消息应该发送到那个队列。

![AMQP中的消息路由](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102075059AMQP%E4%B8%AD%E7%9A%84%E6%B6%88%E6%81%AF%E8%B7%AF%E7%94%B1.png)

**Exchange 类型**：

* **Direct Exchange** 消息中的路由键（routing key）如果和 Binding 中的 binding key 一致， 交换器就将消息发到对应的队列中。路由键与队列名完全匹配，如果一个队列绑定到交换机要求路由键为 “dog”，则只转发 routing key 标记为 “dog” 的消息，不会转发 “dog.puppy”，也不会转发 “dog.guard” 等等。它是**完全匹配**、**单播**的模式。

    ![DirectExchange](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102075121DirectExchange.png)

* **Fanout Exchange** 每个发到 fanout 类型交换器的消息都会分到所有绑定的队列上去。fanout 交换器**不处理路由键**，只是简单的将队列绑定到交换器上，每个发送到交换器的消息都会被转发到与该交换器绑定的所有队列上。它是广播的模式。

    ![FanoutExchange](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102075146FanoutExchange.png)

* **Topic Exchange** 它通过模式匹配分配消息的路由键属性，将路由键和某个模式进行匹配，此时队列需要绑定到一个模式上。它将路由键和绑定键的字符串切分成单词，**这些单词之间用点隔开**。它同样也会识别两个通配符：符号 `“#”` 和符号 `“*”`。# 匹配 0 个或多个单词，* 匹配 1 个单词。

    ![TopicExchange](https://images.cnblogs.com/cnblogs_com/parzulpan/1907498/o_210102075154TopicExchange.png)

### 安装测试

* Docker 安装 RabbitMQ：`docker pull rabbitmq:3.7.28-management`
* 启动 RabbitMQ：`docker run --name rabbitmq -p 5672:5672 -p 15672:15672 -d rabbitmq:3.7.28-management`
* 进入**管理页面** `http://localhost:15672/`，输入 用户名 guest，密码 guest
* 在管理页面添加 **Exchanges** 和 **Queues**
* 引入 spring-boot-starter-amqp

    ```xml
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-amqp</artifactId>
            </dependency>
    ```

* 编写配置文件：

    ```properties
    spring.rabbitmq.username=guest
    spring.rabbitmq.password=guest
    ```

* 主配置类添加上 `@EnableRabbit`，编写测试文件：

    ```java
    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class IntegrationMessageApplicationTests {

        @Autowired
        RabbitTemplate rabbitTemplate;

        /**
         * 发送消息
         * 单播，一对一的
         */
        @Test
        public void testDirectExchangeSend() {
            // 方式1：使用特定的路由密钥将消息发送到特定的交换机。
            // 需要构造一个 Message(byte[] body, MessageProperties messageProperties) ，定义消息体内容和消息头
            // rabbitTemplate.send(String exchange, String routingKey, Message message);

            // 方式2：将 Java对象 转换为 Amqp Message，会自动序列化，然后使用特定的路由密钥将其发送到特定的交换机。
            // rabbitTemplate.convertAndSend(String exchange, String routingKey, Object message);
            HashMap<String, Object> map = new HashMap<>();
            map.put("msg", "第一个消息");
            map.put("data", Arrays.asList("HelloWorld", 1024, true, new Book(12315125, "RabbitMQ 实战", "parzulpan")));
            rabbitTemplate.convertAndSend("exchange.direct", "parzulpan.news", map);    // 对象以默认 jdk 序列化的形式发送
        }

        /**
         * 接收消息
         */
        @Test
        public void testDirectExchangeReceive() {
            // 方式1
            // rabbitTemplate.receive(String queueName)

            // 方式2
            // rabbitTemplate.receiveAndConvert(String queueName)

            Object receive = rabbitTemplate.receiveAndConvert("parzulpan.news");
            System.out.println(receive.getClass());
            System.out.println(receive);
        }

        /**
         * 发送消息
         * 广播，一对多的
         */
        @Test
        public void testFanoutExchangeSend() {
            rabbitTemplate.convertAndSend("exchange.fanout", "", new Book(124123561, "RabbitMQ 源码剖析", "parzulpan"));
        }

        /**
         * 接收消息
         */
        @Test
        public void testFanoutExchangeReceive() {
            Object receive = rabbitTemplate.receiveAndConvert("parzulpan");
            System.out.println(receive.getClass());
            System.out.println(receive);
        }


        /**
         * 发送消息
         * 模式匹配
         */
        @Test
        public void testTopicExchangeSend() {
            rabbitTemplate.convertAndSend("exchange.topic", "parzulpan.#", new Book(1541351332, "RabbitMQ 优化", "parzulpan"));
        }

        /**
         * 接收消息
         */
        @Test
        public void testTopicExchangeReceive() {
            Object receive1 = rabbitTemplate.receiveAndConvert("parzulpan");
            System.out.println(receive1.getClass());
            System.out.println(receive1);
            Object receive2 = rabbitTemplate.receiveAndConvert("parzulpan.emps");
            System.out.println(receive2.getClass());
            System.out.println(receive2);
        }
    }
    ```

* 为了 Json 格式序列化，可以自定义消息转换器

    ```java
    /**
     * @Author : parzulpan
     * @Time : 2021-01
     * @Desc : 自定义 AMQP 配置类
     */

    @Configuration
    public class CustomAMQPConfig {

        /**
         * 自定义消息转换器
         */
        @Bean
        public MessageConverter messageConverter() {
            return new Jackson2JsonMessageConverter();
        }
    }
    ```

### 消息监听

可以在业务层方法上监听消息队列的内容：

```java
/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc : 数据业务类
 */

@Service
public class BookService {

    @RabbitListener(queues = {"parzulpan", "parzulpan.emps"})
    public void receive(Book book) {
        System.out.println("收到消息: " + book);
    }
}
```

当监听的队列收到消息时就会执行方法。

### AmqpAdmin 使用

之前的安装测试，都是在管理页面添加的 Exchanges 和 Queues 等，也可以通过 AmqpAdmin 创建和删除 Exchanges、Queues、Binding 等。

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrationMessageApplicationTests {
    @Autowired
    AmqpAdmin amqpAdmin;

    @Test
    public void testAmqpAdmin() {
        // 创建 Exchange
        amqpAdmin.declareExchange(new DirectExchange("AmqpAdminExchange.direct", true, false));

        // 创建 Queue
        amqpAdmin.declareQueue(new Queue("AmqpAdmin.queue", true));

        // 创建 Binding
        amqpAdmin.declareBinding(new Binding("AmqpAdmin.queue", Binding.DestinationType.QUEUE,
                "AmqpAdminExchange.direct","AmqpAdmin.parzulpan", null));
    }

    @Test
    public void testAmqpAdminDelete() {
        // 创建 Exchange
        amqpAdmin.deleteExchange("AmqpAdminExchange.direct");

        // 创建 Queue
        amqpAdmin.deleteQueue("AmqpAdmin.queue");
    }
}
```

## 常用 MQ 对比

[参考 常见主流MQ之间的对比](https://blog.csdn.net/MasonCen/article/details/84990339?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.control&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.control)

[RabbitMQ高频面试题](https://blog.csdn.net/Design407/article/details/103636161?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-3.control&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-3.control)

## 练习和总结
