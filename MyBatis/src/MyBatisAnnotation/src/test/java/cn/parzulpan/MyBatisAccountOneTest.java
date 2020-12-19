package cn.parzulpan;

import cn.parzulpan.dao.AccountDAO;
import cn.parzulpan.dao.UserDAO;
import cn.parzulpan.domain.Account;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public class MyBatisAccountOneTest {
    private InputStream is;
    private SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    private SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;
    private AccountDAO accountDAO;

    @Before
    public void init() throws Exception {
        System.out.println("Before...");
        is = Resources.getResourceAsStream("SqlMapConfig.xml");
        sqlSessionFactory = builder.build(is);
        sqlSession = sqlSessionFactory.openSession();
        accountDAO = sqlSession.getMapper(AccountDAO.class);
    }

    @After
    public void destroy() throws Exception {
        System.out.println("After...");
        sqlSession.commit();
        sqlSession.close();
        is.close();
    }

    @Test
    public void findAllTest() {
        List<Account> accounts = accountDAO.findAll();
        for (Account account : accounts) {
            System.out.println();
            System.out.println(account);
            System.out.println(account.getUser());
        }
    }
}
