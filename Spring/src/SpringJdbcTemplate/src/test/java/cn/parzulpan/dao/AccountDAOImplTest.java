package cn.parzulpan.dao;

import domain.Account;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户持久层实现类的测试
 */

public class AccountDAOImplTest {
    ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
//    AccountDAO ad = ac.getBean("accountDAO1", AccountDAO.class);
    AccountDAO ad = ac.getBean("accountDAO2", AccountDAO.class);

    @Test
    public void findAll() {
        List<Account> accounts = ad.findAll();
        for (Account account : accounts) {
            System.out.println(account);
        }
    }

    @Test
    public void findById() {
        Account account = ad.findById(1);
        System.out.println(account);
    }

    @Test
    public void findByName() {
        Account account = ad.findByName("aaa");
        System.out.println(account);
    }

    @Test
    public void update() {
        ad.update(new Account(6, "update", 214.0));
    }

    @Test
    public void insert() {
        ad.insert(new Account(99, "insert", 3125616.425));
    }

    @Test
    public void delete() {
        ad.delete(7);
    }

    @Test
    public void getCount() {
        Long count = ad.getCount();
        System.out.println(count);
    }
}