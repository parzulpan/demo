package cn.parzulpan;

import cn.parzulpan.dao.UserDAO;
import cn.parzulpan.domain.Account;
import cn.parzulpan.domain.User;
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

public class MyBatisAccountManyTest {
    private InputStream is;
    private SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    private SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;
    private UserDAO userDAO;

    @Before
    public void init() throws Exception {
        System.out.println("Before...");
        is = Resources.getResourceAsStream("SqlMapConfig.xml");
        sqlSessionFactory = builder.build(is);
        sqlSession = sqlSessionFactory.openSession();
        userDAO = sqlSession.getMapper(UserDAO.class);
    }

    @After
    public void destroy() throws Exception {
        System.out.println("After...");
        sqlSession.commit();
        sqlSession.close();
        is.close();
    }

    @Test
    public void findAllWithAccountTest() {
        List<User> users = userDAO.findAllWithAccount();
        for (User user : users) {
            System.out.println();
            System.out.println(user);
            System.out.println(user.getAccounts());
        }
    }
}
