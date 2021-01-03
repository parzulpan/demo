# SpringBoot1.x 任务

[文章源码](https://github.com/parzulpan/demo/tree/main/SpringBoot/1.x/src/integration-task)

## 异步任务

在 Java 应用中，绝大多数情况下都是通过同步的方式来实现交互处理的。但是在处理与第三方系统交互的时候，容易造成响应迟缓的情况，之前大部分都是使用
**多线程**来完成此类任务，其实，在 Spring 3.x 之后，就已经内置了 `@Async` 来完美解决这个问题。

* AsyncService.java

    ```java
    /**
     * @Author : parzulpan
     * @Time : 2021-01
     * @Desc :
     */

    @Service
    public class AsyncService {

        @Async  // 告诉 Spring 这是一个异步方法
        public void hello() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("数据处理中");
        }
    }
    ```

* AsyncController.java

    ```java
    /**
     * @Author : parzulpan
     * @Time : 2021-01
     * @Desc :
     */

    @Service
    public class AsyncService {

        @Async  // 告诉 Spring 这是一个异步方法
        public void hello() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("数据处理中");
        }
    }
    ```

* 主配置类开启异步注解支持

    ```java
    @EnableAsync    // 开启异步注解支持
    @SpringBootApplication
    public class IntegrationTaskApplication {}
    ```

## 定时任务

项目开发中经常需要执行一些定时任务，比如需要在每天凌晨时候，分析一次前一天的日志信息。Spring 为我们提供了异步执行任务调度的方式，提供
`TaskExecutor`、`TaskScheduler` 接口。

* ScheduleService.java

    ```java
    /**
     * @Author : parzulpan
     * @Time : 2021-01
     * @Desc :
     */

    @Service
    public class ScheduleService {

        /**
         * cron 表达式
         *
         * second(秒), minute（分）, hour（时）, day of month（日）, month（月）, day of week（周几）.
         * 示例：0 * * * * 0-6  代表从周日到周六，每分钟的第0秒执行
         *
         * 常用：
         *  【0 0/5 14,18 * * ?】 每天 14 点整，和 18 点整，每隔 5 分钟执行一次
         *  【0 15 10 ? * 1-6】 每个月的周一至周六 10:15 执行一次
         *  【0 0 2 ? * 6L】 每个月的最后一个周六凌晨 2:00 执行一次
         *  【0 0 2 LW * ?】 每个月的最后一个工作日凌晨 2:00 执行一次
         *  【0 0 2-4 ? * 1#1】 每个月的第一个周一凌晨2:00 到 4:00期间，每个整点都执行一次；
         */
        // @Scheduled(cron = "0 * * * * 0-6")
        // @Scheduled(cron = "0,1,2,3,4 * * * * 0-6")
        // @Scheduled(cron = "0-4 * * * * 0-6")
        @Scheduled(cron = "0/4 * * * * 0-6")  // 每 4 秒执行一次
        public void runHello() {
            System.out.println("runHello...");
        }
    }
    ```

* 主配置类开启定时任务注解支持

    ```java
    @EnableScheduling   // 开启定时任务注解支持
    @EnableAsync    // 开启异步注解支持
    @SpringBootApplication
    public class IntegrationTaskApplication {}
    ```

* cron 表达式

    | 字段 | 允许值 | 允许的特殊字符 |
    | :--- | :--- | :--- |
    | 秒 | 0-59 | `, - * /` |
    | 分 | 0-59 | `, - * /` |
    | 小时 | 0-23 | `, - * /` |
    | 日期 | 1-31 | `, - * ? / L W C` |
    | 月份 | 1-12 | `, - * /` |
    | 星期 | 0-7 或 SUN-SAT，0 和 7 都是 SUN | `, - * ? / L C #` |

* 特殊字符
  * `,` 枚举
  * `-` 区间
  * `*` 任意
  * `/` 步长
  * `?` 日/星期冲突匹配
  * `L` 最后
  * `W` 工作日
  * `C` 和 calendar 联系后计算过的值
  * `#` 星期，4#2，第2个星期四

## 邮件任务

邮件发送需要引入 `spring-boot-starter-mail`，SpringBoot 自动配置 `MailSenderAutoConfiguration`，然后定义 `MailProperties` 内容，配置在配置文件中，IOC 容器会自动装配 JavaMailSender。

* 加入依赖

    ```xml
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-mail</artifactId>
            </dependency>
    ```

* 编辑配置文件

    ```yml
    spring:
    mail:
        username: 1129768687@qq.com
        password: wiacrbulowhhgagh # 使用授权码
        host: smtp.qq.com
    #    properties:
    #      mail.smtp.ssl.enable: true  # 开启 smtp 服务器
    ```

* 编写测试文件

    ```java
    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class IntegrationTaskApplicationTests {

        @Autowired
        JavaMailSenderImpl javaMailSender;

        @Test
        public void sendSimpleMail() {
            // 定义一个简单邮件消息
            SimpleMailMessage message = new SimpleMailMessage();

            // 邮件设置
            message.setSubject("通知-明天放假");  // 标题
            message.setText("2020年元旦节放假一天～");    // 内容
            message.setTo(new String[]{"parzulpan@163.com", "parzulpan@gmail.com"});
            message.setFrom("1129768687@qq.com");

            // 发送
            javaMailSender.send(message);
        }

        @Test
        public void sendMimeMail() throws MessagingException {
            // 定义一个复杂邮件消息
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 邮件设置
            helper.setSubject("通知-后天放假");
            helper.setText("<b style='color:red'> 2020年元旦节放假三天～ </b>", true);
            helper.setTo(new String[]{"parzulpan@163.com", "parzulpan@gmail.com"});
            helper.setFrom("1129768687@qq.com");

            // 上传附件
            helper.addAttachment("hello.java", new File("src/main/resources/static/hello.java"));
            helper.addAttachment("猫.png", new File("src/main/resources/static/猫.png"));

            javaMailSender.send(message);
        }
    }
    ```

## 练习和总结
