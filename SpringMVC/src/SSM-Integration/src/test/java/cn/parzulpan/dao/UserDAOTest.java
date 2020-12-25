package cn.parzulpan.dao;

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

import static org.junit.Assert.*;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 对 用户持久层接口  进行单元测试
 */

public class UserDAOTest {
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
    public void findAll() {
        List<User> users = userDAO.findAll();
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void findById() {
        User user = userDAO.findById(1);
        System.out.println(user);
    }

    @Test
    public void findByName() {
        User user = userDAO.findByName("admin");
        System.out.println(user);
    }

    @Test
    public void save() {
        userDAO.save(new User(null, "test", "test1234"));
    }
}