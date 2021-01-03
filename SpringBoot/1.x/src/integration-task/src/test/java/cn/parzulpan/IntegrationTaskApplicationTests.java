package cn.parzulpan;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

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
