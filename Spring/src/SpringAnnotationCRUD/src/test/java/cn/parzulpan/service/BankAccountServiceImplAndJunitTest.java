package cn.parzulpan.service;

import cn.parzulpan.config.SpringConfiguration;
import cn.parzulpan.domain.BankAccount;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 对银行账户的业务层接口的实现类进行单元测试，整合 Junit 解决重复代码问题，使用注解
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class BankAccountServiceImplAndJunitTest {
    @Autowired
    private BankAccountService as;

    @Test
    public void findAll() {
        // 通过注解获取容器
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        BankAccountService as = ac.getBean("bankAccountService", BankAccountService.class);
        List<BankAccount> accounts = as.findAll();
        for (BankAccount account : accounts) {
            System.out.println(account);
        }
    }

    @Test
    public void findById() {
        // 整合 Junit
        BankAccount account = as.findById(1);
        System.out.println(account);
    }

    @Test
    public void save() {
        // 整合 Junit
        as.save(new BankAccount(null, "ta", 4325.12314));
    }

    @Test
    public void update() {
        // 整合 Junit
        BankAccount account = as.findById(1);
        account.setMoney(5153.325);
        as.update(account);
    }

    @Test
    public void deleteById() {
        // 整合 Junit
        as.deleteById(4);
    }
}