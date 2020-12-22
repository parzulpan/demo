package cn.parzulpan.service;

import cn.parzulpan.domain.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 对 账户的业务层 进行单元测试
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:bean.xml")
public class AccountServiceImplTest {

    @Autowired
    private  AccountService as;

    @Test
    public void findById() {
        Account account = as.findById(1);
        System.out.println(account);
    }

    @Test
    public void transfer() {
        as.transfer("aaa", "bbb", 100.0);
    }
}