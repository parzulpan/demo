package cn.parzulpan.test;

import cn.parzulpan.dao.AccountDAO;
import cn.parzulpan.domain.Account;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 多表查询
 */

public class MyBatisQueryTest {
    private InputStream is;
    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    private SqlSessionFactory factory;
    private SqlSession session;
    private AccountDAO accountDAO;

    @Before
    public void init() throws IOException {
        is = Resources.getResourceAsStream("SqlMapConfig.xml");
        factory = builder.build(is);
        session = factory.openSession();
        accountDAO = session.getMapper(AccountDAO.class);
    }

    @After
    public void destroy() throws IOException {
        session.commit();   // 事务提交
        session.close();
        is.close();
    }

    @Test
    public void findAllTest() {
        List<Account> accounts = accountDAO.findAll();
        for (Account account : accounts) {
            System.out.println(account);
            System.out.println(account.getUser());
        }
    }

    @Test
    public void findAllLazyTest() {
        List<Account> accounts = accountDAO.findAllLazy();

    }
}
