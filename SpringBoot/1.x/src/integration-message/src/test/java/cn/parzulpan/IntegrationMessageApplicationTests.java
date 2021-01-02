package cn.parzulpan;


import cn.parzulpan.bean.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrationMessageApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

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
