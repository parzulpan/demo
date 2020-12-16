package cn.parzulpan.test;

import cn.parzulpan.dao.UserDAO;
import cn.parzulpan.domain.QueryV;
import cn.parzulpan.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public class MyBatisCRUDTest {
    private InputStream is;
    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    private SqlSessionFactory factory;
    private SqlSession session;
    private UserDAO userDAO;

    @Before
    public void init() throws IOException {
        is = Resources.getResourceAsStream("SqlMapConfig.xml");
        factory = builder.build(is);
        session = factory.openSession();
        userDAO = session.getMapper(UserDAO.class);
    }

    @After
    public void destroy() throws IOException {
        session.commit();   // 事务提交
        session.close();
        is.close();
    }

    @Test
    public void findAllTest() {
        List<User> userList = userDAO.findAll();
        userList.forEach(System.out::println);
    }

    @Test
    public void findByIdTest() {
        User user = userDAO.findById(41);
        System.out.println(user);
    }

    @Test
    public void saveUserTest() {
        User user = new User(null, "modify username", new Date(), "男", "Beijing");
        System.out.println("save before: " + user); // User{id=null, ...}
        int i = userDAO.saveUser(user);
        System.out.println(i);
        System.out.println("save after: " + user); // User{id=52, ...}
    }

    @Test
    public void updateUserTest() {
       User user = userDAO.findById(42);
       user.setUsername("Tom Tim");
       user.setAddress("瑞典");
       int i = userDAO.updateUser(user);
       System.out.println(i);
    }

    @Test
    public void deleteUserTest() {
        int i = userDAO.deleteUser(49);
        System.out.println(i);
    }

    @Test
    public void findByNameTest() {
        List<User> userList = userDAO.findByName("%Tim%");
        userList.forEach(System.out::println);
    }

    @Test
    public void findByNameV2Test() {
        List<User> userList = userDAO.findByNameV2("Tim");
        userList.forEach(System.out::println);
    }

    @Test
    public void findTotalTest() {
        int total = userDAO.findTotal();
        System.out.println(total);
    }

    @Test
    public void findByQueryVTest() {
        User user = new User();
        user.setUsername("%Tim%");
        QueryV queryV = new QueryV();
        queryV.setUser(user);
        List<User> userList = userDAO.findByQueryV(queryV);
        userList.forEach(System.out::println);
    }
}
