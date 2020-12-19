package cn.parzulpan;

import cn.parzulpan.dao.UserDAO;
import cn.parzulpan.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public class MyBatisAnnotationCRUDTest {
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
    public void findAllTest() {
        List<User> users = userDAO.findAll();
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void findByIdTest() {
        User user = userDAO.findById(41);
        System.out.println(user);
    }

    @Test
    public void saveUserTest() {
        User user = new User(null, "annotation username", new Date(), "男", "Beijing");
        System.out.println("save before: " + user); // User{id=null, ...}
        int i = userDAO.saveUser(user);
        System.out.println(i);
        System.out.println("save after: " + user); // User{id=53, ...}
    }

    @Test
    public void updateUserTest() {
        User user = userDAO.findById(42);
        user.setUserName("Tom Tim Tom AA");
        user.setUserAddress("瑞典");
        int i = userDAO.updateUser(user);
        System.out.println(i);
    }

    @Test
    public void deleteUserTest() {
        int i = userDAO.deleteUser(53);
        System.out.println(i);
    }

    @Test
    public void findTotalTest() {
        int total = userDAO.findTotal();
        System.out.println(total);
    }

    @Test
    public void findByNameTest() {
        List<User> users = userDAO.findByName("%Tim%");
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void findByIdHighCacheTest() {
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        UserDAO dao1 = sqlSession1.getMapper(UserDAO.class);
        User user1 = dao1.findById(41);
        System.out.println(user1.hashCode());   // 765284253
        sqlSession1.close();    // 一级缓存消失

        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        UserDAO dao2 = sqlSession2.getMapper(UserDAO.class);
        User user2 = dao2.findById(41);
        System.out.println(user2.hashCode());   // 1043351526
        sqlSession1.close();    // 一级缓存消失

        System.out.println(user1 == user2); // false

    }
}
