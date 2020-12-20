package cn.parzulpan.ui;

import cn.parzulpan.service.AccountService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public class ClientDI {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        AccountService asi = ac.getBean("accountServiceDI", AccountService.class);
        System.out.println(asi);
        asi.saveAccount();  // call saveAccount() parzulpan 100 Sun Dec 20 19:27:49 CST 2020

        AccountService asi2 = ac.getBean("accountServiceDI2", AccountService.class);
        System.out.println(asi2);
        asi2.saveAccount();  // call saveAccount() 库里 30 Sun Dec 20 20:11:01 CST 2020

        AccountService asi3 = ac.getBean("accountServiceDI3", AccountService.class);
        System.out.println(asi3);
        asi3.saveAccount();

    }
}
