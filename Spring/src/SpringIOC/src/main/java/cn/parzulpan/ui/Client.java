package cn.parzulpan.ui;

import cn.parzulpan.service.AccountService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public class Client {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        AccountService as = ac.getBean("accountServiceImpl", AccountService.class);
        System.out.println(as);
        as.saveAccount();

        AccountService as2 = ac.getBean("accountServiceImpl", AccountService.class);
        System.out.println(as == as2);

        ac.close();
    }
}
