package cn.parzulpan.service;

import cn.parzulpan.domain.BankAccount;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;


/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 对银行账户的业务层接口的实现类进行单元测试
 * 可以看到，每个测试方法都重新获取了一次 Spring 的核心容器，造成了不必要的重复代码，这个问题可以整合 Junit 解决
 */

public class BankAccountServiceImplTest {

    @Test
    public void findAll() {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        BankAccountService as = ac.getBean("bankAccountService", BankAccountService.class);
        List<BankAccount> accounts = as.findAll();
        for (BankAccount account : accounts) {
            System.out.println(account);
        }
    }

    @Test
    public void findById() {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        BankAccountService as = ac.getBean("bankAccountService", BankAccountService.class);
        BankAccount account = as.findById(1);
        System.out.println(account);
    }

    @Test
    public void save() {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        BankAccountService as = ac.getBean("bankAccountService", BankAccountService.class);
        as.save(new BankAccount(null, "ta", 4325.12314));
    }

    @Test
    public void update() {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        BankAccountService as = ac.getBean("bankAccountService", BankAccountService.class);
        BankAccount account = as.findById(1);
        account.setMoney(5153.325);
        as.update(account);
    }

    @Test
    public void deleteById() {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        BankAccountService as = ac.getBean("bankAccountService", BankAccountService.class);
        as.deleteById(4);
    }
}