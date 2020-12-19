package cn.parzulpan.test;

import cn.parzulpan.dao.UserDAO;
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
import java.nio.file.Paths;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 缓存
 */

public class MyBatisCacheTest {
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
    public void findByIdCacheTest() {
        User user1 = userDAO.findByIdCache(41);
        System.out.println(user1.hashCode());  // 1439337960
        User user2 = userDAO.findByIdCache(41);
        System.out.println(user2.hashCode());  // 1439337960

        System.out.println(user1 == user2); // true
    }

    @Test
    public void findByIdCacheClearTest() {
        User user1 = userDAO.findByIdCache(41);
        System.out.println(user1.hashCode());  // 1439337960

        // 使缓存消失方法一：关闭 SqlSession 对象
        // session.close();

        // 使缓存消失方法二
        session.clearCache();

        // session = factory.openSession();
        userDAO = session.getMapper(UserDAO.class);

        User user2 = userDAO.findByIdCache(41);
        System.out.println(user2.hashCode());  // 315860201

        System.out.println(user1 == user2); // false
    }

    @Test
    public void findByIdHighCacheTest() {
        SqlSession sqlSession1 = factory.openSession();
        UserDAO dao1 = sqlSession1.getMapper(UserDAO.class);
        User user1 = dao1.findByIdHighCache(41);
        System.out.println(user1.hashCode());   // 765284253
        sqlSession1.close();    // 一级缓存消失

        SqlSession sqlSession2 = factory.openSession();
        UserDAO dao2 = sqlSession2.getMapper(UserDAO.class);
        User user2 = dao2.findByIdHighCache(41);
        System.out.println(user2.hashCode());   // 1043351526
        sqlSession1.close();    // 一级缓存消失

        System.out.println(user1 == user2); // false

    }
}
