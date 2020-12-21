package cn.parzulpan.service;

import cn.parzulpan.domain.BankAccount;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 测试 银行账户的业务层接口的实现类
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:bean.xml")
public class BankAccountServiceImplTest {

//    @Autowired
//    private BankAccountService as;

    // 指定 BankAccountService 的代理对象
    @Resource(name = "proxyBankAccountService")
    private BankAccountService as;

    @Test
    public void findAllTest() {
        List<BankAccount> accounts = as.findAll();
        for (BankAccount account : accounts) {
            System.out.println(account);
        }
    }

    @Test
    public void transfer() {
        as.transfer("aaa", "bbb", 100.0);
    }
}