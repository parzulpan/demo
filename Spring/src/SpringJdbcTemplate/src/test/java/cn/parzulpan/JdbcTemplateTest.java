package cn.parzulpan;


import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : JdbcTemplate 对象的创建和基本使用
 */

public class JdbcTemplateTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        JdbcTemplate jt = ac.getBean("jdbcTemplate", JdbcTemplate.class);
        jt.execute("insert into bankAccount(name, money) values ('caf', 25415.6)");
    }
}
