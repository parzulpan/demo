package cn.parzulpan;

import cn.parzulpan.service.AccountService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 测试 AOP XML 配置
 */

public class XmlAOPTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        AccountService as = ac.getBean("accountService", AccountService.class);
        as.saveAccount();
        System.out.println();
        as.updateAccount(1024);
        System.out.println();
        as.deleteAccount();
    }
}
