package cn.parzulpan.test;

import cn.parzulpan.dao.AccountDAO;
import cn.parzulpan.dao.RoleDAO;
import cn.parzulpan.domain.Account;
import cn.parzulpan.domain.Role;
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
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 多表查询
 */

public class MyBatisManyQueryTest {
    private InputStream is;
    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    private SqlSessionFactory factory;
    private SqlSession session;
    private RoleDAO roleDAO;

    @Before
    public void init() throws IOException {
        is = Resources.getResourceAsStream("SqlMapConfig.xml");
        factory = builder.build(is);
        session = factory.openSession();
        roleDAO = session.getMapper(RoleDAO.class);
    }

    @After
    public void destroy() throws IOException {
        session.commit();   // 事务提交
        session.close();
        is.close();
    }

    @Test
    public void findAllTest() {
        List<Role> roles = roleDAO.findAll();
        for (Role role : roles) {
            System.out.println();
            System.out.println("----- " +  " -----");
            System.out.println(role);
            if (role != null) {
                System.out.println(role.getUsers());
            }
        }
    }
}

