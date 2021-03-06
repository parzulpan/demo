package cn.parzulpan.test;

import cn.parzulpan.dao.UserDAO;
import cn.parzulpan.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-15
 * @Desc :
 */

public class MyBatisTest {
    public static void main(String[] args) throws IOException {
        // 1. 读取配置文件
        // 使用类加载器，它只能读取类路径的配置文件
        // 使用 ServletContext 对象的 getRealPath()
        InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");

        // 2. 创建 SqlSessionFactory 的构建者对象
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        // 3. 使用构建者创建工厂对象 SqlSessionFactory
        // 创建工厂对象 使用了建造者模式
        // 优势：把对象的创建细节隐藏，使用者直接调用方法即可拿到对象
        SqlSessionFactory factory = builder.build(is);

        // 4. 使用 SqlSessionFactory 生产 SqlSession 对象
        // 生产 SqlSession 对象 使用了工厂模式
        // 优势：解藕，降低了类之间的依赖关系
        SqlSession session = factory.openSession();

        // 5. 使用 SqlSession 对象 创建 DAO 接口的的代理对象
        // 创建 DAO 接口的代理对象 使用了代理模式
        // 优势：在不修改源码的基础上对已有方法增强
        UserDAO userDAO = session.getMapper(UserDAO.class);

        // 6. 使用代理对象执行方法
        List<User> users = userDAO.findAll();
        users.forEach(System.out::println);

        // 7. 释放资源
        session.close();
        is.close();
    }
}
