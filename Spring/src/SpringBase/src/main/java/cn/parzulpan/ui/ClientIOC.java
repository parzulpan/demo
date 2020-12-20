package cn.parzulpan.ui;

import cn.parzulpan.dao.AccountDAO;
import cn.parzulpan.service.AccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 使用 IOC
 */

public class ClientIOC {
    public static void main(String[] args) {
        // 使用 ApplicationContext 接口，获取 Spring 核心容器
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        // 根据 id 获取 Bean 对象
        AccountService as = ac.getBean("accountService", AccountService.class);
        System.out.println(as);
        AccountDAO ad = ac.getBean("accountDAO", AccountDAO.class);
        System.out.println(ad);

        System.out.println("------");

        //
        AccountService asi = ac.getBean("accountServiceIOC", AccountService.class);
        System.out.println(asi);
        AccountDAO adi = ac.getBean("accountDAOIOC", AccountDAO.class);
        System.out.println(adi);
        adi.saveAccount();

        ac.close(); // 手动关闭容器

    }

}
